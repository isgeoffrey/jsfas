package jsfas.db.main.persistence.service;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.nio.charset.Charset;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

import org.joda.time.LocalDateTime;
import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.core.io.ClassPathResource;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.freemarker.FreeMarkerTemplateUtils;
import org.springframework.web.multipart.MultipartFile;

import com.itextpdf.text.Document;
import com.itextpdf.text.FontFactory;
import com.itextpdf.text.pdf.PdfWriter;
import com.itextpdf.tool.xml.XMLWorkerFontProvider;
import com.itextpdf.tool.xml.XMLWorkerHelper;

import freemarker.template.Configuration;
import jsfas.common.constants.AppConstants;
import jsfas.common.constants.ApplStatusConstants;
import jsfas.common.constants.AprvTypeConstants;
import jsfas.common.constants.RoleConstants;
import jsfas.common.exception.InvalidParameterException;
import jsfas.common.exception.RecordModifiedException;
import jsfas.common.exception.RecordNotExistException;
import jsfas.common.json.CommonJson;
import jsfas.common.utils.GeneralUtil;
import jsfas.db.main.persistence.domain.ElApplActDAO;
import jsfas.db.main.persistence.domain.ElApplAprvStatusDAO;
import jsfas.db.main.persistence.domain.ElApplHdrDAO;
import jsfas.db.main.persistence.domain.ElApplLoaDAO;
import jsfas.db.main.persistence.domain.ElEmNotificationDAO;
import jsfas.db.main.persistence.domain.ElUpldFileDAO;
import jsfas.db.main.persistence.repository.ElApplActRepository;
import jsfas.db.main.persistence.repository.ElApplAprvStatusRepository;
import jsfas.db.main.persistence.repository.ElApplHdrRepository;
import jsfas.db.main.persistence.repository.ElApplLoaRepository;
import jsfas.db.main.persistence.repository.ElEmNotificationRepository;
import jsfas.db.main.persistence.repository.ElParamTabRepository;
import jsfas.db.main.persistence.repository.ElUpldFileRepository;
import jsfas.db.rbac.persistence.repository.UserRoleGroupRepository;
import jsfas.security.SecurityUtils;

public class LoaEventHandler implements LoaService {
    
	private final Logger log = LoggerFactory.getLogger(LoaEventHandler.class);
    
	@Autowired
	Environment env;
	
	@Autowired
	ElApplActRepository elApplActRepository;
	
	@Autowired
	ElApplHdrRepository elApplHdrRepository;
	
	@Autowired
	ElApplLoaRepository elApplLoaRepository;
	
	@Autowired
	ElUpldFileRepository elUpldFileRepository;
	
	@Autowired
	ElParamTabRepository elParamTabRepository;
	
    @Autowired
    Configuration freemarkerConfiguration;
    
	@Autowired
	ElEmNotificationRepository elEmNotificationRepository;
	
	@Autowired
	ExtraLoadApplicationService extraLoadApplicationService;
	
	@Autowired
	ElApplAprvStatusRepository elApplAprvStatusRepository;
	
	@Autowired
	CommonRoutineService commonRoutineService;
	
	@Autowired
	UserRoleGroupRepository userRoleGroupRepository;

