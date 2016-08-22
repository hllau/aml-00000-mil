package com.asiamiles.ixClsMileageCal.beans.calculator;
import java.io.IOException;
import java.net.MalformedURLException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.ArrayList;

import org.xml.sax.SAXException;

import com.asiamiles.ixClsMileageCal.beans.common.Log;
import com.asiamiles.ixClsMileageCal.beans.common.McDAO;
import com.asiamiles.ixClsMileageCal.beans.common.McPropBean;
import com.cathaypacific.clsUtil.URLinvokerBean;
import com.cathaypacific.clsUtil.xmlparser.ParsedElementIF;
import com.cathaypacific.clsUtil.xmlparser.XMLParser;

/*************************************************************************
	Java program for getting parameters from MileageCalParam and
	putting result into MileageCalRecord in Mileage Calculator - Spending
	miles.

	QTW RA-20040406 : return eligible accrual sector and flight type 
	                  indicator for codeshare/joint venture  flight

	QTW AMV3-20041018 : generate return flights based on customer input and
	                    alternative results of direct flight
	                    
	SR143 - 20050808: handle DISALLOW_OPEN_JAW,DISALLOW_STOPOVER,DISALLOW_TRANSFER
	                  
    SR177 - KA oneworld (Flight award calculator)	                  
*************************************************************************/
public class MileageCalRedem {
	private String memberID;
	private String[] origin = null;
	private String[] destination = null;
	private String[] airline = null;
	private String[] parentAirline = null;  
	private ArrayList result = null;
	private int sectorIdx;
	private int awardTypeInd;
	private boolean sameParentDiffChild;  
	private String OWRT;
	private String awardType;
  	private Connection conn;
  	private Statement stmt;
  	private ResultSet rset;
	private String returnCode;
  	private MileageRedemReturn mcr, redemResult;			//QTW AMV3-20041018
	private ArrayList altRedemResult = new ArrayList();

	private McDAO mileageCalcDAO = McDAO.getInstance();
	
	private String xmlURL;
	private String SappID;
   	private String Spassword;
	
	private String[] OW_AwardType;
	private String[] OW_UgradeAwardType;
	private String[] RT_AwardType;
	private String[] RT_UgradeAwardType;
	private String[] RT_CompAwardType;

	private final String[] errDatabase = {"SYS001","SYS002"};
	private final String[] errInput = {"SYS003","SYS004"};
	private final String[] errOneWay = {"OUT001","OUT002","OUT003","OUT009"};
	private final String[] errRoundTrip = {"OUT004","OUT005","OUT006","OUT007","OUT008","OUT009","OUT010","OUT011"};
	private final String[] errGenRT = {"OUT012","OUT013"};			//QTW AMV3-20041018

	private String[] zoneExclude;
	private String[] zoneExAward;
	private final String[] zoneMap = {"S","A","B","C","D","E","F"};
					      	
	private final int mileageBlock  = 2000;
	private final int moneyPerBlock = 60;
	
	private final int OW_AwardTypeInd = 1;
	private final int OW_UgradeAwardTypeInd = 2;
	private final int RT_AwardTypeInd = 3;
	private final int RT_UgradeAwardTypeInd = 4;
	private final int RT_CompAwardTypeInd = 5;
	
	private final String asiamilesProgram = "A";
	private final String oneworldProgram = "O";
	private final String onewayInd = "OW";
	private final String roundtripInd = "RT";
	private final String cx = "CPA";
	
	private final String FUTURE_DATE = "15102007";
//	private final String FUTURE_DATE = McPropBean.getProperty("FUTURE_PERIOD_TO");

	ArrayList calSector = new ArrayList();					//QTW AMV3-20041018
	ArrayList altSector = new ArrayList();					//QTW AMV3-20041018
	

	private final ArrayList otherAirlines = getOtherAirlines();	//	 SR177 - KA oneworld (Flight award calculator)
	  	
