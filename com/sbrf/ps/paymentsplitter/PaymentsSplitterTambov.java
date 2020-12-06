package com.sbrf.ps.paymentsplitter;

import java.io.PrintWriter;
//import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.epam.sbrf.bc.data.PaymentData;
import com.sberbank.sbclients.util.dao.ConnectionSource;
import com.sberbank.sbclients.util.dao.J2EEContainerConnectionSource;
import com.sbrf.bc.plugin.PluginConfigurationHelper;
import com.sbrf.util.logging.SBRFLogger;


public class PaymentsSplitterTambov implements PaymentsSplitterFactory {

    Config co;

    public PaymentsSplitterTambov(Properties properties) {
        co = new Config(new PluginConfigurationHelper(properties));
    }

    @Override
    public PaymentSplitterInterface createPaymentsSplitter() {
        // TODO Auto-generated method stub
        return new Splitter(co, new J2EEContainerConnectionSource());
    }

    public class Config {
        public String receiverNumberToSplit;
        public String nompu;
        public Integer numberKOD1;
        public Integer numberSUMMA1;
        public Integer numberKOD2;
        public Integer numberSUMMA2;

        public Config(PluginConfigurationHelper helper) {
            receiverNumberToSplit = helper.getString("receiverNumberToSplit", "36884");             // номер организации для которого идёт расщепление
            nompu = helper.getString("nompu", "001,002 "); // по nompu определяется поставщик в 4000-ике
            numberKOD1 = helper.getInteger("numberKOD1", 2); // номер дополнительного реквизита где находится код Организации 1 
            numberKOD2 = helper.getInteger("numberKOD2", 4); // номер дополнительного реквизита где находится код Организации 2
            numberSUMMA1 = helper.getInteger("numberSUMMA1", 3); // номер дополнительного реквизита где находится Сумма Организации 1
            numberSUMMA2 = helper.getInteger("numberSUMMA2", 5); // номер дополнительного реквизита где находится Сумма Организации 2


        }

    }

    public class Splitter extends PaymentSplitterAbstract {

        ConnectionSource connectionsSource;
       // private List<String> toNewReceiverNumbers;
        Map<String, String> mapKOD = new Hashtable<String, String>(); // ассоциативный массив кодов организаций и номеров из справочника specific_clients
        //private List<BigDecimal> rates;
        Config config;

        public Splitter(Config confgi, ConnectionSource connectionsSource) {
            this.config = confgi;
            this.connectionsSource = connectionsSource;

        }

        @Override
        public void close() {
        }

