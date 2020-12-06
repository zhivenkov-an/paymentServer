/*
 * Created on 19.10.2012
 */
package com.sbrf.cch.reports.sbm;

import java.io.IOException;
import java.io.OutputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Map;
import java.util.Properties;
import java.util.logging.Logger;

import org.apache.xml.serialize.BaseMarkupSerializer;
import org.apache.xml.serialize.OutputFormat;
import org.apache.xml.serialize.XMLSerializer;
import org.xml.sax.ContentHandler;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.AttributesImpl;

import com.epam.sbrf.report.ParametersFactory;
import com.epam.sbrf.report.ReportFormats;
import com.epam.sbrf.report.ReportType;
import com.epam.sbrf.report.exceptions.ReportException;
import com.epam.sbrf.report.model.InputParameter;
import com.epam.sbrf.report.model.Parameter;
import com.sberbank.sbclients.util.dao.ConnectionSource;
import com.sberbank.sbclients.util.dao.J2EEContainerConnectionSource;
//import com.sberbank.sbclients.util.dao.SQLGenerator;
//import com.sberbank.sbclients.util.dao.db2.AbstractDAO;
import com.sberbank.sbclients.util.dao.db2.DAOUtil;
import com.sbrf.bc.plugin.BillingPlugin;
import com.sbrf.bc.plugin.PluginConfigurationHelper;



import com.sbrf.util.classloader.ResourceHelper;
import com.sbrf.util.logging.SBRFLogger;
import com.sbrf.util.sql.group.DefaultGroupHandler;
import com.sbrf.util.sql.group.GroupFieldDescription;
import com.sbrf.util.sql.group.GroupHandler;
import com.sbrf.util.sql.group.GroupSelector;
import com.sbrf.util.sql.group.NameFieldDescription;
import com.sbrf.util.sql.group.SelectorException;
import com.sbrf.util.text.MessageFormat;


public class SbmReportTxt implements ReportType, BillingPlugin{
    // наследуем два интерфейса,  обязательный для всех в АС БЦ BillingPlugin и специальный для построения отчетов ReportType 
    private final Config config;
    private final Logger logger;
    private final ConnectionSource connectionSource;
    ConnectionSource co; // глобальная переменная, коннект к базе
    
    public SbmReportTxt(Properties properties) {
        PluginConfigurationHelper context = new PluginConfigurationHelper(properties);
        this.config = new Config(context);
        this.connectionSource = new J2EEContainerConnectionSource(this.config.connectionSource);
        this.logger = SBRFLogger.getInstance(getClass().getName());
    }
    public String[] getFormats() {
        // TODO возвращаем два допустимых формата txt и xml 
        return new String[]{ReportFormats.TXT,ReportFormats.XML};
    }


    public Parameter[] getParameters() throws ReportException {
        // TODO задаем параметры отчета
        Parameter operDay = ParametersFactory.getOperDay().setId("operDay").setRequired(true);
        Parameter speck = new InputParameter("speck", "Спецклиент").setMinLength(5).setMaxLength(5).setDefault("00330").setRequired(true);
        return new Parameter[] {operDay,speck};
    }

    public String getStyle(String format) {
        // выбираем стиль
        if (ReportFormats.TXT.equals(format)) {
            //Class clazz = this.getClass();
            return "/com/sbrf/cch/reports/sbm/SbmTxtType.txt.xsl";

        }
        return null;
    }

    public int getTimeout() {
        // TODO допустимый таймаут в милисекундах, в течение этого времени отчёт собираеть, если не собрался отваливаеться
        return this.config.timeout;
    }
    
    /**
     * Подготавливает данные.
     * 
     * @param inParameters входные параметры
     * @param arg1 выходные параметры
     * @param out выходной поток
     * 
     * @throws InterruptedException исключение при прерывании
     * @throws ReportException исключение
     * @throws SQLException исключение базы данных
     */
   
