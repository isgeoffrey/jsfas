package jsfas.db.main.persistence.domain;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "FAS_STK_CUST_DEPT_V")
public class FasDeptVDAO implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 7014124243661906645L;

	@Id
	@Column(name="CUST_DEPTID")
	private String deptID;
	
	@Column(name="CUST_DEPT_DESCR_SHORT")
	private String deptDescrShort;
	
	@Column(name="CUST_DEPT_DESCR")
	private String deptDescrLong;

	public String getDeptID() {
		return deptID;
	}

	public String getDeptDescrShort() {
		return deptDescrShort;
	}

	public String getDeptDescrLong() {
		return deptDescrLong;
	}
}
