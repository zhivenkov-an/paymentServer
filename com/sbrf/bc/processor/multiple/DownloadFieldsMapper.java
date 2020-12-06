package com.sbrf.bc.processor.multiple;

import java.util.Map;
import java.util.TreeMap;

import billing.util.SumConverter;

import com.sbrf.data.Maket1Data;

public class DownloadFieldsMapper {

    private static final String DEFAULT_SPLITTER = "@";
    
    /* Параметры, необходимые для формирования имен файлов*/
    /**
     * Номер выгрузки архива за день или уникальный номер файла в архиве. Число
     */
    public static final String FILE_NUMBER = "fileNumber";
    /**
     * Год - одна цифра даты формирования платежного поручения. Число
     */
    public static final String ONE_DIGIT_YEAR = "oneDigitYear";
    /**
     * Код поставщика услуг (1-я часть кода спецклиента). Строка
     */
    public static final String PROVIDER_CODE = "providerCode";
    /**
     * Код подразделения поставщика услуг (2-я часть кода спецклиента). Строка
     */
    public static final String PROVIDER_DEPARTMENT = "providerDepartment";
    /**
     * Первые 3 символа кода спецклиента. Строка
     */
    public static final String FIRST_3_CODE_SYMBOLS = "first3CodeSymbols";
    /**
     * Последние 2 символа кода спецклиентаб начиная с 4го. Строка
     */
    public static final String LAST_2_CODE_SYMBOLS = "last2CodeSymbols";
    
    /**
     * Имя файла.
     */
    public static final String FILE_NAME = "fileName";
    /**
     * Количество платежей
     */
    public static final String NUMBER_OF_PAYMENTS = "numberOfPayments";
    
    /**
     * БИК банка получателя.
     */
    public static final String BIK_ALIAS = "bik";
    /**
     * Корреспондентский счет банка получателя.
     */
    public static final String CORR_ACCOUNT_ALIAS = "corrAccount";
    /**
     * Статус составителя расчетного документа.
     */
    public static final String CREATOR_STATUS_ALIAS = "creatorStatus";
    /**
     * ИНН получателя.
     */
    public static final String INN_ALIAS = "inn";
    /**
     * Код бюджетной классификации.
     */
    public static final String KBK_ALIAS = "kbk";
    /**
     * КПП получателя платежа.
     */
    public static final String KPP_ALIAS = "kpp";
    /**
     * Код по ОКАТО.
     */
    public static final String OKATO_ALIAS = "okato";
    /**
     * Вид операции.
     */
    public static final String OPER_TYPE_ALIAS = "operationType";
    /**
     * Порядковый номер платежного поручения.
     */
    public static final String ORDER_NUM_ALIAS = "orderNum";
    /**
     * Источник формирования платежного поручения.
     */
    public static final String ORDER_SOURCE_ALIAS = "orderSource";
    /**
     * Номер рейса.
     */
    public static final String PASSAGE_NUM_ALIAS = "passageNum";
    /**
     * Адрес плательщика.
     */
    public static final String PAYER_ADDRESS_ALIAS = "payerAddress";
    /**
     * БИК банка плательщика.
     */
    public static final String PAYER_BIK_ALIAS = "payerBik";
    /**
     * Корреспондентский счет банка плательщика.
     */
    public static final String PAYER_CORR_ALIAS = "payerCorrAccount";
    /**
     * ИНН плательщика из ПП.
     */
    public static final String PAYER_INN_ALIAS = "payerInn";
    /**
     * ИНН плательщика из платежа.
     */
    public static final String PAYER_INN_PAYMENTS_ALIAS = "payerInnPayments";