	public MileageRedemReturn redemMileage(MileageCalParam inputParam) {
		//Initialize variables
		boolean marchFlag2 = false;  
		boolean marchFlag = false;
		boolean invalidSector = false;
		boolean returnFlightFlag = false;						
		boolean directFlightFlag = false;								
		String returnFltType = "  ";					//QTW RA-20040406
		
		try {
			get_DBConnection();
		} catch (Exception e) {
			Log.writeErrorLog(this.getClass().getName()+"->Get DB Connection Error:"+e.getMessage());
			returnCode = errDatabase[0];
			mcr = MileageErrorReturn();
			return mcr;
		}
		
    	try {
		   	ArrayList param = inputParam.getSectorParameters();
		   	memberID = inputParam.getMemberID();
		   	awardType = inputParam.getAwardType();
		   	if (param == null) {
				mcr = new MileageRedemReturn(null, MileageRedemReturn.EMPTY_CODE, null, null, null);
		   	} else {
		   		for (int i = 0; i < param.size(); i++) {	
       					MileageCalSector m = (MileageCalSector)param.get(i);
					if (getCalMileage(m.getOrigin(),m.getDestination(),m.getAirline()) != -1) {
						returnFltType = getFlightType(m.getOrigin(),m.getDestination(),m.getAirline());						
  						MileageCalSector returnSector = new MileageCalSector(m.getOrigin(),m.getDestination(),m.getAirline(),returnFltType);	
						calSector.add(returnSector);
					} else {
						invalidSector = true;
					}		
				}
		     		if (invalidSector) {
		     		// return input sector error
					returnCode = errInput[1];
			  		mcr = MileageErrorReturn();
		     		} else { 	// return MC spend results
		     	    	int[] cnt = new int[5];
                        OW_AwardType = new String[10];
			    		OW_UgradeAwardType = new String[10];
			    		RT_AwardType = new String[10];
			    		RT_UgradeAwardType = new String[10];
			    		RT_CompAwardType = new String[10];
			    		for (int x = 0; x < 10; x++) {
							OW_AwardType[x] = " ";
							OW_UgradeAwardType[x] = " ";
							RT_AwardType[x] = " ";
							RT_UgradeAwardType[x] = " ";
							RT_CompAwardType[x] = " "; 
			    		}	
	     	    		String awardCodeQuery = "select CODE,FK1_MRCH_CLAS_CODE,FK2_RT_OW_IND_CODE from AWARD_TYPE" +
							" where FK2_RT_OW_IND_CODE ='OW' OR FK2_RT_OW_IND_CODE ='RT'";
			    		rset = stmt.executeQuery (awardCodeQuery);
    			   		while (rset.next ()) {
    						OWRT = rset.getString (3);
    						switch (rset.getInt (2)) {
				  			case 1:
								if (OWRT.equals(onewayInd)) {
									OW_AwardType[cnt[0]] = rset.getString (1);
									cnt[0]++;  
								} else {
									RT_AwardType[cnt[1]] = rset.getString (1);
									cnt[1]++;
								}
								break;
				  			case 2:
				  				if (OWRT.equals(onewayInd)) {
									OW_UgradeAwardType[cnt[2]] = rset.getString (1);
									cnt[2]++;  
								} else {
									RT_UgradeAwardType[cnt[3]] = rset.getString (1);
									cnt[3]++;
								}
								break;
				  			case 3: 
				  				RT_CompAwardType[cnt[4]] = rset.getString (1);
								cnt[4]++;  
								break;
    						}	
  			    		}
		     	    		int count = 0;
		     	    		zoneExclude = new String[20];
			    		zoneExAward = new String[20];
			    		parentAirline = new String[20];
			    		for (int y = 0; y < 20; y++) {
						zoneExclude[y] = " ";
						zoneExAward[y] = " ";
						parentAirline[y] = " ";
			    		}	
			    		String zoneExcludeQuery = "select ZONE from ZONE_EXCLUDE" +
						" where START_DATE <= SYSDATE() and END_DATE >= SYSDATE()";
			    		rset = stmt.executeQuery (zoneExcludeQuery);
  			    		while (rset.next ()) {
	 					zoneExclude[count] = rset.getString (1);
						count++; 
			    		}
			    		count = 0;
					String awardExcludeQuery = "select AWARD from AWARD_EXCLUDE" +
						" where START_DATE <= SYSDATE() and END_DATE >= SYSDATE()";
					rset = stmt.executeQuery (awardExcludeQuery);
    					while (rset.next ()) {
						zoneExAward[count] = rset.getString (1);
						count++; 
					}
					
		     	    		String awardTypeQuery = "select distinct FK2_RT_OW_IND_CODE from AWARD_TYPE" +
				 				" where CODE ='" + awardType + "'";
		     	    		rset = stmt.executeQuery (awardTypeQuery);
    			    		while (rset.next ())
       				   			OWRT = rset.getString (1);
       				
     	 		   		if ((OWRT.equals(onewayInd)) && (calSector.size() > 2)) {	//one way cannot more than 2 sectors 
				    		returnCode = errOneWay[3];
				   	 		mcr = MileageErrorReturn();
       			    		} else {	
       			        		if (OWRT.equals(roundtripInd)) {
			    	    			String returnCarrier = " "; 
   			    	    			boolean openjawInd = false;
   			    	    			if (param.size() == 1) {	// input round trip award + single sector
       								MileageCalSector s = (MileageCalSector)param.get(0);
   			    					if (getCalMileage(s.getDestination(),s.getOrigin(),s.getAirline()) != -1) {
		      							returnFlightFlag = true;						
       									returnFltType = getFlightType(s.getDestination(),s.getOrigin(),s.getAirline());	
  									MileageCalSector returnSector = new MileageCalSector(s.getDestination(),s.getOrigin(),s.getAirline(),returnFltType);	
  									calSector.add(returnSector);
								}  else {
									returnFlightFlag = false;
									directFlightFlag = false;
									if (allowMixedCarrier(awardType,param)) {
										returnCarrier = "CPA";
	   									String query = "Select FKX1_CARRIERCODE from SECTOR_REDEMPTION,Carrier_Item " +	
											  	"where FKX1_CARRIERCODE=FK1_CARRIER_CODE " +
												" and FKX1_CARRIERCODE='" + returnCarrier + "'" +
												" and FK3_AWARD_PGM_TYPE='AWARD PROGRAM'" +
												" and START_DATE<=SYSDATE() and END_DATE>=SYSDATE()" +
												" and FK2_M_AND_A_CODE='" + awardType + "'" +
												" and FKX_O_AIRPORT_CODE='" + s.getDestination() + "'" +
			  									" and FKX_D_AIRPORT_CODE ='" + s.getOrigin() + "'" +
			  									" and MIXED_CARRIER_FLAG = 'Y'";
										rset = stmt.executeQuery (query);
										while (rset.next ()) {
		      								directFlightFlag = true;	
											returnCarrier = rset.getString (1);
     										returnFltType = getFlightType(s.getDestination(),s.getOrigin(),returnCarrier);	
										}
										if (!directFlightFlag) {
	   										query = "Select FKX1_CARRIERCODE from SECTOR_REDEMPTION,Carrier_Item " +	
											  	"where FKX1_CARRIERCODE=FK1_CARRIER_CODE " +
												" and FK3_AWARD_PGM_TYPE='AWARD PROGRAM'" +
												" and START_DATE<=SYSDATE() and END_DATE>=SYSDATE()" +
												" and FK2_M_AND_A_CODE='" + awardType + "'" +
												" and FKX_O_AIRPORT_CODE='" + s.getDestination() + "'" +
			  									" and FKX_D_AIRPORT_CODE ='" + s.getOrigin() + "'" +
			  									" and MIXED_CARRIER_FLAG = 'Y'";
											rset = stmt.executeQuery (query);
											while (rset.next ()) {
		      									directFlightFlag = true;	
												returnCarrier = rset.getString (1);
     											returnFltType = getFlightType(s.getDestination(),s.getOrigin(),returnCarrier);	
											}
										}
									}
									if (directFlightFlag) {
  										MileageCalSector returnSector = new MileageCalSector(s.getDestination(),s.getOrigin(),returnCarrier,returnFltType);	
  										altSector.add(returnSector);
									}
								}
		    	   			}  else {  	// input round trip award + multiple sectors
		    					String tempDestination = inputParam.getSectorParameters(0).getOrigin();
		    					String tempOrigin = inputParam.getSectorParameters(param.size()-1).getDestination();
		    					String query = "Select count (distinct COUNTRY_SELECTED) from AIRPORT, CITY " +
  	 			  				"where FKX_MAIN_CITY_CODE = IATA_CODE and AIRPORT_CODE in ('" +
			  	   				tempOrigin + "','" + tempDestination + "')";
		   						rset = stmt.executeQuery (query);
		   						while (rset.next ())
									openjawInd = (rset.getInt(1) == 1);
								
								returnFlightFlag = true;
								if (!openjawInd) {	// generate return flight sectors + direct flight sectors
									ArrayList carriers = getCarrier(param);
									ArrayList parentCarriers = getParentCarrier(param);
									for (int i = param.size() - 1; i >= 0; i--) {	// generate return flight sectors
       										MileageCalSector mc = (MileageCalSector)param.get(i);
										if (getCalMileage(mc.getDestination(),mc.getOrigin(),mc.getAirline()) != -1) {
       											returnFltType = getFlightType(mc.getDestination(),mc.getOrigin(),mc.getAirline());						
  											MileageCalSector returnArrayList = new MileageCalSector(mc.getDestination(),mc.getOrigin(),mc.getAirline(),returnFltType);
											calSector.add(returnArrayList);
										} else { returnFlightFlag = false; }	
									}

									if (allowMixedCarrier(awardType,param) || (carriers.size() == 1) || (parentCarriers.size() == 1)) {
										for (int i = 0; i < carriers.size(); i++) {	// generate direct flight sectors
											returnCarrier = (String) carriers.get(i);
   			    								if (getCalMileage(tempOrigin,tempDestination,returnCarrier) != -1) {
   			    									directFlightFlag = true;
												returnFltType = getFlightType(tempOrigin,tempDestination,returnCarrier);
  												MileageCalSector returnArrayList = new MileageCalSector(tempOrigin,tempDestination,returnCarrier,returnFltType);
   			    									altSector.add(returnArrayList);
   			    								}
										} 
									}
					    		} 	
					    	}	// end of round trip generation
						} else {
							if (!OWRT.equals(onewayInd)) {
       			        		returnCode = errInput[0];
								mcr = MileageErrorReturn();
							}
						}		  	
     			    }
					if (OWRT.equals(onewayInd)) {
						if (mcr == null) {
							mcr = OW_RedemReturn(calSector);
						}
					} else { 
						if (OWRT.equals(roundtripInd)) {
							ArrayList errorCode = new ArrayList();
							if (directFlightFlag) {		// generate alternative results in ArrayList
								for (int i = 0; i < altSector.size(); i++) {
									ArrayList altRedemSector = new ArrayList();
									for (int r = 0; r < param.size(); r++) {
										altRedemSector.add(calSector.get(r));
									}
									altRedemSector.add(altSector.get(i));
									MileageRedemReturn altResult = RT_RedemReturn(altRedemSector);
									if (altResult.getReturn_status() == MileageRedemReturn.SUCCESS_CODE) {
										altRedemResult.add(altResult);
									}
								}
								if (altRedemResult.size() == 0) {
									directFlightFlag = false;
								}
							}
							if (returnFlightFlag) {
								if (!allowMixedCarrier(awardType,calSector) && (getParentCarrier(calSector).size() != 1)) {
									returnFlightFlag = false;
									if (param.size() == 1) {
										errorCode.add(errRoundTrip[6]);
									} else {
										errorCode.add(errRoundTrip[5]);
									}
								} else {
									redemResult = RT_RedemReturn(calSector);
									if (redemResult.getReturn_status() != MileageRedemReturn.SUCCESS_CODE) {
										returnFlightFlag = false;
										if (directFlightFlag) {
											errorCode.add(errRoundTrip[6]);
										} else {
											if (param.size() == 1) {
												errorCode.add(errRoundTrip[6]);
											} else {
												errorCode.add(redemResult.getErrorCode(0));
											}
										}
									}
								}
							} else {
								if (directFlightFlag) {
									errorCode.add(errGenRT[0]);
								} else {
									if (param.size() == 1)  {
										errorCode.add(errRoundTrip[6]);
									} else {
										errorCode.add(errGenRT[1]);
									}
								}
							}

							if ((!returnFlightFlag) && (!directFlightFlag)) {
								mcr = new MileageRedemReturn(null, MileageRedemReturn.ERROR_CODE, errorCode, null, null);
							} 
							if ((returnFlightFlag) && (!directFlightFlag)) {
		                				mcr = new MileageRedemReturn(redemResult.getMileage(), MileageRedemReturn.BASIC_ITIN_SUCCESS_CODE, null, null, calSector);
		                				mcr.setTopupFlag();
							} 
							if ((returnFlightFlag) && (directFlightFlag)) {
		                				mcr = new MileageRedemReturn(redemResult.getMileage(), MileageRedemReturn.BASIC_ALT_ITIN_SUCCESS_CODE, null, altRedemResult, calSector);
		                				mcr.setTopupFlag();
							} 
							if ((!returnFlightFlag) && (directFlightFlag)) {
		                				mcr = new MileageRedemReturn(null, MileageRedemReturn.ALT_ITIN_SUCCESS_CODE, errorCode, altRedemResult, null);
							} 
						}
					}
			    	} 	// end of MC spend results
			}	
		   	return mcr;	
		}
		catch (SQLException e) {
			Log.writeErrorLog(this.getClass().getName()+"->Error:"+e.getMessage());
			returnCode = errDatabase[1];
			mcr = MileageErrorReturn();
			return mcr;
		}
		finally {
			close_DBConnection();
		}
	}

