package jsfas.web.controller;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.text.StringEscapeUtils;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import jsfas.common.annotation.Function;
import jsfas.common.constants.AppConstants;
import jsfas.common.constants.ApplStatusConstants;
import jsfas.common.constants.ResponseCodeConstants;
import jsfas.common.constants.RestURIConstants;
import jsfas.common.exception.InvalidParameterException;
import jsfas.common.json.CommonJson;
import jsfas.common.json.Response;
import jsfas.common.utils.GeneralUtil;
import jsfas.db.main.persistence.domain.ElApplActDAO;
import jsfas.db.main.persistence.domain.ElApplHdrDAO;
import jsfas.db.main.persistence.repository.ElApplHdrRepository;
import jsfas.db.main.persistence.service.ExtraLoadApplicationEventHandler;
import jsfas.db.main.persistence.service.ExtraLoadPaymentService;
import jsfas.db.main.persistence.service.GeneralApiService;

@RestController
@ControllerAdvice
public class ExtraLoadPaymentController extends CommonApiController {

	@Autowired
	ExtraLoadPaymentService extraLoadPaymentService;

	@Autowired
	ElApplHdrRepository elApplHdrRepository;
	
	@Autowired
	GeneralApiService generalApiService;
	
	@RequestMapping(value = RestURIConstants.PAYMENTS, method = RequestMethod.GET)
	public Response getPymtList(HttpServletRequest request) throws Exception {
		Response response = new Response();
		
		CommonJson data = new CommonJson().set("pymts",
				GeneralUtil.jsonArrayToCommonJson(extraLoadPaymentService.getPymtList()));
		
		response.setData(data);
		return setSuccess(response);
	}
	
	@RequestMapping(value = RestURIConstants.PAYMENTS_FOR_APPROVERS, method = RequestMethod.GET)
	public Response getPymtsPendingRemoteUserApproval(HttpServletRequest request) throws Exception {
		Response response = new Response();
		
		CommonJson data = new CommonJson().set("pymts_appls",
				GeneralUtil.jsonArrayToCommonJson(extraLoadPaymentService.getPymtsPendingRemoteUserApproval()));
		
		response.setData(data);
		return setSuccess(response);
	}
	
	@RequestMapping(value = RestURIConstants.PAYMENTS_BY_ID, method = RequestMethod.GET)
	public Response getPymtDetails(HttpServletRequest request, @PathVariable(value = "id") String applHdrId) throws Exception {
		Response response = new Response();
		
		CommonJson data = new CommonJson().set("pymt",
				GeneralUtil.jsonObjectToCommonJson(extraLoadPaymentService.getPymtDetails(applHdrId)));
		
		response.setData(data);
		return setSuccess(response);
	}
	
	@RequestMapping(value = RestURIConstants.PAYMENTS_BY_ID, method = RequestMethod.PATCH)
	public Response editPymt(HttpServletRequest request, @PathVariable(value = "id") String applHdrId, @RequestBody CommonJson inputJson) throws Exception {
		Response response = new Response();
		
		JSONObject requestBody = GeneralUtil.commonJsonToJsonObject(inputJson);
		
		CommonJson data = new CommonJson().set("pymt",
				GeneralUtil.jsonObjectToCommonJson(extraLoadPaymentService.editPymt(applHdrId, requestBody, getOpPageName(request))));

		ElApplHdrDAO hdrDAO = elApplHdrRepository.findOne(applHdrId);

		response.setData(data);
		String warningMessage = "";
		if(requestBody.optInt("el_appl_is_updated") == 1 || hdrDAO.getApplStatCde().equals(ApplStatusConstants.PYMT_REJECTED)) {
			warningMessage += generalApiService.generateBudgetWarningMessage(requestBody);
		}
		if(!warningMessage.equals("")) {
			response.setMeta(ResponseCodeConstants.SUCCESS_WITH_WARNING, warningMessage);
			return response;
		}
		return setSuccess(response);
	}
	
	@RequestMapping(value = RestURIConstants.PAYMENTS_STATUS, method = RequestMethod.PATCH)
	public Response updatePymtStatus(HttpServletRequest request, @PathVariable(value="id") String applHdrId, @RequestBody CommonJson inputJson) throws Exception {
		Response response = new Response();
		
		JSONObject requestBody = GeneralUtil.commonJsonToJsonObject(inputJson);
		
		CommonJson data = new CommonJson().set("pymt",
				GeneralUtil.jsonObjectToCommonJson(extraLoadPaymentService.updatePymtStatus(applHdrId, requestBody, false, getOpPageName(request))));
		
		response.setData(data);
		return setSuccess(response);
	}
	
