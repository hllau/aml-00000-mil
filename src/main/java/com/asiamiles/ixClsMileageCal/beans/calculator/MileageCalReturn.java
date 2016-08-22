package com.asiamiles.ixClsMileageCal.beans.calculator;

import java.util.*;

public class MileageCalReturn {

	public static final int EMPTY_CODE = 0;
	public static final int SUCCESS_CODE = 1;
	public static final int ERROR_CODE = -1;
	// added for amv3
	public static final int BASIC_ITIN_SUCCESS_CODE = 1;
	public static final int BASIC_ALT_ITIN_SUCCESS_CODE = 2;
	public static final int ALT_ITIN_SUCCESS_CODE = 3;

	protected int return_status; // Validation return_status
	protected ArrayList mileage;
	protected ArrayList errorCode;
	protected MileageCalParam mcParam;
	// added for amv3
	protected ArrayList altCalReturn;
	// CPPYOW add for CPL4338 20120815 start
	protected boolean isToIBE;

	protected boolean isCompanionTicket;

	// CPPYOW add for CPL4338 20120815 end

	/* Default constructor */
	public MileageCalReturn() {
		return_status = 0;
		mileage = new ArrayList();
		errorCode = new ArrayList();
		altCalReturn = new ArrayList();
	}

	/*
	 * Constructor for Mileage Redemption Please refer to the Constructor of
	 * MileageRedemReturn
	 */
	public MileageCalReturn(ArrayList mileage, int return_status, ArrayList errorCode, ArrayList altCalReturn) { // changed
																										// for
																										// amv3
		this.mileage = mileage;
		this.return_status = return_status;
		this.errorCode = errorCode;
		this.altCalReturn = altCalReturn;
	}

	public void setReturn_status(int return_status) {
		this.return_status = return_status;
	}

	public void setMileage(ArrayList mileage) {
		this.mileage = mileage;
	}

	public void setErrorCode(ArrayList errorCode) {
		this.errorCode = errorCode;
	}

	public void setErrorCode(String errorCode) {
		this.errorCode.add(errorCode);
	}

	public int getReturn_status() {
		return this.return_status;
	}

	public ArrayList getMileage() {
		return this.mileage;
	}

	public ArrayList getErrorCode() {
		return this.errorCode;
	}

	public String getErrorCode(int i) {
		return (String) this.errorCode.get(i);
	}

	public void setMileageCalParam(MileageCalParam mcParam) {
		this.mcParam = mcParam;
	}

	public MileageCalParam getMileageCalParam() {
		return this.mcParam;
	}

	// added for amv3
	public ArrayList getAltCalReturn() {
		return this.altCalReturn;
	}

	public MileageCalReturn(ArrayList mileage, int return_status, ArrayList errorCode) {
		this.mileage = mileage;
		this.return_status = return_status;
		this.errorCode = errorCode;
	}

	// CPPYOW add for CPL4338 20120815 start
	public boolean isToIBE() {
		return isToIBE;
	}

	public void setToIBE(boolean isToIBE) {
		this.isToIBE = isToIBE;
	}

	public boolean isCompanionTicket() {
		return isCompanionTicket;
	}

	public void setCompanionTicket(boolean isCompanionTicket) {
		this.isCompanionTicket = isCompanionTicket;
	}

	// CPPYOW add for CPL4338 20120815 end
}
