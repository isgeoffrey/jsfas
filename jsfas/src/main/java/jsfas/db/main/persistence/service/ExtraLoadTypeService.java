package jsfas.db.main.persistence.service;

import org.json.JSONArray;
import org.json.JSONObject;

public interface ExtraLoadTypeService {
	// General - Get extra load type list
	public JSONArray getExtraLoadTypeList(JSONObject inputJson) throws Exception;
	
	// Get extra load type mapping list
	public JSONArray getExtraLoadTypeMappingList(JSONObject inputJson) throws Exception;
	
	// Get extra load type detail
	public JSONObject getExtraLoadTypeDetail(String elTypeId) throws Exception;
	
	// Create extra load type and mapping
	public JSONObject createExtraLoadType(JSONObject inputJson, String opPageName) throws Exception;
	
	// Update extra load type and mapping
	public JSONObject updateExtraLoadType(String elTypeId, JSONObject inputJson, String opPageName) throws Exception;
	
	// Delete extra load type mapping
	public JSONObject deleteExtraLoadTypeMapping(String elTypeSalElemntId, JSONObject inputJson, String opPageName) throws Exception;
	
}
