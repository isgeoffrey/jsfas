package jsfas.db.main.persistence.service;

import java.math.BigDecimal;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

public interface GeneralApiService {

	// Get staff list
	public JSONArray getStaffList(JSONObject inputJson) throws Exception;
	
	// Get staff list by userId
	public JSONArray getStaffListByUserId(String userId, String type) throws Exception;
	
	// Get applicant list by userId
	public JSONArray getApplicantListByUserId(String userId) throws Exception;
	
	// Get program list
	public JSONArray getProgramList(JSONObject inputJson) throws Exception;
	
	// Get course list
	public JSONArray getCourseList(JSONObject inputJson) throws Exception;
	
	// Get co-course list
	public JSONObject getCoCourseList(JSONObject inputJson) throws Exception;
	
	// Get payment type list
	public JSONArray getPaymentTypeList(JSONObject inputJson) throws Exception;
	
	// Get category list
	public JSONArray getCategoryList(JSONObject inputJson) throws Exception;
	
	// Get account code list
	public JSONArray getAccountCodeList(JSONObject inputJson) throws Exception;
	
	// Get analysis code list
	public JSONArray getAnalysisCodeList(JSONObject inputJson) throws Exception;
	
	// Get fund code list
	public JSONArray getFundCodeList(JSONObject inputJson) throws Exception;
	
	// Get proj id list
	public JSONArray getProjIdList(JSONObject inputJson) throws Exception;
	
	// Get Class code list
	public JSONArray getClassCodeList(JSONObject inputJson) throws Exception;
	
	// Get Dept id list
	public JSONArray getDeptIdList(JSONObject inputJson) throws Exception;
	
	// Get Academic terms list
	public JSONArray getAcadTermList(JSONObject inputJson) throws Exception;
	
	// Get salary element list
	public JSONArray getSalElementList(JSONObject inputJson) throws Exception;
	
	// Get BCO approver list
	public JSONArray getBCOAprvList(JSONObject inputJson) throws Exception;
	
	// Call FMS budget check API
	public JSONArray getBudgetCheckResult(JSONArray coaLineJsonArr) throws Exception;
	
	// Get Application Status List
	public JSONArray getApplStatusList(JSONObject inputJson) throws Exception;

	public BigDecimal calculateTotalAmountFromApi(JSONObject inputJson) throws Exception;

	public JSONArray calculateMPF(JSONObject inputJson) throws Exception;

	public JSONArray calculateAmountPerMonth(JSONObject inputJson) throws Exception;

	public BigDecimal calculateCOARecurrentTotalAmount(Long startDtLong, Long endDtLong, BigDecimal amount);
	
	public String generateBudgetWarningMessage(JSONObject inputJson) throws Exception;

	public String generateApplStartDateWarningMessage(JSONObject inputJson) throws Exception;
	
	public String generateSemesterWarningMessage(JSONObject inputJson) throws Exception;

	public boolean checkHistVersionSameAsCurrent(String applHdrId);
	
	public JSONArray getAcadYrList() throws Exception;
	
	public JSONObject getCurrentTerm() throws Exception;

	public JSONObject createElApplAct(String applHdrId, JSONObject inputJson, String actionName, String remark, String actionFrom, String opPageName) throws Exception;

	public boolean checkIsRejected(String applHdrId);
	
	public JSONObject getProvostAprv();

	public JSONObject getDefaultDeptHead(String deptid);

	public List<String> findBatchApprovalDailyEmailUserList() throws Exception;

	JSONArray getSchlList(JSONObject inputJson) throws Exception;
	
	
}