	public MileageRedemReturn OW_RedemReturn (ArrayList sector) throws SQLException {
		String[] AwardTypes = null;
		ArrayList carriers = getCarrier(sector);
		ArrayList parentCarriers = getParentCarrier(sector);
		MileageRedemReturn mcr = null;
		AwardTypes = getAwardTypes(onewayInd);
		try {
		
			if (calNofOpenJaw(sector) > 0) {
				returnCode = errOneWay[0];
				mcr = MileageErrorReturn();
			} else {
		    		if (  ( (carriers.size() == 2) && 
		    				(parentCarriers.size() ==2) && 
							(!withAirlineCX(sector))
							) || 
							(sameCountry(sector) && (!domesticTravel(sector)))
						) {  // cppcnh 20030310
					returnCode = errRoundTrip[5];
					mcr = MileageErrorReturn();
		    		}  else {
					mcr = getCalRedemAward(AwardTypes,asiamilesProgram,sector);
					if (mcr == null) {
//						cppnxu update for REV.4463 20140312 START
						if (awardType.equals("OWPEC") &&
							carriers.size()==1 && parentCarriers.size()==1 &&
							!carriers.get(0).equals("CPA") &&
							!carriers.get(0).equals("BAW") &&
							!carriers.get(0).equals("QFA")) {
							returnCode = errRoundTrip[7];
						}else{
			    			if (eligibleCarrierAward(asiamilesProgram,carriers)) {
				    			returnCode = errRoundTrip[5];
			   	 		} else {
	    					if (awardTypeInd == OW_UgradeAwardTypeInd) {
								returnCode = errOneWay[1];
							} else {
			    				if (awardType.equals("OWPEC")) {
			    					returnCode = errRoundTrip[7];
			    				} else {		
			    					returnCode = errOneWay[2];
			    				}	
							}
			    		}
						}
			   			mcr = MileageErrorReturn();
					}
//					cppnxu update for REV.4463 20140312 END
		    	}
			}
		} catch (SQLException e) {}
		return mcr;
	}

	public MileageRedemReturn RT_RedemReturn (ArrayList sector) throws SQLException {
		String[] AwardTypes = null;
		int nofOpenJaw = 0;
		ArrayList carriers = getCarrier(sector);
		ArrayList parentCarriers = getParentCarrier(sector);
		MileageRedemReturn mcr = null;
		
		AwardTypes = getAwardTypes(roundtripInd);
		try {
		    if (!sameCountry(sector)) {
				returnCode = errRoundTrip[0];
				mcr = MileageErrorReturn();
		    } else {	
				nofOpenJaw = calNofOpenJaw (sector);
				if (nofOpenJaw > 2) { 
				    returnCode = errRoundTrip[1];
				    mcr = MileageErrorReturn();
				} else {
				    if (	(carriers.size() == 1) || 
				    		(parentCarriers.size() == 1) ||
				    		((parentCarriers.size() == 2) && withAirlineCX(sector))// ||
				    		//((parentCarriers.size() == 2) && isAMAward(parentCarriers) )
				    	) {
				
				    	if (nofOpenJaw > 1) {
						    returnCode = errRoundTrip[5];
						    mcr = MileageErrorReturn();
						} else {
						    mcr = getCalRedemAward(AwardTypes,asiamilesProgram,sector);
						    if (mcr == null) {
						    	if (eligibleCarrierAward(asiamilesProgram,carriers)) {
							    returnCode = errRoundTrip[5];
						       	} else {
						    	    if (awardType.equals("UEC")) {
						    	    	returnCode = errRoundTrip[2];
						    	    } else {
						    	    	if (awardType.equals("RTPEC")) {
						    	    	    returnCode = errRoundTrip[7];
						    	   	} else { 	   	
						    	   		switch (awardTypeInd) {
									        case RT_UgradeAwardTypeInd: 	
										    returnCode = errRoundTrip[3];
							  	    		    break;
									        case RT_CompAwardTypeInd:
										    returnCode = errRoundTrip[4];
										    break;
									        default: returnCode = errRoundTrip[5];
									    }
									}
							    }
							}
							mcr = MileageErrorReturn();
						    }
						}	
				    } else {
				    	if (!oneworldCarriers(oneworldProgram,carriers,withAirlineCX(sector), parentCarriers.size())) {
						    returnCode = errRoundTrip[6];
						    mcr = MileageErrorReturn();
						} else {
						    mcr = getCalRedemAward(AwardTypes,oneworldProgram,sector);
						    if (mcr == null) {
								returnCode = errRoundTrip[6];
							   	mcr = MileageErrorReturn();
						    }
						}
				    }			    
		    	}
		    }
		} catch (SQLException e) {
			Log.writeErrorLog(this.getClass().getName()+"->Error:"+e.getMessage());
		}
		
		return mcr;
	}

