package com.sbrf.bc.processor.analitica;

import java.util.Map;
import java.util.TreeMap;

/**
 * Формат написания дополнительных параметров и их ID для формирования проводок в АРМ Аналитика.
 * @author Usenko-VA
 *
 */
public class AnaliticaFieldFormat {
    
    /*
     * Синтетические реквизиты и реквизиты для формировани имен файлов
     */
    public static final String debet = "debet";
    public static final String credit = "credit";
    public static final String sum = "sum";
    public static final String operDay = "operDay";
    public static final String linum = "linum";
    public static final String osb = "osb";
    public static final String filial = "filial";
    public static final String transactionComment = "transactionComment";
    public static final String fileNumber = "fileNumber";
    /*
     * Аналитические реквизиты и их алиасы
     */
    public static final String osbNumber = "osbNumber";
    public static final String osbNumberFormat = "2={0}";
    
    public static final String kvitCenter = "kvitCenter";
    public static final String kvitCenterFormat = "3={0}";
    
    public static final String checkNumber = "checkNumber";
    public static final String checkNumberFormat = "4={0}";
    
    public static final String paymentDemandNum = "paymentDemandNum";
    public static final String paymentDemandNumFormat = "5={0}";
    
    public static final String documentType = "documentType";
    public static final String documentTypeFormat = "6={0}";
    
    public static final String documentNumber = "documentNumber";
    public static final String documentNumFormat = "7={0}";
    
    public static final String paymentDocNumber = "paymentDocNumber";
    public static final String paymentDocNumberFormat = "8={0}";
    
    public static final String checkSeries = "checkSeries";
    public static final String checkSeriesFormat = "12={0}";
        
    public static final String filialNumber = "filialNumber";
    public static final String filialNumberFormat = "13={0}";
    
    public static final String text1 = "text1";
    public static final String text1Format = "14={0,text,limit = 40}";
    
    public static final String text2 = "text2";
    public static final String text2Format = "15={0,text,limit = 40}";
    
    public static final String plasticCardNum = "plasticCardNum";
    public static final String plasticCardNumFormat = "16={0}";
    
    public static final String clientName = "clientName";
    public static final String clientNameFormat = "17={0}";
    
    public static final String reservField = "reservField";
    public static final String reservFieldFormat = "18={0}";
    
    public static final String transactionName = "transactionName";
    public static final String transactionNameFormat = "19={0}";
    
    public static final String paymentDateBegin = "paymentDateBegin";
    public static final String paymentDateBeginFormat = "20={0,date,dd.MM.yyyy}";
    
    public static final String paymentDateEnd = "paymentDateEnd";
    public static final String paymentDateEndFormat = "21={0,date,dd.MM.yyyy}";
    
    public static final String documentCreationDate = "documentCreationDate";
    public static final String documentCreationDateFormat = "22={0,date,dd.MM.yyyy}";
    
    public static final String text3 = "text3";
    public static final String text3Format = "23={0,text,limit = 40}";
    
    public static final String text4 = "text4";
    public static final String text4Format = "24={0,text,limit = 40}";
    
    public static final String text5 = "text5";
    public static final String text5Format = "25={0,text,limit = 40}";
    
    public static final String percent = "percent";
    public static final String percentFormat = "26={0,number,percent}";
    
    public static final String obligationName = "obligationName";
    public static final String obligationNameFormat = "27={0,text,limit=40}";
    
    public static final String receiverName = "receiverName";
    public static final String receiverNameFormat = "28={0,text,limit=40}";
    
    public static final String payerAccount = "payerAccount";
    public static final String payerAccFormat = "31={0,text,fill=' ';width=25;align=left}";
    
    public static final String receiverAccount = "receiverAccount";
    public static final String receiverAccountFormat = "32={0,text,fill=' ';width=25;align=left}";
    
    public static final String coinName = "coinName";
    public static final String coinNameFormat = "33={0,text,limit=40}";
    
    public static final String transactingType = "transactiongType";
    public static final String transactingTypeFormat = "34={0,text,limit=35}";
    
    public static final String obligationNameNoNominal = "obligationNameNoNominal";
    public static final String obligationNameNoNominalFormat = "35={0,text,limit=40;}";
    
    public static final String obligationNameNominal = "obligationNameNominal";
    public static final String obligationNameNominalFormat = "36={0,text,limit=40;}";
    
    public static final String documentQuantity = "documentQuantity";
    public static final String documentQuantityFormat = "37={0,number,integer}";
    
    public static final String receiverFilial = "receiverFilial";
    public static final String receiverFilialFormat = "38={0,number,integer}";
    
    //public static final String settleAccount = "settleAccount";
    //public static final String settleAccountFormat = "39={0,text,}"
    
    public static final String symbolDocumentNumber = "symbolDocumentNumber";
    public static final String symbolDocumentNumberFormat = "40={0,text,limit=20}";
    
    public static final String regDocNumber = "registrationDocumentNumber";
    public static final String regDocNumberFormat = "41={0,number,integer}";
    
    public static final String creationDate = "creationDate";
    public static final String creationDateFormat = "42={0,date,dd.MM.yyyy}";
    
    public static final String regOrderNumber = "regOrderNumber";
    public static final String regOrderNumberFormat = "43={0,number,integer}";
    
    public static final String orderNumber = "orderNumber";
    public static final String orderNumberFormat = "44={0,text,limit=12}";
    
    public static final String additionalSum = "additionalSum";
    public static final String additionalSumFormat = "45={0,number,cyrrency}";
    
    public static final String transactionDate = "transactionDate";
    public static final String transactionDateFormat = "46={0,date,dd.MM.yyyy}";
    