	@Override
	public JSONArray getLoaList(String userId) throws Exception {
		JSONArray jsonArr = new JSONArray();
		
		List<Map<String, Object>> resultMapList = elApplHdrRepository.findApprovedByUserId(userId);	
		
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
	public JSONArray getLoaDetailsById(String id) throws Exception {
		JSONArray jsonArr = new JSONArray();
		
		extraLoadApplicationService.isLoaAuthUser(id);
		
		ElApplHdrDAO hdr = elApplHdrRepository.findOne(id);
		
		String job_catg_cde = hdr.getApplUserJobCatg();
		String workflowType = extraLoadApplicationService.getApplWorkflowType(job_catg_cde);
		
		
		List<Map<String, Object>> resultMapList = elApplHdrRepository.findDetailsById(id);	
		
		
		for (Map<String, Object> resultMap : resultMapList) {
			JSONObject jsonObj = new JSONObject();
			jsonArr.put(jsonObj);
			for (String keys : resultMap.keySet()) {
				jsonObj.put(keys.toLowerCase(), resultMap.get(keys));
			}
			jsonObj.put("appl_workflow_type", workflowType);
		}
		
		return jsonArr;
	}
	
	@Override
	public JSONArray getLoaFileList(String applHdrId) throws Exception {
		JSONArray jsonArr = new JSONArray();
		
		// Auth Checking
		String remoteUser = SecurityUtils.getCurrentLogin();
		ElApplHdrDAO elApplHdrDAO = elApplHdrRepository.findOne(applHdrId);
		
		if (elApplHdrDAO == null) {
			throw new RecordNotExistException("Extra Load Application");
		}
		
		if (remoteUser.equals(elApplHdrDAO.getApplRequesterId()) 
				|| remoteUser.equals(elApplHdrDAO.getApplUserId())
				|| commonRoutineService.isAuthorized(AppConstants.ENQUIRE_LOA_FUNC_CDE,  " ", null)) {
			// pass
		} else {
			throw new InvalidParameterException("Not valid user for this Extra Load Application");
		}
		
		List<Map<String, Object>> resultMapList = elApplLoaRepository.findFilesByApplHdrId(applHdrId);	
		
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
	public void updateLoaFileList(String applHdrId, CommonJson inputJSON, String opPageName) throws Exception {
		String remoteUser = SecurityUtils.getCurrentLogin();
		
		ElApplHdrDAO elApplHdrDAO = elApplHdrRepository.findOne(applHdrId);
		List<ElApplLoaDAO> elApplLoaDAOList = elApplLoaRepository.findByApplHdrId(applHdrId);
		
		// Check Exist
		if(elApplHdrDAO == null) {
            throw new RecordNotExistException("Appl Hdr ID: " + applHdrId);
		}
		
		
		// Auth Checking		
		if (remoteUser.equals(elApplHdrDAO.getApplRequesterId()) 
				|| commonRoutineService.isAuthorized(AppConstants.ENQUIRE_LOA_FUNC_CDE,  " ", null)) {
			// pass
		} else {
			throw new InvalidParameterException("Not valid user for this Extra Load Application");
		}
		
		// Check Hdr modified
		if(!elApplHdrDAO.getModCtrlTxt().equals(inputJSON.get("mod_ctrl_txt"))) {
			throw new RecordModifiedException();
		}
		
		
		JSONObject json = inputJSON.toJSONObject();
		JSONArray fileList = json.optJSONArray("file_list");
		if (fileList != null) {
			for (int i=0; i < fileList.length(); i++) {
				JSONObject o = fileList.getJSONObject(i);
				ElApplLoaDAO elApplLoaDAO = elApplLoaRepository.findOneByFileId(o.getString("id"));
				if(elApplLoaDAO != null) {
					// Check Loa modified
					if(!elApplLoaDAO.getModCtrlTxt().equals(o.getString("mod_ctrl_txt"))) {
//						System.out.println("02");
						throw new RecordModifiedException();
					}else {
						// Remove needed file from list
						elApplLoaDAOList.remove(elApplLoaDAO);
					}
				}else {
				// New file
				ElApplLoaDAO newLoaDAO = new ElApplLoaDAO();
				newLoaDAO.setApplHdrId(applHdrId);
				newLoaDAO.setFileCategory("ATTM");
				newLoaDAO.setFileId(o.getString("id"));
				newLoaDAO.setCreatUser(remoteUser);
				newLoaDAO.setChngUser(remoteUser);
				newLoaDAO.setOpPageNam(opPageName);
				
				elApplLoaRepository.save(newLoaDAO);
				}
			}
		}
		// All item in list is not needed
		for(ElApplLoaDAO obj:elApplLoaDAOList) {
			elApplLoaRepository.delete(obj);
		}
	}
	
	@Override
	@Transactional(value = "transactionManagerJselMain", rollbackFor = Exception.class)
	public void updateLoaStatusByHdrId(String applHdrId, JSONObject inputJson, String opPageName) throws Exception {
		String remoteUser = SecurityUtils.getCurrentLogin();
		Timestamp currTS = GeneralUtil.getCurrentTimestamp();
		String newModCtrlTxt = GeneralUtil.genModCtrlTxt();
		
		String modCtrlTxt = inputJson.getString("currentModCtrlTxt");
		String remarks = inputJson.optString("remarks");
		Integer actionCode = inputJson.getInt("acknowledgeType");
		
		String role = inputJson.getString("role");
		boolean isRequester = role.equalsIgnoreCase(RoleConstants.REQUESTER);
		boolean isApplicant = role.equalsIgnoreCase(RoleConstants.APPLICANT);
		
		boolean isRelease = isRequester && actionCode == 1;
		boolean isAccept = isApplicant && actionCode == 1;
		boolean isReject = isApplicant && actionCode == 0;
		
		List<ElApplLoaDAO> elApplLoaDAOList = elApplLoaRepository.findByApplHdrId(applHdrId);
		ElApplHdrDAO elApplHdrDAO = elApplHdrRepository.findOneForUpdate(applHdrId);

		// Check Exist
		if(elApplHdrDAO == null) {
            throw new RecordNotExistException("Appl Hdr ID: " + applHdrId);
		}
		if(elApplLoaDAOList == null) {
            throw new RecordNotExistException("Appl Hdr ID: " + applHdrId);
		}
		
		// Check Hdr modified
		if(!elApplHdrDAO.getModCtrlTxt().equals(modCtrlTxt)) {
			throw new RecordModifiedException();
		}
		
		// validation base on role
		if (isRequester) {
			if (!remoteUser.equals(elApplHdrDAO.getApplRequesterId())) {
				throw new InvalidParameterException("Not application requestor");
			}
			
			if (!elApplHdrDAO.getApplStatCde().equals(ApplStatusConstants.ISSUE_OFFER) && !elApplHdrDAO.getApplStatCde().equals(ApplStatusConstants.OFFER_REJECTED)) {
				throw new InvalidParameterException("Not available for requestor action");
			}
			
			if (!isRelease) {
				throw new InvalidParameterException("Invalid action");
			}else {
				// Check if any LoA exist
				List<ElApplLoaDAO> loaList = elApplLoaRepository.findByApplHdrId(applHdrId);
				if(loaList.size() == 0) {
					throw new InvalidParameterException("No Letter of Appointment exists");
				}
			}
			
		} else {
			if (!remoteUser.equals(elApplHdrDAO.getApplUserId())) {
				throw new InvalidParameterException("Not application applicant");
			}
			
			if (!ApplStatusConstants.RELEASE_OFFER.equals(elApplHdrDAO.getApplStatCde())) {
				throw new InvalidParameterException("LoA is not pending for applicant to accept");
			}
			
			if (!isAccept && !isReject) {
				throw new InvalidParameterException("Invalid action");
			}
		}
		
		// Update status base on actions
		if (isRelease) {
			elApplHdrDAO.setApplStatCde(ApplStatusConstants.RELEASE_OFFER);
		} else if (isAccept) {
			// ETAP-45
			// 3. If Budget Error, after LoA accepted, routed to FO Rectification
			if ("E".equals(elApplHdrDAO.getBrPostInd())) {
				elApplHdrDAO.setApplStatCde(ApplStatusConstants.PENDING_RECTIFY);
				
				// insert to approver table
				insertFORectifyApprv(applHdrId, opPageName);
			} else {
				elApplHdrDAO.setApplStatCde(ApplStatusConstants.READY_SUBM);
			}	
			
			for(ElApplLoaDAO obj:elApplLoaDAOList) {
				obj.setAccepted(1);
				obj.setAcceptanceDttm(currTS);
				obj.setCreatUser(remoteUser);
				obj.setChngUser(remoteUser);
				obj.setOpPageNam(opPageName);
				obj.setModCtrlTxt(modCtrlTxt);
				
				elApplLoaRepository.save(obj);
			}
		} else if (isReject) {
			elApplHdrDAO.setApplStatCde(ApplStatusConstants.OFFER_REJECTED);
		}
		
		elApplHdrDAO.setModCtrlTxt(newModCtrlTxt);
		elApplHdrDAO.setChngDat(currTS);
		elApplHdrDAO.setChngUser(remoteUser);
		
		elApplHdrRepository.save(elApplHdrDAO);
		
		// Activity Table
		ElApplActDAO newActDAO = new ElApplActDAO();
		newActDAO.setApplHdrId(applHdrId);
		
		if (isRelease) {
			newActDAO.setAction("Release Offer");
		}else if(isAccept) {
			newActDAO.setAction("Accept Offer");
		}else {
			newActDAO.setAction("Reject Offer");
		}
		
		newActDAO.setActionBy(remoteUser);
		newActDAO.setActionDttm(currTS);
		newActDAO.setOpPageNam(opPageName);
		newActDAO.setChngUser(remoteUser);
		newActDAO.setCreatUser(remoteUser);
		newActDAO.setRoleType(role.toUpperCase());
		
		elApplActRepository.save(newActDAO);
		String applNbr = elApplHdrDAO.getApplNbr();
		
		// send email
		if (isRelease) {
			sendEmailToNotifyReleaseOfLoa(applHdrId, opPageName);
			
			// update my AI
			commonRoutineService.callAIWorkListApprvAPI(applNbr, "true", remoteUser, "Pending Release Offer");
			createMyAiTaskForReleaseOfLoa(applHdrId);
		} else {
			sendEmailToRequestorAfterApplicantAction(applHdrId, opPageName, isAccept, remarks);
			commonRoutineService.callAIWorkListApprvAPI(applNbr, isAccept ? "true" : "false", remoteUser, "Pending Accept Offer");
			
			if (isAccept) {
				if (ApplStatusConstants.PENDING_RECTIFY.equals(elApplHdrDAO.getApplStatCde())) {
					createMyAdminTaskForFORectify(elApplHdrDAO);
				} else {
					createMyAdminTaskForReadySum(elApplHdrDAO);
				}
			} else {
				commonRoutineService.callAIWorkListCompleteAPI(applNbr, "false", "completed");
				createMyAiTaskForRejection(elApplHdrDAO);
			}
		}
//		sendEmailToRequestorToReview(applHdrId, opPageName, isAccept, remarks);
	}

	@Transactional(value = "transactionManagerJselMain", rollbackFor = Exception.class)
	private void insertLoA (String applHdrId, String opPageName, String updateUser) throws Exception {
		
		Object params = this.getFileDetailsById(applHdrId);
		ElUpldFileDAO fileDao = this.generateLoA(params, opPageName, updateUser);
		
		ElApplLoaDAO newLoaDAO = new ElApplLoaDAO();
		newLoaDAO.setApplHdrId(applHdrId);
		newLoaDAO.setFileCategory("Offer");
		newLoaDAO.setFileId(fileDao.getId());
		newLoaDAO.setCreatUser(updateUser);
		newLoaDAO.setChngUser(updateUser);
		newLoaDAO.setOpPageNam(opPageName);
		
		elApplLoaRepository.save(newLoaDAO);
		elUpldFileRepository.save(fileDao);
	}
	private ElUpldFileDAO generateLoA (Object documentParam, String opPageName, String updateUser) throws Exception {

		Document document = new Document();
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		PdfWriter writer = PdfWriter.getInstance(document, baos);
		String templateFileName = "pdf_example_old.ftl";
		
		String etap88Flag = elParamTabRepository.findETAP88Flag();
		
		if(etap88Flag != null && etap88Flag.equals("Y")) {
			templateFileName = "pdf_example.ftl";
		}
		
		
		
		document.open();
		
		String htmlStr = FreeMarkerTemplateUtils.processTemplateIntoString(
				freemarkerConfiguration.getTemplate(templateFileName, "UTF-8"), documentParam);
		
		XMLWorkerFontProvider fontImp = new XMLWorkerFontProvider(
				new ClassPathResource("font/msjhl.ttc").getPath());

		FontFactory.register(new ClassPathResource("font/msjhl.ttc").getPath(), "Microsoft JhengHei UI Light");

		FontFactory.setFontImp(fontImp);
		XMLWorkerHelper.getInstance().parseXHtml(writer, document,
				new ByteArrayInputStream(htmlStr.getBytes("UTF-8")), null, Charset.forName("UTF-8"), fontImp);
//		System.out.println(new ClassPathResource("src\\main\\webapp\\WEB-INF\\views\\resources\\msjh.ttc").getPath());
		
        document.close();
        
        ElUpldFileDAO dao = new ElUpldFileDAO();
        
        byte[] pdf = baos.toByteArray();
        dao.setFileContent(pdf);
        dao.setFileName("Letter of Appointment(LoA).pdf");
        dao.setFileType("application/pdf");
        dao.setFileExtension("pdf");
        
		dao.setCreatUser(updateUser);
		dao.setChngUser(updateUser);
		dao.setOpPageNam(opPageName);
		
//		elUpldFileRepository.save(dao);
		
		return dao;
	}
	
	private Map<String, Object> getFileDetailsById (String id) {
		Map<String, Object> params = new HashMap<String,Object>();
		try {
			
			List<Map<String, Object>> paramDetails = elApplLoaRepository.getFileDetails_old(id);
			
			String etap88Flag = elParamTabRepository.findETAP88Flag();
			
			if(etap88Flag != null && etap88Flag.equals("Y")) {
				paramDetails = elApplLoaRepository.getFileDetails(id);
			}
			if(paramDetails.size()>1) {
				log.error("More than one value when calling elApplLoaRepository.getFileDetails");
				throw new ArrayIndexOutOfBoundsException();
			}
			
			params.putAll(paramDetails.get(0));
			
			LocalDateTime currentDate = LocalDateTime.now();
			String formattedDate = currentDate.toString("dd/MM/YYYY");
			String URL = "src\\main\\webapp\\WEB-INF\\views\\resources\\images\\hkust.png";
				
			Timestamp NEW_APPL_START_DT = GeneralUtil.initNullTimestamp((Timestamp) params.getOrDefault("APPL_START_DT", null));
			Timestamp NEW_APPL_END_DT = GeneralUtil.initNullTimestamp((Timestamp) params.getOrDefault("APPL_END_DT", null));;
			Object NEW_PYMT_TYPE_CDE = params.getOrDefault("PYMT_TYPE_CDE", null);
			
			if (GeneralUtil.NULLTIMESTAMP == NEW_APPL_START_DT) {
				params.put("APPL_START_DT", null);	
			}else {
				params.put("APPL_START_DT", GeneralUtil.getStringByDate(NEW_APPL_START_DT));	
			}
			
			if (GeneralUtil.NULLTIMESTAMP == NEW_APPL_END_DT) {
				params.put("APPL_END_DT", null);	
			}else {
				params.put("APPL_END_DT", GeneralUtil.getStringByDate(NEW_APPL_END_DT));	
			}

			if (NEW_PYMT_TYPE_CDE instanceof String) {
				if (NEW_PYMT_TYPE_CDE.equals("INSTALM")) {
					params.put("PYMT_TYPE_CDE", "Installment(s)");
				}else if (NEW_PYMT_TYPE_CDE.equals("RECURR")) {
					params.put("PYMT_TYPE_CDE", "Monthly Recurring");
				}
			}
			
			params.put("date", formattedDate);
			params.put("url", URL);
//			params.put("APPL_START_DT", NEW_APPL_START_DT);	
//			params.put("APPL_END_DT", NEW_APPL_END_DT);	
//			params.put("appl_user_name", "SubjectA");
//			params.put("el_type_nam", "Other: Guest Lecturer");
//			params.put("acad_plan_descr", "A213 - Methematics (Financial Mathematics and Statistics)");
//			params.put("appl_start_dt", "1 July 2022");
//			params.put("appl_end_dt", "31 August 2022");
//			params.put("pymt_amt", "1,500.00 HKD");
//			params.put("pymt_type_cde", "Monthly");

		} catch (Exception e) {
			log.error("Error at LoaEventHandler.getFileDetailsById: "+e);
		}
		return params;
	}
	
//	private List<ElApplHdrDAO> getListForLoA (){
//		List<ElApplHdrDAO> postList = elApplHdrRepository.findAllBrPosted();
//
//		return postList;
//	}
	
	@Override
	@Transactional(value = "transactionManagerJselMain", rollbackFor = Exception.class)
	public void loaScheduledJob (String jobUser, String op_page_nam) throws Exception{
		List<ElApplHdrDAO> postList = elApplHdrRepository.findAllBrPosted();
		Timestamp currTS = GeneralUtil.getCurrentTimestamp();
		
		// create loa and save to repo
		for (ElApplHdrDAO dao: postList) {
			this.insertLoA(dao.getId(),op_page_nam,jobUser);
			dao.setApplStatCde(ApplStatusConstants.ISSUE_OFFER);
			
			// Insert to activity table
			ElApplActDAO newActDAO = new ElApplActDAO();
			newActDAO.setApplHdrId(dao.getId());

			newActDAO.setAction("Issue Offer");
			newActDAO.setActionBy("System");
			newActDAO.setActionDttm(currTS);
			newActDAO.setOpPageNam(op_page_nam);
			newActDAO.setChngUser(jobUser);
			newActDAO.setCreatUser(jobUser);
			newActDAO.setRoleType("SYSTEM");
			
			elApplActRepository.save(newActDAO);
			
			// send email
			sendEmailToNotifyIssueOfLoa(dao.getId(), op_page_nam);
			createMyAiTaskForIssueOfLoa(dao.getId());
		}
		
		// change status to issue_offer
		elApplHdrRepository.saveAll(postList);
		
		
	}

	@Override
	@Transactional(value = "transactionManagerJselMain", rollbackFor = Exception.class)
	public void sendRemindEmail (String applHdrId, JSONObject inputJson, String opPagenam) throws Exception{
		String remoteUser = SecurityUtils.getCurrentLogin();
		Timestamp currTS = GeneralUtil.getCurrentTimestamp();
		String newModCtrlTxt = GeneralUtil.genModCtrlTxt();
		
		String modCtrlTxt = inputJson.getString("currentModCtrlTxt");
		
		String role = inputJson.getString("role");
		boolean isRequester = role.equalsIgnoreCase(RoleConstants.REQUESTER);
		
		List<ElApplLoaDAO> elApplLoaDAOList = elApplLoaRepository.findByApplHdrId(applHdrId);
		ElApplHdrDAO elApplHdrDAO = elApplHdrRepository.findOne(applHdrId);

		// Check Exist
		if(elApplHdrDAO == null) {
            throw new RecordNotExistException("Appl Hdr ID: " + applHdrId);
		}
		if(elApplLoaDAOList == null) {
            throw new RecordNotExistException("Appl Hdr ID: " + applHdrId);
		}
		
		// Check Hdr modified
		if(!elApplHdrDAO.getModCtrlTxt().equals(modCtrlTxt)) {
			throw new RecordModifiedException();
		}
		
		// validation base on role
		if (isRequester) {
			if (!remoteUser.equals(elApplHdrDAO.getApplRequesterId())) {
				throw new InvalidParameterException("Not application requestor");
			}
			
			if (!elApplHdrDAO.getApplStatCde().equals(ApplStatusConstants.RELEASE_OFFER)) {
				throw new InvalidParameterException("Not available for sending remind email.");
			}
		} else {
			throw new InvalidParameterException("Not application requestor");
		}
		
		this.sendRemindEmailToNotifyReleaseOfLoa(applHdrId, opPagenam);
	}
	
	private void sendEmailToNotifyIssueOfLoa(String applHdrId, String opPageName) throws Exception {
		String webAppURLPrefix = env.getProperty(AppConstants.PATH_APP_PREFIX);
		ElApplHdrDAO elApplHdrDAO = elApplHdrRepository.findOne(applHdrId);
		
		String applicantId = elApplHdrDAO.getApplUserId();
		String requestorId = elApplHdrDAO.getApplRequesterId();
		String applNbr = elApplHdrDAO.getApplNbr();
		String reqestorName = elApplHdrDAO.getApplRequesterName();
		
		String emailFrom = " ";
		String emailTo = requestorId + "@ust.hk" ;
		String subject = "Letter of Appointment for Extra Load Application (Appl no. ${applNbr})".replace("${applNbr}", applNbr);
		String content = 
				"Dear " + reqestorName + ", " + "<br>"
				+ "<br>" 
				+ "The Letter of appointment (LoA) is issued for Extra Load Application (Appl no. ${applNbr}). Please click the following link to take action:" + "<br>"
				+ "<br>"
				+ GeneralUtil.createHyperLinks(webAppURLPrefix + "/loa_preview?id=" + applHdrId)
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
	
	private void sendEmailToNotifyReleaseOfLoa(String applHdrId, String opPageName) throws Exception {
		String webAppURLPrefix = env.getProperty(AppConstants.PATH_APP_PREFIX);
		ElApplHdrDAO elApplHdrDAO = elApplHdrRepository.findOne(applHdrId);
		
		String applicantId = elApplHdrDAO.getApplUserId();
		String applNbr = elApplHdrDAO.getApplNbr();
		String applicantName = elApplHdrDAO.getApplUserName();
		
		String emailFrom = " ";
		String emailTo = applicantId + "@ust.hk" ;
		String subject = "Pending LoA Acceptance for Extra Load Application (Appl no. ${applNbr})".replace("${applNbr}", applNbr);
		String content = 
				"Dear " + applicantName + ", " + "<br>"
				+ "<br>" 
				+ "The Letter of Appointment for extra load application (Appl no. ${applNbr}) is pending for your acceptance. Please click the following link to take action:" + "<br>"
				+ "<br>"
				+ GeneralUtil.createHyperLinks(webAppURLPrefix + "/loa_preview?id=" + applHdrId)
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
	
	private void sendRemindEmailToNotifyReleaseOfLoa(String applHdrId, String opPageName) throws Exception {
		String webAppURLPrefix = env.getProperty(AppConstants.PATH_APP_PREFIX);
		ElApplHdrDAO elApplHdrDAO = elApplHdrRepository.findOne(applHdrId);
		
		String applicantId = elApplHdrDAO.getApplUserId();
		String applNbr = elApplHdrDAO.getApplNbr();
		String applicantName = elApplHdrDAO.getApplUserName();
		
		String emailFrom = " ";
		String emailTo = applicantId + "@ust.hk" ;
		String subject = "Remind of Pending LoA Acceptance for Extra Load Application (Appl no. ${applNbr})".replace("${applNbr}", applNbr);
		String content = 
				"Dear " + applicantName + ", " + "<br>"
				+ "<br>"
				+ "This is a reminder for your pending LoA acceptance."
				+ "<br>"
				+ "<br>" 
				+ "The Letter of Appointment for extra load application (Appl no. ${applNbr}) is pending for your acceptance. Please click the following link to take action:" + "<br>"
				+ "<br>"
				+ GeneralUtil.createHyperLinks(webAppURLPrefix + "/loa_preview?id=" + applHdrId)
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
	
	private void sendEmailToRequestorAfterApplicantAction(String applHdrId, String opPageName, boolean isAccept, String remarks) throws Exception {
		remarks = GeneralUtil.initNullString(remarks);
		
		String webAppURLPrefix = env.getProperty(AppConstants.PATH_APP_PREFIX);
		ElApplHdrDAO elApplHdrDAO = elApplHdrRepository.findOne(applHdrId);
		
		String requestorId = elApplHdrDAO.getApplRequesterId();
		String requestorName = elApplHdrDAO.getApplRequesterName();
		String applNbr = elApplHdrDAO.getApplNbr();
		
		String emailFrom = " ";
		String emailTo = requestorId + "@ust.hk" ;
		String subject = "${action} LoA for Extra Load Application (Appl no. ${applNbr})".replace("${applNbr}", applNbr)
				.replace("${action}", isAccept ? "Accepted" : "Rejected");
		
		String content = 
				"Dear " + requestorName +", " + "<br>"
				+ "<br>" 
				+ "The Letter of Appointment for extra load application (Appl no. ${applNbr}) is ${action}."
				+ "<br><br>"
				+ (remarks.isBlank() ? "" : "Remarks by applicant: " + remarks + " <br><br>")
				+ "Please click the following link to take action:" + "<br>"
				+ "<br>"
				+ GeneralUtil.createHyperLinks(webAppURLPrefix + (isAccept ? "/extra_load_payment/view?id=" : "/loa_preview?id=") + applHdrId)
				+ "<br><br>"
				;
		
		content = content.replace("${applNbr}", applNbr).replace("${action}", isAccept ? "accepted" : "rejected");
		
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
	
	private void sendEmailToRequestorToReview(String applHdrId, String opPageName, boolean isAccept) throws Exception {
		String webAppURLPrefix = env.getProperty(AppConstants.PATH_APP_PREFIX);
		ElApplHdrDAO elApplHdrDAO = elApplHdrRepository.findOne(applHdrId);
		
		String requestorId = elApplHdrDAO.getApplRequesterId();
		String requestorName = elApplHdrDAO.getApplRequesterName();
		String applNbr = elApplHdrDAO.getApplNbr();
		
		String emailFrom = " ";
		String emailTo = requestorId + "@ust.hk" ;
		String subject = "Review / Amend the LoA for Extra Load Application (Appl no. ${applNbr})".replace("${applNbr}", applNbr);
		
		String content = 
				"Dear " + requestorName +", " + "<br>"
				+ "<br>" 
				+ "The Letter of Appointment for extra load application (Appl no. ${applNbr}) is asked for review / amend. Please click the following link to take action:" + "<br>"
				+ "<br>"
				+ GeneralUtil.createHyperLinks(webAppURLPrefix + "/loa_preview?id=" + applHdrId)
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
		
		elEmNotificationRepository.save(elEmNotificationDAO);
	}

	@Override
	public JSONArray getLoaEnqiuryList(JSONObject inputJson) throws Exception {
		JSONArray jsonArr = new JSONArray();
		
		// TODO: Validating user = FO?

		String applName = GeneralUtil.refineParam(inputJson.optString("appl_name").trim());
		String requesterName = GeneralUtil.refineParam(inputJson.optString("requester_name").trim());
		String applNbr = GeneralUtil.refineParam(inputJson.optString("appl_nbr").trim());
		String dept = GeneralUtil.refineParam(inputJson.optString("dept").trim());
		String applStatCde = GeneralUtil.refineParam(inputJson.optString("appl_stat_cde").trim());
		String brNo = GeneralUtil.refineParam(inputJson.optString("br_no").trim());
		
		List<Map<String, Object>> resultMapList = elApplHdrRepository.searchLoAEnquiry(applName, requesterName, applNbr, applStatCde, dept, brNo);	
		
		for (Map<String, Object> resultMap : resultMapList) {
			JSONObject jsonObj = new JSONObject();
			jsonArr.put(jsonObj);
			for (String keys : resultMap.keySet()) {
				jsonObj.put(keys.toLowerCase(), resultMap.get(keys));
			}
		}
		
		return jsonArr;
	}
	
	private void insertFORectifyApprv(String applHdrId, String opPageName) throws Exception {
		String remoteUser = SecurityUtils.getCurrentLogin();

		// insert FO P&B Approver for payment
		List<ElApplAprvStatusDAO> daoList= new ArrayList<>();
		// Add user under the role group as approver
		List<String> userList = userRoleGroupRepository.findByRoleGroupDesc("FO Rectification");
		
		for (String userName: userList) {	
			ElApplAprvStatusDAO dao = new ElApplAprvStatusDAO();
			
			dao.setApplHdrId(applHdrId);			
			dao.setArpvSeq(elApplAprvStatusRepository.findMaxAprvSeq(applHdrId) + 1);
			dao.setAprvTypeCde(AprvTypeConstants.FO_RECTIFY);
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
	
	private void createMyAiTaskForIssueOfLoa(String applHdrId) throws Exception {
		String webAppURLPrefix = env.getProperty(AppConstants.PATH_APP_PREFIX);
		ElApplHdrDAO elApplHdrDAO = elApplHdrRepository.findOne(applHdrId);
		
		String applNbr = elApplHdrDAO.getApplNbr();
		String subject = "Pending Release Letter of Appointment for Extra Load Application (Appl no. ${applNbr})".replace("${applNbr}", applNbr)
				+ " (Applicant : " + elApplHdrDAO.getApplUserName() + ")";
		
		try {
			JSONObject params = new JSONObject();
			params.put("request_id", elApplHdrDAO.getApplNbr());
			params.put("initiator", elApplHdrDAO.getApplRequesterId());
			params.put("action", "Pending Release Offer");
			params.put("message", subject);
			params.put("url", webAppURLPrefix + "/loa_preview?id=" + applHdrId);
			params.put("receiver", new JSONArray().put(elApplHdrDAO.getApplRequesterId()));

			commonRoutineService.callAIWorkListNotiAPI(params);
		} catch(Exception e) {
			log.error(e.toString());
		}
		
	}
	
	private void createMyAiTaskForReleaseOfLoa(String applHdrId) throws Exception {
		String webAppURLPrefix = env.getProperty(AppConstants.PATH_APP_PREFIX);
		ElApplHdrDAO elApplHdrDAO = elApplHdrRepository.findOne(applHdrId);
		
		String applNbr = elApplHdrDAO.getApplNbr();
		String subject = "Pending LoA Acceptance for Extra Load Application (Appl no. ${applNbr})".replace("${applNbr}", applNbr)
				+ " (Applicant : " + elApplHdrDAO.getApplUserName() + ")";
		
		try {
			JSONObject params = new JSONObject();
			params.put("request_id", elApplHdrDAO.getApplNbr());
			params.put("initiator", elApplHdrDAO.getApplRequesterId());
			params.put("action", "Pending Accept Offer");
			params.put("message", subject);
			params.put("url", webAppURLPrefix + "/loa_preview?id=" + applHdrId);
			params.put("receiver", new JSONArray().put(elApplHdrDAO.getApplUserId()));

			commonRoutineService.callAIWorkListNotiAPI(params);
		} catch(Exception e) {
			log.error(e.toString());
		}
		
	}
	
	private void createMyAiTaskForRejection(ElApplHdrDAO hdrDAO) {
		String webAppURLPrefix = env.getProperty(AppConstants.PATH_APP_PREFIX);
		String applNbr = hdrDAO.getApplNbr();
		String applUserName = hdrDAO.getApplUserName();
		
		String subject = "Rejected LoA for Extra Load Application (Appl no. ${applNbr})".replace("${applNbr}", applNbr) + " (Applicant : " + applUserName + ")";
		
		try {
			JSONObject params = new JSONObject();
			params.put("request_id", hdrDAO.getApplNbr());
			params.put("initiator", hdrDAO.getApplRequesterId());
			params.put("action", "Pending Decision");
			params.put("receiver", new JSONArray().put(hdrDAO.getApplRequesterId()));
			params.put("message", subject);
			params.put("url", webAppURLPrefix + "/loa_preview?id=" + hdrDAO.getId());

			commonRoutineService.callAIWorkListNotiAPI(params);
		} catch(Exception e) {
			log.error(e.toString());
		}
	}
	
	private void createMyAdminTaskForFORectify(ElApplHdrDAO hdrDAO) throws Exception {
		String webAppURLPrefix = env.getProperty(AppConstants.PATH_APP_PREFIX);
		String applNbr = hdrDAO.getApplNbr();
		String applUserName = hdrDAO.getApplUserName();
		List<ElApplAprvStatusDAO> pendingAprvs = elApplAprvStatusRepository.findAllNextFORectifyAprv(hdrDAO.getId());
		
		if (pendingAprvs.size() == 0) {
			throw new RecordNotExistException("Pending Approver");
		}
		
		String subject = "Pending FO Rectification for Extra Load Application (Appl no. ${applNbr})".replace("${applNbr}", applNbr) + " (Applicant : " + applUserName + ")";
		
		List<String> assignee = new ArrayList<>();
		
		for (var elApplAprvStatusDAO : pendingAprvs) {
			assignee.add(elApplAprvStatusDAO.getAprvUserId());
		}
		
		try {
			JSONObject params = new JSONObject();
			params.put("request_id", hdrDAO.getApplNbr());
			params.put("initiator", hdrDAO.getApplRequesterId());
			params.put("action", "Pending FO Rectification");
			params.put("message", subject);
			params.put("url", webAppURLPrefix + "/extra_load_payment/approve?id=" + hdrDAO.getId());
			params.put("receiver", new JSONArray(assignee));

			commonRoutineService.callAIWorkListNotiAPI(params);
		} catch(Exception e) {
			log.error(e.toString());
		}
	}
	
	public void createMyAdminTaskForReadySum(ElApplHdrDAO hdrDAO) {
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
}
