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
@Table(name="EL_APPL_LOA")
public class ElApplLoaDAO implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -1874649554288844470L;
	
	@Id
	@Column(name="ID")
	private String id;
	@Column(name="APPL_HDR_ID")
	private String applHdrId;
	@Column(name="FILE_CATEGORY")
	private String fileCategory;
	@Column(name="FILE_ID")
	private String fileId;
	@Column(name="ACCEPTED")
	private Integer accepted;
	@Column(name="ACCEPTANCE_DTTM")
	private Timestamp acceptanceDttm;
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

	public ElApplLoaDAO() {
		super();
		this.id = UUID.randomUUID().toString();
		this.applHdrId = " ";
		this.fileCategory = " ";
		this.fileId = " ";
		this.accepted = -1;
		this.acceptanceDttm = GeneralUtil.initNullTimestamp(null);
		
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
	public String getFileCategory() {
		return fileCategory;
	}
	public void setFileCategory(String fileCategory) {
		this.fileCategory = fileCategory;
	}
	public String getFileId() {
		return fileId;
	}
	public void setFileId(String fileId) {
		this.fileId = fileId;
	}
	public Integer getAccepted() {
		return accepted;
	}
	public void setAccepted(Integer accepted) {
		this.accepted = accepted;
	}
	public Timestamp getAcceptanceDttm() {
		return acceptanceDttm;
	}
	public void setAcceptanceDttm(Timestamp acceptanceDttm) {
		this.acceptanceDttm = acceptanceDttm;
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