    public static final String terminalNumber = "terminalNumber";
    public static final String terminalNumberFormat = "47={0,text,limit=20}";
    
    public static final String transactionTime = "transactionTime";
    public static final String transactionTimeFormat = "48={0,date, HH:mm:ss}";
    
    public static final String orderRegistry = "orderRegistry";
    public static final String orderRegistryFormat = "49={0,text,limit=20}";
    
    public static final String NDSMark = "NDSMark";
    public static final String NDSMarkFormat = "50={0,text,limit=20}";
    
    public static final String currencyCode = "currencyCode";
    public static final String currencyCodeFormat = "51={0,text,limit=3}";
    
    public static final String spareRequisite = "spareRequisite"; 
    public static final String spareRequisiteFormat = "53={0}";
    
    public static final String authCode = "authorisationCode";
    public static final String authCodeFormat = "54={0,text,limit=10}";
    
    public static final String corrAccCurrency = "corrAccCurrency";
    public static final String corrAccCurrencyFormat = "55={0,text,limit=3}";
    
    public static final String depositType = "depositType";
    public static final String depositTypeFormat = "56={0,number,integer}";
    
    public static final String beginDate = "beginDate"; 
    public static final String beginDateFormat = "57={0,date,dd.MM.yyyy}";
    
    public static final String beginTime = "beginTime"; 
    public static final String beginTimeFormat = "58={0,date,HH:mm:ss}";
    
    public static final String commissionMark = "commissionMark";
    public static final String commissionMarkFormat = "59={0,text,limit=3}"; 
    
    public static final String transitionMark = "transitionMark";
    public static final String transitionMarkFormat = "60={0,text,limit=30}";
    
    public static final String sNumDoc = "sNumDoc";
    public static final String sNumDocFormat = "1001={0,text,limit=20}";
    
    public static final String idDoc = "idDoc";
    public static final String idDocFormat = "1002={0,text,limit=9}";
    
    public static final String idCarry = "idCarry";
    public static final String idCarryFormat = "1003={0,text,limit=9}";
    
    
    private static Map<String, String> fillingMap = new TreeMap<String, String>();
    
    static {
        
        fillingMap.put(osbNumber, osbNumberFormat);
        fillingMap.put(kvitCenter, kvitCenterFormat);
        fillingMap.put(checkNumber, checkNumberFormat);
        fillingMap.put(paymentDemandNum, paymentDemandNumFormat);
        fillingMap.put(documentType, documentTypeFormat);
        fillingMap.put(documentNumber, documentNumFormat);
        fillingMap.put(paymentDocNumber, paymentDocNumberFormat);
        fillingMap.put(checkSeries, checkSeriesFormat);
        fillingMap.put(filialNumber, filialNumberFormat);
        fillingMap.put(text1, text1Format);
        
        fillingMap.put(text2, text2Format);
        fillingMap.put(plasticCardNum, plasticCardNumFormat);
        fillingMap.put(clientName, clientNameFormat);
        fillingMap.put(reservField, reservFieldFormat);
        fillingMap.put(transactionName, transactionNameFormat);
        fillingMap.put(paymentDateBegin, paymentDateBeginFormat);
        fillingMap.put(paymentDateEnd, paymentDateEndFormat);
        fillingMap.put(documentCreationDate, documentCreationDateFormat);
        fillingMap.put(text3, text3Format);
        fillingMap.put(text4, text4Format);
        
        fillingMap.put(percent, percentFormat);
        fillingMap.put(obligationName, obligationNameFormat);
        fillingMap.put(receiverName, receiverNameFormat);
        fillingMap.put(payerAccount, payerAccFormat);
        fillingMap.put(receiverAccount, receiverAccountFormat);
        fillingMap.put(coinName, coinNameFormat);
        fillingMap.put(transactingType, transactingTypeFormat);
        fillingMap.put(obligationNameNoNominal, obligationNameNoNominalFormat);
        fillingMap.put(obligationNameNominal, obligationNameNominalFormat);
        fillingMap.put(documentQuantity, documentQuantityFormat);
        
        fillingMap.put(receiverFilial, receiverFilialFormat);
        fillingMap.put(symbolDocumentNumber, symbolDocumentNumberFormat);
        fillingMap.put(regDocNumber, regDocNumberFormat);
        fillingMap.put(creationDate, creationDateFormat);
        fillingMap.put(regOrderNumber, regOrderNumberFormat);
        fillingMap.put(orderNumber, orderNumberFormat);
        fillingMap.put(additionalSum, additionalSumFormat);
        fillingMap.put(transactionDate, transactionDateFormat);
        fillingMap.put(terminalNumber, terminalNumberFormat);
        fillingMap.put(transactionTime, transactionTimeFormat);
        
        fillingMap.put(orderRegistry, orderRegistryFormat);
        fillingMap.put(NDSMark, NDSMarkFormat);
        fillingMap.put(currencyCode, currencyCodeFormat);
        fillingMap.put(spareRequisite, spareRequisiteFormat);
        fillingMap.put(authCode, authCodeFormat);
        fillingMap.put(corrAccCurrency, corrAccCurrencyFormat);
        fillingMap.put(depositType, depositTypeFormat);
        fillingMap.put(beginDate, beginDateFormat);
        fillingMap.put(beginTime, beginTimeFormat);
        
        fillingMap.put(commissionMark, commissionMarkFormat);
        fillingMap.put(transitionMark, transitionMarkFormat);
        fillingMap.put(sNumDoc, sNumDocFormat);
        fillingMap.put(idDoc, idDocFormat);
        fillingMap.put(idCarry, idCarryFormat);
    }
   
    public static final String getFormat(String alias) {
        return fillingMap.get(alias);
    }

}
