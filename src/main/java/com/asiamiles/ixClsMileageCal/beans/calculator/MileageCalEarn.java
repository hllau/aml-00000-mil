package com.asiamiles.ixClsMileageCal.beans.calculator;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

import com.asiamiles.ixClsMileageCal.beans.common.McDAO;

/*************************************************************************
	Java program for getting parameters from MileageEarnParam and
	putting result into MileageCalRecord in Mileage Calculator - Earning
	miles.
	
	QTW RA-20040406 : return eligible accrual sector and flight type 
	                  indicator for codeshare/joint venture  flight
	                  
	QTW AMV3-20041018 : generate return flights based on customer input and
	                    alternative results of direct flight
	                  
*************************************************************************/
public class MileageCalEarn {
	private String[] origin = null;
	private String[] destination = null;
	private String[] airline = null;
	private String[] classes = null;
  	private Connection conn;
  	private Statement stmt;
  	private ResultSet rset;
  	private String returnCode;
  	private MileageEarnReturn mcr;						//QTW AMV3-20041018
	private String mileClass;

	private final String[] errDatabase = {"SYS001","SYS002"}; 
	private final String[] errGenRT = {"OUT012","OUT013"}; 			//QTW AMV3-20041018
	private final String oneworldProgram ="O";
	private static McDAO mileageCalcDAO = McDAO.getInstance();

	public static void main(String args[]) {}
	

