package jsfas.db.main.persistence.domain;

import java.io.Serializable;
import java.math.BigDecimal;
import java.sql.Timestamp;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Entity
public class FoApplicationEnquiryRaw implements Serializable {
	
	@Id	
	@Column(name = "ID")
	protected Integer id;
	@Column(name = "app_id")
	protected String appId;
	@Column(name = "app_nbr")
	protected String appNbr;
	@Column(name = "BR_NO")
    protected String brNo;
	@Column(name = "VERSION_NO")
    protected int versionNo;
	@Column(name = "APPL_REQUESTER_DEPT")
    protected String applRequesterDept;
	@Column(name = "APPL_REQUESTER_ID")
    protected String applRequesterId;
	@Column(name = "APPL_REQUESTER_NAME")
    protected String applRequesterName;
	@Column(name = "APPL_USER_ID")
    protected String applUserId;
	@Column(name = "APPL_USER_NAME")
    protected String applUserName;
	@Column(name = "APPL_DTTM")
    protected Timestamp applDttm;
	@Column(name = "APPL_STAT_CDE")
    protected String applStatCde;
	@Column(name = "APPL_STAT_DESCR")
    protected String applStatDescr;
	
	@Column(name = "CATEGORY_DESCR")
	protected String categoryDescr;
	
	@Column(name = "PENDING_APRVER_ID")
    protected String pendingAprverId;
	@Column(name = "PENDING_APRVER_NAME")
    protected String pendingAprverName;
	@Column(name = "PENDING_APRVER_TYPE")
    protected String pendingAprverType;
	
	@Column(name = "EL_TYPE_nam")
	protected String elTypeNam;
	
	@Column(name = "appl_start_term_desc")
	protected String applStartTerm;
	@Column(name = "appl_end_term_desc")
	protected String applEndTerm;
	
	@Column(name = "APPL_START_DT")
    protected Timestamp applStartDt;
	@Column(name = "APPL_END_DT")
    protected Timestamp applEndDt;
	@Column(name = "PRGM_CDE")
    protected String prgmCde;
	@Column(name = "SCH_CDE")
    protected String schCde;
	@Column(name = "DEPT")
    protected String dept;
	@Column(name = "APP_AMT")
    protected BigDecimal appAmt;
	@Column(name = "PYMT_AMT")
    protected BigDecimal pymtAmt;
	@Column(name = "MPF_AMT")
    protected BigDecimal mpfAmt;
	@Column(name = "PYMT_TYPE_CDE")
    protected String pymtTypeCde;
	@Column(name = "INSTALM_NO")
    protected Integer instalmNo;
	@Column(name = "INSTALM_SEQ")
    protected Integer instalmSeq;
	@Column(name = "PYMT_START_DT")
    protected Timestamp pymtStartDt;
	@Column(name = "PYMT_END_DT")
    protected Timestamp pymtEndDt;
	@Column(name = "CREAT_DAT")
    protected Timestamp creatDat;
	@Column(name = "CHNG_DAT")
    protected Timestamp chngDat;
	@Column(name = "APRV_TYPE_CDE")
	protected String aprvTypeCde;
	@Column(name = "APRV_USER_ID")
    protected String aprvUserId;
	@Column(name = "APRV_USER_NAME")
    protected String aprvUserName;
	@Column(name = "APPROVED")
    protected Integer approved;
	@Column(name = "APRV_DTTM")
    protected Timestamp aprvDttm;
	
	@Column(name = "aprv_inpost_name")
	protected String aprvInpostName;
	
	@Column(name = "appl_user_dept")
	protected String applUserDept;
	
	@Column(name = "appl_user_id_count")
	protected Integer applUserIdCount;
	
	@Column(name="appl_start_term")
	protected String applStartTermStrm;
	
	@Column(name = "appl_end_term")
	protected String applEndTermStrm;
	
	@Column(name = "appl_schl_short")
	protected String applSchlShort;
	
	@Column(name="appl_schl_long")
	protected String applSchlLong;
	
	@Column(name = "req_schl_short")
	protected String reqSchlShort;
	
	@Column(name="req_schl_long")
	protected String reqSchlLong;

