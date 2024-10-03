package jsfas.db.main.persistence.domain;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Embeddable;

public class UserFuncVDAOPK implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 4635067303332465002L;
	
	@Column(name = "OPRID")
	private String oprID;
	
	@Column(name = "CUST_DEPTID")
	private String deptID;
	
	@Column(name = "FUNC_CDE")
	private String functionCode;
	
	@Column(name = "FUNC_SUB_CDE")
	private String functionSubCode;

	public String getOprID() {
		return oprID;
	}

	public String getDeptID() {
		return deptID;
	}
	
	public String getFunctionCode() {
		return functionCode;
	}

	public String getFunctionSubCode() {
		return functionSubCode;
	}
	
	private String getCompositeKey() {
	    return this.getOprID() + this.getDeptID() + this.getFunctionCode() + this.getFunctionSubCode();
	}
	
	@Override
	public boolean equals(Object obj) {
	    if(obj instanceof UserFuncVDAOPK) {
	    	UserFuncVDAOPK userFuncVDAOPK = (UserFuncVDAOPK)obj;
	        return this.getCompositeKey().equals(userFuncVDAOPK.getCompositeKey());
	    }
	    return false;
	}
	
	@Override
	public int hashCode() {
	    return this.getCompositeKey().hashCode();
	}
}
