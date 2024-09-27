package jsfas.db.main.persistence.service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import org.apache.commons.lang.StringUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.client.OAuth2RestTemplate;
import org.springframework.transaction.annotation.Transactional;

import jsfas.common.constants.PymtTypeConstants;
import jsfas.common.exception.InvalidDateException;
import jsfas.common.exception.InvalidParameterException;
import jsfas.common.json.CommonJson;
import jsfas.common.object.CoaObject;
import jsfas.common.utils.GeneralUtil;
import jsfas.db.main.persistence.domain.ElAcadTermVDAO;
import jsfas.db.main.persistence.domain.ElAcctChrtVDAO;
import jsfas.db.main.persistence.domain.ElAnalChrtVDAO;
import jsfas.db.main.persistence.domain.ElApplActDAO;
import jsfas.db.main.persistence.domain.ElApplHdrDAO;
import jsfas.db.main.persistence.domain.ElApplPymtScheduleDAO;
import jsfas.db.main.persistence.domain.ElApplStatTabDAO;
import jsfas.db.main.persistence.domain.ElBcoAprvVDAO;
import jsfas.db.main.persistence.domain.ElCategoryTabDAO;
import jsfas.db.main.persistence.domain.ElClassChrtVDAO;
import jsfas.db.main.persistence.domain.ElDeptChrtVDAO;
import jsfas.db.main.persistence.domain.ElInpostStaffImpVDAO;
import jsfas.db.main.persistence.domain.ElProjChrtVDAO;
import jsfas.db.main.persistence.domain.ElPymtTypeTabDAO;
import jsfas.db.main.persistence.domain.ElSalElementVDAO;
import jsfas.db.main.persistence.domain.ElSchlChrtVDAO;
import jsfas.db.main.persistence.repository.ElAcadPlanVRepository;
import jsfas.db.main.persistence.repository.ElAcadTermVRepository;
import jsfas.db.main.persistence.repository.ElAcctChrtVRepository;
import jsfas.db.main.persistence.repository.ElAnalChrtVRepository;
import jsfas.db.main.persistence.repository.ElApplActRepository;
import jsfas.db.main.persistence.repository.ElApplHdrHistRepository;
import jsfas.db.main.persistence.repository.ElApplHdrRepository;
import jsfas.db.main.persistence.repository.ElApplStatTabRepository;
import jsfas.db.main.persistence.repository.ElBcoAprvVRepository;
import jsfas.db.main.persistence.repository.ElCategoryTabRepository;
import jsfas.db.main.persistence.repository.ElClassChrtVRepository;
import jsfas.db.main.persistence.repository.ElCrseClassVRepository;
import jsfas.db.main.persistence.repository.ElDeptChrtVRepository;
import jsfas.db.main.persistence.repository.ElFundChrtVRepository;
import jsfas.db.main.persistence.repository.ElInpostStaffImpVRepository;
import jsfas.db.main.persistence.repository.ElParamTabRepository;
import jsfas.db.main.persistence.repository.ElProjChrtVRepository;
import jsfas.db.main.persistence.repository.ElPymtTypeTabRepository;
import jsfas.db.main.persistence.repository.ElSalElementVRepository;
import jsfas.db.main.persistence.repository.ElSchlChrtVRepository;
import jsfas.security.SecurityUtils;

public class GeneralApiEventHandler implements GeneralApiService {

	@Inject
	Environment env;

	@Autowired
	ElCategoryTabRepository elCategoryTabRepository;

	@Autowired
	ElPymtTypeTabRepository elPymtTypeTabRepository;

	@Autowired
	ElAcctChrtVRepository elAcctChrtVRepository;

	@Autowired
	ElAnalChrtVRepository elAnalChrtVRepository;

	@Autowired
	ElFundChrtVRepository elFundChrtVRepository;

	@Autowired
	ElProjChrtVRepository elProjChrtVRepository;

	@Autowired
	ElClassChrtVRepository elClassChrtVRepository;

	@Autowired
	ElAcadTermVRepository elAcadTermVRepository;

	@Autowired
	ElSalElementVRepository elSalElementVRepository;

	@Autowired
	ElInpostStaffImpVRepository elInpostStaffImpVRepository;

	@Autowired
	ElBcoAprvVRepository elBcoAprvVRepository;

	@Autowired
	ElDeptChrtVRepository elDeptChrtVRepository;

	@Autowired
	ElCrseClassVRepository elCrseClassVRepository;

	@Autowired
	ElAcadPlanVRepository elAcadPlanVRepository;

	@Autowired
	ElApplStatTabRepository elApplStatTabRepository;
	
	@Autowired
	ElApplHdrRepository elApplHdrRepository;
	
	@Autowired
	ElApplActRepository elApplActRepository;
	
	@Autowired
	ElApplHdrHistRepository elApplHdrHistRepository;

	@Autowired
	ElParamTabRepository elParamTabRepository;
	
	@Autowired
	ElSchlChrtVRepository elSchlChrtVRepository;
	
	@Autowired
	private OAuth2RestTemplate jssvIsoOAuth2RestTemplate;
	
	private final Logger log = LoggerFactory.getLogger(this.getClass());

	@Override
	public JSONArray getStaffList(JSONObject inputJson) throws Exception {
		// TODO Need review just for init development
		JSONArray outputJson = new JSONArray();

		// Get user input
		String searchKey = inputJson.optString("username");
		
		// allow search applicant or all staff, default all staff
		String type = inputJson.optString("type", "staff");

		// At least three characters
		if (searchKey.isBlank() || searchKey.length() < 3) {
			throw new InvalidParameterException("Please input at least 3 characters");
		}
		
		if(type.equals("staff")) {
			List<Map<String, Object>> resultMapList = elInpostStaffImpVRepository.searchAllStaff(searchKey);
			
			for (Map<String, Object> resultMap : resultMapList) {
				JSONObject jsonObj = new JSONObject();
				outputJson.put(jsonObj);
				
				jsonObj.put("user_id", resultMap.get("user_nam"));
				jsonObj.put("user_name", resultMap.get("display_nam"));
			}
			
		} else if(type.equals("applicant")) {
			List<Map<String, Object>> resultMapList = elInpostStaffImpVRepository.searchAllApplicant(searchKey);
			
			for (Map<String, Object> resultMap : resultMapList) {
				String role = " ";
				switch((String) resultMap.get("job_catg_cde")){
					case "A010" :
					case "A020" :
					case "A030" :
					case "A040":
						role = "Senior";
						break;
					case "R020" :
					case "I020" :
						role = "Support";
						break;
					default :
						role = "Support";
				}
				
				
				JSONObject jsonObj = new JSONObject();
				outputJson.put(jsonObj);
				
				jsonObj.put("user_id", resultMap.get("user_nam"));
				jsonObj.put("user_name", resultMap.get("display_nam"));
				// TODO: change
				String tos = GeneralUtil.initBlankString(resultMap.get("tos") != null ? resultMap.get("tos").toString() : "");
				Boolean defaultMpfCommit = true;
				// tos = a , default no mpf commitment 
				if(tos.equals("A"))
					defaultMpfCommit = false;
				
				jsonObj.put("defaultMpfCommit", defaultMpfCommit);
				
				jsonObj.put("role", role);
				jsonObj.put("dept", resultMap.get("dept"));
				jsonObj.put("dept_id", resultMap.get("dept_id"));
			}
		}
		
		return outputJson;
	}

	@Override
	public JSONArray getStaffListByUserId(String userId, String type) throws Exception {
		JSONArray outputJson = new JSONArray();
		String primaryInd = "Y";
		String acadNonAcadInd = "%";
		
		if(type.equals("staff")) {
			// no change
		} 
		else if(type.equals("applicant")) {
			primaryInd = "%";
			acadNonAcadInd = "A";
		}

		List<Map<String, Object>> resultList = elInpostStaffImpVRepository.findByUserIdAndPrimaryIndAndAcadNonAcadInd(userId, primaryInd, acadNonAcadInd);

		for (Map<String, Object> result : resultList) {
			JSONObject jsonObj = new JSONObject();
			outputJson.put(jsonObj);

			jsonObj
					.put("emplid", result.get("emplid"))
					.put("user_id", result.get("user_nam"))
					.put("display_nam", result.get("display_nam"))
					.put("dept_id", result.get("dept_id"))
					.put("job_catg_cde", result.get("job_catg_cde"));
		}

		return outputJson;
	}
	
	// TBC
	@Override
	public JSONArray getApplicantListByUserId(String userId) throws Exception {
		JSONArray outputJson = new JSONArray();

		List<Map<String, Object>> resultList = elInpostStaffImpVRepository.findApplicantByUserId(userId);

		for (Map<String, Object> result : resultList) {
			JSONObject jsonObj = new JSONObject();
			outputJson.put(jsonObj);

			jsonObj
					.put("emplid", result.get("emplid"))
					.put("user_id", result.get("user_nam"))
					.put("display_nam", result.get("display_nam"))
					.put("dept_id", result.get("dept_id"))
					.put("job_catg_cde", result.get("job_catg_cde"));
		}

		return outputJson;
	}

	@Override
	public JSONArray getProgramList(JSONObject inputJson) throws Exception {
		JSONArray outputJson = new JSONArray();

		// Get params
		String searchkey = inputJson.optString("prgm");

		List<Map<String, Object>> resultList = elAcadPlanVRepository.searchPrgm(searchkey);

		for (Map<String, Object> result : resultList) {
			JSONObject jsonObj = new JSONObject();
			outputJson.put(jsonObj);

			jsonObj
					.put("prgm_cde", result.get("acad_plan"))
					.put("prgm_descr", result.get("acad_plan_descr"))
					.put("sch_cde", result.get("acad_group"))
					.put("dept", result.get("acad_org"))
					.put("dept_descr_short", result.get("acad_org_descr_short"));
		}

		return outputJson;
	}

