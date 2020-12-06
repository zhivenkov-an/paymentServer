package ru.sbrf.ccb.rsv;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Properties;
//import oracle.jdbc.OracleDriver;


public class RepGenStart {
    /**
     * @param args
     * @throws Exception
     */
    
    private static int    PERSON_MAJOR     = 0;
    private static int    PERSON_MINOR     = 0;
    private static int    DEPOSIT_MAJOR    = 0;
    private static int    DEPOSIT_MINOR    = 0;
    private static int    CUR_ROW_NUM      = 1;
    private static int    OUT_FILE_CNT     = 1;
    private static int    REP_GOOD_CNT     = 0;
    private static int    REP_BAD_CNT      = 0;
    private static String REP_DEBT_DATE    = null;
    private static String REP_FIO          = null;
    private static String REP_BRANCH_FIL   = null;
    private static String REP_ADDRESS_1    = null;
    private static String REP_ADDRESS_2    = null;
    private static String REP_MOBPHONE     = null;
    private static String REP_MOBPHONE_OLD = null;
    private static String REP_CARD_TYPE    = null;
    private static String REP_CARD_NUMBER  = null;
    private static String REP_CURRENCY     = null;
    private static String REP_DEBT_TYPE    = null;
    private static String OUT_FILE_DIR     = null;
    private static String SYS_FILE_SEP     = null;
    private static String CUR_ROW          = null;
    private static String CUR_ID_MEGA      = "0";
    
    private static BigDecimal REP_ACTIVE_DEBT = new BigDecimal("0.00");
    private static Connection DBConnection = null;                              //������������� ���������� ��� ���������� � ��
    private static PreparedStatement TBlistStmt = null;                         //������������� ���������� � ��������� � ��
    private static PreparedStatement MaxDebtCountStmt = null;
    private static PreparedStatement AllCardDebtStmt = null;                    
    private static PreparedStatement CardClassStmt = null;
    private static PreparedStatement ClientInfoStmt = null;
    private static PreparedStatement SecondAddressStmt = null;
    private static ResultSet TBlistResult = null;                               //������������� ���������� � ������������ ������� � ��
    private static ResultSet MaxDebtCountResult = null;
    private static ResultSet AllCardDebtResult = null;
    private static ResultSet CardClassResult = null;
    private static ResultSet ClientInfoResult = null;
    private static ResultSet SecondAddressResult = null;
    private static ArrayList<String> OutputDataArray = new ArrayList<String>(); //������������� �������
    private static OutputStream OutFile = null;                                 //������������� ���������� ��������� ������
    
