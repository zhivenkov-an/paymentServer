package com.sbrf.bc.plugin.filters.gisgmp9013;

//import java.sql.SQLException;
//import java.io.IOException;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import com.epam.sbrf.bc.data.AliasedPair;
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
        if (paymentData != null && paymentData.getSpecialClientCode() != null && paymentData.getSpecialClientCode().equals(config.formatSpecCode1) && (paymentData.getPaymentType() == 3 || paymentData.getPaymentType() == 4 || paymentData.getPaymentType() == 8 || paymentData.getPaymentType() == 6 || paymentData.getPaymentType() == 5)) {
                String addReq[] = paymentData.getAdditionalRequisites().split("@");
                String seriyaPost = "";
                String datePost = "";
                if (addReq.length <= (config.filNumData-1)){ //если не хватает дополнительного реквизита дата постановления
                    datePost="01042013"; 
                } else{
                    datePost=addReq[config.filNumData-1].replaceAll("[^0-9]", ""); // заменяем все символы кроме цифровых                    
                }
                // проверяем значение даты если оказываеться неформатной, то присваеваем фиктивное значение
                if (datePost.length() != 8){
                    datePost="01042013"; 
                }
                if (addReq.length <= (config.filNumSeriya-1)){ //если не хватает дополнительного реквизита серия и номер постановления
                    seriyaPost="ZZZZZZZZ"; 
                } else{
                    addReq[config.filNumSeriya-1]=addReq[config.filNumSeriya-1].toUpperCase(); // переводим в верхний регистр
                    seriyaPost=addReq[config.filNumSeriya-1].replaceAll("[^0-9A-ZА-Я]", "");
                }                
                int i;
                if ( seriyaPost.length() > 12){
                    seriyaPost=seriyaPost.substring(0, 12);
                }
                for (i=seriyaPost.length();i<12;i++){ //добиваем серию и номер до 12 знаков
                    seriyaPost = seriyaPost + "Z";
                }
                
                // высчитываем дату от начала года
                //Calendar calDay = new GregorianCalendar(Integer.parseInt(datePost.substring(4,8)), Integer.parseInt(datePost.substring(2,4)), Integer.parseInt(datePost.substring(0,2)));
                Calendar calDay;
                calDay = Calendar.getInstance();
                calDay.clear();
                calDay.set(Calendar.YEAR,Integer.parseInt(datePost.substring(4,8)));
                calDay.set(Calendar.MONTH,Integer.parseInt(datePost.substring(2,4))-1); // т.к. отсчет месяца идёт с нуля
                calDay.set(Calendar.DAY_OF_MONTH,Integer.parseInt(datePost.substring(0,2)));
                
                String stDay = Integer.toString(calDay.get(Calendar.DAY_OF_YEAR)) + datePost.substring(7,8); // переводм в формат номер дня в году + заключительный разряд года
                int numDay = Integer.parseInt(stDay);                
             // преобразуем в модифицированный base64                
                String base64CrcRusChars = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZАБВГДЕЖЗИКЛМНОПРСТУФХЦЧШЬЭЮЯ";
                int ost = 0;//остаток
                int des = numDay;//целая часть
                String base64Day = "";
                //String tempSt = "";                
                do{
                   ost = des%64;
                   des = des/64;
                   base64Day = base64CrcRusChars.charAt(ost) + base64Day;
                  // tempSt = tempSt + "Остаток " + ost + " закодирован " + base64CrcRusChars.charAt(ost);
                } while (des != 0);
                String stUIN;
                stUIN = "18810" + base64Day + seriyaPost;
                // для расчета контрольного разряда
                Map<Character, Integer> reqsCheckCrcCharsMap = new HashMap<Character, Integer>();
                String reqsCheckCrcRusChars = "АБВГДЕЖЗИКЛМНОПРСТУФХЦЧШЩЭЮЯ----Ъ--Ы-----Ь";
                String reqsCheckCrcChars = reqsCheckCrcRusChars + "A-B--E---K-MHO-PCTY-X-------DFGIJLNQRSUVWZ";
                int len = reqsCheckCrcRusChars.length();
                int counter = 0;
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
               //paymentData.getPaymentProperties().put("supplierBillID", new AliasedPair("supplierBillID",stUIN) );
                // начиная с версии 2.006.10 УИН находит в pay.payments
               paymentData.setUin(stUIN);
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
    int filNumSeriya;
    int filNumData;
    Config(PluginConfigurationHelper context) {                  
        formatSpecCode1 = context.getString("formatSpecCode1", "07360");
      //  paymentType = context.getString("paymentType", "3,4");        
        filNumSeriya = context.getInteger("filNumSeriya", 5);
        filNumData = context.getInteger("filNumData", 6);
    }

    }

}
