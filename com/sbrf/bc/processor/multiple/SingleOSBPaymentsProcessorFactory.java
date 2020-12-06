/*
 * Created on 26.05.2009
 */
package com.sbrf.bc.processor.multiple;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Properties;
import java.util.Random;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.zip.ZipException;

import com.sberbank.sbclients.util.dao.ConnectionSource;
import com.sberbank.sbclients.util.dao.J2EEContainerConnectionSource;
import com.sberbank.sbclients.util.dao.NullConnectionSource;
import com.sberbank.sbclients.util.dao.SqlOutAccessor;
import com.sberbank.sbclients.util.dao.db2.AbstractDAO;
import com.sberbank.sbclients.util.dao.db2.DAOUtil;
import com.sbrf.bc.im.ejb.seq.SequenceFactory;
import com.sbrf.bc.im.ejb.seq.SequenceSingle;
import com.sbrf.bc.plugin.BillingPlugin;
import com.sbrf.bc.plugin.PluginConfigurationHelper;
import com.sbrf.bc.plugin.SettingNames;
import com.sbrf.bc.processor.FileMetadata;
import com.sbrf.bc.processor.OutgoingRegistryProcessor;
import com.sbrf.bc.processor.OutgoingRegistryProcessorFactory;
import com.sbrf.bc.processor.Param;
import com.sbrf.bc.processor.Parameters;
import com.sbrf.bc.processor.ProcessorException;
import com.sbrf.bc.processor.RegistryContext;
import com.sbrf.bc.processor.ZipOrFileGroupOutputStream;
import com.sbrf.bc.processor.operday.OperDayFactory;
import com.sbrf.dao.DownloadsPropertiesDAO;
import com.sbrf.dao.Maket1DAO;
import com.sbrf.data.DownloadsPropertiesData;
import com.sbrf.data.Maket1Data;
import com.sbrf.masspay.ucm.MasspayTypes;
import com.sbrf.util.classloader.ResourceHelper;
import com.sbrf.util.io.CrlfPrintWriter;
import com.sbrf.util.logging.SBRFLogger;
import com.sbrf.util.sql.group.DefaultGroupHandler;
import com.sbrf.util.sql.group.GroupFieldDescription;
import com.sbrf.util.sql.group.GroupSelector;
import com.sbrf.util.sql.group.LinkedMapCompositeGroupHandler;
import com.sbrf.util.sql.group.NameFieldDescription;
import com.sbrf.util.sql.group.SelectorException;
import com.sbrf.util.text.LightDateFormat;
import com.sbrf.util.text.MessageFormat;
import com.sbrf.util.text.NamedMessageFormat;

/**
 * Осуществляет выгрузку платежей в формате спецклиентов, сгруппированных по получателям и их услугам, без учета
 * платежных поручений, в соответствии с пунктом 2.15 плана перехода на безбумажную технологию.
 * ID в АС ЦУП - 68471.
 * 
 * @author bogdanov-sa
 */
public class SingleOSBPaymentsProcessorFactory implements OutgoingRegistryProcessorFactory, BillingPlugin {
    private final Config config;

    public SingleOSBPaymentsProcessorFactory(Properties properties) {
        PluginConfigurationHelper context = new PluginConfigurationHelper(properties);
        this.config = new Config(context);
    }

    /* (non-Javadoc)
     * @see com.sbrf.bc.processor.OutgoingRegistryProcessorFactory#newProcessor(com.sbrf.bc.processor.Param)
     */
    public OutgoingRegistryProcessor newProcessor(Param param) throws ProcessorException {
        ConnectionSource connectionSource = new J2EEContainerConnectionSource();
        String downloadDateParam = param.findValue(Parameters.DOWNLOAD_DATE);
        DateFormat dateFormat = new LightDateFormat("dd.MM.yyyy");
        Date transferDate = null;
        if (downloadDateParam != null) {
            try {
                transferDate = dateFormat.parse(downloadDateParam); 
            } catch (ParseException e) {
                throw new ProcessorException("Неправильный формат даты. " + e.getMessage());
            }
        } else {
            transferDate = OperDayFactory.getOperDayPlugin().getOperationDay();
        }
        return new Implementation(connectionSource, config, transferDate);
    }
    
    public static final class Counter {
        private int count = 0;
        private long sum = 0;
        private long recipientCommission = 0;
        
        public void init() {
            this.count = 0;
            this.sum = 0;
            this.recipientCommission = 0;
        }
        
