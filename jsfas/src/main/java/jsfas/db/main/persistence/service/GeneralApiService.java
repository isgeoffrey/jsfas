package jsfas.db.main.persistence.service;

import org.json.JSONArray;

public interface GeneralApiService {
	
	// Get Department List
	public JSONArray getDeptList() throws Exception;
	
	//Get status for stocktake header
	public JSONArray getStocktakePlanStatus() throws Exception;
	
}