	public String getAppId() {
		return appId;
	}
	public void setAppId(String appId) {
		this.appId = appId;
	}
	public String getBrNo() {
		return brNo;
	}
	public void setBrNo(String brNo) {
		this.brNo = brNo;
	}
	public Integer getVersionNo() {
		return versionNo;
	}
	public void setVersionNo(Integer versionNo) {
		this.versionNo = versionNo;
	}
	public String getApplRequesterDept() {
		return applRequesterDept;
	}
	public void setApplRequesterDept(String applRequesterDeptId) {
		this.applRequesterDept = applRequesterDeptId;
	}
	public String getApplRequesterName() {
		return applRequesterName;
	}
	public void setApplRequesterName(String applRequesterName) {
		this.applRequesterName = applRequesterName;
	}
	public String getApplUserId() {
		return applUserId;
	}
	public void setApplUserId(String applUserId) {
		this.applUserId = applUserId;
	}
	public String getApplUserName() {
		return applUserName;
	}
	public void setApplUserName(String applUserName) {
		this.applUserName = applUserName;
	}
	public Timestamp getApplDttm() {
		return applDttm;
	}
	public void setApplDttm(Timestamp applDttm) {
		this.applDttm = applDttm;
	}
	public String getApplStatCde() {
		return applStatCde;
	}
	public void setApplStatCde(String applStatCde) {
		this.applStatCde = applStatCde;
	}
	public String getApplStatDescr() {
		return applStatDescr;
	}
	public void setApplStatDescr(String applStatDescr) {
		this.applStatDescr = applStatDescr;
	}
	public String getCategorDescr() {
		return categoryDescr;
	}
	public void setCategoryDescr(String categoryDescr) {
		this.categoryDescr = categoryDescr;
	}	
	public Timestamp getApplStartDt() {
		return applStartDt;
	}
	public void setApplStartDt(Timestamp applStartDt) {
		this.applStartDt = applStartDt;
	}
	public Timestamp getApplEndDt() {
		return applEndDt;
	}
	public void setApplEndDt(Timestamp applEndDt) {
		this.applEndDt = applEndDt;
	}
	public String getPrgmCde() {
		return prgmCde;
	}
	public void setPrgmCde(String prgmCde) {
		this.prgmCde = prgmCde;
	}
	public String getSchCde() {
		return schCde;
	}
	public void setSchCde(String schCde) {
		this.schCde = schCde;
	}
	public String getDept() {
		return dept;
	}
	public void setDept(String dept) {
		this.dept = dept;
	}
	public BigDecimal getPymtAmt() {
		return pymtAmt;
	}
	public void setPymtAmt(BigDecimal pymtAmt) {
		this.pymtAmt = pymtAmt;
	}
	public BigDecimal getMpfAmt() {
		return mpfAmt;
	}
	public void setMpfAmt(BigDecimal mpfAmt) {
		this.mpfAmt = mpfAmt;
	}
	public String getPymtTypeCde() {
		return pymtTypeCde;
	}
	public void setPymtTypeCde(String pymtTypeCde) {
		this.pymtTypeCde = pymtTypeCde;
	}
	public Integer getInstalmNo() {
		return instalmNo;
	}
	public void setInstalmNo(Integer instalmNo) {
		this.instalmNo = instalmNo;
	}
	public Integer getInstalmSeq() {
		return instalmSeq;
	}
	public void setInstalmSeq(Integer instalmSeq) {
		this.instalmSeq = instalmSeq;
	}
	public Timestamp getPymtStartDt() {
		return pymtStartDt;
	}
	public void setPymtStartDt(Timestamp pymtStartDt) {
		this.pymtStartDt = pymtStartDt;
	}
	public Timestamp getPymtEndDt() {
		return pymtEndDt;
	}
	public void setPymtEndDt(Timestamp pymtEndDt) {
		this.pymtEndDt = pymtEndDt;
	}
	public Timestamp getCreatDat() {
		return creatDat;
	}
	public void setCreatDat(Timestamp creatDat) {
		this.creatDat = creatDat;
	}
	public Timestamp getChngDat() {
		return chngDat;
	}
	public void setChngDat(Timestamp chngDat) {
		this.chngDat = chngDat;
	}
	public String getAprvTypeCde() {
		return aprvTypeCde;
	}
	public void setAprvTypeCde(String aprvTypeCde) {
		this.aprvTypeCde = aprvTypeCde;
	}
	public String getAprvUserId() {
		return aprvUserId;
	}
	public void setAprvUserId(String aprvUserId) {
		this.aprvUserId = aprvUserId;
	}
	public String getAprvUserName() {
		return aprvUserName;
	}
	public void setAprvUserName(String aprvUserName) {
		this.aprvUserName = aprvUserName;
	}
	public Integer getApproved() {
		return approved;
	}
	public void setApproved(Integer approved) {
		this.approved = approved;
	}
	public Timestamp getAprvDttm() {
		return aprvDttm;
	}
	public void setAprvDttm(Timestamp aprvDttm) {
		this.aprvDttm = aprvDttm;
	}
	public String getPendingAprverId() {
		return pendingAprverId;
	}
	public void setPendingAprverId(String pendingAprverId) {
		this.pendingAprverId = pendingAprverId;
	}
	public String getPendingAprverName() {
		return pendingAprverName;
	}
	public void setPendingAprverName(String pendingAprverName) {
		this.pendingAprverName = pendingAprverName;
	}
	public String getElTypeNam() {
		return elTypeNam;
	}
	public void setElTypeDescr(String elTypeNam) {
		this.elTypeNam = elTypeNam;
	}
	public String getApplStartTerm() {
		return applStartTerm;
	}
	public void setApplStartTerm(String applStartTerm) {
		this.applStartTerm = applStartTerm;
	}
	public String getApplEndTerm() {
		return applEndTerm;
	}
	public void setApplEndTerm(String applEndTerm) {
		this.applEndTerm = applEndTerm;
	}
	public String getApplRequesterId() {
		return applRequesterId;
	}
	public void setApplRequesterId(String applRequesterid) {
		this.applRequesterId = applRequesterid;
	}
	public String getPendingAprverType() {
		return pendingAprverType;
	}
	public void setPendingAprverType(String pendingAprverType) {
		this.pendingAprverType = pendingAprverType;
	}
	public String getAppNbr() {
		return appNbr;
	}
	public void setAppNbr(String appNbr) {
		this.appNbr = appNbr;
	}
	public BigDecimal getAppAmt() {
		return appAmt;
	}
	public void setAppAmt(BigDecimal appAmt) {
		this.appAmt = appAmt;
	}
	public String getAprvInpostName() {
		return aprvInpostName;
	}
	public void setAprvInpostName(String aprvInpostName) {
		this.aprvInpostName = aprvInpostName;
	}
	public String getApplUserDept() {
		return applUserDept;
	}
	public void setApplUserDept(String applUserDept) {
		this.applUserDept = applUserDept;
	}
	public Integer getApplUserIdCount() {
		return applUserIdCount;
	}
	public void setApplUserIdCount(Integer applUserIdCount) {
		this.applUserIdCount = applUserIdCount;
	}
	public String getApplStartTermStrm() {
		return applStartTermStrm;
	}
	public void setApplStartTermStrm(String applStartTermStrm) {
		this.applStartTermStrm = applStartTermStrm;
	}
	public String getApplEndTermStrm() {
		return applEndTermStrm;
	}
	public void setApplEndTermStrm(String applEndTermStrm) {
		this.applEndTermStrm = applEndTermStrm;
	}
	public String getApplSchlShort() {
		return applSchlShort;
	}
	public void setApplSchlShort(String applSchlShort) {
		this.applSchlShort = applSchlShort;
	}
	public String getApplSchlLong() {
		return applSchlLong;
	}
	public void setApplSchlLong(String applSchlLong) {
		this.applSchlLong = applSchlLong;
	}
	public String getReqSchlShort() {
		return reqSchlShort;
	}
	public void setReqSchlShort(String reqSchlShort) {
		this.reqSchlShort = reqSchlShort;
	}
	public String getReqSchlLong() {
		return reqSchlLong;
	}
	public void setReqSchlLong(String reqSchlLong) {
		this.reqSchlLong = reqSchlLong;
	}
}
