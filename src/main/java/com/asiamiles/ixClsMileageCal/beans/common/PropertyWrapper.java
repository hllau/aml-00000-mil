package com.asiamiles.ixClsMileageCal.beans.common;

import java.util.*;
import java.io.*;

/**
 * <p> PropertyWrapper is used to contain the contents of the properties file
 * in a key-value pairs format. The characters in properties file must be unicode if
 * they are non-Latin 1 and non-Unicode. You can try to use "natvie2ascii" to convert
 * the properties files into unicode format.
 *
 * <p> Created at: 4/DEC/2000
 * <br> Updated at: 29/MAR/2001
 *
 * @author	Maggie Yip (IMT E-Business)
 * @version	%I%, %G%
 * @since	JDK1.1.8
 */
public class PropertyWrapper {
	private PropertyResourceBundle prb;

	private static final String OS_NAME = System.getProperty("os.name");

	private final static String FILE_LOC = McPropBean.RESOURCE_FILE_PATH;
	private final static String WINXP_FILE_LOC = McPropBean.WINXP_RESOURCE_FILE_LOC_PATH;

	public PropertyWrapper() {
	}

	/**
	 * class constructor. Load the properties file into this PropertyWrapper.
	 * by passing an inputstream.
	 *
	 * @param	in		InputStream of the properties file
	 */
	public PropertyWrapper(InputStream in) throws IOException {
		prb = new PropertyResourceBundle(in);
	}
	
	public PropertyWrapper(String inFile) throws IOException {

		FileInputStream fileInput = null;
        ResourceBundle bundle = null;
        try {
	        	if(OS_NAME.indexOf("Windows") >= 0)
	        		fileInput = new FileInputStream(WINXP_FILE_LOC + inFile);
	        	else
	        		fileInput = new FileInputStream(FILE_LOC + inFile);
	        	prb = new PropertyResourceBundle(fileInput);
				
		}  catch (Throwable t) {
			//prb = ResourceBundle.getBundle(RESOURCE_LOC);
		} finally {
           	if(fileInput != null){
               	try {
                   	fileInput.close();
				} catch (Throwable t){
				}
			}
		}
	}


	/**
	 * Method to return all the keys of the properties file
	 *
	 * @return	a Enumeration which contains all the keys
	 */
	public Enumeration getKeys() {
		return prb.getKeys();
	}

	/**
	 * Method to return the value by passing a key name
	 *
	 * @param	name	name of the key
	 * @return	value of the key
	 */
	public String getValue(String name) {
		String value = new String();
		try {
			value = prb.getString(name);
		} catch (MissingResourceException e) {
			Log.writeErrorLog("Exception: PropertyWrapper -> getValue: " + e.toString() + ", missing key: " + e.getKey());
		}
		return value;
	}

}
