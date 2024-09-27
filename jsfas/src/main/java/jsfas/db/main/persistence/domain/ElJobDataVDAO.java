package jsfas.db.main.persistence.domain;

import java.io.Serializable;
import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table(name="EL_JOB_DATA_V")
public class ElJobDataVDAO implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -8879321548846265822L;

	@EmbeddedId
	private ElJobDataVDAOPK elJobDataVDAOPK;
	
	@Column(name="EMPL_CTG")
	private String emplCtg;
	
	@Column(name="PAY_GROUP")
	private String payGroup;

	public ElJobDataVDAOPK getElJobDataVDAOPK() {
		return elJobDataVDAOPK;
	}

	public void setElJobDataVDAOPK(ElJobDataVDAOPK elJobDataVDAOPK) {
		this.elJobDataVDAOPK = elJobDataVDAOPK;
	}

	public String getEmplCtg() {
		return emplCtg;
	}

	public void setEmplCtg(String emplCtg) {
		this.emplCtg = emplCtg;
	}

	public String getPayGroup() {
		return payGroup;
	}

	public void setPayGroup(String payGroup) {
		this.payGroup = payGroup;
	}

	

}
