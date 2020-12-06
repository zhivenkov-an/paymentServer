package com.sbrf.cch.passwd;

import java.util.Properties;
import java.util.logging.Logger;

import com.epam.sbrf.bc.jdbc.SpecialConnectionSource;

import com.sbrf.bc.plugin.PluginConfigurationHelper;
import com.sbrf.bc.processor.FileMetadata;
import com.sbrf.bc.processor.IncomingRegistryProcessor;
import com.sbrf.bc.processor.IncomingRegistryProcessorFactory;
import com.sbrf.bc.processor.ProcessorException;
import com.sbrf.util.logging.SBRFLogger;

/**
 * Расчет хэша пароля.
 * 
 * @author 
 *
 */
public class PasswdByRegistryProcessorFactory implements IncomingRegistryProcessorFactory {

    private final PasswdConfig config;
    private final Logger logger;
    
    public PasswdByRegistryProcessorFactory(Properties properties) {
        config = new PasswdConfig(new PluginConfigurationHelper(properties));
        logger = SBRFLogger.getInstance(this.getClass().getSimpleName());
    }
    
    public IncomingRegistryProcessor newProcessor(FileMetadata fileMetadata) throws ProcessorException {
        return new PasswdByRegistryProcessor(new SpecialConnectionSource("jdbc/AdminDataSource"), config, logger, fileMetadata);
    }

}
