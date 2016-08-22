package com.asiamiles.ixClsMileageCal.beans.common;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import com.asiamiles.ixClsMileageCal.GenericMileageCal;
//import com.cathaypacific.utility.DatabaseUtility;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.sql.DataSource;


public class McDAO extends GenericMileageCal
{
	static Context envContext;
	public static McDAO getInstance() 
	{
		if (instance == null) {
			instance = new McDAO();
			try {
				Context initContext = new InitialContext();
				envContext  = (Context)initContext.lookup("java:/comp/env");


			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return instance;
	}
	

	public Object clone() throws CloneNotSupportedException
	{
		throw new CloneNotSupportedException(); 
	}
	
	public Connection getConnection()
	{
		try {
			DataSource ds = (DataSource)envContext.lookup("jdbc/asia_mile_datasource");
			Connection conn = ds.getConnection();
//			Connection conn =
//                    DriverManager.getConnection("jdbc:mysql://localhost/asia_mile?" +
//                            "user=demo&password=password");
//		Connection conn = DatabaseUtility.getDSConnection("ixClsMileageCal", "amcalc");
			conn.setAutoCommit(false);
			return conn;
		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException(e);
		}
	}

//	public Connection getConnection(String pApplCode, String pDbID) throws Exception
//	{
//		Connection conn = DatabaseUtility.getDSConnection(pApplCode, pDbID);
//		conn.setAutoCommit(false);
//		return conn;
//	}

	public void closeConnection(Connection pConn)
	{
		try {
			if(null!=pConn) {
				pConn.close();
				pConn = null;
			}
		} catch (Exception e) {
		}
	}

	private static McDAO instance;
	

}