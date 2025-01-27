package com.sbrf.report.types.clientletter.cch;

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
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang.StringUtils;
import org.apache.xml.serialize.BaseMarkupSerializer;
import org.apache.xml.serialize.OutputFormat;
import org.apache.xml.serialize.XMLSerializer;
import org.xml.sax.ContentHandler;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.AttributesImpl;

import com.epam.sbrf.bc.config.CommonConfigPlugin;
import com.epam.sbrf.bc.data.PaymentOrder;
import com.epam.sbrf.common.util.DateUtil;
import com.epam.sbrf.report.ParametersFactory;
import com.epam.sbrf.report.ReportFormats;
import com.epam.sbrf.report.ReportOptions;
import com.epam.sbrf.report.ReportType;
import com.epam.sbrf.report.exceptions.ReportException;
import com.epam.sbrf.report.model.InputParameter;
import com.epam.sbrf.report.model.Option;
import com.epam.sbrf.report.model.Parameter;
import com.epam.sbrf.report.model.SelectParameter;
import com.epam.sbrf.report.model.InputParameter.DataType;
import com.sberbank.sbclients.util.dao.ConnectionSource;
import com.sberbank.sbclients.util.dao.J2EEContainerConnectionSource;
import com.sberbank.sbclients.util.dao.db2.DAOUtil;
import com.sbrf.bc.plugin.BillingPlugin;
import com.sbrf.bc.plugin.BillingPluginFactory;
import com.sbrf.bc.plugin.PluginConfigurationHelper;
import com.sbrf.bc.plugin.PluginNotFoundException;
import com.sbrf.report.util.ReportPaymentOrderUtil;
import com.sbrf.report.util.ReportSettingNames;
import com.sbrf.util.classloader.ResourceHelper;
import com.sbrf.util.text.LightDateFormat;
import com.sbrf.util.text.MessageFormat;

public class ClientLetterReportType implements ReportType, BillingPlugin {
    private final Config config;
    private final ConnectionSource connectionSource;
    private final CommonConfigPlugin commonConfigPlugin;
    private ThreadLocal<String> letterStyleConfig;
    
    final Option fromPayment = new Option("fromPayment", "Определить по платежу");
    final Option sbol = new Option("sbol", "Сбербанк Онл@йн");
    final Option osb = new Option("osb", "В отделении");
    final Option selfServ = new Option("selfServ", "Устройство самообслуживания");
    
    final Option letterWithTemplate = new Option("withTemplate", "Бланк с текстом");
    final Option letterTextOnly = new Option("textOnly", "Только текст");
    
    
    public ClientLetterReportType(Properties properties) throws ReportException {
        PluginConfigurationHelper context = new PluginConfigurationHelper(properties);
        this.config = new Config(context);
        this.connectionSource = new J2EEContainerConnectionSource();
        this.letterStyleConfig = new ThreadLocal<String>();
        try {
            this.commonConfigPlugin = (CommonConfigPlugin) new BillingPluginFactory().findPlugin(CommonConfigPlugin.PLUGIN_NAME);
        } catch (PluginNotFoundException e) {
            throw new ReportException(e);
        }
    }

    public Parameter[] getParameters() throws ReportException {
        Parameter linum = new InputParameter("linum", "ID платежа", DataType.INTEGER).setRequired(true).setDescription("Идентификатор платежа");
        List<Option> options = new ArrayList<Option>();
        options.add(fromPayment);
        options.add(osb);
        options.add(sbol);
        options.add(selfServ);
        Parameter clientName = new InputParameter("client", "Для клиента").setDescription("Имя клиента. Если имя не указано, используется информация из платежа");
        Parameter clientAddress = new InputParameter("clientAddress", "Адрес клиента").setDescription("Адрес клиента. Если имя не указано, используется информация из платежа");
        Parameter clientIO = new InputParameter("clientIO", "Имя Отчетство (для обращения)").setDescription("Имя Отчетство. Если не указано, используется обращение плательщик");
        Parameter numberLetter = new InputParameter("numberLetter", "Исходящий №").setDescription("Исходящий №");
        Parameter dayLetter = ParametersFactory.getOperDay().setId("dayLetter");
        dayLetter.setDescription("Дата письма");
        dayLetter.setLabel("Дата письма");
        dayLetter.setRequired(false);
        Parameter system = new SelectParameter("system", "Способ оплаты").setOptions(options).setDescription("Способ оплаты. Определенный по платежу способ оплаты может быть недостоверным.");
        Parameter assigner = new SelectParameter("assigner", "Подписант").setOptions(config.signersList);
        SelectParameter styleSheetConfig = new SelectParameter("styleSheetConfig","Оформление");
        options = new ArrayList<Option>();
        options.add(letterTextOnly);        
        options.add(letterWithTemplate);
        styleSheetConfig.setOptions(options);
        return new Parameter[] { linum, clientName, clientAddress, system, assigner, styleSheetConfig, clientIO, numberLetter, dayLetter};
    }

