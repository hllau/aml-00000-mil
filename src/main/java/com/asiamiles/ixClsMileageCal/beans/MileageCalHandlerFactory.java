package com.asiamiles.ixClsMileageCal.beans;

import com.asiamiles.ixClsMileageCal.GenericMileageCal;
import com.asiamiles.ixClsMileageCal.MileageCalHandlerIF;


/**
 * @version 	1.0
 * @author
 */
public class MileageCalHandlerFactory extends GenericMileageCal
{
	private static final String PACKAGE_PATH = "com.asiamiles.ixClsMileageCal.beans.";
	private static MileageCalHandlerFactory instance;

	/**
	 * get CommandFactory object
	 *
	 * @return object of CommandFactory
	 */
	public static MileageCalHandlerFactory getInstance()
	{                
		if (instance == null) {
			instance = new MileageCalHandlerFactory();
		}
		return instance;
	}

	/**
	* get the command handler class
	*
	* @param command action type
	* @return class of command handler
	*/
	public MileageCalHandlerIF getCommandHandler(String pClassName) 
	{
		MileageCalHandlerIF cmdHandler = null;
		try {
			if(pClassName==null || pClassName.trim().length()<=0)
				return null;                        
			Class handlerClass = Class.forName(PACKAGE_PATH + pClassName);
			cmdHandler = (MileageCalHandlerIF)handlerClass.newInstance();

		} catch (ClassNotFoundException cnfe) {
			writeErrorLog("CommandFactory>ClassNotFoundException: " + cnfe);
		} catch (Exception e) {
			writeErrorLog("CommandFactory>Exception: " + e);
		} finally {
			
		}
		
		return cmdHandler;
	}
}