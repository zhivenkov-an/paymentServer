package com.sbrf.cch.reports.formrstreport;

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
import java.util.LinkedList;
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
 * Ежемесячный отчет *.rst
 * @author Usenko-VA
 *
 */
public class FormRstReportType extends AbstractSbtReportType implements ReportType, BillingPlugin {
    
    private static final String FILE_ENCODING = System.getProperty("file.encoding");
    private static final String GROUP_BY_PAYMENT_MEANS = "groupByPaymentMeans";
    
    private final Config config;
    private final Option yes;
    private final Option no;
    //private final ConnectionSource connectionSource;
    private final Logger logger;
    
    final Option TRANSFERRED;
    final Option NOT_TRANSFERRED;
    final Option ALL_STATES;

    public FormRstReportType(Properties properties) {
        super(properties);
        PluginConfigurationHelper context = new PluginConfigurationHelper(properties);
        this.config = new Config(context);
        //this.connectionSource = new J2EEContainerConnectionSource("jdbc/VSPDataSource");
        
        this.yes = new Option("yes", "Да");
        this.no = new Option("no", "Нет");
        
        this.TRANSFERRED = new Option("", "");
        this.NOT_TRANSFERRED = new Option("", "");
        this.ALL_STATES = new Option("", "");
        
        this.logger = SBRFLogger.getInstance(this.getClass().getName());
    }

    public String[] getFormats() {
        if (this.config.htmlReportTypeAvailable)
            return new String[] {ReportFormats.XLS_TABLE_XML, ReportFormats.TXT, ReportFormats.HTML, ReportFormats.XML,};
        else
            return new String[] {ReportFormats.XLS_TABLE_XML, ReportFormats.XML,};
    }

