package jsfas.db.main.persistence.service;

import org.json.JSONObject;

public interface StocktakeExcelGenService {

	JSONObject commitAssetGeneration(JSONObject inputJson) throws Exception;
	JSONObject rfidGeneration(JSONObject inputJson) throws Exception;
}
