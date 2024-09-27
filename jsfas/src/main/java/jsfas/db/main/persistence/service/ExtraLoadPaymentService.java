package jsfas.db.main.persistence.service;

import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

import jsfas.common.object.Coa;

public interface ExtraLoadPaymentService {
	// Get payment list (requester / applicant = login user)
	public JSONArray getPymtList() throws Exception;
	
	// Get pending for remote user approval payment list
	public JSONArray getPymtsPendingRemoteUserApproval() throws Exception;
	
	// Get payment detail
	public JSONObject getPymtDetails(String applHdrId) throws Exception;
	
	// Update payment
	public JSONObject editPymt(String applHdrId, JSONObject inputJson, String opPageName) throws Exception;
	
	// Update payment status
	public JSONObject updatePymtStatus(String applHdrId, JSONObject inputJson, boolean isChanged, String opPageName) throws Exception;
	
	public JSONArray getHRMSPaymentFileForPreview(String applHdrId) throws Exception;
	
	public List<Coa> getCoaDiffForPymtAmendment(String applHdrId, JSONArray coaLineJsonArr) throws Exception;

	public JSONArray getHRMSPaymentFileForListing(JSONObject inputJson) throws Exception;

	public JSONObject updatePaymentScheduleByPNB(String applHdrId, JSONObject inputJson, String opPageName) throws Exception;
	
	// Get payment records
	public JSONArray getPymtRecords(String applHdrId) throws Exception;

	public JSONObject batchApprovalUpdateApplStatus(JSONArray inputJson, String opPageName) throws Exception;

	public JSONObject downloadPendingPNBReport() throws Exception;
}
