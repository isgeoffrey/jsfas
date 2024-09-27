package jsfas.db.main.persistence.domain;

import java.io.Serializable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name="EL_SAL_ELEMENT_V")
public class ElSalElementVDAO implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -678155856288265822L;

	@Id
	@Column(name="SAL_ELEMENT_CDE")
	private String salElementCde;
	
	@Column(name="SAL_ELEMENT_DESC")
	private String salElementDesc;

	public String getSalElementCde() {
		return salElementCde;
	}

	public void setSalElementCde(String salElementCde) {
		this.salElementCde = salElementCde;
	}

	public String getSalElementDesc() {
		return salElementDesc;
	}

	public void setSalElementDesc(String salElementDesc) {
		this.salElementDesc = salElementDesc;
	}

}
