package com.sbrf.bc.plugin.filters.doubleKommission;


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
//import com.epam.sbrf.bc.dictionary.BrakTypeCachingDictionary;
import com.epam.sbrf.bc.dictionary.TemplateDictionary;
import com.epam.sbrf.bc.jdbc.SpecialConnectionSource;
import com.epam.sbrf.bc.plugin.PaymentsFilter;
import com.epam.sbrf.bc.plugin.PaymentsFilterFactory;
import com.epam.sbrf.exception.FilterException;
import com.sberbank.sbclients.util.dao.ConnectionSource;
//import com.sbrf.util.text.NamedMessageFormat;
import com.sbrf.bc.plugin.PluginConfigurationHelper;

/**
 * 
 * @author Zhivenkov-AN
 *  Браковать платежи если срабатывает условие взимания двойной комиссии
 */
public class DoubleKommissionFilter implements PaymentsFilterFactory {

    private final Config config;

    public DoubleKommissionFilter(Properties properties) {
        config = new Config(new PluginConfigurationHelper(properties));
    }

    public PaymentsFilter createPaymentsFilter() {
        return new Implementation(config);
    }

    private static final class Implementation implements PaymentsFilter {
        private TemplateDictionary templateDictionary;
        //private BrakTypeCachingDictionary brakDictionary;
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

                            if (paymentData.getPayerCommission()>0 && !paymentData.getRateCode().equals("NONE") && !paymentData.getInn().equals(config.iskInn1) && !paymentData.getInn().equals(config.iskInn2) && !paymentData.getInn().equals(config.iskInn3)) {

                                braks.add(new BrakValue(BrakCode.MANUAL_BRAK, "Двойное взимание комиссии", "Двойное взимание комиссии", true));
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
           // brakDictionary = BrakTypeCachingDictionary.getInstance(connectionSource);
            templateDictionary = new TemplateDictionary(connectionSource);
            try {
                templateDictionary.init();
            } catch (SQLException e) {
                throw new FilterException(e);
            }
       
        }
    }

    private static final class Config {
        final String iskInn1;
        final String iskInn2;
        final String iskInn3;
        //final String addReqFormat;

        public Config(PluginConfigurationHelper helper) {
            iskInn1 = helper.getString("iskInn1", "6901067107");
            iskInn2 = helper.getString("iskInn2", "6901067107");
            iskInn3 = helper.getString("iskInn3", "6901067107");
          //  addReqFormat = helper.getString("addReqFormat", ".* ");
        }
    }
}
