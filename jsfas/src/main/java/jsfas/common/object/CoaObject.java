package jsfas.common.object;

import org.json.JSONObject;
import org.springframework.lang.Nullable;

import jsfas.common.utils.GeneralUtil;

public class CoaObject {
	private String analysis_cde;
	private String acct_cde;
	private String fund_cde;
	private String class_cde;
	private String dept_id;
	private String proj_id;
	private String bco_aprv_id;
	private String bco_aprv_name;

	
	
	
	public CoaObject (String analysis_cde,String acct_cde,String fund_cde,String class_cde,String dept_id,String proj_id,String bco_aprv_id, String bco_aprv_name) {
		this.analysis_cde = GeneralUtil.initNullString(analysis_cde);
		this.acct_cde = GeneralUtil.initNullString(acct_cde);
		this.fund_cde = GeneralUtil.initNullString(fund_cde);
		this.class_cde = GeneralUtil.initNullString(class_cde);
		this.dept_id = GeneralUtil.initNullString(dept_id);
		this.proj_id = GeneralUtil.initNullString(proj_id);
		this.bco_aprv_id = GeneralUtil.initNullString(bco_aprv_id);
		this.bco_aprv_name = GeneralUtil.initNullString(bco_aprv_name);
	}
	
	public CoaObject () {
		this.analysis_cde = GeneralUtil.initNullString(null);
		this.acct_cde = GeneralUtil.initNullString(null);
		this.fund_cde = GeneralUtil.initNullString(null);
		this.class_cde = GeneralUtil.initNullString(null);
		this.dept_id = GeneralUtil.initNullString(null);
		this.proj_id = GeneralUtil.initNullString(null);
		this.bco_aprv_id = GeneralUtil.initNullString(null);
		this.bco_aprv_name = GeneralUtil.initNullString(null);
	}
	
	public static Boolean isEqual (CoaObject firstCoa,CoaObject secondCoa) {
		Boolean coaEqual = true;
		
		if (!firstCoa.analysis_cde.equalsIgnoreCase(secondCoa.analysis_cde)){
			coaEqual = false;
		}
		if (!firstCoa.acct_cde.equalsIgnoreCase(secondCoa.acct_cde)) {
			coaEqual = false;
		}
		if (!firstCoa.fund_cde.equalsIgnoreCase(secondCoa.fund_cde)) {
			coaEqual = false;
		}
		if (!firstCoa.class_cde.equalsIgnoreCase(secondCoa.class_cde)) {
			coaEqual = false;
		}
		if (!firstCoa.dept_id.equalsIgnoreCase(secondCoa.dept_id)) {
			coaEqual = false;
		}
		if (!firstCoa.proj_id.equalsIgnoreCase(secondCoa.proj_id)) {
			coaEqual = false;
		}
		if (!firstCoa.bco_aprv_id.equalsIgnoreCase(secondCoa.bco_aprv_id)) {
			coaEqual = false;
		}
		if (!firstCoa.bco_aprv_name.equalsIgnoreCase(secondCoa.bco_aprv_name)) {
			coaEqual = false;
		}
		return coaEqual;
	}
	
	public Boolean isEqual (CoaObject targetCoa) {		
		Boolean coaEqual = true;
		if (!analysis_cde.equalsIgnoreCase(targetCoa.analysis_cde)){
			coaEqual = false;
		}
		if (!acct_cde.equalsIgnoreCase(targetCoa.acct_cde)) {
			coaEqual = false;
		}
		if (!fund_cde.equalsIgnoreCase(targetCoa.fund_cde)) {
			coaEqual = false;
		}
		if (!class_cde.equalsIgnoreCase(targetCoa.class_cde)) {
			coaEqual = false;
		}
		if (!dept_id.equalsIgnoreCase(targetCoa.dept_id)) {
			coaEqual = false;
		}
		if (!proj_id.equalsIgnoreCase(targetCoa.proj_id)) {
			coaEqual = false;
		}
		if (!bco_aprv_id.equalsIgnoreCase(targetCoa.bco_aprv_id)) {
			coaEqual = false;
		}
		if (!bco_aprv_name.equalsIgnoreCase(targetCoa.bco_aprv_name)) {
			coaEqual = false;
		}

		return coaEqual;
	}
	
	public String getBco_aprv_id() {
		return bco_aprv_id;
	}
	
	public void setBco_aprv_id(String bco_aprv_id) {
		this.bco_aprv_id = GeneralUtil.initNullString(bco_aprv_id);
	}
	
	public String getBco_aprv_name() {
		return bco_aprv_name;
	}
	
	public void setBco_aprv_name(String bco_aprv_name) {
		this.bco_aprv_name = GeneralUtil.initNullString(bco_aprv_name);
	}
	

	public String getAnalysis_cde() {
		return analysis_cde;
	}

	public void setAnalysis_cde(String analysis_cde) {
		this.analysis_cde = GeneralUtil.initNullString(analysis_cde);
	}

	public String getAcct_cde() {
		return acct_cde;
	}

	public void setAcct_cde(String acct_cde) {
		this.acct_cde = GeneralUtil.initNullString(acct_cde);
	}

	public String getFund_cde() {
		return fund_cde;
	}

	public void setFund_cde(String fund_cde) {
		this.fund_cde = GeneralUtil.initNullString(fund_cde);
	}

	public String getClass_cde() {
		return class_cde;
	}

	public void setClass_cde(String class_cde) {
		this.class_cde = GeneralUtil.initNullString(class_cde);
	}

	public String getDept_id() {
		return dept_id;
	}

	public void setDept_id(String dept_id) {
		this.dept_id = GeneralUtil.initNullString(dept_id);
	}

	public String getProj_id() {
		return proj_id;
	}

	public void setProj_id(String proj_id) {
		this.proj_id = GeneralUtil.initNullString(proj_id);
	}
}
