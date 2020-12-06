package com.epam.sbrf.payorder.plugins;

import com.epam.sbrf.accdocs.sbrf3.processors.DataProcessorConfig;
import com.epam.sbrf.common.util.CommonUtilFactoryImpl;
import com.epam.sbrf.common.util.PluginFacade;
import com.sberbank.sbclients.util.dao.J2EEContainerConnectionSource;
import com.sberbank.sbclients.util.dao.db2.DAOUtil;
import com.sbrf.bc.processor.FileMetadata;
import com.sbrf.bc.processor.OutgoingRegistryProcessor;
import com.sbrf.bc.processor.OutgoingRegistryProcessorFactory;
import com.sbrf.bc.processor.Param;
import com.sbrf.bc.processor.ProcessorException;
import com.sbrf.bc.processor.RegistryContext;
import com.sbrf.bc.processor.RegistryProcessor;
import com.sbrf.bc.processor.RegistryProcessorDelegate;
import com.sbrf.bc.processor.operday.OperDay;

import java.sql.Connection;
import java.sql.PreparedStatement;

import java.sql.SQLException;


import java.util.Date;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.sbrf.util.classloader.ResourceHelper;
import com.sbrf.util.logging.SBRFLogger;
import com.sbrf.util.text.MessageFormat;
import com.sbrf.bc.plugin.PluginConfigurationHelper;
import com.sbrf.bc.plugin.PluginNotFoundException;

/**
 * Фильтр выгрузки платежного поручения ИРЦ.
 * 
 * 
 */
public class Sbrf3DownloadFactoryFixed implements OutgoingRegistryProcessorFactory{
    
    private final Logger logger = SBRFLogger.getInstance("com.sbrf.bc.Logger");    
    private static final PluginFacade pluginFactory = CommonUtilFactoryImpl.newInstance().createPluginFacade();
    private final Config config;
    /**
     * Создает новую фабрику.
     * 
     * @param properties
     *            параметры
     */
    public Sbrf3DownloadFactoryFixed(Properties properties) {
        PluginConfigurationHelper helper = new PluginConfigurationHelper(properties);
        config = new Config(helper);
        logger.info(getClass().getName() + " version: 1.0");
        
    }
    public OutgoingRegistryProcessor newProcessor(Param param) throws ProcessorException {
        // TODO Auto-generated method stub
        return new Processor(config, param);
    }
    public static final class Processor implements OutgoingRegistryProcessor {
        private final Config config;
        private final Logger logger = SBRFLogger.getInstance("com.sbrf.bc.Logger");
        private final Param param;
        private final RegistryProcessor registryProcessor;

        public Processor(Config config, Param param) {
        this.config = config;
        this.param = param;
        //this.registryProcessor = new RegistryProcessorDelegate(RegistryProcessorDelegate.DEFAULT_JNDI_NAME);
        this.registryProcessor = new RegistryProcessorDelegate("java:comp/env/ejb/RegistryProcessorRecursive");        
        }

