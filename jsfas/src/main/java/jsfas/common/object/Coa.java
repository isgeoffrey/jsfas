package jsfas.common.object;

import java.math.BigDecimal;

import org.apache.commons.lang.StringUtils;
import org.json.JSONObject;

import jsfas.common.utils.GeneralUtil;

public class Coa implements Comparable<Coa> {

	private String accountCode;
	private String analysisCode;
	private String fundCode;
	private String departmentId;
	private String projectId;
	private String classCode;
	private BigDecimal balance;
	
	public Coa() {
	}
	
	public Coa(String accountCode, String analysisCode, String fundCode, String departmentId, String projectId, String classCode, BigDecimal balance) {
		this.accountCode = GeneralUtil.initBlankString(accountCode);
		this.analysisCode = GeneralUtil.initBlankString(analysisCode);
		this.fundCode = GeneralUtil.initBlankString(fundCode);
		this.departmentId = GeneralUtil.initBlankString(departmentId);
		this.projectId = GeneralUtil.initBlankString(projectId);
		this.classCode = GeneralUtil.initBlankString(classCode);
		this.balance = GeneralUtil.initNullBigDecimal(balance);
	}
	
	public Coa(String accountCode, String analysisCode, String fundCode, String departmentId, String projectId, String classCode) {
		this(accountCode, analysisCode, fundCode, departmentId, projectId, classCode, BigDecimal.ZERO);
	}

	public Coa(JSONObject jsonObj) {
		this(GeneralUtil.initBlankString(jsonObj.optString("acct_cde").trim()),
				GeneralUtil.initBlankString(jsonObj.optString("analysis_cde").trim()),
				GeneralUtil.initBlankString(jsonObj.optString("fund_cde").trim()),
				GeneralUtil.initBlankString(jsonObj.optString("dept_id").trim()),
				GeneralUtil.initBlankString(jsonObj.optString("proj_id").trim()),
				GeneralUtil.initBlankString(jsonObj.optString("class").trim()),
				new BigDecimal(jsonObj.optDouble("pymt_amt")));
	}
	
	public String getAccountCode() {
		return accountCode;
	}

	public void setAccountCode(String accountCode) {
		this.accountCode = accountCode;
	}

	public String getAnalysisCode() {
		return analysisCode;
	}

	public void setAnalysisCode(String analysisCode) {
		this.analysisCode = analysisCode;
	}

	public String getFundCode() {
		return fundCode;
	}

	public void setFundCode(String fundCode) {
		this.fundCode = fundCode;
	}

	public String getDepartmentId() {
		return departmentId;
	}

	public void setDepartmentId(String departmentId) {
		this.departmentId = departmentId;
	}

	public String getProjectId() {
		return projectId;
	}

	public void setProjectId(String projectId) {
		this.projectId = projectId;
	}

	public String getClassCode() {
		return classCode;
	}

	public void setClassCode(String classCode) {
		this.classCode = classCode;
	}
	
	public BigDecimal getBalance() {
		return balance;
	}

	public void setBalance(BigDecimal balance) {
		this.balance = balance;
	}
	
	@Override
	public String toString() {
		return "COA: [chartfield: {"
			+ "account: " + StringUtils.defaultString(getAccountCode(), "(null)")
			+ ", analysis: " + StringUtils.defaultString(getAnalysisCode(), "(null)") 
			+ ", fund: " + StringUtils.defaultString(getFundCode(), "(null)")
			+ ", department: " + StringUtils.defaultString(getDepartmentId(), "(null)")
			+ ", project: " + StringUtils.defaultString(getProjectId(), "(null)")
			+ ", class: " + StringUtils.defaultString(getClassCode(), "(null)")
			+ "}, balance: " + GeneralUtil.formatBigDecimal(getBalance())
			+ "]";
	}
	
	@Override
	public boolean equals(Object other) {
		if (other == null) return false;
		if (other == this) return true;
		if (!(other instanceof Coa))return false;
		Coa otherMyClass = (Coa)other;
		
		return this.getAccountCode().equals(otherMyClass.getAccountCode())
				&& this.getAnalysisCode().equals(otherMyClass.getAnalysisCode())
				&& this.getFundCode().equals(otherMyClass.getFundCode())
				&& this.getDepartmentId().equals(otherMyClass.getDepartmentId())
				&& this.getProjectId().equals(otherMyClass.getProjectId())
				&& this.getClassCode().equals(otherMyClass.getClassCode());
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((accountCode == null) ? 0 : accountCode.hashCode());
		result = prime * result + ((analysisCode == null) ? 0 : analysisCode.hashCode());
		result = prime * result + ((classCode == null) ? 0 : classCode.hashCode());
		result = prime * result + ((departmentId == null) ? 0 : departmentId.hashCode());
		result = prime * result + ((fundCode == null) ? 0 : fundCode.hashCode());
		result = prime * result + ((projectId == null) ? 0 : projectId.hashCode());
		return result;
	}
	
	@Override
	public int compareTo(Coa coa) {
		return this.getUniqueId().compareTo(coa.getUniqueId());
	}
	
	public String getUniqueId() {
		return StringUtils.defaultString(this.getAccountCode(), "")
			+ StringUtils.defaultString(this.getAnalysisCode(), "")
			+ StringUtils.defaultString(this.getFundCode(), "")
			+ StringUtils.defaultString(this.getDepartmentId(), "")
			+ StringUtils.defaultString(this.getProjectId(), "")
			+ StringUtils.defaultString(this.getClassCode(), "");
	}

}