package com.sbrf.bc.processor.selfservdevice.fix;

import java.util.Date;
import java.util.Properties;

import com.sbrf.bc.plugin.BillingPlugin;
import com.sbrf.bc.plugin.PluginConfigurationHelper;
import com.sbrf.bc.processor.OutgoingRegistryProcessor;
import com.sbrf.bc.processor.OutgoingRegistryProcessorFactory;
import com.sbrf.bc.processor.Param;
import com.sbrf.bc.processor.ProcessorException;
import com.sbrf.bc.processor.operday.OperDayFactory;

/**
 * Прамая загрузка платежей из АС БЦ. Данной функциональностью никто не
 * пользуется, и нужна ли она будет неизветсно.
 * 
 * @author usenko-v
 * 
 */
public class BillingDownloadProcessorFactory implements BillingPlugin, OutgoingRegistryProcessorFactory {
    private BillingDownloadProcessorConfig config;

    public BillingDownloadProcessorFactory(Properties properties) {
        PluginConfigurationHelper context = new PluginConfigurationHelper(properties);
        this.config = new BillingDownloadProcessorConfig(context);
    }

    public OutgoingRegistryProcessor newProcessor(Param param) throws ProcessorException {
        Date paymentDate = OperDayFactory.getOperationPerformanceDate(param);
        return new BillingDownloadProcessor(config, paymentDate);
    }

}
