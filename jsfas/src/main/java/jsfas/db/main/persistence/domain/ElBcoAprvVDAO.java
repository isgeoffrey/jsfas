package jsfas.db.main.persistence.domain;

import java.io.Serializable;
import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table(name="EL_BCO_APRV_V")
public class ElBcoAprvVDAO implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -678155856288265822L;

	@EmbeddedId
	private ElBcoAprvVDAOPK elBcoAprvVDAOPK;
	
	@Column(name="PROJ_NBR")
	private String projNbr;

	public ElBcoAprvVDAOPK getElBcoAprvVDAOPK() {
		return elBcoAprvVDAOPK;
	}

	public void setElBcoAprvVDAOPK(ElBcoAprvVDAOPK elBcoAprvVDAOPK) {
		this.elBcoAprvVDAOPK = elBcoAprvVDAOPK;
	}

	public String getProjNbr() {
		return projNbr;
	}

	public void setProjNbr(String projNbr) {
		this.projNbr = projNbr;
	}

}
