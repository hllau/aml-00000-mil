package com.asiamiles.ixClsMileageCal.beans.calculator;

import java.util.Properties;
import java.util.ArrayList;

public class MileageCalParam {

	protected int noOfSectors;
	protected String memberID;
	protected String awardType;
	protected ArrayList sectorParameters;
//amv3 added
	protected boolean roundTrip;

	/* Constructor for Mileage Earning
		noOfSectors: number of sector
		memberID: member ID, "-1" if no memeber ID
		sectorParameters: a ArrayList contains maximum FOUR MileageCalSector. Each MileageCalSector contains elements of each sector
							Elements are: origin, destination, airline, class

		usage: use " MileageCalParam(no_of_sector, ArrayList_object) "
	*/
	public MileageCalParam(int noOfSectors, ArrayList sectorParameters, String memberID, boolean roundTrip) {
		this.sectorParameters = sectorParameters;
		this.noOfSectors = noOfSectors;
		this.memberID = memberID;
		this.roundTrip = roundTrip;
	}

	/* Constructor for Mileage Redemption
		noOfSectors: number of sector
		awardType: String array of award type, the first one in the array is chosen by user
		memberID: member ID, "-1" if no memeber ID
		sectorParameters: a ArrayList contains maximum FIVE MileageCalSector. Each MileageCalSector contains elements of each sector
							Elements are: origin, destination, airline

		usage: if there is member id, use " MileageCalParam(no_of_sector, award_type_array, ArrayList_object, member_id) "
				if there is no member id, use " MileageCalParam(no_of_sector, award_type_array, ArrayList_object, "") "
	*/
	public MileageCalParam(int noOfSectors, String awardType, ArrayList sectorParameters, String memberID) {
		this.sectorParameters = sectorParameters;
		this.noOfSectors = noOfSectors;
		this.awardType = awardType;
		this.memberID = memberID;
	}

	public void setNoOfSectors(int noOfSectors) {
		this.noOfSectors = noOfSectors;
	}

	public void setAwardType(String awardType) {
		this.awardType = awardType;
	}

	public void setMemberID(String memberID) {
		this.memberID = memberID;
	}

	public void setSectorParameters(ArrayList sectorParameters) {
		this.sectorParameters = sectorParameters;
	}

	public void setSectorParameters(Properties sectorParameters) {
		this.sectorParameters.add(sectorParameters);
	}

	public int getNoOfSectors() {
		return this.noOfSectors;
	}

	public String getAwardType() {
		return this.awardType;
	}

	public String getMemberID() {
		return this.memberID;
	}

	public ArrayList getSectorParameters() {
		return this.sectorParameters;
	}

	public MileageCalSector getSectorParameters(int i) {
		return (MileageCalSector)(this.sectorParameters.get(i));
	}
}
