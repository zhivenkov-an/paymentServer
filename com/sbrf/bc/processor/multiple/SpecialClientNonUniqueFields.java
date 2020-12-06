/*
 * Created on 06.08.2009
 */
package com.sbrf.bc.processor.multiple;

/**
 * Интерфейс содержит номера полей для выгрузки спецклиентов
 * Номера могут пересекаться
 * @author Petrov-AM
 */
interface SpecialClientNonUniqueFields {
    /**
     * 0 - дата формирования платежного поручения
     */
    int ZIP_TRANSFER_DATE = 0;

    /**
     * 1 - год - одна цифра даты формирования платежного поручения 
     */
    int ZIP_ONE_DIGIT_YEAR = 1;

    /**
     * 2 - код поставщика услуг (1-я часть кода спецклиента)
     */
    int ZIP_PROVIDER_CODE = 2;

    /**
     * 3 - номер выгрузки файла за день
     */
    int ZIP_FILE_NUMBER = 3;

    /**
     * 0 - дата формирования платежного поручения
     */
    int FILE_NAME_TRANSFER_DATE = 0;

    /**
     * 1 - год - одна цифра даты формирования платежного поручения 
     */
    int FILE_NAME_ONE_DIGIT_YEAR = 1;

    /**
     * 2 - код поставщика услуг (1-я часть кода спецклиента)
     */
    int FILE_NAME_PROVIDER_CODE = 2;

    /**
     * 3 - подразделение поставщика услуг (2-я часть кода спецклиента)
     */
    int FILE_NAME_PROVIDER_DEPARTMENT = 3;

    /**
     * 4 - V - условный номер вида платежа (номер услуги)
     */
    int FILE_NAME_V = 4;

    /**
     * 5 - уникальный номер файла в архиве
     */
    int FILE_NAME_NUMBER = 5;

    /**
     * 6 - первые 3 символа кода спецклиента
     */
    int FILE_NAME_KKK = 6;

    /**
     * 7 - последние символы кода спецклиента, начиная с 4-го
     */
    int FILE_NAME_LL = 7;

    /**
     * 0 - дата (предполагаемая) формирования платежного поручения
     */
    int HEADER_TRANSFER_DATE = 0;

    /**
     * 1 - код поставщика услуг (1-я часть кода спецклиента)
     */
    int HEADER_PROVIDER_CODE = 1;

    /**
     * 2 - подразделение поставщика услуг (2-я часть кода спецклиента)
     */
    int HEADER_PROVIDER_DEPARTMENT = 2;

    /**
     * 3 - V - условный номер вида платежа (номер услуги)
     */
    int HEADER_V = 3;

    /**
     * 4 - имя файла
     */
    int HEADER_FILE_NAME = 4;

    /**
     * 0 - дата платежа
     */
    int PAY_PAYMENT_DATE = 0;

    /**
     * 1 - признак электронного платежа
     */
    int PAY_ELECTRONIC_PAYMENT_FLAG = 1;

    /**
     * 2 - ОСБ
     */
    int PAY_OSB = 2;

    /**
     * 3 - филиал
     */
    int PAY_FILIAL = 3;

    /**
     * 4 - кассир
     */
    int PAY_CASHIER = 4;

    /**
     * 5 - номер документа
     */
    int PAY_DOCUMENT_NUMBER = 5;

    /**
     * 6 - сумма плюс пени
     */
    int PAY_SUM_ONE = 6;

    /**
     * 7 - сумма
     */
    int PAY_SUM_TWO = 7;

    /**
     * 8 - пени
     */
    int PAY_SUM_THREE = 8;

    /**
     * 9 - чистая сумма
     */
    int PAY_SUM_FOUR = 9;

    /**
     * 10 - тип платежа
     */
    int PAY_PAYMENT_TYPE = 10;

    /**
     * 11 - код спецклиента
     */
    int PAY_SPECIAL_CLIENT_CODE = 11;

    /**
     * 12 - условный номер вида платежа (номер услуги)
     */
    int PAY_V = 12;

    /**
     * 13 - длина доп.реквизитов
     */
    int PAY_ADD_REQ_LENGTH = 13;

    /**
     * 14 - дополнительные реквизиты
     */
    int PAY_ADDITIONAL_REQUISITES = 14;

