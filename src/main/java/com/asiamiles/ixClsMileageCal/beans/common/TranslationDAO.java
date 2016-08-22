package com.asiamiles.ixClsMileageCal.beans.common;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;

import com.asiamiles.ixClsMileageCal.GenericMileageCal;


public class TranslationDAO extends GenericMileageCal
{
	private static final long REFRESH_PERIOD = Long.parseLong(McPropBean.getProperty("REFRESH_INTERVAL"));
	private static TranslationDAO instance;
	private static long loadTime;
	private static Hashtable resultHash;
	private static Hashtable returnHash;
	private static Connection conn;
	
	private static String AWARDTYPE_DATA = "AWARDTYPE_DATA";
	 
		
	public static TranslationDAO getInstance() 
	{
		if (instance == null) {
			instance = new TranslationDAO();
		}
		return instance;
	}
	

	public Object clone() throws CloneNotSupportedException
	{
		throw new CloneNotSupportedException(); 
	}

	public static List getTranslationData(String pName)
	{
		if (hasExpired()) {
			loadTime = System.currentTimeMillis();
			loadData();
		}

		HashMap tmpMap = (HashMap)returnHash.get(pName);
		if (null!=tmpMap) {
			List list = new ArrayList(tmpMap.entrySet());
//			if (!pName.equals(AWARDTYPE_DATA))
//			{
//				if (!"COMPANYSIZE_DATA".equals(pName)) {
//					Collections.sort(list, new MapComparator());
//				} else {
//					Collections.sort(list, new MapComparator("BYKEY"));;
//				}
//			}

			return list;
		} else {
			return null;	
		}
	}

	public static HashMap getTranslationMapping(String pName)
	{
		if (hasExpired()) {					
			loadTime = System.currentTimeMillis();
			loadData();
		}							

		return (HashMap)returnHash.get(pName);
	}

	private static void loadData()
	{
		writeInfoLog("loadData...");
		resultHash = new Hashtable();
			
		try {
			conn = mileageCalDao.getConnection();
			
			writeInfoLog("loading awardtypes ....");
			loadAwardTypes();
			writeInfoLog("loading airport (en) ....");
			loadAirport_en();
			writeInfoLog("loading airport (zh) ....");
			loadAirport_zh();
			writeInfoLog("loading airport (sc) ....");
			loadAirport_sc();
			writeInfoLog("loading airport (ja) ....");
			loadAirport_ja();
			writeInfoLog("loading airport (ko) ....");
			loadAirport_ko();
			
			returnHash = resultHash;
		} catch (Exception e) {
			writeErrorLog("TranslationDAO>loadData: Exception" + e);
		} finally {
			mileageCalDao.closeConnection(conn);
		}			
		
		writeInfoLog("loadData finished.");

	}
	


	public static void loadAirport_en()
	{	// use AIRPORT to show All airports iso valid airport, to be validated by system
		String sql = "SELECT AIRPORT_CODE, AIRPORT_NAME " +
					 "FROM AIRPORT ";
	
		try {
			ResultSet rs;
			Statement stmt = conn.createStatement();
			HashMap map = new HashMap();
			
			rs = stmt.executeQuery(sql);

			while (rs.next()) {
				map.put(rs.getString("AIRPORT_CODE"), rs.getString("AIRPORT_NAME"));
			}

			resultHash.put("AIRPORT_DATA_en", map);
			stmt.close();
			rs.close();
			
		} catch (Exception e) {
			writeErrorLog("TranslationDAO>loadAirport: Exception" + e);
			writeErrorLog("SQL: " + sql);
		}				
		
	}
	

	public static void loadAirport_zh()
	{	// use AIRPORT to show All airports iso valid airport, to be validated by system
		String sql = "SELECT AIRPORT.AIRPORT_CODE as AIRPORT_CODE, " +
					 "nvl(AIRPORT_NAME.AIRPORT_NAME,AIRPORT.AIRPORT_NAME) as AIRPORT_NAME " +
					 "FROM AIRPORT " +
					 "left join AIRPORT_NAME on AIRPORT_NAME.AIRPORT_CODE = AIRPORT.AIRPORT_CODE and AIRPORT_NAME.LANG_CODE = 'tc' " ;
	
		try {
			ResultSet rs;
			Statement stmt = conn.createStatement();
			HashMap map = new HashMap();
			
			rs = stmt.executeQuery(sql);

			while (rs.next()) {
				map.put(rs.getString("AIRPORT_CODE"), rs.getString("AIRPORT_NAME"));
			}

			resultHash.put("AIRPORT_DATA_zh", map);
			stmt.close();
			rs.close();
			
		} catch (Exception e) {
			writeErrorLog("TranslationDAO>loadAirport: Exception" + e);
			writeErrorLog("SQL: " + sql);
		}				
		
	}