        public void add(Maket1Data payment) {
            if (payment.getPaymentDate() != null) {
                this.count++;
                this.sum += payment.getSum();
                this.recipientCommission += payment.getServiceCommission();
            }
        }
        
        public int getCount() {
            return count;
        }
        
        public long getRecipientCommission() {
            return recipientCommission;
        }
        
        public long getSum() {
            return sum;
        }
        
        public long getClearSum() {
            return sum - recipientCommission;
        }
    }
    
    private static final class Context {
        final Config config;
        final Logger logger;
        final Date transferDate;
        final Maket1DAO maket1DAO;
        final DownloadsPropertiesDAO downloadsPropertiesDAO;
        final Maket1Data paymentData;
        final DownloadsPropertiesData propertiesData;
        final int maket1Position;
        final int propertiesPosition;
        final Integer year;
        final Collection files;
        long registryId;
        ZipOrFileGroupOutputStream outputStream;
        PrintWriter output;
        String informationalLineFormat;
        int fileNumber;
        Connection connection;
        
        Context(Config config, Logger logger, Date transferDate, Connection connection) {
            this.logger = logger;
            this.transferDate = transferDate;
            this.config = config;
            this.paymentData = new Maket1Data();
            this.propertiesData = new DownloadsPropertiesData();
            this.maket1DAO = new Maket1DAO(NullConnectionSource.getInstance());            
            this.downloadsPropertiesDAO = new DownloadsPropertiesDAO(NullConnectionSource.getInstance());
            this.propertiesPosition = 1;
            this.maket1Position = getPosition(this.downloadsPropertiesDAO, this.propertiesPosition);
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(this.transferDate);
            this.year = new Integer(calendar.get(Calendar.YEAR) % 10);
            this.files = new ArrayList();
            this.connection = connection;
        }
        
