package jsfas.db.main.persistence.domain;

import java.io.Serializable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name="EL_INPOST_STAFF_IMP_V")
public class ElInpostStaffImpVDAO implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 8908170327161930260L;

	// TODO Need review
	@Id
	@Column(name="EMPLID")
	private String emplid;
	
	@Column(name="USER_NAM")
	private String userNam;
	
	@Column(name="EMAIL_ADDR")
	private String emailAddr;
	
	@Column(name="LAST_NAM")
	private String lastNam;
	
	@Column(name="FIRST_NAM")
	private String firstNam;
	
	@Column(name="DISPLAY_NAM")
	private String displayNam;
	
	@Column(name="DEPT_ID")
	private String deptId;
	
	@Column(name="JOB_CATG_CDE")
	private String jobCatgCde;
	
	@Column(name="TOS")
	private String tos;
	
	public String getEmplid() {
		return emplid;
	}
	public void setEmplid(String emplid) {
		this.emplid = emplid;
	}
	public String getUserNam() {
		return userNam;
	}
	public void setUserNam(String userNam) {
		this.userNam = userNam;
	}
	public String getEmailAddr() {
		return emailAddr;
	}
	public void setEmailAddr(String emailAddr) {
		this.emailAddr = emailAddr;
	}
	public String getLastNam() {
		return lastNam;
	}
	public void setLastNam(String lastNam) {
		this.lastNam = lastNam;
	}
	public String getFirstNam() {
		return firstNam;
	}
	public void setFirstNam(String firstNam) {
		this.firstNam = firstNam;
	}
	public String getDisplayNam() {
		return displayNam;
	}
	public void setDisplayNam(String displayNam) {
		this.displayNam = displayNam;
	}
	public String getDeptId() {
		return deptId;
	}
	public void setDeptId(String deptId) {
		this.deptId = deptId;
	}
	public String getJobCatgCde() {
		return jobCatgCde;
	}
	public void setJobCatgCde(String jobCatgCde) {
		this.jobCatgCde = jobCatgCde;
	}
	public String getTos() {
		return tos;
	}
	public void setTos(String tos) {
		this.tos = tos;
	}
}