	@Override
	public JSONArray getCourseList(JSONObject inputJson) throws Exception {
		JSONArray outputJson = new JSONArray();

		// Get params
		String startStrm = inputJson.optString("start_strm");
		String endStrm = inputJson.optString("end_strm");
		
		if (endStrm.isBlank()) endStrm = startStrm;
		
		List<ElAcadTermVDAO> strmDaoList = elAcadTermVRepository.findWithinStartAndEndTerm(startStrm, endStrm);
		
		for(ElAcadTermVDAO strmDao : strmDaoList) {
			List<Map<String, Object>> resultList = elCrseClassVRepository.findCourseDetailsByStrm(strmDao.getStrm());
			
			// valid course from SIS
			if(resultList.size() > 0) {
				for (Map<String, Object> result : resultList) {
					JSONObject jsonObj = new JSONObject()
							.put("strm", result.get("strm"))
							.put("crse_id", result.get("crse_id"))
							.put("crse_cde", result.get("crse_cde"))
							//.put("crse_descr", result.get("crse_title"))
							.put("section", result.get("class_section"))
							.put("credit", result.get("units_acad_prog"))
							.put("yr_term_desc", result.get("yr_term_desc"))
							;

					outputJson.put(jsonObj);
				}
			} 
			else {
				// get course list from previous 2 year
				List<Map<String, Object>> prevousCrseResultList = elCrseClassVRepository.findPreviousCourseDetailsByStrm(strmDao.getStrm());
				for (Map<String, Object> result : prevousCrseResultList) {
					JSONObject jsonObj = new JSONObject()
							.put("strm", strmDao.getStrm())
							.put("crse_id", result.get("crse_id"))
							.put("crse_cde", result.get("crse_cde"))
							//.put("crse_descr", result.get("crse_title"))
							.put("section", "-")
							.put("credit", -1)
							.put("yr_term_desc", strmDao.getYrTermDesc())
							;

					outputJson.put(jsonObj);
				}
			}
			
		}

		return outputJson;
	}

	@Override
	public JSONObject getCoCourseList(JSONObject inputJson) throws Exception {
		// Get params
		String strm = inputJson.optString("strm").trim();
		String crseId = inputJson.optString("crse_id").trim();
		
		Map<String, Object> result = elCrseClassVRepository.findCoCouseByStrmAndCrseId(strm, crseId);
		JSONObject jsonObj =  new JSONObject();
		
		String coListCrseIds = "";
		if (!result.isEmpty()) {
			coListCrseIds = (String) result.get("colist_crse_ids");			
		}
		
		jsonObj
		.put("strm", strm)
		.put("crse_id", crseId)
		.put("colist_crse_ids", coListCrseIds.isBlank() ? new JSONArray() : coListCrseIds.split("; "));
		
		return jsonObj;
	}
	@Override
	public JSONArray getPaymentTypeList(JSONObject inputJson) throws Exception {
		JSONArray outputJson = new JSONArray();

		List<ElPymtTypeTabDAO> daoList = elPymtTypeTabRepository.findAll();

		for (ElPymtTypeTabDAO dao : daoList) {
			JSONObject jsonObj = new JSONObject()
					.put("pymt_type_cde", dao.getPymtTypeCde())
					.put("pymt_type_descr", dao.getPymtTypeDescr())
					.put("mod_ctrl_txt", dao.getModCtrlTxt());
			outputJson.put(jsonObj);
		}

		return outputJson;
	}

	@Override
	public JSONArray getCategoryList(JSONObject inputJson) throws Exception {
		JSONArray outputJson = new JSONArray();

		// default % (all)
		String obsolete = inputJson.optString("obsolete", "%");

		List<ElCategoryTabDAO> daoList = elCategoryTabRepository.findByObsolete(obsolete);

		for (ElCategoryTabDAO dao : daoList) {
			JSONObject jsonObj = new JSONObject()
					.put("category_cde", dao.getCategoryCde())
					.put("category_descr", dao.getCategoryDescr())
					.put("obsolete", dao.getObsolete())
					.put("mod_ctrl_txt", dao.getModCtrlTxt());
			outputJson.put(jsonObj);
		}

		return outputJson;
	}

	@Override
	public JSONArray getAccountCodeList(JSONObject inputJson) throws Exception {
		JSONArray outputJson = new JSONArray();

		List<ElAcctChrtVDAO> daoList = elAcctChrtVRepository.findAll();

		for (ElAcctChrtVDAO dao : daoList) {
			JSONObject jsonObj = new JSONObject()
					.put("acct_cde", dao.getAcctChrt());
			outputJson.put(jsonObj);
		}

		return outputJson;
	}

	@Override
	public JSONArray getAnalysisCodeList(JSONObject inputJson) throws Exception {
		JSONArray outputJson = new JSONArray();

		List<ElAnalChrtVDAO> daoList = elAnalChrtVRepository.findAll();

		for (ElAnalChrtVDAO dao : daoList) {
			JSONObject jsonObj = new JSONObject()
					.put("analysis_cde", dao.getAnalChrt());
			outputJson.put(jsonObj);
		}

		return outputJson;
	}

	@Override
	public JSONArray getFundCodeList(JSONObject inputJson) throws Exception {
		JSONArray outputJson = new JSONArray();

		// Get params
		String deptId = inputJson.optString("dept_id");
		String projId = inputJson.optString("proj_id");

		List<Map<String, Object>> resultList = elFundChrtVRepository.findByDeptIdOrProjId(deptId, projId);

		for (Map<String, Object> result : resultList) {
			JSONObject jsonObj = new JSONObject()
					.put("fund_cde", result.get("fund_cde"))
					.put("fund_long_desc", result.get("fund_long_desc"));
			outputJson.put(jsonObj);
		}

		return outputJson;
	}

	@Override
	public JSONArray getProjIdList(JSONObject inputJson) throws Exception {
		JSONArray outputJson = new JSONArray();

		List<ElProjChrtVDAO> daoList = elProjChrtVRepository.findAll();

		for (ElProjChrtVDAO dao : daoList) {
			JSONObject jsonObj = new JSONObject()
					.put("proj_id", dao.getProjId())
					.put("proj_nbr", dao.getProjNbr());
			outputJson.put(jsonObj);
		}

		return outputJson;
	}

	@Override
	public JSONArray getClassCodeList(JSONObject inputJson) throws Exception {
		JSONArray outputJson = new JSONArray();

		// get params
		String projId = inputJson.optString("proj_id");
		String fundCde = inputJson.optString("fund_cde");

		ElProjChrtVDAO elProjChrtVDAO = elProjChrtVRepository.findOne(projId);

		// If Project ID is not entered or Class Code is not required, then, the view
		// for Class Code selection would be PS_ZR_EL_CLASS_V
		if (elProjChrtVDAO == null || "N".equals(elProjChrtVDAO.getClassRequired())) {
			List<ElClassChrtVDAO> daoList = elClassChrtVRepository.findAll();

			for (ElClassChrtVDAO dao : daoList) {
				JSONObject jsonObj = new JSONObject()
						.put("class_cde", dao.getClassChrt());
				outputJson.put(jsonObj);
			}
		}
		// filter the record from PS_ZR_EL_BDLDGR_V with PROJECT_ID and FUND_CODE
		// specified
		else {
			List<Map<String, Object>> resultList = elFundChrtVRepository.findClassCdeByProjIdAndFundCde(projId,
					fundCde);

			for (Map<String, Object> result : resultList) {
				JSONObject jsonObj = new JSONObject()
						.put("class_cde", result.get("class_cde"));
				outputJson.put(jsonObj);
			}
		}

		return outputJson;
	}

	@Override
	public JSONArray getDeptIdList(JSONObject inputJson) throws Exception {
		JSONArray outputJson = new JSONArray();

		List<ElDeptChrtVDAO> daoList = elDeptChrtVRepository.findAll();

		for (ElDeptChrtVDAO dao : daoList) {
			JSONObject jsonObj = new JSONObject()
					.put("dept_id", dao.getDeptId());
			outputJson.put(jsonObj);
		}

		return outputJson;
	}

	@Override
	public JSONArray getAcadTermList(JSONObject inputJson) throws Exception {
		JSONArray outputJson = new JSONArray();

		List<ElAcadTermVDAO> daoList = elAcadTermVRepository.findAll();

		for (ElAcadTermVDAO dao : daoList) {
			JSONObject jsonObj = new JSONObject()
					.put("strm", dao.getStrm())
					.put("acad_yr", dao.getAcadYr())
					.put("acad_yr_desc", dao.getAcadYrDesc())
					.put("yr_term_desc", dao.getYrTermDesc());
//					.put("term_begin_dat", dao.getTermBeginDat())
//					.put("term_end_dat", dao.getTermEndDat());
			outputJson.put(jsonObj);
		}

		return outputJson;
	}
	
	@Override
	public JSONArray getSchlList(JSONObject inputJson) throws Exception {
		JSONArray outputJson = new JSONArray();

		List<Map<String,Object>> daoList = elSchlChrtVRepository.findAllDistinctSorted();

		for (Map<String,Object> dao : daoList) {
			JSONObject jsonObj = new JSONObject()
					.put("schl_id", dao.get("schl_id"))
					.put("schl_short_desc", dao.get("schl_short_desc"))
					.put("schl_long_desc", dao.get("schl_long_desc"));
			outputJson.put(jsonObj);
		}

		return outputJson;
	}

	@Override
	public JSONArray getSalElementList(JSONObject inputJson) throws Exception {
		JSONArray outputJson = new JSONArray();

		List<ElSalElementVDAO> daoList = elSalElementVRepository.findAll();

		for (ElSalElementVDAO dao : daoList) {
			JSONObject jsonObj = new JSONObject()
					.put("sal_element_cde", dao.getSalElementCde())
					.put("sal_element_desc", dao.getSalElementDesc());
			outputJson.put(jsonObj);
		}

		return outputJson;
	}

	@Override
	public JSONArray getBCOAprvList(JSONObject inputJson) throws Exception {
		JSONArray outputJson = new JSONArray();

		// Get search param
		String id = inputJson.optString("id");
		String idType = inputJson.optString("dept_proj_ind");

		if (id.isBlank()) {
			throw new InvalidParameterException("Missing required parameter proj_id or dept_id");
		}

		List<Map<String, Object>> resultMapList = elBcoAprvVRepository.findByIdandType(id, idType);

		for (Map<String, Object> resultMap : resultMapList) {
			JSONObject jsonObj = new JSONObject();
			outputJson.put(jsonObj);

			for (String keys : resultMap.keySet()) {
				jsonObj.put(keys.toLowerCase(), resultMap.get(keys));
			}
		}

		return outputJson;
	}

