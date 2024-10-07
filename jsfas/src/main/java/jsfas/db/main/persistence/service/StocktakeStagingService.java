package jsfas.db.main.persistence.service;

import org.json.JSONArray;
import org.json.JSONObject;

public interface StocktakeStagingService {

	JSONObject getStagingItemsById (JSONObject inputJson) throws Exception;

	JSONObject updateStagingByRow (JSONObject inputJson) throws Exception;

	JSONObject clearStagingByRow (JSONObject inputJson) throws Exception;

	JSONObject deleteAllStagingById (JSONObject inputJson) throws Exception;
}