    /**
     * КПП плательщика.
     */
    public static final String PAYER_KPP_ALIAS = "payerKpp";
    /**
     * Наименование плательщика.
     */
    public static final String PAYER_NAME_ALIAS = "payerName";
    /**
     * Расчетный счет плательщика.
     */
    public static final String PAYER_SETTLE_ALIAS = "payerSettleAccount";
    /**
     * Номер лицевого счета получателя.
     */
    public static final String PERSONAL_ACCOUNT_ALIAS = "personalAccount";
    /**
     * Расчетный счет получателя.
     */
    public static final String SETTLE_ACCOUNT_ALIAS = "settleAccount";
    /**
     * Дата составления налогового документа.
     */
    public static final String TAXES_DOC_DATE_ALIAS = "taxesDocumentDate";
    /**
     * Номер налогового документа.
     */
    public static final String TAXES_DOC_NUMBER_ALIAS = "taxesDocumentNumber";
    /**
     * Тип налогового документа.
     */
    public static final String TAXES_DOC_TYPE_ALIAS = "taxesDocumentType";
    /**
     * Налоговый период.
     */
    public static final String TAXES_PERIOD_ALIAS = "taxesPeriod";
    /**
     * Основание налогового платежа.
     */
    public static final String TAXES_REASON_ALIAS = "taxesReason";

    /**
     * Наименование получателя, из платежа. (из единственного в случае
     * отдельного платежного поручения или первого попавшегося в случае
     * сводного).
     */
    public static final String RECEIVER_NAME_ALIAS = "receiverName";
    /**
     * Дата приема платежа.
     */
    public static final String PAYMENT_DATE_ALIAS = "paymentDate";
    /**
     * Дата перечисления средств. Равна дате операционного дня.
     */
    public static final String TRANSFER_DATE_ALIAS = "transferDate";
    /**
     * Число платежей в платежном поручении. Для отдельного ПП всегда 1.
     */
    public static final String PAYMENT_COUNT_ALIAS = "paymentCount";
    /**
     * Код спецклиента.
     */
    public static final String SPECIAL_CLIENT_CODE_ALIAS = "specialClientCode";
    /**
     * Сумма платежного поручения = сумма всех платежей, входящих в платежное
     * поручение, минус комисcия с получателя.
     */
    public static final String PAYMENT_ORDER_SUM_ALIAS = "paymentOrderSum";// вместо
                                                                           // sum
    /**
     * Исходная сумма принятых платежей = сумма всех платежей, входящих в
     * платежное поручение.
     */
    public static final String PAYMENTS_SUM_ALIAS = "paymentsSum";
    /**
     * Сумма комиссии, удержанной с получателя = сумма комиссии всех платежей,
     * вошедших в платежное поручение.
     */
    public static final String RECIPIENT_COMMISSION_ALIAS = "recipientCommission";
    /**
     * Идентификатор платежа.
     */
    public static final String LINUM_ALIAS = "linum";
    /**
     * Номер отделения.
     */
    public static final String OSB_ALIAS = "osb";
    /**
     * Номер филиала.
     */
    public static final String FILIAL_ALIAS = "filial";
    /**
     * Номер кассира.
     */
    public static final String CASHIER_ALIAS = "cashier";
    /**
     * Номер платежа.
     */
    public static final String DOCUMENT_NUMBER_ALIAS = "documentNumber";
    /**
     * Тип платежа.
     */
    public static final String PAYMENT_TYPE_ALIAS = "paymentType";
    /**
     * Сумма комиссий за дополнительные услуги.
     */
    public static final String ADD_SERVICE_COMMISSION_SUM_ALIAS = "addServiceCommision";
    /**
     * Сумма пени.
     */
    public static final String SUPEN_SUM_ALIAS = "supenSum";
    /**
     * Cумма комиссии с плательщика.
     */
    public static final String PAYER_COMMISION_ALIAS = "payerCommisionSum";
    /**
     * Дополнительные реквизиты (символы '@' заменены на пробелы).
     */
    public static final String ADDITIONAL_REQUISITES_ALIAS = "addReq";
    