	@Override
	public JSONArray getBudgetCheckResult(JSONArray inputJsonArr) throws Exception {
		JSONArray outputList = new JSONArray();

		String jssvEndpoint = env.getProperty("jssv.link");
		String fmsBudgetCheckApiUrl = jssvEndpoint + "/fms/fms_budget_check";
		ResponseEntity<CommonJson> response = null;

		try {
			JSONArray coaLineJsonArr = new JSONArray();

			for (int i = 1; i <= inputJsonArr.length(); i++) {
				JSONObject inputObj = inputJsonArr.getJSONObject(i - 1);

				coaLineJsonArr.put(
						new JSONObject().put("businessUnitGL", "HKUST")
								.put("account", GeneralUtil.initBlankString(inputObj.optString("acct_cde")))
								.put("chartfield1", GeneralUtil.initBlankString(inputObj.optString("analysis_cde")))
								.put("fundCode", GeneralUtil.initBlankString(inputObj.optString("fund_cde")))
								.put("deptid", GeneralUtil.initBlankString(inputObj.optString("dept_id")))
								.put("projectid", GeneralUtil.initBlankString(inputObj.optString("proj_id")))
								.put("classFld", GeneralUtil.initBlankString(inputObj.optString("class")))
								.put("lineRef", String.valueOf(i))
								.put("budgetDt", " ")
								.put("amount", new BigDecimal(inputObj.optDouble("pymt_amt")).toString()));
			}
			log.info(coaLineJsonArr.toString());

			Map<String, Object> inputJson = new HashMap<>();
			inputJson.put("coaLineList", GeneralUtil.jsonArrayToCommonJson(coaLineJsonArr));

			log.info("input coaLineList json for API request: {}", inputJson.toString());

			HttpHeaders headers = new HttpHeaders();
			headers.setContentType(MediaType.APPLICATION_JSON);

			HttpEntity<Map<String, Object>> request = new HttpEntity<>(inputJson, headers);

			response = jssvIsoOAuth2RestTemplate.postForEntity(fmsBudgetCheckApiUrl, request, CommonJson.class);

			log.debug("response status: {}", response.getStatusCode());
			log.debug("response data: {}", response.getBody().props());

			if (!response.getStatusCode().equals(HttpStatus.OK)) {
				log.error("bad response from API: {}", fmsBudgetCheckApiUrl);
				log.error("response data: {}", response.getBody().props());
			} else {
				if (response.getBody().get("status").equals("200")) {

					CommonJson responseData = response.getBody();
					JSONObject responseJSONObj = GeneralUtil.commonJsonToJsonObject(responseData);
					log.debug(responseJSONObj.toString(0));

					JSONArray lineResults = responseJSONObj.getJSONObject("commonJson").getJSONObject("Result")
							.getJSONArray("lineResults");
					log.debug(lineResults.toString(0));

					return lineResults;
				}
			}

		} catch (Exception e) {
			log.error("fail to get response from API: {}", fmsBudgetCheckApiUrl);
			log.error("error Message: {}", e.toString());
		}

		return outputList;
	}

	@Override
	public JSONArray getApplStatusList(JSONObject inputJson) throws Exception {
		JSONArray outputJson = new JSONArray();
		
		List<ElApplStatTabDAO> daoList = elApplStatTabRepository.findAllSortByDescr();

		for (ElApplStatTabDAO dao : daoList) {
			outputJson.put(
					new JSONObject()
							.put("appl_stat_cde", dao.getApplStatCde())
							.put("appl_stat_descr", dao.getApplStatDescr()));
		}
		return outputJson;
	}

	@Override
	public BigDecimal calculateTotalAmountFromApi(JSONObject inputJson) throws Exception {
		// BigDecimal
		BigDecimal totalAmt = new BigDecimal(0);
		JSONObject pymtMethod = inputJson;
		JSONArray pymtSchedule = pymtMethod.optJSONArray("pymt_schedule");
		
		if (pymtMethod.optString("pymt_type_cde").isBlank()) {
			throw new InvalidParameterException();
		}
		
		if (pymtMethod.getString("pymt_type_cde").equals("RECURR")) {
			for (int i = 0; i < pymtSchedule.length(); i++) {
				JSONObject scheduleObj = pymtSchedule.optJSONObject(i);

				// BigDecimal
				BigDecimal calculatedMonthlyAmount = new BigDecimal(0);

				// temp
				// BigDecimal
				BigDecimal monthlyAmount = new BigDecimal(0);

				Long startDtLong = scheduleObj.optLong("pymt_start_dt");
				Long endDtLong = scheduleObj.optLong("pymt_end_dt");

				Timestamp pymtStartDt = GeneralUtil.initNullTimestampFromLong(startDtLong);
				Timestamp pymtEndDt = GeneralUtil.initNullTimestampFromLong(endDtLong);

				Calendar startCal = Calendar.getInstance();
				startCal.setTimeInMillis(startDtLong);
				int startDay = GeneralUtil.getTimestampField(pymtStartDt, Calendar.DAY_OF_MONTH);
				int startMonth = GeneralUtil.getTimestampField(pymtStartDt, Calendar.MONTH);
				int startYear = GeneralUtil.getTimestampField(pymtStartDt, Calendar.YEAR);
				int countStartMthYear = startYear * 12 + startMonth;

				Calendar endCal = Calendar.getInstance();
				endCal.setTimeInMillis(endDtLong);
				int endDay = GeneralUtil.getTimestampField(pymtEndDt, Calendar.DAY_OF_MONTH);
				int endMonth = GeneralUtil.getTimestampField(pymtEndDt, Calendar.MONTH);
				int endYear = GeneralUtil.getTimestampField(pymtEndDt, Calendar.YEAR);
				int countEndMthYear = endYear * 12 + endMonth;
				for (int currentMonth = countStartMthYear; currentMonth <= countEndMthYear; currentMonth++) {

					JSONArray detailArr = scheduleObj.optJSONArray("details");
					for (int j = 0; j < detailArr.length(); j++) {
						JSONObject detailObj = detailArr.optJSONObject(j);
						if (!Double.isNaN(detailObj.optDouble("pymt_amt"))) {
							monthlyAmount = BigDecimal.valueOf(detailObj.optDouble("pymt_amt"));
						}
						// to BigDecimal, to 2 decimal

						int currentStartDay = 0;
						int currentEndDay = 0;
						// get current month
						Calendar currentCal = Calendar.getInstance();
						currentCal.set(currentMonth / 12, (currentMonth - 1) % 12, 1);

						// get start date (occupied)
						if (currentMonth == countStartMthYear) {
							currentStartDay = startDay;
						} else {
							currentStartDay = 1;
						}
						// get end date (occupied)
						if (currentMonth == countEndMthYear) {
							currentEndDay = endDay;
						} else {
							currentEndDay = currentCal.getActualMaximum(Calendar.DAY_OF_MONTH);
						}
						// get 1st date of the month
						// get last date of the month
						// get total month dates (non-occupied)
						// get occupied date
						int occupiedDate = currentEndDay - currentStartDay + 1;

						// BigDecimal
						BigDecimal dayPercent = new BigDecimal(occupiedDate).divide(
								new BigDecimal(currentCal.getActualMaximum(Calendar.DAY_OF_MONTH)), 10,
								RoundingMode.HALF_UP);

						calculatedMonthlyAmount = dayPercent.multiply(monthlyAmount).setScale(2, RoundingMode.HALF_UP);

						totalAmt = totalAmt.add(calculatedMonthlyAmount);
						// end of for loop of detailArr
					}
					// end of for loop of pymtSchedule
				} // for
			}
		} else {
			for (int i = 0; i < pymtSchedule.length(); i++) {
				JSONObject scheduleObj = pymtSchedule.optJSONObject(i);
				JSONArray detailArr = scheduleObj.optJSONArray("details");
				for (int j = 0; j < detailArr.length(); j++) {
					JSONObject detailObj = detailArr.optJSONObject(j);
					if (!Double.isNaN(detailObj.optDouble("pymt_amt"))) {
						totalAmt = totalAmt.add(new BigDecimal(detailObj.optDouble("pymt_amt")));
					}
				}
			}
		}
		return totalAmt.setScale(2, RoundingMode.HALF_UP);
	}

