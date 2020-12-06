package com.sbrf.bc.processor.selfservdevice.fix;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;

import com.sberbank.sbclients.util.dao.ConnectionSource;
import com.sberbank.sbclients.util.dao.db2.AbstractDAO;

public class BCPaymentData extends AbstractDAO {
    protected BCPaymentData(ConnectionSource connectionSource) {
        super(connectionSource);
        // TODO Auto-generated constructor stub
    }

    private long linum;
    private Long linpu;
    private String dopid;
    private Date datep;
    private Date timep;
    private Date billingDateTime;
    private String noosb;
    private String nofil;
    private String nocas;
    private String npdoc;
    private int typla;
    private int status;
    private long sumpl;
    private long sumpu;
    private long supen;
    private long sumag;
    private long payerCommission;
    private Long rnum;
    private Integer rline;
    private String plinf;
    private int nompu;
    private int nomdp;
    private Integer nomus;
    private String bik;
    private String corrAcc;
    private String settleAcc;
    private String providerInfo;
    private Long inRegId;
    private Integer inRegLine;
    private Date transferDate;
    private String inn;
    private String kpp;
    private String kbk;
    private String okato;
    private String paymentOrder;

    public long getLinum() {
        return linum;
    }

    public void  setLinum(long linum) {
        this.linum = linum;
    }

    public Long getLinpu() {
        return linpu;
    }

    public void  setLinpu(Long linpu) {
        this.linpu = linpu;
    }

    public String getDopid() {
        return dopid;
    }

    public void  setDopid(String dopid) {
        this.dopid = dopid;
    }

    public Date getDatep() {
        return datep;
    }

    public void  setDatep(Date datep) {
        this.datep = datep;
    }

    public Date getTimep() {
        return timep;
    }

    public void  setTimep(Date timep) {
        this.timep = timep;
    }

    public Date getBillingDateTime() {
        return billingDateTime;
    }

    public void  setBillingDateTime(Date billingDateTime) {
        this.billingDateTime = billingDateTime;
    }

    public String getNoosb() {
        return noosb;
    }

    public void  setNoosb(String noosb) {
        this.noosb = noosb;
    }

    public String getNofil() {
        return nofil;
    }

    public void  setNofil(String nofil) {
        this.nofil = nofil;
    }

    public String getNocas() {
        return nocas;
    }

    public void  setNocas(String nocas) {
        this.nocas = nocas;
    }

    public String getNpdoc() {
        return npdoc;
    }

    public void  setNpdoc(String npdoc) {
        this.npdoc = npdoc;
    }

    public int getTypla() {
        return typla;
    }

    public void  setTypla(int typla) {
        this.typla = typla;
    }

    public int getStatus() {
        return status;
    }

    public void  setStatus(int status) {
        this.status = status;
    }

    public long getSumpl() {
        return sumpl;
    }

    public void  setSumpl(long sumpl) {
        this.sumpl = sumpl;
    }

    public long getSumpu() {
        return sumpu;
    }

    public void  setSumpu(long sumpu) {
        this.sumpu = sumpu;
    }

    public long getSupen() {
        return supen;
    }

    public void  setSupen(long supen) {
        this.supen = supen;
    }

    public long getSumag() {
        return sumag;
    }

    public void  setSumag(long sumag) {
        this.sumag = sumag;
    }

    public long getPayerCommission() {
        return payerCommission;
    }

    public void  setPayerCommission(long payerCommission) {
        this.payerCommission = payerCommission;
    }

    public Long getRnum() {
        return rnum;
    }

    public void  setRnum(Long rnum) {
        this.rnum = rnum;
    }

    public Integer getRline() {
        return rline;
    }

    public void  setRline(Integer rline) {
        this.rline = rline;
    }

    public String getPlinf() {
        return plinf;
    }

    public void  setPlinf(String plinf) {
        this.plinf = plinf;
    }

    public int getNompu() {
        return nompu;
    }

    public void  setNompu(int nompu) {
        this.nompu = nompu;
    }

    public int getNomdp() {
        return nomdp;
    }

