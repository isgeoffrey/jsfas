package jsfas.db.main.persistence.service;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.text.DecimalFormat;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;

import jsfas.common.utils.GeneralUtil;
import jsfas.db.main.persistence.domain.ElApplCourseDAO;
import jsfas.db.main.persistence.repository.ElApplCourseRepository;
import jsfas.db.main.persistence.repository.ElApplHdrRepository;
import jsfas.db.main.persistence.repository.ElApplPymtScheduleRepository;

public class ReportEventHandler implements ReportService {
	@Autowired
	Environment env;
	
	@Autowired
	ElApplHdrRepository elApplHdrRepository;
	
	@Autowired
	ElApplPymtScheduleRepository elApplPymtScheduleRepository;
	
	@Autowired
	ElApplCourseRepository elApplCourseRepository;
	
	@Override
	public JSONArray getPaymentStatusReportList(JSONObject inputJson) throws Exception {
		JSONArray jsonArr = new JSONArray();
		
		// TODO: Validating user = FO?

		String applName = GeneralUtil.refineParam(inputJson.optString("appl_name"));
		String requesterName = GeneralUtil.refineParam(inputJson.optString("requester_name"));
		String applNbr = GeneralUtil.refineParam(inputJson.optString("appl_nbr"));
		String dept = GeneralUtil.refineParam(inputJson.optString("dept"));
		String applStatCde = GeneralUtil.refineParam(inputJson.optString("appl_stat_cde"));
		
		List<Map<String, Object>> resultMapList = elApplHdrRepository.searchPaymentStatusReport(applName, requesterName, applNbr, applStatCde, dept);	
		Integer rowNoIndex = 0;
		for (Map<String, Object> resultMap : resultMapList) {
			JSONObject jsonObj = new JSONObject();
			jsonArr.put(jsonObj);
			for (String keys : resultMap.keySet()) {
				jsonObj.put(keys.toLowerCase(), resultMap.get(keys));
			}
			jsonObj.put("rowNo", rowNoIndex.toString());
			rowNoIndex++;
		}
		
		return jsonArr;
	}
	