	public static void loadAirport_sc()
	{	// use AIRPORT to show All airports iso valid airport, to be validated by system
		String sql = "SELECT AIRPORT.AIRPORT_CODE as AIRPORT_CODE, " +
					 "nvl(AIRPORT_NAME.AIRPORT_NAME,AIRPORT.AIRPORT_NAME) as AIRPORT_NAME " +
					 "FROM AIRPORT " +
					 "left join AIRPORT_NAME on AIRPORT_NAME.AIRPORT_CODE = AIRPORT.AIRPORT_CODE and AIRPORT_NAME.LANG_CODE = 'sc' " ;
	
		try {
			ResultSet rs;
			Statement stmt = conn.createStatement();
			HashMap map = new HashMap();
			
			rs = stmt.executeQuery(sql);

			while (rs.next()) {
				map.put(rs.getString("AIRPORT_CODE"), rs.getString("AIRPORT_NAME"));
			}

			resultHash.put("AIRPORT_DATA_sc", map);
			stmt.close();
			rs.close();
			
		} catch (Exception e) {
			writeErrorLog("TranslationDAO>loadAirport: Exception" + e);
			writeErrorLog("SQL: " + sql);
		}				
		
	}
	
	public static void loadAirport_ja()
	{	// use AIRPORT to show All airports iso valid airport, to be validated by system
		String sql = "SELECT AIRPORT.AIRPORT_CODE as AIRPORT_CODE, " +
					 "nvl(AIRPORT_NAME.AIRPORT_NAME,AIRPORT.AIRPORT_NAME) as AIRPORT_NAME " +
					 "FROM AIRPORT " +
					 "left join AIRPORT_NAME on AIRPORT_NAME.AIRPORT_CODE = AIRPORT.AIRPORT_CODE and AIRPORT_NAME.LANG_CODE = 'jp' " ;
	
		try {
			ResultSet rs;
			Statement stmt = conn.createStatement();
			HashMap map = new HashMap();
			
			rs = stmt.executeQuery(sql);

			while (rs.next()) {
				map.put(rs.getString("AIRPORT_CODE"), rs.getString("AIRPORT_NAME"));
			}

			resultHash.put("AIRPORT_DATA_ja", map);
			stmt.close();
			rs.close();
			
		} catch (Exception e) {
			writeErrorLog("TranslationDAO>loadAirport: Exception" + e);
			writeErrorLog("SQL: " + sql);
		}				
		
	}
	
	public static void loadAirport_ko()
	{	// use AIRPORT to show All airports iso valid airport, to be validated by system
		String sql = "SELECT AIRPORT.AIRPORT_CODE as AIRPORT_CODE, " +
					 "nvl(AIRPORT_NAME.AIRPORT_NAME,AIRPORT.AIRPORT_NAME) as AIRPORT_NAME " +
					 "FROM AIRPORT " +
					 "left join AIRPORT_NAME on AIRPORT_NAME.AIRPORT_CODE = AIRPORT.AIRPORT_CODE and AIRPORT_NAME.LANG_CODE = 'ka' " ;
	
		try {
			ResultSet rs;
			Statement stmt = conn.createStatement();
			HashMap map = new HashMap();
			
			rs = stmt.executeQuery(sql);

			while (rs.next()) {
				map.put(rs.getString("AIRPORT_CODE"), rs.getString("AIRPORT_NAME"));
			}

			resultHash.put("AIRPORT_DATA_ko", map);
			stmt.close();
			rs.close();
			
		} catch (Exception e) {
			writeErrorLog("TranslationDAO>loadAirport: Exception" + e);
			writeErrorLog("SQL: " + sql);
		}				
		
	}
	
