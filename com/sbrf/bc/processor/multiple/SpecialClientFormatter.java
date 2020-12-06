/*
 * Created on 30.07.2009
 */
package com.sbrf.bc.processor.multiple;

import java.util.Date;

import billing.util.SumConverter;

import com.sbrf.data.DownloadsPropertiesData;
import com.sbrf.data.Maket1Data;

/**
 * Класс предназначен для унифицированного формирования
 * параметров платежей, платежных поручений, контрольных
 * строк и имен файлов
 * @author Petrov-AM
 */
public class SpecialClientFormatter {
    
    /**
     * Значение по умолчанию для разделителя доп.реквизитов 
     */
    private static final String DEFAULT_SEPARATOR = "@";
    
    /**
     * Число, суммируемое с длиной доп.реквизитов для получения
     * значения длины из 1-ого макета
     */
    private static final int ADD_REQ_ADD_LENGTH = 8;

    private static final SumConverter sumConverter = SumConverter.getInstance();
    
    /**
     * Конструктор по умолчанию, чтобы пользоваться только 
     * static методами данного класса
     */
    private SpecialClientFormatter() {
        
    }
    
    /**
     * Создает массив из SpecialClientFields.MAX_PARAMETER_NUMBER
     * пустых строк
     * @return массив пустых строк
     */
    private static Object[] populateEmptyStrings() {
        Object[] result = new Object[SpecialClientFields.MAX_PARAMETER_NUMBER + 1];
        for (int i = 0; i <= SpecialClientFields.MAX_PARAMETER_NUMBER; i++) {
            result[i] = "";
        }
        return result;
    }
    
    /**
     * Метод заполняет массив пар-ров нужными значениями для 
     * имени архива
     * @param payment - класс, содержащий запись из таблицы PAY.PAYMENTS
     * @param data - класс, содержащий запись из таблицы RG.DOWNLOADS_PROPS 
     * @param transferDate - дата формирования платежного поручения
     * @param oneDigitYear - первая цифра даты формирования платежного поручения
     * @param fileNumber - номер файла
     * @return созданный и заполненный массив параметров
     */
    static public Object[] makeZipFileNameParameters(
            Maket1Data payment,
            DownloadsPropertiesData data,
            Date transferDate,
            Integer oneDigitYear,
            String payerAccount,
            long fileNumber
            ) {
        Object[] result = populateEmptyStrings();
        result[SpecialClientNonUniqueFields.ZIP_TRANSFER_DATE] = transferDate;
        result[SpecialClientNonUniqueFields.ZIP_ONE_DIGIT_YEAR] = oneDigitYear; 
        result[SpecialClientNonUniqueFields.ZIP_PROVIDER_CODE] = data.getProviderCode();
        result[SpecialClientNonUniqueFields.ZIP_FILE_NUMBER] = new Long(fileNumber);
        result[SpecialClientFields.FILE_NUMBER] = new Long(fileNumber);
        populateCommonParameters(result, payment, data, transferDate, oneDigitYear, payerAccount);
        return result;
    }
    
