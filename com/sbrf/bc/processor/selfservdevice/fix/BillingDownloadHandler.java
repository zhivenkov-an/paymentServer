package com.sbrf.bc.processor.selfservdevice.fix;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.xml.sax.SAXException;

//import com.epam.sbrf.bc.data.BrakValue;
import com.epam.sbrf.bc.data.PaymentData;
import com.sberbank.sbclients.util.dao.ConnectionSource;
import com.sberbank.sbclients.util.dao.db2.DAOUtil;
import com.sbrf.bc.payments.PaymentFormatter;
import com.sbrf.bc.plugin.BillingPluginFactory;
import com.sbrf.bc.plugin.PluginNotFoundException;
import com.sbrf.bc.processor.FileMetadata;
import com.sbrf.bc.processor.RegistryContext;
import com.sbrf.bc.processor.ZipOrFileGroupOutputStream;
import com.sbrf.util.io.CrlfPrintWriter;
import com.sbrf.util.sql.group.DefaultGroupHandler;
import com.sbrf.util.sql.group.GroupFieldDescription;
import com.sbrf.util.sql.group.SelectorException;
import com.sbrf.util.text.LightDateFormat;
import com.sbrf.util.text.NamedMessageFormat;

public class BillingDownloadHandler extends DefaultGroupHandler {
    private final BillingDownloadProcessorConfig config;
    private final RegistryContext context;
    private final CrlfPrintWriter output;
    private final CrlfPrintWriter warnings;
    private final Logger logger;
    private final Date downloadDate;
    private final Date currDate;
    private final DateFormat paymentServerDateFormat;
    private final DateFormat paymentServerTimeFormat;
    
    private final SelfServiceTagsPlugin documentTags;
    private ZipOrFileGroupOutputStream zipStream;
    private final PaymentToXmlWriter paymentWriter;
    
    private PaymentData paymentData;
    BCPaymentData bcPaymentData;
    private Map<String, String> additionalRequisites;
    private Map<String, Object> transactionInfo;
    private Map<String, Object> paymentInfo;
    private Map<String, Object> parametersForFileNames;

    private String paymentDestinationFake;
    private int regLine;
    private int ignoredBrak;
    private int blockPaymentsCount;
    private int totalPaymentsCount;
    private int blockEntryCount;
    private List<FileMetadata> serializedPaymentFiles;
    
    private int indexInResultSet;

    public BillingDownloadHandler(ConnectionSource connectionSource, BillingDownloadProcessorConfig config, RegistryContext registryContext, CrlfPrintWriter outputWriter, CrlfPrintWriter warningsWriter, Logger logger, String login, Date downloadDate) throws PluginNotFoundException {
        this.config = config;
        this.context = registryContext;
        this.output = outputWriter;
        this.warnings = warningsWriter;
        this.logger = logger;
        this.downloadDate = downloadDate;
        this.currDate = Calendar.getInstance().getTime();
        this.paymentServerDateFormat = new LightDateFormat(config.paymentServerDateFormat);
        this.paymentServerTimeFormat = new LightDateFormat(config.paymentServerTimeFormat);
        BillingPluginFactory billingPluginFactory = new BillingPluginFactory();
        documentTags = (SelfServiceTagsPlugin) billingPluginFactory.findPlugin(config.xmlDocumentFormatTagsPlugin);
        this.paymentWriter = new PaymentToXmlWriter(documentTags);
        parametersForFileNames = new HashMap<String, Object>();
        parametersForFileNames.put("currentDate", currDate);
        parametersForFileNames.put("paymentDate", downloadDate);
        
        serializedPaymentFiles = new ArrayList<FileMetadata>();
        paymentInfo = new TreeMap<String, Object>();
        transactionInfo = new TreeMap<String, Object>();
        indexInResultSet = 1;
    }
    
    @Override
    public void start() throws SelectorException {
        output.println("Дата проведения платежей: " + downloadDate);
    }
    