	public MileageRedemReturn getCalRedemAward (String[] AwardTypes,String program,ArrayList sector) throws SQLException {
		MileageRedemReturn m = null;
		result = new ArrayList();
		int reqTopup_Mileage = 0;
		int reqTopup_Money = 0;
		
		// PEY_DAY1  AM.com added one parameter airline  IBM 2011-2-17 Start 
		MileageCalSector temp_sector = (MileageCalSector)sector.get(0);
		String airline=temp_sector.getAirline();
		// PEY_DAY1  AM.com added one parameter airline  IBM 2011-2-17 Start 	
		
		String[] RTEC_awardType = {"RTEC","UEC"};
	

		if (eligibleItin(awardType,program,sector)) {  // cppcnh 20030310
			String zone = getCalZone(program,sector);
			//PEY_DAY1  AM.com added one parameter airline  IBM 2011-2-17 Start
		   	int reqAward = getCalSpendAward(zone,awardType,airline);
		   	int future_reqAward = getCalSpendAward(zone,awardType,FUTURE_DATE,airline);
		   	//PEY_DAY1  AM.com added one parameter airline  IBM 2011-2-17 Start
		   	if (reqAward != -1) {
//				reqTopup_Mileage = getCalTopup_Mileage(memberID,reqAward);
				reqTopup_Mileage = reqAward;
				if (reqTopup_Mileage == -1) {reqTopup_Money = reqTopup_Mileage;}
				else {reqTopup_Money = (int) ((reqTopup_Mileage / mileageBlock) * moneyPerBlock);}
				MileageCalAward returnRes = new MileageCalAward(awardType,reqAward,reqTopup_Mileage,reqTopup_Money, program, zone,future_reqAward);
				result.add(returnRes);
	   	    	if (RTEC_awardType[0].equals(awardType)) {  
	   	    	    if (eligibleItin(RTEC_awardType[1],program,sector)) {  
		   			    reqAward = getCalSpendAward(zone,RTEC_awardType[1],airline);
		   		 	    if (reqAward != -1) {
		   		 	    	reqTopup_Mileage = getCalTopup_Mileage(memberID,reqAward);
						if (reqTopup_Mileage == -1) {reqTopup_Money = reqTopup_Mileage;}
						else {reqTopup_Money = (int) ((reqTopup_Mileage / mileageBlock) * moneyPerBlock);}
							returnRes = new MileageCalAward(RTEC_awardType[1],reqAward,reqTopup_Mileage,reqTopup_Money);
							result.add(returnRes);
					    }
					}
				}
	   			m = new MileageRedemReturn(result, MileageRedemReturn.SUCCESS_CODE, null, null,sector);
		   		m.setTopupFlag();
		   	}
		} 
		return m;
	}

	public ArrayList getCalRedemAwardMiles (String[] AwardTypes,String program,ArrayList sector) throws SQLException {
		result = new ArrayList();
		int reqTopup_Mileage = 0;
		int reqTopup_Money = 0;
//		 PEY_DAY1  AM.com added one parameter airline IBM 2011-2-17 Start
		MileageCalSector temp_sector = (MileageCalSector)sector.get(0);
		String airline=temp_sector.getAirline();
//		 PEY_DAY1  AM.com added one parameter airline IBM 2011-2-17 End

		if (eligibleItin(awardType,program,sector)) {  
			String zone = getCalZone(program,sector);
//			 PEY_DAY1  AM.com added one parameter airline IBM 2011-2-17 Start
		   	int reqAward = getCalSpendAward(zone,awardType,airline);
		   	int future_reqAward = getCalSpendAward(zone,awardType,FUTURE_DATE,airline);
//			 PEY_DAY1  AM.com added one parameter airline  IBM 2011-2-17 End
		   	if (reqAward != -1) {
				reqTopup_Mileage = getCalTopup_Mileage(memberID,reqAward);
				if (reqTopup_Mileage == -1) {
					reqTopup_Money = reqTopup_Mileage;
				} else {
					reqTopup_Money = (int) ((reqTopup_Mileage / mileageBlock) * moneyPerBlock);
				}
				MileageCalAward returnRes = new MileageCalAward(awardType,reqAward,reqTopup_Mileage,reqTopup_Money, program, zone, future_reqAward);
				result.add(returnRes);
		   	}
		} 
		return result;
	}

	public String[] getAwardTypes (String OW_RT_Ind) {
	    boolean found = false;
	    String[] awardTypes = null;

	    if (OW_RT_Ind.equals(onewayInd)) {
		for (int i = 0; i < OW_AwardType.length; i++) {
		    if (OW_AwardType[i].equals(awardType)) {
		    	found = true;
		    }
		}
		if (found) {
		    awardTypeInd = OW_AwardTypeInd;
		    awardTypes = OW_AwardType;
		} else {
		    for (int i = 0; i < OW_UgradeAwardType.length; i++) {
				if (OW_UgradeAwardType[i].equals(awardType)) {
				    found = true;
				}
		    }
		    if (found) {
				awardTypeInd = OW_UgradeAwardTypeInd;
				awardTypes = OW_UgradeAwardType;
		    } else {
				String[] temp = {awardType};
				awardTypes = temp;
		    }
		}
	    } else {
		for (int i = 0; i < RT_AwardType.length; i++) {
			if (RT_AwardType[i].equals(awardType)) {
			    found = true;
			}
		}
		if (found) {
		    awardTypeInd = RT_AwardTypeInd;	
		    awardTypes = RT_AwardType;
		} else {
		    for (int i = 0; i < RT_UgradeAwardType.length; i++) {
				if (RT_UgradeAwardType[i].equals(awardType)) {
				    found = true;
				}
		    }
		    if (found) {
				awardTypeInd = RT_UgradeAwardTypeInd;	
				awardTypes = RT_UgradeAwardType;
		    } else {
			for (int i = 0; i < RT_CompAwardType.length; i++) {
				if (RT_CompAwardType[i].equals(awardType)) {
					found = true;
				}
			}
			if (found) {
			    awardTypeInd = RT_CompAwardTypeInd;
			    awardTypes = RT_CompAwardType;
			} else { 
			   String[] temp = {awardType};
			   awardTypes = temp;
			}
		    }
		}
	    }
	    return awardTypes;
	}
	
	public MileageRedemReturn MileageErrorReturn () {
		//Putting error code into ArrayList if there is error
		ArrayList errorCode = new ArrayList();
		errorCode.add(returnCode);
		//create more Properties object and put them into ArrayList errorCode if there is more than one error code
		mcr = new MileageRedemReturn(null, MileageRedemReturn.ERROR_CODE, errorCode, null, null);
		return mcr;
	}

	public int getCalMileage (String origin, String destination, String airline) throws SQLException {
		int miles = -1;
		String query = " ";
		try {
		   query = "Select distinct MARKETING_MILEAGE from SECTOR_REDEMPTION " +		
		  	   "where FKX_O_AIRPORT_CODE='" + origin +
		  	   "' and FKX_D_AIRPORT_CODE ='" + destination +
		  	   "' and FKX1_CARRIERCODE='" + airline + "'";
		   rset = stmt.executeQuery (query);
    		   while (rset.next ()) 
			miles = rset.getInt(1);
		} 
		catch (SQLException e) {}
		return miles;
	}

