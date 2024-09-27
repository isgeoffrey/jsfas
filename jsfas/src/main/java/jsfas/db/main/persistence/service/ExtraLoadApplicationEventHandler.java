package jsfas.db.main.persistence.service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.net.URLEncoder;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import org.apache.commons.lang.StringUtils;
import org.apache.http.annotation.Obsolete;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import jsfas.common.constants.AppConstants;
import jsfas.common.constants.ApplStatusConstants;
import jsfas.common.constants.AprvTypeConstants;
import jsfas.common.constants.PymtStatusConstants;
import jsfas.common.constants.PymtTypeConstants;
import jsfas.common.constants.RoleConstants;
import jsfas.common.exception.InvalidDateException;
import jsfas.common.exception.InvalidParameterException;
import jsfas.common.exception.RecordModifiedException;
import jsfas.common.exception.RecordNotExistException;
import jsfas.common.utils.GeneralUtil;
import jsfas.db.main.persistence.domain.ElAcadPlanVDAO;
import jsfas.db.main.persistence.domain.ElAcadTermVDAO;
import jsfas.db.main.persistence.domain.ElApplActDAO;
import jsfas.db.main.persistence.domain.ElApplAprvStatusDAO;
import jsfas.db.main.persistence.domain.ElApplAttachmentsDAO;
import jsfas.db.main.persistence.domain.ElApplBudgetDAO;
import jsfas.db.main.persistence.domain.ElApplColistCourseDAO;
import jsfas.db.main.persistence.domain.ElApplColistCourseHistDAO;
import jsfas.db.main.persistence.domain.ElApplCourseDAO;
import jsfas.db.main.persistence.domain.ElApplCourseHistDAO;
import jsfas.db.main.persistence.domain.ElApplElTypeDAO;
import jsfas.db.main.persistence.domain.ElApplElTypeHistDAO;
import jsfas.db.main.persistence.domain.ElApplHdrDAO;
import jsfas.db.main.persistence.domain.ElApplHdrHistDAO;
import jsfas.db.main.persistence.domain.ElApplPrgmDAO;
import jsfas.db.main.persistence.domain.ElApplPrgmHistDAO;
import jsfas.db.main.persistence.domain.ElApplPymtMethodDAO;
import jsfas.db.main.persistence.domain.ElApplPymtMethodHistDAO;
import jsfas.db.main.persistence.domain.ElApplPymtScheduleDAO;
import jsfas.db.main.persistence.domain.ElApplPymtScheduleHistDAO;
import jsfas.db.main.persistence.domain.ElApplStatTabDAO;
import jsfas.db.main.persistence.domain.ElBcoAprvVDAO;
import jsfas.db.main.persistence.domain.ElDeptChrtVDAO;
import jsfas.db.main.persistence.domain.ElEmNotificationDAO;
import jsfas.db.main.persistence.domain.ElInpostStaffImpVDAO;
import jsfas.db.main.persistence.domain.ElParamTabDAO;
import jsfas.db.main.persistence.domain.ElProjChrtVDAO;
import jsfas.db.main.persistence.domain.ElTypeSalElemntTabDAO;
import jsfas.db.main.persistence.domain.ElTypeTabDAO;
import jsfas.db.main.persistence.domain.FoApplicationEnquiryRaw;
import jsfas.db.main.persistence.repository.ElAcadPlanVRepository;
import jsfas.db.main.persistence.repository.ElAcadTermVRepository;
import jsfas.db.main.persistence.repository.ElApplActRepository;
import jsfas.db.main.persistence.repository.ElApplAprvStatusRepository;
import jsfas.db.main.persistence.repository.ElApplAttachmentsRepository;
import jsfas.db.main.persistence.repository.ElApplBudgetRepository;
import jsfas.db.main.persistence.repository.ElApplColistCourseHistRepository;
import jsfas.db.main.persistence.repository.ElApplColistCourseRepository;
import jsfas.db.main.persistence.repository.ElApplCourseHistRepository;
import jsfas.db.main.persistence.repository.ElApplCourseRepository;
import jsfas.db.main.persistence.repository.ElApplElTypeHistRepository;
import jsfas.db.main.persistence.repository.ElApplElTypeRepository;
import jsfas.db.main.persistence.repository.ElApplHdrHistRepository;
import jsfas.db.main.persistence.repository.ElApplHdrRepository;
import jsfas.db.main.persistence.repository.ElApplPrgmHistRepository;
import jsfas.db.main.persistence.repository.ElApplPrgmRepository;
import jsfas.db.main.persistence.repository.ElApplPymtMethodHistRepository;
import jsfas.db.main.persistence.repository.ElApplPymtMethodRepository;
import jsfas.db.main.persistence.repository.ElApplPymtScheduleHistRepository;
import jsfas.db.main.persistence.repository.ElApplPymtScheduleRepository;
import jsfas.db.main.persistence.repository.ElApplStatTabRepository;
import jsfas.db.main.persistence.repository.ElBcoAprvVRepository;
import jsfas.db.main.persistence.repository.ElCrseClassVRepository;
import jsfas.db.main.persistence.repository.ElDeptChrtVRepository;
import jsfas.db.main.persistence.repository.ElEmNotificationRepository;
import jsfas.db.main.persistence.repository.ElInpostStaffImpVRepository;
import jsfas.db.main.persistence.repository.ElParamTabRepository;
import jsfas.db.main.persistence.repository.ElProjChrtVRepository;
import jsfas.db.main.persistence.repository.ElTypeSalElemntTabRepository;
import jsfas.db.main.persistence.repository.ElTypeTabRepository;
import jsfas.db.main.persistence.repository.FoApplicationEnquiryRepository;
import jsfas.security.SecurityUtils;

public class ExtraLoadApplicationEventHandler implements ExtraLoadApplicationService {
	
	@Autowired
	Environment env;

	@Autowired
	ElApplHdrRepository elApplHdrRepository;

	@Autowired
	ElApplHdrHistRepository elApplHdrHistRepository;
	
	@Autowired
	ElApplPrgmRepository elApplPrgmRepository;
	 
	@Autowired
	ElApplPrgmHistRepository elApplPrgmHistRepository;
	
	@Autowired
	ElApplCourseRepository elApplCourseRepository;
	
	@Autowired
	ElApplColistCourseRepository elApplColistCourseRepository;

	@Autowired
	ElApplCourseHistRepository elApplCourseHistRepository;
	
	@Autowired
	ElApplColistCourseHistRepository elApplColistCourseHistRepository;
	
	@Autowired
	ElApplActRepository elApplActRepository;
	
	@Autowired
	ElApplAttachmentsRepository elApplAttachmentsRepository;
	
	@Autowired
	ElApplElTypeRepository elApplElTypeRepository;
	
	@Autowired
	ElApplElTypeHistRepository elApplElTypeHistRepository;
	
	@Autowired
	ElApplPymtMethodRepository elApplPymtMethodRepository;
	
	@Autowired
	ElApplPymtScheduleRepository elApplPymtScheduleRepository;
	
	@Autowired
	ElApplPymtMethodHistRepository elApplPymtMethodHistRepository;

	@Autowired
	ElApplPymtScheduleHistRepository elApplPymtScheduleHistRepository;
	
	@Autowired
	ElApplBudgetRepository elApplBudgetRepository;
	
	@Autowired
	ElApplAprvStatusRepository elApplAprvStatusRepository;
	
	@Autowired
	ElTypeSalElemntTabRepository elTypeSalElemntTabRepository;
	
	@Autowired
	ElEmNotificationRepository elEmNotificationRepository;
	
	@Autowired
	ElBcoAprvVRepository elBcoAprvVRepository;
	
	@Autowired
	ElCrseClassVRepository elCrseClassVRepository;
	
	@Autowired
	GeneralApiService generalApiService;
	
	@Autowired
	ElTypeTabRepository elTypeTabRepository;
	
	@Autowired
	ElAcadPlanVRepository elAcadPlanVRepository;
	
	@Autowired
	ElAcadTermVRepository elAcadTermVRepository;
	
	@Autowired
	ElDeptChrtVRepository elDeptChrtVRepository;
	
	@Autowired
	ElProjChrtVRepository elProjChrtVRepository;
	
	@Autowired
	ElInpostStaffImpVRepository elInpostStaffImpVRepository;
	
	@Autowired
	ElApplStatTabRepository elApplStatTabRepository;
	
	@Autowired
	ElParamTabRepository elParamTabRepository;
	
	@Autowired
	CommonRoutineService commonRoutineService;
	
	@Autowired
	FoApplicationEnquiryRepository foApplicationEnquiryRepository;
	
	private final Logger log = LoggerFactory.getLogger(this.getClass());
	
	@Override
	@Transactional(value = "transactionManagerJselMain", rollbackFor = Exception.class)
	public JSONObject createElApplication(JSONObject inputJson, String opPageName) throws Exception {
		JSONObject outputJson = new JSONObject();
		String remoteUser = SecurityUtils.getCurrentLogin();
		
		// Sanitize input json (1. remove uuid and empty JSONObject 2. Trim string values)
		GeneralUtil.sanitizeInputJson(inputJson);
		log.debug(inputJson.toString(0));
		
		// Get user input
		boolean isSubmit = ElApplActDAO.SUBMIT.equalsIgnoreCase(inputJson.getString("action_type"));
		
		// validation
		// Hdr validate user id and get user_name
		validateElApplHdr(inputJson);
		
		if (isSubmit) {
//			validateOW2Attachments(inputJson.optInt("ow2_attached_ind"), inputJson.getJSONArray("el_appl_attachments"));
			validateElApplPrgm(inputJson.optJSONObject("el_appl_prgm"));
			validateElCourseRelatedDetails(inputJson.getInt("appl_start_term"), inputJson.optInt("appl_end_term"), inputJson.getJSONArray("el_appl_course"));
			validateElApplElType(inputJson.getJSONArray("el_appl_el_type"));
			validateElApplPymtMethodAndSchedule(inputJson);
			// validateApplicationApprvs under validateElApplPymtMethodAndSchedule
			validateDeptHeadApprover(inputJson.optString("appl_user_id"), inputJson.optJSONArray("el_appl_arpv_status"));
		}
		
		ElApplHdrDAO hdrDAO = new ElApplHdrDAO();
		
		hdrDAO.setVersionNo(1);
		upsertApplHdr(inputJson, opPageName, hdrDAO);
		
		// Insert other tables
		String applHdrId = hdrDAO.getId();
		insertElApplAttachments(applHdrId, inputJson.getJSONArray("el_appl_attachments"), ElApplAttachmentsDAO.APPLICATION, opPageName);
		upsertProgram(applHdrId, inputJson.optJSONObject("el_appl_prgm"), opPageName);
		upsertCourseRelatedDetails(applHdrId, inputJson.getJSONArray("el_appl_course"),  opPageName);
		upsertElApplElType(applHdrId, inputJson.getJSONArray("el_appl_el_type"), opPageName);
		upsertPymtMethod(applHdrId, inputJson, false, isSubmit, opPageName);
		upsertElApplApprv(applHdrId, inputJson.optJSONArray("el_appl_arpv_status"), isSubmit, opPageName);
		
		
		
		// if submit, insert into activity table and approver
		if (isSubmit) {
			generalApiService.createElApplAct(applHdrId,
					new JSONObject().put("action", ElApplActDAO.SUBMIT).put("role", RoleConstants.REQUESTER), "", "",
					"appl", opPageName);
			// Assume application will send to applicant for acceptance after submission
			
			// check if status == PENDING_APPL_ACCT, send email to applicant
			if(hdrDAO.getApplStatCde().equals(ApplStatusConstants.PENDING_APPL_ACCT)) {
				sendEmailToApplicantToAcceptApplication(applHdrId, opPageName);
				createMyAiTaskForApplicantDeclar(applHdrId);
			}
			else {
				sendEmailToPendingAprv(applHdrId, opPageName);
				createMyAiTaskForPendingAprv(applHdrId);
			}
		}
		
		return outputJson.put("el_appl_id", hdrDAO.getId());
	}

	private void upsertApplHdr(JSONObject inputJson, String opPageName, ElApplHdrDAO hdrDAO) throws Exception {
		Timestamp CURR_TS = GeneralUtil.getCurrentTimestamp();
		String applUserId = inputJson.optString("appl_user_id");
		String remoteUser = SecurityUtils.getCurrentLogin();
		boolean isSubmit = ElApplActDAO.SUBMIT.equalsIgnoreCase(inputJson.getString("action_type"));
		boolean isInsert = hdrDAO.getCreatUser().isBlank();
		
		// validation
		if (applUserId.trim().isBlank()) {
			throw new InvalidParameterException("Applicant name is required");
		}
		
		// if process of integration with HRMS/FMS, cannot edit
		// TODO: need update
		if (hdrDAO.getBrPostInd() == "R"|| hdrDAO.getBrPostInd() == "I" || hdrDAO.getPymtPostInd() == "R" || hdrDAO.getPymtPostInd() == "I") {
			throw new InvalidParameterException("Applicant cannot be editted when process of integration with HRMS/FMS");
		}
				
		if (isInsert) {
			// look up in staff view
			JSONArray remoteUserStaffData = generalApiService.getStaffListByUserId(remoteUser, "staff");
			if (remoteUserStaffData.length() == 0) {
				throw new InvalidParameterException("Reqester staff info is not found");
			}
			// return the record with primary job index = Y 
			String remoteUserName = remoteUserStaffData.getJSONObject(0).getString("display_nam");
			String remoteUserEmplid = remoteUserStaffData.getJSONObject(0).getString("emplid");
			String remoteUserDeptid = remoteUserStaffData.getJSONObject(0).getString("dept_id");
			
			hdrDAO.setApplRequesterId(remoteUser);
			hdrDAO.setApplRequesterName(remoteUserName);
			hdrDAO.setApplRequesterEmplid(remoteUserEmplid);
			hdrDAO.setApplRequesterDeptid(remoteUserDeptid);
			
			hdrDAO.setCreatUser(remoteUser);
		} else {
			hdrDAO.setModCtrlTxt(GeneralUtil.genModCtrlTxt());
			hdrDAO.setChngDat(CURR_TS);
		}
		
		if (isSubmit && hdrDAO.getApplNbr().isBlank()) {
			String applNbr = GeneralUtil.leftPad(elApplHdrRepository.findNextApplNo(), 10, "0");
			hdrDAO.setApplNbr(applNbr);
		}
		
		// Can't change applicant after submission
		if (ApplStatusConstants.DRAFT.equals(hdrDAO.getApplStatCde())) {
			// 20231228 #1 : search acad staff record
			JSONArray applUserStaffData = generalApiService.getStaffListByUserId(applUserId, "applicant");
			if (applUserStaffData.length() == 0) {
				throw new InvalidParameterException("Applicant staff info is not found");
			}
			
			String applUserName = inputJson.optString("appl_user_name");
			String applUserEmplId = applUserStaffData.getJSONObject(0).optString("emplid");
			String applUserDeptId = " ";
			String applUserJobCatgCode = " ";
			
			for (int k=0; k< applUserStaffData.length(); k++) {
				String userJobCatgCode = applUserStaffData.getJSONObject(k).optString("job_catg_cde");
				
				if(!getApplWorkflowType(userJobCatgCode).equals(" ")) {
					applUserDeptId = applUserStaffData.getJSONObject(k).optString("dept_id");
					applUserJobCatgCode = userJobCatgCode;
					break;
				}
			}
			
			if(applUserJobCatgCode.equals(" ")) {
				throw new InvalidParameterException("applicant of this application is not applicable to this system.");
			}
			
			hdrDAO.setApplUserId(applUserId);
			// 20231228 #403 : set applicant emplid
			hdrDAO.setApplUserEmplid(applUserEmplId);
			hdrDAO.setApplUserName(applUserName);
			// 20240117 : set dept id and job catg code
			hdrDAO.setApplUserDeptId(applUserDeptId);
			hdrDAO.setApplUserJobCatg(applUserJobCatgCode);
		}
		
		Timestamp applStartDt;
		Timestamp applEndDt;
		
		try {
			Long startDtLong = inputJson.optLong("appl_start_dt");
			Long endDtLong = inputJson.optLong("appl_end_dt");
			applStartDt = GeneralUtil.initNullTimestampFromLong(startDtLong);
			applEndDt = GeneralUtil.initNullTimestampFromLong(endDtLong);
		} catch (Exception e) {
			throw new InvalidDateException();
		}
		
		// determine the status for submitted application
		String nextStatus = getApplWorkflowType(hdrDAO.getApplUserJobCatg()).equals("A") ? ApplStatusConstants.PENDING : ApplStatusConstants.PENDING_APPL_ACCT;
		log.info("nextStatus: " + nextStatus);
		
		hdrDAO.setApplStartTerm(inputJson.optString("appl_start_term"));
		hdrDAO.setApplEndTerm(GeneralUtil.initBlankString(inputJson.optString("appl_end_term")));
		hdrDAO.setApplStartDt(GeneralUtil.truncateDate(applStartDt));
		hdrDAO.setApplEndDt(GeneralUtil.truncateDate(applEndDt));

		hdrDAO.setCategoryCde(GeneralUtil.initBlankString(inputJson.optString("category_cde")));		
		hdrDAO.setApplDttm(CURR_TS);
		hdrDAO.setApplStatCde(isSubmit ? nextStatus : ApplStatusConstants.DRAFT);
		hdrDAO.setOw2AttachedInd(getApplWorkflowType(hdrDAO.getApplUserJobCatg()).equals("A") ? 0 : 1);
		hdrDAO.setPymtAprvRequired(inputJson.optInt("pymt_aprv_required"));
		
		//reset declaration filed
		hdrDAO.setUserDecl_1(0);
		hdrDAO.setUserDecl_2(0);
		hdrDAO.setUserDecl2FromDt(GeneralUtil.NULLTIMESTAMP);
		hdrDAO.setUserDecl2ToDt(GeneralUtil.NULLTIMESTAMP);
		
		hdrDAO.setHodDecl_1_1(0);
		hdrDAO.setHodDecl_1_2(0);
		
		// ETAP-109 Payroll descr
		JSONObject pymtMethod = inputJson.optJSONObject("el_appl_pymt_method");
		String payrollDescr = " ";
		if (pymtMethod != null) payrollDescr = pymtMethod.optString("payroll_descr").trim();
		
		hdrDAO.setPayrollDescr(GeneralUtil.initBlankString(payrollDescr));
		
		hdrDAO.setChngUser(remoteUser);
		hdrDAO.setOpPageNam(opPageName);
		
		elApplHdrRepository.save(hdrDAO);
	}

	@Override
	@Transactional(value = "transactionManagerJselMain", rollbackFor = Exception.class)
	public JSONObject editElApplication(String applHdrId, JSONObject inputJson, String opPageName) throws Exception {
		String remoteUser = SecurityUtils.getCurrentLogin();
		JSONObject outputJson = new JSONObject();
		
		// basic validation
		ElApplHdrDAO hdrDAO = elApplHdrRepository.findOneForUpdate(applHdrId);
		
		if(!remoteUser.equals(hdrDAO.getApplRequesterId())) {
			throw new InvalidParameterException("Not application requestor");
		}
		
		if (hdrDAO == null) {
			throw new RecordNotExistException("Extra Load Application");
		}
		
		if (!hdrDAO.getModCtrlTxt().equals(inputJson.getString("mod_ctrl_txt"))) {
			throw new RecordModifiedException();
		}
		
		// 20231228 #359 not allow application to edit when pending for approval
		List<String> validStatusList = Arrays.asList(
					ApplStatusConstants.DRAFT, 
//					ApplStatusConstants.PENDING,
					ApplStatusConstants.REJECTED, 
					ApplStatusConstants.READY_SUBM
				);
		
		if (!validStatusList.contains(hdrDAO.getApplStatCde())) {
			throw new InvalidParameterException("Extra Load Application not available for edit");
		}

		
		boolean isSubmit = ElApplActDAO.SUBMIT.equalsIgnoreCase(inputJson.getString("action_type"));
		// Submitted application can not be saved as draft
		if (!ApplStatusConstants.DRAFT.equals(hdrDAO.getApplStatCde()) && !isSubmit) {
			throw new InvalidParameterException("Submitted application cannot be saved as draft");
		}
		
		// Sanitize input json (1. remove uuid and empty JSONObject 2. Trim string values)
		GeneralUtil.sanitizeInputJson(inputJson);
		log.debug(inputJson.toString(0));
		

		boolean isOnGoing = ApplStatusConstants.READY_SUBM.equalsIgnoreCase(hdrDAO.getApplStatCde()) && hdrDAO.getBrNo() != " ";
		
		// input validation
		// Hdr validate user id and get user_name
		validateElApplHdr(inputJson);
		
		if (isSubmit) {
//			validateOW2Attachments(inputJson.optInt("ow2_attached_ind"), inputJson.getJSONArray("el_appl_attachments"));
			validateElApplPrgm(inputJson.optJSONObject("el_appl_prgm"));
			validateElCourseRelatedDetails(inputJson.getInt("appl_start_term"), inputJson.optInt("appl_end_term"), inputJson.getJSONArray("el_appl_course"));
			validateElApplElType(inputJson.getJSONArray("el_appl_el_type"));
			validateElApplPymtMethodAndSchedule(inputJson);
			// validateApplicationApprvs under validateElApplPymtMethodAndSchedule
			validateDeptHeadApprover(inputJson.optString("appl_user_id"), inputJson.optJSONArray("el_appl_arpv_status"));
		}
		// TODO: if approval started, obsolete this application and submit as new
		// cancel
		
		// Else edit 
		
		if(!isOnGoing) {
			upsertApplHdr(inputJson, opPageName, hdrDAO);
			
			insertElApplAttachments(applHdrId, inputJson.getJSONArray("el_appl_attachments"), ElApplAttachmentsDAO.APPLICATION, opPageName);
			upsertProgram(applHdrId, inputJson.optJSONObject("el_appl_prgm"), opPageName);
			upsertCourseRelatedDetails(applHdrId, inputJson.getJSONArray("el_appl_course"), opPageName);
			upsertElApplElType(applHdrId, inputJson.getJSONArray("el_appl_el_type"), opPageName);
		}else {
			hdrDAO.setApplStatCde(ApplStatusConstants.PENDING_APPL_ACCT);
			hdrDAO.setVersionNo(hdrDAO.getVersionNo()+1);
		}
		upsertPymtMethod(applHdrId, inputJson, isOnGoing, isSubmit, opPageName);
		upsertElApplApprv(applHdrId, inputJson.optJSONArray("el_appl_arpv_status"), isSubmit, opPageName);
		
		
		// if submit, insert into activity table and approver
		if (isSubmit) {
			// Disabled on 20240222 Not applicable any more
			// create email to approved email before updating approver list
//			sendEmailToAprvForEdittedAppl(applHdrId, opPageName);
			
			generalApiService.createElApplAct(applHdrId,
					new JSONObject().put("action", ElApplActDAO.SUBMIT).put("role", RoleConstants.REQUESTER), "", "",
					"appl", opPageName);
			
			// send email if submit
			// Assume application will send to applicant for acceptance after submission
			// check if status == PENDING_APPL_ACCT, send email to applicant
			if(hdrDAO.getApplStatCde().equals(ApplStatusConstants.PENDING_APPL_ACCT)) {
				sendEmailToApplicantToAcceptApplication(applHdrId, opPageName);
				createMyAiTaskForApplicantDeclar(applHdrId);
			}
			else {
				sendEmailToPendingAprv(applHdrId, opPageName);
				createMyAiTaskForPendingAprv(applHdrId);
			}
		}else {
		}
		
		return outputJson.put("el_appl_id", hdrDAO.getId());
	}
	
	private void insertElApplAttachments(String applHdrId, JSONArray inputJson, String fileCatg, String opPageName) throws Exception {
		String remoteUser = SecurityUtils.getCurrentLogin();
		
		List<ElApplAttachmentsDAO> daoList = new ArrayList<>();
		
		for (int i=0; i< inputJson.length(); i++) {
			JSONObject jsonObj = inputJson.getJSONObject(i);
			String elApplAttachmentsId = jsonObj.optString("el_appl_attachments_id");
			boolean isInsert = elApplAttachmentsId.isBlank();
			
			if (jsonObj.optString("file_id").isBlank()) {
				throw new InvalidParameterException("Attachment no." + String.valueOf(i + 1) +" did not upload successfully. Please delete and retry.");
			}
			
			if (isInsert) {
				ElApplAttachmentsDAO dao = new ElApplAttachmentsDAO();
				
				dao.setApplHdrId(applHdrId);
				dao.setFileCategory(fileCatg);
				dao.setFileId(jsonObj.getString("file_id"));
				
				dao.setCreatUser(remoteUser);
				dao.setChngUser(remoteUser);
				dao.setOpPageNam(opPageName);
				
				daoList.add(dao);
			} else {
				ElApplAttachmentsDAO dao = elApplAttachmentsRepository.findOne(elApplAttachmentsId);
				
				if (dao == null || !dao.getFileId().equals(jsonObj.getString("file_id"))) {
					throw new RecordNotExistException("ElApplAttachmentsDAO");
				}
				
				daoList.add(dao);
			}
		}
		
		elApplAttachmentsRepository.saveAll(daoList);
		
		// Delete removed record
		List<String> upsertDAOId = daoList.stream().map(dao -> dao.getId()).collect(Collectors.toList());
		
		List<ElApplAttachmentsDAO> removedDAOList = upsertDAOId.isEmpty()
				? elApplAttachmentsRepository.findByApplHdrIdAndFileCategory(applHdrId, fileCatg)
				: elApplAttachmentsRepository.findByApplHdrIdAndFileCategoryForRemove(applHdrId, fileCatg, upsertDAOId);
		
		elApplAttachmentsRepository.deleteAll(removedDAOList);
		
	}
	
	// assume program must be inputed
	private void upsertProgram(String applHdrId, JSONObject inputJson, String opPageName) throws Exception {
		if (inputJson == null) {
			return;
		}
		String remoteUser = SecurityUtils.getCurrentLogin();
		String elApplPrgmId = inputJson.optString("el_appl_prgm_id");
		boolean isInsert = elApplPrgmId.isBlank();
		
		ElApplPrgmDAO dao = null;
		
		if (isInsert) {
			dao = new ElApplPrgmDAO();
			dao.setApplHdrId(applHdrId);
			dao.setCreatUser(remoteUser);
		} else {
			dao = elApplPrgmRepository.findOneForUpdate(elApplPrgmId);
			
			if (dao == null) {
				throw new RecordNotExistException("Extra Load Application - Program Details");
			}
			
			if (!dao.getModCtrlTxt().equals(inputJson.getString("mod_ctrl_txt"))) {
				throw new RecordModifiedException();
			}
			
			if (!dao.getApplHdrId().equals(applHdrId)) {
				throw new InvalidParameterException("Extra Load Application and Program Details does not match");
			}
			
			dao.setModCtrlTxt(GeneralUtil.genModCtrlTxt());
			dao.setChngDat(GeneralUtil.getCurrentTimestamp());
		}
		
//		Timestamp prgmStartDt;
//		Timestamp prgmEndDt;
//		
//		try {
//			Long startDtLong = inputJson.optLong("prgm_start_dt");
//			Long endDtLong = inputJson.optLong("prgm_end_dt");
//			prgmStartDt = GeneralUtil.initNullTimestampFromLong(startDtLong);
//			prgmEndDt = GeneralUtil.initNullTimestampFromLong(endDtLong);
//		} catch (Exception e) {
//			throw new InvalidDateException();
//		}
		
//		dao.setPrgmTerm(inputJson.optString("prgm_term"));
//		dao.setPrgmStartDttm(GeneralUtil.truncateDate(prgmStartDt));
//		dao.setPrgmEndDttm(GeneralUtil.truncateDate(prgmEndDt));
		dao.setPrgmCde(inputJson.optString("prgm_cde"));
		dao.setSchCde(inputJson.optString("sch_cde"));
		dao.setDept(inputJson.optString("dept"));
		
		dao.setChngUser(remoteUser);
		dao.setOpPageNam(opPageName);
		
		elApplPrgmRepository.save(dao);
		
		// delete other records
		List<ElApplPrgmDAO> removedDAOList = elApplPrgmRepository.findByApplHdrIdAndId(applHdrId, dao.getId());
		elApplPrgmRepository.deleteAll(removedDAOList);
	}
	
	private void upsertCourseRelatedDetails(String applHdrId, JSONArray inputJson, String opPageName) throws Exception {
		String remoteUser = SecurityUtils.getCurrentLogin();
		
		List<ElApplCourseDAO> courseDAOList = new ArrayList<>();
		List<ElApplColistCourseDAO> colistCourseDAOList = new ArrayList<>();
		
		for (int i=0; i< inputJson.length(); i++) {
			JSONObject jsonObj = inputJson.getJSONObject(i);
			
			String elApplCourseId = jsonObj.optString("el_appl_course_id");
			boolean isInsert = elApplCourseId.isBlank();
			
			ElApplCourseDAO courseDAO = null;
			
			if (isInsert) {
				courseDAO = new ElApplCourseDAO();				
				courseDAO.setCreatUser(remoteUser);
				courseDAO.setApplHdrId(applHdrId);
			} else {
				courseDAO = elApplCourseRepository.findOne(elApplCourseId);
				
				if (courseDAO == null) {
					throw new RecordNotExistException("Extra Load Application - Program Details");
				}
				
				if (!courseDAO.getModCtrlTxt().equals(jsonObj.getString("mod_ctrl_txt"))) {
					throw new RecordModifiedException();
				}
				
				if (!courseDAO.getApplHdrId().equals(applHdrId)) {
					throw new InvalidParameterException("Extra Load Application and Program Details does not match");
				}
				
				courseDAO.setModCtrlTxt(GeneralUtil.genModCtrlTxt());
				courseDAO.setChngDat(GeneralUtil.getCurrentTimestamp());
			}
			
			String acad_term = jsonObj.optString("crse_cde").split("_")[0];
			String crse_id = jsonObj.optString("crse_id");
			String crse_cde = jsonObj.optString("crse_cde").split("_")[1];
			
			String crseSection = jsonObj.optString("section");
			BigDecimal crseCredit = Double.isNaN(jsonObj.optDouble("credit")) ? null : new BigDecimal(jsonObj.optDouble("credit"));
			
			String crse_descr = " ";
			String crse_offer_nbr = " ";
			String class_session_code = " ";
			
			// ETAP-118 don't need to check course id with given term in SIS for 'future' course
			
			if(crseCredit != null && !(crseCredit.compareTo(new BigDecimal(-1)) == 0)) {
				// find course info
				List<Map<String, Object>> resultList = elCrseClassVRepository.findCourseInfoByStrmAndCrseId(acad_term, crse_id);
				
				if(resultList.size() == 0) {
					throw new InvalidParameterException("Course Infomation cannot be found. Please verify selected courses are within selected semester.");
				}
				String crse_cde_temp = resultList.get(0).get("crse_cde").toString();
				crse_descr = resultList.get(0).get("crse_title").toString();
				crse_offer_nbr = resultList.get(0).get("crse_offer_nbr").toString();
				class_session_code = resultList.get(0).get("class_session_code").toString();
				
				if(!crse_cde_temp.equals(crse_cde))
					throw new InvalidParameterException("Course Infomation cannot be found. Please verify selected courses are within selected semester.");
			} 
			else {
				// ETAP-118 only check with course id if the course is existed in past 2 year
				List<Map<String, Object>> resultList = elCrseClassVRepository.findPreviousCourseInfoByStrmAndCrseIdAndCrseCode(acad_term, crse_id, crse_cde);
				
				if(resultList.size() == 0) {
					throw new InvalidParameterException("Course Infomation cannot be found. Please verify selected courses are within selected semester.");
				}
				
				//crse_cde = resultList.get(0).get("crse_cde").toString();
				crse_descr = resultList.get(0).get("crse_title").toString();
			}
			
			courseDAO.setCrseTerm(acad_term);
			courseDAO.setCrseId(crse_id);
			courseDAO.setCrseCde(crse_cde);
			courseDAO.setCrseDescr(crse_descr);
			courseDAO.setCrseOfferNbr(crse_offer_nbr);
			courseDAO.setSection(jsonObj.optString("section"));
			courseDAO.setSectionCode(class_session_code);
			courseDAO.setCredit(Double.isNaN(jsonObj.optDouble("credit")) ? null : new BigDecimal(jsonObj.optDouble("credit")));
			courseDAO.setCrseCoTaughtHr(Double.isNaN(jsonObj.optDouble("crse_co_taught_hr")) ? null : new BigDecimal(jsonObj.optDouble("crse_co_taught_hr")));
			courseDAO.setCrseTotalHr(Double.isNaN(jsonObj.optDouble("crse_total_hr")) ? null : new BigDecimal(jsonObj.optDouble("crse_total_hr")));
			courseDAO.setCrseRemark(GeneralUtil.initBlankString(jsonObj.optString("crse_remark")));
			
			courseDAO.setChngUser(remoteUser);
			courseDAO.setOpPageNam(opPageName);
			
			courseDAOList.add(courseDAO);
			
			// Insert Colist Course
			JSONArray colistCourseArr = jsonObj.optJSONArray("colist_course");
			String applCrseId = courseDAO.getId();
			
			if (colistCourseArr != null) {
				for (int j=0; j< colistCourseArr.length(); j++) {
					JSONObject colistJsonObj = colistCourseArr.getJSONObject(j);
					
					String elApplColistCourseId = colistJsonObj.optString("el_appl_colist_course_id");
					boolean isInsertColist = elApplColistCourseId.isBlank();
					
					ElApplColistCourseDAO colistDAO = null;
					
					if (isInsertColist) {
						colistDAO = new ElApplColistCourseDAO();
						colistDAO.setCreatUser(remoteUser);
						colistDAO.setApplHdrId(applHdrId);
					} else {
						colistDAO = elApplColistCourseRepository.findOne(elApplColistCourseId);
						
						if (colistDAO == null) {
							throw new RecordNotExistException("Extra Load Application - Program Details");
						}
						
						if (!colistDAO.getModCtrlTxt().equals(colistJsonObj.getString("mod_ctrl_txt"))) {
							throw new RecordModifiedException();
						}
						
						if (!colistDAO.getApplHdrId().equals(applHdrId)) {
							throw new InvalidParameterException("Extra Load Application and Program Details does not match");
						}
						
						colistDAO.setModCtrlTxt(GeneralUtil.genModCtrlTxt());
						colistDAO.setChngDat(GeneralUtil.getCurrentTimestamp());
					}
					
					String co_crse_id = colistJsonObj.optString("crse_id");
					String colistCrseTerm = colistJsonObj.optString("crse_cde").split("_")[0];
					
					// find course info
					List<Map<String, Object>> co_crse_resultList = elCrseClassVRepository.findCourseInfoByStrmAndCrseId(colistCrseTerm, co_crse_id);
					
					if(co_crse_resultList.size() == 0) {
						throw new InvalidParameterException("Course Infomation cannot be found. Please verify selected courses are within selected semester.");
					}
					String co_crse_cde = co_crse_resultList.get(0).get("crse_cde").toString();
					String co_crse_descr = co_crse_resultList.get(0).get("crse_title").toString();
					String co_crse_offer_nbr = co_crse_resultList.get(0).get("crse_offer_nbr").toString();
					String co_class_session_code = co_crse_resultList.get(0).get("class_session_code").toString();
					
					colistDAO.setColistCrseTerm(colistCrseTerm);
					colistDAO.setApplCrseId(applCrseId);
					colistDAO.setColistCrseId(co_crse_id);
					colistDAO.setColistCrseCde(co_crse_cde);
					colistDAO.setColistCrseOfferNbr(co_crse_offer_nbr);
					colistDAO.setSection(colistJsonObj.optString("section"));
					colistDAO.setSectionCode(co_class_session_code);
					colistDAO.setCredit(Double.isNaN(colistJsonObj.optDouble("credit")) ? null : new BigDecimal(colistJsonObj.optDouble("credit")));
					colistDAO.setCrseCoTaughtHr(Double.isNaN(colistJsonObj.optDouble("crse_co_taught_hr")) ? null : new BigDecimal(colistJsonObj.optDouble("crse_co_taught_hr")));
					colistDAO.setCrseTotalHr(Double.isNaN(colistJsonObj.optDouble("crse_total_hr")) ? null : new BigDecimal(colistJsonObj.optDouble("crse_total_hr")));
					
					colistDAO.setChngUser(remoteUser);
					colistDAO.setOpPageNam(opPageName);
					
					colistCourseDAOList.add(colistDAO);
				}
			}
		}
		
		elApplCourseRepository.saveAll(courseDAOList);
		elApplColistCourseRepository.saveAll(colistCourseDAOList);
		
		// Delete removed record
		List<String> upsertCourseDAOId = courseDAOList.stream().map(dao -> dao.getId()).collect(Collectors.toList());
		
		List<ElApplCourseDAO> removedCourseDAO = upsertCourseDAOId.isEmpty()
				? elApplCourseRepository.findByApplHdrId(applHdrId)
				: elApplCourseRepository.findByApplHdrIdForRemove(applHdrId, upsertCourseDAOId);
		
		elApplCourseRepository.deleteAll(removedCourseDAO);

		List<String> upsertColistCourseDAOId = colistCourseDAOList.stream().map(dao -> dao.getId())
				.collect(Collectors.toList());
		
		List<ElApplColistCourseDAO> removedColistCourseDAO = upsertColistCourseDAOId.isEmpty()
				? elApplColistCourseRepository.findByApplHdrId(applHdrId)
				: elApplColistCourseRepository.findByApplHdrIdForRemove(applHdrId, upsertColistCourseDAOId);
		
		elApplColistCourseRepository.deleteAll(removedColistCourseDAO);
	}
	
