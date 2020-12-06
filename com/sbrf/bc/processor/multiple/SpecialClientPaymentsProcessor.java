/*
 * Created on 16.03.2009
 */
package com.sbrf.bc.processor.multiple;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.sberbank.sbclients.util.dao.ConnectionSource;
import com.sberbank.sbclients.util.dao.NullConnectionSource;
import com.sberbank.sbclients.util.dao.SqlOutAccessor;
import com.sbrf.bc.processor.FileMetadata;
import com.sbrf.bc.processor.OutgoingRegistryProcessor;
import com.sbrf.bc.processor.ProcessorException;
import com.sbrf.bc.processor.RegistryContext;
import com.sbrf.bc.processor.dao.ProcessRegistryUtilDAO;
import com.sbrf.dao.DownloadsPropertiesDAO;
import com.sbrf.dao.Maket1DAO;
import com.sbrf.dao.RMaketDAO;
import com.sbrf.util.logging.SBRFLogger;
import com.sbrf.util.sql.group.GroupFieldDescription;
import com.sbrf.util.sql.group.GroupSelector;
import com.sbrf.util.sql.group.NameFieldDescription;
import com.sbrf.util.sql.group.SelectorException;
import com.sbrf.util.text.MessageFormat;

/**
 * @author petrov-am
 */
public class SpecialClientPaymentsProcessor extends ProcessRegistryUtilDAO implements OutgoingRegistryProcessor {
    private final SpecialClientPaymentsProcessorConfig config;
    private final String sql;
    private final Logger logger;
    final Date operationDate;
    DownloadsPropertiesDAO downloadsPropertiesDAO;
    Maket1DAO maket1DAO;
    RMaketDAO maketRDAO;
    
    public SpecialClientPaymentsProcessor(ConnectionSource connectionSource, SpecialClientPaymentsProcessorConfig config, Date operDate) {
        super(connectionSource);
        this.config = config;
        this.logger = SBRFLogger.getInstance("com.sbrf.bc.Logger");
        this.operationDate = operDate;
        this.downloadsPropertiesDAO = new DownloadsPropertiesDAO(NullConnectionSource.getInstance());
        this.maket1DAO = new Maket1DAO(NullConnectionSource.getInstance());
        this.maketRDAO = new RMaketDAO(NullConnectionSource.getInstance());
        this.sql = makeSql(maket1DAO, "B", downloadsPropertiesDAO, "A", maketRDAO, "C", config.selectPaymentsSQLFormat);       
    }

