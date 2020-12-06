package com.epam.sbrf.accdocs.sbrf3.plugins.search.impl;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.logging.Logger;

import com.epam.sbrf.accdocs.sbrf3.constants.SBRF3FieldConstants;
import com.epam.sbrf.accdocs.sbrf3.plugins.search.DataSearchPlugin;
import com.epam.sbrf.accdocs.sbrf3.plugins.search.PayOrderDataSearchPlugin;
import com.epam.sbrf.accdocs.sbrf3.processors.DataProcessor;
import com.epam.sbrf.common.constants.AccountingDocumentTypes;
import com.epam.sbrf.common.constants.FieldConstants;
import com.epam.sbrf.common.util.Util;
import com.sbrf.bc.plugin.PluginConfigurationHelper;
import com.sbrf.util.logging.SBRFLogger;
import com.sbrf.util.text.LightDateFormat;

/**
 * Плагин для поиска расчетных документов. Все зависимые от типа
 * документа операции выполняются здесь.
 */
public class PayOrderDataSearchPluginImplFixed extends DataSearchPluginImpl implements DataSearchPlugin, PayOrderDataSearchPlugin {

    private final Logger logger = SBRFLogger.getInstance("com.sbrf.bc.Logger");

    private final Config config;


    public PayOrderDataSearchPluginImplFixed(Properties properties) {
        super(properties);
        PluginConfigurationHelper helper = new PluginConfigurationHelper(properties);
        config = new Config(helper);
        logger.info(getClass().getName());
    }

    protected Map<String, Object> getFields(ResultSet rs, Date operDay) throws SQLException {
        LightDateFormat DATE_FORMAT = new LightDateFormat("ddMMyy");

        Map<String, Object> map = new HashMap<String, Object>();

        String formattedOperDay = DATE_FORMAT.format(operDay);
        map.put(SBRF3FieldConstants.ORDER_NUM_STR, getString(rs, FieldConstants.ORDER_NUM));
        map.put(SBRF3FieldConstants.ORDER_NUM, getInt(rs, FieldConstants.ORDER_NUM));

        map.put(SBRF3FieldConstants.AM, String.valueOf(get_long(rs, FieldConstants.SUM)));
        map.put(SBRF3FieldConstants.BC, getString(rs, FieldConstants.BIC));
        map.put(SBRF3FieldConstants.BK, getString(rs, FieldConstants.CORR_ACC));
        map.put(SBRF3FieldConstants.BN, getString(rs, FieldConstants.BANK));
        map.put(SBRF3FieldConstants.CA, getString(rs, FieldConstants.SETTLE_ACC));
        map.put(SBRF3FieldConstants.CU, config.cuConst);
        map.put(SBRF3FieldConstants.DA, getString(rs, FieldConstants.PAYER_SETTLE_ACC));
        map.put(SBRF3FieldConstants.DH, DATE_FORMAT.format(getDate(rs, FieldConstants.PAYMENT_DATE)));
        map.put(SBRF3FieldConstants.DT, config.dtConst);
        map.put(SBRF3FieldConstants.ED, DATE_FORMAT.format(getDate(rs, FieldConstants.BASE_PAYMENT_DATE)));
        map.put(SBRF3FieldConstants.FD, DATE_FORMAT.format(getDate(rs, FieldConstants.PAYMENT_DATE)));
        map.put(SBRF3FieldConstants.FF, config.ffConst);

        map.put(SBRF3FieldConstants.IB, "");
        map.put(SBRF3FieldConstants.IC, "");
        map.put(SBRF3FieldConstants.IN, "000" + getString(rs, FieldConstants.ORDER_NUM) + formattedOperDay + config.documentTypeField);
        map.put(SBRF3FieldConstants.IS, "");

        map.put(SBRF3FieldConstants.KP, getString(rs, FieldConstants.PAYER_KPP));
        map.put(SBRF3FieldConstants.KR, getString(rs, FieldConstants.KPP));

        map.put(SBRF3FieldConstants.LD, DATE_FORMAT.format(operDay));
        map.put(SBRF3FieldConstants.MP, "");
        map.put(SBRF3FieldConstants.MT, config.mtConst);

        map.put(SBRF3FieldConstants.NB, getString(rs, FieldConstants.KBK));
        map.put(SBRF3FieldConstants.NC, getString(rs, FieldConstants.OKATO));
        map.put(SBRF3FieldConstants.ND, getString(rs, FieldConstants.TAXES_DOCUMENT_DATE));
        map.put(SBRF3FieldConstants.NN, getString(rs, FieldConstants.TAXES_DOCUMENT_NUMBER));
        map.put(SBRF3FieldConstants.NO, getString(rs, FieldConstants.TAXES_REASON));
        map.put(SBRF3FieldConstants.NP, getString(rs, FieldConstants.TAXES_PERIOD));
        map.put(SBRF3FieldConstants.NS, getString(rs, FieldConstants.CREATOR_STATUS));
        map.put(SBRF3FieldConstants.NT, getString(rs, FieldConstants.TAXES_DOCUMENT_TYPE));

        map.put(SBRF3FieldConstants.PA, config.paConst);
        map.put(SBRF3FieldConstants.PN,
                Util.truncateToLength(getString(rs, FieldConstants.PAYER_NAME), config.payerNameMaxLength));
        map.put(SBRF3FieldConstants.PP,
                Util.truncateToLength(getString(rs, FieldConstants.PAYMENT_DESTINATION), config.paymentDestinationMaxLength));
        map.put(SBRF3FieldConstants.PT, config.ptConst);

        map.put(SBRF3FieldConstants.RA, getString(rs, FieldConstants.SETTLE_ACC));
        map.put(SBRF3FieldConstants.RC, config.rcConst);
        map.put(SBRF3FieldConstants.RI, getString(rs, FieldConstants.INN));
        map.put(SBRF3FieldConstants.RF, "");
        map.put(SBRF3FieldConstants.RN,
                Util.truncateToLength(getString(rs, FieldConstants.PAYER_INFO), config.payerInfoMaxLength));
        map.put(SBRF3FieldConstants.RS, config.rsConst);

        map.put(SBRF3FieldConstants.SA, getString(rs, FieldConstants.PAYER_SETTLE_ACC));
        map.put(SBRF3FieldConstants.SB, getString(rs, FieldConstants.PAYER_BANK));

        map.put(SBRF3FieldConstants.SC, config.scConst);
        map.put(SBRF3FieldConstants.SD, DATE_FORMAT.format(getDate(rs, FieldConstants.PAYMENT_DATE)));
        map.put(SBRF3FieldConstants.SF, "");
        map.put(SBRF3FieldConstants.SH, getString(rs, FieldConstants.OPERATION_TYPE));
        map.put(SBRF3FieldConstants.SI, getString(rs, FieldConstants.PAYER_INN));
        map.put(SBRF3FieldConstants.SK, getString(rs, FieldConstants.PAYER_CORR_ACC));
        map.put(SBRF3FieldConstants.SN, getString(rs, FieldConstants.PAYER_BIK));
        map.put(SBRF3FieldConstants.SS, config.ssConst);
        map.put(SBRF3FieldConstants.ST, "");
        
        // код ПП (УИН)
        map.put(SBRF3FieldConstants.UI, getString(rs, FieldConstants.UIN));
// добавляем для получение тега ND равному дате базового дня
        if (config.ndFieldIfBK) {
            if ("0".equals(map.get(SBRF3FieldConstants.BK))) {
                map.put(SBRF3FieldConstants.ND, DATE_FORMAT.format(getDate(rs, FieldConstants.BASE_PAYMENT_DATE)));
            }

        }

        for (String fieldName : config.gammaTagsToEraseIfZero) {
            String value = (String) map.get(fieldName);
            if ("0".equals(value)) {
                map.remove(fieldName);
            }
        }

        return map;
    }

