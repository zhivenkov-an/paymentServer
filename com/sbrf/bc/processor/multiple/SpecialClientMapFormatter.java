package com.sbrf.bc.processor.multiple;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.Map.Entry;

import billing.util.SumConverter;

import com.sberbank.sbclients.util.dao.db2.DAOUtil;
import com.sbrf.bc.nsi.BillingObjectTypes;
import com.sbrf.data.DownloadsPropertiesData;
import com.sbrf.data.Maket1Data;
public class SpecialClientMapFormatter {
    
    protected SpecialClientMapFormatter() {
        
    }
    
    /**
     * Метод заполняет мэп пар-ров нужными значениями для 
     * имени архива
     * @param payment - класс, содержащий запись из таблицы PAY.PAYMENTS
     * @param data - класс, содержащий запись из таблицы RG.DOWNLOADS_PROPS 
     * @param transferDate - дата формирования платежного поручения
     * @param oneDigitYear - первая цифра даты формирования платежного поручения
     * @param fileNumber - номер файла
     * @return созданный и заполненный мэп параметров
     */
    static public Map makeZipFileNameParameters(
            Set aliasSet,
            Maket1Data payment,
            DownloadsPropertiesData data,
            Date transferDate,
            Integer oneDigitYear,
            String payerAccount,
            long fileNumber
            ) {
        Map result = new TreeMap();
        populateMapParameters(aliasSet, result, payment, data, transferDate, oneDigitYear, payerAccount);
        result.put(DownloadFieldsMapper.FILE_NUMBER, new Long(fileNumber));
        return result;
    }
    
    /**
     * Метод заполняет мэп пар-ров нужными значениями для 
     * имен файлов
     * @param payment - класс, содержащий запись из таблицы PAY.PAYMENTS
     * @param data - класс, содержащий запись из таблицы RG.DOWNLOADS_PROPS 
     * @param transferDate - дата формирования платежного поручения
     * @param oneDigitYear - первая цифра даты формирования платежного поручения
     * @param fileNumber - номер файла
     * @param kkk - первые 3 символа кода спецклиента
     * @param ll - последние символы кода спецклиента, начиная с 4-го
     * @return созданный и заполненный мэп параметров
     */
    public static Map makeFileNameParameters(
            Set aliasSet,
            Maket1Data payment,
            DownloadsPropertiesData data,
            Date transferDate,
            Integer oneDigitYear,
            String payerAccount,
            long fileNumber,
            String kkk,
            String ll
            ) {
        Map result = new TreeMap();
        populateMapParameters(aliasSet, result, payment, data, transferDate, oneDigitYear, payerAccount);
        result.put(DownloadFieldsMapper.FILE_NUMBER, new Long(fileNumber));
        result.put(DownloadFieldsMapper.FIRST_3_CODE_SYMBOLS, kkk);
        result.put(DownloadFieldsMapper.LAST_2_CODE_SYMBOLS, ll);
        return result;
    }
    
    /**
     * Метод заполняет мэп пар-ров нужными значениями для 
     * заголовков файлов
     * @param payment - класс, содержащий запись из таблицы PAY.PAYMENTS
     * @param data - класс, содержащий запись из таблицы RG.DOWNLOADS_PROPS 
     * @param transferDate - дата формирования платежного поручения
     * @param oneDigitYear - первая цифра даты формирования платежного поручения
     * @param fileName - имя файла
     * @return созданный и заполненный мэп параметров
     */
    public static Map makeHeaderParameters(
            Set aliasSet,
            Maket1Data payment,
            DownloadsPropertiesData data,
            Date transferDate,
            Integer oneDigitYear,
            String payerAccount,
            String fileName) {
        Map result = new TreeMap();
        populateMapParameters(aliasSet, result, payment, data, transferDate, oneDigitYear, payerAccount);
        result.put(DownloadFieldsMapper.FILE_NAME, fileName);
        return result;
    }
    