    public static void main(String[] args) throws Exception {
        String INPUT_ID_MEGA      = args[0];
        Class.forName("oracle.jdbc.OracleDriver");                               //�������� ������ �� �����
        
//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
        DBConnection=DriverManager.getConnection("jdbc:oracle:thin:@10.143.15.201:1521:dbdpc", "wsback", "wsback");     // Stand201
//        DBConnection=DriverManager.getConnection("jdbc:oracle:thin:@10.71.134.19:1521:ift_ecod", "wsback", "wsback");   // ECOD Test
//        DBConnection=DriverManager.getConnection("jdbc:oracle:thin:@10.67.24.212:1552:dbdpc", "wsback", "wsback");      // ECOD Prom
//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////       
        try {
            // ��������� ������������ ���������� ��
            TBlistStmt=DBConnection.prepareStatement("select distinct id_mega from operday.dpcoperday");
            TBlistStmt.execute();                                               //��������� ������
            TBlistResult=TBlistStmt.getResultSet();                             //������� ��������� �������
            while (TBlistResult.next() && CUR_ID_MEGA=="0") {
                if (INPUT_ID_MEGA.equalsIgnoreCase(TBlistResult.getString(1))) {
                    CUR_ID_MEGA = INPUT_ID_MEGA;
                    System.out.println("��������������� ����: " + CUR_ID_MEGA);
                }
            }
            if (CUR_ID_MEGA.equalsIgnoreCase("0")) {
                System.out.println("������� ������������ �������� ID_MEGA: " + INPUT_ID_MEGA);
            }
            // ���� ������� ���������� ������           
            if (CUR_ID_MEGA != "0") {
                // ���������� ���������� ��� ������� ������ //
                Properties s = System.getProperties();
                SYS_FILE_SEP = s.getProperty("file.separator");
                OUT_FILE_DIR = s.getProperty("user.home") + SYS_FILE_SEP + "SMSNotifications" + SYS_FILE_SEP;
                if (new File(OUT_FILE_DIR).isDirectory()) {
                } else {
                    File Dir = new File(OUT_FILE_DIR);
                    Dir.mkdir();
                }
                // ���������� ����������� ���������� ������ ��� ������
                System.out.println("���������� ����������� ���������� ������...");
                MaxDebtCountStmt = DBConnection.prepareStatement(
                                   "SELECT ROUND(MAX(cnt), -1) as MAX_CNT FROM (" +
                                   "SELECT DC.DEPOSIT_MINOR, DC.DEPOSIT_MAJOR, DC.ID_MEGA, COUNT(*) as cnt" + 
                                   "  FROM deposit.cardservicedebt DC " + 
                                   " WHERE id_mega = ? " + 
                                   "   AND debtflag = 1 " + 
                                   "   AND day >= TO_DATE('01.02.2011','DD.MM.YYYY') " + 
                                   " GROUP BY deposit_minor, deposit_major, id_mega)");
                MaxDebtCountStmt.setString(1, CUR_ID_MEGA);
                MaxDebtCountStmt.execute();                                     //��������� ������
                MaxDebtCountResult = MaxDebtCountStmt.getResultSet();           //������� ��������� �������
                while (MaxDebtCountResult.next() && OUT_FILE_CNT==1) {
                    OUT_FILE_CNT = MaxDebtCountResult.getInt(1);
                }
                System.out.println("��� ������ ������ � ������ ������������ �������� ���������� " + OUT_FILE_CNT + " ������.");
                // �������� �������� ��������������
                System.out.println("�������� �������� �������������� �� ��...");
                AllCardDebtStmt = DBConnection.prepareStatement(
                                  "SELECT DC.ID_MAJOR, DC.ID_MINOR, DC.DEPOSIT_MAJOR, DC.DEPOSIT_MINOR, " +
                                  "       to_char(trunc(DC.DAY), 'DD.MM.YYYY') as DAY, NVL((SELECT CPT.cpt_name FROM nsi.card_pay_type CPT WHERE CPT.CPT_PT=DC.TXKIND),'��� ������') as DEBT_TYPE, " +
                                  "       DEBTCASH-(PAYCASH-CANCELCASH-RETURNCASH) as ACTIVE_DEBT, " +
                                  "       nvl(CARDNO, 0) as CARD_NUMBER " +
                                  "  FROM deposit.cardservicedebt DC " +
                                  " WHERE DC.id_mega = ? " +
                                  "   AND DC.debtflag = 1 " +
                                  "   AND DC.day >= TO_DATE('01.02.2011','DD.MM.YYYY') and rownum <= 1000");
                AllCardDebtStmt.setString(1, CUR_ID_MEGA);
                AllCardDebtStmt.execute();                                      //��������� ������
                AllCardDebtResult = AllCardDebtStmt.getResultSet();             //������� ��������� �������
                // ���������� ��������� �������, ����������� �������� �������������� ��������
                // ������������ ������ �������������
                System.out.println("������������ ��������� ������...");
                while (AllCardDebtResult.next()) {
                    // �������� �������� �����
                    DEPOSIT_MAJOR = AllCardDebtResult.getInt(3);
                    DEPOSIT_MINOR = AllCardDebtResult.getInt(4);
                    REP_CARD_NUMBER = AllCardDebtResult.getString(8);
                    REP_DEBT_DATE = AllCardDebtResult.getString(5);
                    REP_DEBT_TYPE = AllCardDebtResult.getString(6);
                    REP_ACTIVE_DEBT = AllCardDebtResult.getBigDecimal(7);
                    CardClassStmt = DBConnection.prepareStatement(
                                    "SELECT CC.name " +
                                    "  FROM nsi.card_class CC, deposit.dcard DDC " +
                                    " WHERE CC.id_mega = ? " +
                                    "   AND CC.code_asf = DDC.codecard " +
                                    "   AND DDC.deposit_major = ? " +
                                    "   AND DDC.deposit_minor = ? " +
                                    "   AND nvl(DDC.numcard, 0) = ? " +
                                    "   AND rownum = 1");
                    CardClassStmt.setString(1, CUR_ID_MEGA);
                    CardClassStmt.setInt(2, DEPOSIT_MAJOR);
                    CardClassStmt.setInt(3, DEPOSIT_MINOR);
                    CardClassStmt.setString(4, REP_CARD_NUMBER);
                    CardClassStmt.execute();
                    CardClassResult = CardClassStmt.getResultSet();
                    while (CardClassResult.next()) {
                        REP_CARD_TYPE = CardClassResult.getString(1);
                    }
                    if (REP_CARD_TYPE == null) {REP_CARD_TYPE = "��� ������";}
                    //  �������� ���������� �� ������� //
                    ClientInfoStmt = DBConnection.prepareStatement(
                                     "SELECT DD.person_major, DD.person_minor, " +
                                     "       CP.surname||' '||CP.firstname||' '||CP.secondname as FIO, DD.branchno||'/'||DD.office as OSB, " +
                                     "       decode(DD.currency, 810, 'RUB', 840, 'USD', 978, 'EUR', DD.currency) as currency, " +
                                     "       nvl(replace(replace(replace(CP.mobphone,'('),')'),'-'), '��� ������') as mobphone, " +
                                     "       nvl(CP.mobphone, '��� ������') as mobphone_orig, " +
                                     "       nvl(CP.ACTADDRESS, '��� ������') as address_2 " +
                                     "  FROM deposit.deposit DD, client.person CP " +
                                     " WHERE DD.id_mega = ? " +
                                     "   AND DD.id_major = ? " +
                                     "   AND DD.id_minor = ? " +
                                     "   AND CP.id_mega = ? " +
                                     "   AND DD.person_major=CP.id_major" +
                                     "   AND DD.person_minor=CP.id_minor");
                    ClientInfoStmt.setString(1, CUR_ID_MEGA);
                    ClientInfoStmt.setInt(2, DEPOSIT_MAJOR);
                    ClientInfoStmt.setInt(3, DEPOSIT_MINOR);
                    ClientInfoStmt.setString(4, CUR_ID_MEGA);
                    ClientInfoStmt.execute();
                    ClientInfoResult = ClientInfoStmt.getResultSet();
                    while (ClientInfoResult.next()) {
                        PERSON_MAJOR = ClientInfoResult.getInt(1);
                        PERSON_MINOR = ClientInfoResult.getInt(2);
                        REP_FIO = ClientInfoResult.getString(3);
                        REP_BRANCH_FIL = ClientInfoResult.getString(4);
                        REP_CURRENCY = ClientInfoResult.getString(5);
                        REP_MOBPHONE = ClientInfoResult.getString(6);
                        REP_MOBPHONE_OLD = ClientInfoResult.getString(7);
                        REP_ADDRESS_2 = ClientInfoResult.getString(8);
                    }
                    // ������������ ������ ���������� ���������
                    if (REP_MOBPHONE.length()==10 && REP_MOBPHONE.substring(0, 1).equals("9")) {
                        REP_MOBPHONE = "+7".concat(REP_MOBPHONE);
                    } else if (REP_MOBPHONE.length()==11 && REP_MOBPHONE.substring(0, 2).equals("89") && REP_MOBPHONE.substring(7, 8) != "!") {
                        REP_MOBPHONE = "+7".concat(REP_MOBPHONE.substring(1));
                    } else if (REP_MOBPHONE.length()==11 && REP_MOBPHONE.substring(0, 2).equals("79") && REP_MOBPHONE.substring(7, 8) != "!") {
                        REP_MOBPHONE = "+".concat(REP_MOBPHONE);
                    } else if (REP_MOBPHONE.length()==11 && REP_MOBPHONE.substring(7, 9).equals("!9")) {
                        REP_MOBPHONE = "+7".concat(REP_MOBPHONE.substring(8, 11) + REP_MOBPHONE.substring(0, 7));
                    }
                    //  �������� �������� ����� //
                    SecondAddressStmt = DBConnection.prepareStatement(
                                        "SELECT CASE " +
                                        "       WHEN CA.zip is null AND CA.country is NULL AND CA.region is NULL AND CA.city is NULL AND CA.street is NULL AND CA.house is NULL " +
                                        "       THEN '��� ������' " +
                                        "       ELSE (CASE WHEN (CA.zip is NULL OR CA.zip = '000000') THEN '��� ��������� �������' ELSE CA.zip END ||" +
                                        "             CASE WHEN CA.country is NULL THEN '' ELSE ', '||CA.country END ||" +
                                        "             CASE WHEN CA.region is NULL THEN '' ELSE ', '||CA.region END ||" + 
                                        "             CASE WHEN CA.city is NULL THEN '' ELSE ', '||CA.city END ||" +
                                        "             CASE WHEN (CA.street is NULL OR CA.street = '0') THEN '' ELSE ', ��.'||CA.street END ||" +
                                        "             CASE WHEN (CA.house is NULL OR CA.house = '0') THEN '' ELSE ', �.'||CA.house END ||" +
                                        "             CASE WHEN (CA.section is NULL OR CA.section = '0') THEN '' ELSE ', ������ '||CA.section END ||" +
                                        "             CASE WHEN (CA.flat is NULL OR CA.flat = '0') THEN '' ELSE ', ��.'||CA.flat END ||" +
                                        "             ' ('||decode(CA.kind, 0, '����� ��������', 1, '����� ��������', 2, '����� ����������', CA.kind)||')') " + 
                                        "        END as address_1 " +
                                        "  FROM client.address CA " +
                                        " WHERE CA.person_major = ? " +
                                        "   AND CA.person_minor = ? " +
                                        "   AND CA.id_mega = ? " +
                                        "   AND CA.isactual = 1 AND rownum = 1");
                    SecondAddressStmt.setInt(1, PERSON_MAJOR);
                    SecondAddressStmt.setInt(2, PERSON_MINOR);
                    SecondAddressStmt.setString(3, CUR_ID_MEGA);
                    SecondAddressStmt.execute();
                    SecondAddressResult = SecondAddressStmt.getResultSet();
                    while (SecondAddressResult.next()) {
                        REP_ADDRESS_1 = SecondAddressResult.getString(1);
                    }
                    // ��������� ������
                    CUR_ROW = REP_MOBPHONE + ";" +
                              REP_MOBPHONE_OLD + ";" +
                              AllCardDebtResult.getString(5) + ";" +
                              REP_ACTIVE_DEBT + ";" +
                              REP_CURRENCY + ";" +
                              AllCardDebtResult.getString(6) + ";" +
                              REP_BRANCH_FIL + ";" +
                              AllCardDebtResult.getString(8) + ";" +
                              REP_CARD_TYPE + ";" +
                              REP_FIO + ";" +
                              REP_ADDRESS_1 + ";" +
                              REP_ADDRESS_2;
                    // ��������� ������ � ������                    
                    OutputDataArray.add(CUR_ROW);
                    // ���� ��������� ���������� ��� ���������� �����
                    REP_CARD_TYPE = null;
                    REP_FIO = null;
                    REP_BRANCH_FIL = null;
                    REP_CURRENCY = null;
                    REP_MOBPHONE = null;
                    REP_MOBPHONE_OLD = null;
                    REP_ADDRESS_2 = null;
                    REP_ADDRESS_1 = null;
                    // ������ �� �����
                    try {CardClassStmt.close();} catch (Exception e1) {}         //��������� ���������� �������
                    try {CardClassResult.close();} catch (Exception sqle) {}     //��������� ���������� � ����������� ������� � ��
                    try {SecondAddressStmt.close();} catch (Exception sqle) {}   //��������� ���������� �������
                    try {SecondAddressResult.close();} catch (Exception sqle) {} //��������� ���������� � ����������� ������� � ��
                    try {ClientInfoStmt.close();} catch (Exception sqle) {}      //��������� ���������� �������
                    try {ClientInfoResult.close();} catch (Exception sqle) {}    //��������� ���������� � ����������� ������� � ��
                }
                // ��������� ���������� ������ ������
                Collections.sort(OutputDataArray);
                // ������� ������ ����� (���� ��� ����)
                for (int i = 0; i < OUT_FILE_CNT; i++) {
                    if (new File(OUT_FILE_DIR + "SMSNotifications" + "_" + (i+1) + ".csv").exists()) {
                        new File(OUT_FILE_DIR + "SMSNotifications" + "_" + (i+1) + ".csv").delete();
                    }
                }
                if (new File(OUT_FILE_DIR + "SMSNotifications" + "_BadPhone" + ".csv").exists()) {
                    new File(OUT_FILE_DIR + "SMSNotifications" + "_BadPhone" + ".csv").delete();
                }
                // ������������ ������ � ���������� �� � �����
                System.out.println("���������� ������ � �����...");
                for (int i = 0; i < OutputDataArray.size(); i++) {
                    if (CUR_ROW_NUM <= OUT_FILE_CNT) {
                        if (OutputDataArray.get(i).substring(0, 2).equalsIgnoreCase("+7")) {
                            File OUT_FILE = new File(OUT_FILE_DIR + "SMSNotifications" + "_" + CUR_ROW_NUM + ".csv");
                            BufferedOutputStream OUT_STREAM = new BufferedOutputStream(new FileOutputStream(OUT_FILE, true));
                            OUT_STREAM.write((OutputDataArray.get(i)+"\r\n").getBytes("Cp1251"));
                            OUT_STREAM.close();
                            CUR_ROW_NUM++;
                            REP_GOOD_CNT++;
                        } else {
                            File OUT_FILE = new File(OUT_FILE_DIR + "SMSNotifications" + "_BadPhone" + ".csv");
                            BufferedOutputStream OUT_STREAM = new BufferedOutputStream(new FileOutputStream(OUT_FILE, true));
                            OUT_STREAM.write((OutputDataArray.get(i)+"\r\n").getBytes("Cp1251"));
                            OUT_STREAM.close();
                            REP_BAD_CNT++;
                        }
                    } else {
                        CUR_ROW_NUM = 1;
                        if (OutputDataArray.get(i).substring(0, 2).equalsIgnoreCase("+7")) {
                            File OUT_FILE = new File(OUT_FILE_DIR + "SMSNotifications" + "_" + CUR_ROW_NUM + ".csv");
                            BufferedOutputStream OUT_STREAM = new BufferedOutputStream(new FileOutputStream(OUT_FILE, true));
                            OUT_STREAM.write((OutputDataArray.get(i)+"\r\n").getBytes("Cp1251"));
                            OUT_STREAM.close();
                            CUR_ROW_NUM++;
                            REP_GOOD_CNT++;
                        } else {
                            File OUT_FILE = new File(OUT_FILE_DIR + "SMSNotifications" + "_BadPhone" + ".csv");
                            BufferedOutputStream OUT_STREAM = new BufferedOutputStream(new FileOutputStream(OUT_FILE, true));
                            OUT_STREAM.write((OutputDataArray.get(i)+"\r\n").getBytes("Cp1251"));
                            OUT_STREAM.close();
                            REP_BAD_CNT++;
                        }
                    }
                }
                // ������� ����������
                System.out.println("���������� �������: " + (REP_GOOD_CNT + REP_BAD_CNT) + ", Good: " + REP_GOOD_CNT + ", Bad: " + REP_BAD_CNT + ".");
            } else {
                System.out.println("��� ������� ������� ������������ ���������.");    
            }
        } finally {
           try {DBConnection.close();} catch (Exception e) {}                   //��������� ����������
           try {AllCardDebtStmt.close();} catch (Exception e) {}                //��������� ���������� �������
           try {AllCardDebtResult.close();} catch (Exception e) {}              //��������� ���������� � ����������� ������� � ��
           try {TBlistStmt.close();} catch (Exception e) {}                     //��������� ���������� �������
           try {TBlistResult.close();} catch (Exception e) {}                   //��������� ���������� � ����������� ������� � ��
           try {OutFile.close();} catch (Exception e) {}                        //��������� ������ � ����
           try {OutputDataArray.notify();} catch (Exception e) {}               //��������� ������
        }
    }
}