    protected int getDocumentType() {
        return AccountingDocumentTypes.PAYMENT_ORDER.getCode();
    }

    @Override
    protected String getFileNameTemplate() {
        return config.fileNameFormat;
    }

    private static final class Config extends DataSearchPluginImpl.Config {

        // формат выходного файла
        final String fileNameFormat;
        final boolean ndFieldIfBK; // настройка для ЦЧБ, если BK = 0, значит бюджет и оьязан быть ND
        
        // константы из файла-примера
        final String cuConst;

        final String dtConst;

        final String ptConst;

        final String rcConst;

        final String rsConst;

        final String scConst;

        final String ssConst;

        final String paConst;

        final String ffConst;

        final String mtConst;

        final int paymentDestinationMaxLength;

        final int payerNameMaxLength;

        final int payerInfoMaxLength;
        
        final String[] gammaTagsToEraseIfZero;

        Config(PluginConfigurationHelper context) {
            super(context);
            fileNameFormat = context.getString("output.fileNameFormat", 
                    "{" + DataProcessor.OPER_DAY_FILENAME_ATTRIBUTE + ",date,ddMM}-"
                    + "r_{" + CUSTOM_ROUTE + "}-pn_{" + CUSTOM_PASSAGE + "}-"
                    + "pc_{" + CUSTOM_PART_CODE + "}-"
                    + "ot_{" + CUSTOM_OPERATION_TYPE + "}-"
                    + "{" + DataProcessor.FILE_COUNTER_FILENAME_ATTRIBUTE + "}.txt");

            cuConst = context.getString("cuConst", "RUR");
            dtConst = context.getString("dtConst", "001");
            mtConst = context.getString("mtConst", "102");
            ptConst = context.getString("ptConst", "1201");
            rcConst = context.getString("rcConst", "4400000000");
            rsConst = context.getString("rsConst", "МФО");
            scConst = context.getString("scConst", "13");
            ssConst = context.getString("ssConst", "МФО");
            paConst = context.getString("paConst", "9900000KPP");
            ffConst = context.getString("ffConst", "1");

            gammaTagsToEraseIfZero = context.getString("gammaTagsToEraseIfZero", "unused").split(",");

            paymentDestinationMaxLength = context.getInteger("paymentDestinationMaxLength", 210);
            payerNameMaxLength = context.getInteger("payerNameMaxLength", 160);
            payerInfoMaxLength = context.getInteger("payerInfoMaxLength", 160);
            ndFieldIfBK = context.getBoolean("ndFieldIfBK", false);            
        }
    }
}
