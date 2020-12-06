package com.sbrf.cch.addUser;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.regex.Pattern;

import com.sbrf.bc.plugin.PluginConfigurationHelper;
import com.sbrf.bc.processor.operday.OperDay;

public class AddUserConfig {
    
    final DateFormat dateFormatRepeat = new SimpleDateFormat("yyyyMMdd");
    final String fileEncoding;
    final String dateFormat;
    final String roleUser;
    final Integer validity;
    final Pattern informationalRepeat;
    final String sqlQueryPath;
    final String sqlQueryPath2;
    final String sqlQueryPath3;
    // наименование плагина операционного дня
    final String operDayPluginName;

    public AddUserConfig(PluginConfigurationHelper context) {
        operDayPluginName = context.getString("operDayPluginName", OperDay.PLUGIN_NAME);
        fileEncoding = context.getString("fileEncoding", "Cp1251");
        dateFormat = context.getString("dateFormat", "dd.MM.yyyy");
        roleUser = context.getString("roleUser", "vsp_viewer");
        // период действия первоначального пароля
        validity = context.getInteger("validity",40); 
        // входная строка NUM|ROLE|DOMAIN_NAME|FIO|POSITION|DEPT работает регулярка ^(.*)\|(.*)\|(.*)\|(.*)\|(.*)\|(.*)$ 
        informationalRepeat = Pattern.compile(context.getString("informationalRepeat", "^(.*)\\t(.*)\\t(.*)\\t(.*)\\t(.*)\\t(.*)$"));        
        sqlQueryPath = context.getString("sqlQueryPath", "com/sbrf/cch/addUser/InsertUconf.sql");
        sqlQueryPath2 = context.getString("sqlQueryPath2", "com/sbrf/cch/addUser/InsertUgrus.sql");
        sqlQueryPath3 = context.getString("sqlQueryPath3", "com/sbrf/cch/addUser/InsertUright.sql");
    }

}
