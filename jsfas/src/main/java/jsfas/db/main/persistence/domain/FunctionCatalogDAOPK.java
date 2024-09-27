package jsfas.db.main.persistence.domain;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Embeddable;

@Embeddable
public class FunctionCatalogDAOPK implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 4635067302952465002L;

	@Column(name = "SYS_CATG_CDE")
	private String systemCatalogCode;
	
	@Column(name = "FUNC_CATG_CDE")
	private String functionCatalogCode;

	public String getSystemCatalogCode() {
		return systemCatalogCode;
	}

	public String getFunctionCatalogCode() {
		return functionCatalogCode;
	}

	public void setSystemCatalogCode(String systemCatalogCode) {
		this.systemCatalogCode = systemCatalogCode;
	}

	public void setFunctionCatalogCode(String functionCatalogCode) {
		this.functionCatalogCode = functionCatalogCode;
	}
	
	private String getCompositeKey() {
	    return this.getSystemCatalogCode() + this.getFunctionCatalogCode();
	}
	
	@Override
	public boolean equals(Object obj) {
	    if(obj instanceof FunctionCatalogDAOPK) {
	        FunctionCatalogDAOPK functionCatalogDAOPK = (FunctionCatalogDAOPK)obj;
	        return this.getCompositeKey().equals(functionCatalogDAOPK.getCompositeKey());
	    }
	    return false;
	}
	
	@Override
	public int hashCode() {
	    return this.getCompositeKey().hashCode();
	}
	
}
