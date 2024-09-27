package jsfas.db.main.persistence.domain;

import java.io.Serializable;
import java.math.BigDecimal;

import javax.persistence.Column;
import javax.persistence.Embeddable;

@Embeddable
public class ElBcoAprvVDAOPK implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 4624524054553298575L;

	@Column(name = "USER_ID")
	private String userId;
	
	@Column(name = "USER_NAME")
	private String userName;
	
	@Column(name = "BCO_IND")
	private String bcoInd;

	@Column(name = "BUSINESS_UNIT")
	private String businessUnit;

	@Column(name = "BASIC_ID")
	private String basicId;

	@Column(name = "DEPT_PROJ_IND")
	private String deptProjInd;
	
	@Column(name = "FUND_CODE")
	private String fundCode;
	
	@Column(name = "ACCOUNT")
	private String account;

	@Column(name = "MAX_BR_AMT")
	private BigDecimal maxBrAmt;

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getBcoInd() {
		return bcoInd;
	}

	public void setBcoInd(String bcoInd) {
		this.bcoInd = bcoInd;
	}

	public String getBusinessUnit() {
		return businessUnit;
	}

	public void setBusinessUnit(String businessUnit) {
		this.businessUnit = businessUnit;
	}

	public String getBasicId() {
		return basicId;
	}

	public void setBasicId(String basicId) {
		this.basicId = basicId;
	}

	public String getDeptProjInd() {
		return deptProjInd;
	}

	public void setDeptProjInd(String deptProjInd) {
		this.deptProjInd = deptProjInd;
	}
	
	public String getFundCode() {
		return fundCode;
	}

	public void setFundCode(String fundCode) {
		this.fundCode = fundCode;
	}

	public String getAccount() {
		return account;
	}

	public void setAccount(String account) {
		this.account = account;
	}

	public BigDecimal getMaxBrAmt() {
		return maxBrAmt;
	}

	public void setMaxBrAmt(BigDecimal maxBrAmt) {
		this.maxBrAmt = maxBrAmt;
	}
}
