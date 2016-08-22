package com.asiamiles.ixClsMileageCal.servlets.calculator;

import java.io.IOException;
import java.util.ArrayList;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.asiamiles.ixClsMileageCal.beans.calculator.MileageCalBasicParam;
import com.asiamiles.ixClsMileageCal.beans.calculator.MileageCalConstants;
import com.asiamiles.ixClsMileageCal.beans.calculator.MileageCalEarn;
import com.asiamiles.ixClsMileageCal.beans.calculator.MileageCalParam;
import com.asiamiles.ixClsMileageCal.beans.calculator.MileageCalRedem;
import com.asiamiles.ixClsMileageCal.beans.calculator.MileageCalReturn;
import com.asiamiles.ixClsMileageCal.beans.calculator.MileageCalSector;
import com.asiamiles.ixClsMileageCal.beans.calculator.ParamValidate;
import com.asiamiles.ixClsMileageCal.beans.common.Log;
import com.asiamiles.ixClsMileageCal.beans.common.McPropBean;
import com.asiamiles.ixClsMileageCal.beans.mileageCal.MileageCalView;
import com.cathaypacific.utility.log.CorrelationInfo;
import com.cathaypacific.utility.log.LogUtil;
import com.cathaypacific.utility.log.ThreadLocalUtil;
import com.cathaypacific.utility.log.UUID;

public class MileageCalCtrl extends HttpServlet {

	private final String SPEND = MileageCalConstants.CALCULATOR_IND_SPEND;
	private final String EARN = MileageCalConstants.CALCULATOR_IND_EARN;

	private final String PARAM_AWARDCLASS = MileageCalConstants.REQUEST_PARAMETER_AWARDCLASS_CODE;
	private final String PARAM_LANGUAGE = MileageCalConstants.REQUEST_PARAMETER_LANGUAGE;
	private final String PARAM_CALC_IND = MileageCalConstants.REQUEST_PARAMETER_CALCULATOR_IND;
	private final String PARAM_CALC_ACT = MileageCalConstants.REQUEST_PARAMETER_CALCULATOR_ACTION;
	private final String PARAM_SUBMIT_TYPE = MileageCalConstants.REQUEST_PARAMETER_SUBMIT_TYPE;

	private final String VALUE_CALC_ACT_OW = MileageCalConstants.CALCULATOR_ACTION_ONEWAY;
	private final String VALUE_CALC_ACT_RT = MileageCalConstants.CALCULATOR_ACTION_ROUNDTRIP;

	private final String VALUE_SUBMIT_CALC = MileageCalConstants.SUBMIT_TYPE_CALCULATE;
	private final String VALUE_SUBMIT_FORM = MileageCalConstants.SUBMIT_TYPE_FORM;
	private final String VALUE_SUBMIT_ADDSEC = MileageCalConstants.SUBMIT_TYPE_ADDSECTOR;
	private final String VALUE_SUBMIT_CALCIT = MileageCalConstants.SUBMIT_TYPE_CALCULATE_ALT_ITIN;

	private final String OBJ_BASIC_PARAM = MileageCalConstants.REQUESTION_OBJ_BASIC_PARAM;
	private final String OBJ_MILEAGE_RTN = MileageCalConstants.REQUESTION_OBJ_MILEAGE_RTN;

	private final String JSP_EARN_FORM = "/airearncalculator/earnForm.jsp";
	private final String JSP_EARN_OUTPUT = "/airearncalculator/earnOutput.jsp";
	private final String JSP_EARN_ALT_OUTPUT = "/airearncalculator/earnOutputAltItin.jsp";
	private final String JSP_SPEND_FORM = "/flightawardfinder/spendForm.jsp";
	private final String JSP_SPEND_OUTPUT = "/flightawardfinder/spendOutput.jsp";
	private final String JSP_SPEND_ALT_OUTPUT = "/flightawardfinder/spendOutputAltItin.jsp";
	private final String JSP_ERROR = "/error.jsp";

	private final String SITE_AM = "AM";
	private final String LANG_EN = "en";

	public void init(ServletConfig config) throws ServletException {
		super.init(config);
	}