    /**
     * Метод заполняет массив пар-ров нужными значениями для 
     * имен файлов
     * @param payment - класс, содержащий запись из таблицы PAY.PAYMENTS
     * @param data - класс, содержащий запись из таблицы RG.DOWNLOADS_PROPS 
     * @param transferDate - дата формирования платежного поручения
     * @param oneDigitYear - первая цифра даты формирования платежного поручения
     * @param fileNumber - номер файла
     * @param kkk - первые 3 символа кода спецклиента
     * @param ll - последние символы кода спецклиента, начиная с 4-го
     * @return созданный и заполненный массив параметров
     */
    public static Object[] makeFileNameParameters(
            Maket1Data payment,
            DownloadsPropertiesData data,
            Date transferDate,
            Integer oneDigitYear,
            String payerAccount,
            long fileNumber,
            String kkk,
            String ll
            ) {
        Object[] result = populateEmptyStrings();
        result[SpecialClientNonUniqueFields.FILE_NAME_TRANSFER_DATE] = transferDate;
        result[SpecialClientNonUniqueFields.FILE_NAME_ONE_DIGIT_YEAR] = oneDigitYear; 
        result[SpecialClientNonUniqueFields.FILE_NAME_PROVIDER_CODE] = data.getProviderCode();
        result[SpecialClientNonUniqueFields.FILE_NAME_PROVIDER_DEPARTMENT] = data.getProviderDepartment();
        result[SpecialClientNonUniqueFields.FILE_NAME_V] = data.getServiceKind();
        result[SpecialClientNonUniqueFields.FILE_NAME_NUMBER] = new Long(fileNumber);
        result[SpecialClientNonUniqueFields.FILE_NAME_KKK] = kkk;
        result[SpecialClientNonUniqueFields.FILE_NAME_LL] = ll;
        result[SpecialClientFields.FILE_NUMBER] = new Long(fileNumber);
        result[SpecialClientFields.KKK] = kkk;
        result[SpecialClientFields.LL] = ll;
        populateCommonParameters(result, payment, data, transferDate, oneDigitYear, payerAccount);
        return result;
    }
    
    /**
     * Метод заполняет массив пар-ров нужными значениями для 
     * имен файлов
     * @param payment - класс, содержащий запись из таблицы PAY.PAYMENTS
     * @param data - класс, содержащий запись из таблицы RG.DOWNLOADS_PROPS 
     * @param transferDate - дата формирования платежного поручения
     * @param oneDigitYear - первая цифра даты формирования платежного поручения
     * @param fileName - имя файла
     * @return созданный и заполненный массив параметров
     */
    public static Object[] makeHeaderParameters(
            Maket1Data payment,
            DownloadsPropertiesData data,
            Date transferDate,
            Integer oneDigitYear,
            String payerAccount,
            String fileName) {
        Object[] result = populateEmptyStrings();
        result[SpecialClientNonUniqueFields.HEADER_TRANSFER_DATE] = transferDate;
        result[SpecialClientNonUniqueFields.HEADER_PROVIDER_CODE] = data.getProviderCode();
        result[SpecialClientNonUniqueFields.HEADER_PROVIDER_DEPARTMENT] = data.getProviderDepartment();
        result[SpecialClientNonUniqueFields.HEADER_V] = data.getServiceKind();
        result[SpecialClientNonUniqueFields.HEADER_FILE_NAME] = fileName;
        result[SpecialClientFields.FILE_NAME] = fileName;
        populateCommonParameters(result, payment, data, transferDate, oneDigitYear, payerAccount);
        return result;
    }

