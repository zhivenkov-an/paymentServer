package com.sbrf.bc.processor.analitica;

import java.io.IOException;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.NumberFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang.StringUtils;

import billing.util.SumConverter;

import com.epam.sbrf.bc.plugin.AccountFabric;
import com.epam.sbrf.bc.plugin.AccountFactory;
import com.epam.sbrf.bc.plugin.Route;
import com.epam.sbrf.exception.AccountException;
import com.sberbank.sbclients.util.dao.ConnectionSource;
import com.sberbank.sbclients.util.dao.db2.DAOUtil;
import com.sbrf.bc.nsi.BillingObjectTypes;
import com.sbrf.bc.plugin.BillingPluginFactory;
import com.sbrf.bc.plugin.PluginNotFoundException;
import com.sbrf.bc.processor.selfservdevice.SelfServiceTagsPlugin;
import com.sbrf.report.types.form277report.Form277ReportType;
import com.sbrf.util.io.CrlfPrintWriter;
import com.sbrf.util.sql.group.DefaultGroupHandler;
import com.sbrf.util.sql.group.GroupFieldDescription;
import com.sbrf.util.sql.group.SelectorException;
import com.sbrf.util.text.LightDateFormat;
import com.sbrf.util.text.MessageFormat;

/**
 * Данные, выгружаемые в этом плагине должны соответствовать данным из сводного мемориального ордера.
 * @see Form277ReportType 
 * @author Usenk-VA
 */
final class AnaliticaDownloadHandler extends DefaultGroupHandler {
    private final AnaliticaDownloadConfig config;
    private final ConnectionSource connectionSource;
    private final Date operDay;
    private final MessageFormat fileNameFormat;
    private final MessageFormat transactionFormat;
    private final BillingPluginFactory billingPluginFactory;
    private final AccountFabric accountFabric;
    private final Logger logger;
    private FileGroupOutputStream outputStream;
    private CrlfPrintWriter outputWriter;
    private String cardNumber;
    private String authorCode;
    private String transactionDate;
    private String deviceNumber;
    private String operationTime;
    private String code;
    private int counter;
    private final NumberFormat numberFormat;
    private boolean hasTrasaction;
    private SumConverter sumConverter = SumConverter.getInstance();
    private final Connection connToRgProps;
    private final String FIND_PROPS_SQL = "select NAME, VALUE from RG.PROPS where OBJECT_TYPE = ? and OBJECT_ID = ?";
    private final PreparedStatement findPropsStatement;
    private final LightDateFormat outputDate;
    private final Map<String, String> unnecessaryFieldsMap;
    private final Map<String, String> unnecessaryFieldsToSet;
    private final SelfServiceTagsPlugin documentTags;

    public AnaliticaDownloadHandler(AnaliticaDownloadConfig config, Logger logger, FileGroupOutputStream outputStream, ConnectionSource source, Date operDay) throws AccountException, PluginNotFoundException, SQLException {
        this.config = config;
        this.outputStream = outputStream;
        this.connectionSource = source;
        this.operDay = operDay;
        this.fileNameFormat = new MessageFormat(config.fileNameFormat);
        this.transactionFormat = new MessageFormat(config.transactionFormat);
        this.numberFormat = NumberFormat.getInstance(Locale.ENGLISH);
        this.logger = logger;
        numberFormat.setMaximumFractionDigits(2);
        numberFormat.setMinimumFractionDigits(2);
        this.billingPluginFactory = new BillingPluginFactory();
        AccountFactory accountFactory = (AccountFactory) billingPluginFactory.findPlugin(AccountFactory.PLUGIN_NAME);
        this.accountFabric = accountFactory.createNewAccountFabric(connectionSource);
        this.accountFabric.init();
        this.connToRgProps = connectionSource.getConnection();
        this.findPropsStatement = connToRgProps.prepareStatement(FIND_PROPS_SQL);
        this.counter = 0;
        this.outputDate = new LightDateFormat("dd.MM.yyyy");
        this.documentTags = (SelfServiceTagsPlugin) billingPluginFactory.findPlugin(config.xmlDocumentFormatTagsPlugin);
        
        unnecessaryFieldsMap = new HashMap<String, String>();
        unnecessaryFieldsToSet = new HashMap<String, String>();
        Pattern pattern = Pattern.compile("([\\w]+)=([\\w]+)");
        Matcher matcher = pattern.matcher(config.unnecessaryFieldsNames);
        while(matcher.find()) {
            String key = matcher.group(1);
            String value = matcher.group(2);
            unnecessaryFieldsMap.put(key, value);
        }
    }