    /**
     * Длина дополнительных реквизитов (без ведущих и заерщающих пробелов).
     */
    public static final String ADDITIONAL_REQUISITES_LENGTH_ALIAS = "addReqLength";
    /**
     * Первый дополнительный реквизит.
     */
    public static final String ADD_REQ_1_ALIAS = ADDITIONAL_REQUISITES_ALIAS + "1";
    /**
     * Второй дополнительный реквизит.
     */
    public static final String ADD_REQ_2_ALIAS = ADDITIONAL_REQUISITES_ALIAS + "2";
    /**
     * Третий дополнительный реквизит.
     */
    public static final String ADDITIONAL_REQUISITES_ALIAS_3_ALIAS = ADDITIONAL_REQUISITES_ALIAS + "3";
    /**
     * Четвертый дополнительный реквизит.
     */
    public static final String ADDITIONAL_REQUISITES_ALIAS_4_ALIAS = ADDITIONAL_REQUISITES_ALIAS + "4";
    /**
     * Пятый дополнительный реквизит.
     */
    public static final String ADDITIONAL_REQUISITES_ALIAS_5_ALIAS = ADDITIONAL_REQUISITES_ALIAS + "5";
    /**
     * Шестой дополнительный реквизит.
     */
    public static final String ADDITIONAL_REQUISITES_ALIAS_6_ALIAS = ADDITIONAL_REQUISITES_ALIAS + "6";
    /**
     * Седьмой дополнительный реквизит.
     */
    public static final String ADDITIONAL_REQUISITES_ALIAS_7_ALIAS = ADDITIONAL_REQUISITES_ALIAS + "7";
    /**
     * Восьмой дополнительный реквизит.
     */
    public static final String ADDITIONAL_REQUISITES_ALIAS_8_ALIAS = ADDITIONAL_REQUISITES_ALIAS + "8";
    /**
     * Девятый дополнительный реквизит.
     */
    public static final String ADDITIONAL_REQUISITES_ALIAS_9_ALIAS = ADDITIONAL_REQUISITES_ALIAS + "9";
    /**
     * Десятый дополнительный реквизит.
     */
    public static final String ADDITIONAL_REQUISITES_ALIAS_10_ALIAS = ADDITIONAL_REQUISITES_ALIAS + "10";
    /**
     * Условный номер услуги.
     */
    public static final String SERVICE_KIND_ALIAS = "serviceKind";
    /**
     * Идентификатор реестра, из которого пришла инф-ция о платеже.
     */
    public static final String IN_REG_ID_ALIAS = "inRegId";
    /**
     * Номер строки реестра, из которого пришла инф-ция о платеже.
     */
    public static final String IN_REG_LINE_ALIAS = "inRegLine";
    /**
     * Признак электронного платежа.
     */
    public static final String ELECTRONIC_PAYMENT_FLAG_ALIAS = "electronicPaymentFlag";
    /**
     * Номер счета плательщика.
     */
    public static final String PAYER_ACC_ALIAS = "payerAcc";
    /**
     * Информация о плательщике.
     */
    public static final String PAYER_INFO_ALIAS = "payerInfo";
    
    /**
     * Информация о плательщике (второй алиас).
     */
    public static final String PAYER_PERSONAL_ACCOUNT_ALIAS = "payerPersonalAccount";
    /**
     * Дата и место рождения плательщика.
     */
    public static final String PAYER_BIRTHDAY_ALIAS = "payerBirthday";
    /**
     * Номер получателя (номер организации).
     */
    public static final String RECEIVER_NUMBER_ALIAS = "receiverNumber";
    /**
     * Именная информация о плательщике.
     */
    public static final String PERSONAL_INFO_ALIAS = "personalInfo";
    /**
     * Назначение платежа.
     */
    public static final String PAYMENT_DESTINATION_ALIAS = "paymentDestination";
    /**
     * Код операции.
     */
    public static final String OPER_ALIAS = "oper";
    /**
     * Проводка АС ФОСБ.
     */
    public static final String TEL_ALIAS = "tel";
    /**
     * Алгоритм формирования платежных поручений.
     */
    public static final String PAY_ORDER_ALGORITHM = "payOrderAlgorithm";
    /**
     * Код тарифа.
     */
    public static final String RATE_CODE_ALIAS = "rateCode";
    /**
     * Сумма платежа. Long
     */
    public static final String SUM_ALIAS = "sum";
    /**
     * Сумма платежа в рублях. BigDecimal
     */
    public static final String SUM_ROUBLES_ALIAS = "sumRoubles";
    /**
     * Пеня платежа. Long
     */
    public static final String SUPEN_ALIAS = "supen";
    /**
     * Пеня платежа в рублях. BigDecimal
     */
    public static final String SUPEN_ROUBLES_ALIAS = "supenRoubles";
    /**
     * Сумма комиссии с получателя. Long
     */
    public static final String SERVICE_COMM_ALIAS = "serviceComm";
    /**
     * Сумма комиссии с получателя  в рублях. BigDecimal.
     */
    public static final String SERVICE_COMM_ROUBLES_ALIAS = "serviceCommRoubles";
    /**
     * Сумма комиссии с плательщика. Long
     */
    public static final String PAYMENT_PAYER_COMM_ALIAS = "payerComm";
    /**
     * Сумма комиссии с плательщика  в рублях. BigDecimal.
     */
    public static final String PAYMENT_PAYER_COMM_ROUBLES_ALIAS = "payerCommRoubles";
    /**
     * Сумма помле удержания комиссии. Long
     */
    public static final String SUM_CLEAR_ALIAS = "sumClear";
    /**
     * Сумма помле удержания комиссии в рублях. BigDecimal
     */
    public static final String SUM_CLEAR_ROUBLES_ALIAS = "sumClearRoubles";
    /**
     * Маршрут
     */
    public static final String ROUTE_ALIAS = "route";
    
    
    