    /**
     * Метод заполняет массив пар-ров нужными значениями для платежа 
     * @param payment - класс, содержащий запись из таблицы PAY.PAYMENTS
     * @param data - класс, содержащий запись из таблицы RG.DOWNLOADS_PROPS 
     * @param transferDate - дата формирования платежного поручения
     * @param oneDigitYear - первая цифра даты формирования платежного поручения
     * @param sum - исходная сумма платежа
     * @param clearSum - сумма после удержания комиссии
     * @param recipientCommission - комиссия с получателя
     * @return созданный и заполненный массив параметров
     */
    public static Object[] makePaymentLineParameters(
            Maket1Data payment, 
            DownloadsPropertiesData data,
            Date transferDate,
            Integer oneDigitYear,
            String payerAccount,
            long sum,
            long clearSum,
            long recipientCommission) {
        Object[] result = populateEmptyStrings();
        result[SpecialClientNonUniqueFields.PAY_PAYMENT_DATE] = payment.getPaymentDate();
        result[SpecialClientNonUniqueFields.PAY_ELECTRONIC_PAYMENT_FLAG] = payment.getElectronicPaymentFlag();
        result[SpecialClientNonUniqueFields.PAY_OSB] = payment.getOsb();
        result[SpecialClientNonUniqueFields.PAY_FILIAL] = payment.getFilial();
        result[SpecialClientNonUniqueFields.PAY_CASHIER] = payment.getCashier();
        result[SpecialClientNonUniqueFields.PAY_DOCUMENT_NUMBER] = payment.getDocumentNumber();
        result[SpecialClientNonUniqueFields.PAY_SUM_ONE] = new Long(sum);
        result[SpecialClientNonUniqueFields.PAY_SUM_TWO] = new Long(sum);
        result[SpecialClientNonUniqueFields.PAY_SUM_THREE] = new Long(0L);
        result[SpecialClientNonUniqueFields.PAY_SUM_FOUR] = new Long(clearSum);
        result[SpecialClientNonUniqueFields.PAY_PAYMENT_TYPE] = new Integer(payment.getPaymentType());
        result[SpecialClientNonUniqueFields.PAY_SPECIAL_CLIENT_CODE] = payment.getSpecialClientCode();
        result[SpecialClientNonUniqueFields.PAY_V] = payment.getServiceKind();
        result[SpecialClientNonUniqueFields.PAY_ADD_REQ_LENGTH] = calcAddReqLength(payment);
        result[SpecialClientNonUniqueFields.PAY_ADDITIONAL_REQUISITES] = payment.getAdditionalRequisites();
        populateSums(sum, clearSum, recipientCommission, result);
        populateCommonParameters(result, payment, data, transferDate, oneDigitYear, payerAccount);       
        return result;
    }
    
    /**
     * Метод заполняет массив пар-ров нужными значениями для отделений 
     * @param payment - класс, содержащий запись из таблицы PAY.PAYMENTS
     * @param data - класс, содержащий запись из таблицы RG.DOWNLOADS_PROPS 
     * @param transferDate - дата формирования платежного поручения
     * @param oneDigitYear - первая цифра даты формирования платежного поручения
     * @param numberOfPayments - кол-во платежей в ОСБ
     * @param sum - исходная сумма платежа
     * @param clearSum - сумма после удержания комиссии
     * @param recipientCommission - комиссия с получателя
     * @return созданный и заполненный массив параметров
     */
    public static Object[] makeOSBLineParameters(
            Maket1Data payment, 
            DownloadsPropertiesData data,
            Date transferDate,
            Integer oneDigitYear,
            String payerAccount,
            long numberOfPayments,
            long sum,
            long clearSum,
            long recipientCommission) {
        Object[] result = populateEmptyStrings();
        result[SpecialClientNonUniqueFields.OSB_TRANSFER_DATE] = transferDate;
        result[SpecialClientNonUniqueFields.OSB_OSB] = payment.getOsb();
        result[SpecialClientNonUniqueFields.OSB_NUMBER_OF_PAYMENTS] = new Long(numberOfPayments);
        result[SpecialClientNonUniqueFields.OSB_SUM_ONE] = new Long(sum);
        result[SpecialClientNonUniqueFields.OSB_SUM_TWO] = new Long(sum);
        result[SpecialClientNonUniqueFields.OSB_SUM_THREE] = new Long(0L);
        result[SpecialClientNonUniqueFields.OSB_SUM_FOUR] = new Long(clearSum);
        result[SpecialClientNonUniqueFields.OSB_SPECIAL_CLIENT_CODE] = payment.getSpecialClientCode();
        result[SpecialClientNonUniqueFields.OSB_V] = payment.getServiceKind();
        result[SpecialClientNonUniqueFields.OSB_BIK] = makeEmptyFromNull(payment.getBik());
        result[SpecialClientNonUniqueFields.OSB_CORR_ACC] = makeEmptyFromNull(payment.getCorrAcc());
        result[SpecialClientNonUniqueFields.OSB_SETTLE_ACC] = makeEmptyFromNull(payment.getSettleAcc());
        result[SpecialClientFields.NUMBER_OF_PAYMENTS] = new Long(numberOfPayments);
        populateSums(sum, clearSum, recipientCommission, result);
        populateCommonParameters(result, payment, data, transferDate, oneDigitYear, payerAccount); 
        return result;
    }
    