    @Override
    public void startField(GroupFieldDescription description, Object value, ResultSet resultSet) throws SelectorException {
        paymentData = new PaymentData();
        additionalRequisites = new TreeMap<String, String>();
        paymentDestinationFake = null;

        paymentInfo.clear();
        transactionInfo.clear();
        indexInResultSet = 1;
        try {
            indexInResultSet = populateCommonData(resultSet, indexInResultSet);
        if ("NOOSB".equals(description.getName())) {
            blockEntryCount++;
            parametersForFileNames.put("noosb", DAOUtil.getString(resultSet, "NOOSB"));
            parametersForFileNames.put("blockEntryNumber", Integer.toString(blockEntryCount));
            startOutputBlockStream(parametersForFileNames);
        } else if ("LINUM".equals(description.getName())) {
            paymentData.setAdditionalRequisites("");
            /*
             * Разделение файлов на блоки, содержащие
             * BillingDownloadProcessorConfig.limitPaymentsInFile
             */
            if (blockPaymentsCount >= config.limitPaymentsInFile) {
                endOutputBlockStream();
                blockEntryCount++;
                parametersForFileNames.put("blockEntryNumber", Integer.toString(blockEntryCount));
                startOutputBlockStream(parametersForFileNames);
            }
        }
        } catch (SQLException e) {
            throw new SelectorException(e);
        }

    }

    @Override
    public void nextRecord(ResultSet resultSet) throws SelectorException {
        try {
            // здесь получаем только допсвойства
            regLine++;
            int indexInResultSetAddProps = indexInResultSet;
            String requisiteShortName = DAOUtil.getString(resultSet, indexInResultSetAddProps++);
            Object value = DAOUtil.getString(resultSet, indexInResultSetAddProps++);
            if ((requisiteShortName != null) && (value != null)) {
                documentTags.setShortNameValue(paymentData, requisiteShortName, value.toString());
                paymentDestinationFake = paymentDestinationFake + value.toString();
                additionalRequisites.put(requisiteShortName, value.toString());
            }
        } catch (SQLException e) {
            logger.log(Level.INFO, "NextRecord: Ошибка при чтении доп.параметра. платежа. Строка набора данных " + regLine, e);
        }
    }

    @Override
    public void endField(GroupFieldDescription description, Object value) throws SelectorException {
        try {
            if ("NOOSB".equals(description.getName())) {
                endOutputBlockStream();
                blockEntryCount = 0;
            } else if ("LINUM".equals(description.getName())) {
                additionalRequisites.put(documentTags.SERVICEKIND_SHORT, paymentData.getServiceKind());
                additionalRequisites.put(documentTags.TRANSACTION_TERMINAL_NUMBER, bcPaymentData.getNocas());
                additionalRequisites.put(documentTags.TRANSACTION_OPERATION_TIME, paymentServerTimeFormat.format(bcPaymentData.getTimep()));
                additionalRequisites.put(documentTags.TRANSACTION_PAYMENT_DATE, paymentServerDateFormat.format(bcPaymentData.getTimep()));
                additionalRequisites.put(documentTags.KPP_SHORT, bcPaymentData.getKpp());
                additionalRequisites.put(documentTags.KBK_SHORT, bcPaymentData.getKbk());
                additionalRequisites.put(documentTags.OKATO_SHORT, bcPaymentData.getOkato());
                additionalRequisites.put(documentTags.PROVIDER_INFO_SHORT, bcPaymentData.getProviderInfo());
                if (config.usePlinfAsAdditionalRequisites) {
                    additionalRequisites.put(documentTags.ADDITIONAL_REQUISITES_SHORT, bcPaymentData.getPlinf());
                }
                
                if (additionalRequisites.get(documentTags.PAYMENTDESTINATION_SHORT) == null 
                        && additionalRequisites.get(documentTags.PAYMENTDESTINATION_SHORT.toUpperCase()) == null) {
                    additionalRequisites.put(documentTags.PAYMENTDESTINATIONFAKE_SHORT, paymentDestinationFake);
                }
                if (paymentInfo.get(documentTags.PAYMENT_DOCUMENT_NUMBER) != null) {
                    blockPaymentsCount++;
                    totalPaymentsCount++;
                    transferFromParametersToPayment();
                    paymentWriter.startTransaction(transactionInfo);
                    paymentWriter.startPayment(paymentInfo);
                    paymentWriter.makeProperties(additionalRequisites);
                    paymentWriter.endPayment();
                    paymentWriter.endTransaction();
                } else {
                    output.println("Платеж в XML не сериализован, т.к. основные параметры не заполнены. Строка набора данных " + regLine);
                    logger.log(Level.WARNING, "Платеж в XML не сериализован, т.к. основные параметры не заполнены. Строка набора данных " + regLine);
                }
                additionalRequisites.clear();
                paymentDestinationFake = "";
            }
        } catch (SAXException e) {
            output.println("Ошибка при закрытии файлов выгрузки из Billing");
            logger.log(Level.SEVERE, "Ошибка при закрытии файлов выгрузки из Billing", e);
        }
    }

