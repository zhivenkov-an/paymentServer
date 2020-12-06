package com.sbrf.bc.plugin.filters.dog;

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

public class AdditionalRequisitesFilterDog implements PaymentsFilterFactory {

    private final Config config;

    public AdditionalRequisitesFilterDog(Properties properties) {
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

           
// если флаг добавления собачки установлен и поле тип платежа не относиться к ИПТ
        if (config.addDog && paymentData != null && paymentData.getSpecialClientCode() != null && paymentData.getPaymentType() != config.typeIptBeznal && paymentData.getPaymentType() != config.typeIptNal) {
                paymentData.setAdditionalRequisites(paymentData.getAdditionalRequisites() + "@");
            }
        
        // добавляем для определенного кода спецклиента ИПТ 
        if (config.addDog && paymentData != null && paymentData.getSpecialClientCode().equals(config.specCode) && paymentData.getPaymentType() == config.typeIptBeznal && paymentData.getPaymentType() == config.typeIptNal) {
            paymentData.setAdditionalRequisites(paymentData.getAdditionalRequisites() + "@");
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
    
    final boolean addDog;
    final int typeIptBeznal;
    final int typeIptNal;
    String specCode;    

    Config(PluginConfigurationHelper context) {
        typeIptBeznal = context.getInteger("typeIptBeznal", 4);
        typeIptNal = context.getInteger("typeIptNal", 3);        
        addDog = context.getBoolean("addDog", true);
        specCode = context.getString("specCode", "04890");

    }

    }

}
