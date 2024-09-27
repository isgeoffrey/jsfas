package jsfas.web.controller;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import jsfas.common.annotation.Function;
import jsfas.common.constants.AppConstants;
import jsfas.common.constants.RestURIConstants;
import jsfas.common.json.CommonJson;
import jsfas.common.json.Response;
import jsfas.common.utils.GeneralUtil;
import jsfas.db.main.persistence.service.LoaService;
import jsfas.security.SecurityUtils;

@RestController
@ControllerAdvice
public class LoaController extends CommonApiController {
	
	@Autowired
	LoaService loaService;

	
	@RequestMapping(value = RestURIConstants.LOA, method = RequestMethod.GET)
	public Response getLoaList(HttpServletRequest request) 
					throws Exception {
		
		CommonJson data = new CommonJson().set("loa", GeneralUtil.jsonArrayToCommonJson(loaService.getLoaList(SecurityUtils.getCurrentLogin())));
		
		Response response = new Response();
		response.setData(data);

		return setSuccess(response);
	}

	@RequestMapping(value = RestURIConstants.LOA_BY_ID, method = RequestMethod.GET)
	public Response getLoaDetails(HttpServletRequest request, 
			@PathVariable String id) 
					throws Exception {
		
		CommonJson data = new CommonJson().set("loaDetails", GeneralUtil.jsonArrayToCommonJson(loaService.getLoaDetailsById(id)));
		
		Response response = new Response();
		response.setData(data);

		return setSuccess(response);
	}
/*
	@RequestMapping(value = RestURIConstants.LOA_STATUS_BY_ID, method = RequestMethod.PATCH)
	public Response updateLoaStatus(HttpServletRequest request, 
			@PathVariable String id, @RequestBody CommonJson inputJson) 
					throws Exception {
		
		loaService.updateLoaStatusByLoaId(id, inputJson, getOpPageName(request));
		
		Response response = new Response();

		return setSuccess(response);
	}*/

	@RequestMapping(value = RestURIConstants.LOA_FILE_BY_ID, method = RequestMethod.GET)
	public Response getAttachmentFilesList(HttpServletRequest request, 
			@PathVariable String id) 
					throws Exception {
		
		CommonJson data = new CommonJson().set("loaFileList", GeneralUtil.jsonArrayToCommonJson(loaService.getLoaFileList(id)));
		
		Response response = new Response();
		response.setData(data);

		return setSuccess(response);
	}

	@RequestMapping(value = RestURIConstants.LOA_FILE_BY_ID, method = RequestMethod.PATCH)
	public Response updateAttachmentFilesList(HttpServletRequest request, 
			@PathVariable String id, @RequestBody CommonJson inputJson) 
					throws Exception {

		loaService.updateLoaFileList(id, inputJson, getOpPageName(request));
		CommonJson data = new CommonJson()
				.set("loaDetails", GeneralUtil.jsonArrayToCommonJson(loaService.getLoaDetailsById(id)))
				.set("loaFileList", GeneralUtil.jsonArrayToCommonJson(loaService.getLoaFileList(id)));
		
		Response response = new Response();
		response.setData(data);

		return setSuccess(response);
	}
	
	@RequestMapping(value = RestURIConstants.LOA_STATUS_BY_ID, method = RequestMethod.PATCH)
	public Response updateLoaAcceptance(HttpServletRequest request, 
			@PathVariable String id, @RequestBody CommonJson inputJson) 
					throws Exception {
		JSONObject jsonObj = inputJson.toJSONObject();
		
		loaService.updateLoaStatusByHdrId(id, jsonObj, getOpPageName(request));
		Response response = new Response();

		return setSuccess(response);
	}
	
	@RequestMapping(value = RestURIConstants.SEND_REMIND_EMAIL, method = RequestMethod.POST)
	public Response sendRemindEmail(HttpServletRequest request, 
			@PathVariable String id, @RequestBody CommonJson inputJson) 
					throws Exception {
		JSONObject jsonObj = inputJson.toJSONObject();
		
		loaService.sendRemindEmail(id, jsonObj, getOpPageName(request));
		Response response = new Response();

		return setSuccess(response);
	}

	
	@Function(functionCode = AppConstants.ENQUIRE_LOA_FUNC_CDE)
	@RequestMapping(value = RestURIConstants.ENQUIRY_LOA, method = RequestMethod.GET)
	public Response getLoaEnquiryList(HttpServletRequest request, @RequestParam Map<String, String> paramMap) 
					throws Exception {

		JSONObject requestParam = new JSONObject(paramMap);
		
		CommonJson data = new CommonJson().set("loa", GeneralUtil.jsonArrayToCommonJson(loaService.getLoaEnqiuryList(requestParam)));
		
		Response response = new Response();
		response.setData(data);

		return setSuccess(response);
	}

}
