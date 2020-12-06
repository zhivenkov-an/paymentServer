package com.sbrf.bc.processor.selfservdevice.fix;

import java.io.IOException;
import java.io.OutputStream;
import java.text.DateFormat;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.commons.lang.StringUtils;
import org.apache.xml.serialize.BaseMarkupSerializer;
import org.apache.xml.serialize.OutputFormat;
import org.apache.xml.serialize.XMLSerializer;
import org.xml.sax.ContentHandler;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.AttributesImpl;

import com.sbrf.bc.plugin.BillingPluginFactory;
import com.sbrf.bc.plugin.PluginNotFoundException;
import com.sbrf.util.logging.SBRFLogger;
import com.sbrf.util.text.LightDateFormat;

public class PaymentToXmlWriter {
    private ContentHandler hd;
    private final AttributesImpl emptyAtts;
    private final DateFormat dateFormat;
    private final DateFormat timeFormat;
    private SelfServiceTagsPlugin documentTags;
    
    public PaymentToXmlWriter(SelfServiceTagsPlugin xmlFormatTags) {
        this.emptyAtts = new AttributesImpl();
        this.dateFormat = new LightDateFormat("yyyy-MM-dd");
        this.timeFormat = new LightDateFormat("HH:mm:ss");
        this.documentTags = xmlFormatTags;
    }

    public void init(OutputStream out, String encoding, boolean indent) throws IOException {
        OutputFormat format = new OutputFormat("XML", encoding, indent);
        BaseMarkupSerializer serializer = new XMLSerializer(out, format);
        hd = serializer.asContentHandler();
    }

    public void close() {

    }

    public void startDocument(Map<String, Object> info) throws SAXException {
        hd.startDocument();
        hd.startElement("", "", documentTags.JBT_OUTPUT, emptyAtts);
        writeMustHaveNode(documentTags.FORMAT_VERSION, "3");
        hd.startElement("", "", documentTags.OUTPUT_PERIOD, emptyAtts);
        writeMustHaveNode(documentTags.STARTDATE, dateFormat.format(info.get(documentTags.STARTDATE)));
        writeMustHaveNode(documentTags.STARTTIME, timeFormat.format(info.get(documentTags.STARTDATE)));
        writeMustHaveNode(documentTags.ENDDATE, dateFormat.format(info.get(documentTags.ENDDATE)));
        writeMustHaveNode(documentTags.ENDTIME, timeFormat.format(info.get(documentTags.ENDDATE)));
        hd.endElement("", "", documentTags.OUTPUT_PERIOD);
        writeMustHaveNode(documentTags.SOURCE_SERVER, info.get(documentTags.SOURCE_SERVER));
        hd.startElement("", "", documentTags.TRANSACTIONS, emptyAtts);
    }

    public void endDocument() throws SAXException {
        hd.endElement("", "", documentTags.TRANSACTIONS);
        hd.endElement("", "", documentTags.JBT_OUTPUT);
    }

