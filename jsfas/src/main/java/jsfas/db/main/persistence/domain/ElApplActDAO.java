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
@Table(name="EL_APPL_ACT")
public class ElApplActDAO implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -4935927215508973677L;

	public static final String SUBMIT = "submit";
	public static final String REMOVE = "remove";
	public static final String CLOSE = "close";
	public static final String APPROVE = "approve";
	public static final String RETURN = "return";
	public static final String DRAFT = "draft";
	
	// for applicant declaration
	public static final String ACCEPT = "accept";
	public static final String REJECT = "reject";
	

	public static final String SUBMIT_APPL = "Submit Application";
	public static final String REMOVE_APPL = "Remove";
	public static final String CLOSE_APPL = "Not valid action";
	public static final String APPROVE_APPL = "Approve";
	//public static final String RETURN_APPL = "Return"; obsolete, seems as reject
	public static final String DRAFT_APPL = "Draft";
	public static final String REJECT_APPL = "Reject";
	public static final String ACCEPT_APPL = "Accept";

	public static final String ISSUE_OFFER = "Issue Offer";
	
	public static final String SUBMIT_PYMT = "Submit Payment";
	public static final String REMOVE_PYMT = "Remove";
	public static final String CLOSE_PYMT = "Close";
	public static final String APPROVE_PYMT = "Approve";
	//public static final String RETURN_PYMT = "Return"; obsolete, seems as reject
	public static final String DRAFT_PYMT = "Not valid action";
	public static final String REJECT_PYMT = "Reject";
	public static final String ACCEPT_PYMT = "Accept";

	public static final String INTEGRATE_HRMS = "Integrate with HRMS";
	public static final String COMPLETED = "Completed";
	
	@Id
	@Column(name="ID")
	private String id;
	@Column(name="APPL_HDR_ID")
	private String applHdrId;
	@Column(name="ACTION")
	private String action;
	@Column(name="DESCR")
	private String descr;
	@Column(name="ACTION_BY")
	private String actionBy;
	@Column(name="ACTION_DTTM")
	private Timestamp actionDttm;
	@Column(name="ROLE_TYPE")
	private String roleType;
	
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
	
	public ElApplActDAO() {
		super();
		this.id = UUID.randomUUID().toString();
		this.applHdrId = " ";
		this.action = " ";
		this.descr = " ";
		this.actionBy = " ";
		this.actionDttm = GeneralUtil.NULLTIMESTAMP;
		this.roleType = " ";
		
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
	public String getAction() {
		return action;
	}
	public void setAction(String action) {
		this.action = action;
	}
	public String getDescr() {
		return descr;
	}
	public void setDescr(String descr) {
		this.descr = descr;
	}
	public String getActionBy() {
		return actionBy;
	}
	public void setActionBy(String actionBy) {
		this.actionBy = actionBy;
	}
	public Timestamp getActionDttm() {
		return actionDttm;
	}
	public void setActionDttm(Timestamp actionDttm) {
		this.actionDttm = actionDttm;
	}
	public String getRoleType() {
		return roleType;
	}

	public void setRoleType(String roleType) {
		this.roleType = roleType;
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
