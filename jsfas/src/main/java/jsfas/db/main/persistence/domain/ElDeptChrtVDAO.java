package jsfas.db.main.persistence.domain;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name="EL_DEPT_CHRT_V")
public class ElDeptChrtVDAO implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 4170681721442491679L;

	@Id
	@Column(name="DEPT_ID")
	public String deptId;
	
	@Column (name="DEPT_SHORT_DESC")
	public String deptShortDesc;
	
	@Column (name="DEPT_LONG_DESC")
	public String deptLongDesc;

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
}
