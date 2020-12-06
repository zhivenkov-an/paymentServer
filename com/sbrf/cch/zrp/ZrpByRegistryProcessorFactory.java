package com.sbrf.cch.zrp;

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
 * Добавление строк для заведения карточных счетов Зарплатного проекта.
 * 
 * @author 
 *
 */
public class ZrpByRegistryProcessorFactory implements IncomingRegistryProcessorFactory {

    private final ZrpConfig config;
    private final Logger logger;
    
    public ZrpByRegistryProcessorFactory(Properties properties) {
        config = new ZrpConfig(new PluginConfigurationHelper(properties));
        logger = SBRFLogger.getInstance(this.getClass().getSimpleName());
    }
    
    public IncomingRegistryProcessor newProcessor(FileMetadata fileMetadata) throws ProcessorException {
        return new ZrpByRegistryProcessor(new SpecialConnectionSource("jdbc/BillingDataSource"), config, logger, fileMetadata);
    }

}