    public void  setNomdp(int nomdp) {
        this.nomdp = nomdp;
    }

    public Integer getNomus() {
        return nomus;
    }

    public void  setNomus(Integer nomus) {
        this.nomus = nomus;
    }

    public String getBik() {
        return bik;
    }

    public void  setBik(String bik) {
        this.bik = bik;
    }

    public String getCorrAcc() {
        return corrAcc;
    }

    public void  setCorrAcc(String corrAcc) {
        this.corrAcc = corrAcc;
    }

    public String getSettleAcc() {
        return settleAcc;
    }

    public void  setSettleAcc(String settleAcc) {
        this.settleAcc = settleAcc;
    }

    public String getProviderInfo() {
        return providerInfo;
    }

    public void  setProviderInfo(String providerInfo) {
        this.providerInfo = providerInfo;
    }

    public Long getInRegId() {
        return inRegId;
    }

    public void  setInRegId(Long inRegId) {
        this.inRegId = inRegId;
    }

    public Integer getInRegLine() {
        return inRegLine;
    }

    public void  setInRegLine(Integer inRegLine) {
        this.inRegLine = inRegLine;
    }

    public Date getTransferDate() {
        return transferDate;
    }

    public void  setTransferDate(Date transferDate) {
        this.transferDate = transferDate;
    }

    public String getInn() {
        return inn;
    }

    public void  setInn(String inn) {
        this.inn = inn;
    }

    public String getKpp() {
        return kpp;
    }

    public void  setKpp(String kpp) {
        this.kpp = kpp;
    }

    public String getKbk() {
        return kbk;
    }

    public void  setKbk(String kbk) {
        this.kbk = kbk;
    }

    public String getOkato() {
        return okato;
    }

    public void  setOkato(String okato) {
        this.okato = okato;
    }

    public String getPaymentOrder() {
        return paymentOrder;
    }

    public void  setPaymentOrder(String paymentOrder) {
        this.paymentOrder = paymentOrder;
    }
    
    public static int populate(BCPaymentData value, ResultSet resultSet, int startIndex) throws SQLException {
        int index = startIndex;
        value.setLinum(get_long(resultSet, index++));
        value.setLinpu(getLong(resultSet, index++));
        value.setDopid(getString(resultSet, index++));
        value.setDatep(getDate(resultSet, index++));
        value.setTimep(getTime(resultSet, index++));
        value.setBillingDateTime(getTimestamp(resultSet, index++));
        value.setNoosb(getString(resultSet, index++));
        value.setNofil(getString(resultSet, index++));
        value.setNocas(getString(resultSet, index++));
        value.setNpdoc(getString(resultSet, index++));
        value.setTypla(get_int(resultSet, index++));
        value.setStatus(get_int(resultSet, index++));
        value.setSumpl(get_long(resultSet, index++));
        value.setSumpu(get_long(resultSet, index++));
        value.setSupen(get_long(resultSet, index++));
        value.setSumag(get_long(resultSet, index++));
        value.setPayerCommission(get_long(resultSet, index++));
        value.setRnum(getLong(resultSet, index++));
        value.setRline(getInt(resultSet, index++));
        value.setPlinf(getString(resultSet, index++));
        value.setNompu(get_int(resultSet, index++));
        value.setNomdp(get_int(resultSet, index++));
        value.setNomus(getInt(resultSet, index++));
        value.setBik(getString(resultSet, index++));
        value.setCorrAcc(getString(resultSet, index++));
        value.setSettleAcc(getString(resultSet, index++));
        value.setProviderInfo(getString(resultSet, index++));
        value.setInRegId(getLong(resultSet, index++));
        value.setInRegLine(getInt(resultSet, index++));
        value.setTransferDate(getDate(resultSet, index++));
        value.setInn(getString(resultSet, index++));
        value.setKpp(getString(resultSet, index++));
        value.setKbk(getString(resultSet, index++));
        value.setOkato(getString(resultSet, index++));
        value.setPaymentOrder(getString(resultSet, index++));
        return index;
    }
}