	@Override
	public JSONArray calculateMPF(JSONObject inputJson) throws Exception {
//		keep for debugging
//		System.out.println(inputJson.toString(3));
		/*
		JSONArray outputJson = new JSONArray(); // final response
		JSONArray pymtSchedule = inputJson.optJSONArray("pymt_schedule"); // get list of pymt_schedule
		String pymtTypeCde = GeneralUtil.initNullString(inputJson.optString("pymt_type_cde"));
		JSONObject objectOfMonths = new JSONObject(); // to map pymt_dates to month in installment
		BigDecimal monthlyAmount; // sum all the pymt in a month to get monthlAmount

		if (pymtTypeCde.isBlank()) {
			log.error("calculateMPF a pymt_type_cde is not valid");
			throw new InvalidParameterException("Please provide a payment method");
		}

		for (int i = 0; i < pymtSchedule.length(); i++) { // loop through payment schedule (for installment mostly)
			monthlyAmount = GeneralUtil.initNullBigDecimal(""); // reinit to 0
			JSONObject pymt = pymtSchedule.optJSONObject(i); // a single unit of pymtSchedule
			JSONObject pymtsInMonth = new JSONObject().put("monthlyAmount", new BigDecimal(0)).put("details",
					new JSONArray()); // should be my target object to put into objectOfMonths

			Timestamp pymtStartDate = GeneralUtil.initNullTimestampFromLong(pymt.optLong("pymt_start_dt"));
			Timestamp pymtEndDate = GeneralUtil.initNullTimestampFromLong(pymt.optLong("pymt_end_dt"));

			if (pymtStartDate == GeneralUtil.NULLTIMESTAMP) { // validate pymtstartdate regardless
				log.error("calculateMPF a pymt_start_dt is not valid");
				throw new InvalidParameterException("Please provide valid start date");
			}

			JSONArray pymtDetails = pymt.optJSONArray("details"); // get details from single unit of pymt

			for (int j = 0; j < pymtDetails.length(); j++) { // loop through all details
				JSONObject detail = pymtDetails.optJSONObject(j); // single unit of detail from single unit of pymt

				JSONObject coaPymtDetail = new JSONObject();
				JSONObject coa = new JSONObject();
				Double pymt_amt;

				if (!Double.isNaN(detail.optDouble("pymt_amt")) && detail.optDouble("pymt_amt") > 0) {
					pymt_amt = detail.optDouble("pymt_amt"); // get the pymt_amt in detail
					monthlyAmount = monthlyAmount.add(new BigDecimal(pymt_amt));
				} else {
					log.error("calculateMPF a pymt_amt is not valid");
					throw new InvalidParameterException("Please provide valid amount within two decimal points");
				}
				
				// delete null analysis_cde
				if(detail.has("analysis_cde") &&  detail.opt("analysis_cde").equals(null)) {
					detail.remove("analysis_cde");
				}

				if(detail.has("acct_cde") && detail.opt("acct_cde").equals(null)) {
					detail.remove("acct_cde");
				}
				
				if(detail.has("fund_cde") && detail.opt("fund_cde").equals(null)) {
					detail.remove("fund_cde");
				}
				
				if(detail.has("class_cde") && detail.opt("class_cde").equals(null)) {
					detail.remove("class_cde");
				}
				
				if(detail.has("dept_id") && detail.opt("dept_id").equals(null)) {
					detail.remove("dept_id");
				}
				
				if(detail.has("proj_id") && detail.opt("proj_id").equals(null)) {
					detail.remove("proj_id");
				}
				
				if(detail.has("bco_aprv_id") && detail.opt("bco_aprv_id").equals(null)) {
					detail.remove("bco_aprv_id");
				}
				
				if(detail.has("bco_aprv_name") && detail.opt("bco_aprv_name").equals(null)) {
					detail.remove("bco_aprv_name");
				}
//				.put("bco_aprv_id", "isod05")
//				.put("bco_aprv_name", "TONG, Kenny")

				coa
						.put("analysis_cde", detail.opt("analysis_cde"))
						.put("acct_cde", detail.opt("acct_cde"))
						.put("fund_cde", detail.opt("fund_cde"))
						.put("class_cde", detail.opt("class"))
						.put("dept_id", detail.opt("dept_id"))
						.put("proj_id", detail.opt("proj_id"))
						.put("bco_aprv_id", detail.opt("bco_aprv_id"))
						.put("bco_aprv_name", detail.opt("bco_aprv_name"));

				coaPymtDetail // repackage the detail so that we can compare coa value easily
						.put("coa", coa)
						.put("pymt_amt", pymt_amt);

				JSONArray newDetails = pymtsInMonth.optJSONArray("details");
				newDetails.put(coaPymtDetail);

				pymtsInMonth.put("details", newDetails);
				pymtsInMonth.put("monthlyAmount", monthlyAmount);

			}

			if (pymtTypeCde.equalsIgnoreCase("INSTALM")) {
				String targetDateString= GeneralUtil.getStringByDate11(pymtStartDate);

				// add data to targetDateString
				if (!objectOfMonths.has(targetDateString)) {
					objectOfMonths.put(targetDateString, pymtsInMonth);
				} else {
					Object oldMonthlyAmount = objectOfMonths.optJSONObject(targetDateString).get("monthlyAmount");
					JSONArray oldDetailsInMonth = objectOfMonths.optJSONObject(targetDateString)
							.optJSONArray("details");

					// add monthlyAmount together
					if (oldMonthlyAmount instanceof BigDecimal) {
						oldMonthlyAmount = ((BigDecimal) oldMonthlyAmount).add(monthlyAmount);
					} else {
						// throw error to guarantee mpf accuracy
						log.error("Error during addition of monthly amount: oldMonthlyAmount is not instanceof BigDecimal");
						throw new Exception("Calculation of mpf incomplete");
					}

					// check details if same coa exists
					for (int k = 0; k < pymtsInMonth.optJSONArray("details").length(); k++) {
						Boolean noDetail = true;
						JSONObject detail = pymtsInMonth.optJSONArray("details").optJSONObject(k);
						CoaObject currentCoa = GeneralUtil.jsonObjectToCoaObject(detail.optJSONObject("coa"));
						for (int l = 0; l < oldDetailsInMonth.length(); l++) {
							// uses noDetail to check if detail already exist in old details
							if (currentCoa.isEqual(GeneralUtil
									.jsonObjectToCoaObject(oldDetailsInMonth.optJSONObject(l).optJSONObject("coa")))) {
								noDetail = false;
								BigDecimal newValue = new BigDecimal(detail.optDouble("pymt_amt"));
								newValue = newValue
										.add(new BigDecimal(oldDetailsInMonth.optJSONObject(l).optDouble("pymt_amt")));
								detail.put("pymt_amt", newValue.doubleValue());
								oldDetailsInMonth.put(l, detail);
							}
						}

						if (noDetail) {
							oldDetailsInMonth.put(pymtsInMonth.optJSONArray("details").optJSONObject(k));
						}
					}

					// after packaging new data
					objectOfMonths.put(targetDateString, new JSONObject()
							.put("details", oldDetailsInMonth)
							.put("monthlyAmount", oldMonthlyAmount));
				}
			} else if (pymtTypeCde.equalsIgnoreCase("RECURR")) {
				if (pymtEndDate == GeneralUtil.NULLTIMESTAMP) { //pymt_start_dt already checked as needed in both operations
					log.error("calculateMPF pymt_end_dt is not valid");
					throw new InvalidParameterException("Please provide valid end date");
				}

				if (pymtStartDate.after(pymtEndDate)) {
					log.error("CalculateMPF payment start date later than payment end date");
					throw new InvalidParameterException("Start date cannot be after than end date");
				}

				Calendar startCalendar = new GregorianCalendar();
				startCalendar.setTimeInMillis(pymtStartDate.getTime());
				Calendar endCalendar = new GregorianCalendar();
				endCalendar.setTimeInMillis(pymtEndDate.getTime());

				int differenceInMonths = 0;

				if (startCalendar == null || endCalendar == null) {
					log.error("calculateMPF has error when converting provided dates to Calendar");
					throw new InvalidParameterException("Please provide valid start and end date");
				} else {
					differenceInMonths = (endCalendar.get(Calendar.YEAR) * 12 + endCalendar.get(Calendar.MONTH))
							- (startCalendar.get(Calendar.YEAR) * 12 + startCalendar.get(Calendar.MONTH));
				}

				for (int k = 0; k <= differenceInMonths; k++) {
					Calendar targetDateCalendar = new GregorianCalendar();
					targetDateCalendar.setTimeInMillis(pymtStartDate.getTime());
					targetDateCalendar.add(Calendar.MONTH, k);
					DateTimeFormatter  targetDateFormat = DateTimeFormatter.ofPattern("yyyy-MM"); // takes the key used in objectOfMonths in yyyy-MM format to order correctly
					String targetDateString = targetDateFormat.format(LocalDateTime.ofInstant(targetDateCalendar.toInstant(), targetDateCalendar.getTimeZone().toZoneId()).toLocalDate());

					if (k == 0) {

						BigDecimal maxDateInStartPaymentMonth = new BigDecimal(startCalendar.getActualMaximum(Calendar.DAY_OF_MONTH));
						BigDecimal startPaymentDate = new BigDecimal(startCalendar.getActualMaximum(Calendar.DAY_OF_MONTH) - startCalendar.get(Calendar.DAY_OF_MONTH) + 1);
						BigDecimal changeRatio = startPaymentDate.divide(maxDateInStartPaymentMonth, 2, RoundingMode.HALF_UP);
						
						JSONArray newDetailsInMonth = new JSONArray();
						BigDecimal newMonthlyAmount = new BigDecimal(pymtsInMonth.optDouble("monthlyAmount"));

						newMonthlyAmount = newMonthlyAmount.multiply(changeRatio).setScale(2, RoundingMode.HALF_UP);
						
						// check details if same coa exists
						for (int l = 0; l < pymtsInMonth.optJSONArray("details").length(); l++) {
							JSONObject oldCoa = pymtsInMonth.optJSONArray("details").optJSONObject(l).optJSONObject("coa");
							BigDecimal oldPymt = new BigDecimal(pymtsInMonth.optJSONArray("details").optJSONObject(l).optDouble("pymt_amt"));
							oldPymt = oldPymt.multiply(changeRatio).setScale(2, RoundingMode.HALF_UP);
							
							newDetailsInMonth.put(new JSONObject()
									.put("coa", oldCoa)
									.put("pymt_amt", oldPymt.doubleValue())
									);
						}
						objectOfMonths.put(targetDateString, new JSONObject()
								.put("details", newDetailsInMonth)
								.put("monthlyAmount", newMonthlyAmount));
					} else if (k == differenceInMonths) {

						BigDecimal endPaymentDate = new BigDecimal(endCalendar.get(Calendar.DAY_OF_MONTH));
						BigDecimal maxDateInEndPaymentMonth = new BigDecimal(endCalendar.getActualMaximum(Calendar.DAY_OF_MONTH));
						BigDecimal changeRatio = endPaymentDate.divide(maxDateInEndPaymentMonth, 2, RoundingMode.HALF_UP);
						
						JSONArray newDetailsInMonth = new JSONArray();
						BigDecimal newMonthlyAmount = new BigDecimal(pymtsInMonth.optDouble("monthlyAmount"));

						newMonthlyAmount = newMonthlyAmount.multiply(changeRatio).setScale(2, RoundingMode.HALF_UP);
						
						// check details if same coa exists
						for (int l = 0; l < pymtsInMonth.optJSONArray("details").length(); l++) {
							JSONObject oldCoa = pymtsInMonth.optJSONArray("details").optJSONObject(l).optJSONObject("coa");
							BigDecimal oldPymt = new BigDecimal(pymtsInMonth.optJSONArray("details").optJSONObject(l).optDouble("pymt_amt"));
							oldPymt = oldPymt.multiply(changeRatio).setScale(2, RoundingMode.HALF_UP);
							
							newDetailsInMonth.put(new JSONObject()
									.put("coa", oldCoa)
									.put("pymt_amt", oldPymt.doubleValue())
									);
						}
						objectOfMonths.put(targetDateString, new JSONObject()
								.put("details", newDetailsInMonth)
								.put("monthlyAmount", newMonthlyAmount));
					} else {
						objectOfMonths.put(targetDateString, pymtsInMonth);
					}
				}

			} else {
				log.error("Provided pymtTypeCde at calculateMPF is not valid: " + pymtTypeCde);
				throw new InvalidParameterException("Please provide valid payment method");
			}
		}

//		keep for debugging
//		 System.out.println("final objectOfMonths");
//		 System.out.println(objectOfMonths.toString(3));
		
		JSONArray names = objectOfMonths.names();

		BigDecimal currentMpf;
		BigDecimal sumOfMpf;
		JSONObject largestValue;
		JSONObject smallestValue;
		// total Calculation
		for (int i = 0; i < names.length(); i++) {
			largestValue = new JSONObject().put("value", new BigDecimal(0)).put("index", 0);// reinit to empty object
			smallestValue = new JSONObject().put("value", new BigDecimal(1500)).put("index", 0); // reinit to empty
																									// object
			currentMpf = GeneralUtil.initNullBigDecimal(""); // reinit 0
			sumOfMpf = GeneralUtil.initNullBigDecimal(""); // reinit 0

			Object value = objectOfMonths.get(names.optString(i));
			
			if (value instanceof JSONObject) {
				JSONArray detailsInCalculation = ((JSONObject) value).optJSONArray("details");

				BigDecimal monthlyAmountInCalculation = (BigDecimal) ((JSONObject) value).get("monthlyAmount");
				BigDecimal monthlyMpfContribution = monthlyAmountInCalculation.multiply(new BigDecimal(0.05))
						.setScale(2, RoundingMode.HALF_UP);
				if (monthlyMpfContribution.compareTo(new BigDecimal(1500)) > 0) {
					monthlyMpfContribution = new BigDecimal(1500);
				}

				for (int j = 0; j < detailsInCalculation.length(); j++) {
					JSONObject targetObject = detailsInCalculation.optJSONObject(j);
					BigDecimal pymtInMonth = new BigDecimal(targetObject.optDouble("pymt_amt"));

					currentMpf = pymtInMonth.divide(monthlyAmountInCalculation, 2, RoundingMode.HALF_UP)
							.multiply(monthlyMpfContribution).setScale(2, RoundingMode.HALF_UP);

					int compareCurrentMpfWithZero = currentMpf.compareTo(new BigDecimal(0));
					int compareCurrentMpfWithSmallest = currentMpf
							.compareTo(GeneralUtil.initNullBigDecimal((BigDecimal) smallestValue.get("value")));
					int compareCurrentMpfWithLargest = currentMpf
							.compareTo(GeneralUtil.initNullBigDecimal((BigDecimal) largestValue.get("value")));

					if (compareCurrentMpfWithZero <= 0) { // value smaller than 0, set currentMpf to 0.01
						currentMpf = new BigDecimal(0.01);
					}

					if (compareCurrentMpfWithSmallest <= 0) {
						smallestValue.put("value", currentMpf);
						smallestValue.put("index", j);
					}

					if (compareCurrentMpfWithLargest >= 0) {
						largestValue.put("value", currentMpf);
						largestValue.put("index", j);
					}

					targetObject.put("mpf_amt", currentMpf);
					detailsInCalculation.put(j, targetObject);
					sumOfMpf = sumOfMpf.add(currentMpf); // add ratio after adjustments does not need to be zero
				}

				int compareSumOfMpfWithMonthlyMpf = sumOfMpf.compareTo(monthlyMpfContribution);
				if (compareSumOfMpfWithMonthlyMpf == 1) {

					JSONObject targetObject = detailsInCalculation.optJSONObject(largestValue.getInt("index"));
					BigDecimal newValue = ((BigDecimal) largestValue.get("value"))
							.subtract(sumOfMpf.subtract(monthlyMpfContribution));
					targetObject.put("mpf_amt", newValue);
					detailsInCalculation.put(largestValue.getInt("index"), targetObject);

				} else if (compareSumOfMpfWithMonthlyMpf == -1) {
					JSONObject targetObject = detailsInCalculation.optJSONObject(smallestValue.getInt("index"));
					BigDecimal newValue = ((BigDecimal) smallestValue.get("value"))
							.add(monthlyMpfContribution.subtract(sumOfMpf));
					targetObject.put("mpf_amt", newValue);
					detailsInCalculation.put(smallestValue.getInt("index"), targetObject);
				}

				objectOfMonths.put(names.optString(i), new JSONObject().put("details", detailsInCalculation)
						.put("monthlyAmount", monthlyAmountInCalculation).put("monthlyMpf", monthlyMpfContribution));
			} else {
				// throw new error since there should be object from the key provided. Throw exception to guarantee accuracy
				log.error("mpf calculation objectOfMonths.get(names.optString(i)) is not an instance of JSONObject");
				throw new Exception("Unexpected Error");
			}

		}

		// // copy of loop to repack data into output
		for (int i = 0; i < names.length(); i++) {
			Object value = objectOfMonths.get(names.optString(i));
			if (value instanceof JSONObject) {
				JSONObject jsonObject = (JSONObject) value;
				JSONArray finalDetails = jsonObject.optJSONArray("details");				
				for (int j = 0; j < finalDetails.length(); j++) {
					outputJson.put(new JSONObject()
							.put("pymt_dt", GeneralUtil.getDFormatter12().parse(names.optString(i)+"-28").getTime()) // change string of date to millis
							.put("proj_id", finalDetails.optJSONObject(j).optJSONObject("coa").opt("proj_id"))
							.put("dept_id", finalDetails.optJSONObject(j).optJSONObject("coa").opt("dept_id"))
							.put("fund_cde", finalDetails.optJSONObject(j).optJSONObject("coa").opt("fund_cde"))
							.put("class", finalDetails.optJSONObject(j).optJSONObject("coa").opt("class_cde"))
							.put("acct_cde", finalDetails.optJSONObject(j).optJSONObject("coa").opt("acct_cde"))
							.put("analysis_cde", finalDetails.optJSONObject(j).optJSONObject("coa").opt("analysis_cde"))
							.put("bco_aprv_id", finalDetails.optJSONObject(j).optJSONObject("coa").opt("bco_aprv_id"))
							.put("bco_aprv_name", finalDetails.optJSONObject(j).optJSONObject("coa").opt("bco_aprv_name"))
							.put("pymt_amt", finalDetails.optJSONObject(j).opt("mpf_amt"))
							.put("monthly_mpf", jsonObject.opt("monthlyMpf")));
				}
			}
		}
		
//		keep for debugging
//		 System.out.println("final outputJson");
//		 System.out.println(outputJson.toString(3));
//		 System.out.println("end");
		return outputJson;
		*/


		String role = inputJson.getString("role");
		
		String errMsg = " ";
			String paymentMth = " "; //INSTALM, RECURR
		
			int MaxNoOfMthCOA = 1200; //safe to assume the Month-COA combination is under 1200
			int noOfMthCOA=0;
			
			ArrayList<ArrayList<Object>> mthCOA = new ArrayList<>(MaxNoOfMthCOA);
			for(int i=0; i < MaxNoOfMthCOA; i++) {
				mthCOA.add(new ArrayList<Object>());
				mthCOA.get(i).add(" ");  //indicate this Month-COA combination is not occupied yet															
			}//for

			//////////////Getting input
			int no_of_lines = 0;
			int no_of_MthCOA = 0;

			JSONArray pymtScheduleArr = inputJson.optJSONArray("pymt_schedule");
			paymentMth = GeneralUtil.initBlankString(inputJson.optString("pymt_type_cde"));
			


			if ((!(paymentMth.equals("INSTALM"))) && (!(paymentMth.equals("RECURR")))) {
				errMsg = "Invalid Payment Method.";
			}

			for (int i = 0; i < pymtScheduleArr.length(); i++) {
				JSONObject jsonObj = pymtScheduleArr.getJSONObject(i);
				JSONArray schLine = jsonObj.optJSONArray("details");

				Timestamp pymnStartDt = GeneralUtil.NULLTIMESTAMP;
				Timestamp pymnEndDt = GeneralUtil.NULLTIMESTAMP;

				Long startDtLong = jsonObj.optLong("pymt_start_dt");
				Long endDtLong = !jsonObj.isNull("pymt_end_dt") ? jsonObj.optLong("pymt_end_dt") : jsonObj.optLong("pymt_start_dt");
				
				pymnStartDt = GeneralUtil.initNullTimestampFromLong(startDtLong);
				pymnEndDt = GeneralUtil.initNullTimestampFromLong(endDtLong);
				
			
				BigDecimal totalAmt = new BigDecimal("0.00"); // for recurrent only
						
				//*** can be put at the i-level */
				
				Calendar startCal = Calendar.getInstance();
				startCal.setTimeInMillis(startDtLong);
				int startDay = GeneralUtil.getTimestampField(pymnStartDt, Calendar.DAY_OF_MONTH);
				int startMonth = GeneralUtil.getTimestampField(pymnStartDt, Calendar.MONTH);
				int startYear = GeneralUtil.getTimestampField(pymnStartDt, Calendar.YEAR);
				
				int countStartMthYear = startYear * 12 + startMonth;

				Calendar endCal = Calendar.getInstance();
				endCal.setTimeInMillis(endDtLong);
				int endDay = GeneralUtil.getTimestampField(pymnEndDt, Calendar.DAY_OF_MONTH);
				int endMonth = GeneralUtil.getTimestampField(pymnEndDt, Calendar.MONTH);
				int endYear = GeneralUtil.getTimestampField(pymnEndDt, Calendar.YEAR);
			
				
				int countEndMthYear = endYear * 12 + endMonth;

				//*** End- can be put at the i-level */
				
				for (int j=0; j<schLine.length();j++) {
					JSONObject detailObj = schLine.getJSONObject(j);

					String s1=GeneralUtil.initBlankString(inputJson.optString("pymt_type_cde"));
					String s2=GeneralUtil.convertTimestampToString(pymnStartDt, "T").substring(0,6);
					String s3=GeneralUtil.convertTimestampToString(pymnEndDt, "T").substring(0,6);
					String s4= GeneralUtil.initBlankString(detailObj.optString("proj_id"));
					if(s4.equals("null")) {
						s4 = " ";
					}
					String s5= GeneralUtil.initBlankString(detailObj.optString("dept_id"));
					if(s5.equals("null")) {
						s5 = " ";
					}
					String s6= GeneralUtil.initBlankString(detailObj.optString("fund_cde"));
					if(s6.equals("null")) {
						s6 = " ";
					}
					String s7= GeneralUtil.initBlankString(detailObj.optString("class"));
					if(s7.equals("null")) {
						s7 = " ";
					}
					//String s8= GeneralUtil.initBlankString(detailObj.optString("acct_cde"));
					String s8= " ";
					if(role.equals("Senior")) {
						s8 = "5211";
					} else if (role.equals("Support")) {
						s8 = "5212";
					} else {
						log.debug("MPF alloc: Role no found");
					}
					//String s9= GeneralUtil.initBlankString(detailObj.optString("analysis_cde"));
					String s9= "741";
					if(Double.isNaN(detailObj.optDouble("pymt_amt"))) {
						throw new InvalidParameterException("Payment amount is blank!");
					}
					BigDecimal b10 = new BigDecimal(detailObj.optString("pymt_amt"));
					String s11= GeneralUtil.initBlankString(detailObj.optString("bco_aprv_id"));
					String s12= GeneralUtil.initBlankString(detailObj.optString("bco_aprv_name"));
				
							if (paymentMth.equals("INSTALM")) {

								
								no_of_lines++; /* assume no line with be skipped */

								String tmpMthCoa=s2 + "-" + s4 + "-" + s5 + "-" + s6 + "-" +
								                 s7 + "-" + s8 + "-" + s9;

								//find whther the Month-COA already exists
								int tmpIdx = -1;
								for(int loopMthCOA=0; loopMthCOA < no_of_MthCOA; loopMthCOA++) {
									String tmpStr = (String) (mthCOA.get(loopMthCOA).get(1));
									if (tmpStr.equals(tmpMthCoa)) {
										tmpIdx = loopMthCOA;
										break;
									}
								}//for
					
								if (tmpIdx >= 0) {
									 BigDecimal fBD = (BigDecimal) (mthCOA.get(tmpIdx).get(8));
									 BigDecimal b10b = b10.add(fBD);
									 b10b.setScale(2, RoundingMode.HALF_UP);
									 mthCOA.get(tmpIdx).set(8, b10b);							 
								} // found
								else {
									no_of_MthCOA++;
									mthCOA.get(no_of_MthCOA - 1).set(0, s2);  //YYYYMM
									mthCOA.get(no_of_MthCOA - 1).add(tmpMthCoa); //combination
									mthCOA.get(no_of_MthCOA - 1).add(s4); //project
									mthCOA.get(no_of_MthCOA - 1).add(s5); //department
									mthCOA.get(no_of_MthCOA - 1).add(s6); //fund
									mthCOA.get(no_of_MthCOA - 1).add(s7); //class
									mthCOA.get(no_of_MthCOA - 1).add(s8); //account
									mthCOA.get(no_of_MthCOA - 1).add(s9); //analysys
									mthCOA.get(no_of_MthCOA - 1).add(b10); //amount
									//implicitly assume that if COA is the same, approver is the same
									mthCOA.get(no_of_MthCOA - 1).add(s11); //approver
									mthCOA.get(no_of_MthCOA - 1).add(s12); // name																					
								} //not found			



							} //(paymentMth.equals("INSTALM"))  
							else	if (paymentMth.equals("RECURR") && (i==0)) {
								 	
									for (int currentMonth = countStartMthYear; currentMonth <= countEndMthYear; currentMonth++) {

										//JSONArray detailArr = scheduleObj.optJSONArray("details");
										//for (int j = 0; j < detailArr.length(); j++) {
											//JSONObject detailObj = detailArr.optJSONObject(j);
											BigDecimal monthlyAmount = b10;
											//if (!Double.isNaN(detailObj.optDouble("pymt_amt"))) {
											//	monthlyAmount = BigDecimal.valueOf(detailObj.optDouble("pymt_amt"));
											//}
											// to BigDecimal, to 2 decimal

											int currentStartDay = 0;
											int currentEndDay = 0;
											// get current month
											Calendar currentCal = Calendar.getInstance();
											currentCal.set(currentMonth / 12, (currentMonth - 1) % 12, 1);

											// get start date (occupied)
											if (currentMonth == countStartMthYear) {
												currentStartDay = startDay;
											} else {
												currentStartDay = 1;
											}
											// get end date (occupied)
											if (currentMonth == countEndMthYear) {
												currentEndDay = endDay;
											} else {
												currentEndDay = currentCal.getActualMaximum(Calendar.DAY_OF_MONTH);
											}
											// get 1st date of the month
											// get last date of the month
											// get total month dates (non-occupied)
											// get occupied date
											int occupiedDate = currentEndDay - currentStartDay + 1;

											// BigDecimal
											BigDecimal dayPercent = new BigDecimal(occupiedDate).divide(
													new BigDecimal(currentCal.getActualMaximum(Calendar.DAY_OF_MONTH)), 10,
													RoundingMode.HALF_UP);
											BigDecimal calculatedMonthlyAmount = dayPercent.multiply(monthlyAmount).setScale(2, RoundingMode.HALF_UP);

											totalAmt = totalAmt.add(calculatedMonthlyAmount);
											
											//add record
											no_of_lines++; /* assume no line with be skipped */

											//s2 - need rewrite, s3 no use
											//b10 = calculatedMonthlyAmount;
											
											if ((currentCal.get(Calendar.MONTH) + 1) < 10) {
													s2 = ((currentMonth - 1) / 12) + "0" + (currentCal.get(Calendar.MONTH) + 1);
											}
											else {
												 	s2 = String.valueOf((currentMonth - 1) / 12) + String.valueOf((currentCal.get(Calendar.MONTH) + 1));
											}
											
											String tmpMthCoa=s2 + "-" + s4 + "-" + s5 + "-" + s6 + "-" +
											                 s7 + "-" + s8 + "-" + s9;
											                 
											//find whther the Month-COA already exists
											int tmpIdx = -1;
											for(int loopMthCOA=0; loopMthCOA < no_of_MthCOA; loopMthCOA++) {
												String tmpStr = (String) (mthCOA.get(loopMthCOA).get(1));
												if (tmpStr.equals(tmpMthCoa)) {
													tmpIdx = loopMthCOA;
													break;
												}
											}//for
								
											if (tmpIdx >= 0) {
												 BigDecimal fBD = (BigDecimal) (mthCOA.get(tmpIdx).get(8));
												 BigDecimal b10b = b10.add(fBD);
												 b10b.setScale(2, RoundingMode.HALF_UP);
												 mthCOA.get(tmpIdx).set(8, b10b);							 
											} // found
											else {
												no_of_MthCOA++;
												mthCOA.get(no_of_MthCOA - 1).set(0, s2);  //YYYYMM
												mthCOA.get(no_of_MthCOA - 1).add(tmpMthCoa); //combination
												mthCOA.get(no_of_MthCOA - 1).add(s4); //project
												mthCOA.get(no_of_MthCOA - 1).add(s5); //department
												mthCOA.get(no_of_MthCOA - 1).add(s6); //fund
												mthCOA.get(no_of_MthCOA - 1).add(s7); //class
												mthCOA.get(no_of_MthCOA - 1).add(s8); //account
												mthCOA.get(no_of_MthCOA - 1).add(s9); //analysis
												mthCOA.get(no_of_MthCOA - 1).add(calculatedMonthlyAmount); //amount
												//implicitly assume that if COA is the same, approver is the same
												mthCOA.get(no_of_MthCOA - 1).add(s11); //approver
												mthCOA.get(no_of_MthCOA - 1).add(s12); // name																					
											} //not found			
											
											//end - add record																								
												
										//} // end of for loop of detailArr
											
										
									} // for each month
									 	
							} // (paymentMth.equals("RECURR") && (i==0))

					} // for each schedule line - j


				
			} // for each schedule - i
			
			/// Cal MPF
			// Loop for Month
			
			ArrayList<String> monthList = new ArrayList<>();
			for(int i =0; i < no_of_MthCOA; i++) {
				if(mthCOA.get(i).get(0).equals(" ") || monthList.contains((String) mthCOA.get(i).get(0))) {
					continue;
				}
				monthList.add((String) mthCOA.get(i).get(0));
			}
			for(String currentMonth:monthList) {
				BigDecimal totalAmount = new BigDecimal(0.0);
				int monthlength = 0;
				for(int i =0; i < no_of_MthCOA; i++) {
					// Find same month
					if(mthCOA.get(i).get(0).equals(" ") || !currentMonth.equals(mthCOA.get(i).get(0))) {
						continue;
					}
					totalAmount = totalAmount.add((BigDecimal) mthCOA.get(i).get(8));
					monthlength++;
				}
				// Cal MPF
				BigDecimal monthlyMPF = totalAmount.divide(new BigDecimal(20), 2, RoundingMode.HALF_UP);
				if(monthlyMPF.compareTo(new BigDecimal(1500)) > 0) {
					monthlyMPF = new BigDecimal(1500);
				}
				BigDecimal remainMPF = monthlyMPF;
				
				for(int i =0; i < no_of_MthCOA; i++) {
					// Find same month
					if(mthCOA.get(i).get(0).equals(" ") || !currentMonth.equals(mthCOA.get(i).get(0))) {
						continue;
					}
					if(((BigDecimal) mthCOA.get(i).get(8)).intValue() == 0) {
						throw new InvalidParameterException("Payment amount cannot be 0!");
					}
					BigDecimal ratio = ((BigDecimal) mthCOA.get(i).get(8)).divide(totalAmount, 10, RoundingMode.HALF_UP);
					mthCOA.get(i).add(ratio);
				}
				for(int i =0; i < no_of_MthCOA; i++) {
					// Find same month
					if(mthCOA.get(i).get(0).equals(" ") || !currentMonth.equals(mthCOA.get(i).get(0))) {
						continue;
					}
					int line = 0;
					if (line+1 != monthlength) {
						line++;
						BigDecimal coaMPF = monthlyMPF.multiply((BigDecimal) mthCOA.get(i).get(11)).setScale(2, RoundingMode.HALF_UP);
						mthCOA.get(i).add(coaMPF);
						remainMPF = remainMPF.add(coaMPF.negate());
					}else {
						mthCOA.get(i).add(remainMPF);
					}
				}
				
				
				
			}
			JSONArray outputList = new JSONArray();
			
			if(!errMsg.isBlank()) {
				throw new InvalidParameterException("MPF calculation error: " + errMsg);
			}

			for(int i=0; i < no_of_MthCOA; i++) {
				String a = (String) (mthCOA.get(i).get(0));
				if (!(a.equals(" "))) {
//					System.out.println("Slot " + (i+1) + " : time: " + (String) (mthCOA.get(i).get(0)));
//					System.out.println("Slot " + (i+1) + " : combination: " + (String) (mthCOA.get(i).get(1)));
//					System.out.println("Slot " + (i+1) + " : project: " + (String) (mthCOA.get(i).get(2)));
//					System.out.println("Slot " + (i+1) + " : dept: " + (String) (mthCOA.get(i).get(3)));
//					System.out.println("Slot " + (i+1) + " : fund: " + (String) (mthCOA.get(i).get(4)));
//					System.out.println("Slot " + (i+1) + " : class: " + (String) (mthCOA.get(i).get(5)));
//					System.out.println("Slot " + (i+1) + " : account: " + (String) (mthCOA.get(i).get(6)));
//					System.out.println("Slot " + (i+1) + " : analysis: " + (String) (mthCOA.get(i).get(7)));
//					System.out.println("Slot " + (i+1) + " : amount: " + (BigDecimal) (mthCOA.get(i).get(8)));
//					System.out.println("Slot " + (i+1) + " : approver: " + (String) (mthCOA.get(i).get(9)));
//					System.out.println("Slot " + (i+1) + " : name: " + (String) (mthCOA.get(i).get(10)));
//					System.out.println("Slot " + (i+1) + " : ratio: " + (BigDecimal) (mthCOA.get(i).get(11)));
//					System.out.println("Slot " + (i+1) + " : COAMPF: " + (BigDecimal) (mthCOA.get(i).get(12)));
					
					
					JSONObject jsonObj = new JSONObject();
					String date = (String) mthCOA.get(i).get(0);
					date = date.concat("28000000000");
					
					Long pymtDtLong = GeneralUtil.getDFormatter7().parse(date).getTime();
					
					// set pymt dt to be last day of the month
					Timestamp pymtDtSt = GeneralUtil.initNullTimestampFromLong(pymtDtLong);
					
					
					Calendar cal = Calendar.getInstance();
					cal.setTime(pymtDtSt);
					int lastDay = cal.getActualMaximum(Calendar.DAY_OF_MONTH);
					cal.set(Calendar.DATE, lastDay);
					
					Timestamp pymtDt_new = new Timestamp(cal.getTime().getTime());
					
					jsonObj.put("pymt_dt",pymtDt_new)
					.put("proj_id",(String) (mthCOA.get(i).get(2)))
					.put("dept_id",(String) (mthCOA.get(i).get(3)))
					.put("fund_cde",(String) (mthCOA.get(i).get(4)))
					.put("class",(String) (mthCOA.get(i).get(5)))
					.put("acct_cde",(String) (mthCOA.get(i).get(6)))
					.put("analysis_cde",(String) (mthCOA.get(i).get(7)))
					.put("pymt_amt",(BigDecimal) (mthCOA.get(i).get(12)))
					.put("bco_aprv_id",(String) (mthCOA.get(i).get(9)))
					.put("bco_aprv_name",(String) (mthCOA.get(i).get(10)))
					// Just put COA amount is ok, sum by frontend.
					.put("monthly_mpf", (BigDecimal) (mthCOA.get(i).get(12)));
					
					outputList.put(jsonObj);
				}
			}//for
		return outputList;
	}