    public void prepareData(Map inParameters, Map outParameters, OutputStream out) throws InterruptedException, ReportException {
        Connection connection = null;
        //Connection connection = co.getConnection();
        PreparedStatement statement = null;
        ResultSet resultSet = null;
        
        
        // TODO использовать GroupHandler
         
        try{
            Date daet = (Date)inParameters.get("operDay");
            String speck = (String)inParameters.get("speck");
            this.logger.finest(">>>>>daet: " + daet);
            this.logger.finest(">>>>>speck: " + speck);
            //ConditionsDAO additionalConditions = new ConditionsDAO(this.connectionSource);
            //String additionalWhere = additionalConditions.makeSQLConditions(inParameters);
            String sqlFormat = ResourceHelper.getResourceAsString(this.config.sqlQueryPath);
            String sql = MessageFormat.format(sqlFormat, new Object[] { daet, speck });
            this.logger.finest(">>>>> SQL:\n" + sql);
            long startTime = Calendar.getInstance().getTimeInMillis();
            connection = this.connectionSource.getConnection();
            statement = connection.prepareStatement(sql);
            resultSet = statement.executeQuery();
            long executionTime = Calendar.getInstance().getTimeInMillis() - startTime;
            this.logger.finest(">>>>>" + getClass().getName() + ": Query executed in " + executionTime + " ms.");

            OutputFormat format = new OutputFormat("XML", "UTF-8", true);
            BaseMarkupSerializer serializer = new XMLSerializer(out, format);
            ContentHandler hd = serializer.asContentHandler();
            NameFieldDescription[] fields = new NameFieldDescription[1];            
            fields[0] = new NameFieldDescription("LINUM"); // LINUM Уникальный ключ по которому идёт 
            
            this.logger.finest(">>>>> fields[0]=" + fields[0]);           
            
            
            GroupSelector selector = new GroupSelector(fields);
            this.logger.finest(">>>>> до rezultSet=" + resultSet.getRow()+ " ");
            GroupHandler handler = new FormReportSbmHandler(hd, daet);            
            selector.parse(resultSet, handler);
            
            this.logger.finest(">>>>> после selector=" + selector);
      
        } catch(SQLException e) {
            // TODo logger
            e.printStackTrace(); // TODo logger
            throw new ReportException(e);
        } catch (SelectorException e) {
            throw new ReportException(e);
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            throw new ReportException(e);
        } finally {
    
            DAOUtil.close(resultSet);
            DAOUtil.close(connection);            
        }
    }
  /*  private static final class ConditionsDAO extends AbstractDAO
    {
      public ConditionsDAO(ConnectionSource connectionSource)
      {
        super(connectionSource);
      }

      public String makeSQLConditions(Map inParameters) {
          StringBuffer result = new StringBuffer();

          String inn = (String)inParameters.get("item");
          //String sum = (String)inParameters.get("sum");
          //String additional_requisites = (String)inParameters.get("additional_requisites");
          
          
          SQLGenerator sqlGen = new SQLGenerator();
          makeStringCondition("item", inn, sqlGen);
          //makeStringCondition("SUM", sum, sqlGen);
          //makeStringCondition("ADDITIONAL_REQUISITES", additional_requisites, sqlGen);
          
          
          if (sqlGen.getWhere() != null) {
            result.append(" and ");
            result.append(sqlGen.getWhere());
          }
          return result.toString();
        }
    }*/
    private static final class Config
    {
      final int timeout;
      final String operDayPluginName;
      final String connectionSource;
      final String sqlQueryPath;

      public Config(PluginConfigurationHelper context)
      {
        this.timeout = context.getInteger("timeout", 120000);
        this.operDayPluginName = context.getString("operDayPluginName", "com.sbrf.bc.operday.OperDay");
        this.connectionSource = context.getString("connectionSource", "java:comp/env/jdbc/UncommittedReadDataSource");
        this.sqlQueryPath = context.getString("sqlQueryPath", "/com/sbrf/cch/reports/sbm/FormSbmReportType.sql");
      }
    }
    private static final class FormReportSbmHandler extends DefaultGroupHandler
    {
      private final ContentHandler hd;
      private final Date start;
      //реквизиты для вывода в sbm
      private String payment_date;
      private String osb;
      private String cashier;
      private String filial;
      private String sum_service;
      private String special_client_code;
      private String tel;
      private String bik;
      private String corr_acc;
      private String settle_acc;
      private String inn;
      private String sum;
      private String payer_info;
      private String payment_kinds_code;
      private String document_info;
      private String additional_requisites;
      private String receiver_number;
      private String linum;
      private final DateFormat dateFormat;
      private final AttributesImpl emptyAttributes;
      private Logger logger;
      
      private long totalCount;
              
      public FormReportSbmHandler(ContentHandler hd, Date daet)
      {
        this.hd = hd;
        this.start = daet;
      //  
        this.dateFormat = new SimpleDateFormat("dd.MM.yyyy");
        this.emptyAttributes = new AttributesImpl();
        totalCount = 0;
        
        }

      public void start() throws SelectorException {
          try {
              this.hd.startDocument();
              this.hd.startElement("", "", "root", this.emptyAttributes);
              
              String date = this.dateFormat.format(this.start);
              this.hd.startElement("", "", "payment_date", this.emptyAttributes);
              this.hd.characters(date.toCharArray(), 0, date.length());
              this.hd.endElement("", "", "payment_date");  
              this.hd.startElement("", "", "items", emptyAttributes);
              
            } catch (SAXException e) {
              throw new SelectorException(e);
            }
      }

