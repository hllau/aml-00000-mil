package com.asiamiles.ixClsMileageCal.beans.calculator;


public class MileageCalSector {
	protected String origin;
	protected String destination;
	protected String airline;
	protected String classType;
	protected String codeShareOrJointVenture;
	// allowed values are "CS", "JV" or ""

	/* Constructor for Mileage Redemption
	
		usage: if there is member id, use " MileageCalParam(no_of_sector, award_type_array, ArrayList_object, member_id) "
				if there is no member id, use " MileageCalParam(no_of_sector, award_type_array, ArrayList_object, "") "
	*/
	public MileageCalSector(
		String origin,
		String destination,
		String airline,
		String classType,
		String codeShareOrJointVenture) {
		this.origin = origin;
		this.destination = destination;
		this.airline = airline;
		this.classType = classType;
		this.codeShareOrJointVenture = codeShareOrJointVenture;
	}

	public MileageCalSector(
		String origin,
		String destination,
		String airline,
		String codeShareOrJointVenture) {
		this.origin = origin;
		this.destination = destination;
		this.airline = airline;
		this.codeShareOrJointVenture = codeShareOrJointVenture;
	}

	public String getOrigin() {
		return this.origin;
	}

	public String getDestination() {
		return this.destination;
	}

	public String getAirline() {
		return this.airline;
	}

	public String getClassType() {
		return this.classType;
	}

	public String getCodeShareOrJointVenture() {
		return this.codeShareOrJointVenture;
	}

}
