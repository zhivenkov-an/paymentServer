package com.sbrf.bc.processor.selfservdevice.fix;

import java.util.Map;
import java.util.Properties;
import java.util.TreeMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.epam.sbrf.bc.data.PaymentData;
import com.sbrf.bc.plugin.BillingPlugin;
import com.sbrf.bc.plugin.PluginConfigurationHelper;

public class SelfServiceTagsPlugin implements  BillingPlugin{
    
    public static final String PLUGIN_NAME = "com.sbrf.bc.processor.selfservdevice.SelfServiceTagsPlugin";
    
    /**
     * Структурные элементы
     */
    public String JBT_OUTPUT; 
    public String FORMAT_VERSION;
    public String OUTPUT_PERIOD;
    public String STARTDATE;
    public String STARTTIME;
    public String ENDDATE;
    public String ENDTIME;
    public String SOURCE_SERVER;
    public String TRANSACTIONS;
    public String TRANSACTION_ELEMENT;
    
    public String PAYMENTS;
    public String PAYMENT;
    public String PARAMETERS;
    public String PARAMETER;

    /**
     * Элементы транзакции
     */
    public String TRANSACTION_TRN_ID;
    public String TRANSACTION_TRN_ID_REM;
    public String TRANSACTION_SRVCODE;
    public String TRANSACTION_ICM;
    public String TRANSACTION_ICM_REM;
    public String TRANSACTION_PAYMENT_TYPE;
    public String TRANSACTION_CASHIER;
    public String TRANSACTION_TERMINAL_NUMBER;
    public String TRANSACTION_OSB;
    public String TRANSACTION_COD_TB;
    
    public String TRANSACTION_PAYMENT_MEAN;
    public String TRANSACTION_PAYMENT_SERVICE_CHANNEL;
    public String TRANSACTION_PAYMENT_KIND;

    public String TRANSACTION_PAYMENT_DATE;
    public String TRANSACTION_OPERATION_DATE;
    public String TRANSACTION_OPERATION_TIME;
    public String TRANSACTION_SUM;
    public String TRANSACTION_IDENT;
    public String TRANSACTION_USN_MC;
    public String TRANSACTION_ICON_MC;
    public String TRANSACTION_BDT_MC;
    public String TRANSACTION_BN_MC;
    public String TRANSACTION_NTF_MC;
    public String TRANSACTION_SERT_MC;
    
    public String TRANSACTION_USN_CC;
    public String TRANSACTION_ICON_CC;
    public String TRANSACTION_TSN_CC;
    public String TRANSACTION_ACCOUNT_CC;
    public String TRANSACTION_BANK_CC;
    public String TRANSACTION_ACCTYPE_CC;
    public String TRANSACTION_SERT_CC;
    public String TRANSACTION_CARD_NUMBER;
    public String TRANSACTION_AUTH_CODE;
    public String TRANSACTION_TERMID;
    
    public String TRANSACTION_MERCHID;
    public String TRANSACTION_DATE_CFS;
    public String TRANSACTION_TIME_CFS;
    public String TRANSACTION_NEED_AMMOUNT;
    public String TRANSACTION_DEVICE_NUMBER;
    public String TRANSACTION_BNKCIOD_OPEN;
    
    public String SV_UTRNNO;
    public String ASBC_PAY_ID;
    /**
     * Элементы реквизитов платежа
     */
    public String PAYMENT_DOCUMENT_NUMBER;
    public String PAYMENT_SUM;
    public String PAYMENT_PAYER_COMMISSION;
    public String PAYMENT_AMMOUNT_OVER;
    public String PAYMENT_RESDOC;
    public String PAYMENT_BC_TRN;
    public String PAYMENT_DATE_EXE;
    public String PAYMENT_TIME_EXE;
    public String PAYMENT_RECEIVER_NUMBER;
    public String PAYMENT_SPECIAL_CLIENT_CODE;
    
    public String PAYMENT_RECEIVER_NAME;
    public String PAYMENT_BIK;
    public String PAYMENT_CORR_ACC;
    public String PAYMENT_SETTLE_ACC;
    public String PAYMENT_INN;
    public String PAYMENT_BANK;
    public String PAYMENT_RECEIPMERCHID;
    public String PAYMENT_RECEIPFILIALNUM;
    public String PAYMENT_PERSONAL_ACCOUNT;
    public String PAYMENT_PERSENT;
    public String PAYMENT_AMMOUNTCOMMMIN;
    
    public String PAYMENT_AMMOUNTCOMMMAX;
    public String PAYMENT_DOGOVOR;
    public String PAYMENT_PAYER_INFO;
    public String PAYMENT_STREET;
    public String PAYMENT_HOUSE;
    public String PAYMENT_BUILDING;
    public String PAYMENT_FLAT;
    public String PAYMENT_PAYER_ACC;
    public String PAYMENT_PERIOD;
    public String PAYMENT_PAYMENT_DESTINATION;
    /**
     * Элементы дополнительных параметров
     */
        
    public String PARAMETER_SHORT_NAME;
    public String PARAMETER_TYPE;
    public String PARAMETER_VALUE;
    
