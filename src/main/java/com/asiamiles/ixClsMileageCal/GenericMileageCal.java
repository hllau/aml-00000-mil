package com.asiamiles.ixClsMileageCal;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;

import com.asiamiles.ixClsMileageCal.beans.common.CachingHash;
import com.asiamiles.ixClsMileageCal.beans.common.Log;
import com.asiamiles.ixClsMileageCal.beans.common.McDAO;
import com.asiamiles.ixClsMileageCal.beans.common.McPropBean;
import com.asiamiles.ixClsMileageCal.beans.common.TranslationDAO;
import com.cathaypacific.clsUtil.ContentBean;
import com.cathaypacific.clsUtil.DateFormatBean;
import com.cathaypacific.clsUtil.UtilBean;



public class GenericMileageCal 
{
	
	private static Log logger = Log.getInstance();
	public static McDAO mileageCalDao = McDAO.getInstance();
	public static McPropBean prop = McPropBean.getInstance();
	public static DateFormatBean dateFormatter = DateFormatBean.getInstance();
//	public static TranslationDAO translationDao = TranslationDAO.getInstance();
	public static UtilBean util = UtilBean.getInstance();
	public static CachingHash cachingHash = CachingHash.getInstance();
	

	public static Map content(String pLang, String pApplCode)
    {
    	return (Map)ContentBean.getContent(pLang, pApplCode);
	}

	public static void writeDebugLog(String pLog)
    {
    	Log.writeDebugLog(pLog);
    }

	public static void writeInfoLog(String pLog)
    {
		Log.writeInfoLog(pLog);
    }

	public static void writeErrorLog(String pLog)
    {
		Log.writeErrorLog(pLog);
    }
	
	public static String replace(String originalStr, String pattern, String replacedStr) {
		if (originalStr == null || pattern == null || replacedStr == null) {
			Log.writeErrorLog("GenericMileageCal.replace->"+"Original string: " + originalStr);
			Log.writeErrorLog("GenericMileageCal.replace->"+"Pattern: " + pattern);
			Log.writeErrorLog("GenericMileageCal.replace->"+"Replaced string: " + replacedStr);
			return originalStr;
		}
		int start = 0;
		int index = -1;
		while ((index = originalStr.indexOf(pattern, start)) > -1) {
			originalStr = originalStr.substring(0, index) + replacedStr + originalStr.substring(index + pattern.length());
			start = index + replacedStr.length();
		}
		return originalStr;
	}

	public static String getOneWayPriorityCode()
	{
		String rtnCde = "OUEC";
		
		final String APPLCODE = "MILEAGECAL";
		final String DEFAULT_LANG = "en";
		final String MESSAGE_CODE = "NEW_PRIORITY_CODE_START_DATE";
		final String DATETIME_FORMAT = "yyyyMMdd";
		
		Map contentMap;
		
		try
		{
			contentMap = (Map)content(DEFAULT_LANG, APPLCODE);
									
			Date now = new Date();
			// Get Date from
			DateFormat df = new SimpleDateFormat(DATETIME_FORMAT);
		    Date date_from = df.parse(UtilBean.getValueFromMap(contentMap, MESSAGE_CODE).trim());
		    
			if ( now.after(date_from))
			{
				rtnCde = "OPEC";
			}
			
		} catch (Exception e)
		{
		}
		
		writeInfoLog("getOneWayPriorityCode->"+rtnCde);
		
		return rtnCde;
		
	}
	
	public static String getPriorityCode()
	{
		String rtnCde = "UEC";
		
		final String APPLCODE = "MILEAGECAL";
		final String DEFAULT_LANG = "en";
		final String MESSAGE_CODE = "NEW_PRIORITY_CODE_START_DATE";
		final String DATETIME_FORMAT = "yyyyMMdd";
		
		Map contentMap;
		
		try
		{
			contentMap = (Map)content(DEFAULT_LANG, APPLCODE);
									
			Date now = new Date();
			// Get Date from
			DateFormat df = new SimpleDateFormat(DATETIME_FORMAT);
		    Date date_from = df.parse(UtilBean.getValueFromMap(contentMap, MESSAGE_CODE).trim());
		    
			if ( now.after(date_from))
			{
				rtnCde = "PEC";
			}
			
		} catch (Exception e)
		{
		}
		
		writeInfoLog("getPriorityCode->"+rtnCde);
		
		return rtnCde;
		
	}
}
