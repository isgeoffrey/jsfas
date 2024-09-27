package jsfas.db.main.persistence.domain;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.UUID;

import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import jsfas.common.constants.ApplStatusConstants;
import jsfas.common.utils.GeneralUtil;

@Entity
@Table(name="EL_APPL_HDR_HIST")
public class ElApplHdrHistDAO implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -6586077593953367460L;

	@EmbeddedId
	private ElApplHdrHistDAOPK elApplHdrHistDAOPK;
	@Column(name="APPL_NBR")
	private String applNbr;
	@Column(name="APPL_USER_ID")
	private String applUserId;
	@Column(name="APPL_USER_NAME")
	private String applUserName;
	@Column(name="APPL_USER_EMPLID")
	private String applUserEmplid;
	@Column(name="APPL_USER_DEPTID")
	private String applUserDeptId;
	@Column(name="APPL_USER_JOB_CATG")
	private String applUserJobCatg;
	@Column(name="APPL_REQUESTER_ID")
	private String applRequesterId;
	@Column(name="APPL_REQUESTER_NAME")
	private String applRequesterName;
	@Column(name="APPL_REQUESTER_EMPLID")
	private String applRequesterEmplid;
	@Column(name="APPL_REQUESTER_DEPTID")
	private String applRequesterDeptid;
	@Column(name="CATEGORY_CDE")
	private String categoryCde;
	@Column(name="APPL_START_TERM")
	private String applStartTerm;
	@Column(name="APPL_END_TERM")
	private String applEndTerm;
	@Column(name="APPL_START_DT")
	private Timestamp applStartDt;
	@Column(name="APPL_END_DT")
	private Timestamp applEndDt;
	@Column(name="APPL_DTTM")
	private Timestamp applDttm;
	@Column(name="APPL_STAT_CDE")
	private String applStatCde;
	@Column(name="OW2_ATTACHED_IND")
	private Integer ow2AttachedInd ;
	@Column(name="PYMT_APRV_REQUIRED")
	private Integer pymtAprvRequired ;
	
	@Column(name="BR_NO")
	private String brNo;
	@Column(name="BR_POST_IND")
	private String brPostInd;
	@Column(name="PYMT_POST_IND")
	private String pymtPostInd;
	@Column(name="REVISED_STATUS")
	private String revisedStatus;
	
	@Column(name="USER_DECL_1")
	private Integer userDecl_1;
	@Column(name="USER_DECL_2")
	private Integer userDecl_2;
	
	@Column(name="USER_DECL_2_FROM_DT")
	private Timestamp userDecl2FromDt;
	@Column(name="USER_DECL_2_TO_DT")
	private Timestamp userDecl2ToDt;
	
	@Column(name="HOD_DECL_1_1")
	private Integer hodDecl_1_1;
	@Column(name="HOD_DECL_1_2")
	private Integer hodDecl_1_2;
	
	@Column(name="PAYROLL_DESCR")
	private String payrollDescr;
	
	@Column(name="OBSOLETE")
	private Integer obsolete;
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
	
	public ElApplHdrHistDAO(ElApplHdrDAO originDAO) {
		super();
		this.setElApplHdrHistDAOPK(new ElApplHdrHistDAOPK(originDAO.getId(), originDAO.getVersionNo()));
		this.applNbr = originDAO.getApplNbr();
		this.applUserId = originDAO.getApplUserId();
		this.applUserName = originDAO.getApplUserName();
		this.applUserEmplid = originDAO.getApplUserEmplid();
		this.applUserDeptId = originDAO.getApplUserDeptId();
		this.applUserJobCatg = originDAO.getApplUserJobCatg();
		this.applRequesterId = originDAO.getApplRequesterId();
		this.applRequesterName = originDAO.getApplRequesterName();
		this.applRequesterEmplid = originDAO.getApplRequesterEmplid();
		this.applRequesterDeptid = originDAO.getApplRequesterDeptid();
		this.categoryCde = originDAO.getCategoryCde();
		this.applStartTerm = originDAO.getApplStartTerm();
		this.applEndTerm = originDAO.getApplEndTerm();
		this.applStartDt = originDAO.getApplStartDt();
		this.applEndDt = originDAO.getApplEndDt();
		this.applDttm = originDAO.getApplDttm();
		this.applStatCde = originDAO.getApplStatCde();
		this.ow2AttachedInd = originDAO.getOw2AttachedInd();
		this.pymtAprvRequired = originDAO.getPymtAprvRequired();
		this.brNo = originDAO.getBrNo();
		this.brPostInd = originDAO.getBrPostInd();
		this.pymtPostInd = originDAO.getPymtPostInd();
		this.revisedStatus = originDAO.getRevisedStatus();
		this.obsolete = originDAO.getObsolete();
		
		this.userDecl_1 = originDAO.getUserDecl_1();
		this.userDecl_2 = originDAO.getUserDecl_2();
		this.userDecl2FromDt = originDAO.getUserDecl2FromDt();
		this.userDecl2ToDt = originDAO.getUserDecl2ToDt();
		
		this.hodDecl_1_1 = originDAO.getHodDecl_1_1();
		this.hodDecl_1_2 = originDAO.getHodDecl_1_2();
		this.payrollDescr = originDAO.getPayrollDescr();
		
		this.modCtrlTxt = GeneralUtil.genModCtrlTxt();
		this.creatDat = GeneralUtil.getCurrentTimestamp();
		this.creatUser = " ";
		this.chngDat = GeneralUtil.getCurrentTimestamp();
		this.chngUser = " ";
		this.opPageNam = " ";
	}

	public ElApplHdrHistDAO() {
		super();
		this.setElApplHdrHistDAOPK(new ElApplHdrHistDAOPK(UUID.randomUUID().toString(), 0));
		this.applNbr = " ";
		this.applUserId = " ";
		this.applUserName = " ";
		this.applUserEmplid = " ";
		this.applUserDeptId = " ";
		this.applUserJobCatg = " ";
		this.applRequesterId = " ";
		this.applRequesterName = " ";
		this.applRequesterEmplid = " ";
		this.applRequesterDeptid = " ";
		this.categoryCde = " ";
		this.applStartTerm = " ";
		this.applEndTerm = " ";
		this.applStartDt = GeneralUtil.NULLTIMESTAMP;
		this.applEndDt = GeneralUtil.NULLTIMESTAMP;
		this.applDttm = GeneralUtil.NULLTIMESTAMP;
		this.applStatCde = ApplStatusConstants.DRAFT;
		this.ow2AttachedInd = 0;
		this.pymtAprvRequired = 1;
		this.brNo = " ";
		this.brPostInd = "N";
		this.pymtPostInd = "N";
		this.revisedStatus = "R";
		this.obsolete = 0;
		
		this.userDecl_1 = 0;
		this.userDecl_2 = 0;
		this.userDecl2FromDt = GeneralUtil.NULLTIMESTAMP;
		this.userDecl2ToDt = GeneralUtil.NULLTIMESTAMP;
		
		this.hodDecl_1_1 = 0;
		this.hodDecl_1_2 = 0;
		
		this.payrollDescr = " ";
		
		this.modCtrlTxt = GeneralUtil.genModCtrlTxt();
		this.creatDat = GeneralUtil.getCurrentTimestamp();
		this.creatUser = " ";
		this.chngDat = GeneralUtil.getCurrentTimestamp();
		this.chngUser = " ";
		this.opPageNam = " ";
	}
	public ElApplHdrHistDAOPK getElApplHdrHistDAOPK() {
		return elApplHdrHistDAOPK;
	}
	public void setElApplHdrHistDAOPK(ElApplHdrHistDAOPK elApplHdrHistDAOPK) {
		this.elApplHdrHistDAOPK = elApplHdrHistDAOPK;
	}
	public String getApplNbr() {
		return applNbr;
	}
	public void setApplNbr(String applNbr) {
		this.applNbr = applNbr;
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
	public String getApplUserEmplid() {
		return applUserEmplid;
	}
	public void setApplUserEmplid(String applUserEmplid) {
		this.applUserEmplid = applUserEmplid;
	}
	public String getApplUserDeptId() {
		return applUserDeptId;
	}
	public void setApplUserDeptId(String applUserDeptId) {
		this.applUserDeptId = applUserDeptId;
	}
	public String getApplUserJobCatg() {
		return applUserJobCatg;
	}
	public void setApplUserJobCatg(String applUserJobCatg) {
		this.applUserJobCatg = applUserJobCatg;
	}
	public String getApplRequesterId() {
		return applRequesterId;
	}
	public void setApplRequesterId(String applRequesterId) {
		this.applRequesterId = applRequesterId;
	}
	public String getApplRequesterName() {
		return applRequesterName;
	}
	public void setApplRequesterName(String applRequesterName) {
		this.applRequesterName = applRequesterName;
	}
	public String getApplRequesterEmplid() {
		return applRequesterEmplid;
	}
	public void setApplRequesterEmplid(String applRequesterEmplid) {
		this.applRequesterEmplid = applRequesterEmplid;
	}
	public String getApplRequesterDeptid() {
		return applRequesterDeptid;
	}
	public void setApplRequesterDeptid(String applRequesterDeptid) {
		this.applRequesterDeptid = applRequesterDeptid;
	}
	public String getCategoryCde() {
		return categoryCde;
	}
	public void setCategoryCde(String categoryCde) {
		this.categoryCde = categoryCde;
	}
	public String getApplStartTerm() {
		return applStartTerm;
	}
	public void setApplStartTerm(String applTerm) {
		this.applStartTerm = applTerm;
	}
	
	public String getApplEndTerm() {
		return applEndTerm;
	}

	public void setApplEndTerm(String applEndTerm) {
		this.applEndTerm = applEndTerm;
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
	public Integer getOw2AttachedInd() {
		return ow2AttachedInd;
	}
	public void setOw2AttachedInd(Integer ow2AttachedInd) {
		this.ow2AttachedInd = ow2AttachedInd;
	}
	public Integer getPymtAprvRequired() {
		return pymtAprvRequired;
	}
	public void setPymtAprvRequired(Integer pymtAprvRequired) {
		this.pymtAprvRequired = pymtAprvRequired;
	}
	public String getBrNo() {
		return brNo;
	}
	public void setBrNo(String brNo) {
		this.brNo = brNo;
	}
	public String getBrPostInd() {
		return brPostInd;
	}
	public void setBrPostInd(String brPostInd) {
		this.brPostInd = brPostInd;
	}
	public String getPymtPostInd() {
		return pymtPostInd;
	}
	public void setPymtPostInd(String pymtPostInd) {
		this.pymtPostInd = pymtPostInd;
	}
	public String getRevisedStatus() {
		return revisedStatus;
	}
	public void setRevisedStatus(String revisedStatus) {
		this.revisedStatus = revisedStatus;
	}
	public Integer getObsolete() {
		return obsolete;
	}
	public void setObsolete(Integer obsolete) {
		this.obsolete = obsolete;
	}
	
	public Integer getUserDecl_1() {
		return userDecl_1;
	}

	public void setUserDecl_1(Integer userDecl_1) {
		this.userDecl_1 = userDecl_1;
	}

	public Integer getUserDecl_2() {
		return userDecl_2;
	}

	public void setUserDecl_2(Integer userDecl_2) {
		this.userDecl_2 = userDecl_2;
	}

	public Timestamp getUserDecl2FromDt() {
		return userDecl2FromDt;
	}

	public void setUserDecl2FromDt(Timestamp userDecl2FromDt) {
		this.userDecl2FromDt = userDecl2FromDt;
	}

	public Timestamp getUserDecl2ToDt() {
		return userDecl2ToDt;
	}

	public void setUserDecl2ToDt(Timestamp userDecl2ToDt) {
		this.userDecl2ToDt = userDecl2ToDt;
	}

	public Integer getHodDecl_1_1() {
		return hodDecl_1_1;
	}

	public void setHodDecl_1_1(Integer hodDecl_1_1) {
		this.hodDecl_1_1 = hodDecl_1_1;
	}

	public Integer getHodDecl_1_2() {
		return hodDecl_1_2;
	}

	public void setHodDecl_1_2(Integer hodDecl_1_2) {
		this.hodDecl_1_2 = hodDecl_1_2;
	}

	public String getPayrollDescr() {
		return payrollDescr;
	}

	public void setPayrollDescr(String payrollDescr) {
		this.payrollDescr = payrollDescr;
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