    /**
     * Метод заполняет мэп пар-ров нужными значениями для платежа 
     * @param payment - класс, содержащий запись из таблицы PAY.PAYMENTS
     * @param data - класс, содержащий запись из таблицы RG.DOWNLOADS_PROPS 
     * @param transferDate - дата формирования платежного поручения
     * @param oneDigitYear - первая цифра даты формирования платежного поручения
     * @param sum - исходная сумма платежа
     * @param clearSum - сумма после удержания комиссии
     * @param recipientCommission - комиссия с получателя
     * @return созданный и заполненный мэп параметров
     */
    public static Map makePaymentLineParameters(
            Set aliasSet,
            Maket1Data payment, 
            DownloadsPropertiesData data,
            Date transferDate,
            Integer oneDigitYear,
            String payerAccount,
            long sum,
            long clearSum,
            long recipientCommission) {
        Map result = new TreeMap();
        populateMapParameters(aliasSet, result, payment, data, transferDate, oneDigitYear, payerAccount);
        populateMapSums(sum, clearSum, recipientCommission, result);
        return result;
    }
    
    /**
     * Метод заполняет мэп пар-ров нужными значениями для отделений 
     * @param payment - класс, содержащий запись из таблицы PAY.PAYMENTS
     * @param data - класс, содержащий запись из таблицы RG.DOWNLOADS_PROPS 
     * @param transferDate - дата формирования платежного поручения
     * @param oneDigitYear - первая цифра даты формирования платежного поручения
     * @param numberOfPayments - кол-во платежей в ОСБ
     * @param sum - исходная сумма платежа
     * @param clearSum - сумма после удержания комиссии
     * @param recipientCommission - комиссия с получателя
     * @return созданный и заполненный мэп параметров
     */
    public static Map makeOSBLineParameters(
            Set aliasSet,
            Maket1Data payment, 
            DownloadsPropertiesData data,
            Date transferDate,
            Integer oneDigitYear,
            String payerAccount,
            long numberOfPayments,
            long sum,
            long clearSum,
            long recipientCommission) {
        Map result = new TreeMap();
        populateMapParameters(aliasSet, result, payment, data, transferDate, oneDigitYear, payerAccount);
        populateMapSums(sum, clearSum, recipientCommission, result);
        result.put(DownloadFieldsMapper.NUMBER_OF_PAYMENTS, new Long(numberOfPayments));
        return result;
    }
    
    /**
     * Метод заполняет мэп пар-ров нужными значениями для строки платежного поручения 
     * @param payment - класс, содержащий запись из таблицы PAY.PAYMENTS
     * @param data - класс, содержащий запись из таблицы RG.DOWNLOADS_PROPS 
     * @param transferDate - дата формирования платежного поручения
     * @param oneDigitYear - первая цифра даты формирования платежного поручения
     * @param numberOfPayments - кол-во платежей в платежном поручении
     * @param sum - исходная сумма платежа
     * @param clearSum - сумма после удержания комиссии
     * @param recipientCommission - комиссия с получателя
     * @return созданный и заполненный мэп параметров
     */
    public static Map makePaymentOrderLineParameters(
            Set aliasSet,
            Maket1Data payment,
            DownloadsPropertiesData data,
            Date transferDate,
            Integer oneDigitYear,
            String payerAccount,
            long numberOfPayments,
            long sum,
            long clearSum,
            long recipientCommission) {
        Map result = new TreeMap();
        populateMapParameters(aliasSet, result, payment, data, transferDate, oneDigitYear, payerAccount);
        populateMapSums(sum, clearSum, recipientCommission, result);
        result.put(DownloadFieldsMapper.NUMBER_OF_PAYMENTS, new Long(numberOfPayments));
        return result;
    }
    
    /**
     * Метод заполняет мэп пар-ров нужными значениями для 
     * имен файлов
     * @param payment - класс, содержащий запись из таблицы PAY.PAYMENTS
     * @param data - класс, содержащий запись из таблицы RG.DOWNLOADS_PROPS 
     * @param transferDate - дата формирования платежного поручения
     * @param oneDigitYear - первая цифра даты формирования платежного поручения
     * @param numberOfPayments - кол-во платежей в файле
     * @param sum - исходная сумма платежа
     * @param clearSum - сумма после удержания комиссии
     * @param recipientCommission - комиссия с получателя
     * @return созданный и заполненный мэп параметров
     */
    public static Map makeFooterParameters(
            Set aliasSet,
            Maket1Data payment,
            DownloadsPropertiesData data,
            Date transferDate,
            Integer oneDigitYear,
            String payerAccount,
            long numberOfPayments,
            long sum,
            long clearSum,
            long recipientCommission) {
        Map result = new TreeMap();
        populateMapParameters(aliasSet, result, payment, data, transferDate, oneDigitYear, payerAccount);
        populateMapSums(sum, clearSum, recipientCommission, result);
        result.put(DownloadFieldsMapper.NUMBER_OF_PAYMENTS, new Long(numberOfPayments));
        return result;
    }
    
