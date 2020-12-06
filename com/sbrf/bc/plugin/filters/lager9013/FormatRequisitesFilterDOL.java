package com.sbrf.bc.plugin.filters.lager9013;

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

public class FormatRequisitesFilterDOL implements PaymentsFilterFactory {

    private final Config config;

    public FormatRequisitesFilterDOL(Properties properties) {
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

           

     // если код.спецклиента и ОСБ равно заданному в конфиге, то форматируем по условиям дурацкого Липецкого договора
        if (paymentData != null && paymentData.getSpecialClientCode() != null && paymentData.getSpecialClientCode().equals(config.formatSpecCode1)) {
                String addReq[] = paymentData.getAdditionalRequisites().split("@");  
                paymentData.setAdditionalRequisites("Прием наличных средств за ДОЛ " + addReq[0] + " от " + addReq[1] + " Отделение №" + addReq[2]);
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
    
    String formatSpecCode1;    
    Config(PluginConfigurationHelper context) {                  
        formatSpecCode1 = context.getString("formatSpecCode1", "50048");
    }

    }

}
