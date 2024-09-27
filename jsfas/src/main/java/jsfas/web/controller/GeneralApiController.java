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

import jsfas.common.constants.RestURIConstants;
import jsfas.common.json.CommonJson;
import jsfas.common.json.Response;
import jsfas.common.utils.GeneralUtil;
import jsfas.db.main.persistence.service.GeneralApiService;

@RestController
@ControllerAdvice
public class GeneralApiController extends CommonApiController {

	@Autowired
	GeneralApiService generalApiService;
	
	private final Logger log = LoggerFactory.getLogger(this.getClass());
	
	@RequestMapping(value = RestURIConstants.CATEGORY_LIST, method = RequestMethod.GET)
	public Response getCategoryList(HttpServletRequest request, @RequestParam Map<String,String> paramMap) throws Exception {
		Response response = new Response();
		
		JSONObject requestParam = new JSONObject(paramMap);
		
		CommonJson data = new CommonJson().set("category_list", 
				GeneralUtil.jsonArrayToCommonJson(generalApiService.getCategoryList(requestParam)));
		
		response.setData(data);
		return setSuccess(response);
	}
	
	@RequestMapping(value = RestURIConstants.PAYMENT_TYPE_LIST, method = RequestMethod.GET)
	public Response getPaymentTypeList(HttpServletRequest request, @RequestParam Map<String,String> paramMap) throws Exception {
		Response response = new Response();
		
		JSONObject requestParam = new JSONObject(paramMap);
		
		CommonJson data = new CommonJson().set("pymt_type_list", 
				GeneralUtil.jsonArrayToCommonJson(generalApiService.getPaymentTypeList(requestParam)));
		
		response.setData(data);
		return setSuccess(response);
	}
	
	@RequestMapping(value = RestURIConstants.ACCOUT_CODE_LIST, method = RequestMethod.GET)
	public Response getAccountCodeList(HttpServletRequest request, @RequestParam Map<String,String> paramMap) throws Exception {
		Response response = new Response();
		
		JSONObject requestParam = new JSONObject(paramMap);
		
		CommonJson data = new CommonJson().set("acct_cde_list", 
				GeneralUtil.jsonArrayToCommonJson(generalApiService.getAccountCodeList(requestParam)));
		
		response.setData(data);
		return setSuccess(response);
	}
	
	@RequestMapping(value = RestURIConstants.ANALYSIS_CODE_LIST, method = RequestMethod.GET)
	public Response getAnalysisCodeList(HttpServletRequest request, @RequestParam Map<String,String> paramMap) throws Exception {
		Response response = new Response();
		
		JSONObject requestParam = new JSONObject(paramMap);
		
		CommonJson data = new CommonJson().set("analysis_cde_list", 
				GeneralUtil.jsonArrayToCommonJson(generalApiService.getAnalysisCodeList(requestParam)));
		
		response.setData(data);
		return setSuccess(response);
	}
	
	@RequestMapping(value = RestURIConstants.FUND_CODE_LIST, method = RequestMethod.GET)
	public Response getFundCodeList(HttpServletRequest request, @RequestParam Map<String,String> paramMap) throws Exception {
		Response response = new Response();
		
		JSONObject requestParam = new JSONObject(paramMap);
		
		CommonJson data = new CommonJson().set("fund_cde_list", 
				GeneralUtil.jsonArrayToCommonJson(generalApiService.getFundCodeList(requestParam)));
		
		response.setData(data);
		return setSuccess(response);
	}
	
	@RequestMapping(value = RestURIConstants.PROJ_ID_LIST, method = RequestMethod.GET)
	public Response getProjIdList(HttpServletRequest request, @RequestParam Map<String,String> paramMap) throws Exception {
		Response response = new Response();
		
		JSONObject requestParam = new JSONObject(paramMap);
		
		CommonJson data = new CommonJson().set("proj_id_list", 
				GeneralUtil.jsonArrayToCommonJson(generalApiService.getProjIdList(requestParam)));
		
		response.setData(data);
		return setSuccess(response);
	}
	
