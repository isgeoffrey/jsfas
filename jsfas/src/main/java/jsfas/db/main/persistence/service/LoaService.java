package jsfas.db.main.persistence.service;

import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.web.multipart.MultipartFile;

import jsfas.common.json.CommonJson;
import jsfas.db.main.persistence.domain.ElApplHdrDAO;

public interface LoaService {
	public JSONArray getLoaList(String userId) throws Exception;
	public JSONArray getLoaDetailsById(String id) throws Exception;
	//public void updateLoaStatusByLoaId(String loaId, CommonJson inputJson, String opPageName) throws Exception;
	
	public JSONArray getLoaFileList(String applHdrId) throws Exception;
	public void updateLoaFileList(String loaId, CommonJson inputJSON, String opPageName) throws Exception;
	
	public void updateLoaStatusByHdrId(String applHdrId, JSONObject inputJson, String opPageName) throws Exception;
	public void loaScheduledJob(String jobUser, String op_page_nam) throws Exception;
	public JSONArray getLoaEnqiuryList(JSONObject inputJson) throws Exception;
	public void sendRemindEmail(String applHdrId, JSONObject inputJson, String opPagenam) throws Exception;
	
	public void createMyAdminTaskForReadySum(ElApplHdrDAO hdrDAO) ;
}