    public void startTransaction(Map<String, Object> info) throws SAXException {
        hd.startElement("", "", documentTags.TRANSACTION_ELEMENT, emptyAtts);
        writeMustHaveNode(documentTags.TRANSACTION_TRN_ID, info.get(documentTags.TRANSACTION_TRN_ID));
        writeMustHaveNode(documentTags.TRANSACTION_SRVCODE, info.get(documentTags.TRANSACTION_SRVCODE));
        writeMustHaveNode(documentTags.TRANSACTION_ICM, info.get(documentTags.TRANSACTION_ICM));
        writeMustHaveNode(documentTags.TRANSACTION_ICM_REM, info.get(documentTags.TRANSACTION_ICM_REM));
        writeMustHaveNode(documentTags.TRANSACTION_PAYMENT_TYPE, info.get(documentTags.TRANSACTION_PAYMENT_TYPE));
        writeMustHaveNode(documentTags.TRANSACTION_TERMINAL_NUMBER, info.get(documentTags.TRANSACTION_TERMINAL_NUMBER));
        writeMustHaveNode(documentTags.TRANSACTION_OSB, info.get(documentTags.TRANSACTION_OSB));
        writeMustHaveNode(documentTags.TRANSACTION_COD_TB, info.get(documentTags.TRANSACTION_COD_TB));
        writeMustHaveNode(documentTags.TRANSACTION_PAYMENT_DATE, dateFormat.format(info.get(documentTags.TRANSACTION_PAYMENT_DATE)));
        writeMustHaveNode(documentTags.TRANSACTION_OPERATION_TIME, timeFormat.format(info.get(documentTags.TRANSACTION_OPERATION_TIME)));
        
        writeMustHaveNode(documentTags.TRANSACTION_SUM, info.get(documentTags.TRANSACTION_SUM));
        writeMustHaveNode(documentTags.TRANSACTION_IDENT, info.get(documentTags.TRANSACTION_IDENT));
        writeMustHaveNode(documentTags.TRANSACTION_USN_MC, info.get(documentTags.TRANSACTION_USN_MC));
        writeMustHaveNode(documentTags.TRANSACTION_ICON_MC, info.get(documentTags.TRANSACTION_ICON_MC));
        writeMustHaveNode(documentTags.TRANSACTION_BDT_MC, info.get(documentTags.TRANSACTION_BDT_MC));
        writeMustHaveNode(documentTags.TRANSACTION_BN_MC, info.get(documentTags.TRANSACTION_BN_MC));
        writeMustHaveNode(documentTags.TRANSACTION_NTF_MC, info.get(documentTags.TRANSACTION_NTF_MC));
        writeMustHaveNode(documentTags.TRANSACTION_SERT_MC, info.get(documentTags.TRANSACTION_SERT_MC));
        writeMustHaveNode(documentTags.TRANSACTION_USN_CC, info.get(documentTags.TRANSACTION_USN_CC));
        writeMustHaveNode(documentTags.TRANSACTION_ICON_CC, info.get(documentTags.TRANSACTION_ICON_CC));
        
        writeMustHaveNode(documentTags.TRANSACTION_TSN_CC, info.get(documentTags.TRANSACTION_TSN_CC));
        writeMustHaveNode(documentTags.TRANSACTION_ACCOUNT_CC, info.get(documentTags.TRANSACTION_ACCOUNT_CC));
        writeMustHaveNode(documentTags.TRANSACTION_BANK_CC, info.get(documentTags.TRANSACTION_BANK_CC));
        writeMustHaveNode(documentTags.TRANSACTION_ACCTYPE_CC, info.get(documentTags.TRANSACTION_ACCTYPE_CC));
        writeMustHaveNode(documentTags.TRANSACTION_SERT_CC, info.get(documentTags.TRANSACTION_SERT_CC));
        writeMustHaveNode(documentTags.TRANSACTION_CARD_NUMBER, info.get(documentTags.TRANSACTION_CARD_NUMBER));
        writeMustHaveNode(documentTags.TRANSACTION_AUTH_CODE, info.get(documentTags.TRANSACTION_AUTH_CODE));
        writeMustHaveNode(documentTags.TRANSACTION_TERMID, info.get(documentTags.TRANSACTION_TERMID));
        writeMustHaveNode(documentTags.TRANSACTION_MERCHID, info.get(documentTags.TRANSACTION_MERCHID));
        writeMustHaveNode(documentTags.TRANSACTION_DATE_CFS, info.get(documentTags.TRANSACTION_DATE_CFS));
        
        writeMustHaveNode(documentTags.TRANSACTION_TIME_CFS, info.get(documentTags.TRANSACTION_TIME_CFS));
        writeMustHaveNode(documentTags.TRANSACTION_NEED_AMMOUNT, info.get(documentTags.TRANSACTION_NEED_AMMOUNT));
        writeMustHaveNode(documentTags.TRANSACTION_DEVICE_NUMBER, info.get(documentTags.TRANSACTION_DEVICE_NUMBER));
        writeMustHaveNode(documentTags.TRANSACTION_BNKCIOD_OPEN, info.get(documentTags.TRANSACTION_BNKCIOD_OPEN));
        
        hd.startElement("", "", documentTags.PAYMENTS, emptyAtts);
    }

    public void endTransaction() throws SAXException {
        hd.endElement("", "", documentTags.PAYMENTS);
        hd.endElement("", "", documentTags.TRANSACTION_ELEMENT);
    }

