package jsfas.db.main.persistence.service;

import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import javax.inject.Inject;

import org.apache.commons.lang.StringUtils;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.CreationHelper;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.transaction.annotation.Transactional;

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
import jsfas.common.object.Coa;
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
import jsfas.db.main.persistence.domain.ElDeptChrtVDAO;
import jsfas.db.main.persistence.domain.ElEmNotificationDAO;
import jsfas.db.main.persistence.domain.ElInpostStaffImpVDAO;
import jsfas.db.main.persistence.domain.ElJobDataVDAO;
import jsfas.db.main.persistence.domain.ElTypeSalElemntTabDAO;
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
import jsfas.db.main.persistence.repository.ElCrseClassVRepository;
import jsfas.db.main.persistence.repository.ElDeptChrtVRepository;
import jsfas.db.main.persistence.repository.ElEmNotificationRepository;
import jsfas.db.main.persistence.repository.ElInpostStaffImpVRepository;
import jsfas.db.main.persistence.repository.ElJobDataVRepository;
import jsfas.db.main.persistence.repository.ElTypeSalElemntTabRepository;
import jsfas.db.rbac.persistence.repository.UserRoleGroupRepository;
import jsfas.security.SecurityUtils;

public class ExtraLoadPaymentEventHandler implements ExtraLoadPaymentService {

	@Autowired
    private Environment env;
    
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
	ExtraLoadApplicationService extraLoadApplicationService;
	
	@Autowired
	GeneralApiService generalApiService;
	
	@Autowired
	ElEmNotificationRepository elEmNotificationRepository;
	
	@Autowired
	ElAcadPlanVRepository elAcadPlanVRepository;
	
	@Autowired
	ElAcadTermVRepository elAcadTermVRepository;
	
	@Autowired
	ElInpostStaffImpVRepository elInpostStaffImpVRepository;
	
	@Autowired
	ElApplStatTabRepository elApplStatTabRepository;
	
	@Autowired
	ElDeptChrtVRepository elDeptChrtVRepository;
	
	@Autowired
	CommonRoutineService commonRoutineService;
	
	@Autowired
	UserRoleGroupRepository userRoleGroupRepository;
	
	@Autowired
	ElJobDataVRepository elJobDataVRepository;
	
	@Autowired
	ElCrseClassVRepository elCrseClassVRepository;
	
	@Autowired
	LoaService loaService;

	private final Logger log = LoggerFactory.getLogger(this.getClass());
	