	public MileageEarnReturn earnMileage(MileageCalParam inputParam) {
		//Initialize variables
		int num = inputParam.getNoOfSectors() + 1;
		boolean tripType = inputParam.roundTrip;                      	//QTW AMV3-20041018    
		origin = new String[num];
		destination = new String[num];
		airline = new String[num];
		classes = new String[num];
		int asiaMiles = 0;
		int clubMiles = 0;
		float bonus = 0f;

		ArrayList carriers = new ArrayList();					//QTW AMV3-20041018
		ArrayList Classes  = new ArrayList();					//QTW AMV3-20041018
		boolean marchFlag2 = false;  					//QTW AMV3-20041018
		boolean marchFlag = false;					//QTW AMV3-20041018
		boolean marchClass = false;					//QTW AMV3-20041018

 		String returnClass = "X";
		String returnCarrier = "CPA";
		String milesQuery = " ";
		String returnFltType = "  ";					//QTW RA-20040406
		ArrayList result = new ArrayList();
		ArrayList altResult = new ArrayList();				//QTW AMV-20041018
		ArrayList errorCode = new ArrayList();				//QTW AMV-20041018
  		ArrayList altCalReturn = new ArrayList(); 				//QTW AMV3-20041018
		try {
			get_DBConnection();
		} catch (Exception e) {
			returnCode = errDatabase[0];
			MileageErrorReturn();
			return mcr;
		}
	
    		try {
		   //Getting parameters from MileageCalParam
		   ArrayList param = inputParam.getSectorParameters();

		   if (param == null) {
			mcr = new MileageEarnReturn(false, null, MileageEarnReturn.EMPTY_CODE, null, null); //QTW AMV3-20041018
		      	return mcr;
		   } else {
//		     String insert = "Insert into tb_amcalc_log values ('" + inputParam.getMemberID() +
//		     		     "','WEB',SYSDATE()" + ")";
//		     stmt.execute (insert);
		    for (int i = 0; i < param.size(); i++) {
		        asiaMiles = clubMiles = 0;
		        bonus = 0f;
			MileageCalSector p = (MileageCalSector)param.get(i);
			origin[i] = p.getOrigin();
			destination[i] = p.getDestination();
			airline[i] = p.getAirline();
			classes[i] = p.getClassType();
			if ((classes[i].equals("F")) || 
			    ((classes[i].equals("C")) && (!returnClass.equals("F"))) ||
			    ((classes[i].equals("Y")) && (returnClass.equals("X")))) {
				returnClass = classes[i];
			} 	
			milesQuery = "select distinct MARKETING_MILEAGE,FLIGHT_TYPE_IND from SECTOR_ACCRUAL" +  //QTW RA-20040406
				" where FKX_O_AIRPORT_CODE ='" + p.getOrigin() +
				"' and FKX_D_AIRPORT_CODE ='" + p.getDestination() +
				"' and FKX1_CARRIERCODE ='" + p.getAirline() + "'";
			rset = stmt.executeQuery (milesQuery);
    			while (rset.next ()) {
       				asiaMiles = (int) rset.getInt (1);
       				returnFltType = rset.getString (2);						//QTW RA-20040406
       			}
			if (inputParam.getMemberID().equals(" ") || (!oneworldFlag(p.getAirline()))) {
				clubMiles = -1;
			} else {
				clubMiles = asiaMiles;
			}
			bonus = getBonusFactor(p.getClassType(),p.getAirline()); 
			asiaMiles = (int) (asiaMiles * bonus + 0.5);
			
		     	for (int c = 0; c < Classes.size(); c++) {
		     		if (Classes.get(c).equals(p.getClassType())) {
					marchClass = true;
				}
		     	}
		     	if (!marchClass) {
		     		Classes.add(p.getClassType());
		     	} else {marchClass = false;}
		     	
		     	for (int j = 0; j < carriers.size(); j++) {
		     		if (carriers.get(j).equals(p.getAirline())) {
					marchFlag = true;
				}
		     	}
		     	if (!marchFlag) {
		     		carriers.add(p.getAirline());
				String tempAirline = p.getAirline();  // cppcnh start 20030310
				String tempParent = getParentAirline(tempAirline);  
		     		for (int k = 0; k < carriers.size(); k++) {
		     			if (carriers.get(k).equals(tempParent)) {
		     				marchFlag2 = true;
		     			}	
		     		}
		     		if (!marchFlag2) {	
		     			carriers.add(tempParent);
		     		} else {marchFlag2 = false;}  // cppcnh end 20030310
		     	} else {marchFlag = false;}
		     	 				
			MileageCalSectorMiles res = new MileageCalSectorMiles(origin[i], destination[i], airline[i], 
						   classes[i], returnFltType, asiaMiles, clubMiles);	  	//QTW RA-20040406
			result.add(res);
			altResult.add(res);								//QTW AMV3-20041018
		   } 
		   if (!tripType) {                                                                            	//QTW AMV3-20041018
		     	mcr = new MileageEarnReturn(false, result, MileageEarnReturn.SUCCESS_CODE, null, null);
		   } else {
		      	int i = param.size() - 1;
		      	asiaMiles = 0;
		      	bonus = 0f;
		      	boolean returnFlightFlag = true;
		      	boolean directFlightFlag = false;							//QTW AMV3-20041018		
		      	boolean samePort = destination[i].equals(origin[0]);
		      	if (samePort) {	
		               	mcr = new MileageEarnReturn(false, result, MileageEarnReturn.SUCCESS_CODE, null, null);
		      	} else {
//		      		returnFlightFlag = true;
		        	for (i = param.size() - 1; i >= 0; i--) {
		        		asiaMiles = clubMiles = 0;
		       			bonus = 0f;
		      			milesQuery = "select distinct MARKETING_MILEAGE,FLIGHT_TYPE_IND from SECTOR_ACCRUAL" +   //QTW RA-20040406
						" where FKX_O_AIRPORT_CODE ='" + destination[i] +
						"' and FKX_D_AIRPORT_CODE ='" + origin[i] +
						"' and FKX1_CARRIERCODE ='" + airline[i] + "'";
					rset = stmt.executeQuery (milesQuery);
    					while (rset.next ()) {
       						asiaMiles = (int) rset.getInt (1);
       						returnFltType = rset.getString (2);						//QTW RA-20040406
       					}
					if (inputParam.getMemberID().equals(" ") || (!oneworldFlag(airline[i]))) {
						clubMiles = -1;
					} else {
						clubMiles = asiaMiles;
					}
					bonus = getBonusFactor(classes[i],airline[i]);
					asiaMiles = (int) (asiaMiles * bonus + 0.5);
					if (asiaMiles == 0) {
						returnFlightFlag = false;
					}
					MileageCalSectorMiles res = new MileageCalSectorMiles(destination[i], origin[i], airline[i], 
							   classes[i], returnFltType, asiaMiles, clubMiles);			//QTW RA-20040406
	  				result.add(res);
				}
				if ((Classes.size() == 1) && ((!returnFlightFlag) || (param.size() != 1))) {							// Alternative results
					i = param.size() - 1;
					for (int m = 0; m < carriers.size(); m++) {
		        			asiaMiles = clubMiles = 0;
		       				bonus = 0f;
		      				milesQuery = "select distinct MARKETING_MILEAGE,FLIGHT_TYPE_IND from SECTOR_ACCRUAL" +   //QTW RA-20040406
							" where FKX_O_AIRPORT_CODE ='" + destination[i] +
							"' and FKX_D_AIRPORT_CODE ='" + origin[0] +
							"' and FKX1_CARRIERCODE ='" + carriers.get(m) + "'";
	      					rset = stmt.executeQuery (milesQuery);
    		      				while (rset.next ()) {
       							asiaMiles = (int) rset.getInt (1);
       							returnFltType = rset.getString (2);						//QTW RA-20040406
       							returnCarrier = (String)carriers.get(m);
       		      				}
       		      				if ((asiaMiles == 0) && (param.size() == 1)) {
		        				asiaMiles = clubMiles = 0;
		       					bonus = 0f;
		       					returnCarrier = " ";
		      					milesQuery = "select MARKETING_MILEAGE,FKX1_CARRIERCODE,FLIGHT_TYPE_IND from SECTOR_ACCRUAL, CLASS_GROUP" +	
			   					" where FK1_CARRIER_CODE=FKX1_CARRIERCODE and START_DATE <= SYSDATE() AND END_DATE >= SYSDATE()" +
			   					" and FKX_O_AIRPORT_CODE ='" + destination[i] + "' and FKX_D_AIRPORT_CODE ='" + origin[0] + 
								"' and FKX1_CARRIERCODE='CPA' and FK2_MIL_CLAS_CODE ='" + classes[i] + "' order by FKX1_CARRIERCODE";

		      					rset = stmt.executeQuery (milesQuery);
    		     	 				while (rset.next ()) {
       								asiaMiles = (int) rset.getInt (1);
     								returnCarrier = rset.getString (2);
       								returnFltType = rset.getString (3);
       		      					}
		      					if (asiaMiles == 0) {
		        					asiaMiles = clubMiles = 0;
		        					returnCarrier = " ";
		       						bonus = 0f;
		      						milesQuery = "select MARKETING_MILEAGE,FKX1_CARRIERCODE,FLIGHT_TYPE_IND from SECTOR_ACCRUAL, CLASS_GROUP" +	
			   						" where FK1_CARRIER_CODE=FKX1_CARRIERCODE and START_DATE <= SYSDATE() AND END_DATE >= SYSDATE()" +
			   						" and FKX_O_AIRPORT_CODE ='" + destination[i] + "' and FKX_D_AIRPORT_CODE ='" + origin[0] + 
									"' and FK2_MIL_CLAS_CODE ='" + classes[i] + "' order by FKX1_CARRIERCODE";
		      						rset = stmt.executeQuery (milesQuery);
  		      						while (rset.next ()) {
       									asiaMiles = (int) rset.getInt (1);
     									returnCarrier = rset.getString (2);
       									returnFltType = rset.getString (3);
       									break;
     								}
     		      					}
       		      				}
		      				if (asiaMiles > 0) {
		      					directFlightFlag = true;
							if (inputParam.getMemberID().equals(" ") || (!oneworldFlag(returnCarrier))) {
								clubMiles = -1;
							} else {
								clubMiles = asiaMiles;
							}
							bonus = getBonusFactor(classes[i],returnCarrier);
							asiaMiles = (int) (asiaMiles * bonus + 0.5);
							MileageCalSectorMiles returnRes = new MileageCalSectorMiles(destination[i], origin[0], returnCarrier, 
						   					classes[i], returnFltType, asiaMiles, clubMiles);	//QTW RA-20040406
							ArrayList altEarnResult = new ArrayList();
							for (int r = 0; r < altResult.size(); r++) {
								altEarnResult.add(altResult.get(r));
							}
		   					altEarnResult.add(returnRes);
							altCalReturn.add(new MileageEarnReturn(true, altEarnResult, MileageEarnReturn.SUCCESS_CODE, null, null));
		      				}
					}
				   }
				} 
				if ((!returnFlightFlag) && (!directFlightFlag)) {
					if (param.size() == 1) 
						errorCode.add(errGenRT[0]);
					else
						errorCode.add(errGenRT[1]);
		                	mcr = new MileageEarnReturn(false, null, MileageEarnReturn.ERROR_CODE, errorCode, null);
				} 
				if ((returnFlightFlag) && (!directFlightFlag)) {
		                	mcr = new MileageEarnReturn(false, result, MileageEarnReturn.BASIC_ITIN_SUCCESS_CODE, null, null);
				} 
				if ((returnFlightFlag) && (directFlightFlag)) {
		                	mcr = new MileageEarnReturn(true, result, MileageEarnReturn.BASIC_ALT_ITIN_SUCCESS_CODE, null, altCalReturn);
				} 
				if ((!returnFlightFlag) && (directFlightFlag)) {
					errorCode.add(errGenRT[0]);
		                	mcr = new MileageEarnReturn(true, null, MileageEarnReturn.ALT_ITIN_SUCCESS_CODE, errorCode, altCalReturn);
				} 
		     } 
		     return mcr;
		   }
              	}
		catch (Exception e) {
			e.printStackTrace();
			returnCode = errDatabase[1];
			MileageErrorReturn();
			return mcr;
		}
    		finally {
			close_DBConnection();
    		}
	}