	private void upsertElApplElType(String applHdrId, JSONArray elTypeJsonList, String opPageName) throws Exception {
		String remoteUser = SecurityUtils.getCurrentLogin();
		
		List<ElApplElTypeDAO> daoList = new ArrayList<>();
		
		if(elTypeJsonList.length() > 0) {
			for (int i=0; i< elTypeJsonList.length(); i++) {
				JSONObject jsonObj = elTypeJsonList.getJSONObject(i);
				
				String elApplElTypeId = jsonObj.optString("el_appl_el_type_id");
				boolean isInsert = elApplElTypeId.isBlank();
				
				ElApplElTypeDAO dao = null;
				
				if (isInsert) {
					dao = new ElApplElTypeDAO();
					dao.setCreatUser(remoteUser);
					dao.setApplHdrId(applHdrId);
				} else {
					dao = elApplElTypeRepository.findOne(elApplElTypeId);
					
					if (dao == null) {
						throw new RecordNotExistException("Extra Load Application - Extra Load Type Details");
					}
					
					if (!dao.getModCtrlTxt().equals(jsonObj.getString("mod_ctrl_txt"))) {
						throw new RecordModifiedException();
					}
					
					if (!dao.getApplHdrId().equals(applHdrId)) {
						throw new InvalidParameterException("Extra Load Application and Extra Load Type Details does not match");
					}
					
					dao.setModCtrlTxt(GeneralUtil.genModCtrlTxt());
					dao.setChngDat(GeneralUtil.getCurrentTimestamp());
				}
				
				dao.setElTypeId(jsonObj.optString("el_type_id", " "));
				dao.setElTypeDescr(jsonObj.optString("el_type_descr", " "));
				
				Integer stdtEnrolled = 0;
				
				try {
					stdtEnrolled = jsonObj.getInt("stdt_enrolled");
				} catch(Exception e) {
					stdtEnrolled = null;
				}
				dao.setStdtEnrolled(stdtEnrolled);
				dao.setPymtAmt(Double.isNaN(jsonObj.optDouble("pymt_amt")) ? null : new BigDecimal(jsonObj.optString("pymt_amt")));  
//				dao.setPmtCurrency(jsonObj.getString("pmt_currency"));
				
				dao.setChngUser(remoteUser);
				dao.setOpPageNam(opPageName);
				
				daoList.add(dao);
			}
		}
		
		if(daoList.size() > 0)
			elApplElTypeRepository.saveAll(daoList);
		
		// Delete removed record
		List<String> upsertDAOId = daoList.stream().map(dao -> dao.getId()).collect(Collectors.toList());
		
		List<ElApplElTypeDAO> removedDAOList = upsertDAOId.isEmpty() ? elApplElTypeRepository.findByApplHdrId(applHdrId)
				: elApplElTypeRepository.findByApplHdrIdForRemove(applHdrId, upsertDAOId);
		
		if(removedDAOList.size() > 0)
			elApplElTypeRepository.deleteAll(removedDAOList);
	}
	