    /**
     * Формирует ключевые для сортировки поля. Значения имен берет из интерфейса
     * SpecialClientDBNames. Порядок должен соответствовать порядку order by в
     * запросе selectPayments.sql
     * 
     * @return
     */
    private GroupFieldDescription[] makeGroupFieldDescriptions() {
        Collection<GroupFieldDescription> groupFieldDescriptions = new ArrayList<GroupFieldDescription>();

        groupFieldDescriptions.add(new NameFieldDescription(SpecialClientSelectorNames.providerCode));
        groupFieldDescriptions.add(new NameFieldDescription(SpecialClientSelectorNames.providerDepartment));
        groupFieldDescriptions.add(new NameFieldDescription(SpecialClientSelectorNames.transferDate));
        groupFieldDescriptions.add(new NameFieldDescription(SpecialClientSelectorNames.paymentOrderSource));
        groupFieldDescriptions.add(new NameFieldDescription(SpecialClientSelectorNames.paymentOrder));
        groupFieldDescriptions.add(new NameFieldDescription(SpecialClientSelectorNames.serviceKind));
        groupFieldDescriptions.add(new NameFieldDescription(SpecialClientSelectorNames.osb));

        return groupFieldDescriptions.toArray(new GroupFieldDescription[groupFieldDescriptions.size()]);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.sbrf.bc.processor.OutgoingRegistryProcessor#download(com.sbrf.bc.processor.RegistryContext)
     */
    public FileMetadata[] download(RegistryContext registryContext) throws ProcessorException {
        SpecialClientGroupHandler specialClientGroupHandler = new SpecialClientGroupHandler(this.config, this.logger, this.operationDate);
        GroupFieldDescription[] groupFieldDescription = this.makeGroupFieldDescriptions();
        GroupSelector groupSelector = new GroupSelector(groupFieldDescription);
        Connection con = getConnection();
        ResultSet resultSet = null;
        PreparedStatement ps = null;
        try {
            ps = con.prepareStatement(this.sql);
            int index = 1;
            setDate(ps, index++, operationDate);
            setString(ps, index++, config.validPaymentsStatus);
            setString(ps, index++, config.inValidRouteStatus);
            setInt(ps, index++, config.groupOsbId);
            setInt(ps, index++, DownloadTypes.NORMAL);
            resultSet = ps.executeQuery();
            groupSelector.parse(resultSet, specialClientGroupHandler);
            return specialClientGroupHandler.getResult();
        } catch (SQLException e) {
            ProcessorException exception = new ProcessorException(e);
            logger.log(Level.SEVERE, "SpecialClientPaymentsProcessor:download():" + e.getMessage(), exception);
            throw exception;
        } catch (SelectorException e) {
            ProcessorException exception = new ProcessorException(e);
            logger.log(Level.SEVERE, "SpecialClientPaymentsProcessor:download():" + e.getMessage(), exception);
            throw exception;
        } finally {
            close(resultSet);
            close(ps);
            close(con);
        }
    }

    /*
     * TODO перенести во вспомогательный класс
     */
    public static String makeSql(Maket1DAO maket1DAO, String maket1TableAlias, DownloadsPropertiesDAO downloadsPropertiesDAO, String downloadsPropertiesTableAlias, String format) {
        String downloadsPropertiesColumns = getDownloadsPropertiesColumns(downloadsPropertiesDAO, downloadsPropertiesTableAlias);
        String maket1Columns = getMaket1Columns(maket1DAO, maket1TableAlias);
        MessageFormat sqlFormat = new MessageFormat(format);
        String result = sqlFormat.format(new Object[]{downloadsPropertiesColumns, maket1Columns});
        return result;
    }
    
    public static String makeSql(Maket1DAO maket1DAO, String maket1TableAlias, DownloadsPropertiesDAO downloadsPropertiesDAO, String downloadsPropertiesTableAlias, RMaketDAO maketRDAO, String maketRTableAlias, String format) {
        String downloadsPropertiesColumns = getDownloadsPropertiesColumns(downloadsPropertiesDAO, downloadsPropertiesTableAlias);
        String maket1Columns = getMaket1Columns(maket1DAO, maket1TableAlias);
        String maketRColumns = getMaketRColumns(maketRDAO, maketRTableAlias);
        MessageFormat sqlFormat = new MessageFormat(format);
        String result = sqlFormat.format(new Object[]{downloadsPropertiesColumns, maket1Columns, maketRColumns});
        return result;
    }

    /*
     * TODO перенести во вспомогательный класс
     */
    public static String getMaket1Columns(Maket1DAO maket1DAO, String table) {
        final List<String> columns = new ArrayList<String>();
        maket1DAO.addOuts(new SqlOutAccessor() {
            public void addOut(String columnName) {
                columns.add(columnName);
            }            
        });
        StringBuffer columnList = new StringBuffer();
        boolean first = true;
        for (Iterator<String> i = columns.iterator(); i.hasNext();) {
            String column = i.next();
            if (first) {
                first = false;
            } else {
                columnList.append(", ");
            }
            columnList.append(table).append('.').append(column);
        }
        String columnsStr = columnList.toString();
        return columnsStr;
    }
    
    /*
     * TODO перенести во вспомогательный класс
     */
    public static String getMaketRColumns(RMaketDAO rMaketDAO, String table) {
        final List<String> columns = new ArrayList<String>();
        rMaketDAO.addOuts(new SqlOutAccessor() {
            public void addOut(String columnName) {
                columns.add(columnName);
            }            
        });
        StringBuffer columnList = new StringBuffer();
        boolean first = true;
        for (Iterator<String> i = columns.iterator(); i.hasNext();) {
            String column = i.next();
            if (first) {
                first = false;
            } else {
                columnList.append(", ");
            }
            columnList.append(table).append('.').append(column);
        }
        String columnsStr = columnList.toString();
        return columnsStr;
    }
    
    /*
     * TODO перенести во вспомогательный класс
     */
    public static String getDownloadsPropertiesColumns(DownloadsPropertiesDAO downloadsPropertiesDAO, String table) {
        final List<String> columns = new ArrayList<String>();
        downloadsPropertiesDAO.addOuts(new SqlOutAccessor() {
            public void addOut(String columnName) {
                columns.add(columnName);
            }            
        });
        StringBuffer columnList = new StringBuffer();
        boolean first = true;
        for (Iterator<String> i = columns.iterator(); i.hasNext();) {
            String column = i.next();
            if (first) {
                first = false;
            } else {
                columnList.append(", ");
            }
            columnList.append(table).append('.').append(column);
        }
        String columnsStr = columnList.toString();
        return columnsStr;
    }
}