	// QTW RA-20040406 Start
	public String getFlightType (String origin, String destination, String airline) throws SQLException {
		String query = " ";
		String flightType = "  ";
		try {
		   query = "Select distinct FLIGHT_TYPE_IND from SECTOR_REDEMPTION " +		
		  	   "where FKX_O_AIRPORT_CODE='" + origin +
		  	   "' and FKX_D_AIRPORT_CODE ='" + destination +
		  	   "' and FKX1_CARRIERCODE='" + airline + "'";
		   rset = stmt.executeQuery (query);
    		   while (rset.next ()) 
			flightType = rset.getString (1);;
		} 
		catch (SQLException e) {}
		return flightType;
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
		
		if (withAirlineCX(airline))
			parent = cx;

			
		return parent;
	}
	
	public String getCarrier (String origin, String destination) throws SQLException {
		String carrierCode = " ";
		String query = " ";
		try {
		   	query = "Select FKX1_CARRIERCODE from SECTOR_REDEMPTION,Carrier_Item " +	
				"where FKX1_CARRIERCODE=FK1_CARRIER_CODE and " +
				"MIXED_CARRIER_FLAG='Y' and FK3_AWARD_PGM_TYPE='AWARD PROGRAM'" +
				" and START_DATE<=SYSDATE() and END_DATE>=SYSDATE()" +
				" and FK2_M_AND_A_CODE='" + awardType + "'" +
				" and FKX_O_AIRPORT_CODE='" + origin +
			  	"' and FKX_D_AIRPORT_CODE ='" + destination + "'";
			rset = stmt.executeQuery (query);
			while (rset.next ()) {
				carrierCode = rset.getString (1);
				if (withAirlineCX(carrierCode))
					break;
			}	
		} 
		catch (SQLException e) {}
		return carrierCode;
	}
	
	public int getCalTopup_Mileage (String memberID, int reqAward) throws SQLException {
		int topupMiles = -1;
		int totalMiles = 0;
		try {
			Hashtable hashtbl = new Hashtable();
	    		ArrayList vResult = new ArrayList();
	    		hashtbl.put("SappID", SappID);
   	 		hashtbl.put("Spassword", Spassword);
   	 		hashtbl.put("ImportCustomerIdNumber", memberID);
   	 		hashtbl.put("RequestRecordType", "00000000000");
   	 		hashtbl.put("RequestMore", "1");
   	 		hashtbl.put("Language", "ENG");
	    		String xmlStr = getXmlStr(xmlURL, hashtbl);
	    		vResult = xmlParsing(xmlStr);
		  	for (int r = 0; r < vResult.size(); r++) {
				Hashtable hs = (Hashtable) vResult.get(r);
				String mileage = (String) hs.get("CurrentAvailableAmMiles");
				totalMiles = Integer.parseInt(mileage);
			}
   
		   if (totalMiles >= reqAward) {topupMiles = 0;}
		   else {
		   	if (totalMiles >= reqAward * 0.7) {
		   		topupMiles = (int)((reqAward - totalMiles - 1)/mileageBlock + 1);
		   		topupMiles *= mileageBlock;
		   	}
		   }	
		} 
		catch (IOException f) {
			System.out.println ("IOException: " + f);
		}
		catch (SAXException g) {
			System.out.println ("SAXException: " + g);
		}
		return topupMiles;
	}

	public String getXmlStr(String url, Hashtable hash) throws MalformedURLException, IOException {
		Log logger = new Log();
		URLinvokerBean invoker = new URLinvokerBean(url);
		Enumeration e = hash.keys();
		while(e.hasMoreElements()){
			String key = (String) e.nextElement();
			String value = (String) hash.get(key); 
			invoker.addPostParam(key, value);
		}
		try {
			invoker.httpPost();
		} catch (Exception e1) {
			Log.writeErrorLog(e1.getMessage());
			//e1.printStackTrace();
		}
		return invoker.getHtmlContent();
	}
	
	public ArrayList xmlParsing(String xmlContent) throws SAXException, IOException {
		String [] listOfFieldsForPNS ={ "MpoMembershipJoinDate",
					"MemberTitle",
					"MemberFamilyName",
					"FullName",
					"LLCodePage",
					"LLFullName",
					"BirthDate",
					"EnrollmentDate",
					"CurrentTier",
					"CurrentTotalAmMiles",
					"CurrentAvailableAmMiles",
					"CurrentClubMiles",
					"CurrentClubSectors",
					"TierStartDate",
					"TierRenewalDate",
					"FirstBucketMiles",
					"FirstBucketExpiryDate",
					"FavouriteBusinessDestinations",
					"FavouriteLeisureDestinations",
					"PreferredMailingCountry"
					};
		
		ArrayList vOutput = new ArrayList();
		vOutput.clear();
		
        	// add the code for getting information from XML
		XMLParser parser = XMLParser.getInstance(XMLParser.SAX_PARSER);
		parser.parseXml(xmlContent);
		ParsedElementIF[] pElements = parser.getParsedElements();
		Hashtable tmpSet = new Hashtable();
		for(int i = 0; i < pElements.length; i++) {
			if(pElements[i] != null) {
				String eName = pElements[i].getName();
				if (eName != null) {
				   if (eName.indexOf("CurrentClubMiles") != -1 ) {
				   	if ( tmpSet.size() != 0 ) {
					   Hashtable tmpSetClone = (Hashtable) tmpSet.clone();
					   vOutput.add(tmpSetClone);
					   tmpSet.clear();
					}
				   }
				
				   for(int m = 0; m < listOfFieldsForPNS.length; m++) {	
				   	if(eName.equals(listOfFieldsForPNS[m])) {
				   	   if( (String)(pElements[i].getValue()) == null )
				   	   	tmpSet.put(listOfFieldsForPNS[m],"");
				   	   else {
					   	tmpSet.put(listOfFieldsForPNS[m],pElements[i].getValue());
					   }
				   	}
				   }  
				
				}
			}
		}
		pElements = null;
		parser = null;
		return vOutput;
	}