    protected DownloadFieldsMapper() {
    }

    private interface Element {
        public Object getValue(Maket1Data paymentData);
    }

    private static Map fieldsForDownload;

    static {
        fieldsForDownload = new TreeMap();
        fieldsForDownload.put(BIK_ALIAS, new Element() {
            public Object getValue(Maket1Data paymentData) {
                return paymentData.getBik();
            }
        });
        fieldsForDownload.put(CORR_ACCOUNT_ALIAS, new Element() {
            public Object getValue(Maket1Data paymentData) {
                return paymentData.getCorrAcc();
            }
        });
        fieldsForDownload.put(INN_ALIAS, new Element() {
            public Object getValue(Maket1Data paymentData) {
                return paymentData.getInn();
            }
        });
        fieldsForDownload.put(KBK_ALIAS, new Element() {
            public Object getValue(Maket1Data paymentData) {
                return paymentData.getKbk();
            }
        });
        fieldsForDownload.put(KPP_ALIAS, new Element() {
            public Object getValue(Maket1Data paymentData) {
                return paymentData.getKpp();
            }
        });
        fieldsForDownload.put(OKATO_ALIAS, new Element() {
            public Object getValue(Maket1Data paymentData) {
                return paymentData.getOkato();
            }
        });
        fieldsForDownload.put(DOCUMENT_NUMBER_ALIAS, new Element() {
            public Object getValue(Maket1Data paymentData) {
                return paymentData.getDocumentNumber();
            }
        });
        fieldsForDownload.put(PAYMENT_TYPE_ALIAS, new Element() {
            public Object getValue(Maket1Data paymentData) {
                return new Integer(paymentData.getPaymentType());
            }
        });
        fieldsForDownload.put(CASHIER_ALIAS, new Element() {
            public Object getValue(Maket1Data paymentData) {
                return paymentData.getCashier();
            }
        });
        fieldsForDownload.put(ELECTRONIC_PAYMENT_FLAG_ALIAS, new Element() {
            public Object getValue(Maket1Data paymentData) {
                return paymentData.getElectronicPaymentFlag();
            }
        });
        fieldsForDownload.put(ADD_SERVICE_COMMISSION_SUM_ALIAS, new Element() {
            public Object getValue(Maket1Data paymentData) {
                return new Long(paymentData.getAddServiceCommission());
            }
        });
        fieldsForDownload.put(CORR_ACCOUNT_ALIAS, new Element() {
            public Object getValue(Maket1Data paymentData) {
                return paymentData.getCorrAcc();
            }
        });
        fieldsForDownload.put(FILIAL_ALIAS, new Element() {
            public Object getValue(Maket1Data paymentData) {
                return paymentData.getFilial();
            }
        });
        fieldsForDownload.put(LINUM_ALIAS, new Element() {
            public Object getValue(Maket1Data paymentData) {
                return new Long(paymentData.getLinum());
            }
        });
        fieldsForDownload.put(OSB_ALIAS, new Element() {
            public Object getValue(Maket1Data paymentData) {
                return paymentData.getOsb();
            }
        });
        fieldsForDownload.put(PAYMENT_PAYER_COMM_ALIAS, new Element() {
            public Object getValue(Maket1Data paymentData) {
                return new Long(paymentData.getPayerCommission());
            }
        });
        fieldsForDownload.put(PAYMENT_DATE_ALIAS, new Element() {
            public Object getValue(Maket1Data paymentData) {
                return paymentData.getPaymentDate();
            }
        });
        fieldsForDownload.put(ORDER_NUM_ALIAS, new Element() {
            public Object getValue(Maket1Data paymentData) {
                return paymentData.getPaymentOrder();
            }
        });
        fieldsForDownload.put(ORDER_SOURCE_ALIAS, new Element() {
            public Object getValue(Maket1Data paymentData) {
                return paymentData.getPaymentOrderSource();
            }
        });
        fieldsForDownload.put(PERSONAL_ACCOUNT_ALIAS, new Element() {
            public Object getValue(Maket1Data paymentData) {
                return paymentData.getPersonalAccount();
            }
        });
        fieldsForDownload.put(PERSONAL_INFO_ALIAS, new Element() {
            public Object getValue(Maket1Data paymentData) {
                return paymentData.getPersonalInfo();
            }
        });
        fieldsForDownload.put(RECIPIENT_COMMISSION_ALIAS, new Element() {
            public Object getValue(Maket1Data paymentData) {
                return new Long(paymentData.getServiceCommission());
            }
        });
        fieldsForDownload.put(SETTLE_ACCOUNT_ALIAS, new Element() {
            public Object getValue(Maket1Data paymentData) {
                return new Long(paymentData.getSettleAcc());
            }
        });
        fieldsForDownload.put(SPECIAL_CLIENT_CODE_ALIAS, new Element() {
            public Object getValue(Maket1Data paymentData) {
                return paymentData.getSpecialClientCode();
            }
        });
        fieldsForDownload.put(SUM_ALIAS, new Element() {
            public Object getValue(Maket1Data paymentData) {
                return new Long(paymentData.getSum());
            }
        });
        fieldsForDownload.put(SUPEN_ALIAS, new Element() {
            public Object getValue(Maket1Data paymentData) {
                return new Long(paymentData.getSupen());
            }
        });
        fieldsForDownload.put(SERVICE_COMM_ALIAS, new Element() {
            public Object getValue(Maket1Data paymentData) {
                return new Long(paymentData.getServiceCommission());
            }
        });
        fieldsForDownload.put(TRANSFER_DATE_ALIAS, new Element() {
            public Object getValue(Maket1Data paymentData) {
                return paymentData.getTransferDate();
            }
        });
        fieldsForDownload.put(ADDITIONAL_REQUISITES_ALIAS, new Element() {
            public Object getValue(Maket1Data paymentData) {
                return paymentData.getAdditionalRequisites().replaceAll("@", " ");
            }
        });
        fieldsForDownload.put(ADD_REQ_1_ALIAS, new Element() {
            public Object getValue(Maket1Data paymentData) {
                Object result = getAddRequisiteByNumber(paymentData, 1);
                return result;
            }
        });
        fieldsForDownload.put(ADD_REQ_2_ALIAS, new Element() {
            public Object getValue(Maket1Data paymentData) {
                Object result = getAddRequisiteByNumber(paymentData, 2);
                return result;
            }
        });
        fieldsForDownload.put(ADDITIONAL_REQUISITES_ALIAS_3_ALIAS, new Element() {
            public Object getValue(Maket1Data paymentData) {
                Object result = getAddRequisiteByNumber(paymentData, 3);
                return result;
            }
        });
        fieldsForDownload.put(ADDITIONAL_REQUISITES_ALIAS_4_ALIAS, new Element() {
            public Object getValue(Maket1Data paymentData) {
                Object result = getAddRequisiteByNumber(paymentData, 4);
                return result;
            }
        });
        fieldsForDownload.put(ADDITIONAL_REQUISITES_ALIAS_5_ALIAS, new Element() {
            public Object getValue(Maket1Data paymentData) {
                Object result = getAddRequisiteByNumber(paymentData, 5);
                return result;
            }
        });
        fieldsForDownload.put(ADDITIONAL_REQUISITES_ALIAS_6_ALIAS, new Element() {
            public Object getValue(Maket1Data paymentData) {
                Object result = getAddRequisiteByNumber(paymentData, 6);
                return result;
            }
        });
        fieldsForDownload.put(ADDITIONAL_REQUISITES_ALIAS_7_ALIAS, new Element() {
            public Object getValue(Maket1Data paymentData) {
                Object result = getAddRequisiteByNumber(paymentData, 7);
                return result;
            }
        });
        fieldsForDownload.put(ADDITIONAL_REQUISITES_ALIAS_8_ALIAS, new Element() {
            public Object getValue(Maket1Data paymentData) {
                Object result = getAddRequisiteByNumber(paymentData, 8);
                return result;
            }
        });
        fieldsForDownload.put(ADDITIONAL_REQUISITES_ALIAS_9_ALIAS, new Element() {
            public Object getValue(Maket1Data paymentData) {
                Object result = getAddRequisiteByNumber(paymentData, 9);
                return result;
            }
        });
        fieldsForDownload.put(ADDITIONAL_REQUISITES_ALIAS_10_ALIAS, new Element() {
            public Object getValue(Maket1Data paymentData) {
                Object result = getAddRequisiteByNumber(paymentData, 10);
                return result;
            }
        });
        fieldsForDownload.put(OPER_ALIAS, new Element() {
            public Object getValue(Maket1Data paymentData) {
                return paymentData.getOper();
            }
        });
        fieldsForDownload.put(PAYER_ADDRESS_ALIAS, new Element() {
            public Object getValue(Maket1Data paymentData) {
                return paymentData.getPayerAddress();
            }
        });
        fieldsForDownload.put(PAYER_ACC_ALIAS, new Element() {
            public Object getValue(Maket1Data paymentData) {
                return paymentData.getPayerAcc();
            }
        });
        fieldsForDownload.put(PAYER_INFO_ALIAS, new Element() {
            public Object getValue(Maket1Data paymentData) {
                return paymentData.getPayerInfo();
            }
        });
        fieldsForDownload.put(RATE_CODE_ALIAS, new Element() {
            public Object getValue(Maket1Data paymentData) {
                return paymentData.getRatesCode();
            }
        });
        fieldsForDownload.put(PAY_ORDER_ALGORITHM, new Element() {
            public Object getValue(Maket1Data paymentData) {
                return paymentData.getPayOrderAlgorithm();
            }
        });
        fieldsForDownload.put(PAYMENT_DESTINATION_ALIAS, new Element() {
            public Object getValue(Maket1Data paymentData) {
                return paymentData.getPaymentDestination();
            }
        });
        fieldsForDownload.put(TEL_ALIAS, new Element() {
            public Object getValue(Maket1Data paymentData) {
                return paymentData.getTel();
            }
        });
        fieldsForDownload.put(RECEIVER_NUMBER_ALIAS, new Element() {
            public Object getValue(Maket1Data paymentData) {
                return paymentData.getReceiverNumber();
            }
        });
        fieldsForDownload.put(RECEIVER_NAME_ALIAS, new Element() {
            public Object getValue(Maket1Data paymentData) {
                return paymentData.getReceiverName();
            }
        });
        fieldsForDownload.put(PAYER_BIRTHDAY_ALIAS, new Element() {
            public Object getValue(Maket1Data paymentData) {
                return paymentData.getPayerBirthday();
            }
        });
        fieldsForDownload.put(SPECIAL_CLIENT_CODE_ALIAS, new Element() {
            public Object getValue(Maket1Data paymentData) {
                return paymentData.getSpecialClientCode();
            }
        });
        fieldsForDownload.put(TAXES_DOC_DATE_ALIAS, new Element() {
            public Object getValue(Maket1Data paymentData) {
                return paymentData.getTaxesDocumentDate();
            }
        });
        fieldsForDownload.put(TAXES_DOC_NUMBER_ALIAS, new Element() {
            public Object getValue(Maket1Data paymentData) {
                return paymentData.getTaxesDocumentNumber();
            }
        });
        fieldsForDownload.put(TAXES_DOC_TYPE_ALIAS, new Element() {
            public Object getValue(Maket1Data paymentData) {
                return paymentData.getTaxesDocumentType();
            }
        });
        fieldsForDownload.put(TAXES_PERIOD_ALIAS, new Element() {
            public Object getValue(Maket1Data paymentData) {
                return paymentData.getTaxesPeriod();
            }
        });
        fieldsForDownload.put(TAXES_REASON_ALIAS, new Element() {
            public Object getValue(Maket1Data paymentData) {
                return paymentData.getTaxesReason();
            }
        });
        fieldsForDownload.put(CREATOR_STATUS_ALIAS, new Element() {
            public Object getValue(Maket1Data paymentData) {
                return paymentData.getCreatorStatus();
            }
        });
        fieldsForDownload.put(PAYER_INN_ALIAS, new Element() {
            public Object getValue(Maket1Data paymentData) {
                return paymentData.getPayerInn();
            }
        });
        fieldsForDownload.put(SERVICE_KIND_ALIAS, new Element() {
            public Object getValue(Maket1Data paymentData) {
                return paymentData.getServiceKind();
            }
        });
        fieldsForDownload.put(IN_REG_ID_ALIAS, new Element() {
            public Object getValue(Maket1Data paymentData) {
                return new Long(paymentData.getInRegId());
            }
        });
        fieldsForDownload.put(IN_REG_LINE_ALIAS, new Element() {
            public Object getValue(Maket1Data paymentData) {
                return new Long(paymentData.getInRegLine());
            }
        });
        fieldsForDownload.put(ROUTE_ALIAS, new Element() {
            public Object getValue(Maket1Data paymentData) {
                return paymentData.getRoute();
            }
        });
        fieldsForDownload.put(SUM_ROUBLES_ALIAS, new Element() {
            public Object getValue(Maket1Data paymentData) {
                return SumConverter.getInstance().toBigDecimal(paymentData.getSum());
            }
        });
        fieldsForDownload.put(SERVICE_COMM_ROUBLES_ALIAS, new Element() {
            public Object getValue(Maket1Data paymentData) {
                return SumConverter.getInstance().toBigDecimal(paymentData.getServiceCommission());
            }
        });
        fieldsForDownload.put(PAYMENT_PAYER_COMM_ROUBLES_ALIAS, new Element() {
            public Object getValue(Maket1Data paymentData) {
                return SumConverter.getInstance().toBigDecimal(paymentData.getPayerCommission());
            }
        });
        fieldsForDownload.put(SUM_CLEAR_ALIAS, new Element() {
            public Object getValue(Maket1Data paymentData) {
                return new Long(paymentData.getSum() - paymentData.getServiceCommission());
            }
        });
        fieldsForDownload.put(SUM_CLEAR_ROUBLES_ALIAS, new Element() {
            public Object getValue(Maket1Data paymentData) {
                return SumConverter.getInstance().toBigDecimal(paymentData.getSum() - paymentData.getServiceCommission());
            }
        });
    }

    /**
     * Возвращяет значение поля из макета платежа согласно алиасу.
     * 
     * @param paymentData
     *            данные платежа
     * @param alias
     *            алиас
     * @return Object в случае наличия значения, null в случае отсутствия
     */
    public static final Object getField(Maket1Data paymentData, String alias) {
        Object result = null;
        Element element = ((Element) fieldsForDownload.get(alias));
        if (element != null) {
            result = element.getValue(paymentData);
        }
        return result;
    }
    
    /**
     * Возвращает реквизит из строки доп. реквизитов по его порядковому номеру
     * 
     * @param paymentData
     *            данные платежа
     * @param number
     *            порядковый номер
     * @return Object со значением либо null, если нет доп.реквизита с таким
     *         номером
     */
    public static final Object getAddRequisiteByNumber(Maket1Data paymentData, int number) {
        Object result = null;
        String addReq = paymentData.getAdditionalRequisites();
        if (addReq == null) {
            return result;
        }
        String separated[] = addReq.split(DEFAULT_SPLITTER, -1);
        if (separated.length < number) {
            return result;
        } else {
            result = separated[number - 1];
        }
        return result;
    }
   
}
