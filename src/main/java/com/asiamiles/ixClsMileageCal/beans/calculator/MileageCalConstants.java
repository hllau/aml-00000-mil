/*
 * Created on Aug 28, 2008
 *
 * 
 * Window - Preferences - Java - Code Style - Code Templates
 */
package com.asiamiles.ixClsMileageCal.beans.calculator;


/**
 * @author CPPALAC
 *
 * 
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class MileageCalConstants {

	private MileageCalConstants () { }
	public static MileageCalConstants getInstance() 
	{
		if (instance == null) {
			instance = new MileageCalConstants();
		}
		return instance;
	}
	public Object clone() throws CloneNotSupportedException
	{
		throw new CloneNotSupportedException(); 
	}
	
	// request parameters
	public static final String REQUEST_PARAMETER_ORIGIN_CODE		= "origin";
	public static final String REQUEST_PARAMETER_DESTINATION_CODE 	= "destination";
	public static final String REQUEST_PARAMETER_AIRLINE_CODE 		= "airline";
	public static final String REQUEST_PARAMETER_ORIGIN_TEXT		= "origin_text_";
	public static final String REQUEST_PARAMETER_DESTINATION_TEXT 	= "destination_text_";
	public static final String REQUEST_PARAMETER_CLASS_CODE 		= "class";
	public static final String REQUEST_PARAMETER_AWARDTYPE_CODE 	= "awardType";
	public static final String REQUEST_PARAMETER_MEMBERID	 		= "memberID";
	public static final String REQUEST_PARAMETER_AWARDCLASS_CODE 	= "awardClass";
	public static final String REQUEST_PARAMETER_SECTOR_NO			= "noOfSector";
	
	public static final String REQUEST_PARAMETER_LANGUAGE			= "lang";
	public static final String REQUEST_PARAMETER_CALCULATOR_IND		= "calculatorInd";
	public static final String REQUEST_PARAMETER_CALCULATOR_ACTION	= "calculatorAction";
	public static final String REQUEST_PARAMETER_SUBMIT_TYPE		= "submitType";
			
	// value of the parameters
	public static final String CALCULATOR_IND_SPEND				= "spend";
	public static final String CALCULATOR_IND_EARN 				= "earn";
	
	public static final String CALCULATOR_ACTION_ROUNDTRIP		= "roundtrip";
	public static final String CALCULATOR_ACTION_ONEWAY			= "oneway";
	
	public static final String SUBMIT_TYPE_FORM					= "FORM";
	public static final String SUBMIT_TYPE_CALCULATE			= "CALCULATE";
	public static final String SUBMIT_TYPE_CALCULATE_ALT_ITIN	= "CALCULATE_ALT-ITIN";
	public static final String SUBMIT_TYPE_ADDSECTOR			= "ADDSECTOR";
				
	
	// return parameters objects
	public static final String REQUESTION_OBJ_BASIC_PARAM		= "basicParam";
	public static final String REQUESTION_OBJ_MILEAGE_RTN		= "MileageReturn";
	

	private static MileageCalConstants instance;
}