	@Override
	public JSONArray calculateAmountPerMonth(JSONObject inputJson) throws Exception {
		JSONArray paymentDetails = inputJson.getJSONArray("pymt_schedule");
		JSONArray outputJson = new JSONArray();
		for (int i = 1; i <= inputJson.length(); i++) {

		}

		CommonJson test = GeneralUtil.stringToCommonJsonObject("\"pymt_start_dt\": 1696953600000");
		JSONObject test2 = GeneralUtil.commonJsonToJsonObject(test);
		Timestamp start = new Timestamp(test2.optLong("pymt_start_dt"));
		return outputJson;
	}
	
	@Override
	public BigDecimal calculateCOARecurrentTotalAmount(Long startDtLong, Long endDtLong, BigDecimal amount) {

		BigDecimal calculatedMonthlyAmount = new BigDecimal(0.0);
		BigDecimal totalAmt = new BigDecimal(0.0);
		Timestamp pymtStartDt = GeneralUtil.initNullTimestampFromLong(startDtLong);
		Timestamp pymtEndDt = GeneralUtil.initNullTimestampFromLong(endDtLong);

		Calendar startCal = Calendar.getInstance();
		startCal.setTimeInMillis(startDtLong);
		int startDay = GeneralUtil.getTimestampField(pymtStartDt, Calendar.DAY_OF_MONTH);
		int startMonth = GeneralUtil.getTimestampField(pymtStartDt, Calendar.MONTH);
		int startYear = GeneralUtil.getTimestampField(pymtStartDt, Calendar.YEAR);
		int countStartMthYear = startYear * 12 + startMonth;

		Calendar endCal = Calendar.getInstance();
		endCal.setTimeInMillis(endDtLong);
		int endDay = GeneralUtil.getTimestampField(pymtEndDt, Calendar.DAY_OF_MONTH);
		int endMonth = GeneralUtil.getTimestampField(pymtEndDt, Calendar.MONTH);
		int endYear = GeneralUtil.getTimestampField(pymtEndDt, Calendar.YEAR);
		int countEndMthYear = endYear * 12 + endMonth;
		for (int currentMonth = countStartMthYear; currentMonth <= countEndMthYear; currentMonth++) {

			int currentStartDay = 0;
			int currentEndDay = 0;
			// get current month
			Calendar currentCal = Calendar.getInstance();
			currentCal.set(currentMonth / 12, (currentMonth - 1) % 12, 1);
			// get start date (occupied)
			if (currentMonth == countStartMthYear) {
				currentStartDay = startDay;
			} else {
				currentStartDay = 1;
			}
			// get end date (occupied)
			if (currentMonth == countEndMthYear) {
				currentEndDay = endDay;
			} else {
				currentEndDay = currentCal.getActualMaximum(Calendar.DAY_OF_MONTH);
			}
			// get 1st date of the month
			// get last date of the month
			// get total month dates (non-occupied)
			// get occupied date
			int occupiedDate = currentEndDay - currentStartDay + 1;
	
			// BigDecimal
			BigDecimal dayPercent = new BigDecimal(occupiedDate).divide(
				new BigDecimal(currentCal.getActualMaximum(Calendar.DAY_OF_MONTH)), 10,
				RoundingMode.HALF_UP);
			calculatedMonthlyAmount = dayPercent.multiply(amount).setScale(2, RoundingMode.HALF_UP);
			totalAmt = totalAmt.add(calculatedMonthlyAmount);
		}
		return totalAmt;
	}
	
