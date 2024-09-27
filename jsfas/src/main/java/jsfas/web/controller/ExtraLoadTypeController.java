package jsfas.web.controller;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;

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
import jsfas.common.constants.RestURIConstants;
import jsfas.common.json.CommonJson;
import jsfas.common.json.Response;
import jsfas.common.utils.GeneralUtil;
import jsfas.db.main.persistence.service.ExtraLoadTypeService;

@RestController
@ControllerAdvice
public class ExtraLoadTypeController extends CommonApiController {

	@Autowired
	ExtraLoadTypeService extraLoadTypeService;
	
	private final Logger log = LoggerFactory.getLogger(this.getClass());
	
	@RequestMapping(value = RestURIConstants.EXTRA_LOAD_TYPES, method = RequestMethod.GET)
	public Response getExtraLoadTypeList(HttpServletRequest request, @RequestParam Map<String,String> paramMap) throws Exception {
		Response response = new Response();
		
		JSONObject requestParam = new JSONObject(paramMap);

		CommonJson data = new CommonJson().set("el_type_list", 
				GeneralUtil.jsonArrayToCommonJson(extraLoadTypeService.getExtraLoadTypeList(requestParam)));
		
		response.setData(data);
		return setSuccess(response);	
	}
	
	@Function(functionCode = AppConstants.EL_TYPE_MAINT_FUNC_CDE, functionSubCode = "A")
	@RequestMapping(value = RestURIConstants.EXTRA_LOAD_TYPES, method = RequestMethod.POST)
	public Response createExtraLoadType(HttpServletRequest request, @RequestBody CommonJson inputJson) throws Exception {
		Response response = new Response();
		
		JSONObject requestBody = GeneralUtil.commonJsonToJsonObject(inputJson);
		
		CommonJson data = new CommonJson().set("el_type", 
				GeneralUtil.jsonObjectToCommonJson(extraLoadTypeService.createExtraLoadType(requestBody, getOpPageName(request))));
		
		response.setData(data);
		return setSuccess(response);	
	}
	
	@RequestMapping(value = RestURIConstants.EXTRA_LOAD_TYPE_MAPPING, method = RequestMethod.GET)
	public Response getExtraLoadTypeMappingList(HttpServletRequest request, @RequestParam Map<String,String> paramMap) throws Exception {
		Response response = new Response();
		
		JSONObject requestParam = new JSONObject(paramMap);
		
		CommonJson data = new CommonJson().set("el_type_mapping_list", 
				GeneralUtil.jsonArrayToCommonJson(extraLoadTypeService.getExtraLoadTypeMappingList(requestParam)));
		
		response.setData(data);
		return setSuccess(response);	
	}
	
	@Function(functionCode = AppConstants.EL_TYPE_MAINT_FUNC_CDE, functionSubCode = "E")
	@RequestMapping(value = RestURIConstants.EXTRA_LOAD_TYPES_BY_ID, method = RequestMethod.GET)
	public Response getExtraLoadTypeDetail(HttpServletRequest request, @PathVariable("type-id") String elTypeId) throws Exception {
		Response response = new Response();
		
		CommonJson data = new CommonJson().set("el_type", GeneralUtil.jsonObjectToCommonJson(extraLoadTypeService.getExtraLoadTypeDetail(elTypeId)));
		
		response.setData(data);
		return setSuccess(response);	
	}
	
	@Function(functionCode = AppConstants.EL_TYPE_MAINT_FUNC_CDE, functionSubCode = "C")
	@RequestMapping(value = RestURIConstants.EXTRA_LOAD_TYPES_BY_ID, method = RequestMethod.PATCH)
	public Response updateExtraLoadType(HttpServletRequest request, @PathVariable("type-id") String elTypeId,  @RequestBody CommonJson inputJson) throws Exception {
		Response response = new Response();
		
		JSONObject requestBody = GeneralUtil.commonJsonToJsonObject(inputJson);
		
		CommonJson data = new CommonJson().set("el_type",
				GeneralUtil.jsonObjectToCommonJson(extraLoadTypeService.updateExtraLoadType(elTypeId, requestBody, getOpPageName(request))));
		
		response.setData(data);
		return setSuccess(response);	
	}
	
	@Function(functionCode = AppConstants.EL_TYPE_MAINT_FUNC_CDE, functionSubCode = "R")
	@RequestMapping(value = RestURIConstants.EXTRA_LOAD_TYPE_MAPPING_BY_ID, method = RequestMethod.DELETE)
	public Response deleteExtraLoadType(HttpServletRequest request, @PathVariable("id") String elTypeSalElemntId,  @RequestBody CommonJson inputJson) throws Exception {
		Response response = new Response();
		
		JSONObject requestBody = GeneralUtil.commonJsonToJsonObject(inputJson);
		
		CommonJson data = new CommonJson().set("el_type",
				GeneralUtil.jsonObjectToCommonJson(extraLoadTypeService.deleteExtraLoadTypeMapping(elTypeSalElemntId, requestBody, getOpPageName(request))));
		
		response.setData(data);
		return setSuccess(response);	
	}
//	@RequestMapping(value = RestURIConstants.EXTRA_LOAD_TYPES, method = RequestMethod.GET)
//	public Response getExtraLoadTypeDetails(HttpServletRequest request, @RequestBody CommonJson inputJson) throws Exception {
//		Response response = new Response();
//		
//		//CommonJson data = new CommonJson().set("el_type", extraLoadTypeService.createExtraLoadType(inputJson, getOpPageName(request)));
//		
//		//response.setData(data);
//		return setSuccess(response);	
//	}
}
