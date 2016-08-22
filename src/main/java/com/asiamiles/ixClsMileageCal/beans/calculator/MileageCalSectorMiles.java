package com.asiamiles.ixClsMileageCal.beans.calculator;


public class MileageCalSectorMiles extends MileageCalSector {
	private int am_mileage;
	private int club_mileage;

	/* Constructor for Mileage Redemption
	
		usage: if there is member id, use " MileageCalParam(no_of_sector, award_type_array, ArrayList_object, member_id) "
				if there is no member id, use " MileageCalParam(no_of_sector, award_type_array, ArrayList_object, "") "
	*/
	public MileageCalSectorMiles(
		String origin,
		String destination,
		String airline,
		String classType,
		String codeShareOrJointVenture,
		int am_mileage,
		int club_mileage) {
		super(origin, destination, airline, classType,codeShareOrJointVenture);
		this.am_mileage = am_mileage;
		this.club_mileage = club_mileage;
	}

	public MileageCalSectorMiles(
		MileageCalSector mcs,
		int am_mileage,
		int club_mileage) {
		super(
			mcs.getOrigin(),
			mcs.getDestination(),
			mcs.getAirline(),
			mcs.getClassType(),
			mcs.getCodeShareOrJointVenture());
		this.am_mileage = am_mileage;
		this.club_mileage = club_mileage;
	}

	public int getAm_mileage() {
		return this.am_mileage;
	}

	public int getClub_mileage() {
		return this.club_mileage;
	}
}