    /**
     * Метод заполняет массив пар-ров нужными значениями для строки платежного поручения 
     * @param payment - класс, содержащий запись из таблицы PAY.PAYMENTS
     * @param data - класс, содержащий запись из таблицы RG.DOWNLOADS_PROPS 
     * @param transferDate - дата формирования платежного поручения
     * @param oneDigitYear - первая цифра даты формирования платежного поручения
     * @param numberOfPayments - кол-во платежей в платежном поручении
     * @param sum - исходная сумма платежа
     * @param clearSum - сумма после удержания комиссии
     * @param recipientCommission - комиссия с получателя
     * @return созданный и заполненный массив параметров
     */
    public static Object[] makePaymentOrderLineParameters(
            Maket1Data payment,
            DownloadsPropertiesData data,
            Date transferDate,
            Integer oneDigitYear,
            String payerAccount,
            long numberOfPayments,
            long sum,
            long clearSum,
            long recipientCommission) {
        Object[] result = populateEmptyStrings();
        result[SpecialClientNonUniqueFields.ORDER_TRANSFER_DATE] = transferDate;
        result[SpecialClientNonUniqueFields.ORDER_NUMBER_OF_PAYMENTS] = new Long(numberOfPayments);
        result[SpecialClientNonUniqueFields.ORDER_SUM_ONE] = new Long(sum);
        result[SpecialClientNonUniqueFields.ORDER_SUM_TWO] = new Long(sum);
        result[SpecialClientNonUniqueFields.ORDER_SUM_THREE] = new Long(0L);
        result[SpecialClientNonUniqueFields.ORDER_SUM_FOUR] = new Long(clearSum);
        result[SpecialClientNonUniqueFields.ORDER_BIK] = makeEmptyFromNull(payment.getBik());
        result[SpecialClientNonUniqueFields.ORDER_CORR_ACC] = makeEmptyFromNull(payment.getCorrAcc());
        result[SpecialClientNonUniqueFields.ORDER_SETTLE_ACC] = makeEmptyFromNull(payment.getSettleAcc());
        result[SpecialClientNonUniqueFields.ORDER_INN] = makeEmptyFromNull(payment.getInn());
        result[SpecialClientNonUniqueFields.ORDER_PERSONAL_ACCOUNT] = makeEmptyFromNull(payment.getPersonalAccount());
        result[SpecialClientNonUniqueFields.ORDER_KPP] = makeEmptyFromNull(payment.getKpp());
        result[SpecialClientNonUniqueFields.ORDER_OKATO] = makeEmptyFromNull(payment.getOkato());
        result[SpecialClientNonUniqueFields.ORDER_KBK] = makeEmptyFromNull(payment.getKbk());
        result[SpecialClientNonUniqueFields.ORDER_NUMBER] = payment.getPaymentOrder();
        result[SpecialClientNonUniqueFields.ORDER_SOURCE] = makeEmptyFromNull(payment.getPaymentOrderSource());
        result[SpecialClientFields.NUMBER_OF_PAYMENTS] = new Long(numberOfPayments);
        populateSums(sum, clearSum, recipientCommission, result);
        populateCommonParameters(result, payment, data, transferDate, oneDigitYear, payerAccount);  
        return result;
    }
    