    public void startPayment(Map<String, Object> info) throws SAXException {
        hd.startElement("", "", documentTags.PAYMENT, emptyAtts);
        
        writeMustHaveNode(documentTags.PAYMENT_DOCUMENT_NUMBER,  info.get(documentTags.PAYMENT_DOCUMENT_NUMBER));
        writeMustHaveNode(documentTags.PAYMENT_SUM,  info.get(documentTags.PAYMENT_SUM));
        writeMustHaveNode(documentTags.PAYMENT_AMMOUNT_OVER,  info.get(documentTags.PAYMENT_AMMOUNT_OVER));
        writeMustHaveNode(documentTags.PAYMENT_PAYER_COMMISSION,  info.get(documentTags.PAYMENT_PAYER_COMMISSION));
        writeMustHaveNode(documentTags.PAYMENT_RESDOC,  info.get(documentTags.PAYMENT_RESDOC));
        writeMustHaveNode(documentTags.PAYMENT_BC_TRN,  info.get(documentTags.PAYMENT_BC_TRN));
        writeMustHaveNode(documentTags.PAYMENT_DATE_EXE,  info.get(documentTags.PAYMENT_DATE_EXE));
        writeMustHaveNode(documentTags.PAYMENT_TIME_EXE,  info.get(documentTags.PAYMENT_TIME_EXE));
        writeMustHaveNode(documentTags.PAYMENT_RECEIVER_NUMBER,  info.get(documentTags.PAYMENT_RECEIVER_NUMBER));
        writeMustHaveNode(documentTags.PAYMENT_SPECIAL_CLIENT_CODE,  info.get(documentTags.PAYMENT_SPECIAL_CLIENT_CODE));
        
        writeMustHaveNode(documentTags.PAYMENT_RECEIVER_NAME,  info.get(documentTags.PAYMENT_RECEIVER_NAME));
        writeMustHaveNode(documentTags.PAYMENT_BIK,  info.get(documentTags.PAYMENT_BIK));
        writeMustHaveNode(documentTags.PAYMENT_CORR_ACC,  info.get(documentTags.PAYMENT_CORR_ACC));
        writeMustHaveNode(documentTags.PAYMENT_SETTLE_ACC,  info.get(documentTags.PAYMENT_SETTLE_ACC));
        writeMustHaveNode(documentTags.PAYMENT_INN,  info.get(documentTags.PAYMENT_INN));
        writeMustHaveNode(documentTags.PAYMENT_BANK,  info.get(documentTags.PAYMENT_BANK));
        writeMustHaveNode(documentTags.PAYMENT_RECEIPMERCHID,  info.get(documentTags.PAYMENT_RECEIPMERCHID));
        writeMustHaveNode(documentTags.PAYMENT_RECEIPFILIALNUM,  info.get(documentTags.PAYMENT_RECEIPFILIALNUM));
        writeMustHaveNode(documentTags.PAYMENT_PERSONAL_ACCOUNT,  info.get(documentTags.PAYMENT_PERSONAL_ACCOUNT));
        writeMustHaveNode(documentTags.PAYMENT_PERSENT,  info.get(documentTags.PAYMENT_PERSENT));
        writeMustHaveNode(documentTags.PAYMENT_AMMOUNTCOMMMIN,  info.get(documentTags.PAYMENT_AMMOUNTCOMMMIN));
        
        writeMustHaveNode(documentTags.PAYMENT_AMMOUNTCOMMMAX,  info.get(documentTags.PAYMENT_AMMOUNTCOMMMAX));
        writeMustHaveNode(documentTags.PAYMENT_DOGOVOR,  info.get(documentTags.PAYMENT_DOGOVOR));
        writeMustHaveNode(documentTags.PAYMENT_PAYER_INFO,  info.get(documentTags.PAYMENT_PAYER_INFO));
        writeMustHaveNode(documentTags.PAYMENT_STREET,  info.get(documentTags.PAYMENT_STREET));
        writeMustHaveNode(documentTags.PAYMENT_HOUSE,  info.get(documentTags.PAYMENT_HOUSE));
        writeMustHaveNode(documentTags.PAYMENT_BUILDING,  info.get(documentTags.PAYMENT_BUILDING));
        writeMustHaveNode(documentTags.PAYMENT_FLAT,  info.get(documentTags.PAYMENT_FLAT));
        writeMustHaveNode(documentTags.PAYMENT_PAYER_ACC,  info.get(documentTags.PAYMENT_PAYER_ACC));
        writeMustHaveNode(documentTags.PAYMENT_PERIOD,  info.get(documentTags.PAYMENT_PERIOD));
        writeMustHaveNode(documentTags.PAYMENT_PAYMENT_DESTINATION,  info.get(documentTags.PAYMENT_PAYMENT_DESTINATION));
        
        hd.startElement("", "", documentTags.PARAMETERS, emptyAtts);
    }

    public void endPayment() throws SAXException {
        hd.endElement("", "", documentTags.PARAMETERS);
        hd.endElement("", "", documentTags.PAYMENT);
    }

    public void makeProperty(String shortName, String value) throws SAXException {
        hd.startElement("", "", documentTags.PARAMETER, emptyAtts);
        writeMustHaveNode(documentTags.PARAMETER_SHORT_NAME, shortName);
        writeMustHaveNode(documentTags.PARAMETER_VALUE, value);
        hd.endElement("", "", documentTags.PARAMETERS);
    }
    
    public void makeProperties(Map<String, String> properties) throws SAXException {
        Set<Entry<String, String>> propsSet = properties.entrySet();
        for (Entry<String, String> entry : propsSet) {
            if (StringUtils.isNotBlank(entry.getKey()) && StringUtils.isNotEmpty(entry.getValue())) {
                makeProperty(entry.getKey(), entry.getValue());
            }
        }
    }
    
    private void writeNode(String alias, Object value) throws SAXException {
        if (value != null) {
            String strValue = value.toString();
            hd.startElement("", "", alias, emptyAtts);
            hd.characters(strValue.toCharArray(), 0, strValue.length());
            hd.endElement("", "", alias);
        } 
    }
    
    private void writeMustHaveNode(String alias, Object value) throws SAXException {
        hd.startElement("", "", alias, emptyAtts);
        if (value != null) {
            String strValue = value.toString();
            hd.characters(strValue.toCharArray(), 0, strValue.length());
        } 
        hd.endElement("", "", alias);
    }
}
