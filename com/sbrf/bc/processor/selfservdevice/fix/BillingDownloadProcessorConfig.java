package com.sbrf.bc.processor.selfservdevice.fix;

import com.sbrf.bc.plugin.PluginConfigurationHelper;

public class BillingDownloadProcessorConfig extends SelfServiceRegistryConfig{
    final String SQLQueryPath;
    final boolean ignoreBrak;
    final boolean uploadIntoDB;
    final boolean indentXML;
    final String warningsOutputFileNameFormat;
    final String defaultOsb;
    final String zipOutputFileNameFormat;
    final String zipBlockOutputFileNameFormat;
    final String additionalWhere;
    final String timeFormat;
    final String paymentServerTimeFormat;
    final String paymentServerDateFormat;
    final int limitPaymentsInFile;
    final String billingDatasourceJDBCName;
    final boolean usePlinfAsAdditionalRequisites;
    
    public BillingDownloadProcessorConfig(PluginConfigurationHelper context) {
        super(context);
        this.SQLQueryPath = context.getString("SQLQueryPath", "/com/sbrf/bc/processor/selfservdevice/fix/BillingDownloadProcessor.sql");
        this.ignoreBrak = context.getBoolean("ignoreBrak", false);
        this.warningsOutputFileNameFormat = context.getString("warningsOutputFileNameFormat", "{paymentDate,date,yyyyMMdd}_{currentDate,date,yyyyMMdd}-warnings-r");
        this.defaultOsb = context.getString("defaultOsb", "5278");
        this.zipOutputFileNameFormat = context.getString("zipOutputFileNameFormat", "billing{noosb}_{paymentDate,date,yyyyMMdd}_{currentDate,date,yyyyMMddHHmm}_{blockEntryNumber}.zip");
        this.zipBlockOutputFileNameFormat = context.getString("zipOutputBlockFileNameFormat", "billing{paymentDate,date,yyyyMMdd}_{blockEntryNumber}.xml");
        this.timeFormat = context.getString("timeFormat", "HHmmss");
        this.paymentServerTimeFormat = context.getString("paymentServerTimeFormat", "HH:mm:ss");
        this.paymentServerDateFormat = context.getString("paymentServerDateFormat", "yyyy-MM-dd");
        this.uploadIntoDB = context.getBoolean("uploadIntoDB", false);
        this.indentXML = context.getBoolean("indentXML", true);
        this.limitPaymentsInFile = context.getInteger("limitPaymentsInFile", 5000);
        this.additionalWhere = context.getString("additionalWhereCondition", "");
        this.billingDatasourceJDBCName = context.getString("billingDatasourceJDBCName", "jdbc/BillingDataSource");
        this.usePlinfAsAdditionalRequisites = context.getBoolean("usePlinfAsAdditionalRequisites", true);        
    }
    
}