    /**
     * 0 - дата формирования платежного поручения
     */
    int OSB_TRANSFER_DATE = 0;

    /**
     * 1 - ОСБ
     */
    int OSB_OSB = 1;

    /**
     * 2 - количество платежей ОСБ
     */
    int OSB_NUMBER_OF_PAYMENTS = 2;

    /**
     * 3 - сумма плюс пени
     */
    int OSB_SUM_ONE = 3;

    /**
     * 4 - сумма
     */
    int OSB_SUM_TWO = 4;

    /**
     * 5 - пени
     */
    int OSB_SUM_THREE = 5;

    /**
     * 6 - чистая сумма
     */
    int OSB_SUM_FOUR = 6;

    /**
     * 7 - код спецклиента
     */
    int OSB_SPECIAL_CLIENT_CODE = 7;

    /**
     * 8 - условный номер вида платежа (номер услуги)
     */
    int OSB_V = 8;

    /**
     * 9 - БИК
     */
    int OSB_BIK = 9;

    /**
     * 10 - корсчет
     */
    int OSB_CORR_ACC = 10;

    /**
     * 11 - расчетный счет
     */
    int OSB_SETTLE_ACC = 11;

    /**
     * 0 - дата формирования платежного поручения
     */
    int ORDER_TRANSFER_DATE = 0;

    /**
     * 1 - количество платежей в платежном поручении
     */
    int ORDER_NUMBER_OF_PAYMENTS = 1;

    /**
     * 2 - сумма плюс пени
     */
    int ORDER_SUM_ONE = 2;

    /**
     * 3 - сумма
     */
    int ORDER_SUM_TWO = 3;

    /**
     * 4 - пени
     */
    int ORDER_SUM_THREE = 4;

    /**
     * 5 - чистая сумма
     */
    int ORDER_SUM_FOUR = 5;

    /**
     * 6 - БИК
     */
    int ORDER_BIK = 6;

    /**
     * 7 - корсчет
     */
    int ORDER_CORR_ACC = 7;

    /**
     * 8 - расчетный счет
     */
    int ORDER_SETTLE_ACC = 8;

    /**
     * 9 - ИНН
     */
    int ORDER_INN = 9;

    /**
     * 10 - лицевой счет
     */
    int ORDER_PERSONAL_ACCOUNT = 10;

    /**
     * 11 - КПП
     */
    int ORDER_KPP = 11;

    /**
     * 12 - ОКАТО
     */
    int ORDER_OKATO = 12;

    /**
     * 13 - КБК
     */
    int ORDER_KBK = 13;

    /**
     * 14 - номер платежного поручения
     */
    int ORDER_NUMBER = 14;

    /**
     * 15 - источник формирования платежного поручения
     */
    int ORDER_SOURCE = 15;

    /**
     * 0 - дата формирования платежного поручения
     */
    int FILE_TRANSFER_DATE = 0;

    /**
     * 1 - количество платежей в файле
     */
    int FILE_NUMBER_OF_PAYMENTS = 1;

    /**
     * 2 - сумма плюс пени
     */
    int FILE_SUM_ONE = 2;

    /**
     * 3 - сумма
     */
    int FILE_SUM_TWO = 3;

    /**
     * 4 - пени
     */
    int FILE_SUM_THREE = 4;

    /**
     * 5 - чистая сумма
     */
    int FILE_SUM_FOUR = 5;

    /**
     * 6 - БИК
     */
    int FILE_BIK = 6;

    /**
     * 7 - корсчет
     */
    int FILE_CORR_ACC = 7;

    /**
     * 8 - расчетный счет
     */
    int FILE_SETTLE_ACC = 8;

    /**
     * 9 - ИНН
     */
    int FILE_INN = 9;

    /**
     * 10 - лицевой счет
     */
    int FILE_PERSONAL_ACCOUNT = 10;

    /**
     * 11 - КПП
     */
    int FILE_KPP = 11;

    /**
     * 12 - ОКАТО
     */
    int FILE_OKATO = 12;

    /**
     * 13 - КБК
     */
    int FILE_KBK = 13;

    /**
     * 14 - номер платежного поручения
     */
    int FILE_ORDER_NUMBER = 14;

    /**
     * 15 - источник формирования платежного поручения
     */
    int FILE_ORDER_SOURCE = 15;

}