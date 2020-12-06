/*
 * Created on 16.03.2009
 */
package com.sbrf.bc.processor.multiple;

import com.sbrf.bc.plugin.PluginConfigurationHelper;
import com.sbrf.bc.plugin.SettingNames;
import com.sbrf.util.classloader.ResourceHelper;

/**
 * @author petrov-am
 */
public class SpecialClientPaymentsProcessorConfig {
    final String outputLineSeparator;
    final String selectPaymentsSQLFormat;
    final boolean useRgProps;
    final String outputInformationEncoding;
    final String outputDirectory;
    final String fileNameIndformation;
    final String validPaymentsStatus;
    final String inValidRouteStatus;
    final int groupOsbId;
    public SpecialClientPaymentsProcessorConfig(PluginConfigurationHelper context) {
        this.outputLineSeparator = context.getLineSeparator("output.line.separator");
        String selectPaymentsFileName = context.getString("selectPaymentsFileName", "selectPaymentsSpecialClient.sql");
        this.selectPaymentsSQLFormat = ResourceHelper.getResourceAsString(this.getClass(), selectPaymentsFileName);
        this.useRgProps = context.getBoolean("useRgProps", true);
        outputInformationEncoding = context.getString("outputInformationEncoding", "Cp1251");
        outputDirectory = context.getString(SettingNames.OUTPUT_DIRECTORY, "");
        fileNameIndformation = context.getString("fileNameIndformation", "special-client-handler-response-{0,date,yyyy-MM-dd-HH-mm}.txt");
        this.validPaymentsStatus = context.getString("validPaymentsStatus", "TRANSFERRED");
        this.inValidRouteStatus = context.getString("inValidRouteStatus", "MANUAL");
        groupOsbId = context.getInteger("groupOsbId", 1);
    }
}