    public void start() throws SelectorException {
        outputStream.start();
        counter = 0;
    }

    public void startField(GroupFieldDescription description, Object value, ResultSet resultSet) throws SelectorException {
        try {
            if (description.getName().equalsIgnoreCase("PART_CODE")) {
                String partCode = DAOUtil.getString(resultSet, "PART_CODE");
                String osb = DAOUtil.getString(resultSet, "OSB");
                counter++;
                String osbName;
                if (config.single) {
                    osbName = "ALL";
                } else {
                    osbName = "OSB" + osb;
                }
                String name = fileNameFormat.format(new Object[] { operDay, new Integer(counter), partCode, osbName });
                outputStream.startBlock(name);
                outputWriter = new CrlfPrintWriter(outputStream, config.lineSeparator, config.outputEncoding);
            }
            if (description.getName().equalsIgnoreCase("LINUM")) {
                String addReq = DAOUtil.getString(resultSet, "ADDITIONAL_REQUISITES");
                hasTrasaction = false;
                code = "";
                cardNumber = "";
                authorCode = "";
                Date paymentDate = DAOUtil.getDate(resultSet, "PAYMENT_DATE");
                transactionDate = outputDate.format(paymentDate);
                deviceNumber = "";
                operationTime = config.defaultOperationTime;
                if (config.useRgProps) {
                    getRequisitesFromDb(DAOUtil.get_long(resultSet, "LINUM"));
                    if (config.useAllRequisites){
                        disassembleRequisit(addReq);
                    }
                } else {
                    disassembleRequisit(addReq);
                }
                if (config.useRgPropsForReceiver) {
                    getRequisitesFromDbForReceiver(DAOUtil.get_long(resultSet, "RECEIVER_NUMBER"));
                }
                if (config.excludeDeviceInfoFromCards) {
                    removeDeviceInfoFromCards();
                }
            }
        } catch (IOException e) {
            SelectorException exception = new SelectorException(e);
            logger.throwing(this.getClass().getName(), "startField", exception);
            throw exception;
        } catch (SQLException e) {
            SelectorException exception = new SelectorException(e);
            logger.throwing(this.getClass().getName(), "startField", exception);
            throw exception;
        }
    }

