package jsfas.db.main.persistence.domain;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.UUID;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import jsfas.common.utils.GeneralUtil;

@Entity
@Table(name="EL_APPL_PRGM_HIST")
public class ElApplPrgmHistDAO implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -3685009226585366516L;

	@Id
	@Column(name="ID")
	private String id;
	@Column(name="APPL_HDR_ID")
	private String applHdrId;
//	@Column(name="PRGM_TERM")
//	private String prgmTerm;
//	@Column(name="PRGM_START_DTTM")
//	private Timestamp prgmStartDttm;
//	@Column(name="PRGM_END_DTTM")
//	private Timestamp prgmEndDttm;
	@Column(name="PRGM_CDE ")
	private String prgmCde ;
	@Column(name="SCH_CDE")
	private String schCde;
	@Column(name="DEPT")
	private String dept;
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
	
	public ElApplPrgmHistDAO(ElApplPrgmDAO originDAO, ElApplHdrDAO hdrDAO) {
		super();
		this.id = UUID.randomUUID().toString();
		this.applHdrId = originDAO.getApplHdrId();
//		this.prgmTerm = " ";
//		this.prgmStartDttm = GeneralUtil.NULLTIMESTAMP;
//		this.prgmEndDttm = GeneralUtil.NULLTIMESTAMP;
		this.prgmCde = originDAO.getPrgmCde();
		this.schCde = originDAO.getSchCde();
		this.dept = originDAO.getDept();
		
		this.versionNo = hdrDAO.getVersionNo();
		
		this.modCtrlTxt = GeneralUtil.genModCtrlTxt();
		this.creatDat = GeneralUtil.getCurrentTimestamp();
		this.creatUser = " ";
		this.chngDat = GeneralUtil.getCurrentTimestamp();
		this.chngUser = " ";
		this.opPageNam = " ";
	}
	
	public ElApplPrgmHistDAO() {
		super();
		this.id = UUID.randomUUID().toString();
		this.applHdrId = " ";
//		this.prgmTerm = " ";
//		this.prgmStartDttm = GeneralUtil.NULLTIMESTAMP;
//		this.prgmEndDttm = GeneralUtil.NULLTIMESTAMP;
		this.prgmCde = " ";
		this.schCde = " ";
		this.dept = " ";
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
//	public String getPrgmTerm() {
//		return prgmTerm;
//	}
//	public void setPrgmTerm(String prgmTerm) {
//		this.prgmTerm = prgmTerm;
//	}
//	public Timestamp getPrgmStartDttm() {
//		return prgmStartDttm;
//	}
//	public void setPrgmStartDttm(Timestamp prgmStartDttm) {
//		this.prgmStartDttm = prgmStartDttm;
//	}
//	public Timestamp getPrgmEndDttm() {
//		return prgmEndDttm;
//	}
//	public void setPrgmEndDttm(Timestamp prgmEndDttm) {
//		this.prgmEndDttm = prgmEndDttm;
//	}
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