        // получение операционного дня
        private Date getOperDay() throws ProcessorException {
            OperDay od;
            try {                
                od = (OperDay) pluginFactory.findPlugin(config.operDayPluginName);
            } catch (PluginNotFoundException e) {
                logger.log(Level.SEVERE, "Не найден плагин опер. дня " + e.getMessage(), e);
                throw new ProcessorException("Не найден плагин опер. дня " + e.getMessage());
            }
            return od.getOperationDay();
        }
        /**
         * устанавливаем признак выгрузки платежного поручения по расчетному счету
         */
        private void setUnloading(){
            Connection connection = null;
            PreparedStatement statement = null;
            J2EEContainerConnectionSource connectionSource = null;
            connectionSource = new J2EEContainerConnectionSource();            
            String sql = "";
            String flagUnloadingAll = "";
            String flagUnloadingOrg = "";
            String uslWhere = "";
            String uslWhereTwo = "";
            try {
             // формируем sql запрос из параметров плагина и даты операционного дня
                connection = connectionSource.getConnection(); 
                if (config.flagUnloading.equals("1")){
                    flagUnloadingAll = "1"; 
                    flagUnloadingOrg = "0";                    
                }
                else {
                    flagUnloadingAll = "0"; 
                    flagUnloadingOrg = "1";
                }
                if (config.settleAccFilter.length()==20 && config.numberFilter.equals("1")){ // если задан фильтр по расчетному счету
                    uslWhere = " and SETTLE_ACC = " + config.settleAccFilter;
                    uslWhereTwo = " and SETTLE_ACC <> " + config.settleAccFilter;
                }
                if (config.orderNumFilter.length()>0 && config.numberFilter.equals("2")){ // если задан фильтр по номеру платежного поручения
                    uslWhere = " and ORDER_NUM in (" + config.orderNumFilter + ")";
                    uslWhereTwo = " and NOT ORDER_NUM in (" + config.orderNumFilter + ")";
                }
                if (config.specialClientFilter.length()>0 && config.numberFilter.equals("3")){ // если задан фильтр по коду спецклиента, то дополнительно выполняем подзапрос
                    String sqlFormatFind = ResourceHelper.getResourceAsString(config.sqlQueryFindOrderNum);
                    sql = MessageFormat.format(sqlFormatFind, new Object[] {this.getOperDay(), config.specialClientFilter});
                    uslWhere = " and ORDER_NUM in (" + sql + ")";
                    uslWhereTwo = " and NOT ORDER_NUM in (" + sql + ")";
                    sql = "";
                }
                //select ORDER_NUM from pay.r_maket left join pay.payments on pay.r_maket.order_num=pay.payments.payment_order and pay.r_maket.payment_date=pay.payments.transfer_date  where pay.r_maket.PAYMENT_DATE = '2012-12-01' and pay.payments.special_client_code in ('00847') group by pay.r_maket.ORDER_NUM with ur;
                String sqlFormat = ResourceHelper.getResourceAsString(config.sqlQueryPath);
                sql = MessageFormat.format(sqlFormat, new Object[] {flagUnloadingOrg, this.getOperDay(), uslWhere});
             // sql = "update pay.r_maket set unloading=1 where PAYMENT_DATE = '2012-11-24' and SETTLE_ACC = '40702810213000004348';";                
                logger.info("\n\n  ==ZHAN==: SQL:\n" + sql);
                statement = connection.prepareStatement(sql);                
                statement.executeUpdate(); // выполняеться при update
                // выполняем второй запрос
                sql = MessageFormat.format(sqlFormat, new Object[] {flagUnloadingAll, this.getOperDay(), uslWhereTwo});
                logger.info("\n\n  ==ZHAN==: SQL2:\n" + sql);
                statement = connection.prepareStatement(sql);
                statement.executeUpdate(); // выполняеться при update
            } catch (SQLException e) {
                // TODO Auto-generated catch block
                logger.info("\n\n  ==ZHAN==: ошибка при выполнение update");
                e.printStackTrace();
            } catch (ProcessorException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            DAOUtil.close(connection);
            
        }              
        
        
        public FileMetadata[] download(RegistryContext context) throws ProcessorException {
            // 
            RegistryContext newContext = context;
            DataProcessorConfig dataProcessorConfig = new DataProcessorConfig();
            dataProcessorConfig.setOperDay(getOperDay());           
                      
            
            FileMetadata[] registries = null;
            try {
                // устанавливаем признак выгруженности для платежных поручений
                logger.info("==ZHAN==: запускаем обновление");
                this.setUnloading();
                logger.info("==ZHAN==: вызываем обычную выгрузку Sbrf3");
                registries = registryProcessor.downloadRegistries(0,config.plaginName, param, newContext);
            } catch (PluginNotFoundException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            return registries;
        }

    }

    public static final class Config {
     // наименование плагина операционного дня
        final String operDayPluginName;
     // расчетный счет поставщика для фильтра
        final String settleAccFilter;
        final String plaginName;
        final String orderNumFilter;
        final String sqlQueryPath;
        final String sqlQueryFindOrderNum;
        final String flagUnloading;
        final String specialClientFilter;
        final String numberFilter;
        public Config(PluginConfigurationHelper context) {              
            operDayPluginName = context.getString("operDayPluginName", OperDay.PLUGIN_NAME);
            settleAccFilter = context.getString("settleAccFilter", "40702810213000004348");
            plaginName = context.getString("plaginNameRedirec", "com.sbrf.bc.document.outgoing.SBRF3_9013");
            orderNumFilter = context.getString("orderNumFilter", "");
            specialClientFilter = context.getString("specialClientFilter", "");
            sqlQueryPath = context.getString("sqlQueryPath", "com/epam/sbrf/payorder/plugins/UpdateUnloading.sql");
            sqlQueryFindOrderNum = context.getString("sqlQueryFindOrderNum", "com/epam/sbrf/payorder/plugins/FindOrderNum.sql");                                                                              
            flagUnloading = context.getString("flagUnloading", "0");
            numberFilter = context.getString("numberFilter", "3");
        }


    }

    
}