    public void nextRecord(ResultSet resultSet) throws SelectorException {
        try {
            String route = DAOUtil.getString(resultSet, "ROUTE");
            Date transferDate = DAOUtil.getDate(resultSet, "TRANSFER_DATE");
            String paymentOrder = "";
            String order277 = "";
            String debet = DAOUtil.getString(resultSet, "DEBET");
            long longSum = DAOUtil.get_long(resultSet, "SUM");
            long longServComm = DAOUtil.get_long(resultSet, "SERVICE_COMMISSION");
            long longPayComm = DAOUtil.get_long(resultSet, "PAYER_COMMISSION");
            if (debet == null) {
                debet = DAOUtil.getString(resultSet, "TEL");
            }
            String credit = "";
            String transferName = "";
            BigDecimal sum;
            String linkType = DAOUtil.getString(resultSet, "LINK_TYPE");
            if (linkType == null) {
                linkType = "";
            }
            if (linkType.equals("")||(linkType.equals("MFR"))){
                if (route.equals(Route.INTERNAL)) {
                    code = config.participantCode;
                    paymentOrder = DAOUtil.getString(resultSet, "PAYMENT_ORDER");
                    credit = DAOUtil.getString(resultSet, "SETTLE_ACC");
                } else {
                    order277 = Long.toString(DAOUtil.get_long(resultSet, "MEM_ORDER_NUM") + config.inDoc);
                    credit = DAOUtil.getString(resultSet, "CREDIT");
                }
                hasTrasaction = true;
                transferName = "ПЕРЕЧИСЛЕНЫ ПЛАТЕЖИ С ПЛАСТИКОВЫХ КАРТ(РКО)";
                sum = sumConverter.toBigDecimal(longSum);
                BigDecimal transSum = sum.subtract(sumConverter.toBigDecimal(longServComm));
                printData(new Object[] { code, paymentOrder, order277, transferDate, debet, credit, transferName, transSum, cardNumber, authorCode, transactionDate, deviceNumber, operationTime });
            }

            if ((linkType.equals("RECEIV_DOGOV"))||(linkType.equals("RECEIV_NOT_DOGOV"))){
                makeInternalTransferLine(debet, DAOUtil.getString(resultSet, "SETTLE_ACC"), DAOUtil.getString(resultSet, "PAYMENT_ORDER"), route, transferDate, longSum, longServComm);
                if (DAOUtil.getLong(resultSet, "SERVICE_COMMISSION").longValue() != 0) {
                    if (DAOUtil.getInt(resultSet, "RECEIVER_NUMBER").longValue() == 0) {
                        transferName = "Комиссия с получателя за переводы (списания) средств физических лиц на счета юридических лиц при отсутствии договора с юридическим лицом";
                    } else {
                        transferName = "Комиссия с получателя за переводы (списания) средств физических лиц на счета юридических лиц при наличии договора с юридическим лицом";
                    }
                    credit = DAOUtil.getString(resultSet, "CREDIT");
                    order277 = Long.toString(DAOUtil.get_long(resultSet, "MEM_ORDER_NUM") + config.inDoc);
                    sum = sumConverter.toBigDecimal(longServComm);
                    printData(new Object[] { code, paymentOrder, order277, transferDate, debet, credit, transferName, sum, cardNumber, authorCode, transactionDate, deviceNumber, operationTime });
                }
            }
            // комисия с плательщика
            if ((linkType.equals("PAYER_DOGOV"))|| (linkType.equals("PAYER_NOT_DOGOV")) || (linkType.equals("PAYER_CARD"))){
                makeInternalTransferLine(debet, DAOUtil.getString(resultSet, "SETTLE_ACC"), DAOUtil.getString(resultSet, "PAYMENT_ORDER"), route, transferDate, longSum, longServComm);
                if (DAOUtil.getLong(resultSet, "PAYER_COMMISSION").longValue() != 0) {
                    if (DAOUtil.getInt(resultSet, "RECEIVER_NUMBER").intValue() == 0) {
                        transferName = "Комиссия с плательщика за переводы (списания) средств физических лиц на счета юридических лиц при отсутствии договора с юридическим лицом";
                    } else {
                        transferName = "Комиссия с плательщика за переводы (списания) средств физических лиц на счета юридических лиц при наличии договора с юридическим лицом";
                    }
                    credit = DAOUtil.getString(resultSet, "CREDIT");
                    order277 = Long.toString(DAOUtil.get_long(resultSet, "MEM_ORDER_NUM") + config.inDoc);
                    sum = sumConverter.toBigDecimal(longPayComm);
                    printData(new Object[] { code, paymentOrder, order277, transferDate, debet, credit, transferName, sum, cardNumber, authorCode, transactionDate, deviceNumber, operationTime });
                }
            }
        } catch (SQLException e) {
            SelectorException exception = new SelectorException(e);
            logger.throwing(this.getClass().getName(), "nextField", exception);
            throw exception;
        }
    }

    public void endField(GroupFieldDescription description, Object value) throws SelectorException {
        try {
            if (description.getName().equalsIgnoreCase("PART_CODE")) {
                outputWriter.close();
                outputStream.endBlock();
            }
        } catch (IOException e) {
            SelectorException exception = new SelectorException(e);
            logger.throwing(this.getClass().getName(), "endField", exception);
            throw exception;
        }
    }

    public void end() throws SelectorException {
        try {
            outputStream.stop();
        } catch (IOException e) {
            throw new SelectorException(e);
        }
    }

    public void cleanup() {
        DAOUtil.close(findPropsStatement);
        DAOUtil.close(connToRgProps);
    }

    public void error(SelectorException exception) throws SelectorException {
        try {
            outputStream.stop();
        } catch (IOException e) {
            throw new SelectorException(e);
        }
        throw exception;

    }

    public void fatal(SelectorException exception) throws SelectorException {
        try {
            outputStream.stop();
        } catch (IOException e) {
            throw new SelectorException(e);
        }
        logger.throwing(this.getClass().getName(), "fatal", exception);
        throw exception;
    }

    private void printData(Object[] data) {
        String line = transactionFormat.format(data);
        for (String key : unnecessaryFieldsToSet.keySet()) {
            line = line + "|" + key + "=" + unnecessaryFieldsToSet.get(key);
        }
        outputWriter.println(line);
    }
    
