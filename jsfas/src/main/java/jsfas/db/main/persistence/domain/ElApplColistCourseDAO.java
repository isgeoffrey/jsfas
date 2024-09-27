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
@Table(name="EL_APPL_COLIST_COURSE")
public class ElApplColistCourseDAO implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 7538361737182085253L;
	
	@Id
	@Column(name="ID")
	private String id;
	@Column(name="APPL_HDR_ID")
	private String applHdrId;
	@Column(name="APPL_CRSE_ID")
	private String applCrseId;
	@Column(name="COLIST_CRSE_TERM")
	private String colistCrseTerm;
	@Column(name="COLIST_CRSE_ID")
	private String colistCrseId;
	@Column(name="COLIST_CRSE_OFFER_NBR")
	private String colistCrseOfferNbr;
	@Column(name="COLIST_CRSE_CDE")
	private String colistCrseCde;
	@Column(name="SECTION")
	private String section;
	@Column(name="SESSION_CODE")
	private String sectionCode ;
	@Column(name="CREDIT")
	private BigDecimal credit;
	@Column(name="CRSE_CO_TAUGHT_HR")
	private BigDecimal crseCoTaughtHr;
	@Column(name="CRSE_TOTAL_HR")
	private BigDecimal crseTotalHr;
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
	
	public ElApplColistCourseDAO() {
		super();
		this.id = UUID.randomUUID().toString();
		this.applHdrId = " ";
		this.applCrseId = " ";
		this.colistCrseTerm = " ";
		this.colistCrseId = " ";
		this.colistCrseOfferNbr = " ";
		this.colistCrseCde = " ";
		this.section = " ";
		this.sectionCode = " ";
		this.credit = BigDecimal.ZERO;
		this.crseCoTaughtHr = BigDecimal.ZERO;
		this.crseTotalHr = BigDecimal.ZERO;
		
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
	public String getColistCrseTerm() {
		return colistCrseTerm;
	}
	public void setColistCrseTerm(String colistCrseTerm) {
		this.colistCrseTerm = colistCrseTerm;
	}
	public String getApplCrseId() {
		return applCrseId;
	}
	public void setApplCrseId(String applCrseId) {
		this.applCrseId = applCrseId;
	}
	public String getColistCrseId() {
		return colistCrseId;
	}
	public void setColistCrseId(String colistCrseId) {
		this.colistCrseId = colistCrseId;
	}
	public String getColistCrseOfferNbr() {
		return colistCrseOfferNbr;
	}
	public void setColistCrseOfferNbr(String colistCrseOfferNbr) {
		this.colistCrseOfferNbr = colistCrseOfferNbr;
	}
	public String getColistCrseCde() {
		return colistCrseCde;
	}
	public void setColistCrseCde(String colistCrseCde) {
		this.colistCrseCde = colistCrseCde;
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