	public String getCalZone (String program, ArrayList sector) throws SQLException {
		String zone = " ";
		String zoneCheck = " ";
		boolean zoneFound = false;
		ArrayList carriers = getCarrier(sector);
		try {
		   int calDistance  = getCalDistance(program,sector);
 		   String query = "Select IDENTIFIER from Program_Zone " +
		  	   "where FK1_AWARD_PGM_CODE='" + program +
		  	   "' -- and START_DATE <= SYSDATE() and END_DATE >= SYSDATE() \n" +
		  	   "and MINIMUM_DISTANCE<=" +  calDistance + " and MAXIMUM_DISTANCE >=" + calDistance;
		   rset = stmt.executeQuery (query);
    		   while (rset.next ())
			zone = rset.getString(1);
		   if (program.equals(asiamilesProgram)) {
		     for (int i = 0; i < carriers.size(); i++) {
		      	if (!withAirlineCX(carriers.get(i).toString())) {
		      	   zoneCheck = zone.concat((String) carriers.get(i));
		      	   for (int j = 0; j < zoneExclude.length; j++) {
		      	   	if (zoneCheck.equals(zoneExclude[j])) {
		      	   	   zoneFound = true;
		      	   	   for (int k = 0; k < zoneMap.length; k++) {
							if (zone.equals(zoneMap[k])) {
							   k++;
							   zone = zoneMap[k];
							}
		      	   	   }
		      	   	}
		      	   }
		      	   if (!zoneFound) {
		      	   		zoneCheck = zoneCheck.concat((String) awardType);
						for (int a = 0; a < zoneExAward.length; a++) {
				      	   	   if (zoneCheck.equals(zoneExAward[a])) {
					      	   	   	zoneFound = true;
					      	   	   	for (int b = 0; b < zoneMap.length; b++) {
					      	   	   	   if (zone.equals(zoneMap[b])) {
					      	   	   	   	  b++;
					      	   	   	      zone = zoneMap[b];
					      	   	   	   }
					      	   	   	}
				      	   	   }
						}
		      	   }
		      	}
		     }
		   }
 		} 
		catch (SQLException e) {}
		return zone;
	}
 /*
  * 
  * comments by IBM  for add one parameter airline
  * 
  */
//	public int getCalSpendAward (String zone,String award_type) throws SQLException {
//		int miles = -1;
//		try {
//		   String query = "Select AWARD_VALUE from Program_Package,Package_Item " +
//		  	   "where FK1_PACKAGE_CODE=FK2_PACKAGE_CODE " + 
//		  	   "and START_DT <= SYSDATE() and END_DT >= SYSDATE() " +
//		  	   "and FK1_M_AND_A_CODE='" + award_type + "' and FK2_PP_AAZ_ID='" + zone + "'";
//		   rset = stmt.executeQuery (query);
//    		   while (rset.next ())
//			miles = rset.getInt(1);
//		} 
//		catch (SQLException e) {}
//		return miles;
//	}

//	public int getCalSpendAward (String zone,String award_type, String inDate) throws SQLException {
//		int miles = -1;
//		String dateTimeFormat = McPropBean.getProperty("DATETIME_FORMAT");;
//		try {
//		   String query = "Select AWARD_VALUE from Program_Package,Package_Item " +
//		  	   "where FK1_PACKAGE_CODE=FK2_PACKAGE_CODE " + 
//				
//		  	   "and to_date('" + inDate + "','" + dateTimeFormat + "') between START_DT and END_DT " + 
//		  	   "and FK1_M_AND_A_CODE='" + award_type + "' and FK2_PP_AAZ_ID='" + zone + "'";
//		   rset = stmt.executeQuery (query);
//    		   while (rset.next ())
//			miles = rset.getInt(1);
//		} 
//		catch (SQLException e) {}
//		return miles;
//	}
	/**
	 * add parameter airline;overLoad
	 * 
	 */
	public int getCalSpendAward (String zone,String award_type,String airline) throws SQLException {
		int miles = -1;
		try {
	   String query = "Select distinct Program_Package.AWARD_VALUE from Program_Package,Award_Package " +
	  	   "where Program_Package.FK1_PACKAGE_CODE=Award_Package.FK1_PACKAGE_CODE " + 
	  	 "and FK1_CARRIER_CODE='" + airline +
	  	   "' -- and START_DT <= SYSDATE() and END_DT >= SYSDATE() \n " +
	  	   "and FK2_M_AND_A_CODE='" + award_type + "' and FK2_PP_AAZ_ID='" + zone + "'";
		   rset = stmt.executeQuery (query);
    		   while (rset.next ())
			miles = rset.getInt(1);
		} 
		catch (SQLException e) {}
		return miles;
	}

	
	/**
	 * add parameter airline;overLoad
	 * 
	 */
	public int getCalSpendAward (String zone,String award_type, String inDate,String airline) throws SQLException {
		int miles = -1;
		//String dateTimeFormat = McPropBean.getProperty("DATETIME_FORMAT");;
		try {
	   String query = "Select Program_Package.AWARD_VALUE from Program_Package,Award_Package " +
	  	   "where Program_Package.FK1_PACKAGE_CODE=Program_Package.FK1_PACKAGE_CODE " +
	  	   "and FK1_CARRIER_CODE='" + airline +
	  	   "' and str_to_date('" + inDate + "','%d%m%Y') between START_DT and END_DT " +
	  	   "and FK2_M_AND_A_CODE='" + award_type + "' and FK2_PP_AAZ_ID='" + zone + "'";
		   rset = stmt.executeQuery (query);
    		   while (rset.next ())
			miles = rset.getInt(1);
		} 
		catch (SQLException e) {}
		return miles;
	}
	
	public int getCalDistance (String program, ArrayList sector)  throws SQLException {
		int distance = 0;
		int m1 = 0;
		int m2 = 0;
		int m = 0;
		try {
			int [] mileage = getSectorMileage(sector);
			if ((program.equals(oneworldProgram)) || 
		    	(program.equals(asiamilesProgram) && OWRT.equals(onewayInd))) {
				for (int i = 0; i < mileage.length; i++) {
					distance += mileage[i];
				}
			} else {
				for (int i = 0; i < mileage.length; i++) {
					m1 = m2 = 0;
					for (int j = 0; j < i; j++) {
						m1 += mileage[j];
					}
					for (int k = i; k < mileage.length; k++) {
						m2 += mileage[k];
					}
					if (m1 > m2) {m = m1;}
					else {m = m2;}
					if (i == 0) {distance = m;
					} else {
						if (m < distance) {distance = m;}
					}
				}
			}
		} catch (SQLException e) {}
		return distance;
	}	
		
	public boolean sameCountry (ArrayList sector) throws SQLException {
		boolean openJawFlag = false;
		try {
		   MileageCalSector first = (MileageCalSector)sector.get(0);
		   MileageCalSector last  = (MileageCalSector)sector.get(sector.size()-1);
		   String query = "Select count (distinct COUNTRY_SELECTED) from AIRPORT, CITY " +
		  	   "where FKX_MAIN_CITY_CODE = IATA_CODE and AIRPORT_CODE in ('" +
		  	   first.getOrigin() + "','" + last.getDestination() + "')";
		   rset = stmt.executeQuery (query);
		   while (rset.next ())
		   		openJawFlag = (rset.getInt(1) == 1);
		} 
		catch (SQLException e) {}
		return openJawFlag;
	}


	public boolean eligibleItin (String awardType, String awardProgram, ArrayList sector) throws SQLException {
		boolean eligibleItinFlag = true;
		String query = " ";
		ArrayList carriers = getCarrier(sector);
		ArrayList parentCarriers = getParentCarrier(sector);
		try {
		
	    	if ((carriers.size() == 1) || (parentCarriers.size() == 1) || (awardProgram.equals(oneworldProgram))) {  
	   	    	query = "Select count(*) from Carrier_Item " +
//	  	   		"where START_DATE <= SYSDATE() and END_DATE >= SYSDATE() " +
	  	   		"where 1=1 " +
	  	   		"and FK1_CARRIER_CODE = '" + carriers.get(0) +
	  	  		"' and FK2_M_AND_A_CODE = '" + awardType +
	  	   		"' and FK3_AWARD_PGM_CODE = '" + awardProgram + "'";
	   	    	rset = stmt.executeQuery (query);
		   	    	while (rset.next ())
				eligibleItinFlag &= (rset.getInt(1) > 0);
		   	    eligibleItinFlag &= allowPEYAirline(sector, awardType);
	    	} else {
	    		for (int i = 0; i < carriers.size(); i++) {
	   	    	query = "Select count(*) from Carrier_Item " +
	  	   		"where START_DATE <= SYSDATE() and END_DATE >= SYSDATE() " +
	  	   		"and FK1_CARRIER_CODE = '" + carriers.get(i) +
	  	  		"' and FK2_M_AND_A_CODE = '" + awardType +
	  	   		"' and FK3_AWARD_PGM_CODE = '" + awardProgram +
	  	   		"' and MIXED_CARRIER_FLAG = 'Y'";
	   	    	rset = stmt.executeQuery (query);
		   	    	while (rset.next ())
				eligibleItinFlag &= (rset.getInt(1) > 0);
			}
	    		eligibleItinFlag &= withAirlineCX(sector);
	    	}

			eligibleItinFlag &= eligibleSectorLimit(awardType,awardProgram,sector);                                                              		//SR143
		} 
		catch (SQLException e) {}
		return eligibleItinFlag;
	}