	// One extra load type can have one payment method
	// One payment method has one payment schedule
	private void upsertPymtMethod(String applHdrId, JSONObject inputJson, boolean isOnGoing, boolean isSubmit, String opPageName) throws Exception {
		String remoteUser = SecurityUtils.getCurrentLogin();
		String modCtrlTxt = GeneralUtil.genModCtrlTxt();
		
		// Assume: 
		// 1. application now only have 1 extra load type
		// 2. 1 extra load type only have 1 payment method
		
		JSONObject methodJson = inputJson.optJSONObject("el_appl_pymt_method");
		JSONArray mpfJson = inputJson.optJSONArray("el_appl_mpf");
		if(mpfJson.length() > 0) {
			boolean error = false;
			JSONObject genMPFJson = new JSONObject();
			genMPFJson.put("role", inputJson.optString("role"));
			genMPFJson.put("pymt_type_cde", methodJson.optString("pymt_type_cde"));
			genMPFJson.put("pymt_schedule", methodJson.optJSONArray("pymt_schedule"));
			JSONArray regenMPF = generalApiService.calculateMPF(genMPFJson);
			if(mpfJson.length() != regenMPF.length()) {
				log.debug("Error of length diff");
				error = true;
			}
			
			
			for(int i = 0; i < regenMPF.length();i++) {
				JSONObject currInputMPF = mpfJson.getJSONObject(i);
				JSONObject currRegenMPF = regenMPF.getJSONObject(i);
				if(!currInputMPF.get("analysis_cde").toString().trim().equals(currRegenMPF.get("analysis_cde").toString().trim())) {
					log.debug("currInputMPF: " + currInputMPF.get("analysis_cde").toString().trim());
					log.debug("currRegenMPF: " + currRegenMPF.get("analysis_cde").toString().trim());
					log.debug("Error of analysis_cde diff");
					error = true;
				}
				if(!currInputMPF.get("acct_cde").toString().trim().equals(currRegenMPF.get("acct_cde").toString().trim())) {
					log.debug("currInputMPF: " + currInputMPF.get("acct_cde").toString().trim());
					log.debug("currRegenMPF: " + currRegenMPF.get("acct_cde").toString().trim());
					log.debug("Error of acct_cde diff");
					error = true;
				}
				if(!currInputMPF.get("fund_cde").toString().trim().equals(currRegenMPF.get("fund_cde").toString().trim())) {
					log.debug("currInputMPF: " + currInputMPF.get("fund_cde").toString().trim());
					log.debug("currRegenMPF: " + currRegenMPF.get("fund_cde").toString().trim());
					log.debug("Error of fund_cde diff");
					error = true;
				}
				if(!currInputMPF.get("dept_id").toString().trim().equals(currRegenMPF.get("dept_id").toString().trim())) {
					log.debug("currInputMPF: " + currInputMPF.get("dept_id").toString().trim());
					log.debug("currRegenMPF: " + currRegenMPF.get("dept_id").toString().trim());
					log.debug("Error of dept_id diff");
					error = true;
				}
				if(!currInputMPF.get("proj_id").toString().trim().equals(currRegenMPF.get("proj_id").toString().trim())) {
					log.debug("currInputMPF: " + currInputMPF.get("proj_id").toString().trim());
					log.debug("currRegenMPF: " + currRegenMPF.get("proj_id").toString().trim());
					log.debug("Error of proj_id diff");
					error = true;
				}
				if(!currInputMPF.get("class").toString().trim().equals(currRegenMPF.get("class").toString().trim())) {
					log.debug("currInputMPF: " + currInputMPF.get("class").toString().trim());
					log.debug("currRegenMPF: " + currRegenMPF.get("class").toString().trim());
					log.debug("Error of class diff");
					error = true;
				}
				BigDecimal currInputAmt = new BigDecimal(currInputMPF.get("pymt_amt").toString().trim()).setScale(2);
				BigDecimal currRegenAmt = new BigDecimal(currRegenMPF.get("pymt_amt").toString().trim()).setScale(2);
				if(!currInputAmt.equals(currRegenAmt)) {
					log.debug("currInputMPF: " + currInputAmt);
					log.debug("currRegenMPF: " + currRegenAmt);
					log.debug("Error of pymt_amt diff");
					error = true;
				}
				
				
				Timestamp currInputTime = new Timestamp(currInputMPF.optLong("pymt_dt"));
				Timestamp currRegenTime = new Timestamp(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.S").parse(currRegenMPF.getString("pymt_dt")).getTime());
				if(!currInputTime.equals(currRegenTime)) {
					log.debug("currInputTime: " + currInputTime.toLocalDateTime());
					log.debug("currRegenTime: " + currRegenTime.toLocalDateTime());
					log.debug("Error of time diff");
					error = true;
				}
				if(error) {
					throw new InvalidParameterException("Error when saving MPF, Please click \"Calculate MPF\" and try again.");
				}
				log.debug("Checking pass on " + i);
			}
		}
		List<ElApplElTypeDAO> elApplElTypeDAOList = elApplElTypeRepository.findByApplHdrId(applHdrId);
		if (elApplElTypeDAOList.isEmpty()) {
//			throw new RecordNotExistException("Extra Load Type Record");
			return;
		}
		
		methodJson.put("el_type_id", elApplElTypeDAOList.get(0).getElTypeId());
		
		String elApplPymtMethodId = methodJson.optString("el_appl_pymt_method_id");
		boolean isInsertPymtMethod = elApplPymtMethodId.isBlank();
		
		ElApplPymtMethodDAO elApplPymtMethodDAO = null;
		
		if (isInsertPymtMethod) {
			if(isOnGoing) {
				throw new InvalidParameterException("Re-submitted application cannot change payment method.");
			}
			elApplPymtMethodDAO = new ElApplPymtMethodDAO();
			elApplPymtMethodDAO.setCreatUser(remoteUser);
		} else {
			elApplPymtMethodDAO = elApplPymtMethodRepository.findOne(elApplPymtMethodId);
			
			if (elApplPymtMethodDAO == null) {
				throw new RecordNotExistException("Extra Load Application - Payment Details");
			}
			
			if (!elApplPymtMethodDAO.getModCtrlTxt().equals(methodJson.getString("mod_ctrl_txt"))) {
				throw new RecordModifiedException();
			}
			
			if (!elApplPymtMethodDAO.getApplHdrId().equals(applHdrId)) {
				throw new InvalidParameterException("Extra Load Application and Payment Details does not match");
			}
			
			elApplPymtMethodDAO.setModCtrlTxt(modCtrlTxt);
			elApplPymtMethodDAO.setChngDat(GeneralUtil.getCurrentTimestamp());
		}
		
		String elTypeId = methodJson.optString("el_type_id", " ");
		
		if (elTypeId.isBlank()) {
			throw new InvalidParameterException("Please selecte extra load type.");
		}
		
		ElApplElTypeDAO elApplElTypeDAO = elApplElTypeRepository.findByApplHdrIdAndElTypeId(applHdrId, methodJson.getString("el_type_id"));
		
		if (elApplElTypeDAO == null) {
			throw new RecordNotExistException("Selected Extra load type in payment ");
		}
		
		String applElTypeID = elApplElTypeDAO.getId();
		
		Timestamp pymnStartDt = GeneralUtil.NULLTIMESTAMP;
		Timestamp pymnEndDt = GeneralUtil.NULLTIMESTAMP;
		
		String pymtTypeCde = methodJson.optString("pymt_type_cde");
		JSONArray pymtScheduleArr = methodJson.optJSONArray("pymt_schedule");
		
		
		
		// use default value 
		if (pymtScheduleArr == null || pymtScheduleArr.length() == 0) {
			
		}
		
		else {
			
			// check user select payment method
			if (GeneralUtil.isBlankString(pymtTypeCde)) {
				throw new InvalidParameterException("Please select payment method in payment details.");
			}
			
			try {
				if (pymtTypeCde.equals(PymtTypeConstants.ONETIME)) {
					// assume should have 1 item in pymt_schedule array only
					Long startDtLong = pymtScheduleArr.optJSONObject(0).optLong("pymt_start_dt");
					pymnStartDt = GeneralUtil.initNullTimestampFromLong(startDtLong);
					pymnEndDt = pymnStartDt;
				}
				else if(pymtTypeCde.equals(PymtTypeConstants.INSTALM)) {
					// find earliest pymt_start_dt as start date, latest pymt_start_dt as end dat
					long earliestPymtStartDt = GeneralUtil.INFINTYTIMESTAMP.getTime();
					long latestPymtStartDt = 0;
					
					for (int k = 0; k < pymtScheduleArr.length(); k++) {
					    long pymtStartDt = pymtScheduleArr.optJSONObject(k).optLong("pymt_start_dt");
					    if (pymtStartDt < earliestPymtStartDt) {
					        earliestPymtStartDt = pymtStartDt;
					    }
					    if (pymtStartDt > latestPymtStartDt) {
					    	latestPymtStartDt = pymtStartDt;
					    }
					}
					
					pymnStartDt = GeneralUtil.initNullTimestampFromLong(earliestPymtStartDt);
					pymnEndDt =  GeneralUtil.initNullTimestampFromLong(latestPymtStartDt);
				}
				else if(pymtTypeCde.equals(PymtTypeConstants.RECURR)) {
					// assume should have 1 item in pymt_schedule array only
					Long startDtLong = pymtScheduleArr.optJSONObject(0).optLong("pymt_start_dt");
					pymnStartDt = GeneralUtil.initNullTimestampFromLong(startDtLong);
					Long endDtLong = pymtScheduleArr.optJSONObject(0).optLong("pymt_end_dt");
					pymnEndDt = GeneralUtil.initNullTimestampFromLong(endDtLong);
				}
				else {
					throw new InvalidParameterException("Please select an correct payment method in payment details.");
				}
			} catch (Exception e) {
				throw new InvalidDateException();
			}
		}
		
		elApplPymtMethodDAO.setApplHdrId(applHdrId);
		elApplPymtMethodDAO.setApplElTypeId(applElTypeID);
		elApplPymtMethodDAO.setPymtCategory("SALARY");
		elApplPymtMethodDAO.setPymtLineNo(1);
		elApplPymtMethodDAO.setPymtTypeCde(pymtTypeCde);
		
		elApplPymtMethodDAO.setPymtFreq(pymtTypeCde.equals(PymtTypeConstants.RECURR) ? "M" : " ");
		elApplPymtMethodDAO.setPymtAmt(Double.isNaN(methodJson.optDouble("pymt_amt")) ? null : new BigDecimal(methodJson.optString("pymt_amt")));
		
		elApplPymtMethodDAO.setPymtStartDt(GeneralUtil.truncateDate(pymnStartDt));
		elApplPymtMethodDAO.setPymtEndDt(GeneralUtil.truncateDate(pymnEndDt));
		
		elApplPymtMethodDAO.setChngUser(remoteUser);
		elApplPymtMethodDAO.setOpPageNam(opPageName);
		
		elApplPymtMethodRepository.save(elApplPymtMethodDAO);
		
		// OW2 index
		ElApplHdrDAO elApplHdrDAO = elApplHdrRepository.findOne(applHdrId);
		int ow2 = elApplHdrDAO.getOw2AttachedInd();
		
		ElTypeSalElemntTabDAO salElemntDAO = elTypeSalElemntTabRepository.findByElTypeIdAndPymtTypeCdeAndObsolete(elTypeId, pymtTypeCde, 0);
		if(salElemntDAO == null) {
			throw new InvalidParameterException("No vaild salary element for extra load type and payment method!");
		}
		String salElemnt = ow2 == 0 ? salElemntDAO.getSalElemnt() : salElemntDAO.getSalElemntOw2();
		
		boolean isInsertMPFPymtMethod = inputJson.optInt("el_appl_mpf_updated") == 1 ? true : false;
		//tmp
		isInsertMPFPymtMethod = true;
		
		ElApplPymtMethodDAO elApplMPFPymtMethodDAO = null;
		
		if (isInsertMPFPymtMethod) {
			elApplPymtMethodRepository.deleteAll(elApplPymtMethodRepository.findMPFMethodByApplHdrId(applHdrId));
			elApplMPFPymtMethodDAO = new ElApplPymtMethodDAO();
			elApplMPFPymtMethodDAO.setCreatUser(remoteUser);
		} else {
			// assume there is only 1 MPF method and it is insert the same time as SALARY
			elApplMPFPymtMethodDAO = elApplPymtMethodRepository.findMPFMethodByApplHdrId(applHdrId).get(0);
			
			if (elApplMPFPymtMethodDAO == null) {
				throw new RecordNotExistException("Extra Load Application - Payment Details");
			}
			
			if (!elApplMPFPymtMethodDAO.getModCtrlTxt().equals(methodJson.getString("mod_ctrl_txt"))) {
				throw new RecordModifiedException();
			}
			
			if (!elApplMPFPymtMethodDAO.getApplHdrId().equals(applHdrId)) {
				throw new InvalidParameterException("Extra Load Application and Payment Details does not match");
			}
			
		}
		
		pymtTypeCde = " ";
		

		elApplMPFPymtMethodDAO.setApplHdrId(applHdrId);
		elApplMPFPymtMethodDAO.setApplElTypeId(applElTypeID);
		elApplMPFPymtMethodDAO.setPymtCategory("MPF");
		elApplMPFPymtMethodDAO.setPymtLineNo(1);
		elApplMPFPymtMethodDAO.setPymtTypeCde(pymtTypeCde);
		
		elApplMPFPymtMethodDAO.setPymtFreq(" ");
		
		elApplMPFPymtMethodDAO.setPymtStartDt(GeneralUtil.truncateDate(pymnStartDt));
		elApplMPFPymtMethodDAO.setPymtEndDt(GeneralUtil.truncateDate(pymnEndDt));

		elApplMPFPymtMethodDAO.setModCtrlTxt(modCtrlTxt);
		elApplMPFPymtMethodDAO.setChngDat(GeneralUtil.getCurrentTimestamp());
		elApplMPFPymtMethodDAO.setChngUser(remoteUser);
		elApplMPFPymtMethodDAO.setOpPageNam(opPageName);
		
		// also include save elApplMPFPymtMethodDAO
		upsertElPymtScheduleAndMPF(applHdrId, elApplPymtMethodDAO.getId(), pymtScheduleArr, salElemnt, elApplMPFPymtMethodDAO, mpfJson, elApplPymtMethodDAO, elApplElTypeDAO, isSubmit, opPageName);
	}
//	private void upsertPymtMethod(String applHdrId, JSONArray inputJson, String opPageName) throws Exception {
//		String remoteUser = SecurityUtils.getCurrentLogin();
//		
//		List<ElApplPymtMethodDAO> elApplPymtMethodDAOList = new ArrayList<>();
//				
//		// Rebuilt JSONArray if selected all, put el_type_id
//		// Assume that in edit case that will be no "ALL"
//		if (inputJson.length() == 1 && inputJson.getJSONObject(0).optString("el_type_id").equals("ALL")) {
//			List<ElApplElTypeDAO> elApplElTypeDAOList = elApplElTypeRepository.findByApplHdrId(applHdrId);
//			
//			if (elApplElTypeDAOList.isEmpty()) {
//				throw new RecordNotExistException("Extra Load Type Application Amonut Record ");
//			}
//			
//			for (int i=0;i<elApplElTypeDAOList.size();i++) {
//				if (i==0) {
//					inputJson.getJSONObject(0).put("el_type_id", elApplElTypeDAOList.get(i).getElTypeId());
//				} else {
//					inputJson.put(new JSONObject(inputJson.getJSONObject(0).toString()).put("el_type_id", elApplElTypeDAOList.get(i).getElTypeId()));
//				}
//			}
//		}
//		
//		for (int i=0; i< inputJson.length(); i++) {
//			JSONObject jsonObj = inputJson.getJSONObject(i);
//			String elApplPymtMethodId = jsonObj.optString("el_appl_pymt_method_id");
//			boolean isInsertPymtMethod = elApplPymtMethodId.isBlank();
//			
//			ElApplPymtMethodDAO elApplPymtMethodDAO = null;
//			if (isInsertPymtMethod) {
//				elApplPymtMethodDAO = new ElApplPymtMethodDAO();
//				elApplPymtMethodDAO.setCreatUser(remoteUser);
//			} else {
//				elApplPymtMethodDAO = elApplPymtMethodRepository.findOne(elApplPymtMethodId);
//				
//				if (elApplPymtMethodDAO == null) {
//					throw new RecordNotExistException("Extra Load Application - Payment Details");
//				}
//				
//				if (!elApplPymtMethodDAO.getModCtrlTxt().equals(jsonObj.getString("mod_ctrl_txt"))) {
//					throw new RecordModifiedException();
//				}
//				
//				if (!elApplPymtMethodDAO.getApplHdrId().equals(applHdrId)) {
//					throw new InvalidParameterException("Extra Load Application and Payment Details does not match");
//				}
//				
//				elApplPymtMethodDAO.setModCtrlTxt(GeneralUtil.genModCtrlTxt());
//				elApplPymtMethodDAO.setChngDat(GeneralUtil.getCurrentTimestamp());
//			}
//			
//			String elTypeId = jsonObj.optString("el_type_id");
//			
//			if (elTypeId.isBlank()) {
//				throw new InvalidParameterException("Please selecte extra load type in payment details.");
//			}
//			
//			ElApplElTypeDAO elApplElTypeDAO = elApplElTypeRepository.findByApplHdrIdAndElTypeId(applHdrId, jsonObj.getString("el_type_id"));
//			
//			if (elApplElTypeDAO == null) {
//				throw new RecordNotExistException("Selected Extra load type in payment ");
//			}
//			
//			String applElTypeID = elApplElTypeDAO.getId();
//			Timestamp pymnStartDt = GeneralUtil.NULLTIMESTAMP;
//			Timestamp pymnEndDt = GeneralUtil.NULLTIMESTAMP;
//			Integer pymtFreq = jsonObj.getInt("pymt_freq");
//			
//			String pymtTypeCde = jsonObj.optString("pymt_type_cde");
//			JSONArray pymtScheduleArr = jsonObj.optJSONArray("pymt_schedule");
//			
//			// use default value 
//			if (pymtScheduleArr == null || pymtScheduleArr.length() == 0) {
//				
//			}
//			
//			else {
//				try {
//					if (pymtTypeCde.equals(PymtTypeConstants.ONETIME)) {
//						// assume should have 1 item in pymt_schedule array only
//						Long startDtLong = pymtScheduleArr.optJSONObject(0).optLong("pymt_start_dt");
//						pymnStartDt = GeneralUtil.initNullTimestampFromLong(startDtLong);
//						pymnEndDt = pymnStartDt;
//						pymtFreq = 1;
//					}
//					else if(pymtTypeCde.equals(PymtTypeConstants.INSTALM)) {
//						// find earliest pymt_start_dt as start date, latest pymt_start_dt as end dat
//						long earliestPymtStartDt = GeneralUtil.INFINTYTIMESTAMP.getTime();
//						long latestPymtStartDt = 0;
//						
//						for (int k = 0; k < pymtScheduleArr.length(); k++) {
//						    long pymtStartDt = pymtScheduleArr.optJSONObject(k).optLong("pymt_start_dt");
//						    if (pymtStartDt < earliestPymtStartDt) {
//						        earliestPymtStartDt = pymtStartDt;
//						    }
//						    if (pymtStartDt > latestPymtStartDt) {
//						    	latestPymtStartDt = pymtStartDt;
//						    }
//						}
//						
//						pymnStartDt = GeneralUtil.initNullTimestampFromLong(earliestPymtStartDt);
//						pymnEndDt =  GeneralUtil.initNullTimestampFromLong(latestPymtStartDt);
//						pymtFreq = pymtScheduleArr.length();
//					}
//					else if(pymtTypeCde.equals(PymtTypeConstants.RECURR)) {
//						// assume should have 1 item in pymt_schedule array only
//						Long startDtLong = pymtScheduleArr.optJSONObject(0).optLong("pymt_start_dt");
//						pymnStartDt = GeneralUtil.initNullTimestampFromLong(startDtLong);
//						Long endDtLong = pymtScheduleArr.optJSONObject(0).optLong("pymt_end_dt");
//						pymnEndDt = GeneralUtil.initNullTimestampFromLong(endDtLong);
//						
////						pymtFreq = calculatePaymentDates(pymnStartDt, pymnEndDt).size();
//					}
//				} catch (Exception e) {
//					throw new InvalidDateException();
//				}
//			}
//			
//			elApplPymtMethodDAO.setApplHdrId(applHdrId);
//			elApplPymtMethodDAO.setApplElTypeId(applElTypeID);
//			elApplPymtMethodDAO.setPymtLineNo(i + 1);
//			elApplPymtMethodDAO.setPymtTypeCde(pymtTypeCde);
//			elApplPymtMethodDAO.setPymtFreq("M");
//			elApplPymtMethodDAO.setPymtAmt(elApplElTypeDAO.getPymtAmt());
//			elApplPymtMethodDAO.setPymtStartDt(GeneralUtil.truncateDate(pymnStartDt));
//			elApplPymtMethodDAO.setPymtEndDt(GeneralUtil.truncateDate(pymnEndDt));
//			
//			elApplPymtMethodDAO.setChngUser(remoteUser);
//			elApplPymtMethodDAO.setOpPageNam(opPageName);
//			
//			elApplPymtMethodDAOList.add(elApplPymtMethodDAO);
//			
//			jsonObj.put("appl_pymt_method_id", elApplPymtMethodDAO.getId());
//		}
//		
//		elApplPymtMethodRepository.saveAll(elApplPymtMethodDAOList);
//		
//		// Delete removed records
//		List<String> upsertDAOId = elApplPymtMethodDAOList.stream().map(dao -> dao.getId()).collect(Collectors.toList());
//		
//		List<ElApplPymtMethodDAO> removedDAOList = upsertDAOId.isEmpty()
//				? elApplPymtMethodRepository.findByApplHdrId(applHdrId)
//				: elApplPymtMethodRepository.findByApplHdrIdForRemove(applHdrId, upsertDAOId);
//
//		elApplPymtMethodRepository.deleteAll(removedDAOList);
//	}
	
	private void upsertElPymtScheduleAndMPF(String applHdrId, String pymtMethodId, JSONArray inputJson, String salElemnt, ElApplPymtMethodDAO elApplMPFPymtMethodDAO, JSONArray mpfJson, ElApplPymtMethodDAO elApplPymtMethodDAO, ElApplElTypeDAO elApplElTypeDAO, boolean isSubmit, String opPageName) throws Exception {
		String remoteUser = SecurityUtils.getCurrentLogin();
		
		List<ElApplPymtScheduleDAO> daoList = new ArrayList<>();
		for (int i = 0; i < inputJson.length(); i++) {

			String pymtMethodCde = elApplPymtMethodDAO.getPymtTypeCde();
			JSONObject schHdr = inputJson.optJSONObject(i);
			
			Timestamp pymnStartDt = GeneralUtil.NULLTIMESTAMP;
			Timestamp pymnEndDt = GeneralUtil.NULLTIMESTAMP;
			
			Long startDtLong = schHdr.optLong("pymt_start_dt");
			Long endDtLong = !schHdr.isNull("pymt_end_dt") ? schHdr.optLong("pymt_end_dt") : schHdr.optLong("pymt_start_dt");
			
			
			pymnStartDt = GeneralUtil.initNullTimestampFromLong(startDtLong);
			pymnEndDt = GeneralUtil.initNullTimestampFromLong(endDtLong);
			
			JSONArray schLine = schHdr.optJSONArray("details");
			
			for (int j = 0; j < schLine.length(); j++) {
				JSONObject detailObj = schLine.optJSONObject(j);
				
				ElApplPymtScheduleDAO elApplPymtScheduleDAO = new ElApplPymtScheduleDAO();
				
				elApplPymtScheduleDAO.setApplHdrId(applHdrId);
				elApplPymtScheduleDAO.setApplPymtMethodId(pymtMethodId);
				elApplPymtScheduleDAO.setPymtSchedNo(i + 1);
				elApplPymtScheduleDAO.setPymtSchedLine(j + 1);
				elApplPymtScheduleDAO.setSalElemnt(salElemnt);
				
				elApplPymtScheduleDAO.setPymtStartDt(pymnStartDt);
				elApplPymtScheduleDAO.setPymtEndDt(pymnEndDt);

				elApplPymtScheduleDAO.setProjId(!(detailObj.isNull("proj_id") || detailObj.getString("proj_id").isBlank()) ? detailObj.getString("proj_id") : " ");
				elApplPymtScheduleDAO.setProjNbr(!(detailObj.isNull("proj_nbr") || detailObj.getString("proj_nbr").isBlank()) ? detailObj.getString("proj_nbr") : " ");
				elApplPymtScheduleDAO.setDeptId(!(detailObj.isNull("dept_id") || detailObj.getString("dept_id").isBlank()) ? detailObj.getString("dept_id") : " ");
				elApplPymtScheduleDAO.setFundCde(!(detailObj.isNull("fund_cde") || detailObj.getString("fund_cde").isBlank()) ? detailObj.getString("fund_cde") : " ");
				elApplPymtScheduleDAO.setClassCde(!(detailObj.isNull("class") || detailObj.getString("class").isBlank()) ? detailObj.getString("class") : " ");
				elApplPymtScheduleDAO.setAcctCde(!(detailObj.isNull("acct_cde") || detailObj.getString("acct_cde").isBlank()) ? detailObj.getString("acct_cde") : " ");
				elApplPymtScheduleDAO.setAnalysisCde(!(detailObj.isNull("analysis_cde") || detailObj.getString("analysis_cde").isBlank()) ? detailObj.getString("analysis_cde") : " ");
				
				elApplPymtScheduleDAO.setBcoAprvId(!(detailObj.isNull("bco_aprv_id") || detailObj.getString("bco_aprv_id").isBlank()) ? detailObj.getString("bco_aprv_id") : " ");
				elApplPymtScheduleDAO.setBcoAprvName(!(detailObj.isNull("bco_aprv_name") || detailObj.getString("bco_aprv_name").isBlank()) ? detailObj.getString("bco_aprv_name") : " ");
				
				elApplPymtScheduleDAO.setPymtLineAmt(Double.isNaN(detailObj.optDouble("pymt_amt")) ? null : new BigDecimal(detailObj.optDouble("pymt_amt")));

				if(pymtMethodCde.equals(PymtTypeConstants.RECURR) && pymnStartDt != GeneralUtil.NULLTIMESTAMP && pymnEndDt != GeneralUtil.NULLTIMESTAMP && elApplPymtScheduleDAO.getPymtLineAmt() != null) {
					elApplPymtScheduleDAO.setPymtLineAmtTot(generalApiService.calculateCOARecurrentTotalAmount(startDtLong, endDtLong, elApplPymtScheduleDAO.getPymtLineAmt()));
				}else {
					elApplPymtScheduleDAO.setPymtLineAmtTot(elApplPymtScheduleDAO.getPymtLineAmt());
				}
				
				//elApplPymtScheduleDAO.setPymtStatusCde(PymtStatusConstants.PENDING);
				
				elApplPymtScheduleDAO.setCreatUser(remoteUser);
				elApplPymtScheduleDAO.setChngUser(remoteUser);
				elApplPymtScheduleDAO.setOpPageNam(opPageName);
				
				daoList.add(elApplPymtScheduleDAO);
			}
		}
		
		BigDecimal totalAmount = new BigDecimal(0.0);
		for (int i = 0; i < mpfJson.length(); i++) {
			
			JSONObject detailObj = mpfJson.optJSONObject(i);
			
			Timestamp pymnStartDt = GeneralUtil.NULLTIMESTAMP;
			Timestamp pymnEndDt = GeneralUtil.NULLTIMESTAMP;
			
			Long startDtLong = detailObj.optLong("pymt_dt");
			Long endDtLong = detailObj.optLong("pymt_dt");
			
			pymnStartDt = GeneralUtil.initNullTimestampFromLong(startDtLong);
			pymnEndDt = GeneralUtil.initNullTimestampFromLong(endDtLong);
			
			//JSONArray schLine = schHdr.optJSONArray("el_appl_mpf");
			
			//for (int j = 0; j < schLine.length(); j++) {
				//JSONObject detailObj = inputJson.optJSONObject(i);
				
				if(!Double.isNaN(detailObj.optDouble("pymt_amt"))) totalAmount = totalAmount.add(new BigDecimal(detailObj.optString("pymt_amt")));
				
				ElApplPymtScheduleDAO elApplPymtScheduleDAO = new ElApplPymtScheduleDAO();
				
				elApplPymtScheduleDAO.setApplHdrId(applHdrId);
				elApplPymtScheduleDAO.setPymtSchedNo(detailObj.optInt("rowNum"));
				elApplPymtScheduleDAO.setPymtSchedLine(1);
				elApplPymtScheduleDAO.setApplPymtMethodId(elApplMPFPymtMethodDAO.getId());
				
				elApplPymtScheduleDAO.setPymtStartDt(pymnStartDt);
				elApplPymtScheduleDAO.setPymtEndDt(pymnEndDt);
				
				elApplPymtScheduleDAO.setProjId(!(detailObj.isNull("proj_id") || detailObj.getString("proj_id").isBlank()) ? detailObj.getString("proj_id") : " ");
				elApplPymtScheduleDAO.setProjNbr(!(detailObj.isNull("proj_nbr") || detailObj.getString("proj_nbr").isBlank()) ? detailObj.getString("proj_nbr") : " ");
				elApplPymtScheduleDAO.setDeptId(!(detailObj.isNull("dept_id") || detailObj.getString("dept_id").isBlank()) ? detailObj.getString("dept_id") : " ");
				elApplPymtScheduleDAO.setFundCde(!(detailObj.isNull("fund_cde") || detailObj.getString("fund_cde").isBlank()) ? detailObj.getString("fund_cde") : " ");
				elApplPymtScheduleDAO.setClassCde(!(detailObj.isNull("class") || detailObj.getString("class").isBlank()) ? detailObj.getString("class") : " ");
				elApplPymtScheduleDAO.setAcctCde(!(detailObj.isNull("acct_cde") || detailObj.getString("acct_cde").isBlank()) ? detailObj.getString("acct_cde") : " ");
				elApplPymtScheduleDAO.setAnalysisCde(!(detailObj.isNull("analysis_cde") || detailObj.getString("analysis_cde").isBlank()) ? detailObj.getString("analysis_cde") : " ");
				
				elApplPymtScheduleDAO.setBcoAprvId(!(detailObj.isNull("bco_aprv_id") || detailObj.getString("bco_aprv_id").isBlank()) ? detailObj.getString("bco_aprv_id") : " ");
				elApplPymtScheduleDAO.setBcoAprvName(!(detailObj.isNull("bco_aprv_name") || detailObj.getString("bco_aprv_name").isBlank()) ? detailObj.getString("bco_aprv_name") : " ");
				
				elApplPymtScheduleDAO.setPymtLineAmt(Double.isNaN(detailObj.optDouble("pymt_amt")) ? null : new BigDecimal(detailObj.optString("pymt_amt")));
				
				elApplPymtScheduleDAO.setPymtLineAmtTot(elApplPymtScheduleDAO.getPymtLineAmt());
				
				//elApplPymtScheduleDAO.setPymtStatusCde(PymtStatusConstants.PENDING);
				
				elApplPymtScheduleDAO.setCreatUser(remoteUser);
				elApplPymtScheduleDAO.setChngUser(remoteUser);
				elApplPymtScheduleDAO.setOpPageNam(opPageName);
				
				daoList.add(elApplPymtScheduleDAO);
			//}
			
		}
		
		elApplMPFPymtMethodDAO.setPymtAmt(totalAmount);
		if(isSubmit && elApplElTypeDAO.getPymtAmt().compareTo(elApplMPFPymtMethodDAO.getPymtAmt().add(elApplPymtMethodDAO.getPymtAmt())) != 0 ) {
			throw new InvalidParameterException("The input extra load amount does not match the sum of salary and MPF!");
		}
		
		
		elApplPymtMethodRepository.save(elApplMPFPymtMethodDAO);
		// find exist to delete
		List<ElApplPymtScheduleDAO> removedDAOList = elApplPymtScheduleRepository.findByApplHdrId(applHdrId);
		
		elApplPymtScheduleRepository.deleteAll(removedDAOList);
		elApplPymtScheduleRepository.saveAll(daoList);
	}
	
//	private void upsertElPymtSchedule(String applHdrId, JSONArray inputJson, String opPageName) throws Exception {
//		String remoteUser = SecurityUtils.getCurrentLogin();
//		
//		List<ElApplPymtScheduleDAO> daoList = new ArrayList<>();
//
//		for (int i = 0; i < inputJson.length(); i++) {
//			JSONObject jsonObj = inputJson.getJSONObject(i);
//			
//			String pymtTypeCde = jsonObj.optString("pymt_type_cde");
//			String elTypeId = jsonObj.optString("el_type_id");
//			String applPymtMethodId = jsonObj.optString("appl_pymt_method_id");
//			
//			// Find payment method
//			ElApplPymtMethodDAO elApplPymtMethodDAO = elApplPymtMethodRepository.findOne(applPymtMethodId);
//			if (elApplPymtMethodDAO == null) {
//				throw new RecordNotExistException("Payment Method Record");
//			}
//			
//			// Find salary element mapping if payment method selected
//			String salElemnt = "";
//			
//			if (!pymtTypeCde.isBlank()) {
//				ElTypeSalElemntTabDAO salElemntDAO = elTypeSalElemntTabRepository.findByElTypeIdAndPymtTypeCdeAndObsolete(elTypeId, pymtTypeCde, 0);
//
//				if (salElemntDAO == null) {
//					throw new RecordNotExistException("Salary Elemtent for Extra load type in payment");
//				}
//				
//				if (elApplHdrRepository.findOne(applHdrId).getOw2AttachedInd().equals(0)) {
//					salElemnt = salElemntDAO.getSalElemnt();
//				} else {
//					salElemnt = salElemntDAO.getSalElemntOw2();
//				}
//			}
//
//			// Start insert
//			// Skip if no payment schedule selected
//			JSONArray pymtScheduleArr = jsonObj.optJSONArray("pymt_schedule");
//			if (pymtScheduleArr == null || pymtScheduleArr.length() == 0) {
//				continue;
//			}
//			
//			// Case 1 One-Time and Case 2 Installment
//			if (pymtTypeCde.equals(PymtTypeConstants.ONETIME) || pymtTypeCde.equals(PymtTypeConstants.INSTALM)) {
//				for (int j=0; j<pymtScheduleArr.length();j++) {
//					JSONObject pymtScheduleObj = pymtScheduleArr.getJSONObject(j);
//					String elApplPymtMethodId = pymtScheduleObj.optString("el_appl_pymt_schedule_id");
//					boolean isInsert = elApplPymtMethodId.isBlank();
//
//					ElApplPymtScheduleDAO elApplPymtScheduleDAO = null;
//					
//					if (isInsert) {
//						elApplPymtScheduleDAO = new ElApplPymtScheduleDAO();
//						elApplPymtScheduleDAO.setCreatUser(remoteUser);
//					} else {
//						elApplPymtScheduleDAO = elApplPymtScheduleRepository.findOne(elApplPymtMethodId);
//						
//						if (elApplPymtScheduleDAO == null) {
//							throw new RecordNotExistException("Extra Load Application - Payment Schedule");
//						}
//						
//						if (!elApplPymtScheduleDAO.getModCtrlTxt().equals(pymtScheduleObj.getString("mod_ctrl_txt"))) {
//							throw new RecordModifiedException();
//						}
//						
//						if (!elApplPymtScheduleDAO.getApplHdrId().equals(applHdrId)) {
//							throw new InvalidParameterException("Extra Load Application and Payment Schedule does not match");
//						}
//						
//						elApplPymtScheduleDAO.setModCtrlTxt(GeneralUtil.genModCtrlTxt());
//						elApplPymtScheduleDAO.setChngDat(GeneralUtil.getCurrentTimestamp());
//					}
//					
//					elApplPymtScheduleDAO.setApplHdrId(applHdrId);
//					elApplPymtScheduleDAO.setApplPymtMethodId(applPymtMethodId);
////					elApplPymtScheduleDAO.setPymtSchedNo(null);
//					elApplPymtScheduleDAO.setSalElemnt(salElemnt);
//					
//					Long startDtLong = pymtScheduleObj.optLong("pymt_start_dt");
//					Timestamp pymnStartDt = GeneralUtil.initNullTimestampFromLong(startDtLong);
//					
//					elApplPymtScheduleDAO.setPymtDt(GeneralUtil.truncateDate(pymnStartDt));
//					elApplPymtScheduleDAO.setPymtStatusCde(PymtStatusConstants.PENDING);
//					
//					// If one time, use pymt_amt saved in EL_APPL_PYMT_METHOD
//					// ELSE user amount inputed by user
//					elApplPymtScheduleDAO.setPymtAmt(pymtTypeCde.equals(PymtTypeConstants.ONETIME) ? elApplPymtMethodDAO.getPymtAmt() : (new BigDecimal(pymtScheduleObj.optDouble("pymt_amt"))));
//					
//					elApplPymtScheduleDAO.setChngUser(remoteUser);
//					elApplPymtScheduleDAO.setOpPageNam(opPageName);
//					
//					daoList.add(elApplPymtScheduleDAO);
//				}
//			}
//			// Case 3 Recurrent
//			// All current records in DB will be deleted
//			else if (pymtTypeCde.equals(PymtTypeConstants.RECURR)) {
//				JSONObject pymtScheduleObj = pymtScheduleArr.getJSONObject(0);
//				Long startDtLong = pymtScheduleObj.optLong("pymt_start_dt");
//				Long endDtLong = pymtScheduleObj.optLong("pymt_end_dt");
//
//				// FOR Save as Draft: insert only if both start and end date is selected
//				if (startDtLong == 0 || endDtLong == 0) {
//					continue;
//				}
//				
//				Timestamp pymnStartDt = new Timestamp(startDtLong);
//				Timestamp pymnEndDt = new Timestamp(endDtLong);
//				List<Timestamp> paymentDates = calculatePaymentDates(pymnStartDt, pymnEndDt);
//				
//				// Divide Payment amount by no. of periods
//				BigDecimal pymtAmt = null;
//
//				if (elApplPymtMethodDAO.getPymtAmt() != null) {
//					pymtAmt = elApplPymtMethodDAO.getPymtAmt().divide(new BigDecimal(paymentDates.size()), 2, RoundingMode.HALF_UP);
//				}
//				
//				for (Timestamp paymentDate :paymentDates) {
//					ElApplPymtScheduleDAO elApplPymtScheduleDAO = new ElApplPymtScheduleDAO();
//					
//					elApplPymtScheduleDAO.setApplHdrId(applHdrId);
//					elApplPymtScheduleDAO.setApplPymtMethodId(applPymtMethodId);
////					elApplPymtScheduleDAO.setPymtSchedNo(null);
//					elApplPymtScheduleDAO.setSalElemnt(salElemnt);
//					elApplPymtScheduleDAO.setPymtAmt(pymtAmt);
//					elApplPymtScheduleDAO.setPymtDt(paymentDate);
//					elApplPymtScheduleDAO.setPymtStatusCde(PymtStatusConstants.PENDING);
//					
//					elApplPymtScheduleDAO.setCreatUser(remoteUser);
//					elApplPymtScheduleDAO.setChngUser(remoteUser);
//					elApplPymtScheduleDAO.setOpPageNam(opPageName);
//					
//					daoList.add(elApplPymtScheduleDAO);				
//				}
//			}
//		}
//		// TODO validate total amount in schedule against payment method and el_appl_el_type ?
//		
//		elApplPymtScheduleRepository.saveAll(daoList);
//		
//		// Delete removed records
//		List<String> upsertDAOId = daoList.stream().map(dao -> dao.getId()).collect(Collectors.toList());
//		
//		List<ElApplPymtScheduleDAO> removedDAOList = upsertDAOId.isEmpty()
//				? elApplPymtScheduleRepository.findByApplHdrId(applHdrId)
//				: elApplPymtScheduleRepository.findByApplHdrIdForRemove(applHdrId, upsertDAOId);
//		
//		elApplPymtScheduleRepository.deleteAll(removedDAOList);
//	}
	
//	private void upsertElApplBudget(String applHdrId, JSONArray inputJson, String opPageName) throws Exception {
//		String remoteUser = SecurityUtils.getCurrentLogin();
//		
//		// No need to save if array is empty
//		if (inputJson.length() == 0) {
//			return;
//		}
//		
//		// Rebuilt JSONArray if selected all, put el_type_id
//		if (inputJson.length() == 1 && inputJson.getJSONObject(0).optString("el_type_id").equals("ALL")) {
//			List<ElApplElTypeDAO> elApplElTypeDAOList = elApplElTypeRepository.findByApplHdrId(applHdrId);
//			
//			if (elApplElTypeDAOList.isEmpty()) {
//				throw new RecordNotExistException("Extra Load Type Application Amonut Record ");
//			}
//			
//			for (int i=0;i<elApplElTypeDAOList.size();i++) {
//				if (i==0) {
//					inputJson.getJSONObject(0).put("el_type_id", elApplElTypeDAOList.get(i).getElTypeId());
//				} else {
//					inputJson.put(new JSONObject(inputJson.getJSONObject(0).toString()).put("el_type_id", elApplElTypeDAOList.get(i).getElTypeId()));
//				}
//			}
//		}
//		
//		List<ElApplBudgetDAO> daoList = new ArrayList<>();
//		
//		for (int i=0; i< inputJson.length(); i++) {
//			JSONObject jsonObj = inputJson.getJSONObject(i);
//			JSONArray detailsArr = jsonObj.optJSONArray("details");
//						
//			// validation
//			ElApplElTypeDAO elApplElTypeDAO = elApplElTypeRepository.findByApplHdrIdAndElTypeId(applHdrId, jsonObj.optString("el_type_id"));
//			
//			String applElTypeID = null;
//			
//			if (elApplElTypeDAO == null) {
////				throw new RecordNotExistException("Selected Extra load type in payment ");
//			} else {
//				applElTypeID = elApplElTypeDAO.getId();
//			}
//			
//			for (int j =0;j < detailsArr.length(); j++) {
//				JSONObject detailObj = detailsArr.getJSONObject(j);
//				
//				String elApplBudgetId = detailObj.optString("el_appl_budget_id");
//				boolean isInsert = elApplBudgetId.isBlank();
//				
//				ElApplBudgetDAO dao = null;
//				
//				if (isInsert) {
//					dao = new ElApplBudgetDAO();
//					dao.setCreatUser(remoteUser);
//				} else {
//					dao = elApplBudgetRepository.findOne(elApplBudgetId);
//					
//					if (dao == null) {
//						throw new RecordNotExistException("Extra Load Application - Budget");
//					}
//					
//					if (!dao.getModCtrlTxt().equals(detailObj.getString("mod_ctrl_txt"))) {
//						throw new RecordModifiedException();
//					}
//					
//					if (!dao.getApplHdrId().equals(applHdrId)) {
//						throw new InvalidParameterException("Extra Load Application and Budget Details does not match");
//					}
//					
//					dao.setModCtrlTxt(GeneralUtil.genModCtrlTxt());
//					dao.setChngDat(GeneralUtil.getCurrentTimestamp());
//				}
//								
//				dao.setApplHdrId(applHdrId);
//				dao.setApplElTypeId(applElTypeID);
//				dao.setAcctCde(GeneralUtil.initBlankString(detailObj.optString("acct_cde").trim()));
//				dao.setAnalysisCde(GeneralUtil.initBlankString(detailObj.optString("analysis_cde").trim()));
//				dao.setFundCde(GeneralUtil.initBlankString(detailObj.optString("fund_cde").trim()));
//				dao.setProjId(GeneralUtil.initBlankString(detailObj.optString("proj_id").trim()));
//				dao.setProjNbr(GeneralUtil.initBlankString(detailObj.optString("proj_nbr").trim()));
//				dao.setClassCde(GeneralUtil.initBlankString(detailObj.optString("class").trim()));
//				dao.setBcoAprvId(GeneralUtil.initBlankString(detailObj.optString("bco_aprv_id").trim()));
//				dao.setBcoAprvName(GeneralUtil.initBlankString(detailObj.optString("bco_aprv_name").trim()));
//				
//				// TODO temp handle to test save as draft
//				dao.setBudgAcctShare(Double.isNaN(detailObj.optDouble("budg_acct_share")) ? null : new BigDecimal(Math.min(1, detailObj.optDouble("budg_acct_share"))));
//				dao.setBudgAcctAmt(Double.isNaN(detailObj.optDouble("budg_acct_amt")) ? null : new BigDecimal(detailObj.optDouble("budg_acct_amt")));
//				
//				dao.setChngUser(remoteUser);
//				dao.setOpPageNam(opPageName);
//				
//				daoList.add(dao);
//			}
//		}
//		
//		elApplBudgetRepository.saveAll(daoList);
//		
//		// Delete removed records
//		List<String> upsertDAOId = daoList.stream().map(dao -> dao.getId()).collect(Collectors.toList());
//		
//		List<ElApplBudgetDAO> removedDAOList = upsertDAOId.isEmpty() ? elApplBudgetRepository.findByApplHdrId(applHdrId)
//				: elApplBudgetRepository.findByApplHdrIdForRemove(applHdrId, upsertDAOId);
//		
//		elApplBudgetRepository.deleteAll(removedDAOList);
//	}
	
	private void upsertElApplApprv(String applHdrId, JSONArray inputJson, boolean isSubmit, String opPageName) throws Exception {
		for (int i=0; i< inputJson.length(); i++) {
			JSONObject jsonObj = inputJson.getJSONObject(i);
			
			if (jsonObj.optString("aprv_type_cde").isBlank() || jsonObj.optString("aprv_user_id").isBlank()) {
				throw new InvalidParameterException("Approver Type / Approver name is required");
			}
		}
		
		
		String remoteUser = SecurityUtils.getCurrentLogin();
		List<ElApplAprvStatusDAO> daoList = new ArrayList<>();
		
		// Delete all old record before insert new
		List<ElApplAprvStatusDAO> currAprvDAOList = elApplAprvStatusRepository.findByApplHdrId(applHdrId);
		elApplAprvStatusRepository.deleteAll(currAprvDAOList);

		List<String> aprvTypeList = new ArrayList<String>();
		// Add non BCO approver to new JSONArray
		JSONArray newJsonArr = new JSONArray();
		for (int i=0; i< inputJson.length(); i++) {
			JSONObject jsonObj = inputJson.getJSONObject(i);
			if (!(AprvTypeConstants.BCO_APPL.equals(jsonObj.optString("aprv_type_cde")) || AprvTypeConstants.FO_SR.equals(jsonObj.optString("aprv_type_cde")) || AprvTypeConstants.FO_SFM.equals(jsonObj.optString("aprv_type_cde")) || AprvTypeConstants.PROVOST.equals(jsonObj.optString("aprv_type_cde")))) {
				newJsonArr.put(jsonObj);
				if(!aprvTypeList.contains("aprv_type_cde")) {
					aprvTypeList.add(jsonObj.optString("aprv_type_cde"));
				}
			}
		}
		
		// #20240131 add submit checking to allow save non-bco approver when save draft
		// Add saved bco approver in EL_APPL_PYMT_SCHEDULE to newJsonArr
		if (isSubmit) {
			List<Map<String, String>> bcoAprvMapList = elApplPymtScheduleRepository.findBcoAprvNotInElApplAprvStatus(applHdrId);
			
			int arpvSeq = newJsonArr.length() + 1 ;
			if (!bcoAprvMapList.isEmpty()) {
				Set<String> bcoAprvIdSet = new HashSet<>();
				for (Map<String, String> bcoAprvMap : bcoAprvMapList) {
					String bcoAprvId = bcoAprvMap.get("bco_aprv_id");
					
					if (!bcoAprvIdSet.contains(bcoAprvId)) {
						bcoAprvIdSet.add(bcoAprvId);
						JSONObject bcoAprvObj = new JSONObject();
						newJsonArr.put(bcoAprvObj);
						
						bcoAprvObj
						.put("arpv_seq", arpvSeq)
						.put("aprv_type_cde", AprvTypeConstants.BCO_APPL)
						.put("aprv_user_id", bcoAprvId)
						.put("aprv_user_name", bcoAprvMap.get("bco_aprv_name"))
						;
					}
				}
			}
		}
		
		int seq_no = 0;
		for (int i=1; i<= newJsonArr.length(); i++) {
			JSONObject jsonObj = newJsonArr.getJSONObject(i-1);
			
			// no update as all record should be deleted
			
			ElApplAprvStatusDAO dao = new ElApplAprvStatusDAO();
			dao.setCreatUser(remoteUser);
			
			dao.setApplHdrId(applHdrId);
			dao.setArpvSeq(seq_no++);
			dao.setAprvTypeCde(jsonObj.getString("aprv_type_cde"));
			dao.setAprvUserId(jsonObj.getString("aprv_user_id"));
			/*
			JSONArray applList = generalApiService.getStaffListByUserId(jsonObj.getString("aprv_user_id"));
			
			if (applList.length() == 0) {
				throw new InvalidParameterException("Approver is not found");
			}
			
			String AprvName = applList.getJSONObject(0).getString("display_nam");
			
			dao.setAprvUserName(AprvName);		
			*/
			dao.setAprvUserName(jsonObj.getString("aprv_user_name"));
			dao.setAprvRemark(GeneralUtil.initBlankString(jsonObj.optString("aprv_remark")));
			
			dao.setChngUser(remoteUser);
			dao.setOpPageNam(opPageName);
			
			if(!isSubmit) {
				dao.setApproved(-2);
			}
			
			daoList.add(dao);
		}
		// #20240131 add submit checking to allow save non-bco approver when save draft
		//
		// Start for exceptional approval to Provost
		//
		// tentative add try catch to avoid application cannot be submitted
		if (isSubmit) {
			try {
				ElApplHdrDAO hdrDAO = elApplHdrRepository.findOne(applHdrId);
				String applicantId = hdrDAO.getApplUserId();
				String applicantEmplid = hdrDAO.getApplUserEmplid();
				String applicationStartSem = hdrDAO.getApplStartTerm();
				String applicationEndSem = hdrDAO.getApplEndTerm();
				
				JSONObject provostAprv = generalApiService.getProvostAprv();
				String PROVOST_ID = provostAprv.getString("aprv_id");
				String PROVOST_NAME = provostAprv.getString("aprv_name");
				
				// find application extr load type
				// Assume only have 1 extra load type
				List<ElApplElTypeDAO> elTypeDaoList = elApplElTypeRepository.findByApplHdrId(applHdrId);
				boolean isSBM = false;
				List<ElInpostStaffImpVDAO> sbmList = elInpostStaffImpVRepository.findSBMByEmplid(applicantEmplid);
				log.debug("SBM List Size: {}", sbmList.size());
				if(sbmList.size() > 0) {
					isSBM = true;
				}
				if(elTypeDaoList.size() > 0) {
					ElApplElTypeDAO elTypeDao = elTypeDaoList.get(0);
					
					ElTypeTabDAO elTypeDAO = elTypeTabRepository.getOne(elTypeDao.getElTypeId());
					
					if(elTypeDAO == null)
						throw new RecordNotExistException("Extra Load Type");
					
					String elTypeName = elTypeDAO.getElTypeNam();
					
					if(!isSBM) { // ETAP-91, skip PROVOST if is SBM
						// all check for the same acad year
						// Case 1: For dept head with 1 course approved previously Now request for 2nd course teaching
						// Case 2: For dept head with 2 course approved previously Now submit 3rd request for student supervision
						// Case 3: For Dean / AVP (the applicant) with CPEP Director fees approved previously Now submit 1 course for teaching
						if(elTypeName.toUpperCase().equals("course teaching".toUpperCase())) {
							boolean toProvost = false;
							
							// TODO: check by hod view later?
							// check if applicant is dept head
							List<ElBcoAprvVDAO> deptHeadAprvRightList = elBcoAprvVRepository.findDeptHeadApprvRightByUserId(applicantId);
							
							
							if(deptHeadAprvRightList.size() != 0) {
								// do checking on approved application
								
								// Assume application semester is always exist
								// find approved application with course in the same acad yr of the applicant
								List<ElApplHdrDAO> applicantApplHdrList = elApplHdrRepository.findApprovedApplWithCrseByUserIdAndSem(applicantId, applicationStartSem, applicationEndSem);
								
								// Case 1: For dept head with 1 course approved previously Now request for 2nd course teaching
								if(applicantApplHdrList.size() >= 1) {
									// route to Provost for approval
									ElApplAprvStatusDAO dao = new ElApplAprvStatusDAO();
									dao.setCreatUser(remoteUser);
									
									dao.setApplHdrId(applHdrId);
									dao.setArpvSeq(aprvTypeList.size() + 2);
									dao.setAprvTypeCde(AprvTypeConstants.PROVOST);
									dao.setAprvUserId(PROVOST_ID);
									dao.setAprvUserName(PROVOST_NAME);		
									dao.setAprvRemark(" ");
									
									dao.setChngUser(remoteUser);
									dao.setOpPageNam(opPageName);
									
									daoList.add(dao);
									
									toProvost = true;
								}
							}
							
							if(toProvost == false) {
								// check user is dean / avp
								
								List<ElInpostStaffImpVDAO> staffJobList = elInpostStaffImpVRepository.findDeanAndAvpRoleByEmplid(applicantEmplid);
								if(staffJobList.size() != 0) {
									List<ElApplHdrDAO> applicantApplHdrList = elApplHdrRepository.findApprovedApplWithCrseByUserIdAndSem(applicantId, applicationStartSem, applicationEndSem);
									// Case 3: For Dean / AVP (the applicant) with CPEP Director fees approved previously Now submit 1 course for teaching
									
									if(applicantApplHdrList.size() >= 1) {
										// route to Provost for approval
										ElApplAprvStatusDAO dao = new ElApplAprvStatusDAO();
										dao.setCreatUser(remoteUser);
										
										dao.setApplHdrId(applHdrId);
										dao.setArpvSeq(aprvTypeList.size() + 2);
										dao.setAprvTypeCde(AprvTypeConstants.PROVOST);
										dao.setAprvUserId(PROVOST_ID);
										dao.setAprvUserName(PROVOST_NAME);		
										dao.setAprvRemark(" ");
										
										dao.setChngUser(remoteUser);
										dao.setOpPageNam(opPageName);
										
										daoList.add(dao);
										
										toProvost = true;
									}
								}
							}
							
							
						} else if(elTypeName.toUpperCase().equals("project supervision".toUpperCase())) {
							// TODO: check by hod view later?
							// check if applicant is dept head
							List<ElBcoAprvVDAO> deptHeadAprvRightList = elBcoAprvVRepository.findDeptHeadApprvRightByUserId(applicantId);
							
							
							if(deptHeadAprvRightList.size() != 0) {
								// do checking on approved application
								
								// Assume application semester is always exist
								// find approved application with course in the same acad yr of the applicant
								List<ElApplHdrDAO> applicantApplHdrList = elApplHdrRepository.findApprovedApplWithCrseByUserIdAndSem(applicantId, applicationStartSem, applicationEndSem);
								
								// Case 2: For dept head with 2 course approved previously Now submit 3rd request for student supervision
								if(applicantApplHdrList.size() >= 2) {
									// route to Provost for approval
									ElApplAprvStatusDAO dao = new ElApplAprvStatusDAO();
									dao.setCreatUser(remoteUser);
									
									dao.setApplHdrId(applHdrId);
									dao.setArpvSeq(aprvTypeList.size() + 2);
									dao.setAprvTypeCde(AprvTypeConstants.PROVOST);
									dao.setAprvUserId(PROVOST_ID);
									dao.setAprvUserName(PROVOST_NAME);	
									dao.setAprvRemark(" ");
									
									dao.setChngUser(remoteUser);
									dao.setOpPageNam(opPageName);
									
									daoList.add(dao);
								}
							}
						}
					}
				}
			} catch (Exception e) {
				log.error("error in exceptional approval: " + e.toString());
			}
		}
		//
		// End for exceptional approval to Provost
		//
		

		/*
		ElApplAprvStatusDAO dao = new ElApplAprvStatusDAO();
		dao.setCreatUser(remoteUser);
		
		dao.setApplHdrId(applHdrId);
		dao.setArpvSeq(aprvTypeList.size() + 2);
		dao.setAprvTypeCde(AprvTypeConstants.PROVOST);
		dao.setAprvUserId("isod11");
		dao.setAprvUserName("Jimmy Fung");		
		dao.setAprvRemark(" ");
		
		dao.setChngUser(remoteUser);
		dao.setOpPageNam(opPageName);
		
		daoList.add(dao);
		*/
		elApplAprvStatusRepository.saveAll(daoList);
	}

	@Override
	@Transactional(value = "transactionManagerJselMain", rollbackFor = Exception.class)
	public JSONObject createElApplAct(String applHdrId, JSONObject inputJson, String opPageName) throws Exception {
		JSONObject outputJson = new JSONObject();
		String remoteUser = SecurityUtils.getCurrentLogin();
		
		ElApplActDAO dao = new ElApplActDAO();
		
		dao.setApplHdrId(applHdrId);
		dao.setAction(inputJson.getString("action").toLowerCase());
		// 20231229 #77 show remark in act history
		dao.setDescr(GeneralUtil.initBlankString(inputJson.optString("aprv_remark")));
		dao.setActionBy(remoteUser);
		dao.setActionDttm(GeneralUtil.getCurrentTimestamp());
		
		dao.setCreatUser(remoteUser);
		dao.setChngUser(remoteUser);
		dao.setOpPageNam(opPageName);
		
		elApplActRepository.save(dao);
		
		return outputJson;
	}
	

    private List<Timestamp> calculatePaymentDates(Timestamp startDate, Timestamp endDate) {
        List<Timestamp> paymentDates = new ArrayList<>();

        LocalDateTime paymentDate = startDate.toLocalDateTime();
        while (!paymentDate.isAfter(endDate.toLocalDateTime())) {
            Timestamp timestamp = Timestamp.valueOf(paymentDate);
            paymentDates.add(timestamp);

            paymentDate = paymentDate.plusMonths(1);
        }

        return paymentDates;
    }

	@Override
	public JSONObject getElApplicationDetails(String applHdrId) throws Exception {
		// TODO Auto-generated method stub
		JSONObject outputJson = new JSONObject();
		ElApplHdrDAO elApplHdrDAO = elApplHdrRepository.findOne(applHdrId);
		
		if (elApplHdrDAO == null) {
			throw new RecordNotExistException("Extra Load Application");
		}
		
		// Auth Checking
		isElApplAuthUser(applHdrId);
		
		ElDeptChrtVDAO deptDAO = elDeptChrtVRepository.findOne(elApplHdrDAO.getApplUserDeptId());
		String dept = elApplHdrDAO.getApplUserDeptId();
		if(deptDAO != null) {
			dept = deptDAO.getDeptShortDesc();
		}

//		ElInpostStaffImpVDAO staffDAO = elInpostStaffImpVRepository.findPrimaryByEmplid(elApplHdrDAO.getApplUserEmplid());
		Map<String, Object> applicantInfo = elInpostStaffImpVRepository.searchApplicantByEmplid(elApplHdrDAO.getApplUserEmplid());
		
		String role = " ";
		if(applicantInfo.size() != 0) {
			
			String jobCatgCde = (String) applicantInfo.get("job_catg_cde");
			
			switch(jobCatgCde){
				case "A010" :
				case "A020" :
				case "A030" :
				case "A040" :
					role = "Senior";
					break;
				case "R020" :
				case "I020" :
					role = "Support";
					break;
				default :
					role = "Support";
			}
		}
		// application header
		List<ElApplStatTabDAO> statusList = elApplStatTabRepository.findAll();
		
		outputJson
		.put("uuid", elApplHdrDAO.getId())
		.put("el_appl_hdr_id", elApplHdrDAO.getId())
		.put("appl_nbr", elApplHdrDAO.getApplNbr())
		.put("appl_user_id", elApplHdrDAO.getApplUserId())
		.put("appl_user_name", elApplHdrDAO.getApplUserName())
		.put("appl_user_dept", dept)
		.put("appl_user_deptid", elApplHdrDAO.getApplUserDeptId())
		.put("appl_requester_id", elApplHdrDAO.getApplRequesterId())
		.put("appl_requester_name", elApplHdrDAO.getApplRequesterName())
		.put("appl_requester_deptid", elApplHdrDAO.getApplRequesterDeptid())
		.put("category_cde", elApplHdrDAO.getCategoryCde())
		.put("appl_dttm", elApplHdrDAO.getApplDttm())
		.put("appl_stat_cde", elApplHdrDAO.getApplStatCde())
		.put("appl_stat_desc", statusList.stream().filter(x->x.getApplStatCde().equals(elApplHdrDAO.getApplStatCde())).findFirst().isPresent() ? 
				statusList.stream().filter(x->x.getApplStatCde().equals(elApplHdrDAO.getApplStatCde())).findFirst().get().getApplStatDescr() : " ")
		.put("ow2_attached_ind", elApplHdrDAO.getOw2AttachedInd())
		.put("pymt_aprv_required", elApplHdrDAO.getPymtAprvRequired())
		.put("obsolete", elApplHdrDAO.getObsolete())
		.put("appl_start_term", elApplHdrDAO.getApplStartTerm())
		.put("appl_end_term", elApplHdrDAO.getApplEndTerm())
		.put("appl_start_dt", elApplHdrDAO.getApplStartDt())
		.put("appl_end_dt", elApplHdrDAO.getApplEndDt())
		.put("mod_ctrl_txt", elApplHdrDAO.getModCtrlTxt())
		.put("br_no", elApplHdrDAO.getBrNo())
		.put("appl_workflow_type", getApplWorkflowType(elApplHdrDAO.getApplUserJobCatg()))
		.put("version_no", elApplHdrDAO.getVersionNo())
//		.put("tos", staffDAO.getTos())
		.put("role", role)
		;
		
		// other details
		JSONObject prgmJsonObj = getElApplPrgm(applHdrId);		
		outputJson.put("el_appl_prgm", prgmJsonObj);
		
		JSONArray courseJsonArr = getElApplCourse(applHdrId);		
		outputJson.put("el_appl_course", courseJsonArr);
		
		JSONArray elApplJsonArr = getElApplElType(applHdrId);
		outputJson.put("el_appl_el_type", elApplJsonArr);
		
		JSONObject elApplPymtArr = getElApplPymtMethod(applHdrId);
		outputJson.put("el_appl_pymt_method", elApplPymtArr);
		
//		JSONArray elApplBudgetArr = getElApplBudget(applHdrId);
//		outputJson.put("el_appl_budget", elApplBudgetArr);
		
		JSONArray elApplMPFArr = getElApplPymtMPF(applHdrId);
		outputJson.put("el_appl_mpf", elApplMPFArr);
		
		JSONArray elApplAprvArr = getElApplAprv(applHdrId);
		outputJson.put("el_appl_arpv_status", elApplAprvArr);
		
		JSONArray elApplAttachmentArr = getElApplAttachemt(applHdrId, ElApplAttachmentsDAO.APPLICATION);
		outputJson.put("el_appl_attachments", elApplAttachmentArr);
		
		JSONArray elApplActArr = getElApplAct(applHdrId);
		outputJson.put("el_appl_act", elApplActArr);

		JSONObject previousMethodObj = getPreviousElApplPymtMethod(applHdrId);
		outputJson.put("previous_method", previousMethodObj);
		
		// get applicant declaration 
		JSONObject userDeclJson = new JSONObject();
		userDeclJson.put("user_decl_1", elApplHdrDAO.getUserDecl_1());
		userDeclJson.put("user_decl_2", elApplHdrDAO.getUserDecl_2());
		userDeclJson.put("user_decl_2_from_dt", elApplHdrDAO.getUserDecl2FromDt());
		userDeclJson.put("user_decl_2_to_dt", elApplHdrDAO.getUserDecl2ToDt());
		
		JSONArray elUserDecSuprtAttmArr = getElApplAttachemt(applHdrId, ElApplAttachmentsDAO.SUPPORTING);
		userDeclJson.put("user_dec_suprt_attachments", elUserDecSuprtAttmArr);
		
		outputJson.put("appl_user_decl", userDeclJson);
		
		// get dept head declaration 
		JSONObject hodDeclJson = new JSONObject();
		hodDeclJson.put("hod_decl_1_1", elApplHdrDAO.getHodDecl_1_1());
		hodDeclJson.put("hod_decl_1_2", elApplHdrDAO.getHodDecl_1_2());
		
		outputJson.put("appl_hod_decl", hodDeclJson);
		
		return outputJson;
	}

	private JSONArray getElApplAct(String applHdrId) throws Exception {
		JSONArray jsonArr = new JSONArray();
		List<ElApplActDAO> daoList = elApplActRepository.findByApplHdrId(applHdrId);
		
		for (ElApplActDAO dao : daoList) {
			JSONObject jsonObj = new JSONObject();
			jsonArr.put(jsonObj);
			
			jsonObj
			.put("el_appl_attachments_id", dao.getId())
			.put("uuid", dao.getId())
			.put("action", dao.getAction())
			.put("descr", dao.getDescr())
			.put("action_by", dao.getActionBy())
			.put("action_dttm", dao.getActionDttm())
			.put("mod_ctrl_txt", dao.getModCtrlTxt())
			.put("role", dao.getRoleType())
			;
		}
		String pendingUser = "";
		String pendingRole = "";
		ElApplHdrDAO hdr = elApplHdrRepository.findOne(applHdrId);
		
		if(hdr != null && !hdr.getApplStatCde().equalsIgnoreCase(ApplStatusConstants.DRAFT)) {
			
			switch (hdr.getApplStatCde()) {
			case ApplStatusConstants.PENDING_APPL_ACCT:
			case ApplStatusConstants.RELEASE_OFFER:
			case ApplStatusConstants.PENDING_DECL:
				pendingUser = hdr.getApplUserId();
				pendingRole = "Applicant";
				break;
			case ApplStatusConstants.REJECTED:
			case ApplStatusConstants.ISSUE_OFFER:
			case ApplStatusConstants.OFFER_REJECTED:
			case ApplStatusConstants.READY_SUBM:
			case ApplStatusConstants.PYMT_REJECTED:
				pendingUser = hdr.getApplRequesterId();
				pendingRole = "Requester";
				break;
			case ApplStatusConstants.PENDING:
			case ApplStatusConstants.PENDING_RECTIFY:
			case ApplStatusConstants.PENDING_PYMT_APPR:
			case ApplStatusConstants.PENDING_FOPNB:
				//approver
				List<ElApplAprvStatusDAO> aprvDAOList = elApplAprvStatusRepository.findAllPendingAprv(applHdrId);
				ElApplAprvStatusDAO nextAprvDAO = elApplAprvStatusRepository.findAllPendingAprv(applHdrId).get(0);
				if(nextAprvDAO != null) {
				String nextType = nextAprvDAO.getAprvTypeCde();
					if(!nextType.equalsIgnoreCase(AprvTypeConstants.FO_SR) && !nextType.equalsIgnoreCase(AprvTypeConstants.FO_SFM) && !nextType.equalsIgnoreCase(AprvTypeConstants.FO_PNB) && !nextType.equalsIgnoreCase(AprvTypeConstants.FO_RECTIFY)) {
						pendingUser = nextAprvDAO.getAprvUserId();
						pendingRole = nextAprvDAO.getAprvTypeCde();
					}else {
						boolean pendingFoSrFound = false;
						boolean pendingFoSfmFound = false;
						boolean pendingFoPnbFound = false;
						boolean pendingFoRecFound = false;
		
						for (ElApplAprvStatusDAO dao : aprvDAOList) {
							// 20240207 Change for FO Team approval
							String aprvUserId = dao.getAprvUserId();
							
							if (AprvTypeConstants.FO_SR.equals(dao.getAprvTypeCde()) && dao.getApproved() == -1 ) {
								if (pendingFoSrFound) {
									continue;
								} else {
									pendingFoSrFound = true;
									aprvUserId = String.join(", ", aprvDAOList.stream().filter(e -> e.getAprvTypeCde().equals(AprvTypeConstants.FO_SR)).distinct().map(e -> e.getAprvUserId()).collect(Collectors.toList()));
								}
							} else if (AprvTypeConstants.FO_SFM.equals(dao.getAprvTypeCde()) && dao.getApproved() == -1 ) {
								if (pendingFoSrFound || pendingFoSfmFound) {
									break;
								}
								if (pendingFoSfmFound) {
									continue;
								} else {
									pendingFoSfmFound = true;
									aprvUserId = String.join(", ", aprvDAOList.stream().filter(e -> e.getAprvTypeCde().equals(AprvTypeConstants.FO_SFM)).distinct().map(e -> e.getAprvUserId()).collect(Collectors.toList()));
								}
							} else if (AprvTypeConstants.FO_PNB.equals(dao.getAprvTypeCde()) && dao.getApproved() == -1 ) {
								if (pendingFoSrFound || pendingFoSfmFound) {
									break;
								}
								if (pendingFoPnbFound) {
									continue;
								} else {
									pendingFoPnbFound = true;
									aprvUserId = String.join(", ", aprvDAOList.stream().filter(e -> e.getAprvTypeCde().equals(AprvTypeConstants.FO_PNB)).distinct().map(e -> e.getAprvUserId()).collect(Collectors.toList()));
								}
							} else if (AprvTypeConstants.FO_RECTIFY.equals(dao.getAprvTypeCde()) && dao.getApproved() == -1 ) {
								if (pendingFoRecFound) {
									continue;
								} else {
									pendingFoRecFound = true;
									aprvUserId = String.join(", ", aprvDAOList.stream().filter(e -> e.getAprvTypeCde().equals(AprvTypeConstants.FO_RECTIFY)).distinct().map(e -> e.getAprvUserId()).collect(Collectors.toList()));
								}
							}
							pendingUser = aprvUserId;
							pendingRole = nextAprvDAO.getAprvTypeCde();
						}
					}
				}
				break;
			case ApplStatusConstants.APPROVED:
			case ApplStatusConstants.PYMT_SUBM:
				pendingUser = "System";
				pendingRole = "System";
				break;
			case ApplStatusConstants.REMOVED:
			case ApplStatusConstants.COMPLETED:
				pendingUser = "N/A";
				break;
			
			}
			if(!pendingUser.equals("N/A")) {
				JSONObject jsonObj = new JSONObject();
				jsonArr.put(jsonObj);
				
				jsonObj
				.put("el_appl_attachments_id", "pending")
				.put("uuid", "pending")
				.put("action", "Pending by")
				.put("descr", " ")
				.put("action_by", pendingUser)
				.put("role", pendingRole)
				;
			}
		}
		
		return jsonArr;
	}

	private JSONArray getElApplAttachemt(String applHdrId, String fileCatg) throws Exception {
		JSONArray jsonArr = new JSONArray();
		List<Map<String, String>> resultList = elApplAttachmentsRepository.findDetailsByApplHdrIdAndFileCategory(applHdrId, fileCatg);
		
		for (Map<String, String> result : resultList) {
			JSONObject jsonObj = new JSONObject();
			jsonArr.put(jsonObj);
			
			jsonObj
			.put("el_appl_attachments_id", result.get("el_appl_attachments_id"))
			.put("uuid", result.get("el_appl_attachments_id"))
			.put("file_id", result.get("file_id"))
			.put("file_category", result.get("file_category"))
			.put("file_name", result.get("file_name"))
			.put("mod_ctrl_txt", result.get("mod_ctrl_txt"))
			;
		}
		
		return jsonArr;
	}

	private JSONObject getElApplPrgm(String applHdrId) throws Exception {
		ElApplPrgmDAO prgmDAO = elApplPrgmRepository.findByApplHdrId(applHdrId);
		JSONObject jsonObj = new JSONObject();
		
		if (prgmDAO != null) {
			
			ElAcadPlanVDAO elAcadPlanVDao = elAcadPlanVRepository.findByAcadPlanAndAcadOrgAndAcadGroup(prgmDAO.getPrgmCde(), prgmDAO.getDept(), prgmDAO.getSchCde());
			String dept_descr_short = elAcadPlanVDao != null ? elAcadPlanVDao.getAcadOrgDescrShort() : "";
			
			jsonObj
			.put("el_appl_prgm_id", prgmDAO.getId())
			.put("uuid", prgmDAO.getId())
//			.put("prgm_term", prgmDAO.getPrgmTerm())
			.put("prgm_cde", prgmDAO.getPrgmCde())
			.put("sch_cde", prgmDAO.getSchCde())
			.put("dept", prgmDAO.getDept())
			.put("dept_descr_short", dept_descr_short)
//			.put("prgm_start_dt", GeneralUtil.NULLTIMESTAMP.equals(prgmDAO.getPrgmStartDttm()) ? null : prgmDAO.getPrgmStartDttm())
//			.put("prgm_end_dt", GeneralUtil.NULLTIMESTAMP.equals(prgmDAO.getPrgmEndDttm()) ? null : prgmDAO.getPrgmEndDttm())
			.put("mod_ctrl_txt", prgmDAO.getModCtrlTxt())
			;
		}
		
		return jsonObj;			
	}
	
	private JSONArray getElApplCourse(String applHdrId) throws JSONException {
		List<ElApplCourseDAO> courseDAOList = elApplCourseRepository.findByApplHdrId(applHdrId);
		List<ElAcadTermVDAO> acadTermList = elAcadTermVRepository.findAll();
		JSONArray jsonArr = new JSONArray();
		
		for (ElApplCourseDAO courseDAO : courseDAOList) {
			JSONObject jsonObj = new JSONObject();
			String crseTermDesc = "";
			for (ElAcadTermVDAO acadTerm: acadTermList) {
				if (courseDAO.getCrseTerm().equals(acadTerm.getStrm())) {
					crseTermDesc = acadTerm.getYrTermDesc();
					break;
				}
			}
			
			jsonObj
			.put("el_appl_course_id", courseDAO.getId())
			.put("uuid", courseDAO.getId())
			.put("crse_term", courseDAO.getCrseTerm())
			.put("crse_id", courseDAO.getCrseId())
			.put("crse_cde", courseDAO.getCrseTerm() + "_" + courseDAO.getCrseCde())
			.put("crse_cde_descr", courseDAO.getCrseCde() + " (" + crseTermDesc + ")")
			.put("crse_descr", courseDAO.getCrseDescr())
			.put("section", courseDAO.getSection())
			.put("credit", courseDAO.getCredit())
			.put("crse_co_taught_hr", courseDAO.getCrseCoTaughtHr())
			.put("crse_total_hr", courseDAO.getCrseTotalHr())
			.put("crse_remark", courseDAO.getCrseRemark())
			.put("mod_ctrl_txt", courseDAO.getModCtrlTxt())			
			;
			
			List<ElApplColistCourseDAO> colistCourseDAOList = elApplColistCourseRepository.findByApplCrseId(courseDAO.getId());
			
			JSONArray colistCourseJsonArr = new JSONArray();
			for (ElApplColistCourseDAO colistCourseDAO : colistCourseDAOList) {
				JSONObject colistCourseJsonObj = new JSONObject();
				String colistTermDesc = "";
				for (ElAcadTermVDAO acadTerm: acadTermList) {
					if (courseDAO.getCrseTerm().equals(acadTerm.getStrm())) {
						colistTermDesc = acadTerm.getYrTermDesc();
						break;
					}
				}
				
				colistCourseJsonObj
				.put("el_appl_colist_course_id", colistCourseDAO.getId())
				.put("uuid", colistCourseDAO.getId())
				.put("appl_crse_id", colistCourseDAO.getApplCrseId())
				.put("crse_term", colistCourseDAO.getColistCrseTerm())
				.put("crse_id", colistCourseDAO.getColistCrseId())
				.put("crse_cde", colistCourseDAO.getColistCrseTerm() + "_" + colistCourseDAO.getColistCrseCde())
				.put("crse_cde_descr", colistCourseDAO.getColistCrseCde() + " (" + colistTermDesc + ")")
				.put("section", colistCourseDAO.getSection())
				.put("credit", colistCourseDAO.getCredit())
				.put("crse_co_taught_hr", colistCourseDAO.getCrseCoTaughtHr())
				.put("crse_total_hr", colistCourseDAO.getCrseTotalHr())
				.put("mod_ctrl_txt", colistCourseDAO.getModCtrlTxt())
				;
				
				colistCourseJsonArr.put(colistCourseJsonObj);
			}
			
			jsonObj.put("colist_course", colistCourseJsonArr);
			
			jsonArr.put(jsonObj);
		}
		
		return jsonArr;
	}
	
	private JSONArray getElApplElType(String applHdrId) throws Exception {
		List<ElApplElTypeDAO> daoList = elApplElTypeRepository.findByApplHdrId(applHdrId);
		JSONArray jsonArr = new JSONArray();
		
		for (ElApplElTypeDAO dao : daoList) {
			JSONObject jsonObj = new JSONObject();
			
			jsonObj
			.put("el_appl_el_type_id", dao.getId())
			.put("uuid", dao.getId())
			.put("el_type_id", dao.getElTypeId())
			.put("el_type_descr", dao.getElTypeDescr())
			.put("stdt_enrolled", dao.getStdtEnrolled())
			.put("pymt_amt", dao.getPymtAmt())
			.put("pmt_currency", dao.getPmtCurrency())
			.put("mod_ctrl_txt", dao.getModCtrlTxt())			
			;
			
			jsonArr.put(jsonObj);
		}
		
		return jsonArr;
	}
	
	private JSONObject getElApplPymtMethod(String applHdrId) throws Exception {
		List<JSONObject> outputList = new ArrayList<>();
		ElApplHdrDAO elApplHdrDAO = elApplHdrRepository.findOne(applHdrId);
		
		List<Map<String, Object>> resultMapList = elApplPymtMethodRepository.findDetailsByApplHdrId(applHdrId);
		
		int prevSchNo = 0;
		
		for (Map<String, Object> resultMap : resultMapList) {
			String elApplPymtMethodId = (String) resultMap.get("el_appl_pymt_method_id");

			JSONObject outputMap = outputList.stream()
					.filter(map -> {
						try {
							return map.get("el_appl_pymt_method_id").equals(elApplPymtMethodId);
						} catch (JSONException e) {
						}
						return false;
					}).findFirst().orElse(null);
			
			if (outputMap == null) {
				outputMap = new JSONObject();
				
				outputMap.put("el_appl_pymt_method_id", resultMap.get("el_appl_pymt_method_id"));
				outputMap.put("uuid", resultMap.get("el_appl_pymt_method_id"));
				outputMap.put("pymt_type_cde",resultMap.get("pymt_type_cde"));
				outputMap.put("pymt_amt",resultMap.get("pymt_amt"));
				outputMap.put("mod_ctrl_txt", resultMap.get("mod_ctrl_txt"));
				outputMap.put("pymt_schedule", new JSONArray());
				outputMap.put("payroll_descr", elApplHdrDAO.getPayrollDescr());
				
				outputList.add(outputMap);
			}

			// Skip if el_appl_pymt_schedule_id is empty
			if (((String) resultMap.get("el_appl_pymt_schedule_id")).isBlank()) {
				continue;
			}
			// For recurrent, return 1 row for display only
			/*
			boolean isRecurr = PymtTypeConstants.RECURR.equals(resultMap.get("pymt_type_cde"));
			if (isRecurr && ((JSONArray) outputMap.get("pymt_schedule")).length() > 0) {
				continue;
			}
			*/
			
			JSONObject pymtScheduleJsonObj = new JSONObject();
			int curtSchlNo = ((BigDecimal) resultMap.get("pymt_sched_no")).intValue();
			
			if(curtSchlNo == prevSchNo) {
				pymtScheduleJsonObj = ((JSONArray) outputMap.get("pymt_schedule")).getJSONObject(curtSchlNo-1);
			} else {
				pymtScheduleJsonObj.put("pymt_sched_no", curtSchlNo);
				pymtScheduleJsonObj.put("pymt_start_dt", resultMap.get("pymt_start_dt").equals(GeneralUtil.NULLTIMESTAMP) ? null : resultMap.get("pymt_start_dt"));
				pymtScheduleJsonObj.put("pymt_end_dt", resultMap.get("pymt_end_dt").equals(GeneralUtil.NULLTIMESTAMP) ? null : resultMap.get("pymt_end_dt"));
				pymtScheduleJsonObj.put("pymt_status_cde", resultMap.get("pymt_status_cde"));
				pymtScheduleJsonObj.put("details", new JSONArray());
				
				
				prevSchNo = curtSchlNo;
				
				((JSONArray) outputMap.get("pymt_schedule")).put(pymtScheduleJsonObj);	
			}
			
			JSONObject pymtScheduleLineJsonObj = new JSONObject();
			
			int curtSchlLineNo = ((BigDecimal) resultMap.get("pymt_sched_line")).intValue();
			
			pymtScheduleLineJsonObj.put("pymt_sched_line", curtSchlLineNo);
			pymtScheduleLineJsonObj.put("proj_id", resultMap.get("proj_id").toString().trim());
			pymtScheduleLineJsonObj.put("proj_nbr", resultMap.get("proj_nbr").toString().trim());
			pymtScheduleLineJsonObj.put("dept_id", resultMap.get("dept_id").toString().trim());
			pymtScheduleLineJsonObj.put("fund_cde", resultMap.get("fund_cde").toString().trim());
			pymtScheduleLineJsonObj.put("class", resultMap.get("class_cde").toString().trim());
			pymtScheduleLineJsonObj.put("acct_cde", resultMap.get("acct_cde").toString().trim());
			pymtScheduleLineJsonObj.put("analysis_cde", resultMap.get("analysis_cde").toString().trim());
			
			pymtScheduleLineJsonObj.put("bco_aprv_id", resultMap.get("bco_aprv_id").toString().trim());
			pymtScheduleLineJsonObj.put("bco_aprv_name", resultMap.get("bco_aprv_name").toString().trim());
			
			pymtScheduleLineJsonObj.put("pymt_amt", resultMap.get("schedule_pymt_amt"));
			
			((JSONArray) pymtScheduleJsonObj.get("details")).put(pymtScheduleLineJsonObj);
		}
		
		JSONArray jsonArr = new JSONArray(outputList);
		
		if(jsonArr.length() == 0) {
			return new JSONObject();
		}
		else 
			return jsonArr.getJSONObject(0);
	}
	
	private JSONObject getPreviousElApplPymtMethod(String applHdrId) throws Exception {
		List<JSONObject> outputList = new ArrayList<>();
		
		List<Map<String, Object>> resultMapList = elApplPymtMethodHistRepository.findDetailsByApplHdrId(applHdrId);
		
		int prevSchNo = 0;
		
		for (Map<String, Object> resultMap : resultMapList) {
			String elApplPymtMethodId = (String) resultMap.get("el_appl_pymt_method_id");

			// 20231228 Comment added: while for loop, assume multiple method, filter only current method id record from outputList
			JSONObject outputMap = outputList.stream()
					.filter(map -> {
						try {
							return map.get("el_appl_pymt_method_id").equals(elApplPymtMethodId);
						} catch (JSONException e) {
						}
						return false;
					}).findFirst().orElse(null);
			
			if (outputMap == null) {
				outputMap = new JSONObject();
				
				outputMap.put("el_appl_pymt_method_id", resultMap.get("el_appl_pymt_method_id"));
				outputMap.put("uuid", resultMap.get("el_appl_pymt_method_id"));
				outputMap.put("pymt_type_cde",resultMap.get("pymt_type_cde"));
				outputMap.put("pymt_amt",resultMap.get("pymt_amt"));
				outputMap.put("mod_ctrl_txt", resultMap.get("mod_ctrl_txt"));
				outputMap.put("pymt_schedule", new JSONArray());
				
				outputList.add(outputMap);
			}

			// Skip if el_appl_pymt_schedule_id is empty
			if (((String) resultMap.get("el_appl_pymt_schedule_id")).isBlank()) {
				continue;
			}
			// For recurrent, return 1 row for display only
			/*
			boolean isRecurr = PymtTypeConstants.RECURR.equals(resultMap.get("pymt_type_cde"));
			if (isRecurr && ((JSONArray) outputMap.get("pymt_schedule")).length() > 0) {
				continue;
			}
			*/
			
			JSONObject pymtScheduleJsonObj = new JSONObject();
			int curtSchlNo = ((BigDecimal) resultMap.get("pymt_sched_no")).intValue();
			
			if(curtSchlNo == prevSchNo) {
				pymtScheduleJsonObj = ((JSONArray) outputMap.get("pymt_schedule")).getJSONObject(curtSchlNo-1);
			} else {
				pymtScheduleJsonObj.put("pymt_sched_no", curtSchlNo);
				pymtScheduleJsonObj.put("pymt_start_dt", resultMap.get("pymt_start_dt").equals(GeneralUtil.NULLTIMESTAMP) ? null : resultMap.get("pymt_start_dt"));
				pymtScheduleJsonObj.put("pymt_end_dt", resultMap.get("pymt_end_dt").equals(GeneralUtil.NULLTIMESTAMP) ? null : resultMap.get("pymt_end_dt"));
				pymtScheduleJsonObj.put("pymt_status_cde", resultMap.get("pymt_status_cde"));
				pymtScheduleJsonObj.put("details", new JSONArray());
				
				prevSchNo = curtSchlNo;
				
				((JSONArray) outputMap.get("pymt_schedule")).put(pymtScheduleJsonObj);	
			}
			
			JSONObject pymtScheduleLineJsonObj = new JSONObject();
			
			int curtSchlLineNo = ((BigDecimal) resultMap.get("pymt_sched_line")).intValue();

			pymtScheduleLineJsonObj.put("el_appl_pymt_schedule_id", resultMap.get("el_appl_pymt_schedule_id"));
			pymtScheduleLineJsonObj.put("pymt_sched_line", curtSchlLineNo);
			pymtScheduleLineJsonObj.put("proj_id", resultMap.get("proj_id").toString().trim());
			pymtScheduleLineJsonObj.put("proj_nbr", resultMap.get("proj_nbr").toString().trim());
			pymtScheduleLineJsonObj.put("dept_id", resultMap.get("dept_id").toString().trim());
			pymtScheduleLineJsonObj.put("fund_cde", resultMap.get("fund_cde").toString().trim());
			pymtScheduleLineJsonObj.put("class", resultMap.get("class_cde").toString().trim());
			pymtScheduleLineJsonObj.put("acct_cde", resultMap.get("acct_cde").toString().trim());
			pymtScheduleLineJsonObj.put("analysis_cde", resultMap.get("analysis_cde").toString().trim());
			
			pymtScheduleLineJsonObj.put("bco_aprv_id", resultMap.get("bco_aprv_id").toString().trim());
			pymtScheduleLineJsonObj.put("bco_aprv_name", resultMap.get("bco_aprv_name").toString().trim());
			
			pymtScheduleLineJsonObj.put("pymt_amt", resultMap.get("schedule_pymt_amt"));
			
			((JSONArray) pymtScheduleJsonObj.get("details")).put(pymtScheduleLineJsonObj);
		}
		
		JSONArray jsonArr = new JSONArray(outputList);
		
		if(jsonArr.length() == 0) {
			return new JSONObject();
		}
		else 
			return jsonArr.getJSONObject(0);
	}
	
	private JSONArray getElApplPymtMPF(String applHdrId) throws Exception {
		JSONArray jsonArr = new JSONArray();
		List<ElApplPymtScheduleDAO> daoList = elApplPymtScheduleRepository.findMPFByApplHdrId(applHdrId);
		
		for (ElApplPymtScheduleDAO dao : daoList) {
			JSONObject jsonObj = new JSONObject();
			jsonArr.put(jsonObj);

			jsonObj
			.put("rowNum", dao.getPymtSchedLine())
			.put("pymt_dt", dao.getPymtStartDt())
			.put("proj_nbr", dao.getProjNbr())
			.put("proj_id", dao.getProjId())
			.put("dept_id", dao.getDeptId())
			.put("fund_cde", dao.getFundCde())
			.put("class", dao.getClassCde())
			.put("acct_cde", dao.getAcctCde())
			.put("analysis_cde", dao.getAnalysisCde())
			.put("pymt_amt", dao.getPymtLineAmt())
			.put("bco_aprv_id", dao.getBcoAprvId())
			.put("bco_aprv_name", dao.getBcoAprvName())
			;
		}
		
		return jsonArr;
	}
	
//	private JSONArray getElApplBudget(String applHdrId) throws Exception {
//		List<JSONObject> outputList = new ArrayList<>();
//		
//		List<Map<String, Object>> resultMapList = elApplBudgetRepository.findDetailsByApplHdrId(applHdrId);
//		
//		for (Map<String, Object> resultMap : resultMapList) {
//			String elTypeId = (String) resultMap.get("el_type_id");
//
//			JSONObject outputMap = outputList.stream()
//					.filter(map -> {
//						try {
//							return map.get("el_type_id").equals(elTypeId);
//						} catch (JSONException e) {
//						}
//						return false;
//					}).findFirst().orElse(null);
//						
//			if (outputMap == null) {
//				outputMap = new JSONObject();
//				outputMap
//				.put("el_type_id", resultMap.get("el_type_id"))
//				.put("uuid", resultMap.get("el_type_id"))
//				.put("el_type_descr", resultMap.get("el_type_descr"))
//				.put("pymt_amt", resultMap.get("pymt_amt"))
//				.put("pmt_currency", resultMap.get("pmt_currency"))
//				.put("details", new JSONArray())
//				;
//				
//				outputList.add(outputMap);
//			}
//			JSONObject budgetdetailObj = new JSONObject();
//			
//			budgetdetailObj
//			.put("el_appl_budget_id", resultMap.get("el_appl_budget_id"))
//			.put("uuid", resultMap.get("el_appl_budget_id"))
//			.put("acct_cde", resultMap.get("acct_cde"))
//			.put("analysis_cde", resultMap.get("analysis_cde"))
//			.put("fund_cde", resultMap.get("fund_cde"))
//			.put("proj_id", resultMap.get("proj_id"))
//			.put("proj_nbr", resultMap.get("proj_nbr"))
//			.put("class", resultMap.get("class"))
//			.put("bco_aprv_id", resultMap.get("bco_aprv_id"))
//			.put("bco_aprv_name", resultMap.get("bco_aprv_name"))
//			.put("budg_acct_share", resultMap.get("budg_acct_share"))
//			.put("budg_acct_amt", resultMap.get("budg_acct_amt"))
//			.put("mod_ctrl_txt", resultMap.get("mod_ctrl_txt"))
//			;
//			
//			((JSONArray) outputMap.get("details")).put(budgetdetailObj);
//		}
//		
//		JSONArray jsonArr = new JSONArray(outputList);
//		return jsonArr;
//	}
	

	private JSONArray getElApplAprv(String applHdrId) throws Exception {
		JSONArray jsonArr = new JSONArray();
		List<ElApplAprvStatusDAO> daoList = elApplAprvStatusRepository.findByApplHdrId(applHdrId);
		
		boolean pendingFoSrFound = false;
		boolean pendingFoSfmFound = false;
		boolean pendingFoPnbFound = false;
		boolean pendingFoRectify = false;
		for (ElApplAprvStatusDAO dao : daoList) {
			// 20240207 Change for FO Team approval
			String aprvUserId = dao.getAprvUserId();
			
			if (AprvTypeConstants.FO_SR.equals(dao.getAprvTypeCde()) && dao.getApproved() == -1 ) {
				if (pendingFoSrFound) {
					continue;
				} else {
					pendingFoSrFound = true;
					aprvUserId = String.join(", ", daoList.stream().filter(e -> e.getAprvTypeCde().equals(AprvTypeConstants.FO_SR)).distinct().map(e -> e.getAprvUserId()).collect(Collectors.toList()));
				}
			} else if (AprvTypeConstants.FO_SFM.equals(dao.getAprvTypeCde()) && dao.getApproved() == -1 ) {
				if (pendingFoSfmFound) {
					continue;
				} else {
					pendingFoSfmFound = true;
					aprvUserId = String.join(", ", daoList.stream().filter(e -> e.getAprvTypeCde().equals(AprvTypeConstants.FO_SFM)).distinct().map(e -> e.getAprvUserId()).collect(Collectors.toList()));
				}
			} else if (AprvTypeConstants.FO_PNB.equals(dao.getAprvTypeCde()) && dao.getApproved() == -1 ) {
				if (pendingFoPnbFound) {
					continue;
				} else {
					pendingFoPnbFound = true;
					aprvUserId = String.join(", ", daoList.stream().filter(e -> e.getAprvTypeCde().equals(AprvTypeConstants.FO_PNB)).distinct().map(e -> e.getAprvUserId()).collect(Collectors.toList()));
				}
			} else if (AprvTypeConstants.FO_RECTIFY.equals(dao.getAprvTypeCde()) && dao.getApproved() == -1 ) {
				if (pendingFoRectify) {
					continue;
				} else {
					pendingFoRectify = true;
					aprvUserId = String.join(", ", daoList.stream().filter(e -> e.getAprvTypeCde().equals(AprvTypeConstants.FO_RECTIFY)).distinct().map(e -> e.getAprvUserId()).collect(Collectors.toList()));
				}
			}
			
			JSONObject jsonObj = new JSONObject();
			jsonArr.put(jsonObj);

			jsonObj
			.put("el_appl_arpv_status_id", dao.getId())
			.put("uuid", dao.getId())
			.put("arpv_seq", dao.getArpvSeq())
			.put("aprv_type_cde", dao.getAprvTypeCde())
			.put("aprv_user_id", aprvUserId)
			.put("aprv_user_name", dao.getAprvUserName())
			.put("approved", dao.getApproved())
			.put("aprv_dttm", dao.getAprvDttm())
			.put("aprv_remark", dao.getAprvRemark())
			.put("mod_ctrl_txt", dao.getModCtrlTxt())
			;
		}
		
		return jsonArr;
	}
	

	@Override
	public JSONArray getMyApplications() throws Exception {
		String remoteUser = SecurityUtils.getCurrentLogin();
		JSONArray jsonArr = new JSONArray();
		List<Map<String, Object>> resultMapList = elApplHdrRepository.findMyApplications(remoteUser);
		
		for (Map<String, Object> resultMap : resultMapList) {
			JSONObject jsonObj = new JSONObject();
			jsonArr.put(jsonObj);

			for (String keys : resultMap.keySet()) {
				jsonObj.put(keys.toLowerCase(), resultMap.get(keys));
			}
		}
		
		return jsonArr;
	}

	@Override
	@Obsolete // TODO
	public JSONArray genApplicationApprvs(JSONObject inputJson) throws Exception {
		JSONArray jsonArr = new JSONArray();		
		// Dummy for development
		List<String> testList = Arrays.asList("YU, Michael", "LEUNG, Ka Hei", "YUEN, Quin");
		List<String> typeList = Arrays.asList("DEPT_HEAD", "DEPT_HEAD", "BCO");
		
		for (int i =1; i<=testList.size(); i++) {
			JSONObject jsonObj = new JSONObject();
			jsonArr.put(jsonObj);
			
			jsonObj
			.put("aprv_seq", i)
			.put("aprv_user_id", "isod0" + i)
			.put("aprv_user_name", testList.get(i-1))
			.put("aprv_type_cde", typeList.get(i-1))
			;
		}
		
		return jsonArr;
	}

	@Override
	public void validateApplicationApprvs(JSONArray inputJson) throws Exception {
		Map<String, String> queryParams = new HashMap<>();
		queryParams.put("timestamp", GeneralUtil.genModCtrlTxt().substring(0, 14)); // yyyyMMddHHmmss
		queryParams.put("remote_user", SecurityUtils.getCurrentLogin());
//		queryParams.put("remote_user", "isod01");
		queryParams.put("business_unit", "HKUST");
		
		// Get COAs
		for (int i=1; i<= inputJson.length(); i++) {
			JSONObject coaJsonObj = inputJson.getJSONObject(i-1);
			queryParams.put("coa_account_" + i, coaJsonObj.optString("acct_cde"));
			queryParams.put("coa_chartfield1_" + i, coaJsonObj.optString("analysis_cde"));
			queryParams.put("coa_fund_code_" + i, coaJsonObj.optString("fund_cde"));
			queryParams.put("coa_deptid_" + i, coaJsonObj.optString("dept_id"));
			queryParams.put("coa_project_id_" + i, coaJsonObj.optString("proj_id"));
			queryParams.put("coa_class_fld_" + i, coaJsonObj.optString("class"));
			queryParams.put("approver_" + i, coaJsonObj.optString("bco_aprv_id"));
			queryParams.put("amount_" + i, coaJsonObj.optString("pymt_amt"));
			
			if (i==inputJson.length()) {
				queryParams.put("num_of_coa", ""+i);
			}
		}
		
		// Build the query string
        StringBuilder queryString = new StringBuilder();
        for (Map.Entry<String, String> entry : queryParams.entrySet()) {
            String key = URLEncoder.encode(entry.getKey(), "UTF-8");
            String value = URLEncoder.encode(entry.getValue(), "UTF-8");
            queryString.append(key).append("=").append(value).append("&");
        }
        
        // Remove the trailing '&' character
        if (queryString.length() > 0) {
            queryString.setLength(queryString.length() - 1);
        }
        // Base URL
        String baseUrl = env.getProperty(AppConstants.BUDGET_APPROVAL_API_URL);
        
        // Construct the URL with query parameters
        String url = baseUrl + "?" + queryString.toString();
        log.debug("url: {}", url);
        
        Document document = GeneralUtil.loadXMLFromLink(url);
        
	    // Get the value of the "status_code" element
	    String statusCode = document.getElementsByTagName("status_code").item(0).getTextContent();
	    String statusDescr = document.getElementsByTagName("status_descr").item(0).getTextContent();
	    
	    // List to save invalid lines
	    List<String> invalidList = new ArrayList<>();
	    
	    // Check if the "status_code" is "00" or no error msg
	    if ("00".equals(statusCode) && statusDescr.isBlank()) {
	        // Get all "coa_distrib_line" elements
	        NodeList coaDistribLines = document.getElementsByTagName("coa_distrib_line");

	        for (int i = 0; i < coaDistribLines.getLength(); i++) {
	            Node coaDistribLine = coaDistribLines.item(i);
	            NodeList approvalIndList = ((Element) coaDistribLine).getElementsByTagName("approval_ind");

	            if (approvalIndList.getLength() > 0) {
	                String approvalIndValue = approvalIndList.item(0).getTextContent();
	                log.debug("Approval Ind: {}", approvalIndValue);
	                
	                // Add to invalidList if approval_ind = "N"
	                if ("N".equals(approvalIndValue)) {
	                	NodeList linenbrList = ((Element) coaDistribLine).getElementsByTagName("line_nbr");
	                	String lineNbr = linenbrList.item(0).getTextContent();
	                	
	                	invalidList.add(lineNbr);
	                }
	            }
	        }
	    } else {
	        throw new InvalidParameterException("Approver validation error: " + statusDescr);
	    }
	    
	    // Throw exception if there are invalid lines
	    if (invalidList.size() > 0) {
	    	String errMsg = "Invalid approver for line " + String.join(", ", invalidList);
	    	throw new InvalidParameterException(errMsg);
	    }
	}

	@Override
	public JSONArray getApplsPendingRemoteUserApproval() throws Exception {
		String remoteUser = SecurityUtils.getCurrentLogin();
		JSONArray jsonArr = new JSONArray();
		List<Map<String, Object>> resultMapList = elApplHdrRepository.findPendingRemoteUserApproval(remoteUser);
		
		for (Map<String, Object> resultMap : resultMapList) {
			JSONObject jsonObj = new JSONObject();
			jsonArr.put(jsonObj);

			for (String keys : resultMap.keySet()) {
				jsonObj.put(keys.toLowerCase(), resultMap.get(keys));
			}
		}
		
		return jsonArr;
	}

	@Override
	@Transactional(value = "transactionManagerJselMain", rollbackFor = Exception.class)
	public JSONObject updateElApplicationStatus(String applHdrId, JSONObject inputJson, String opPageName) throws Exception {
		JSONObject outputJson = new JSONObject();

		// validation
		ElApplHdrDAO hdrDAO = elApplHdrRepository.findOneForUpdate(applHdrId);
		
		if (hdrDAO == null) {
			throw new RecordNotExistException("Extra Load Application");
		}
		
		if (!hdrDAO.getModCtrlTxt().equals(inputJson.getString("mod_ctrl_txt"))) {
			throw new RecordModifiedException();
		}
		if (hdrDAO.getApplStatCde().equals(ApplStatusConstants.PYMT_REJECTED)) {
			throw new InvalidParameterException("Paymet rejected application cannot be edit from application list.");
		}
		
		
		String role = inputJson.getString("role");
		boolean isRequester = role.equalsIgnoreCase(RoleConstants.REQUESTER);
		boolean isApplicant = role.equalsIgnoreCase(RoleConstants.APPLICANT);
		String remoteUser = SecurityUtils.getCurrentLogin();

		ElApplAprvStatusDAO elApplAprvStatusDAO = null;

		if (isRequester) {
			if (!remoteUser.equals(hdrDAO.getApplRequesterId())) {
				throw new InvalidParameterException("Not application requestor");
			}
		} else if (isApplicant) {
			if (!remoteUser.equals(hdrDAO.getApplUserId())) {
				throw new InvalidParameterException("Not application applicant");
			}
			
			if (!ApplStatusConstants.PENDING_APPL_ACCT.equals(hdrDAO.getApplStatCde())) {
				throw new InvalidParameterException("Application is not pending for applicant to accept");
			}
			
		} else {
			if (!ApplStatusConstants.PENDING.equals(hdrDAO.getApplStatCde()) || 0 != hdrDAO.getObsolete()) {
				throw new InvalidParameterException("Application is not pending for approval");
			}
			elApplAprvStatusDAO = elApplAprvStatusRepository.findNextAprv(applHdrId);
			
			if (elApplAprvStatusDAO == null || !remoteUser.equals(elApplAprvStatusDAO.getAprvUserId())) {
				throw new InvalidParameterException("Not pending approver");
			}
		}
		// validation ends
		
		// start update
		Timestamp currTS = GeneralUtil.getCurrentTimestamp();
		String modCtrlTxt = GeneralUtil.genModCtrlTxt();
		String applNbr = hdrDAO.getApplNbr();
		
		// Requester action assume remove only
		if (isRequester) {
			hdrDAO.setApplStatCde(ApplStatusConstants.REMOVED);
			hdrDAO.setObsolete(1);
			
			hdrDAO.setChngDat(currTS);
			hdrDAO.setChngUser(remoteUser);
			hdrDAO.setOpPageNam(opPageName);
			hdrDAO.setModCtrlTxt(modCtrlTxt);
			
			// send email to Approver for cancellation of application
			sendEmailToAprvForCancelledAppl(applHdrId, opPageName);
			commonRoutineService.callAIWorkListCompleteAPI(applNbr, "false", "cancelled");
		} else if (isApplicant) {
			// Applicant to accept or reject to application submission only
			boolean isAccept = ElApplActDAO.ACCEPT.equalsIgnoreCase(inputJson.getString("action"));
			boolean isReject = ElApplActDAO.REJECT.equalsIgnoreCase(inputJson.getString("action"));
			
			if(!isAccept && !isReject) {
				throw new InvalidParameterException("Invalid action.");
			}
			
			if(isAccept) {
				// Assume always have approver for the next step
				hdrDAO.setApplStatCde(ApplStatusConstants.PENDING);
				
				// check if need to update applicant declaration field
				if(getApplWorkflowType(hdrDAO.getApplUserJobCatg()).equals("B")) {
					JSONObject applUserDeclJsonObj = inputJson.getJSONObject("appl_user_decl");
					
					int Declaration1 = applUserDeclJsonObj.getInt("user_decl_1");
					int Declaration2 = applUserDeclJsonObj.getInt("user_decl_2");
					
					if(Declaration1 != 1 && Declaration1 != 2 && Declaration2 != 1 && Declaration2 != 2) {
						log.error("[updateElApplicationStatus] applUserDeclJsonObj: " + applUserDeclJsonObj.toString());
						throw new InvalidParameterException("Please complete all the declaration.");
					}
					
					Timestamp Declaration2_from_dt = GeneralUtil.NULLTIMESTAMP;
					Timestamp Declaration2_to_dt = GeneralUtil.NULLTIMESTAMP;
					
					try {
						Declaration2_from_dt = new Timestamp(applUserDeclJsonObj.getLong("user_decl_2_from_dt"));
					} catch(Exception e) {
						
					}
					try {
						Declaration2_to_dt = new Timestamp(applUserDeclJsonObj.getLong("user_decl_2_to_dt"));
					} catch(Exception e) {
						
					}
					
					if(Declaration2 == 1) {
						if(Declaration2_from_dt == GeneralUtil.NULLTIMESTAMP || Declaration2_to_dt == GeneralUtil.NULLTIMESTAMP) {
							throw new InvalidParameterException("Please select the period to undertake the extra load activity.");
						}
					}
					
					hdrDAO.setUserDecl_1(Declaration1);
					hdrDAO.setUserDecl_2(Declaration2);
					hdrDAO.setUserDecl2FromDt(Declaration2_from_dt);
					hdrDAO.setUserDecl2ToDt(Declaration2_to_dt);
					
					// insert applicant uploaded support attachment
					insertElApplAttachments(applHdrId, applUserDeclJsonObj.getJSONArray("user_dec_suprt_attachments"), ElApplAttachmentsDAO.SUPPORTING, opPageName);
				} else {
					log.error("[updateElApplicationStatus] workflow");
					throw new InvalidParameterException("Invalid job catalogy for the action.");
				}
				
				hdrDAO.setChngDat(currTS);
				hdrDAO.setChngUser(remoteUser);
				hdrDAO.setOpPageNam(opPageName);
				hdrDAO.setModCtrlTxt(modCtrlTxt);
				
				sendEmailToPendingAprv(applHdrId, opPageName);
				// update My AI
				commonRoutineService.callAIWorkListApprvAPI(applNbr, "true", remoteUser, "Pending Declaration by " + remoteUser);
				createMyAiTaskForPendingAprv(applHdrId);
			} else if(isReject) {
				hdrDAO.setApplStatCde(ApplStatusConstants.REJECTED);
				
				hdrDAO.setChngDat(currTS);
				hdrDAO.setChngUser(remoteUser);
				hdrDAO.setOpPageNam(opPageName);
				hdrDAO.setModCtrlTxt(modCtrlTxt);
				
				sendEmailToSubmitterOnApplicantReject(applHdrId, opPageName);
				// update My AI
				commonRoutineService.callAIWorkListApprvAPI(applNbr, "false", remoteUser, "Pending Declaration by " + remoteUser);
				commonRoutineService.callAIWorkListCompleteAPI(applNbr, "false", "completed");
				createPendingMyAdminTaskForRequestor(hdrDAO);
			}
				
		} else {
			boolean isApprove = ElApplActDAO.APPROVE.equalsIgnoreCase(inputJson.getString("action"));
			
			// update elApplAprvStatusDAO
			elApplAprvStatusDAO.setApproved(isApprove ? 1 : 0);
			elApplAprvStatusDAO.setAprvDttm(currTS);
			elApplAprvStatusDAO.setAprvRemark(GeneralUtil.initBlankString(inputJson.optString("aprv_remark")));
			elApplAprvStatusDAO.setChngDat(currTS);
			elApplAprvStatusDAO.setChngUser(remoteUser);
			elApplAprvStatusDAO.setOpPageNam(opPageName);
			elApplAprvStatusDAO.setModCtrlTxt(modCtrlTxt);
			
			if(isApprove) {
				// check if current approver is dept head role
				if(elApplAprvStatusDAO.getAprvTypeCde().equals(AprvTypeConstants.DEPT_HEAD)) {
					// check application workflow type
					if(getApplWorkflowType(hdrDAO.getApplUserJobCatg()).equals("B")) {
						// update the declaration for dept head
						JSONObject applUserDeclJsonObj = inputJson.getJSONObject("appl_hod_decl");
						
						if(applUserDeclJsonObj != null) {
							int Declaration1 = applUserDeclJsonObj.getInt("hod_decl_1_1");
							int Declaration2 = applUserDeclJsonObj.getInt("hod_decl_1_2");
							
							if(Declaration1 != 1 && Declaration1 != 2 && Declaration2 != 1 && Declaration2 != 2) {
								log.error("[updateElApplicationStatus] applUserDeclJsonObj: " + applUserDeclJsonObj.toString());
								throw new InvalidParameterException("Please complete all the declaration.");
							}
							
							hdrDAO.setHodDecl_1_1(Declaration1);
							hdrDAO.setHodDecl_1_2(Declaration2);
						}
					}
				}
			}
			
			// 20231229 #251: try to approve if next approval role is the same user
			List<ElApplAprvStatusDAO> allPedningAprvList = elApplAprvStatusRepository.findAllPendingAprv(applHdrId);
			
			int currentSeq = elApplAprvStatusDAO.getArpvSeq();
			boolean isChngSeq = false;
			List<ElApplAprvStatusDAO> updateList = new ArrayList<>();
			
			for(ElApplAprvStatusDAO dao : allPedningAprvList) {
				// assume the dao object should be the current updated dao 
				if(dao.getId().equals(elApplAprvStatusDAO.getId()))
					continue;
				
				if(isChngSeq == true && currentSeq != dao.getArpvSeq())
					break;
				
				if(currentSeq != dao.getArpvSeq()) {
					isChngSeq = true;
					currentSeq = dao.getArpvSeq();
					log.info("change seq");
				}
				
				if(remoteUser.equals(dao.getAprvUserId())) {
					log.info("update approver");
					dao.setApproved(isApprove ? 1 : 0);
					dao.setAprvDttm(currTS);
					dao.setAprvRemark(GeneralUtil.initBlankString(inputJson.optString("aprv_remark")));
					dao.setChngDat(currTS);
					dao.setChngUser(remoteUser);
					dao.setOpPageNam(opPageName);
					dao.setModCtrlTxt(modCtrlTxt);
					
					updateList.add(dao);
					
					isChngSeq = false;
				}
			}
			
			if(updateList.size() > 0)
				elApplAprvStatusRepository.saveAll(updateList);
			
			
			// if approve, check if there are next approver
			// if yes, workflow to send email
			// Else, update header to APPROVED
			if (isApprove) {
				ElApplAprvStatusDAO nextPendingAprv = elApplAprvStatusRepository.findNextAprv(applHdrId);
				
				// TODO notify workflow
				if (nextPendingAprv != null) {
					hdrDAO.setChngDat(currTS);
					hdrDAO.setChngUser(remoteUser);
					hdrDAO.setOpPageNam(opPageName);
					hdrDAO.setModCtrlTxt(modCtrlTxt);
					
					// send email to next aprv
					sendEmailToPendingAprv(applHdrId, opPageName);
					// update My Ai
					commonRoutineService.callAIWorkListApprvAPI(applNbr, "true", remoteUser, "Pending Approval by " + remoteUser);
					createMyAiTaskForPendingAprv(applHdrId);
				} else {
					if(elApplHdrHistRepository.findMaxVersionNoById(applHdrId) < hdrDAO.getVersionNo()) {
						// Should be the first backup version.	
						
						hdrDAO.setApplStatCde(ApplStatusConstants.APPROVED);
						
						hdrDAO.setChngDat(currTS);
						hdrDAO.setChngUser(remoteUser);
						hdrDAO.setOpPageNam(opPageName);
						hdrDAO.setModCtrlTxt(modCtrlTxt);
						
						// 20231229 #371 mark the BR_POST_IND='R' when application is approved
						hdrDAO.setBrPostInd("R");
						
						// Backup Version
						
						ElApplHdrHistDAO saveHdrDAO = new ElApplHdrHistDAO(hdrDAO);
						saveHdrDAO.setCreatUser(remoteUser);
						saveHdrDAO.setChngUser(remoteUser);
						saveHdrDAO.setOpPageNam(opPageName);
						
						// Assume there is only 1 MPF method and 1 SALARY
						ElApplPymtMethodDAO elApplMPFPymtMethodDAO = elApplPymtMethodRepository.findMPFMethodByApplHdrId(applHdrId).get(0);
						ElApplPymtMethodDAO elApplSalaryPymtMethodDAO = elApplPymtMethodRepository.findSalaryMethodByApplHdrId(applHdrId).get(0);
						List<ElApplPymtMethodHistDAO> saveMethodList = new ArrayList<>();
						
						ElApplPymtMethodHistDAO elApplMPFPymtMethodHistDAO = new ElApplPymtMethodHistDAO(elApplMPFPymtMethodDAO, hdrDAO);
						elApplMPFPymtMethodHistDAO.setCreatUser(remoteUser);
						elApplMPFPymtMethodHistDAO.setChngUser(remoteUser);
						elApplMPFPymtMethodHistDAO.setOpPageNam(opPageName);
						saveMethodList.add(elApplMPFPymtMethodHistDAO);
						
						ElApplPymtMethodHistDAO elApplSalaryPymtMethodHistDAO = new ElApplPymtMethodHistDAO(elApplSalaryPymtMethodDAO, hdrDAO);
						elApplSalaryPymtMethodHistDAO.setCreatUser(remoteUser);
						elApplSalaryPymtMethodHistDAO.setChngUser(remoteUser);
						elApplSalaryPymtMethodHistDAO.setOpPageNam(opPageName);
						saveMethodList.add(elApplSalaryPymtMethodHistDAO);
						
						List<ElApplPymtScheduleDAO> mpfList = elApplPymtScheduleRepository.findMPFByApplHdrId(applHdrId);
						List<ElApplPymtScheduleHistDAO> saveScheduleList = new ArrayList<>();
						for(ElApplPymtScheduleDAO dao: mpfList) {
							ElApplPymtScheduleHistDAO histDAO = new ElApplPymtScheduleHistDAO(dao, elApplMPFPymtMethodHistDAO);
							histDAO.setCreatUser(remoteUser);
							histDAO.setChngUser(remoteUser);
							histDAO.setOpPageNam(opPageName);
							
							saveScheduleList.add(histDAO);
						}
						
						List<ElApplPymtScheduleDAO> salaryList = elApplPymtScheduleRepository.findSalaryByApplHdrId(applHdrId);
						for(ElApplPymtScheduleDAO dao: salaryList) {
							ElApplPymtScheduleHistDAO histDAO = new ElApplPymtScheduleHistDAO(dao, elApplSalaryPymtMethodHistDAO);
							histDAO.setCreatUser(remoteUser);
							histDAO.setChngUser(remoteUser);
							histDAO.setOpPageNam(opPageName);
							
							saveScheduleList.add(histDAO);
						}
						
						List<ElApplCourseDAO> courseList = elApplCourseRepository.findByApplHdrId(applHdrId);
						List<ElApplCourseHistDAO> saveCourseList = new ArrayList<>();
						List<ElApplColistCourseHistDAO> saveColistList = new ArrayList<>();
	
						for(ElApplCourseDAO dao: courseList) {
							ElApplCourseHistDAO histDAO = new ElApplCourseHistDAO(dao, hdrDAO);
							histDAO.setCreatUser(remoteUser);
							histDAO.setChngUser(remoteUser);
							histDAO.setOpPageNam(opPageName);
							
							saveCourseList.add(histDAO);
	
							List<ElApplColistCourseDAO> colistList = elApplColistCourseRepository.findByApplCrseId(dao.getId());
							for(ElApplColistCourseDAO coDAO: colistList) {
								ElApplColistCourseHistDAO coHistDAO = new ElApplColistCourseHistDAO(coDAO, histDAO);
								coHistDAO.setCreatUser(remoteUser);
								coHistDAO.setChngUser(remoteUser);
								coHistDAO.setOpPageNam(opPageName);
								
								saveColistList.add(coHistDAO);
							}
						}
						
						List<ElApplElTypeDAO> elTypeList = elApplElTypeRepository.findByApplHdrId(applHdrId);
						List<ElApplElTypeHistDAO> saveElTypeList = new ArrayList<>(); 
	
						for(ElApplElTypeDAO dao: elTypeList) {
							ElApplElTypeHistDAO histDAO = new ElApplElTypeHistDAO(dao, hdrDAO);
							histDAO.setCreatUser(remoteUser);
							histDAO.setChngUser(remoteUser);
							histDAO.setOpPageNam(opPageName);
							
							saveElTypeList.add(histDAO);
						}
	
						ElApplPrgmDAO prgmDao = elApplPrgmRepository.findByApplHdrId(applHdrId);
						if(prgmDao != null) {
							ElApplPrgmHistDAO savePrgmDAO = new ElApplPrgmHistDAO(prgmDao, hdrDAO);
							savePrgmDAO.setCreatUser(remoteUser);
							savePrgmDAO.setChngUser(remoteUser);
							savePrgmDAO.setOpPageNam(opPageName);
							
							elApplPrgmHistRepository.save(savePrgmDAO);
						}
						
						elApplHdrHistRepository.save(saveHdrDAO);
						elApplCourseHistRepository.saveAll(saveCourseList);
						elApplColistCourseHistRepository.saveAll(saveColistList);
						elApplElTypeHistRepository.saveAll(saveElTypeList);
						elApplPymtMethodHistRepository.saveAll(saveMethodList);
						elApplPymtScheduleHistRepository.saveAll(saveScheduleList);
					}
					
					// update My Ai
					commonRoutineService.callAIWorkListApprvAPI(applNbr,  "true", remoteUser, "Pending Approval by " + remoteUser);
					createMyAiTaskForPendingIntegration(applHdrId);
				}
				
			} 
			// if reject, update header status to REJECTED
			else {
				hdrDAO.setApplStatCde(ApplStatusConstants.REJECTED);
				
				hdrDAO.setChngDat(currTS);
				hdrDAO.setChngUser(remoteUser);
				hdrDAO.setOpPageNam(opPageName);
				hdrDAO.setModCtrlTxt(modCtrlTxt);
				
				// send email to notify requestor
				sendEmailToSubmitterOnApproverReject(applHdrId, opPageName);
				// update My Ai
				commonRoutineService.callAIWorkListApprvAPI(applNbr, "false", remoteUser, "Pending Approval by " + remoteUser);
				commonRoutineService.callAIWorkListCompleteAPI(applNbr, "false", "completed");
				createPendingMyAdminTaskForRequestor(hdrDAO);
			}
		}
		
		// creation record in action table
		if (!RoleConstants.REQUESTER.equalsIgnoreCase(role) && !RoleConstants.APPLICANT.equalsIgnoreCase(role)) {
			if (elApplAprvStatusDAO != null) {
				inputJson.put("role", elApplAprvStatusDAO.getAprvTypeCde());
			}
		}
		generalApiService.createElApplAct(applHdrId, inputJson, "", "", "appl", opPageName);
		
		return outputJson;
	}

	@Override
	public JSONArray getApplsForEnquiry(JSONObject inputJson) throws Exception {
		JSONArray outputJson = new JSONArray();
		String remoteUser = SecurityUtils.getCurrentLogin();
		
		// Get param
		String applName = GeneralUtil.refineParam(inputJson.optString("appl_name"));
		String applNbr = GeneralUtil.refineParam(inputJson.optString("appl_nbr"));
		String prgmTerm = GeneralUtil.refineParam(inputJson.optString("prgm_term"));
		String schCde = GeneralUtil.refineParam(inputJson.optString("sch_cde"));
		String prgmCde = GeneralUtil.refineParam(inputJson.optString("prgm_cde"));
		String applStatCde = inputJson.optString("appl_stat_cde");
		
		List<String> applStatusList = elApplStatTabRepository.findAll().stream().map(dao -> dao.getApplStatCde()).collect(Collectors.toList());
//		List<String> applStatusList = Arrays.asList(ApplStatusConstants.REMOVED, ApplStatusConstants.COMPLETED, ApplStatusConstants.DRAFT);
		
		if (!applStatCde.isBlank()) {
			applStatusList = applStatusList.stream().filter(e -> e.equals(applStatCde)).collect(Collectors.toList());
		}
		
		Timestamp applDttm = GeneralUtil.NULLTIMESTAMP;
		Timestamp prgmStartDt = GeneralUtil.NULLTIMESTAMP;
		Timestamp prgmEndDt = GeneralUtil.INFINTYTIMESTAMP;
		
		try {
			applDttm = new Timestamp(inputJson.getLong("appl_dttm"));
		} catch(Exception e) {
		}
		try {
			prgmStartDt = new Timestamp(inputJson.getLong("prgm_start_dttm"));
		} catch(Exception e) {
		}
		try {
			prgmEndDt = new Timestamp(inputJson.getLong("prgm_end_dttm"));
		} catch(Exception e) {
		}
		
		log.debug("remoteUser {}, applName {}, applDttm {}, applNbr {}, prgmTerm {}, schCde {}, prgmCde {}, applStatCde {}, prgmStartDt {}, prgmEndDt {}"
				,remoteUser, applName, applDttm, applNbr, prgmTerm, schCde, prgmCde, applStatCde, prgmStartDt, prgmEndDt);
		
		List<Map<String, Object>> resultMapList = elApplHdrRepository.searchAll(remoteUser, applName, applDttm, applNbr,
				applStatusList, prgmTerm, schCde, prgmCde, prgmStartDt, prgmEndDt);
		
		for (Map<String, Object> resultMap : resultMapList) {
			JSONObject jsonObj = new JSONObject();
			outputJson.put(jsonObj);

			for (String keys : resultMap.keySet()) {
				jsonObj.put(keys.toLowerCase(), resultMap.get(keys));
			}
		}
		
		return outputJson;
	}
	
	private void validateElApplHdr(JSONObject inputJson) throws Exception {
		// appl_user_id
		String applUserId = inputJson.optString("appl_user_id");
		if (applUserId.isBlank()) {
			throw new InvalidParameterException("Applicant User Id is required");
		}
		
		// 20231228 #1 : check applicant user should be acad staff
		JSONArray applList = generalApiService.getStaffListByUserId(applUserId, "applicant");
		
		if (applList.length() == 0) {
			throw new InvalidParameterException("Applicant is not found");
		}
		inputJson.put("appl_user_name", applList.getJSONObject(0).getString("display_nam"));
		
		// Payroll Related Description
		JSONObject pymtMethod = inputJson.optJSONObject("el_appl_pymt_method");
		if (pymtMethod != null && pymtMethod.optString("payroll_descr").trim().length() > 30) {
			throw new InvalidParameterException("Payroll Related Description is limited to 30 characters.");
		}
		// if draft, no need remaining validation
		if (ElApplActDAO.DRAFT.equalsIgnoreCase(inputJson.getString("action_type"))) {
			return ;
		}
		// category_cde
		if (GeneralUtil.initBlankString(inputJson.optString("category_cde")).isBlank()) {
			throw new InvalidParameterException("Payment Type is required");
		}
		
		// appl_term
//		if (GeneralUtil.initBlankString(inputJson.optString("appl_start_term")).isBlank() || GeneralUtil.initBlankString(inputJson.optString("appl_end_term")).isBlank()) {
//			throw new InvalidParameterException("Semester is required");
//		}
		if (GeneralUtil.initBlankString(inputJson.optString("appl_start_term")).isBlank()) {
			throw new InvalidParameterException("Starting Semester is required");
		}
		log.debug("a:{}xx", inputJson.optString("appl_end_term"));
		if (!GeneralUtil.initBlankString(inputJson.optString("appl_end_term")).isBlank() && inputJson.getInt("appl_start_term") > inputJson.getInt("appl_end_term")) {
			throw new InvalidParameterException("Not valid Semester period");
		}

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

		// appl_start_dt
		if (applStartDt == GeneralUtil.NULLTIMESTAMP) {
			throw new InvalidParameterException("Period is required");
		}
		// appl_end_dt
		if (applEndDt == GeneralUtil.NULLTIMESTAMP) {
			throw new InvalidParameterException("Period is required");
		}
		// payroll description
		if (pymtMethod == null || pymtMethod.optString("payroll_descr").trim().isBlank()) {
			throw new InvalidParameterException("Payroll Related Description is required");
		}
	}
	
	private void validateElApplPrgm(JSONObject inputJson) throws Exception {
//		if (inputJson == null) {
//			throw new InvalidParameterException("Program Details is required"); 
//		}
		// prgm_term
//		if (inputJson.optString("prgm_term").isBlank()) {
//			throw new InvalidParameterException("Semester is required");
//		}
		// prgm_cde
//		if (GeneralUtil.initBlankString(inputJson.optString("prgm_cde")).isBlank()) {
//			throw new InvalidParameterException("Program is required");
//		}
//		if (inputJson.optLong("prgm_start_dt") == 0 || inputJson.optLong("prgm_end_dt") == 0) {
//			throw new InvalidParameterException("Please select valid program period");
//		}
		
		if(!GeneralUtil.initBlankString(inputJson.optString("prgm_cde")).isBlank()) {
			// TODO: only for initial development, should be looking up from view in back end
			if (GeneralUtil.initBlankString(inputJson.optString("sch_cde")).isBlank()) {
				throw new InvalidParameterException("School is required");
			}
			// TODO: only for initial development, should be looking up from view in back end
			if (GeneralUtil.initBlankString(inputJson.optString("dept")).isBlank()) {
				throw new InvalidParameterException("Department is required");
			}
		}
	}
	
	private void validateElCourseRelatedDetails(Integer startTerm, Integer endTerm, JSONArray inputJson) throws Exception {
		if (inputJson == null || inputJson.length() == 0) {
			// 20231229 Bobby allow submit without input course info
//			throw new InvalidParameterException("Couse Details is required"); 
			return;
		}
		for (int i=0; i< inputJson.length(); i++) {
			JSONObject courseJsonObj = inputJson.getJSONObject(i);
			// crse_id
			if (GeneralUtil.initBlankString(courseJsonObj.optString("crse_id")).isBlank()) {
//				throw new InvalidParameterException("Course Id is required");
			}
			// Term
			String acad_term = courseJsonObj.optString("crse_cde").split("_")[0];
			if (!GeneralUtil.isInteger(acad_term)) {
				throw new InvalidParameterException("Course Semester is invalid");
			}
			
			Integer termInt = Integer.valueOf(acad_term);
			// If user did not select end term, then endTerm = 0
			if (endTerm != 0 ) {
				if (!(startTerm <= termInt && endTerm >= termInt)) {
					throw new InvalidParameterException("Course is not within selected semester");
				}
			} else {
				if (startTerm.compareTo(termInt) != 0) {
					throw new InvalidParameterException("Course is not within selected semester");
				}
			}

			// get from view by crse id
//			// crse_cde
//			if (GeneralUtil.initBlankString(courseJsonObj.optString("crse_cde")).isBlank()) {
//				throw new InvalidParameterException("Course Code is required");
//			}
//			// crse_descr
//			if (GeneralUtil.initBlankString(courseJsonObj.optString("crse_descr")).isBlank()) {
//				throw new InvalidParameterException("Course Descriptions is required");
//			}
			// section
			if (GeneralUtil.initBlankString(courseJsonObj.optString("section")).isBlank()) {
				throw new InvalidParameterException("Course Section is required");
			}
			// credit
			if (Double.isNaN(courseJsonObj.optDouble("credit"))) {
				throw new InvalidParameterException("Course Credit is required");
			}
			// crse_co_taught_hr need check decimal
			if (Double.isNaN(courseJsonObj.optDouble("crse_co_taught_hr")) || Double.compare(courseJsonObj.optDouble("crse_co_taught_hr"), 0) < 0) {
				throw new InvalidParameterException("Co-taught hour is required / invalid");
			}
			// crse_total_hr need check decimal
			if (Double.isNaN(courseJsonObj.optDouble("crse_total_hr")) || Double.compare(courseJsonObj.optDouble("crse_total_hr"), 0) < 0) {
				throw new InvalidParameterException("Total hour is required / invalid");
			}
			
			// Co-list course
			JSONArray colistCourseArr = courseJsonObj.optJSONArray("colist_course");
			if (colistCourseArr != null) {
				for (int j=0; j< colistCourseArr.length(); j++) {
					JSONObject colistJsonObj = colistCourseArr.getJSONObject(j);
					// crse_id
					if (GeneralUtil.initBlankString(colistJsonObj.optString("crse_id")).isBlank()) {
//						throw new InvalidParameterException("Co-List Course Id is required");
					}
					// Term
					if (!GeneralUtil.isInteger(colistJsonObj.optString("crse_cde").split("_")[0])) {
						throw new InvalidParameterException("Co-Course Semester is invalid");
					}
					Integer coTermInt = Integer.valueOf(acad_term);
					if (!(startTerm <= coTermInt && endTerm >= coTermInt)) {
						throw new InvalidParameterException("Co-Course is not within selected semester");
					}
					// get from view by crse id
//					// crse_cde
//					if (GeneralUtil.initBlankString(colistJsonObj.optString("crse_cde")).isBlank()) {
//						throw new InvalidParameterException("Co-List Course Code is required");
//					}
					// section
					if (GeneralUtil.initBlankString(colistJsonObj.optString("section")).isBlank()) {
						throw new InvalidParameterException("Co-List Course Section is required");
					}
					// credit need check decimal
					if (Double.isNaN(colistJsonObj.optDouble("credit")) || Double.compare(colistJsonObj.optDouble("credit"), 0) < 0) {
						throw new InvalidParameterException("Co-List Course Credit is required / invalid");
					}
					// crse_co_taught_hr need check decimal
					if (Double.isNaN(colistJsonObj.optDouble("crse_co_taught_hr")) || Double.compare(colistJsonObj.optDouble("crse_co_taught_hr"), 0) < 0) {
						throw new InvalidParameterException("Co-List Co-taught hour is required / invalid");
					}
					// crse_total_hr need check decimal
					if (Double.isNaN(colistJsonObj.optDouble("crse_total_hr")) || Double.compare(colistJsonObj.optDouble("crse_total_hr"), 0) < 0) {
						throw new InvalidParameterException("Co-List Total hour is required / invalid");
					}
				}
			}
		}
	}
	
	private void validateElApplElType(JSONArray elTypeJsonList) throws Exception {
		if (elTypeJsonList == null || elTypeJsonList.length() == 0) {
			throw new InvalidParameterException("Extra Load Tpye is required"); 
		}
		for (int i=0; i< elTypeJsonList.length(); i++) {
			JSONObject jsonObj = elTypeJsonList.getJSONObject(i);
			// el_type_id
			if (GeneralUtil.initBlankString(jsonObj.optString("el_type_id")).isBlank()) {
				throw new InvalidParameterException("Extra Load Type is required");
			}
			// el_type_descr
			if (GeneralUtil.initBlankString(jsonObj.optString("el_type_descr")).isBlank()) {
				throw new InvalidParameterException("Extra Load Descriptions is required");
			}
			// stdt_enrolled
//			if (GeneralUtil.initBlankString(jsonObj.optString("stdt_enrolled")).isBlank()) {
//				throw new InvalidParameterException("No. of Student Enrolled is required");
//			}
			if(!GeneralUtil.initBlankString(jsonObj.optString("stdt_enrolled")).isBlank())
				if (!Pattern.matches("^\\d+$", jsonObj.optString("stdt_enrolled").trim())) {
					throw new InvalidParameterException("No. of Student Enrolled is invalid");
				}
			// pymt_amt need check decimal
			if (Double.isNaN(jsonObj.optDouble("pymt_amt")) || Double.compare(jsonObj.optDouble("pymt_amt"), 0) < 0) {
				throw new InvalidParameterException("Extra Load Amount is required");
			}
		}
	}
	
	
//	private void validateOW2Attachments(int ow2AttachedInt, JSONArray attachmentJsonList) throws Exception {
//		if(ow2AttachedInt == 1 && (attachmentJsonList == null || attachmentJsonList.length() == 0)) {
//			throw new InvalidParameterException("Approved OW-2 Form is not attached");
//		}
//	}
	
	private void validateElApplPymtMethodAndSchedule(JSONObject inputJson) throws Exception {
		JSONObject pymtMethod = inputJson.optJSONObject("el_appl_pymt_method");
		
		if (pymtMethod == null || pymtMethod.optString("pymt_type_cde").isBlank()) {
			throw new InvalidParameterException("Payment Method is required");
		}
		
		JSONArray pymtScheduleArr = pymtMethod.optJSONArray("pymt_schedule");
		
		if (pymtScheduleArr == null || pymtScheduleArr.length() == 0) {
			throw new InvalidParameterException("Payment Schedule is required"); 
		}
		JSONArray coaLineJsonArr = new JSONArray();
		JSONArray coaLineJsonArrWithBCO = new JSONArray();
		
		JSONArray coaLineForPjPreiodChecking = new JSONArray();
		JSONArray jsonArrForPymtDatChcking = new JSONArray();

		BigDecimal checkingTotalAmount = BigDecimal.ZERO;
		for (int i=0; i< pymtScheduleArr.length(); i++) {
			JSONObject pymtSchedulObj = pymtScheduleArr.getJSONObject(i);
			// el_type_id
			// pymt_type_cde
			
			// Payment Schedule
			String pymtTypeCde = pymtMethod.optString("pymt_type_cde");
			switch(pymtTypeCde) {
				case PymtTypeConstants.ONETIME:
					// pymt_start_dt
					if (pymtSchedulObj.optLong("pymt_start_dt") == 0) {
						throw new InvalidParameterException("Please select valid payment date");
					}
					break;
				case PymtTypeConstants.INSTALM:
					// pymt_start_dt
					if (pymtSchedulObj.optLong("pymt_start_dt") == 0) {
						throw new InvalidParameterException("Please select valid payment date");
					}
					break;
				case PymtTypeConstants.RECURR:
					// pymt_start_dt
					if (pymtSchedulObj.optLong("pymt_start_dt") == 0 || pymtSchedulObj.optLong("pymt_end_dt") == 0) {
						throw new InvalidParameterException("Please select valid payment period");
					}
					break;
				default:
					throw new InvalidParameterException("Payment Method is invalid");
			}
			
			Long pymtStartDt = pymtSchedulObj.optLong("pymt_start_dt");
			Long pymtEndDt = pymtSchedulObj.optLong("pymt_end_dt");
			
			// for pymt dat checking
			JSONObject objForPymtDatChking = new JSONObject();
			objForPymtDatChking.put("sched_no", i+1)
							.put("pymt_type", pymtTypeCde)
							.put("pymt_start_dt", pymtStartDt)
							.put("pymt_end_dt", pymtStartDt)
							.put("type", "salary");
			
			jsonArrForPymtDatChcking.put(objForPymtDatChking);
			
			JSONArray pymtDetails = pymtSchedulObj.optJSONArray("details");
			for (int k=0; k < pymtDetails.length(); k++) {
				JSONObject detailObj = pymtDetails.getJSONObject(k);
				// proj_id && dept_id
				if (GeneralUtil.initBlankString(detailObj.optString("proj_id")).isBlank() && GeneralUtil.initBlankString(detailObj.optString("dept_id")).isBlank()) {
					throw new InvalidParameterException("Either Project Id or Department is required");
				}
				if (!GeneralUtil.initBlankString(detailObj.optString("proj_id")).isBlank() && !GeneralUtil.initBlankString(detailObj.optString("dept_id")).isBlank()) {
					throw new InvalidParameterException("Only one of Project Id or Department is required");
				}
				// acct_cde
				if (GeneralUtil.initBlankString(detailObj.optString("acct_cde")).isBlank()) {
					throw new InvalidParameterException("Account Code is required");
				}
				// analysis_cde
				if (GeneralUtil.initBlankString(detailObj.optString("analysis_cde")).isBlank()) {
					throw new InvalidParameterException("Analysis Code is required");
				}
				// fund_cde
				if (GeneralUtil.initBlankString(detailObj.optString("fund_cde")).isBlank()) {
					throw new InvalidParameterException("Fund Code is required");
				}
				// bco_aprv_id
				if (GeneralUtil.initBlankString(detailObj.optString("bco_aprv_id")).isBlank()) {
					throw new InvalidParameterException("BCO Approver User Id is required");
				}
				// bco_aprv_name
				if (GeneralUtil.initBlankString(detailObj.optString("bco_aprv_name")).isBlank()) {
					throw new InvalidParameterException("BCO Approver User Name is required");
				}
				// pymt_amt need check decimal
				if (Double.isNaN(detailObj.optDouble("pymt_amt")) || Double.compare(detailObj.optDouble("pymt_amt"), 0) < 0) {
					throw new InvalidParameterException("Payment Amount is invalid");
				}
				
				// Add to arr for later calling API
				if(pymtMethod.optString("pymt_type_cde").equals(PymtTypeConstants.RECURR)) {
					JSONObject newObj = new JSONObject();
					BigDecimal checkingAmt = generalApiService.calculateCOARecurrentTotalAmount(pymtSchedulObj.optLong("pymt_start_dt"), pymtSchedulObj.optLong("pymt_end_dt"), new BigDecimal(detailObj.optString("pymt_amt","0")));
					newObj.put("proj_id", detailObj.optString("proj_id"))
						  .put("dept_id", detailObj.optString("dept_id"))
						  .put("acct_cde", detailObj.optString("acct_cde"))
						  .put("analysis_cde", detailObj.optString("analysis_cde"))
						  .put("fund_cde", detailObj.optString("fund_cde"))
						  .put("bco_aprv_id", detailObj.optString("bco_aprv_id"))
						  .put("bco_aprv_name", detailObj.optString("bco_aprv_name"))
						  .put("pymt_amt", checkingAmt)
						  ;
					checkingTotalAmount = checkingTotalAmount.add(checkingAmt);
					coaLineJsonArr.put(newObj);
					coaLineJsonArrWithBCO.put(newObj);
				}else {
					checkingTotalAmount = checkingTotalAmount.add(new BigDecimal(detailObj.optString("pymt_amt", "0")).setScale(2));
					coaLineJsonArr.put(detailObj);
					coaLineJsonArrWithBCO.put(detailObj);
				}
				
				// Add to JSONArray for project period checking
				JSONObject objForPjPeriodCk = new JSONObject();
				objForPjPeriodCk.put("sched_no", i+1)
								.put("sched_line", k+1)
								.put("pymt_start_dt", pymtStartDt)
								.put("pymt_end_dt", pymtStartDt)
								.put("proj_id", detailObj.optString("proj_id"))
								.put("type", "salary");
				
				coaLineForPjPreiodChecking.put(objForPjPeriodCk);
								
			}
		}
		BigDecimal checkingMethodAmount = new BigDecimal(pymtMethod.optString("pymt_amt", "0")).setScale(2);
		if(!checkingMethodAmount.equals(checkingTotalAmount)) {
			throw new InvalidParameterException("Payment Total Amount is invalid, please update any COA amount and try again.");
		}
		
		JSONArray mpfJson = inputJson.getJSONArray("el_appl_mpf");
		
		for (int j=0; j< mpfJson.length(); j++) {
			JSONObject detailObj = mpfJson.getJSONObject(j);
			// proj_id && dept_id
			if (GeneralUtil.initBlankString(detailObj.optString("proj_id")).isBlank() && GeneralUtil.initBlankString(detailObj.optString("dept_id")).isBlank()) {
				throw new InvalidParameterException("Either Project Id or Department is required");
			}
			if (!GeneralUtil.initBlankString(detailObj.optString("proj_id")).isBlank() && !GeneralUtil.initBlankString(detailObj.optString("dept_id")).isBlank()) {
				throw new InvalidParameterException("Only one of Project Id or Department is required");
			}
			// acct_cde
			if (GeneralUtil.initBlankString(detailObj.optString("acct_cde")).isBlank()) {
				throw new InvalidParameterException("Account Code is required");
			}
			// analysis_cde
			if (GeneralUtil.initBlankString(detailObj.optString("analysis_cde")).isBlank()) {
				throw new InvalidParameterException("Analysis Code is required");
			}
			// fund_cde
			if (GeneralUtil.initBlankString(detailObj.optString("fund_cde")).isBlank()) {
				throw new InvalidParameterException("Fund Code is required");
			}
			// bco_aprv_id
			if (GeneralUtil.initBlankString(detailObj.optString("bco_aprv_id")).isBlank()) {
				throw new InvalidParameterException("BCO Approver User Id is required");
			}
			// bco_aprv_name
			if (GeneralUtil.initBlankString(detailObj.optString("bco_aprv_name")).isBlank()) {
				throw new InvalidParameterException("BCO Approver User Name is required");
			}
			// pymt_amt need check decimal
			if (Double.isNaN(detailObj.optDouble("pymt_amt")) || Double.compare(detailObj.optDouble("pymt_amt"), 0) < 0) {
				throw new InvalidParameterException("Payment Amount is invalid");
			}
			// Add to arr for later calling API
			 coaLineJsonArr.put(detailObj);
			 
			 Long pymtStartDt = detailObj.optLong("pymt_dt");
			 
			// Add to JSONArray for project period checking
			JSONObject objForPjPeriodCk = new JSONObject();
			objForPjPeriodCk.put("sched_no", j + 1)
							.put("sched_line", j)
							.put("pymt_start_dt", pymtStartDt)
							.put("pymt_end_dt", pymtStartDt)
							.put("proj_id", detailObj.optString("proj_id"))
							.put("type", "mpf");
			
			coaLineForPjPreiodChecking.put(objForPjPeriodCk);
		}
		
		// validate pymt date
		validatePaymentDate(jsonArrForPymtDatChcking);
		
		// validate project period vs pymt date
		validateProjectPreiod(coaLineForPjPreiodChecking);
		
		// TODO: MPF also check apprvs after bco info input
		validateApplicationApprvs(coaLineJsonArrWithBCO);
		
		//validateApplicationApprvs(coaLineJsonArr);
		
		JSONArray budgetCheckResult = generalApiService.getBudgetCheckResult(coaLineJsonArr);
		
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
				errMsgList.add(errMsg);
				if("01".equals(lineResult.getString("COAlineErrCode")) || "02".equals(lineResult.getString("COAlineErrCode")) || "04".equals(lineResult.getString("COAlineErrCode"))) {
					throwErrMsgList.add(errMsg);
				}
			}

			if ("W".equals(lineResult.getString("COAlineResult"))) {
				String warningMsg = "Line " + lineResult.get("lineRef") + ": " + lineResult.get("COAlineErrMsg");
				warningList.add(warningMsg);
			}
		}
		
//		if (errMsgList.size() > 0) { Jira 45
//			throw new InvalidParameterException("Budget Check Error: \n " + String.join(" \n ", errMsgList));
//		}
		if (throwErrMsgList.size() > 0) {
			throw new InvalidParameterException("Budget Check Error: \n " + String.join(" \n ", throwErrMsgList));
		}
	}
	
	// 20231229 : #397 add checking on dept head approver
	private void validateDeptHeadApprover(String applUserId, JSONArray aprvStatusJsonList) throws Exception {
		
		List<String> deptHeadUserList = new ArrayList();
		List<String> applicantDeptIdList = new ArrayList();
		
		// find all dept head approver
		for (int i=0; i< aprvStatusJsonList.length(); i++) {
			JSONObject jsonObj = aprvStatusJsonList.getJSONObject(i);
			if (AprvTypeConstants.DEPT_HEAD.equals(jsonObj.optString("aprv_type_cde"))) {
				deptHeadUserList.add(jsonObj.optString("aprv_user_id"));
			}
		}
		
		if(deptHeadUserList.size() > 0) {
			
			// find user dept ids
			JSONArray applUserStaffData = generalApiService.getStaffListByUserId(applUserId, "applicant");
			if (applUserStaffData.length() == 0) {
				throw new InvalidParameterException("Applicant staff info is not found");
			}
			
			for (int i=0; i< applUserStaffData.length(); i++) {
				JSONObject jsonObj = applUserStaffData.getJSONObject(i);
				applicantDeptIdList.add(jsonObj.optString("dept_id"));
			}
			
			for (String deptHeadUserId : deptHeadUserList) {
				// for each approver, check dept head approval right in the user dept(s)
				boolean haveDeptAprvRight = false;
				for (String userDeptId : applicantDeptIdList) {
					List<ElBcoAprvVDAO> list = elBcoAprvVRepository.findDeptHeadApprvRightByDeptIdAndUserId(userDeptId, deptHeadUserId);
					if(list.size() > 0) {
						haveDeptAprvRight = true;
						break;
					}
				}
				
				if(!haveDeptAprvRight) {
					throw new InvalidParameterException("Selected Approver(s) do not have department head approval right.");
				}
			}
		}
	}
	
	public void validateProjectPreiod(JSONArray pymtJsonList) throws Exception {
		
		String check_msg = " ";
		
		for (int i=0; i< pymtJsonList.length(); i++) {
			JSONObject jsonObj = pymtJsonList.getJSONObject(i);
			
			String schedNo = jsonObj.getString("sched_no");
			String schedLine = jsonObj.getString("sched_line");
			
			Long pymtStartDatLong =  jsonObj.getLong("pymt_start_dt");
			Long pymtEndDatLong =  jsonObj.getLong("pymt_end_dt");
			
			String type = jsonObj.getString("type");
			
			Timestamp pymtStartDat = GeneralUtil.initNullTimestampFromLong(pymtStartDatLong);
			Timestamp pymtEndDat = GeneralUtil.initNullTimestampFromLong(pymtEndDatLong);
			
			log.info(GeneralUtil.convertTimestampToString(pymtStartDat, "D"));
			
			String pjId = jsonObj.getString("proj_id");
			
			if(!GeneralUtil.initBlankString(pjId).equals(" ")) {
				// check pj period
				ElProjChrtVDAO pjDao = elProjChrtVRepository.findByProjId(pjId);
				
				if(pjDao == null) {
					throw new InvalidParameterException("Invaild Project Id: " + pjId);
				}
				else {
					Timestamp pjStartDt = pjDao.getStartDt();
					Timestamp pjEndDt = pjDao.getEndDt();
					
					// check pymt date within the pj dat
					if(!(pymtStartDat.compareTo(pjStartDt) >= 0 && pymtEndDat.compareTo(pjEndDt) <= 0)) {
						String pjStartDtStr = GeneralUtil.convertTimestampToString(pjStartDt, "D");
						String pjEndDtStr = GeneralUtil.convertTimestampToString(pjEndDt, "D");
						
						String msg = "";
						
						if(type.equals("salary"))
							msg = schedNo + "." + schedLine + ": The payment date is not within the project period (" + pjStartDtStr + " - " + pjEndDtStr + ")";
						else
							msg = "MPF line " + schedNo + ": The payment date is not within the project period (" + pjStartDtStr + " - " + pjEndDtStr + ")";
						
						if(" ".equals(check_msg))
							check_msg = msg;
						else
							check_msg = check_msg + "\n" + msg;
					}
				}
			}
		}  
		
		if(!" ".equals(check_msg))
			throw new InvalidParameterException("Project Period Checking: \n" + check_msg);
	}
	
	public void validatePaymentDate(JSONArray pymtJsonList) throws Exception {
		
		String check_msg = " ";
		Timestamp currentDat = GeneralUtil.getCurrentTimestamp();
		currentDat = GeneralUtil.getTimestamp(currentDat.getYear() + 1900, currentDat.getMonth() + 1, currentDat.getDate());
		
		log.debug("currentTime" + currentDat);
		
		for (int i=0; i< pymtJsonList.length(); i++) {
			JSONObject jsonObj = pymtJsonList.getJSONObject(i);
			
			String schedNo = jsonObj.getString("sched_no");
			
			Long pymtStartDatLong =  jsonObj.getLong("pymt_start_dt");
			Long pymtEndDatLong =  jsonObj.getLong("pymt_end_dt");
			
			String type = jsonObj.getString("type");
			String pymtType = jsonObj.getString("pymt_type");
			
			Timestamp pymtStartDat = GeneralUtil.initNullTimestampFromLong(pymtStartDatLong);
			Timestamp pymtEndDat = GeneralUtil.initNullTimestampFromLong(pymtEndDatLong);
			
			log.info(GeneralUtil.convertTimestampToString(pymtStartDat, "D"));
			
			if(pymtStartDat.before(currentDat) || pymtEndDat.before(currentDat)) {
				String msg = "";
				String pymtStartDtStr = GeneralUtil.convertTimestampToString(pymtStartDat, "D");
				String pymtEndDtStr = GeneralUtil.convertTimestampToString(pymtEndDat, "D");
				
				if(pymtType.equals(PymtTypeConstants.INSTALM))
					msg = "Payment " + schedNo + ": Payment date cannot be in the past (" + pymtStartDtStr + ")";
				else
					msg = "Payment " + schedNo + ": Payment date cannot be in the past (" + pymtStartDtStr + " - " + pymtEndDtStr + ")";
				
				if(" ".equals(check_msg))
					check_msg = msg;
				else
					check_msg = check_msg + "\n" + msg;
			}
		}  
		
		if(!" ".equals(check_msg))
			throw new InvalidParameterException("Payment Date Checking: \n" + check_msg);
	}
	
	/*
	private void validateElApplMPF(JSONArray inputJson) throws Exception {
		if (inputJson == null || inputJson.length() == 0) {
			throw new InvalidParameterException("Payment Details is required"); 
		}
		
		JSONArray coaLineJsonArr = new JSONArray();
		JSONArray coaLineJsonArrWithBCO = new JSONArray();
		for (int i=0; i< inputJson.length(); i++) {
			JSONObject pymtMethodObj = inputJson.getJSONObject(i);
			// el_type_id
			if (pymtMethodObj.optString("el_type_id").isBlank()) {
				throw new InvalidParameterException("Extra Load Type is required");
			}
			// pymt_type_cde
			if (pymtMethodObj.optString("pymt_type_cde").isBlank()) {
				throw new InvalidParameterException("Payment Method is required");
			}
			
			// Payment Schedule
			JSONArray pymtScheduleArr = pymtMethodObj.optJSONArray("pymt_schedule");
			if (pymtScheduleArr == null || pymtScheduleArr.length() == 0) {
				throw new InvalidParameterException("Payment Schedule is required"); 
			}
			
			for (int j=0; j < pymtScheduleArr.length(); j++) {
				JSONObject pymtSchedulObj = pymtScheduleArr.getJSONObject(j);
				String pymtTypeCde = pymtMethodObj.optString("pymt_type_cde");
				
				switch(pymtTypeCde) {
					case PymtTypeConstants.ONETIME:
						// pymt_start_dt
						if (pymtSchedulObj.optLong("pymt_start_dt") == 0) {
							throw new InvalidParameterException("Please select valid payment date");
						}
						break;
					case PymtTypeConstants.INSTALM:
						// pymt_start_dt
						if (pymtSchedulObj.optLong("pymt_start_dt") == 0) {
							throw new InvalidParameterException("Please select valid payment date");
						}
						break;
					case PymtTypeConstants.RECURR:
						// pymt_start_dt
						if (pymtSchedulObj.optLong("pymt_start_dt") == 0 || pymtSchedulObj.optLong("pymt_end_dt") == 0) {
							throw new InvalidParameterException("Please select valid payment period");
						}
						break;
					default:
						throw new InvalidParameterException("Payment Method is invalid");
				}
				JSONArray pymtDetails = pymtSchedulObj.optJSONArray("details");
				for (int k=0; k < pymtScheduleArr.length(); k++) {
					JSONObject detailObj = pymtDetails.getJSONObject(k);
					
					// proj_id && dept_id
					if (detailObj.optString("proj_id").isBlank() && detailObj.optString("dept_id").isBlank()) {
						throw new InvalidParameterException("Either Project Id or Department is required");
					}
					if (!detailObj.optString("proj_id").isBlank() && !detailObj.optString("dept_id").isBlank()) {
						throw new InvalidParameterException("Only one of Project Id or Department is required");
					}
					// acct_cde
					if (detailObj.optString("acct_cde").isBlank()) {
						throw new InvalidParameterException("Account Code is required");
					}
					// analysis_cde
					if (detailObj.optString("analysis_cde").isBlank()) {
						throw new InvalidParameterException("Analysis Code is required");
					}
					// fund_cde
					if (detailObj.optString("fund_cde").isBlank()) {
						throw new InvalidParameterException("Fund Code is required");
					}
					// bco_aprv_id
					if (detailObj.optString("bco_aprv_id").isBlank()) {
						throw new InvalidParameterException("BCO Approver User Id is required");
					}
					// bco_aprv_name
					if (detailObj.optString("bco_aprv_name").isBlank()) {
						throw new InvalidParameterException("BCO Approver User Name is required");
					}
					// pymt_amt
					if (Double.isNaN(detailObj.optDouble("pymt_amt"))) {
						throw new InvalidParameterException("Payment Amount is invalid");
					}
					
					// Add to arr for later calling API
					coaLineJsonArr.put(detailObj);
					coaLineJsonArrWithBCO.put(detailObj);
				}
			}
			JSONArray mpfJson = pymtMethodObj.getJSONArray("el_appl_mpf_details");
			for (int j=0; j< mpfJson.length(); j++) {
				JSONObject detailObj = mpfJson.getJSONObject(j);

				// proj_id && dept_id
				if (detailObj.optString("proj_id").isBlank() && detailObj.optString("dept_id").isBlank()) {
					throw new InvalidParameterException("Either Project Id or Department is required");
				}
				if (!detailObj.optString("proj_id").isBlank() && !detailObj.optString("dept_id").isBlank()) {
					throw new InvalidParameterException("Only one of Project Id or Department is required");
				}
				// acct_cde
				if (detailObj.optString("acct_cde").isBlank()) {
					throw new InvalidParameterException("Account Code is required");
				}
				// analysis_cde
				if (detailObj.optString("analysis_cde").isBlank()) {
					throw new InvalidParameterException("Analysis Code is required");
				}
				// fund_cde
				if (detailObj.optString("fund_cde").isBlank()) {
					throw new InvalidParameterException("Fund Code is required");
				}
				// Add to arr for later calling API
				coaLineJsonArr.put(detailObj);
			}
		}
		validateApplicationApprvs(coaLineJsonArrWithBCO);
		

		JSONArray budgetCheckResult = generalApiService.getBudgetCheckResult(coaLineJsonArr);
		
		if (budgetCheckResult.length() == 0) {
			throw new Exception("Error when budget check");
		}
		List<String> errMsgList = new ArrayList<>();
		
		for (int i=0; i<budgetCheckResult.length(); i++) {
			JSONObject lineResult = budgetCheckResult.getJSONObject(i);
			log.debug("lineResult: {}", lineResult);
			
			if ("N".equals(lineResult.getString("COAlineResult"))) {
				String errMsg = "Line " + lineResult.get("lineRef") + ": " + lineResult.get("COAlineErrMsg");
				errMsgList.add(errMsg);
			}
		}
		
		if (errMsgList.size() > 0) {
			throw new InvalidParameterException("Budget Check Error: \n " + String.join(" \n ", errMsgList));
		}

	}
	*/
	
