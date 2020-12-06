package com.sbrf.bc.plugin.filters.nullAdd;


import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
//import java.util.HashMap;
import java.util.List;
//import java.util.Map;
import java.util.Properties;
//import java.util.regex.Matcher;
//import java.util.regex.Pattern;

import com.epam.sbrf.bc.data.BrakCode;
import com.epam.sbrf.bc.data.BrakValue;
import com.epam.sbrf.bc.data.PaymentData;
import com.epam.sbrf.bc.dictionary.BrakTypeCachingDictionary;
import com.epam.sbrf.bc.dictionary.TemplateDictionary;
import com.epam.sbrf.bc.jdbc.SpecialConnectionSource;
import com.epam.sbrf.bc.plugin.PaymentsFilter;
import com.epam.sbrf.bc.plugin.PaymentsFilterFactory;
import com.epam.sbrf.exception.FilterException;
import com.sberbank.sbclients.util.dao.ConnectionSource;
import com.sbrf.bc.plugin.PluginConfigurationHelper;
//import com.sbrf.util.text.NamedMessageFormat;

/**
 * 
 * @author Stadnichenko-OV
 * 
 */
public class NullAdditionalRequisitesFilter implements PaymentsFilterFactory {

    private final Config config;

    public NullAdditionalRequisitesFilter(Properties properties) {
        config = new Config(new PluginConfigurationHelper(properties));
    }

    public PaymentsFilter createPaymentsFilter() {
        return new Implementation(config);
    }

    private static final class Implementation implements PaymentsFilter {
        private TemplateDictionary templateDictionary;
        private BrakTypeCachingDictionary brakDictionary;
        private final Config config;
        //private Pattern addReqFormatPattern;

        public Implementation(Config config) {
            this.config = config;
        }

        public BrakValue[] check(PaymentData paymentData, BrakValue[] brakArray) throws FilterException {
            List braks = new ArrayList(Arrays.asList(brakArray));
            if (paymentData != null && paymentData.getSpecialClientCode() != null) {
               
                        ArrayList requisitePairList = new ArrayList();
                        try {
                            templateDictionary.checkRequisites(paymentData, requisitePairList);
                            StringBuffer wrongField = new StringBuffer();                            
                            if (paymentData.getAdditionalRequisites().contains("\t") || paymentData.getAdditionalRequisites().contains("\n") || paymentData.getAdditionalRequisites().contains("\r\n")) {
                            	braks.add(new BrakValue(BrakCode.MANUAL_BRAK, "Содержит запреденный символ (табуляция, перевод строки)", "Содержит запреденный символ (табуляция, перевод строки)", true));
                            }
                            if (paymentData.getAdditionalRequisites().equals("")) {
                                braks.add(brakDictionary.get(BrakCode.TEMPLATE_REQUISITES_BRAK).fill(paymentData, wrongField));
                            }
                            try {
                                    if (paymentData.getInn().length()<10) {
                                        braks.add(new BrakValue(BrakCode.MANUAL_BRAK, "Пустой ИНН организации", "Пустой ИНН организации", true));                                
                                     }
                                 } catch (Exception e) {
                                     braks.add(new BrakValue(BrakCode.MANUAL_BRAK, "Пустой ИНН организации", "Пустой ИНН организации", true));
                                     new FilterException(e);
                                   }
                        } catch (SQLException e) {
                            new FilterException(e);
                        }
                    }                
            
            return (BrakValue[]) braks.toArray(brakArray);
        }

       

        public void close() throws FilterException {
            templateDictionary.close();
        }

        public void init() throws FilterException {
            ConnectionSource connectionSource = new SpecialConnectionSource();
            brakDictionary = BrakTypeCachingDictionary.getInstance(connectionSource);
            templateDictionary = new TemplateDictionary(connectionSource);
            try {
                templateDictionary.init();
            } catch (SQLException e) {
                throw new FilterException(e);
            }
       
        }
    }

    private static final class Config {
        //final String brakCode;
        //final String addReqFormat;

        public Config(PluginConfigurationHelper helper) {
          //  brakCode = helper.getString("brakCode", BrakCode.TEMPLATE_REQUISITES_BRAK + "|" + BrakCode.MLS_BRAK);
          //  addReqFormat = helper.getString("addReqFormat", ".* ");
        }
    }
}
