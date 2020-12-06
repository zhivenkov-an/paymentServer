
package com.sbrf.bc.plugin.filters.accountNullED108;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;
import java.util.regex.Pattern;

import org.apache.commons.lang.StringUtils;

import com.epam.sbrf.bc.data.BrakCode;
import com.epam.sbrf.bc.data.BrakValue;
import com.epam.sbrf.bc.data.PaymentData;
import com.epam.sbrf.bc.plugin.PaymentsFilter;
import com.epam.sbrf.bc.plugin.PaymentsFilterFactory;
import com.epam.sbrf.exception.FilterException;
import com.sbrf.bc.plugin.PluginConfigurationHelper;
import com.sbrf.util.text.MessageFormat;

/**
 * Плагин общих браковок для платежей.
 * TODO: перенести проверки ИНН и счета в общую логику
 * @author Stadnichenko-OV
 *
 */
public class CustomizedConditionsFilter implements PaymentsFilterFactory {
    private final Config config;
    
    public CustomizedConditionsFilter(Properties properties) {
        PluginConfigurationHelper context = new PluginConfigurationHelper(properties);
        this.config = new Config(context);
    }
    
    public PaymentsFilter createPaymentsFilter() {
        return new Implementation(config);
    }
    
    private static final class Implementation implements PaymentsFilter {
        private final Config config;
        
        public Implementation(Config config) {
            this.config = config;
        }
        
        public BrakValue[] check(PaymentData paymentData, BrakValue[] brakArray) throws FilterException {
            List<BrakValue> braks = new ArrayList<BrakValue>(Arrays.asList(brakArray));
            for (int i = 0; i < config.specCodesList.size(); i++) {
                if (StringUtils.isBlank(paymentData.getSpecialClientCode())) {
                    braks.add(new BrakValue(BrakCode.CUSTOM_BRAK, "Отсутствует код спец.клиента", "Отсутствует код спец.клиента", true));
                }else if (paymentData.getSpecialClientCode().matches(config.specCodesList.get(i))) {
                    String message = MessageFormat.format(config.message, new Object[]{config.specCodesList.get(i)});
                    braks.add(new BrakValue(BrakCode.CUSTOM_BRAK, message, message, config.isCritical));
                }
            }
            if (paymentData.getPayerInn() != null && !paymentData.getPayerInn().matches(config.checkPayerInnRegExp) && paymentData.getPayOrderAlgorithm().equals(config.algorithm)) {
                braks.add(new BrakValue(BrakCode.CUSTOM_BRAK, "Некорректный ИНН плательщика в сводном поручении ED108", "Некорректный ИНН плательщика в сводном поручении ED108", config.isCritical));
            }
            if (paymentData.getPayerAcc() != null && !paymentData.getPayerAcc().matches(config.checkPayerAccountRegExp) && paymentData.getPayOrderAlgorithm().equals(config.algorithm)) {
                braks.add(new BrakValue(BrakCode.CUSTOM_BRAK, "Некорректный счет плательщика в сводном поручении ED108", "Некорректный счет плательщика в сводном поручении ED108", config.isCritical));
            }
            return braks.toArray(brakArray);
        }

        public void close() throws FilterException {
        }

        public void init() throws FilterException {
        }
        
    }
    
    private static final class Config {
        final String specCodes;
        final String algorithm;        
        final String message;
        final boolean isCritical;
        List<String> specCodesList;
        final String checkPayerAccountRegExp;
        final String checkPayerInnRegExp;
        
        public Config(PluginConfigurationHelper context) {
            this.specCodes = context.getString("specCodes", "");
            this.algorithm = context.getString("algorithm", "COMMON_BUDGET_9013");
            this.message = context.getString("message", "Отбракован по коду спец.клиента. Шаблон {0}.");
            this.isCritical = context.getBoolean("isCritical", false);
            makeList();
            checkPayerAccountRegExp = context.getString("checkPayerAccountRegExp", "()|(4\\d{4}810\\d{12})");
            checkPayerInnRegExp = context.getString("checkPayerInnRegExp", "()|(\\d{12})");
        }
        
        private void makeList() {
            this.specCodesList = new ArrayList<String>();
            String[] codes = specCodes.split(",");
            for (int i = 0; i < codes.length; i++) {
                if (StringUtils.isNotBlank(codes[i])) {
                    specCodesList.add(codes[i].trim());
                }
            }
        }
    }

}
