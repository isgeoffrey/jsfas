package jsfas.db.main.persistence.service;

import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.multipart.MultipartFile;

public interface StocktakeExcelService {

	JSONObject insertExcelToStaging (MultipartFile uploadFile, String stkPlanId, String opPageName) throws Exception;
	JSONObject downloadExcel(JSONObject inputJson) throws Exception;
}