    /**
     * Псевдонимы для доп.реквизитов. Именя заданы согласно классификатору Shortnames.
     */
    //Псевдоним для номера платежа, который был распечатан на чеке клиента
    public String REAL_PAYMENT_ID_SHORT;
    public String REAL_VSP_SHORT;
    //Параметры транзакции. Необходимы для возможной претензионной работы (используются Уральским банком).
    public String TRANSACTION_TRN_ID_SHORT;
    public String TRANSACTION_TRN_ID_REM_SHORT;
    public String TRANSACTION_SRVCODE_SHORT;
    public String PERIOD_SHORT;
    public String BIK_SHORT;
    public String CORRACCOUNT_SHORT;
    public String CREATORSTATUS_SHORT;
    public String INN_SHORT;
    public String KBK_SHORT;
    public String KPP_SHORT;
    public String OKATO_SHORT;
    public String PERSONALACCOUNT_SHORT;
    public String SETTLEACCOUNT_SHORT;
    public String SPECIALCLIENTCODE_SHORT;
    public String SERVICEKIND_SHORT;
    public String PAYMENTTYPE_SHORT;
    public String RECEIVERNUMBER_SHORT;
    public String PAYMENTDESTINATION_SHORT;
    public String PAYMENTDESTINATIONFAKE_SHORT;
    public String RECEIVERNAME_SHORT;
    public String ADDITIONAL_REQUISITES_SHORT;
    
    /*
     * Плательщик
     */
    public String PAYERADDRESS_SHORT;
    public String PAYERBIK_SHORT;
    public String PAYERINN_SHORT;
    public String PAYERINNPAYMENTS_SHORT;
    public String PAYERNAME_SHORT;
    public String PAYERSETTLEACCOUNT_SHORT;
    public String PAYERPERSONALACCOUNT_SHORT;
    public String PAYERACC_SHORT;
    public String PAYERBIRTHDAY_SHORT;
    public String PAYERFIO_SHORT;
    public String PAYERLASTNAME_SHORT;
    public String PAYERFIRSTNAME_SHORT;
    public String PAYERMIDDLENAME_SHORT;
    public String PAYERFIRSTNAME_SHORT_I;
    public String PAYERMIDDLENAME_SHORT_I;
    
    public String REGION_SHORT;
    public String ZIPCODE_SHORT;
    public String TOWNSHIP_SHORT;
    public String TOWN_SHORT;
    public String COMMUNITY_SHORT;
    public String STREET_SHORT;
    public String HOUSE_SHORT;
    public String BUILDING_SHORT;
    public String FLAT_SHORT;
    public String STATE_SHORT;
    public String PAYERBIRTHDATE_SHORT;
    public String PAYERBIRTHPLACE_SHORT;
    
    public String TAXESDOCUMENTDATE_SHORT;
    public String TAXESDOCUMENTNUMBER_SHORT;
    public String TAXESDOCUMENTTYPE_SHORT;
    public String TAXESPERIOD_SHORT;
    public String TAXESREASON_SHORT;
    public String PROVIDER_INFO_SHORT;
    
    static final Pattern innPattern = Pattern.compile("\\d+");
    static final Pattern numbers = Pattern.compile("\\d+");

