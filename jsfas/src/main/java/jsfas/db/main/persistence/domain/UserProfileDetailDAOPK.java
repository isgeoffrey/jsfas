package jsfas.db.main.persistence.domain;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

import com.fasterxml.jackson.annotation.JsonBackReference;

public class UserProfileDetailDAOPK implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 2794270561569471416L;

	@ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name="USER_NAM")
	private UserProfileHeaderDAO userProfileHeader;
	
	@Column(name="ACCESS_FUNC_CDE")
	private String functionCode;
	
	@Column(name="ACCESS_FUNC_SUB_CDE")
	private String functionSubCode;
	
	

	@JsonBackReference
	public UserProfileHeaderDAO getUserProfileHeader() {
		return userProfileHeader;
	}

	public String getFunctionCode() {
		return functionCode;
	}

	public String getFunctionSubCode() {
		return functionSubCode;
	}

	public void setUserProfileHeader(UserProfileHeaderDAO userProfileHeader) {
		this.userProfileHeader = userProfileHeader;
	}

	public void setFunctionCode(String functionCode) {
		this.functionCode = functionCode;
	}

	public void setFunctionSubCode(String functionSubCode) {
		this.functionSubCode = functionSubCode;
	}
	
	private String getCompositeKey() {
	    return this.getUserProfileHeader().getUserName() + this.getFunctionCode() + this.getFunctionSubCode();
	}

	@Override
	public boolean equals(Object obj) {
	    if(obj instanceof UserProfileDetailDAOPK) {
	        UserProfileDetailDAOPK userProfileDetailDAOPK = (UserProfileDetailDAOPK)obj;
	        return this.getCompositeKey().equals(userProfileDetailDAOPK.getCompositeKey());
	    }
	    return false;
	}
	
	@Override
	public int hashCode() {
	    return this.getCompositeKey().hashCode();
	}
}
