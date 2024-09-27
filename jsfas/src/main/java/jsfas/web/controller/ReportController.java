package jsfas.web.controller;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.ControllerAdvice;
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
import jsfas.db.main.persistence.service.ReportService;

@RestController
@ControllerAdvice
public class ReportController extends CommonApiController {
	
	@Autowired
	private ReportService reportService;
	
	@Function(functionCode = AppConstants.REPORT_PAYMENT_STATUS_FUNC_CDE)
	@RequestMapping(value = RestURIConstants.REPORT_PAYMENT_STATUS, method = RequestMethod.GET)
	public Response getPaymentStatusReportList(HttpServletRequest request, @RequestParam Map<String, String> paramMap) 
					throws Exception {

		JSONObject requestParam = new JSONObject(paramMap);
		
		CommonJson data = new CommonJson().set("report", GeneralUtil.jsonArrayToCommonJson(reportService.getPaymentStatusReportList(requestParam)));
		
		Response response = new Response();
		response.setData(data);

		return setSuccess(response);
	}
	
	@Function(functionCode = AppConstants.REPORT_PAYMENT_PROCESSED_FUNC_CDE)
	@RequestMapping(value = RestURIConstants.REPORT_PAYMENT_PROCESSED, method = RequestMethod.GET)
	public Response getPaymentPostedReportList(HttpServletRequest request, @RequestParam Map<String, String> paramMap) 
					throws Exception {

		JSONObject requestParam = new JSONObject(paramMap);
		
		CommonJson data = new CommonJson().set("report", GeneralUtil.jsonObjectToCommonJson(reportService.getPostedPaymentReportList(requestParam)));
		
		Response response = new Response();
		response.setData(data);

		return setSuccess(response);
	}
}
