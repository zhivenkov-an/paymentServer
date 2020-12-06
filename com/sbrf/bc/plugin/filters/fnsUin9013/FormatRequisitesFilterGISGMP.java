package com.sbrf.bc.plugin.filters.fnsUin9013;




import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import com.epam.sbrf.bc.data.AliasedPair;

import com.epam.sbrf.bc.data.BrakValue;
import com.epam.sbrf.bc.data.PaymentData;
import com.epam.sbrf.bc.plugin.PaymentsFilter;
import com.epam.sbrf.bc.plugin.PaymentsFilterFactory;
import com.epam.sbrf.exception.FilterException;

import com.sberbank.sbclients.util.dao.J2EEContainerConnectionSource;
import com.sbrf.bc.dictionary.PaymentOrderAlgorithmCachingDictionary;
import com.sbrf.bc.plugin.PluginConfigurationHelper;


public class FormatRequisitesFilterGISGMP implements PaymentsFilterFactory {

    private final Config config;

    public FormatRequisitesFilterGISGMP(Properties properties) {
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

        
     // если код.спецклиента и тип платежа равны заданным в конфиге, то добавляем ИУН    
        //  && config.paymentType.indexOf(paymentData.getPaymentType())>0
        String arraySpec[] = config.formatSpecCode1.split(",");
        Arrays.sort(arraySpec);
        if (paymentData != null && paymentData.getSpecialClientCode() != null && Arrays.binarySearch(arraySpec, paymentData.getSpecialClientCode())>=0  && (paymentData.getPaymentType() == 3 || paymentData.getPaymentType() == 4 || paymentData.getPaymentType() == 8)) {
                String addReq[] = paymentData.getAdditionalRequisites().split("@");
                String stIndex = "";
                String stUIN = "";
                stIndex=addReq[config.filNumData-1].replaceAll("[^0-9]", ""); // заменяем все символы кроме цифровых                 
                if ( stIndex.length() == 20 ){ //если полный 20тизначный индекс
                	stUIN = stIndex;
                }                                           
                if ( stIndex.length() == 15 ){ //если старый 15тизначный индекс
                	stUIN = "1820" + stIndex;
                    // для расчета контрольного разряда
                    Map<Character, Integer> reqsCheckCrcCharsMap = new HashMap<Character, Integer>();
                    String reqsCheckCrcRusChars = "АБВГДЕЖЗИКЛМНОПРСТУФХЦЧШЩЭЮЯ----Ъ--Ы-----Ь";
                    String reqsCheckCrcChars = reqsCheckCrcRusChars + "A-B--E---K-MHO-PCTY-X-------DFGIJLNQRSUVWZ";
                    int len = reqsCheckCrcRusChars.length();
                    int counter = 0;
                    int i;
                    for (i = 0; i < reqsCheckCrcChars.length(); i++) {
                        if (counter++ == len) {
                            len = Integer.MAX_VALUE;
                            counter = 1;
                        }
                        if (reqsCheckCrcChars.charAt(i) != '-') {
                            reqsCheckCrcCharsMap.put(reqsCheckCrcChars.charAt(i), counter);
                        }
                    }        
                    
                    SupplierBillVerificator verificator = new SupplierBillVerificator(null, null);
                  //Добавляем контрольный разряд
                    //stUIN = stUIN + "контрольный разряд" + verificator.calculateControlChar(stUIN, reqsCheckCrcCharsMap) + "номер дня=" + stDay + "в base64=" + base64Day + tempSt;
                    stUIN = stUIN +  verificator.calculateControlChar(stUIN, reqsCheckCrcCharsMap);                	
                }         
                
               if (stUIN.length()==20) { // записываем УИН                                                                                          
                 //paymentData.getPaymentProperties().put("supplierBillID", new AliasedPair("supplierBillID",stUIN) );
            	   paymentData.setUin(stUIN);
               }
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
    //String paymentType;    
    int filNumData;
    Config(PluginConfigurationHelper context) {                  
        formatSpecCode1 = context.getString("formatSpecCode1", "76199,76299,76399,76499,76599,76699");
      //  paymentType = context.getString("paymentType", "3,4");         
        filNumData = context.getInteger("filNumData", 1);
    }

    }

}