    @Override
    public void end() throws SelectorException {
        blockEntryCount = 0;
    }

    @Override
    public void cleanup() {
        output.println("         Прочитано из базы Billing :" + totalPaymentsCount);
        if (!config.uploadIntoDB) {
            output.println();
            output.println("В текущей настройке прямая загрузка не происходит. Создаются XML файлы с платежами.");
            output.println();
        }
        if (config.ignoreBrak) {
            output.println();
            output.println("В текущей настройке бракованные платежи игнорируются и не учавствуют в загрузке.");
            output.println("   Обнаружено бракованных платежей :" + ignoredBrak);
        }
        warnings.close();
        output.close();
        ignoredBrak = 0;

    }

    public void error(SelectorException exception) throws SelectorException {
        throw exception;
    }

    public void fatal(SelectorException exception) throws SelectorException {
        throw exception;
    }

    private void printMessage(PrintWriter outputWriter, String message, int lineNumber) {
        outputWriter.print(message + " ");
        printPaymentInfo(output, lineNumber);
    }

    private void printPaymentInfo(PrintWriter outputWriter, int lineNumber) {
        if (paymentData != null) {
            outputWriter.print("Строка " + lineNumber + ". ");
            outputWriter.println("Информация о платеже: " + PaymentFormatter.formatKey(paymentData.getPaymentDate(), paymentData.getOriginalOsb(), paymentData.getFilial(), paymentData.getDocumentNumber(), paymentData.getPaymentType()));
        }
    }

    public List<FileMetadata> getPaymentFiles() {
        return serializedPaymentFiles;
    }

    private void collectStreamFiles() {
        try {
            FileMetadata[] files = zipStream.getFilesMetadata();
            for (int i = 0; i < files.length; i++) {
                serializedPaymentFiles.add(files[i]);
            }
        } catch (RuntimeException e) {
            output.println("Ошибка при получении файла для передачи клиенту.");
            logger.log(Level.SEVERE, "Ошибка при получении файла для передачи клиенту.", e);
        }
    }

    private void startOutputBlockStream(Map<String, Object> parameters) throws SelectorException {
        output.println();
        output.println("Запись платежей в файл " + NamedMessageFormat.format(config.zipBlockOutputFileNameFormat, parameters));
        blockPaymentsCount = 0;
        try {
            zipStream = ZipOrFileGroupOutputStream.makeStream(true, true, config.outputDirectory);
            zipStream.start(NamedMessageFormat.format(config.zipOutputFileNameFormat, parameters));
            zipStream.startBlock(NamedMessageFormat.format(config.zipBlockOutputFileNameFormat, parameters));
            paymentWriter.init(zipStream, config.outputEncoding, config.indentXML);
            paymentWriter.startDocument(transactionInfo);
        } catch (IOException e) {
            output.println("Ошибка при инициализации потока записи XML файла.");
            logger.log(Level.SEVERE, "Ошибка при инициализации потока записи XML файла", e);
            throw new SelectorException(e);
        } catch (SAXException e) {
            output.println("Ошибка при попытке записи начала XML.");
            logger.log(Level.SEVERE, "Ошибка при попытке записи начала XML.", e);
            throw new SelectorException(e);
        }
    }

    private void endOutputBlockStream() {
        try {
            paymentWriter.endDocument();
            paymentWriter.close();
        } catch (SAXException e) {
            output.println("Ошибка при попытке завершения XML.");
            logger.log(Level.SEVERE, "Ошибка при попытке завершения начала XML.", e);
        }
        try {
            zipStream.endBlock();
            zipStream.stop();
        } catch (IOException e) {
            output.println("Ошибка при закрытии потока записи XML файла.");
            logger.log(Level.SEVERE, "Ошибка при закрытии потока записи XML файла.", e);
        }
        collectStreamFiles();
        output.println("Файл содержит " + blockPaymentsCount + " платежей");
        output.println("------------------------------------------------------------------------------");
        blockPaymentsCount = 0;
    }

