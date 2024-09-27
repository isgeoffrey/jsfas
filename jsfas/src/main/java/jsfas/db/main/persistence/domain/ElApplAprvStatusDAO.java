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
@Table(name="EL_APPL_APRV_STATUS")
public class ElApplAprvStatusDAO implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -3987170877214362235L;
	
	@Id
	@Column(name="ID")
	private String id;
	@Column(name="APPL_HDR_ID")
	private String applHdrId;
	@Column(name="ARPV_SEQ")
	private Integer arpvSeq;
	@Column(name="APRV_TYPE_CDE")
	private String aprvTypeCde;
	@Column(name="APRV_USER_ID")
	private String aprvUserId;
	@Column(name="APRV_USER_NAME")
	private String aprvUserName;
	@Column(name="APPROVED")
	private Integer approved;
	@Column(name="APRV_DTTM")
	private Timestamp aprvDttm;
	@Column(name="APRV_REMARK")
	private String aprvRemark;
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
	
	public ElApplAprvStatusDAO() {
		super();
		this.id = UUID.randomUUID().toString();
		this.applHdrId = " ";
		this.arpvSeq = null;
		this.aprvTypeCde = " ";
		this.aprvUserId = " ";
		this.aprvUserName = " ";
		this.approved = -1;
		this.aprvDttm = GeneralUtil.NULLTIMESTAMP;
		this.aprvRemark = " ";
		
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
	public Integer getArpvSeq() {
		return arpvSeq;
	}
	public void setArpvSeq(Integer arpvSeq) {
		this.arpvSeq = arpvSeq;
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
	public String getAprvRemark() {
		return aprvRemark;
	}
	public void setAprvRemark(String aprvRemark) {
		this.aprvRemark = aprvRemark;
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