	@RequestMapping(value = RestURIConstants.CLASS_CODE_LIST, method = RequestMethod.GET)
	public Response getClassCodeList(HttpServletRequest request, @RequestParam Map<String,String> paramMap) throws Exception {
		Response response = new Response();
		
		JSONObject requestParam = new JSONObject(paramMap);
		
		CommonJson data = new CommonJson().set("class_cde_list", 
				GeneralUtil.jsonArrayToCommonJson(generalApiService.getClassCodeList(requestParam)));
		
		response.setData(data);
		return setSuccess(response);
	}
	
	@RequestMapping(value = RestURIConstants.DEPT_ID_LIST, method = RequestMethod.GET)
	public Response getDeptIdList(HttpServletRequest request, @RequestParam Map<String,String> paramMap) throws Exception {
		Response response = new Response();
		
		JSONObject requestParam = new JSONObject(paramMap);
		
		CommonJson data = new CommonJson().set("dept_id_list", 
				GeneralUtil.jsonArrayToCommonJson(generalApiService.getDeptIdList(requestParam)));
		
		response.setData(data);
		return setSuccess(response);
	}
	
	@RequestMapping(value = RestURIConstants.ACAD_TERMS, method = RequestMethod.GET)
	public Response getAcadTermList(HttpServletRequest request, @RequestParam Map<String,String> paramMap) throws Exception {
		Response response = new Response();
		
		JSONObject requestParam = new JSONObject(paramMap);
		
		CommonJson data = new CommonJson().set("acad_term_list", 
				GeneralUtil.jsonArrayToCommonJson(generalApiService.getAcadTermList(requestParam)));
		
		response.setData(data);
		return setSuccess(response);
	}
	
	@RequestMapping(value = RestURIConstants.SCHL_LIST, method = RequestMethod.GET)
	public Response getSchlList(HttpServletRequest request, @RequestParam Map<String,String> paramMap) throws Exception {
		Response response = new Response();
		
		JSONObject requestParam = new JSONObject(paramMap);
		
		CommonJson data = new CommonJson().set("schl_list", 
				GeneralUtil.jsonArrayToCommonJson(generalApiService.getSchlList(requestParam)));
		
		response.setData(data);
		return setSuccess(response);
	}
	
	@RequestMapping(value = RestURIConstants.SAL_ELEMENT_LIST, method = RequestMethod.GET)
	public Response getSalElementList(HttpServletRequest request, @RequestParam Map<String,String> paramMap) throws Exception {
		Response response = new Response();
		
		JSONObject requestParam = new JSONObject(paramMap);
		
		CommonJson data = new CommonJson().set("sal_element_list", 
				GeneralUtil.jsonArrayToCommonJson(generalApiService.getSalElementList(requestParam)));
		
		response.setData(data);
		return setSuccess(response);
	}
	
	@RequestMapping(value = RestURIConstants.STAFF_LIST, method = RequestMethod.GET)
	public Response getStaffList(HttpServletRequest request, @RequestParam Map<String,String> paramMap) throws Exception {
		Response response = new Response();
		
		JSONObject requestParam = new JSONObject(paramMap);
		
		CommonJson data = new CommonJson().set("staff_list", 
				GeneralUtil.jsonArrayToCommonJson(generalApiService.getStaffList(requestParam)));
		
		response.setData(data);
		return setSuccess(response);
	}
	
	@RequestMapping(value = RestURIConstants.BCO_APPROVERS, method = RequestMethod.GET)
	public Response getBCOAprvListByProject(HttpServletRequest request, @RequestParam Map<String,String> paramMap) throws Exception {
		Response response = new Response();
		
		JSONObject requestParam = new JSONObject(paramMap);
		
		CommonJson data = new CommonJson().set("bco_aprv_list", 
				GeneralUtil.jsonArrayToCommonJson(generalApiService.getBCOAprvList(requestParam)));
		
		response.setData(data);
		return setSuccess(response);
	}
	
