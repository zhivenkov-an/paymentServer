package com.sbrf.bc.ural.bankhelper.utils;

import java.sql.SQLException;

import com.ibm.db2.jcc.DB2Diagnosable;
import com.ibm.db2.jcc.DB2Sqlca;

@SuppressWarnings("serial")
public class XSQLException extends RuntimeException 
{
	private String sql_msg = "diagnostic message not found";
	public XSQLException(SQLException throwable) {
		super(throwable);
		sql_msg2String(throwable);
	}

	public XSQLException(String string, SQLException throwable) {
		super(string, throwable);
		sql_msg2String(throwable);
	}

	public XSQLException(String string) {
		super(string);
	}

	public XSQLException() {
	}
	@Override
	public String getMessage()
	{
		return super.getMessage()+" "+sql_msg;
	}

	private void sql_msg2String(SQLException e)
	{
		if (e instanceof DB2Diagnosable) 
		{
			DB2Diagnosable diag = (DB2Diagnosable)e;
			try {
				DB2Sqlca a = diag.getSqlca();
				if (a!=null) sql_msg = a.getMessage();
				else sql_msg = "null";
			} catch (SQLException e1) {
				sql_msg =  "error on retrive error message ";
			}
		}
	}
}
