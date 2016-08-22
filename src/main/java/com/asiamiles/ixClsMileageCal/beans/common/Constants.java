/*
 * Created on Nov 30, 2007
 *
 * 
 * Window - Preferences - Java - Code Style - Code Templates
 */
package com.asiamiles.ixClsMileageCal.beans.common;

/**
 * @author CPPALAC
 *
 * 
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class Constants {
	
	private Constants () { }
	public static Constants getInstance() 
	{
		if (instance == null) {
			instance = new Constants();
		}
		return instance;
	}
	public Object clone() throws CloneNotSupportedException
	{
		throw new CloneNotSupportedException(); 
	}

	
	private static String OS_NAME = System.getProperty("os.name");
	private static boolean ISWINDOWS = (OS_NAME.indexOf("Windows") >= 0);
	public static boolean isWindows()
	{
		return ISWINDOWS;
	}
	
	public static class REDEEMFROMPORT 
	{
		public static String SESSION_VIEW = "REDEEMFROMPORTVIEW";
		public static String SESSION_ERR = "REDEEMFROMPORTERR";
	}
	
	public static class REDEEMTOPORT 
	{
		public static String SESSION_VIEW = "REDEEMTOPORTVIEW";
		public static String SESSION_ERR = "REDEEMTOPORTERR";
	}
	
	public static class REDEEMCARRIER 
	{
		public static String SESSION_VIEW = "REDEEMCARRIERVIEW";
		public static String SESSION_ERR = "REDEEMCARRIERERR";
	}
	
	public static class EARNFROMPORT 
	{
		public static String SESSION_VIEW = "EARNFROMPORTVIEW";
		public static String SESSION_ERR = "EARNFROMPORTERR";
	}
	
	public static class EARNTOPORT 
	{
		public static String SESSION_VIEW = "EARNTOPORTVIEW";
		public static String SESSION_ERR = "EARNTOPORTERR";
	}

	public static class AVAILABLECLASSES 
	{
		public static String SESSION_VIEW = "AVAILABLECLASSESVIEW";
		public static String SESSION_ERR = "AVAILABLECLASSESERR";
	}
	
	public static String HASH_STATUS_CODE = "statusCode";
	public static String HASH_ERR_MSG = "errorMsg";	
	public static String HASH_SUCCESS_STATUS_CODE = "0000";
	
	public static String ENCODING = "UTF-8";
	public static String CONTENT_TYPE = "text/html;charset=UTF-8";
	
	public static String STR_CONTENT_MAP = "MILEAGECAL";
	
	private static Constants instance;
}
