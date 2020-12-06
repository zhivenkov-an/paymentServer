package com.sbrf.bc.plugin.filters.budgetNotShablon;


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
//import com.sbrf.bc.plugin.PluginConfigurationHelper;
//import com.sbrf.util.text.NamedMessageFormat;

/**
 * 
 * @author Zhivenkov-AN
 *  Браковать платежи если срабатывает условие взимания двойной комиссии
 */
public class BudgetNotShablonFilter implements PaymentsFilterFactory {

  //  private final Config config;

    public BudgetNotShablonFilter(Properties properties) {
    //    config = new Config(new PluginConfigurationHelper(properties));
    }

    public PaymentsFilter createPaymentsFilter() {
        return new Implementation();
    }

    private static final class Implementation implements PaymentsFilter {
        private TemplateDictionary templateDictionary;
        //private BrakTypeCachingDictionary brakDictionary;
       // private final Config config;
        //private Pattern addReqFormatPattern;

        /*public Implementation(Config config) {
            this.config = config;
        }*/

        public BrakValue[] check(PaymentData paymentData, BrakValue[] brakArray) throws FilterException {
            List braks = new ArrayList(Arrays.asList(brakArray));            
            if (paymentData != null && paymentData.getCorrAcc() != null && paymentData.getKbk() != null) {
               
                        ArrayList requisitePairList = new ArrayList();
                        try {
                            templateDictionary.checkRequisites(paymentData, requisitePairList);
                            //StringBuffer wrongField = new StringBuffer();
                            if (paymentData.getCorrAcc().length()==20 && paymentData.getKbk().length()==20) {
                                //braks.add(brakDictionary.get(BrakCode.TEMPLATE_BRAK).fill(paymentData, wrongField));
                                braks.add(new BrakValue(BrakCode.MANUAL_BRAK, "Бюджетный шаблон а счет не в ГРКЦ ГУ Банка России", "Бюджетный шаблон а счет не в ГРКЦ ГУ Банка России", true));
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

   /* private static final class Config {
        //final String brakCode;
        //final String addReqFormat;

        public Config(PluginConfigurationHelper helper) {
          //  brakCode = helper.getString("brakCode", BrakCode.TEMPLATE_REQUISITES_BRAK + "|" + BrakCode.MLS_BRAK);
          //  addReqFormat = helper.getString("addReqFormat", ".* ");
        }
    }*/
}
