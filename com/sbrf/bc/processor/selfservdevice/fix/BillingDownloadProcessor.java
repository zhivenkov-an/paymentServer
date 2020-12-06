package com.sbrf.bc.processor.selfservdevice.fix;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.epam.sbrf.bc.jdbc.SpecialConnectionSource;
import com.sberbank.sbclients.admin.businessdelegate.AdminBusinessDelegate;
import com.sberbank.sbclients.util.dao.ConnectionSource;
import com.sberbank.sbclients.util.dao.J2EEContainerConnectionSource;
import com.sberbank.sbclients.util.dao.db2.DAOUtil;
import com.sbrf.bc.command.Command;
import com.sbrf.bc.command.CommandException;
import com.sbrf.bc.command.CommandServerDelegate;
import com.sbrf.bc.plugin.PluginNotFoundException;
import com.sbrf.bc.processor.FileMetadata;
import com.sbrf.bc.processor.OutgoingRegistryProcessor;
import com.sbrf.bc.processor.ProcessorException;
import com.sbrf.bc.processor.RegistryContext;
import com.sbrf.bc.processor.operday.OperDayFactory;
import com.sbrf.util.classloader.ResourceHelper;
import com.sbrf.util.io.CrlfPrintWriter;
import com.sbrf.util.logging.SBRFLogger;
import com.sbrf.util.sql.group.GroupSelector;
import com.sbrf.util.sql.group.NameFieldDescription;
import com.sbrf.util.sql.group.SelectorException;
import com.sbrf.util.text.LightDateFormat;
import com.sbrf.util.text.MessageFormat;
import com.sbrf.util.text.NamedMessageFormat;

public class BillingDownloadProcessor implements OutgoingRegistryProcessor {
    private final BillingDownloadProcessorConfig config;
    private final Date downloadDate;
    private final Logger logger;
    private String login;
    private final ConnectionSource insertConnectionSource;
    private final ConnectionSource dataConnectionSource;

    public BillingDownloadProcessor(BillingDownloadProcessorConfig config, Date date) {
        this.config = config;
        this.downloadDate = date;
        this.logger = SBRFLogger.getInstance(this.getClass().getSimpleName());
        this.insertConnectionSource = new SpecialConnectionSource();
        this.dataConnectionSource = new J2EEContainerConnectionSource(config.billingDatasourceJDBCName);
        try {
            CommandServerDelegate serv = new CommandServerDelegate(CommandServerDelegate.DEFAULT_JNDI_NAME);
            login = (String) serv.executeCommand(new Command() {
                public Object execute() throws CommandException {
                    return AdminBusinessDelegate.get(AdminBusinessDelegate.DEFAULT_JNDI_NAME).getUserAttributes().getLogin();
                }
            });
        } catch (Throwable e) {
            logger.log(Level.WARNING, "Ошибка при попытке получить логин загружающего платеж: " + e.getMessage(), e);
        }
    }

    public FileMetadata[] download(RegistryContext registryContext) throws ProcessorException {
        List<FileMetadata> result = new ArrayList<FileMetadata>();
        String fileName = null;
        String warningsFileName = null;
        File receipt = null;
        File warningsFile = null;
        CrlfPrintWriter outputWriter = null;
        CrlfPrintWriter warningsWriter = null;
        Date currDate = Calendar.getInstance().getTime();
        BillingDownloadHandler handler = null;
        
        Connection connection = null;
        PreparedStatement statement = null;
        ResultSet resultSet = null;
        try {
            Map<String, Object> parameters = new HashMap<String, Object>();
            parameters.put("currentDate", currDate);
            parameters.put("transferDate", OperDayFactory.getOperDayPlugin().getOperationDay());
            parameters.put("paymentDate", downloadDate);
            fileName = NamedMessageFormat.format(config.outputFileNameFormat, parameters);
            warningsFileName = NamedMessageFormat.format(config.warningsOutputFileNameFormat, parameters);

            receipt = File.createTempFile(fileName, "");
            warningsFile = File.createTempFile(warningsFileName, "");

            outputWriter = new CrlfPrintWriter(new FileOutputStream(receipt), config.lineSeparator);
            warningsWriter = new CrlfPrintWriter(new FileOutputStream(warningsFile), config.lineSeparator);
            outputWriter.println("Старт выгрузки платежей: " + new LightDateFormat("dd.MM.yyyy:HH:mm:ss").format(new Date()));

            connection = dataConnectionSource.getConnection();
            connection.setTransactionIsolation(Connection.TRANSACTION_READ_UNCOMMITTED);
            connection.setReadOnly(true);
            String sql = ResourceHelper.getResourceAsString(this.getClass(), config.SQLQueryPath);
            sql = new MessageFormat(sql).format(new Object[] { downloadDate, config.additionalWhere });
            logger.info(sql);
            statement = connection.prepareStatement(sql);
            resultSet = statement.executeQuery();

            NameFieldDescription fields[] = new NameFieldDescription[] {new NameFieldDescription("NOOSB"),  new NameFieldDescription("LINUM") };
            GroupSelector selector = new GroupSelector(fields);
            handler = new BillingDownloadHandler(insertConnectionSource, config, registryContext, outputWriter, warningsWriter, logger, login, downloadDate);
            selector.parse(resultSet, handler);
        } catch (IOException e) {
            throw new ProcessorException(e);
        } catch (SQLException e) {
            if (outputWriter != null) {
                outputWriter.println("Произошла ошибка при обращении к базе данных Billing.");
            }
            logger.log(Level.WARNING, "Произошла ошибка при обращении к базе данных Billing.", e);
            throw new ProcessorException(e);
        } catch (SelectorException e) {
            logger.log(Level.WARNING, "GroupSelector: Произошла непредвиденная ошибка при обработке платежа.", e);
            if (outputWriter != null) {
                outputWriter.println("GroupSelector: Произошла непредвиденная ошибка при обработке платежа.");
            }
        } catch (PluginNotFoundException e) {
            logger.log(Level.SEVERE, "Не найден плагин с константами псевдонимов и форматом XML документа", e);
            throw new ProcessorException(e);
        } finally {
            DAOUtil.close(resultSet);
            DAOUtil.close(statement);
            DAOUtil.close(connection);
        }
        result.add(new FileMetadata(receipt, config.outputDirectory, fileName));
        result.add(new FileMetadata(warningsFile, config.outputDirectory, warningsFileName));
        if (handler != null) {
            result.addAll(handler.getPaymentFiles());
        }
        return result.toArray(new FileMetadata[] {});
    }

}
