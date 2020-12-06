package com.sbrf.bc.processor.analitica;

import com.sbrf.bc.plugin.PluginConfigurationHelper;
import com.sbrf.bc.plugin.SettingNames;
import com.sbrf.bc.processor.selfservdevice.SelfServiceTagsPlugin;

final class AnaliticaDownloadConfig {
    final boolean single;
    final String participantCode;
    final String outputEncoding;
    final String lineSeparator;
    final String outputDirectory;
    final String fileNameFormat;
    final String transactionFormat;
    final String defaultOperationTime;
    final long inDoc;
    /**
     * Использование доп.реквизитов из RG.PROC
     */
    final boolean useRgProps;
    /**
     * Использовать доп.реквизиты из обоих источников:
     * из PAY.PAYMENTS и если useRgProps == true из RG.PROC(RG.PROPS приоритетнее)
     */
    final boolean useAllRequisites;
    final boolean excludeDeviceInfoFromCards;
    
    final boolean useRgPropsForReceiver;
    final String unnecessaryFieldsNames;
    final String xmlDocumentFormatTagsPlugin;
    
    public AnaliticaDownloadConfig(PluginConfigurationHelper context) {
        this.single = context.getBoolean("single", false);
        this.participantCode = context.getString("participantCode", "9900000KPP");
        this.outputEncoding = context.getString(SettingNames.OUTPUT_ENCODING, "windows-1251");
        this.outputDirectory = context.getString(SettingNames.OUTPUT_DIRECTORY, "");
        this.lineSeparator = context.getLineSeparator("lineSeparator");
        this.fileNameFormat = context.getString(SettingNames.FILE_NAME_FORMAT, "{0,date,yyyyMMdd}.k{2}");
        this.transactionFormat = context.getString("transactionFormat","{0,}|{1}|{2}|{3,date,dd.MM.yy}|{4,text,width=25}|{5,text,width=25}|{6}|{7,number,0.00}|{8}|{9}|{10}|{11}|{12}");
        this.defaultOperationTime = context.getString("defaultOperationTime", "00-00-00");
        this.useRgProps = context.getBoolean("useRgProps",true);
        this.useAllRequisites = context.getBoolean("useAllrequisites", true);
        this.inDoc = Long.parseLong(context.getString("inDoc", "900000000"));
        this.excludeDeviceInfoFromCards = context.getBoolean("excludeDeviceInfoFromCards", true);
        this.useRgPropsForReceiver = context.getBoolean("useRgPropsForReceiver", true);
        this.unnecessaryFieldsNames = context.getString("unnecessaryFieldsNames", "merchantNumber=73");
        this.xmlDocumentFormatTagsPlugin = context.getString("xmlDocumentFormatTagsPlugin", SelfServiceTagsPlugin.PLUGIN_NAME);
        
    }        
}
