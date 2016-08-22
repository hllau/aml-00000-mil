package com.asiamiles.ixClsMileageCal;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public interface MileageCalHandlerIF 
{
	
	/**
	 * Return the specific jsp page according to the request event.
	 * 
	 * @param request Object that encapsulates the request to the servlet
	 * @param response Object that encapsulates the response from the servlet
	 * @return url string for dispatch
	 */
	abstract String execute(HttpServletRequest pReq, HttpServletResponse pRes);

}