        @Override
        public PaymentData[] getSplittedPayments(PaymentData paymentData, PrintWriter messageWriter, Logger logger) throws PaymentsSplitterException {
            if (paymentData.getReceiverNumber() != null && config.receiverNumberToSplit.equals(paymentData.getReceiverNumber()) && !paymentData.isStorned()) {
                try {
                    messageWriter.println("Платеж с номером документа " + paymentData.getDocumentNumber() + " будет расщеплен.");
                    String[] splittedReqs = paymentData.getAdditionalRequisites().split("@");

                    //paymentData.setAdditionalRequisites(paymentData.getAdditionalRequisites().replaceAll("СТР 0.00:ПЕН ", "") + "@" + paymentData.getSum());
                    List<PaymentData> newPayments = new ArrayList<PaymentData>();
                    long firstSum = paymentData.getSum();
                    long commonSum = 0;
                    if ( mapKOD.get(splittedReqs[config.numberKOD1]) == null ){                    	
                    	throw new PaymentsSplitterException("Не определен код Организации 1 " + splittedReqs[config.numberKOD1] + ", номер в 4000-ике " + mapKOD.get(splittedReqs[config.numberKOD1]) + " , суммма " + splittedReqs[config.numberSUMMA1] + "\n");
                    }
                    
                    if ( mapKOD.get(splittedReqs[config.numberKOD2]) == null ){
                    	throw new PaymentsSplitterException("Не определен код Организации 2 " + splittedReqs[config.numberKOD2] + ", номер в 4000-ике " + mapKOD.get(splittedReqs[config.numberKOD2]) + " , суммма " + splittedReqs[config.numberSUMMA2] + "\n");
                    }

                    // TODO                    
                    // платеж в Управляющую компанию
                        PaymentData newPayment = copyPayment(paymentData);
                        newPayment.setDocumentNumber("1" + paymentData.getDocumentNumber());                        
                        long sumPu = Long.parseLong(splittedReqs[config.numberSUMMA1].trim().replace(".", ""));// в 4 реквизите лежит сумма в УК
                        commonSum += sumPu;                         
                        newPayment.setSum(sumPu);
                        newPayment.setReceiverNumber(mapKOD.get(splittedReqs[config.numberKOD1])); // в 3 реквизите лежит код УК
                        newPayments.add(newPayment);
                   // платеж Регионального оператора
                        newPayment = copyPayment(paymentData);
                        newPayment.setDocumentNumber("2" + paymentData.getDocumentNumber());                        
                        long sumRo = Long.parseLong(splittedReqs[config.numberSUMMA2].trim().replace(".", ""));// в пятом реквизите лежит сумма в Регионального оператора
                        commonSum += sumRo;
                        newPayment.setSum(sumRo);
                        newPayment.setPayerCommission(0); // обнуляем комиссию с плательщика для второго расщеплённого платежа
                        newPayment.setReceiverNumber(mapKOD.get(splittedReqs[config.numberKOD2])); // в 5 реквизите лежит код Регионального оператора
                        newPayments.add(newPayment);
                        
                    messageWriter.println("Платеж с " + paymentData.getPaymentDate() + " " + paymentData.getOsb() + " " + paymentData.getFilial() + " номером документа " + paymentData.getDocumentNumber() + " был расщеплен на " + newPayments.size() + " платежей.");
                    
                    if (commonSum != firstSum) {
                        messageWriter.println("Суммы не сошлись, сумма платежа " + firstSum + ", общая " + commonSum);
                        throw new PaymentsSplitterException("Суммы не сошлись, сумма платежа " + firstSum + ", общая " + commonSum);
                    }

                    return newPayments.toArray(new PaymentData[0]);
                } catch (Exception e) {
                    logger.log(Level.INFO, "Произошла ошибка при расщеплении платежа", e);
                    messageWriter.println("Ошибка при расщеплении платежаж с " + paymentData.getPaymentDate() + " " + paymentData.getOsb() + " " + paymentData.getFilial() + " номером документа " + paymentData.getDocumentNumber());
                    throw new PaymentsSplitterException(e);
                }
            }
            throw new PaymentsSplitterException("Невозможно расщепить");
        }

        @Override
        public void init() {            
            Connection connection = connectionsSource.getConnection();

            final Logger logger = SBRFLogger.getInstance("com.sbrf.bc.Logger");
            String[] splittedNompu = config.nompu.split(",");
            try {

            	for (int i=0;i<splittedNompu.length;i++)
            	{
                    PreparedStatement ps = connection.prepareStatement("select receiver_number from pay.specific_clients where nompu = ? and receiver_number <> ?");
	                ps.setString(1, splittedNompu[i]);
	                ps.setString(2, config.receiverNumberToSplit);                
	                ResultSet rs = ps.executeQuery();    
	                                
	                while (rs.next()) {
	                    String receiverNumber = rs.getString(1); 
	                    logger.info("\n\n  ==ZHAN==: Код получателя " + splittedNompu[i] + " Номер организации " +  receiverNumber + "\n");
	                    mapKOD.put(splittedNompu[i], receiverNumber);
	                }
	                rs.close();
	                ps.close();
            	}
                if (mapKOD.isEmpty()) { // Если ассоциативный массив пустой
                    throw new RuntimeException("Не найдены коды организаций для расщепления");
                }
                connection.close();
            } catch (SQLException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }

        @Override
        public boolean checkForSplit(PaymentData paymentData) {
            return paymentData.getReceiverNumber() != null && config.receiverNumberToSplit.equals(paymentData.getReceiverNumber()) && !paymentData.isStorned();
        }

    }

}
