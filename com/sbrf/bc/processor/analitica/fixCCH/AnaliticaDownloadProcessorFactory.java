package com.sbrf.bc.processor.analitica.fixCCH;


import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.ParseException;
import java.util.Date;
import java.util.Properties;
import java.util.logging.Logger;

import com.epam.sbrf.bc.jdbc.SpecialConnectionSource;
import com.epam.sbrf.exception.AccountException;
import com.epam.sbrf.state.constants.PaymentStateConstants;
import com.sberbank.sbclients.util.dao.ConnectionSource;
import com.sberbank.sbclients.util.dao.db2.AbstractDAO;
import com.sberbank.sbclients.util.dao.db2.DAOUtil;
import com.sbrf.bc.plugin.BillingPlugin;
import com.sbrf.bc.plugin.PluginConfigurationHelper;
import com.sbrf.bc.plugin.PluginNotFoundException;
import com.sbrf.bc.processor.FileMetadata;
import com.sbrf.bc.processor.OutgoingRegistryProcessor;
import com.sbrf.bc.processor.OutgoingRegistryProcessorFactory;
import com.sbrf.bc.processor.Param;
import com.sbrf.bc.processor.Parameters;
import com.sbrf.bc.processor.ProcessorException;
import com.sbrf.bc.processor.RegistryContext;
import com.sbrf.bc.processor.operday.OperDayFactory;
import com.sbrf.util.classloader.ResourceHelper;
import com.sbrf.util.logging.SBRFLogger;
import com.sbrf.util.sql.group.GroupHandler;
import com.sbrf.util.sql.group.GroupSelector;
import com.sbrf.util.sql.group.NameFieldDescription;
import com.sbrf.util.sql.group.SelectorException;
import com.sbrf.util.text.LightDateFormat;


public class AnaliticaDownloadProcessorFactory implements BillingPlugin, OutgoingRegistryProcessorFactory {
    private final AnaliticaDownloadConfig config;
           
    
    public AnaliticaDownloadProcessorFactory(Properties properties) {
        PluginConfigurationHelper context = new PluginConfigurationHelper(properties);
        this.config = new AnaliticaDownloadConfig(context);
    }
    
    public OutgoingRegistryProcessor newProcessor(Param param) throws ProcessorException {
        ConnectionSource connectionSource = new SpecialConnectionSource();
        String downloadDateParam = param.findValue(Parameters.DOWNLOAD_DATE);
        DateFormat dateFormat = new LightDateFormat("dd.MM.yyyy");
        Date downloadDate = null;
        if (downloadDateParam != null) {
            try {
                downloadDate = dateFormat.parse(downloadDateParam); 
            } catch (ParseException e) {
                throw new ProcessorException("Неправильный формат даты. " + e.getMessage());
            }
        } else {
            downloadDate = OperDayFactory.getOperDayPlugin().getOperationDay();
        }
        return new Implementation(connectionSource, config, downloadDate);
    }
    

    private static final class Implementation extends AbstractDAO implements OutgoingRegistryProcessor {
        private AnaliticaDownloadConfig config;
        private Date operDay;
        private FileGroupOutputStream outputStream;
        private ConnectionSource connectionSource;
        private Logger logger;
        protected Implementation(ConnectionSource connectionSource, AnaliticaDownloadConfig config, Date date) {
            super(connectionSource);
            this.config = config;
            this.operDay = date;
            this.connectionSource = connectionSource;
            this.logger = SBRFLogger.getInstance("com.sbrf.bc.Logger");
            
        }
        public FileMetadata[] download(RegistryContext registryContext) throws ProcessorException {
            String sql="";
            Connection connection = null;
            PreparedStatement statement = null;
            ResultSet resultSet = null;
            try {
                connection = getConnection();                
                sql = ResourceHelper.getResourceAsString(this.getClass(), config.sqlAnalit);
                logger.info("\n ====ZHAN:"+ sql + "\n");
                statement = connection.prepareStatement(sql);
                int i = 1;
                DAOUtil.setDate(statement, i++, operDay);
                //DAOUtil.setString(statement, i++, PaymentStateConstants.TRANSFERRED_STATE);
                resultSet = statement.executeQuery();
                NameFieldDescription fields[] = { new NameFieldDescription("OSB")};
                GroupSelector selector = new GroupSelector(fields);
                outputStream = new FileGroupOutputStream(config.outputDirectory, config.single);
                GroupHandler handler = new AnaliticaDownloadHandler(config, logger, outputStream, connectionSource, operDay);
                selector.parse(resultSet, handler);
            } catch (SQLException e) {
                throw new ProcessorException(e);
            }catch (SelectorException e) {
                throw new ProcessorException(e);
            }catch (PluginNotFoundException e) {
                throw new ProcessorException(e);
            }catch (AccountException e) {
                throw new ProcessorException(e);
            }finally{
                close(resultSet);
                close(statement);
                close(connection);
            }
            
        
            return outputStream.getFilesMetadata();
        }
    }   
    
}    

