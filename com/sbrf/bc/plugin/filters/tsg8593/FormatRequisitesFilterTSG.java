package com.sbrf.bc.plugin.filters.tsg8593;

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

public class FormatRequisitesFilterTSG implements PaymentsFilterFactory {

    private final Config config;

    public FormatRequisitesFilterTSG(Properties properties) {
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
        if (paymentData != null && paymentData.getSpecialClientCode() != null && paymentData.getOsb().equals(config.osbCode) && (paymentData.getSpecialClientCode().equals(config.formatSpecCode1) || paymentData.getSpecialClientCode().equals(config.formatSpecCode2) || paymentData.getSpecialClientCode().equals(config.formatSpecCode3))) {
                String addReq[] = paymentData.getAdditionalRequisites().split("@");                
                paymentData.setAdditionalRequisites("оплата за " + addReq[1].substring(2,4) + "-" + addReq[1].substring(0,2) + ", л/счет: " + addReq[0].substring(6,11) +  ", эл.эн(день/ночь)=" + addReq[2] + "/" + addReq[3] + ", г.вода=" + addReq[4] + ", х.вода=" + addReq[5]);
                //paymentData.setAdditionalRequisites("оплата за " + addReq[1].substring(2,4) + "-" + addReq[1].substring(0,2) + ", л/счет: ");
                
            }        
     // если код.спецклиента и ОСБ равно заданному в конфиге, то форматируем по условиям дурацкого Липецкого договора
        if (paymentData != null && paymentData.getSpecialClientCode() != null && paymentData.getOsb().equals(config.osbCode) && paymentData.getSpecialClientCode().equals(config.formatSpecCode4)) {
                String addReq[] = paymentData.getAdditionalRequisites().split("@");  
                paymentData.setAdditionalRequisites("оплата за " + addReq[1].substring(2,4) + "-" + addReq[1].substring(0,2) + ", л/счет: " + addReq[0].substring(6,11) + ", плательщик: " + addReq[2]+ ", эл.эн(день/ночь)=" + addReq[3] + "/" + addReq[4] + ", г.вода=" + addReq[5] + ", х.вода=" + addReq[6]);
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
    
    //final boolean addDog;
    //final int typeIptBeznal;
    //final int typeIptNal;
    String formatSpecCode1;    
    String formatSpecCode2;
    String formatSpecCode3;
    String formatSpecCode4;
    String osbCode;

    Config(PluginConfigurationHelper context) {            
        
        formatSpecCode1 = context.getString("formatSpecCode1", "00377");
        formatSpecCode2 = context.getString("formatSpecCode2", "00372");
        formatSpecCode3 = context.getString("formatSpecCode3", "00339");
        formatSpecCode4 = context.getString("formatSpecCode4", "00338");
        osbCode = context.getString("osbCode", "8593");

    }

    }

}
