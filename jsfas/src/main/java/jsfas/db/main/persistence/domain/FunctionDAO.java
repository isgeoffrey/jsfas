package jsfas.db.main.persistence.domain;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.LinkedHashSet;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.JoinColumns;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;

@Entity
@Table(name = "EL_FUNC_TAB")
public class FunctionDAO implements Serializable, Comparable<FunctionDAO> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 4269561498939353932L;

	@EmbeddedId
    private FunctionDAOPK functionPK;
	
	@Column(name="FUNC_CATG_CDE")
	private String functionCatalogCode;
	
	@Column(name="FUNC_SEQ_NBR")
	private Integer functionSeq;
	
	@Column(name="FUNC_SUB_SEQ_NBR")
	private Integer functionSubSeq;
	
	@Column(name="FUNC_DESC")
	private String functionDesc;
	
	@Column(name="FIRST_PAGE_NAM")
	private String firstPageName;
	
	@Column(name="MENU_GEN_IND")
	private String menuGenInd;
	
	@Column(name="USER_PROF_GEN_IND")
	private String userProfGenInd;
	
	@Column(name="RECORD_FILTER_1_LABEL")
	private String recordFilter1Label;
	
	@Column(name="RECORD_FILTER_1_HELP")
	private String recordFilter1Help;
	
	@Column(name="RECORD_FILTER_2_LABEL")
	private String recordFilter2Label;
	
	@Column(name="RECORD_FILTER_2_HELP")
	private String recordFilter2Help;
	
	@Column(name="RECORD_FILTER_3_LABEL")
	private String recordFilter3Label;
	
	@Column(name="RECORD_FILTER_3_HELP")
	private String recordFilter3Help;
	
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
	@JoinColumns({
		@JoinColumn(name="FUNC_CDE", referencedColumnName="FUNC_CDE"),
		@JoinColumn(name="FUNC_SUB_CDE", referencedColumnName="FUNC_SUB_CDE")
	})
	private Set<FunctionPageDAO> functionPages = new LinkedHashSet<FunctionPageDAO>();
	
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumns({
		@JoinColumn(name="SYS_CATG_CDE")
	})
	private SystemCatalogDAO systemCatalog;

	public FunctionDAOPK getFunctionPK() {
		return functionPK;
	}
	
	public String getFunctionCatalogCode() {
		return functionCatalogCode;
	}

	public Integer getFunctionSeq() {
		return functionSeq;
	}

	public Integer getFunctionSubSeq() {
		return functionSubSeq;
	}

	public String getFunctionDesc() {
		return functionDesc;
	}

	public String getFirstPageName() {
		return firstPageName;
	}

	public String getMenuGenInd() {
		return menuGenInd;
	}

	public String getUserProfGenInd() {
		return userProfGenInd;
	}

	public String getRecordFilter1Label() {
		return recordFilter1Label;
	}

	public String getRecordFilter1Help() {
		return recordFilter1Help;
	}

	public String getRecordFilter2Label() {
		return recordFilter2Label;
	}

	public String getRecordFilter2Help() {
		return recordFilter2Help;
	}

	public String getRecordFilter3Label() {
		return recordFilter3Label;
	}

	public String getRecordFilter3Help() {
		return recordFilter3Help;
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

	@JsonBackReference	
	public SystemCatalogDAO getSystemCatalog() {
		return systemCatalog;
	}

	@JsonManagedReference
	public Set<FunctionPageDAO> getFunctionPages() {
		return functionPages;
	}
	
	public void setFunctionPK(FunctionDAOPK functionPK) {
		this.functionPK = functionPK;
	}
	
	public void setFunctionCatalogCode(String functionCatalogCode) {
		this.functionCatalogCode = functionCatalogCode;
	}

	public void setFunctionSeq(Integer functionSeq) {
		this.functionSeq = functionSeq;
	}

	public void setFunctionSubSeq(Integer functionSubSeq) {
		this.functionSubSeq = functionSubSeq;
	}

	public void setFunctionDesc(String functionDesc) {
		this.functionDesc = functionDesc;
	}

	public void setFirstPageName(String firstPageName) {
		this.firstPageName = firstPageName;
	}

	public void setRecordFilter1Label(String recordFilter1Label) {
		this.recordFilter1Label = recordFilter1Label;
	}

	public void setMenuGenInd(String menuGenInd) {
		this.menuGenInd = menuGenInd;
	}
	
	public void setUserProfGenInd(String userProfGenInd) {
		this.userProfGenInd = userProfGenInd;
	}
	
	public void setRecordFilter1Help(String recordFilter1Help) {
		this.recordFilter1Help = recordFilter1Help;
	}

	public void setRecordFilter2Label(String recordFilter2Label) {
		this.recordFilter2Label = recordFilter2Label;
	}

	public void setRecordFilter2Help(String recordFilter2Help) {
		this.recordFilter2Help = recordFilter2Help;
	}

	public void setRecordFilter3Label(String recordFilter3Label) {
		this.recordFilter3Label = recordFilter3Label;
	}

	public void setRecordFilter3Help(String recordFilter3Help) {
		this.recordFilter3Help = recordFilter3Help;
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

	public void setSystemCatalog(SystemCatalogDAO systemCatalog) {
		this.systemCatalog = systemCatalog;
	}
	
	public void setFunctionPages(Set<FunctionPageDAO> functionPages) {
		this.functionPages = functionPages;
	}
	
	@Override
	public int compareTo(FunctionDAO o) {
		
		int functionResult = (this.getFunctionSeq()).compareTo(o.getFunctionSeq());
        if (functionResult != 0)
        {
            return functionResult;
        }
		
		return (this.getFunctionSubSeq()).compareTo(o.getFunctionSubSeq());
	}
	
}
