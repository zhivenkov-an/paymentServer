package com.sbrf.cch.reports.formsbm;

import java.io.IOException;
import java.io.OutputStream;
import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.logging.Logger;

import org.apache.xml.serialize.BaseMarkupSerializer;
import org.apache.xml.serialize.OutputFormat;
import org.apache.xml.serialize.XMLSerializer;
import org.xml.sax.ContentHandler;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.AttributesImpl;

import com.epam.sbrf.report.AbstractSbtReportType;
import com.epam.sbrf.report.ParametersFactory;
import com.epam.sbrf.report.ReportFormats;
import com.epam.sbrf.report.ReportType;
import com.epam.sbrf.report.exceptions.ReportException;
import com.epam.sbrf.report.model.InputParameter;
import com.epam.sbrf.report.model.Option;
import com.epam.sbrf.report.model.Parameter;
import com.epam.sbrf.report.model.SelectParameter;
import com.epam.sbrf.state.constants.PaymentStateConstants;
import com.sberbank.sbclients.util.dao.ConnectionSource;
import com.sberbank.sbclients.util.dao.SQLGenerator;
import com.sberbank.sbclients.util.dao.db2.AbstractDAO;
import com.sberbank.sbclients.util.dao.db2.DAOUtil;
import com.sbrf.bc.plugin.BillingPlugin;
import com.sbrf.bc.plugin.PluginConfigurationHelper;
import com.sbrf.bc.processor.operday.OperDay;
import com.sbrf.report.util.ReportSettingNames;
import com.sbrf.util.classloader.ResourceHelper;
import com.sbrf.util.logging.SBRFLogger;
import com.sbrf.util.sql.group.DefaultGroupHandler;
import com.sbrf.util.sql.group.GroupFieldDescription;
import com.sbrf.util.sql.group.GroupHandler;
import com.sbrf.util.sql.group.GroupSelector;
import com.sbrf.util.sql.group.NameFieldDescription;
import com.sbrf.util.sql.group.SelectorException;
import com.sbrf.util.text.MessageFormat;

/**
 * ‚ *.sbm
 * @author zhivenkov-an
 *
 */
public class FormSbmReportType extends AbstractSbtReportType implements ReportType, BillingPlugin {
    private static final String FILE_ENCODING = System.getProperty("file.encoding");
    
    private final Config config;
    private final Option yes;
    private final Option no;
    //private final ConnectionSource connectionSource;
    private final Logger logger;
    
    final Option TRANSFERRED;
    final Option NOT_TRANSFERRED;
    final Option ALL_STATES;

    public FormSbmReportType(Properties properties) {
        PluginConfigurationHelper context = new PluginConfigurationHelper(properties);
        this.config = new Config(context);
        //this.connectionSource = new J2EEContainerConnectionSource(config.connectionSource);
        
        this.yes = new Option("yes", "Р”Р°");
        this.no = new Option("no", "РќРµС‚");
        
        this.TRANSFERRED = new Option("", "");
        this.NOT_TRANSFERRED = new Option("", "");
        this.ALL_STATES = new Option("", "");
        
        this.logger = SBRFLogger.getInstance(this.getClass().getName());
    }

    public String[] getFormats() {
        return new String[] {ReportFormats.XLS_TABLE_XML, ReportFormats.TXT, ReportFormats.HTML, ReportFormats.XML,};
    }

    public String getStyle(String format) {
        if (ReportFormats.HTML.equals(format)) {
            Class clazz = this.getClass();
            String resourcePath = config.htmlStylePath;
            URL url = clazz.getResource(resourcePath);
            if (url != null) {
                return resourcePath;
            }
        }
        if (ReportFormats.TXT.equals(format)) {
            Class clazz = this.getClass();
            String resourcePath = config.txtStylePath;
            URL url = clazz.getResource(resourcePath);
            if (url != null) {
                return resourcePath;
            }
        }
        if (ReportFormats.XLS_TABLE_XML.equals(format)) {
            Class clazz = this.getClass();
            String resourcePath = config.excellStylePath;
            URL url = clazz.getResource(resourcePath);
            if (url != null) {
                return resourcePath;
            }
        }
        return null;
    }

    public int getTimeout() {
        return config.timeout;
    }