//	private void validateElApplBudget(JSONArray inputJson) throws Exception {
//		if (inputJson == null || inputJson.length() == 0) {
//			throw new InvalidParameterException("Budget Fund Details is required"); 
//		}
//		
//		// for COA Line Checking via API
//		JSONArray coaLineJsonArr = new JSONArray();
//		for (int i=0; i< inputJson.length(); i++) {
//			JSONObject jsonObj = inputJson.getJSONObject(i);
//			// el_type_id
//			if (jsonObj.optString("el_type_id").isBlank()) {
//				throw new InvalidParameterException("Extra Load Type is required");
//			}
//			
//			JSONArray detailsArr = jsonObj.optJSONArray("details");
//			for (int j=0; j< detailsArr.length(); j++) {
//				JSONObject detailObj = detailsArr.getJSONObject(j);
//				// TODO: Either dept_id or proj_id
//				if (detailObj.optString("proj_id").isBlank()) {
//					throw new InvalidParameterException("Project Id is required");
//				}
//				// acct_cde
//				if (detailObj.optString("acct_cde").isBlank()) {
//					throw new InvalidParameterException("Account Code is required");
//				}
//				// analysis_cde
//				if (detailObj.optString("analysis_cde").isBlank()) {
//					throw new InvalidParameterException("Analysis Code is required");
//				}
//				// fund_cde
//				if (detailObj.optString("fund_cde").isBlank()) {
//					throw new InvalidParameterException("Fund Code is required");
//				}
//				// TODO: review if compute in back end budg_acct_share
//				if (Double.isNaN(detailObj.optDouble("budg_acct_share"))) {
//					throw new InvalidParameterException("Budget % of Share is required");
//				}
//				// budg_acct_amt
//				if (Double.isNaN(detailObj.optDouble("budg_acct_amt"))) {
//					throw new InvalidParameterException("Budget Amount is required / invalid");
//				}
//				// bco_aprv_id
//				if (detailObj.optString("bco_aprv_id").isBlank()) {
//					throw new InvalidParameterException("BCO Approver User Id is required");
//				}
//				// bco_aprv_name
//				if (detailObj.optString("bco_aprv_name").isBlank()) {
//					throw new InvalidParameterException("BCO Approver User Name is required");
//				}
//				
//				// Add to arr for later calling API
//				coaLineJsonArr.put(detailObj);
//			}
//		} 
//		
//		// Budget Check by calling FMS API
//		// Temp block for development
//		if (true) {
//			return;
//		}
//		JSONArray budgetCheckResult = generalApiService.getBudgetCheckResult(coaLineJsonArr);
//		
//		if (budgetCheckResult.length() == 0) {
//			throw new Exception("Error when budget check");
//		}
//		List<String> errMsgList = new ArrayList<>();
//		
//		for (int i=0; i<budgetCheckResult.length(); i++) {
//			JSONObject lineResult = budgetCheckResult.getJSONObject(i);
//			log.debug("lineResult: {}", lineResult);
//			
//			if ("N".equals(lineResult.getString("COAlineResult"))) {
//				String errMsg = "Line " + lineResult.get("lineRef") + ": " + lineResult.get("COAlineErrMsg");
//				errMsgList.add(errMsg);
//			}
//		}
//		
//		if (errMsgList.size() > 0) {
//			throw new InvalidParameterException("Budget Check Error: \n " + String.join(" \n ", errMsgList));
//		}
//		
//		// Call API to validate BCOs
//		validateApplicationApprvs(coaLineJsonArr);
//	}
	
	private void sendEmailToApplicantToAcceptApplication(String applHdrId, String opPageName) throws Exception {
		String webAppURLPrefix = env.getProperty(AppConstants.PATH_APP_PREFIX);
		
		ElApplHdrDAO elApplHdrDAO = elApplHdrRepository.findOne(applHdrId);
		
		String applicantName = elApplHdrDAO.getApplUserName();
		String applicantId = elApplHdrDAO.getApplUserId();
		String applNbr = elApplHdrDAO.getApplNbr();
		
		String emailFrom = " ";
		String emailTo = applicantId + "@ust.hk";
		String subject = "Pending Declaration for Extra Load Application (Appl no. ${applNbr})".replace("${applNbr}", applNbr);
		String content = 
				"Dear " + applicantName + ", " + "<br>"
				+ "<br>" 
				+ "An extra load application (Appl no. ${applNbr}) is ";
		
		if(generalApiService.checkIsRejected(applHdrId)) {
			content += "resubmitted and ";
		}
		
		content += "pending for your declaration. Please click the following link to take action:" + "<br>"
				+ "<br>"
				+ GeneralUtil.createHyperLinks(webAppURLPrefix + "/extra_load_request/approve?id=" + applHdrId)
				+ "<br><br>"
				;
		
		content = content.replace("${applNbr}", applNbr);
		
		ElEmNotificationDAO elEmNotificationDAO = new ElEmNotificationDAO();
		
		elEmNotificationDAO.setApplHdrId(applHdrId);;
		elEmNotificationDAO.setEmailFrom(emailFrom);
		elEmNotificationDAO.setEmailTo(emailTo);
		elEmNotificationDAO.setSubject(subject);
		elEmNotificationDAO.setContent(content);
		
		String remoteUser = SecurityUtils.getCurrentLogin();
		elEmNotificationDAO.setCreatUser(remoteUser);
		elEmNotificationDAO.setChngUser(remoteUser);
		elEmNotificationDAO.setOpPageNam(opPageName);
		
		elEmNotificationRepository.save(elEmNotificationDAO);
	}
	
	private void createMyAiTaskForApplicantDeclar(String applHdrId) throws Exception {
		String webAppURLPrefix = env.getProperty(AppConstants.PATH_APP_PREFIX);
		
		ElApplHdrDAO elApplHdrDAO = elApplHdrRepository.findOne(applHdrId);
		
		String applicantId = elApplHdrDAO.getApplUserId();
		String applNbr = elApplHdrDAO.getApplNbr();
		
		String subject = "Pending Declaration / Approval for Extra Load Application (Appl no. ${applNbr})".replace("${applNbr}", applNbr)
				 + " (Applicant : " + elApplHdrDAO.getApplUserName() + ")";
		try {
			JSONObject params = new JSONObject();
			params.put("request_id", elApplHdrDAO.getApplNbr());
			params.put("initiator", elApplHdrDAO.getApplRequesterId());
			params.put("action", "Pending Declaration by " + applicantId);
			params.put("receiver", new JSONArray().put(applicantId));
			params.put("message", subject);
			params.put("url", webAppURLPrefix + "/extra_load_request/approve?id=" + applHdrId);

			commonRoutineService.callAIWorkListNotiAPI(params);
		} catch(Exception e) {
			log.error(e.toString());
		}
	}
	private void sendEmailToSubmitterOnApplicantReject(String applHdrId, String opPageName) throws Exception {
		String webAppURLPrefix = env.getProperty(AppConstants.PATH_APP_PREFIX);

		ElApplHdrDAO elApplHdrDAO = elApplHdrRepository.findOne(applHdrId);
		
		String applicantName = elApplHdrDAO.getApplUserName();
		String applicantId = elApplHdrDAO.getApplUserId();
		String requestorName = elApplHdrDAO.getApplRequesterName();
		String requestoerId = elApplHdrDAO.getApplRequesterId();
		
		String applNbr = elApplHdrDAO.getApplNbr();
		
		String emailFrom = " ";
		String emailTo = requestoerId + "@ust.hk";
		String emailCc = applicantId + "@ust.hk";
		String subject = "Extra Load Application (Appl no. ${applNbr}) Rejected".replace("${applNbr}", applNbr);
		String content = 
				"Dear " + requestorName + ", " + "<br>"
				+ "<br>"
				+ "An extra load application (Appl no. ${applNbr}) is rejected." + "<br>"
				+ "<br>"
				+ "Rejector role: Applicant" + "<br>"
				+ "<br>"
				+ " Please click the following link to take action:" + "<br>"
				+ "<br>"
				+ GeneralUtil.createHyperLinks(webAppURLPrefix + "/extra_load_request/edit?id=" + applHdrId)
				+ "<br><br>"
				;
		
		content = content.replace("${applNbr}", applNbr);
		
		ElEmNotificationDAO elEmNotificationDAO = new ElEmNotificationDAO();
		
		elEmNotificationDAO.setApplHdrId(applHdrId);;
		elEmNotificationDAO.setEmailFrom(emailFrom);
		elEmNotificationDAO.setEmailTo(emailTo);
		elEmNotificationDAO.setEmailCc(emailCc);
		elEmNotificationDAO.setSubject(subject);
		elEmNotificationDAO.setContent(content);
		
		String remoteUser = SecurityUtils.getCurrentLogin();
		elEmNotificationDAO.setCreatUser(remoteUser);
		elEmNotificationDAO.setChngUser(remoteUser);
		elEmNotificationDAO.setOpPageNam(opPageName);
		
		elEmNotificationRepository.save(elEmNotificationDAO);
	}
	
	private void sendEmailToPendingAprv(String applHdrId, String opPageName) throws Exception {
		String webAppURLPrefix = env.getProperty(AppConstants.PATH_APP_PREFIX);
		ElApplAprvStatusDAO elApplAprvStatusDAO = elApplAprvStatusRepository.findNextAprv(applHdrId);
		ElApplHdrDAO elApplHdrDAO = elApplHdrRepository.findOne(applHdrId);
		
		if (elApplAprvStatusDAO == null) {
			throw new RecordNotExistException("Pending Approver");
		}
		
		List<String> skipUserList = generalApiService.findBatchApprovalDailyEmailUserList();
		if(skipUserList.contains(elApplAprvStatusDAO.getAprvUserId())) {
			log.debug("Skip email to {}", elApplAprvStatusDAO.getAprvUserId());
			return;
		}
		
		String aprvUserId = elApplAprvStatusDAO.getAprvUserId();
		String applNbr = elApplHdrDAO.getApplNbr();
		String role = transformAprvTypeDesc(elApplAprvStatusDAO);
		
		String emailFrom = " ";
		String emailTo = aprvUserId + "@ust.hk";
		String subject = "Pending Approval for Extra Load Application (Appl no. ${applNbr})".replace("${applNbr}", applNbr);
		String content = 
				"Dear " + aprvUserId + ", " + "<br>"
				+ "<br>" 
				+ "An extra load applicaiton (Appl no. ${applNbr}) is ";

		if(generalApiService.checkIsRejected(applHdrId)) {
			content += "resubmitted and ";
		}
		
		content	+= "pending for your approval."
				+ "<br><br>"
				+ "Role : ${role}"
				+ "<br><br>"
				+ "Please click the following link to take action:" + "<br>"
				+ "<br>"
				+ GeneralUtil.createHyperLinks(webAppURLPrefix + "/extra_load_request/approve?id=" + applHdrId)
				+ "<br><br>" 
				;
		
		content = content.replace("${applNbr}", applNbr).replace("${role}", role);
		
		ElEmNotificationDAO elEmNotificationDAO = new ElEmNotificationDAO();
		
		elEmNotificationDAO.setApplHdrId(applHdrId);;
		elEmNotificationDAO.setEmailFrom(emailFrom);
		elEmNotificationDAO.setEmailTo(emailTo);
		elEmNotificationDAO.setSubject(subject);
		elEmNotificationDAO.setContent(content);
		
		String remoteUser = SecurityUtils.getCurrentLogin();
		elEmNotificationDAO.setCreatUser(remoteUser);
		elEmNotificationDAO.setChngUser(remoteUser);
		elEmNotificationDAO.setOpPageNam(opPageName);
		
		elEmNotificationRepository.save(elEmNotificationDAO);
	}
	
	private void createMyAiTaskForPendingAprv(String applHdrId) throws Exception {
		String webAppURLPrefix = env.getProperty(AppConstants.PATH_APP_PREFIX);
		ElApplAprvStatusDAO elApplAprvStatusDAO = elApplAprvStatusRepository.findNextAprv(applHdrId);
		ElApplHdrDAO elApplHdrDAO = elApplHdrRepository.findOne(applHdrId);
		
		if (elApplAprvStatusDAO == null) {
			throw new RecordNotExistException("Pending Approver");
		}
		
		String aprvUserId = elApplAprvStatusDAO.getAprvUserId();
		String applNbr = elApplHdrDAO.getApplNbr();
		String subject = "Pending Declaration / Approval for Extra Load Application (Appl no. ${applNbr})".replace("${applNbr}", applNbr)
				 + " (Applicant : " + elApplHdrDAO.getApplUserName() + ")";
		
		try {
			JSONObject params = new JSONObject();
			params.put("request_id", elApplHdrDAO.getApplNbr());
			params.put("initiator", elApplHdrDAO.getApplRequesterId());
			params.put("action", "Pending Approval by " + aprvUserId);
			params.put("receiver", new JSONArray().put(aprvUserId));
			params.put("message", subject);
			params.put("url", webAppURLPrefix + "/extra_load_request/approve?id=" + applHdrId);

			commonRoutineService.callAIWorkListNotiAPI(params);
		} catch(Exception e) {
			log.error(e.toString());
		}
	}
	
	private void createMyAiTaskForPendingIntegration(String applHdrId) throws Exception {
		ElApplHdrDAO elApplHdrDAO = elApplHdrRepository.findOne(applHdrId);
		
		String applNbr = elApplHdrDAO.getApplNbr();
		String subject = "Extra Load Application (Appl no. ${applNbr})".replace("${applNbr}", applNbr)
				 + " (Applicant : " + elApplHdrDAO.getApplUserName() + ")";
		
		try {
			JSONObject params = new JSONObject();
			params.put("request_id", elApplHdrDAO.getApplNbr());
			params.put("initiator", elApplHdrDAO.getApplRequesterId());
			params.put("action", "Pending Integration");
			params.put("message", subject);

			commonRoutineService.callAIWorkListNotiAPI(params);
		} catch(Exception e) {
			log.error(e.toString());
		}
		
	}

	private void sendEmailToSubmitterOnApproverReject(String applHdrId, String opPageName) throws Exception {
		String webAppURLPrefix = env.getProperty(AppConstants.PATH_APP_PREFIX);

		ElApplAprvStatusDAO elApplAprvStatusDAO = elApplAprvStatusRepository.findRecentRejectorByApplHdrId(applHdrId);
		ElApplHdrDAO elApplHdrDAO = elApplHdrRepository.findOne(applHdrId);
		
		String requestorName = elApplHdrDAO.getApplRequesterName();
		String requestoerId = elApplHdrDAO.getApplRequesterId();
		String role = transformAprvTypeDesc(elApplAprvStatusDAO);
		
		String applNbr = elApplHdrDAO.getApplNbr();
		
		String emailFrom = " ";
		String emailTo = requestoerId + "@ust.hk";
		String subject = "Extra Load Application (Appl no. ${applNbr}) Rejected".replace("${applNbr}", applNbr);
		String content = 
				"Dear " + requestorName + ", " + "<br>"
				+ "<br>"
				+ "An extra load application (Appl no. ${applNbr}) is rejected." + "<br>"
				+ "<br>"
				+ "Rejector role : ${role}" + "<br>"
				+ "<br>"
				+ "Please click the following link to take action:" + "<br>"
				+ "<br>"
				+ GeneralUtil.createHyperLinks(webAppURLPrefix + "/extra_load_request/edit?id=" + applHdrId)
				+ "<br><br>"
				;
		
		content = content.replace("${applNbr}", applNbr).replace("${role}", role);
		
		ElEmNotificationDAO elEmNotificationDAO = new ElEmNotificationDAO();
		
		elEmNotificationDAO.setApplHdrId(applHdrId);
		elEmNotificationDAO.setEmailFrom(emailFrom);
		elEmNotificationDAO.setEmailTo(emailTo);
		elEmNotificationDAO.setSubject(subject);
		elEmNotificationDAO.setContent(content);
		
		String remoteUser = SecurityUtils.getCurrentLogin();
		elEmNotificationDAO.setCreatUser(remoteUser);
		elEmNotificationDAO.setChngUser(remoteUser);
		elEmNotificationDAO.setOpPageNam(opPageName);
		
		elEmNotificationRepository.save(elEmNotificationDAO);
	}
	
	private String transformAprvTypeDesc(ElApplAprvStatusDAO elApplAprvStatusDAO) {
		String role;
		switch(elApplAprvStatusDAO.getAprvTypeCde()) {
			case AprvTypeConstants.AD_HOC:
				role = "Ad hoc approver" ;
				break;
			case AprvTypeConstants.BCO_APPL:
			case AprvTypeConstants.BCO_PYMT:
				role = "Budget Control Officer" ;
				break;
			case AprvTypeConstants.DEPT_HEAD:
				role = "Department Head" ;
				break;
			case AprvTypeConstants.PROVOST:
				role = "Provost Office" ;
				break;
			default:
				role = "" ;
				break;
		}
		return role;
	}
	
	public String getApplWorkflowType(String jobCatgCode) {
		String type;
		
		switch(jobCatgCode) {
		case "A010":
		case "A040":
			type = "A" ;
			break;
		case "A020":
		case "A030":
		case "R020":
		case "I020":
			type = "B" ;
			break;
		default:
			type = " " ;
			break;
		}
		return type;
	}
	
	private void Switch(String aprvTypeCde) {
		// TODO Auto-generated method stub
		
	}

	private void sendEmailToAprvForCancelledAppl(String applHdrId, String opPageName) throws Exception {
		// find approved approver and current pending approver
		List<ElApplAprvStatusDAO> elApplAprvStatusDAOList = elApplAprvStatusRepository.findApprovedAndPendingAprv(applHdrId);
		ElApplHdrDAO elApplHdrDAO = elApplHdrRepository.findOne(applHdrId);
		
		if (elApplAprvStatusDAOList.size() == 0) {
			return;
		}
		
		String applNbr = elApplHdrDAO.getApplNbr();
		
		List<ElEmNotificationDAO> elEmNotificationDAOList = new ArrayList<>();
		
		for (ElApplAprvStatusDAO elApplAprvStatusDAO : elApplAprvStatusDAOList) {
			String aprvUserId = elApplAprvStatusDAO.getAprvUserId();
			String role = transformAprvTypeDesc(elApplAprvStatusDAO);
			
			String emailFrom = " ";
			String emailTo = aprvUserId + "@ust.hk";
			String subject = "Extra Load Application (Appl no. ${applNbr}) Cancelled".replace("${applNbr}", applNbr);
			String content = 
					"Dear " + aprvUserId + ", " + "<br>"
					+ "<br>" 
					+ "An extra load application (Appl no. ${applNbr}) is cancelled." 
					+ "<br><br>"
					+ "Role : ${role}"
					+ "<br><br>"
					;
			content = content.replace("${applNbr}", applNbr).replace("${role}", role);

			ElEmNotificationDAO elEmNotificationDAO = new ElEmNotificationDAO();
			
			elEmNotificationDAO.setApplHdrId(applHdrId);
			elEmNotificationDAO.setEmailFrom(emailFrom);
			elEmNotificationDAO.setEmailTo(emailTo);
			elEmNotificationDAO.setSubject(subject);
			elEmNotificationDAO.setContent(content);
			
			String remoteUser = SecurityUtils.getCurrentLogin();
			elEmNotificationDAO.setCreatUser(remoteUser);
			elEmNotificationDAO.setChngUser(remoteUser);
			elEmNotificationDAO.setOpPageNam(opPageName);
			
			elEmNotificationDAOList.add(elEmNotificationDAO);
		}
		
		elEmNotificationRepository.saveAll(elEmNotificationDAOList);
	}
	
	private void sendEmailToAprvForEdittedAppl(String applHdrId, String opPageName) throws Exception {
		ElApplHdrDAO elApplHdrDAO = elApplHdrRepository.findOne(applHdrId);
		// find approved approver and current pending approver
		if(ApplStatusConstants.PENDING.equals(elApplHdrDAO.getApplStatCde())) {
			List<ElApplAprvStatusDAO> elApplAprvStatusDAOList = elApplAprvStatusRepository.findApprovedAndPendingAprv(applHdrId);
			
			if (elApplAprvStatusDAOList.size() == 0) {
				return;
			}
			
			List<ElEmNotificationDAO> elEmNotificationDAOList = new ArrayList<>();
			
			for (ElApplAprvStatusDAO elApplAprvStatusDAO : elApplAprvStatusDAOList) {
				String aprvUserId = elApplAprvStatusDAO.getAprvUserId();
				String applNbr = elApplHdrDAO.getApplNbr();
				String role = transformAprvTypeDesc(elApplAprvStatusDAO);
				
				String emailFrom = " ";
				String emailTo = aprvUserId + "@ust.hk";
				String subject = "Extra Load Application (Appl no. ${applNbr}) Resubmitted".replace("${applNbr}", applNbr);
				String content = 
						"Dear " + aprvUserId + ", " + "<br>"
						+ "<br>"
						+ "An extra load applicaiton (Appl no. ${applNbr}) is resubmitted."
						+ "You will recieve a seperate notification email when it is pending for your approval." + "<br>"
						+ "<br>" 
						+ "Role : ${role}"
						+ "<br><br>"
						;
				
				content = content.replace("${applNbr}", applNbr).replace("${role}", role);

				ElEmNotificationDAO elEmNotificationDAO = new ElEmNotificationDAO();
				
				elEmNotificationDAO.setApplHdrId(applHdrId);
				elEmNotificationDAO.setEmailFrom(emailFrom);
				elEmNotificationDAO.setEmailTo(emailTo);
				elEmNotificationDAO.setSubject(subject);
				elEmNotificationDAO.setContent(content);
				
				String remoteUser = SecurityUtils.getCurrentLogin();
				elEmNotificationDAO.setCreatUser(remoteUser);
				elEmNotificationDAO.setChngUser(remoteUser);
				elEmNotificationDAO.setOpPageNam(opPageName);
				
				elEmNotificationDAOList.add(elEmNotificationDAO);
			}
			
			elEmNotificationRepository.saveAll(elEmNotificationDAOList);
		}
	}


	@Override
	public JSONArray getAllApplsForEnquiry(JSONObject inputJson) throws Exception {
		JSONArray outputJson = new JSONArray();
		String remoteUser = SecurityUtils.getCurrentLogin();
		// TODO: validating User?
		
		
		
		// Get param
//		String applName = GeneralUtil.refineParam(inputJson.optString("appl_name"));
//		String applNbr = GeneralUtil.refineParam(inputJson.optString("appl_nbr"));
//		String prgmTerm = GeneralUtil.refineParam(inputJson.optString("prgm_term"));
//		String schCde = GeneralUtil.refineParam(inputJson.optString("sch_cde"));
//		String prgmCde = GeneralUtil.refineParam(inputJson.optString("prgm_cde"));
//		String applStatCde = inputJson.optString("appl_stat_cde");
		
//		List<String> applStatusList = Arrays.asList(ApplStatusConstants.REMOVED, ApplStatusConstants.COMPLETED);
//		List<String> applStatusList = Arrays.asList(ApplStatusConstants.REMOVED, ApplStatusConstants.COMPLETED, ApplStatusConstants.DRAFT);
		
//		if (!applStatCde.isBlank()) {
//			applStatusList = applStatusList.stream().filter(e -> e.equals(applStatCde)).collect(Collectors.toList());
//		}
		
//		Timestamp applDttm = GeneralUtil.NULLTIMESTAMP;
//		Timestamp prgmStartDt = GeneralUtil.NULLTIMESTAMP;
//		Timestamp prgmEndDt = GeneralUtil.INFINTYTIMESTAMP;
		
//		try {
//			applDttm = new Timestamp(inputJson.getLong("appl_dttm"));
//		} catch(Exception e) {
//		}
//		try {
//			prgmStartDt = new Timestamp(inputJson.getLong("prgm_start_dttm"));
//		} catch(Exception e) {
//		}
//		try {
//			prgmEndDt = new Timestamp(inputJson.getLong("prgm_end_dttm"));
//		} catch(Exception e) {
//		}
//		
//		log.debug("remoteUser {}, applName {}, applDttm {}, applNbr {}, prgmTerm {}, schCde {}, prgmCde {}, applStatCde {}, prgmStartDt {}, prgmEndDt {}"
//				,remoteUser, applName, applDttm, applNbr, prgmTerm, schCde, prgmCde, applStatCde, prgmStartDt, prgmEndDt);
		
		
		String applName = GeneralUtil.refineParam(inputJson.optString("appl_name").trim());
		String requesterName = GeneralUtil.refineParam(inputJson.optString("requester_name").trim());
		String applNbr = GeneralUtil.refineParam(inputJson.optString("appl_nbr").trim());
		String dept = GeneralUtil.refineParam(inputJson.optString("dept").trim());
		String applStatCde = GeneralUtil.refineParam(inputJson.optString("appl_stat_cde").trim());
		String brNo = GeneralUtil.refineParam(inputJson.optString("br_no").trim());
		
		List<String> applStatusFilterList = Arrays.asList(ApplStatusConstants.DRAFT);
		List<Map<String, Object>> resultMapList = elApplHdrRepository.searchApplicationEnquiry(applStatusFilterList, applName, requesterName, applNbr, applStatCde, dept, brNo);
		
		for (Map<String, Object> resultMap : resultMapList) {
			JSONObject jsonObj = new JSONObject();
			outputJson.put(jsonObj);

			for (String keys : resultMap.keySet()) {
				jsonObj.put(keys.toLowerCase(), resultMap.get(keys));
			}
		}
		
		return outputJson;
	}
	
	public JSONArray getFoEnquiryApplicationData(JSONObject inputJson) throws Exception {
		
		String remoteUser = SecurityUtils.getCurrentLogin();
		JSONArray outputJsonArray = new JSONArray();
		
		String applName = GeneralUtil.refineParam(inputJson.optString("appl_name").trim());
		String requesterName = GeneralUtil.refineParam(inputJson.optString("requester_name").trim());
		String applNbr = GeneralUtil.refineParam(inputJson.optString("appl_nbr").trim());
		String dept = GeneralUtil.refineParam(inputJson.optString("dept").trim());
		String applStatCde = GeneralUtil.refineParam(inputJson.optString("appl_stat_cde").trim());
		String brNo = GeneralUtil.refineParam(inputJson.optString("br_no").trim());
		String elTypes = GeneralUtil.refineParam(inputJson.optString("el_type_id").trim());
		String applSchlId = GeneralUtil.refineParam(inputJson.optString("schl_id").trim());
		
		String applFromString = GeneralUtil.initNullString(inputJson.optString("appl_from").trim());
		String applToPlusOneMonthString = GeneralUtil.initNullString(inputJson.optString("appl_to").trim());
		
		Integer startSem = inputJson.optInt("start_sem");
		Integer endSem = inputJson.optInt("end_sem");

		Timestamp applFrom = GeneralUtil.NULLTIMESTAMP;
		Timestamp applToPlusOneMonth = GeneralUtil.INFINTYTIMESTAMP;
		
		if(!GeneralUtil.isBlankString(applFromString)) {
			applFrom = GeneralUtil.convertStringToTimestamp(applFromString);
		}
		
		if(!GeneralUtil.isBlankString(applToPlusOneMonthString)) {
			applToPlusOneMonth = GeneralUtil.convertStringToTimestamp(applToPlusOneMonthString);
		}
		
		if(applToPlusOneMonth.before(applFrom) || applToPlusOneMonth.equals(applFrom)) {
			throw new InvalidParameterException("\"Application To\" must come after \"Application From\"");
		}
		
		if (endSem == 0) {
			endSem = 9999;
		}
		
		if (endSem<startSem) {
			throw new InvalidParameterException("Ending Semester must come after Starting Semester, or should be empty");
		}
		
		String pendingFoSr = "N";
		String pendingBco = "N";
		if ("PENDING_FO_SR".equals(applStatCde)) {
			pendingFoSr = "Y";
			applStatCde = "%%";
		}
		else if ("PENDING_PYMT_BCO".equals(applStatCde)) {
			pendingBco = "Y";
			applStatCde = "%%";
		}
		
		log.info("getEnquiryTemp remoteUser {}, applName {}, applSchlId{}, requesterName {} ,applNbr {}, dept{}, applStatCde {}, brNo {}, applFrom {}, applToPlusOneMonth {}, elTypes {}, pendingFoSr {} , startSem {}, endSem {},"
				,remoteUser, applName, applSchlId, requesterName, applNbr, dept, applStatCde, brNo, applFrom, applToPlusOneMonth, elTypes, pendingFoSr,startSem,endSem);
		
				List<FoApplicationEnquiryRaw> resultMapList = foApplicationEnquiryRepository.getTempEnquiryData(applName, applSchlId, requesterName, applNbr, dept, applStatCde, brNo, applFrom, applToPlusOneMonth, elTypes, pendingFoSr, pendingBco,startSem,endSem);
		FoApplicationEnquiryCleanup cleanedData = new FoApplicationEnquiryCleanup(resultMapList,elApplPymtScheduleRepository);
		
		cleanedData.parseAndAddToHash();
		outputJsonArray = cleanedData.convertHashToJSONArray();
		
		return outputJsonArray;
		
	}
	
	public JSONArray getRelatedApprovedAppls(String applHdrId) throws Exception {
		JSONArray outputJson = new JSONArray();
		
		// basic validation
		ElApplHdrDAO hdrDAO = elApplHdrRepository.findOneForUpdate(applHdrId);
		
		if (hdrDAO == null) {
			throw new RecordNotExistException("Extra Load Application");
		}
		
		String applicantId = hdrDAO.getApplUserId();
		
		// find approved application of the applicant for review
		List<ElApplHdrDAO> resultApplsList = elApplHdrRepository.getApprovedApplsByUserId(applicantId);
		List<ElAcadTermVDAO> acadTermList = elAcadTermVRepository.findAll();
		List<ElApplElTypeDAO> elTypeList = elApplElTypeRepository.findAll();
		List<ElApplStatTabDAO> statusList = elApplStatTabRepository.findAll();
		
		
		for(ElApplHdrDAO appl : resultApplsList) {
			// Course
			List<ElApplCourseDAO> courseDAOList = elApplCourseRepository.findByApplHdrId(appl.getId());
			
			JSONArray crseJsonArr = new JSONArray();
			
			for (ElApplCourseDAO courseDAO : courseDAOList) {
				JSONObject jsonObj = new JSONObject();
				
//				String crseTermDesc = "";
//				for (ElAcadTermVDAO acadTerm: acadTermList) {
//					if (courseDAO.getCrseTerm().equals(acadTerm.getStrm())) {
//						crseTermDesc = acadTerm.getYrTermDesc();
//						break;
//					}
//				}
				
				jsonObj
				.put("crse_term", courseDAO.getCrseTerm())
//				.put("crse_term_desc", crseTermDesc)
				.put("crse_term_desc", acadTermList.stream().filter(x->x.getStrm().equals(courseDAO.getCrseTerm())).findFirst().isPresent() ? 
						acadTermList.stream().filter(x->x.getStrm().equals(courseDAO.getCrseTerm())).findFirst().get().getYrTermDesc() : " ")
				.put("crse_cde", courseDAO.getCrseCde())	
				;
				
				crseJsonArr.put(jsonObj);
			}
			
			// Extra Load Type
			String elTypeNam = " ";
			BigDecimal pymtAmt = BigDecimal.ZERO;
			String currency = " ";
			
			List<ElApplElTypeDAO> daoList = elApplElTypeRepository.findByApplHdrId(appl.getId());
			
			if(daoList.size() != 0) {
				
				for (ElApplElTypeDAO elType: elTypeList) {
					if (daoList.get(0).getElTypeId().equals(elType.getElTypeId())) {
						elTypeNam = elType.getElTypeDescr();
						pymtAmt = elType.getPymtAmt();
						currency = elType.getPmtCurrency();
						break;
					}
				}
				
			}
			
			// set return json
			JSONObject applObj = new JSONObject();
			
			applObj.put("id", appl.getId())
				.put("appl_nbr", appl.getApplNbr())
				.put("appl_user_id", appl.getApplUserId())
				.put("appl_user_name", appl.getApplUserName())
				.put("appl_statCde", appl.getApplStatCde())
				.put("appl_statCde_desc", statusList.stream().filter(x->x.getApplStatCde().equals(appl.getApplStatCde())).findFirst().isPresent() ? 
						statusList.stream().filter(x->x.getApplStatCde().equals(appl.getApplStatCde())).findFirst().get().getApplStatDescr() : " ")
				.put("appl_start_term", appl.getApplStartTerm())
				.put("appl_end_term", appl.getApplEndTerm())
				.put("appl_start_term_descr", acadTermList.stream().filter(x->x.getStrm().equals(appl.getApplStartTerm())).findFirst().isPresent() ? 
						acadTermList.stream().filter(x->x.getStrm().equals(appl.getApplStartTerm())).findFirst().get().getYrTermDesc() : " ")
				.put("appl_end_term_descr", acadTermList.stream().filter(x->x.getStrm().equals(appl.getApplEndTerm())).findFirst().isPresent() ? 
						acadTermList.stream().filter(x->x.getStrm().equals(appl.getApplEndTerm())).findFirst().get().getYrTermDesc() : " ")
				.put("appl_start_dt", appl.getApplStartDt())
				.put("appl_end_dt", appl.getApplEndDt())
				.put("appl_crse_list", crseJsonArr)
				.put("appl_eltype_name", elTypeNam)
				.put("appl_amt", pymtAmt)
				.put("appl_amt_currency", currency);
			
			outputJson.put(applObj);
		}
		
		return outputJson;
	}
	
	@Override
	public void isLoaAuthUser(String applHdrId) throws Exception {
		String remoteUser = SecurityUtils.getCurrentLogin();
		ElApplHdrDAO elApplHdrDAO = elApplHdrRepository.findOne(applHdrId);
		
		if (elApplHdrDAO == null) {
			throw new RecordNotExistException("Extra Load Application");
		}
		
		if (remoteUser.equals(elApplHdrDAO.getApplRequesterId()) 
				|| remoteUser.equals(elApplHdrDAO.getApplUserId())
				|| commonRoutineService.isAuthorized(AppConstants.ENQUIRE_LOA_FUNC_CDE, " ", null)) {
			return;
		}
		
		throw new InvalidParameterException("Not valid user for this Extra Load Application");
	}
	
	@Override
	public void isElApplAuthUser(String applHdrId) throws Exception {
		String remoteUser = SecurityUtils.getCurrentLogin();
		ElApplHdrDAO elApplHdrDAO = elApplHdrRepository.findOne(applHdrId);
		
		if (elApplHdrDAO == null) {
			throw new RecordNotExistException("Extra Load Application");
		}
		
		if (remoteUser.equals(elApplHdrDAO.getApplRequesterId()) 
				|| remoteUser.equals(elApplHdrDAO.getApplUserId())
				|| commonRoutineService.isAuthorized(AppConstants.ENQUIRE_APPL_FUNC_CDE, " ", null)
				|| commonRoutineService.isAuthorized(AppConstants.ENQUIRE_LOA_FUNC_CDE, " ", null)) {
			return;
		}
		
		// ETAP-44 allow approver view approved applications
		if (elApplActRepository.findRemoteUserApprovedAppl(remoteUser).contains(applHdrId)) {
			return;
		}
		
		ElApplAprvStatusDAO elApplAprvStatusDAO = elApplAprvStatusRepository.findNextAprv(applHdrId);
		
		if (elApplAprvStatusDAO != null && remoteUser.equals(elApplAprvStatusDAO.getAprvUserId())) {
			return ;
		}
		
		throw new InvalidParameterException("Not valid user for this Extra Load Application");
	}
	
	private void createPendingMyAdminTaskForRequestor(ElApplHdrDAO hdrDAO) {
		String webAppURLPrefix = env.getProperty(AppConstants.PATH_APP_PREFIX);
		String applNbr = hdrDAO.getApplNbr();
		String applUserName = hdrDAO.getApplUserName();
		
		String subject = "Rejected Extra Load Application (Appl no. ${applNbr})".replace("${applNbr}", applNbr) 
				+ " (Applicant : " + applUserName + ")";
		
		try {
			JSONObject params = new JSONObject();
			params.put("request_id", hdrDAO.getApplNbr());
			params.put("initiator", hdrDAO.getApplRequesterId());
			params.put("action", "Pending Decision");
			params.put("receiver", new JSONArray().put(hdrDAO.getApplRequesterId()));
			params.put("message", subject);
			params.put("url", webAppURLPrefix + "/extra_load_request/edit?id=" + hdrDAO.getId());

			commonRoutineService.callAIWorkListNotiAPI(params);
		} catch(Exception e) {
			log.error(e.toString());
		}
	}
	
	private void sendEmailToRequestorForPymtReject(String applHdrId, String opPageName) throws Exception {
		String webAppURLPrefix = env.getProperty(AppConstants.PATH_APP_PREFIX);

		ElApplAprvStatusDAO elApplAprvStatusDAO = elApplAprvStatusRepository.findRecentRejectorByApplHdrId(applHdrId);
		ElApplHdrDAO elApplHdrDAO = elApplHdrRepository.findOne(applHdrId);
		String requestorId = elApplHdrDAO.getApplRequesterId();
		String requestorName = elApplHdrDAO.getApplRequesterName();
		String role = transformAprvTypeDesc(elApplAprvStatusDAO);
		
		String applNbr = elApplHdrDAO.getApplNbr();
		
		String emailFrom = " ";
		String emailTo = requestorId + "@ust.hk";
		String subject = "Extra Load Payment Submission (Appl no. ${applNbr}) Rejected".replace("${applNbr}", applNbr);
		String content = 
				"Dear " + requestorName + ", " + "<br>"
				+ "<br>" 
				+ "An extra load payment submission (Appl no. ${applNbr}) is rejected." + "<br>"
				+ "<br>"
				+ "Rejector role : ${role}" + "<br>"
				+ "<br>"
				+ "Please click the following link to take action:" + "<br>"
				+ "<br>"
				+ GeneralUtil.createHyperLinks(webAppURLPrefix + "/extra_load_payment/view?id=" + applHdrId)
				+ "<br><br>" 
				;
		
		content = content.replace("${applNbr}", applNbr).replace("${role}", role);
		
		ElEmNotificationDAO elEmNotificationDAO = new ElEmNotificationDAO();
		
		elEmNotificationDAO.setApplHdrId(applHdrId);;
		elEmNotificationDAO.setEmailFrom(emailFrom);
		elEmNotificationDAO.setEmailTo(emailTo);
		elEmNotificationDAO.setSubject(subject);
		elEmNotificationDAO.setContent(content);
		
		String remoteUser = SecurityUtils.getCurrentLogin();
		elEmNotificationDAO.setCreatUser(remoteUser);
		elEmNotificationDAO.setChngUser(remoteUser);
		elEmNotificationDAO.setOpPageNam(opPageName);
		
		elEmNotificationRepository.save(elEmNotificationDAO);
	}
	
	private void sendEmailToPendingPymtAprv(String applHdrId, String opPageName) throws Exception {
		String webAppURLPrefix = env.getProperty(AppConstants.PATH_APP_PREFIX);
//		ElApplAprvStatusDAO elApplAprvStatusDAO = elApplAprvStatusRepository.findNextPymtAprv(applHdrId);
		ElApplHdrDAO elApplHdrDAO = elApplHdrRepository.findOne(applHdrId);
		
		List<ElApplAprvStatusDAO> pendingAprvs = elApplAprvStatusRepository.findAllNextPymtAprv(applHdrId);
		
		if (pendingAprvs.size() == 0) {
			throw new RecordNotExistException("Pending Approver");
		}
		
		List<String> assignee = new ArrayList<>();

		List<String> skipUserList = generalApiService.findBatchApprovalDailyEmailUserList();
		
		for (var elApplAprvStatusDAO : pendingAprvs) {
			if(!elApplAprvStatusDAO.getAprvTypeCde().startsWith("FO") && skipUserList.contains(elApplAprvStatusDAO.getAprvUserId())) {
				// Assume FO always need to receive email as they cannot approve application on batch approval page 
				log.debug("Skip email to {}", elApplAprvStatusDAO.getAprvUserId());
				continue;
			}
			assignee.add(elApplAprvStatusDAO.getAprvUserId() + "@ust.hk");
		}
		if(assignee.isEmpty()) {
			return;
		}
		
		String role = transformAprvTypeDesc(pendingAprvs.get(0));
		String aprvUserId =  pendingAprvs.size() > 1 ? role : pendingAprvs.get(0).getAprvUserId();
		String applNbr = elApplHdrDAO.getApplNbr();
		
		
		String emailFrom = " ";
		String emailTo = String.join(", ", assignee);
		String subject = "Pending Approval for Extra Load Payment Submission (Appl no. ${applNbr})".replace("${applNbr}", applNbr);
		String content = 
				"Dear " + aprvUserId + ", " + "<br>"
				+ "<br>" 
				+ "An extra load payment submission (Appl no. ${applNbr}) is ";
				
		if(generalApiService.checkIsRejected(applHdrId)) {
			content += "resubmitted and ";
		}
				
		content +=  "pending for your approval."
				+ "<br><br>"
				+ "Role : ${role}"
				+ "<br><br>"
				+ "Please click the following link to take action:" + "<br>"
				+ "<br>"
				+ GeneralUtil.createHyperLinks(webAppURLPrefix + "/extra_load_payment/approve?id=" + applHdrId)
				+ "<br><br>" 
				;
		
		content = content.replace("${applNbr}", applNbr).replace("${role}", role);
		
		ElEmNotificationDAO elEmNotificationDAO = new ElEmNotificationDAO();
		
		elEmNotificationDAO.setApplHdrId(applHdrId);;
		elEmNotificationDAO.setEmailFrom(emailFrom);
		elEmNotificationDAO.setEmailTo(emailTo);
		elEmNotificationDAO.setSubject(subject);
		elEmNotificationDAO.setContent(content);
		
		String remoteUser = SecurityUtils.getCurrentLogin();
		elEmNotificationDAO.setCreatUser(remoteUser);
		elEmNotificationDAO.setChngUser(remoteUser);
		elEmNotificationDAO.setOpPageNam(opPageName);
		
		elEmNotificationRepository.save(elEmNotificationDAO);
	}
	
	private void createMyAiTaskForRejection(ElApplHdrDAO hdrDAO) {
		String webAppURLPrefix = env.getProperty(AppConstants.PATH_APP_PREFIX);
		String applNbr = hdrDAO.getApplNbr();
		String applUserName = hdrDAO.getApplUserName();
		
		String subject = "Rejected Extra Load Payment Submission (Appl no. ${applNbr})".replace("${applNbr}", applNbr) + " (Applicant : " + applUserName + ")";
		
		try {
			JSONObject params = new JSONObject();
			params.put("request_id", hdrDAO.getApplNbr());
			params.put("initiator", hdrDAO.getApplRequesterId());
			params.put("action", "Pending Decision");
			params.put("receiver", new JSONArray().put(hdrDAO.getApplRequesterId()));
			params.put("message", subject);
			params.put("url", webAppURLPrefix + "/extra_load_payment/view?id=" + hdrDAO.getId());

			commonRoutineService.callAIWorkListNotiAPI(params);
		} catch(Exception e) {
			log.error(e.toString());
		}
	}
	
	private void createMyAiTaskForPendingPaymentAprv(String applHdrId) throws Exception {
		String webAppURLPrefix = env.getProperty(AppConstants.PATH_APP_PREFIX);
//		ElApplAprvStatusDAO elApplAprvStatusDAO = elApplAprvStatusRepository.findNextPymtAprv(applHdrId);
		ElApplHdrDAO elApplHdrDAO = elApplHdrRepository.findOne(applHdrId);
		List<ElApplAprvStatusDAO> pendingAprvs = elApplAprvStatusRepository.findAllNextPymtAprv(applHdrId);
		
		if (pendingAprvs.size() == 0) {
			throw new RecordNotExistException("Pending Approver");
		}
		
		List<String> assignee = new ArrayList<>();
		
		for (var elApplAprvStatusDAO : pendingAprvs) {
			assignee.add(elApplAprvStatusDAO.getAprvUserId());
		}
		
		String applNbr = elApplHdrDAO.getApplNbr();
		String subject = "Pending Approval for Extra Load Payment Submission (Appl no. ${applNbr})".replace("${applNbr}", applNbr)
				+ " (Applicant : " + elApplHdrDAO.getApplUserName() + ")";
		
		try {
			JSONObject params = new JSONObject();
			params.put("request_id", elApplHdrDAO.getApplNbr());
			params.put("initiator", elApplHdrDAO.getApplRequesterId());
			params.put("action", "Pending Approval by " + String.join(", ", assignee));
			params.put("receiver", new JSONArray(assignee));
			params.put("message", subject);
			params.put("url", webAppURLPrefix + "/extra_load_payment/approve?id=" + applHdrId);

			commonRoutineService.callAIWorkListNotiAPI(params);
		} catch(Exception e) {
			log.error(e.toString());
		}
	}

	@Override
	public JSONArray getBatchApplsPendingRemoteUserApproval() throws Exception {
		String remoteUser = SecurityUtils.getCurrentLogin();
		JSONArray jsonArr = new JSONArray();
		List<Map<String, Object>> resultMapList = elApplHdrRepository.findPendingRemoteUserBatchApproval(remoteUser);
		
		int errorWithNoNbr = 0;
		for (Map<String, Object> resultMap : resultMapList) {
			JSONObject jsonObj = new JSONObject();
			jsonArr.put(jsonObj);
			try {
				for (String keys : resultMap.keySet()) {
					jsonObj.put(keys.toLowerCase(), resultMap.get(keys));
				}
				jsonObj.put("el_appl_pymt_method", getElApplPymtMethod(jsonObj.getString("el_appl_hdr_id")));
				jsonObj.put("el_appl_mpf", getElApplPymtMPF(jsonObj.getString("el_appl_hdr_id")));
				jsonObj.put("el_appl_el_type", getElApplElType(jsonObj.getString("el_appl_hdr_id")));
				jsonObj.put("appl_type", getApplWorkflowType(jsonObj.getString("appl_user_job_catg")));
				jsonObj.put("appl_aprv", getElApplAprv(jsonObj.getString("el_appl_hdr_id")));
				
				if(ApplStatusConstants.PENDING_APPL_ACCT.equalsIgnoreCase(jsonObj.getString("appl_stat_cde"))) {
					jsonObj.put("role", RoleConstants.APPLICANT);
					if("B".equals(getApplWorkflowType(jsonObj.getString("appl_user_job_catg")))) {
						jsonObj.put("decl_req", true);
					}
				}else {
					ElApplAprvStatusDAO elApplAprvStatusDAO = elApplAprvStatusRepository.findNextAprv(jsonObj.getString("el_appl_hdr_id"));
					if(elApplAprvStatusDAO == null || !remoteUser.equals(elApplAprvStatusDAO.getAprvUserId())) {
						List<ElApplAprvStatusDAO> pendingAprvs = elApplAprvStatusRepository.findAllNextPymtAprv(jsonObj.getString("el_appl_hdr_id"));
						
						for (var pendingAprv : pendingAprvs) {
							if (remoteUser.equals(pendingAprv.getAprvUserId())) {
								elApplAprvStatusDAO = pendingAprv;
								break;
							}
						}
						
						if(elApplAprvStatusDAO == null || !remoteUser.equals(elApplAprvStatusDAO.getAprvUserId())){
							log.debug(remoteUser);
							log.debug(elApplAprvStatusDAO.getAprvUserId());
							continue;
						}
					}
					jsonObj.put("role", elApplAprvStatusDAO.getAprvTypeCde());
					jsonObj.put("aprv_status_id", elApplAprvStatusDAO.getId());
					if("B".equals(getApplWorkflowType(jsonObj.getString("appl_user_job_catg")))) {
						jsonObj.put("decl_req", true);
					}
					elApplAprvStatusDAO = elApplAprvStatusRepository.findRecentApproveByApplHdrId(jsonObj.getString("el_appl_hdr_id"));
					if(elApplAprvStatusDAO != null) {
						jsonObj.put("last_aprv_name", elApplAprvStatusDAO.getAprvUserName());
						jsonObj.put("last_aprv_id", elApplAprvStatusDAO.getAprvUserId());
					}
				}
				
				// get applicant declaration 
				ElApplHdrDAO elApplHdrDAO = elApplHdrRepository.findOne(jsonObj.getString("el_appl_hdr_id"));
				JSONObject userDeclJson = new JSONObject();
				userDeclJson.put("user_decl_0", elApplHdrDAO.getUserDecl_1() != 0 || elApplHdrDAO.getUserDecl_2() != 0 ? 1 : 0);
				userDeclJson.put("user_decl_1", elApplHdrDAO.getUserDecl_1());
				userDeclJson.put("user_decl_2", elApplHdrDAO.getUserDecl_2());
				userDeclJson.put("user_decl_2_from_dt", elApplHdrDAO.getUserDecl2FromDt());
				userDeclJson.put("user_decl_2_to_dt", elApplHdrDAO.getUserDecl2ToDt());
				
				JSONArray elUserDecSuprtAttmArr = getElApplAttachemt(jsonObj.getString("el_appl_hdr_id"), ElApplAttachmentsDAO.SUPPORTING);
				userDeclJson.put("user_dec_suprt_attachments", elUserDecSuprtAttmArr);
				
				jsonObj.put("appl_user_decl", userDeclJson);
				
				// get dept head declaration 
				JSONObject hodDeclJson = new JSONObject();
				hodDeclJson.put("hod_decl_1_0", elApplHdrDAO.getHodDecl_1_1() != 0 || elApplHdrDAO.getHodDecl_1_2() != 0 ? 1 : 0);
				hodDeclJson.put("hod_decl_1_1", elApplHdrDAO.getHodDecl_1_1());
				hodDeclJson.put("hod_decl_1_2", elApplHdrDAO.getHodDecl_1_2());
				jsonObj.put("appl_hod_decl", hodDeclJson);
			}catch (Exception e) {
				try{
					jsonObj.put("el_appl_hdr_id", resultMap.get("el_appl_hdr_id"));
					jsonObj.put("applNbr", resultMap.get("appl_nbr"));
					jsonObj.put("message", e.getMessage());
				}catch (Exception e2) {
					errorWithNoNbr++;
					jsonArr.remove(jsonArr.length()-1);
				}
			}
		}
		if(errorWithNoNbr != 0) {
			JSONObject jsonObj = new JSONObject();
			jsonArr.put(jsonObj);
			jsonObj.put("rowNbr", 0);
			jsonObj.put("applNbr", "--");
			jsonObj.put("message", "There are additional " + errorWithNoNbr + " records cannot be shown, please contact ISO.");
			
		}
		
		return jsonArr;
	}
	
	@Override
	public JSONArray batchApprovalInputValidation(JSONArray inputJson) throws Exception {
		
		JSONArray errorList = new JSONArray();
		try {
			for(int i = 0; i < inputJson.length();i++ ) {
				JSONObject obj = inputJson.getJSONObject(i);
				JSONObject error = new JSONObject();
				
				if(obj.optString("action").isBlank()) {
					// skip line without action
					continue;
				}
				
				ElApplHdrDAO hdrDAO = elApplHdrRepository.findOneForUpdate(obj.optString("appl_hdr_id"));
				if(hdrDAO == null) {
					continue;
				}
				if("B".equals(getApplWorkflowType(hdrDAO.getApplUserJobCatg())) && !obj.optString("action").equalsIgnoreCase(ElApplActDAO.REJECT)){
					if(obj.optString("role").equalsIgnoreCase(RoleConstants.APPLICANT)) {
						try {
							JSONObject applUserDeclJsonObj = obj.getJSONObject("appl_user_decl");
	
							int Declaration0 = applUserDeclJsonObj.getInt("user_decl_0");
							int Declaration1 = applUserDeclJsonObj.getInt("user_decl_1");
							int Declaration2 = applUserDeclJsonObj.getInt("user_decl_2");
							
							if(Declaration0 != 1 || (Declaration1 != 1 && Declaration1 != 2) || (Declaration2 != 1 && Declaration2 != 2)) {
								error.put("rowNbr", obj.get("rowNbr"));
								error.put("applNbr", obj.get("appl_nbr"));
								error.put("message", "You have not completed the declaraction.");
								errorList.put(error);
								continue;
							}
							
							Timestamp Declaration2_from_dt = GeneralUtil.NULLTIMESTAMP;
							Timestamp Declaration2_to_dt = GeneralUtil.NULLTIMESTAMP;
							
							try {
								Declaration2_from_dt = new Timestamp(applUserDeclJsonObj.getLong("user_decl_2_from_dt"));
							} catch(Exception e) {
	
							}
							try {
								Declaration2_to_dt = new Timestamp(applUserDeclJsonObj.getLong("user_decl_2_to_dt"));
							} catch(Exception e) {
	
							}
							
							if(Declaration2 == 1) {
								if(Declaration2_from_dt == GeneralUtil.NULLTIMESTAMP || Declaration2_to_dt == GeneralUtil.NULLTIMESTAMP) {
									error.put("rowNbr", obj.get("rowNbr"));
									error.put("applNbr", obj.get("appl_nbr"));
									error.put("message", "Please select the period to undertake the extra load activity.");
									errorList.put(error);
									continue;
								}
							}
						}catch(Exception e) {
							error.put("rowNbr", obj.get("rowNbr"));
							error.put("applNbr", obj.get("appl_nbr"));
							error.put("message", "You have not completed the declaraction.");
							errorList.put(error);
							continue;
						}
					} else if (obj.optString("role").equalsIgnoreCase("DEPT_HEAD")) {
						try {
							JSONObject applUserDeclJsonObj = obj.getJSONObject("appl_hod_decl");
							
							if(applUserDeclJsonObj != null) {
								int Declaration0 = applUserDeclJsonObj.getInt("hod_decl_1_0");
								int Declaration1 = applUserDeclJsonObj.getInt("hod_decl_1_1");
								int Declaration2 = applUserDeclJsonObj.getInt("hod_decl_1_2");
	
								if(Declaration0 != 1 || (Declaration1 != 1 && Declaration1 != 2) || (Declaration2 != 1 && Declaration2 != 2)) {
									error.put("rowNbr", obj.get("rowNbr"));
									error.put("applNbr", obj.get("appl_nbr"));
									error.put("message", "You have not completed the declaraction.");
									errorList.put(error);
									continue;
								}
							}else {
								error.put("rowNbr", obj.get("rowNbr"));
								error.put("applNbr", obj.get("appl_nbr"));
								error.put("message", "You have not completed the declaraction.");
								errorList.put(error);
								continue;
							}
						}catch (Exception e) {
							error.put("rowNbr", obj.get("rowNbr"));
							error.put("applNbr", obj.get("appl_nbr"));
							error.put("message", "You have not completed the declaraction.");
							errorList.put(error);
							continue;
						}
					}
				}
				
			}
		} catch(Exception e) {
			log.debug("Error when getting input");
			throw new InvalidParameterException("Can not get data, please try again.");
			// Error when getting input
		}
		return errorList;
	}
	
	@Override
	public JSONObject batchApprovalUpdateApplStatus(JSONArray inputJson, String opPageName) throws Exception {
		JSONObject outputJson = new JSONObject();
		JSONArray approvedArray = new JSONArray();
		JSONArray rejectedArray = new JSONArray();
		JSONArray errorArray = new JSONArray();
		int processCount = 0;
		for(int i = 0; i < inputJson.length();i++ ) {
			JSONObject obj = inputJson.getJSONObject(i);
			JSONObject processOutput = new JSONObject();
			if(processCount >= 25) {
				break;
			}
			if(StringUtils.isNotBlank(obj.optString("action"))) {
				try {
					processOutput = updateBatchApprovalByLine(obj.getString("appl_hdr_id"), obj, opPageName);
					
					if(ElApplActDAO.ACCEPT.equalsIgnoreCase(obj.getString("action")) || ElApplActDAO.APPROVE.equalsIgnoreCase(obj.getString("action"))) {
						approvedArray.put(processOutput);
					}else if(ElApplActDAO.REJECT.equalsIgnoreCase(obj.getString("action"))) {
						rejectedArray.put(processOutput);
					}
				}catch (Exception e) {
					JSONObject errorJson = new JSONObject();
					try {
						errorJson.put("applNbr", obj.getString("appl_nbr"));
					}catch (Exception e2) {
						errorJson.put("applNbr", "Can not get application number!");
					}
					errorJson.put("error", e.getMessage());
					errorArray.put(errorJson);
				}
				processCount++;
			}
		}
		outputJson.put("approved", approvedArray);
		outputJson.put("rejected", rejectedArray);
		outputJson.put("errMsg", errorArray);
		return outputJson;
	}

	@Transactional(value = "transactionManagerJselMain", rollbackFor = Exception.class)
	private JSONObject updateBatchApprovalByLine(String applHdrId, JSONObject inputJson, String opPageName) throws Exception {
		JSONObject outputJson = new JSONObject();
		boolean isPymt = false;
		outputJson.put("applNbr", inputJson.get("appl_nbr"));

		// validation
		ElApplHdrDAO hdrDAO = elApplHdrRepository.findOneForUpdate(applHdrId);
		
		if (hdrDAO == null) {
			throw new RecordNotExistException("Extra Load Application.");
//			outputJson.put("error", "Extra Load Application does not exist!");
//			return outputJson;
		}
		
		if (!hdrDAO.getModCtrlTxt().equals(inputJson.getString("mod_ctrl_txt"))) {
			throw new RecordModifiedException();
//			outputJson.put("error", "Record modified by other process!");
//			return outputJson;
		}
		
		
		String role = inputJson.optString("role");
		if (role.isBlank()) {
			throw new InvalidParameterException("Invalid role.");
		}
		
		boolean isApplicant = role.equalsIgnoreCase(RoleConstants.APPLICANT);
		String remoteUser = SecurityUtils.getCurrentLogin();

		ElApplAprvStatusDAO elApplAprvStatusDAO = null;

		if (isApplicant) {
			if (!remoteUser.equals(hdrDAO.getApplUserId())) {
				throw new InvalidParameterException("You are not the application applicant.");
//				outputJson.put("error", "You are not the application applicant");
//				return outputJson;
			}
			
			if (!ApplStatusConstants.PENDING_APPL_ACCT.equals(hdrDAO.getApplStatCde())) {
				throw new InvalidParameterException("Application is not pending for applicant to accept.");
//				outputJson.put("error", "Application is not pending for applicant to accept");
//				return outputJson;
			}
			
		} else {
			if(!ApplStatusConstants.PENDING_PYMT_APPR.equals(hdrDAO.getApplStatCde())){
				if (!ApplStatusConstants.PENDING.equals(hdrDAO.getApplStatCde()) || 0 != hdrDAO.getObsolete()) {
					throw new InvalidParameterException("Application is not pending for approval.");
//					outputJson.put("error", "Application is not pending for approval");
//					return outputJson;
				}
				elApplAprvStatusDAO = elApplAprvStatusRepository.findNextAprv(applHdrId);
				
				if (elApplAprvStatusDAO == null || !remoteUser.equals(elApplAprvStatusDAO.getAprvUserId()) || !elApplAprvStatusDAO.getId().equals(inputJson.optString("aprv_status_id"))) {
					throw new InvalidParameterException("You are not the pending approver.");
				}
			}else {
				List<ElApplAprvStatusDAO> pendingAprvs = elApplAprvStatusRepository.findAllNextPymtAprv(applHdrId);

				for (var pendingAprv : pendingAprvs) {
					if (remoteUser.equals(pendingAprv.getAprvUserId())) {
						elApplAprvStatusDAO = pendingAprv;
						break;
					}
				}
				
				if (elApplAprvStatusDAO == null) {
					try {
						elApplAprvStatusDAO = elApplAprvStatusRepository.findOne(inputJson.optString("aprv_status_id"));
						if(elApplAprvStatusDAO == null || elApplAprvStatusDAO.getApproved() != -1) {
							throw new RecordModifiedException();
//							outputJson.put("error", "Record modified by other process!");
//							return outputJson;
						}
					}catch (Exception e) {
						throw new InvalidParameterException("You are not the pending approver.");
//						outputJson.put("error", "You are not the pending approver");
//						return outputJson;
					}
				}
			}
		}
		// validation ends
		
		// start update
		Timestamp currTS = GeneralUtil.getCurrentTimestamp();
		String modCtrlTxt = GeneralUtil.genModCtrlTxt();
		String applNbr = hdrDAO.getApplNbr();
		
		if (isApplicant) {
			// Applicant to accept or reject to application submission only
			boolean isAccept = ElApplActDAO.ACCEPT.equalsIgnoreCase(inputJson.getString("action"));
			boolean isReject = ElApplActDAO.REJECT.equalsIgnoreCase(inputJson.getString("action"));
			
			if(!isAccept && !isReject) {
				throw new InvalidParameterException("Invalid action.");
			}
			
			if(isAccept) {
				// Assume always have approver for the next step
				hdrDAO.setApplStatCde(ApplStatusConstants.PENDING);
				
				// check if need to update applicant declaration field
				if(getApplWorkflowType(hdrDAO.getApplUserJobCatg()).equals("B")) {
					JSONObject applUserDeclJsonObj = inputJson.getJSONObject("appl_user_decl");
					
					int Declaration1 = applUserDeclJsonObj.getInt("user_decl_1");
					int Declaration2 = applUserDeclJsonObj.getInt("user_decl_2");
					
					if(Declaration1 != 1 && Declaration1 != 2 && Declaration2 != 1 && Declaration2 != 2) {
						// Should be block by previous validation
						log.error("[updateElApplicationStatus] applUserDeclJsonObj: " + applUserDeclJsonObj.toString());
						throw new InvalidParameterException("Please complete all the declaration.");
//						outputJson.put("error", "Please complete all the declaration.");
//						return outputJson;
					}
					
					Timestamp Declaration2_from_dt = GeneralUtil.NULLTIMESTAMP;
					Timestamp Declaration2_to_dt = GeneralUtil.NULLTIMESTAMP;
					
					try {
						Declaration2_from_dt = new Timestamp(applUserDeclJsonObj.getLong("user_decl_2_from_dt"));
					} catch(Exception e) {
						
					}
					try {
						Declaration2_to_dt = new Timestamp(applUserDeclJsonObj.getLong("user_decl_2_to_dt"));
					} catch(Exception e) {
						
					}
					
					if(Declaration2 == 1) {
						if(Declaration2_from_dt == GeneralUtil.NULLTIMESTAMP || Declaration2_to_dt == GeneralUtil.NULLTIMESTAMP) {
							// Should be block by previous validation
							throw new InvalidParameterException("Please select the period to undertake the extra load activity.");
//							outputJson.put("error", "Please select the period to undertake the extra load activity.");
//							return outputJson;
						}
					}
					
					hdrDAO.setUserDecl_1(Declaration1);
					hdrDAO.setUserDecl_2(Declaration2);
					hdrDAO.setUserDecl2FromDt(Declaration2_from_dt);
					hdrDAO.setUserDecl2ToDt(Declaration2_to_dt);
					
					// insert applicant uploaded support attachment
					insertElApplAttachments(applHdrId, applUserDeclJsonObj.getJSONArray("user_dec_suprt_attachments"), ElApplAttachmentsDAO.SUPPORTING, opPageName);
				} else {
					log.error("[updateElApplicationStatus] workflow");
					throw new InvalidParameterException("Invalid job catalogy for the action.");
//					outputJson.put("error", "Invalid job catalogy for the action.");
//					return outputJson;
				}
				
				hdrDAO.setChngDat(currTS);
				hdrDAO.setChngUser(remoteUser);
				hdrDAO.setOpPageNam(opPageName);
				hdrDAO.setModCtrlTxt(modCtrlTxt);
				
				sendEmailToPendingAprv(applHdrId, opPageName);
				// update My AI
				commonRoutineService.callAIWorkListApprvAPI(applNbr, "true", remoteUser, "Pending Declaration by " + remoteUser);
				createMyAiTaskForPendingAprv(applHdrId);
			} else if(isReject) {
				hdrDAO.setApplStatCde(ApplStatusConstants.REJECTED);
				
				hdrDAO.setChngDat(currTS);
				hdrDAO.setChngUser(remoteUser);
				hdrDAO.setOpPageNam(opPageName);
				hdrDAO.setModCtrlTxt(modCtrlTxt);
				
				sendEmailToSubmitterOnApplicantReject(applHdrId, opPageName);
				// update My AI
				commonRoutineService.callAIWorkListApprvAPI(applNbr, "false", remoteUser, "Pending Declaration by " + remoteUser);
				commonRoutineService.callAIWorkListCompleteAPI(applNbr, "false", "completed");
				createPendingMyAdminTaskForRequestor(hdrDAO);
			}
				
		} else {
			if(ElApplActDAO.ACCEPT.equalsIgnoreCase(inputJson.getString("action"))){
				inputJson.put("action", ElApplActDAO.APPROVE);
			}
			boolean isApprove = ElApplActDAO.APPROVE.equalsIgnoreCase(inputJson.getString("action"));
			
			// update elApplAprvStatusDAO
			elApplAprvStatusDAO.setApproved(isApprove ? 1 : 0);
			elApplAprvStatusDAO.setAprvDttm(currTS);
			elApplAprvStatusDAO.setAprvRemark(GeneralUtil.initBlankString(inputJson.optString("aprv_remark")));
			elApplAprvStatusDAO.setChngDat(currTS);
			elApplAprvStatusDAO.setChngUser(remoteUser);
			elApplAprvStatusDAO.setOpPageNam(opPageName);
			elApplAprvStatusDAO.setModCtrlTxt(modCtrlTxt);
			
			elApplAprvStatusRepository.save(elApplAprvStatusDAO);
			
			if(hdrDAO.getApplStatCde().equals(ApplStatusConstants.PENDING)) {
				
				if(isApprove) {
					// check if current approver is dept head role
					if(elApplAprvStatusDAO.getAprvTypeCde().equals(AprvTypeConstants.DEPT_HEAD)) {
						// check application workflow type
						if(getApplWorkflowType(hdrDAO.getApplUserJobCatg()).equals("B")) {
							// update the declaration for dept head
							JSONObject applUserDeclJsonObj = inputJson.getJSONObject("appl_hod_decl");
							
							if(applUserDeclJsonObj != null) {
								int Declaration1 = applUserDeclJsonObj.getInt("hod_decl_1_1");
								int Declaration2 = applUserDeclJsonObj.getInt("hod_decl_1_2");
								
								if(Declaration1 != 1 && Declaration1 != 2 && Declaration2 != 1 && Declaration2 != 2) {
									log.error("[updateElApplicationStatus] applUserDeclJsonObj: " + applUserDeclJsonObj.toString());
									// Should be block by previous validation
									throw new InvalidParameterException("Please complete all the declaration.");
//									outputJson.put("error", "Please complete all the declaration.");
//									return outputJson;
								}
								
								hdrDAO.setHodDecl_1_1(Declaration1);
								hdrDAO.setHodDecl_1_2(Declaration2);
							}
						}
					}
				}
				
				// 20231229 #251: try to approve if next approval role is the same user
				List<ElApplAprvStatusDAO> allPedningAprvList = elApplAprvStatusRepository.findAllPendingAprv(applHdrId);
				
				int currentSeq = elApplAprvStatusDAO.getArpvSeq();
				boolean isChngSeq = false;
				List<ElApplAprvStatusDAO> updateList = new ArrayList<>();
				for(ElApplAprvStatusDAO dao : allPedningAprvList) {
					// assume the dao object should be the current updated dao 
					if(dao.getId().equals(elApplAprvStatusDAO.getId()))
						continue;
					
					if(isChngSeq == true && currentSeq != dao.getArpvSeq())
						break;
					
					if(currentSeq != dao.getArpvSeq()) {
						isChngSeq = true;
						currentSeq = dao.getArpvSeq();
						log.info("change seq");
					}
					if(remoteUser.equals(dao.getAprvUserId())) {
						log.info("update approver");
						dao.setApproved(isApprove ? 1 : 0);
						dao.setAprvDttm(currTS);
						dao.setAprvRemark(GeneralUtil.initBlankString(inputJson.optString("aprv_remark")));
						dao.setChngDat(currTS);
						dao.setChngUser(remoteUser);
						dao.setOpPageNam(opPageName);
						dao.setModCtrlTxt(modCtrlTxt);
						
						updateList.add(dao);
						
						isChngSeq = false;
					}
				}
				
				if(updateList.size() > 0)
					elApplAprvStatusRepository.saveAll(updateList);
				
				
				// if approve, check if there are next approver
				// if yes, workflow to send email
				// Else, update header to APPROVED
				if (isApprove) {
					ElApplAprvStatusDAO nextPendingAprv = elApplAprvStatusRepository.findNextAprv(applHdrId);
					
					// TODO notify workflow
					if (nextPendingAprv != null) {
						hdrDAO.setChngDat(currTS);
						hdrDAO.setChngUser(remoteUser);
						hdrDAO.setOpPageNam(opPageName);
						hdrDAO.setModCtrlTxt(modCtrlTxt);
						
						// send email to next aprv
						sendEmailToPendingAprv(applHdrId, opPageName);
						// update My Ai
						commonRoutineService.callAIWorkListApprvAPI(applNbr, "true", remoteUser, "Pending Approval by " + remoteUser);
						createMyAiTaskForPendingAprv(applHdrId);
					} else {
						if(elApplHdrHistRepository.findMaxVersionNoById(applHdrId) < hdrDAO.getVersionNo()) {
							// Should be the first backup version.	
							
							hdrDAO.setApplStatCde(ApplStatusConstants.APPROVED);
							
							hdrDAO.setChngDat(currTS);
							hdrDAO.setChngUser(remoteUser);
							hdrDAO.setOpPageNam(opPageName);
							hdrDAO.setModCtrlTxt(modCtrlTxt);
							
							// 20231229 #371 mark the BR_POST_IND='R' when application is approved
							hdrDAO.setBrPostInd("R");
							
							// Backup Version
							
							ElApplHdrHistDAO saveHdrDAO = new ElApplHdrHistDAO(hdrDAO);
							saveHdrDAO.setCreatUser(remoteUser);
							saveHdrDAO.setChngUser(remoteUser);
							saveHdrDAO.setOpPageNam(opPageName);
							
							// Assume there is only 1 MPF method and 1 SALARY
							ElApplPymtMethodDAO elApplMPFPymtMethodDAO = elApplPymtMethodRepository.findMPFMethodByApplHdrId(applHdrId).get(0);
							ElApplPymtMethodDAO elApplSalaryPymtMethodDAO = elApplPymtMethodRepository.findSalaryMethodByApplHdrId(applHdrId).get(0);
							List<ElApplPymtMethodHistDAO> saveMethodList = new ArrayList<>();
							
							ElApplPymtMethodHistDAO elApplMPFPymtMethodHistDAO = new ElApplPymtMethodHistDAO(elApplMPFPymtMethodDAO, hdrDAO);
							elApplMPFPymtMethodHistDAO.setCreatUser(remoteUser);
							elApplMPFPymtMethodHistDAO.setChngUser(remoteUser);
							elApplMPFPymtMethodHistDAO.setOpPageNam(opPageName);
							saveMethodList.add(elApplMPFPymtMethodHistDAO);
							
							ElApplPymtMethodHistDAO elApplSalaryPymtMethodHistDAO = new ElApplPymtMethodHistDAO(elApplSalaryPymtMethodDAO, hdrDAO);
							elApplSalaryPymtMethodHistDAO.setCreatUser(remoteUser);
							elApplSalaryPymtMethodHistDAO.setChngUser(remoteUser);
							elApplSalaryPymtMethodHistDAO.setOpPageNam(opPageName);
							saveMethodList.add(elApplSalaryPymtMethodHistDAO);
							
							List<ElApplPymtScheduleDAO> mpfList = elApplPymtScheduleRepository.findMPFByApplHdrId(applHdrId);
							List<ElApplPymtScheduleHistDAO> saveScheduleList = new ArrayList<>();
							for(ElApplPymtScheduleDAO dao: mpfList) {
								ElApplPymtScheduleHistDAO histDAO = new ElApplPymtScheduleHistDAO(dao, elApplMPFPymtMethodHistDAO);
								histDAO.setCreatUser(remoteUser);
								histDAO.setChngUser(remoteUser);
								histDAO.setOpPageNam(opPageName);
								
								saveScheduleList.add(histDAO);
							}
							
							List<ElApplPymtScheduleDAO> salaryList = elApplPymtScheduleRepository.findSalaryByApplHdrId(applHdrId);
							for(ElApplPymtScheduleDAO dao: salaryList) {
								ElApplPymtScheduleHistDAO histDAO = new ElApplPymtScheduleHistDAO(dao, elApplSalaryPymtMethodHistDAO);
								histDAO.setCreatUser(remoteUser);
								histDAO.setChngUser(remoteUser);
								histDAO.setOpPageNam(opPageName);
								
								saveScheduleList.add(histDAO);
							}
							
							List<ElApplCourseDAO> courseList = elApplCourseRepository.findByApplHdrId(applHdrId);
							List<ElApplCourseHistDAO> saveCourseList = new ArrayList<>();
							List<ElApplColistCourseHistDAO> saveColistList = new ArrayList<>();
		
							for(ElApplCourseDAO dao: courseList) {
								ElApplCourseHistDAO histDAO = new ElApplCourseHistDAO(dao, hdrDAO);
								histDAO.setCreatUser(remoteUser);
								histDAO.setChngUser(remoteUser);
								histDAO.setOpPageNam(opPageName);
								
								saveCourseList.add(histDAO);
		
								List<ElApplColistCourseDAO> colistList = elApplColistCourseRepository.findByApplCrseId(dao.getId());
								for(ElApplColistCourseDAO coDAO: colistList) {
									ElApplColistCourseHistDAO coHistDAO = new ElApplColistCourseHistDAO(coDAO, histDAO);
									coHistDAO.setCreatUser(remoteUser);
									coHistDAO.setChngUser(remoteUser);
									coHistDAO.setOpPageNam(opPageName);
									
									saveColistList.add(coHistDAO);
								}
							}
							
							List<ElApplElTypeDAO> elTypeList = elApplElTypeRepository.findByApplHdrId(applHdrId);
							List<ElApplElTypeHistDAO> saveElTypeList = new ArrayList<>(); 
		
							for(ElApplElTypeDAO dao: elTypeList) {
								ElApplElTypeHistDAO histDAO = new ElApplElTypeHistDAO(dao, hdrDAO);
								histDAO.setCreatUser(remoteUser);
								histDAO.setChngUser(remoteUser);
								histDAO.setOpPageNam(opPageName);
								
								saveElTypeList.add(histDAO);
							}
		
							ElApplPrgmDAO prgmDao = elApplPrgmRepository.findByApplHdrId(applHdrId);
							if(prgmDao != null) {
								ElApplPrgmHistDAO savePrgmDAO = new ElApplPrgmHistDAO(prgmDao, hdrDAO);
								savePrgmDAO.setCreatUser(remoteUser);
								savePrgmDAO.setChngUser(remoteUser);
								savePrgmDAO.setOpPageNam(opPageName);
								
								elApplPrgmHistRepository.save(savePrgmDAO);
							}
							
							elApplHdrHistRepository.save(saveHdrDAO);
							elApplCourseHistRepository.saveAll(saveCourseList);
							elApplColistCourseHistRepository.saveAll(saveColistList);
							elApplElTypeHistRepository.saveAll(saveElTypeList);
							elApplPymtMethodHistRepository.saveAll(saveMethodList);
							elApplPymtScheduleHistRepository.saveAll(saveScheduleList);
						}
						
						// update My Ai
						commonRoutineService.callAIWorkListApprvAPI(applNbr,  "true", remoteUser, "Pending Approval by " + remoteUser);
						createMyAiTaskForPendingIntegration(applHdrId);
					}
					
				} 
				// if reject, update header status to REJECTED
				else {
					hdrDAO.setApplStatCde(ApplStatusConstants.REJECTED);
					
					hdrDAO.setChngDat(currTS);
					hdrDAO.setChngUser(remoteUser);
					hdrDAO.setOpPageNam(opPageName);
					hdrDAO.setModCtrlTxt(modCtrlTxt);
					
					sendEmailToSubmitterOnApproverReject(applHdrId, opPageName);
					// update My Ai
					commonRoutineService.callAIWorkListApprvAPI(applNbr, "false", remoteUser, "Pending Approval by " + remoteUser);
					commonRoutineService.callAIWorkListCompleteAPI(applNbr, "false", "completed");
					createPendingMyAdminTaskForRequestor(hdrDAO);
				}
			}else if (hdrDAO.getApplStatCde().equals(ApplStatusConstants.PENDING_PYMT_APPR)) {
				isPymt = true;
				if (!RoleConstants.APPLICANT.equalsIgnoreCase(role)) {
					// For update MyAdmin, 
					List<String> pendingAprvIds = elApplAprvStatusRepository.findAllNextPymtAprv(applHdrId).stream()
							.map(dao -> dao.getAprvUserId()).collect(Collectors.toList());
					
					// update elApplAprvStatusDAO
					if(!(elApplAprvStatusDAO.getAprvTypeCde().equalsIgnoreCase(RoleConstants.FO_SFM) && !isApprove)) {
						elApplAprvStatusDAO.setApproved(isApprove ? 1 : 0);
						elApplAprvStatusDAO.setAprvDttm(currTS);
						elApplAprvStatusDAO.setAprvRemark(GeneralUtil.initBlankString(inputJson.optString("aprv_remark")));
						elApplAprvStatusDAO.setChngDat(currTS);
						elApplAprvStatusDAO.setChngUser(remoteUser);
						elApplAprvStatusDAO.setOpPageNam(opPageName);
						elApplAprvStatusDAO.setModCtrlTxt(modCtrlTxt);
					}
					
					// if approve, check if there are next approver
					// if yes, workflow to send email
					// Else, update header to PENDING_DECL
					if (isApprove) {
						List<ElApplAprvStatusDAO> pendingAprvs = elApplAprvStatusRepository.findAllNextPymtAprv(applHdrId);

						
						hdrDAO.setChngDat(currTS);
						hdrDAO.setChngUser(remoteUser);
						hdrDAO.setOpPageNam(opPageName);
						hdrDAO.setModCtrlTxt(modCtrlTxt);
//						ElApplAprvStatusDAO nextPendingAprv = elApplAprvStatusRepository.findNextPymtAprv(applHdrId);
						
						// Assume at least 1 more pending approver (FO_SR Team)
						if (pendingAprvs.size() > 0) {
							sendEmailToPendingPymtAprv(applHdrId, opPageName);
							
							commonRoutineService.callAIWorkListApprvAPI(hdrDAO.getApplNbr(), "true", remoteUser, "Pending Approval by " + String.join(", ", pendingAprvIds));
							createMyAiTaskForPendingPaymentAprv(applHdrId);
						}
					} 
					// if reject, update header status to PYMT_RETURNED
					else {
						// TODO: if rejected, set sumbit to " "
						// if reject by FO_SFM, pend to FO_SR
						hdrDAO.setApplStatCde(ApplStatusConstants.PYMT_REJECTED);
		
						boolean anyPosted = false;
						List<ElApplPymtScheduleDAO> scheduleList = elApplPymtScheduleRepository.findSalaryByApplHdrId(hdrDAO.getId());
							for (ElApplPymtScheduleDAO scheDAO : scheduleList) {
								if(PymtStatusConstants.POST.equals(scheDAO.getPymtStatusCde()) && scheDAO.getPymtSubmitDttm() != GeneralUtil.NULLTIMESTAMP) {
									anyPosted = true;
								}
							}
						if(anyPosted) {
							hdrDAO.setPymtPostInd("P");
						}else {
							hdrDAO.setPymtPostInd("N");
						}
						
						sendEmailToRequestorForPymtReject(applHdrId, opPageName);
		//				commonRoutineService.callAIWorkListApprvAPI(hdrDAO.getApplNbr(), "false", remoteUser, "Pending Approval by " + remoteUser);
						createMyAiTaskForRejection(hdrDAO);
						
						hdrDAO.setChngDat(currTS);
						hdrDAO.setChngUser(remoteUser);
						hdrDAO.setOpPageNam(opPageName);
						hdrDAO.setModCtrlTxt(modCtrlTxt);
					}
				}
			}
		}
		
		// creation record in action table
		if (!RoleConstants.REQUESTER.equalsIgnoreCase(role) && !RoleConstants.APPLICANT.equalsIgnoreCase(role)) {
			if (elApplAprvStatusDAO != null) {
				inputJson.put("role", elApplAprvStatusDAO.getAprvTypeCde());
			}
		}
		if(isPymt) {
			generalApiService.createElApplAct(applHdrId, inputJson, "", "", "pymt", opPageName);
		}else {
			generalApiService.createElApplAct(applHdrId, inputJson, "", "", "appl", opPageName);
		}
		
		
		return outputJson;
		
	}
	
	public void createBatchApprovalDailyEmail(int hour, int minute, String jobUser, String opPageName) throws Exception {
		List<String> userList = generalApiService.findBatchApprovalDailyEmailUserList();

		ElParamTabDAO startTimeParamDAO = elParamTabRepository.findBatchApprovalStartTime();
		ElParamTabDAO endTimeParamDAO = elParamTabRepository.findBatchApprovalEndTime();
		boolean getParam = false;
		
		Timestamp startTime = GeneralUtil.getCurrentTimestamp();
		Timestamp endTime = GeneralUtil.getCurrentTimestamp();
		if(startTime.getDay() == 0 || startTime.getDay() == 6) {
			log.info("Batch Approval Daily Email Error: Current time is Saturday or Sunday (day:{}), the job will not be process.");
			return;
		}
		
		if((startTimeParamDAO == null || StringUtils.isBlank(startTimeParamDAO.getValue())) && (endTimeParamDAO == null || StringUtils.isBlank(endTimeParamDAO.getValue()))) {
			if(startTime.getDay() == 1) {
				startTime.setDate(startTime.getDate() - 3);
			}else {
				startTime.setDate(startTime.getDate() - 1);
			}
			startTime.setHours(hour);
			startTime.setMinutes(minute);
			startTime.setSeconds(0);
			startTime.setNanos(0);
			endTime.setHours(hour);
			endTime.setMinutes(minute);
			endTime.setSeconds(0);
			endTime.setNanos(0);
		}else {
			try {
				startTime = GeneralUtil.convertStringToTimestamp(startTimeParamDAO.getValue(), "N");
				endTime = GeneralUtil.convertStringToTimestamp(endTimeParamDAO.getValue(), "N");
				getParam = true;
				
				if(startTime.after(endTime)) {
					log.info("Batch Approval Daily Email Error: The param table start time is after end time, the job will not be process.");
					return;
				}
			}catch (Exception e) {
				log.info("Batch Approval Daily Email Error: The param table data format is wrong, the job will not be process.");
				return;
//				startTime = GeneralUtil.getCurrentTimestamp();
//				endTime = GeneralUtil.getCurrentTimestamp();
//				if(startTime.getDay() == 1) {
//					startTime.setDate(startTime.getDate() - 3);
//				}else {
//					startTime.setDate(startTime.getDate() - 1);
//				}
//				startTime.setHours(hour);
//				startTime.setMinutes(minute);
//				startTime.setSeconds(0);
//				startTime.setNanos(0);
//				endTime.setHours(hour);
//				endTime.setMinutes(minute);
//				endTime.setSeconds(0);
//				endTime.setNanos(0);
//				getParam = false;
			}
		}
		for(String user: userList) {
			createBatchApprovalEmail(startTime, endTime, user, jobUser, opPageName);
		}
		if(getParam) {
			startTimeParamDAO.setValue(" ");
			startTimeParamDAO.setChngDat(GeneralUtil.getCurrentTimestamp());
			startTimeParamDAO.setChngUser(jobUser);
			startTimeParamDAO.setOpPageNam(opPageName);
			startTimeParamDAO.setModCtrlTxt(GeneralUtil.genModCtrlTxt());
			elParamTabRepository.save(startTimeParamDAO);
	
			endTimeParamDAO.setValue(" ");
			endTimeParamDAO.setChngDat(GeneralUtil.getCurrentTimestamp());
			endTimeParamDAO.setChngUser(jobUser);
			endTimeParamDAO.setOpPageNam(opPageName);
			endTimeParamDAO.setModCtrlTxt(GeneralUtil.genModCtrlTxt());
			elParamTabRepository.save(endTimeParamDAO);
		}
		
		
	}
	
	private void createBatchApprovalEmail(Timestamp startTime, Timestamp endTime, String aprvUserId, String jobUser, String opPageName) {
		String webAppURLPrefix = env.getProperty(AppConstants.PATH_APP_PREFIX);
		
		List<Map<String, Object>> applicationList = elApplHdrRepository.findPendingRemoteUserBatchApproval(aprvUserId);
		int count = applicationList.size();
		log.debug("user: {}, application size: {}", aprvUserId, applicationList.size());
		if(count == 0) return;
		List<String> applIdList = new ArrayList<>();
		for(Map<String, Object> appl: applicationList) {
			applIdList.add((String) appl.get("el_appl_hdr_id"));
		}
		List<String> actList = elApplActRepository.findLastActionByApplHdrIdAndTimePeriod(applIdList, startTime, endTime);
		log.debug("user: {}, actList size: {}", aprvUserId, actList.size());
		if(actList.size() == 0) return;
		
		String emailFrom = " ";
		String emailTo = aprvUserId + "@ust.hk";
		String subject = "Pending Approval for Extra Load Application";
		String content = 
				"Dear " + aprvUserId + ", " + "<br>"
				+ "<br>" 
				+ "There are a total of ";

		content += count;
		
		content	+= " application(s) pending for your approval. "
				+ "Please click the following link to take action:" + "<br>"
				+ "<br>"
				+ GeneralUtil.createHyperLinks(webAppURLPrefix + "/pending_application")
				+ "<br><br>" 
				;
		
		ElEmNotificationDAO elEmNotificationDAO = new ElEmNotificationDAO();
		
		elEmNotificationDAO.setApplHdrId(" ");;
		elEmNotificationDAO.setEmailFrom(emailFrom);
		elEmNotificationDAO.setEmailTo(emailTo);
		elEmNotificationDAO.setSubject(subject);
		elEmNotificationDAO.setContent(content);
		
		elEmNotificationDAO.setCreatUser(jobUser);
		elEmNotificationDAO.setChngUser(jobUser);
		elEmNotificationDAO.setOpPageNam(opPageName);
		log.debug("Email for {} is created", aprvUserId);
		elEmNotificationRepository.save(elEmNotificationDAO);
		
	}
}
