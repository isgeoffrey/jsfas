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
@Table(name="EL_APPL_COURSE_HIST")
public class ElApplCourseHistDAO implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -7323251410697108801L;

	@Id
	@Column(name="ID")
	private String id;
	@Column(name="APPL_HDR_ID")
	private String applHdrId;
	@Column(name="CRSE_TERM")
	private String crseTerm;
	@Column(name="CRSE_ID")
	private String crseId;
	@Column(name="CRSE_OFFER_NBR")
	private String crseOfferNbr;
	@Column(name="CRSE_CDE")
	private String crseCde ;
	@Column(name="CRSE_DESCR")
	private String crseDescr ;
	@Column(name="SECTION ")
	private String section ;
	@Column(name="SESSION_CODE")
	private String sectionCode ;
	@Column(name="CREDIT")
	private BigDecimal credit;
	@Column(name="CRSE_CO_TAUGHT_HR")
	private BigDecimal crseCoTaughtHr;
	@Column(name="CRSE_TOTAL_HR")
	private BigDecimal crseTotalHr;
	@Column(name="CRSE_REMARK")
	private String crseRemark;
	@Column(name="VERSION_NO")
	private Integer versionNo;
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
	public ElApplCourseHistDAO(ElApplCourseDAO originDAO, ElApplHdrDAO hdrDAO) {
		super();
		this.id = UUID.randomUUID().toString();
		this.applHdrId = originDAO.getApplHdrId();
		this.crseTerm = originDAO.getCrseTerm();
		this.crseId = originDAO.getCrseId();
		this.crseOfferNbr = originDAO.getCrseOfferNbr();
		this.crseCde = originDAO.getCrseCde();
		this.crseDescr = originDAO.getCrseDescr();
		this.section = originDAO.getSection();
		this.sectionCode = originDAO.getSectionCode();
		this.credit = originDAO.getCredit();
		this.crseCoTaughtHr = originDAO.getCrseCoTaughtHr();
		this.crseTotalHr = originDAO.getCrseTotalHr();
		this.crseRemark = originDAO.getCrseRemark();
		
		this.versionNo = hdrDAO.getVersionNo();
		
		this.modCtrlTxt = GeneralUtil.genModCtrlTxt();
		this.creatDat = GeneralUtil.getCurrentTimestamp();
		this.creatUser = " ";
		this.chngDat = GeneralUtil.getCurrentTimestamp();
		this.chngUser = " ";
		this.opPageNam = " ";
	}
	
	public ElApplCourseHistDAO() {
		super();
		this.id = UUID.randomUUID().toString();
		this.applHdrId = " ";
		this.crseTerm = " ";
		this.crseId = " ";
		this.crseOfferNbr = " ";
		this.crseCde = " ";
		this.crseDescr = " ";
		this.section = " ";
		this.sectionCode = " ";
		this.credit = BigDecimal.ZERO;
		this.crseCoTaughtHr = BigDecimal.ZERO;
		this.crseTotalHr = BigDecimal.ZERO;
		this.crseRemark = " ";
		this.versionNo = 0;
		
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
	public String getCrseTerm() {
		return crseTerm;
	}
	public void setCrseTerm(String crseTerm) {
		this.crseTerm = crseTerm;
	}
	public String getCrseId() {
		return crseId;
	}
	public void setCrseId(String crseId) {
		this.crseId = crseId;
	}
	public String getCrseOfferNbr() {
		return crseOfferNbr;
	}
	public void setCrseOfferNbr(String crseOfferNbr) {
		this.crseOfferNbr = crseOfferNbr;
	}
	public String getCrseCde() {
		return crseCde;
	}
	public void setCrseCde(String crseCde) {
		this.crseCde = crseCde;
	}
	public String getCrseDescr() {
		return crseDescr;
	}
	public void setCrseDescr(String crseDescr) {
		this.crseDescr = crseDescr;
	}
	public String getSection() {
		return section;
	}
	public void setSection(String section) {
		this.section = section;
	}
	public String getSectionCode() {
		return sectionCode;
	}
	public void setSectionCode(String sectionCode) {
		this.sectionCode = sectionCode;
	}
	public BigDecimal getCredit() {
		return credit;
	}
	public void setCredit(BigDecimal credit) {
		this.credit = credit;
	}
	public BigDecimal getCrseCoTaughtHr() {
		return crseCoTaughtHr;
	}
	public void setCrseCoTaughtHr(BigDecimal crseCoTaughtHr) {
		this.crseCoTaughtHr = crseCoTaughtHr;
	}
	public BigDecimal getCrseTotalHr() {
		return crseTotalHr;
	}
	public void setCrseTotalHr(BigDecimal crseTotalHr) {
		this.crseTotalHr = crseTotalHr;
	}
	public String getCrseRemark() {
		return crseRemark;
	}
	public void setCrseRemark(String crseRemark) {
		this.crseRemark = crseRemark;
	}
	public Integer getVersionNo() {
		return versionNo;
	}
	public void setVersionNo(Integer versionNo) {
		this.versionNo = versionNo;
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