	@Override
	public JSONArray getPymtList() throws Exception {
		String remoteUser = SecurityUtils.getCurrentLogin();

		JSONArray outputJson = new JSONArray();

		List<String> statusList = new ArrayList<>(
				Arrays.asList(ApplStatusConstants.READY_SUBM, ApplStatusConstants.PENDING_PYMT_APPR,
						ApplStatusConstants.PENDING_DECL, ApplStatusConstants.PYMT_REJECTED, ApplStatusConstants.PENDING_FOPNB, ApplStatusConstants.PYMT_SUBM));
		// 20231228 #412 add PYMT_SUBM
		// 20240123 add PENDING_FOPNB
		
		List<Map<String, Object>> resultMapList = elApplHdrRepository.findPymts(remoteUser, statusList);

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
	public JSONArray getPymtsPendingRemoteUserApproval() throws Exception {
		String remoteUser = SecurityUtils.getCurrentLogin();

		JSONArray outputJson = new JSONArray();

		List<String> statusList = new ArrayList<>(
				Arrays.asList(ApplStatusConstants.PENDING_PYMT_APPR, ApplStatusConstants.PENDING_FOPNB, ApplStatusConstants.PENDING_RECTIFY));
		List<Map<String, Object>> resultMapList = elApplHdrRepository.findPymtPendingRemoteUserApproval(remoteUser, statusList);

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
	public JSONObject getPymtDetails(String applHdrId) throws Exception {
		JSONObject outputJson = new JSONObject();
		ElApplHdrDAO elApplHdrDAO = elApplHdrRepository.findOne(applHdrId);
		
		if (elApplHdrDAO == null) {
			throw new RecordNotExistException("Extra Load Application");
		}
		
		ElDeptChrtVDAO deptDAO = elDeptChrtVRepository.findOne(elApplHdrDAO.getApplUserDeptId());
		String dept = elApplHdrDAO.getApplUserDeptId();
		if(deptDAO != null) {
			dept = deptDAO.getDeptShortDesc();
		}

//		ElInpostStaffImpVDAO staffDAO = elInpostStaffImpVRepository.findPrimaryByEmplid(elApplHdrDAO.getApplUserEmplid());
		Map<String, Object> applicantInfo = elInpostStaffImpVRepository.searchApplicantByEmplid(elApplHdrDAO.getApplUserEmplid());
		
		String role = " ";
		String jobCatgCde = elApplHdrDAO.getApplUserJobCatg().trim();
		
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
		
//		if(applicantInfo.size() != 0) {
//			
//			String jobCatgCde = (String) applicantInfo.get("job_catg_cde");
//			
//			switch(jobCatgCde){
//				case "A010" :
//				case "A020" :
//				case "A030" :
//				case "A040" :
//					role = "Senior";
//					break;
//				case "R020" :
//				case "I020" :
//					role = "Support";
//					break;
//				default :
//					role = "Support";
//			}
//		}
		
		// application header
		List<ElApplStatTabDAO> statusList = elApplStatTabRepository.findAll();
		
		outputJson
		.put("el_appl_hdr_id", elApplHdrDAO.getId())
		.put("appl_nbr", elApplHdrDAO.getApplNbr())
		.put("appl_user_id", elApplHdrDAO.getApplUserId())
		.put("appl_user_name", elApplHdrDAO.getApplUserName())
		.put("appl_user_dept", dept)
		.put("appl_user_deptid", elApplHdrDAO.getApplUserDeptId())
		.put("appl_requester_id", elApplHdrDAO.getApplRequesterId())
		.put("appl_requester_name", elApplHdrDAO.getApplRequesterName())
		.put("appl_requester_emplid", elApplHdrDAO.getApplRequesterEmplid())
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
		.put("version_no", elApplHdrDAO.getVersionNo())
		.put("br_no", elApplHdrDAO.getBrNo())
//		.put("tos", staffDAO.getTos())
		.put("role", role)
		.put("payroll_descr", elApplHdrDAO.getPayrollDescr())
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
		
		JSONArray elApplAttachmentArr = getElApplAttachemt(applHdrId);
		outputJson.put("el_appl_attachments", elApplAttachmentArr);
		
		JSONArray elApplActArr = getElApplAct(applHdrId);
		outputJson.put("el_appl_act", elApplActArr);
		
		JSONObject previousMethodObj = getPreviousElApplPymtMethod(applHdrId);
		outputJson.put("previous_method", previousMethodObj);
		
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
					if(!nextType.equalsIgnoreCase(AprvTypeConstants.FO_SR) && !nextType.equalsIgnoreCase(AprvTypeConstants.FO_SFM) && !nextType.equalsIgnoreCase(AprvTypeConstants.FO_PNB)  && !nextType.equalsIgnoreCase(AprvTypeConstants.FO_RECTIFY)) {
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

	private JSONArray getElApplAttachemt(String applHdrId) throws Exception {
		JSONArray jsonArr = new JSONArray();
		List<Map<String, String>> resultList = elApplAttachmentsRepository.findDetailsByApplHdrIdAndFileCategory(applHdrId, ElApplAttachmentsDAO.APPLICATION);
		
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
//			.put("prgm_start_dt", prgmDAO.getPrgmStartDttm())
//			.put("prgm_end_dt", prgmDAO.getPrgmEndDttm())
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
			
			if(curtSchlNo == prevSchNo && ((JSONArray) outputMap.get("pymt_schedule")).length() >= curtSchlNo) {
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
	/*
	private JSONArray getElApplPymtMethod(String applHdrId) throws Exception {
		List<JSONObject> outputList = new ArrayList<>();
		
		List<Map<String, Object>> resultMapList = elApplPymtMethodRepository.findDetailsByApplHdrId(applHdrId);
		
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
				outputMap.put("el_type_id", resultMap.get("el_type_id"));
				outputMap.put("pymt_type_cde",resultMap.get("pymt_type_cde"));
				outputMap.put("pymt_freq", resultMap.get("pymt_freq"));
				outputMap.put("pymt_amt", resultMap.get("pymt_amt"));
				outputMap.put("pymt_start_dt", resultMap.get("pymt_start_dt").equals(GeneralUtil.NULLTIMESTAMP) ? null : resultMap.get("pymt_start_dt"));
				outputMap.put("pymt_end_dt", resultMap.get("pymt_end_dt").equals(GeneralUtil.NULLTIMESTAMP) ? null : resultMap.get("pymt_end_dt"));
				outputMap.put("mod_ctrl_txt", resultMap.get("mod_ctrl_txt"));
				outputMap.put("pymt_schedule", new JSONArray());
				
				outputList.add(outputMap);
			}

			// Skip if el_appl_pymt_schedule_id is empty
			if (((String) resultMap.get("el_appl_pymt_schedule_id")).isBlank()) {
				continue;
			}
			
			// For recurrent, return 1 row for display only
			boolean isRecurr = PymtTypeConstants.RECURR.equals(resultMap.get("pymt_type_cde"));
			if (isRecurr && ((JSONArray) outputMap.get("pymt_schedule")).length() > 0) {
				continue;
			}
			
			JSONObject pymtScheduleJsonObj = new JSONObject();
			
			pymtScheduleJsonObj
			.put("el_appl_pymt_schedule_id", resultMap.get("el_appl_pymt_schedule_id"))
			.put("uuid", resultMap.get("el_appl_pymt_schedule_id"))
			.put("pymt_sched_no", resultMap.get("pymt_sched_no"))
			.put("pymt_amt", isRecurr ? resultMap.get("pymt_amt") : resultMap.get("schedule_pymt_amt")) // use total amount if recurrent
			.put("pymt_status_cde", resultMap.get("pymt_status_cde"))
			.put("mod_ctrl_txt", resultMap.get("schedule_mod_ctrl_txt"))
			;
			if (isRecurr) {
				pymtScheduleJsonObj.put("pymt_start_dt", resultMap.get("pymt_start_dt").equals(GeneralUtil.NULLTIMESTAMP) ? null : resultMap.get("pymt_start_dt"));
				pymtScheduleJsonObj.put("pymt_end_dt", resultMap.get("pymt_end_dt").equals(GeneralUtil.NULLTIMESTAMP) ? null : resultMap.get("pymt_end_dt"));
			} else {
				pymtScheduleJsonObj.put("pymt_start_dt", resultMap.get("pymt_start_dt").equals(GeneralUtil.NULLTIMESTAMP) ? null : resultMap.get("pymt_start_dt"));
			}
			
			((JSONArray) outputMap.get("pymt_schedule")).put(pymtScheduleJsonObj);			
		}
		
		JSONArray jsonArr = new JSONArray(outputList);
		return jsonArr;
	}
	*/
	private JSONArray getElApplBudget(String applHdrId) throws Exception {
		List<JSONObject> outputList = new ArrayList<>();
		
		List<Map<String, Object>> resultMapList = elApplBudgetRepository.findDetailsByApplHdrId(applHdrId);
		
		for (Map<String, Object> resultMap : resultMapList) {
			String elTypeId = (String) resultMap.get("el_type_id");

			JSONObject outputMap = outputList.stream()
					.filter(map -> {
						try {
							return map.get("el_type_id").equals(elTypeId);
						} catch (JSONException e) {
						}
						return false;
					}).findFirst().orElse(null);
						
			if (outputMap == null) {
				outputMap = new JSONObject();
				outputMap
				.put("el_type_id", resultMap.get("el_type_id"))
				.put("uuid", resultMap.get("el_type_id"))
				.put("el_type_descr", resultMap.get("el_type_descr"))
				.put("pymt_amt", resultMap.get("pymt_amt"))
				.put("pmt_currency", resultMap.get("pmt_currency"))
				.put("details", new JSONArray())
				;
				
				outputList.add(outputMap);
			}
			JSONObject budgetdetailObj = new JSONObject();
			
			budgetdetailObj
			.put("el_appl_budget_id", resultMap.get("el_appl_budget_id"))
			.put("uuid", resultMap.get("el_appl_budget_id"))
			.put("acct_cde", resultMap.get("acct_cde"))
			.put("analysis_cde", resultMap.get("analysis_cde"))
			.put("fund_cde", resultMap.get("fund_cde"))
			.put("proj_id", resultMap.get("proj_id"))
			.put("proj_nbr", resultMap.get("proj_nbr"))
			.put("class", resultMap.get("class"))
			.put("bco_aprv_id", resultMap.get("bco_aprv_id"))
			.put("bco_aprv_name", resultMap.get("bco_aprv_name"))
			.put("budg_acct_share", resultMap.get("budg_acct_share"))
			.put("budg_acct_amt", resultMap.get("budg_acct_amt"))
			.put("mod_ctrl_txt", resultMap.get("mod_ctrl_txt"))
			;
			
			((JSONArray) outputMap.get("details")).put(budgetdetailObj);
		}
		
		JSONArray jsonArr = new JSONArray(outputList);
		return jsonArr;
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
	@Transactional(value = "transactionManagerJsfasMain", rollbackFor = Exception.class)
	public JSONObject editPymt(String applHdrId, JSONObject inputJson, String opPageName) throws Exception {
		JSONObject outputJson = new JSONObject();
		ElApplHdrDAO hdrDAO = elApplHdrRepository.findOne(applHdrId);
		boolean isChanged = inputJson.optInt("el_appl_is_updated") == 1 ? true : false ;
		
		// validation
		if (hdrDAO == null) {
			throw new RecordNotExistException("Extra Load Application");
		}
		
		if (!hdrDAO.getModCtrlTxt().equals(inputJson.getString("mod_ctrl_txt"))) {
			throw new RecordModifiedException();
		}
		
		// 20231228 #287 Need discuss if COMPLETED allowed to edit or not (COMPLETED  will not show on payment submission list)
		List<String> validStatusList = Arrays.asList(ApplStatusConstants.READY_SUBM,
				ApplStatusConstants.PENDING_PYMT_APPR, ApplStatusConstants.PYMT_REJECTED,
				ApplStatusConstants.PENDING_DECL, ApplStatusConstants.COMPLETED);
		
		if (!validStatusList.contains(hdrDAO.getApplStatCde())) {
			throw new InvalidParameterException("Payment submission not available for edit");
		}
		
		if (hdrDAO.getBrPostInd().equalsIgnoreCase("R") || hdrDAO.getBrPostInd().equalsIgnoreCase("I") || hdrDAO.getPymtPostInd().equalsIgnoreCase("I")) {
			throw new InvalidParameterException("Payment submission not available for edit");
		}
		
		GeneralUtil.sanitizeInputJson(inputJson);
		
		if(hdrDAO.getApplStatCde().equals(ApplStatusConstants.PYMT_REJECTED)) {
			isChanged = true;
		}
		
		
		
//		if(!this.crseCheckingForPymtSubmit(hdrDAO.getId())) {
//			throw new InvalidParameterException("Course details are not yet completed.");
//		}
		
		// ETAP-118: check user course information input for payment submission
		validateElCourseRelatedDetailsForPaymentSubmit(inputJson.getInt("appl_start_term"), inputJson.optInt("appl_end_term"), inputJson.getJSONArray("el_appl_course"));
		
		
		// 20231228 #367 Call budget check function copy from ExtraLoadApplicationEventHandler
		List<Coa> coaDeltaList = new ArrayList<>();
		if(isChanged) {
			coaDeltaList = validateElApplPymtMethodAndSchedule(applHdrId, inputJson);
		}
		// validation ends
		
		// ETAP-118: Update course info
		upsertCourseRelatedDetailsForPaymentSubmit(applHdrId, inputJson.getJSONArray("el_appl_course"), opPageName);
		
		// update records
//		upsertElPymtMethod(applHdrId, inputJson.getJSONArray("el_appl_pymt_method"), opPageName);
		// 20231228 #80 Update selected payment schedule.
		upsertElPymtSchedule(applHdrId, inputJson, inputJson.optJSONArray("el_appl_mpf"), isChanged, opPageName);
		if(isChanged) {
			// 20231228 #80 Update extra load type amount.
			upsertElApplElType(applHdrId, inputJson.getJSONArray("el_appl_el_type"), opPageName);
			
			// update payroll descr
			if (inputJson.optJSONObject("el_appl_pymt_method").optString("payroll_descr").trim().isBlank()) {
				throw new InvalidParameterException("Payroll Related Description is required");
			}
			if (inputJson.optJSONObject("el_appl_pymt_method").optString("payroll_descr").trim().length() > 30) {
				throw new InvalidParameterException("Payroll Related Description is limited to 30 characters.");
			}
			hdrDAO.setPayrollDescr(GeneralUtil.initBlankString(inputJson.optJSONObject("el_appl_pymt_method").optString("payroll_descr").trim()));
			elApplHdrRepository.save(hdrDAO);
		}
//		upsertElApplBudget(applHdrId, inputJson.getJSONArray("el_appl_budget"), opPageName);

		if(ElApplActDAO.SUBMIT.equalsIgnoreCase(inputJson.getString("action"))) {
			// 20231228 #373 Will add FO_SR if submit first time
			if(hdrDAO.getPymtPostInd().equalsIgnoreCase("N") || isChanged == true) {
				insertElApplApprv(applHdrId, inputJson, isChanged, opPageName, coaDeltaList);
			}
		}
		
		inputJson.put("role", RoleConstants.REQUESTER);
		updatePymtStatus(applHdrId, inputJson, isChanged, opPageName);
		
		
		// creation record in action table
		// extraLoadApplicationService.createElApplAct(applHdrId, inputJson, opPageName);
		
		return outputJson.put("el_appl_id", hdrDAO.getId());
	}

	/*
	private void upsertElPymtMethod(String applHdrId, JSONArray inputJson, String opPageName) throws Exception {
		String remoteUser = SecurityUtils.getCurrentLogin();
		
		List<ElApplPymtMethodDAO> elApplPymtMethodDAOList = new ArrayList<>();
				
		// Rebuilt JSONArray if selected all, put el_type_id
		// Assume that in edit case that will be no "ALL"
		if (inputJson.length() == 1 && inputJson.getJSONObject(0).getString("el_type_id").equals("ALL")) {
			List<ElApplElTypeDAO> elApplElTypeDAOList = elApplElTypeRepository.findByApplHdrId(applHdrId);
			
			if (elApplElTypeDAOList.isEmpty()) {
				throw new RecordNotExistException("Extra Load Type Application Amonut Record ");
			}
			
			for (int i=0;i<elApplElTypeDAOList.size();i++) {
				if (i==0) {
					inputJson.getJSONObject(0).put("el_type_id", elApplElTypeDAOList.get(i).getElTypeId());
				} else {
					inputJson.put(new JSONObject(inputJson.getJSONObject(0).toString()).put("el_type_id", elApplElTypeDAOList.get(i).getElTypeId()));
				}
			}
		}
		
		for (int i=0; i< inputJson.length(); i++) {
			JSONObject jsonObj = inputJson.getJSONObject(i);
			String elApplPymtMethodId = jsonObj.optString("el_appl_pymt_method_id");
			boolean isInsertPymtMethod = elApplPymtMethodId.isBlank();
			
			ElApplPymtMethodDAO elApplPymtMethodDAO = null;
			if (isInsertPymtMethod) {
				elApplPymtMethodDAO = new ElApplPymtMethodDAO();
				elApplPymtMethodDAO.setCreatUser(remoteUser);
			} else {
				elApplPymtMethodDAO = elApplPymtMethodRepository.findOne(elApplPymtMethodId);
				
				if (elApplPymtMethodDAO == null) {
					throw new RecordNotExistException("Extra Load Application - Payment Details");
				}
				
				if (!elApplPymtMethodDAO.getModCtrlTxt().equals(jsonObj.getString("mod_ctrl_txt"))) {
					throw new RecordModifiedException();
				}
				
				if (!elApplPymtMethodDAO.getApplHdrId().equals(applHdrId)) {
					throw new InvalidParameterException("Extra Load Application and Payment Details does not match");
				}
				
				elApplPymtMethodDAO.setModCtrlTxt(GeneralUtil.genModCtrlTxt());
				elApplPymtMethodDAO.setChngDat(GeneralUtil.getCurrentTimestamp());
			}
			
			String applElTypeID ;
			
			ElApplElTypeDAO elApplElTypeDAO = elApplElTypeRepository.findByApplHdrIdAndElTypeId(applHdrId, jsonObj.getString("el_type_id"));
			
			if (elApplElTypeDAO == null) {
				throw new RecordNotExistException("Selected Extra load type in payment ");
			}
			
			applElTypeID = elApplElTypeDAO.getId();
			Timestamp pymnStartDt = GeneralUtil.NULLTIMESTAMP;
			Timestamp pymnEndDt = GeneralUtil.NULLTIMESTAMP;
			String pymtFreq = " ";
			
			String pymtTypeCde = jsonObj.getString("pymt_type_cde");
			JSONArray pymtScheduleArr = jsonObj.getJSONArray("pymt_schedule");
			
			try {
				if (pymtTypeCde.equals(PymtTypeConstants.ONETIME)) {
					// assume should have 1 item in pymt_schedule array only
					pymnStartDt = new Timestamp(pymtScheduleArr.getJSONObject(0).getLong("pymt_start_dt"));
					pymnEndDt = pymnStartDt;
					pymtFreq = " ";
				}
				else if(pymtTypeCde.equals(PymtTypeConstants.INSTALM)) {
					// find earliest pymt_start_dt as start date, latest pymt_start_dt as end dat
					long earliestPymtStartDt = GeneralUtil.INFINTYTIMESTAMP.getTime();
					long latestPymtStartDt = 0;
					
					for (int k = 0; k < pymtScheduleArr.length(); k++) {
					    long pymtStartDt = pymtScheduleArr.getJSONObject(k).getLong("pymt_start_dt");
					    if (pymtStartDt < earliestPymtStartDt) {
					        earliestPymtStartDt = pymtStartDt;
					    }
					    if (pymtStartDt > latestPymtStartDt) {
					    	latestPymtStartDt = pymtStartDt;
					    }
					}
					
					pymnStartDt = new Timestamp(earliestPymtStartDt);
					pymnEndDt = new Timestamp(latestPymtStartDt);
					pymtFreq = " ";
				}
				else if(pymtTypeCde.equals(PymtTypeConstants.RECURR)) {
					// assume should have 1 item in pymt_schedule array only
					pymnStartDt = new Timestamp(pymtScheduleArr.getJSONObject(0).getLong("pymt_start_dt"));
					pymnEndDt = new Timestamp(pymtScheduleArr.getJSONObject(0).getLong("pymt_end_dt"));
					
					pymtFreq = "M";
				}
			} catch (Exception e) {
				throw new InvalidDateException();
			}
			
			elApplPymtMethodDAO.setApplHdrId(applHdrId);
			elApplPymtMethodDAO.setApplElTypeId(applElTypeID);
			elApplPymtMethodDAO.setPymtLineNo(i + 1);
			elApplPymtMethodDAO.setPymtTypeCde(jsonObj.getString("pymt_type_cde"));
			elApplPymtMethodDAO.setPymtFreq(pymtFreq);
			elApplPymtMethodDAO.setPymtAmt(elApplElTypeDAO.getPymtAmt());
			elApplPymtMethodDAO.setPymtStartDt(pymnStartDt);
			elApplPymtMethodDAO.setPymtEndDt(pymnEndDt);
			
			elApplPymtMethodDAO.setChngUser(remoteUser);
			elApplPymtMethodDAO.setOpPageNam(opPageName);
			
			elApplPymtMethodDAOList.add(elApplPymtMethodDAO);
			
			jsonObj.put("appl_pymt_method_id", elApplPymtMethodDAO.getId());
		}
		
		elApplPymtMethodRepository.saveAll(elApplPymtMethodDAOList);
		
		// Delete removed records
		List<String> upsertDAOId = elApplPymtMethodDAOList.stream().map(dao -> dao.getId()).collect(Collectors.toList());
		
		List<ElApplPymtMethodDAO> removedDAOList = upsertDAOId.isEmpty()
				? elApplPymtMethodRepository.findByApplHdrId(applHdrId)
				: elApplPymtMethodRepository.findByApplHdrIdForRemove(applHdrId, upsertDAOId);

		elApplPymtMethodRepository.deleteAll(removedDAOList);
	}
	*/
	
	private void upsertCourseRelatedDetailsForPaymentSubmit(String applHdrId, JSONArray inputJson, String opPageName) throws Exception {
		String remoteUser = SecurityUtils.getCurrentLogin();
		
		List<ElApplCourseDAO> courseDAOList = new ArrayList<>();
		List<ElApplColistCourseDAO> colistCourseDAOList = new ArrayList<>();
		
		for (int i=0; i< inputJson.length(); i++) {
			JSONObject jsonObj = inputJson.getJSONObject(i);
			
			String elApplCourseId = jsonObj.optString("el_appl_course_id");
			
			// Assume only section can be updated
			// with crse offer nbr, session code and credit
//			boolean isInsert = elApplCourseId.isBlank();
			boolean isInsert = false;
			
			ElApplCourseDAO courseDAO = null;
			
			if (isInsert) {
				courseDAO = new ElApplCourseDAO();				
				courseDAO.setCreatUser(remoteUser);
				courseDAO.setApplHdrId(applHdrId);
			} else {
				courseDAO = elApplCourseRepository.findOne(elApplCourseId);
				
				if (courseDAO == null) {
					throw new RecordNotExistException("Extra Load Application - Course Details");
				}
				
				if (!courseDAO.getModCtrlTxt().equals(jsonObj.getString("mod_ctrl_txt"))) {
					throw new RecordModifiedException();
				}
				
				if (!courseDAO.getApplHdrId().equals(applHdrId)) {
					throw new InvalidParameterException("Extra Load Application and Course Details does not match");
				}
				
				courseDAO.setModCtrlTxt(GeneralUtil.genModCtrlTxt());
				courseDAO.setChngDat(GeneralUtil.getCurrentTimestamp());
			}
			
			String acad_term = jsonObj.optString("crse_cde").split("_")[0];
			String crse_id = jsonObj.optString("crse_id");
			String crse_cde = jsonObj.optString("crse_cde").split("_")[1];
			
			// find course info
			List<Map<String, Object>> resultList = elCrseClassVRepository.findCourseInfoByStrmAndCrseId(acad_term, crse_id);
			
			if(resultList.size() == 0) {
				throw new InvalidParameterException("Course Infomation cannot be found. Please verify selected courses are within selected semester.");
			}
			String crse_cde_temp = resultList.get(0).get("crse_cde").toString();
			String crse_descr = resultList.get(0).get("crse_title").toString();
			String crse_offer_nbr = resultList.get(0).get("crse_offer_nbr").toString();
			String class_session_code = resultList.get(0).get("class_session_code").toString();
			
			if(!crse_cde_temp.equals(crse_cde))
				throw new InvalidParameterException("Course Infomation cannot be found. Please verify selected courses are within selected semester.");

			
			courseDAO.setCrseTerm(acad_term);
			courseDAO.setCrseId(crse_id);
			courseDAO.setCrseCde(crse_cde);
			courseDAO.setCrseDescr(crse_descr);
			courseDAO.setCrseOfferNbr(crse_offer_nbr);
			courseDAO.setSection(jsonObj.optString("section"));
			courseDAO.setSectionCode(class_session_code);
			courseDAO.setCredit(Double.isNaN(jsonObj.optDouble("credit")) ? null : new BigDecimal(jsonObj.optDouble("credit")));
			
//			courseDAO.setCrseCoTaughtHr(Double.isNaN(jsonObj.optDouble("crse_co_taught_hr")) ? null : new BigDecimal(jsonObj.optDouble("crse_co_taught_hr")));
//			courseDAO.setCrseTotalHr(Double.isNaN(jsonObj.optDouble("crse_total_hr")) ? null : new BigDecimal(jsonObj.optDouble("crse_total_hr")));
//			courseDAO.setCrseRemark(GeneralUtil.initBlankString(jsonObj.optString("crse_remark")));
			
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
//					boolean isInsertColist = elApplColistCourseId.isBlank();
					boolean isInsertColist = false;
					
					ElApplColistCourseDAO colistDAO = null;
					
					if (isInsertColist) {
						colistDAO = new ElApplColistCourseDAO();
						colistDAO.setCreatUser(remoteUser);
						colistDAO.setApplHdrId(applHdrId);
					} else {
						colistDAO = elApplColistCourseRepository.findOne(elApplColistCourseId);
						
						if (colistDAO == null) {
							throw new RecordNotExistException("Extra Load Application - Course Details");
						}
						
						if (!colistDAO.getModCtrlTxt().equals(colistJsonObj.getString("mod_ctrl_txt"))) {
							throw new RecordModifiedException();
						}
						
						if (!colistDAO.getApplHdrId().equals(applHdrId)) {
							throw new InvalidParameterException("Extra Load Application and Course Details does not match");
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
//					String co_crse_descr = co_crse_resultList.get(0).get("crse_title").toString();
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
					
//					colistDAO.setCrseCoTaughtHr(Double.isNaN(colistJsonObj.optDouble("crse_co_taught_hr")) ? null : new BigDecimal(colistJsonObj.optDouble("crse_co_taught_hr")));
//					colistDAO.setCrseTotalHr(Double.isNaN(colistJsonObj.optDouble("crse_total_hr")) ? null : new BigDecimal(colistJsonObj.optDouble("crse_total_hr")));
					
					colistDAO.setChngUser(remoteUser);
					colistDAO.setOpPageNam(opPageName);
					
					colistCourseDAOList.add(colistDAO);
				}
			}
		}
		
		elApplCourseRepository.saveAll(courseDAOList);
		elApplColistCourseRepository.saveAll(colistCourseDAOList);
		
		// Delete removed record
//		List<String> upsertCourseDAOId = courseDAOList.stream().map(dao -> dao.getId()).collect(Collectors.toList());
//		
//		List<ElApplCourseDAO> removedCourseDAO = upsertCourseDAOId.isEmpty()
//				? elApplCourseRepository.findByApplHdrId(applHdrId)
//				: elApplCourseRepository.findByApplHdrIdForRemove(applHdrId, upsertCourseDAOId);
//		
//		elApplCourseRepository.deleteAll(removedCourseDAO);
//
//		List<String> upsertColistCourseDAOId = colistCourseDAOList.stream().map(dao -> dao.getId())
//				.collect(Collectors.toList());
//		
//		List<ElApplColistCourseDAO> removedColistCourseDAO = upsertColistCourseDAOId.isEmpty()
//				? elApplColistCourseRepository.findByApplHdrId(applHdrId)
//				: elApplColistCourseRepository.findByApplHdrIdForRemove(applHdrId, upsertColistCourseDAOId);
//		
//		elApplColistCourseRepository.deleteAll(removedColistCourseDAO);
	}
	
	
	private void upsertElPymtSchedule(String applHdrId, JSONObject inputJson, JSONArray mpfJson, boolean isChanged, String opPageName) throws Exception {
		String remoteUser = SecurityUtils.getCurrentLogin();
		Timestamp currentTime = GeneralUtil.getCurrentTimestamp();
		JSONObject methodJson = inputJson.optJSONObject("el_appl_pymt_method");
		
		List<ElApplElTypeDAO> elApplElTypeDAOList = elApplElTypeRepository.findByApplHdrId(applHdrId);
		if (elApplElTypeDAOList.isEmpty()) {
			throw new RecordNotExistException("Extra Load Type Application Amount Record ");
		}
		
		methodJson.put("el_type_id", elApplElTypeDAOList.get(0).getElTypeId());
		String applPymtMethodId = methodJson.getString("el_appl_pymt_method_id");
		List<ElApplPymtScheduleDAO> daoList = new ArrayList<>();
		//List<ElApplPymtScheduleDAO> removeDaoList = new ArrayList<>();
		JSONArray pymtScheduleArr = methodJson.optJSONArray("pymt_schedule");
		// Find payment method
		
		ElApplPymtMethodDAO elApplPymtMethodDAO = elApplPymtMethodRepository.findOne(applPymtMethodId);
		if (elApplPymtMethodDAO == null) {
			throw new RecordNotExistException("Payment Method Record");
		}
		// Find salary element mapping
		ElTypeSalElemntTabDAO salElemntDAO = elTypeSalElemntTabRepository.findByElTypeIdAndPymtTypeCdeAndObsolete(methodJson.getString("el_type_id"), methodJson.optString("pymt_type_cde"), 0);

		if (salElemntDAO == null) {
			throw new RecordNotExistException("Salary Elemtent for Extra load type in payment ");
		}
		
		String salElemnt = "";
		if (elApplHdrRepository.findOne(applHdrId).getOw2AttachedInd().equals(0)) {
			salElemnt = salElemntDAO.getSalElemnt();
		} else {
			salElemnt = salElemntDAO.getSalElemntOw2();
		}

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
		
		// 20231228 #80 User allow to select payment schedule, not default the first line.
		/*
		boolean foundFirst = false;
		int submitPymtSchedNo = 0;
		*/
		
		for (int i = 0; i < pymtScheduleArr.length(); i++) {
			JSONObject jsonObj = pymtScheduleArr.getJSONObject(i);
			
			// 20231228 #323 paid line cannot be changed
			if(jsonObj.optString("pymt_status_cde").equalsIgnoreCase(PymtStatusConstants.POST)) {
				continue;
			}
			boolean isSelected = jsonObj.optInt("selected") == 1 ? true : false;

			if(isChanged) {
				
				// Start insert
				String pymtMethodCde = methodJson.optString("pymt_type_cde");
				JSONArray schLine = jsonObj.optJSONArray("details");

				Timestamp pymnStartDt = GeneralUtil.NULLTIMESTAMP;
				Timestamp pymnEndDt = GeneralUtil.NULLTIMESTAMP;

				Long startDtLong = jsonObj.optLong("pymt_start_dt");
				Long endDtLong = !jsonObj.isNull("pymt_end_dt") ? jsonObj.optLong("pymt_end_dt") : jsonObj.optLong("pymt_start_dt");
				
				pymnStartDt = GeneralUtil.initNullTimestampFromLong(startDtLong);
				pymnEndDt = GeneralUtil.initNullTimestampFromLong(endDtLong);
				
				for (int j=0; j<schLine.length();j++) {
					JSONObject detailObj = schLine.getJSONObject(j);
					/* 20231228 #295 remove dummy boolean 
					// elApplPymtScheduleId is not passing right now.
					boolean isInsert = GeneralUtil.initBlankString(detailObj.optString("el_appl_pymt_schedule_id")).isBlank();
					boolean isChanged = checkIsChangedSchedule(detailObj);
					boolean isPaid = false;
					*/
					ElApplPymtScheduleDAO elApplPymtScheduleDAO = new ElApplPymtScheduleDAO();
					
					elApplPymtScheduleDAO.setApplHdrId(applHdrId);
					elApplPymtScheduleDAO.setApplPymtMethodId(applPymtMethodId);
					elApplPymtScheduleDAO.setPymtSchedNo(i + 1);
					elApplPymtScheduleDAO.setPymtSchedLine(j + 1);
					elApplPymtScheduleDAO.setSalElemnt(salElemnt);
					
					elApplPymtScheduleDAO.setPymtStartDt(pymnStartDt);
					elApplPymtScheduleDAO.setPymtEndDt(pymnEndDt);
					
					elApplPymtScheduleDAO.setProjId(GeneralUtil.initBlankString(detailObj.optString("proj_id")));
					elApplPymtScheduleDAO.setProjNbr(GeneralUtil.initBlankString(detailObj.optString("proj_nbr")));
					elApplPymtScheduleDAO.setDeptId(GeneralUtil.initBlankString(detailObj.optString("dept_id")));
					elApplPymtScheduleDAO.setFundCde(GeneralUtil.initBlankString(detailObj.optString("fund_cde")));
					elApplPymtScheduleDAO.setClassCde(GeneralUtil.initBlankString(detailObj.optString("class")));
					elApplPymtScheduleDAO.setAcctCde(GeneralUtil.initBlankString(detailObj.optString("acct_cde")));
					elApplPymtScheduleDAO.setAnalysisCde(GeneralUtil.initBlankString(detailObj.optString("analysis_cde")));
					
					elApplPymtScheduleDAO.setBcoAprvId(GeneralUtil.initBlankString(detailObj.optString("bco_aprv_id")));
					elApplPymtScheduleDAO.setBcoAprvName(GeneralUtil.initBlankString(detailObj.optString("bco_aprv_name")));
					
					elApplPymtScheduleDAO.setPymtLineAmt(Double.isNaN(detailObj.optDouble("pymt_amt")) ? null : new BigDecimal(detailObj.optString("pymt_amt")));
					
					if(pymtMethodCde.equals(PymtTypeConstants.RECURR) && pymnStartDt != GeneralUtil.NULLTIMESTAMP && pymnEndDt != GeneralUtil.NULLTIMESTAMP && elApplPymtScheduleDAO.getPymtLineAmt() != null) {
						elApplPymtScheduleDAO.setPymtLineAmtTot(generalApiService.calculateCOARecurrentTotalAmount(startDtLong, endDtLong, elApplPymtScheduleDAO.getPymtLineAmt()));
					}else {
						elApplPymtScheduleDAO.setPymtLineAmtTot(elApplPymtScheduleDAO.getPymtLineAmt());
					}
					
//					elApplPymtScheduleDAO.setPymtStatusCde(PymtStatusConstants.PENDING);
					
					elApplPymtScheduleDAO.setCreatUser(remoteUser);
					elApplPymtScheduleDAO.setChngUser(remoteUser);
					elApplPymtScheduleDAO.setOpPageNam(opPageName);
					// 20231228 #80 User allow to select payment schedule, not default the first line.
					if(isSelected) {
						elApplPymtScheduleDAO.setPymtStatusCde("SUBMIT");
						elApplPymtScheduleDAO.setPymtSubmitDttm(currentTime);
					}else {
						elApplPymtScheduleDAO.setPymtStatusCde(" ");
						elApplPymtScheduleDAO.setPymtSubmitDttm(GeneralUtil.NULLTIMESTAMP);
					}
					daoList.add(elApplPymtScheduleDAO);
				}

					/* 20231228 #80 User allow to select payment schedule, not default the first line.
					if(!foundFirst) {
						submitPymtSchedNo = elApplPymtScheduleDAO.getPymtSchedNo();
						foundFirst = true;
						
						elApplPymtScheduleDAO.setPymtStatusCde("SUBMIT");
						elApplPymtScheduleDAO.setPymtSubmitDttm(currentTime);
					}
					if(foundFirst && elApplPymtScheduleDAO.getPymtSchedNo() == submitPymtSchedNo) {

						elApplPymtScheduleDAO.setPymtStatusCde("SUBMIT");
						elApplPymtScheduleDAO.setPymtSubmitDttm(currentTime);
					}
					*/
				
				/* elApplPymtScheduleId is not passing right now
				if (!isChanged && !isInsert) {
					//ElApplPymtScheduleDAO elApplPymtScheduleDAO = elApplPymtScheduleRepository.findOne(elApplPymtScheduleId);
					
					if(elApplPymtScheduleDAO.getPymtStatusCde() == " " && !foundFirst) {
						submitPymtSchedNo = elApplPymtScheduleDAO.getPymtSchedNo();
						foundFirst = true;
						
						elApplPymtScheduleDAO.setPymtStatusCde("SUBMIT");
						elApplPymtScheduleDAO.setPymtSubmitDttm(currentTime);
						daoList.add(elApplPymtScheduleDAO);
					}
					if(foundFirst && elApplPymtScheduleDAO.getPymtSchedNo() == submitPymtSchedNo) {

						elApplPymtScheduleDAO.setPymtStatusCde("SUBMIT");
						elApplPymtScheduleDAO.setPymtSubmitDttm(currentTime);
						daoList.add(elApplPymtScheduleDAO);
					}
				}
				*/
			}else if(isSelected) { // 20231228 #80 if selected and not changed
				List<ElApplPymtScheduleDAO> elApplPymtScheduleDAOList = elApplPymtScheduleRepository.findSelectedByApplHdrIdAndSchedNo(applHdrId, i+1);
				for(ElApplPymtScheduleDAO dao: elApplPymtScheduleDAOList) {
					dao.setPymtStatusCde("SUBMIT");
					dao.setPymtSubmitDttm(currentTime);
					
					dao.setChngUser(remoteUser);
					dao.setChngDat(currentTime);
					dao.setOpPageNam(opPageName);
					
					daoList.add(dao);
				}
			}
				
		}
		
		if(isChanged) {
		// Assume there is only one MPF payment method
		// tmp, Test case old data may not have MPF method
		List<ElApplPymtMethodDAO> elApplMPFPymtMethodDAOList = elApplPymtMethodRepository.findMPFMethodByApplHdrId(applHdrId);

		// 20231228 #80 sum MPF for update MPF method
		BigDecimal totalMPF = new BigDecimal(0.0);
		
		// 20231228 #291 add function check MPF
		if(mpfJson != null) {
			if(dummyCheckMPF(applHdrId, mpfJson)) {
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
					
					ElApplPymtScheduleDAO elApplPymtScheduleDAO = new ElApplPymtScheduleDAO();
					
					elApplPymtScheduleDAO.setApplHdrId(applHdrId);
					elApplPymtScheduleDAO.setPymtSchedNo(detailObj.optInt("rowNum"));
					elApplPymtScheduleDAO.setPymtSchedLine(1);
					//elApplPymtScheduleDAO.setApplPymtMethodId(elApplMPFPymtMethodDAO.getId());
					if(elApplMPFPymtMethodDAOList != null && !elApplMPFPymtMethodDAOList.isEmpty()) {
						elApplPymtScheduleDAO.setApplPymtMethodId(elApplMPFPymtMethodDAOList.get(0).getId());
					}else {
						// 20231228 #413 Currently assume the MPF method already exists
						// #347 throw error if mpf method not found, assume MPF checkbox should not be allowed to change when submit for payment
						throw new RecordNotExistException("MPF Payment Method Record");
						
						//elApplPymtScheduleDAO.setApplPymtMethodId(" ");
					}
					
					elApplPymtScheduleDAO.setPymtStartDt(pymnStartDt);
					elApplPymtScheduleDAO.setPymtEndDt(pymnEndDt);
					
					elApplPymtScheduleDAO.setProjId(GeneralUtil.initBlankString(detailObj.optString("proj_id")));
					elApplPymtScheduleDAO.setProjNbr(GeneralUtil.initBlankString(detailObj.optString("proj_nbr")));
					elApplPymtScheduleDAO.setDeptId(GeneralUtil.initBlankString(detailObj.optString("dept_id")));
					elApplPymtScheduleDAO.setFundCde(GeneralUtil.initBlankString(detailObj.optString("fund_cde")));
					elApplPymtScheduleDAO.setClassCde(GeneralUtil.initBlankString(detailObj.optString("class")));
					elApplPymtScheduleDAO.setAcctCde(GeneralUtil.initBlankString(detailObj.optString("acct_cde")));
					elApplPymtScheduleDAO.setAnalysisCde(GeneralUtil.initBlankString(detailObj.optString("analysis_cde")));
					
					elApplPymtScheduleDAO.setBcoAprvId(GeneralUtil.initBlankString(detailObj.optString("bco_aprv_id")));
					elApplPymtScheduleDAO.setBcoAprvName(GeneralUtil.initBlankString(detailObj.optString("bco_aprv_name")));
					
					elApplPymtScheduleDAO.setPymtLineAmt(Double.isNaN(detailObj.optDouble("pymt_amt")) ? null : new BigDecimal(detailObj.optDouble("pymt_amt")));
					
					elApplPymtScheduleDAO.setPymtLineAmtTot(elApplPymtScheduleDAO.getPymtLineAmt());
					
	//				elApplPymtScheduleDAO.setPymtStatusCde(PymtStatusConstants.PENDING);
					
					elApplPymtScheduleDAO.setCreatUser(remoteUser);
					elApplPymtScheduleDAO.setChngUser(remoteUser);
					elApplPymtScheduleDAO.setOpPageNam(opPageName);
					
					daoList.add(elApplPymtScheduleDAO);
					
					// 20231228 sum MPF for update MPF method
					totalMPF = totalMPF.add(Double.isNaN(detailObj.optDouble("pymt_amt")) ? new BigDecimal(0.0) : new BigDecimal(detailObj.optDouble("pymt_amt")));
				}
			}
			//}
			// Case 1 One-Time and Case 2 Installment
			/*
			if (pymtTypeCde.equals(PymtTypeConstants.ONETIME) || pymtTypeCde.equals(PymtTypeConstants.INSTALM)) {
				for (int j=0; j<pymtScheduleArr.length();j++) {
					JSONObject pymtScheduleObj = pymtScheduleArr.getJSONObject(j);
					String elApplPymtMethodId = pymtScheduleObj.optString("el_appl_pymt_schedule_id");
					boolean isInsert = elApplPymtMethodId.isBlank();

					ElApplPymtScheduleDAO elApplPymtScheduleDAO = null;
					
					if (isInsert) {
						elApplPymtScheduleDAO = new ElApplPymtScheduleDAO();
						elApplPymtScheduleDAO.setCreatUser(remoteUser);
					} else {
						elApplPymtScheduleDAO = elApplPymtScheduleRepository.findOne(elApplPymtMethodId);
						
						if (elApplPymtScheduleDAO == null) {
							throw new RecordNotExistException("Extra Load Application - Payment Schedule");
						}
						
						if (!elApplPymtScheduleDAO.getModCtrlTxt().equals(pymtScheduleObj.getString("mod_ctrl_txt"))) {
							throw new RecordModifiedException();
						}
						
						if (!elApplPymtScheduleDAO.getApplHdrId().equals(applHdrId)) {
							throw new InvalidParameterException("Extra Load Application and Payment Schedule does not match");
						}
						
						elApplPymtScheduleDAO.setModCtrlTxt(GeneralUtil.genModCtrlTxt());
						elApplPymtScheduleDAO.setChngDat(GeneralUtil.getCurrentTimestamp());
					}
					
					elApplPymtScheduleDAO.setApplHdrId(applHdrId);
					elApplPymtScheduleDAO.setApplPymtMethodId(applPymtMethodId);
//					elApplPymtScheduleDAO.setPymtSchedNo(null);
					elApplPymtScheduleDAO.setSalElemnt(salElemnt);
//					elApplPymtScheduleDAO.setPymtDt(GeneralUtil.truncateDate(new Timestamp(pymtScheduleObj.getLong("pymt_start_dt"))));
					elApplPymtScheduleDAO.setPymtStatusCde(PymtStatusConstants.PENDING);
					
					// If one time, use pymt_amt saved in EL_APPL_PYMT_METHOD
					// ELSE user amount inputed by user
//					elApplPymtScheduleDAO.setPymtAmt(pymtTypeCde.equals(PymtTypeConstants.ONETIME) ? elApplPymtMethodDAO.getPymtAmt() : (new BigDecimal(pymtScheduleObj.getDouble("pymt_amt"))));
					
					elApplPymtScheduleDAO.setChngUser(remoteUser);
					elApplPymtScheduleDAO.setOpPageNam(opPageName);
					
					daoList.add(elApplPymtScheduleDAO);
				}
			}
			// Case 3 Recurrent
			// All current records in DB will be deleted
			else if (pymtTypeCde.equals(PymtTypeConstants.RECURR)) {
				JSONObject pymtScheduleObj = pymtScheduleArr.getJSONObject(0);				
				List<Timestamp> paymentDates = calculatePaymentDates(new Timestamp(pymtScheduleObj.getLong("pymt_start_dt")), new Timestamp(pymtScheduleObj.getLong("pymt_end_dt")));
				
				// Divide Payment amount by no. of periods
				BigDecimal pymtAmt = elApplPymtMethodDAO.getPymtAmt().divide(new BigDecimal(paymentDates.size()), 2, RoundingMode.HALF_UP);
				for (Timestamp paymentDate :paymentDates) {
					ElApplPymtScheduleDAO elApplPymtScheduleDAO = new ElApplPymtScheduleDAO();
					
					elApplPymtScheduleDAO.setApplHdrId(applHdrId);
					elApplPymtScheduleDAO.setApplPymtMethodId(applPymtMethodId);
//					elApplPymtScheduleDAO.setPymtSchedNo(null);
					elApplPymtScheduleDAO.setSalElemnt(salElemnt);
//					elApplPymtScheduleDAO.setPymtAmt(pymtAmt);
//					elApplPymtScheduleDAO.setPymtDt(paymentDate);
					elApplPymtScheduleDAO.setPymtStatusCde(PymtStatusConstants.PENDING);
					
					elApplPymtScheduleDAO.setCreatUser(remoteUser);
					elApplPymtScheduleDAO.setChngUser(remoteUser);
					elApplPymtScheduleDAO.setOpPageNam(opPageName);
					
					daoList.add(elApplPymtScheduleDAO);				
				}
			}
			*/
		}
		
		elApplPymtScheduleRepository.saveAll(daoList);
		
		// 20231228 #323 Prevent paid salary from remove
		daoList.addAll(elApplPymtScheduleRepository.findPostSalaryByApplHdrId(applHdrId));
		
		// Delete removed records
		List<String> upsertDAOId = daoList.stream().map(dao -> dao.getId()).collect(Collectors.toList());
		
		List<ElApplPymtScheduleDAO> removedDAOList = upsertDAOId.isEmpty()
				? elApplPymtScheduleRepository.findByApplHdrId(applHdrId)
				: elApplPymtScheduleRepository.findByApplHdrIdForRemove(applHdrId, upsertDAOId);
		
		elApplPymtScheduleRepository.deleteAll(removedDAOList);
		
		upsertPymtMethod(applHdrId, inputJson, totalMPF, opPageName);
		
		}else { // 20231228 #295 if no changes, will not remove data from database
			elApplPymtScheduleRepository.saveAll(daoList);
		}
	}
	
	
	// 20231228 #80 allow update method start end time and amount, function base on application one
	// One extra load type can have one payment method
	// One payment method has at least one payment schedule
	private void upsertPymtMethod(String applHdrId, JSONObject inputJson, BigDecimal mpfTotalAmount, String opPageName) throws Exception {
		String remoteUser = SecurityUtils.getCurrentLogin();
		String modCtrlTxt = GeneralUtil.genModCtrlTxt();
		
		// Assume: 
		// 1. application now only have 1 extra load type
		// 2. 1 extra load type only have 1 salary payment method
		
		JSONObject methodJson = inputJson.optJSONObject("el_appl_pymt_method");
		List<ElApplElTypeDAO> elApplElTypeDAOList = elApplElTypeRepository.findByApplHdrId(applHdrId);
		if (elApplElTypeDAOList.isEmpty()) {
			throw new RecordNotExistException("Extra Load Type Application Amount Record ");
		}
		
		methodJson.put("el_type_id", elApplElTypeDAOList.get(0).getElTypeId());
		
		String elApplPymtMethodId = methodJson.optString("el_appl_pymt_method_id");
		boolean isInsertPymtMethod = elApplPymtMethodId.isBlank();
		
		ElApplPymtMethodDAO elApplPymtMethodDAO = null;
		
		if (isInsertPymtMethod) {
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
		
		String elTypeId = methodJson.optString("el_type_id");
		
		if (elTypeId.isBlank()) {
			throw new InvalidParameterException("Please selecte extra load type in payment details.");
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

		List<ElApplPymtMethodDAO> ElApplPymtMethodDAOList = new ArrayList<>();
		
		// use default value 
		if (pymtScheduleArr == null || pymtScheduleArr.length() == 0) {
			
		}
		
		else {
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
			} catch (Exception e) {
				throw new InvalidDateException();
			}
		}
		
		elApplPymtMethodDAO.setPymtAmt(Double.isNaN(methodJson.optDouble("pymt_amt")) ? null : new BigDecimal(methodJson.optString("pymt_amt")));
		
		elApplPymtMethodDAO.setPymtStartDt(GeneralUtil.truncateDate(pymnStartDt));
		elApplPymtMethodDAO.setPymtEndDt(GeneralUtil.truncateDate(pymnEndDt));
		
		elApplPymtMethodDAO.setChngUser(remoteUser);
		elApplPymtMethodDAO.setOpPageNam(opPageName);
		
		ElApplPymtMethodDAOList.add(elApplPymtMethodDAO);
		
		
		ElApplPymtMethodDAO elApplMPFPymtMethodDAO = elApplPymtMethodRepository.findMPFMethodByApplHdrId(applHdrId).get(0);
		
		elApplMPFPymtMethodDAO.setPymtAmt(mpfTotalAmount);
		
		elApplMPFPymtMethodDAO.setPymtStartDt(GeneralUtil.truncateDate(pymnStartDt));
		elApplMPFPymtMethodDAO.setPymtEndDt(GeneralUtil.truncateDate(pymnEndDt));
		
		elApplMPFPymtMethodDAO.setModCtrlTxt(modCtrlTxt);
		elApplMPFPymtMethodDAO.setChngDat(GeneralUtil.getCurrentTimestamp());
		elApplMPFPymtMethodDAO.setChngUser(remoteUser);
		elApplMPFPymtMethodDAO.setOpPageNam(opPageName);

		ElApplPymtMethodDAOList.add(elApplMPFPymtMethodDAO);
		elApplPymtMethodRepository.saveAll(ElApplPymtMethodDAOList);
		elApplPymtMethodRepository.flush();
	}
	
//	private void upsertElApplBudget(String applHdrId, JSONArray inputJson, String opPageName) throws Exception {
//		String remoteUser = SecurityUtils.getCurrentLogin();
//		
//		// Rebuilt JSONArray if selected all, put el_type_id
//		if (inputJson.length() == 1 && inputJson.getJSONObject(0).getString("el_type_id").equals("ALL")) {
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
//			JSONArray detailsArr = jsonObj.getJSONArray("details");
//			
//			ElApplElTypeDAO elApplElTypeDAO = elApplElTypeRepository.findByApplHdrIdAndElTypeId(applHdrId, jsonObj.getString("el_type_id"));
//			
//			String applElTypeID ;
//			
//			if (elApplElTypeDAO == null) {
//				throw new RecordNotExistException("Selected Extra load type in payment ");
//			}
//			
//			for (int j =0;j < detailsArr.length(); j++) {
//				JSONObject detailObj = detailsArr.getJSONObject(j);
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
//				applElTypeID = elApplElTypeDAO.getId();
//				
//				dao.setApplHdrId(applHdrId);
//				dao.setApplElTypeId(applElTypeID);
//				dao.setAcctCde(GeneralUtil.initBlankString(detailObj.getString("acct_cde")));
//				dao.setAnalysisCde(GeneralUtil.initBlankString(detailObj.getString("analysis_cde")));
//				dao.setFundCde(GeneralUtil.initBlankString(detailObj.getString("fund_cde")));
//				dao.setProjId(GeneralUtil.initBlankString(detailObj.getString("proj_id")));
//				dao.setProjNbr(GeneralUtil.initBlankString(detailObj.getString("proj_nbr")));
//				dao.setClassCde(GeneralUtil.initBlankString(detailObj.getString("class")));
//				dao.setBcoAprvId(GeneralUtil.initBlankString(detailObj.getString("bco_aprv_id")));
//				dao.setBcoAprvName(GeneralUtil.initBlankString(detailObj.getString("bco_aprv_name")));
//				dao.setBudgAcctShare(new BigDecimal(detailObj.getDouble("budg_acct_share")));
//				dao.setBudgAcctAmt(new BigDecimal(detailObj.getDouble("budg_acct_amt")));
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
	
	private void upsertElApplElType(String applHdrId, JSONArray inputJson, String opPageName) throws Exception {
		String remoteUser = SecurityUtils.getCurrentLogin();
		
		List<ElApplElTypeDAO> daoList = new ArrayList<>();
		
		for (int i=0; i< inputJson.length(); i++) {
			JSONObject jsonObj = inputJson.getJSONObject(i);
			
			String elApplElTypeId = jsonObj.optString("el_appl_el_type_id");
			boolean isInsert = elApplElTypeId.isBlank();
			
			ElApplElTypeDAO dao = null;
			
			if (isInsert) {
				throw new RecordNotExistException("Extra Load Application - Extra Load Type Details");
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
			BigDecimal totalamount = new BigDecimal(0.0);
			List<ElApplPymtMethodDAO> ElApplPymtMethodDAOList = elApplPymtMethodRepository.findByApplHdrId(applHdrId);
			for(ElApplPymtMethodDAO methodDAO:ElApplPymtMethodDAOList) {
				totalamount = totalamount.add(methodDAO.getPymtAmt());
			}
			
			dao.setPymtAmt(totalamount);  
			
			dao.setChngUser(remoteUser);
			dao.setOpPageNam(opPageName);
			
			daoList.add(dao);
		}
		
		elApplElTypeRepository.saveAll(daoList);
		
		// Delete removed record
		List<String> upsertDAOId = daoList.stream().map(dao -> dao.getId()).collect(Collectors.toList());
		
		List<ElApplElTypeDAO> removedDAOList = upsertDAOId.isEmpty() ? elApplElTypeRepository.findByApplHdrId(applHdrId)
				: elApplElTypeRepository.findByApplHdrIdForRemove(applHdrId, upsertDAOId);
		
		elApplElTypeRepository.deleteAll(removedDAOList);
	}
	
	private void insertElApplApprv(String applHdrId, JSONObject inputJson, boolean isChanged, String opPageName, List<Coa> coaDeltaList) throws Exception {
		String remoteUser = SecurityUtils.getCurrentLogin();
		List<ElApplAprvStatusDAO> daoList = new ArrayList<>();
		
		// 20231228 #414 code for user input applicant, need to be moved
		// JSONArray arpvJsonList = inputJson.optJSONArray("el_appl_arpv_status");

		// Delete all old record before insert new
		List<ElApplAprvStatusDAO> currAprvDAOList = elApplAprvStatusRepository.findByApplHdrId(applHdrId);
		elApplAprvStatusRepository.deleteAll(currAprvDAOList);

		/* 20231228 #414 code for user input applicant, need to be moved
		List<String> aprvTypeList = new ArrayList<String>();
		// Add non BCO approver to new JSONArray
		JSONArray newJsonArr = new JSONArray();
		for (int i=0; i< arpvJsonList.length(); i++) {
			JSONObject jsonObj = arpvJsonList.getJSONObject(i);
			if (!(AprvTypeConstants.BCO_APPL.equals(jsonObj.optString("aprv_type_cde")) || AprvTypeConstants.FO_SR.equals(jsonObj.optString("aprv_type_cde")) || AprvTypeConstants.FO_SFM.equals(jsonObj.optString("aprv_type_cde")) || AprvTypeConstants.PROVOST.equals(jsonObj.optString("aprv_type_cde")))) {
				newJsonArr.put(jsonObj);
				if(!aprvTypeList.contains("aprv_type_cde")) {
					aprvTypeList.add(jsonObj.optString("aprv_type_cde"));
				}
			}
		}
		*/
		JSONArray newJsonArr = new JSONArray();
		
		if(isChanged) {
			// Add saved bco approver in EL_APPL_PYMT_SCHEDULE to newJsonArr if the schedule is changed.
			List<Map<String, Object>> bcoAprvMapList = elApplPymtScheduleRepository.findBcoAprvNotInElApplAprvStatusForPymt(applHdrId);
			
			// BCO seq will always be 1
			// int arpvSeq = newJsonArr.length() + 1 ;
			if (!bcoAprvMapList.isEmpty()) {
				Set<String> bcoAprvIdSet = new HashSet<>();
				for (Map<String, Object> bcoAprvMap : bcoAprvMapList) {
					Coa coa = GeneralUtil.mapToCoa(bcoAprvMap);
					// Insert bco aprv if +ve amount
					Coa coaDelta = coaDeltaList.get(coaDeltaList.indexOf(coa));
					if (coaDelta.getBalance().compareTo(new BigDecimal(0)) > 0) {
						String bcoAprvId = (String) bcoAprvMap.get("bco_aprv_id");
						
						if (!bcoAprvIdSet.contains(bcoAprvId)) {
							bcoAprvIdSet.add(bcoAprvId);
							JSONObject bcoAprvObj = new JSONObject();
							newJsonArr.put(bcoAprvObj);
							
							bcoAprvObj
							// BCO seq will always be 1
							// .put("arpv_seq", arpvSeq)
							.put("aprv_type_cde", AprvTypeConstants.BCO_APPL)
							.put("aprv_user_id", bcoAprvId)
							.put("aprv_user_name", bcoAprvMap.get("bco_aprv_name"))
							;
						}
					}
				}
			}
		}
		for (int i=1; i<= newJsonArr.length(); i++) {
			List<String> autoInsertList = Arrays.asList(AprvTypeConstants.FO_SR, AprvTypeConstants.FO_SFM, AprvTypeConstants.PROVOST);
			JSONObject jsonObj = newJsonArr.getJSONObject(i-1);
			if(autoInsertList.contains(jsonObj.optString("aprv_type_cde"))) {
				continue;
			}
			
			// no update as all record should be deleted
			
			ElApplAprvStatusDAO dao = new ElApplAprvStatusDAO();
			dao.setCreatUser(remoteUser);
			
			dao.setApplHdrId(applHdrId);
			// BCO seq will always be 1
			dao.setArpvSeq(1);
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
			
			daoList.add(dao);
		}
		List<String> foList = new ArrayList<String>();
		foList.add(AprvTypeConstants.FO_SR);
		if(checkAmount(inputJson)) {
			foList.add(AprvTypeConstants.FO_SFM);
		}
		
		for (String foAprvType : foList) {
			String roleGrpDesc = "";
			int aprvSeq = 0;
			// FO_SR seq always 2, FO_SFM seq always 3
			if (AprvTypeConstants.FO_SR.equals(foAprvType)) {
				roleGrpDesc = "FO SR";
				aprvSeq = 2;
			} else if (AprvTypeConstants.FO_SFM.equals(foAprvType)) {
				roleGrpDesc = "FO SFM";
				aprvSeq = 3;
			}
			
			// Add user under the role group as approver
			List<String> userList = userRoleGroupRepository.findByRoleGroupDesc(roleGrpDesc);
			log.debug("userList {}", userList.size());
			for (String userName: userList) {
				log.debug("userName {}", userName);
				ElApplAprvStatusDAO dao = new ElApplAprvStatusDAO();
				dao.setCreatUser(remoteUser);
				
				dao.setApplHdrId(applHdrId);
				
				dao.setArpvSeq(aprvSeq);
				dao.setAprvTypeCde(foAprvType);
				dao.setAprvUserId(userName);
				dao.setAprvUserName(" ");
				dao.setAprvRemark(" ");

				dao.setChngUser(remoteUser);
				dao.setOpPageNam(opPageName);
				
				daoList.add(dao);
			}
			
		}
		elApplAprvStatusRepository.saveAll(daoList);
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
	@Transactional(value = "transactionManagerJsfasMain", rollbackFor = Exception.class)
	public JSONObject updatePymtStatus(String applHdrId, JSONObject inputJson, boolean isChanged, String opPageName) throws Exception {
		JSONObject outputJson = new JSONObject();
		
		// validation
		ElApplHdrDAO hdrDAO = elApplHdrRepository.findOneForUpdate(applHdrId);
		
		if (hdrDAO == null) {
			throw new RecordNotExistException("Extra Load Application");
		}
		
		if (!hdrDAO.getModCtrlTxt().equals(inputJson.getString("mod_ctrl_txt"))) {
			throw new RecordModifiedException();
		}
		
		String role = inputJson.getString("role");
		String remoteUser = SecurityUtils.getCurrentLogin();
		
		ElApplAprvStatusDAO elApplAprvStatusDAO = null;
		
		// Requester
		if (RoleConstants.REQUESTER.equalsIgnoreCase(role)) {
			if (!remoteUser.equals(hdrDAO.getApplRequesterId())) {
				throw new InvalidParameterException("Not application requestor");
			}
		}
		// Approver
		else if (RoleConstants.APPROVER.equalsIgnoreCase(role)) {
			if (!ApplStatusConstants.PENDING_PYMT_APPR.equals(hdrDAO.getApplStatCde()) || 0 != hdrDAO.getObsolete()) {
				throw new InvalidParameterException("Application is not pending for payment approval");
			}
			
			List<ElApplAprvStatusDAO> pendingAprvs = elApplAprvStatusRepository.findAllNextPymtAprv(applHdrId);
			
			for (var pendingAprv : pendingAprvs) {
				if (remoteUser.equals(pendingAprv.getAprvUserId())) {
					elApplAprvStatusDAO = pendingAprv;
					break;
				}
			}
			
			if (elApplAprvStatusDAO == null) {
				throw new InvalidParameterException("Not pending payment approver");
			}
			
//			elApplAprvStatusDAO = elApplAprvStatusRepository.findNextPymtAprv(applHdrId);
//			
//			if (elApplAprvStatusDAO == null || !remoteUser.equals(elApplAprvStatusDAO.getAprvUserId())) {
//				throw new InvalidParameterException("Not pending payment approver");
//			}
		} 
		// Applicant
		else if (RoleConstants.APPLICANT.equalsIgnoreCase(role)) {
			if (!ApplStatusConstants.PENDING_DECL.equals(hdrDAO.getApplStatCde()) || 0 != hdrDAO.getObsolete()) {
				throw new InvalidParameterException("Application is not pending for applicant action");
			}
		} 
		// FO PNB
		else if (RoleConstants.FO_PNB.equalsIgnoreCase(role)) {
			if (!ApplStatusConstants.PENDING_FOPNB.equals(hdrDAO.getApplStatCde()) || 0 != hdrDAO.getObsolete()) {
				
				throw new InvalidParameterException("Application is not pending for applicant action");
			}
			
			List<ElApplAprvStatusDAO> pendingAprvs = elApplAprvStatusRepository.findAllNextPymtFOAprv(applHdrId);
			
			for (var pendingAprv : pendingAprvs) {
				if (remoteUser.equals(pendingAprv.getAprvUserId())) {
					elApplAprvStatusDAO = pendingAprv;
					break;
				}
			}
			
			if (elApplAprvStatusDAO == null) {
				throw new InvalidParameterException("Not pending payment approver");
			}
//			elApplAprvStatusDAO = elApplAprvStatusRepository.findNextPymtFOAprv(applHdrId);
//			
//			log.info(elApplAprvStatusDAO.getAprvUserId());
//			
//			if (elApplAprvStatusDAO == null || !remoteUser.equals(elApplAprvStatusDAO.getAprvUserId())) {
//				throw new InvalidParameterException("Not pending payment approver");
//			}
		} 
		// FO SR
		else if (RoleConstants.FO_RECTIFY.equalsIgnoreCase(role)) {
			if (!ApplStatusConstants.PENDING_RECTIFY.equals(hdrDAO.getApplStatCde()) || 0 != hdrDAO.getObsolete()) {
				
				throw new InvalidParameterException("Application is not pending for FO Rectification action");
			}
			
			List<ElApplAprvStatusDAO> pendingAprvs = elApplAprvStatusRepository.findAllNextFORectifyAprv(applHdrId);
			
			for (var pendingAprv : pendingAprvs) {
				if (remoteUser.equals(pendingAprv.getAprvUserId())) {
					elApplAprvStatusDAO = pendingAprv;
					break;
				}
			}
			
			if (elApplAprvStatusDAO == null) {
				throw new InvalidParameterException("Not pending payment approver");
			}
			
//			elApplAprvStatusDAO = elApplAprvStatusRepository.findNextPymtFOSRAprv(applHdrId);
//						
//			if (elApplAprvStatusDAO == null || !remoteUser.equals(elApplAprvStatusDAO.getAprvUserId())) {
//				throw new InvalidParameterException("Not pending payment approver");
//			}
		}
		else {
			throw new InvalidParameterException("Invalid role");
		}
		// validation ends
		
		// start update
		Timestamp currTS = GeneralUtil.getCurrentTimestamp();
		String modCtrlTxt = GeneralUtil.genModCtrlTxt();
		
		// Requester action: submit, remove, close, draft
		if (RoleConstants.REQUESTER.equalsIgnoreCase(role)) {
			if (ElApplActDAO.SUBMIT.equalsIgnoreCase(inputJson.getString("action"))) {
				if(!this.crseCheckingForPymtSubmit(hdrDAO.getId())) {
					throw new InvalidParameterException("Course details are not yet completed. Please update the course information.");
				}
				
				// 20231228 #415 if no approver needed, change status to PENDING_DECL
				List<ElApplAprvStatusDAO> currAprvDAOList = elApplAprvStatusRepository.findAllPendingAprv(applHdrId);
				if(isChanged) {
					hdrDAO.setBrPostInd("N");
					if(generalApiService.checkHistVersionSameAsCurrent(applHdrId)) {
						hdrDAO.setVersionNo(hdrDAO.getVersionNo() + 1);
					}
				}
				if(currAprvDAOList != null && currAprvDAOList.size() > 0) {
					hdrDAO.setApplStatCde(ApplStatusConstants.PENDING_PYMT_APPR);
					hdrDAO.setPymtPostInd("S");
					// send email to notify approver
					sendEmailToPendingAprv(applHdrId, opPageName);
					createMyAiTaskForPendingAprv(applHdrId);
				} else {
					//20240123 route to fo pnb review
//					hdrDAO.setApplStatCde(ApplStatusConstants.PYMT_SUBM);
//					hdrDAO.setPymtPostInd("I");
					hdrDAO.setApplStatCde(ApplStatusConstants.PENDING_FOPNB);
					hdrDAO.setPymtPostInd("S");
					
					insertFOPNBApprv(applHdrId, opPageName);
				}
			}
			else if (ElApplActDAO.DRAFT.equalsIgnoreCase(inputJson.getString("action"))) {
				hdrDAO.setApplStatCde(ApplStatusConstants.READY_SUBM);
			}
			// withdrow
			else if (ElApplActDAO.REMOVE.equalsIgnoreCase(inputJson.getString("action")) || ElApplActDAO.CLOSE.equalsIgnoreCase(inputJson.getString("action"))) {
				int paidScheNo = elApplPymtScheduleRepository.countPaidScheduleNo(applHdrId);
				if (hdrDAO.getApplStatCde().equals(ApplStatusConstants.APPROVED) && hdrDAO.getBrPostInd().equals("Y")) {
					// Check not generating LoA
					throw new InvalidParameterException("Application is not allow to withdraw or close.");
				}
				if (hdrDAO.getBrPostInd().equalsIgnoreCase("R") || hdrDAO.getBrPostInd().equalsIgnoreCase("I") || hdrDAO.getPymtPostInd().equalsIgnoreCase("I")) {
					// Check not pending integration
					throw new InvalidParameterException("Application is not allow to withdraw or close.");
				}
				if (!(hdrDAO.getApplStatCde().equals(ApplStatusConstants.READY_SUBM) || hdrDAO.getApplStatCde().equals(ApplStatusConstants.PYMT_REJECTED)
						|| hdrDAO.getApplStatCde().equals(ApplStatusConstants.ISSUE_OFFER) || hdrDAO.getApplStatCde().equals(ApplStatusConstants.OFFER_REJECTED))) {
					// Check Hdr status
					throw new InvalidParameterException("Application is not allow to withdraw or close.");
				}
				
				if(ElApplActDAO.REMOVE.equalsIgnoreCase(inputJson.getString("action"))) { // not paid, withdraw
					if(paidScheNo != 0) {
						throw new InvalidParameterException("Paid application cannot be withdrawed");
					}
					if(!hdrDAO.getBrNo().isBlank()) {
						hdrDAO.setBrPostInd("R");
					}
					
					sendEmailForWithdrawAppl(applHdrId, opPageName);
					commonRoutineService.callAIWorkListCompleteAPI(hdrDAO.getApplNbr(), "false", "cancelled");
				}else if (ElApplActDAO.CLOSE.equalsIgnoreCase(inputJson.getString("action"))){ // paid
					if(paidScheNo <= 0) {
						throw new InvalidParameterException("Not paid application cannot be closed");
					}
					if(hdrDAO.getBrNo().isBlank()) {
						throw new InvalidParameterException("Not BR intergrated application cannot be closed");
					}
					
					sendEmailForClosedAppl(applHdrId, opPageName);
					commonRoutineService.callAIWorkListCompleteAPI(hdrDAO.getApplNbr(), "true", "completed");
				}
				if(generalApiService.checkHistVersionSameAsCurrent(applHdrId)) {
					hdrDAO.setVersionNo(hdrDAO.getVersionNo() + 1);
				}
				hdrDAO.setApplStatCde(ApplStatusConstants.REMOVED);
				hdrDAO.setObsolete(1);
				
				if(!hdrDAO.getBrNo().isBlank()) {
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
			}
			hdrDAO.setChngDat(currTS);
			hdrDAO.setChngUser(remoteUser);
			hdrDAO.setOpPageNam(opPageName);
			hdrDAO.setModCtrlTxt(modCtrlTxt);
		} 
		// Applicant action : declare
		else if (RoleConstants.APPLICANT.equalsIgnoreCase(role)) {
			// TODO: keep version
			// 20240123 should be no used? route to FO pnb team
//			hdrDAO.setApplStatCde(ApplStatusConstants.PYMT_SUBM);
//			hdrDAO.setPymtPostInd("I");
			hdrDAO.setApplStatCde(ApplStatusConstants.PENDING_FOPNB);
			hdrDAO.setPymtPostInd("S");
			
			insertFOPNBApprv(applHdrId, opPageName);
			
			hdrDAO.setChngDat(currTS);
			hdrDAO.setChngUser(remoteUser);
			hdrDAO.setOpPageNam(opPageName);
			hdrDAO.setModCtrlTxt(modCtrlTxt);
		}
		/*
		else if (RoleConstants.APPLICANT.equalsIgnoreCase(role)) {
			hdrDAO.setApplStatCde(ApplStatusConstants.COMPLETED);
			
			hdrDAO.setChngDat(currTS);
			hdrDAO.setChngUser(remoteUser);
			hdrDAO.setOpPageNam(opPageName);
			hdrDAO.setModCtrlTxt(modCtrlTxt);
		}
		*/
		// Approver action : approve, reject
		else if (RoleConstants.APPROVER.equalsIgnoreCase(role)) {
			// For update MyAdmin, 
			List<String> pendingAprvIds = elApplAprvStatusRepository.findAllNextPymtAprv(applHdrId).stream()
					.map(dao -> dao.getAprvUserId()).collect(Collectors.toList());
			
			boolean isApprove = ElApplActDAO.APPROVE.equalsIgnoreCase(inputJson.getString("action"));
			
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
			
			// Delete other approver with same type
			List<String> foAprvTypeList = Arrays.asList(new String[] {AprvTypeConstants.FO_SR, AprvTypeConstants.FO_SFM, AprvTypeConstants.FO_PNB});
			
			if (foAprvTypeList.contains(elApplAprvStatusDAO.getAprvTypeCde())) {
				if(!(elApplAprvStatusDAO.getAprvTypeCde().equalsIgnoreCase(RoleConstants.FO_SFM) && !isApprove)) {
					List<ElApplAprvStatusDAO> removeList = elApplAprvStatusRepository.findByApplHdrIdAndAprvTypeCdeAndIdNot(applHdrId, elApplAprvStatusDAO.getAprvTypeCde(), elApplAprvStatusDAO.getId());
	
					elApplAprvStatusRepository.deleteAll(removeList);
				}
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
//				ElApplAprvStatusDAO nextPendingAprv = elApplAprvStatusRepository.findNextPymtAprv(applHdrId);
				
				// TODO notify workflow
				if (pendingAprvs.size() > 0) {
					sendEmailToPendingAprv(applHdrId, opPageName);
					
					commonRoutineService.callAIWorkListApprvAPI(hdrDAO.getApplNbr(), "true", remoteUser, "Pending Approval by " + String.join(", ", pendingAprvIds));
					createMyAiTaskForPendingAprv(applHdrId);
				} else {
					if(hdrDAO.getBrPostInd().equals("N")) {
						hdrDAO.setBrPostInd("R");
					}
					// 20240123: route to fo pnb team before submit payment
//					hdrDAO.setApplStatCde(ApplStatusConstants.PYMT_SUBM);
//					hdrDAO.setPymtPostInd("I");
					hdrDAO.setApplStatCde(ApplStatusConstants.PENDING_FOPNB);
					hdrDAO.setPymtPostInd("S");
					
					insertFOPNBApprv(applHdrId, opPageName);
					
					hdrDAO.setChngDat(currTS);
					hdrDAO.setChngUser(remoteUser);
					hdrDAO.setOpPageNam(opPageName);
					hdrDAO.setModCtrlTxt(modCtrlTxt);
					
					// Check if version updated
					if (!generalApiService.checkHistVersionSameAsCurrent(applHdrId)) {

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
					
					// update my ai
//					commonRoutineService.callAIWorkListApprvAPI(hdrDAO.getApplNbr(), "true", remoteUser, "Pending Approval by " + remoteUser);
					createMyAiTaskForFoPNB(applHdrId);
				}
				
			} 
			// if reject, update header status to PYMT_RETURNED
			else {
				// TODO: if rejected, set sumbit to " "
				// if reject by FO_SFM, pend to FO_SR
				if(elApplAprvStatusDAO.getAprvTypeCde().equalsIgnoreCase(RoleConstants.FO_SFM)) {
//					System.out.println("rejected by SFM");
					List<ElApplAprvStatusDAO> oldFoSRList = elApplAprvStatusRepository.findFoSRByByApplHdrId(applHdrId);
					List<ElApplAprvStatusDAO> newFoList = new ArrayList<>();
					List<String> userList = userRoleGroupRepository.findByRoleGroupDesc("FO SR");
					log.debug("userList {}", userList.size());
					for (String userName: userList) {
						log.debug("userName {}", userName);
						ElApplAprvStatusDAO dao = new ElApplAprvStatusDAO();
						dao.setCreatUser(remoteUser);
						
						dao.setApplHdrId(applHdrId);
						
						dao.setArpvSeq(2);
						dao.setAprvTypeCde(AprvTypeConstants.FO_SR);
						dao.setAprvUserId(userName);
						dao.setAprvUserName(" ");
						dao.setAprvRemark(" ");

						dao.setChngUser(remoteUser);
						dao.setOpPageNam(opPageName);
						
						newFoList.add(dao);
					}
					elApplAprvStatusRepository.deleteAll(oldFoSRList);
					elApplAprvStatusRepository.saveAll(newFoList);
					
					sendEmailToPendingAprv(applHdrId, opPageName);
					
					commonRoutineService.callAIWorkListApprvAPI(hdrDAO.getApplNbr(), "true", remoteUser, "Pending Approval by " + String.join(", ", pendingAprvIds));
					createMyAiTaskForPendingAprv(applHdrId);
				} else {
					hdrDAO.setApplStatCde(ApplStatusConstants.PYMT_REJECTED);
	
					System.out.println(elApplAprvStatusDAO.getAprvTypeCde());
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
		} else if (RoleConstants.FO_PNB.equalsIgnoreCase(role)) {
			boolean isApprove = ElApplActDAO.APPROVE.equalsIgnoreCase(inputJson.getString("action"));
			
			// update elApplAprvStatusDAO
			elApplAprvStatusDAO.setApproved(isApprove ? 1 : 0);
			elApplAprvStatusDAO.setAprvDttm(currTS);
			elApplAprvStatusDAO.setAprvRemark(GeneralUtil.initBlankString(inputJson.optString("aprv_remark")));
			elApplAprvStatusDAO.setChngDat(currTS);
			elApplAprvStatusDAO.setChngUser(remoteUser);
			elApplAprvStatusDAO.setOpPageNam(opPageName);
			elApplAprvStatusDAO.setModCtrlTxt(modCtrlTxt);
			
			// Delete other approver with same type
			List<ElApplAprvStatusDAO> removeList = elApplAprvStatusRepository.findByApplHdrIdAndAprvTypeCdeAndIdNot(applHdrId, elApplAprvStatusDAO.getAprvTypeCde(), elApplAprvStatusDAO.getId());

			elApplAprvStatusRepository.deleteAll(removeList);
			
			// if approve, check if there are next approver
			// if yes, workflow to send email
			// Else, update header to PENDING_DECL
			if (isApprove) {
				hdrDAO.setApplStatCde(ApplStatusConstants.PYMT_SUBM);
				hdrDAO.setPymtPostInd("I");
				
				hdrDAO.setChngDat(currTS);
				hdrDAO.setChngUser(remoteUser);
				hdrDAO.setOpPageNam(opPageName);
				hdrDAO.setModCtrlTxt(modCtrlTxt);
				
				// MuAdmin
				createMyAiTaskForPymtSubmitted(applHdrId);
			} 
			// if reject, update header status to PYMT_RETURNED
			else {
				// TODO: if rejected, set sumbit to " "
				hdrDAO.setApplStatCde(ApplStatusConstants.PYMT_REJECTED);
				
				
				
				sendEmailToRequestorForPymtReject(applHdrId, opPageName);
//				commonRoutineService.callAIWorkListApprvAPI(hdrDAO.getApplNbr(), "false", remoteUser, "Pending Approval by " + remoteUser);
				createMyAiTaskForRejection(hdrDAO);
				

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
				
				
				hdrDAO.setChngDat(currTS);
				hdrDAO.setChngUser(remoteUser);
				hdrDAO.setOpPageNam(opPageName);
				hdrDAO.setModCtrlTxt(modCtrlTxt);
			}
		} else if (RoleConstants.FO_RECTIFY.equalsIgnoreCase(role)) {
			// assume approve only
			// update elApplAprvStatusDAO
			elApplAprvStatusDAO.setApproved(1);
			elApplAprvStatusDAO.setAprvDttm(currTS);
			elApplAprvStatusDAO.setAprvRemark(GeneralUtil.initBlankString(inputJson.optString("aprv_remark")));
			elApplAprvStatusDAO.setChngDat(currTS);
			elApplAprvStatusDAO.setChngUser(remoteUser);
			elApplAprvStatusDAO.setOpPageNam(opPageName);
			elApplAprvStatusDAO.setModCtrlTxt(modCtrlTxt);
			
			hdrDAO.setApplStatCde(ApplStatusConstants.READY_SUBM);
			hdrDAO.setBrPostInd("Y");
			
			hdrDAO.setChngDat(currTS);
			hdrDAO.setChngUser(remoteUser);
			hdrDAO.setOpPageNam(opPageName);
			hdrDAO.setModCtrlTxt(modCtrlTxt);
			
			// Delete other approver records with same type
			List<ElApplAprvStatusDAO> removeList = elApplAprvStatusRepository.findByApplHdrIdAndAprvTypeCdeAndIdNot(applHdrId, elApplAprvStatusDAO.getAprvTypeCde(), elApplAprvStatusDAO.getId());

			elApplAprvStatusRepository.deleteAll(removeList);
			
			// MyAdmin task
			loaService.createMyAdminTaskForReadySum(hdrDAO);
		}
		
		// creation record in action table
		if (!RoleConstants.REQUESTER.equalsIgnoreCase(role) && !RoleConstants.APPLICANT.equalsIgnoreCase(role)) {
			if (elApplAprvStatusDAO != null) {
				inputJson.put("role", elApplAprvStatusDAO.getAprvTypeCde());
			}
		}
		
		generalApiService.createElApplAct(applHdrId, inputJson, "", "", "pymt", opPageName);
		
		return outputJson;
	}

	@Transactional(value = "transactionManagerJsfasMain", rollbackFor = Exception.class)
	public void generatorHRMSPayment(String user, String opPageName) throws Exception {
		
		List<String> applHdrIdList = elApplHdrRepository.findPendingPaymentId();
		
		DecimalFormat decimal = new DecimalFormat("0.000000#");
		int piBatchSeq = 1;
		String modCtrlTxt = GeneralUtil.genModCtrlTxt();
		String timestamp = modCtrlTxt.substring(0, 14);
		Timestamp currentTime = GeneralUtil.getCurrentTimestamp();
		
		List<Map<String, Object>> pymtDtl = new ArrayList<>();
		for(String applHdrId : applHdrIdList) {
			List<Map<String, Object>> eachPymtDtl = elApplHdrRepository.findPaymentNotRecurrById(applHdrId);
			pymtDtl.addAll(eachPymtDtl);
		}
		double totalAmt = 0.0;
		
		if(!pymtDtl.isEmpty()) {
			for (Map<String, Object> dtl : pymtDtl) {
				totalAmt += ((BigDecimal)dtl.get("pymt_line_amt")).doubleValue();
			}
	
			String piFileName = "ELS_" + timestamp + "_PI" + String.format("%04d",piBatchSeq) + ".txt";
			File newFile = new File(env.getProperty(AppConstants.UPLOAD_PI_DIR) + "/" + piFileName);
			//newFile.createNewFile();
			FileOutputStream piOutput = new FileOutputStream(env.getProperty(AppConstants.UPLOAD_PI_DIR) + "/" + piFileName);
			//FileOutputStream piOutput = new FileOutputStream("C:\\tmp\\data\\el\\pi\\" + piFileName);
			BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(piOutput));
			//writer.write(StringUtils.rightPad((String)dtl.get("appl_user_id"), 11));
		
			for (Map<String, Object> dtl : pymtDtl) {
				if(StringUtils.isBlank(dtl.get("br_no").toString()) || !"Y".equalsIgnoreCase(dtl.get("br_post_ind").toString())) {
					log.debug("A line of payment (PI) being skip when generating HRMS file duo to missing BR number");
					continue;
				}
				writer.write((String)dtl.get("appl_user_emplid").toString().substring(0,Math.min(dtl.get("appl_user_emplid").toString().length(), 11)));
				writer.write("\t");
				writer.write(((BigDecimal)dtl.getOrDefault("emp_rec_nbr",999)).toString());
				writer.write("\t");
				writer.write((String)dtl.get("sal_elemnt") + " HKG");
				writer.write("\t");
				if(!GeneralUtil.isBlankTimestamp(GeneralUtil.convertStringToTimestamp((String)dtl.get("pymt_rev_start_dt").toString()))) {
					writer.write((String)dtl.get("pymt_rev_start_dt").toString());
				} else {
					writer.write((String)dtl.get("pymt_start_dt").toString());
				}
				writer.write("\t");
				if(!GeneralUtil.isBlankTimestamp(GeneralUtil.convertStringToTimestamp((String)dtl.get("pymt_rev_end_dt").toString()))) {
					writer.write((String)dtl.get("pymt_rev_end_dt").toString());
				} else {
					writer.write((String)dtl.get("pymt_end_dt").toString());
				}
				writer.write("\t");
				writer.write((String)dtl.get("pmt_currency"));
				writer.write("\t");
				writer.write("");	// Rate
				writer.write("\t");
				writer.write(""); // Unit
				writer.write("\t");
				writer.write(""); // Base
				writer.write("\t");
				writer.write(""); // Percent
				writer.write("\t");
				writer.write(decimal.format(dtl.get("pymt_line_amt")));
				writer.write("\t");
				writer.write("A");
				writer.write("\t");
				writer.write((String)dtl.get("appl_start_dt").toString());
				writer.write("\t");
				writer.write((String)dtl.get("appl_end_dt").toString());
				writer.write("\t");
				
				List<ElApplCourseDAO> eacDAOlist = elApplCourseRepository.findByApplHdrId((String)dtl.get("appl_hdr_id"));
				String dsc = "";
				if (!((String) dtl.get("payroll_descr")).isBlank()) {
					dsc = (String) dtl.get("payroll_descr");
				}
				else if(eacDAOlist.size() != 0) {
					for(ElApplCourseDAO eacDAO : eacDAOlist) {
						if(!dsc.isEmpty()) {
							dsc += "," + eacDAO.getCrseCde();
						}else {
							dsc = eacDAO.getCrseCde();
						}
					}
					dsc = dsc.substring(0, Math.min(dsc.length(), 30));
				}else {
					dsc = (String)dtl.get("el_type_descr");
					dsc = dsc.substring(0, Math.min(dsc.length(), 30));
				}
				writer.write(dsc);
				
	
				writer.write("\t");
				writer.write(dtl.get("acct_cde").equals(" ") ? "*" : (String)dtl.get("acct_cde").toString());
				writer.write("\t");
				writer.write(dtl.get("analysis_cde").equals(" ") ? "*" : (String)dtl.get("analysis_cde").toString());
				writer.write("\t");
				writer.write(dtl.get("fund_cde").equals(" ") ? "*" : (String)dtl.get("fund_cde").toString());
				writer.write("\t");
				writer.write(dtl.get("dept_id").equals(" ") ? "*" : (String)dtl.get("dept_id").toString());
				writer.write("\t");
				writer.write(dtl.get("proj_id").equals(" ") ? "*" : (String)dtl.get("proj_id").toString());
				writer.write("\t");
				writer.write(dtl.get("class").equals(" ") ? "*" : (String)dtl.get("class").toString());
				writer.write("\t");
				
				String brFullNo = dtl.get("br_no").toString() 
						+ "-"
						+ String.format("%02d", ((BigDecimal) dtl.get("br_line_no")).intValue())
						+ String.format("%02d", ((BigDecimal) dtl.get("br_dist_line_no")).intValue());
				
				writer.write(brFullNo);
				writer.write("\t");
				writer.write("");	// Dummy
				writer.write("\t");
				writer.write("");	// Pay ERN
				writer.write("\t");
				writer.write("");	// SOVR PIN Code 1
				writer.write("\t");
				writer.write("");	// SOVR Character Value 1
				writer.write("\t");
				writer.write("");	// SOVR Number Value 1
				writer.write("\t");
				writer.write("");	// SOVR Date Value 1
				writer.write("\t");
				writer.write("");	// SOVR PIN Code 2
				writer.write("\t");
				writer.write("");	// SOVR Character Value 2
				writer.write("\t");
				writer.write("");	// SOVR Number Value 2
				writer.write("\t");
				writer.write("");	// SOVR Date Value 2
				writer.write("\t");
				writer.write("");	// SOVR PIN Code 3
				writer.write("\t");
				writer.write("");	// SOVR Character Value 3
				writer.write("\t");
				writer.write("");	// SOVR Number Value 3
				writer.write("\t");
				writer.write("");	// SOVR Date Value 3
				writer.write("\t");
				writer.write((String)dtl.get("hash_value").toString());	// Hash Value
				writer.newLine();
			}
		writer.close();
		piBatchSeq++;
		}
			

		List<Map<String, Object>> pymtDtl2 = new ArrayList<>();
		for(String applHdrId : applHdrIdList) {
			List<Map<String, Object>> eachPymtDtl2 = elApplHdrRepository.findPaymentIsRecurrById(applHdrId);
			pymtDtl2.addAll(eachPymtDtl2);
		}
		totalAmt = 0.0;
		int eaBatchSeq = 1;
		if(!pymtDtl2.isEmpty()) {
			for (Map<String, Object> dtl : pymtDtl2) {
				totalAmt += ((BigDecimal)dtl.get("pymt_line_amt")).doubleValue();
			}
		
			String eaFileName = "ELS_" + timestamp + "_EA" + String.format("%04d",eaBatchSeq) + ".txt";
			File newFile = new File(env.getProperty(AppConstants.UPLOAD_EA_DIR) + "/" + eaFileName);
			//newFile.createNewFile();
			FileOutputStream eaOutput = new FileOutputStream(env.getProperty(AppConstants.UPLOAD_EA_DIR) + "/" + eaFileName);
			//FileOutputStream eaOutput = new FileOutputStream("C:\\tmp\\data\\el\\ea\\" + eaFileName);
			BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(eaOutput));
			//writer.write(StringUtils.rightPad((String)dtl.get("appl_user_id"), 11));
			for (Map<String, Object> dtl : pymtDtl2) {
				if(StringUtils.isBlank(dtl.get("br_no").toString()) || !"Y".equalsIgnoreCase(dtl.get("br_post_ind").toString())) {
					log.debug("A line of payment (EA) being skip when generating HRMS file duo to missing BR number");
					continue;
				}
				writer.write((String)dtl.get("appl_user_emplid").toString().substring(0,Math.min(dtl.get("appl_user_emplid").toString().length(), 11)));
				writer.write("\t");
				writer.write(((BigDecimal)dtl.getOrDefault("emp_rec_nbr",999)).toString());
				writer.write("\t");
				writer.write((String)dtl.get("sal_elemnt") + " HKG");
				writer.write("\t");
				if(!GeneralUtil.isBlankTimestamp(GeneralUtil.convertStringToTimestamp((String)dtl.get("pymt_rev_start_dt").toString()))) {
					writer.write((String)dtl.get("pymt_rev_start_dt").toString());
				} else {
					writer.write((String)dtl.get("pymt_start_dt").toString());
				}
				writer.write("\t");
				if(!GeneralUtil.isBlankTimestamp(GeneralUtil.convertStringToTimestamp((String)dtl.get("pymt_rev_end_dt").toString()))) {
					writer.write((String)dtl.get("pymt_rev_end_dt").toString());
				} else {
					writer.write((String)dtl.get("pymt_end_dt").toString());
				}
				writer.write("\t");
				writer.write((String)dtl.get("pmt_currency"));
				writer.write("\t");
				writer.write("");	// Rate
				writer.write("\t");
				writer.write(""); // Unit
				writer.write("\t");
				writer.write(""); // Base
				writer.write("\t");
				writer.write(""); // Percent
				writer.write("\t");
				writer.write(decimal.format(dtl.get("pymt_line_amt")));
				writer.write("\t");
				if(!GeneralUtil.isBlankTimestamp(GeneralUtil.convertStringToTimestamp((String)dtl.get("pymt_rev_start_dt").toString()))) {
					writer.write((String)dtl.get("pymt_rev_start_dt").toString());
				} else {
					writer.write((String)dtl.get("pymt_start_dt").toString());
				}
				writer.write("\t");
				if(!GeneralUtil.isBlankTimestamp(GeneralUtil.convertStringToTimestamp((String)dtl.get("pymt_rev_end_dt").toString()))) {
					writer.write((String)dtl.get("pymt_rev_end_dt").toString());
				} else {
					writer.write((String)dtl.get("pymt_end_dt").toString());
				}
				writer.write("\t");
				
				writer.write(dtl.get("acct_cde").equals(" ") ? "*" : (String)dtl.get("acct_cde").toString());
				writer.write("\t");
				writer.write(dtl.get("analysis_cde").equals(" ") ? "*" : (String)dtl.get("analysis_cde").toString());
				writer.write("\t");
				writer.write(dtl.get("fund_cde").equals(" ") ? "*" : (String)dtl.get("fund_cde").toString());
				writer.write("\t");
				writer.write(dtl.get("dept_id").equals(" ") ? "*" : (String)dtl.get("dept_id").toString());
				writer.write("\t");
				writer.write(dtl.get("proj_id").equals(" ") ? "*" : (String)dtl.get("proj_id").toString());
				writer.write("\t");
				writer.write(dtl.get("class").equals(" ") ? "*" : (String)dtl.get("class").toString());
				writer.write("\t");
				
				String brFullNo = dtl.get("br_no").toString() 
						+ "-"
						+ String.format("%02d", ((BigDecimal) dtl.get("br_line_no")).intValue())
						+ String.format("%02d", ((BigDecimal) dtl.get("br_dist_line_no")).intValue());
				
				writer.write(brFullNo);
				writer.write("\t");
				writer.write(""); // Dummy
				writer.write("\t");
				writer.write("");	// Pay ERN
				writer.write("\t");
				writer.write("");	// SOVR PIN Code 1
				writer.write("\t");
				writer.write("");	// SOVR Character Value 1
				writer.write("\t");
				writer.write("");	// SOVR Number Value 1
				writer.write("\t");
				writer.write("");	// SOVR Date Value 1
				writer.write("\t");
				writer.write("");	// SOVR PIN Code 2
				writer.write("\t");
				writer.write("");	// SOVR Character Value 2
				writer.write("\t");
				writer.write("");	// SOVR Number Value 2
				writer.write("\t");
				writer.write("");	// SOVR Date Value 2
				writer.write("\t");
				writer.write("");	// SOVR PIN Code 3
				writer.write("\t");
				writer.write("");	// SOVR Character Value 3
				writer.write("\t");
				writer.write("");	// SOVR Number Value 3
				writer.write("\t");
				writer.write("");	// SOVR Date Value 3
				writer.write("\t");
				writer.write((String)dtl.get("hash_value").toString());	// Hash Value
				writer.newLine();
			}
			writer.close();
			eaBatchSeq++;
		}
		
		List<ElApplHdrDAO> hdrList = elApplHdrRepository.findAllById(applHdrIdList);
		List<ElApplHdrDAO> updateHdrList = new ArrayList<>();
		List<ElApplPymtScheduleDAO> updatescheduleList =  new ArrayList<>();
		for(ElApplHdrDAO hdrDAO : hdrList) {
			boolean allPosted = true;
			boolean brPostError = false;
			List<ElApplPymtScheduleDAO> scheduleList = elApplPymtScheduleRepository.findSalaryByApplHdrId(hdrDAO.getId());
				for (ElApplPymtScheduleDAO scheDAO : scheduleList) {
					if(StringUtils.isBlank(scheDAO.getBrNo()) || !"Y".equalsIgnoreCase(scheDAO.getBrPostInd())) {
						brPostError = true;
						continue;
					}
					if(PymtStatusConstants.SUBMIT.equals(scheDAO.getPymtStatusCde()) && scheDAO.getPymtSubmitDttm() != GeneralUtil.NULLTIMESTAMP) {
						scheDAO.setPymtStatusCde(PymtStatusConstants.POST);
						
						scheDAO.setChngUser(user);
						scheDAO.setChngDat(currentTime);
						scheDAO.setModCtrlTxt(modCtrlTxt);
						
						updatescheduleList.add(scheDAO);
					}else {
						if(!scheDAO.getPymtStatusCde().equals(PymtStatusConstants.POST)) allPosted = false;
					}
				}
				if(brPostError) {
					log.debug("{} being skip when generating HRMS file duo to missing BR number", hdrDAO.getApplNbr());
					continue;
				}
				if(allPosted) {
					hdrDAO.setApplStatCde(ApplStatusConstants.COMPLETED);
					hdrDAO.setPymtPostInd("C");
					
					// Update MyAdmin
					commonRoutineService.callAIWorkListCompleteAPI(hdrDAO.getApplNbr(), "true", "completed");
				}else {
					hdrDAO.setApplStatCde(ApplStatusConstants.READY_SUBM);
					hdrDAO.setPymtPostInd("P");
					
					// Update MyAdmin
					createMyAdminTaskForReadySum(hdrDAO);
				}
				hdrDAO.setChngUser(user);
				hdrDAO.setChngDat(currentTime);
				hdrDAO.setModCtrlTxt(modCtrlTxt);
				
				updateHdrList.add(hdrDAO);
				
				generalApiService.createElApplAct(hdrDAO.getId(), null, ElApplActDAO.INTEGRATE_HRMS, "", "system", opPageName);
		}
		
		elApplPymtScheduleRepository.saveAll(updatescheduleList);
		elApplHdrRepository.saveAll(updateHdrList);
	}
	
	public JSONArray getHRMSPaymentFileForPreview(String applHdrId) throws Exception {
		JSONArray outputJson = new JSONArray();
		
		ElApplHdrDAO applHdrDao = elApplHdrRepository.findOne(applHdrId);
		String emplid = applHdrDao.getApplUserEmplid();
		
		ElInpostStaffImpVDAO staffDAO = elInpostStaffImpVRepository.findPrimaryByEmplid(emplid);
		String TOS = staffDAO != null ? staffDAO.getTos() : " ";
		
		DecimalFormat decimal = new DecimalFormat("0.000000#");
		List<ElApplPymtMethodDAO> daoList = elApplPymtMethodRepository.findSalaryMethodByApplHdrId(applHdrId);
		String type = " ";
		
		
		
		for(ElApplPymtMethodDAO dao : daoList ) {
			if(!GeneralUtil.isBlankString(dao.getPymtTypeCde()))
				type = dao.getPymtTypeCde();
		}
		
		if(type.equals("RECURR")) {
			// EA
			List<Map<String, Object>> eachPymtDtl2 = elApplHdrRepository.findPaymentIsRecurrById(applHdrId);
			
			for (Map<String, Object> dtl : eachPymtDtl2) {
				JSONObject lineJson = new JSONObject();

				String effdt = "";
				if(!GeneralUtil.isBlankTimestamp(GeneralUtil.convertStringToTimestamp((String)dtl.get("pymt_rev_start_dt").toString()))) {
					effdt = (String)dtl.get("pymt_rev_start_dt").toString();
				} else {
					effdt = (String)dtl.get("pymt_start_dt").toString();
				}
				
				ElJobDataVDAO jobDataDAO = elJobDataVRepository.findByEmplidWithClosestEffdt(emplid, GeneralUtil.convertStringToTimestamp(effdt));

				lineJson.put("schedule_id", (String)dtl.get("schedule_id"));
				
				lineJson.put("emp_rec_nbr", dtl.getOrDefault("emp_rec_nbr", "999"));
				
				lineJson.put("sal_elemnt", (String)dtl.get("sal_elemnt") + " HKG");

				if(!GeneralUtil.isBlankTimestamp(GeneralUtil.convertStringToTimestamp((String)dtl.get("pymt_rev_start_dt").toString()))) {
					lineJson.put("pymt_start_dt", (String)dtl.get("pymt_rev_start_dt").toString());
				} else if(!GeneralUtil.isBlankTimestamp(GeneralUtil.convertStringToTimestamp((String)dtl.get("pymt_start_dt").toString()))) {
					lineJson.put("pymt_start_dt", (String)dtl.get("pymt_start_dt").toString());
				} else {
					lineJson.put("pymt_start_dt", " ");
				}
				
				if(!GeneralUtil.isBlankTimestamp(GeneralUtil.convertStringToTimestamp((String)dtl.get("pymt_rev_end_dt").toString()))) {
					lineJson.put("pymt_end_dt", (String)dtl.get("pymt_rev_end_dt").toString());
				} else if(!GeneralUtil.isBlankTimestamp(GeneralUtil.convertStringToTimestamp((String)dtl.get("pymt_end_dt").toString()))) {
					lineJson.put("pymt_end_dt", (String)dtl.get("pymt_end_dt").toString());
				} else {
					lineJson.put("pymt_end_dt", " ");
				}
				lineJson.put("pmt_currency", (String)dtl.get("pmt_currency"));
				lineJson.put("pymt_line_amt", decimal.format(dtl.get("pymt_line_amt")));
				if(!GeneralUtil.isBlankTimestamp(GeneralUtil.convertStringToTimestamp((String)dtl.get("pymt_rev_start_dt").toString()))) {
					lineJson.put("appl_start_dt", (String)dtl.get("pymt_rev_start_dt").toString());
				} else if(!GeneralUtil.isBlankTimestamp(GeneralUtil.convertStringToTimestamp((String)dtl.get("pymt_start_dt").toString()))) {
					lineJson.put("appl_start_dt", (String)dtl.get("pymt_start_dt").toString());
				} else {
					lineJson.put("appl_start_dt", " ");
				}
				if(!GeneralUtil.isBlankTimestamp(GeneralUtil.convertStringToTimestamp((String)dtl.get("pymt_rev_end_dt").toString()))) {
					lineJson.put("appl_end_dt", (String)dtl.get("pymt_rev_end_dt").toString());
				} else if(!GeneralUtil.isBlankTimestamp(GeneralUtil.convertStringToTimestamp((String)dtl.get("pymt_end_dt").toString()))) {
					lineJson.put("appl_end_dt", (String)dtl.get("pymt_end_dt").toString());
				} else {
					lineJson.put("appl_end_dt", " ");
				}
				
				lineJson.put("acct_cde", dtl.get("acct_cde").equals(" ") ? "*" : (String)dtl.get("acct_cde").toString());
				lineJson.put("analysis_cde", dtl.get("analysis_cde").equals(" ") ? "*" : (String)dtl.get("analysis_cde").toString());
				lineJson.put("fund_cde", dtl.get("fund_cde").equals(" ") ? "*" : (String)dtl.get("fund_cde").toString());
				lineJson.put("dept_id", dtl.get("dept_id").equals(" ") ? "*" : (String)dtl.get("dept_id").toString());
				lineJson.put("proj_id", dtl.get("proj_id").equals(" ") ? "*" : (String)dtl.get("proj_id").toString());
				lineJson.put("class", dtl.get("class").equals(" ") ? "*" : (String)dtl.get("class").toString());
				
				String brFullNo = dtl.get("br_no").toString() 
						+ "-"
						+ String.format("%02d", ((BigDecimal) dtl.get("br_line_no")).intValue())
						+ String.format("%02d", ((BigDecimal) dtl.get("br_dist_line_no")).intValue());

				if(brFullNo.trim().equals("-0000")) brFullNo = "-";
				lineJson.put("br_no", brFullNo);
				lineJson.put("tos", TOS);
				lineJson.put("pay_group", jobDataDAO != null ? jobDataDAO.getPayGroup() : " ");
				
				outputJson.put(lineJson);
			}
		} else if(type.equals("INSTALM")) {
			// PI
			List<Map<String, Object>> eachPymtDtl = elApplHdrRepository.findPaymentNotRecurrById(applHdrId);
			
			for (Map<String, Object> dtl : eachPymtDtl) {
				
				JSONObject lineJson = new JSONObject();

				String effdt = "";
				if(!GeneralUtil.isBlankTimestamp(GeneralUtil.convertStringToTimestamp((String)dtl.get("pymt_rev_start_dt").toString()))) {
					effdt = (String)dtl.get("pymt_rev_start_dt").toString();
				} else {
					effdt = (String)dtl.get("pymt_start_dt").toString();
				}
				
				ElJobDataVDAO jobDataDAO = elJobDataVRepository.findByEmplidWithClosestEffdt(emplid, GeneralUtil.convertStringToTimestamp(effdt));

				lineJson.put("schedule_id", (String)dtl.get("schedule_id"));
				
				lineJson.put("emp_rec_nbr", dtl.getOrDefault("emp_rec_nbr", "999"));
				
				lineJson.put("sal_elemnt", (String)dtl.get("sal_elemnt") + " HKG");

				if(!GeneralUtil.isBlankTimestamp(GeneralUtil.convertStringToTimestamp((String)dtl.get("pymt_rev_start_dt").toString()))) {
					lineJson.put("pymt_start_dt", (String)dtl.get("pymt_rev_start_dt").toString());
				} else if(!GeneralUtil.isBlankTimestamp(GeneralUtil.convertStringToTimestamp((String)dtl.get("pymt_start_dt").toString()))) {
					lineJson.put("pymt_start_dt", (String)dtl.get("pymt_start_dt").toString());
				} else {
					lineJson.put("pymt_start_dt", " ");
				}
				if(!GeneralUtil.isBlankTimestamp(GeneralUtil.convertStringToTimestamp((String)dtl.get("pymt_rev_end_dt").toString()))) {
					lineJson.put("pymt_end_dt", (String)dtl.get("pymt_rev_end_dt").toString());
				} else if(!GeneralUtil.isBlankTimestamp(GeneralUtil.convertStringToTimestamp((String)dtl.get("pymt_end_dt").toString()))) {
					lineJson.put("pymt_end_dt", (String)dtl.get("pymt_end_dt").toString());
				} else {
					lineJson.put("pymt_end_dt", " ");
				}
				
				lineJson.put("pmt_currency", (String)dtl.get("pmt_currency"));
				lineJson.put("pymt_line_amt", decimal.format(dtl.get("pymt_line_amt")));
				if(!GeneralUtil.isBlankTimestamp(GeneralUtil.convertStringToTimestamp((String)dtl.get("appl_start_dt").toString()))) {
					lineJson.put("appl_start_dt", (String)dtl.get("appl_start_dt").toString());
				} else {
					lineJson.put("appl_start_dt", " ");
				}
				if(!GeneralUtil.isBlankTimestamp(GeneralUtil.convertStringToTimestamp((String)dtl.get("appl_end_dt").toString()))) {
					lineJson.put("appl_end_dt", (String)dtl.get("appl_end_dt").toString());
				} else {
					lineJson.put("appl_end_dt", " ");
				}

				
				List<ElApplCourseDAO> eacDAOlist = elApplCourseRepository.findByApplHdrId((String)dtl.get("appl_hdr_id"));
				String dsc = "";
				if(eacDAOlist.size() != 0) {
					for(ElApplCourseDAO eacDAO : eacDAOlist) {
						if(!dsc.isEmpty()) {
							dsc += "," + eacDAO.getCrseCde();
						}else {
							dsc = eacDAO.getCrseCde();
						}
					}
					dsc = dsc.substring(0, Math.min(dsc.length(), 30));
				}else {
					dsc = (String)dtl.get("el_type_descr");
					dsc = dsc.substring(0, Math.min(dsc.length(), 30));
				}
				
				lineJson.put("el_type_descr", dsc);
				
				lineJson.put("acct_cde", dtl.get("acct_cde").equals(" ") ? "*" : (String)dtl.get("acct_cde").toString());
				lineJson.put("analysis_cde", dtl.get("analysis_cde").equals(" ") ? "*" : (String)dtl.get("analysis_cde").toString());
				lineJson.put("fund_cde", dtl.get("fund_cde").equals(" ") ? "*" : (String)dtl.get("fund_cde").toString());
				lineJson.put("dept_id", dtl.get("dept_id").equals(" ") ? "*" : (String)dtl.get("dept_id").toString());
				lineJson.put("proj_id", dtl.get("proj_id").equals(" ") ? "*" : (String)dtl.get("proj_id").toString());
				lineJson.put("class", dtl.get("class").equals(" ") ? "*" : (String)dtl.get("class").toString());
				
				
				String brFullNo = dtl.get("br_no").toString() 
						+ "-"
						+ String.format("%02d", ((BigDecimal) dtl.get("br_line_no")).intValue())
						+ String.format("%02d", ((BigDecimal) dtl.get("br_dist_line_no")).intValue());
				
				lineJson.put("br_no", brFullNo);
				lineJson.put("tos", TOS);

				lineJson.put("pay_group", jobDataDAO != null ? jobDataDAO.getPayGroup() : " ");
				
				outputJson.put(lineJson);
			}
		}
		
		return outputJson;
	}
	
	private boolean checkAmount(JSONObject obj) {
		List<ElApplPymtMethodDAO> methodList = elApplPymtMethodRepository.findByApplHdrId(obj.optString("el_appl_hdr_id"));
		BigDecimal totalAmount = new BigDecimal(0.0);
		for(ElApplPymtMethodDAO dao:methodList) {
			totalAmount = totalAmount.add(dao.getPymtAmt());
		}
		if(totalAmount.compareTo(new BigDecimal(50000)) > 0) {
			// if larger than 50000
			return true;
		}
		return false;
	}
	
	private boolean dummyCheckMPF(String applHdrId, JSONArray mpfJSON) {
		return true;
	}
	
	private boolean crseCheckingForPymtSubmit(String applHdrId) throws Exception {
		// ETAP-118 checking any course 
		List<ElApplCourseDAO> elCrseDAOlist = elApplCourseRepository.findByApplHdrIdForCourseNotInSIS(applHdrId);
		
		if(elCrseDAOlist.size() == 0)
			return true;
		else
			return false;
	}
	
	private void validateElCourseRelatedDetailsForPaymentSubmit(Integer startTerm, Integer endTerm, JSONArray inputJson) throws Exception {
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
			
			if (courseJsonObj.optString("section").equals("-")) {
				throw new InvalidParameterException("Course details are not yet completed. Please update the course information.");
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

	// 20231228 #367 Call budget check function copy from ExtraLoadApplicationEventHandler
	private List<Coa> validateElApplPymtMethodAndSchedule(String applHdrId, JSONObject inputJson) throws Exception {
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
			
			String pymtStatus = pymtSchedulObj.optString("pymt_status_cde");
			
			if(!"POST".equals(pymtStatus)) {
				// for pymt dat checking
				JSONObject objForPymtDatChking = new JSONObject();
				objForPymtDatChking.put("sched_no", i+1)
								.put("pymt_type", pymtTypeCde)
								.put("pymt_start_dt", pymtStartDt)
								.put("pymt_end_dt", pymtStartDt)
								.put("type", "salary");
				
				jsonArrForPymtDatChcking.put(objForPymtDatChking);
			}
			
			JSONArray pymtDetails = pymtSchedulObj.optJSONArray("details");
			for (int k=0; k < pymtDetails.length(); k++) {
				JSONObject detailObj = pymtDetails.getJSONObject(k);
				// proj_id && dept_id
				if (GeneralUtil.initBlankString(detailObj.optString("proj_id")).isBlank() && GeneralUtil.initBlankString(detailObj.optString("dept_id")).isBlank()) {
					throw new InvalidParameterException("Either Project Id or Department is required");
				}
				if (!(detailObj.isNull("pro_id") || GeneralUtil.initBlankString(detailObj.optString("proj_id")).isBlank()) && !(detailObj.isNull("pro_id") || GeneralUtil.initBlankString(detailObj.optString("dept_id")).isBlank())) {
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
								.put("pymt_end_dt", pymtEndDt)
								.put("proj_id", detailObj.optString("proj_id"))
								.put("type", "salary");
				
				coaLineForPjPreiodChecking.put(objForPjPeriodCk);
				
			}
		}
		BigDecimal checkingMethodAmount = new BigDecimal(pymtMethod.optString("pymt_amt", "0")).setScale(2);
		if(!checkingMethodAmount.equals(checkingTotalAmount)) {
			throw new InvalidParameterException("Payment Total Amount is invalid, please update any COA amount and try again.");
		}
		
		JSONArray mpfJson = inputJson.optJSONArray("el_appl_mpf");
		if(mpfJson != null) {
			for (int j=0; j< mpfJson.length(); j++) {
				JSONObject detailObj = mpfJson.getJSONObject(j);
				System.out.println(detailObj.optDouble("pymt_amt"));
				// proj_id && dept_id
				if (GeneralUtil.initBlankString(detailObj.optString("proj_id")).isBlank() && GeneralUtil.initBlankString(detailObj.optString("dept_id")).isBlank()) {
					throw new InvalidParameterException("Either Project Id or Department is required");
				}
				if (!(detailObj.isNull("pro_id") || GeneralUtil.initBlankString(detailObj.optString("proj_id")).isBlank()) && !(detailObj.isNull("pro_id") || GeneralUtil.initBlankString(detailObj.optString("dept_id")).isBlank())) {
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
		}
		
		extraLoadApplicationService.validatePaymentDate(jsonArrForPymtDatChcking);
		extraLoadApplicationService.validateProjectPreiod(coaLineForPjPreiodChecking);
		
		// Get the Coa Delta from last approved pymt
		List<Coa> coaDeltaList = getCoaDiffForPymtAmendment(applHdrId, coaLineJsonArr);

		for (var newCoa: coaDeltaList) {
			log.debug(newCoa.toString());
		}
		// No need to do budget check if -ve amount
		JSONArray newCoaLineArr = new JSONArray();
		
		for (int x=0; x< coaLineJsonArr.length(); x++) {
			Coa coa = new Coa(coaLineJsonArr.getJSONObject(x));
			
			Coa coaDelta = coaDeltaList.get(coaDeltaList.indexOf(coa));
			if (coaDelta.getBalance().compareTo(new BigDecimal(0)) > 0) {
				newCoaLineArr.put(coaLineJsonArr.getJSONObject(x));
			}
		}
		
		// If No increased pymt amt, no need call API
		if (newCoaLineArr.length() > 0) {
			// TODO: MPF also check apprvs after bco info input
			extraLoadApplicationService.validateApplicationApprvs(newCoaLineArr);
			
			JSONArray budgetCheckResult = generalApiService.getBudgetCheckResult(newCoaLineArr);
			
			if (budgetCheckResult.length() == 0) {
				throw new Exception("Error when budget check");
			}
			List<String> errMsgList = new ArrayList<>();

			List<String> throwErrMsgList = new ArrayList<>();
			for (int i=0; i<budgetCheckResult.length(); i++) {
				JSONObject lineResult = budgetCheckResult.getJSONObject(i);
				log.debug("lineResult: {}", lineResult);
				
				if ("N".equals(lineResult.getString("COAlineResult"))) {
					String errMsg = "Line " + lineResult.get("lineRef") + ": " + lineResult.get("COAlineErrMsg");
					errMsgList.add(errMsg);
					if("01".equals(lineResult.getString("COAlineErrCode")) || "02".equals(lineResult.getString("COAlineErrCode")) || "04".equals(lineResult.getString("COAlineErrCode")) || "10".equals(lineResult.getString("COAlineErrCode"))) {
						throwErrMsgList.add(errMsg);
					}
				}
			}
			
//			if (errMsgList.size() > 0) { Jira 45
//				throw new InvalidParameterException("Budget Check Error: \n " + String.join(" \n ", errMsgList));
//			}
			if (throwErrMsgList.size() > 0) {
				throw new InvalidParameterException("Budget Check Error: \n " + String.join(" \n ", throwErrMsgList));
			}
		}

		return coaDeltaList;
	}
	
	private void sendEmailToPendingAprv(String applHdrId, String opPageName) throws Exception {
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
	
	private String transformAprvTypeDesc(ElApplAprvStatusDAO elApplAprvStatusDAO) {
		String role;
		switch(elApplAprvStatusDAO.getAprvTypeCde()) {
			case AprvTypeConstants.BCO_APPL:
			case AprvTypeConstants.BCO_PYMT:
				role = "Budget Control Officer" ;
				break;
			case AprvTypeConstants.FO_SR:
				role = "FO SR Team" ;
				break;
			case AprvTypeConstants.FO_SFM:
				role = "FO SFM" ;
				break;
			case AprvTypeConstants.FO_PNB:
				role = "FO P&B" ;
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
	
	private void insertFOPNBApprv(String applHdrId, String opPageName) throws Exception {
		String remoteUser = SecurityUtils.getCurrentLogin();

		// insert FO P&B Approver for payment
		
		List<ElApplAprvStatusDAO> daoList= new ArrayList<>();
		// Add user under the role group as approver
		List<String> userList = userRoleGroupRepository.findByRoleGroupDesc("FO P&B");
		
		for (String userName: userList) {	
			ElApplAprvStatusDAO dao = new ElApplAprvStatusDAO();
			
			dao.setApplHdrId(applHdrId);
			// FO P&B Team always to be the last approver
			dao.setArpvSeq(99);
			dao.setAprvTypeCde(AprvTypeConstants.FO_PNB);
			dao.setAprvUserId(userName);
			dao.setAprvUserName(" ");		
			dao.setAprvRemark(" ");
			
			dao.setCreatUser(remoteUser);
			dao.setChngUser(remoteUser);
			dao.setOpPageNam(opPageName);
			
			daoList.add(dao);
		}
		elApplAprvStatusRepository.saveAll(daoList);
	}

	@Override
	public List<Coa> getCoaDiffForPymtAmendment(String applHdrId, JSONArray coaLineJsonArr) throws Exception {
		// Get original coa from pymt history table		
		List<Coa> originalCoaList = elApplPymtScheduleHistRepository.findLatestApprovedPymtGroupByCoa(applHdrId)
				.stream().map(GeneralUtil::mapToCoa).collect(Collectors.toList());
		
		// Convert new Coa into COA object for comparison
		List<Coa> newCoaList = new ArrayList<>();
		for (int i=0; i<coaLineJsonArr.length();i++) {
			JSONObject coaObj = coaLineJsonArr.getJSONObject(i);
			Coa newCoa = new Coa(coaLineJsonArr.getJSONObject(i));
			
			int index = newCoaList.indexOf(newCoa);
			
			if (index == -1) {
				newCoaList.add(newCoa);
			} else {
				Coa sameCoa = newCoaList.get(index);
				sameCoa.setBalance(sameCoa.getBalance().add(new BigDecimal(coaObj.optDouble("pymt_amt"))));
			}
		}

		// calculate the delta
		for (Coa newCoa : newCoaList) {
			int index = originalCoaList.indexOf(newCoa);
			if (index != -1) {
				newCoa.setBalance(newCoa.getBalance().subtract(originalCoaList.get(index).getBalance()));
			}
		}
		
		return newCoaList;
	}
	
	private void sendEmailForWithdrawAppl(String applHdrId, String opPageName) throws Exception {
		// Send to submitter, department head, BCO, ad hoc approver
		// Find past application approvers
		List<String> approvers = elApplActRepository.findPastApproversBeforeReleaseLOA(applHdrId);
		
		// find current approver
		List<ElApplAprvStatusDAO> elApplAprvStatusDAOList = elApplAprvStatusRepository.findAllPendingNonFOAprv(applHdrId);
		
		for (var dao : elApplAprvStatusDAOList) {
			approvers.add(dao.getAprvUserId());
		}
		
		// Filter Provost and distinct
		String aprvProstId = generalApiService.getProvostAprv().getString("aprv_id");
		approvers = approvers.stream().filter(user -> !user.equals(aprvProstId)).distinct().collect(Collectors.toList());
		
		ElApplHdrDAO elApplHdrDAO = elApplHdrRepository.findOne(applHdrId);		
		String applNbr = elApplHdrDAO.getApplNbr();
		
		List<ElEmNotificationDAO> elEmNotificationDAOList = new ArrayList<>();
		
		for (var aprvUserId : approvers) {
			String emailFrom = " ";
			String emailTo = aprvUserId + "@ust.hk";
			String subject = "Extra Load Application (Appl no. ${applNbr}) Withdrawn".replace("${applNbr}", applNbr);
			String content = 
					"Dear " + aprvUserId + ", " + "<br>"
					+ "<br>" 
					+ "An extra load application (Appl no. ${applNbr}) is withdrawn." 
					+ "<br><br>"
//					+ "Role : ${role}"
					+ "<br><br>"
					;
			content = content.replace("${applNbr}", applNbr);

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
	
	private void sendEmailForClosedAppl(String applHdrId, String opPageName) throws Exception {
		// Send to submitter, department head, BCO, ad hoc approver
		// Find past application approvers
		List<String> approvers = elApplActRepository.findPastApproversBeforeReleaseLOA(applHdrId);
		
		// find current approver
		List<ElApplAprvStatusDAO> elApplAprvStatusDAOList = elApplAprvStatusRepository.findAllPendingNonFOAprv(applHdrId);
		
		for (var dao : elApplAprvStatusDAOList) {
			approvers.add(dao.getAprvUserId());
		}
		
		// Filter Provost and distinct
		String aprvProstId = generalApiService.getProvostAprv().getString("aprv_id");
		
		approvers = approvers.stream().filter(user -> !user.equals(aprvProstId)).distinct().collect(Collectors.toList());
		
		ElApplHdrDAO elApplHdrDAO = elApplHdrRepository.findOne(applHdrId);		
		String applNbr = elApplHdrDAO.getApplNbr();
		
		List<ElEmNotificationDAO> elEmNotificationDAOList = new ArrayList<>();
		
		for (var aprvUserId : approvers) {
			String emailFrom = " ";
			String emailTo = aprvUserId + "@ust.hk";
			String subject = "Extra Load Application (Appl no. ${applNbr}) Closed".replace("${applNbr}", applNbr);
			String content = 
					"Dear " + aprvUserId + ", " + "<br>"
					+ "<br>" 
					+ "An extra load application (Appl no. ${applNbr}) is closed." 
					+ "<br><br>"
//					+ "Role : ${role}"
					+ "<br><br>"
					;
			content = content.replace("${applNbr}", applNbr);

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
	
	private void createMyAiTaskForPendingAprv(String applHdrId) throws Exception {
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
	
	private void createMyAiTaskForFoPNB(String applHdrId) throws Exception {
		String webAppURLPrefix = env.getProperty(AppConstants.PATH_APP_PREFIX);
//		ElApplAprvStatusDAO elApplAprvStatusDAO = elApplAprvStatusRepository.findNextPymtAprv(applHdrId);
		ElApplHdrDAO elApplHdrDAO = elApplHdrRepository.findOne(applHdrId);
		List<ElApplAprvStatusDAO> pendingAprvs = elApplAprvStatusRepository.findAllNextPymtFOAprv(applHdrId);
		
		if (pendingAprvs.size() == 0) {
			throw new RecordNotExistException("Pending Approver");
		}
		
		List<String> assignee = new ArrayList<>();
		
		for (var elApplAprvStatusDAO : pendingAprvs) {
			assignee.add(elApplAprvStatusDAO.getAprvUserId());
		}
		
		String applNbr = elApplHdrDAO.getApplNbr();
		String subject = "Pending FO P&B Review for Extra Load Payment Submission (Appl no. ${applNbr})".replace("${applNbr}", applNbr)
				+ " (Applicant : " + elApplHdrDAO.getApplUserName() + ")";
		
		try {
			JSONObject params = new JSONObject();
			params.put("request_id", elApplHdrDAO.getApplNbr());
			params.put("initiator", elApplHdrDAO.getApplRequesterId());
			params.put("action", "Pending Review by " + String.join(", ", assignee));
			params.put("receiver", new JSONArray(assignee));
			params.put("message", subject);
			params.put("url", webAppURLPrefix + "/extra_load_payment/approve?id=" + applHdrId);

			commonRoutineService.callAIWorkListNotiAPI(params);
		} catch(Exception e) {
			log.error(e.toString());
		}
	}
	
	private void createMyAiTaskForPymtSubmitted(String applHdrId) throws Exception {
		ElApplHdrDAO elApplHdrDAO = elApplHdrRepository.findOne(applHdrId);
		
		String applNbr = elApplHdrDAO.getApplNbr();
		String subject = "Extra Load Payment Submission (Appl no. ${applNbr})".replace("${applNbr}", applNbr)
				 + " (Applicant : " + elApplHdrDAO.getApplUserName() + ")";
		
		try {
			JSONObject params = new JSONObject();
			params.put("request_id", elApplHdrDAO.getApplNbr());
			params.put("initiator", elApplHdrDAO.getApplRequesterId());
			params.put("action", "Payment Submitted");
			params.put("message", subject);

			commonRoutineService.callAIWorkListNotiAPI(params);
		} catch(Exception e) {
			log.error(e.toString());
		}
		
	}
	
	private void createMyAdminTaskForReadySum(ElApplHdrDAO hdrDAO) {
		String webAppURLPrefix = env.getProperty(AppConstants.PATH_APP_PREFIX);
		String applNbr = hdrDAO.getApplNbr();
		String applUserName = hdrDAO.getApplUserName();
		
		String subject = "Extra Load Application (Appl no. ${applNbr})".replace("${applNbr}", applNbr) + " (Applicant : " + applUserName + ")";
		
		try {
			JSONObject params = new JSONObject();
			params.put("request_id", hdrDAO.getApplNbr());
			params.put("initiator", hdrDAO.getApplRequesterId());
			params.put("action", "Ready for Payment Submission");
			params.put("receiver", new JSONArray().put(hdrDAO.getApplRequesterId()));
			params.put("message", subject);
			params.put("url", webAppURLPrefix + "/extra_load_payment/view?id=" + hdrDAO.getId());

			commonRoutineService.callAIWorkListNotiAPI(params);
		} catch(Exception e) {
			log.error(e.toString());
		}
	}	
	
	@Override
	public JSONArray getHRMSPaymentFileForListing(JSONObject inputJson) throws Exception {
		JSONArray outputJson = new JSONArray();
		DecimalFormat decimal = new DecimalFormat("0.000000#");
		
		String tos = GeneralUtil.refineParam(inputJson.optString("tos").trim());
		String applName = GeneralUtil.refineParam(inputJson.optString("appl_name").trim());
		String pin = GeneralUtil.refineParam(inputJson.optString("pin").replace(" HKG", "").trim());
		Timestamp startDate = GeneralUtil.initNullTimestampFromLong(inputJson.optLong("start_date"));
		Timestamp endDate = GeneralUtil.initNullTimestampFromLong(inputJson.optLong("end_date"));
		if(endDate.equals(GeneralUtil.NULLTIMESTAMP)) {
			endDate = GeneralUtil.INFINTYTIMESTAMP;
		}
		String payMethod = GeneralUtil.refineParam(inputJson.optString("pymt_type_cde").trim());
		String brNo = inputJson.optString("br_no").trim();
		
		String dateFilter = inputJson.optString("date_filter");
		if(StringUtils.isNotBlank(dateFilter) && (dateFilter.equals("less") || dateFilter.equals("equal") || dateFilter.equals("greater"))
				&& inputJson.optLong("start_date") != 0 && inputJson.optLong("end_date") != 0) {
			throw new InvalidParameterException("Only start date or end date is required when selecting less, equal or greater.");
		}else if(endDate.before(startDate)){
			throw new InvalidParameterException("End date cannot before start date.");
		}
		
//		List<Map<String, Object>> results = elApplHdrRepository.searchPendingForPNB(tos, emplId, applName, pin, startDate, endDate, amount, payMethod);

		List<Map<String, Object>> results = new ArrayList<>();
		if(StringUtils.isBlank(dateFilter)) {
			results = elApplHdrRepository.searchPendingForPNB(tos, applName, pin, startDate, endDate, payMethod);
		}else {
			results = elApplHdrRepository.searchPendingForPNB(tos, applName, pin, GeneralUtil.NULLTIMESTAMP, GeneralUtil.INFINTYTIMESTAMP, payMethod);
		}

		List<String> scheduleIdList = new ArrayList<>();
		List<String> hdrIdList = new ArrayList<>();
		
		for(Map<String, Object> result: results) {

			JSONObject lineJson = new JSONObject();

			String effdt = "";
			if(!GeneralUtil.isBlankTimestamp(GeneralUtil.convertStringToTimestamp((String)result.get("pymt_rev_start_dt").toString()))) {
				effdt = (String)result.get("pymt_rev_start_dt").toString();
			} else {
				effdt = (String)result.get("pymt_start_dt").toString();
			}
			List<ElInpostStaffImpVDAO> elInpostStaffImpVDAOList = elInpostStaffImpVRepository.findByEmplid((String) result.get("appl_user_emplid"));
			ElJobDataVDAO jobDataDAO = elJobDataVRepository.findByEmplidWithClosestEffdt((String) result.get("appl_user_emplid"), GeneralUtil.convertStringToTimestamp(effdt));
			
			lineJson.put("multi_job", elInpostStaffImpVDAOList.size() > 1);
			
			lineJson.put("appl_schedule_id", result.get("appl_schedule_id"));
			lineJson.put("el_appl_hdr_id", result.get("appl_hdr_id"));
			lineJson.put("appl_nbr", result.get("appl_nbr"));
			lineJson.put("appl_user_emplid", result.get("appl_user_emplid"));
			lineJson.put("appl_user_name", result.get("appl_user_name"));
			lineJson.put("currency", result.get("pmt_currency"));
			lineJson.put("pymt_type_cde", result.get("pymt_type_cde"));
			
			lineJson.put("last_approval_time", result.get("last_approval_time"));
			
			lineJson.put("emp_rec_nbr", result.getOrDefault("emp_rec_nbr", "999"));
			
			lineJson.put("sal_element", (String)result.get("sal_elemnt") + " HKG");
			
			if(!GeneralUtil.isBlankTimestamp(GeneralUtil.convertStringToTimestamp((String)result.get("pymt_rev_start_dt").toString()))) {
				lineJson.put("pymt_start_dt", (String)result.get("pymt_rev_start_dt").toString());
			} else if(!GeneralUtil.isBlankTimestamp(GeneralUtil.convertStringToTimestamp((String)result.get("pymt_start_dt").toString()))) {
				lineJson.put("pymt_start_dt", (String)result.get("pymt_start_dt").toString());
			} else {
				lineJson.put("pymt_start_dt", " ");
			}

			if(!GeneralUtil.isBlankTimestamp(GeneralUtil.convertStringToTimestamp((String)result.get("pymt_rev_end_dt").toString()))) {
				lineJson.put("pymt_end_dt", (String)result.get("pymt_rev_end_dt").toString());
			} else if(!GeneralUtil.isBlankTimestamp(GeneralUtil.convertStringToTimestamp((String)result.get("pymt_end_dt").toString()))) {
				lineJson.put("pymt_end_dt", (String)result.get("pymt_end_dt").toString());
			} else {
				lineJson.put("pymt_end_dt", " ");
			}
			lineJson.put("pmt_currency", (String)result.get("pmt_currency"));
			lineJson.put("pymt_line_amt", decimal.format(result.get("pymt_line_amt")));
			if(!GeneralUtil.isBlankTimestamp(GeneralUtil.convertStringToTimestamp((String)result.get("appl_start_dt").toString()))) {
				lineJson.put("appl_start_dt", (String)result.get("appl_start_dt").toString());
			} else {
				lineJson.put("appl_start_dt", " ");
			}
			if(!GeneralUtil.isBlankTimestamp(GeneralUtil.convertStringToTimestamp((String)result.get("appl_end_dt").toString()))) {
				lineJson.put("appl_end_dt", (String)result.get("appl_end_dt").toString());
			} else {
				lineJson.put("appl_end_dt", " ");
			}
			lineJson.put("appl_user_name", result.get("appl_user_name"));
			
			lineJson.put("hv_mpf", result.get("hv_mpf"));
			lineJson.put("mpf_amt", result.get("mpf_amt"));
			lineJson.put("mpf2", result.get("mpf2"));
			lineJson.put("mpf_mth", GeneralUtil.initNullTimestamp((Timestamp) result.get("mpf_mth")));
			
//			lineJson.put("acct_cde", dtl.get("acct_cde").equals(" ") ? "*" : (String)dtl.get("acct_cde").toString());
//			lineJson.put("analysis_cde", dtl.get("analysis_cde").equals(" ") ? "*" : (String)dtl.get("analysis_cde").toString());
//			lineJson.put("fund_cde", dtl.get("fund_cde").equals(" ") ? "*" : (String)dtl.get("fund_cde").toString());
//			lineJson.put("dept_id", dtl.get("dept_id").equals(" ") ? "*" : (String)dtl.get("dept_id").toString());
//			lineJson.put("proj_id", dtl.get("proj_id").equals(" ") ? "*" : (String)dtl.get("proj_id").toString());
//			lineJson.put("class", dtl.get("class").equals(" ") ? "*" : (String)dtl.get("class").toString());
			
			String brFullNo = result.get("br_no").toString() 
					+ "-"
					+ String.format("%02d", ((BigDecimal) result.get("br_line_no")).intValue())
					+ String.format("%02d", ((BigDecimal) result.get("br_dist_line_no")).intValue());
			
			if(brFullNo.trim().equals("-0000")) brFullNo = "-";

			String dummyMpfBrFullNo = result.get("mpf_br_no").toString() 
					+ "-"
					+ String.format("%02d", ((BigDecimal) result.get("mpf_br_line_no")).intValue())
					+ String.format("%02d", ((BigDecimal) result.get("mpf_br_dist_line_no")).intValue());

			if(dummyMpfBrFullNo.trim().equals("-0000")) dummyMpfBrFullNo = "-";
			lineJson.put("br_no", brFullNo);
			lineJson.put("mpf_br_no", result.get("hv_mpf").toString().equals("Y") ? dummyMpfBrFullNo : "-");
			lineJson.put("tos", result.get("tos"));
			lineJson.put("pay_group", jobDataDAO != null ? jobDataDAO.getPayGroup() : " ");

			lineJson.put("mod_ctrl_txt", result.get("mod_ctrl_txt"));
			
			if(!inputJson.optString("appl_nbr").trim().isBlank()) {
				int searchApplNbr = 0;
				try {
					searchApplNbr = Integer.valueOf(inputJson.optString("appl_nbr"));
				}catch (Exception e) {
					throw new InvalidParameterException("Invalid Application Number");
				}
				String applNbrFilter = inputJson.optString("appl_nbr_filter", "equal");
				int applNbr = Integer.valueOf(lineJson.getString("appl_nbr"));
				if("less".equals(applNbrFilter) && !(applNbr < searchApplNbr)) {
					continue;
				}else if("equal".equals(applNbrFilter) && !(applNbr == searchApplNbr)) {
					continue;
				}else if("greater".equals(applNbrFilter) && !(applNbr > searchApplNbr)) {
					continue;
				}
			}
			
			if(!inputJson.optString("appl_id").trim().isBlank()) {
				int searchApplId = 0;
				try {
					searchApplId = Integer.valueOf(inputJson.optString("appl_id"));
				}catch (Exception e) {
					throw new InvalidParameterException("Invalid Employee Id");
				}
				String applNbrFilter = inputJson.optString("appl_id_filter", "equal");
				int applId = Integer.valueOf(lineJson.getString("appl_user_emplid"));
				if("less".equals(applNbrFilter) && !(applId < searchApplId)) {
					continue;
				}else if("equal".equals(applNbrFilter) && !(applId == searchApplId)) {
					continue;
				}else if("greater".equals(applNbrFilter) && !(applId > searchApplId)) {
					continue;
				}
			}
			
			if(startDate != GeneralUtil.NULLTIMESTAMP) {
				if("less".equals(dateFilter) && !GeneralUtil.convertStringToTimestamp(lineJson.getString("pymt_start_dt")).before(startDate)) {
					continue;
				}else if("equal".equals(dateFilter) && !GeneralUtil.isSameDay(GeneralUtil.convertStringToTimestamp(lineJson.getString("pymt_start_dt")), startDate)) {
					continue;
				}else if("greater".equals(dateFilter) && !GeneralUtil.convertStringToTimestamp(lineJson.getString("pymt_start_dt")).after(startDate)) {
					continue;
				}
			}
			
			if(endDate != GeneralUtil.INFINTYTIMESTAMP) {
					if("less".equals(dateFilter) && !GeneralUtil.convertStringToTimestamp(lineJson.getString("pymt_end_dt")).before(endDate)) {
						continue;
					}else if("equal".equals(dateFilter) && !GeneralUtil.isSameDay(GeneralUtil.convertStringToTimestamp(lineJson.getString("pymt_end_dt")), endDate)) {
						continue;
					}else if("greater".equals(dateFilter) && !GeneralUtil.convertStringToTimestamp(lineJson.getString("pymt_end_dt")).after(endDate)) {
						continue;
					}
			}
			
			if(!inputJson.optString("amount").trim().isBlank()) {
				BigDecimal searchAmount = new BigDecimal(-1);
				try {
					if(!inputJson.optString("amount").trim().isBlank()) {
						searchAmount = new BigDecimal((inputJson.optString("amount").trim()));
					}
				}catch (Exception e) {
					throw new InvalidParameterException("The input amount is invaild.");
				}
				
				String amountFilter = inputJson.optString("amount_filter", "equal");
				
				BigDecimal amount = new BigDecimal(lineJson.getString("pymt_line_amt"));
				
				if("less".equals(amountFilter) && !(amount.compareTo(searchAmount) < 0)) {
					continue;
				}else if("equal".equals(amountFilter) && !(amount.compareTo(searchAmount) == 0)) {
					continue;
				}else if("greater".equals(amountFilter) && !(amount.compareTo(searchAmount) > 0)) {
					continue;
				}
			}
			
			if(!inputJson.optString("hv_mpf").trim().isBlank()) {
				String searchHvMpf = inputJson.optString("hv_mpf").trim();
				if(!searchHvMpf.equals(lineJson.get("hv_mpf").toString())) {
					continue;
				}
			}

			if(!inputJson.optString("mpf_amt").trim().isBlank()) {
				BigDecimal searchMPFAmount = new BigDecimal(-1);
				try {
					if(!inputJson.optString("mpf_amt").trim().isBlank()) {
						searchMPFAmount = new BigDecimal((inputJson.optString("mpf_amt").trim()));
					}
				}catch (Exception e) {
					throw new InvalidParameterException("The input MPF amount is invaild.");
				}
				
				String mpfAmountFilter = inputJson.optString("mpf_amt_filter", "equal");
				
				BigDecimal mpfAmount = new BigDecimal(lineJson.getString("mpf_amt"));
				
				if("less".equals(mpfAmountFilter) && !(mpfAmount.compareTo(searchMPFAmount) < 0)) {
					continue;
				}else if("equal".equals(mpfAmountFilter) && !(mpfAmount.compareTo(searchMPFAmount) == 0)) {
					continue;
				}else if("greater".equals(mpfAmountFilter) && !(mpfAmount.compareTo(searchMPFAmount) > 0)) {
					continue;
				}
			}

			if(!(brNo.isBlank() || brFullNo.contains(brNo))) {
				continue;
			}

			
			if(hdrIdList.contains(result.get("appl_hdr_id"))) {
				lineJson.put("is_new_hdr", false);
			}else {
				lineJson.put("is_new_hdr", true);
				hdrIdList.add((String) result.get("appl_hdr_id"));
			}
			
			if(scheduleIdList.contains(result.get("appl_schedule_id"))) {
				lineJson.put("is_new_line", false);
			}else {
				lineJson.put("is_new_line", true);
				scheduleIdList.add((String) result.get("appl_schedule_id"));
			}

			outputJson.put(lineJson);
		}
		
//		if(type.equals("RECURR")) {
//			// EA
//			List<Map<String, Object>> eachPymtDtl2 = elApplHdrRepository.findPaymentIsRecurrById(applHdrId);
//			
//			for (Map<String, Object> dtl : eachPymtDtl2) {
//				JSONObject lineJson = new JSONObject();
//
//				ElJobDataVDAO jobDataDAO = elJobDataVRepository.findByEmplidWithClosestEffdt(emplid, GeneralUtil.convertStringToTimestamp((String)dtl.get("pymt_start_dt").toString()));
//						
//				lineJson.put("sal_elemnt", (String)dtl.get("sal_elemnt") + " HKG");
//				if(!GeneralUtil.isBlankTimestamp(GeneralUtil.convertStringToTimestamp((String)dtl.get("pymt_start_dt").toString()))) {
//					lineJson.put("pymt_start_dt", (String)dtl.get("pymt_start_dt").toString());
//				} else {
//					lineJson.put("pymt_start_dt", " ");
//				}
//				if(!GeneralUtil.isBlankTimestamp(GeneralUtil.convertStringToTimestamp((String)dtl.get("pymt_end_dt").toString()))) {
//					lineJson.put("pymt_end_dt", (String)dtl.get("pymt_end_dt").toString());
//				} else {
//					lineJson.put("pymt_end_dt", " ");
//				}
//				lineJson.put("pmt_currency", (String)dtl.get("pmt_currency"));
//				lineJson.put("pymt_line_amt", decimal.format(dtl.get("pymt_line_amt")));
//				if(!GeneralUtil.isBlankTimestamp(GeneralUtil.convertStringToTimestamp((String)dtl.get("appl_start_dt").toString()))) {
//					lineJson.put("appl_start_dt", (String)dtl.get("appl_start_dt").toString());
//				} else {
//					lineJson.put("appl_start_dt", " ");
//				}
//				if(!GeneralUtil.isBlankTimestamp(GeneralUtil.convertStringToTimestamp((String)dtl.get("appl_end_dt").toString()))) {
//					lineJson.put("appl_end_dt", (String)dtl.get("appl_end_dt").toString());
//				} else {
//					lineJson.put("appl_end_dt", " ");
//				}
//				
//				lineJson.put("acct_cde", dtl.get("acct_cde").equals(" ") ? "*" : (String)dtl.get("acct_cde").toString());
//				lineJson.put("analysis_cde", dtl.get("analysis_cde").equals(" ") ? "*" : (String)dtl.get("analysis_cde").toString());
//				lineJson.put("fund_cde", dtl.get("fund_cde").equals(" ") ? "*" : (String)dtl.get("fund_cde").toString());
//				lineJson.put("dept_id", dtl.get("dept_id").equals(" ") ? "*" : (String)dtl.get("dept_id").toString());
//				lineJson.put("proj_id", dtl.get("proj_id").equals(" ") ? "*" : (String)dtl.get("proj_id").toString());
//				lineJson.put("class", dtl.get("class").equals(" ") ? "*" : (String)dtl.get("class").toString());
//				
//				String brFullNo = dtl.get("br_no").toString() 
//						+ "-"
//						+ String.format("%02d", ((BigDecimal) dtl.get("br_line_no")).intValue())
//						+ String.format("%02d", ((BigDecimal) dtl.get("br_dist_line_no")).intValue());
//				
//				lineJson.put("br_no", brFullNo);
//				lineJson.put("tos", TOS);
//				lineJson.put("pay_group", jobDataDAO != null ? jobDataDAO.getPayGroup() : " ");
//				
//				outputJson.put(lineJson);
//			}
//		} else if(type.equals("INSTALM")) {
//			// PI
//			List<Map<String, Object>> eachPymtDtl = elApplHdrRepository.findPaymentNotRecurrById(applHdrId);
//			
//			for (Map<String, Object> dtl : eachPymtDtl) {
//				
//				JSONObject lineJson = new JSONObject();
//				
//				ElJobDataVDAO jobDataDAO = elJobDataVRepository.findByEmplidWithClosestEffdt(emplid, GeneralUtil.convertStringToTimestamp((String)dtl.get("pymt_start_dt").toString()));
//				
//				lineJson.put("sal_elemnt", (String)dtl.get("sal_elemnt") + " HKG");
//				if(!GeneralUtil.isBlankTimestamp(GeneralUtil.convertStringToTimestamp((String)dtl.get("pymt_start_dt").toString()))) {
//					lineJson.put("pymt_start_dt", (String)dtl.get("pymt_start_dt").toString());
//				} else {
//					lineJson.put("pymt_start_dt", " ");
//				}
//				if(!GeneralUtil.isBlankTimestamp(GeneralUtil.convertStringToTimestamp((String)dtl.get("pymt_end_dt").toString()))) {
//					lineJson.put("pymt_end_dt", (String)dtl.get("pymt_end_dt").toString());
//				} else {
//					lineJson.put("pymt_end_dt", " ");
//				}
//				lineJson.put("pmt_currency", (String)dtl.get("pmt_currency"));
//				lineJson.put("pymt_line_amt", decimal.format(dtl.get("pymt_line_amt")));
//				if(!GeneralUtil.isBlankTimestamp(GeneralUtil.convertStringToTimestamp((String)dtl.get("appl_start_dt").toString()))) {
//					lineJson.put("appl_start_dt", (String)dtl.get("appl_start_dt").toString());
//				} else {
//					lineJson.put("appl_start_dt", " ");
//				}
//				if(!GeneralUtil.isBlankTimestamp(GeneralUtil.convertStringToTimestamp((String)dtl.get("appl_end_dt").toString()))) {
//					lineJson.put("appl_end_dt", (String)dtl.get("appl_end_dt").toString());
//				} else {
//					lineJson.put("appl_end_dt", " ");
//				}
//
//				
//				List<ElApplCourseDAO> eacDAOlist = elApplCourseRepository.findByApplHdrId((String)dtl.get("appl_hdr_id"));
//				String dsc = "";
//				if(eacDAOlist.size == 0) {
//					for(ElApplCourseDAO eacDAO : eacDAOlist) {
//						if(!dsc.isEmpty()) {
//							dsc += "," + eacDAO.getCrseCde();
//						}else {
//							dsc = eacDAO.getCrseCde();
//						}
//					}
//					dsc.substring(0, Math.min(dsc.length(), 30));
//				}else {
//					dsc = (String)dtl.get("el_type_descr");
//				}
//				
//				lineJson.put("descr", dsc);
//				
//				lineJson.put("acct_cde", dtl.get("acct_cde").equals(" ") ? "*" : (String)dtl.get("acct_cde").toString());
//				lineJson.put("analysis_cde", dtl.get("analysis_cde").equals(" ") ? "*" : (String)dtl.get("analysis_cde").toString());
//				lineJson.put("fund_cde", dtl.get("fund_cde").equals(" ") ? "*" : (String)dtl.get("fund_cde").toString());
//				lineJson.put("dept_id", dtl.get("dept_id").equals(" ") ? "*" : (String)dtl.get("dept_id").toString());
//				lineJson.put("proj_id", dtl.get("proj_id").equals(" ") ? "*" : (String)dtl.get("proj_id").toString());
//				lineJson.put("class", dtl.get("class").equals(" ") ? "*" : (String)dtl.get("class").toString());
//				
//				
//				String brFullNo = dtl.get("br_no").toString() 
//						+ "-"
//						+ String.format("%02d", ((BigDecimal) dtl.get("br_line_no")).intValue())
//						+ String.format("%02d", ((BigDecimal) dtl.get("br_dist_line_no")).intValue());
//				
//				lineJson.put("br_no", brFullNo);
//				lineJson.put("tos", TOS);
//
//				lineJson.put("pay_group", jobDataDAO != null ? jobDataDAO.getPayGroup() : " ");
//				
//				outputJson.put(lineJson);
//			}
//		}
		
		return outputJson;
	}
	
	@Override
	@Transactional(value = "transactionManagerJsfasMain", rollbackFor = Exception.class)
	public JSONObject updatePaymentScheduleByPNB(String applHdrId, JSONObject inputJson, String opPageName) throws Exception {
		JSONObject outputJson = new JSONObject();
		JSONArray scheduleArray = inputJson.getJSONArray("schedule_details");
		String remoteUser = SecurityUtils.getCurrentLogin();
		String modCtrlTxt = GeneralUtil.genModCtrlTxt();
		Timestamp currTS = GeneralUtil.getCurrentTimestamp();

		ElApplHdrDAO hdrDAO = elApplHdrRepository.findOneForUpdate(applHdrId);
		
		// validation
		if (hdrDAO == null) {
			throw new RecordNotExistException("Extra Load Application");
		}
		
		if (!hdrDAO.getModCtrlTxt().equals(inputJson.getString("mod_ctrl_txt"))) {
			throw new RecordModifiedException();
		}

		ElApplAprvStatusDAO elApplAprvStatusDAO = null;
		List<ElApplAprvStatusDAO> pendingAprvs = elApplAprvStatusRepository.findAllNextPymtFOAprv(applHdrId);
		
		for (var pendingAprv : pendingAprvs) {
			if (remoteUser.equals(pendingAprv.getAprvUserId())) {
				elApplAprvStatusDAO = pendingAprv;
				break;
			}
		}
		
		if (elApplAprvStatusDAO == null) {
			throw new InvalidParameterException("Not pending payment approver");
		}
		
		ElApplPymtMethodDAO methodDAO = elApplPymtMethodRepository.findSalaryMethodByApplHdrId(applHdrId).get(0);
		
		List<ElApplPymtScheduleDAO> saveDAOList = new ArrayList<>();
		for(int i = 0; i < scheduleArray.length();i++) {
			JSONObject schedule = scheduleArray.getJSONObject(i);
			Timestamp startDate = GeneralUtil.NULLTIMESTAMP;
			Timestamp endDate = GeneralUtil.NULLTIMESTAMP;
				try {
					startDate = GeneralUtil.convertStringToTimestamp(schedule.getString("pymt_start_dt"));
					endDate =  GeneralUtil.convertStringToTimestamp(schedule.getString("pymt_end_dt"));
				} catch (Exception e) {
					throw new InvalidParameterException("Please input date format in DD/MM/YYYY.");
				}
			if(!GeneralUtil.isSameDay(startDate, endDate) && startDate.after(endDate)) {
				throw new InvalidParameterException("The end date is before start date.");
			}
			if(methodDAO.getPymtTypeCde().equalsIgnoreCase("INSTALM")) {
				if(!GeneralUtil.isSameDay(startDate, endDate)) {
					throw new InvalidParameterException("The start date and end date should be the same date for Installment.");
				}
			}
			
			ElApplPymtScheduleDAO dao = elApplPymtScheduleRepository.findOne(schedule.getString("schedule_id"));
			dao.setPymtRevStartDt(startDate);
			dao.setPymtRevEndDt(endDate);
			dao.setEmplNbr(schedule.getInt("emp_rec_nbr"));
			
			dao.setChngDat(currTS);
			dao.setChngUser(remoteUser);
			dao.setModCtrlTxt(modCtrlTxt);
			dao.setOpPageNam(opPageName);
			
			saveDAOList.add(dao);
		}
		elApplPymtScheduleRepository.saveAll(saveDAOList);
		
		hdrDAO.setChngDat(currTS);
		hdrDAO.setChngUser(remoteUser);
		hdrDAO.setModCtrlTxt(modCtrlTxt);
		hdrDAO.setOpPageNam(opPageName);
		
		return outputJson;
	}

	@Override
	public JSONArray getPymtRecords(String applHdrId) throws Exception {
		JSONArray outputJson = new JSONArray();
		
		ElApplHdrDAO elApplHdrDAO = elApplHdrRepository.findOne(applHdrId);
		
		if (elApplHdrDAO == null) {
			throw new RecordNotExistException("Record");
		}
		
		List<Map<String, Object>> resultList = elApplHdrRepository.findPymtRecordByBrNo(elApplHdrDAO.getBrNo());
		
		int seqNo = 1;
		for (var row: resultList) {
			JSONObject jsonObj = new JSONObject();
			jsonObj.put("pymt_dt", row.get("pymt_dt"));
			jsonObj.put("pymt_amt", row.get("pymt_amt"));
			jsonObj.put("seq_no", seqNo++);
			outputJson.put(jsonObj);
		}
		
		return outputJson;
	}

	@Transactional(value = "transactionManagerJsfasMain", rollbackFor = Exception.class)
	private JSONObject pnbBatchUpdateSchedule(String applHdrId, JSONObject inputJson, String opPageName) throws Exception{
		JSONObject outputJson = new JSONObject();
		outputJson.put("applNbr", inputJson.get("appl_nbr"));

		String remoteUser = SecurityUtils.getCurrentLogin();
		String modCtrlTxt = GeneralUtil.genModCtrlTxt();
		Timestamp currTS = GeneralUtil.getCurrentTimestamp();

		ElApplHdrDAO hdrDAO = elApplHdrRepository.findOneForUpdate(applHdrId);
		
		// validation
		if (hdrDAO == null) {
			throw new RecordNotExistException("Extra Load Application");
		}
		
		if (!hdrDAO.getModCtrlTxt().equals(inputJson.getString("mod_ctrl_txt"))) {
			throw new RecordModifiedException();
		}

		ElApplAprvStatusDAO elApplAprvStatusDAO = null;
		List<ElApplAprvStatusDAO> pendingAprvs = elApplAprvStatusRepository.findAllNextPymtFOAprv(applHdrId);
		
		for (var pendingAprv : pendingAprvs) {
			if (remoteUser.equals(pendingAprv.getAprvUserId())) {
				elApplAprvStatusDAO = pendingAprv;
				break;
			}
		}
		
		if (elApplAprvStatusDAO == null) {
			throw new InvalidParameterException("Not pending payment approver");
		}
		
		ElApplPymtMethodDAO methodDAO = elApplPymtMethodRepository.findSalaryMethodByApplHdrId(applHdrId).get(0);
		
		List<ElApplPymtScheduleDAO> saveDAOList = new ArrayList<>();
		JSONObject schedule = inputJson;
		Timestamp startDate = GeneralUtil.NULLTIMESTAMP;
		Timestamp endDate = GeneralUtil.NULLTIMESTAMP;
			try {
	            if (!schedule.getString("pymt_start_dt").matches("\\d{2}/\\d{2}/\\d{4}") || !schedule.getString("pymt_end_dt").matches("\\d{2}/\\d{2}/\\d{4}")) {
					throw new InvalidParameterException("Please input date format in DD/MM/YYYY.");
	            }
				startDate = GeneralUtil.convertStringToTimestamp(schedule.getString("pymt_start_dt"));
				endDate =  GeneralUtil.convertStringToTimestamp(schedule.getString("pymt_end_dt"));
			} catch (Exception e) {
				throw new InvalidParameterException("Please input date format in DD/MM/YYYY.");
			}
		if(!GeneralUtil.isSameDay(startDate, endDate) && startDate.after(endDate)) {
			throw new InvalidParameterException("The end date is before start date.");
		}
		if(methodDAO.getPymtTypeCde().equalsIgnoreCase("INSTALM")) {
			if(!GeneralUtil.isSameDay(startDate, endDate)) {
				throw new InvalidParameterException("The start date and end date should be the same date for Installment.");
			}
		}
		
		ElApplPymtScheduleDAO dao = elApplPymtScheduleRepository.findOneForUpdate(schedule.getString("schedule_id"));
		dao.setPymtRevStartDt(startDate);
		dao.setPymtRevEndDt(endDate);
		dao.setEmplNbr(schedule.getInt("emp_rec_nbr"));
		
		dao.setChngDat(currTS);
		dao.setChngUser(remoteUser);
		dao.setModCtrlTxt(modCtrlTxt);
		dao.setOpPageNam(opPageName);
			
		elApplPymtScheduleRepository.save(dao);
		
		hdrDAO.setChngDat(currTS);
		hdrDAO.setChngUser(remoteUser);
		hdrDAO.setModCtrlTxt(modCtrlTxt);
		hdrDAO.setOpPageNam(opPageName);
		
		elApplHdrRepository.save(hdrDAO);
		
		outputJson.put("new_mod_ctrl_txt", hdrDAO.getModCtrlTxt());
		return outputJson;
	}

	@Transactional(value = "transactionManagerJsfasMain", rollbackFor = Exception.class)
	private JSONObject pnbBatchUpdateHdr(String applHdrId, JSONObject inputJson, String opPageName) throws Exception{
		JSONObject outputJson = new JSONObject();
		outputJson.put("applNbr", inputJson.get("appl_nbr"));
		
		// validation
		ElApplHdrDAO hdrDAO = elApplHdrRepository.findOneForUpdate(applHdrId);
		
		if (hdrDAO == null) {
			throw new RecordNotExistException("Extra Load Application");
		}
		
		if (!hdrDAO.getModCtrlTxt().equals(inputJson.getString("mod_ctrl_txt"))) {
			throw new RecordModifiedException();
		}
		
		String role = inputJson.getString("role");
		String remoteUser = SecurityUtils.getCurrentLogin();
		
		ElApplAprvStatusDAO elApplAprvStatusDAO = null;
		
		if (!ApplStatusConstants.PENDING_FOPNB.equals(hdrDAO.getApplStatCde()) || 0 != hdrDAO.getObsolete()) {
			
			throw new InvalidParameterException("Application is not pending for applicant action");
		}
		
		List<ElApplAprvStatusDAO> pendingAprvs = elApplAprvStatusRepository.findAllNextPymtFOAprv(applHdrId);
		
		for (var pendingAprv : pendingAprvs) {
			if (remoteUser.equals(pendingAprv.getAprvUserId())) {
				elApplAprvStatusDAO = pendingAprv;
				break;
			}
		}
		
		if (elApplAprvStatusDAO == null) {
			throw new InvalidParameterException("Not pending payment approver");
		}
		// validation ends
		
		// start update
		Timestamp currTS = GeneralUtil.getCurrentTimestamp();
		String modCtrlTxt = GeneralUtil.genModCtrlTxt();
		
		boolean isApprove = ElApplActDAO.APPROVE.equalsIgnoreCase(inputJson.getString("action"));
		
		// update elApplAprvStatusDAO
		elApplAprvStatusDAO.setApproved(isApprove ? 1 : 0);
		elApplAprvStatusDAO.setAprvDttm(currTS);
		elApplAprvStatusDAO.setAprvRemark(GeneralUtil.initBlankString(inputJson.optString("aprv_remark")));
		elApplAprvStatusDAO.setChngDat(currTS);
		elApplAprvStatusDAO.setChngUser(remoteUser);
		elApplAprvStatusDAO.setOpPageNam(opPageName);
		elApplAprvStatusDAO.setModCtrlTxt(modCtrlTxt);
		
		// Delete other approver with same type
		List<ElApplAprvStatusDAO> removeList = elApplAprvStatusRepository.findByApplHdrIdAndAprvTypeCdeAndIdNot(applHdrId, elApplAprvStatusDAO.getAprvTypeCde(), elApplAprvStatusDAO.getId());

		elApplAprvStatusRepository.deleteAll(removeList);
		
		// if approve, check if there are next approver
		// if yes, workflow to send email
		// Else, update header to PENDING_DECL
		if (isApprove) {
			hdrDAO.setApplStatCde(ApplStatusConstants.PYMT_SUBM);
			hdrDAO.setPymtPostInd("I");
			
			hdrDAO.setChngDat(currTS);
			hdrDAO.setChngUser(remoteUser);
			hdrDAO.setOpPageNam(opPageName);
			hdrDAO.setModCtrlTxt(modCtrlTxt);
			
			// MuAdmin
			createMyAiTaskForPymtSubmitted(applHdrId);
		} 
		// if reject, update header status to PYMT_RETURNED
		else {
			// TODO: if rejected, set sumbit to " "
			hdrDAO.setApplStatCde(ApplStatusConstants.PYMT_REJECTED);
			
			
			
			sendEmailToRequestorForPymtReject(applHdrId, opPageName);
//			commonRoutineService.callAIWorkListApprvAPI(hdrDAO.getApplNbr(), "false", remoteUser, "Pending Approval by " + remoteUser);
			createMyAiTaskForRejection(hdrDAO);
			

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
			
			
			hdrDAO.setChngDat(currTS);
			hdrDAO.setChngUser(remoteUser);
			hdrDAO.setOpPageNam(opPageName);
			hdrDAO.setModCtrlTxt(modCtrlTxt);
		}
		
		// creation record in action table
		if (!RoleConstants.REQUESTER.equalsIgnoreCase(role) && !RoleConstants.APPLICANT.equalsIgnoreCase(role)) {
			if (elApplAprvStatusDAO != null) {
				inputJson.put("role", elApplAprvStatusDAO.getAprvTypeCde());
			}
		}
		generalApiService.createElApplAct(applHdrId, inputJson, "", "", "pymt", opPageName);

		outputJson.put("new_mod_ctrl_txt", hdrDAO.getModCtrlTxt());
		return outputJson;
	}
	
	@Override
	public JSONObject batchApprovalUpdateApplStatus(JSONArray inputJson, String opPageName) throws Exception {
		JSONObject outputJson = new JSONObject();
		JSONArray approvedArray = new JSONArray();
		JSONArray rejectedArray = new JSONArray();
		JSONArray errorArray = new JSONArray();
		int processCount = 0;
		
		Map<String, String> updatedModCtrlTxt = new HashMap<String, String>();
		for(int i = 0; i < inputJson.length();i++ ) {
			JSONObject obj = inputJson.getJSONObject(i);
			JSONObject processOutput = new JSONObject();
			if(processCount >= 25) {
				break;
			}
			try {
				if(obj.has("is_changed") && obj.getBoolean("is_changed")) {
					if(updatedModCtrlTxt.containsKey(obj.getString("appl_hdr_id"))) {
						obj.put("mod_ctrl_txt", updatedModCtrlTxt.get(obj.getString("appl_hdr_id")));
					}
					processOutput = pnbBatchUpdateSchedule(obj.getString("appl_hdr_id"), obj, opPageName);
					updatedModCtrlTxt.put(obj.getString("appl_hdr_id"), processOutput.getString("new_mod_ctrl_txt"));
				}
			}catch (Exception e) {
				JSONObject errorJson = new JSONObject();
				log.debug(obj.toString());
				try {
					errorJson.put("applNbr", obj.getString("appl_nbr"));
				}catch (Exception e2) {
					errorJson.put("applNbr", "Can not get application number!");
				}
				errorJson.put("error", "Can not update payment details. " + e.getMessage());
				errorArray.put(errorJson);
			}
		}
		for(int i = 0; i < inputJson.length();i++ ) {
			JSONObject obj = inputJson.getJSONObject(i);
			JSONObject processOutput = new JSONObject();
			
			if(StringUtils.isNotBlank(obj.optString("action")) && obj.optBoolean("is_new_hdr")) {
				try {
					if(updatedModCtrlTxt.containsKey(obj.getString("appl_hdr_id"))) {
						obj.put("mod_ctrl_txt", updatedModCtrlTxt.get(obj.getString("appl_hdr_id")));
					}
					processOutput = pnbBatchUpdateHdr(obj.getString("appl_hdr_id"), obj, opPageName);
					updatedModCtrlTxt.put(obj.getString("appl_hdr_id"), processOutput.getString("new_mod_ctrl_txt"));
					
					if(ElApplActDAO.APPROVE.equalsIgnoreCase(obj.getString("action")) || ElApplActDAO.APPROVE.equalsIgnoreCase(obj.getString("action"))) {
						approvedArray.put(processOutput);
					}else {
						rejectedArray.put(processOutput);
					}
				}catch (Exception e) {
					JSONObject errorJson = new JSONObject();
					try {
						errorJson.put("applNbr", obj.getString("appl_nbr"));
					}catch (Exception e2) {
						errorJson.put("applNbr", "Can not get application number!");
					}
					errorJson.put("error", "Can not approve / reject application. " + e.getMessage());
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
	
	@Override
	public JSONObject downloadPendingPNBReport() throws Exception {

		JSONObject outputJson = new JSONObject();
		
		// prepare workbook and sheet
		XSSFWorkbook workbook = new XSSFWorkbook();
		XSSFSheet sheet = workbook.createSheet("Template");
		
		XSSFCellStyle boldStyle = workbook.createCellStyle();
		Font boldFont = workbook.createFont();
		boldFont.setBold(true);
		boldStyle.setFont(boldFont);
		
		XSSFCellStyle numberStyle = workbook.createCellStyle();
		numberStyle.setDataFormat(workbook.createDataFormat().getFormat("#0.00"));
		numberStyle.setAlignment(HorizontalAlignment.RIGHT);
		
		// prepare Excel content
		ArrayList<String> colList = new ArrayList<>(Arrays.asList(
				"Application Number", "TOS", "Employee ID", "Multi Job"
				, "Employment Record #", "Staff Name", "Pay Group", "PIN Code"
				, "Pay Start Date (ELAS) / PI Begin Date (HRMS)", "Pay End Date (ELAS) / PI End Date (HRMS)"
				, "Currency Code", "Amount", "Br Info", "MPF (Y/N)", "Expected MPF Month (For reference)"
				, "Expected MPF Amount (For reference)", "BR MPF Info", "Pay Method", "SR Team approval time"));
		
		XSSFRow headerRow = sheet.createRow(0);
		XSSFCell[] headerCellArr = new XSSFCell[colList.size()];
		for (int i = 0; i < headerCellArr.length; i++) {
			headerCellArr[i] = headerRow.createCell(i);
			headerCellArr[i].setCellStyle(boldStyle);
		}
		

		for (int i = 0; i < colList.size(); i++) {
			String header = colList.get(i);
			headerCellArr[i].setCellValue(header);
		}
		
		List<Map<String, Object>> results = elApplHdrRepository.searchPendingForPNB("%", "%", "%", GeneralUtil.NULLTIMESTAMP, GeneralUtil.INFINTYTIMESTAMP, "%");
		
		int rowCnt = 0;
		for (Map<String, Object> result : results) {
			rowCnt++;
			XSSFRow dataRow = sheet.createRow(rowCnt);
			XSSFCell[] dataCellArr = new XSSFCell[19];
			for (int i = 0; i < dataCellArr.length; i++) {
				dataCellArr[i] = dataRow.createCell(i);
			}
			
			CellStyle ddmmyyyy = workbook.createCellStyle();
			CreationHelper createHelper = workbook.getCreationHelper();
			ddmmyyyy.setDataFormat(
			    createHelper.createDataFormat().getFormat("dd/mm/yyyy"));
			CellStyle mmyyyy = workbook.createCellStyle();
			mmyyyy.setDataFormat(
				    createHelper.createDataFormat().getFormat("mm/yyyy"));
			
			dataCellArr[0].setCellType(CellType.STRING);
			dataCellArr[2].setCellType(CellType.STRING);
			dataCellArr[3].setCellType(CellType.STRING);
			dataCellArr[11].setCellType(CellType.NUMERIC);
			dataCellArr[15].setCellType(CellType.NUMERIC);

			String effdt = "";
			if(!GeneralUtil.isBlankTimestamp(GeneralUtil.convertStringToTimestamp((String)result.get("pymt_rev_start_dt").toString()))) {
				effdt = (String)result.get("pymt_rev_start_dt").toString();
			} else {
				effdt = (String)result.get("pymt_start_dt").toString();
			}
			List<ElInpostStaffImpVDAO> elInpostStaffImpVDAOList = elInpostStaffImpVRepository.findByEmplid((String) result.get("appl_user_emplid"));
			ElJobDataVDAO jobDataDAO = elJobDataVRepository.findByEmplidWithClosestEffdt((String) result.get("appl_user_emplid"), GeneralUtil.convertStringToTimestamp(effdt));
			
			dataCellArr[0].setCellValue((String) result.get("appl_nbr"));
			dataCellArr[1].setCellValue((String) result.get("tos"));
			dataCellArr[2].setCellValue((String) result.get("appl_user_emplid"));
			dataCellArr[3].setCellValue(elInpostStaffImpVDAOList.size() > 1 ? "Y" : "-");
			dataCellArr[4].setCellValue(((BigDecimal) result.getOrDefault("emp_rec_nbr", new BigDecimal(999))).toString());
			dataCellArr[5].setCellValue((String) result.get("appl_user_name"));
			dataCellArr[6].setCellValue(jobDataDAO != null ? jobDataDAO.getPayGroup() : " ");
			dataCellArr[7].setCellValue((String)result.get("sal_elemnt") + " HKG");

			
			String pymt_start_dt = " ";
			if(!GeneralUtil.isBlankTimestamp(GeneralUtil.convertStringToTimestamp((String)result.get("pymt_rev_start_dt").toString()))) {
				pymt_start_dt = (String)result.get("pymt_rev_start_dt").toString();
			} else if(!GeneralUtil.isBlankTimestamp(GeneralUtil.convertStringToTimestamp((String)result.get("pymt_start_dt").toString()))) {
				pymt_start_dt = (String)result.get("pymt_start_dt").toString();
			}
			
			dataCellArr[8].setCellValue(java.util.Date.from(
                    java.time.LocalDate.parse(
                    		pymt_start_dt, 
                            java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy")
                           ).atStartOfDay(java.time.ZoneId.systemDefault()).toOffsetDateTime().toInstant()
                         ));
			
			if(PymtTypeConstants.RECURR.equals(result.get("pymt_type_cde"))) {
				String pymt_end_dt = " ";
				if(!GeneralUtil.isBlankTimestamp(GeneralUtil.convertStringToTimestamp((String)result.get("pymt_rev_end_dt").toString()))) {
					pymt_end_dt = (String)result.get("pymt_rev_end_dt").toString();
				} else if(!GeneralUtil.isBlankTimestamp(GeneralUtil.convertStringToTimestamp((String)result.get("pymt_end_dt").toString()))) {
					pymt_end_dt = (String)result.get("pymt_end_dt").toString();
				}
	
				dataCellArr[9].setCellValue(java.util.Date.from(
	                    java.time.LocalDate.parse(
	                    		pymt_end_dt, 
	                            java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy")
	                           ).atStartOfDay(java.time.ZoneId.systemDefault()).toOffsetDateTime().toInstant()
	                         ));
			}else {
				dataCellArr[9].setCellValue(java.util.Date.from(
	                    java.time.LocalDate.parse(
	                    		pymt_start_dt, 
	                            java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy")
	                           ).atStartOfDay(java.time.ZoneId.systemDefault()).toOffsetDateTime().toInstant()
	                         ));
			}

			dataCellArr[10].setCellValue((String) result.get("pmt_currency"));
			dataCellArr[11].setCellValue(((BigDecimal) result.get("pymt_line_amt")).setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue());
			

			String brFullNo = result.get("br_no").toString() 
					+ "-"
					+ String.format("%02d", ((BigDecimal) result.get("br_line_no")).intValue())
					+ String.format("%02d", ((BigDecimal) result.get("br_dist_line_no")).intValue());
			
			if(brFullNo.trim().equals("-0000")) brFullNo = "-";
			dataCellArr[12].setCellValue(brFullNo);

			dataCellArr[13].setCellValue((Character) result.get("hv_mpf"));
			
			if(result.get("mpf_mth") != null) {
				dataCellArr[14].setCellValue(new java.util.Date(((Timestamp)result.get("mpf_mth")).getTime()));
			}
			
			dataCellArr[15].setCellValue(((BigDecimal) result.get("mpf_amt")).setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue());

			String dummyMpfBrFullNo = result.get("mpf_br_no").toString() 
					+ "-"
					+ String.format("%02d", ((BigDecimal) result.get("mpf_br_line_no")).intValue())
					+ String.format("%02d", ((BigDecimal) result.get("mpf_br_dist_line_no")).intValue());

			if(dummyMpfBrFullNo.trim().equals("-0000")) dummyMpfBrFullNo = "-";
			
			dataCellArr[16].setCellValue(dummyMpfBrFullNo);
			
			String method = "";
			if(PymtTypeConstants.RECURR.equals(result.get("pymt_type_cde"))) {
				method = "Recurrent";
			}else if(PymtTypeConstants.INSTALM.equals(result.get("pymt_type_cde"))) {
				method = "Installment";
			}
			dataCellArr[17].setCellValue(method);

			dataCellArr[18].setCellValue(new java.util.Date(((Timestamp)result.get("last_approval_time")).getTime()));

			dataCellArr[8].setCellStyle(ddmmyyyy);
			dataCellArr[9].setCellStyle(ddmmyyyy);
			dataCellArr[14].setCellStyle(mmyyyy);
			dataCellArr[18].setCellStyle(ddmmyyyy);
		}
		
		// adjust column width
		for (int i = 0; i < headerCellArr.length; i++) {
			sheet.autoSizeColumn(i);
		}
		
		// export Excel file
		try {
            byte[] byteArray = null;

            try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
                workbook.write(baos);
                byteArray = baos.toByteArray();
            } catch (IOException e) {
                e.printStackTrace();
            }
            
            outputJson
                    .put("file_name", "Pending_PNB_out_" + GeneralUtil.genModCtrlTxt() +".xlsx")
                    .put("file_type", "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet")
                    .put("file_content", byteArray);
            return outputJson;
		} catch (Exception e) {
			log.error(Thread.currentThread().getStackTrace()[1].getMethodName()+"@"+this.getClass().getSimpleName()+" Error Occur...", e);
		}
		
		return outputJson;
	}
}
