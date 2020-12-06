package com.sbrf.cch.passwd;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.LineNumberReader;
import java.io.OutputStream;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;




import java.util.logging.Logger;
import java.util.regex.Matcher;




import com.sberbank.sbclients.admin.HashValueMaker;
import com.sberbank.sbclients.util.dao.ConnectionSource;
import com.sberbank.sbclients.util.dao.DAOException;


import com.sbrf.bc.processor.FileMetadata;
import com.sbrf.bc.processor.IncomingRegistryProcessor;
import com.sbrf.bc.processor.ProcessorException;
import com.sbrf.bc.processor.RegistryContext;

import com.sbrf.util.classloader.ResourceHelper;
import com.sbrf.util.text.MessageFormat;

public class PasswdByRegistryProcessor implements IncomingRegistryProcessor {

    protected final ConnectionSource connectionSource;
    private final PasswdConfig config;    
    private final Logger logger;
    private final FileMetadata input;
    OutputStream outErrors;
    
    
    public PasswdByRegistryProcessor(ConnectionSource connectionSource, PasswdConfig config, Logger logger, FileMetadata input) {
        this.connectionSource = connectionSource;
        this.config = config;
        this.logger = logger;
        this.input = input;
    }
    
    public boolean accepts() {
        return true;
    }

    public FileMetadata[] process(RegistryContext context) throws ProcessorException {

        String passwdUser = ""; 
        String sql = "";
        Connection connection = null;
        connection = connectionSource.getConnection(); 
        PreparedStatement statement = null;        
        String sqlFormat = ResourceHelper.getResourceAsString(config.sqlQueryPath);
        FileMetadata fileMetadaErrors = null;
        String heshLine = "";
        LineNumberReader in = null;
        
        logger.info("\n\n  ==ZHAN==: Запуск генерации хэш-пароля!");
        try {
            String fileNameErros = "passwd.out";
            passwdUser = "zgxv1234";
            File fileErrors = File.createTempFile(fileNameErros, ".txt");
            fileMetadaErrors = new FileMetadata(fileErrors, "", fileNameErros);
            outErrors = new FileOutputStream(fileErrors);
            in = new LineNumberReader(new InputStreamReader(new FileInputStream(input.getCurrentFile()), config.fileEncoding));
            Matcher informationalLine = config.informationalRepeat.matcher("");
            Calendar c1 = Calendar.getInstance();
            Calendar c2 = Calendar.getInstance();
            c2.add(Calendar.DATE,90);

            for (String line = in.readLine(); line != null; line = in.readLine()) {
                informationalLine.reset(line);
                //outErrors.write(("\n строка " + line + " регулярка " + config.informationalRepeat + "\n").getBytes());
                if (informationalLine.matches()) {
                    String loginName = informationalLine.group(1);
                    heshLine = HashValueMaker.getInstance().makePasswordHashValue(loginName, passwdUser);
                    outErrors.write(("\n для логина " + loginName + " пароль " + passwdUser + " хэш пароля " + heshLine + "\n").getBytes());
                    //String fioName = informationalLine.group(2);
                    //String jobName = informationalLine.group(3);
                    //String gosbName = informationalLine.group(4);
                    //outErrors.write(("\n разбор входного файла " + loginName + " ФИО: " + fioName + jobName + gosbName).getBytes());
                    // выполняем запрос на сброс пароля
                    sql = MessageFormat.format(sqlFormat, new Object[] {heshLine,loginName,c1.getTime(),c2.getTime()});
                    logger.info("\n\n  ==ZHAN==: SQL:\n" + sql);
                    try {
                        statement = connection.prepareStatement(sql);
                        statement.executeUpdate(); // выполняеться при update
                        statement.close();
                    } catch (SQLException e1) {
                        // TODO Auto-generated catch block
                        e1.printStackTrace();
                    }
                    
                }
            }
            in.close();
            outErrors.close();            
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        Collection<FileMetadata> fileMetadatas = new ArrayList<FileMetadata>();
        fileMetadatas.add(fileMetadaErrors);       
       return fileMetadatas.toArray(new FileMetadata[0]);       
 
    }

}
