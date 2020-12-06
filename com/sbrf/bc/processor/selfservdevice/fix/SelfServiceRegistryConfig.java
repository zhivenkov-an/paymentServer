package com.sbrf.bc.processor.selfservdevice.fix;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.epam.sbrf.bc.plugin.PaymentsFilterFactory;
import com.epam.sbrf.bc.plugin.RouteFactory;
import com.sbrf.bc.plugin.PluginConfigurationHelper;
import com.sbrf.bc.plugin.SettingNames;
import com.sbrf.bc.processor.operday.OperDay;

public class SelfServiceRegistryConfig {
    final String inputRootElement;
    final String inputEncoding;
    final String outputEncoding;
    final String outputDirectory;
    final String inputFileNameFormat;
    final String outputFileNameFormat;
    final String lineSeparator;
    final String defaultFilial;
    final boolean completeFosb;
    final String defaultCashier;
    final String routeFactoryPluginName;
    final String paymentsFilterPluginName;
    final String accountFactoryPluginName;
    final String operDayPluginName;
    final String allowedOperDay;
    final String payOrderAlgorithm;
    final String rateCode;
    final String template;
    final String reestrAlgorithm;
    
    final int commitCount;
    final int batchCount;
    final boolean updateLoadedPayments;
    final String osbToChange;
    final String newOsb;
    final boolean changeOsb;
    final boolean allowMoveOutstandingToPayments;
    final boolean allowMovePaymentsToOutstanding;
    final boolean rollBackOnBatchFail;
    final String dateFormat;
    final List<String> excludeFromStorageParameterShortNames;
    final Map<Integer, String> paymentTypesToAccountKeys;
    final String xmlDocumentFormatTagsPlugin;
    
    final boolean brakOnResolveServiceChannelAndPaymenMeansError;
    
    /**
     * Константы настроек в какие дни допустима загрузка
     */
    public static final String ALL = "ALL";
    public static final String TODAY = "TODAY";
    public static final String NOT_TODAY = "NOT_TODAY";

    public SelfServiceRegistryConfig(PluginConfigurationHelper context) {
        this.inputRootElement = context.getString("inputRootElement", "jbt_output");
        this.inputEncoding = context.getString(SettingNames.INPUT_ENCODING, "windows-1251");
        this.outputEncoding = context.getString(SettingNames.OUTPUT_ENCODING, "windows-1251");
        this.inputFileNameFormat = context.getString(SettingNames.INPUT_FILE_NAME_FORMAT, ".*");
        this.outputFileNameFormat = context.getString("outputFileNameFormat", "{0}-r");
        this.outputDirectory = context.getString(SettingNames.OUTPUT_DIRECTORY, "");
        this.lineSeparator = context.getLineSeparator("lineSeparator");
        this.defaultCashier = context.getString("defaultCashier", "99999");
        this.defaultFilial = context.getString("defaultFilial", "09999");
        completeFosb = context.getBoolean("completeFosb", true);
        this.routeFactoryPluginName = context.getString("routePlugin", RouteFactory.PLUGIN_NAME);
        this.paymentsFilterPluginName = context.getString("paymentsFilterPluginName", PaymentsFilterFactory.PLUGIN_NAME);
        this.accountFactoryPluginName = context.getString("accountPlugin", "com.epam.sbrf.bc.plugin.AccountFactory");
        this.operDayPluginName = context.getString("operDayPluginName", OperDay.PLUGIN_NAME);
        this.allowedOperDay = context.getString("loadConstraint", SelfServiceRegistryConfig.ALL);
        this.payOrderAlgorithm = context.getString("payOrderAlgorithm", "SINGLE");
        this.rateCode = context.getString("rateCode", "NONE");
        this.template = context.getString("template", "FT296");
        this.reestrAlgorithm = context.getString("reestrAlgorithm", "MULTIPLE_PAYMENTS");
        
        this.commitCount = context.getInteger("commitCount", 10000);
        this.batchCount = context.getInteger("batchCount", 5000);
        this.updateLoadedPayments = context.getBoolean("updateLoadedPayments", true);
        this.dateFormat = context.getString("dateFormat", "yyyy-MM-dd");
        
        this.newOsb = context.getString("newOsb", "9038");
        this.osbToChange = context.getString("osbToChange",
                "1569,5278,5281,6901,7811,7813,7954,7970,7977,7978,7981,7982,8641");
        this.changeOsb = context.getBoolean("changeOsb", true);
        this.allowMoveOutstandingToPayments = context.getBoolean("allowMoveOutstandingToPayments", true);
        this.allowMovePaymentsToOutstanding = context.getBoolean("allowMovePaymentsToOutstanding", false);
        this.rollBackOnBatchFail = context.getBoolean("rollBackOnBatchFail", true);
        String excludeShortNames = context.getString(
                "excludeFromStorageParameterShortNames",
                "paymentId,PAYMENTID,specialClientCode,SPECIALCLIENTCODE,recipcode,RECIPCODE,paymentDestinationFake,PAYMENTDESTINATIONFAKE,recip_nameorg,RECIP_NAMEORG,recip_bic,RECIP_BIC,recipcod_spc,RECIPCOD_SPC,recipbank,RECIPBANK,recipaccount,RECIPACCOUNT");
        this.excludeFromStorageParameterShortNames = new ArrayList<String>();
        this.excludeFromStorageParameterShortNames.addAll(Arrays.asList(excludeShortNames.split(",", -1)));
        
        this.paymentTypesToAccountKeys = new HashMap<Integer, String>();
        String[] typesToRoles = context.getString("paymentTypesToAccountRoles", "1#30233;5#30233").split(";", -1);
        Pattern mappingPattern = Pattern.compile("(\\d+)#(\\S+)");
        Matcher matcher = mappingPattern.matcher("");
        for (int i = 0; i < typesToRoles.length; i++) {
            matcher.reset(typesToRoles[i]);
            if (matcher.matches()) {
                paymentTypesToAccountKeys.put(new Integer(Integer.parseInt(matcher.group(1))), matcher.group(2));
            }
        }
        this.xmlDocumentFormatTagsPlugin = context.getString("xmlDocumentFormatTagsPlugin", SelfServiceTagsPlugin.PLUGIN_NAME);
        brakOnResolveServiceChannelAndPaymenMeansError = context.getBoolean("brakOnResolveServiceChannelAndPaymenMeansError", true);
    }
}