    /**
     * Метод заполняет массив пар-ров нужными значениями для 
     * имен файлов
     * @param payment - класс, содержащий запись из таблицы PAY.PAYMENTS
     * @param data - класс, содержащий запись из таблицы RG.DOWNLOADS_PROPS 
     * @param transferDate - дата формирования платежного поручения
     * @param oneDigitYear - первая цифра даты формирования платежного поручения
     * @param numberOfPayments - кол-во платежей в файле
     * @param sum - исходная сумма платежа
     * @param clearSum - сумма после удержания комиссии
     * @param recipientCommission - комиссия с получателя
     * @return созданный и заполненный массив параметров
     */
    public static Object[] makeFooterParameters(
            Maket1Data payment,
            DownloadsPropertiesData data,
            Date transferDate,
            Integer oneDigitYear,
            String payerAccount,
            long numberOfPayments,
            long sum,
            long clearSum,
            long recipientCommission) {
        Object[] result = populateEmptyStrings();
        result[SpecialClientNonUniqueFields.FILE_TRANSFER_DATE] = transferDate;
        result[SpecialClientNonUniqueFields.FILE_NUMBER_OF_PAYMENTS] = new Long(numberOfPayments);
        result[SpecialClientNonUniqueFields.FILE_SUM_ONE] = new Long(sum);
        result[SpecialClientNonUniqueFields.FILE_SUM_TWO] = new Long(sum);
        result[SpecialClientNonUniqueFields.FILE_SUM_THREE] = new Long(0L);
        result[SpecialClientNonUniqueFields.FILE_SUM_FOUR] = new Long(clearSum);
        result[SpecialClientNonUniqueFields.FILE_BIK] = makeEmptyFromNull(payment.getBik());
        result[SpecialClientNonUniqueFields.FILE_CORR_ACC] = makeEmptyFromNull(payment.getCorrAcc());
        result[SpecialClientNonUniqueFields.FILE_SETTLE_ACC] = makeEmptyFromNull(payment.getSettleAcc());
        result[SpecialClientNonUniqueFields.FILE_INN] = makeEmptyFromNull(payment.getInn());
        result[SpecialClientNonUniqueFields.FILE_PERSONAL_ACCOUNT] = makeEmptyFromNull(payment.getPersonalAccount());
        result[SpecialClientNonUniqueFields.FILE_KPP] = makeEmptyFromNull(payment.getKpp());
        result[SpecialClientNonUniqueFields.FILE_OKATO] = makeEmptyFromNull(payment.getOkato());
        result[SpecialClientNonUniqueFields.FILE_KBK] = makeEmptyFromNull(payment.getKbk());
        result[SpecialClientNonUniqueFields.FILE_ORDER_NUMBER] = payment.getPaymentOrder();
        result[SpecialClientNonUniqueFields.FILE_ORDER_SOURCE] = makeEmptyFromNull(payment.getPaymentOrderSource());
        result[SpecialClientFields.NUMBER_OF_PAYMENTS] = new Long(numberOfPayments);
        populateSums(sum, clearSum, recipientCommission, result);
        populateCommonParameters(result, payment, data, transferDate, oneDigitYear, payerAccount);  
        return result;
    }

    private static void populateSums(long sum, long clearSum, long recipientCommission, Object[] result) {
        result[SpecialClientFields.SUM] = new Long(sum);
        result[SpecialClientFields.SUM_CLEAR] = new Long(clearSum);
        result[SpecialClientFields.SUM_ROUBLES] = sumConverter.toBigDecimal(sum);
        result[SpecialClientFields.SUM_CLEAR_ROUBLES] = sumConverter.toBigDecimal(clearSum);
        result[SpecialClientFields.SUM_RECIPIENT_COMMISSION] = new Long(recipientCommission);
        result[SpecialClientFields.SUM_RECIPIENT_COMMISSION_ROUBLES] = sumConverter.toBigDecimal(recipientCommission);
    }

