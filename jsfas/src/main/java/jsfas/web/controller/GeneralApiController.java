package jsfas.web.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.ControllerAdvice;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
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

	@RequestMapping(value = RestURIConstants.STK_PLAN_STATUS, method=RequestMethod.GET)
	public Response getStkPlanStatus() throws Exception {
		Response response = new Response();
		
		CommonJson data = new CommonJson().set("stkPlanStatus",
		GeneralUtil.jsonArrayToCommonJson(generalApiService.getStocktakePlanStatus()));

		response.setData(data);
		return setSuccess(response);
	}

	@RequestMapping(value=RestURIConstants.DEPT_LIST, method=RequestMethod.GET)
	public Response getDeptList () throws Exception{
		Response response = new Response();
		
		CommonJson data = new CommonJson().set("deptList",
		GeneralUtil.jsonArrayToCommonJson(generalApiService.getDeptList()));

		response.setData(data);
		return setSuccess(response);
	}
	
	

}
