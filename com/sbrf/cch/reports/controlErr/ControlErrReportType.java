package com.sbrf.cch.reports.controlErr;

import java.io.IOException;
import java.io.OutputStream;
import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Types;
import java.text.DateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang.StringUtils;
import org.apache.xml.serialize.BaseMarkupSerializer;
import org.apache.xml.serialize.OutputFormat;
import org.apache.xml.serialize.XMLSerializer;
import org.xml.sax.ContentHandler;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.AttributesImpl;

import com.epam.sbrf.report.AbstractSbtReportType;
//import com.epam.sbrf.report.ParametersFactory;
import com.epam.sbrf.report.ReportFormats;
import com.epam.sbrf.report.ReportType;
import com.epam.sbrf.report.exceptions.ReportException;
import com.epam.sbrf.report.model.InputParameter;
import com.epam.sbrf.report.model.Parameter;
import com.epam.sbrf.report.model.InputParameter.DataType;
import com.sberbank.sbclients.util.dao.db2.DAOUtil;
import com.sbrf.bc.plugin.BillingPlugin;
import com.sbrf.bc.plugin.BillingPluginFactory;
import com.sbrf.bc.plugin.PluginConfigurationHelper;
import com.sbrf.bc.plugin.PluginNotFoundException;
import com.sbrf.bc.processor.operday.OperDay;
import com.sbrf.report.util.ReportSettingNames;
import com.sbrf.util.classloader.ResourceHelper;
import com.sbrf.util.logging.SBRFLogger;
import com.sbrf.util.sql.group.DefaultGroupHandler;
import com.sbrf.util.sql.group.GroupHandler;
import com.sbrf.util.sql.group.GroupSelector;
import com.sbrf.util.sql.group.NameFieldDescription;
import com.sbrf.util.sql.group.SelectorException;
import com.sbrf.util.text.LightDateFormat;
//import com.sbrf.util.text.MessageFormat;
import com.sbrf.util.text.NamedMessageFormat;

/**
 * контроль за платежами на перечисление
 * 
 * @author Zhivenkov-AN
 * 
 */
public class ControlErrReportType extends AbstractSbtReportType implements ReportType, BillingPlugin {
    private final Config config;
    private final BillingPluginFactory billingPluginFactory;
    private final Logger logger;
    private final Pattern fetchStatement;
    private static final String FILE_ENCODING = System.getProperty("file.encoding");

    public ControlErrReportType(Properties properties) {
        PluginConfigurationHelper context = new PluginConfigurationHelper(properties);
        this.config = new Config(context);
        this.billingPluginFactory = new BillingPluginFactory();
        this.logger = SBRFLogger.getInstance(this.getClass().getName());
        this.fetchStatement = Pattern.compile("(.*)(fetch\\s+first\\s+\\d+\\s+rows\\s+only)(.*)");

    }

    public Parameter[] getParameters() throws ReportException {
        Date operDay;
        try {
            operDay = ((OperDay) billingPluginFactory.findPlugin(config.operDayPluginName)).getOperationDay();
            String[] inputParams = config.inputParams.split("#");
            Parameter[] params = new Parameter[inputParams.length];
            for (int i = 0; i < inputParams.length; i++) {
                String[] ps = inputParams[i].split(";");
                if (ps[0].equals(DataType.DATE)) {
                    InputParameter param = new InputParameter(ps[1], ps[2], DataType.DATE);
                    param.setLengthLimits(10, 10);
                    param.setRequired(true);
                    param.setDefault(operDay);
                    params[i] = param;
                } else if (ps[0].equals(DataType.STRING)) {
                    InputParameter param = new InputParameter(ps[1], ps[2], DataType.STRING);
                    param.setRequired(true);
                    params[i] = param;
                } else if (ps[0].equals(DataType.INTEGER)) {
                    InputParameter param = new InputParameter(ps[1], ps[2], DataType.INTEGER);
                    param.setRequired(true);
                    params[i] = param;
                } else {
                    InputParameter param = new InputParameter(ps[1], ps[2], ps[0]);
                    param.setRequired(true);
                    params[i] = param;
                    // throw new ReportException("Неизвестный тип параметра.");
                }
            }
            return super.getCommonParameters(params);
        } catch (PluginNotFoundException e) {
            throw new ReportException(e);
        }
    }

