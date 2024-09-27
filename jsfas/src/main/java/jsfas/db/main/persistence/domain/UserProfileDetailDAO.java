package jsfas.db.main.persistence.domain;

import java.io.Serializable;
import java.sql.Timestamp;

import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table(name = "EL_USER_PROFILE_DTL")
public class UserProfileDetailDAO implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 3811006512265593626L;
	
	@EmbeddedId
    private UserProfileDetailDAOPK userProfileDetailPK;

	@Column(name="ACCESS_DATA_FILTER_1_STR")
	private String dataFilter1String;
	
	@Column(name="ACCESS_DATA_FILTER_2_STR")
	private String dataFilter2String;
	
	@Column(name="ACCESS_DATA_FILTER_3_STR")
	private String dataFilter3String;
	
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

	public UserProfileDetailDAOPK getUserProfileDetailPK() {
		return userProfileDetailPK;
	}

	public String getDataFilter1String() {
		return dataFilter1String;
	}

	public String getDataFilter2String() {
		return dataFilter2String;
	}

	public String getDataFilter3String() {
		return dataFilter3String;
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


	public void setUserProfileDetailPK(UserProfileDetailDAOPK userProfileDetailPK) {
		this.userProfileDetailPK = userProfileDetailPK;
	}

	public void setDataFilter1String(String dataFilter1String) {
		this.dataFilter1String = dataFilter1String;
	}

	public void setDataFilter2String(String dataFilter2String) {
		this.dataFilter2String = dataFilter2String;
	}

	public void setDataFilter3String(String dataFilter3String) {
		this.dataFilter3String = dataFilter3String;
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
	 * @param functionId the functionId to set
	 */
	public void setOpPageName(String opPageName) {
		this.opPageName = opPageName;
	}
	
	
	
}