    private void makeInternalTransferLine(String debet, String credit, String paymentOrder, String route, Date transferDate , long sum, long serviceComm) {
        String order277 = "";
        if (!hasTrasaction) {
            if (route.equals(Route.INTERNAL)) {
                code = config.participantCode;
                hasTrasaction = true;
                String transferName = "ПЕРЕЧИСЛЕНЫ ПЛАТЕЖИ С ПЛАСТИКОВЫХ КАРТ(РКО)";
                BigDecimal transSum = sumConverter.toBigDecimal(sum - serviceComm);
                printData(new Object[] { code, paymentOrder, order277, transferDate, debet, credit, transferName, transSum, cardNumber, authorCode, transactionDate, deviceNumber, operationTime });
            }
        }
    }

    private void disassembleRequisit(String requisit) {
        Pattern pattern = Pattern.compile("\\d{4}");
        String groups[] = requisit.split("@",-1);
        for (int i = 0; i < groups.length; i++) {
            Matcher matcher = pattern.matcher(groups[i]);
            if (matcher.matches()) {
                int j;
                int length = 0;
                for (j = i; j < groups.length; j++) {
                    length = length + groups[j].length() + 1;
                }
                if (length == Integer.parseInt(groups[i])) {
                    if ( i + 2 < groups.length) {
                        cardNumber = groups[i + 2];
                    }
                    if ( i + 3 < groups.length) {
                        authorCode = groups[i + 3];
                    }
                    if ( i + 4 < groups.length) {
                        transactionDate = groups[i + 4];
                    }
                    if ( i + 5 < groups.length) {
                        deviceNumber = groups[i + 5];
                    }    
                    if ( i + 6 < groups.length) {
                        operationTime = groups[i + 6];
                    }    
                }

            }
        }
    }
    
    private void removeDeviceInfoFromCards() {
        if (StringUtils.isNotBlank(cardNumber)) {
            deviceNumber = "";
            operationTime = "";
        }
    }

    private void getRequisitesFromDb(long linum) throws SQLException {
        ResultSet resultSet = null;
        try {
            DAOUtil.setInt(findPropsStatement, 1, BillingObjectTypes.OFFLINE_PAYMENT);
            DAOUtil.setLong(findPropsStatement, 2, linum);
            resultSet = findPropsStatement.executeQuery();
            while (resultSet.next()) {
                String name = DAOUtil.getString(resultSet, "NAME");
                if (name.equals(documentTags.TRANSACTION_AUTH_CODE)) {
                    authorCode = DAOUtil.getString(resultSet, "VALUE");
                } else if (name.equals(documentTags.TRANSACTION_CARD_NUMBER)) {
                    cardNumber = DAOUtil.getString(resultSet, "VALUE");
                } else if (name.equals(documentTags.TRANSACTION_TERMINAL_NUMBER)) {
                    deviceNumber = DAOUtil.getString(resultSet, "VALUE");
                } else if (name.equals(documentTags.TRANSACTION_OPERATION_TIME)) {
                    operationTime = DAOUtil.getString(resultSet, "VALUE");
                } else if (name.equals(documentTags.TRANSACTION_OPERATION_DATE)) {
                    transactionDate = DAOUtil.getString(resultSet, "VALUE");
                }
            }
        } catch (SQLException e) {
            throw e;
        } finally {
            DAOUtil.close(resultSet);
        }

    }
    
    private void getRequisitesFromDbForReceiver(long linum) throws SQLException {
        unnecessaryFieldsToSet.clear();
        ResultSet resultSet = null;
        try {
            DAOUtil.setInt(findPropsStatement, 1, BillingObjectTypes.RECEIVER_ORGANIZATION);
            DAOUtil.setLong(findPropsStatement, 2, linum);
            resultSet = findPropsStatement.executeQuery();
            while (resultSet.next()) {
                String name = DAOUtil.getString(resultSet, "NAME");
                if (unnecessaryFieldsMap.containsKey(name)) {
                    unnecessaryFieldsToSet.put(unnecessaryFieldsMap.get(name), DAOUtil.getString(resultSet, "VALUE"));
                }
            }
        } catch (SQLException e) {
            throw e;
        } finally {
            DAOUtil.close(resultSet);
        }

    }
}