    public String[] getFormats() {
        return new String[] {ReportFormats.RTF, ReportFormats.TXT, ReportFormats.XML};
    }

    @SuppressWarnings("unchecked")
    public String getStyle(String format) {
        if (ReportFormats.TXT.equals(format)) {
            Class clazz = this.getClass();
            String resourcePath = config.txtStylePath;
            URL url = clazz.getResource(resourcePath);
            if (url != null) {
                return resourcePath;
            }
        }
        if (ReportFormats.RTF.equals(format)) {
            Class clazz = this.getClass();
            String resourcePath = null;
            if (letterWithTemplate.getKey().equals(letterStyleConfig.get())) {
                resourcePath = config.rtfStylePath;
            } else {
                resourcePath = config.rtfWithoutTemplatePath;
            }
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

    public void prepareData(Map inParameters, Map outParameters, OutputStream data) throws InterruptedException, ReportException {
        outParameters.put(ReportOptions.TXT_ENCODING, "windows-1251");
        Connection connection = null;
        PreparedStatement statement = null;
        ResultSet resultSet = null;
        try {
            int linum = ((Integer) inParameters.get("linum")).intValue();
            String client = (String) inParameters.get("client");
            String clientAddress = (String) inParameters.get("clientAddress");
            String clientIO = (String) inParameters.get("clientIO");            
            String numberLetter = (String) inParameters.get("numberLetter");
            Date dayLetter = (Date) inParameters.get("dayLetter");            
            String assigner = (String) inParameters.get("assigner");
            String systemName = (String) inParameters.get("system");
            letterStyleConfig.set((String)inParameters.get("styleSheetConfig"));
            
            String sql = ResourceHelper.getResourceAsString(config.sqlQueryPath);
            sql = new MessageFormat(sql).format(new Object[]{ });
            connection = connectionSource.getConnection();
            statement = connection.prepareStatement(sql);
            int i = 1;
            DAOUtil.setString(statement, i++, commonConfigPlugin.getTerbank());
            DAOUtil.setInt(statement, i++, linum);
            DAOUtil.setString(statement, i++, commonConfigPlugin.getTerbank());
            DAOUtil.setInt(statement, i++, linum);
            resultSet = statement.executeQuery();
            serializeResultSet(resultSet, data, connection, systemName, assigner, client, clientAddress, clientIO, numberLetter, dayLetter);
        } catch (SQLException e) {
            throw new ReportException(e);
        } finally {
            DAOUtil.close(resultSet);
            DAOUtil.close(statement);
            DAOUtil.close(connection);
        }
    }

    private void serializeResultSet(ResultSet resultSet, OutputStream out, Connection connection, String systemName, String assigner, String client, String clientAddress, String clientIO, String numberLetter, Date dayLetter) throws ReportException {
        try {
            OutputFormat format = new OutputFormat("XML", "windows-1251", false);
            BaseMarkupSerializer serializer = new XMLSerializer(out, format);
            ContentHandler hd = serializer.asContentHandler();
            AttributesImpl emptyAtts = new AttributesImpl();

            Pattern number = Pattern.compile(".*(№\\s*[\\d]{4})");
            DateFormat dateFormat = new LightDateFormat("dd.MM.yyyy");

            ResultSetMetaData metaData = resultSet.getMetaData();

            hd.startDocument();
            hd.startElement("", "", "root", emptyAtts);
            hd.startElement("", "", "items", emptyAtts);


            int counter = metaData.getColumnCount();
            if (resultSet.next()) {
                AttributesImpl atts = new AttributesImpl();
                String system = null;
                if (fromPayment.getKey().equals(systemName)) {
                    system = definePaymentSystem(resultSet);
                } else {
                    system =systemName;
                }
                atts.addAttribute("", "", "type", "", system);
                hd.startElement("", "", "item", atts);
                for (int i = 1; i <= counter; i++) {
                    hd.startElement("", "", metaData.getColumnName(i).toLowerCase(), emptyAtts);
                    String val = null;
                    switch (metaData.getColumnType(i)) {
                        case Types.DATE:
                            val = dateFormat.format(DAOUtil.getDate(resultSet, i));
                            break;
                        case Types.VARCHAR:
                            val = DAOUtil.getString(resultSet, i);
                            break;
                        case Types.BIGINT:
                            val = Long.toString(DAOUtil.get_long(resultSet, i));
                            break;
                        default:
                            DAOUtil.getString(resultSet, i);
                    }
                    if (val == null) {
                        val = "";
                    }
                    if (metaData.getColumnName(i).equalsIgnoreCase("OSB_NAME")) {
                        Matcher matcher = number.matcher(val);
                        if (matcher.matches()) {
                            String toRemove = matcher.group(1);
                            val = StringUtils.remove(val, toRemove);
                        }
                    }
                    if ((metaData.getColumnName(i).equalsIgnoreCase("PAYER_INFO")) && StringUtils.isNotBlank(client)) {
                        val = client;
                    }
                    if ((metaData.getColumnName(i).equalsIgnoreCase("PAYER_ADDRESS")) && StringUtils.isNotBlank(clientAddress)) {
                        val = clientAddress;
                    }
                    
                    hd.characters(val.toCharArray(), 0, val.length());
                    hd.endElement("", "", metaData.getColumnName(i).toLowerCase());

                }
                String value = "";
                long paymentSum = DAOUtil.get_long(resultSet, "SUM");
                
                value = Long.toString(paymentSum / 100);
                writeNode(hd, "sum_rub", value, emptyAtts);
                value = Long.toString(paymentSum % 100);
                writeNode(hd, "sum_cent", value, emptyAtts);
                
                List<PaymentOrder> paymentOrders = ReportPaymentOrderUtil.findAllPaymentOrdersForPayment(connection, DAOUtil.get_long(resultSet, "LINUM"), true);
                if (paymentOrders.size() > 0) {
                    PaymentOrder lastOrder = paymentOrders.get(0);
                    value = Long.toString(lastOrder.getSum() / 100);
                    writeNode(hd, "pay_order_sum_rub", value, emptyAtts);
                    value = Long.toString(lastOrder.getSum() % 100);
                    writeNode(hd, "pay_order_sum_cent", value, emptyAtts);
                    
                    value = "no";
                    if (paymentSum == lastOrder.getSum() + DAOUtil.get_long(resultSet, "SERVICE_COMMISSION")) {
                        value = "yes";
                    }
                    writeNode(hd, "single_pay_order", value, emptyAtts);
                    writeNode(hd, "order_num", lastOrder.getOrderNum(), emptyAtts);
                    writeNode(hd, "order_transfer_date", DateUtil.formatDate(lastOrder.getTransferDate()), emptyAtts);
                    writeNode(hd, "order_bik", lastOrder.getBik(), emptyAtts);
                    writeNode(hd, "order_inn", lastOrder.getInn(), emptyAtts);
                    writeNode(hd, "order_corr_acc", lastOrder.getCorrAccount(), emptyAtts);
                    writeNode(hd, "order_settle_acc", lastOrder.getSettleAcc(), emptyAtts);
                    writeNode(hd, "order_kbk", lastOrder.getKbk(), emptyAtts);
                    writeNode(hd, "order_kpp", lastOrder.getKpp(), emptyAtts);
                    writeNode(hd, "order_okato", lastOrder.getOkato(), emptyAtts);
                    writeNode(hd, "order_receiver_name", lastOrder.getReceiverName(), emptyAtts);
                }

                writeNode(hd, "assigner", assigner, emptyAtts);
                
                value = config.signersMap.get(assigner);
                if (value == null) {
                    value = "";
                }
                writeNode(hd, "assignerPosition", value, emptyAtts);
                // Добавляем поля Имя отчетство
                if (clientIO != null) {
                    value = clientIO;
                }
                else {
                    value = "";
                    }
                writeNode(hd, "fio_payer", value, emptyAtts);
                // Добавляем поле номер
                if (numberLetter != null) {
                    value = numberLetter;
                }
                else {
                    value = "";
                    }
                writeNode(hd, "number_letter", value, emptyAtts);
             // Добавляем поле дата письма
                if (dayLetter != null) {
                    value = dateFormat.format(dayLetter);
                }
                else {
                    value = "";
                    }
                writeNode(hd, "date_letter", value, emptyAtts);
                hd.endElement("", "", "item");
            }

            hd.endElement("", "", "items");
            hd.endElement("", "", "root");
            hd.endDocument();
        } catch (IOException e) {
            throw new ReportException(e);
        } catch (SAXException e) {
            throw new ReportException(e);
        } catch (SQLException e) {
            throw new ReportException(e);
        }
    }
    
    private void writeNode(ContentHandler hd, String name, String value, AttributesImpl atts) throws SAXException {
        hd.startElement("", "", name, atts);
        if (value != null) {
            hd.characters(value.toCharArray(), 0, value.length());
        }
        hd.endElement("", "", name);
    }

    private String definePaymentSystem(ResultSet resultSet) throws SQLException {
        String cashier = DAOUtil.getString(resultSet, "CASHIER");
        String filial = DAOUtil.getString(resultSet, "FILIAL");
        int paymentType = DAOUtil.get_int(resultSet, "PAYMENT_TYPE");
        int filialDigit = filial.charAt(filial.length() - 1) - '0';
        String filialFourDigits = "";
        if (filial.length() >= 4) {
            filialFourDigits = filial.substring(0, 4);
        }
        String result = "selfServ";
        if ((paymentType == 0 && cashier.length() == 5)
                || (!"99999".equals(cashier) && cashier.length() == 5 && paymentType != 0)) {
            result = "osb";
        } else if ((paymentType != 0) && 
                "99999".equals(cashier) &&
                        filialDigit == 4 && config.oldOsbList.contains(filialFourDigits)) {
            result = "sbol";
        }
        return result;
    }

    private final static class Config {
        final int timeout;
        final String sqlQueryPath;
        final String txtStylePath;
        final String rtfStylePath;
        final String rtfWithoutTemplatePath;
        final String signers;
        final String oldOsbString;
        List<String> oldOsbList;
        Map<String, String> signersMap;
        List<Option> signersList;

        public Config(PluginConfigurationHelper context) {
            this.timeout = context.getInteger("timeout", 600000);
            this.sqlQueryPath = context.getString(ReportSettingNames.SQL_QUERY_PATH, "/com/sbrf/report/types/clientletter/cch/ClientLetterReportType.sql");
            this.txtStylePath = context.getString(ReportSettingNames.TXT_STYLE_PATH, "/com/sbrf/report/types/clientletter/cch/ClientLetterReportType.txt.xsl");
            this.rtfStylePath = context.getString("rtfStylePath", "/com/sbrf/report/types/clientletter/cch/ClientLetterReportType.jasper");
            this.rtfWithoutTemplatePath = context.getString("rtfWithoutTemplatePath", "/com/sbrf/report/types/clientletter/cch/ClientLetterWithoutTemplateReportType.jasper");
            this.oldOsbString = context.getString("oldOsb", "9013,8594,8595,8592,8593,8596");
            this.signers = context.getString("signers.V2", "Главный специалист#Ю.Б. Жданова, Начальник сектора#Н.В. Анисимова, Начальник отдела#В.В. Гайдуков, Зам. начальника управления претензионной работы#Н.В. Шаталова");
            makeSigners();
            makeOldOsbList();
        }

        private void makeSigners() {
            signersList = new ArrayList<Option>();
            signersMap = new HashMap<String, String>();
            String[] pairs = signers.split(",", -1);
            for (int i = 0; i < pairs.length; i++) {
                String namePair = pairs[i];
                String[] vals = namePair.split("#", -1);
                if (vals.length == 2) {
                    signersList.add(new Option(vals[1], vals[1]));
                    signersMap.put(vals[1], vals[0]);
                }
            }
        }
        
        private void makeOldOsbList() {
            oldOsbList = new ArrayList<String>();
            String[] osbArray = oldOsbString.split(",", -1);
            for (int i = 0; i < osbArray.length; i++) {
                oldOsbList.add(osbArray[i]);
            }
            
        }
    }
}
