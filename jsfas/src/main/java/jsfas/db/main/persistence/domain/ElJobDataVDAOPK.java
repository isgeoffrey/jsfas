package jsfas.db.main.persistence.domain;

import java.io.Serializable;
import java.math.BigDecimal;
import java.sql.Timestamp;

import javax.persistence.Column;
import javax.persistence.Embeddable;

@Embeddable
public class ElJobDataVDAOPK implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 96543218978598575L;

	@Column(name = "EMPLID")
	private String emplid;
	
	@Column(name = "EFFDT")
	private Timestamp effdt;

	public String getEmplid() {
		return emplid;
	}

	public void setEmplid(String emplid) {
		this.emplid = emplid;
	}

	public Timestamp getEffdt() {
		return effdt;
	}

	public void setEffdt(Timestamp effdt) {
		this.effdt = effdt;
	}
	
	
}
