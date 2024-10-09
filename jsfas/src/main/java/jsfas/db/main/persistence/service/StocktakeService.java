package jsfas.db.main.persistence.service;

import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.web.multipart.MultipartFile;

import jsfas.common.json.CommonJson;

public interface StocktakeService {

	// summary page
	JSONArray getAllStocktakePlans (JSONObject inputJson) throws Exception;
	
	JSONObject deleteStocktakePlan (JSONObject inputJson) throws Exception;

	JSONObject lockStocktakePlan (JSONObject inputJson) throws Exception;

	// view stocktake plan page
	JSONObject getStocktakeById (JSONObject inputJson) throws Exception;
	
	JSONObject getSummaryOfStocktakeById (JSONObject inputJson) throws Exception;

	JSONObject updateStocktakeByRow (JSONObject inputJson) throws Exception;

	JSONObject clearStocktakeByRow (JSONObject inputJson) throws Exception;

	JSONObject HandleStockPlanExcelUpload(MultipartFile uploadFile, String stkPlanId, String opPageName)throws Exception;

	
}