	@Override
	public String generateBudgetWarningMessage(JSONObject inputJson) throws Exception {
		String warningMessage = "";

		JSONArray coaLineJsonArr = new JSONArray();

		JSONObject pymtMethod = inputJson.optJSONObject("el_appl_pymt_method");
		
		JSONArray pymtScheduleArr = pymtMethod.optJSONArray("pymt_schedule");
		

		for (int i=0; i< pymtScheduleArr.length(); i++) {
			JSONObject pymtSchedulObj = pymtScheduleArr.getJSONObject(i);
			JSONArray pymtDetails = pymtSchedulObj.optJSONArray("details");
			for (int k=0; k < pymtDetails.length(); k++) {
				JSONObject detailObj = pymtDetails.getJSONObject(k);
				// Add to arr for later calling API
				if(pymtMethod.optString("pymt_type_cde").equals(PymtTypeConstants.RECURR)) {
					JSONObject newObj = new JSONObject();
					newObj.put("proj_id", detailObj.optString("proj_id"))
						  .put("dept_id", detailObj.optString("dept_id"))
						  .put("acct_cde", detailObj.optString("acct_cde"))
						  .put("analysis_cde", detailObj.optString("analysis_cde"))
						  .put("fund_cde", detailObj.optString("fund_cde"))
						  .put("bco_aprv_id", detailObj.optString("bco_aprv_id"))
						  .put("bco_aprv_name", detailObj.optString("bco_aprv_name"))
						  .put("pymt_amt", calculateCOARecurrentTotalAmount(pymtSchedulObj.optLong("pymt_start_dt"), pymtSchedulObj.optLong("pymt_end_dt"), new BigDecimal(detailObj.optString("pymt_amt"))));
					coaLineJsonArr.put(newObj);
				}else {
					coaLineJsonArr.put(detailObj);
				}
			}
		}
		JSONArray mpfJson = inputJson.getJSONArray("el_appl_mpf");
		for (int j=0; j< mpfJson.length(); j++) {
			JSONObject detailObj = mpfJson.getJSONObject(j);
			// Add to arr for later calling API
			 coaLineJsonArr.put(detailObj);
		}
		
		JSONArray budgetCheckResult = getBudgetCheckResult(coaLineJsonArr);
		if (budgetCheckResult.length() == 0) {
			throw new Exception("Error when budget check");
		}
		List<String> errMsgList = new ArrayList<>();
		List<String> warningList = new ArrayList<>();
		
		List<String> throwErrMsgList = new ArrayList<>();
		for (int i=0; i<budgetCheckResult.length(); i++) {
			JSONObject lineResult = budgetCheckResult.getJSONObject(i);
			log.debug("lineResult: {}", lineResult);
			
			if ("N".equals(lineResult.getString("COAlineResult"))) {
				String errMsg = "Line " + lineResult.get("lineRef") + ": " + lineResult.get("COAlineErrMsg");
				if("01".equals(lineResult.getString("COAlineErrCode")) || "02".equals(lineResult.getString("COAlineErrCode")) || "04".equals(lineResult.getString("COAlineErrCode"))) {
					throwErrMsgList.add(errMsg);
				}
				errMsgList.add(errMsg);
			}

			if ("W".equals(lineResult.getString("COAlineResult"))) {
				String warningMsg = "Line " + lineResult.get("lineRef") + ": ";
				
				// ETAP-97 In line with the setting in all system and eform, please update the insufficient message for CPEP projects.
				// If CPEP fund codes, return a different warning msg: 
				JSONObject coaObj = coaLineJsonArr.getJSONObject(((Integer) lineResult.get("lineRef")) -1);
				String fundCde = coaObj.optString("fund_cde");
				
				log.debug("fundCde: {}", fundCde);
				
				if (isCPEPFundCode(fundCde)) {
					String project = coaObj.optString("proj_id");
					warningMsg += "For Project " + project + " : Insufficient Fund. Please ensure the temporary deficit of CPEP can be recovered.";
				} else {
					warningMsg += lineResult.get("COAlineErrMsg");
				}
				
				warningList.add(warningMsg);
			}
			if (throwErrMsgList.size() > 0) {
				throw new InvalidParameterException("Budget Check Error: \n " + String.join(" \n ", throwErrMsgList));
			}
		}
		
		if (errMsgList.size() > 0) {
			// Jira 45
//			throw new InvalidParameterException("Budget Check Error: \n " + String.join(" \n ", errMsgList));
			warningMessage += "Invalid budget: \n " + String.join(" \n ", errMsgList) + " \n ";
		}

		if(warningList.size()>0) {
			warningMessage += "Budget Check Warning: \n " + String.join(" \n ", warningList);
		}
		return warningMessage;
	}
	