	@RequestMapping(value = RestURIConstants.COURSE_LIST, method = RequestMethod.GET)
	public Response getCourseList(HttpServletRequest request, @RequestParam Map<String,String> paramMap) throws Exception {
		Response response = new Response();
		
		JSONObject requestParam = new JSONObject(paramMap);
		
		CommonJson data = new CommonJson().set("course_list", 
				GeneralUtil.jsonArrayToCommonJson(generalApiService.getCourseList(requestParam)));
		
		response.setData(data);
		return setSuccess(response);
	}
	
	@RequestMapping(value = RestURIConstants.CO_COURSE_LIST, method = RequestMethod.GET)
	public Response getCoCourseList(HttpServletRequest request, @RequestParam Map<String,String> paramMap) throws Exception {
		Response response = new Response();
		
		JSONObject requestParam = new JSONObject(paramMap);
		
		CommonJson data = new CommonJson().set("colist_course", 
				GeneralUtil.jsonObjectToCommonJson(generalApiService.getCoCourseList(requestParam)));
		
		response.setData(data);
		return setSuccess(response);
	}
	
	@RequestMapping(value = RestURIConstants.PROGRAM_LIST, method = RequestMethod.GET)
	public Response getProgramList(HttpServletRequest request, @RequestParam Map<String,String> paramMap) throws Exception {
		Response response = new Response();
		
		JSONObject requestParam = new JSONObject(paramMap);
		
		CommonJson data = new CommonJson().set("prgm_list", 
				GeneralUtil.jsonArrayToCommonJson(generalApiService.getProgramList(requestParam)));
		
		response.setData(data);
		return setSuccess(response);
	}
	
	@RequestMapping(value = RestURIConstants.APPL_STAT_LIST, method = RequestMethod.GET)
	public Response getApplStatusList(HttpServletRequest request, @RequestParam Map<String,String> paramMap) throws Exception {
		Response response = new Response();
		
		JSONObject requestParam = new JSONObject(paramMap);
		
		CommonJson data = new CommonJson().set("appl_stat_list", 
				GeneralUtil.jsonArrayToCommonJson(generalApiService.getApplStatusList(requestParam)));
		
		response.setData(data);
		return setSuccess(response);
	}
	
	@RequestMapping(value = RestURIConstants.TOTAL_AMOUNT, method = RequestMethod.POST)
	public Response getTotalAmount(HttpServletRequest request, @RequestBody CommonJson inputJson) throws Exception {
		Response response = new Response();

		JSONObject requestBody = GeneralUtil.commonJsonToJsonObject(inputJson);
		
		CommonJson data = new CommonJson().set("appl_total_amount", generalApiService.calculateTotalAmountFromApi(requestBody));
		
		response.setData(data);
		return setSuccess(response);
	}
	
	@RequestMapping(value = RestURIConstants.MPF_ALLOCATION, method = RequestMethod.POST)
	public Response getMPFAllocation(HttpServletRequest request, @RequestBody CommonJson inputJson) throws Exception {
		Response response = new Response();
		
		JSONObject requestBody = GeneralUtil.commonJsonToJsonObject(inputJson);
		
		CommonJson data = new CommonJson().set("appl_mpf_details", 
				GeneralUtil.jsonArrayToCommonJson(generalApiService.calculateMPF(requestBody)));
		
		response.setData(data);
		return setSuccess(response);
	}
	
	@RequestMapping(value = RestURIConstants.DEFAULT_DEPT_HEAD, method = RequestMethod.GET)
	public Response getDefaultDeptHead(HttpServletRequest request, @PathVariable String deptid) throws Exception {
		Response response = new Response();
		
		CommonJson data = new CommonJson().set("default_dept_head", 
				GeneralUtil.jsonObjectToCommonJson(generalApiService.getDefaultDeptHead(deptid)));
		
		response.setData(data);
		return setSuccess(response);
	}
	
	
}
