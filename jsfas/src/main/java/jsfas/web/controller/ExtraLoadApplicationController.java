package jsfas.web.controller;

import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import jsfas.common.annotation.Function;
import jsfas.common.constants.AppConstants;
import jsfas.common.constants.ResponseCodeConstants;
import jsfas.common.constants.RestURIConstants;
import jsfas.common.json.CommonJson;
import jsfas.common.json.Response;
import jsfas.common.utils.GeneralUtil;
import jsfas.db.main.persistence.domain.ElApplActDAO;
import jsfas.db.main.persistence.service.ExtraLoadApplicationEventHandler;
import jsfas.db.main.persistence.service.ExtraLoadApplicationService;
import jsfas.db.main.persistence.service.GeneralApiService;

@RestController
@ControllerAdvice
public class ExtraLoadApplicationController extends CommonApiController {

	@Autowired
	ExtraLoadApplicationService extraLoadApplicationService;
	
	@Autowired
	GeneralApiService generalApiService;
	
	private final Logger log = LoggerFactory.getLogger(this.getClass());
	
	@RequestMapping(value = RestURIConstants.EXTRA_LOAD_APPLICATIONS, method = RequestMethod.POST)
	public Response createExtraLoadApplication(HttpServletRequest request, @RequestBody CommonJson inputJson) throws Exception {
		Response response = new Response();
		
		JSONObject requestBody = GeneralUtil.commonJsonToJsonObject(inputJson);
		
		CommonJson data = new CommonJson().set("el_appl",
				GeneralUtil.jsonObjectToCommonJson(extraLoadApplicationService.createElApplication(requestBody, getOpPageName(request))));
		
		response.setData(data);
		String warningMessage = "";
		if(ElApplActDAO.SUBMIT.equalsIgnoreCase(requestBody.getString("action_type"))) {
			warningMessage += generalApiService.generateBudgetWarningMessage(requestBody);
			warningMessage += generalApiService.generateApplStartDateWarningMessage(requestBody);
			warningMessage += generalApiService.generateSemesterWarningMessage(requestBody);
		}
		if(!warningMessage.equals("")) {
			response.setMeta(ResponseCodeConstants.SUCCESS_WITH_WARNING, warningMessage);
			return response;
		}
		return setSuccess(response);
	}
	
	@RequestMapping(value = RestURIConstants.EXTRA_LOAD_APPLICATIONS_BY_ID, method = RequestMethod.PATCH)
	public Response editExtraLoadApplication(HttpServletRequest request, @PathVariable(value="id") String applHdrId, @RequestBody CommonJson inputJson) throws Exception {
		Response response = new Response();
		
		JSONObject requestBody = GeneralUtil.commonJsonToJsonObject(inputJson);
		
		CommonJson data = new CommonJson().set("el_appl",
				GeneralUtil.jsonObjectToCommonJson(extraLoadApplicationService.editElApplication(applHdrId, requestBody, getOpPageName(request))));

		response.setData(data);
		String warningMessage = "";
		if(ElApplActDAO.SUBMIT.equalsIgnoreCase(requestBody.getString("action_type"))) {
			warningMessage += generalApiService.generateBudgetWarningMessage(requestBody);
			warningMessage += generalApiService.generateApplStartDateWarningMessage(requestBody);
		}
		if(!warningMessage.equals("")) {
			response.setMeta(ResponseCodeConstants.SUCCESS_WITH_WARNING, warningMessage);
			return response;
		}
		return setSuccess(response);
	}
	
	@RequestMapping(value = RestURIConstants.EXTRA_LOAD_APPLICATIONS_BY_ID, method = RequestMethod.GET)
	public Response getExtraLoadApplication(HttpServletRequest request, @PathVariable(value="id") String applHdrId) throws Exception {
		Response response = new Response();
		
		CommonJson data = new CommonJson().set("el_appl",
				GeneralUtil.jsonObjectToCommonJson(extraLoadApplicationService.getElApplicationDetails(applHdrId)));
		
		response.setData(data);
		return setSuccess(response);
	}
	
	@RequestMapping(value = RestURIConstants.EXTRA_LOAD_APPLICATIONS, method = RequestMethod.GET)
	public Response getExtraLoadApplication(HttpServletRequest request) throws Exception {
		Response response = new Response();
		
		CommonJson data = new CommonJson().set("el_appls",
				GeneralUtil.jsonArrayToCommonJson(extraLoadApplicationService.getMyApplications()));
		
		response.setData(data);
		return setSuccess(response);
	}
	
	@RequestMapping(value = RestURIConstants.EXTRA_LOAD_APPLICATIONS_APPROVERS, method = RequestMethod.POST)
	public Response getExtraLoadApplication(HttpServletRequest request, @RequestBody CommonJson inputJson) throws Exception {
		Response response = new Response();
		
		JSONObject requestBody = GeneralUtil.commonJsonToJsonObject(inputJson);
		
		CommonJson data = new CommonJson().set("apprvs",
				GeneralUtil.jsonArrayToCommonJson(extraLoadApplicationService.genApplicationApprvs(requestBody)));
		
		response.setData(data);
		return setSuccess(response);
	}
	
	@RequestMapping(value = RestURIConstants.EXTRA_LOAD_APPLICATIONS_FOR_APPROVERS, method = RequestMethod.GET)
	public Response getApplicationsPendingRemoteUserApproval(HttpServletRequest request) throws Exception {
		Response response = new Response();
		
		CommonJson data = new CommonJson().set("el_appls_for_apprvs",
				GeneralUtil.jsonArrayToCommonJson(extraLoadApplicationService.getApplsPendingRemoteUserApproval()));
		
		response.setData(data);
		return setSuccess(response);
	}
	