    public SelfServiceTagsPlugin(Properties properties) {
        PluginConfigurationHelper context = new PluginConfigurationHelper(properties);
        
        JBT_OUTPUT = context.getString("JBT_OUTPUT", "jbt_output");
        FORMAT_VERSION = context.getString("FORMAT_VERSION", "format_version");
        OUTPUT_PERIOD = context.getString("OUTPUT_PERIOD", "output_period");
        STARTDATE = context.getString("STARTDATE", "startdate");
        STARTTIME = context.getString("STARTTIME", "starttime");
        ENDDATE = context.getString("ENDDATE", "enddate");
        ENDTIME = context.getString("ENDTIME", "endtime");
        SOURCE_SERVER = context.getString("SOURCE_SERVER", "source_server");
        TRANSACTIONS = context.getString("TRANSACTIONS", "transactions");
        TRANSACTION_ELEMENT = context.getString("TRANSACTION_ELEMENT", "transactions_element");
        
        PAYMENTS = context.getString("PAYMENTS", "payments");
        PAYMENT = context.getString("PAYMENT", "payment");
        PARAMETERS = context.getString("PARAMETERS", "parameters");
        PARAMETER = context.getString("PARAMETER", "parameter");
        
        TRANSACTION_TRN_ID = context.getString("TRANSACTION_TRN_ID", "trn_id");
        TRANSACTION_TRN_ID_REM = context.getString("TRANSACTION_TRN_ID_REM", "trn_id_rem");
        TRANSACTION_SRVCODE = context.getString("TRANSACTION_SRVCODE", "srvcode");
        TRANSACTION_ICM = context.getString("TRANSACTION_ICM", "icm");
        TRANSACTION_ICM_REM = context.getString("TRANSACTION_ICM_REM", "icm_rem");
        TRANSACTION_PAYMENT_TYPE = context.getString("TRANSACTION_PAYMENT_TYPE", "trantype");
        TRANSACTION_CASHIER = context.getString("TRANSACTION_CASHIER", "termnum");
        TRANSACTION_TERMINAL_NUMBER = context.getString("TRANSACTION_TERMINAL_NUMBER", "termnum");
        TRANSACTION_OSB = context.getString("TRANSACTION_OSB", "filialnum");
        TRANSACTION_COD_TB = context.getString("TRANSACTION_COD_TB", "COD_TB");
        
        TRANSACTION_PAYMENT_MEAN = context.getString("TRANSACTION_PAYMENT_MEAN", "payment_mean");
        TRANSACTION_PAYMENT_SERVICE_CHANNEL = context.getString("TRANSACTION_PAYMENT_SERVICE_CHANNEL", "service_channel");
        TRANSACTION_PAYMENT_KIND = context.getString("TRANSACTION_PAYMENT_KIND", "payment_kind");
        
        TRANSACTION_PAYMENT_DATE = context.getString("TRANSACTION_PAYMENT_DATE", "ldate");
        TRANSACTION_OPERATION_DATE = context.getString("TRANSACTION_OPERATION_DATE", "tropdate");
        TRANSACTION_OPERATION_TIME = context.getString("TRANSACTION_OPERATION_TIME", "ltime");
        TRANSACTION_SUM = context.getString("TRANSACTION_SUM", "amount");
        TRANSACTION_IDENT = context.getString("TRANSACTION_IDENT", "ident");
        TRANSACTION_USN_MC = context.getString("TRANSACTION_USN_MC", "usn_mc");
        TRANSACTION_ICON_MC = context.getString("TRANSACTION_ICON_MC", "icon_mc");
        TRANSACTION_BDT_MC = context.getString("TRANSACTION_BDT_MC", "bdt_mc");
        TRANSACTION_BN_MC = context.getString("TRANSACTION_BN_MC", "bn_mc");
        TRANSACTION_NTF_MC = context.getString("TRANSACTION_NTF_MC", "ntf_mc");
        TRANSACTION_SERT_MC = context.getString("TRANSACTION_SERT_MC", "sert_mc");
        
        TRANSACTION_USN_CC = context.getString("TRANSACTION_USN_CC", "usn_cc");
        TRANSACTION_ICON_CC = context.getString("TRANSACTION_ICON_CC", "icon_cc");
        TRANSACTION_TSN_CC = context.getString("TRANSACTION_TSN_CC", "tsn_cc");
        TRANSACTION_ACCOUNT_CC = context.getString("TRANSACTION_ACCOUNT_CC", "account_cc");
        TRANSACTION_BANK_CC = context.getString("TRANSACTION_BANK_CC", "bank_cc");
        TRANSACTION_ACCTYPE_CC = context.getString("TRANSACTION_ACCTYPE_CC", "acctype_cc");
        TRANSACTION_SERT_CC = context.getString("TRANSACTION_SERT_CC", "sert_cc");
        TRANSACTION_CARD_NUMBER = context.getString("TRANSACTION_CARD_NUMBER", "cardinfo");
        TRANSACTION_AUTH_CODE = context.getString("TRANSACTION_AUTH_CODE", "authcode");
        TRANSACTION_TERMID = context.getString("TRANSACTION_TERMID", "termid");
        
        TRANSACTION_MERCHID = context.getString("TRANSACTION_MERCHID", "merchid");
        TRANSACTION_DATE_CFS = context.getString("TRANSACTION_DATE_CFS", "date_cfs");
        TRANSACTION_TIME_CFS = context.getString("TRANSACTION_TIME_CFS", "time_cfs");
        TRANSACTION_NEED_AMMOUNT = context.getString("TRANSACTION_NEED_AMMOUNT", "needammount");
        TRANSACTION_DEVICE_NUMBER = context.getString("TRANSACTION_DEVICE_NUMBER", "bnkciod");
        TRANSACTION_BNKCIOD_OPEN = context.getString("TRANSACTION_BNKCIOD_OPEN", "bnkiodopen");
        
        SV_UTRNNO = context.getString("SV_UTRNNO", "sv_utrnno");
        ASBC_PAY_ID = context.getString("ASBC_PAY_ID", "asbc_pay_id");
        
        PAYMENT_DOCUMENT_NUMBER = context.getString("PAYMENT_DOCUMENT_NUMBER", "pay_id");
        PAYMENT_SUM = context.getString("PAYMENT_SUM", "amount");
        PAYMENT_PAYER_COMMISSION = context.getString("PAYMENT_PAYER_COMMISSION", "amountcom");
        PAYMENT_AMMOUNT_OVER = context.getString("PAYMENT_AMMOUNT_OVER", "amountover");
        PAYMENT_RESDOC = context.getString("PAYMENT_RESDOC", "resdoc");
        PAYMENT_BC_TRN = context.getString("PAYMENT_BC_TRN", "bc_trn");
        PAYMENT_DATE_EXE = context.getString("PAYMENT_DATE_EXE", "date_exe");
        PAYMENT_TIME_EXE = context.getString("PAYMENT_TIME_EXE", "time_exe");
        PAYMENT_RECEIVER_NUMBER = context.getString("PAYMENT_RECEIVER_NUMBER", "recipcode");
        PAYMENT_SPECIAL_CLIENT_CODE = context.getString("PAYMENT_SPECIAL_CLIENT_CODE", "recipcod_spc");
        
        PAYMENT_RECEIVER_NAME = context.getString("PAYMENT_RECEIVER_NAME", "recip_nameorg");
        PAYMENT_BIK = context.getString("PAYMENT_BIK", "recipbic");
        PAYMENT_CORR_ACC = context.getString("PAYMENT_CORR_ACC", "recipcorraccount");
        PAYMENT_SETTLE_ACC = context.getString("PAYMENT_SETTLE_ACC", "recipaccount");
        PAYMENT_INN = context.getString("PAYMENT_INN", "recipinn");
        PAYMENT_BANK = context.getString("PAYMENT_BANK", "recipbank");
        PAYMENT_RECEIPMERCHID = context.getString("PAYMENT_RECEIPMERCHID", "recipmerchid");
        PAYMENT_RECEIPFILIALNUM = context.getString("PAYMENT_RECEIPFILIALNUM", "recipfilialnum");
        PAYMENT_PERSONAL_ACCOUNT = context.getString("PAYMENT_PERSONAL_ACCOUNT", "persaccount");
        PAYMENT_PERSENT = context.getString("PAYMENT_PERSENT", "percent");
        PAYMENT_AMMOUNTCOMMMIN = context.getString("PAYMENT_AMMOUNTCOMMMIN", "ammountcommin");
        
        PAYMENT_AMMOUNTCOMMMAX = context.getString("PAYMENT_AMMOUNTCOMMMAX", "ammountcommax");
        PAYMENT_DOGOVOR = context.getString("PAYMENT_DOGOVOR", "dogovor");
        PAYMENT_PAYER_INFO = context.getString("PAYMENT_PAYER_INFO", "fio");
        PAYMENT_STREET = context.getString("PAYMENT_STREET", "street");
        PAYMENT_HOUSE = context.getString("PAYMENT_HOUSE", "house");
        PAYMENT_BUILDING = context.getString("PAYMENT_BUILDING", "buiding");
        PAYMENT_FLAT = context.getString("PAYMENT_FLAT", "flat");
        PAYMENT_PAYER_ACC = context.getString("PAYMENT_PAYER_ACC", "useraccount");
        PAYMENT_PERIOD = context.getString("PAYMENT_PERIOD", "period");
        PAYMENT_PAYMENT_DESTINATION = context.getString("PAYMENT_PAYMENT_DESTINATION", "paydesc");
        
        PARAMETER_SHORT_NAME = context.getString("PARAMETER_SHORT_NAME", "shortname");
        PARAMETER_TYPE = context.getString("PARAMETER_TYPE", "type");
        PARAMETER_VALUE = context.getString("PARAMETER_VALUE", "value");
        
        REAL_PAYMENT_ID_SHORT = context.getString("REAL_PAYMENT_ID_SHORT", "paymentId");
        REAL_VSP_SHORT = context.getString("REAL_VSP_SHORT", "vsp");
        TRANSACTION_TRN_ID_SHORT = context.getString("TRANSACTION_TRN_ID_SHORT", "trnId");
        TRANSACTION_TRN_ID_REM_SHORT = context.getString("TRANSACTION_TRN_ID_REM_SHORT", "trnIdRem");
        TRANSACTION_SRVCODE_SHORT = context.getString("TRANSACTION_SRVCODE_SHORT", "srvCode");
        PERIOD_SHORT = context.getString("PERIOD_SHORT", "period");
        BIK_SHORT = context.getString("BIK_SHORT", "bik");
        CORRACCOUNT_SHORT = context.getString("CORRACCOUNT_SHORT", "corrAccount");
        CREATORSTATUS_SHORT = context.getString("CREATORSTATUS_SHORT", "creatorStatus");
        INN_SHORT = context.getString("INN_SHORT", "inn");
        KBK_SHORT = context.getString("KBK_SHORT", "kbk");
        KPP_SHORT = context.getString("KPP_SHORT", "kpp");
        OKATO_SHORT = context.getString("OKATO_SHORT", "okato");
        PERSONALACCOUNT_SHORT = context.getString("PERSONALACCOUNT_SHORT", "personalAccount");
        SETTLEACCOUNT_SHORT = context.getString("SETTLEACCOUNT_SHORT", "settleAccount");
        SPECIALCLIENTCODE_SHORT = context.getString("SPECIALCLIENTCODE_SHORT", "specialClientCode");
        SERVICEKIND_SHORT = context.getString("SERVICEKIND_SHORT", "serviceKind");
        PAYMENTTYPE_SHORT = context.getString("PAYMENTTYPE_SHORT", "paymentType");
        RECEIVERNUMBER_SHORT = context.getString("RECEIVERNUMBER_SHORT", "receiverNumber");
        PAYMENTDESTINATION_SHORT = context.getString("PAYMENTDESTINATION_SHORT", "paymentDestination");
        PAYMENTDESTINATIONFAKE_SHORT = context.getString("PAYMENTDESTINATIONFAKE_SHORT", "paymentDestinationFake");
        RECEIVERNAME_SHORT = context.getString("RECEIVERNAME_SHORT", "receiverName");
        ADDITIONAL_REQUISITES_SHORT = context.getString("ADDITIONAL_REQUISITES_SHORT", "additionalRequisites");
        
        PAYERADDRESS_SHORT = context.getString("PAYERADDRESS_SHORT", "payerAddress");
        PAYERBIK_SHORT = context.getString("PAYERBIK_SHORT", "payerBik");
        PAYERINN_SHORT = context.getString("PAYERINN_SHORT", "payerInn");
        PAYERINNPAYMENTS_SHORT = context.getString("PAYERINNPAYMENTS_SHORT", "payerInnPayments");
        PAYERNAME_SHORT = context.getString("PAYERNAME_SHORT", "payerName");
        PAYERSETTLEACCOUNT_SHORT = context.getString("PAYERSETTLEACCOUNT_SHORT", "payerSettleAccount");
        PAYERPERSONALACCOUNT_SHORT = context.getString("PAYERPERSONALACCOUNT_SHORT", "payerPersonalAccount");
        PAYERACC_SHORT = context.getString("PAYERACC_SHORT", "payerAcc");
        PAYERBIRTHDAY_SHORT = context.getString("PAYERBIRTHDAY_SHORT", "payerBirthday");
        PAYERFIO_SHORT = context.getString("PAYERFIO_SHORT", "payerFio");
        PAYERLASTNAME_SHORT = context.getString("PAYERLASTNAME_SHORT", "payerLastName");
        PAYERFIRSTNAME_SHORT = context.getString("PAYERFIRSTNAME_SHORT", "payerFirstName");
        PAYERMIDDLENAME_SHORT = context.getString("PAYERMIDDLENAME_SHORT", "payerMiddleName");
        PAYERFIRSTNAME_SHORT_I = context.getString("PAYERFIRSTNAME_SHORT_I", "payerFirstNameI");
        PAYERMIDDLENAME_SHORT_I = context.getString("PAYERMIDDLENAME_SHORT_I", "payerMiddleNameI");
        
        REGION_SHORT = context.getString("REGION_SHORT", "region");
        ZIPCODE_SHORT = context.getString("ZIPCODE_SHORT", "zipcode");
        TOWNSHIP_SHORT = context.getString("TOWNSHIP_SHORT", "township");
        TOWN_SHORT = context.getString("TOWN_SHORT", "town");
        COMMUNITY_SHORT = context.getString("COMMUNITY_SHORT", "community");
        STREET_SHORT = context.getString("STREET_SHORT", "street");
        HOUSE_SHORT = context.getString("HOUSE_SHORT", "house");
        BUILDING_SHORT = context.getString("BUILDING_SHORT", "building");
        FLAT_SHORT = context.getString("FLAT_SHORT", "flat");
        STATE_SHORT = context.getString("STATE_SHORT", "state");
        PAYERBIRTHDATE_SHORT = context.getString("PAYERBIRTHDATE_SHORT", "payerBirthDate");
        PAYERBIRTHPLACE_SHORT = context.getString("PAYERBIRTHPLACE_SHORT", "payerBirthPlace");
        
        TAXESDOCUMENTDATE_SHORT = context.getString("TAXESDOCUMENTDATE_SHORT", "taxesDocumentDate");
        TAXESDOCUMENTNUMBER_SHORT = context.getString("TAXESDOCUMENTNUMBER_SHORT", "taxesDocumentNumber");
        TAXESDOCUMENTTYPE_SHORT = context.getString("TAXESDOCUMENTTYPE_SHORT", "taxesDocumentType");
        TAXESPERIOD_SHORT = context.getString("TAXESPERIOD_SHORT", "taxesPeriod");
        TAXESREASON_SHORT = context.getString("TAXESREASON_SHORT", "taxesReson");
        PROVIDER_INFO_SHORT = context.getString("PROVIDER_INFO_SHORT", "provider_info");
    
        makeMaps();
    }
    
