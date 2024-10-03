package jsfas.db.main.persistence.domain;


import java.io.Serializable;
import java.sql.Timestamp;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "FAS_STK_PLAN_HDR")
public class FasStkPlanHdrDAO implements Serializable {

    private static final long serialVersionUID = 7011234243669996645L;
    
    @Id
    @Column(name = "STK_PLAN_ID")
    private String stkPlanId;

    @Column(name = "STK_PLAN_SNAP_DAT")
    private Timestamp stkPlanSnapDat;

    @Column(name = "STK_PLAN_STATUS")
    private String stkPlanStatus;

    @Column(name = "CUST_DEPTID")
    private String custDeptid;

    @Column(name = "CUST_DEPT_DESCR_SHORT")
    private String custDeptDescrShort;

    @Column(name = "CUST_DEPT_DESCR")
    private String custDeptDescr;

    @Column(name = "LOCK_STATUS")
    private String lockStatus;

    @Column(name = "LOCK_USER")
    private String lockUser;

    @Column(name = "LOCK_DAT")
    private Timestamp lockDat;

    @Column(name = "MOD_CTRL_TXT")
    private String modCtrlTxt;

    @Column(name = "CREAT_USER")
    private String creatUser;

    @Column(name = "CREAT_DAT")
    private Timestamp creatDat;

    @Column(name = "CHNG_USER")
    private String chngUser;

    @Column(name = "CHNG_DAT")
    private Timestamp chngDat;

    @Column(name = "OP_PAGE_NAM")
    private String opPageNam;

	public String getStkPlanId() {
		return stkPlanId;
	}

	public void setStkPlanId(String stkPlanId) {
		this.stkPlanId = stkPlanId;
	}

	public Timestamp getStkPlanSnapDat() {
		return stkPlanSnapDat;
	}

	public void setStkPlanSnapDat(Timestamp stkPlanSnapDat) {
		this.stkPlanSnapDat = stkPlanSnapDat;
	}

	public String getStkPlanStatus() {
		return stkPlanStatus;
	}

	public void setStkPlanStatus(String stkPlanStatus) {
		this.stkPlanStatus = stkPlanStatus;
	}

	public String getCustDeptid() {
		return custDeptid;
	}

	public void setCustDeptid(String custDeptid) {
		this.custDeptid = custDeptid;
	}

	public String getCustDeptDescrShort() {
		return custDeptDescrShort;
	}

	public void setCustDeptDescrShort(String custDeptDescrShort) {
		this.custDeptDescrShort = custDeptDescrShort;
	}

	public String getCustDeptDescr() {
		return custDeptDescr;
	}

	public void setCustDeptDescr(String custDeptDescr) {
		this.custDeptDescr = custDeptDescr;
	}

	public String getLockStatus() {
		return lockStatus;
	}

	public void setLockStatus(String lockStatus) {
		this.lockStatus = lockStatus;
	}

	public String getLockUser() {
		return lockUser;
	}

	public void setLockUser(String lockUser) {
		this.lockUser = lockUser;
	}

	public Timestamp getLockDat() {
		return lockDat;
	}

	public void setLockDat(Timestamp lockDat) {
		this.lockDat = lockDat;
	}

	public String getModCtrlTxt() {
		return modCtrlTxt;
	}

	public void setModCtrlTxt(String modCtrlTxt) {
		this.modCtrlTxt = modCtrlTxt;
	}

	public String getCreatUser() {
		return creatUser;
	}

	public void setCreatUser(String creatUser) {
		this.creatUser = creatUser;
	}

	public Timestamp getCreatDat() {
		return creatDat;
	}

	public void setCreatDat(Timestamp creatDat) {
		this.creatDat = creatDat;
	}

	public String getChngUser() {
		return chngUser;
	}

	public void setChngUser(String chngUser) {
		this.chngUser = chngUser;
	}

	public Timestamp getChngDat() {
		return chngDat;
	}

	public void setChngDat(Timestamp chngDat) {
		this.chngDat = chngDat;
	}

	public String getOpPageNam() {
		return opPageNam;
	}

	public void setOpPageNam(String opPageNam) {
		this.opPageNam = opPageNam;
	}


}