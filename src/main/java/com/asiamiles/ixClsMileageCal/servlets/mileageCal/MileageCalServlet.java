package com.asiamiles.ixClsMileageCal.servlets.mileageCal;

import javax.servlet.RequestDispatcher;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.asiamiles.ixClsMileageCal.MileageCalHandlerIF;
import com.asiamiles.ixClsMileageCal.beans.MileageCalHandlerFactory;
import com.asiamiles.ixClsMileageCal.servlets.MileageCalServletAbstract;
import com.cathaypacific.clsUtil.security.SecurityBean;

public class MileageCalServlet extends MileageCalServletAbstract
{	
	private static final String APP_NAME = "MILECAL";
	private static MileageCalHandlerFactory cmdFactory = MileageCalHandlerFactory.getInstance();
	private static SecurityBean security = SecurityBean.getInstance();

	public void requestProcessor(HttpServletRequest pReq,HttpServletResponse pRes)
	{
		writeInfoLog("MileageCalServlet>requestProcessor()");
		boolean error = false; 
		if (security.isAllowUser(APP_NAME, pReq) && security.specificSecurityCheck(APP_NAME, pReq)) {
			// get command handler
			String handlerName = security.getHandlerName(APP_NAME, pReq);
			MileageCalHandlerIF cmdHandler = cmdFactory.getCommandHandler(handlerName);
			String url = cmdHandler.execute(pReq, pRes);

			if(url != null && url.trim().length()>0) {
				RequestDispatcher dispatcher = pReq.getRequestDispatcher(url);
				if(dispatcher != null) {
					try {
						dispatcher.forward(pReq, pRes);
					} catch (Exception e) {
						error = true;
						populateErrorAttributes(pReq, 1100, "Dispatcher to " + url + " got exception: ");
						writeErrorLog("MileageCalServlet>requestProcessor: Dispatcher to " + url + " got exception");
					}	
				} else {
					error = true;
					populateErrorAttributes(pReq, 1100, "unable to dispatch request to " + url);
					writeErrorLog("MileageCalServlet>requestProcessor: Unable to dispatch request to " + url);
				}
			} else {
				error = true;
				populateErrorAttributes(pReq, 1100, "null or empty return url");
				writeErrorLog("MileageCalServlet>requestProcessor: Empty url returned");
			}	
		} else {
			error = true;
			populateErrorAttributes(pReq, 1108, "MileageCalServlet: Invalid security checking");
		}

		if (error) {
			// redirect to error page
			try {
				pReq.getRequestDispatcher("/mileageCal/mileageCal.jsp").forward(pReq, pRes);
			}catch (Exception e) {
				//dont want to empty catch
				//e.printStackTrace(System.err);
				writeErrorLog("MileageCalServlet: "+e);
			}
		}
	}

}