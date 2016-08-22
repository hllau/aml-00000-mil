package com.asiamiles.ixClsMileageCal.beans.calculator;

import java.io.IOException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Properties;

import com.asiamiles.ixClsMileageCal.beans.common.Log;
import com.asiamiles.ixClsMileageCal.beans.common.McDAO;
import com.asiamiles.ixClsMileageCal.beans.common.PropertyWrapper;
import com.asiamiles.ixClsMileageCal.beans.common.ResourceStream;
import com.asiamiles.ixClsMileageCal.beans.common.TranslationDAO;

/**
 * <p> Created at: 09/APR/2001
 * <br> Updated at: 10/MAY/2002 by Richard Huang (Ion Global) = Update generateAwardTypePairs(),printAwardTypeOptionString() methods to retrieve data from DB
 * <br> Updated at: 20/AUG/2002 by Richard Huang (Ion Global) = Update generateClassPairs(),printClassOptionString() methods to retrieve data from DB
 *
 * @author	Maggie Yip (IMT E-Business)
 * @version	%I%, %G%
 * @since	JDK1.1.8
 */
 
public class MileageCalUtil {
	private HashMap cities_en;
	private HashMap cities_zh;
	private HashMap cities_sc;
	private HashMap cities_ja;
	private HashMap cities_ko;
	private Connection conn;
	private static McDAO mileageCalcDAO = McDAO.getInstance();
	
	public MileageCalUtil() throws IOException {
		generateCityPairs();
	}

	public MileageCalUtil(String language) throws IOException {
	}

	private void generateCityPairs() {
//		String sqlString = "Select AIRPORT_CODE, AIRPORT_NAME from AIRPORT";
//		Statement stmt = null;
//		ResultSet rs = null;
//
//		try {
//			cities = new Properties();
//			stmt = conn.createStatement();
//			rs = stmt.executeQuery(sqlString);
//			while (rs.next()) {
//				int i = 1;
//				String code = rs.getString(i++);
//				String name = rs.getString(i++);
//				if (code != null && !code.equals("") && name != null && !name.equals("")) {
//					cities.put(code, name);
//				}
//			}
//			rs.close();
//			rs = null;
//			stmt.close();
//			stmt = null;
//		} catch (SQLException e) {
//			Log.writeErrorLog("Exception: MileageCalUtil -> generateCityPairs: " +e.getMessage());
//			//e.printStackTrace();
//		}
		
		cities_en = TranslationDAO.getTranslationMapping("AIRPORT_DATA_en");
		cities_zh = TranslationDAO.getTranslationMapping("AIRPORT_DATA_zh");
		cities_sc = TranslationDAO.getTranslationMapping("AIRPORT_DATA_sc");
		cities_ja = TranslationDAO.getTranslationMapping("AIRPORT_DATA_ja");
		cities_ko = TranslationDAO.getTranslationMapping("AIRPORT_DATA_ko");
        
	}

	private String getAirportName(String defaultCode, String lang)
	{
		if (cities_en == null) {
			this.generateCityPairs();
		}
	
		if (lang.equals("en"))
			defaultCode = this.cities_en.get(defaultCode).toString();
		else if (lang.equals("zh"))
			defaultCode = this.cities_zh.get(defaultCode).toString();
		else if (lang.equals("sc"))
			defaultCode = this.cities_sc.get(defaultCode).toString();
		else if (lang.equals("ja"))
			defaultCode = this.cities_ja.get(defaultCode).toString();
		else if (lang.equals("ko"))
			defaultCode = this.cities_ko.get(defaultCode).toString();

		return defaultCode;
	}
	
	public String printOriginOptionString(String defaultCode) {
		return printOriginOptionString(defaultCode, "en");
	}
	
	public String printOriginOptionString(String defaultCode, String lang) {
		return getAirportName(defaultCode, lang);
	}

	public String printDestinationOptionString(String defaultCode) {
		return printDestinationOptionString(defaultCode, "en");
	}
	
	public String printDestinationOptionString(String defaultCode, String lang) {
		return getAirportName(defaultCode, lang);
	}


}