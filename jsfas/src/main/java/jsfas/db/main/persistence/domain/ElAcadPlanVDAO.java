package jsfas.db.main.persistence.domain;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name="EL_ACAD_PLAN_V")
public class ElAcadPlanVDAO implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -4761367015216315608L;

	@Id
	@Column(name="ACAD_PLAN")
	private String acadPlan;
	@Column(name="ACAD_PLAN_DESCR")
	private String acadPlanDescr;
	@Column(name="DESCRFORMAL")
	private String descrformal;
	@Column(name="ACAD_CAREER")
	private String acadCareer;
	@Column(name="ACAD_PROG")
	private String acadProg;
	@Column(name="ACAD_PROG_DESCR")
	private String acadProgDescr;
	@Column(name="ACAD_ORG")
	private String acadOrg;
	@Column(name="ACAD_ORG_DESCR_SHORT")
	private String acadOrgDescrShort;
	@Column(name="ACAD_ORG_DESCR")
	private String acadOrgDescr;
	@Column(name="ACAD_GROUP")
	private String acadGroup;
	@Column(name="ACAD_GROUP_DESCR")
	private String acadGroupDescr;
	
	public String getAcadPlan() {
		return acadPlan;
	}
	public void setAcadPlan(String acadPlan) {
		this.acadPlan = acadPlan;
	}
	public String getAcadPlanDescr() {
		return acadPlanDescr;
	}
	public void setAcadPlanDescr(String acadPlanDescr) {
		this.acadPlanDescr = acadPlanDescr;
	}
	public String getDescrformal() {
		return descrformal;
	}
	public void setDescrformal(String descrformal) {
		this.descrformal = descrformal;
	}
	public String getAcadCareer() {
		return acadCareer;
	}
	public void setAcadCareer(String acadCareer) {
		this.acadCareer = acadCareer;
	}
	public String getAcadProg() {
		return acadProg;
	}
	public void setAcadProg(String acadProg) {
		this.acadProg = acadProg;
	}
	public String getAcadProgDescr() {
		return acadProgDescr;
	}
	public void setAcadProgDescr(String acadProgDescr) {
		this.acadProgDescr = acadProgDescr;
	}
	public String getAcadOrg() {
		return acadOrg;
	}
	public void setAcadOrg(String acadOrg) {
		this.acadOrg = acadOrg;
	}
	public String getAcadOrgDescrShort() {
		return acadOrgDescrShort;
	}
	public void setAcadOrgDescrShort(String acadOrgDescrShort) {
		this.acadOrgDescrShort = acadOrgDescrShort;
	}
	public String getAcadOrgDescr() {
		return acadOrgDescr;
	}
	public void setAcadOrgDescr(String acadOrgDescr) {
		this.acadOrgDescr = acadOrgDescr;
	}
	public String getAcadGroup() {
		return acadGroup;
	}
	public void setAcadGroup(String acadGroup) {
		this.acadGroup = acadGroup;
	}
	public String getAcadGroupDescr() {
		return acadGroupDescr;
	}
	public void setAcadGroupDescr(String acadGroupDescr) {
		this.acadGroupDescr = acadGroupDescr;
	}

}