    private static void populateMapSums(long sum, long clearSum, long recipientCommission, Map result) {
        result.put(DownloadFieldsMapper.SUM_ALIAS, new Long(sum));
        result.put(DownloadFieldsMapper.SUM_ROUBLES_ALIAS, SumConverter.getInstance().toBigDecimal(sum));
        result.put(DownloadFieldsMapper.SUM_CLEAR_ALIAS, new Long(clearSum));
        result.put(DownloadFieldsMapper.SUM_CLEAR_ROUBLES_ALIAS, SumConverter.getInstance().toBigDecimal(clearSum));
        result.put(DownloadFieldsMapper.SERVICE_COMM_ALIAS, new Long(recipientCommission));
        result.put(DownloadFieldsMapper.SERVICE_COMM_ROUBLES_ALIAS, SumConverter.getInstance().toBigDecimal(recipientCommission));
    }
    
    /**
     * Метод заполняет мэп пар-ров нужными значениями для общих для всех методов параметров 
     * @param result - мэп из заполненных параметров
     * @param payment - класс, содержащий запись из таблицы PAY.PAYMENTS
     * @param data - класс, содержащий запись из таблицы RG.DOWNLOADS_PROPS 
     * @param transferDate - дата формирования платежного поручения
     * @param oneDigitYear - первая цифра даты формирования платежного поручения
     */
    private static void populateMapParameters(
            Set aliasSet,
            Map result,
            Maket1Data payment,
            DownloadsPropertiesData data,
            Date transferDate,
            Integer oneDigitYear,
            String payerAccount
            ) {
        Iterator i = aliasSet.iterator();
        while (i.hasNext()) {
            Entry entry =(Entry) i.next();
            Object value = DownloadFieldsMapper.getField(payment, (String)entry.getKey());
            result.put(entry.getKey(),value);
        }
        result.put(DownloadFieldsMapper.PAYER_PERSONAL_ACCOUNT_ALIAS, payerAccount);
        result.put(DownloadFieldsMapper.TRANSFER_DATE_ALIAS, transferDate);
        result.put(DownloadFieldsMapper.SERVICE_KIND_ALIAS, data.getServiceKind());
        result.put(DownloadFieldsMapper.ONE_DIGIT_YEAR, oneDigitYear);
        result.put(DownloadFieldsMapper.PROVIDER_CODE, data.getProviderCode());
        result.put(DownloadFieldsMapper.PROVIDER_DEPARTMENT, data.getProviderDepartment());
    }
    
    public static void searchInRgProps(Connection connection, Maket1Data payment, Map parameters) throws SQLException {
        String sql = "select NAME, VALUE from RG.PROPS where OBJECT_TYPE = ? and OBJECT_ID = ? ";
        PreparedStatement statement = null;
        ResultSet resultSet = null;
        try {
            statement = connection.prepareStatement(sql);
            int i = 1;
            DAOUtil.setInt(statement, i++, BillingObjectTypes.OFFLINE_PAYMENT);
            DAOUtil.setLong(statement, i++, payment.getLinum());
            resultSet = statement.executeQuery();
            while (resultSet.next()) {
                String name = DAOUtil.getString(resultSet, "NAME");
                if ((parameters.containsKey(name)) && (parameters.get(name) == null)) {
                    parameters.put(name, DAOUtil.getString(resultSet, "VALUE"));
                }
            }
        } catch (SQLException e) {
            throw e;
        } finally {
            DAOUtil.close(resultSet);
            DAOUtil.close(statement);
        }
    }
}
