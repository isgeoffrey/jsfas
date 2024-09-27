package jsfas.db.main.persistence.service;

import org.json.JSONArray;
import org.json.JSONObject;

public interface ReportService {

	JSONArray getPaymentStatusReportList(JSONObject inputJson) throws Exception;

	JSONObject getPostedPaymentReportList(JSONObject inputJson) throws Exception;

}
