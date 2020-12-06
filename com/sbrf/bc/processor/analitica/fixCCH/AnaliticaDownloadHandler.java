package com.sbrf.bc.processor.analitica.fixCCH;

import java.io.IOException;
import java.math.BigDecimal;
//import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.NumberFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.logging.Logger;
//import java.util.regex.Matcher;
//import java.util.regex.Pattern;



//import billing.util.SumConverter;

import billing.util.SumConverter;

import com.epam.sbrf.bc.plugin.AccountFabric;
import com.epam.sbrf.bc.plugin.AccountFactory;

import com.epam.sbrf.exception.AccountException;
import com.sberbank.sbclients.util.dao.ConnectionSource;
import com.sberbank.sbclients.util.dao.db2.DAOUtil;

import com.sbrf.bc.plugin.BillingPluginFactory;
import com.sbrf.bc.plugin.PluginNotFoundException;
//import com.sbrf.bc.processor.selfservdevice.SelfServiceTagsPlugin;
import com.sbrf.util.io.CrlfPrintWriter;
import com.sbrf.util.sql.group.DefaultGroupHandler;
import com.sbrf.util.sql.group.GroupFieldDescription;
import com.sbrf.util.sql.group.SelectorException;
import com.sbrf.util.text.LightDateFormat;
import com.sbrf.util.text.MessageFormat;

/**
 * Данные, выгружаемые в этом плагине соответствуют cpm файлу для Аналитики.
 * @see  
 * @author 
 */
final class AnaliticaDownloadHandler extends DefaultGroupHandler {
    private final AnaliticaDownloadConfig config;
    private final ConnectionSource connectionSource;
    private final Date operDay;
    private final MessageFormat fileNameFormat;
    private final MessageFormat transactionFormat;
    private final BillingPluginFactory billingPluginFactory;
    private final AccountFabric accountFabric;
    private final Logger logger;
    private FileGroupOutputStream outputStream;
    private CrlfPrintWriter outputWriter;
    private String cardNumber;
    private String authorCode;
    private String comment;

    //private String transactionDate;
    private String deviceNumber;
    private String operationTime;
    //private String code;
    private int counter;
    private final NumberFormat numberFormat;
    //private boolean hasTrasaction;
    private SumConverter sumConverter = SumConverter.getInstance();
    private final Connection connToRgProps;
    private final String FIND_PROPS_SQL = "select NAME, VALUE from RG.PROPS where OBJECT_TYPE = ? and OBJECT_ID = ?";
    private final PreparedStatement findPropsStatement;
    private final LightDateFormat outputDate;
    private final Map<String, String> unnecessaryFieldsMap;
    private final Map<String, String> unnecessaryFieldsToSet;
    //private final SelfServiceTagsPlugin documentTags;

    public AnaliticaDownloadHandler(AnaliticaDownloadConfig config, Logger logger, FileGroupOutputStream outputStream, ConnectionSource source, Date operDay) throws AccountException, PluginNotFoundException, SQLException {
        this.config = config;
        this.outputStream = outputStream;
        this.connectionSource = source;
        this.operDay = operDay;
        this.fileNameFormat = new MessageFormat(config.fileNameFormat);
        this.transactionFormat = new MessageFormat(config.transactionFormat);
        this.numberFormat = NumberFormat.getInstance(Locale.ENGLISH);
        this.logger = logger;
        numberFormat.setMaximumFractionDigits(2);
        numberFormat.setMinimumFractionDigits(2);
        this.billingPluginFactory = new BillingPluginFactory();
        AccountFactory accountFactory = (AccountFactory) billingPluginFactory.findPlugin(AccountFactory.PLUGIN_NAME);
        this.accountFabric = accountFactory.createNewAccountFabric(connectionSource);
        this.accountFabric.init();
        this.connToRgProps = connectionSource.getConnection();
        this.findPropsStatement = connToRgProps.prepareStatement(FIND_PROPS_SQL);
        this.counter = 0;
        this.outputDate = new LightDateFormat("dd.MM.yyyy");
        //this.documentTags = (SelfServiceTagsPlugin) billingPluginFactory.findPlugin(config.xmlDocumentFormatTagsPlugin);
        
        unnecessaryFieldsMap = new HashMap<String, String>();
        unnecessaryFieldsToSet = new HashMap<String, String>();
        //Pattern pattern = Pattern.compile("([\\w]+)=([\\w]+)");
        //Matcher matcher = pattern.matcher(config.unnecessaryFieldsNames);
        /*while(matcher.find()) {
            String key = matcher.group(1);
            String value = matcher.group(2);
            unnecessaryFieldsMap.put(key, value);
        }*/
    }

