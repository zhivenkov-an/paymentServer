/*
 * Created on 16.03.2009
 */
package com.sbrf.bc.processor.multiple;

import java.text.DateFormat;
import java.text.ParseException;
import java.util.Date;
import java.util.Properties;

import com.sberbank.sbclients.util.dao.ConnectionSource;
import com.sberbank.sbclients.util.dao.J2EEContainerConnectionSource;
import com.sbrf.bc.plugin.BillingPlugin;
import com.sbrf.bc.plugin.PluginConfigurationHelper;
import com.sbrf.bc.processor.OutgoingRegistryProcessor;
import com.sbrf.bc.processor.OutgoingRegistryProcessorFactory;
import com.sbrf.bc.processor.Param;
import com.sbrf.bc.processor.Parameters;
import com.sbrf.bc.processor.ProcessorException;
import com.sbrf.bc.processor.operday.OperDayFactory;
import com.sbrf.util.text.LightDateFormat;

/**
 * @author petrov-am
 */
public class SpecialClientPaymentsProcessorFactory implements OutgoingRegistryProcessorFactory, BillingPlugin {

    private final SpecialClientPaymentsProcessorConfig config;
    
    /**
     * 
     */
    public SpecialClientPaymentsProcessorFactory(Properties properties) {
        PluginConfigurationHelper context = new PluginConfigurationHelper(properties);
        config = new SpecialClientPaymentsProcessorConfig(context);
    }

    /* (non-Javadoc)
     * @see com.sbrf.bc.processor.OutgoingRegistryProcessorFactory#newProcessor(com.sbrf.bc.processor.Param)
     */
    public OutgoingRegistryProcessor newProcessor(Param param) throws ProcessorException {
        ConnectionSource connectionSource = new J2EEContainerConnectionSource();
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
        
        return new SpecialClientPaymentsProcessor(connectionSource, config, downloadDate);
    }

}
