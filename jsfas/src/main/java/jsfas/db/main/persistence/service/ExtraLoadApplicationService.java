package jsfas.db.main.persistence.service;

import org.json.JSONArray;
import org.json.JSONObject;

public interface ExtraLoadApplicationService {
	// Create new application
	public JSONObject createElApplication(JSONObject inputJson, String opPageName) throws Exception;
	
	// Edit application
	public JSONObject editElApplication(String applHdrId, JSONObject inputJson, String opPageName) throws Exception;
	
	// Get 1 application 
	public JSONObject getElApplicationDetails(String applHdrId) throws Exception;
	
	// Get my application list (requestor / applicant = login user)
	public JSONArray getMyApplications() throws Exception;
	
	// Get pending for approver application list
	public JSONArray getApplsPendingRemoteUserApproval() throws Exception;
	
	// Generate Approvers List for selection
	public JSONArray genApplicationApprvs(JSONObject inputJson) throws Exception;
	
	// Enquire applications (support searching)
	public JSONArray getApplsForEnquiry(JSONObject inputJson) throws Exception;
	
	// Validate selected approvers
	public void validateApplicationApprvs(JSONArray inputJson) throws Exception;
	
	// Insert action history
	public JSONObject createElApplAct(String applHdrId, JSONObject inputJson, String opPageName) throws Exception;
	
	// update application status
	public JSONObject updateElApplicationStatus(String applHdrId, JSONObject inputJson, String opPageName) throws Exception;
	
	public String getApplWorkflowType(String jobCatgCode) throws Exception;

	public JSONArray getAllApplsForEnquiry(JSONObject inputJson) throws Exception;
	
	public JSONArray getFoEnquiryApplicationData(JSONObject inputJson) throws Exception;
	
	public JSONArray getRelatedApprovedAppls(String applHdrId) throws Exception;
	
	public void validateProjectPreiod(JSONArray pymtJsonList) throws Exception;
	
	public void validatePaymentDate(JSONArray pymtJsonList) throws Exception;
	
	public void isLoaAuthUser(String applHdrId) throws Exception;
	
	public void isElApplAuthUser(String applHdrId) throws Exception;

	public JSONArray getBatchApplsPendingRemoteUserApproval() throws Exception;

	public JSONArray batchApprovalInputValidation(JSONArray inputJson) throws Exception;

	public JSONObject batchApprovalUpdateApplStatus(JSONArray inputJson, String opPageName) throws Exception;
}
