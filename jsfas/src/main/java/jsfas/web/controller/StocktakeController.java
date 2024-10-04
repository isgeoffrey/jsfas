package jsfas.web.controller;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.transaction.Transactional;

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
import jsfas.db.main.persistence.service.StocktakeService;


@RestController
@ControllerAdvice
public class StocktakeController extends CommonApiController {
	
	@Autowired
	StocktakeService stocktakeService;


	@RequestMapping(value = RestURIConstants.STK_PLAN_LIST, method=RequestMethod.POST)
	public Response getStkPlanList(HttpServletRequest request, @RequestBody CommonJson inputJson) throws Exception {
		Response response = new Response();
		
		JSONObject requestBody = GeneralUtil.commonJsonToJsonObject(inputJson);

		CommonJson data = new CommonJson().set("stkPlanList", 
				GeneralUtil.jsonArrayToCommonJson(stocktakeService.getAllStocktakePlans(requestBody)));

		response.setData(data);
		return setSuccess(response);
	}

	@RequestMapping(value = RestURIConstants.STK_PLAN, method = RequestMethod.GET)
	public Response getStkPlanById(HttpServletRequest request, @RequestParam Map<String,String> paramMap) throws Exception {
		Response response = new Response();
		
		JSONObject requestParam = new JSONObject(paramMap);
		
		CommonJson data = new CommonJson().set("stkPlan", 
		GeneralUtil.jsonObjectToCommonJson(stocktakeService.getStocktakeById(requestParam)));
		
		response.setData(data);
		return setSuccess(response);
	}
	
	@RequestMapping(value = RestURIConstants.STK_PLAN, method = RequestMethod.DELETE)
	public Response deleteStkPlanById(HttpServletRequest request, @RequestParam Map<String,String> paramMap) throws Exception {
		Response response = new Response();
		
		JSONObject requestParam = new JSONObject(paramMap);
		
		CommonJson data = new CommonJson().set("stkPlan", GeneralUtil.jsonObjectToCommonJson(stocktakeService.deleteStocktakePlan(requestParam)));
		
		response.setData(data);
		return setSuccess(response);

		
	}

	@RequestMapping(value = RestURIConstants.STK_PLAN_DTL_SUMMARY, method = RequestMethod.GET)
	public Response getStkPlanSummaryById(HttpServletRequest request, @RequestParam Map<String,String> paramMap) throws Exception {
		Response response = new Response();
		
		JSONObject requestParam = new JSONObject(paramMap);
		
		CommonJson data = new CommonJson().set("stkPlanSummary", 
		GeneralUtil.jsonObjectToCommonJson(stocktakeService.getSummaryOfStocktakeById(requestParam)));
		
		response.setData(data);
		return setSuccess(response);
	}

	@RequestMapping(value = RestURIConstants.STK_PLAN_LOCK, method=RequestMethod.GET)
	public Response lockStocktakePlan(HttpServletRequest request, @RequestParam Map<String,String> paramMap) throws Exception{
		Response response = new Response();
		
		JSONObject requestParam = new JSONObject(paramMap);
		
		CommonJson data = new CommonJson().set("stkPlan", GeneralUtil.jsonObjectToCommonJson(stocktakeService.lockStocktakePlan(requestParam)));
		
		response.setData(data);
		return setSuccess(response);
	}

	@RequestMapping(value = RestURIConstants.STK_ITEM, method=RequestMethod.POST)
	public Response updateStockItemByRow(HttpServletRequest request, @RequestBody CommonJson inputJson) throws Exception {
		Response response = new Response();
		
		CommonJson data = new CommonJson()
		.set("stkItem", GeneralUtil.jsonObjectToCommonJson(stocktakeService.updateStocktakeByRow(GeneralUtil.commonJsonToJsonObject(inputJson))))
		.set("stkPlanSummary", GeneralUtil.jsonObjectToCommonJson(stocktakeService.getSummaryOfStocktakeById(GeneralUtil.commonJsonToJsonObject(inputJson))));
		
		response.setData(data);
		return setSuccess(response);
	}

	@RequestMapping(value = RestURIConstants.STK_ITEM_CLEAR, method=RequestMethod.POST)
	public Response clearStockItemByRow(HttpServletRequest request, @RequestBody CommonJson inputJson) throws Exception {
		Response response = new Response();
		
		CommonJson data = new CommonJson()
		.set("stkItem", GeneralUtil.jsonObjectToCommonJson(stocktakeService.clearStocktakeByRow(GeneralUtil.commonJsonToJsonObject(inputJson))))
		.set("stkPlanSummary", GeneralUtil.jsonObjectToCommonJson(stocktakeService.getSummaryOfStocktakeById(GeneralUtil.commonJsonToJsonObject(inputJson))));
		
		response.setData(data);
		return setSuccess(response);
	}
	
	

}
