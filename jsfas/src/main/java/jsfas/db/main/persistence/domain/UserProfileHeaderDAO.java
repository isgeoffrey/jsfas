package jsfas.db.main.persistence.domain;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.LinkedHashSet;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.NamedStoredProcedureQueries;
import javax.persistence.NamedStoredProcedureQuery;
import javax.persistence.OneToMany;
import javax.persistence.ParameterMode;
import javax.persistence.StoredProcedureParameter;
import javax.persistence.Table;

import org.hibernate.annotations.DynamicUpdate;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import jsfas.db.rbac.persistence.domain.UserRoleGroupDAO;
import jsfas.web.serializer.CustomTimestampSerializer;

@Entity
@Table(name = "FAS_USER_PROFILE_HDR")
@DynamicUpdate
@NamedStoredProcedureQueries({
	@NamedStoredProcedureQuery(name = "set_user_nam", 
		procedureName = "SEC_INFO_PKG.SET_USER_NAM",
		parameters = {
				@StoredProcedureParameter(mode = ParameterMode.IN, name = "p_user_nam", type = String.class)
		})
})
public class UserProfileHeaderDAO implements Serializable, Comparable<UserProfileHeaderDAO> {

	/**
	 * 
	 */
	private static final long serialVersionUID = -4601574922449924915L;

	@Id
	@Column(name = "USER_NAM")
	private String userName;
	
	@OneToMany(mappedBy="userProfileDetailPK.userProfileHeader", fetch = FetchType.LAZY, cascade=CascadeType.ALL, orphanRemoval=true)
	private Set<UserProfileDetailDAO> userProfileDetails = new LinkedHashSet<UserProfileDetailDAO>();
	
	@OneToMany(mappedBy = "userRoleGroupPK.userProfileHeader", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<UserRoleGroupDAO> userRoleGroups = new LinkedHashSet<UserRoleGroupDAO>();
	
	@Column(name = "DEFLT_PRINT_QUE")
	private String defaultPrintQueue;
	
	@Column(name = "MOD_CTRL_TXT")
	private String modCtrlTxt;

	@Column(name = "CREAT_USER")
	private String createUser;
	
	@Column(name = "CREAT_DAT")
	private Timestamp createDate;
	
	@Column(name = "CHNG_USER")
	private String changeUser;
	
	@Column(name = "CHNG_DAT")
	@JsonSerialize(using=CustomTimestampSerializer.class)
	private Timestamp changeDate;
	
	@Column(name = "OP_PAGE_NAM")
	private String opPageName;

	public String getUserName() {
		return userName;
	}

	@JsonManagedReference
	public Set<UserProfileDetailDAO> getUserProfileDetails() {
		return userProfileDetails;
	}
	
	public Set<UserRoleGroupDAO> getUserRoleGroups() {
        return userRoleGroups;
    }

    public void setUserRoleGroups(Set<UserRoleGroupDAO> userRoleGroup) {
        this.userRoleGroups = userRoleGroup;
    }
	
	public String getDefaultPrintQueue() {
		return defaultPrintQueue;
	}

	public String getModCtrlTxt() {
		return modCtrlTxt;
	}

	public String getCreateUser() {
		return createUser;
	}

	public Timestamp getCreateDate() {
		return createDate;
	}

	public String getChangeUser() {
		return changeUser;
	}

	public Timestamp getChangeDate() {
		return changeDate;
	}

	/**
	 * @return the functionId
	 */
	public String getOpPageName() {
		return opPageName;
	}


	public void setUserName(String userName) {
		this.userName = userName;
	}

	public void setUserProfileDetails(
			Set<UserProfileDetailDAO> userProfileDetails) {
		this.userProfileDetails = userProfileDetails;
	}
	
	public void setDefaultPrintQueue(String defaultPrintQueue) {
		this.defaultPrintQueue = defaultPrintQueue;
	}

	public void setModCtrlTxt(String modCtrlTxt) {
		this.modCtrlTxt = modCtrlTxt;
	}

	public void setCreateUser(String createUser) {
		this.createUser = createUser;
	}

	public void setCreateDate(Timestamp createDate) {
		this.createDate = createDate;
	}

	public void setChangeUser(String changeUser) {
		this.changeUser = changeUser;
	}

	public void setChangeDate(Timestamp changeDate) {
		this.changeDate = changeDate;
	}

	/**
	 * @param opPageName the functionId to set
	 */
	public void setOpPageName(String opPageName) {
		this.opPageName = opPageName;
	}
	
	public void clearUserProfileDetails(){
		this.userProfileDetails.clear();
	}
	
	@Override
    public boolean equals(Object obj) {
        if(obj instanceof UserProfileHeaderDAO) {
            UserProfileHeaderDAO userProfileHeaderDAO = (UserProfileHeaderDAO)obj;
            return this.getUserName().equals(userProfileHeaderDAO.getUserName());
        }
        return false;
    }
	
	@Override
    public int hashCode() {
        return this.getUserName().hashCode();
    }
	
	@Override
	public int compareTo(UserProfileHeaderDAO o) {
		return (this.getUserName()).compareTo(o.getUserName());
	}
	
}