	@RequestMapping(value = RestURIConstants.PAYMENTS_PREVIEW_BY_ID, method = RequestMethod.GET)
	public Response getPymtPreviewList(HttpServletRequest request, @PathVariable(value = "id") String applHdrId) throws Exception {
		Response response = new Response();
		
		CommonJson data = new CommonJson().set("pymts_preview",
				GeneralUtil.jsonArrayToCommonJson(extraLoadPaymentService.getHRMSPaymentFileForPreview(applHdrId)));
		
		response.setData(data);
		return setSuccess(response);
	}

	@Function(functionCode = AppConstants.ENQUIRE_FNB_FUNC_CDE)
	@RequestMapping(value = RestURIConstants.PENDING_PNB_ENQ, method = RequestMethod.POST)
	public Response getPendingPNBList(HttpServletRequest request, @RequestBody CommonJson inputJson) throws Exception {
		Response response = new Response();

		JSONObject requestBody = GeneralUtil.commonJsonToJsonObject(inputJson);
		
		CommonJson data = new CommonJson().set("pymts",
				GeneralUtil.jsonArrayToCommonJson(extraLoadPaymentService.getHRMSPaymentFileForListing(requestBody)));
		
		response.setData(data);
		return setSuccess(response);
	}
	
	@RequestMapping(value = RestURIConstants.PNB_UPDATE_SCHEDULE, method = RequestMethod.PATCH)
	public Response pnbUpdateSchedule(HttpServletRequest request, @PathVariable(value = "id") String applHdrId, @RequestBody CommonJson inputJson) throws Exception {
		Response response = new Response();

		JSONObject requestBody = GeneralUtil.commonJsonToJsonObject(inputJson);
		
		CommonJson data = new CommonJson().set("pymts",
				GeneralUtil.jsonObjectToCommonJson(extraLoadPaymentService.updatePaymentScheduleByPNB(applHdrId, requestBody, getOpPageName(request))));
		
		response.setData(data);
		return setSuccess(response);
	}
	
	@RequestMapping(value = RestURIConstants.PYMT_RECORDS, method = RequestMethod.GET)
	public Response getPymtRecords(HttpServletRequest request, @PathVariable(value = "id") String applHdrId) throws Exception{
		Response response = new Response();
		
		CommonJson data = new CommonJson().set("pymtRecord", GeneralUtil.jsonArrayToCommonJson(extraLoadPaymentService.getPymtRecords(applHdrId)));
		
		response.setData(data);
		return setSuccess(response);
	}
	
	@RequestMapping(value = RestURIConstants.PNB_BATCH_UPDATE_SCHEDULE, method = RequestMethod.PATCH)
	public Response pnbBatchUpdateSchedule(HttpServletRequest request, @RequestBody CommonJson inputJson) throws Exception {
		Response response = new Response();

		JSONArray requestBody = GeneralUtil.commonJsonToJsonObject(inputJson).getJSONArray("outputJson");
		
		JSONObject obj = extraLoadPaymentService.batchApprovalUpdateApplStatus(requestBody, getOpPageName(request));
		
		CommonJson data = new CommonJson().set("pymts_updated",
				GeneralUtil.jsonObjectToCommonJson(obj));
		
		JSONArray errorList = obj.getJSONArray("errMsg");
		
		if(errorList.length() > 0) {
			response.setCode(ResponseCodeConstants.SUCCESS_WITH_WARNING);
			response.setData(data);
			return response;
		}
		
		response.setData(data);
		return setSuccess(response);
	}
	
	@ResponseBody
	@Function(functionCode = AppConstants.ENQUIRE_FNB_FUNC_CDE)
	@RequestMapping(value = RestURIConstants.PENDING_PNB_REPORT, method = RequestMethod.GET)
    public Response getPendingPNBReport(HttpServletRequest request, HttpServletResponse response) throws Exception {
        CommonJson outputFile;
        
        try {
            outputFile = GeneralUtil.jsonObjectToCommonJson(extraLoadPaymentService.downloadPendingPNBReport());
            
            if (outputFile == null) {
                throw new InvalidParameterException("Cannot download file.");
            }
            
            String fileName = outputFile.get("file_name");
            String mimeType =  outputFile.get("file_type");
            
            response.setContentType(mimeType + "; name=\"" + StringEscapeUtils.escapeJava(fileName) + "\"");
            response.setHeader("Content-Disposition", "inline; filename=\"" + StringEscapeUtils.escapeJava(fileName) + "\"");
            byte[] fileByte = outputFile.get("file_content", byte[].class);
            
            FileCopyUtils.copy(fileByte, response.getOutputStream());
        } catch (Exception ex) {
            throw ex;
        }
        
        return null;
    }
}
