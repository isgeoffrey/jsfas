package jsfas.db.main.persistence.domain;

import java.io.Serializable;
import java.sql.Timestamp;

import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table(name = "EL_FUNC_PAGES_TAB")
public class FunctionPageDAO implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -7098350792865185878L;

	@EmbeddedId
    private FunctionPageDAOPK functionPagePK;
	
	@Column(name = "PAGE_TITLE_TXT")
	private String pageTitleTxt;
	
	@Column(name = "PAGE_TAG_NAM")
	private String pageTagName;
	
	@Column(name = "PAGE_REMARK_TXT")
	private String pageRemarkTxt;
	
	@Column(name = "RBAC_PERM")
	private String rbacPerm;
	
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

	public FunctionPageDAOPK getFunctionPagePK() {
		return functionPagePK;
	}

	public String getPageTitleTxt() {
		return pageTitleTxt;
	}

	public String getPageTagName() {
		return pageTagName;
	}

	public String getPageRemarkTxt() {
		return pageRemarkTxt;
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

	public void setFunctionPagePK(FunctionPageDAOPK functionPagePK) {
		this.functionPagePK = functionPagePK;
	}

	public void setPageTitleTxt(String pageTitleTxt) {
		this.pageTitleTxt = pageTitleTxt;
	}

	public void setPageTagName(String pageTagName) {
		this.pageTagName = pageTagName;
	}

	public void setPageRemarkTxt(String pageRemarkTxt) {
		this.pageRemarkTxt = pageRemarkTxt;
	}

	public String getRbacPerm() {
        return rbacPerm;
    }

    public void setRbacPerm(String rbacPerm) {
        this.rbacPerm = rbacPerm;
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
	
	//custom getter
	public String getCssName() {
		String pageName = this.getFunctionPagePK().getPageName();
		return pageName.replaceAll("_", "-");
	}
}
