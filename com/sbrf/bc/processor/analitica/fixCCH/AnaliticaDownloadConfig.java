package com.sbrf.bc.processor.analitica.fixCCH;

import com.sbrf.bc.plugin.PluginConfigurationHelper;
import com.sbrf.bc.plugin.SettingNames;


final class AnaliticaDownloadConfig {
    final boolean single;
    final String sqlAnalit;
    final String outputEncoding;
    final String lineSeparator;
    final String outputDirectory;
    final String fileNameFormat;
    final String transactionFormat;
    final String defaultOperationTime;

    /**
     * Использование доп.реквизитов из RG.PROC
     */
    //final boolean useRgProps;
    /**
     * Использовать доп.реквизиты из обоих источников:
     * из PAY.PAYMENTS и если useRgProps == true из RG.PROC(RG.PROPS приоритетнее)
     */
    
    public AnaliticaDownloadConfig(PluginConfigurationHelper context) {
        this.single = context.getBoolean("single", false);
        this.sqlAnalit =  context.getString("sqlAnalit", "AnaliticaCPM.sql"); 
        this.outputEncoding = context.getString(SettingNames.OUTPUT_ENCODING, "windows-1251");
        this.outputDirectory = context.getString(SettingNames.OUTPUT_DIRECTORY, "");
        this.lineSeparator = context.getLineSeparator("lineSeparator");
        this.fileNameFormat = context.getString(SettingNames.FILE_NAME_FORMAT, "{0,date,yyyyMMdd}-{2}.cpm");
        this.transactionFormat = context.getString("transactionFormat","{0,number,0.00}|{1,date,dd.MM.yyyy}|{2}|{3}|{4}|{5}");
        this.defaultOperationTime = context.getString("defaultOperationTime", "");
        
    }        
}
