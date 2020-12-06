/*
 * Created on 27.04.2009
 */
package com.sbrf.bc.processor.multiple;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.zip.ZipEntry;
import java.util.zip.ZipException;
import java.util.zip.ZipOutputStream;

import com.sberbank.sbclients.util.CrlfPrintWriter;
import com.sberbank.sbclients.util.dao.J2EEContainerConnectionSource;
import com.sberbank.sbclients.util.dao.NullConnectionSource;
import com.sberbank.sbclients.util.dao.db2.DAOUtil;
import com.sbrf.bc.command.Command;
import com.sbrf.bc.command.CommandException;
import com.sbrf.bc.command.CommandServer;
import com.sbrf.bc.command.CommandServerDelegate;
import com.sbrf.bc.im.ejb.seq.SequenceFactory;
import com.sbrf.bc.im.ejb.seq.SequenceSingle;
import com.sbrf.bc.plugin.BillingPlugin;
import com.sbrf.bc.plugin.BillingPluginFactory;
import com.sbrf.bc.plugin.PluginNotFoundException;
import com.sbrf.bc.processor.FileMetadata;
import com.sbrf.bc.processor.PaymentsHandler;
import com.sbrf.bc.processor.PaymentsHandlerFactory;
import com.sbrf.bc.processor.PaymentsHandlerProcessorException;
import com.sbrf.dao.DownloadsPropertiesDAO;
import com.sbrf.dao.Maket1DAO;
import com.sbrf.dao.RMaketDAO;
import com.sbrf.data.DownloadsPropertiesData;
import com.sbrf.data.Maket1Data;
import com.sbrf.data.RMaketData;
import com.sbrf.util.sql.group.GroupFieldDescription;
import com.sbrf.util.sql.group.GroupHandler;
import com.sbrf.util.sql.group.SelectorException;
import com.sbrf.util.text.MessageFormat;
import com.sbrf.util.text.NamedMessageFormat;

class SpecialClientGroupHandler implements GroupHandler {
    private final Map<String, Object> props;
    private final SpecialClientPaymentsProcessorConfig config;
    private final String newLine;
    private final Date operationDate;
    private final Integer oneDigitYear;
    private final Logger logger;
    private final DownloadsPropertiesData currentPropertiesData;
    private Collection<FileMetadata> filesMetadata;
    private Maket1DAO maket1DAO;
    private RMaketDAO maketRDAO;
    private DownloadsPropertiesDAO downloadsPropertiesDAO;
    private OutputStream outputStream;
    private long sumOsb;
    private long supenOsb;
    private long serviceCommissionOsb;
    private long countPaymentsOsb;
    private long sumFile;
    private long supenFile;
    private long serviceCommissionFile;
    private long countPaymentsFile;
    private final Maket1Data currentMaket1Data;
    private final RMaketData currentMaketRData;
    private boolean newZipFile;
    private boolean newFile;
    private boolean skip;
    private long rr;
    private String currentPayerAccount;
    private Connection connection;
    final BillingPluginFactory billingPluginFactory;
    private boolean usePaymentHandlerPlugin = false;
    private boolean errorWithinPaymentHandler = false;
    private PaymentsHandler paymentHandler = null;
    private PaymentsHandlerFactory paymentHandlerFactory = null;
    private final CommandServer commandServer;
    private File responseFile;
    PrintWriter outputInformation;
    private String outputInformationEncoding;
    private boolean wasErrorInPaymentHandler = false;

