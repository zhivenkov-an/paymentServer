package com.sbrf.cch.passwd;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.regex.Pattern;

import com.sbrf.bc.plugin.PluginConfigurationHelper;
import com.sbrf.bc.processor.operday.OperDay;

public class PasswdConfig {
    
    final DateFormat dateFormatRepeat = new SimpleDateFormat("yyyyMMdd");
    final String fileEncoding;
    final String dateFormat;
    final Pattern informationalRepeat;
    final String sqlQueryPath;
    // наименование плагина операционного дня
    final String operDayPluginName;

    public PasswdConfig(PluginConfigurationHelper context) {
        operDayPluginName = context.getString("operDayPluginName", OperDay.PLUGIN_NAME);
        fileEncoding = context.getString("fileEncoding", "Cp866");
        dateFormat = context.getString("dateFormat", "dd.MM.yyyy");
        informationalRepeat = Pattern.compile(context.getString("informationalRepeat", "^(.*)\\|(.*)\\|(.*)\\|(.*)\\|$"));
        sqlQueryPath = context.getString("sqlQueryPath", "com/sbrf/cch/passwd/UpdatePasswd.sql");
    }

}
