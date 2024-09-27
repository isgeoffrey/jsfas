package jsfas.db.main.persistence.domain;

import java.io.Serializable;
import java.sql.Timestamp;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name="EL_PROJ_CHRT_V")
public class ElProjChrtVDAO implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 6923504830932262483L;
	
	@Id
	@Column(name="PROJ_ID")
	private String projId;

	@Column(name="PROJ_NBR")
	private String projNbr;
	
	@Column(name="CLASS_REQUIRED")
	private String classRequired;
	
	@Column(name="START_DT")
	private Timestamp startDt;
	
	@Column(name="END_DT")
	private Timestamp endDt;

	public String getProjId() {
		return projId;
	}

	public void setProjId(String projId) {
		this.projId = projId;
	}

	public String getProjNbr() {
		return projNbr;
	}

	public void setProjNbr(String projNbr) {
		this.projNbr = projNbr;
	}

	public String getClassRequired() {
		return classRequired;
	}

	public void setClassRequired(String classRequired) {
		this.classRequired = classRequired;
	}

	public Timestamp getStartDt() {
		return startDt;
	}

	public void setStartDt(Timestamp startDt) {
		this.startDt = startDt;
	}

	public Timestamp getEndDt() {
		return endDt;
	}

	public void setEndDt(Timestamp endDt) {
		this.endDt = endDt;
	}
}