      public void startField(GroupFieldDescription description, Object value, ResultSet resultSet) throws SelectorException {
          /*try {
              AttributesImpl atts = new AttributesImpl();              
              atts.addAttribute("", "", "value", "",  value.toString());              
              this.hd.startElement("", "", description.getName().toLowerCase(),atts);
              
            } catch (SAXException e) {
              throw new SelectorException(e);
            }*/
          return;
      }

      public void nextRecord(ResultSet resultSet) throws SelectorException {
        this.logger = SBRFLogger.getInstance(getClass().getName());
        try {
            String linum = "";
            if(DAOUtil.getCharString(resultSet, "LINUM")!=null) linum = DAOUtil.getCharString(resultSet, "PAYMENT_DATE");
            String payment_date = "";
            if(DAOUtil.getCharString(resultSet, "PAYMENT_DATE")!=null) payment_date = DAOUtil.getCharString(resultSet, "PAYMENT_DATE");
            
            String osb = "";            
            if(DAOUtil.getCharString(resultSet, "OSB")!=null) osb = DAOUtil.getCharString(resultSet, "OSB");
            
            String cashier = "";
            if(DAOUtil.getCharString(resultSet, "CASHIER")!=null) cashier = DAOUtil.getCharString(resultSet, "CASHIER");
            
            String filial = "";
            if(DAOUtil.getCharString(resultSet, "FILIAL")!=null) filial = DAOUtil.getCharString(resultSet, "FILIAL");
            
            String sum = "";
            if(DAOUtil.getCharString(resultSet, "SUM")!=null) sum = DAOUtil.getCharString(resultSet, "SUM");
            
            String sum_service = "";
            if(DAOUtil.getCharString(resultSet, "SUM_SERVICE")!=null) sum_service = DAOUtil.getCharString(resultSet, "SUM_SERVICE");
                        
            String special_client_code = "";
            if(DAOUtil.getCharString(resultSet, "SPECIAL_CLIENT_CODE")!=null) special_client_code = DAOUtil.getCharString(resultSet, "SPECIAL_CLIENT_CODE");
            
            String tel = "";
            if(DAOUtil.getCharString(resultSet, "TEL")!=null) tel = DAOUtil.getCharString(resultSet, "TEL");
            
            String bik = "";
            if(DAOUtil.getCharString(resultSet, "BIK")!=null) bik = DAOUtil.getCharString(resultSet, "BIK");
            
            String corr_acc = "";
            if(DAOUtil.getCharString(resultSet, "CORR_ACC")!=null) corr_acc = DAOUtil.getCharString(resultSet, "CORR_ACC");
            
            String settle_acc = "";
            if(DAOUtil.getCharString(resultSet, "SETTLE_ACC")!=null) settle_acc = DAOUtil.getCharString(resultSet, "SETTLE_ACC");
            
            String inn = "";
            if (DAOUtil.getCharString(resultSet, "INN")!=null) inn = DAOUtil.getCharString(resultSet, "INN");
            
            String payer_info = "";
            if (DAOUtil.getCharString(resultSet, "PAYER_INFO")!=null) payer_info = DAOUtil.getCharString(resultSet, "PAYER_INFO");            
            
            String payment_kinds_code = "";
            if (DAOUtil.getCharString(resultSet, "PAYMENT_KINDS_CODE")!=null) payment_kinds_code = DAOUtil.getCharString(resultSet, "PAYMENT_KINDS_CODE");
            
            String receiver_number = "";
            if (DAOUtil.getCharString(resultSet, "RECEIVER_NUMBER")!=null) receiver_number = DAOUtil.getCharString(resultSet, "RECEIVER_NUMBER");
            
            String document_info = "";
            if (DAOUtil.getCharString(resultSet, "DOCUMENT_INFO")!=null) document_info = DAOUtil.getCharString(resultSet, "DOCUMENT_INFO");
            
            String additional_requisites = "";
            if(DAOUtil.getCharString(resultSet, "ADDITIONAL_REQUISITES")!=null) additional_requisites = DAOUtil.getCharString(resultSet, "ADDITIONAL_REQUISITES");
            
            
            this.linum = linum;
            this.payment_date = payment_date;
            this.osb = osb;
            this.cashier = cashier;
            this.filial = filial;
            this.sum_service = sum_service;
            this.special_client_code = special_client_code;
            this.tel = tel;
            this.bik = bik;
            this.corr_acc = corr_acc;
            this.settle_acc = settle_acc;
            this.inn = inn;            
            this.payer_info = payer_info;            
            this.payment_kinds_code = payment_kinds_code;
            this.receiver_number = receiver_number;
            this.document_info = document_info;
            this.sum = sum;  
            this.additional_requisites = additional_requisites;
            totalCount++;
            
        } catch (SQLException e) {
          throw new SelectorException(e);
        }
      }