	@Override
	public JSONObject getPostedPaymentReportList(JSONObject inputJson) throws Exception {
		JSONObject outputJson = new JSONObject();
		JSONArray instalmJsonArr = new JSONArray();
		JSONArray recurrJsonArr = new JSONArray();
		// TODO: Validating user = FO?

		String applName = GeneralUtil.refineParam(inputJson.optString("appl_name"));
		String requesterName = GeneralUtil.refineParam(inputJson.optString("requester_name"));
		String applNbr = GeneralUtil.refineParam(inputJson.optString("appl_nbr"));
		String dept = GeneralUtil.refineParam(inputJson.optString("dept"));
		String applStatCde = GeneralUtil.refineParam(inputJson.optString("appl_stat_cde"));
		DecimalFormat decimal = new DecimalFormat("0.000000#");
		
		List<Map<String, Object>> instalmResultMapList = elApplPymtScheduleRepository.findPostedInstalmPaymentSchedulesForReport();	
		List<Map<String, Object>> recurrResultMapList = elApplPymtScheduleRepository.findPostedRecurrPaymentSchedulesForReport();	
		
		Integer rowNoIndex = 1;
		for (Map<String, Object> resultMap : instalmResultMapList) {
			JSONObject jsonObj = new JSONObject();
			
			
			String brFullNo =  GeneralUtil.isBlankString(resultMap.get("BR_NO").toString().trim()) ?
					null :
					resultMap.get("BR_NO").toString()
					+ "-"
					+ String.format("%02d", ((BigDecimal) resultMap.get("BR_LINE_NO")).intValue())
					+ String.format("%02d", ((BigDecimal) resultMap.get("BR_DIST_LINE_NO")).intValue());
			
			List<ElApplCourseDAO> eacDAOlist = elApplCourseRepository.findByApplHdrId(resultMap.get("appl_hdr_id").toString());
			String dsc = "";
			if(eacDAOlist != null && eacDAOlist.size()>0) {
				for(ElApplCourseDAO eacDAO : eacDAOlist) {
					if(!dsc.isEmpty()) {
						dsc += "," + eacDAO.getCrseCde();
					}else {
						dsc = eacDAO.getCrseCde();
					}
				}
				dsc.substring(0, Math.min(dsc.length(), 30));
			}else {
				dsc = GeneralUtil.isBlankString(GeneralUtil.initNullString((String) resultMap.get("EL_TYPE_DESCR"))) ? null :resultMap.get("EL_TYPE_DESCR").toString();
			}
			
			jsonObj.put("chng_dat", resultMap.get("CHNG_DAT"));
			jsonObj.put("appl_nbr", resultMap.get("APPL_NBR"));
			jsonObj.put("appl_user_emplid", resultMap.get("APPL_USER_EMPLID"));
			jsonObj.put("emp_rec_nbr", resultMap.getOrDefault("EMPL_NBR", "999"));
			jsonObj.put("sal_element", GeneralUtil.isBlankString(GeneralUtil.initNullString((String) resultMap.get("SAL_ELEMNT"))) ? null :resultMap.get("SAL_ELEMNT").toString() +" HKG");
			jsonObj.put("appl_start_dt", resultMap.get("APPL_START_DT"));
			jsonObj.put("appl_end_dt", resultMap.get("APPL_END_DT"));
			jsonObj.put("currency", "HKD");
			jsonObj.put("pymt_line_amt", resultMap.get("PYMT_LINE_AMT"));
			if(!GeneralUtil.isNullTimestamp((Timestamp) resultMap.get("PYMT_REV_START_DT"))) {
				jsonObj.put("pymt_start_dt", resultMap.get("PYMT_REV_START_DT"));
			} else {
				jsonObj.put("pymt_start_dt", resultMap.get("PYMT_START_DT"));
			}
			if(!GeneralUtil.isNullTimestamp((Timestamp) resultMap.get("PYMT_REV_END_DT"))) {
				jsonObj.put("pymt_end_dt", resultMap.get("PYMT_REV_END_DT"));
			} else {
				jsonObj.put("pymt_end_dt", resultMap.get("PYMT_END_DT"));
			}
			jsonObj.put("description", dsc);
			jsonObj.put("acct_cde", resultMap.get("ACCT_CDE"));
			jsonObj.put("analysis_cde", resultMap.get("ANALYSIS_CDE"));
			jsonObj.put("fund_cde", resultMap.get("FUND_CDE"));
			jsonObj.put("dept_id", resultMap.get("DEPT_ID"));
			jsonObj.put("proj_id", resultMap.get("PROJ_ID"));
			jsonObj.put("class_cde", resultMap.get("CLASS_CDE"));
			jsonObj.put("br_no", brFullNo);
			jsonObj.put("rowNo", rowNoIndex.toString());
			
			rowNoIndex++;
			instalmJsonArr.put(jsonObj);
		}
		
		rowNoIndex = 1;
		
		for (Map<String, Object> resultMap : recurrResultMapList) {
			JSONObject jsonObj = new JSONObject();
			
			String brFullNo =  GeneralUtil.isBlankString(resultMap.get("BR_NO").toString().trim()) ?
					null :
					resultMap.get("BR_NO").toString() 
					+ "-"
					+ String.format("%02d", ((BigDecimal) resultMap.get("BR_LINE_NO")).intValue())
					+ String.format("%02d", ((BigDecimal) resultMap.get("BR_DIST_LINE_NO")).intValue());
						
			jsonObj.put("chng_dat", resultMap.get("CHNG_DAT"));
			jsonObj.put("appl_nbr", resultMap.get("APPL_NBR"));
			jsonObj.put("appl_user_emplid", resultMap.get("APPL_USER_EMPLID"));
			jsonObj.put("emp_rec_nbr", resultMap.getOrDefault("EMPL_NBR", "999"));
			jsonObj.put("sal_element", GeneralUtil.isBlankString(GeneralUtil.initNullString((String) resultMap.get("SAL_ELEMNT"))) ? null :resultMap.get("SAL_ELEMNT").toString() +" HKG");
			jsonObj.put("appl_start_dt", resultMap.get("APPL_START_DT"));
			jsonObj.put("appl_end_dt", resultMap.get("APPL_END_DT"));
			jsonObj.put("currency", "HKD");
			jsonObj.put("pymt_line_amt", resultMap.get("PYMT_LINE_AMT"));
			if(!GeneralUtil.isNullTimestamp((Timestamp) resultMap.get("PYMT_REV_START_DT"))) {
				jsonObj.put("pymt_start_dt", resultMap.get("PYMT_REV_START_DT"));
			} else {
				jsonObj.put("pymt_start_dt", resultMap.get("PYMT_START_DT"));
			}
			if(!GeneralUtil.isNullTimestamp((Timestamp) resultMap.get("PYMT_REV_END_DT"))) {
				jsonObj.put("pymt_end_dt", resultMap.get("PYMT_REV_END_DT"));
			} else {
				jsonObj.put("pymt_end_dt", resultMap.get("PYMT_END_DT"));
			}
			jsonObj.put("acct_cde", resultMap.get("ACCT_CDE"));
			jsonObj.put("analysis_cde", resultMap.get("ANALYSIS_CDE"));
			jsonObj.put("fund_cde", resultMap.get("FUND_CDE"));
			jsonObj.put("dept_id", resultMap.get("DEPT_ID"));
			jsonObj.put("proj_id", resultMap.get("PROJ_ID"));
			jsonObj.put("class_cde", resultMap.get("CLASS_CDE"));
			jsonObj.put("rowNo", rowNoIndex.toString());
			
			rowNoIndex++;
			
			jsonObj.put("br_no", brFullNo);
			
			recurrJsonArr.put(jsonObj);
		}
		
		outputJson.put("instalm", instalmJsonArr);
		outputJson.put("recurr", recurrJsonArr);
		
		return outputJson;
	}
}
