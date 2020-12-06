package com.sbrf.bc.ural.bankhelper;

import java.io.FileInputStream;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Properties;


import com.sbrf.bc.ural.bankhelper.utils.DB2Connect;
import com.sbrf.bc.ural.bankhelper.utils.HashValueMaker;
import com.sbrf.bc.ural.bankhelper.utils.ILog;
import com.sbrf.bc.ural.bankhelper.utils.LogStub;
import com.sbrf.bc.ural.bankhelper.utils.XSQLException;

public class Main
{

	private static ILog log;
	private static LogStub log_ext;
	private static Properties prop;
	private static boolean print_stacktrace;

	public static void main(String[] args)
	{
		log = new LogStub();
		try
		{
			InputStream inStream = new FileInputStream("PS.properties");
			prop = new Properties();
			prop.load(inStream);
			inStream.close();
			log_ext = (LogStub)log;
			if (prop.getProperty("debug","false").equalsIgnoreCase("true")) log_ext.debug = true;
			if (prop.getProperty("print_stacktrace","false").equalsIgnoreCase("true")) print_stacktrace = true;
			log_ext.setEncoding(prop.getProperty("output_encoding", "cp1251"));
			String log_name = prop.getProperty("logFile");
			if (log_name!=null)
			{
				log_ext.openLogFile(log_name, prop.getProperty("log_encoding","cp1251"));
			}
		}
		catch (Exception e)
		{
			if (print_stacktrace) e.printStackTrace();
			else log.error(e.getMessage());
			log_ext.h_exception(e);
			log_ext.close();
			return;
		}
		if (args.length!=1)
		{
			log.error("ERROR:не указано имя пользователя");
			System.exit(-1);
		}
		String userName = args[0];
		DB2Connect db2_ps=null;
		try
		{
			db2_ps = new DB2Connect(log,connect(log,prop,"connection_ps"));
		}
		catch (Exception e)
		{
			if (print_stacktrace) e.printStackTrace();
			else log.error(e.getMessage());
			log_ext.h_exception(e);
			log_ext.close();
			System.exit(-1);
		}
		
		try
		{
			//exportTable(db2_ps_war,db2_ps_export);
			execute(db2_ps,userName);
			log.trace("OK");
		}
		catch (Exception e)
		{
//			db2_ps.rollback();
			if (print_stacktrace) e.printStackTrace();
			else log.error(e.getMessage());
			log_ext.h_exception(e);
			//logger.h_exception(e);
			//logger.warning("error message:"+e.getMessage());
			//logger.warning("error exception:"+e.getClass().toString());
		}
		finally
		{
			db2_ps.close();
			log_ext.close();
			//db2_ps_export.close();
			//logger.close();
		}

	}
	public static void execute(DB2Connect db2,String userName)
	{
		log.trace("userName="+userName);
		String userPass = prop.getProperty("password");
		//Random random = new Random(System.currentTimeMillis());
		//int passi = random.nextInt(9999);			
		userPass = "zgxv1234";
		//log.trace("password="+userPass);
		HashValueMaker hashValue = new HashValueMaker();
		
		String hash = hashValue.makePasswordHashValue(userName, userPass);
		log.trace("hashPassword="+hash);
		
		SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy");
		Date dstart = new Date();
		Calendar c = Calendar.getInstance();
		int exp_days = Integer.parseInt(prop.getProperty("password_expired_days","7"));
		c.add(Calendar.DAY_OF_YEAR, exp_days);
		Date dend = c.getTime();
		log.trace("start_date="+sdf.format(dstart)+" end_date="+sdf.format(dend));
		
		//update adm.uconf set uconf_pass='X8sjrgb4mJH6b6pk/V4CjRKQ==',uconf_dstart=current_date,uconf_dend=add_months(current_date,1), uconf_status=1,uconf_passst=1,uconf_pttz=0 where uconf_status<5 and uconf_log='vsp_viewer2'
		String sql = "update adm.uconf set uconf_pass=?,uconf_dstart=?,uconf_dend=?, uconf_status=1,uconf_passst=1,uconf_pttz=0 where uconf_status<5 and uconf_log=?";
		try
		{
			db2.createStatement(sql);
			db2.setString(1, hash);
			db2.setDate(2, dstart);
			db2.setDate(3, dend);
			db2.setString(4, userName);
			int i = db2.executeUpdate();
			if (i!=1) throw new RuntimeException("ошибка выполнения - пользователь:"+userName+" не найден!");
			
		} catch (SQLException e)
		{
			throw new XSQLException("sql error",e);
		}
		finally
		{
			db2.closeResultSetAndStatement();
		}
		((LogStub)log).print("_OK_[:PASSWORD_OUT"+userPass+"]");
	}
	public static Connection connect(ILog log,Properties prop,String conn_name)
	{
		String conn_str = prop.getProperty(conn_name, "");
		if (conn_str.equals("")) throw new RuntimeException("не найдена строка соединения:"+conn_name);
		String[] conn =  conn_str.trim().split("[,]");
//        String connect_string = Config.getConfig().get(section_name+":CONNECTION_STRING");
//        String user = Config.getConfig().get(section_name+":USERNAME");
//        String pass = Config.getConfig().get(section_name+":PASSWORD");
		Connection connection;
        log.trace("Connection to:"+conn[0]);
		try
		{
			//"jdbc:db2://192.168.137.149:50000/PAYSSRV"
			Class.forName("com.ibm.db2.jcc.DB2Driver");   
			connection = DriverManager.getConnection(conn[0], conn[1],conn[2]);
			log.trace("db ver "+connection.getMetaData().getDatabaseMajorVersion()+"."+connection.getMetaData().getDatabaseMinorVersion());
		}
		catch(ClassNotFoundException e)
		{
			throw new RuntimeException("не найден драйвер db2",e);
		} catch (SQLException e)
		{
			log.warning(e.getSQLState());
			throw new RuntimeException("error connection to db2",e);
		}
		return connection;
	}

}