	@RequestMapping(value = RestURIConstants.EXTRA_LOAD_APPLICATIONS_STATUS, method = RequestMethod.POST)
	public Response updateExtraLoadApplicationStatus(HttpServletRequest request, @PathVariable(value="id") String applHdrId, @RequestBody CommonJson inputJson) throws Exception {
		Response response = new Response();
		
		JSONObject requestBody = GeneralUtil.commonJsonToJsonObject(inputJson);
		
		CommonJson data = new CommonJson().set("el_appl",
				GeneralUtil.jsonObjectToCommonJson(extraLoadApplicationService.updateElApplicationStatus(applHdrId, requestBody, getOpPageName(request))));
		
		response.setData(data);
		return setSuccess(response);
	}
	
	@RequestMapping(value = RestURIConstants.ENQUIRE_EXTRA_LOAD_APPLICATIONS, method = RequestMethod.GET)
	public Response getApplsForEnquiry(HttpServletRequest request, @RequestParam Map<String, String> paramMap) throws Exception {
		Response response = new Response();
		JSONObject requestParam = new JSONObject(paramMap);
		
		CommonJson data = new CommonJson().set("el_appls",
				GeneralUtil.jsonArrayToCommonJson(extraLoadApplicationService.getApplsForEnquiry(requestParam)));
		
		response.setData(data);
		return setSuccess(response);
	}
	
	@Function(functionCode = AppConstants.ENQUIRE_APPL_FUNC_CDE)
	@RequestMapping(value = RestURIConstants.ENQUIRY_APPLICATION, method = RequestMethod.GET)
	public Response getAllApplsForEnquiry(HttpServletRequest request, @RequestParam Map<String, String> paramMap) throws Exception {
		Response response = new Response();
		JSONObject requestParam = new JSONObject(paramMap);
		
		CommonJson data = new CommonJson().set("el_appls",
				GeneralUtil.jsonArrayToCommonJson(extraLoadApplicationService.getAllApplsForEnquiry(requestParam)));
		
		response.setData(data);
		return setSuccess(response);
	}
	
	@Function(functionCode = AppConstants.REPORT_APPLICATION_DATA_FUNC_CDE)
	@RequestMapping(value = RestURIConstants.ENQUIRY_REPORT_APPLICATION_DATA, method = RequestMethod.GET)
	public Response getFoEnquiryApplicationData(HttpServletRequest request, @RequestParam Map<String, String> paramMap) throws Exception {
		Response response = new Response();
		JSONObject requestParam = new JSONObject(paramMap);
		
		CommonJson data = new CommonJson().set("el_appls",
				GeneralUtil.jsonArrayToCommonJson(extraLoadApplicationService.getFoEnquiryApplicationData(requestParam)));
		
		response.setData(data);
		return setSuccess(response);
	}
	
	@RequestMapping(value = RestURIConstants.APPROVED_APPLS, method = RequestMethod.GET)
	public Response getRelatedApprovedAppls(HttpServletRequest request, @PathVariable(value="id") String applHdrId) throws Exception {
		Response response = new Response();
//		JSONObject requestParam = new JSONObject();
		
		CommonJson data = new CommonJson()
				.set("approved_appls", GeneralUtil.jsonArrayToCommonJson(extraLoadApplicationService.getRelatedApprovedAppls(applHdrId)))
				.set("acad_yr_list", GeneralUtil.jsonArrayToCommonJson(generalApiService.getAcadYrList()))
				.set("current_term", GeneralUtil.jsonObjectToCommonJson(generalApiService.getCurrentTerm()));
		
		response.setData(data);
		return setSuccess(response);
	}
	
	@RequestMapping(value = RestURIConstants.BATCH_APPROVAL, method = RequestMethod.GET)
	public Response getBatchPendingRemoteUserApproval(HttpServletRequest request) throws Exception {
		Response response = new Response();
		
		CommonJson data = new CommonJson().set("el_appls_for_apprvs",
				GeneralUtil.jsonArrayToCommonJson(extraLoadApplicationService.getBatchApplsPendingRemoteUserApproval()));
		
		response.setData(data);
		return setSuccess(response);
	}

	
	@RequestMapping(value = RestURIConstants.BATCH_APPROVAL, method = RequestMethod.POST)
	public Response batchApprove(HttpServletRequest request, @RequestBody CommonJson inputJson) throws Exception {
		Response response = new Response();
		
		JSONArray requestbody = GeneralUtil.commonJsonToJsonObject(inputJson).getJSONArray("outputJson");
		
		JSONArray errorList = extraLoadApplicationService.batchApprovalInputValidation(requestbody);
		if(errorList.length() > 0) {
			CommonJson data = new CommonJson().set("errMsg", GeneralUtil.jsonArrayToCommonJson(errorList));
			response.setCode(ResponseCodeConstants.SUCCESS_WITH_WARNING);
			response.setData(data);
			return response;
		}
		
		CommonJson data = new CommonJson().set("el_appls_for_apprvs",
				GeneralUtil.jsonObjectToCommonJson(extraLoadApplicationService.batchApprovalUpdateApplStatus(requestbody, getOpPageName(request))));
		
		response.setData(data);
		return setSuccess(response);
	}
}