    public void start() throws SelectorException {
        outputStream.start();
        counter = 0;
    }

    public void startField(GroupFieldDescription description, Object value, ResultSet resultSet) throws SelectorException {
        try {
            if (description.getName().equalsIgnoreCase("OSB")) {                
                String osb = DAOUtil.getString(resultSet, "OSB");
                counter++;
                String osbName;
                
                    osbName = osb;
                
                String name = fileNameFormat.format(new Object[] { operDay, new Integer(counter), osbName }); // группировка для выходного файла
                outputStream.startBlock(name);
                outputWriter = new CrlfPrintWriter(outputStream, config.lineSeparator, config.outputEncoding);
            }          
        } catch (IOException e) {
            SelectorException exception = new SelectorException(e);
            logger.throwing(this.getClass().getName(), "startField", exception);
            throw exception;
        } catch (SQLException e) {
            SelectorException exception = new SelectorException(e);
            logger.throwing(this.getClass().getName(), "startField", exception);
            throw exception;
        }
    }

    public void nextRecord(ResultSet resultSet) throws SelectorException {
        try {           
            
            long longSum = DAOUtil.get_long(resultSet, "SUM");
            BigDecimal sum;                   
            
            sum = sumConverter.toBigDecimal(longSum);
            Date paymentDate = DAOUtil.getDate(resultSet, "PAYMENT_DATE");            
            cardNumber = DAOUtil.getString(resultSet, "CARD");           
            authorCode = DAOUtil.getString(resultSet, "CODAUTH");
            deviceNumber =  DAOUtil.getString(resultSet, "NUMBER_US");
            operationTime = DAOUtil.getString(resultSet, "TIME");
            comment = DAOUtil.getString(resultSet, "COMMENT");                             
                printData(new Object[] { sum, paymentDate, cardNumber, authorCode, deviceNumber, operationTime,operationTime,operationTime,comment});          
           
        } catch (SQLException e) {
            SelectorException exception = new SelectorException(e);
            logger.throwing(this.getClass().getName(), "nextField", exception);
            throw exception;
        }
    }

    public void endField(GroupFieldDescription description, Object value) throws SelectorException {
        try {
            if (description.getName().equalsIgnoreCase("OSB")) {
                outputWriter.close();
                outputStream.endBlock();
            }
        } catch (IOException e) {
            SelectorException exception = new SelectorException(e);
            logger.throwing(this.getClass().getName(), "endField", exception);
            throw exception;
        }
    }

    public void end() throws SelectorException {
        try {
            outputStream.stop();
        } catch (IOException e) {
            throw new SelectorException(e);
        }
    }

    public void cleanup() {
        DAOUtil.close(findPropsStatement);
        DAOUtil.close(connToRgProps);
    }

    public void error(SelectorException exception) throws SelectorException {
        try {
            outputStream.stop();
        } catch (IOException e) {
            throw new SelectorException(e);
        }
        throw exception;

    }

    public void fatal(SelectorException exception) throws SelectorException {
        try {
            outputStream.stop();
        } catch (IOException e) {
            throw new SelectorException(e);
        }
        logger.throwing(this.getClass().getName(), "fatal", exception);
        throw exception;
    }

    private void printData(Object[] data) {
        String line = transactionFormat.format(data);
        for (String key : unnecessaryFieldsToSet.keySet()) {
            line = line + "|" + key + "=" + unnecessaryFieldsToSet.get(key);
        }
        outputWriter.println(line);
    }  
 
    
  
}