package com.asiamiles.ixClsMileageCal.beans.calculator;

import java.util.ArrayList;

import javax.servlet.http.HttpServletRequest;

import com.asiamiles.ixClsMileageCal.beans.common.Log;


/**
 * <p> MileageCalParamObj is used to generate a MileageCalParam object and do the validation
 * on the input parameters.
 * <p> Created at: 09/APR/2001
 * <br> Updated at: 10/MAY/2002 by Richard Huang (Ion Global) = Removed business logic checking
 *
 * @author	Maggie Yip (IMT E-Business)
 * @version	%I%, %G%
 * @since	JDK1.1.8
 */

 public class ParamValidate {
	public static final int EMPTY_CODE = 0;			//status: the http request contains no parameters.
	public static final int SUCCESS_CODE = 1;		//status: validation process is success
	public static final int ERROR_CODE = -1;		//status: validation is fail

	public final String IN_ERROR_AWARDTYPE 		= "IN001";	//error list
	public final String IN_ERROR_SECTORS 		= "IN002";	//error list
	public final String IN_ERROR_SECTORNUMBER 	= "IN003";	//error list
	public final String IN_ERROR_RETURNTRIP 	= "IN004";	//error list

	private final String origin_name 		= MileageCalConstants.REQUEST_PARAMETER_ORIGIN_CODE;
	private final String destination_name 	= MileageCalConstants.REQUEST_PARAMETER_DESTINATION_CODE;
	private final String airline_name 		= MileageCalConstants.REQUEST_PARAMETER_AIRLINE_CODE;
	private final String class_name 		= MileageCalConstants.REQUEST_PARAMETER_CLASS_CODE;
	private final String awardType_name 	= MileageCalConstants.REQUEST_PARAMETER_AWARDTYPE_CODE;
	private final String memberID_name 		= MileageCalConstants.REQUEST_PARAMETER_MEMBERID;
	private final String awardClass_name 	= MileageCalConstants.REQUEST_PARAMETER_AWARDCLASS_CODE;
	private final int earnSectors = 4;
	private final int spendSectors = 5;
	

	private String memberID;
	private ArrayList sectors;
	private String awardType;
	private int noOfSector = 0;
	private boolean roundTrip;

	private int status;
	private MileageCalParam mcParam;
	private ArrayList errorList;


	public ParamValidate(HttpServletRequest request, String calculatorInd) {
		sectors = new ArrayList();
		errorList = new ArrayList();

		status = ParamValidate.EMPTY_CODE;					//default value
		Log.writeDebugLog(this.getClass().getName()+"->ParamValidate - EMPTY_CODE");
		String calculatorAction = request.getParameter("calculatorAction");
		
		if ((calculatorAction == null || calculatorAction.equals("")) && (calculatorInd != null && (!calculatorInd.equals("spend"))) ) {
			status = ParamValidate.EMPTY_CODE;
			if ((memberID = request.getParameter(memberID_name)) == null || memberID.equals("")) {
				memberID = "-1";
			}
			if (calculatorInd != null && calculatorInd.equals("earn")) { 					//either "earn" or "redeem"
				mcParam = new MileageCalParam(0, null, memberID, false);
			} else {
				mcParam = new MileageCalParam(0, "", null, memberID);
			}
		} else {
			status = ParamValidate.SUCCESS_CODE;
			Log.writeDebugLog(this.getClass().getName()+"->ParamValidate - SUCCESS_CODE");
			if(calculatorAction != null && calculatorAction.equals("oneway")) {
				roundTrip = false;
			} else {
				roundTrip = true;
			}
			if (calculatorInd.equals("earn")) { 					//either "earn" or "redeem"
				this.validateEarnParameters(request);
				Log.writeDebugLog(this.getClass().getName()+"->ParamValidate earn - noOfSector=" + noOfSector + " memberID=" + memberID + " type=" + roundTrip);
				mcParam = new MileageCalParam(noOfSector, sectors, memberID, roundTrip);
			} else {
				this.validateRedeemParameters(request);
				Log.writeDebugLog(this.getClass().getName()+"->ParamValidate spend - noOfSector=" + noOfSector + " memberID=" + memberID + " awardType=" + awardType);
				mcParam = new MileageCalParam(noOfSector, awardType, sectors, memberID);
			}
		}
	}

	private void validateEarnParameters(HttpServletRequest request) {
		//validate each value in each sector
		ArrayList temp = new ArrayList();
		boolean flag = false;
		for (int i = earnSectors; i > 0 ; i--) {
			int emptyFlag = 0;
			String origin, destination, airline, classType;
//			debug
			Log.writeDebugLog(this.getClass().getName()+"->i="+i);
			if ((origin = request.getParameter(origin_name + String.valueOf(i))) == null || origin.equals("")) {
//				debug
//				Log.writeDebugLog(this.getClass().getName()+"->(origin = request.getParameter(origin_name + String.valueOf(i))) == null || origin.equals('')\norigin="+origin);
				origin = "";
				flag = true;
				emptyFlag = emptyFlag + 1;
			}
			if ((destination = request.getParameter(destination_name + String.valueOf(i))) == null || destination.equals("")) {
//				debug
//				Log.writeDebugLog(this.getClass().getName()+"->(destination = request.getParameter(destination_name + String.valueOf(i))) == null || destination.equals('')\ndestination="+destination);
				destination = "";
				flag = true;
				emptyFlag = emptyFlag + 1;
			}
			if ((airline = request.getParameter(airline_name + String.valueOf(i))) == null || airline.equals("")) {
//				debug
//				Log.writeDebugLog(this.getClass().getName()+"->(airline = request.getParameter(airline_name + String.valueOf(i))) == null || airline.equals('')\nairline="+airline);
				airline = "";
				flag = true;
				emptyFlag = emptyFlag + 1;
			}
			if ((classType = request.getParameter(class_name + String.valueOf(i))) == null || classType.equals("")) {
//				debug
//				Log.writeDebugLog(this.getClass().getName()+"->(classType = request.getParameter(class_name + String.valueOf(i))) == null || classType.equals('')\nclassType="+classType);
				classType = "";
				flag = true;
				emptyFlag = emptyFlag + 1;
			}
			Log.writeDebugLog(this.getClass().getName()+"->i="+i+" origin=" + origin + " destination=" + destination + " airline=" + airline + " classType=" + classType);
			if (temp.size() == 0 && emptyFlag == 4) {
//				debug
				Log.writeDebugLog(this.getClass().getName()+"->temp.size() == 0 && emptyFlag == 4");
				flag = false;
			} else {
				MileageCalSector mcs = new MileageCalSector(origin, destination, airline, classType, null);
				temp.add(mcs);
			}
		}

		if (temp.size() == 0) {
			noOfSector = 1;
			flag = true;
			sectors.add(new MileageCalSector("", "", "", "", ""));
		} else {
			int k = temp.size() - 1;
			for (int i = 0; i < temp.size(); i ++) {
				sectors.add(temp.get(k));
				k--;
			}
			noOfSector = sectors.size();
			Log.writeDebugLog(this.getClass().getName()+"->noOfSector=" + noOfSector);
		}

		if (flag) {
			errorList.add(this.IN_ERROR_SECTORS);
			status = ParamValidate.ERROR_CODE;
			Log.writeDebugLog(this.getClass().getName()+"->ParamValidate - earn ERROR_CODE");
		}

		//check whether member ID has been passed in
		if ((memberID = request.getParameter(memberID_name)) == null || memberID.equals("")) {
			memberID = "-1";
		}

	}

	private void validateRedeemParameters(HttpServletRequest request) {
		//validate awardClass ( in amv3 ), in previous version, awardType is used.
		if ((awardType = request.getParameter(awardClass_name)) == null || awardType.equals("")) {
			awardType = "";
			status = ParamValidate.ERROR_CODE;
			errorList.add(this.IN_ERROR_AWARDTYPE);
			Log.writeDebugLog(this.getClass().getName()+"->ParamValidate - spend ERROR_CODE 1");
		}
		Log.writeDebugLog(this.getClass().getName()+"->awardClass="+awardType);

		//validate each value in each sector
		ArrayList temp = new ArrayList();
		boolean flag = false;

		for (int i = spendSectors; i > 0 ; i--) {
			int emptyFlag = 0;
			String origin, destination, airline;
			if ((origin = request.getParameter(origin_name + String.valueOf(i))) == null || origin.equals("")) {
				origin = "";
				flag = true;
				emptyFlag = emptyFlag + 1;
			}
			if ((destination = request.getParameter(destination_name + String.valueOf(i))) == null || destination.equals("")) {
				destination = "";
				flag = true;
				emptyFlag = emptyFlag + 1;
			}
			if ((airline = request.getParameter(airline_name + String.valueOf(i))) == null || airline.equals("")) {
				airline = "";
				flag = true;
				emptyFlag = emptyFlag + 1;
			}
			
			Log.writeDebugLog(this.getClass().getName()+"->i="+i+" origin=" + origin + " destination=" + destination + " airline=" + airline);
			
			if (temp.size() == 0 && emptyFlag == 3) {
				flag = false;
			} else {
				MileageCalSector mcs = new MileageCalSector(origin, destination, airline, null);
				temp.add(mcs);
			}
		}

		if (temp.size() == 0) {
			noOfSector = 1;
			flag = true;
			sectors.add(new MileageCalSector("", "", "", ""));
		} else {
			int k = temp.size() - 1;
			for (int i = 0; i < temp.size(); i ++) {
				sectors.add(temp.get(k));
				k--;
			}
			noOfSector = sectors.size();
		}
		if (flag) {
			errorList.add(this.IN_ERROR_SECTORS);
			status = ParamValidate.ERROR_CODE;
			Log.writeInfoLog(this.getClass().getName()+"->ParamValidate - spend ERROR_CODE 2");
		}

		//check whether member ID has been passed in
		if ((memberID = request.getParameter(memberID_name)) == null || memberID.equals("")) {
			memberID = "-1";
		}
	}

	public String getMemberID() {
		return this.memberID;
	}

	public ArrayList getSectors() {
		return this.sectors;
	}

	public String getAwardType() {
		return this.awardType;
	}

	public int getStatus() {
		return this.status;
	}

	public MileageCalParam getMileageCalParam() {
		return this.mcParam;
	}

	public ArrayList getErrorList() {
		return errorList;
	}

}