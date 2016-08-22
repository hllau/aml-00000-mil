package com.asiamiles.ixClsMileageCal.beans.mileageCal;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Hashtable;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.asiamiles.ixClsMileageCal.GenericMileageCal;
import com.asiamiles.ixClsMileageCal.MileageCalHandlerIF;
import com.asiamiles.ixClsMileageCal.beans.common.CachingHash;
import com.cathaypacific.clsUtil.UtilBean;

public class MileageCalHandler extends GenericMileageCal implements MileageCalHandlerIF
{
	private HashMap loginErrMap = new HashMap();
	private MileageCalView mcView = new MileageCalView();

	private String hashKey = "";
	protected static final String HASHDELIMITOR = "/";
	
	public String execute(HttpServletRequest pReq,HttpServletResponse pRes)
	{
		Hashtable reqParam = UtilBean.getReqParam(pReq);
		HttpSession session = pReq.getSession();

		String url = "/mileageCal/mileageCal.jsp";
		paramInit(reqParam);

		try {	
			validation();

			String rtnStrValue = "0";
			
			Hashtable myCachingHash = CachingHash.getMileageCalHash();
			
			writeInfoLog(this.getClass().getName()+">cache hash size:"+myCachingHash.size());
			hashKey = mcView.getOrigin()+HASHDELIMITOR+mcView.getDestination()+HASHDELIMITOR+mcView.getCarrier()+HASHDELIMITOR+mcView.getRoundTripInd();
			
			if (myCachingHash.containsKey(hashKey) )
			{
				writeInfoLog(this.getClass().getName()+">get results from hashtable : " + hashKey);
				
				rtnStrValue = myCachingHash.get(hashKey).toString();
			}
			else
			{
				writeInfoLog(this.getClass().getName()+">get results from DB : " + hashKey);

				//process db access
				rtnStrValue = this.getGCDMileage();
			}

			session.setAttribute("MILEAGECALVIEW", this.mcView);
			session.setAttribute("MILEAGECALVAL", rtnStrValue);

		} catch (Exception e) {
			writeErrorLog("MileageCalHandler>execute");
			writeErrorLog("Exception: " + e);
			writeErrorLog("Customer number: " + this.mcView.getMemberID());
			if (null==this.loginErrMap.get("errorMessage"))
				this.loginErrMap.put("errorMessage", e.toString());	
			session.setAttribute("MILEAGECALVAL", null);
		} finally {
			session.setAttribute("MILEAGECALERR", this.loginErrMap);
		}

		reqParam = null;
		
		writeInfoLog("Dispatch url: " + url);
		return url;
	}

	private void paramInit(Hashtable pHash) 
	{
		writeInfoLog("pHash: " + pHash);
		this.mcView.setlang(UtilBean.getValueOfStr(pHash.get("Language")));
		this.mcView.setEarnRedeemInd(UtilBean.getValueOfStr(pHash.get("EarnRedeemInd")));
		this.mcView.setRoundTripInd(UtilBean.getValueOfStr(pHash.get("RoundTripInd")));
		this.mcView.setCarrier(UtilBean.getValueOfStr(pHash.get("Carrier")));
		this.mcView.setCabinClass(UtilBean.getValueOfStr(pHash.get("CabinClass")));
		this.mcView.setOrigin(UtilBean.getValueOfStr(pHash.get("Origin")));
		this.mcView.setDestination(UtilBean.getValueOfStr(pHash.get("Destination")));
	}
	
	private void validation() throws Exception
	{
		// init
		this.loginErrMap.put("statusCode", "0000");
		
		if (this.mcView.getEarnRedeemInd().length()==0) {
			this.loginErrMap.put("statusCode", "1107");
			this.loginErrMap.put("errorMessage", "Mandatory paramters missing: EarnRedeemInd");
			throw new Exception("Mandatory paramters missing: EarnRedeemInd"); 
		}
			
		if (this.mcView.getRoundTripInd().length()==0) {
			this.loginErrMap.put("statusCode", "1107");
			this.loginErrMap.put("errorMessage", "Mandatory paramters missing: RoundTripInd");
			throw new Exception("Mandatory paramters missing: RoundTripInd"); 
		}

		if (this.mcView.getCarrier().length()==0) {
			this.loginErrMap.put("statusCode", "1107");
			this.loginErrMap.put("errorMessage", "Mandatory paramters missing: Carrier");
			throw new Exception("Mandatory paramters missing: Carrier"); 
		}

		if (this.mcView.getCabinClass().length()==0) {
			this.loginErrMap.put("statusCode", "1107");
			this.loginErrMap.put("errorMessage", "Mandatory paramters missing: CabinClass");
			throw new Exception("Mandatory paramters missing: CabinClass"); 
		}

		if (this.mcView.getOrigin().length()==0) {
			this.loginErrMap.put("statusCode", "1107");
			this.loginErrMap.put("errorMessage", "Mandatory paramters missing: Origin");
			throw new Exception("Mandatory paramters missing: Origin"); 
		}

		if (this.mcView.getDestination().length()==0) {
			this.loginErrMap.put("statusCode", "1107");
			this.loginErrMap.put("errorMessage", "Mandatory paramters missing: Destination");
			throw new Exception("Mandatory paramters missing: Destination"); 
		}
	}
	


	private String getGCDMileage() throws SQLException, Exception
	{
		ResultSet rs;
		String mileageGCD = "0";
		Connection conn = mileageCalDao.getConnection();
		String sql = "";

		try
		{
			sql = "SELECT MARKETING_MILEAGE " +
			 "FROM ACCRUAL_AIRPORT_PAIR  " + 
			 "WHERE FKX_O_AIRPORT_CODE = ? " +
			 "AND FKX_D_AIRPORT_CODE = ? " +
			 "AND FKX1_CARRIERCODE = ? ";
			
			PreparedStatement ps_query = conn.prepareStatement(sql);
			ps_query.setString(1, this.mcView.getOrigin()); 
			ps_query.setString(2, this.mcView.getDestination()); 
			ps_query.setString(3, this.mcView.getCarrier());
			rs = ps_query.executeQuery();
	

			while (rs.next()) {
				mileageGCD = rs.getString("MARKETING_MILEAGE");				
			}
			
			if ("RT".equals(mcView.getRoundTripInd()))
				mileageGCD = Integer.toString(Integer.parseInt(mileageGCD) * 2);

			//cache the results into the hash
			CachingHash.addMileageCalHash(hashKey,mileageGCD);
			
			
			ps_query.close();
			rs.close();			

		} catch (SQLException e) {
			writeErrorLog("MileageCalHandler>getGCDMileage has SQLException" + e);
			writeErrorLog("SQL: " + sql);
			
		} catch (Exception e) {
			writeErrorLog("MileageCalHandler>getGCDMileage has Exception" + e);
			writeErrorLog("SQL: " + sql);
			
		} finally {				
			mileageCalDao.closeConnection(conn);
		}
		
		return mileageGCD;
	}
}