	public boolean oneworldFlag (String carrier) throws SQLException {
		boolean oneworldCarrier = true;
		try {
		   	String query = "Select count (*) from Carrier_Item " +
		  	   "where FK3_AWARD_PGM_CODE='" + oneworldProgram + 
		  	   "' and FK1_CARRIER_CODE='" + carrier + "'";
		   	rset = stmt.executeQuery (query);
    		   	while (rset.next ())
				oneworldCarrier &= (rset.getInt(1) > 0);
		} catch (SQLException e) {}
		return oneworldCarrier;
	}

	public boolean carrierClassFlag (String cls, String carrier) throws SQLException {
		boolean classFlag = true;
		String bonusQuery = " ";
		try {
			bonusQuery = "select count(*) from CLASS_GROUP where FK2_MIL_CLAS_CODE='" + cls + 
				"' and START_DATE <= SYSDATE() AND END_DATE >= SYSDATE()" +
				"  and FK1_CARRIER_CODE='" + carrier + "'";
			rset = stmt.executeQuery (bonusQuery);
    		   	while (rset.next ())
				classFlag &= (rset.getInt(1) > 0);
		} catch (SQLException e) {}
		return classFlag;
	}

	public float getBonusFactor (String cls, String carrier) throws SQLException {
		float bonus = 1f;
		mileClass = "Y";
		String bonusQuery = " ";
		try {
//			bonusQuery = "select distinct FK2_MIL_CLAS_CODE from CLASS_GROUP where FK2_MIL_CLAS_CODE='" + cls + 
//				"' and START_DATE <= SYSDATE() AND END_DATE >= SYSDATE()" +
//				"  and FK1_CARRIER_CODE='" + carrier + "'";
//			rset = stmt.executeQuery (bonusQuery);
//  			while (rset.next ())
//				mileClass = rset.getString(1);	
			if (carrierClassFlag(cls,carrier)) {
				bonusQuery = "Select PERCENTAGE from ACCRUAL_RATE" +
					" where CODE ='" + cls + "'";
				rset = stmt.executeQuery (bonusQuery);
    				while (rset.next ())
					bonus = rset.getFloat(1);
			} else {
				bonus = 0;
			}
		} catch (SQLException e) {}
		return bonus;
	}

	public String getParentAirline (String airline) throws SQLException {
		String parent = null;
		String query = " ";
		try {
			query = "Select FK1_CARRIER_CODE from Carrier " +
				"where CARRIER_CODE='" + airline + "'";
			rset = stmt.executeQuery (query);
			while (rset.next ())
				parent = rset.getString (1);	
			if (parent == null)
				parent = airline;	 
		}
		catch (SQLException e) {}
		return parent;
	}

	public void MileageErrorReturn () {
		//Putting error code into ArrayList if there is error
		ArrayList errorCode = new ArrayList();
		errorCode.add(returnCode);
		//create more Properties object and put them into ArrayList errorCode if there is more than one error code
		mcr = new MileageEarnReturn(false, null, MileageEarnReturn.ERROR_CODE, errorCode, null);
	}

	public void get_DBConnection () throws Exception {
 		conn = mileageCalcDAO.getConnection();
		stmt = conn.createStatement ();
	}

	public void close_DBConnection () {
     		try { 
			rset.close();
			rset=null;
			stmt.close();
			stmt=null;
			conn.close(); 
		} catch (Exception e) {}
	}

}