    public String[] getFormats() {
        return new String[] { ReportFormats.HTML, ReportFormats.XLS_TABLE_XML, ReportFormats.XML };
    }

    public String getStyle(String format) {
        if (ReportFormats.HTML.equals(format)) {
            Class<?> clazz = this.getClass();
            String resourcePath = config.htmlStylePath;
            URL url = clazz.getResource(resourcePath);
            if (url != null) {
                return resourcePath;
            }
        }
        if (ReportFormats.TXT.equals(format)) {
            Class<?> clazz = this.getClass();
            String resourcePath = config.txtStylePath;
            URL url = clazz.getResource(resourcePath);
            if (url != null) {
                return resourcePath;
            }
        }
        if (ReportFormats.XLS_TABLE_XML.equals(format)) {
            Class<?> clazz = this.getClass();
            String resourcePath = config.exelStylePath;
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

    public void prepareData(Map<Object, Object> inParameters, Map<Object, Object> outParameters, OutputStream data) throws InterruptedException, ReportException {
        Connection connection = null;
        PreparedStatement statement = null;
        ResultSet resultSet = null;
        ContentHandler hd = null;
        String sql = null;
        try {
            OutputFormat format = new OutputFormat("XML", "UTF-8", true);
            BaseMarkupSerializer serializer = new XMLSerializer(data, format);
            hd = serializer.asContentHandler();
            Map<String, Object> inParams = new HashMap<String ,Object>();
            for (Object key : inParameters.keySet()) {
                inParams.put(key.toString(), inParameters.get(key));
            }
            // добавляем условие исключения
            
            inParams.put("uslWhere", config.uslIskluthen);
            inParams.put("uslInn", config.uslInn);
            String sqlFormat = ResourceHelper.getResourceAsString(config.sqlQueryPath, FILE_ENCODING);
            String sql2 = NamedMessageFormat.format(sqlFormat,  inParams);
            logger.info("\n ====ZHAN:"+ sql2 + "\n");
            connection = this.getDatasource(inParameters).getConnection();
            //sql = query;
            sql = sql2;
            if (StringUtils.containsIgnoreCase(sql, "update") || StringUtils.containsIgnoreCase(sql, "insert") || StringUtils.containsIgnoreCase(sql, "delete") || StringUtils.containsIgnoreCase(sql, "merge")) {
                throw new SQLException("Запрос может содержать только select.");
            }
            if (!StringUtils.containsIgnoreCase(sql, "fetch")) {
                sql = sql + " fetch first " + config.maxNumberOfRows + " rows only";
            } else {
                if (config.preventLongQueries) {
                    Matcher matcher = fetchStatement.matcher(sql);
                    if (matcher.matches()) {
                        String toReplace = matcher.group(2);
                        Pattern fetchSeparation = Pattern.compile("(.*\\s+)(\\d+)(\\s+.*)");
                        Matcher matcherNext = fetchSeparation.matcher(toReplace);
                        if (matcherNext.matches()) {
                            String digits = matcherNext.group(2);
                            try {
                                long number = Long.parseLong(digits);
                                if (number > config.maxNumberOfRows) {
                                    sql = StringUtils.replace(sql, toReplace, " fetch first " + config.maxNumberOfRows + " rows only");
                                }
                            } catch (NumberFormatException e) {
                                logger.finest("Can't find and check number of rows to fetch in initial statement.");
                            }
                        }
                    }
                }
            }
            logger.finest(sql);
            statement = connection.prepareStatement(sql);
            resultSet = statement.executeQuery();
            NameFieldDescription fields[] = new NameFieldDescription[0];
            GroupSelector selector = new GroupSelector(fields);
            GroupHandler handler = new UniversalReportHandler(hd, resultSet, config, config.messageHeader);
            selector.parse(resultSet, handler);
        } catch (SQLException e) {
            logger.log(Level.INFO, "Произошла ошибка в запросе", e);
            createExplainFile(hd, e.getMessage(), sql);
        } catch (IOException e) {
            logger.log(Level.INFO, "Произошла ошибка в запросе", e);
            throw new ReportException(e);
        } catch (SelectorException e) {
            logger.log(Level.INFO, "Произошла ошибка в запросе", e);
            throw new ReportException(e);
        } finally {
            DAOUtil.close(resultSet);
            DAOUtil.close(statement);
            DAOUtil.close(connection);
        }
    }

    private void createExplainFile(ContentHandler hd, String message, String sql) throws ReportException {
        try {
            AttributesImpl atts = new AttributesImpl();
            hd.startDocument();
            hd.startElement("", "", "root", atts);

            hd.startElement("", "", "header", atts);
            hd.characters(sql.toCharArray(), 0, sql.length());
            hd.endElement("", "", "header");

            hd.startElement("", "", "error", atts);
            hd.startElement("", "", "message", atts);
            if (StringUtils.isNotBlank(message)) {
                String value = "Произошла ошибка при выполнении запроса. " + message;
                hd.characters(value.toCharArray(), 0, value.length());
            }
            hd.endElement("", "", "message");
            hd.endElement("", "", "error");

            hd.endElement("", "", "root");
            hd.endDocument();
        } catch (SAXException e) {
            throw new ReportException(e);
        }

    }

    private static final class UniversalReportHandler extends DefaultGroupHandler {
        private final ContentHandler hd;
        private final Config config;
        private final AttributesImpl emptyAtts;
        private final ResultSetMetaData meta;
        private final DateFormat dateFormat;
        private final String messageHeader;
        private int rowsCount;

        public UniversalReportHandler(ContentHandler hd, ResultSet resultSet, Config config, String messageHeader) throws SQLException {
            this.hd = hd;
            this.config = config;
            this.emptyAtts = new AttributesImpl();
            this.meta = resultSet.getMetaData();
            this.dateFormat = new LightDateFormat("dd.MM.yyyy");
            this.messageHeader = messageHeader;
            rowsCount = 0;
        }

        public void start() throws SelectorException {
            try {
                hd.startDocument();
                hd.startElement("", "", "root", emptyAtts);

                hd.startElement("", "", "header", emptyAtts);
                hd.characters(messageHeader.toCharArray(), 0, messageHeader.length());
                hd.endElement("", "", "header");

                hd.startElement("", "", "items", emptyAtts);
                makeColumnsDefinition();
                rowsCount = 0;
            } catch (SAXException e) {
                throw new SelectorException(e);
            }
        }

        public void nextRecord(ResultSet resultSet) throws SelectorException {
            try {
                rowsCount++;
                if (rowsCount < config.maxNumberOfRows) {
                    int count = meta.getColumnCount();
                    hd.startElement("", "", "item", emptyAtts);
                    for (int i = 1; i <= count; i++) {
                        AttributesImpl a = new AttributesImpl();
                        // a.addAttribute("", "", "name", "",
                        // meta.getColumnName(i).toLowerCase());
                        hd.startElement("", "", "item_elem", a);
                        if (DAOUtil.getObject(resultSet, i) != null) {
                            String value = "";
                            switch (meta.getColumnType(i)) {
                                case Types.DATE:
                                    value = dateFormat.format(DAOUtil.getDate(resultSet, i));
                                    break;
                                case Types.VARCHAR:
                                    value = DAOUtil.getString(resultSet, i);
                                    break;
                                default:
                                    value = DAOUtil.getString(resultSet, i);
                            }
                            hd.characters(value.toCharArray(), 0, value.length());
                        }
                        hd.endElement("", "", "item_elem");
                    }
                    hd.endElement("", "", "item");
                }
            } catch (SAXException e) {
                throw new SelectorException(e);
            } catch (SQLException e) {
                throw new SelectorException(e);
            }
        }

        public void end() throws SelectorException {
            try {
                makeSummary();
                hd.endElement("", "", "items");
                hd.endElement("", "", "root");
                hd.endDocument();
            } catch (SAXException e) {
                throw new SelectorException(e);
            }
        }

        public void cleanup() {
        }

        public void error(SelectorException exception) throws SelectorException {
            throw exception;
        }

        public void fatal(SelectorException exception) throws SelectorException {
            throw exception;
        }

        private void makeColumnsDefinition() throws SelectorException {
            try {
                int count = meta.getColumnCount();
                if (config.headerNames.length != count && config.useHeaderNames) throw new SelectorException("Количество колонок в запросе не совпадает с заголовком в настройках.");
                hd.startElement("", "", "columns", emptyAtts);
                for (int i = 1; i <= count; i++) {
                    AttributesImpl atts = new AttributesImpl();
                    atts.addAttribute("", "", "size", "", Integer.toString(getHeaderLength(i, meta)));
                    hd.startElement("", "", "column", atts);
                    if (config.useHeaderNames) {
                        hd.characters(config.headerNames[i-1].toCharArray(), 0, config.headerNames[i-1].length());
                    } else {
                        hd.characters(meta.getColumnName(i).toCharArray(), 0, meta.getColumnName(i).length());
                    }
                    hd.endElement("", "", "column");
                }
                hd.endElement("", "", "columns");
            } catch (SAXException e) {
                throw new SelectorException(e);
            } catch (SQLException e) {
                throw new SelectorException(e);
            }
        }

        private int getHeaderLength(int columnNumber, ResultSetMetaData metaData) throws SQLException {
            String colName = metaData.getColumnName(columnNumber);
            int length = metaData.getColumnDisplaySize(columnNumber);
            if (length > 30) {
                length = 30;
            }
            if (length < colName.length()) {
                length = colName.length() + 1;
            }
            return length;
        }

        private void makeSummary() throws SelectorException {
            try {
                hd.startElement("", "", "summary", emptyAtts);

                String value = Integer.toString(rowsCount);
                hd.startElement("", "", "counter", emptyAtts);
                hd.characters(value.toCharArray(), 0, value.length());
                hd.endElement("", "", "counter");

                value = Integer.toString(config.maxNumberOfRows);
                hd.startElement("", "", "maxrows", emptyAtts);
                hd.characters(value.toCharArray(), 0, value.length());
                hd.endElement("", "", "maxrows");

                hd.startElement("", "", "message", emptyAtts);
                if (rowsCount >= config.maxNumberOfRows) {
                    value = "В результате выполнения запроса получено количество записей более допустимого.";
                    hd.characters(value.toCharArray(), 0, value.length());
                }
                hd.endElement("", "", "message");

                hd.endElement("", "", "summary");
            } catch (SAXException e) {
                throw new SelectorException(e);
            }
        }
    }

    private final static class Config {
        final int timeout;
        final String operDayPluginName;
        final String htmlStylePath;
        final String txtStylePath;
        final String exelStylePath;
        final int maxNumberOfRows;
        final boolean preventLongQueries;

        final String messageHeader;
        final String[] headerNames;
        final boolean useHeaderNames;
        final String inputParams;
        final String uslIskluthen;
        final String uslInn;        
        final String sqlQueryPath;

        public Config(PluginConfigurationHelper context) {
            this.timeout = context.getInteger("timeout", 60000);
            this.operDayPluginName = context.getString("operDayPluginName", OperDay.PLUGIN_NAME);
            this.htmlStylePath = context.getString(ReportSettingNames.HTML_STYLE_PATH, "/com/sbrf/cch/reports/controlErr/ControlErrReportType.html.xsl");
            this.txtStylePath = context.getString(ReportSettingNames.TXT_STYLE_PATH, "/com/sbrf/cch/reports/controlErr/ControlErrReportType.txt.xsl");
            this.exelStylePath = context.getString(ReportSettingNames.XLS_STYLE_PATH, "/com/sbrf/cch/reports/controlErr/ControlErrReportType.xls.xsl");
            this.maxNumberOfRows = context.getInteger("maxNumberOfRows", 10000);
            this.preventLongQueries = context.getBoolean("preventLongQueries", true);            
            this.sqlQueryPath = context.getString(ReportSettingNames.SQL_QUERY_PATH, "/com/sbrf/cch/reports/controlErr/ControlBudgetReport.sql");
            this.messageHeader = context.getString("messageHeader", "Корректность заполнения атрибутов платежного поручения");
            this.headerNames = context.getString("headerNames", "Критерий ошибки, Получатель, Назначение платежа, ОСБ, Кодспецклиента, Номер п.п.").split(",");
            this.useHeaderNames = context.getBoolean("useHeaderNames", true);
            this.inputParams = context.getString("inputParams", "date;inputDate1;Дата перечисления");
            this.uslIskluthen = context.getString("uslIskluthen", "'00330','00300'");
            this.uslInn = context.getString("uslInn", "'6831020409','4825024049','3123110760'");            
            
        }
    }

}
