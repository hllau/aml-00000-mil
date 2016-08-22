package com.asiamiles.ixClsMileageCal.beans.common;

import com.cathaypacific.clsUtil.PropertiesBean;


public class McPropBean
{

	private static final String OS_NAME = System.getProperty("os.name");
	
	private static final String RESOURCE_LOC = "repos.prop.amcalc.ixClsMileageCal";
	private static String FILE_LOC = "";
	
	private static final String PROPERTIES_FILE_NAME = "ixClsMileageCal.properties";;
	public static final String RESOURCE_FILE_PATH = "/repos/prop/amcalc/";
	private static final String RESOURCE_FILE_LOC = RESOURCE_FILE_PATH + PROPERTIES_FILE_NAME;
	public static final String WINXP_RESOURCE_FILE_LOC_PATH = "/repos/prop/amcalc/";
	private static final String WINXP_RESOURCE_FILE_LOC = WINXP_RESOURCE_FILE_LOC_PATH + PROPERTIES_FILE_NAME;


	public static McPropBean getInstance() 
	{
		if (instance == null) {
			instance = new McPropBean();
			if(OS_NAME.indexOf("Windows") >= 0)
				FILE_LOC = WINXP_RESOURCE_FILE_LOC;
        	else
        		FILE_LOC = RESOURCE_FILE_LOC;
		}
		return instance;
	}


	public Object clone() throws CloneNotSupportedException
	{
		throw new CloneNotSupportedException(); 
	}
	
	public final static String getProperty(String key) throws java.util.MissingResourceException
	{
		PropertiesBean prop = PropertiesBean.getInstance(RESOURCE_LOC, FILE_LOC);
		return PropertiesBean.getProperty(key, "MC");
	}

	private static McPropBean instance;
}