	@Override
	public String generateApplStartDateWarningMessage(JSONObject inputJson) throws Exception {
		Timestamp currentTime = GeneralUtil.getCurrentTimestamp();

		Timestamp applStartDt;
		Timestamp applEndDt;
		
		try {
			Long startDtLong = inputJson.optLong("appl_start_dt");
			applStartDt = GeneralUtil.initNullTimestampFromLong(startDtLong);
		} catch (Exception e) {
			throw new InvalidDateException();
		}
		// dayDiff = applStartDt - currentTime
//		System.out.println("currentTime" + currentTime);
//		System.out.println("applStartDt" + applStartDt);
		int dayDiff = applStartDt.compareTo(currentTime);
		if(dayDiff < 0) {
			// applStartDt < currentTime
			return "The application start before today! \n";
		}
		return "";
	}
	
	@Override
	public String generateSemesterWarningMessage(JSONObject inputJson) throws Exception {
		// ETAP-57 Cross check semester and date periods
		
		Timestamp applStartDt = null;
		Timestamp applEndDt = null;
		try {
			Long startDtLong = inputJson.optLong("appl_start_dt");
			Long endDtLong = inputJson.optLong("appl_end_dt");
			applStartDt = GeneralUtil.initNullTimestampFromLong(startDtLong);
			applEndDt = GeneralUtil.initNullTimestampFromLong(endDtLong);
		} catch (Exception e) {
			throw new InvalidDateException();
		}
		
		String startTerm = inputJson.optString("appl_start_term");
		String endTerm = StringUtils.isNotBlank(inputJson.optString("appl_end_term")) ? inputJson.optString("appl_end_term") : inputJson.optString("appl_start_term");
		
		Map<String, Object> s = elAcadTermVRepository.getTermStartAndEndDat(startTerm, endTerm);
		
		Timestamp termStart = (Timestamp) s.get("min_start");
		Timestamp termEnd = (Timestamp) s.get("max_end");
		
		if (termStart.after(GeneralUtil.truncateDate(applStartDt)) || termEnd.before(GeneralUtil.truncateDate(applEndDt))) {
			return "Application period is not within selected semester(s)! \n";
		}
		
		return "";
	}
	
