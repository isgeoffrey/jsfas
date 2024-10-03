package jsfas.db.main.persistence.domain;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Embeddable;

@Embeddable
public class UserRoleDAOPK implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 4635067302952265002L;
	
	@Column(name = "OPRID")
	private String oprID;
	
	@Column(name = "CUST_DEPTID")
	private String deptID;
	
	//is view only have select permission
	public String getOprID() {
		return oprID;
	}

	public String getDeptID() {
		return deptID;
	}
	
	private String getCompositeKey() {
	    return this.getOprID() + this.getDeptID();
	}
	
	@Override
	public boolean equals(Object obj) {
	    if(obj instanceof UserRoleDAOPK) {
	    	UserRoleDAOPK userRoleDAOPK = (UserRoleDAOPK)obj;
	        return this.getCompositeKey().equals(userRoleDAOPK.getCompositeKey());
	    }
	    return false;
	}
	
	@Override
	public int hashCode() {
	    return this.getCompositeKey().hashCode();
	}



}
