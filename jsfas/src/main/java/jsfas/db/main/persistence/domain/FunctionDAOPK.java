package jsfas.db.main.persistence.domain;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Embeddable;

@Embeddable
public class FunctionDAOPK implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1268949377341917757L;

	@Column(name = "FUNC_CDE")
	private String functionCode;
	
	@Column(name = "FUNC_SUB_CDE")
	private String functionSubCode;

	public String getFunctionCode() {
		return functionCode;
	}

	public String getFunctionSubCode() {
		return functionSubCode;
	}

	public void setFunctionCode(String functionCode) {
		this.functionCode = functionCode;
	}

	public void setFunctionSubCode(String functionSubCode) {
		this.functionSubCode = functionSubCode;
	}
	
	private String getCompositeKey() {
	    return this.getFunctionCode() + this.getFunctionSubCode();
	}

	@Override
	public boolean equals(Object obj) {
	    if(obj instanceof FunctionDAOPK) {
	        FunctionDAOPK functionDAOPK = (FunctionDAOPK)obj;
	        return this.getCompositeKey().equals(functionDAOPK.getCompositeKey());
	    }
	    return false;
	}
	
	@Override
	public int hashCode() {
	    return this.getCompositeKey().hashCode();
	}
	
}