    public SpecialClientGroupHandler(SpecialClientPaymentsProcessorConfig config, Logger logger, Date operationDate) {
        props = new HashMap<String, Object>();
        this.operationDate = operationDate;
        this.oneDigitYear = this.getOneDigit(operationDate);
        props.put("operationDate", operationDate); // TODO make aliases
        props.put("oneDigitYear", oneDigitYear);
        this.config = config;
        this.logger = logger;
        this.newLine = this.config.outputLineSeparator;
        this.currentPropertiesData = new DownloadsPropertiesData();
        this.currentMaket1Data = new Maket1Data();
        this.currentMaketRData = new RMaketData();
        this.currentPayerAccount = "";
        this.connection = new J2EEContainerConnectionSource().getConnection();
        billingPluginFactory = new BillingPluginFactory();
        commandServer = new CommandServerDelegate(CommandServerDelegate.DEFAULT_JNDI_NAME);
        outputInformationEncoding = config.outputInformationEncoding;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.sbrf.util.sql.group.GroupHandler#start()
     */
    public void start() throws SelectorException{
        logger.entering(this.getClass().getName(), "start");
        this.filesMetadata = null;
        this.filesMetadata = new ArrayList<FileMetadata>();
        this.maket1DAO = new Maket1DAO(NullConnectionSource.getInstance());
        this.maketRDAO = new RMaketDAO(NullConnectionSource.getInstance());
        this.downloadsPropertiesDAO = new DownloadsPropertiesDAO(NullConnectionSource.getInstance());
        this.newZipFile = true;
        this.newFile = true;
        this.initRR();
        this.skip = false;
        try {
            final Date currentDate = Calendar.getInstance().getTime();
            responseFile = File.createTempFile("payments-r-", ".tmp");
            outputInformation = new CrlfPrintWriter(new OutputStreamWriter(new FileOutputStream(responseFile), outputInformationEncoding), newLine);                
            outputInformation.println(MessageFormat.format("{0,date,dd.MM.yyyy HH:mm}", new Object[]{currentDate}));
            outputInformation.println("Информация об ошибках в плагинах обработки платежей");
        } catch (IOException e) {
            logger.throwing(this.getClass().getName(), "start", e);
            throw new SelectorException(e);
        }
        // logger.exiting(this.getClass().getName(), "start");
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.sbrf.util.sql.group.GroupHandler#startField(com.sbrf.util.sql.group
     * .GroupFieldDescription, java.lang.Object, java.sql.ResultSet)
     */
    public void startField(GroupFieldDescription description, Object value, ResultSet resultSet) throws SelectorException {

        logger.entering(this.getClass().getName(), "startField", new Object[] { description.getName(), value });
        try {
            if (description.getName().equals(SpecialClientSelectorNames.providerCode)) {
                usePaymentHandlerPlugin = false;

                downloadsPropertiesDAO.populate(currentPropertiesData, resultSet, 1);
                String pluginHandler = currentPropertiesData.getPluginHandler();
                Integer providerId = currentPropertiesData.getProviderId();
                if (pluginHandler != null && !pluginHandler.equals("") && providerId != null) {
                    usePaymentHandlerPlugin = true;
                    errorWithinPaymentHandler = false;
                    paymentHandlerFactory = null;
                    paymentHandler = null;
                    BillingPlugin plugin;
                    try {
                        plugin = billingPluginFactory.findPlugin(providerId, pluginHandler);
                    } catch (PluginNotFoundException e) {
                        logger.throwing(this.getClass().getName(), "startField", e);
                        errorWithinPaymentHandler = true;
                        outputInformation.println("Плагин " + providerId + "_" + pluginHandler + " не найден.");
                        wasErrorInPaymentHandler = true;
                        return;
                    }
                    paymentHandlerFactory = (PaymentsHandlerFactory) plugin;
                    return;
                }
                // Делаем новый архив
                // Обработка будет в nextRecord, т.к. нужна хотя бы первая
                // запись
                this.initRR();
                this.newZipFile = true;
            } else if (description.getName().equals(SpecialClientSelectorNames.providerDepartment)) {
            } else if (description.getName().equals(SpecialClientSelectorNames.transferDate)) {
            } else if (description.getName().equals(SpecialClientSelectorNames.paymentOrderSource)) {
            } else if (description.getName().equals(SpecialClientSelectorNames.paymentOrder)) {
            } else if (description.getName().equals(SpecialClientSelectorNames.serviceKind)) {
                if (usePaymentHandlerPlugin) {
                    return;
                }
                int index = downloadsPropertiesDAO.populate(currentPropertiesData, resultSet, 1);
                index = maket1DAO.populate(currentMaket1Data, resultSet, index);
                maketRDAO.populate(currentMaketRData, resultSet, index);
                currentPayerAccount = currentMaketRData.getPayerSettleAcc();
                if (currentPropertiesData.getMakeEmptyFiles() == 0 && currentMaket1Data.getPaymentDate() == null) {
                    this.skip = true;
                    return;
                }
                // Добавляем новый файл
                // Обработка будет в nextRecord, т.к. нужна хотя бы первая
                // запись
                this.skip = false;
                this.nextRR();
                this.newFile = true;
                this.initFileCounters();
            } else if (description.getName().equals(SpecialClientSelectorNames.osb)) {
                if (usePaymentHandlerPlugin) {
                    return;
                }
                this.initOsbCounters();
            }
            // logger.exiting(this.getClass().getName(), "startField");
        } catch (SQLException e) {
            SelectorException exception = new SelectorException(e);
            logger.throwing(this.getClass().getName(), "startField", exception);
            throw exception;
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.sbrf.util.sql.group.GroupHandler#nextRecord(java.sql.ResultSet)
     */
    public void nextRecord(final ResultSet resultSet) throws SelectorException {
        try {
            int index = downloadsPropertiesDAO.populate(currentPropertiesData, resultSet, 1);
            index = maket1DAO.populate(currentMaket1Data, resultSet, index);

            if (usePaymentHandlerPlugin) {
                if (errorWithinPaymentHandler) {
                    return;
                }
                try {
                    if (paymentHandler == null) {
                        paymentHandler = paymentHandlerFactory.newPaymentHandler();
                        commandServer.executeCommand(new Command() {
                            public Object execute() throws CommandException {
                                try {
                                    paymentHandler.init(resultSet, props);
                                    return null;
                                } catch (PaymentsHandlerProcessorException e) {
                                    throw new CommandException(e);
                                } catch (RuntimeException e) {
                                    throw new CommandException(e);
                                }
                            }
                        });
                    }
                    if (currentMaket1Data.getPaymentDate() != null) {
                        paymentHandler.nextPayment(resultSet);
                    }
                } catch (PaymentsHandlerProcessorException e) {
                    logger.throwing(this.getClass().getName(), "nextRecord", e);
                    errorWithinPaymentHandler = true;
                }  catch (RuntimeException e) {
                    logger.throwing(this.getClass().getName(), "nextRecord", e);
                    errorWithinPaymentHandler = true;
                }  catch (CommandException e) {
                    logger.throwing(this.getClass().getName(), "nextRecord", e);
                    errorWithinPaymentHandler = true;
                }
                if (errorWithinPaymentHandler) {
                    writeErrorToResponeFile();
                }
                return;
            }
            if (skip) {
                return;
            }
            maketRDAO.populate(currentMaketRData, resultSet, index);
            currentPayerAccount = currentMaketRData.getPayerSettleAcc();
            if (this.newZipFile) {
                this.makeNewZipFile();
                this.newZipFile = false;
            }
            if (this.newFile) {
                String fileName = this.makeNewFile();
                if (currentPropertiesData.getHeaderPrint() != 0) {
                    this.writeHeader(fileName);
                }
                this.newFile = false;

            }
            if (currentMaket1Data.getPaymentDate() != null && currentPropertiesData.getPayPrint() != 0) {
                // Если не присоединен пустой платеж
                this.writePaymentLine();
            }
        } catch (SQLException e) {
            SelectorException exception = new SelectorException(e);
            logger.throwing(this.getClass().getName(), "nextRecord", exception);
            throw exception;
        } catch (IOException e) {
            SelectorException exception = new SelectorException(e);
            logger.throwing(this.getClass().getName(), "nextRecord", exception);
            throw exception;
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.sbrf.util.sql.group.GroupHandler#endField(com.sbrf.util.sql.group
     * .GroupFieldDescription, java.lang.Object)
     */
    public void endField(GroupFieldDescription description, Object value) throws SelectorException {
        logger.entering(this.getClass().getName(), "endField", new Object[] { description.getName(), value });

        try {
            if (description.getName().equals(SpecialClientSelectorNames.providerCode)) {
                if (usePaymentHandlerPlugin) {
                    usePaymentHandlerPlugin = false;
                    
                    // получаем результат обработки
                    try {
                        if (!errorWithinPaymentHandler && paymentHandler != null) {
                            paymentHandler.endPayments();
                            filesMetadata.addAll(paymentHandler.getResult());
                        }
                    } catch (PaymentsHandlerProcessorException e) {
                        logger.throwing(this.getClass().getName(), "endField", e);
                        writeErrorToResponeFile();
                    }
                    // высвобождаем ресурсы
                    try {
                        if (paymentHandler != null) {
                            commandServer.executeCommand(new Command() {
                                public Object execute() throws CommandException {
                                    try {
                                        paymentHandler.stop();
                                        return null;
                                    } catch (PaymentsHandlerProcessorException e) {
                                        throw new CommandException(e);
                                    }  catch (RuntimeException e) {
                                        throw new CommandException(e);
                                    }
                                }
                            });
                        }
                    } catch (CommandException e) {
                        logger.throwing(this.getClass().getName(), "endField", e);
                        writeErrorToResponeFile();
                    }
                    return;
                }
                this.closeZipFile();
            } else if (description.getName().equals(SpecialClientSelectorNames.providerDepartment)) {
            } else if (description.getName().equals(SpecialClientSelectorNames.transferDate)) {
            } else if (description.getName().equals(SpecialClientSelectorNames.paymentOrderSource)) {
            } else if (description.getName().equals(SpecialClientSelectorNames.paymentOrder)) {
            } else if (description.getName().equals(SpecialClientSelectorNames.serviceKind)) {
                if (usePaymentHandlerPlugin) {
                    return;
                }

                if (skip) {
                    return;
                }
                if (currentMaket1Data.getPaymentDate() != null) {
                    if (this.currentPropertiesData.getOrderPrint() != 0) {
                        // Если не присоединен пустой платеж
                        this.writePaymentOrderLine();
                    }
                    if (currentPropertiesData.getFooterPrint() != 0) {
                        // Footer и PaymentInfo для выгрузки данного типа
                        // идентичны
                        this.writePaymentOrderLine();
                    }
                }
                this.closeFile();
            } else if (description.getName().equals(SpecialClientSelectorNames.osb)) {
                if (usePaymentHandlerPlugin) {
                    return;
                }
                if (skip) {
                    return;
                }
                if (this.currentPropertiesData.getOsbPrint() != 0 && currentMaket1Data.getPaymentDate() != null) {
                    // Если не присоединен пустой платеж
                    this.writeOSBControlLine();
                }
                this.addOsbToFileCounters();
            }
        } catch (IOException e) {
            SelectorException exception = new SelectorException(e);
            logger.throwing(this.getClass().getName(), "endField", exception);
            throw exception;
        } catch (SQLException e) {
            SelectorException exception = new SelectorException(e);
            logger.throwing(this.getClass().getName(), "endField", exception);
            throw exception;
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.sbrf.util.sql.group.GroupHandler#end()
     */
    public void end() throws SelectorException {
        logger.entering(this.getClass().getName(), "end");
        if (this.outputStream != null) {
            try {
                this.outputStream.close();
            } catch (IOException e) {
                SelectorException exception = new SelectorException(e);
                logger.throwing(this.getClass().getName(), "end", exception);
                throw exception;
            }
        }
        outputInformation.flush();
        outputInformation.close();
        if (wasErrorInPaymentHandler) {
            logger.warning("Exception has occured processing payments. See details in protocol, which was send to the workstation.");
            filesMetadata.add(new FileMetadata(responseFile, config.outputDirectory, MessageFormat.format(config.fileNameIndformation, new Object[]{Calendar.getInstance().getTime()})));
        } else {
            responseFile.delete();
        }
    }

    /**
     * Метод служит для получения результата работы
     * 
     * @return коллекцию метаданных файлов выгрузки
     */
    FileMetadata[] getResult() {
        return filesMetadata.toArray(new FileMetadata[filesMetadata.size()]);
    }

    private void writeHeader(String fileName) throws IOException, SQLException {
        String line;
        if (isNamedMessageFormat(currentPropertiesData.getHeaderFormat())) {
            NamedMessageFormat namedHeaderFormat = new NamedMessageFormat(currentPropertiesData.getHeaderFormat());
            Set aliasSet = namedHeaderFormat.getFieldNames();
            Map parameters = SpecialClientMapFormatter.makeHeaderParameters(aliasSet, this.currentMaket1Data, this.currentPropertiesData, this.operationDate, this.oneDigitYear, this.currentPayerAccount, fileName);
            if (config.useRgProps) {
                SpecialClientMapFormatter.searchInRgProps(connection, currentMaket1Data, parameters);
            }
            line = namedHeaderFormat.format(parameters);
        } else {
            MessageFormat headerFormat = new MessageFormat(currentPropertiesData.getHeaderFormat());
            line = headerFormat.format(SpecialClientFormatter.makeHeaderParameters(this.currentMaket1Data, this.currentPropertiesData, this.operationDate, this.oneDigitYear, this.currentPayerAccount, fileName));
        }
        if (line != null && line.length() > 0) {
            this.outputStream.write((line + newLine).getBytes(currentPropertiesData.getFileEncoding()));
        }
    }

    private void writePaymentLine() throws IOException, SQLException {
        long sum = currentMaket1Data.getSum();
        long supen = currentMaket1Data.getSupen();
        long serviceCommission = currentMaket1Data.getServiceCommission();
        sumOsb += sum;
        supenOsb += supen;
        serviceCommissionOsb += serviceCommission;
        countPaymentsOsb++;
        String line;
        if (isNamedMessageFormat(currentPropertiesData.getPayFormat())) {
            NamedMessageFormat namedPayFormat = new NamedMessageFormat(currentPropertiesData.getPayFormat());
            Set aliasSet = namedPayFormat.getFieldNames();
            Map parameters = SpecialClientMapFormatter.makePaymentLineParameters(
                    aliasSet,
                    this.currentMaket1Data,
                    this.currentPropertiesData,
                    this.operationDate,
                    this.oneDigitYear,
                    this.currentPayerAccount,
                    sum,
                    SpecialClientGroupHandler.calcClearSum(sum, serviceCommission),
                    serviceCommission);
            if (config.useRgProps) {
                SpecialClientMapFormatter.searchInRgProps(connection, currentMaket1Data, parameters);
            }
            line = namedPayFormat.format(parameters);
        } else {
            MessageFormat payFormat = new MessageFormat(currentPropertiesData.getPayFormat());
            line = payFormat.format(SpecialClientFormatter.makePaymentLineParameters(
                    this.currentMaket1Data,
                    this.currentPropertiesData,
                    this.operationDate,
                    this.oneDigitYear,
                    this.currentPayerAccount,
                    sum,
                    SpecialClientGroupHandler.calcClearSum(sum, serviceCommission),
                    serviceCommission));
        }
        if (line != null && line.length() > 0) {
            this.outputStream.write((line + newLine).getBytes(currentPropertiesData.getFileEncoding()));
        }
    }

    private void writeOSBControlLine() throws IOException, SQLException {
        String line;
        if (isNamedMessageFormat(currentPropertiesData.getOsbFormat())) {
            NamedMessageFormat namedOsbLineFormat = new NamedMessageFormat(currentPropertiesData.getOsbFormat());
            Set aliasSet = namedOsbLineFormat.getFieldNames();
            Map parameters = SpecialClientMapFormatter.makeOSBLineParameters(aliasSet, this.currentMaket1Data, this.currentPropertiesData, this.operationDate, this.oneDigitYear, this.currentPayerAccount, countPaymentsOsb, sumOsb, SpecialClientGroupHandler.calcClearSum(
                    sumOsb,
                    serviceCommissionOsb), serviceCommissionOsb);
            if (config.useRgProps) {
                SpecialClientMapFormatter.searchInRgProps(connection, currentMaket1Data, parameters);
            }
            line = namedOsbLineFormat.format(parameters);
        } else {
            MessageFormat osbFormat = new MessageFormat(currentPropertiesData.getOsbFormat());
            line = osbFormat.format(SpecialClientFormatter.makeOSBLineParameters(this.currentMaket1Data, this.currentPropertiesData, this.operationDate, this.oneDigitYear, this.currentPayerAccount, countPaymentsOsb, sumOsb, SpecialClientGroupHandler.calcClearSum(
                    sumOsb,
                    serviceCommissionOsb), serviceCommissionOsb));
        }
        if (line != null && line.length() > 0) {
            this.outputStream.write((line + newLine).getBytes(currentPropertiesData.getFileEncoding()));
        }
    }

    private void writePaymentOrderLine() throws IOException, SQLException {
        String line;
        if (isNamedMessageFormat(currentPropertiesData.getOrderFormat())) {
            NamedMessageFormat namedPayOrderFormat = new NamedMessageFormat(currentPropertiesData.getOrderFormat());
            Set aliasSet = namedPayOrderFormat.getFieldNames();
            Map parameters = SpecialClientMapFormatter.makePaymentOrderLineParameters(
                    aliasSet,
                    this.currentMaket1Data,
                    this.currentPropertiesData,
                    this.operationDate,
                    this.oneDigitYear,
                    this.currentPayerAccount,
                    countPaymentsFile,
                    sumFile,
                    SpecialClientGroupHandler.calcClearSum(sumFile, serviceCommissionFile),
                    serviceCommissionFile);
            if (config.useRgProps) {
                SpecialClientMapFormatter.searchInRgProps(connection, currentMaket1Data, parameters);
            }
            line = namedPayOrderFormat.format(parameters);
        } else {
            MessageFormat fileFormat = new MessageFormat(currentPropertiesData.getOrderFormat());
            line = fileFormat.format(SpecialClientFormatter.makePaymentOrderLineParameters(this.currentMaket1Data, this.currentPropertiesData, this.operationDate, this.oneDigitYear, this.currentPayerAccount, countPaymentsFile, sumFile, SpecialClientGroupHandler.calcClearSum(
                    sumFile,
                    serviceCommissionFile), serviceCommissionFile));
        }
        if (line != null && line.length() > 0) {
            this.outputStream.write((line + newLine).getBytes(currentPropertiesData.getFileEncoding()));
        }

    }

    /**
     * Имя для zip файла
     * 
     * @return
     * @throws SQLException
     */
    private String getNewZipFileName() throws SQLException {
        String result;
        if (isNamedMessageFormat(currentPropertiesData.getZipFileNameFormat())) {
            NamedMessageFormat namedZipFileFormat = new NamedMessageFormat(currentPropertiesData.getZipFileNameFormat());
            Set aliasSet = namedZipFileFormat.getFieldNames();
            Map parameters = SpecialClientMapFormatter.makeZipFileNameParameters(aliasSet, this.currentMaket1Data, this.currentPropertiesData, this.operationDate, this.oneDigitYear, this.currentPayerAccount, getNewID(operationDate, this.currentPropertiesData.getProviderCode()
                    + this.currentPropertiesData.getProviderDepartment()));
            if (config.useRgProps) {
                SpecialClientMapFormatter.searchInRgProps(connection, currentMaket1Data, parameters);
            }
            result = namedZipFileFormat.format(parameters);
        } else {
            MessageFormat zipFileNameFormat = new MessageFormat(currentPropertiesData.getZipFileNameFormat());
            // {0,date,ddMM}{1}{2}.{3} или {0,date,ddMM}{1}{2}.zip
            result = zipFileNameFormat.format(SpecialClientFormatter.makeZipFileNameParameters(this.currentMaket1Data, this.currentPropertiesData, this.operationDate, this.oneDigitYear, this.currentPayerAccount, getNewID(
                    operationDate,
                    this.currentPropertiesData.getProviderCode() + this.currentPropertiesData.getProviderDepartment())));
        }
        return result;
    }

    /**
     * Имя для обычного файла
     * 
     * @return
     * @throws SQLException
     */
    private String getNewFileName() throws SQLException {
        Calendar cal = Calendar.getInstance();
        cal.setTime(operationDate);
        String result;
        if (isNamedMessageFormat(currentPropertiesData.getFileNameFormat())) {
            NamedMessageFormat namedFileNameFormat = new NamedMessageFormat(currentPropertiesData.getFileNameFormat());
            Set aliasSet = namedFileNameFormat.getFieldNames();
            Map parameters = SpecialClientMapFormatter.makeFileNameParameters(aliasSet, this.currentMaket1Data, this.currentPropertiesData, this.operationDate, this.oneDigitYear, this.currentPayerAccount, this.rr, "", "");
            if (config.useRgProps) {
                SpecialClientMapFormatter.searchInRgProps(connection, currentMaket1Data, parameters);
            }
            result = namedFileNameFormat.format(parameters);
        } else {
            MessageFormat fileNameFormat = new MessageFormat(currentPropertiesData.getFileNameFormat());
            // {0,date,ddMM}{1}{2}.{3}{4} или {0,date,ddMM}{1}{2}.{5,number,000}
            result = fileNameFormat.format(SpecialClientFormatter.makeFileNameParameters(this.currentMaket1Data, this.currentPropertiesData, this.operationDate, this.oneDigitYear, this.currentPayerAccount, this.rr, "", ""));
        }
        return result;
    }

    private long getNewID(Date date, String suffix) {
        SequenceSingle sequence = SequenceFactory.getDailySequence(date, suffix + "_payments");
        return sequence.getNextID();
    }

    private void makeNewZipFile() throws IOException, SQLException {
        boolean zip = currentPropertiesData.getUseZip() != 0;
        String fileName;
        File file;
        if (zip) {
            // Получим имя для zip файла
            fileName = this.getNewZipFileName();
            file = File.createTempFile("payments-", ".zip");
            filesMetadata.add(new FileMetadata(file, currentPropertiesData.getOutputDir(), fileName));
            this.outputStream = new ZipOutputStream(new FileOutputStream(file));
        }
    }

    private void closeZipFile() throws IOException {
        boolean zip = currentPropertiesData.getUseZip() != 0;
        if (zip) {
            if (outputStream != null) {
                this.outputStream.close();
            }
        }
    }

    /**
     * @throws IOException
     *             В зависимости от настроек, нужно либо добавить новое имя
     *             файла в архив, либо добавить в метаданные результатов новый
     *             файл, создав его
     * @throws SQLException
     */
    private String makeNewFile() throws IOException, SQLException {
        String fileName;
        File file;
        boolean zip = currentPropertiesData.getUseZip() != 0;
        fileName = this.getNewFileName();
        if (zip) {
            try {
                ((ZipOutputStream) this.outputStream).putNextEntry(new ZipEntry(fileName));
            } catch (ZipException e) {
                logger.log(Level.SEVERE, "ZipException occured in putNextEntry method, trying to put next entry with another name");
                fileName = fileName + "-" + new Random(Calendar.getInstance().getTimeInMillis()).nextInt(100);
                ((ZipOutputStream) this.outputStream).putNextEntry(new ZipEntry(fileName));
            }
        } else {
            file = File.createTempFile("payments-", ".tmp");
            this.outputStream = new FileOutputStream(file);
            filesMetadata.add(new FileMetadata(file, currentPropertiesData.getOutputDir(), fileName));
        }
        return fileName;
    }

    private void closeFile() {
        boolean zip = currentPropertiesData.getUseZip() != 0;
        try {
            if (zip) {
                ((ZipOutputStream) outputStream).closeEntry();
            } else {
                if (this.outputStream != null) {
                    this.outputStream.close();
                }
            }
        } catch (IOException e) {
            logger.info(this.getClass().getName() + " closeFile(): Error: " + e);
            e.printStackTrace();
        }
    }

    /**
     * Инициализация счетчиков ОСБ
     */
    private void initOsbCounters() {
        sumOsb = 0;
        supenOsb = 0;
        serviceCommissionOsb = 0;
        countPaymentsOsb = 0;
    }

    private void addOsbToFileCounters() {
        sumFile += sumOsb;
        supenFile += supenOsb;
        serviceCommissionFile += serviceCommissionOsb;
        countPaymentsFile += countPaymentsOsb;
    }

    private void initFileCounters() {
        sumFile = 0;
        supenFile = 0;
        serviceCommissionFile = 0;
        countPaymentsFile = 0;
    }

    private void initRR() {
        this.rr = 0;
    }

    private void nextRR() {
        this.rr++;
    }

    /*
     * (non-Javadoc)
     * 
     * @seecom.sbrf.util.sql.group.GroupHandler#error(com.sbrf.util.sql.group.
     * SelectorException)
     */
    public void error(SelectorException exception) throws SelectorException {
        // TODO Попробовать обработать ошибку, чтобы продолжить выполнение
        throw exception;
    }

    /*
     * (non-Javadoc)
     * 
     * @seecom.sbrf.util.sql.group.GroupHandler#fatal(com.sbrf.util.sql.group.
     * SelectorException)
     */
    public void fatal(SelectorException exception) throws SelectorException {
        throw exception;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.sbrf.util.sql.group.GroupHandler#cleanup()
     */
    public void cleanup() {
        if (this.outputStream != null) {
            try {
                this.outputStream.close();
            } catch (IOException e) {
                logger.log(Level.SEVERE, e.getMessage(), e);
            }
        }
        DAOUtil.close(connection);
    }

    private static long calcClearSum(long sum, long serviceCommission) {
        return sum - serviceCommission;
    }

    /**
     * Метод предназначен для получения последней цифры в значении года
     * 
     * @param transferDate
     *            - дата
     * @return
     */
    private Integer getOneDigit(Date transferDate) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(transferDate);
        return new Integer(cal.get(Calendar.YEAR) % 10);
    }

    private boolean isNamedMessageFormat(String format) {
        Pattern pattern = Pattern.compile(".*\\{\\d+.*");
        Matcher matcher = pattern.matcher(format);
        if (matcher.matches()) {
            return false;
        }
        return true;
    }
    
    private void writeErrorToResponeFile() {
        outputInformation.println("Ошибка при обработке ProviderCode = "
                + currentPropertiesData.getProviderCode() + " ProviderDepartment = "
                + currentPropertiesData.getProviderDepartment() + " ServiceKind = "
                + currentPropertiesData.getServiceKind());
        wasErrorInPaymentHandler = true;
    }
}
