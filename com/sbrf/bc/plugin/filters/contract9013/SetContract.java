package com.sbrf.bc.plugin.filters.contract9013;

//import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;

//import com.epam.sbrf.bc.data.BrakCode;
import com.epam.sbrf.bc.data.BrakValue;
import com.epam.sbrf.bc.data.PaymentData;
import com.epam.sbrf.bc.plugin.PaymentsFilter;
import com.epam.sbrf.bc.plugin.PaymentsFilterFactory;
import com.epam.sbrf.exception.FilterException;
//import com.epam.sbrf.payorder.data.FormAlgorithmType;
import com.sberbank.sbclients.util.dao.J2EEContainerConnectionSource;
import com.sbrf.bc.dictionary.PaymentOrderAlgorithmCachingDictionary;
import com.sbrf.bc.plugin.PluginConfigurationHelper;

public class SetContract implements PaymentsFilterFactory {

    private final Config config;

    public SetContract(Properties properties) {
    PluginConfigurationHelper context = new PluginConfigurationHelper(properties);
    config = new Config(context);
    }

    public PaymentsFilter createPaymentsFilter() {
    return new PaymentsFilterImpl(config);
    }

    private static final class PaymentsFilterImpl implements PaymentsFilter {

    private PaymentOrderAlgorithmCachingDictionary algorithmsDictionary;

    Config config;

    PaymentsFilterImpl(Config config) {
        this.config = config;

    }

    @Override
    public BrakValue[] check(PaymentData paymentData, BrakValue[] brakArray) throws FilterException {
        List<BrakValue> braks = new ArrayList<BrakValue>(Arrays.asList(brakArray));      

           
// если номер организации менее 50 000 и кодспецклиента не равен 50610 или 50330 то выставляем признак договорной организации
        if (paymentData != null && paymentData.getSpecialClientCode() != null && ! paymentData.getSpecialClientCode().equals(config.specContrBudget) && ! paymentData.getSpecialClientCode().equals(config.specContrCommer) && Integer.parseInt(paymentData.getReceiverNumber())<50000) {
                paymentData.setContr(1);
            }
     // если номер организации больше или равен 50 000 то выставляем признак без договорной организации
        if (paymentData != null && (Integer.parseInt(paymentData.getReceiverNumber())>=50000 || paymentData.getSpecialClientCode().equals(config.specContrBudget) || paymentData.getSpecialClientCode().equals(config.specContrCommer))) {
                paymentData.setContr(0);
            }
        
             
        return braks.toArray(brakArray);
    }

    @Override
    public void close() throws FilterException {
        algorithmsDictionary.close();

    }

    @Override
    public void init() throws FilterException {
        algorithmsDictionary = new PaymentOrderAlgorithmCachingDictionary(new J2EEContainerConnectionSource());
        algorithmsDictionary.init();

    }

    }

    private static final class Config {   
    
    final String specContrBudget;    
    final String specContrCommer;

    Config(PluginConfigurationHelper context) {
        specContrBudget = context.getString("specContrBudget", "50610");
        specContrCommer = context.getString("specContrCommer", "50330");        
        

    }

    }

}
