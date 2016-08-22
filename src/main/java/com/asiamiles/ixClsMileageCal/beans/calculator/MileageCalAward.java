package com.asiamiles.ixClsMileageCal.beans.calculator;


public class MileageCalAward {
	protected String award_type;
	protected String award_program;
	protected String zone;

	protected int am_mileage;
	protected int future_am_mileage;
	protected int topup_mileage;
	protected int topup_money;
//	added for amv3
	protected String origin;
	protected String destination;
	protected String airline;
	protected String codeShareOrJointVenture;
	protected String ticketOrUpgrade;
	protected String classType;
	

	/* Constructor for Mileage Redemption

		usage: if there is member id, use " MileageCalParam(no_of_sector, award_type_array, ArrayList_object, member_id) "
				if there is no member id, use " MileageCalParam(no_of_sector, award_type_array, ArrayList_object, "") "
	*/
	public MileageCalAward(String award_type, int am_mileage, int topup_mileage, int topup_money) {
		this.award_type = award_type;
		this.topup_mileage = topup_mileage;
		this.am_mileage = am_mileage;
		this.topup_money = topup_money;
		this.award_program = "";
		this.zone = "";
		this.future_am_mileage = 0;
	}
	
	public MileageCalAward(String award_type, int am_mileage, int topup_mileage, int topup_money, String award_program, String zone, int future_am_mileage) {
		this.award_type = award_type;
		this.topup_mileage = topup_mileage;
		this.am_mileage = am_mileage;
		this.topup_money = topup_money;
		this.award_program = award_program;
		this.zone = zone;
		this.future_am_mileage = future_am_mileage;
	}

	public String getAward_type() { return this.award_type; }

	public int getAm_Mileage() { return this.am_mileage; }

	public int getTopup_mileage() { return this.topup_mileage; }

	public int getTopup_money() { return this.topup_money; }

	/**
	 * @return Returns the award_program.
	 */
	public String getAward_program() {
		return award_program;
	}
	/**
	 * @return Returns the zone.
	 */
	public String getZone() {
		return zone;
	}
	/**
	 * @return Returns the future_am_mileage.
	 */
	public int getFuture_am_mileage() {
		return future_am_mileage;
	}
}
