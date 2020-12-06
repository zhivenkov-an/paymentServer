package com.sbrf.report.formreportUniversal;

import com.epam.sbrf.report.ParametersFactory;
import com.epam.sbrf.report.ReportType;
import com.epam.sbrf.report.exceptions.ReportException;
import com.epam.sbrf.report.model.InputParameter;

import com.epam.sbrf.report.model.Parameter;

import com.sberbank.sbclients.util.dao.ConnectionSource;
import com.sberbank.sbclients.util.dao.J2EEContainerConnectionSource;
import com.sberbank.sbclients.util.dao.SQLGenerator;
import com.sberbank.sbclients.util.dao.db2.AbstractDAO;
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

public class FormReportUniversalType
  implements ReportType, BillingPlugin
{
  private final Config config;
  private final ConnectionSource connectionSource;
  private final Logger logger;
  
  public FormReportUniversalType(Properties properties)
  {
    PluginConfigurationHelper context = new PluginConfigurationHelper(properties);
    this.config = new Config(context);
    this.connectionSource = new J2EEContainerConnectionSource(this.config.connectionSource);
    this.logger = SBRFLogger.getInstance(getClass().getName());
  }

  public String[] getFormats() {
    return new String[] { "rtf", "xml" };
  }

  public String getStyle(String format) {
	  if ("rtf".equals(format)) {
		  Class clazz = getClass();
	      String resourcePath = "/com/sbrf/report/formreportUniversal/FormReportUniversalType.rtf.xsl";
	      this.logger.finest(">>>resourcePath: " + resourcePath);
	      URL url = clazz.getResource(resourcePath);
	      if (url != null) {
	        return resourcePath;
	      }
	  }
    return null;
  }

  public int getTimeout() {
    return this.config.timeout;
  }

  public Parameter[] getParameters() throws ReportException {
    Calendar calendar = Calendar.getInstance();
    calendar.set(5, 1);
    calendar.add(6, -1);
    Date endDate = calendar.getTime();
    calendar.set(5, 1);
    Date startDate = calendar.getTime();
    Parameter startDay = ParametersFactory.getOperDay().setDefault(startDate).setId("start_date").setDescription("Начальная дата(включительно)").setLabel("Начальная дата");
    Parameter endDay = ParametersFactory.getOperDay().setDefault(endDate).setId("end_date").setDescription("Конечная дата(включительно)").setLabel("Конечная дата");
    Parameter bik = new InputParameter("bik", "БИК").setDirectory("SPECIFIC_CLIENTS", "BIK", "RECEIVER_NAME").setDescription("Принимает в качестве значения шаблон");
    Parameter settleAcc = new InputParameter("settleAcc", "Расч.счет").setDirectory("SPECIFIC_CLIENTS", "SETTLE_ACC", "RECEIVER_NAME").setDescription("Принимает в качестве значения шаблон");
    Parameter inn = new InputParameter("inn", "ИНН получателя").setDirectory("SPECIFIC_CLIENTS", "INN", "RECEIVER_NAME");
    Parameter specClientCode = new InputParameter("specClientCode", "Код спец.клиента").setDirectory("SPECIFIC_CLIENTS", "SPECIAL_CLIENT_CODE", "RECEIVER_NAME").setDescription("Принимает в качестве значения шаблон");
        
    return new Parameter[] { startDay, endDay, bik, settleAcc, inn, specClientCode };
  }

  public void prepareData(Map inParameters, Map outParameters, OutputStream data) throws InterruptedException, ReportException
  {
    Connection connection = null;
    PreparedStatement statement = null;
    ResultSet resultSet = null;
    try {
      Date startDate = (Date)inParameters.get("start_date");
      Date endDate = (Date)inParameters.get("end_date");
      
      String addAttr = "";
      String addWhere = "";
      String addSelect = "";
      String addGroup = "";
      String EQ1 = "";
      String EQ2 = "";
      String EQ3 = "";
      String EQ4 = "";
      String JS1 = "";
      String JS2 = "";
      String JS3 = "";
      
      String bik = (String)inParameters.get("bik");
      String sAcc = (String)inParameters.get("settleAcc");
      String inn = (String)inParameters.get("inn");
      String scc = (String)inParameters.get("specClientCode");
      
      this.logger.finest(">>>>>BIK: " + bik);
      this.logger.finest(">>>>>SETTL_ACC: " + sAcc);
      this.logger.finest(">>>>>INN: " + inn);
      this.logger.finest(">>>>>SPECIAL_CLIENT_CODE: " + scc);
      
      int j = 0;
      
      if (bik!=null) {
    	  addAttr += ", BIK";
    	  addSelect += ", BIK AS BIK";
    	  addWhere += "BIK = '" + bik + "'";
    	  EQ1 += "T1.BIK = T2.BIK";
    	  EQ2 += "J1.BIK = T3.BIK";
    	  EQ3 += "J2.BIK = T4.BIK";
    	  EQ4 += "J3.BIK = T5.BIK";
    	  addGroup += "BIK";
    	  JS1 += "T1.BIK AS BIK";
    	  JS2 += "J1.BIK AS BIK";
    	  JS3 += "J2.BIK AS BIK";
    	  j++;
      }
      
      if (sAcc!=null) {
    	  addAttr += ", SACC";
    	  addSelect += ", SETTLE_ACC AS SACC";
    	  if (j!=0) addWhere += " AND "; 
    	  addWhere += " SETTLE_ACC = '" + sAcc + "'";
    	  if (j!=0) {
    		  EQ1 += " AND ";
    		  EQ2 += " AND ";
    		  EQ3 += " AND ";
    		  EQ4 += " AND ";
    		  JS1 += ", ";
    		  JS2 += ", ";
    		  JS3 += ", ";
    	  }
    	  EQ1 += "T1.SACC = T2.SACC";
    	  EQ2 += "J1.SACC = T3.SACC";
    	  EQ3 += "J2.SACC = T4.SACC";
    	  EQ4 += "J3.SACC = T5.SACC";
    	  if(j!=0) addGroup += ", ";
    	  addGroup += "SETTLE_ACC";
    	  JS1 += "T1.SACC AS SACC";
    	  JS2 += "J1.SACC AS SACC";
    	  JS3 += "J2.SACC AS SACC";
    	  j++;
      }
      
      if (inn!=null) {
    	  addAttr += ", INN";
    	  addSelect += ", INN AS INN";
    	  if (j!=0) addWhere += " AND "; 
    	  addWhere += "INN = '" + inn + "'";
    	  if (j!=0) {
    		  EQ1 += " AND ";
    		  EQ2 += " AND ";
    		  EQ3 += " AND ";
    		  EQ4 += " AND ";
    		  JS1 += ", ";
    		  JS2 += ", ";
    		  JS3 += ", ";
    	  }
    	  EQ1 += "T1.INN = T2.INN";
    	  EQ2 += "J1.INN = T3.INN";
    	  EQ3 += "J2.INN = T4.INN";
    	  EQ4 += "J3.INN = T5.INN";
    	  if(j!=0) addGroup += ", ";
    	  addGroup += "INN";
    	  JS1 += "T1.INN AS INN";
    	  JS2 += "J1.INN AS INN";
    	  JS3 += "J2.INN AS INN";
    	  j++;
      }
      
      if (scc!=null) {
    	  addAttr += ", SCC";
    	  addSelect += ", LEFT(SPECIAL_CLIENT_CODE, " + scc.length() + ") AS SCC";
    	  if (j!=0) addWhere += " AND "; 
    	  addWhere += "LEFT(SPECIAL_CLIENT_CODE, " + scc.length() + ") = '" + scc +"'";
    	  if (j!=0) {
    		  EQ1 += " AND ";
    		  EQ2 += " AND ";
    		  EQ3 += " AND ";
    		  EQ4 += " AND ";
    		  JS1 += ", ";
    		  JS2 += ", ";
    		  JS3 += ", ";
    	  }
    	  EQ1 += "T1.SCC = T2.SCC";
    	  EQ2 += "J1.SCC = T3.SCC";
    	  EQ3 += "J2.SCC = T4.SCC";
    	  EQ4 += "J3.SCC = T5.SCC";
    	  if(j!=0) addGroup += ", ";
    	  addGroup += "LEFT(SPECIAL_CLIENT_CODE, " + scc.length() + ")";
    	  JS1 += "T1.SCC AS SCC";
    	  JS2 += "J1.SCC AS SCC";
    	  JS3 += "J2.SCC AS SCC";
    	  j++;
      }
      
      ConditionsDAO additionalConditions = new ConditionsDAO(this.connectionSource);
      String additionalWhere = additionalConditions.makeSQLConditions(inParameters);
      String sqlFormat = ResourceHelper.getResourceAsString(this.config.sqlQueryPath);
      String sql = MessageFormat.format(sqlFormat, new Object[] { startDate, endDate, addAttr, addSelect, addWhere, addGroup, EQ1, EQ2, EQ3, EQ4, JS1, JS2, JS3 });
      this.logger.finest(">>>>> SQL:/n" + sql);
      long startTime = Calendar.getInstance().getTimeInMillis();
      connection = this.connectionSource.getConnection();
      statement = connection.prepareStatement(sql);
      resultSet = statement.executeQuery();
      long executionTime = Calendar.getInstance().getTimeInMillis() - startTime;
      this.logger.fine(getClass().getName() + ": Query executed in " + executionTime + " ms.");

      OutputFormat format = new OutputFormat("XML", "UTF-8", true);
      BaseMarkupSerializer serializer = new XMLSerializer(data, format);
      ContentHandler hd = serializer.asContentHandler();
      NameFieldDescription[] fields = new NameFieldDescription[1];
      fields[0] = new NameFieldDescription("NAMEORG");
      GroupSelector selector = new GroupSelector(fields);
      GroupHandler handler = new FormReportUniversalHandler(hd, startDate, endDate);
      selector.parse(resultSet, handler);
    } catch (SQLException e) {
      throw new ReportException(e);
    } catch (SelectorException e) {
      throw new ReportException(e);
    } catch (IOException e) {
      throw new ReportException(e);
    }
  }

  private static final class ConditionsDAO extends AbstractDAO
  {
    public ConditionsDAO(ConnectionSource connectionSource)
    {
      super(connectionSource);
    }

    public String makeSQLConditions(Map inParameters) {
        StringBuffer result = new StringBuffer();

        String subname = (String)inParameters.get("nameorg");
        
        SQLGenerator sqlGen = new SQLGenerator();
        makeStringCondition("nameorg", subname, sqlGen);
        
        if (sqlGen.getWhere() != null) {
          result.append(" and ");
          result.append(sqlGen.getWhere());
        }
        return result.toString();
      }
  }

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
      this.sqlQueryPath = context.getString("sqlQueryPath", "/com/sbrf/report/formreportUniversal/FormReportUniversalType.sql");
    }
  }

  private static final class FormReportUniversalHandler extends DefaultGroupHandler
  {
    private final ContentHandler hd;
    private final Date start;
    private final Date end;
    private String nameorg;
    private String percent;
    private String bdebt;
    private String allplat;
    private String counter;
    private String alltrans;
    private String price;
    private String edebt;
    private final DateFormat dateFormat;
    private final AttributesImpl emptyAttributes;
            
    public FormReportUniversalHandler(ContentHandler hd, Date startDate, Date endDate)
    {
      this.hd = hd;
      this.start = startDate;
      this.end = endDate;
      
      this.dateFormat = new SimpleDateFormat("dd.MM.yyyy");
      this.emptyAttributes = new AttributesImpl();
      
      }

    public void start() throws SelectorException {
    	try {
            this.hd.startDocument();
            this.hd.startElement("", "", "root", this.emptyAttributes);
            
            String date = this.dateFormat.format(this.start);
            this.hd.startElement("", "", "startDate", this.emptyAttributes);
            this.hd.characters(date.toCharArray(), 0, date.length());
            this.hd.endElement("", "", "startDate");

            date = this.dateFormat.format(this.end);
            this.hd.startElement("", "", "endDate", this.emptyAttributes);
            this.hd.characters(date.toCharArray(), 0, date.length());
            this.hd.endElement("", "", "endDate");
            
          } catch (SAXException e) {
            throw new SelectorException(e);
          }
    }

    public void startField(GroupFieldDescription description, Object value, ResultSet resultSet) throws SelectorException {
    	try {
        	AttributesImpl atts = new AttributesImpl();
        	atts.addAttribute("", "", "value", "", value.toString());
        	this.hd.startElement("", "", description.getName().toLowerCase(), atts);
          } catch (SAXException e) {
            throw new SelectorException(e);
          }
    }

    public void nextRecord(ResultSet resultSet) throws SelectorException {
      try {
    	  String nameorg = "";
    	  if (DAOUtil.getCharString(resultSet, "NAMEORG")!=null) nameorg = DAOUtil.getCharString(resultSet, "NAMEORG");
    	  String percent = "NONE";
    	  if(DAOUtil.getCharString(resultSet, "PERCENT")!=null) percent = DAOUtil.getCharString(resultSet, "PERCENT");
          String bdebt = "0";
          if(DAOUtil.getCharString(resultSet, "BDEBT")!=null) bdebt = DAOUtil.getCharString(resultSet, "BDEBT");
          String allplat = "0";
          if(DAOUtil.getCharString(resultSet, "ALLPLAT")!=null) allplat = DAOUtil.getCharString(resultSet, "ALLPLAT");
          String counter = "0";
          if(DAOUtil.getCharString(resultSet, "COUNTER")!=null) counter = DAOUtil.getCharString(resultSet, "COUNTER");
          String alltrans = "0";
          if(DAOUtil.getCharString(resultSet, "ALLTRANS")!=null) alltrans = DAOUtil.getCharString(resultSet, "ALLTRANS");
          String price = "0";
          if(DAOUtil.getCharString(resultSet, "PRICE")!=null) price = DAOUtil.getCharString(resultSet, "PRICE");
          String edebt = "0";
          if(DAOUtil.getCharString(resultSet, "EDEBT")!=null) edebt = DAOUtil.getCharString(resultSet, "EDEBT");
    	  
          this.nameorg = nameorg;
    	  this.percent = percent;  
    	  this.bdebt = bdebt;
    	  this.allplat = allplat;
    	  this.counter = counter;
    	  this.alltrans = alltrans;
    	  this.price = price;
    	  this.edebt = edebt;
      } catch (SQLException e) {
        throw new SelectorException(e);
      }
    }

    public void endField(GroupFieldDescription description, Object value) throws SelectorException {
    	try {
    		if (description.getName().equals("NAMEORG")) {
    			
    			this.hd.startElement("", "", "nameorg", this.emptyAttributes);
    		    this.hd.characters(nameorg.toCharArray(), 0, nameorg.length());
    		    this.hd.endElement("", "", "nameorg");
    			
    			this.hd.startElement("", "", "percent", this.emptyAttributes);
    		    this.hd.characters(percent.toCharArray(), 0, percent.length());
    		    this.hd.endElement("", "", "percent");
    			
    		    this.hd.startElement("", "", "bdebt", this.emptyAttributes);
    		    this.hd.characters(bdebt.toCharArray(), 0, bdebt.length());
    		    this.hd.endElement("", "", "bdebt");
    		    
    		    this.hd.startElement("", "", "allplat", this.emptyAttributes);
    		    this.hd.characters(allplat.toCharArray(), 0, allplat.length());
    		    this.hd.endElement("", "", "allplat");
    		    
    		    this.hd.startElement("", "", "counter", this.emptyAttributes);
    		    this.hd.characters(counter.toCharArray(), 0, counter.length());
    		    this.hd.endElement("", "", "counter");
    		    
    		    this.hd.startElement("", "", "alltrans", this.emptyAttributes);
    		    this.hd.characters(alltrans.toCharArray(), 0, alltrans.length());
    		    this.hd.endElement("", "", "alltrans");
    		    
    		    this.hd.startElement("", "", "price", this.emptyAttributes);
    		    this.hd.characters(price.toCharArray(), 0, price.length());
    		    this.hd.endElement("", "", "price");
    		    
    		    this.hd.startElement("", "", "edebt", this.emptyAttributes);
    		    this.hd.characters(edebt.toCharArray(), 0, edebt.length());
    		    this.hd.endElement("", "", "edebt");
    		        	        
    	    	}
    		this.hd.endElement("", "", description.getName().toLowerCase());
          } catch (SAXException e) {
            throw new SelectorException(e);
          }
    }

    public void end() throws SelectorException {
    	try {
    		this.hd.endElement("", "", "root");
          } catch (SAXException e) {
            throw new SelectorException(e);
          }
      } 
    }

    public void error(SelectorException exception) throws SelectorException {
    }

    public void fatal(SelectorException exception) throws SelectorException {
    }

    public void cleanup() {
        
  }
}