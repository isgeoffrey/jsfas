package jsfas.db.main.persistence.domain;

import java.io.Serializable;
import java.sql.Timestamp;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "FAS_MSG_TAB")
public class MessageTableDAO implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 7014784243661906645L;

	@Id
	@Column(name="MSG_CDE")
	private String messageCode;
	
	@Column(name="MSG_TXT")
	private String messageText;
	
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

	public String getMessageCode() {
		return messageCode;
	}

	public String getMessageText() {
		return messageText;
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

	public String getOpPageName() {
		return opPageName;
	}

	public void setMessageCode(String messageCode) {
		this.messageCode = messageCode;
	}

	public void setMessageText(String messageText) {
		this.messageText = messageText;
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

	public void setOpPageName(String opPageName) {
		this.opPageName = opPageName;
	}
	
}
