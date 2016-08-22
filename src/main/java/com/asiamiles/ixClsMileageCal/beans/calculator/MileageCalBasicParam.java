/*
 * Created on Oct 26, 2004
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package com.asiamiles.ixClsMileageCal.beans.calculator;

import java.util.ArrayList;
import java.util.ArrayList;

import javax.servlet.http.HttpServletRequest;

import com.asiamiles.ixClsMileageCal.beans.common.Log;

/**
 * @author IMTLKM
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class MileageCalBasicParam {
	public final int maxNoOfSector = 4;
	public final int minNoOfSector = 1;
	public String calculatorInd="";
	public String calculatorAction="";
	public String submitType="";
	public String language="";
	public String memberID="";
	public String site="";
	public String type="";
	public ArrayList errorMsg;
	public boolean airlineState[];
	public boolean originState[];
	public boolean destinationState[];
	public boolean classState[];
	public int noOfSector;
	public String awardClass= "";
	
	public final String PARAM_ORIGIN 		= MileageCalConstants.REQUEST_PARAMETER_ORIGIN_CODE;
	public final String PARAM_DESTINATION 	= MileageCalConstants.REQUEST_PARAMETER_DESTINATION_CODE;
	public final String PARAM_ORIGIN_TEXT	= MileageCalConstants.REQUEST_PARAMETER_ORIGIN_TEXT;
	public final String PARAM_DESTINATION_TEXT	= MileageCalConstants.REQUEST_PARAMETER_DESTINATION_TEXT;
	public final String PARAM_AIRLINE 		= MileageCalConstants.REQUEST_PARAMETER_AIRLINE_CODE;
	public final String PARAM_CLASS 		= MileageCalConstants.REQUEST_PARAMETER_CLASS_CODE;
	public final String PARAM_SECTOR_NO		= MileageCalConstants.REQUEST_PARAMETER_SECTOR_NO;

	public final String SPEND	= MileageCalConstants.CALCULATOR_IND_SPEND;
	public final String EARN	= MileageCalConstants.CALCULATOR_IND_EARN;

	public MileageCalBasicParam() {
		super();
		errorMsg = new ArrayList();
		airlineState = new boolean[maxNoOfSector];
		originState = new boolean[maxNoOfSector];
		destinationState = new boolean[maxNoOfSector];
		classState = new boolean[maxNoOfSector];
		for(int i=0; i < maxNoOfSector; i++) {
			airlineState[i] = true;
			originState[i] = true;
			destinationState[i] = true;
			classState[i] = true;
		}
	}
	
	public boolean firstLvlParamValidate(HttpServletRequest request, String calculatorInd) {
		if(calculatorInd.equals(SPEND)) {
			return spendParamValidate(request);
		} else {
			return earnParamValidate(request);
		}
	}
	
	private boolean spendParamValidate(HttpServletRequest request) {
		boolean overallState = true;
		String s="";
		String so="";
		String sd="";
		try {
			noOfSector = Integer.parseInt(request.getParameter(PARAM_SECTOR_NO));
		} catch (Exception e) {
			noOfSector = 1;
		}
		boolean wholeSectorEmptyState[] = new boolean[maxNoOfSector];
		int lastFilledSector=0;
		for(int i = 0; i < noOfSector; i++) {
			int j = i+1;
			s = request.getParameter(PARAM_AIRLINE+j);
			if(s == null || s.equals("")) {
				airlineState[i] = false;
			}
			s = request.getParameter(PARAM_ORIGIN+j);
			so = request.getParameter(PARAM_ORIGIN_TEXT+j);
			if(s == null || s.equals("")) {
				originState[i] = false;
			}
			s = request.getParameter(PARAM_DESTINATION+j);
			sd = request.getParameter(PARAM_DESTINATION_TEXT+j);
			if(s == null || s.equals("")) {
				destinationState[i] = false;
			}
			wholeSectorEmptyState[i] = !(airlineState[i] || originState[i] || destinationState[i]);
			wholeSectorEmptyState[i] =  wholeSectorEmptyState[i] && (so == null || so.equals("")) && (sd == null || sd.equals(""));
			if(wholeSectorEmptyState[i] == true) {
				if(i == 0) {
					overallState = false;
				} else {
					overallState = overallState && true;
					airlineState[i] = true;
					originState[i] = true;
					destinationState[i] = true;
				}
			} else {
				overallState = overallState && airlineState[i] && originState[i] && destinationState[i];
				lastFilledSector = i;
			}
			Log.writeDebugLog(this.getClass().getName()+"->i="+i+ 
					"; overallState="+overallState +
					"; airlineState[i]="+airlineState[i] +
					"; originState[i]="+originState[i] +
					"; destinationState[i]="+destinationState[i]);
		}
		for(int i=1; i < lastFilledSector; i++) {
			if(wholeSectorEmptyState[i] == true) {
				overallState = overallState && false;
				airlineState[i] = false;
				originState[i] = false;
				destinationState[i] = false;
			}
		}
		if(overallState == false) {
			errorMsg.add(getErrorPrefix(SPEND)+"IN002");
		}
		return overallState;
	}
	
	private boolean earnParamValidate(HttpServletRequest request) {
		boolean overallState = true;
		try {
			noOfSector = Integer.parseInt(request.getParameter(PARAM_SECTOR_NO));
		} catch (Exception e) {
			noOfSector = 1;
		}
		boolean wholeSectorEmptyState[] = new boolean[maxNoOfSector];
		int lastFilledSector=0;
		for(int i = 0; i < noOfSector; i++) {
			int j = i+1;
			String s = request.getParameter(PARAM_AIRLINE+j);
			if(s == null || s.equals("")) {
				airlineState[i] = false;
			}
			s = request.getParameter(PARAM_ORIGIN+j);
			if(s == null || s.equals("")) {
				originState[i] = false;
			}
			s = request.getParameter(PARAM_DESTINATION+j);
			if(s == null || s.equals("")) {
				destinationState[i] = false;
			}
			s = request.getParameter(PARAM_CLASS+j);
			if(s == null || s.equals("")) {
				classState[i] = false;
			}
			wholeSectorEmptyState[i] = !(airlineState[i] || originState[i] || destinationState[i] || classState[i]);
			if(wholeSectorEmptyState[i] == true) {
				if(i == 0) {
					overallState = false;
				} else {
					overallState = overallState && true;
					airlineState[i] = true;
					originState[i] = true;
					destinationState[i] = true;
					classState[i] = true;
				}
			} else {
				overallState = overallState && airlineState[i] && originState[i] && destinationState[i] && classState[i];
				lastFilledSector = i;
			}
		}
		for(int i=1; i < lastFilledSector; i++) {
			if(wholeSectorEmptyState[i] == true) {
				overallState = overallState && false;
				airlineState[i] = false;
				originState[i] = false;
				destinationState[i] = false;
				classState[i] = false;
			}
		}
		if(overallState == false) {
			errorMsg.add(getErrorPrefix(EARN)+"IN002");
		}
		return overallState;
	}
	
	private String getErrorPrefix(String earnOrSpend) {
		if(earnOrSpend.equals(EARN)) {
			return "error_earn_";
		} else {
			return "error_redeem_";
		}
	}
	
	public void calculationErrors(MileageCalReturn mcReturn) {
		String errorPrefix = getErrorPrefix(calculatorInd);
		ArrayList errorCodeList = mcReturn.getErrorCode();
		if (errorCodeList != null && errorCodeList.size() > 0)
		{
			for (int i=0; i < errorCodeList.size(); i++) {
				Log.writeDebugLog(this.getClass().getName() + "calculationErrors->" + i + " - " + errorCodeList.get(i));
				errorMsg.add(errorPrefix+errorCodeList.get(i));
			}
		}
		
	}
	

}