    private void transferFromParametersToPayment() {
        Object temp = null;
        //Транзакция
        temp = additionalRequisites.get(documentTags.TRANSACTION_TRN_ID);
        if (temp != null) {
            paymentInfo.put(documentTags.TRANSACTION_TRN_ID, temp);
        }
        temp = additionalRequisites.get(documentTags.TRANSACTION_TRN_ID.toUpperCase());
        if (temp != null) {
            paymentInfo.put(documentTags.TRANSACTION_TRN_ID, temp);
        }
        temp = additionalRequisites.get(documentTags.TRANSACTION_CARD_NUMBER);
        if (temp != null) {
            paymentInfo.put(documentTags.TRANSACTION_CARD_NUMBER, temp);
        }
        temp = additionalRequisites.get(documentTags.TRANSACTION_CARD_NUMBER.toUpperCase());
        if (temp != null) {
            paymentInfo.put(documentTags.TRANSACTION_CARD_NUMBER, temp);
        }
        temp = additionalRequisites.get(documentTags.TRANSACTION_AUTH_CODE);
        if (temp != null) {
            paymentInfo.put(documentTags.TRANSACTION_AUTH_CODE, temp);
        }
        temp = additionalRequisites.get(documentTags.TRANSACTION_AUTH_CODE.toUpperCase());
        if (temp != null) {
            paymentInfo.put(documentTags.TRANSACTION_AUTH_CODE, temp);
        }
        //Платеж
        temp = additionalRequisites.get(documentTags.REAL_PAYMENT_ID_SHORT);
        if (temp != null) {
            paymentInfo.put(documentTags.PAYMENT_DOCUMENT_NUMBER, temp);
        }
        temp = additionalRequisites.get(documentTags.REAL_PAYMENT_ID_SHORT.toUpperCase());
        if (temp != null) {
            paymentInfo.put(documentTags.PAYMENT_DOCUMENT_NUMBER, temp);
        }
        // Удаление фиктивного параметра PAYMENTDESTINATION перед записью
        // платежа в файл
        additionalRequisites.remove(documentTags.PAYMENTDESTINATIONFAKE_SHORT);

    }

