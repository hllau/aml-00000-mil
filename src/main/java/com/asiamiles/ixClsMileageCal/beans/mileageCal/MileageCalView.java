package com.asiamiles.ixClsMileageCal.beans.mileageCal;


public class MileageCalView 
{
	protected String transactionCode = "";

	protected String lang = "";
	protected String earnRedeemInd = "";	// E for earn; R for redeem
	protected String roundTripInd = "";		// RT for round trip; OW for one-way
	protected String carrier = "";			// 3-letter carrier code
	protected String cabinClass = "";		// Y for Econ; C for Biz; F for First
	protected String origin = "";			// IATA 3-letter airport code
	protected String destination = "";		// IATA 3-letter airport code

	protected String memberID = "-1";
	protected String viewName;


	public void setMemberID(String pMemberID) 
	{
		this.memberID = pMemberID;
	}

	public String getMemberID() 
	{
		this.memberID = "".equals(this.memberID) ? "-1" : this.memberID;
		return this.memberID;
	}

	public void setViewName(String pViewName) 
	{
		this.viewName = pViewName;
	}

	public String getViewName() 
	{
		return this.viewName;
	}
	
	/**
	 * Returns the lang.
	 * @return String
	 */
	public String getLang() {
		return this.lang;
	}

	/**
	 * Returns the earnRedeemInd.
	 * @return String
	 */
	public String getEarnRedeemInd() {
		return this.earnRedeemInd;
	}

	/**
	 * Returns the roundTripInd.
	 * @return String
	 */
	public String getRoundTripInd() {
		return this.roundTripInd;
	}

	/**
	 * Returns the carrier.
	 * @return String
	 */
	public String getCarrier() {
		return this.carrier;
	}

	/**
	 * Returns the transactionCode.
	 * @return String
	 */
	public String getTransactionCode() {
		return this.transactionCode;
	}

	/**
	 * Returns the origin.
	 * @return String
	 */
	public String getOrigin() {
		return this.origin;
	}

	/**
	 * Returns the cabinClass.
	 * @return String
	 */
	public String getCabinClass() {
		return this.cabinClass;
	}


	/**
	 * Returns the destination.
	 * @return String
	 */
	public String getDestination() {
		return this.destination;
	}


	
	/**
	 * Sets the lang.
	 * @param lang The lang to set
	 */
	public void setlang(String pLang) 
	{
		this.lang = pLang;
	}

	/**
	 * Sets the earnRedeemInd.
	 * @param earnRedeemInd The earnRedeemInd to set
	 */
	public void setEarnRedeemInd(String pInd) 
	{
		this.earnRedeemInd = pInd;
	}

	/**
	 * Sets the roundTripInd.
	 * @param pInd 
	 */
	public void setRoundTripInd(String pInd) 
	{
		this.roundTripInd = pInd;
	}

	/**
	 * Sets the carrier.
	 * @param pCarrier The carrier to set
	 */
	public void setCarrier(String pCarrier) 
	{
		this.carrier = pCarrier;
	}

	/**
	 * Sets the transactionCode.
	 * @param pTransactionCode The transactionCode to set
	 */
	public void setTransactionCode(String pTransactionCode) 
	{
		this.transactionCode = pTransactionCode;
	}

	/**
	 * Sets the cabinClass.
	 * @param pCabinClass The cabinClass to set
	 */
	public void setCabinClass(String pCabinClass) 
	{
		this.cabinClass = pCabinClass;
	}

	/**
	 * Sets the origin.
	 * @param pOrigin The origin to set
	 */
	public void setOrigin(String pOrigin) 
	{
		this.origin = pOrigin;
	}

	/**
	 * Sets the destination.
	 * @param pDestination The destination to set
	 */
	public void setDestination(String pDestination) 
	{
		this.destination = pDestination;
	}

}