    /**
     * Мэп для автоматического заполнения реквизитов
     */
    private interface Element {
        public void setValue(PaymentData paymentData, String value);
    }

    public static Map<String, Object> tagsCorrelation;
    public static Map<String, Object> shortNameTagsCorrelation;
    
    void makeMaps(){
        tagsCorrelation = new TreeMap<String, Object>();
        
        tagsCorrelation.put(TRANSACTION_OSB, new Element() {
            public void setValue(PaymentData paymentData, String value) {
                paymentData.setOsb(value);
                paymentData.setOriginalOsb(value);
            }
        });
        Element docNumber = new Element() {
            public void setValue(PaymentData paymentData, String value) {
                paymentData.setDocumentNumber(value);
            }
        };
        tagsCorrelation.put(PAYMENT_DOCUMENT_NUMBER, docNumber);
        tagsCorrelation.put(TRANSACTION_PAYMENT_TYPE, new Element() {
            public void setValue(PaymentData paymentData, String value) {
                paymentData.setPaymentType(Integer.parseInt(value));
            }
        });
        tagsCorrelation.put(PAYMENT_SUM, new Element() {
            public void setValue(PaymentData paymentData, String value) {
                paymentData.setSum(Long.parseLong(value));
            }
        });
        
        tagsCorrelation.put(PAYMENT_PAYER_COMMISSION, new Element() {
            public void setValue(PaymentData paymentData, String value) {
                paymentData.setPayerCommission(Long.parseLong(value));
            }
        });
        
        tagsCorrelation.put(PAYMENT_PAYER_INFO, new Element() {
            public void setValue(PaymentData paymentData, String value) {
                paymentData.setPayerInfo(value);
            }
        });
        
        Element bik = new Element() {
            public void setValue(PaymentData paymentData, String value) {
                Matcher matcher = numbers.matcher(value);
                if (matcher.matches()) {
                    paymentData.setBik(value);
                }    
            }
        };
        tagsCorrelation.put(PAYMENT_BIK, bik);
        
        tagsCorrelation.put(PAYMENT_RECEIVER_NUMBER, new Element() {
            public void setValue(PaymentData paymentData, String value) {
                Matcher matcher = numbers.matcher(value);
                if (matcher.matches()) {
                    paymentData.setReceiverNumber(value);
                }    
            }
        });
        tagsCorrelation.put(PAYMENT_PAYER_ACC, new Element() {
            public void setValue(PaymentData paymentData, String value) {
                paymentData.setPayerAcc(value);
            }
        });
        tagsCorrelation.put(PAYMENT_SETTLE_ACC, new Element() {
            public void setValue(PaymentData paymentData, String value) {
                Matcher matcher = numbers.matcher(value);
                if (matcher.matches()) {
                    paymentData.setSettleAcc(value);
                }
            }
        });
               
        Element addAddress = new Element() {
           public void setValue(PaymentData paymentData, String value) {
               if (value != null) {
                   if (paymentData.getPayerAddress() != null) {
                       paymentData.setPayerAddress(paymentData.getPayerAddress() + " " + value);
                   } else {
                       paymentData.setPayerAddress(value);
                   }
               }
           }
        };
        
        tagsCorrelation.put(PAYMENT_STREET, addAddress);
        tagsCorrelation.put(PAYMENT_BUILDING, addAddress);
        tagsCorrelation.put(PAYMENT_HOUSE, addAddress);
        tagsCorrelation.put(PAYMENT_FLAT, addAddress);
        
        Element inn = new Element() {
            public void setValue(PaymentData paymentData, String value) {
                Matcher matcher = innPattern.matcher(value);
                if (matcher.matches()) {
                    paymentData.setInn(value);
                }    
            }
        };
        tagsCorrelation.put(PAYMENT_INN, inn);
        
        tagsCorrelation.put(TRANSACTION_TERMINAL_NUMBER, new Element() {
            public void setValue(PaymentData paymentData, String value) {
                paymentData.setCashier(value);
            }
        });
        
        Element specCode = new Element() {
            public void setValue(PaymentData paymentData, String value) {
                Matcher matcher = numbers.matcher(value);
                if (matcher.matches()) {
                    paymentData.setSpecialClientCode(value);
                }    
            }
        };
        tagsCorrelation.put(PAYMENT_SPECIAL_CLIENT_CODE, specCode);
        
        Element receiverName = new Element() {
            public void setValue(PaymentData paymentData, String value) {
                paymentData.setReceiverName(value);
            }
        };
        tagsCorrelation.put(PAYMENT_RECEIVER_NAME, receiverName);
        
        tagsCorrelation.put(TRANSACTION_CASHIER, new Element() {
            public void setValue(PaymentData paymentData, String value) {
                paymentData.setCashier(value);
            }
        });
        
        Element paymentDest = new Element() {
            public void setValue(PaymentData paymentData, String value) {
                paymentData.setPaymentDestination(value);
            }
        };
        tagsCorrelation.put(PAYMENT_PAYMENT_DESTINATION, paymentDest);
        
        Element corrAcc = new Element() {
            public void setValue(PaymentData paymentData, String value) {
                paymentData.setCorrAcc(value);
            }
        };
        tagsCorrelation.put(CORRACCOUNT_SHORT, corrAcc);
        
        shortNameTagsCorrelation = new TreeMap<String, Object>();
        shortNameTagsCorrelation.put(REAL_PAYMENT_ID_SHORT, docNumber);
        shortNameTagsCorrelation.put(REAL_PAYMENT_ID_SHORT.toUpperCase(), docNumber);
        
        shortNameTagsCorrelation.put(TOWN_SHORT, addAddress);
        shortNameTagsCorrelation.put(STREET_SHORT, addAddress);
        shortNameTagsCorrelation.put(BUILDING_SHORT, addAddress);
        shortNameTagsCorrelation.put(HOUSE_SHORT, addAddress);
        shortNameTagsCorrelation.put(FLAT_SHORT, addAddress);
        
        Element creatorStatus = new Element() {
            public void setValue(PaymentData paymentData, String value) {
                Matcher matcher = numbers.matcher(value);
                if (matcher.matches()) {
                    paymentData.setCreatorStatus(value);
                }    
            }
        };
        shortNameTagsCorrelation.put(CREATORSTATUS_SHORT, creatorStatus);
        shortNameTagsCorrelation.put(CREATORSTATUS_SHORT.toUpperCase(), creatorStatus);
        
        
        shortNameTagsCorrelation.put(INN_SHORT, inn);
        shortNameTagsCorrelation.put(INN_SHORT.toUpperCase(), inn);
        
        Element kpp = new Element() {
            public void setValue(PaymentData paymentData, String value) {
                Matcher matcher = numbers.matcher(value);
                if (matcher.matches()) {
                    paymentData.setKpp(value);
                }    
            }
        };
        shortNameTagsCorrelation.put(KPP_SHORT, kpp);
        shortNameTagsCorrelation.put(KPP_SHORT.toUpperCase(), kpp);
        
        Element kbk = new Element() {
            public void setValue(PaymentData paymentData, String value) {
                Matcher matcher = numbers.matcher(value);
                if (matcher.matches()) {
                    paymentData.setKbk(value);
                }    
            }
        };
        shortNameTagsCorrelation.put(KBK_SHORT, kbk);
        shortNameTagsCorrelation.put(KBK_SHORT.toUpperCase(), kbk);
        
        Element okato = new Element() {
            public void setValue(PaymentData paymentData, String value) {
                Matcher matcher = numbers.matcher(value);
                if (matcher.matches()) {
                    paymentData.setOkato(value);
                }    
            }
        };
        shortNameTagsCorrelation.put(OKATO_SHORT,okato);
        shortNameTagsCorrelation.put(OKATO_SHORT.toUpperCase(),okato);
        
        Element personalAcc = new Element() {
            public void setValue(PaymentData paymentData, String value) {
                paymentData.setPersonalAccount(value);
            }
        };
        shortNameTagsCorrelation.put(PERSONALACCOUNT_SHORT, personalAcc);
        shortNameTagsCorrelation.put(PERSONALACCOUNT_SHORT.toUpperCase(), personalAcc);
        
        shortNameTagsCorrelation.put(SPECIALCLIENTCODE_SHORT, specCode);
        shortNameTagsCorrelation.put(SPECIALCLIENTCODE_SHORT.toUpperCase(), specCode);
        
        Element servKind = new Element() {
            public void setValue(PaymentData paymentData, String value) {
                Matcher matcher = numbers.matcher(value);
                if (matcher.matches()) {
                  paymentData.setServiceKind(value);  
                }    
            }
        };
        shortNameTagsCorrelation.put(SERVICEKIND_SHORT, servKind);
        shortNameTagsCorrelation.put(SERVICEKIND_SHORT.toUpperCase(), servKind);
        
        Element receiverNumber = new Element() {
            public void setValue(PaymentData paymentData, String value) {
                paymentData.setReceiverNumber(value);
            }
        };
        shortNameTagsCorrelation.put(RECEIVERNUMBER_SHORT, receiverNumber);
        shortNameTagsCorrelation.put(RECEIVERNUMBER_SHORT.toUpperCase(), receiverNumber);
        
        shortNameTagsCorrelation.put(RECEIVERNAME_SHORT, receiverName);
        shortNameTagsCorrelation.put(RECEIVERNAME_SHORT.toUpperCase(), receiverName);
        
        shortNameTagsCorrelation.put(PAYMENTDESTINATION_SHORT, paymentDest);
        shortNameTagsCorrelation.put(PAYMENTDESTINATION_SHORT.toUpperCase(), paymentDest);
        
        /*
         * Реквизиты плательщика
         */
        Element payerInn = new Element() {
            public void setValue(PaymentData paymentData, String value) {
                if (value != null && innPattern.matcher(value).matches()) {
                    paymentData.setPayerInn(value);
                }
            }
        };
        
        shortNameTagsCorrelation.put(PAYERINNPAYMENTS_SHORT, payerInn);
        shortNameTagsCorrelation.put(PAYERINNPAYMENTS_SHORT.toUpperCase(), payerInn);
        
        Element payerBirthDay = new Element() {
            public void setValue(PaymentData paymentData, String value) {
                paymentData.setPayerBirthday(value);
            }
        };
        shortNameTagsCorrelation.put(PAYERBIRTHDAY_SHORT, payerBirthDay);
        shortNameTagsCorrelation.put(PAYERBIRTHDAY_SHORT.toUpperCase(), payerBirthDay);
        shortNameTagsCorrelation.put(PAYERBIRTHDATE_SHORT, payerBirthDay);
        
        Element payAddr =new Element() {
            public void setValue(PaymentData paymentData, String value) {
                paymentData.setPayerAddress(value);
            }
        }; 
        shortNameTagsCorrelation.put(PAYERADDRESS_SHORT, payAddr);
        shortNameTagsCorrelation.put(PAYERADDRESS_SHORT.toUpperCase(), payAddr);
        
        Element payerFio = new Element() {
            public void setValue(PaymentData paymentData, String value) {
                paymentData.setPayerInfo(value);
            }
        };
        shortNameTagsCorrelation.put(PAYERFIO_SHORT, payerFio);
        shortNameTagsCorrelation.put(PAYERFIO_SHORT.toUpperCase(), payerFio);
        
        Element payerInfoProcessor = new Element() {
            public void setValue(PaymentData paymentData, String value) {
                if (paymentData.getPayerInfo() != null) {
                    paymentData.setPayerInfo(paymentData.getPayerInfo() + " " + value);
                } else {
                    paymentData.setPayerInfo(value);
                }
            }
        }; 
        shortNameTagsCorrelation.put(PAYERFIRSTNAME_SHORT, payerInfoProcessor);
        shortNameTagsCorrelation.put(PAYERFIRSTNAME_SHORT.toUpperCase(), payerInfoProcessor);
        shortNameTagsCorrelation.put(PAYERMIDDLENAME_SHORT, payerInfoProcessor);
        shortNameTagsCorrelation.put(PAYERMIDDLENAME_SHORT.toUpperCase(), payerInfoProcessor);
        shortNameTagsCorrelation.put(PAYERLASTNAME_SHORT, payerInfoProcessor);
        shortNameTagsCorrelation.put(PAYERLASTNAME_SHORT.toUpperCase(), payerInfoProcessor);
        shortNameTagsCorrelation.put(PAYERMIDDLENAME_SHORT_I, payerInfoProcessor);
        shortNameTagsCorrelation.put(PAYERMIDDLENAME_SHORT_I.toUpperCase(), payerInfoProcessor);
        shortNameTagsCorrelation.put(PAYERFIRSTNAME_SHORT_I, payerInfoProcessor);
        shortNameTagsCorrelation.put(PAYERFIRSTNAME_SHORT_I.toUpperCase(), payerInfoProcessor);
        
        /*
         * Налоговые поля
         */
        Element tDocDate = new Element() {
            public void setValue(PaymentData paymentData, String value) {
                paymentData.setTaxesDocumentDate(value);
            }
        };
        shortNameTagsCorrelation.put(TAXESDOCUMENTDATE_SHORT, tDocDate);
        shortNameTagsCorrelation.put(TAXESDOCUMENTDATE_SHORT.toUpperCase(), tDocDate);
        
        Element tDocNumber = new Element() {
            public void setValue(PaymentData paymentData, String value) {
                paymentData.setTaxesDocumentNumber(value);
            }
        };
        shortNameTagsCorrelation.put(TAXESDOCUMENTNUMBER_SHORT, tDocNumber);
        shortNameTagsCorrelation.put(TAXESDOCUMENTNUMBER_SHORT.toUpperCase(), tDocNumber);
        
        Element tDocType = new Element() {
            public void setValue(PaymentData paymentData, String value) {
                paymentData.setTaxesDocumentType(value);
            }
        };
        shortNameTagsCorrelation.put(TAXESDOCUMENTTYPE_SHORT, tDocType);
        shortNameTagsCorrelation.put(TAXESDOCUMENTTYPE_SHORT.toUpperCase(), tDocType);
        
        Element tPeriod = new Element() {
            public void setValue(PaymentData paymentData, String value) {
                paymentData.setTaxesPeriod(value);
            }
        };
        shortNameTagsCorrelation.put(TAXESPERIOD_SHORT, tPeriod);
        shortNameTagsCorrelation.put(TAXESPERIOD_SHORT.toUpperCase(), tPeriod);
        
        Element tReason = new Element() {
            public void setValue(PaymentData paymentData, String value) {
                paymentData.setTaxesReason(value);
            }
        };
        shortNameTagsCorrelation.put(TAXESREASON_SHORT, tReason);
        shortNameTagsCorrelation.put(TAXESREASON_SHORT.toUpperCase(), tReason);
        
        Element addReq = new Element() {
            public void setValue(PaymentData paymentData, String value) {
                paymentData.setAdditionalRequisites(value);
            }
        };
        shortNameTagsCorrelation.put(ADDITIONAL_REQUISITES_SHORT, addReq);
        shortNameTagsCorrelation.put(ADDITIONAL_REQUISITES_SHORT.toUpperCase(), addReq);
        
        Element vsp = new Element() {
            public void setValue(PaymentData paymentData, String value) {
                paymentData.setFilial(value);
            }
        };
        shortNameTagsCorrelation.put(REAL_VSP_SHORT, vsp);
        shortNameTagsCorrelation.put(REAL_VSP_SHORT.toUpperCase(), vsp);
    }

    public final void setMainRequisiteValue(PaymentData paymentData, String tag, String value) {
        Element element = (Element) tagsCorrelation.get(tag);
        if (element != null) {
            element.setValue(paymentData, value);
        }
    }
    
    public final void setShortNameValue(PaymentData paymentData, String tag, String value) {
        Element element = (Element) shortNameTagsCorrelation.get(tag);
        if (element != null) {
            element.setValue(paymentData, value);
        }
    }
}
