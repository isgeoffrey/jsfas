package jsfas.db.main.persistence.domain;

import java.io.Serializable;

import javax.persistence.Column;

public class FasStkPlanDtlDAOPK implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 4635064433952465002L;
	
	@Column(name = "STK_PLAN_ID")
	 private String stkPlanId;
	    
	@Column(name = "BUSINESS_UNIT")
	private String businessUnit;
	
	@Column(name = "ASSET_ID")
	private String assetId;

	public String getStkPlanId() {
		return stkPlanId;
	}

	public void setStkPlanId(String stkPlanId) {
		this.stkPlanId = stkPlanId;
	}

	public String getBusinessUnit() {
		return businessUnit;
	}

	public void setBusinessUnit(String businessUnit) {
		this.businessUnit = businessUnit;
	}

	public String getAssetId() {
		return assetId;
	}

	public void setAssetId(String assetId) {
		this.assetId = assetId;
	}
	
	private String getCompositeKey() {
	    return this.getStkPlanId() + this.getBusinessUnit() + this.getAssetId();
	}
	
	@Override
	public boolean equals(Object obj) {
	    if(obj instanceof FasStkPlanDtlDAOPK) {
	    	FasStkPlanDtlDAOPK fasStkPlanDtlDAOPK = (FasStkPlanDtlDAOPK)obj;
	        return this.getCompositeKey().equals(fasStkPlanDtlDAOPK.getCompositeKey());
	    }
	    return false;
	}
	
	@Override
	public int hashCode() {
	    return this.getCompositeKey().hashCode();
	}
}