	 public int calSectorLimit (boolean allowOpenjaw, boolean allowStopover, boolean allowTransfer, String OWRT, String awardProgram) { //SR143
	 	  int sectorLimit = 1;
	 	  try {
	 	   if (OWRT.equals(onewayInd)) {
	 	    if (allowStopover || allowTransfer) 
	 	     sectorLimit++;
	 	   }
	 	   else {
	 	    if (awardProgram.equals(asiamilesProgram)) {
	 	     if (allowOpenjaw) 
	 	     {                                                                                         
	 	        sectorLimit = sectorLimit + calNofOpenJaw(calSector);  
	 	     }                                                                                          
	 	     if (allowStopover) 
	 	      sectorLimit = sectorLimit + 2;
	 	     if (allowTransfer) 
	 	      sectorLimit = sectorLimit + 2;    
	 	     if ((!allowStopover) && (!allowTransfer))                             
	 	      sectorLimit++;                                                                    
	 	    }
	 	    else {
	 	     if (allowOpenjaw) 
	 	     {                                                                                          
	 	        sectorLimit = sectorLimit + calNofOpenJaw(calSector);  
	 	     }                                                                                          
	 	     if (allowStopover) 
	 	     	sectorLimit = sectorLimit + 5;
	 	     if (allowTransfer) 
	 	     	sectorLimit = sectorLimit + 2;
	 	     if ((!allowStopover) && (!allowTransfer))                             
	 	     	sectorLimit++;                                                                    
	 	    }
	 	   }
	 	  } 
	 	  catch (Exception e) {}
	 	  return sectorLimit;
	 	 } 



	public boolean eligibleSectorLimit (String awardType, String awardProgram, ArrayList sector) throws SQLException {                                                 //SR143
		String query = " ";
		String OWRT = " ";
		boolean allowOpenjaw = true;
		boolean allowStopover = true;
		boolean allowTransfer = true;
		boolean eligibleFlag = true;
		try {
		     	String awardTypeQuery = "select distinct FK2_RT_OW_IND_CODE from AWARD_TYPE" +
				 		" where CODE ='" + awardType + "'";
			rset = stmt.executeQuery (awardTypeQuery);
    			while (rset.next ())
       				OWRT = rset.getString (1);
			if (allowMixedCarrier(awardType,sector)) {
				if (calSectorLimit(allowOpenjaw,allowStopover,allowTransfer,OWRT,awardProgram) < sector.size()) {
					eligibleFlag = false;                                                                                                            
				}
			}
			else {
				if (getParentCarrier(sector).size() == 1) {
					query = "Select DISALLOW_OPEN_JAW,DISALLOW_STOPOVER,DISALLOW_TRANSFER from Carrier " +
						"where CARRIER_CODE='" + getCarrier(sector).get(0) + "'";
					rset = stmt.executeQuery (query);
					while (rset.next ()) {
						allowOpenjaw  &= rset.getString(1).equals("N");	
						allowStopover &= rset.getString(2).equals("N");
						allowTransfer &= rset.getString(3).equals("N");
					}
					if ((!allowOpenjaw && (calNofOpenJaw(sector) > 0)) || (calSectorLimit(allowOpenjaw,allowStopover,allowTransfer,OWRT,awardProgram) < sector.size())) {
						eligibleFlag = false;                                                                                                            
					}
				}
				else {
					eligibleFlag = false;                                                                                                            
				}
			}
		} 
		catch (SQLException e) {} 
		return eligibleFlag;
	}

	public boolean eligibleCarrierAward (String awardProgram, ArrayList carriers) throws SQLException {
		String query = " ";
		boolean eligibleFlag = true;
		try {
		    	for (int i = 0; i < carriers.size(); i++) {
		   	    query = "Select count(*) from Carrier_Item " +
		  	   	"where START_DATE <= SYSDATE() and END_DATE >= SYSDATE() " +
		  	   	"and FK1_CARRIER_CODE = '" + carriers.get(i) +
		  	  	"' and FK2_M_AND_A_CODE = '" + awardType +
		  	   	"' and FK3_AWARD_PGM_CODE = '" + awardProgram + "'";
		   	    rset = stmt.executeQuery (query);
    		   	    while (rset.next ())
				eligibleFlag &= (rset.getInt(1) > 0);
			}
		} 
		catch (SQLException e) {} 
		return eligibleFlag;
	}
	
	public boolean oneworldCarriers (String program,ArrayList carriers, boolean withAirlineCX, int parentCarriersSize) throws SQLException {
		boolean oneworldFlag = true;
		try {
		    for (int i = 0; i < carriers.size(); i++) {
		   	String query = "Select count (*) from Carrier_Item " +
		  	   "where FK3_AWARD_PGM_CODE='" + program + 
		  	   "' and FK1_CARRIER_CODE='" + carriers.get(i) + "'";
		   	rset = stmt.executeQuery (query);
    		   	while (rset.next ())
				oneworldFlag &= (rset.getInt(1) > 0);
		    } 
		}
		catch (SQLException e) {}
		
		//return oneworldFlag && ( withAirlineCX && parentCarriersSize > 2 || parentCarriersSize <=2);
		return oneworldFlag;
	}

	public boolean domesticTravel (ArrayList sector) throws SQLException {
		boolean domestic = true;
		try {
		    for (int i = 0; i < sector.size(); i++) {
		    	MileageCalSector s = (MileageCalSector)sector.get(i);
		   	String query = "Select count (distinct COUNTRY_SELECTED) from AIRPORT, CITY " +
		  	   "where FKX_MAIN_CITY_CODE = IATA_CODE and AIRPORT_CODE in ('" +
		  	   s.getOrigin() + "','" + s.getDestination() + "')";
		   	rset = stmt.executeQuery (query);
  		   	while (rset.next ())
				domestic &= (rset.getInt(1) == 1);		    
		    }
		    if (domestic) {
		    	for (int j = 0; j < sector.size()-1; j++) {
		    		MileageCalSector next = (MileageCalSector)sector.get(j+1);
		    		MileageCalSector prev = (MileageCalSector)sector.get(j);
		       		String query = "Select count (distinct COUNTRY_SELECTED) from AIRPORT, CITY " +
		  	   		"where FKX_MAIN_CITY_CODE = IATA_CODE and AIRPORT_CODE in ('" +
		  	   		next.getOrigin() + "','" + prev.getDestination() + "')";
		   		rset = stmt.executeQuery (query);
  		   		while (rset.next ())
					domestic &= (rset.getInt(1) == 1);		    
		    	}		    
		    }	
		}    
		catch (SQLException e) {}
		return domestic;		
	}
	
