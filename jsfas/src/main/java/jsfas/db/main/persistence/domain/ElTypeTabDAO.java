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
@Table(name="EL_TYPE_TAB")
public class ElTypeTabDAO implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 5203346626510674921L;
	
	@Id
	@Column(name="ID")
	private String id;
	@Column(name="EL_TYPE_NAM")
	private String elTypeNam;
	@Column(name="EL_TYPE_DESCR")
	private String elTypeDescr;
	@Column(name="CATEGORY_CDE")
	private String categoryCde;
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
	
	public ElTypeTabDAO() {
		super();
		this.id = UUID.randomUUID().toString();
		this.elTypeNam = " ";
		this.elTypeDescr = " ";
		this.categoryCde = " ";
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
	public String getElTypeNam() {
		return elTypeNam;
	}
	public void setElTypeNam(String elTypeNam) {
		this.elTypeNam = elTypeNam;
	}
	public String getElTypeDescr() {
		return elTypeDescr;
	}
	public void setElTypeDescr(String elTypeDescr) {
		this.elTypeDescr = elTypeDescr;
	}
	public String getCategoryCde() {
		return categoryCde;
	}
	public void setCategoryCde(String categoryCde) {
		this.categoryCde = categoryCde;
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
