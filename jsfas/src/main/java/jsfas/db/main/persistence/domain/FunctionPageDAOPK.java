package jsfas.db.main.persistence.domain;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.JoinColumns;
import javax.persistence.ManyToOne;

import com.fasterxml.jackson.annotation.JsonBackReference;

@Embeddable
public class FunctionPageDAOPK implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 6604928736179311056L;

	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumns({
		@JoinColumn(name="FUNC_CDE"),
		@JoinColumn(name="FUNC_SUB_CDE")
	})
	private FunctionDAO function;
	
	@Column(name = "PAGE_NAM")
	private String pageName;

	@JsonBackReference	
	public FunctionDAO getFunction() {
		return function;
	}

	public String getPageName() {
		return pageName;
	}

	public void setFunction(FunctionDAO function) {
		this.function = function;
	}

	public void setPageName(String pageName) {
		this.pageName = pageName;
	}
	
	private String getCompositeKey() {
	    return this.getFunction().getFunctionPK().getFunctionCode() + this.getFunction().getFunctionPK().getFunctionSubCode() + this.getPageName();
	}
	
	@Override
	public boolean equals(Object obj) {
	    if(obj instanceof FunctionPageDAOPK) {
	        FunctionPageDAOPK functionPageDAOPK = (FunctionPageDAOPK)obj;
	        return this.getCompositeKey().equals(functionPageDAOPK.getCompositeKey());
	    }
	    return false;
	}
	
	@Override
	public int hashCode() {
	    return this.getCompositeKey().hashCode();
	}
	
}