	public static void loadAwardTypes()
	{
		String sql = "select ticket_upgrade, award_class, award_code from award_class order by ticket_upgrade";
		try {
//			ResultSet rs;
//			Statement stmt = conn.createStatement();
			HashMap map = new HashMap();
//			
//			rs = stmt.executeQuery(sql);			

			String hm = "";
			// "," seperate the class and code, ";" seperate multiple results
			
//			companion	first	FCC
//			companion	business	BCB
			hm = "first" + "," + "FCC" + ";";
			hm += "business" + "," + "BCB" + ";";
			map.put("companion", hm);
			
//			one_way	first	OWFC
//			one_way	p_economy	OWPEC
//			one_way	business	OWBC
//			one_way	economy	OWEC
			hm = "first" + "," + "OWFC" + ";";
			hm +=  "p_economy" + "," + "OWPEC" + ";";
			hm +=  "business" + "," + "OWBC" + ";";
			hm +=  "economy" + "," + "OWEC" + ";";
			map.put("one_way", hm);
			
//			unrestricted	economy	UEC -> PEC (from 2009/1/1)
			hm = "economy" + "," + getPriorityCode() + ";";
			map.put("unrestricted", hm);
			
//			round_trip_upgrade	e_to_pe	UGYP
//			round_trip_upgrade	e_to_b	UGEC
//			round_trip_upgrade	b_to_first	UGCF
//			round_trip_upgrade	pe_to_b	UGPC
			hm = "e_to_pe" + "," + "UGYP" + ";";
			hm +=  "e_to_b" + "," + "UGEC" + ";";
			hm +=  "b_to_first" + "," + "UGCF" + ";";
			hm +=  "pe_to_b" + "," + "UGPC" + ";";
			map.put("round_trip_upgrade", hm);
			
//			round_trip	first	RTFC
//			round_trip	p_economy	RTPEC
//			round_trip	business	RTBC
//			round_trip	economy	RTEC
			hm = "first" + "," + "RTFC" + ";";
			hm +=  "p_economy" + "," + "RTPEC" + ";";
			hm +=  "business" + "," + "RTBC" + ";";
			hm +=  "economy" + "," + "RTEC" + ";";
			map.put("round_trip", hm);
			
//			one_way_upgrade	e_to_b	OWUC
//			one_way_upgrade	b_to_first	OWUF
			hm = "e_to_b" + "," + "OWUC" + ";";
//PEY LE AM.com  update by  IMTMKC 2012-07-31 Start
			hm +=  "e_to_pe" + "," + "OWUYP" + ";";
//PEY LE AM.com  update by  IMTMKC 2012-07-31 End
//PEY_DAY1 AM.com added for premium economy to business class by IBM 2010-1-31 Start
			hm +=  "pe_to_b" + "," + "OWUPC" + ";";
//PEY_DAY1 AM.com added for premium economy to business class by IBM 2010-1-31 End
			hm +=  "b_to_first" + "," + "OWUF" + ";";
			map.put("one_way_upgrade", hm);

									
//			unrestricted_ow	economy	OUEC -> OPEC (from 2009/1/1)
			hm = "economy" + "," + getOneWayPriorityCode() + ";";
			map.put("unrestricted_ow", hm);

			
			//CPPPEP ADD FOR AML.31473 20140630 START
			hm = "first" + "," + "OWFPT1" + ";";
			hm +=  "p_economy" + "," + "OWPPT1" + ";";
			hm +=  "business" + "," + "OWBPT1" + ";";
			hm +=  "economy" + "," + "OWEPT1" + ";";
			map.put("one_way_prio_tier1", hm);
			
			hm = "first" + "," + "OWFPT2" + ";";
			hm +=  "p_economy" + "," + "OWPPT2" + ";";
			hm +=  "business" + "," + "OWBPT2" + ";";
			hm +=  "economy" + "," + "OWEPT2" + ";";
			map.put("one_way_prio_tier2", hm);
			
			hm = "first" + "," + "RTFPT1" + ";";
			hm +=  "p_economy" + "," + "RTPPT1" + ";";
			hm +=  "business" + "," + "RTBPT1" + ";";
			hm +=  "economy" + "," + "RTEPT1" + ";";
			map.put("round_trip_prio_tier1", hm);
			
			hm = "first" + "," + "RTFPT2" + ";";
			hm +=  "p_economy" + "," + "RTPPT2" + ";";
			hm +=  "business" + "," + "RTBPT2" + ";";
			hm +=  "economy" + "," + "RTEPT2" + ";";
			map.put("round_trip_prio_tier2", hm);
			//CPPPEP ADD FOR AML.31473 20140630 END
			
//			while (rs.next()) {	
//				
//				String hm = "";
//				// "," seperate the class and code, ";" seperate multiple results
//				hm = rs.getString("award_class") + "," + rs.getString("award_code") + ";";
//				if (map.containsKey(rs.getString("ticket_upgrade")))
//				{
//					hm = map.get(rs.getString("ticket_upgrade")).toString() + hm;
//					map.put(rs.getString("ticket_upgrade"), hm);
//				}
//				else
//					map.put(rs.getString("ticket_upgrade"), hm);
//			}
//			
			resultHash.put(AWARDTYPE_DATA, map);
//			stmt.close();
//			rs.close();			

		} catch (Exception e) {
			writeErrorLog("TranslationDAO>loadAwardType: Exception" + e);
			writeErrorLog("SQL: " + sql);
		}				
	}
	
	private static boolean hasExpired()
	{
		boolean hasExpired = true;
		long now = System.currentTimeMillis();
		if (loadTime!=0)
			hasExpired = (REFRESH_PERIOD < (now - loadTime));
	
		return hasExpired;
	}

	public static void refresh()
	{
		writeInfoLog("DB refreshed.");
		loadData();		
	}
}