    @SuppressWarnings("boxing")
    private int populateCommonData(ResultSet resultSet, int index) throws SelectorException {
        try {
            bcPaymentData = new BCPaymentData(null); 
            int innerIndex = BCPaymentData.populate(bcPaymentData, resultSet, index);
            
            paymentData = convertBCIntoPSPaymentData();
            
            paymentData.setReceiverName(DAOUtil.getString(resultSet, innerIndex++));
            paymentData.setReceiverNumber(DAOUtil.getString(resultSet, innerIndex++));
            paymentData.setSpecialClientCode(DAOUtil.getString(resultSet, innerIndex++));
            
            
            // Заполнение для сериализации в XML файл
            transactionInfo.put(documentTags.STARTDATE, bcPaymentData.getDatep());
            transactionInfo.put(documentTags.ENDDATE, bcPaymentData.getDatep());
            transactionInfo.put(documentTags.SOURCE_SERVER, "0000");

            transactionInfo.put(documentTags.TRANSACTION_TRN_ID_REM, "0");
            transactionInfo.put(documentTags.TRANSACTION_SRVCODE, "0");
            transactionInfo.put(documentTags.TRANSACTION_PAYMENT_TYPE, bcPaymentData.getTypla());
            transactionInfo.put(documentTags.TRANSACTION_CASHIER, bcPaymentData.getNocas());
            transactionInfo.put(documentTags.TRANSACTION_OSB, bcPaymentData.getNoosb());
            transactionInfo.put(documentTags.REAL_VSP_SHORT, bcPaymentData.getNofil());
            transactionInfo.put(documentTags.TRANSACTION_COD_TB, "");
            transactionInfo.put(documentTags.TRANSACTION_PAYMENT_DATE, bcPaymentData.getDatep());
            transactionInfo.put(documentTags.TRANSACTION_OPERATION_TIME, bcPaymentData.getTimep());
            transactionInfo.put(documentTags.TRANSACTION_SUM, new Integer(0));
            transactionInfo.put(documentTags.TRANSACTION_NEED_AMMOUNT, new Integer(0));
            transactionInfo.put(documentTags.TRANSACTION_DEVICE_NUMBER, "0");

            paymentInfo.put(documentTags.PAYMENT_DOCUMENT_NUMBER, bcPaymentData.getNpdoc());
            paymentInfo.put(documentTags.PAYMENT_SUM, bcPaymentData.getSumpl());
            paymentInfo.put(documentTags.PAYMENT_AMMOUNT_OVER, new Integer(0));
            paymentInfo.put(documentTags.PAYMENT_PAYER_COMMISSION, bcPaymentData.getPayerCommission());
            paymentInfo.put(documentTags.PAYMENT_RESDOC, "1");
            paymentInfo.put(documentTags.PAYMENT_BC_TRN, new Integer(0));
            paymentInfo.put(documentTags.PAYMENT_RECEIVER_NAME, paymentData.getReceiverName());
            paymentInfo.put(documentTags.PAYMENT_RECEIVER_NUMBER, paymentData.getReceiverNumber());
            paymentInfo.put(documentTags.PAYMENT_SPECIAL_CLIENT_CODE, paymentData.getSpecialClientCode());
            paymentInfo.put(documentTags.PAYMENT_BIK, bcPaymentData.getBik());
            paymentInfo.put(documentTags.PAYMENT_CORR_ACC, bcPaymentData.getCorrAcc());
            paymentInfo.put(documentTags.PAYMENT_SETTLE_ACC, bcPaymentData.getSettleAcc());
            paymentInfo.put(documentTags.PAYMENT_INN, bcPaymentData.getInn());
            paymentInfo.put(documentTags.PAYMENT_PERSONAL_ACCOUNT, "");
            paymentInfo.put(documentTags.PAYMENT_PERSENT, "");
            paymentInfo.put(documentTags.PAYMENT_AMMOUNTCOMMMIN, new Integer(0));
            paymentInfo.put(documentTags.PAYMENT_AMMOUNTCOMMMAX, new Integer(0));
            paymentInfo.put(documentTags.PAYMENT_DOGOVOR, "");
            paymentInfo.put(documentTags.PAYMENT_PAYER_INFO, "");
            paymentInfo.put(documentTags.PAYMENT_STREET, "");
            paymentInfo.put(documentTags.PAYMENT_HOUSE, "");
            paymentInfo.put(documentTags.PAYMENT_BUILDING, "");
            paymentInfo.put(documentTags.PAYMENT_FLAT, "");
            paymentInfo.put(documentTags.PAYMENT_PAYER_ACC, "");
            paymentInfo.put(documentTags.PAYMENT_PAYMENT_DESTINATION, "");
            
            return innerIndex;
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Ошибка чтения данных из Billing", e);
            throw new SelectorException(e);
        }
    }
    
    private PaymentData convertBCIntoPSPaymentData() {
        PaymentData result = new PaymentData();
        
        result.setPaymentDate(bcPaymentData.getDatep());
        result.setOsb(bcPaymentData.getNoosb());
        result.setOriginalOsb(bcPaymentData.getNoosb());
        result.setFilial(bcPaymentData.getNofil());
        result.setCashier(bcPaymentData.getNocas());
        result.setDocumentNumber(bcPaymentData.getNpdoc());
        result.setPaymentType(bcPaymentData.getTypla());
        result.setSum(bcPaymentData.getSumpl());
        result.setPayerCommission(bcPaymentData.getPayerCommission());
        result.setInRegId(context.getRegistryId());
        
        result.setServiceKind(Integer.toString(bcPaymentData.getNomus().intValue()));
        result.setInRegLine(regLine);
        
        
        result.setAdditionalRequisites(bcPaymentData.getPlinf());
        result.setBik(bcPaymentData.getBik());
        result.setCorrAcc(bcPaymentData.getCorrAcc());
        result.setSettleAcc(bcPaymentData.getSettleAcc());
        result.setInn(bcPaymentData.getInn());
        result.setKpp(bcPaymentData.getKpp());
        result.setOkato(bcPaymentData.getOkato());
        
        return result;
    }

    private void printPaymentInfo(PrintWriter outputWriter) {
        if (paymentData != null) {
            outputWriter.println("Информация о платеже: " + PaymentFormatter.formatKey(paymentData.getPaymentDate(), paymentData.getOriginalOsb(), paymentData.getFilial(), paymentData.getDocumentNumber(), paymentData.getPaymentType()));
        }
    }

}
