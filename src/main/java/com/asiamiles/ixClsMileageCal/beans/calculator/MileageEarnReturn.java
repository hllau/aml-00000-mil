package com.asiamiles.ixClsMileageCal.beans.calculator;

import java.util.ArrayList;

public class MileageEarnReturn extends MileageCalReturn {
	private boolean returnFlightFlag;
	private int totalAMMileage;
	private int totalAMReturnMileage;
	private int totalClubMileage;
	private int totalClubReturnMileage;

	/* Constructor for Mileage Earning
		returnFlightFlag: indicator for any direct flight for return trip is found. If true, return flight information is the
						last element in ArrayList mileage.
		mileage: a ArrayList contains maximum FIVE MileageCalSectorMiles. Each MileageCalSectorMiles contains elements of each
					sector record.
					Elements are: origin, destination, airline, class, am_mileage, club_mileage
		status:	indicator of error occurred in validation or searching, 1 for no error and search success, -1 for error exists
					0 for no input parameters
		errorCode: a ArrayList contains a list of error code.

		usage: if there is no error and without return flight, use " MileageEarnReturn(false, ArrayList_object, MileageEarnReturn.SUCCESS_CODE, null) "
				if there is no error and with return flight, use " MileageEarnReturn(true, ArrayList_object, MileageEarnReturn, null) "
				if there is any error, use " MileageEarnReturn(false, null, MileageEarnReturn.ERROR_CODE, ArrayList_object) "
	*/
	public MileageEarnReturn(boolean returnFlightFlag, ArrayList mileage, int status, ArrayList errorCode, ArrayList altCalReturn) {
		super(mileage, status, errorCode, altCalReturn);
		this.returnFlightFlag = returnFlightFlag;
	}

	public void setReturnFlightFlag(boolean returnFlightFlag) {
		this.returnFlightFlag = returnFlightFlag;
	}

	public void setMileage(MileageCalSectorMiles mileage) {
		this.mileage.add(mileage);
	}

	public boolean getReturnFlightFlag() { return returnFlightFlag; }

	public MileageCalSectorMiles getMileage(int i) { return (MileageCalSectorMiles)(getMileage().get(i)); }

	public void setTotalMiles() {
		totalAMMileage = 0;
		totalClubMileage = 0;
		totalAMReturnMileage = 0;
		totalClubReturnMileage = 0;

		for (int i = 0; i < getMileage().size(); i++) {
			MileageCalSectorMiles mcsm = getMileage(i);
			if (!this.returnFlightFlag) {
				totalAMMileage = totalAMMileage + mcsm.getAm_mileage();
				totalClubMileage = totalClubMileage + mcsm.getClub_mileage();
			} else {
				int k = i;
				if (++k == this.getMileage().size()) {
					totalAMReturnMileage = totalAMReturnMileage + mcsm.getAm_mileage();
					totalClubReturnMileage = totalClubReturnMileage + mcsm.getClub_mileage();
				} else {
					totalAMMileage = totalAMMileage + mcsm.getAm_mileage();
					totalClubMileage = totalClubMileage + mcsm.getClub_mileage();
					totalAMReturnMileage = totalAMReturnMileage + mcsm.getAm_mileage();
					totalClubReturnMileage = totalClubReturnMileage + mcsm.getClub_mileage();
				}
			}
		}
	}

	public String toXMLString() {
		String xmlMileage = new String();
		String xmlReturnMileage = new String();
		if (this.return_status == MileageEarnReturn.BASIC_ALT_ITIN_SUCCESS_CODE
			|| this.return_status == MileageEarnReturn.BASIC_ITIN_SUCCESS_CODE) {
			for (int i = 0; i < this.getMileage().size(); i++) {
				MileageCalSectorMiles mcSectorMiles = this.getMileage(i);
				String origin = "<origin>" + mcSectorMiles.getOrigin() + "</origin>";
				String destination = "<destination>" + mcSectorMiles.getDestination() + "</destination>";
				String airline = "<airline>" + mcSectorMiles.getAirline() + "</airline>";
				String classType = "<classType>" + mcSectorMiles.getClassType() + "</classType>";
				String amMileage = "<amMileage>" + String.valueOf(mcSectorMiles.getAm_mileage()) + "</amMileage>";
				if (!this.returnFlightFlag) {
					xmlMileage = xmlMileage + "";
				} else {
					int k = i;
					if (++k == this.getMileage().size()) {
						xmlReturnMileage = xmlReturnMileage + "";
					} else {
						xmlMileage = xmlMileage + "";
						xmlReturnMileage = xmlReturnMileage + "";
					}
				}
			}
		} else {
			return "<error>Invalid Return</error>";
		}
		return xmlMileage + xmlReturnMileage;
	}
}
