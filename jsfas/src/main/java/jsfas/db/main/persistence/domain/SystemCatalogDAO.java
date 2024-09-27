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
import javax.persistence.JoinColumn;
import javax.persistence.JoinColumns;
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonManagedReference;

@Entity
@Table(name = "FAS_SYS_CATG_TAB")
public class SystemCatalogDAO implements Serializable, Comparable<SystemCatalogDAO> {
	/**
	 * 
	 */
	private static final long serialVersionUID = -5020382247505149883L;

	@Id
	@Column(name = "SYS_CATG_CDE")
	private String systemCatalogCode;
	
	@Column(name = "SYS_CATG_SEQ_NBR")
	private Integer systemCatalogSeq;
	
	@Column(name = "SYS_CATG_SHORT_DESC")
	private String systemCatalogShortDesc;
	
	@Column(name = "SYS_CATG_LONG_DESC")
	private String systemCatalogLongDesc;
		
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

	@OneToMany(fetch = FetchType.LAZY, cascade=CascadeType.ALL)
	@OrderBy("SYS_CATG_SEQ_NBR")
	@JoinColumns({
		@JoinColumn(name="SYS_CATG_CDE", referencedColumnName="SYS_CATG_CDE"),
	})
	private Set<FunctionCatalogDAO> functionCatalogs = new LinkedHashSet<FunctionCatalogDAO>();
	
	public String getSystemCatalogCode() {
		return systemCatalogCode;
	}

	public Integer getSystemCatalogSeq() {
		return systemCatalogSeq;
	}

	public String getSystemCatalogShortDesc() {
		return systemCatalogShortDesc;
	}

	public String getSystemCatalogLongDesc() {
		return systemCatalogLongDesc;
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

	public String getOpPageName() {
		return opPageName;
	}

	@JsonManagedReference
	public Set<FunctionCatalogDAO> getFunctionCatalogs() {
		return functionCatalogs;
	}
	
	public void setSystemCatalogCode(String systemCatalogCode) {
		this.systemCatalogCode = systemCatalogCode;
	}

	public void setSystemCatalogSeq(Integer systemCatalogSeq) {
		this.systemCatalogSeq = systemCatalogSeq;
	}

	public void setSystemCatalogShortDesc(String systemCatalogShortDesc) {
		this.systemCatalogShortDesc = systemCatalogShortDesc;
	}

	public void setSystemCatalogLongDesc(String systemCatalogLongDesc) {
		this.systemCatalogLongDesc = systemCatalogLongDesc;
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

	public void setOpPageName(String opPageName) {
		this.opPageName = opPageName;
	}

	public void setFunctionCatalogs(Set<FunctionCatalogDAO> functionCatalogs) {
		this.functionCatalogs = functionCatalogs;
	}
	
	@Override
	public int compareTo(SystemCatalogDAO o) {
		return (this.getSystemCatalogSeq()).compareTo(o.getSystemCatalogSeq());
	}
	
}