      public void endField(GroupFieldDescription description, Object value) throws SelectorException {
          try {
              //if (description.getName().equals("INN")) {
                  
                  this.hd.startElement("", "", "item", this.emptyAttributes);
                  
                  
                  this.hd.startElement("", "", "totalCount", this.emptyAttributes);
                  this.hd.characters(("строка " + totalCount).toCharArray(),0,("строка " + totalCount).length());
                  this.hd.endElement("", "", "totalCount");
                  
                  this.hd.startElement("", "", "payment_date", this.emptyAttributes);
                  this.hd.characters(payment_date.toCharArray(), 0, payment_date.length());
                  this.hd.endElement("", "", "payment_date");
                  
                  this.hd.startElement("", "", "osb", this.emptyAttributes);
                  this.hd.characters(osb.toCharArray(), 0, osb.length());
                  this.hd.endElement("", "", "osb");
                  
                  this.hd.startElement("", "", "cashier", this.emptyAttributes);
                  this.hd.characters(cashier.toCharArray(), 0, cashier.length());
                  this.hd.endElement("", "", "cashier");
                  
                  this.hd.startElement("", "", "filial", this.emptyAttributes);
                  this.hd.characters(filial.toCharArray(), 0, filial.length());
                  this.hd.endElement("", "", "filial");
                  
                  this.hd.startElement("", "", "sum", this.emptyAttributes);
                  this.hd.characters(sum.toCharArray(), 0, sum.length());
                  this.hd.endElement("", "", "sum");
                  
                  this.hd.startElement("", "", "sum", this.emptyAttributes);
                  this.hd.characters(sum.toCharArray(), 0, sum.length());
                  this.hd.endElement("", "", "sum");
                  
                  this.hd.startElement("", "", "sum_service", this.emptyAttributes);
                  this.hd.characters(sum_service.toCharArray(), 0, sum_service.length());
                  this.hd.endElement("", "", "sum_service");
                  
                  this.hd.startElement("", "", "special_client_code", this.emptyAttributes);
                  this.hd.characters(special_client_code.toCharArray(), 0, special_client_code.length());
                  this.hd.endElement("", "", "special_client_code");
                  
                  this.hd.startElement("", "", "tel", this.emptyAttributes);
                  this.hd.characters(tel.toCharArray(), 0, tel.length());
                  this.hd.endElement("", "", "tel");
                  
                  this.hd.startElement("", "", "bik", this.emptyAttributes);
                  this.hd.characters(bik.toCharArray(), 0, bik.length());
                  this.hd.endElement("", "", "bik");
                  
                  this.hd.startElement("", "", "corr_acc", this.emptyAttributes);
                  this.hd.characters(corr_acc.toCharArray(), 0, corr_acc.length());
                  this.hd.endElement("", "", "corr_acc");
                  
                  this.hd.startElement("", "", "settle_acc", this.emptyAttributes);
                  this.hd.characters(settle_acc.toCharArray(), 0, settle_acc.length());
                  this.hd.endElement("", "", "settle_acc");
                  
                  this.hd.startElement("", "", "inn", this.emptyAttributes);
                  this.hd.characters(inn.toCharArray(), 0, inn.length());
                  this.hd.endElement("", "", "inn");
                  
                  this.hd.startElement("", "", "payer_info", this.emptyAttributes);
                  this.hd.characters(payer_info.toCharArray(), 0, payer_info.length());
                  this.hd.endElement("", "", "payer_info");
                  
                  this.hd.startElement("", "", "payment_kinds_code", this.emptyAttributes);
                  this.hd.characters(payment_kinds_code.toCharArray(), 0, payment_kinds_code.length());
                  this.hd.endElement("", "", "payment_kinds_code");
                  
                  this.hd.startElement("", "", "receiver_number", this.emptyAttributes);
                  this.hd.characters(receiver_number.toCharArray(), 0, receiver_number.length());
                  this.hd.endElement("", "", "receiver_number");
                  
                  this.hd.startElement("", "", "document_info", this.emptyAttributes);
                  this.hd.characters(document_info.toCharArray(), 0, document_info.length());
                  this.hd.endElement("", "", "document_info");
                  
                  this.hd.startElement("", "", "additional_requisites", this.emptyAttributes);
                  this.hd.characters(additional_requisites.toCharArray(), 0, additional_requisites.length());
                  this.hd.endElement("", "", "additional_requisites");
                  
                  this.hd.endElement("", "", "item");
              //}                                     
                 
              //this.hd.endElement("", "", description.getName().toLowerCase());
            } catch (SAXException e) {
              throw new SelectorException(e);
            }
      }

      public void end() throws SelectorException {
          try {
              this.hd.endElement("", "", "items");
              this.hd.endElement("", "", "root");
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
 }
}





