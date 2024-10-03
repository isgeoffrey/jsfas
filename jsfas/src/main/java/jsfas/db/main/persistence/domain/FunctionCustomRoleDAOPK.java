package jsfas.db.main.persistence.domain;


import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Embeddable;

@Embeddable
public class FunctionCustomRoleDAOPK implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 4635067302952466002L;

	@Column(name = "FUNC_CDE")
	private String functionCode;
	
	@Column(name = "FUNC_SUB_CDE")
	private String functionSubCode;

	public String getFunctionCode() {
		return functionCode;
	}

	public void setFunctionCode(String functionCode) {
		this.functionCode = functionCode;
	}

	public String getFunctionSubCode() {
		return functionSubCode;
	}

	public void setFunctionSubCode(String functionSubCode) {
		this.functionSubCode = functionSubCode;
	}
	
	private String getCompositeKey() {
	    return this.getFunctionCode() + this.getFunctionSubCode();
	}
	
	@Override
	public boolean equals(Object obj) {
	    if(obj instanceof FunctionCustomRoleDAOPK) {
	    	FunctionCustomRoleDAOPK functionCustomRoleDAOPK = (FunctionCustomRoleDAOPK)obj;
	        return this.getCompositeKey().equals(functionCustomRoleDAOPK.getCompositeKey());
	    }
	    return false;
	}
	
	@Override
	public int hashCode() {
	    return this.getCompositeKey().hashCode();
	}

	
	
}