	public int calNofOpenJaw (ArrayList sector) throws SQLException {
		int nofOpenJaw = 0;
		try {
		    for (int i = 0; i < sector.size()-1; i++) {
		    	MileageCalSector prev = (MileageCalSector)sector.get(i);
		    	MileageCalSector next = (MileageCalSector)sector.get(i+1);
		   	String query = "Select count (distinct FKX_MAIN_CITY_CODE) from AIRPORT " +
		  	   "where AIRPORT_CODE in ('" + prev.getDestination() + "','" + next.getOrigin() + "')";
		   	rset = stmt.executeQuery (query);
    		   	while (rset.next ())
				if (rset.getInt(1) == 2) {
					nofOpenJaw += 1;
				}
		    }
		    if (sameCountry(sector) && !sameOD(sector)) {
		    	if ((OWRT.equals(roundtripInd)) || (OWRT.equals(onewayInd) && !domesticTravel(sector))) {  // cppcnh 20030310
		    		nofOpenJaw += 1;
		    	}  // cppcnh 20030310
		    }
		} 
		catch (SQLException e) {}
		return nofOpenJaw;
	}

	public boolean allowMixedCarrier (String awardType, ArrayList sector) throws SQLException {
		boolean flag = true;
		boolean amAward = false;
		
		ArrayList carriers = getCarrier(sector);
		ArrayList parentCarriers = getParentCarrier(sector);
		if (
		(carriers.size() == 1) || 
		(parentCarriers.size() == 1) ||
		((parentCarriers.size() == 2) && withAirlineCX(sector))) {
			amAward = true;
		}
		
		Log.writeInfoLog("allowMixedCarrier - amAward: " + amAward);	
		
		try {
		   	for (int i = 0; i < sector.size(); i++) {	
       				MileageCalSector m = (MileageCalSector)sector.get(i);
  	     			String mixedCarrierQuery = "Select count(*) from Carrier_Item " +
					"where FK1_CARRIER_CODE='" + m.getAirline() + "'" +
					" and FK3_AWARD_PGM_TYPE='AWARD PROGRAM'" +
					" and START_DATE<=SYSDATE() and END_DATE>=SYSDATE()" +
					" and FK2_M_AND_A_CODE='" + awardType + "'";
					if (amAward)
						mixedCarrierQuery +=	  	     			
					" and MIXED_CARRIER_FLAG = 'Y'";
				rset = stmt.executeQuery (mixedCarrierQuery);
				while (rset.next ()) 
					flag &= (rset.getInt (1) > 0);
			}
		} catch (SQLException e) {}
		return flag;		
	}

	public int [] getSectorMileage (ArrayList sector)  throws SQLException {
		int[] mileage = new int[sector.size()];
		try {
			for (int i = 0; i < sector.size(); i++) {
				MileageCalSector p = (MileageCalSector)sector.get(i);
				mileage[i] = getCalMileage(p.getOrigin(),p.getDestination(),p.getAirline());
			}
		} catch (SQLException e) {}
		return mileage;
	}

	
	/***
	 * 
	 * @author CPPALAC
	 *
	 * SR177 - KA oneworld (Flight award calculator)
	 * Window - Preferences - Java - Code Style - Code Templates
	 */	 
	private ArrayList getOtherAirlines()
	{
		ArrayList rtnAirlines = new ArrayList();
		rtnAirlines.add(cx);
		
		// read airline code from database
		String query = " ";
		try {
			get_DBConnection();
		   	query = "select CARRIER.CARRIER_CODE from TUTPARM " +
					"inner join CARRIER on CARRIER.AIRLINE_DESIGNATOR = TUTPARM.PARAM_VALUE and PARAM_NAME = 'OTHER HOSTS' ";
			rset = stmt.executeQuery (query);
			while (rset.next ()) {
				rtnAirlines.add(rset.getString(1));
			}	
		} catch (SQLException e) {
			Log.writeErrorLog(this.getClass().getName()+"->Error:"+e.getMessage());
		} catch (Exception e) {
			Log.writeErrorLog(this.getClass().getName()+"->Error:"+e.getMessage());
		}
		finally {
			close_DBConnection();
		}
		
		
		return rtnAirlines; 
	}
	
	public boolean withAirlineCX (String airlineCode) {
		boolean flag = false;

		// SR177 - KA oneworld (Flight award calculator)
		for (int j = 0; j < otherAirlines.size(); j++) {
			if (otherAirlines.get(j).equals(airlineCode)) {
				flag = true;
			}
		}

		return flag;		
	}
	
	public boolean withAirlineCX (ArrayList sector) {
		boolean flag = false;
     	for (int i = 0; i < sector.size(); i++) {
			MileageCalSector p = (MileageCalSector)sector.get(i);
			// SR177 - KA oneworld (Flight award calculator)
			for (int j = 0; j < otherAirlines.size(); j++) {
				if (otherAirlines.get(j).equals(p.getAirline())) {
					flag = true;
				}
			}
		}
		return flag;		
	}

	public boolean sameOD (ArrayList sector) {
		boolean flag = true;
		MileageCalSector first = (MileageCalSector)sector.get(0);
		MileageCalSector last  = (MileageCalSector)sector.get(sector.size()-1);
		return flag = (first.getOrigin().equals(last.getDestination()));
	}

	public ArrayList getCarrier (ArrayList sector) {
		ArrayList carrier = new ArrayList();
		boolean marchFlag  = false;  
		for (int i = 0; i < sector.size(); i++) {	
       			MileageCalSector m = (MileageCalSector)sector.get(i);
		     	for (int j = 0; j < carrier.size(); j++) {
		     		if (carrier.get(j).equals(m.getAirline())) {
					marchFlag = true;
				}
		     	}
		     	if (!marchFlag) {
		     	 	carrier.add(m.getAirline());
		     	 } else {marchFlag = false;}
		}
		return carrier;
	}
	
	public ArrayList getParentCarrier (ArrayList sector) throws SQLException {
		ArrayList parentCarrier = new ArrayList();
		boolean marchFlag  = false;  
		try {
			for (int i = 0; i < sector.size(); i++) {	
       				MileageCalSector m = (MileageCalSector)sector.get(i);
				String tempAirline = m.getAirline();  
				String tempParent = getParentAirline(tempAirline);  
		     	 	for (int k = 0; k < parentCarrier.size(); k++) {
		     	 		if (parentCarrier.get(k).equals(tempParent)) {
		     	 			marchFlag = true;
		     	 		}	
		     	 	}
		     	 	if (!marchFlag) {	
		     	 		parentCarrier.add(tempParent);
		     	 	} else {marchFlag = false;}  
			}
		} catch (SQLException e) {}
		return parentCarrier;
	}
	
	public void get_DBConnection () throws Exception {
//		xmlURL = McPropBean.getProperty("xmlURL");
//		SappID = McPropBean.getProperty("SappID");
//	 	Spassword = McPropBean.getProperty("Spassword");

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
	
	//cppnxu 20140225 add for JJ start
	private boolean allowPEYAirline(ArrayList sector,String awardType){
		boolean flag=true;
		ArrayList carriers = getCarrier(sector);
		String carrier=(String)carriers.get(0);
		if (awardType.equals("OWPEC") && !carrier.equals("CPA") && !carrier.equals("BAW") && !carrier.equals("QFA")) {
			flag=false;
		}
		Log.writeInfoLog("allow PEY " + flag);
		return flag;
	}
	//cppnxu 20140225 add for JJ start
	
	
}
