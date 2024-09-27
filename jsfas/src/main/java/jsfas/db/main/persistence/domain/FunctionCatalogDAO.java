package jsfas.db.main.persistence.domain;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.JoinColumns;
import javax.persistence.OneToMany;
import javax.persistence.Table;

@Entity
@Table(name = "EL_FUNC_CATG_TAB")
public class FunctionCatalogDAO implements Serializable, Comparable<FunctionCatalogDAO> {

	/**
	 * 
	 */
	private static final long serialVersionUID = -4474700687351273272L;

	@EmbeddedId
    private FunctionCatalogDAOPK functionCatalogPK;
	
	@Column(name = "FUNC_CATG_SEQ_NBR")
	private Integer functionCatalogSeq;
	
	@Column(name = "FUNC_CATG_DESC")
	private String functionCatalogDesc;
	
	@OneToMany(fetch = FetchType.LAZY, cascade=CascadeType.ALL)
	@JoinColumns({
		@JoinColumn(name="SYS_CATG_CDE", referencedColumnName="SYS_CATG_CDE"),
		@JoinColumn(name="FUNC_CATG_CDE", referencedColumnName="FUNC_CATG_CDE")
	})
	private List<FunctionDAO> functions = new ArrayList<FunctionDAO>();
	
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

	public FunctionCatalogDAOPK getFunctionCatalogPK() {
		return functionCatalogPK;
	}

	public Integer getFunctionCatalogSeq() {
		return functionCatalogSeq;
	}

	public String getFunctionCatalogDesc() {
		return functionCatalogDesc;
	}
	
	public List<FunctionDAO> getFunctions() {
		return functions;
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

	public void setFunctionCatalogPK(FunctionCatalogDAOPK functionCatalogPK) {
		this.functionCatalogPK = functionCatalogPK;
	}

	public void setFunctionCatalogSeq(Integer functionCatalogSeq) {
		this.functionCatalogSeq = functionCatalogSeq;
	}

	public void setFunctionCatalogDesc(String functionCatalogDesc) {
		this.functionCatalogDesc = functionCatalogDesc;
	}
	
	public void setFunctions(List<FunctionDAO> functions) {
		this.functions = functions;
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
	
	@Override
	public int compareTo(FunctionCatalogDAO o) {
		return (this.getFunctionCatalogSeq()).compareTo(o.getFunctionCatalogSeq());
	}
	
}
