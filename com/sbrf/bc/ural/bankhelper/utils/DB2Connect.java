package com.sbrf.bc.ural.bankhelper.utils;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;

import com.sberbank.sbclients.util.dao.ConnectionSource;

public class DB2Connect
{
	private ILog log;
    Connection connection = null;   
    ResultSet resultSet = null;   
    PreparedStatement statement = null;   
    //private Savepoint savepoint= null;

    public DB2Connect(ILog logger,ConnectionSource cs)
    {
    	this.log = logger;
    	log.info("open connection...");
    	connection = cs.getConnection();
    }
    public DB2Connect(ILog logger,Connection conn)
    {
    	this.log = logger;
    	connection = conn;
    }
    public void beginTransaction()
    {
		if (connection==null) throw new RuntimeException("not connected!");
    	try
		{
    		connection.setAutoCommit(false);
			//savepoint = connection.setSavepoint();
		} catch (SQLException e)
		{
			throw new XSQLException("sql error",e);
		}
    }
    public void rollback()
    {
		if (connection==null) throw new RuntimeException("not connected!");
    	try
		{
    		connection.rollback();
			//connection.rollback(savepoint);
		} catch (SQLException e)
		{
			throw new XSQLException("sql error",e);
		}
    }
    public void commit()
    {
		if (connection==null) throw new RuntimeException("not connected!");
    	try
		{
			connection.commit();
		} catch (SQLException e)
		{
			throw new XSQLException("sql error",e);
		}
    }
	public void createStatement(String sql)
	{
		if (connection==null) throw new RuntimeException("not connected!");
		if (statement!=null) closeStatement();
		//log.trace(sql);
		try
		{
			statement = connection.prepareStatement(sql);
		} catch (SQLException e)
		{
			log.warning(e.getSQLState());
			throw new RuntimeException("error create statement",e);
		}
	}
	public void clearParameters() throws SQLException
	{
		if (connection==null) throw new RuntimeException("not connected!");
		if (statement==null) throw new RuntimeException("statement not exists!");
		statement.clearParameters();
	}
	public void setString(int pIndex,String value) throws SQLException
	{
		if (connection==null) throw new RuntimeException("not connected!");
		if (statement==null) throw new RuntimeException("statement not exists!");
		statement.setString(pIndex, value);
	}
	public void setDate(int pIndex,Date value) throws SQLException
	{
		if (connection==null) throw new RuntimeException("not connected!");
		if (statement==null) throw new RuntimeException("statement not exists!");
        java.sql.Date d_sql = new java.sql.Date(value.getTime());
		statement.setDate(pIndex, d_sql);
	}
	public int executeUpdate()
	{
		if (connection==null) throw new RuntimeException("not connected!");
		if (statement==null) throw new RuntimeException("statement not exists!");
		if (resultSet!=null) throw new RuntimeException("resultSet already exists!");
		try
		{
			int cnt =  statement.executeUpdate();
			return cnt;
		} catch (SQLException e)
		{
			//log.error("sql_state"+e.getSQLState()+" code="+e.getErrorCode()+" "+e.getMessage());
			throw new XSQLException("sql error",e);
		} 
	}
	public int executeQuery_returnInt()
	{
		ResultSet r = executeQuery();
		int rez = 0;
		try
		{
			if (!r.next()) throw new RuntimeException("запрос ничего не возвратил!");
			rez = r.getInt(1);
			
		} catch (SQLException e)
		{
			throw new RuntimeException("sql error",e);
		}
		closeResultSet();
		return rez;
	}
	public ResultSet executeQuery()
	{
		if (connection==null) throw new RuntimeException("not connected!");
		if (statement==null) throw new RuntimeException("statement not exists!");
		if (resultSet!=null) throw new RuntimeException("resultSet already exists!");
		try
		{
			resultSet = statement.executeQuery();
			return resultSet;
		} catch (SQLException e)
		{
			//log.error("sql_state"+e.getSQLState()+" code="+e.getErrorCode()+" "+e.getMessage());
			throw new XSQLException("sql error",e);
		} 
	}
	public void closeResultSetAndStatement()
	{
		if (resultSet!=null) 
			try{resultSet.close();}catch(Exception e){}
		resultSet = null;

		if (statement!=null) 
			try{statement.close();}catch(Exception e){}
		statement = null;
	}
	public void closeResultSet()
	{
		if (resultSet!=null) 
			try{resultSet.close();}catch(Exception e){}
		resultSet = null;
	}
	public void closeStatement()
	{
		if (statement!=null) 
			try{statement.close();}catch(Exception e){}
		statement = null;
	}
	public void close()
	{
		log.trace("closing connection...");
		closeResultSetAndStatement();
		if (connection!=null) 
			try{connection.close();}catch(Exception e){}
		connection = null;
	}
}
