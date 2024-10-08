package jsfas.web.controller;

import java.util.Map;
import java.util.Optional;

import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.transaction.Transactional;

import org.apache.commons.io.FileUtils;
import java.io.File;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.core.env.Environment;
import org.apache.commons.io.FilenameUtils;

import jsfas.common.constants.AppConstants;
import jsfas.common.constants.RestURIConstants;
import jsfas.common.exception.StocktakeExcelValidationException;
import jsfas.common.json.CommonJson;
import jsfas.common.json.CommonResponseJson;
import jsfas.common.json.Response;
import jsfas.common.utils.GeneralUtil;
import jsfas.db.main.persistence.service.StocktakeExcelService;
import jsfas.db.main.persistence.service.StocktakeService;
import jsfas.db.main.persistence.service.StocktakeStagingService;


@RestController
@ControllerAdvice
public class StocktakeController extends CommonApiController {
	
	@Inject Environment env;

	@Autowired
	StocktakeService stocktakeService;

	@Autowired
	StocktakeStagingService stagingService;

	@Autowired
	StocktakeExcelService stockExcelService;


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

	@RequestMapping(value = RestURIConstants.STK_PLAN_HDR, method = RequestMethod.GET)
	public Response getStkPlanHdrById(HttpServletRequest request, @RequestParam Map<String,String> paramMap) throws Exception {
		Response response = new Response();
		
		JSONObject requestParam = new JSONObject(paramMap);
		
		CommonJson data = new CommonJson().set("stkPlanHdr", 
		GeneralUtil.jsonObjectToCommonJson(stocktakeService.getStocktakeHdrById(requestParam)));
		
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
	
	@RequestMapping(value = RestURIConstants.STK_PENDING, method = RequestMethod.GET)
	public Response getStagingItemsById(HttpServletRequest request, @RequestParam Map<String,String> paramMap) throws Exception {
		Response response = new Response();
		
		JSONObject requestParam = new JSONObject(paramMap);
		
		CommonJson data = new CommonJson().set("stkPlan", 
		GeneralUtil.jsonObjectToCommonJson(stagingService.getStagingItemsById(requestParam)));
		
		response.setData(data);
		return setSuccess(response);
	}

	@RequestMapping(value = RestURIConstants.STK_PENDING, method=RequestMethod.DELETE)
	public Response deleteStagingItems(HttpServletRequest request, @RequestParam Map<String,String> paramMap) throws Exception {
		Response response = new Response();
		
		JSONObject requestParam = new JSONObject(paramMap);
		
		stagingService.deleteAllStagingById(requestParam);
		
		return setSuccess(response);
	}

	@RequestMapping(value = RestURIConstants.STK_PENDING, method=RequestMethod.POST)
	public Response uploadExcelToStaging(@RequestParam("file") MultipartFile uploadFile, @RequestParam("stkPlanId") String stkPlanId, @RequestParam("opPageName") String opPageName) throws Exception{
		Response response = new Response();

		// String originalFileName = uploadFile.getOriginalFilename();
		
		// String uploadFileName = FilenameUtils.getBaseName(originalFileName);
		// String uploadFileExt = FilenameUtils.getExtension(originalFileName);

		// File dir = new File(env.getProperty("downld.dir"));

		// File outputFile = new File(dir.getAbsolutePath() + File.separator
		// 		+ uploadFileName + "." + uploadFileExt);

		// FileUtils.writeByteArrayToFile(outputFile, uploadFile.getBytes());

		// System.out.println("stkPlanId: "+stkPlanId);
		// System.out.println("opPageName: "+opPageName);
		stockExcelService.insertExcelToStaging(uploadFile, stkPlanId, opPageName);
			
		return setSuccess(response);
	}
	

	@RequestMapping(value = RestURIConstants.STG_ITEM, method=RequestMethod.POST)
	public Response updateStagingItemByRow(HttpServletRequest request, @RequestBody CommonJson inputJson) throws Exception {
		Response response = new Response();
		
		CommonJson data = new CommonJson()
		.set("stkItem", GeneralUtil.jsonObjectToCommonJson(stagingService.updateStagingByRow(GeneralUtil.commonJsonToJsonObject(inputJson))))
		.set("stkPlanSummary", GeneralUtil.jsonObjectToCommonJson(stocktakeService.getSummaryOfStocktakeById(GeneralUtil.commonJsonToJsonObject(inputJson))));
		
		response.setData(data);
		return setSuccess(response);
	}

	@RequestMapping(value = RestURIConstants.STG_ITEM_CLEAR, method=RequestMethod.POST)
	public Response clearStagingItemByRow(HttpServletRequest request, @RequestBody CommonJson inputJson) throws Exception {
		Response response = new Response();
		
		CommonJson data = new CommonJson()
		.set("stkItem", GeneralUtil.jsonObjectToCommonJson(stagingService.clearStagingByRow(GeneralUtil.commonJsonToJsonObject(inputJson))))
		.set("stkPlanSummary", GeneralUtil.jsonObjectToCommonJson(stocktakeService.getSummaryOfStocktakeById(GeneralUtil.commonJsonToJsonObject(inputJson))));
		
		response.setData(data);
		return setSuccess(response);
	}

	@RequestMapping(value = RestURIConstants.SUBMIT_STK_PLAN, method = RequestMethod.GET)
	public Response submitStkPlanById(HttpServletRequest request, @RequestParam Map<String,String> paramMap) throws Exception {
		Response response = new Response();
		
		JSONObject requestParam = new JSONObject(paramMap);
		
		// stockExcelService.commitAssetGeneration(requestParam);
		return setSuccess(response);
	}

	@RequestMapping(value = RestURIConstants.UPDATE_STK_ITEM, method=RequestMethod.POST)
	public Response updateDtlwithStagingItem(HttpServletRequest request, @RequestBody CommonJson inputJson) throws Exception {
		Response response = new Response();
		
		CommonJson data = new CommonJson()
		.set("stkPlanId", stagingService.updateDtlWithStgById(GeneralUtil.commonJsonToJsonObject(inputJson)));
		
		response.setData(data);
		return setSuccess(response);
	}


	@ExceptionHandler(StocktakeExcelValidationException.class)
	public CommonResponseJson uploadFileDataError(HttpServletRequest request, StocktakeExcelValidationException e) {
		CommonResponseJson responseJson = new CommonResponseJson();
		responseJson.setStatus(AppConstants.RESPONSE_JSON_EXCEL_ERROR);
		responseJson.setMessage(e.getMessage());
		responseJson.setCommonJsonList(e.getErrorList());
		
		
		// if(Optional.ofNullable(request).isPresent()) {
		// 	log.debug("{} encountered upload excel file data format error for url = {}", SecurityUtils.getCurrentLogin(), request.getRequestURL());
		// } else {
		// 	log.debug("{} encountered upload excel file data format error", SecurityUtils.getCurrentLogin());
		// }
		
		// log.debug("handleError URL = " + request.getRequestURL());
		// log.debug("handleError MSG = " + Thread.currentThread().getStackTrace()[1].getMethodName()+"@"+this.getClass().getSimpleName(), e);
		
		return responseJson;
	}

}
