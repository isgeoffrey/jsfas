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
@Table(name="EL_TYPE_SAL_ELEMNT_TAB")
public class ElTypeSalElemntTabDAO implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1699417168780449968L;

	@Id
	@Column(name="ID")
	private String id;
	@Column(name="EL_TYPE_ID")
	private String elTypeId;
	@Column(name="PYMT_TYPE_CDE")
	private String pymtTypeCde;
	@Column(name="SAL_ELEMNT")
	private String salElemnt;
	@Column(name="SAL_ELEMNT_OW2")
	private String salElemntOw2;
	@Column(name="EFFECTIVE_DT")
	private Timestamp effectiveDt;
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
	
	public ElTypeSalElemntTabDAO() {
		super();
		this.id = UUID.randomUUID().toString();
		this.elTypeId = " ";
		this.pymtTypeCde = " ";
		this.salElemnt = " ";
		this.salElemntOw2 = " ";
		this.effectiveDt = GeneralUtil.NULLTIMESTAMP;
		this.obsolete = 0;
		
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
	public String getElTypeId() {
		return elTypeId;
	}
	public void setElTypeId(String elTypeId) {
		this.elTypeId = elTypeId;
	}
	public String getPymtTypeCde() {
		return pymtTypeCde;
	}
	public void setPymtTypeCde(String pymtTypeCde) {
		this.pymtTypeCde = pymtTypeCde;
	}
	public String getSalElemnt() {
		return salElemnt;
	}
	public void setSalElemnt(String salElemnt) {
		this.salElemnt = salElemnt;
	}
	public String getSalElemntOw2() {
		return salElemntOw2;
	}
	public void setSalElemntOw2(String salElemntOw2) {
		this.salElemntOw2 = salElemntOw2;
	}
	public Timestamp getEffectiveDt() {
		return effectiveDt;
	}
	public void setEffectiveDt(Timestamp effectiveDt) {
		this.effectiveDt = effectiveDt;
	}
	public Integer getObsolete() {
		return obsolete;
	}
	public void setObsolete(Integer obsolete) {
		this.obsolete = obsolete;
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