    /**
     * Метод заполняет массив пар-ров нужными значениями для общих для всех методов параметров 
     * @param result - массив из заполненных параметров
     * @param payment - класс, содержащий запись из таблицы PAY.PAYMENTS
     * @param data - класс, содержащий запись из таблицы RG.DOWNLOADS_PROPS 
     * @param transferDate - дата формирования платежного поручения
     * @param oneDigitYear - первая цифра даты формирования платежного поручения
     */
    private static void populateCommonParameters(
            Object[] result,
            Maket1Data payment,
            DownloadsPropertiesData data,
            Date transferDate,
            Integer oneDigitYear,
            String payerAccount
            ) {
        populateAdditionalRequisites(result, payment.getAdditionalRequisites());
        result[SpecialClientFields.TRANSFER_DATE] = transferDate;
        result[SpecialClientFields.ONE_DIGIT_YEAR] = oneDigitYear;
        result[SpecialClientFields.PROVIDER_CODE] = data.getProviderCode();
        result[SpecialClientFields.PROVIDER_DEPARTMENT] = data.getProviderDepartment();
        result[SpecialClientFields.V] = data.getServiceKind();
        result[SpecialClientFields.PAYMENT_DATE] = payment.getPaymentDate();
        result[SpecialClientFields.ELECTRONIC_PAYMENT_FLAG] = payment.getElectronicPaymentFlag();
        result[SpecialClientFields.OSB] = payment.getOsb();
        result[SpecialClientFields.FILIAL] = payment.getFilial();
        result[SpecialClientFields.CASHIER] = payment.getCashier();
        result[SpecialClientFields.DOCUMENT_NUMBER] = payment.getDocumentNumber();
        result[SpecialClientFields.PAYMENT_TYPE] = new Integer(payment.getPaymentType());
        result[SpecialClientFields.SPECIAL_CLIENT_CODE] = payment.getSpecialClientCode();
        result[SpecialClientFields.ADDITIONAL_REQUISITES] = payment.getAdditionalRequisites();
        result[SpecialClientFields.BIK] = makeEmptyFromNull(payment.getBik());
        result[SpecialClientFields.CORR_ACC] = makeEmptyFromNull(payment.getCorrAcc());
        result[SpecialClientFields.SETTLE_ACC] = makeEmptyFromNull(payment.getSettleAcc());
        result[SpecialClientFields.INN] = makeEmptyFromNull(payment.getInn());
        result[SpecialClientFields.PERSONAL_ACCOUNT] = makeEmptyFromNull(payment.getPersonalAccount());
        result[SpecialClientFields.KPP] = makeEmptyFromNull(payment.getKpp());
        result[SpecialClientFields.OKATO] = makeEmptyFromNull(payment.getOkato());
        result[SpecialClientFields.KBK] = makeEmptyFromNull(payment.getKbk());
        result[SpecialClientFields.ORDER_NUMBER] = payment.getPaymentOrder();
        result[SpecialClientFields.SOURCE] = makeEmptyFromNull(payment.getPaymentOrderSource());
        result[SpecialClientFields.PAYER_SETTLE_ACC] = payerAccount;
    }

    /**
     * Метод заполняет массив пар-ров нужными значениями для дополнительных реквизитов 
     * @param data - массив пар-ров для заполнения 
     * @param additionalRequisites - строка дополнительных реквизитов
     */
    private static void populateAdditionalRequisites(Object[] data, String additionalRequisites) {
        if (additionalRequisites == null || additionalRequisites.length() == 0) {
            return;
        }
        String fields[] = additionalRequisites.split(DEFAULT_SEPARATOR);
        for (int i = 0; i < Math.min(SpecialClientFields.ADD_REQ_MAX_NUMBER, fields.length); i++) {
            data[i + SpecialClientFields.ADD_REQ_START_INDEX] = fields[i];
        }
    }

    /**
     * Возвращает значение пустую строку, если строка null, иначе саму строку
     * @param str
     * @return
     */
    private static String makeEmptyFromNull(String str) {
        if (str == null) {
            return "";
        } else {
            return str;
        }
    }
    
    /**
     * Подсчет длины доп.реквизитов
     * @param payment - класс, содержащий запись из таблицы PAY.PAYMENTS
     * @return длина доп.реквизитов
     */
    private static Integer calcAddReqLength(Maket1Data payment) {
        String addReq = payment.getAdditionalRequisites();
        if (addReq == null) {
            addReq = "";
        }
        int lenAddReq = addReq.length() + ADD_REQ_ADD_LENGTH;
        return new Integer(lenAddReq);
    }

}