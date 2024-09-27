package jsfas.db.main.persistence.domain;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name="EL_SCHL_CHRT_V")
public class ElSchlChrtVDAO implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 5647069568212524326L;

	@Id
	@Column(name="DEPT_ID")
	private String deptId;

	@Column(name="dept_short_desc")
	private String deptShortDesc;
	
	@Column(name="dept_long_desc")
	private String deptLongDesc;
	
	@Column(name="schl_id")
	private String schlId;
	
	@Column(name="schl_short_desc")
	private String schlShortDesc;
	
	@Column(name="schl_long_desc")
	private String schlLongDesc;

	public String getDeptId() {
		return deptId;
	}

	public void setDeptId(String deptId) {
		this.deptId = deptId;
	}

	public String getDeptShortDesc() {
		return deptShortDesc;
	}

	public void setDeptShortDesc(String deptShortDesc) {
		this.deptShortDesc = deptShortDesc;
	}

	public String getDeptLongDesc() {
		return deptLongDesc;
	}

	public void setDeptLongDesc(String deptLongDesc) {
		this.deptLongDesc = deptLongDesc;
	}

	public String getSchlId() {
		return schlId;
	}

	public void setSchlId(String schlId) {
		this.schlId = schlId;
	}

	public String getSchlShortDesc() {
		return schlShortDesc;
	}

	public void setSchlShortDesc(String schlShortDesc) {
		this.schlShortDesc = schlShortDesc;
	}

	public String getSchlLongDesc() {
		return schlLongDesc;
	}

	public void setSchlLongDesc(String schlLongDesc) {
		this.schlLongDesc = schlLongDesc;
	}
}

