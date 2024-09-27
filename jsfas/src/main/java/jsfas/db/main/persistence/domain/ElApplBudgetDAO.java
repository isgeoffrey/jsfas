package jsfas.db.main.persistence.domain;

import java.io.Serializable;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.UUID;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import jsfas.common.utils.GeneralUtil;

@Entity
@Table(name="EL_APPL_BUDGET")
public class ElApplBudgetDAO implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 6257444389059166422L;

	@Id
	@Column(name="ID")
	private String id;
	@Column(name="APPL_HDR_ID")
	private String applHdrId;
	@Column(name="APPL_EL_TYPE_ID")
	private String applElTypeId;
	@Column(name="ACCT_CDE")
	private String acctCde;
	@Column(name="ANALYSIS_CDE")
	private String analysisCde;
	@Column(name="FUND_CDE")
	private String fundCde;
	@Column(name="PROJ_ID")
	private String projId;
	@Column(name="PROJ_NBR")
	private String projNbr;
	@Column(name="CLASS")
	private String classCde;
	@Column(name="BCO_APRV_ID")
	private String bcoAprvId;
	@Column(name="BCO_APRV_NAME")
	private String bcoAprvName;
	@Column(name="BUDG_ACCT_SHARE")
	private BigDecimal budgAcctShare;
	@Column(name="BUDG_ACCT_AMT")
	private BigDecimal budgAcctAmt;
	@Column(name="BR_NO")
	private String brNo;
	@Column(name="BR_CREATED_DTTM")
	private Timestamp brCreatedDttm;
	@Column(name="MOD_CTRL_TXT")
	private String modCtrlTxt;
	@Column(name="CREAT_USER")
	private String creatUser;
	@Column(name="CREAT_DAT")
	private Timestamp creatDat;
	@Column(name="CHNG_USER")
	private String chngUser;
	@Column(name="CHNG_DAT")
	private Timestamp chngDat;
	@Column(name="OP_PAGE_NAM")
	private String opPageNam;
	
	public ElApplBudgetDAO() {
		super();
		this.id = UUID.randomUUID().toString();
		this.applHdrId = " ";
		this.applElTypeId = " ";
		this.acctCde = " ";
		this.analysisCde = " ";
		this.fundCde = " ";
		this.projId = " ";
		this.projNbr = " ";
		this.classCde = " ";
		this.bcoAprvId = " ";
		this.bcoAprvName = " ";
		this.budgAcctShare = BigDecimal.ZERO;
		this.budgAcctAmt = BigDecimal.ZERO;
		this.brNo = " ";
		this.brCreatedDttm = GeneralUtil.NULLTIMESTAMP;
		
		this.modCtrlTxt = GeneralUtil.genModCtrlTxt();
		this.creatDat = GeneralUtil.getCurrentTimestamp();
		this.creatUser = " ";
		this.chngDat = GeneralUtil.getCurrentTimestamp();
		this.chngUser = " ";
		this.opPageNam = " ";
	}
//	public String getId() {
//		return id;
//	}
//	public void setId(String id) {
//		this.id = id;
//	}
//	public String getApplHdrId() {
//		return applHdrId;
//	}
//	public void setApplHdrId(String applHdrId) {
//		this.applHdrId = applHdrId;
//	}
//	public String getApplElTypeId() {
//		return applElTypeId;
//	}
//	public void setApplElTypeId(String applElTypeId) {
//		this.applElTypeId = applElTypeId;
//	}
//	public String getAcctCde() {
//		return acctCde;
//	}
//	public void setAcctCde(String acctCde) {
//		this.acctCde = acctCde;
//	}
//	public String getAnalysisCde() {
//		return analysisCde;
//	}
//	public void setAnalysisCde(String analysisCde) {
//		this.analysisCde = analysisCde;
//	}
//	public String getFundCde() {
//		return fundCde;
//	}
//	public void setFundCde(String fundCde) {
//		this.fundCde = fundCde;
//	}
//	public String getProjNbr() {
//		return projNbr;
//	}
//	public void setProjNbr(String projNbr) {
//		this.projNbr = projNbr;
//	}
//	public String getClassCde() {
//		return classCde;
//	}
//	public void setClassCde(String classCde) {
//		this.classCde = classCde;
//	}
//	public BigDecimal getBudgAcctShare() {
//		return budgAcctShare;
//	}
//	public void setBudgAcctShare(BigDecimal budgAcctShare) {
//		this.budgAcctShare = budgAcctShare;
//	}
//	public BigDecimal getBudgAcctAmt() {
//		return budgAcctAmt;
//	}
//	public void setBudgAcctAmt(BigDecimal budgAcctAmt) {
//		this.budgAcctAmt = budgAcctAmt;
//	}
//	public String getBrNo() {
//		return brNo;
//	}
//	public void setBrNo(String brNo) {
//		this.brNo = brNo;
//	}
//	public Timestamp getBrCreatedDttm() {
//		return brCreatedDttm;
//	}
//	public void setBrCreatedDttm(Timestamp brCreatedDttm) {
//		this.brCreatedDttm = brCreatedDttm;
//	}
//	public String getModCtrlTxt() {
//		return modCtrlTxt;
//	}
//	public void setModCtrlTxt(String modCtrlTxt) {
//		this.modCtrlTxt = modCtrlTxt;
//	}
//	public String getCreatUser() {
//		return creatUser;
//	}
//	public void setCreatUser(String creatUser) {
//		this.creatUser = creatUser;
//	}
//	public Timestamp getCreatDat() {
//		return creatDat;
//	}
//	public void setCreatDat(Timestamp creatDat) {
//		this.creatDat = creatDat;
//	}
//	public String getChngUser() {
//		return chngUser;
//	}
//	public void setChngUser(String chngUser) {
//		this.chngUser = chngUser;
//	}
//	public Timestamp getChngDat() {
//		return chngDat;
//	}
//	public void setChngDat(Timestamp chngDat) {
//		this.chngDat = chngDat;
//	}
//	public String getOpPageNam() {
//		return opPageNam;
//	}
//	public void setOpPageNam(String opPageNam) {
//		this.opPageNam = opPageNam;
//	}
//	public String getProjId() {
//		return projId;
//	}
//	public void setProjId(String projId) {
//		this.projId = projId;
//	}
//	public String getBcoAprvId() {
//		return bcoAprvId;
//	}
//	public void setBcoAprvId(String bcoAprvId) {
//		this.bcoAprvId = bcoAprvId;
//	}
//	public String getBcoAprvName() {
//		return bcoAprvName;
//	}
//	public void setBcoAprvName(String bcoAprvName) {
//		this.bcoAprvName = bcoAprvName;
//	}
	
}