    public String getStyle(String format) {
        Class<?> clazz = this.getClass();
        if (ReportFormats.HTML.equals(format)) {
            String resourcePath = config.htmlStylePath;
            URL url = clazz.getResource(resourcePath);
            if (url != null) {
                return resourcePath;
            }
        }
        if (ReportFormats.TXT.equals(format)) {
            String resourcePath = config.txtStylePath;
            URL url = clazz.getResource(resourcePath);
            if (url != null) {
                return resourcePath;
            }
        }
        if (ReportFormats.XLS_TABLE_XML.equals(format)) {
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
        Parameter corrAcc = new InputParameter("corrAcc", "Кор.счет").setDirectory("SPECIFIC_CLIENTS", "CORR_ACC", "RECEIVER_NAME").setDescription("Принимает в качестве значения шаблон");
        Parameter settleAcc = new InputParameter("settleAcc", "Расч.счет").setDirectory("SPECIFIC_CLIENTS", "SETTLE_ACC", "RECEIVER_NAME").setDescription("Принимает в качестве значения шаблон");
        Parameter inn = new InputParameter("inn", "ИНН получателя").setDirectory("SPECIFIC_CLIENTS", "INN", "RECEIVER_NAME");
        Parameter specClientCode = new InputParameter("specClientCode", "Код спец.клиента").setDirectory("SPECIFIC_CLIENTS", "SPECIAL_CLIENT_CODE", "RECEIVER_NAME").setDescription("Принимает в качестве значения шаблон");
        List<Option> options = new ArrayList<Option>();
        options.add(yes);
        options.add(no);
        
        
        Parameter groupByOSB = new SelectParameter("groupByOsb", "Группировать по ОСБ?").setOptions(options);        
        Parameter groupByPaymentDate = new SelectParameter("groupByPaymentDate", "Группировать по дате платежа?").setOptions(options);
        Parameter groupByTransferDate = new SelectParameter("groupByTransferDate", "Группировать по дате опер.дня?").setOptions(options);
        Parameter groupByServiceChannel = new SelectParameter("groupByServiceChannel", "Группировать по каналам приема?").setOptions(options);
        Parameter groupByPaymentMeans = new SelectParameter(GROUP_BY_PAYMENT_MEANS, "Группировать по платежному средству?").setOptions(options);
        // добавляем свои параметры группировки
        Parameter groupByFilial = new SelectParameter("groupByFilial", "Группировать по филиалам?").setOptions(options);
        Parameter groupByCashier = new SelectParameter("groupByCashier", "Группировать по операторам?").setOptions(options);
        
        // меняем флаги нет и да 
        options.remove(yes);
        options.remove(no);
        options.add(no);
        options.add(yes);
        Parameter groupByBik = new SelectParameter("groupByBik", "Группировать по БИК+корр.счету?").setOptions(options);
        Parameter groupBySettleAcc = new SelectParameter("groupBySettleAcc", "Группировать по расч.счету?").setOptions(options);
        Parameter groupBySPC = new SelectParameter("groupBySPC", "Группировать по коду спец.клиента?").setOptions(options);
        
        options = new ArrayList<Option>();
        String partQueryKey = " and 1 = 1";
        options.add(new Option(partQueryKey, "Все состояния"));
        partQueryKey = " and (STATE = '" + PaymentStateConstants.TRANSFERRED_STATE + "' or STATE = '" + PaymentStateConstants.RETRANSFERRED_STATE + "')";
        options.add(new Option(partQueryKey, "Перечисленные"));
        partQueryKey = " and (STATE <> '" + PaymentStateConstants.TRANSFERRED_STATE + "' and STATE <> '" + PaymentStateConstants.RETRANSFERRED_STATE + "')";
        options.add(new Option(partQueryKey, "Неперечисленные"));
        SelectParameter payState = new SelectParameter("paymentState", "Состояние платежей");
        payState.setOptions(options);
        return super.getCommonParameters( startDay, endDay, payState, bik, corrAcc, settleAcc, inn, specClientCode, groupByBik, groupBySettleAcc, groupByOSB, groupBySPC, groupByPaymentDate, groupByTransferDate, groupByServiceChannel, groupByPaymentMeans, groupByFilial, groupByCashier );
    }

    
    public void prepareData(Map<Object, Object> inParameters, Map<Object, Object> outParameters, OutputStream data) throws InterruptedException, ReportException {
        Connection connection = null;
        PreparedStatement statement = null;
        ResultSet resultSet = null;
        try {
            Date startDate = (Date) inParameters.get("start_date");
            Date endDate = (Date) inParameters.get("end_date");
            final String yesValue = (String)yes.getKey();
            boolean groupByBik = yesValue.equals( inParameters.get("groupByBik") );
            boolean groupBySettleAcc = yesValue.equals( inParameters.get("groupBySettleAcc") );
            boolean groupByOSB = yesValue.equals( inParameters.get("groupByOsb") );
            boolean groupBySPC = yesValue.equals( inParameters.get("groupBySPC") );
            boolean groupByPaymentDate = yesValue.equals( inParameters.get("groupByPaymentDate") );
            boolean groupByTransferDate = yesValue.equals( inParameters.get("groupByTransferDate") );
            boolean groupByServiceChannel = yesValue.equals( inParameters.get("groupByServiceChannel") );
            boolean groupByPaymentMeans = yesValue.equals( inParameters.get(GROUP_BY_PAYMENT_MEANS) );
            boolean groupByFilial = yesValue.equals( inParameters.get("groupByFilial") );
            boolean groupByCashier = yesValue.equals( inParameters.get("groupByCashier") );
            
            String addOrderBy = " order by ";
            String addFields = "";
            String groupByFields = "";
            String groupedByXls = "";
            String joints = "";
            //NameFieldDescription[] fieldsList = new NameFieldDescription[8];
            List<NameFieldDescription> fieldsList = new LinkedList<NameFieldDescription>();
            boolean putOrderByComa = false;
            //int j = 0;
            if (groupByBik) {
                addOrderBy = addOrderBy + " BIK, CORR_ACC";
                addFields = addFields + ", BIK, CORR_ACC";
                putOrderByComa = true;
                fieldsList.add(new NameFieldDescription("BIK") );
                fieldsList.add(new NameFieldDescription("CORR_ACC"));
                groupedByXls = groupedByXls + "/БИК/Номер корр.счета";
            }
            if (groupBySettleAcc) {
                if (putOrderByComa) {
                    addOrderBy = addOrderBy + ",";
                }
                putOrderByComa = true;
                addOrderBy = addOrderBy + " SETTLE_ACC";
                addFields = addFields + ", SETTLE_ACC";
                fieldsList.add(new NameFieldDescription("SETTLE_ACC"));
                groupedByXls = groupedByXls + "/Номер расч.счета";
            }
            if (groupBySPC) {
                if (putOrderByComa) {
                    addOrderBy = addOrderBy + ",";
                }
                putOrderByComa = true;
                addOrderBy = addOrderBy + " SPECIAL_CLIENT_CODE";
                addFields = addFields + ", SPECIAL_CLIENT_CODE";
                fieldsList.add(new NameFieldDescription("SPECIAL_CLIENT_CODE"));
                groupedByXls = groupedByXls + "/Код спец. клиента";
            }
            if (groupByOSB) {
                if (putOrderByComa) {
                    addOrderBy = addOrderBy + ",";
                }
                putOrderByComa = true;
                addOrderBy = addOrderBy + " OSB";
                addFields = addFields + ", OSB";
                fieldsList.add(new NameFieldDescription("OSB"));
                groupedByXls = groupedByXls + "/Номер Отделения";
            }
            if (groupByFilial) {
                if (putOrderByComa) {
                    addOrderBy = addOrderBy + ",";
                }
                putOrderByComa = true;
                addOrderBy = addOrderBy + " FILIAL";
                addFields = addFields + ", FILIAL";
                fieldsList.add(new NameFieldDescription("FILIAL"));
                groupedByXls = groupedByXls + "/Номер Филиала";
            }
            if (groupByCashier) {
                if (putOrderByComa) {
                    addOrderBy = addOrderBy + ",";
                }
                putOrderByComa = true;
                addOrderBy = addOrderBy + " CASHIER";
                addFields = addFields + ", CASHIER";
                fieldsList.add(new NameFieldDescription("CASHIER"));
                groupedByXls = groupedByXls + "/Номер Оператора";
            }
            groupByFields = addFields;
            if (groupByServiceChannel) {
                if (putOrderByComa) {
                    addOrderBy = addOrderBy + ",";
                }
                putOrderByComa = true;
                addOrderBy = addOrderBy + " sc.NAME";
                addFields = addFields + ", sc.NAME as SERVICE_CHANNEL_NAME";
                groupByFields = groupByFields +  ", sc.NAME";
                fieldsList.add(new NameFieldDescription("SERVICE_CHANNEL_NAME"));
                groupedByXls = groupedByXls + "/Канал обслуживания";
                joints += " left join PRP.SERVICE_CHANNEL sc on sc.ALIAS = P.service_channel_alias ";
            }            
            
            if (groupByPaymentMeans) {
                if (putOrderByComa) {
                    addOrderBy = addOrderBy + ",";
                }
                putOrderByComa = true;
                addOrderBy = addOrderBy + " pm.NAME";
                addFields = addFields + ", pm.NAME as PAYMENT_MEANS_NAME";
                groupByFields = groupByFields +  ", pm.NAME";
                fieldsList.add(new NameFieldDescription("PAYMENT_MEANS_NAME"));
                groupedByXls = groupedByXls + "/Платежное средство";
                joints += " left join PRP.PAYMENT_MEANS pm on pm.ALIAS = P.payment_means_alias ";
            }
            
            if (groupByPaymentDate) {
                if (putOrderByComa) {
                    addOrderBy = addOrderBy + ",";
                }
                putOrderByComa = true;
                addOrderBy = addOrderBy + " PAYMENT_DATE";
                addFields = addFields + ", PAYMENT_DATE";
                groupByFields = groupByFields + ", PAYMENT_DATE";
                fieldsList.add(new NameFieldDescription("PAYMENT_DATE"));
                groupedByXls = groupedByXls + "/Дата платежа";
            }
            if (groupByTransferDate) {
                if (putOrderByComa) {
                    addOrderBy = addOrderBy + ",";
                }
                putOrderByComa = true;
                addOrderBy = addOrderBy + " TRANSFER_DATE";
                addFields = addFields + ", TRANSFER_DATE";
                groupByFields = groupByFields + ", TRANSFER_DATE";
                fieldsList.add(new NameFieldDescription("TRANSFER_DATE"));
                groupedByXls = groupedByXls + "/Дата перечисления";
            }
            if (fieldsList.size() == 0) {
                groupedByXls = groupedByXls + "/Суммирование";
                addOrderBy = "";
            }
            ConditionsDAO additionalConditions = new ConditionsDAO(this.getDatasource(inParameters));
            String additionalWhere = additionalConditions.makeSQLConditions(inParameters);
            String sqlFormat = ResourceHelper.getResourceAsString(config.sqlQueryPath, FILE_ENCODING);
            String sql = MessageFormat.format(sqlFormat, new Object[]{addFields, addOrderBy, startDate, endDate, additionalWhere, groupByFields, joints});
            logger.info("\n ====ZHAN:"+ sql);
            long startTime = Calendar.getInstance().getTimeInMillis();
            connection = this.getDatasource(inParameters).getConnection();
            statement = connection.prepareStatement(sql);
            
            resultSet = statement.executeQuery();
            long executionTime = Calendar.getInstance().getTimeInMillis() - startTime;
            logger.info(this.getClass().getSimpleName() + ": Query executed in " + executionTime + " ms.");
            
            OutputFormat format = new OutputFormat("XML", "UTF-8", true);
            BaseMarkupSerializer serializer = new XMLSerializer(data, format);
            ContentHandler hd = serializer.asContentHandler();
            NameFieldDescription fields[] = new NameFieldDescription[fieldsList.size()];
            int k = 0;
            for (NameFieldDescription fieldDescription : fieldsList) {
                fields[k++] = fieldDescription;
            }
            /* for (int k = 0; k < j; k++) {
                fields[k] = fieldsList[k];
            } */
            String fieldToPrint = "";
            int totalPosition = 0;
            int subSumPosition = 0;
            String subSumField = "";
            String totalSumField = "";
            if (fieldsList.size() > 0) {
                fieldToPrint = fields[fieldsList.size()-1].getName();
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
            GroupHandler handler = new FormRstReportHandler(hd, startDate, endDate, groupedByXls, fieldToPrint, subSumField, totalSumField);
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
     * Обработчик для GroupSelector
     * @author Usenko-VA
     *
     */
    private static final class FormRstReportHandler extends DefaultGroupHandler {
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
        private long tailPayerComm;
        private long headSum;
        private long headCount;
        private long headServiceComm;
        private long headPayerComm;
        private long totalSum;
        private long totalServiceComm;
        private long totalPayerComm;
        private long totalCount;
        
        public FormRstReportHandler(ContentHandler hd, Date startDate, Date endDate, String groupedBy, String fieldToPrint, String subSumField, String totalSumField) {
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
            tailPayerComm = 0;
            tailSum = 0;
            headCount = 0;
            headSum = 0;
            headServiceComm = 0;
            headPayerComm = 0;
            totalSum = 0;
            totalServiceComm = 0;
            totalPayerComm = 0;
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
                    if (value != null) {
                        atts.addAttribute("", "", "value", "",  value.toString());
                    } else {
                        atts.addAttribute("", "", "value", "",  "не определен");                        
                    }
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
                long payerComm = DAOUtil.get_long(resultSet, "PAYER_COMMISSION");
                tailSum = tailSum + sum;
                tailCount = tailCount + count;
                tailServiceComm = tailServiceComm + servComm;
                tailPayerComm = tailPayerComm + payerComm;
                headSum = headSum + sum;
                headCount = headCount + count;
                headServiceComm = headServiceComm + servComm;
                headPayerComm = headPayerComm + payerComm;
                totalSum = totalSum + sum;
                totalCount = totalCount + count;
                totalServiceComm = totalServiceComm + servComm;
                totalPayerComm = totalPayerComm + payerComm;
            } catch (SQLException e) {
                throw new SelectorException(e);
            }
        }

        public void endField(GroupFieldDescription description, Object value) throws SelectorException {
            try {
                if (description.getName().equals(fieldToPrint)) {
                    printSumsElement("item", tailSum, tailCount, tailServiceComm, tailPayerComm);
                    tailSum = 0;
                    tailServiceComm = 0;
                    tailPayerComm = 0;
                    tailCount = 0;
                }
                if (description.getName().equals(subSumField)) {
                    printSumsElement("summary", headSum, headCount, headServiceComm, headPayerComm);
                    headSum = 0;
                    headServiceComm = 0;
                    headPayerComm = 0;
                    headCount = 0;
                }
                if (description.getName().equals(totalSumField)) {
                    printSumsElement("total_summary", totalSum, totalCount, totalServiceComm,totalPayerComm);
                    totalSum = 0;
                    totalCount = 0;
                    totalServiceComm = 0;
                    totalPayerComm = 0;
                }
                hd.endElement("", "", description.getName().toLowerCase());
            } catch (SAXException e) {
                throw new SelectorException(e);
            }
        }

        public void end() throws SelectorException {
            try {
                if (totalCount >0) {
                    printSumsElement("total_summary", totalSum, totalCount, totalServiceComm, totalPayerComm);
                    totalSum = 0;
                    totalCount = 0;
                    totalServiceComm = 0;
                    totalPayerComm = 0;
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
        
        private void printSumsElement(String name, long sum, long count, long serviceComm, long payerComm) throws SAXException {
            hd.startElement("", "", name, emptyAttributes);
            
            String ammount = Long.toString(sum);
            hd.startElement("", "", "sum", emptyAttributes);
            hd.characters(ammount.toCharArray(), 0, ammount.length());
            hd.endElement("", "", "sum");
            
            ammount = Long.toString(serviceComm);
            hd.startElement("", "", "serviceComm", emptyAttributes);
            hd.characters(ammount.toCharArray(), 0, ammount.length());
            hd.endElement("", "", "serviceComm");
            
            ammount = Long.toString(payerComm);
            hd.startElement("", "", "payerComm", emptyAttributes);
            hd.characters(ammount.toCharArray(), 0, ammount.length());
            hd.endElement("", "", "payerComm");
            
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
        final boolean htmlReportTypeAvailable;
        
        public Config(PluginConfigurationHelper context) {
            this.timeout = context.getInteger(ReportSettingNames.TIMEOUT, 120000);
            this.operDayPluginName = context.getString(ReportSettingNames.OPER_DAY_PLUGIN_NAME, OperDay.PLUGIN_NAME);
            //this.connectionSource = context.getString("connectionSource", "jdbc/VSPDataSource");
            this.sqlQueryPath = context.getString(ReportSettingNames.SQL_QUERY_PATH, "/com/sbrf/cch/reports/formrstreport/FormRstReportType.sql");
            this.htmlStylePath = context.getString(ReportSettingNames.HTML_STYLE_PATH, "/com/sbrf/report/types/formrstreport/FormRstReportType.html.xsl");
            this.txtStylePath = context.getString(ReportSettingNames.TXT_STYLE_PATH, "/com/sbrf/report/types/formrstreport/FormRstReportType.txt.xsl");
            this.excellStylePath = context.getString(ReportSettingNames.XLS_STYLE_PATH, "/com/sbrf/cch/reports/formrstreport/FormRstReportType.xls.xsl");
            this.htmlReportTypeAvailable = context.getBoolean(ReportSettingNames.HTML_FORMAT_ENABLED, false);
        }

    }
    
    private static final class ConditionsDAO extends AbstractDAO {
        
        public ConditionsDAO (ConnectionSource connectionSource) {
            super(connectionSource);
        }
        
        public String makeSQLConditions(Map<Object, Object> inParameters) {
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