	@Override
	public boolean checkHistVersionSameAsCurrent(String applHdrId) {
		// histVersion = 0 if no backup in history table
		ElApplHdrDAO hdrDAO = elApplHdrRepository.findOne(applHdrId);
		int histVersion = elApplHdrHistRepository.findMaxVersionNoById(hdrDAO.getId());
		
		if(histVersion == hdrDAO.getVersionNo()) {
			return true;
		}
		
		return false;
	}
	
	@Override
	public JSONArray getAcadYrList() throws Exception {
		JSONArray outputJsonArray = new JSONArray();
		
		List<Map<String, Object>> resultMapList = elAcadTermVRepository.getAllAcadYr();
		
		for (Map<String, Object> resultMap : resultMapList) {
			JSONObject jsonObj = new JSONObject();
			outputJsonArray.put(jsonObj);

			for (String keys : resultMap.keySet()) {
				jsonObj.put(keys.toLowerCase(), resultMap.get(keys));
			}
		}
		
		return outputJsonArray;
	}
	
	@Override
	public JSONObject getCurrentTerm() throws Exception {
		JSONObject outputJson = new JSONObject();
		
		ElAcadTermVDAO currentTermDAO = elAcadTermVRepository.getCurrentTerm();
		
		outputJson
		.put("acad_yr", currentTermDAO.getAcadYr())
		.put("strm", currentTermDAO.getStrm())
		.put("yr_term_desc", currentTermDAO.getYrTermDesc());
		
		return outputJson;
	}

	@Override
	@Transactional(value = "transactionManagerJselMain", rollbackFor = Exception.class)
	public JSONObject createElApplAct(String applHdrId, JSONObject inputJson, String action, String remark, String actionFrom, String opPageName) throws Exception {
		JSONObject outputJson = new JSONObject();
		String remoteUser = SecurityUtils.getCurrentLogin();
		String actionBy = remoteUser;
		String roleType = " ";
		
		if (inputJson != null) {
			roleType = inputJson.optString("role");
		}
		
		ElApplActDAO dao = new ElApplActDAO();
		if((action.isBlank() && remark.isBlank())) {
			action = inputJson.getString("action").toLowerCase();
			remark = GeneralUtil.initBlankString(inputJson.optString("aprv_remark"));
		}
		switch (actionFrom) {
		case "appl":
			switch (action) {
				case ElApplActDAO.SUBMIT :
					action = ElApplActDAO.SUBMIT_APPL;
					break;
				case ElApplActDAO.REMOVE :
					action = ElApplActDAO.REMOVE_APPL;
					break;
				case ElApplActDAO.CLOSE :
					action = ElApplActDAO.CLOSE_APPL;
					break;
				case ElApplActDAO.APPROVE :
					action = ElApplActDAO.APPROVE_APPL;
					break;
				case ElApplActDAO.DRAFT :
					action = ElApplActDAO.DRAFT_APPL;
					break;
				case ElApplActDAO.RETURN :
				case ElApplActDAO.REJECT :
					action = ElApplActDAO.REJECT_APPL;
					break;
				case ElApplActDAO.ACCEPT :
					action = ElApplActDAO.ACCEPT_APPL;
					break;
				}
			break;
		case "pymt":
			switch (action) {
				case ElApplActDAO.SUBMIT :
					action = ElApplActDAO.SUBMIT_PYMT;
					break;
				case ElApplActDAO.REMOVE :
					action = ElApplActDAO.REMOVE_PYMT;
					break;
				case ElApplActDAO.CLOSE :
					action = ElApplActDAO.CLOSE_PYMT;
					break;
				case ElApplActDAO.APPROVE :
					action = ElApplActDAO.APPROVE_PYMT;
					break;
				case ElApplActDAO.DRAFT :
					action = ElApplActDAO.DRAFT_PYMT;
					break;
				case ElApplActDAO.RETURN :
				case ElApplActDAO.REJECT :
					action = ElApplActDAO.REJECT_PYMT;
					break;
				case ElApplActDAO.ACCEPT :
					action = ElApplActDAO.ACCEPT_PYMT;
					break;
			}
			break;
		case "system":
			switch (action) {
			case ElApplActDAO.INTEGRATE_HRMS :
			case ElApplActDAO.COMPLETED :
				// action no need to change
				roleType = "System";
				break;
			}
			actionBy = "System";
			roleType = "System";
			break;
		}
		
		dao.setApplHdrId(applHdrId);
		dao.setAction(action);
		// 20231229 #77 show remark in act history
		dao.setDescr(GeneralUtil.initBlankString(remark));
		dao.setActionBy(actionBy);
		dao.setActionDttm(GeneralUtil.getCurrentTimestamp());
		dao.setRoleType(roleType.toUpperCase());
		
		dao.setCreatUser(remoteUser);
		dao.setChngUser(remoteUser);
		dao.setOpPageNam(opPageName);
		
		elApplActRepository.save(dao);
		
		return outputJson;
	}
	
	@Override
	public boolean checkIsRejected(String applHdrId) {
		List<String> actintList = elApplActRepository.findActionByApplHdrId(applHdrId);
		boolean isRejected = false;
		for(String action: actintList) {
			switch (action) {
			case ElApplActDAO.REJECT_APPL :
			//case ElApplActDAO.REJECT_PYMT :
				isRejected = true;
				break;
			case ElApplActDAO.ISSUE_OFFER :
			case ElApplActDAO.INTEGRATE_HRMS :
				isRejected = false;
				break;
			}
		}
		
		return isRejected;
	}
	
	@Override
	public JSONObject getProvostAprv() {
		JSONObject output = new JSONObject();
		
		String aprvId = elParamTabRepository.findProvostAprvId();
		String aprvName = elParamTabRepository.findProvostAprvName();

		try {
			output.put("aprv_id", GeneralUtil.initBlankString(aprvId));
			output.put("aprv_name", GeneralUtil.initBlankString(aprvName));
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return output;
	}
	
	@Override
	public JSONObject getDefaultDeptHead(String deptid) {
		JSONObject output = new JSONObject();
		ElBcoAprvVDAO dao = elBcoAprvVRepository.findDefaultDeptHeadByDeptId(deptid);
		if(dao != null) {
			try {
				output.put("aprv_id", GeneralUtil.initBlankString(dao.getElBcoAprvVDAOPK().getUserId()));
				output.put("aprv_name", GeneralUtil.initBlankString(dao.getElBcoAprvVDAOPK().getUserName()));
				log.debug(dao.getElBcoAprvVDAOPK().getUserId());
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} else {
			log.debug("Cannot find default dept head.");
		}
		
		return output;
	}
	
	private boolean isCPEPFundCode(String fundCde) {
		List<String> isCPEPFundCodeList = Arrays.asList(
				"6010",
				"6021",
				"6021D",
				"6031",
				"6034",
				"6071",
				"6099",
				"6100",
				"6102",
				"6103",
				"6109",
				"6121",
				"6200",
				"6201",
				"6400",
				"6400D",
				"6401",
				"6403",
				"6406",
				"6407",
				"9972"
				);
		
		return isCPEPFundCodeList.contains(fundCde.trim());
	}

	@Override
	public List<String> findBatchApprovalDailyEmailUserList() throws Exception{
		List<String> userList = new ArrayList<>();
		
		List<ElInpostStaffImpVDAO> deptHeadListForSBM = elInpostStaffImpVRepository.findDeptHeadForSchool();
		List<ElInpostStaffImpVDAO> deansListForSBM = elInpostStaffImpVRepository.findDeansForSchool();
		String exception = elParamTabRepository.findBatchApprovalUserList();
		List<String> exceptionList = exception != null && exception.length() > 0 ? Arrays.asList(exception.split(",")) : new ArrayList<>();
		
		for(ElInpostStaffImpVDAO deptHead: deptHeadListForSBM) {
			if(!userList.contains(deptHead.getUserNam())) {
				userList.add(deptHead.getUserNam());
			}
		}
		for(ElInpostStaffImpVDAO dean: deansListForSBM) {
			if(!userList.contains(dean.getUserNam())) {
				userList.add(dean.getUserNam());
			}
		}
		for(String exceptionUser: exceptionList) {
			String user = exceptionUser.trim();
			if(!StringUtils.isBlank(user) && !userList.contains(user)) {
				userList.add(user);
			}
		}
		if(userList.isEmpty()) {
			userList.add(" ");
		}
		log.debug("List of skipping email user: {}", userList);
		return userList;
	}
}