    public Parameter[] getParameters() throws ReportException {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.DAY_OF_MONTH, 1);
        calendar.add(Calendar.DAY_OF_YEAR, -1);
        Date endDate = calendar.getTime();
        calendar.set(Calendar.DAY_OF_MONTH, 1);
        Date startDate = calendar.getTime();
        Parameter startDay = ParametersFactory.getOperDay().setDefault(startDate).setId("start_date").setDescription("Начальная дата(включительно)").setLabel("Начальная дата");
        Parameter endDay = ParametersFactory.getOperDay().setDefault(endDate).setId("end_date").setDescription("Конечная дата(включительно)").setLabel("Конечная дата");
        Parameter bik = new InputParameter("bik", "БИК").setDirectory("SPECIFIC_CLIENTS", "BIK", "RECEIVER_NAME").setDescription("Принимает в качестве значения шаблон");
        Parameter corrAcc = new InputParameter("corrAcc", "Корр.счет").setDirectory("SPECIFIC_CLIENTS", "CORR_ACC", "RECEIVER_NAME").setDescription("Принимает в качестве значения шаблон");
        Parameter settleAcc = new InputParameter("settleAcc", "Расч.счет").setDirectory("SPECIFIC_CLIENTS", "SETTLE_ACC", "RECEIVER_NAME").setDescription("Принимает в качестве значения шаблон");
        Parameter inn = new InputParameter("inn", "ИНН получателя").setDirectory("SPECIFIC_CLIENTS", "INN", "RECEIVER_NAME");
        Parameter specClientCode = new InputParameter("specClientCode", "Код спец.клиента").setDirectory("SPECIFIC_CLIENTS", "SPECIAL_CLIENT_CODE", "RECEIVER_NAME").setDescription("Принимает в качестве значения шаблон");
        List options = new ArrayList();
        options.add(yes);
        options.add(no);
        Parameter groupByBik = new SelectParameter("groupByBik", "Группировать по БИК+корр.счету?").setOptions(options);
        Parameter groupBySettleAcc = new SelectParameter("groupBySettleAcc", "Группировать по расч.счету?").setOptions(options);
        Parameter groupByOSB = new SelectParameter("groupByOsb", "Группировать по ОСБ?").setOptions(options);
        Parameter groupBySPC = new SelectParameter("groupBySPC", "Группировать по коду спец.клиента?").setOptions(options);
        Parameter groupByPaymentDate = new SelectParameter("groupByPaymentDate", "Группировать по дате платежа?").setOptions(options);
        Parameter groupByTransferDate = new SelectParameter("groupByTransferDate", "Группировать по дате опер.дня?").setOptions(options);
        
