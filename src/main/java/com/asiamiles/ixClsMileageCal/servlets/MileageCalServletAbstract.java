package com.asiamiles.ixClsMileageCal.servlets;
import java.util.HashMap;

import javax.servlet.Servlet;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.asiamiles.ixClsMileageCal.beans.calculator.MileageCalConstants;
import com.asiamiles.ixClsMileageCal.beans.common.Constants;
import com.asiamiles.ixClsMileageCal.beans.common.Log;
import com.cathaypacific.clsUtil.UtilBean;
import com.cathaypacific.utility.log.LogUtil;
import com.cathaypacific.utility.log.ThreadLocalUtil;

public abstract class MileageCalServletAbstract extends HttpServlet implements Servlet 
{
	// asbstract variables
	protected String Session_Err;
	
	//private static Log logger;
	public static UtilBean util = UtilBean.getInstance();
	
	public void init(ServletConfig pConf)
		throws ServletException
	{
		super.init(pConf);
	}

	protected void doPost(HttpServletRequest pReq, HttpServletResponse pRes)
		throws ServletException 
	{
		doProcess(pReq, pRes);
	}

	protected void doGet(HttpServletRequest pReq, HttpServletResponse pRes)
		throws ServletException 
	{
		// action not allow... but vgn call.......!!!
		doProcess(pReq, pRes);
	}

	protected void doProcess(HttpServletRequest pReq, HttpServletResponse pRes)
		throws ServletException 
	{
		//cpppep ADD for AML31473 20140825 START  update am logger
		String memberId = pReq.getParameter(MileageCalConstants.REQUEST_PARAMETER_MEMBERID);
		ThreadLocalUtil.setCorrelationInfo(
			LogUtil.createCorrelationInfo(pReq.getSession().getId(), ("-1".equals(memberId) || null == memberId)?"":memberId, "")
		);
		//cpppep ADD for AML31473 20140825 END
		requestProcessor(pReq, pRes);
	}

	protected static void writeDebugLog(String pLog)
    {
    	Log.writeDebugLog(pLog);
    }

	protected static void writeInfoLog(String pLog)
    {
    	Log.writeInfoLog(pLog);
    }

	protected static void writeErrorLog(String pLog)
    {
    	Log.writeErrorLog(pLog);
    }

    protected abstract void requestProcessor(HttpServletRequest pReq, HttpServletResponse pRes);
        
    protected void populateErrorAttributes(HttpServletRequest pReq, int errorCode, String otherMessage)
	{		
		if(pReq != null) {
			HttpSession session = pReq.getSession();
			HashMap errHash = new HashMap();
			errHash.put(Constants.HASH_STATUS_CODE, Integer.toString(errorCode));
			errHash.put(Constants.HASH_ERR_MSG, otherMessage);
			session.setAttribute(Session_Err, errHash);
		}
	}
}
