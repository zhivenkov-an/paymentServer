package com.sbrf.cch.zrp;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.regex.Pattern;

import com.sbrf.bc.plugin.PluginConfigurationHelper;
import com.sbrf.bc.processor.operday.OperDay;

public class ZrpConfig {
    
    final DateFormat dateFormatRepeat = new SimpleDateFormat("yyyyMMdd");
    final String fileEncoding;
    final String dateFormat;
    final Pattern informationalRepeat;
    final String sqlQueryPath;
    // наименование плагина операционного дня
    final String operDayPluginName;

    public ZrpConfig(PluginConfigurationHelper context) {
        operDayPluginName = context.getString("operDayPluginName", OperDay.PLUGIN_NAME);
        fileEncoding = context.getString("fileEncoding", "Cp1251");
        dateFormat = context.getString("dateFormat", "dd.MM.yyyy");
        // период действия первоначального пароля 
        // входная строка Фамилия Имя отчетство													1			2			3				4			5		6		7		8			9			10			11		12			13				14			15			16			17				18		19				20				21					22				23		24		25				26				27			28				29		30							31		32		33		34		35	 
        //informationalRepeat = Pattern.compile(context.getString("informationalRepeat", "^([А-Яа-я]+)\\t([А-Яа-я]+)\\t([А-Яа-я]+)\\t(\\d+)\\t(\\d+)\\t(\\d+)\\t(\\d+)\\t(\\d+)\\t([А-Яа-я- ]+)\\t([0-9 ]+)\\t(\\d+)\\t([0-9-]+)\\t([А-Яа-я -]+)\\t([0-9-]+)\\t([0-9-]+)\\t([А-Яа-я]+)\\t([А-Яа-я- ]+)\\t(\\d+)\\t([А-Яа-я- ]+)\\t([А-Яа-я- ]+)\\t([А-Яа-я- ]+)\\t([А-Яа-я- ]+)\\t(\\d*)\\t(\\d*)\\t(\\d*)\\t([A-Za-z]+)\\t([A-Za-z]+)\\t([A-Za-z]+)\\t([А-Яа-я ]+)\\t(\\d+)\\t([A-Za-zА-Яа-я0-9- ]+)\\t(\\d*)\\t(\\d*)\\t(\\d*)\\t(\\d*)$"));        
        informationalRepeat = Pattern.compile(context.getString("informationalRepeat", "^([А-Яа-я]+)\\t([А-Яа-я]+)\\t([А-Яа-я]+)\\t(\\d+)\\t(\\d+)\\t(\\d+)\\t(\\d+)\\t(\\d+)\\t([А-Яа-я- ]+)\\t([0-9 ]+)\\t(\\d+)\\t([0-9-]+)\\t([А-Яа-я -]+)\\t([0-9-]+)\\t([0-9-]+)\\t([А-Яа-я]+)\\t([А-Яа-я- ]+)\\t(\\d+)\\t([А-Яа-я- ]+)\\t([А-Яа-я- ]+)\\t([А-Яа-я- ]+)\\t([А-Яа-я- ]+)\\t(\\d*)\\t(\\d*)\\t(\\d*)\\t([A-Za-z ]+)\\t([A-Za-z ]+)\\t([A-Za-z ]+)\\t([А-Яа-я ]+)\\t(\\d*)\\t([A-Za-zА-Яа-я0-9- ]+)\\t(\\d*)\\t(\\d*)\\t(\\d*)\\t(\\d*)$"));
        sqlQueryPath = context.getString("sqlQueryPath", "com/sbrf/cch/zrp/InsertZrp.sql");
    }

}
