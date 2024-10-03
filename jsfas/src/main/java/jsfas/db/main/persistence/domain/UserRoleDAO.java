package jsfas.db.main.persistence.domain;

import java.io.Serializable;
import java.sql.Timestamp;

import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;

import javax.persistence.Table;

@Entity
@Table(name = "FAS_PS_ZR_CUST_ROLE_V")
public class UserRoleDAO implements Serializable, Comparable<UserRoleDAO> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 4635067302952466002L;
	
	@EmbeddedId
	private UserRoleDAOPK userRoleDAOPK;
	
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
	
	private UserRoleDAOPK getUserRoleDAOPK() {
		return userRoleDAOPK;
	}
	
	public String getIsController() {
		return isController;
	}

	public String getIsCoordinator() {
		return isCoordinator;
	}

	public String getIsUser() {
		return isUser;
	}

	public String getIsCoordinatorEndorser() {
		return isCoordinatorEndorser;
	}

	public String getIsUserEditable() {
		return isUserEditable;
	}

	public String getIsFoSuperuser() {
		return isFoSuperuser;
	}
	
	@Override
	public int compareTo(UserRoleDAO o) {
		return (Integer.valueOf(this.userRoleDAOPK.hashCode()).compareTo(Integer.valueOf(o.getUserRoleDAOPK().hashCode())));
	}

}
