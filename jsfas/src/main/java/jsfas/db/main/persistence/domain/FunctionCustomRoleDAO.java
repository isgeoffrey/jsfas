package jsfas.db.main.persistence.domain;

import java.io.Serializable;
import java.sql.Timestamp;

import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;

import javax.persistence.Table;

@Entity
@Table(name = "FAS_FUNC_CUSTROLE_TAB")
public class FunctionCustomRoleDAO implements Serializable, Comparable<FunctionCustomRoleDAO> {

	/**
	 * 
	 */
	private static final long serialVersionUID = -4474701687351273272L;
	
	@EmbeddedId
	private FunctionCustomRoleDAOPK functionCustomRoleDAOPK;
	
	@Column(name = "ZR_ISCNTRLLER")
	private String isController;
	
	@Column(name = "ZR_ISCOORDTR")
	private String isCoordinator;
	
	@Column(name = "ZR_ISUSER")
	private String isUser;

	@Column(name = "ZR_ISCOORDTR_ENDOR")
	private String isCoordinatorEndorser;
	
	@Column(name = "ZR_ISUSER_EDIT")
	private String isUserEditable;
	
	@Column(name = "ZR_FO_SUPERUSER")
	private String isFoSuperuser;
	
	@Column(name = "MOD_CTRL_TXT")
	private String modCtrlTxt;
	
	@Column(name = "CREAT_USER")
	private String createUser;
	
	@Column(name = "CREAT_DAT")
	private Timestamp createDate;
	
	@Column(name = "CHNG_USER")
	private String changeUser;
	
	@Column(name = "CHNG_DAT")
	private Timestamp changeDate;
	
	@Column(name = "OP_PAGE_NAM")
	private String opPageName;
	
	
	public FunctionCustomRoleDAOPK getFunctionCustomRoleDAOPK() {
		return functionCustomRoleDAOPK;
	}


	public void setFunctionCustomRoleDAOPK(FunctionCustomRoleDAOPK functionCustomRoleDAOPK) {
		this.functionCustomRoleDAOPK = functionCustomRoleDAOPK;
	}


	public String getIsController() {
		return isController;
	}


	public void setIsController(String isController) {
		this.isController = isController;
	}


	public String getIsCoordinator() {
		return isCoordinator;
	}


	public void setIsCoordinator(String isCoordinator) {
		this.isCoordinator = isCoordinator;
	}


	public String getIsUser() {
		return isUser;
	}


	public void setIsUser(String isUser) {
		this.isUser = isUser;
	}


	public String getIsCoordinatorEndorser() {
		return isCoordinatorEndorser;
	}


	public void setIsCoordinatorEndorser(String isCoordinatorEndorser) {
		this.isCoordinatorEndorser = isCoordinatorEndorser;
	}


	public String getIsUserEditable() {
		return isUserEditable;
	}


	public void setIsUserEditable(String isUserEditable) {
		this.isUserEditable = isUserEditable;
	}


	public String getIsFoSuperuser() {
		return isFoSuperuser;
	}


	public void setIsFoSuperuser(String isFoSuperuser) {
		this.isFoSuperuser = isFoSuperuser;
	}


	public String getModCtrlTxt() {
		return modCtrlTxt;
	}


	public void setModCtrlTxt(String modCtrlTxt) {
		this.modCtrlTxt = modCtrlTxt;
	}


	public String getCreateUser() {
		return createUser;
	}


	public void setCreateUser(String createUser) {
		this.createUser = createUser;
	}


	public Timestamp getCreateDate() {
		return createDate;
	}


	public void setCreateDate(Timestamp createDate) {
		this.createDate = createDate;
	}


	public String getChangeUser() {
		return changeUser;
	}


	public void setChangeUser(String changeUser) {
		this.changeUser = changeUser;
	}


	public Timestamp getChangeDate() {
		return changeDate;
	}


	public void setChangeDate(Timestamp changeDate) {
		this.changeDate = changeDate;
	}


	public String getOpPageName() {
		return opPageName;
	}


	public void setOpPageName(String opPageName) {
		this.opPageName = opPageName;
	}

	@Override
	public int compareTo(FunctionCustomRoleDAO o) {
		return (Integer.valueOf(this.functionCustomRoleDAOPK.hashCode()).compareTo(Integer.valueOf(o.getFunctionCustomRoleDAOPK().hashCode())));
	}
}