        options = new ArrayList();
        String partQueryKey = " and 1 = 1";
        options.add(new Option(partQueryKey, "Все состояния"));
        partQueryKey = " and (STATE = '" + PaymentStateConstants.TRANSFERRED_STATE + "' or STATE = '" + PaymentStateConstants.RETRANSFERRED_STATE + "')";
        options.add(new Option(partQueryKey, "Перечисленные"));
        partQueryKey = " and (STATE <> '" + PaymentStateConstants.TRANSFERRED_STATE + "' and STATE <> '" + PaymentStateConstants.RETRANSFERRED_STATE + "')";
        options.add(new Option(partQueryKey, "Неперечисленные"));
        SelectParameter payState = new SelectParameter("paymentState", "Состояние платежей");
        payState.setOptions(options);
        return super.getCommonParameters( startDay, endDay, payState, bik, corrAcc, settleAcc, inn, specClientCode, groupByBik, groupBySettleAcc, groupByOSB, groupBySPC, groupByPaymentDate, groupByTransferDate);
    }

    
    public void prepareData(Map inParameters, Map outParameters, OutputStream data) throws InterruptedException, ReportException {
        Connection connection = null;
        PreparedStatement statement = null;
        ResultSet resultSet = null;
        try {
            Date startDate = (Date) inParameters.get("start_date");
            Date endDate = (Date) inParameters.get("end_date");
            String groupByBik = (String) inParameters.get("groupByBik");
            String groupBySettleAcc = (String) inParameters.get("groupBySettleAcc");
            String groupByOSB = (String) inParameters.get("groupByOsb");
            String groupBySPC = (String) inParameters.get("groupBySPC");
            String groupByPaymentDate = (String) inParameters.get("groupByPaymentDate");
            String groupByTransferDate = (String) inParameters.get("groupByTransferDate");
            String addOrderBy = " order by ";
            String addFields = "";
            String groupedBy = "";
            NameFieldDescription[] fieldsList = new NameFieldDescription[7];
            boolean putOrderByComa = false;
            int j = 0;
            if (groupByBik.equals(yes.getKey())) {
                addOrderBy = addOrderBy + " BIK, CORR_ACC";
                addFields = addFields + ", BIK, CORR_ACC";
                putOrderByComa = true;
                fieldsList[j++] = new NameFieldDescription("BIK");
                fieldsList[j++] = new NameFieldDescription("CORR_ACC");
                groupedBy = groupedBy + "/БИК/Номер корр.счета";
            }
            if (groupBySettleAcc.equals(yes.getKey())) {
                if (putOrderByComa) {
                    addOrderBy = addOrderBy + ",";
                }
                putOrderByComa = true;
                addOrderBy = addOrderBy + " SETTLE_ACC";
                addFields = addFields + ", SETTLE_ACC";
                fieldsList[j++] = new NameFieldDescription("SETTLE_ACC");
                groupedBy = groupedBy + "/Номер расч.счета";
            }
            if (groupBySPC.equals(yes.getKey())) {
                if (putOrderByComa) {
                    addOrderBy = addOrderBy + ",";
                }
                putOrderByComa = true;
                addOrderBy = addOrderBy + " SPECIAL_CLIENT_CODE";
                addFields = addFields + ", SPECIAL_CLIENT_CODE";
                fieldsList[j++] = new NameFieldDescription("SPECIAL_CLIENT_CODE");
                groupedBy = groupedBy + "/Код спец. клиента";
            }
            if (groupByOSB.equals(yes.getKey())) {
                if (putOrderByComa) {
                    addOrderBy = addOrderBy + ",";
                }
                putOrderByComa = true;
                addOrderBy = addOrderBy + " OSB";
                addFields = addFields + ", OSB";
                fieldsList[j++] = new NameFieldDescription("OSB");
                groupedBy = groupedBy + "/Номер Отделения";
            }
            if (groupByPaymentDate.equals(yes.getKey())) {
                if (putOrderByComa) {
                    addOrderBy = addOrderBy + ",";
                }
                putOrderByComa = true;
                addOrderBy = addOrderBy + " PAYMENT_DATE";
                addFields = addFields + ", PAYMENT_DATE";
                fieldsList[j++] = new NameFieldDescription("PAYMENT_DATE");
                groupedBy = groupedBy + "/Дата платежа";
            }
            if (groupByTransferDate.equals(yes.getKey())) {
                if (putOrderByComa) {
                    addOrderBy = addOrderBy + ",";
                }
                putOrderByComa = true;
                addOrderBy = addOrderBy + " TRANSFER_DATE";
                addFields = addFields + ", TRANSFER_DATE";
                fieldsList[j++] = new NameFieldDescription("TRANSFER_DATE");
                groupedBy = groupedBy + "/Дата перечисления";
            }
            if (j == 0) {
                groupedBy = groupedBy + "/Суммирование";
                addOrderBy = "";
            }
            ConditionsDAO additionalConditions = new ConditionsDAO(this.getDatasource(inParameters));
            String additionalWhere = additionalConditions.makeSQLConditions(inParameters);
            String sqlFormat = ResourceHelper.getResourceAsString(config.sqlQueryPath, FILE_ENCODING);
            String sql = MessageFormat.format(sqlFormat, new Object[]{addFields, addOrderBy, startDate, endDate, additionalWhere});
            logger.finest(sql);
            long startTime = Calendar.getInstance().getTimeInMillis();
            connection = this.getDatasource(inParameters).getConnection();
            statement = connection.prepareStatement(sql);
            resultSet = statement.executeQuery();
            long executionTime = Calendar.getInstance().getTimeInMillis() - startTime;
            logger.fine(this.getClass().getName() + ": Query executed in " + executionTime + " ms.");
            
            OutputFormat format = new OutputFormat("XML", "UTF-8", true);
            BaseMarkupSerializer serializer = new XMLSerializer(data, format);
            ContentHandler hd = serializer.asContentHandler();
            NameFieldDescription fields[] = new NameFieldDescription[j];
            for (int k = 0; k < j; k++) {
                fields[k] = fieldsList[k];
            }
            String fieldToPrint = "";
            int totalPosition = 0;
            int subSumPosition = 0;
            String subSumField = "";
            String totalSumField = "";
            if (j > 0) {
                fieldToPrint = fields[j-1].getName();
                if (fields[0].getName().equals("BIK")) {
                    if ((fields.length > 2) && (fields[2].getName().equals("SETTLE_ACC"))) {
                        totalPosition = 2;
                    } else {
                        totalPosition = 1;
                    }
                }
                if (totalPosition < fields.length - 1 ) {
                    subSumPosition = totalPosition + 1;
                }
                subSumField = fields[subSumPosition].getName();
                totalSumField = fields[totalPosition].getName();
                if (subSumField.equals(totalSumField)) {
                    subSumField = "";
                }
            }
            GroupSelector selector = new GroupSelector(fields);
            GroupHandler handler = new FormSbmReportHandler(hd, startDate, endDate, groupedBy, fieldToPrint, subSumField, totalSumField);
            selector.parse(resultSet, handler);
        } catch (SQLException e) {
            throw new ReportException(e);
        } catch (SelectorException e) {
            throw new ReportException(e);
        } catch (IOException e) {
            throw new ReportException(e);
        }
    }
    
    /**
     * РћР±СЂР°Р±РѕС‚С‡РёРє РґР»СЏ GroupSelector
     * @author Usenko-VA
     *
     */
    private static final class FormSbmReportHandler extends DefaultGroupHandler {
        private final ContentHandler hd;
        private final Date start;
        private final Date end;
        private final String groupedBy;
        private final DateFormat dateFormat;
        private final AttributesImpl emptyAttributes;
        private final String fieldToPrint;
        private final String subSumField;
        private final String totalSumField;
        private long tailSum;
        private long tailCount;
        private long tailServiceComm;
        private long headSum;
        private long headCount;
        private long headServiceComm;
        private long totalSum;
        private long totalServiceComm;
        private long totalCount;
        
        public FormSbmReportHandler(ContentHandler hd, Date startDate, Date endDate, String groupedBy, String fieldToPrint, String subSumField, String totalSumField) {
            this.hd = hd;
            this.start = startDate;
            this.end = endDate;
            this.groupedBy = groupedBy;
            this.dateFormat = new SimpleDateFormat("dd.MM.yyyy");
            this.emptyAttributes = new AttributesImpl();
            this.fieldToPrint = fieldToPrint;
            this.subSumField = subSumField;
            this.totalSumField = totalSumField;
            tailCount = 0;
            tailServiceComm = 0;
            tailSum = 0;
            headCount = 0;
            headSum = 0;
            headServiceComm = 0;
            totalSum = 0;
            totalServiceComm = 0;
            totalCount = 0;
        }

        public void start() throws SelectorException {
            try {
                hd.startDocument();
                hd.startElement("", "", "root", emptyAttributes);
                
                String date = dateFormat.format(start);
                hd.startElement("", "", "start", emptyAttributes);
                hd.characters(date.toCharArray(), 0, date.length());
                hd.endElement("", "", "start");
                
                date = dateFormat.format(end);
                hd.startElement("", "", "end", emptyAttributes);
                hd.characters(date.toCharArray(), 0, date.length());
                hd.endElement("", "", "end");
                
                hd.startElement("", "", "grouped", emptyAttributes);
                hd.characters(groupedBy.toCharArray(), 0, groupedBy.length());
                hd.endElement("", "", "grouped");
                
                
                hd.startElement("", "", "items", emptyAttributes);
            } catch (SAXException e) {
                throw new SelectorException(e);
            }
        }

        public void startField(GroupFieldDescription description, Object value, ResultSet resultSet) throws SelectorException {
            try {
                AttributesImpl atts = new AttributesImpl();
                if (description.getName().equals("PAYMENT_DATE") || description.getName().equals("TRANSFER_DATE")) {
                    String dateValue = dateFormat.format(DAOUtil.getDate(resultSet, description.getName()));
                    atts.addAttribute("", "", "value", "", dateValue);
                } else {
                    atts.addAttribute("", "", "value", "",  value.toString());
                }
                hd.startElement("", "", description.getName().toLowerCase(),atts);
            } catch (SAXException e) {
                throw new SelectorException(e);
            } catch (SQLException e) {
                throw new SelectorException(e);
            } 
        }

        public void nextRecord(ResultSet resultSet) throws SelectorException {
            try {
                long sum = DAOUtil.get_long(resultSet, "SUM");
                long count = DAOUtil.get_long(resultSet, "COUNTER");
                long servComm = DAOUtil.get_long(resultSet, "SERVICE_COMMISSION");
                tailSum = tailSum + sum;
                tailCount = tailCount + count;
                tailServiceComm = tailServiceComm + servComm;
                headSum = headSum + sum;
                headCount = headCount + count;
                headServiceComm = headServiceComm + servComm;
                totalSum = totalSum + sum;
                totalCount = totalCount + count;
                totalServiceComm = totalServiceComm + servComm;
            } catch (SQLException e) {
                throw new SelectorException(e);
            }
        }

        public void endField(GroupFieldDescription description, Object value) throws SelectorException {
            try {
                if (description.getName().equals(fieldToPrint)) {
                    printSumsElement("item", tailSum, tailCount, tailServiceComm);
                    tailSum = 0;
                    tailServiceComm = 0;
                    tailCount = 0;
                }
                if (description.getName().equals(subSumField)) {
                    printSumsElement("summary", headSum, headCount, headServiceComm);
                    headSum = 0;
                    headServiceComm = 0;
                    headCount = 0;
                }
                if (description.getName().equals(totalSumField)) {
                    printSumsElement("total_summary", totalSum, totalCount, totalServiceComm);
                    totalSum = 0;
                    totalCount = 0;
                    totalServiceComm = 0;
                }
                hd.endElement("", "", description.getName().toLowerCase());
            } catch (SAXException e) {
                throw new SelectorException(e);
            }
        }

        public void end() throws SelectorException {
            try {
                if (totalCount >0) {
                    printSumsElement("total_summary", totalSum, totalCount, totalServiceComm);
                    totalSum = 0;
                    totalCount = 0;
                    totalServiceComm = 0;
                }
                hd.endElement("", "", "items");
                hd.endElement("", "", "root");
            } catch (SAXException e) {
                throw new SelectorException(e);
            }
        }

        public void error(SelectorException exception) throws SelectorException {
        }

        public void fatal(SelectorException exception) throws SelectorException {
        }

        public void cleanup() {
        }
        
        private void printSumsElement(String name, long sum, long count, long serviceComm) throws SAXException {
            hd.startElement("", "", name, emptyAttributes);
            
            String ammount = Long.toString(sum);
            hd.startElement("", "", "sum", emptyAttributes);
            hd.characters(ammount.toCharArray(), 0, ammount.length());
            hd.endElement("", "", "sum");
            
            ammount = Long.toString(serviceComm);
            hd.startElement("", "", "serviceComm", emptyAttributes);
            hd.characters(ammount.toCharArray(), 0, ammount.length());
            hd.endElement("", "", "serviceComm");
            
            ammount = Long.toString(count);
            hd.startElement("", "", "counter", emptyAttributes);
            hd.characters(ammount.toCharArray(), 0, ammount.length());
            hd.endElement("", "", "counter");
            
            hd.endElement("", "", name);
        }
    }
    
    private final static class Config {
        final int timeout;
        final String operDayPluginName;
        //final String connectionSource;
        final String sqlQueryPath;
        final String htmlStylePath;
        final String txtStylePath;
        final String excellStylePath;
        
        public Config(PluginConfigurationHelper context) {
            this.timeout = context.getInteger(ReportSettingNames.TIMEOUT, 120000);
            this.operDayPluginName = context.getString(ReportSettingNames.OPER_DAY_PLUGIN_NAME, OperDay.PLUGIN_NAME);
            //this.connectionSource = context.getString("connectionSource", "java:comp/env/jdbc/UncommittedReadDataSource");
            this.sqlQueryPath = context.getString(ReportSettingNames.SQL_QUERY_PATH, "/com/sbrf/report/types/formsbmreport/FormSbmReportType.sql");
            this.htmlStylePath = context.getString(ReportSettingNames.HTML_STYLE_PATH, "/com/sbrf/report/types/formsbmreport/FormSbmReportType.html.xsl");
            this.txtStylePath = context.getString(ReportSettingNames.TXT_STYLE_PATH, "/com/sbrf/report/types/formsbmreport/FormSbmReportType.txt.xsl");
            this.excellStylePath = context.getString(ReportSettingNames.XLS_STYLE_PATH, "/com/sbrf/report/types/formsbmreport/FormSbmReportType.xls.xsl");
        }

    }
    
    private static final class ConditionsDAO extends AbstractDAO {
        
        public ConditionsDAO (ConnectionSource connectionSource) {
            super(connectionSource);
        }
        
        public String makeSQLConditions(Map inParameters) {
            StringBuffer result = new StringBuffer();
            
            String bik = (String) inParameters.get("bik");
            String corrAcc = (String) inParameters.get("corrAcc");
            String settleAcc = (String) inParameters.get("settleAcc");
            String specClient = (String) inParameters.get("specClientCode");
            String payState = (String) inParameters.get("paymentState");
            String inn = (String) inParameters.get("inn");
            
            result.append(payState);
            SQLGenerator sqlGen = new SQLGenerator();
            makeStringCondition("BIK", bik, sqlGen);
            makeStringCondition("CORR_ACC", corrAcc, sqlGen);
            makeStringCondition("SETTLE_ACC", settleAcc, sqlGen);
            makeStringCondition("INN", inn, sqlGen);
            makeStringCondition("SPECIAL_CLIENT_CODE", specClient, sqlGen);
            if (sqlGen.getWhere() != null) {
                result.append(" and ");
                result.append(sqlGen.getWhere());
            }
            return result.toString();
        }
    }
    
         
}
