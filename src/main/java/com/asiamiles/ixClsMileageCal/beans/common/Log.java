package com.asiamiles.ixClsMileageCal.beans.common;

import com.cathaypacific.utility.log.CorrelationInfo;
import com.cathaypacific.utility.log.LogUtil;
import com.cathaypacific.utility.log.ThreadLocalUtil;

import java.util.Date;
import java.util.Map;


/**
 * Common logger used by ammember
 * 
 */
public class Log
{  	

	public static Log getInstance() 
	{
		if (instance == null) {
			instance = new Log();
//			com.cathaypacific.clsUtil.Log.setLogger("IXCLSMILEAGECAL");
		}
		return instance;
	}
	
	public Object clone() throws CloneNotSupportedException
	{
		throw new CloneNotSupportedException(); 
	}

	/**
	* Write log in debug level
	* 
	*/
	public static void writeDebugLog(String pLog)
    {
		//cpppep UPDATE for AML31473 20140821 START
		//com.cathaypacific.clsUtil.Log.writeDebugLog(pLog);
		String finalMsg = getFianlMsg(pLog);
		System.out.println(new Date().toString()+" debug:"+pLog);
		//com.cathaypacific.clsUtil.Log.writeDebugLog(finalMsg);
		//cpppep UPDATE for AML31473 20140821 END
    }

	/**
	* Write log in info level
	* 
	*/
	public static void writeInfoLog(String pLog)
    {
		//cpppep UPDATE for AML31473 20140821 START
		//com.cathaypacific.clsUtil.Log.writeInfoLog(pLog);
		String finalMsg = getFianlMsg(pLog);
		System.out.println(new Date().toString()+" info:"+finalMsg);
//		com.cathaypacific.clsUtil.Log.writeInfoLog(finalMsg);
		//cpppep UPDATE for AML31473 20140821 END
    }

	/**
	* Write log in error level
	* 
	*/
	public static void writeErrorLog(String pLog)
    {
		//cpppep UPDATE for AML31473 20140821 START
		//com.cathaypacific.clsUtil.Log.writeErrorLog(pLog);
		String finalMsg = getFianlMsg(pLog);
		System.out.println(new Date().toString()+" error:"+finalMsg);
//		com.cathaypacific.clsUtil.Log.writeErrorLog(finalMsg);
		//cpppep UPDATE for AML31473 20140821 END
    }
	
	private static Log instance;
	
	
	
	//cpppep ADD for AML31473 20140821 START
	private static String getFianlMsg(String pLog){
		String classAndMethod = getClassAndMethod(0);
		String machine = LogUtil.getMachine();
		CorrelationInfo correlationInfo = ThreadLocalUtil.getCorrelationInfo();
		Map corrMap = LogUtil.getCorrelationMsgMap(correlationInfo);
		String correlationID = corrMap.get("correlationId").toString();
        String msg = corrMap.get("correlationMsg").toString();
        return LogUtil.getMessage(classAndMethod, correlationID, machine, msg, pLog);
	}
	
	private static String getClassAndMethod(final int deep) {
        final StackTraceElement[] stes = new Throwable().getStackTrace();
        for (int i = 0; i < stes.length; i++) {
        	final String className = stes[i].getFileName();
            final String methodName = stes[i].getMethodName();
            if (!"Log.java".equals(className) && methodName.indexOf("$") == -1
            		&& !"writeDebugLog".equals(methodName) && !"writeInfoLog".equals(methodName) && !"writeErrorLog".equals(methodName)) {
            	if((i+deep) < stes.length){
            		return " Method: " +  stes[i+deep].getFileName() +  "\t "  + stes[i+deep].getMethodName() + "(...)" + "\t";
            	}else{
            		return " Method: " +  className +  "\t "  + methodName + "(...)" + "\t";
            	}
            }
		}
        return " ";
    }
	//cpppep ADD for AML31473 20140821 END

}