	public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doPost(request, response);
	}

	private void processRequestFrom(HttpServletRequest request, HttpServletResponse response) throws ServletException,
			IOException {
		//cpppep ADD for AML31473 20140821 START  update am logger
		String memberId = request.getParameter(MileageCalConstants.REQUEST_PARAMETER_MEMBERID);
		ThreadLocalUtil.setCorrelationInfo(
			LogUtil.createCorrelationInfo(request.getSession().getId(), ("-1".equals(memberId) || null == memberId)?"":memberId, "")
		);
		//cpppep ADD for AML31473 20140821 END
		
		Log.writeDebugLog("MileageCalCtrl->processRequestFrom->ENTRY");
		MileageCalParam mcParam;
		MileageCalReturn mcReturn = null;
		ParamValidate mcParmObj;

		ParamValidate mileageCalParamObj;
		MileageCalParam mileageCalParam;
		MileageCalReturn mileageCalReturn = null;

		String language = LANG_EN;
		if ((language = request.getParameter(PARAM_LANGUAGE)) == null || language.equals("")) {
			language = LANG_EN;
		}

		String calculatorInd = request.getParameter(PARAM_CALC_IND);
		if (calculatorInd == null || calculatorInd.equals("")) {
			calculatorInd = SPEND;
		}

		String calculatorAction = request.getParameter(PARAM_CALC_ACT);
		if (calculatorAction == null || calculatorAction.equals("")) {
			if (calculatorInd.equals(EARN)) {
				calculatorAction = VALUE_CALC_ACT_RT;
			} else
				calculatorAction = VALUE_CALC_ACT_OW;
		}

		String site = SITE_AM;

		String submitType = "";
		if ((submitType = request.getParameter(PARAM_SUBMIT_TYPE)) == null || submitType.equals("")) {
			// error
			submitType = VALUE_SUBMIT_FORM;
		}

		MileageCalBasicParam basicParam = new MileageCalBasicParam();
		basicParam.calculatorAction = calculatorAction;
		basicParam.calculatorInd = calculatorInd;
		basicParam.language = language;
		basicParam.awardClass = request.getParameter(PARAM_AWARDCLASS);
		// basicParam.memberID = memberID;
		basicParam.site = site;
		basicParam.submitType = submitType;
		request.setAttribute(OBJ_BASIC_PARAM, basicParam);

		// Non-calculation requests: POPUP, ADDSECTOR and FORM
		if (site.equals(SITE_AM) == true) { // to be extended to support
			// upcoming MPO site revamp
			if (submitType.equals(VALUE_SUBMIT_CALC) != true && submitType.equals(VALUE_SUBMIT_CALCIT) != true) {
				if (submitType.equals(VALUE_SUBMIT_FORM) || submitType.equals(VALUE_SUBMIT_ADDSEC)) {
					if (calculatorInd.equals(EARN)) {
						gotoPage(JSP_EARN_FORM, request, response);
					} else {
						gotoPage(JSP_SPEND_FORM, request, response);
					}
					return;
				}
				// else if(submitType.equals("POPUP")) {
				// MileageCalPopUpHandler popupHandler = new
				// MileageCalPopUpHandler(basicParam, request);
				// popupHandler.preparePopup(request, response);
				// //gotoPage("/mc_popup.jsp", request, response);
				// return;
				// }
				// else if(submitType.equals(VALUE_SUBMIT_ADDSEC)) {
				// if(calculatorInd.equals(EARN)) {
				// gotoPage(JSP_EARN_FORM, request, response);
				// } else {
				// gotoPage(JSP_SPEND_FORM, request, response);
				// }
				// return;
				// }
			} else {
				// Calculation request
				if (basicParam.firstLvlParamValidate(request, calculatorInd) == false) {
					if (calculatorInd.equals(EARN)) {
						gotoPage(JSP_EARN_FORM, request, response);
					} else {
						gotoPage(JSP_SPEND_FORM, request, response);
					}
					return;
				}
				if (calculatorInd.equals(EARN)) {
					mileageCalParamObj = new ParamValidate(request, EARN);
				} else {
					Log.writeDebugLog("MileageCalCtrl->processRequestFrom->validate SPEND");
					mileageCalParamObj = new ParamValidate(request, SPEND);
				}
				mileageCalParam = mileageCalParamObj.getMileageCalParam();

				/*
				 * get error code from mcParamObj, if there is error in
				 * mcParamObj, construct either MileageEarnReturn or
				 * MileageRedemReturn by passing error code and mcParamObj. . .
				 */
				if (mileageCalParamObj.getStatus() == ParamValidate.EMPTY_CODE) {
					mileageCalReturn = new MileageCalReturn(null, MileageCalReturn.EMPTY_CODE, null, null);
					mileageCalReturn.setMileageCalParam(mileageCalParam);
				} else if (mileageCalParamObj.getStatus() == ParamValidate.ERROR_CODE) {
					mileageCalReturn = new MileageCalReturn(null, MileageCalReturn.ERROR_CODE, mileageCalParamObj
							.getErrorList(), null);
					mileageCalReturn.setMileageCalParam(mileageCalParam);
				} else if (mileageCalParamObj.getStatus() == ParamValidate.SUCCESS_CODE) {
					/*
					 * if there is no error, get the MileageCalParam and pass it
					 * to do the calculation. It should return either
					 * MileageEarnReturn or MileageRedemReturn. .
					 */
					if (calculatorInd.equals(EARN)) {
						MileageCalEarn mce = new MileageCalEarn();
						mileageCalReturn = (MileageCalReturn) mce.earnMileage(mileageCalParam);

					} else { // to be complete for redeem
						// CPPYOW tag
						Log.writeDebugLog("MileageCalCtrl->processRequestFrom->validate SUCCESS");
						ArrayList v = new ArrayList();
						MileageCalRedem mcr = new MileageCalRedem();
						mileageCalReturn = (MileageCalReturn) mcr.redemMileage(mileageCalParam);
						// CPPYOW add for CPL4338 20120815 start
						mileageCalReturn.setToIBE(isOneWorldAirline(mileageCalParam));
						mileageCalReturn.setCompanionTicket(isOWCompanion(request, mileageCalParam));
						// CPPYOW add for CPL4338 20120815 end
					}
					mileageCalReturn.setMileageCalParam(mileageCalParam);
				}
				Log.writeDebugLog("mileageCalReturn isToIBE: " + mileageCalReturn.isToIBE());
				request.setAttribute(OBJ_MILEAGE_RTN, mileageCalReturn);
				request.setAttribute(PARAM_LANGUAGE, language);
				request.setAttribute(PARAM_CALC_ACT, calculatorAction);

				int status = mileageCalReturn.getReturn_status();

				if (calculatorInd.equals(EARN)) {
					if (status == MileageCalReturn.BASIC_ALT_ITIN_SUCCESS_CODE
							|| status == MileageCalReturn.BASIC_ITIN_SUCCESS_CODE
							|| status == MileageCalReturn.ALT_ITIN_SUCCESS_CODE) {
						if (submitType.equals(VALUE_SUBMIT_CALC)) {
							gotoPage(JSP_EARN_OUTPUT, request, response);
						} else {
							gotoPage(JSP_EARN_ALT_OUTPUT, request, response);
						}
					} else {
						basicParam.calculationErrors(mileageCalReturn);
						gotoPage(JSP_EARN_FORM, request, response);
					}
				} else if (calculatorInd.equals(SPEND)) {
					if (status == MileageCalReturn.BASIC_ALT_ITIN_SUCCESS_CODE
							|| status == MileageCalReturn.BASIC_ITIN_SUCCESS_CODE
							|| status == MileageCalReturn.ALT_ITIN_SUCCESS_CODE) {
						if (submitType.equals(VALUE_SUBMIT_CALC)) {
							gotoPage(JSP_SPEND_OUTPUT, request, response);
						} else {
							gotoPage(JSP_SPEND_ALT_OUTPUT, request, response);
						}
					} else {
						basicParam.calculationErrors(mileageCalReturn);
						gotoPage(JSP_SPEND_FORM, request, response);
					}
				} else {
					gotoPage(JSP_ERROR, request, response);
				}
				return;
			}
		}

	}

	public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		MileageCalParam mcParam;
		MileageCalReturn mcReturn = null;
		ParamValidate mcParmObj;

		try {
			String language = LANG_EN;
			if ((language = request.getParameter(PARAM_LANGUAGE)) == null || language.equals("")) {
				language = LANG_EN;
			}

			String calculatorInd = "";
			if ((calculatorInd = request.getParameter(PARAM_CALC_IND)) == null || calculatorInd.equals("")) {
				calculatorInd = SPEND;
				// gotoPage("error.jsp", request, response);
				// return;
			}

			String calculatorAction = request.getParameter(PARAM_CALC_ACT);
			if (calculatorAction == null || calculatorAction.equals("")) {
				if (calculatorInd.equals(EARN)) {
					calculatorAction = VALUE_CALC_ACT_OW;
				} else
					calculatorAction = VALUE_CALC_ACT_OW;
			}

			String site = "";
			if ((site = request.getParameter("site")) == null || site.equals("")) {
				site = SITE_AM;
			}

			if (site.equals(SITE_AM) == true) { // to be extended to support
				// upcoming MPO site revamp
				processRequestFrom(request, response);
				return;
			}

			if (calculatorInd.equals(EARN)) {
				mcParmObj = new ParamValidate(request, EARN);
			} else {
				mcParmObj = new ParamValidate(request, SPEND);
			}
			mcParam = mcParmObj.getMileageCalParam();

			/*
			 * get error code from mcParamObj, if there is error in mcParamObj,
			 * construct either MileageEarnReturn or MileageRedemReturn by
			 * passing error code and mcParamObj. . .
			 */
			if (mcParmObj.getStatus() == ParamValidate.EMPTY_CODE) {
				mcReturn = new MileageCalReturn(null, MileageCalReturn.EMPTY_CODE, null);
				mcReturn.setMileageCalParam(mcParam);
			} else if (mcParmObj.getStatus() == ParamValidate.ERROR_CODE) {
				mcReturn = new MileageCalReturn(null, MileageCalReturn.ERROR_CODE, mcParmObj.getErrorList());
				mcReturn.setMileageCalParam(mcParam);
			} else if (mcParmObj.getStatus() == ParamValidate.SUCCESS_CODE) {
				/*
				 * if there is no error, get the MileageCalParam and pass it to
				 * do the calculation. It should return either MileageEarnReturn
				 * or MileageRedemReturn. .
				 */

				if (calculatorInd.equals(EARN)) {
					MileageCalEarn mce = new MileageCalEarn();
					mcReturn = (MileageCalReturn) mce.earnMileage(mcParam);

				} else {
					ArrayList v = new ArrayList();
					MileageCalRedem mcr = new MileageCalRedem();
					mcReturn = (MileageCalReturn) mcr.redemMileage(mcParam);
				}
				mcReturn.setMileageCalParam(mcParam);
			}

			request.setAttribute(OBJ_MILEAGE_RTN, mcReturn);
			request.setAttribute(PARAM_LANGUAGE, language);
			// request.setAttribute(PARAM_CALC_IND, calculatorInd);
			request.setAttribute(PARAM_CALC_ACT, calculatorAction);

			int status = mcReturn.getReturn_status();

		} catch (Exception e) {
			Log.writeErrorLog(this.getClass().getName() + "->Exception: doPost: " + e.getMessage());
		}
	}

	private void gotoPage(String address, HttpServletRequest request, HttpServletResponse response) {
		try {
			Log.writeInfoLog(this.getClass().getName() + "->gotoPage: dispatching to '" + address + "'");
			RequestDispatcher rqd = getServletContext().getRequestDispatcher(address);
			rqd.forward(request, response);
		} catch (ServletException e) {
			Log.writeErrorLog(this.getClass().getName() + "->Exception: gotoPage: " + e.getMessage());
		} catch (IOException e) {
			Log.writeErrorLog(this.getClass().getName() + "->Exception: gotoPage: " + e.getMessage());
		}
	}

	// CPPYOW add for CPL4338 20120815 start
	private boolean isOWCompanion(HttpServletRequest request, MileageCalParam mileageCalParam) {
		Log.writeDebugLog("MileageCalCtrl->isOWCompanion->ENTRY");
		boolean flag = false;
		ArrayList sectorParameters = mileageCalParam.getSectorParameters();
		String owCarriers = McPropBean.getProperty("red.oneworld.others.carrier");
		Log.writeDebugLog("awardType=" + request.getParameter("awardType"));
		switch (sectorParameters.size()) {
		case 1:
			MileageCalSector sector = (MileageCalSector) (sectorParameters.get(0));
			if (owCarriers.indexOf(sector.getAirline()) != -1 && "companion".equals(request.getParameter("awardType"))) {
				flag = true;
			}
			break;
		case 2:
			String airline1 = ((MileageCalSector) (sectorParameters.get(0))).getAirline();
			String airline2 = ((MileageCalSector) (sectorParameters.get(1))).getAirline();
			if (owCarriers.indexOf(airline1) != -1 && owCarriers.indexOf(airline2) != -1 && airline1.equals(airline2)
					&& "companion".equals(request.getParameter("awardType"))) {
				flag = true;
			}
			break;
		case 3:
		case 4:
			for (int i = 0; i < sectorParameters.size(); i++) {
				String airline = ((MileageCalSector) (sectorParameters.get(i))).getAirline();
				if ((airline.equals("AAL") && "companion".equals(request.getParameter("awardType")))
						|| (airline.equals("BAW") && "companion".equals(request.getParameter("awardType")))
						|| (airline.equals("QFA") && "companion".equals(request.getParameter("awardType")))) {
					flag = true;
				}
				break;
			}
		}
		Log.writeDebugLog("isOWCompanion " + flag);
		Log.writeDebugLog("MileageCalCtrl->isOWCompanion->EXIT");
		return flag;

	}

	private boolean isOneWorldAirline(MileageCalParam mileageCalParam) {
		Log.writeDebugLog("MileageCalCtrl->isOneWorldAirline->ENTRY");
		boolean flag = false;
		ArrayList sectorParameters = mileageCalParam.getSectorParameters();
		String oneWorldCarriers = McPropBean.getProperty("red.oneworld.all.carrier");
		String owCarriers = McPropBean.getProperty("red.oneworld.others.carrier");
		Log.writeDebugLog("oneWorldCarriers: " + oneWorldCarriers);
		Log.writeDebugLog("OtherCarriers: " + owCarriers);
		Log.writeDebugLog("MileageCalCtrl->isOneWorldAirline: sectorParameters size:" + sectorParameters.size());
		switch (sectorParameters.size()) {
		case 1:
			MileageCalSector sector = (MileageCalSector) (sectorParameters.get(0));
			flag = oneWorldCarriers.indexOf(sector.getAirline()) != -1 ? true : false;
			break;
		case 2:
			String airline1 = ((MileageCalSector) (sectorParameters.get(0))).getAirline();
			String airline2 = ((MileageCalSector) (sectorParameters.get(1))).getAirline();
			if (oneWorldCarriers.indexOf(airline1) != -1 && oneWorldCarriers.indexOf(airline2) != -1) {
				flag = true;
			}
			break;
		case 3:
		case 4:
			int cxorkaCount = 0;
			int owCount = 0;
			int othersCount = 0;
			for (int i = 0; i < sectorParameters.size(); i++) {
				String airline = ((MileageCalSector) (sectorParameters.get(i))).getAirline();
				if (airline.equals("CPA") || airline.equals("HDA")) {
					cxorkaCount++;
				}
				if (owCarriers.indexOf(airline) != -1) {
					owCount++;
				}
				if (oneWorldCarriers.indexOf(airline) == -1) {
					othersCount++;
				}
			}
			if ((owCount >= 2 && cxorkaCount >= 1 && othersCount == 0)
					|| (owCount == 0 && cxorkaCount >= 3 && othersCount == 0)) {
				flag = true;
			}
			break;
		}
		Log.writeDebugLog("MileageCalCtrl->isOneWorldAirline: " + flag);
		Log.writeDebugLog("MileageCalCtrl->isOneWorldAirline->EXIT");
		return flag;
	}
	// CPPYOW add for CPL4338 20120815 end

}