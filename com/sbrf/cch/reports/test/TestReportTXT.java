/*
 * Created on 18.10.2012
 */
package com.sbrf.cch.reports.test;

import java.io.IOException;
import java.io.OutputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;
import java.util.Map;
import java.util.Properties;

import com.epam.sbrf.report.ParametersFactory;
import com.epam.sbrf.report.ReportFormats;
import com.epam.sbrf.report.ReportType;
import com.epam.sbrf.report.exceptions.ReportException;
import com.epam.sbrf.report.model.InputParameter;
import com.epam.sbrf.report.model.Parameter;
import com.sberbank.sbclients.util.dao.ConnectionSource;
import com.sberbank.sbclients.util.dao.J2EEContainerConnectionSource;
import com.sberbank.sbclients.util.dao.db2.DAOUtil;
import com.sbrf.bc.plugin.BillingPlugin;

public class TestReportTXT implements ReportType, BillingPlugin{
    ConnectionSource co;
    
    public TestReportTXT(Properties properties) {
        //PluginConfigurationHelper context = new PluginConfigurationHelper(properties);
        co = new J2EEContainerConnectionSource();
        //Connection connection = co.getConnection();
                
        //this.billingPluginFactory = new BillingPluginFactory();
        //this.config = new Config(context);
        //this.connectionSource = new J2EEContainerConnectionSource();        
    }
    public String[] getFormats() {
        // TODO Auto-generated method stub
        return new String[]{ReportFormats.TXT,ReportFormats.XML};
    }


    public Parameter[] getParameters() throws ReportException {
        // TODO Auto-generated method stub
        Parameter operDay = ParametersFactory.getOperDay().setId("operDay").setRequired(true);
        Parameter bik = new InputParameter("bik", "БИК").setMinLength(9).setMaxLength(9).setDefault("046850649").setRequired(true);
        return new Parameter[] {operDay,bik};
    }

    public String getStyle(String format) {
        // 
        if (ReportFormats.TXT.equals(format)) {
            //Class clazz = this.getClass();
            return "/com/sbrf/cch/reports/test/TestTxtType.txt.xsl";

        }
        return null;
    }

    public int getTimeout() {
        // TODO Auto-generated method stub
        return 30000;
    }

    public void prepareData(Map inParameters, Map arg1, OutputStream out) throws InterruptedException, ReportException {
        Connection connection = co.getConnection();
        
        Date daet = (Date)inParameters.get("operDay");
        String bik = (String)inParameters.get("bik");
        
        // TODO ипсользовать GroupHandler
        ResultSet rs = null; 
        try{
        connection.setTransactionIsolation(Connection.TRANSACTION_READ_UNCOMMITTED);
        out.write("<?xml version=\"1.0\" encoding=\"UTF-8\"?> <root><items>".getBytes("UTF-8")); // TODO encoding
               
        PreparedStatement preparedStatement = connection.prepareStatement("select sum, inn from pay.payments where transfer_date = ? and bik = ? ");
                preparedStatement.setDate(1, new java.sql.Date(daet.getTime())); 
                preparedStatement.setString(2, bik);
                rs = preparedStatement.executeQuery();
                while(rs.next()) {
                    Long q = rs.getLong(1);
                    String inn = rs.getString(2);
                    out.write(("<item><sum>" + q + "</sum><inn>" + inn + "</inn></item>").getBytes("UTF-8"));
                }
                out.write("</items></root>".getBytes("UTF-8"));
        } catch(SQLException e) {
            // TODo logger
            e.printStackTrace(); // TODo logger
            throw new ReportException(e);
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            throw new ReportException(e);
        } finally {
            // TODO использовать DAOUtil
            DAOUtil.close(rs);
            DAOUtil.close(connection);
                //rs.close(); // TODo проверка на null
        //connection.close();
        }
    }

}

