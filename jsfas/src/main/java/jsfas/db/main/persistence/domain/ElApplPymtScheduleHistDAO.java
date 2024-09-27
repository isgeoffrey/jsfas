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
@Table(name="EL_APPL_PYMT_SCHEDULE_HIST")
public class ElApplPymtScheduleHistDAO implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -67334416983251549L;

	@Id
	@Column(name="ID")
	private String id;
	@Column(name="APPL_HDR_ID")
	private String applHdrId;
	@Column(name="APPL_PYMT_METHOD_ID")
	private String applPymtMethodId;
	@Column(name="VERSION_NO")
	private int versionNo;
	@Column(name="PYMT_START_DT")
	private Timestamp pymtStartDt;
	@Column(name="PYMT_END_DT")
	private Timestamp pymtEndDt;
	@Column(name="PYMT_REV_START_DT")
	private Timestamp pymtRevStartDt;
	@Column(name="PYMT_REV_END_DT")
	private Timestamp pymtRevEndDt;
	@Column(name="EMPL_NBR")
	private Integer emplNbr;
	@Column(name="PYMT_SCHED_NO")
	private Integer pymtSchedNo;
	@Column(name="PYMT_SCHED_LINE")
	private Integer pymtSchedLine;
	@Column(name="PROJ_ID")
	private String projId;
	@Column(name="PROJ_NBR")
	private String projNbr;
	@Column(name="DEPT_ID")
	private String deptId;
	@Column(name="FUND_CDE")
	private String fundCde;
	@Column(name="CLASS_CDE")
	private String classCde;
	@Column(name="ACCT_CDE")
	private String acctCde;
	@Column(name="ANALYSIS_CDE")
	private String analysisCde;
	@Column(name="BCO_APRV_ID")
	private String bcoAprvId;
	@Column(name="BCO_APRV_NAME")
	private String bcoAprvName;
	@Column(name="PYMT_LINE_AMT")
	private BigDecimal pymtLineAmt;
	@Column(name="PYMT_LINE_AMT_TOT")
	private BigDecimal pymtLineAmtTot;
	@Column(name="BR_NO")
	private String brNo;
	@Column(name="BR_LINE_NO")
	private Integer brLineNo;
	@Column(name="BR_DIST_LINE_NO")
	private Integer brDistLineNo;
	@Column(name="BR_POST_IND")
	private String brPostInd;
	@Column(name="SAL_ELEMNT")
	private String salElemnt;
	@Column(name="PYMT_SUBMIT_DTTM")
	private Timestamp pymtSubmitDttm;
	@Column(name="PYMT_STATUS_CDE")
	private String pymtStatusCde;
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
	
	public ElApplPymtScheduleHistDAO() {
		super();
		this.id = UUID.randomUUID().toString();
		this.applHdrId = " ";
		this.applPymtMethodId = " ";
		this.pymtStartDt = GeneralUtil.NULLTIMESTAMP;
		this.pymtEndDt = GeneralUtil.NULLTIMESTAMP;
		this.pymtRevStartDt = GeneralUtil.NULLTIMESTAMP;
		this.pymtRevEndDt = GeneralUtil.NULLTIMESTAMP;
		this.emplNbr = 999;
		this.pymtSchedNo = 0;
		this.pymtSchedLine = 0;
		this.versionNo = 0;
		
		this.projId = " ";
		this.projNbr = " ";
		this.deptId = " ";
		this.fundCde = " ";
		this.classCde = " ";
		this.acctCde = " ";
		this.analysisCde = " ";
		this.bcoAprvId = " ";
		this.bcoAprvName = " ";
		this.pymtLineAmt = BigDecimal.ZERO;
		this.pymtLineAmtTot = BigDecimal.ZERO;
		
		this.brNo = " ";
		this.brLineNo = 0;
		this.brDistLineNo = 0;
		this.brPostInd = "N";
		
		this.salElemnt = " ";
		this.pymtSubmitDttm = GeneralUtil.NULLTIMESTAMP;
		this.pymtStatusCde = " ";
		
		this.modCtrlTxt = GeneralUtil.genModCtrlTxt();
		this.creatDat = GeneralUtil.getCurrentTimestamp();
		this.creatUser = " ";
		this.chngDat = GeneralUtil.getCurrentTimestamp();
		this.chngUser = " ";
		this.opPageNam = " ";
	}

	public ElApplPymtScheduleHistDAO(ElApplPymtScheduleDAO elApplPymtScheduleDAO, ElApplPymtMethodHistDAO methodDAO) {
		super();
		this.id = UUID.randomUUID().toString();
		this.applHdrId = elApplPymtScheduleDAO.getApplHdrId();
		this.applPymtMethodId = methodDAO.getId();
		this.pymtStartDt = elApplPymtScheduleDAO.getPymtStartDt();
		this.pymtEndDt = elApplPymtScheduleDAO.getPymtEndDt();
		this.pymtRevStartDt = elApplPymtScheduleDAO.getPymtRevStartDt();
		this.pymtRevEndDt = elApplPymtScheduleDAO.getPymtRevStartDt();
		this.emplNbr = elApplPymtScheduleDAO.getEmplNbr();
		this.pymtSchedNo = elApplPymtScheduleDAO.getPymtSchedNo();
		this.pymtSchedLine = elApplPymtScheduleDAO.getPymtSchedLine();
		this.versionNo = methodDAO.getVersionNo();
		
		this.projId = elApplPymtScheduleDAO.getProjId();
		this.projNbr = elApplPymtScheduleDAO.getProjNbr();
		this.deptId = elApplPymtScheduleDAO.getDeptId();
		this.fundCde = elApplPymtScheduleDAO.getFundCde();
		this.classCde = elApplPymtScheduleDAO.getClassCde();
		this.acctCde = elApplPymtScheduleDAO.getAcctCde();
		this.analysisCde = elApplPymtScheduleDAO.getAnalysisCde();
		this.bcoAprvId = elApplPymtScheduleDAO.getBcoAprvId();
		this.bcoAprvName = elApplPymtScheduleDAO.getBcoAprvName();
		this.pymtLineAmt = elApplPymtScheduleDAO.getPymtLineAmt();
		this.pymtLineAmtTot = elApplPymtScheduleDAO.getPymtLineAmtTot();
		
		this.brNo = elApplPymtScheduleDAO.getBrNo();
		this.brLineNo = elApplPymtScheduleDAO.getBrLineNo();
		this.brDistLineNo = elApplPymtScheduleDAO.getBrDistLineNo();
		this.brPostInd = elApplPymtScheduleDAO.getBrPostInd();
		
		this.salElemnt = elApplPymtScheduleDAO.getSalElemnt();
		this.pymtSubmitDttm = elApplPymtScheduleDAO.getPymtSubmitDttm();
		this.pymtStatusCde = elApplPymtScheduleDAO.getPymtStatusCde();
		
		this.modCtrlTxt = GeneralUtil.genModCtrlTxt();
		this.creatDat = GeneralUtil.getCurrentTimestamp();
		this.creatUser = " ";
		this.chngDat = GeneralUtil.getCurrentTimestamp();
		this.chngUser = " ";
		this.opPageNam = " ";
	}
	
	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getApplHdrId() {
		return applHdrId;
	}

	public void setApplHdrId(String applHdrId) {
		this.applHdrId = applHdrId;
	}

	public String getApplPymtMethodId() {
		return applPymtMethodId;
	}

	public void setApplPymtMethodId(String applPymtMethodId) {
		this.applPymtMethodId = applPymtMethodId;
	}

	public int getVersionNo() {
		return versionNo;
	}

	public void setVersionNo(int versionNo) {
		this.versionNo = versionNo;
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

	public Timestamp getPymtRevStartDt() {
		return pymtRevStartDt;
	}

	public void setPymtRevStartDt(Timestamp pymtRevStartDt) {
		this.pymtRevStartDt = pymtRevStartDt;
	}

	public Timestamp getPymtRevEndDt() {
		return pymtRevEndDt;
	}

	public void setPymtRevEndDt(Timestamp pymtRevEndDt) {
		this.pymtRevEndDt = pymtRevEndDt;
	}

	public Integer getEmplNbr() {
		return emplNbr;
	}

	public void setEmplNbr(Integer emplNbr) {
		this.emplNbr = emplNbr;
	}

	public Integer getPymtSchedNo() {
		return pymtSchedNo;
	}

	public void setPymtSchedNo(Integer pymtSchedNo) {
		this.pymtSchedNo = pymtSchedNo;
	}

	public Integer getPymtSchedLine() {
		return pymtSchedLine;
	}

	public void setPymtSchedLine(Integer pymtSchedLine) {
		this.pymtSchedLine = pymtSchedLine;
	}

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

	public String getDeptId() {
		return deptId;
	}

	public void setDeptId(String deptId) {
		this.deptId = deptId;
	}

	public String getFundCde() {
		return fundCde;
	}

	public void setFundCde(String fundCde) {
		this.fundCde = fundCde;
	}

	public String getClassCde() {
		return classCde;
	}

	public void setClassCde(String classCde) {
		this.classCde = classCde;
	}

	public String getAcctCde() {
		return acctCde;
	}

	public void setAcctCde(String acctCde) {
		this.acctCde = acctCde;
	}

	public String getAnalysisCde() {
		return analysisCde;
	}

	public void setAnalysisCde(String analysisCde) {
		this.analysisCde = analysisCde;
	}

	public String getBcoAprvId() {
		return bcoAprvId;
	}

	public void setBcoAprvId(String bcoAprvId) {
		this.bcoAprvId = bcoAprvId;
	}

	public String getBcoAprvName() {
		return bcoAprvName;
	}

	public void setBcoAprvName(String bcoAprvName) {
		this.bcoAprvName = bcoAprvName;
	}

	public BigDecimal getPymtLineAmt() {
		return pymtLineAmt;
	}

	public void setPymtLineAmt(BigDecimal pymtLineAmt) {
		this.pymtLineAmt = pymtLineAmt;
	}

	public BigDecimal getPymtLineAmtTot() {
		return pymtLineAmtTot;
	}

	public void setPymtLineAmtTot(BigDecimal pymtLineAmtTot) {
		this.pymtLineAmtTot = pymtLineAmtTot;
	}

	public String getBrNo() {
		return brNo;
	}

	public void setBrNo(String brNo) {
		this.brNo = brNo;
	}

	public Integer getBrLineNo() {
		return brLineNo;
	}

	public void setBrLineNo(Integer brLineNo) {
		this.brLineNo = brLineNo;
	}

	public Integer getBrDistLineNo() {
		return brDistLineNo;
	}

	public void setBrDistLineNo(Integer brDistLineNo) {
		this.brDistLineNo = brDistLineNo;
	}

	public String getBrPostInd() {
		return brPostInd;
	}

	public void setBrPostInd(String brPostInd) {
		this.brPostInd = brPostInd;
	}

	public String getSalElemnt() {
		return salElemnt;
	}

	public void setSalElemnt(String salElemnt) {
		this.salElemnt = salElemnt;
	}

	public Timestamp getPymtSubmitDttm() {
		return pymtSubmitDttm;
	}

	public void setPymtSubmitDttm(Timestamp pymtSubmitDttm) {
		this.pymtSubmitDttm = pymtSubmitDttm;
	}

	public String getPymtStatusCde() {
		return pymtStatusCde;
	}

	public void setPymtStatusCde(String pymtStatusCde) {
		this.pymtStatusCde = pymtStatusCde;
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