        void clear() {
            if (output != null) {
                output.close();
            }
            if (outputStream != null) {
                try {
                    outputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            output = null;
            outputStream = null;
        }  
        
        void closeConnection() {
            DAOUtil.close(connection);
        }
        
        FileMetadata[] getFiles() {
            return (FileMetadata[]) files.toArray(new FileMetadata[0]);
        }
    }
    
    private static final class PreliminaryRecordHandler extends DefaultGroupHandler {
        private final Context context;
        
        PreliminaryRecordHandler(Context context) {
            this.context = context;
        }
        public void nextRecord(ResultSet resultSet) throws SelectorException {
            try {
                context.maket1DAO.populate(context.paymentData, resultSet, context.maket1Position);
            } catch (SQLException e) {
                logAndThrow(context, e, "nextRecord", this.getClass());
            }
        }
        
        public void cleanup() {
            context.clear();
        }
    }
    
    private static final class RecipientHandler extends DefaultGroupHandler {
        private final Context context;
        private final SequenceSingle sequence;
        
        RecipientHandler(Context context) {
            this.context = context;
            this.sequence = SequenceFactory.getDailySequence(context.transferDate, "S_OSB_" + context.propertiesData.getProviderCode(), 0 , 1, 1);
        }
        
        public void startField(GroupFieldDescription description, Object value, ResultSet resultSet) throws SelectorException {
            try {
                context.downloadsPropertiesDAO.populate(context.propertiesData, resultSet, context.propertiesPosition);
                boolean useZip = context.propertiesData.getUseZip() == 1;
                boolean makeEmptyZip = context.propertiesData.getMakeEmptyFiles() == 1;
                String zipFileNameFormat = useZip ? context.propertiesData.getZipFileNameFormat() : null;
                context.fileNumber = 0;
                String outputDir = context.propertiesData.getOutputDir();
                context.outputStream = ZipOrFileGroupOutputStream.makeStream(useZip, makeEmptyZip, outputDir);
                context.outputStream.start(makeZipFileName(zipFileNameFormat));
            } catch (SQLException e) {
                logAndThrow(context, e, "startField", this.getClass());
            } catch (IOException e) {
                logAndThrow(context, e, "startField", this.getClass());
            }
        }
        
        private String makeZipFileName(String zipFileNameFormat) throws SQLException {
            if (zipFileNameFormat == null) {
                return null;
            }
            long nextId = sequence.getNextID();
            long fileId = nextId;
            String result;
            if (isNamedMessageFormat(zipFileNameFormat)) {
                NamedMessageFormat namedMessageFormat = new NamedMessageFormat (zipFileNameFormat);
                Set aliasSet = namedMessageFormat.getFieldNames();
                Map parameters = SpecialClientMapFormatter.makeZipFileNameParameters(
                        aliasSet,
                        context.paymentData,
                        context.propertiesData,
                        context.transferDate,
                        context.year,
                        "",
                        fileId);
                if (context.config.useRgProps) {
                    //доступ в RG.PROPS
                    searchInRgProps(context, parameters);
                }
                result = namedMessageFormat.format(parameters);
            } else {
                Object[] parameters = SpecialClientFormatter.makeZipFileNameParameters(
                        context.paymentData,
                        context.propertiesData,
                        context.transferDate,
                        context.year,
                        "",
                        fileId);
                result = MessageFormat.format(zipFileNameFormat, parameters);
            }
            return result;
        }
        
        public void endField(GroupFieldDescription description, Object value) throws SelectorException {
            try {
                context.outputStream.stop();
                FileMetadata files[] = context.outputStream.getFilesMetadata();
                for (int i = 0; i < files.length; i++) {
                    context.files.add(files[i]);
                }
            } catch (IOException e) {
                logAndThrow(context, e, "endField", this.getClass());
            }
        }
        
        public void error(SelectorException exception) throws SelectorException {
            PrintWriter output = null;
            try {
                File protocol = File.createTempFile("error-", ".txt");
                output = new PrintWriter(new FileOutputStream(protocol));
                output.println("Exception occured while downloading");
                output.println("Download id: " + context.registryId);
                output.println("ProviderCode: " + context.propertiesData.getProviderCode());
                output.println("Class: " + this.getClass().getName());
                output.println("ExceptionClass: " + exception.getClass().getName());
                exception.printStackTrace(output);
                output.close();
                output = null;
                context.files.add(new FileMetadata(protocol, context.config.errorOutputDirectory, MessageFormat.format("error-single-osb-{0,date,yyyy-MM-dd-HH-mm-ss-SSS}.txt", new Object[]{Calendar.getInstance().getTime()})));
            } catch (FileNotFoundException e) {
                context.logger.logp(Level.SEVERE, this.getClass().getName(), "error", "Exception", e);
                throw exception;
            } catch (IOException e) {
                context.logger.logp(Level.SEVERE, this.getClass().getName(), "error", "Exception", e);
                throw exception;
            } finally {
                if (output != null) {
                    output.close();
                }
            }
        }
    }
    
    private static final class ServiceHandler extends DefaultGroupHandler {
        private final Context context;
        private final Counter counter;
        private String footerFormat;
        private boolean skip;
        
        ServiceHandler(final Context context) {
            this.context = context;
            this.counter = new Counter();
        }
        
        public void startField(GroupFieldDescription description, Object value, ResultSet resultSet) throws SelectorException {
            try {                
                context.maket1DAO.populate(context.paymentData, resultSet, context.maket1Position);
                context.downloadsPropertiesDAO.populate(context.propertiesData, resultSet, context.propertiesPosition);
                if (context.propertiesData.getMakeEmptyFiles() == 0 && noPayment(context.paymentData)) {
                    this.skip = true;
                    /*
                     *  пропустить запись, состоящую из нулевых значений, если
                     *  отключена настройка "делать пустые файлы"
                     */ 
                    return;
                }
                this.skip = false;
                counter.init();                
                String fileName = makeFileName();
                try {
                    context.outputStream.startBlock(fileName);
                } catch (ZipException e) {
                    context.logger.log(Level.SEVERE, "ZipException occured in startBlock method, trying to start block with another name");
                    fileName = fileName + "-" + new Random(Calendar.getInstance().getTimeInMillis()).nextInt(100);
                    context.outputStream.startBlock(fileName);
                }
                context.output = new CrlfPrintWriter(context.outputStream, context.config.lineSeparator, context.propertiesData.getFileEncoding());
                if (context.propertiesData.getHeaderPrint() == 1) {
                    String headerFormat = context.propertiesData.getHeaderFormat();
                    String header;
                    if (isNamedMessageFormat(headerFormat)) {
                        NamedMessageFormat namedFormat = new NamedMessageFormat(headerFormat);
                        Set aliasSet = namedFormat.getFieldNames();
                        Map parameters = SpecialClientMapFormatter.makeHeaderParameters(aliasSet, context.paymentData, context.propertiesData, context.transferDate, context.year, "", fileName);
                        if (context.config.useRgProps) {
                            searchInRgProps(context, parameters);
                        }
                        header = namedFormat.format(parameters);
                    } else {
                        header = MessageFormat.format(headerFormat, SpecialClientFormatter.makeHeaderParameters(context.paymentData, context.propertiesData, context.transferDate, context.year, "", fileName));
                    }
                    context.output.println(header);
                }
            } catch (SQLException e) {
                logAndThrow(context, e, "startField", this.getClass());
            } catch (IOException e) {
                logAndThrow(context, e, "startField", this.getClass());
            }
            if (context.propertiesData.getPayPrint() == 1) {
                context.informationalLineFormat = context.propertiesData.getPayFormat();
            } else {
                context.informationalLineFormat = null;
            }
            if (context.propertiesData.getFooterPrint() == 1) {
                this.footerFormat = context.propertiesData.getFooterFormat();
            } else {
                this.footerFormat = null;
            }
        }        
        
        
        public void nextRecord(ResultSet resultSet) throws SelectorException {
            if (skip) {
                return;
            }
            counter.add(context.paymentData);
        }
        
        public void endField(GroupFieldDescription description, Object value) throws SelectorException {
            if (skip) {
                return;
            }
            try {
                if (context.propertiesData.getFooterPrint() == 1) {
                    String str = "";
                    if (isNamedMessageFormat(footerFormat)) {
                        NamedMessageFormat namedFormat = new NamedMessageFormat(footerFormat);
                        Set aliasSet = namedFormat.getFieldNames();
                        Map parameters = SpecialClientMapFormatter.makeFooterParameters(
                                aliasSet,
                                context.paymentData,
                                context.propertiesData,
                                context.transferDate,
                                context.year,
                                "",
                                counter.getCount(),
                                counter.getSum(),
                                counter.getClearSum(),
                                counter.getRecipientCommission());
                        if (context.config.useRgProps) {
                            searchInRgProps(context, parameters);
                        }
                        str = namedFormat.format(parameters);
                    } else {
                        Object parameters[] = SpecialClientFormatter.makeFooterParameters(
                                context.paymentData,
                                context.propertiesData,
                                context.transferDate,
                                context.year,
                                "",
                                counter.getCount(),
                                counter.getSum(),
                                counter.getClearSum(),
                                counter.getRecipientCommission());
                        str = MessageFormat.format(footerFormat, parameters);
                    }
                    context.output.println(str);
                }
                context.output.flush();
                context.outputStream.endBlock();
            } catch (IOException e) {
                logAndThrow(context, e, "endField", this.getClass());
            } catch (SQLException e) {
                logAndThrow(context, e, "endField", this.getClass());
            }
        }
        
        private String makeFileName() throws SQLException {
            String specialClientCode = context.propertiesData.getCspec();
            String kkk = specialClientCode.length() > 3 ? specialClientCode.substring(0, 3) : specialClientCode;
            String ll = specialClientCode.length() > 3 ? specialClientCode.substring(3) : "";
            context.fileNumber++;
            String result;
            String fileNameFormat = context.propertiesData.getFileNameFormat();
            if (isNamedMessageFormat(fileNameFormat)) {
                NamedMessageFormat namedFormat = new NamedMessageFormat(fileNameFormat);
                Set aliasSet = namedFormat.getFieldNames();
                Map parameters = SpecialClientMapFormatter.makeFileNameParameters(aliasSet, context.paymentData, context.propertiesData, context.transferDate, context.year, "", context.fileNumber, kkk, ll);
                if (context.config.useRgProps) {
                    searchInRgProps(context, parameters);
                }
                result = namedFormat.format(parameters);
            } else {
                Object parameters[] = SpecialClientFormatter.makeFileNameParameters(context.paymentData, context.propertiesData, context.transferDate, context.year, "", context.fileNumber, kkk, ll);
                result = MessageFormat.format(fileNameFormat, parameters);
            }
            return result;
        }        
    }
    
    private static final class OSBHandler extends DefaultGroupHandler {
        private final Context context;
        private final Counter counter;
        private String osbLineFormat;
        private boolean skip;
        
        OSBHandler(final Context context) {
            this.context = context;
            this.counter = new Counter();
        }
        
        public void startField(GroupFieldDescription description, Object value, ResultSet resultSet) throws SelectorException {
            if (value == null || context.propertiesData.getOsbPrint() == 0) {
                this.skip = true;
                return;
            }
            this.skip = false;
            counter.init();
            this.osbLineFormat = context.propertiesData.getOsbFormat();
        }        
        
        
        public void nextRecord(ResultSet resultSet) throws SelectorException {
            if (skip) {
                return;
            }
            counter.add(context.paymentData);
        }
        
        public void endField(GroupFieldDescription description, Object value) throws SelectorException {
            if (skip) {
                return;
            }
            try {
                String str;
                if (isNamedMessageFormat(osbLineFormat)) {
                    NamedMessageFormat namedFormat = new NamedMessageFormat(osbLineFormat);
                    Set aliasSet = namedFormat.getFieldNames();
                    Map parameters = SpecialClientMapFormatter.makeOSBLineParameters(
                            aliasSet,
                            context.paymentData,
                            context.propertiesData,
                            context.transferDate,
                            context.year,
                            "",
                            counter.getCount(),
                            counter.getSum(),
                            counter.getClearSum(),
                            counter.getRecipientCommission());
                    if (context.config.useRgProps) {
                        searchInRgProps(context, parameters);
                    }
                    str = namedFormat.format(parameters);
                } else {
                    Object parameters[] = SpecialClientFormatter.makeOSBLineParameters(
                            context.paymentData,
                            context.propertiesData,
                            context.transferDate,
                            context.year,
                            "",
                            counter.getCount(),
                            counter.getSum(),
                            counter.getClearSum(),
                            counter.getRecipientCommission());
                    str = MessageFormat.format(osbLineFormat, parameters);
                }
                context.output.println(str);
            } catch (SQLException e) {
                throw new SelectorException(e);
            }
        }
    }
    
    private static final class FinalRecordHandler extends DefaultGroupHandler {
        private final Context context;
        private final Counter counter;        
        
        FinalRecordHandler(Context context) {
            this.context = context;
            this.counter = new Counter();
        }
        
        public void nextRecord(ResultSet resultSet) throws SelectorException {
            if (noPayment(context.paymentData)) {
                return;
            }
            if (context.propertiesData.getPayPrint() != 1) {
                return;
            }
            try {
                this.counter.init();
                this.counter.add(context.paymentData);
                String str;
                if (isNamedMessageFormat(context.informationalLineFormat)) {
                    NamedMessageFormat namedFormat = new NamedMessageFormat(context.informationalLineFormat);
                    Set aliasSet = namedFormat.getFieldNames();
                    Map parameters = SpecialClientMapFormatter.makePaymentLineParameters(aliasSet, context.paymentData, context.propertiesData, context.transferDate, context.year, "", counter.getSum(), counter.getClearSum(), counter.getRecipientCommission());
                    if (context.config.useRgProps) {
                        searchInRgProps(context, parameters);
                    }
                    str = namedFormat.format(parameters);
                } else {
                    Object parameters[] = SpecialClientFormatter.makePaymentLineParameters(context.paymentData, context.propertiesData, context.transferDate, context.year, "", counter.getSum(), counter.getClearSum(), counter.getRecipientCommission());
                    str = MessageFormat.format(context.informationalLineFormat, parameters);
                }
                context.output.println(str);
            } catch (SQLException e) {
                throw new SelectorException(e);
            }
        }        
    }
        
    private static final class Implementation extends AbstractDAO implements OutgoingRegistryProcessor {
        final Context context;
        final LinkedHashMap<Object, Object> handlers;
        final String sql;
        final Config config;
        
        protected Implementation(ConnectionSource connectionSource, Config config, Date transferDate) {
            super(connectionSource);
            Logger logger = SBRFLogger.getInstance("com.sbrf.bc.Logger");
            this.config = config;
            this.context = new Context(config, logger, transferDate, getConnection());
            this.handlers = new LinkedHashMap<Object, Object>();
            this.sql = SpecialClientPaymentsProcessor.makeSql(context.maket1DAO, "B", context.downloadsPropertiesDAO, "A", config.sqlFormat);
        }

        public FileMetadata[] download(RegistryContext registryContext) throws ProcessorException {
            context.logger.log(Level.INFO, "SingleOSB: download started");
            context.registryId = registryContext.getRegistryId();
            Connection connection = null;
            PreparedStatement statement = null;
            ResultSet resultSet = null;
            try {
                connection = getConnection();
                statement = connection.prepareStatement(sql);
                int index = 1;
                setDate(statement, index++, context.transferDate);
                setString(statement, index++, config.validPaymentsStatus);
                setString(statement, index++, config.inValidRouteStatus);
                setInt(statement, index++, config.groupOsbId);
                setInt(statement, index++, DownloadTypes.OSB_SINGLE);
                resultSet = statement.executeQuery();
                GroupFieldDescription fieldDescriptions[] = {
                            new NameFieldDescription("PROVIDER_CODE"),
                            new NameFieldDescription("PROVIDER_DEPARTMENT"),
                            new NameFieldDescription("SERVICE_KIND"),
                            new NameFieldDescription("OSB")
                        };
                LinkedMapCompositeGroupHandler groupHandler = new LinkedMapCompositeGroupHandler();
                groupHandler.addHandler("-1", new PreliminaryRecordHandler(context));
                groupHandler.addHandler("PROVIDER_CODE", new RecipientHandler(context));                
                groupHandler.addHandler("SERVICE_KIND", new ServiceHandler(context));
                groupHandler.addHandler("OSB", new OSBHandler(context));
                groupHandler.addHandler("-2", new FinalRecordHandler(context));
                GroupSelector groupSelector = new GroupSelector(fieldDescriptions);
                groupSelector.parse(resultSet, groupHandler);
                return context.getFiles();
            } catch (SQLException e) {
                ProcessorException exception = new ProcessorException(e);
                context.logger.throwing(this.getClass().getName(), "downloadInternal", exception);
                throw exception;
            } catch (SelectorException e) {
                ProcessorException exception = new ProcessorException(e);
                context.logger.throwing(this.getClass().getName(), "downloadInternal", exception);
                throw exception;
            } catch (RuntimeException e) {
                ProcessorException exception = new ProcessorException(e);
                context.logger.throwing(this.getClass().getName(), "downloadInternal", exception);
                throw exception;
            } finally {
                close(resultSet);
                close(statement);
                close(connection);
                context.clear();
                context.closeConnection();
            }
        }        
    }
    
    private static final class Config {
        final String sqlFormat;
        final String lineSeparator;
        final String errorOutputDirectory;
        final boolean useRgProps;
        final String validPaymentsStatus;
        final String inValidRouteStatus;
        final int groupOsbId;
        
        public Config(PluginConfigurationHelper context) {
            String sql1 = context.getString("sql", null);
            if (sql1 == null || sql1.length() == 0) {
                String sqlResource = context.getString("sqlResource", "single-osb.sql");
                sql1 = ResourceHelper.getResourceAsString(SingleOSBPaymentsProcessorFactory.class, sqlResource);                
            }
            this.sqlFormat = sql1;
            this.lineSeparator = context.getLineSeparator("lineSeparator");
            this.errorOutputDirectory = context.getString(SettingNames.ERROR_OUTPUT_DIRECTORY, "");
            this.useRgProps = context.getBoolean("useRgProps", true);
            this.validPaymentsStatus = context.getString("validPaymentsStatus", "TRANSFERRED");
            this.inValidRouteStatus = context.getString("inValidRouteStatus", "MANUAL");
            groupOsbId = context.getInteger("groupOsbId", 1);
        }        
    }

    static int getPosition(DownloadsPropertiesDAO downloadsPropertiesDAO, int startIndex) {
        final MasspayTypes.IntegerHolder holder = new MasspayTypes.IntegerHolder(); 
        holder.value = 0;
        downloadsPropertiesDAO.addOuts(new SqlOutAccessor() {
            public void addOut(String columnName) {
                holder.value++;
            }});
        return holder.value + startIndex;
    }
    
    static int getPosition(Maket1DAO maket1DAO, int startIndex) {
        final MasspayTypes.IntegerHolder holder = new MasspayTypes.IntegerHolder(); 
        holder.value = 0;
        maket1DAO.addOuts(new SqlOutAccessor() {
            public void addOut(String columnName) {
                holder.value++;
            }});
        return holder.value + startIndex;
    }
    
    static String decodeNullString(String str) {
        if (str == null) {
            return "";
        } else {
            return str;
        }
    }
    
    static void logAndThrow(Context context, Exception e, String method, Class sourceClass) throws SelectorException {
        SelectorException exception = new SelectorException(e);
        context.logger.throwing(sourceClass.getName(), method, exception);
        throw exception;
    }
    
    static boolean noPayment(Maket1Data paymentData) {
        return paymentData.getPaymentDate() == null;
    }

    static boolean isNamedMessageFormat(String format) {
        Pattern pattern = Pattern.compile(".*\\{\\d+.*");
        Matcher matcher = pattern.matcher(format);
        if (matcher.matches()) {
            return false;
        }
        return true;
    }
    
    static void searchInRgProps(Context context, Map parameters) throws SQLException  {
        SpecialClientMapFormatter.searchInRgProps(context.connection, context.paymentData, parameters);
    }
}
