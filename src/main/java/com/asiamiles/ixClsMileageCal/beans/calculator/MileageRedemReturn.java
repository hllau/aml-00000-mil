package com.asiamiles.ixClsMileageCal.beans.calculator;

import java.util.ArrayList;
//added for amv3

public class MileageRedemReturn extends MileageCalReturn {
	private boolean topupFlag;
	private ArrayList sector;			//object is of type MileageCalSector

	/* Constructor for Mileage Redemption
		mileage: a ArrayList contains maximum FOUR MileageCalAward. Each MileageCalAward contains elements of each award type mileage required information
					Elements are: award_type, topup_mileage, topup_money (topup_mileage and topup_money are -1 if top-up required
					exceed 30% of mileage or there is no member ID in input parameters)
					The first MileageCalAward is the award type that user chooses, the rest of MileageCalAward are the
					related award type of the first one.
		status:	indicator of error occurred in validation or searching, 1 for no error and search success, -1 for error exists
					0 for no input parameters
		errorCode: a ArrayList contains a list of error code.

		usage: if there is no error , use " MileageRedemReturn(ArrayList_object, MileageRedemReturn.SUCCESS_CODE, null) "
				if there is any error, use " MileageRedemReturn(null, MileageRedemReturn.ERROR_CODE, ArrayList_object) "
	*/
// changed for amv3
	public MileageRedemReturn(ArrayList mileage, int status, ArrayList errorCode, ArrayList altCalReturn, ArrayList sector) {
		super(mileage, status, errorCode, altCalReturn);							
		this.sector = sector;		
	}

	public void setTopupFlag() {
		topupFlag = false;
		for (int i = 0; i < getMileage().size(); i++) {
			MileageCalAward mca = getMileage(i);
			if (mca.getTopup_mileage() > 0) {
				topupFlag = true;
			}
		}
	}

	public void setMileage(MileageCalAward mileage) {
		this.mileage.add(mileage);
	}

	public boolean getTopupFlag() { return topupFlag; }

// added for amv3
	public ArrayList getSector() {
		return this.sector;											
	}														

	public MileageCalAward getMileage(int i) { return (MileageCalAward)(getMileage().get(i)); }

	public String toXMLString() {
		String xmlMileage = new String();
		String xmlReturnMileage = new String();
		String xmlSector = new String();

//		if (this.return_status == MileageRedemReturn.BASIC_ALT_ITIN_SUCCESS_CODE 
//			|| this.return_status == MileageRedemReturn.BASIC_ITIN_SUCCESS_CODE) {

		if (this.return_status == MileageRedemReturn.SUCCESS_CODE) {
			MileageCalParam mcParam = this.getMileageCalParam();
			for (int i = 0; i < mcParam.getSectorParameters().size(); i++) {
				MileageCalSector mcSector = mcParam.getSectorParameters(i);
				String origin = "<origin>" + mcSector.getOrigin() + "</origin>";
				String destination = "<destination>" + mcSector.getDestination() + "</destination>";
				String airline = "<airline>" + mcSector.getAirline() + "</airline>";
				xmlSector = xmlSector + "";
			}

			for (int i = 0; i < this.getMileage().size(); i++) {
				MileageCalAward mcAwardMiles = this.getMileage(i);
				String awardType = "<awardType>" + mcAwardMiles.getAward_type() + "</awardType>";
				String amMileage = "<amMileage>" + mcAwardMiles.getAm_Mileage() + "</amMileage>";
				String topupMileage = "<topupMileage>" + mcAwardMiles.getTopup_mileage() + "</topupMileage>";
				String topupMoney = "<topupMoney>" + mcAwardMiles.getTopup_money() + "</topupMoney>";
				if (i == 0) {
					xmlMileage = xmlMileage + "";
					xmlReturnMileage = xmlReturnMileage + "";
				} else {
					xmlReturnMileage = xmlReturnMileage + "";
				}
			}
		} else {
			return "<error>Invalid Return</error>";
		}
		return xmlMileage + xmlReturnMileage;
	}
}
