package jsfas.db.main.persistence.service;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;

import javax.inject.Inject;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.security.oauth2.client.OAuth2RestTemplate;
import org.springframework.transaction.annotation.Transactional;

import jsfas.common.constants.AppConstants;
import jsfas.common.exception.RecordModifiedException;
import jsfas.common.exception.RecordRemovedException;
import jsfas.common.json.CommonJson;
import jsfas.common.json.ResponseJson;
import jsfas.common.utils.GeneralUtil;
import jsfas.config.OAuth2Configuration;
import jsfas.db.main.persistence.domain.FunctionCatalogDAO;
import jsfas.db.main.persistence.domain.FunctionDAO;
import jsfas.db.main.persistence.domain.UserProfileDetailDAO;
import jsfas.db.main.persistence.domain.UserProfileDetailDAOPK;
import jsfas.db.main.persistence.domain.UserProfileHeaderDAO;
import jsfas.db.main.persistence.repository.FunctionCatalogRepository;
import jsfas.db.main.persistence.repository.FunctionRepository;
import jsfas.db.main.persistence.repository.UserProfileDetailRepository;
import jsfas.db.main.persistence.repository.UserProfileHeaderRepository;
import jsfas.security.SecurityUtils;

/**
 * @author iswill
 * @since 8/12/2016
 */
public class UserProfileEventHandler implements UserProfileService {
	@Autowired
	private Environment env;
	
	@Autowired
	private CommonRoutineService commonRoutineService;
	
	@Autowired
	private UserProfileHeaderRepository userProfileHeaderRepository;
	
	@Autowired
	private UserProfileDetailRepository userProfileDetailRepository;
	
	@Autowired
	private FunctionCatalogRepository functionCatalogRepository;
	
	@Autowired
	private FunctionRepository functionRepository;
	
	@Autowired
	private OAuth2RestTemplate jssvIsoOAuth2RestTemplate;
	
	private final Logger log = LoggerFactory.getLogger(this.getClass());
	
	@Override
	@Transactional(value = "transactionManagerJselMain", rollbackFor = Exception.class)
	public synchronized ResponseJson insertUserProfile(CommonJson inputJson, String opPageName) throws Exception {
		ResponseJson result = new ResponseJson();
		
		if (SecurityUtils.getCurrentLogin().equalsIgnoreCase(inputJson.getFilterValue("userName"))){
			result.setStatus(AppConstants.RESPONSE_JSON_FAIL_CODE);
			result.setMessage("For security purpose, you cannot setup your own user profile.");
			return result;
		}
		
		String remoteUser = SecurityUtils.getCurrentLogin();
		String modCtrlTxt = GeneralUtil.genModCtrlTxt();
		Timestamp createDate = GeneralUtil.getCurrentTimestamp();
		
		UserProfileHeaderDAO userProfile = userProfileHeaderRepository.findOne(inputJson.getFilterValue("userName"));
		commonRoutineService.setSecurityInfoPackage();
		
		if (userProfile == null) {
			result = validateUserName(inputJson.getFilterValue("userName"));
			if (result.getStatus().equalsIgnoreCase(AppConstants.RESPONSE_JSON_FAIL_CODE)) {
				return result;
			}
			
			userProfile = new UserProfileHeaderDAO();
			userProfile.setUserName(inputJson.getFilterValue("userName"));
			userProfile.setDefaultPrintQueue(GeneralUtil.initBlankString(inputJson.getFilterValue("printQueue")));
			
			userProfile.setModCtrlTxt(modCtrlTxt);
			userProfile.setCreateUser(remoteUser);
			userProfile.setCreateDate(createDate);
			userProfile.setChangeUser(remoteUser);
			userProfile.setChangeDate(createDate);
			userProfile.setOpPageName(opPageName);
		} else {
			result.setStatus(AppConstants.RESPONSE_JSON_FAIL_CODE);
			result.setMessage("User network account name (" + inputJson.getFilterValue("userName") + ") already existed.");
			return result;
		}
		
		String[] settingArray = inputJson.getFilterValue("userProfileSetting").split("\\|");
		
		String funcCode = " ";
		String funcSubCode = " ";
		
		for (String setting : settingArray) {
			if(!setting.trim().isEmpty()){
				funcCode = " ";
				funcSubCode = " ";
				
				if (setting != null && !setting.isEmpty()) {
					if (setting.length() > AppConstants.FUNC_CDE_LEN) {
						funcCode = setting.substring(0, AppConstants.FUNC_CDE_LEN);
						funcSubCode = setting.substring(AppConstants.FUNC_CDE_LEN);
					} else {
						funcCode = setting;
						funcSubCode = " ";
					}	
				}
				
				UserProfileDetailDAO userDetail = new UserProfileDetailDAO();
				UserProfileDetailDAOPK userDetailPk = new UserProfileDetailDAOPK();
				userDetailPk.setFunctionCode(funcCode);
				userDetailPk.setFunctionSubCode(funcSubCode);
				userDetailPk.setUserProfileHeader(userProfile);
				
				userDetail.setUserProfileDetailPK(userDetailPk);
				userDetail.setDataFilter1String(" ");
				userDetail.setDataFilter2String(" ");
				userDetail.setDataFilter3String(" ");
				
				userDetail.setModCtrlTxt(modCtrlTxt);
				userDetail.setCreateUser(remoteUser);
				userDetail.setCreateDate(createDate);
				userDetail.setChangeUser(remoteUser);
				userDetail.setChangeDate(createDate);
				userDetail.setOpPageName(opPageName);
				
				userProfile.getUserProfileDetails().add(userDetail);
			}
		}
		
		userProfileHeaderRepository.save(userProfile);
		userProfileHeaderRepository.flush();
		
		//insert first page
		userProfileHeaderRepository.save(userProfile);
		
		userProfileDetailRepository.insertFirstPages(AppConstants.SYS_CATG_CDE, inputJson.getFilterValue("userName"), remoteUser, createDate, modCtrlTxt, opPageName);
		
		result.setStatus(AppConstants.RESPONSE_JSON_SUCCESS_CODE);
		
		return result;
	}
	
	@Override
	@Transactional(value = "transactionManagerJselMain", rollbackFor = Exception.class)
	public synchronized ResponseJson updateUserProfile(CommonJson inputJson, String opPageName) throws Exception {
		ResponseJson result = new ResponseJson(); 
		
		if (SecurityUtils.getCurrentLogin().equalsIgnoreCase(inputJson.getFilterValue("userName"))){
			result.setStatus(AppConstants.RESPONSE_JSON_FAIL_CODE);
			result.setMessage("For security purpose, you cannot change your own user profile.");
			return result;
		}
		
		String remoteUser = SecurityUtils.getCurrentLogin();
		String modCtrlTxt = GeneralUtil.genModCtrlTxt();
		Timestamp changeDate = GeneralUtil.getCurrentTimestamp();
		
		UserProfileHeaderDAO userProfile = userProfileHeaderRepository.findOne(inputJson.getFilterValue("userName"));
		commonRoutineService.setSecurityInfoPackage();
		
		if (userProfile == null) {
			throw new RecordRemovedException();
		} else {
			if (!userProfile.getModCtrlTxt().equalsIgnoreCase(inputJson.getFilterValue("modCtrlTxt"))) {
				throw new RecordModifiedException();
			}
			
			userProfile.getUserProfileDetails().clear();
			userProfile.setDefaultPrintQueue(GeneralUtil.initBlankString(inputJson.getFilterValue("printQueue")));
			
			userProfile.setModCtrlTxt(modCtrlTxt);
			userProfile.setChangeUser(remoteUser);
			userProfile.setChangeDate(changeDate);
			userProfile.setOpPageName(opPageName);
		}
		
		String[] settingArray = inputJson.getFilterValue("userProfileSetting").split("\\|");
		
		String funcCode = " ";
		String funcSubCode = " ";
		
		for(String setting : settingArray){
			if(!setting.trim().isEmpty()){
				funcCode = " ";
				funcSubCode = " ";
				
				if (setting != null && !setting.isEmpty()) {
					if (setting.length() > AppConstants.FUNC_CDE_LEN) {
						funcCode = setting.substring(0, AppConstants.FUNC_CDE_LEN);
						funcSubCode = setting.substring(AppConstants.FUNC_CDE_LEN);
					} else {
						funcCode = setting;
						funcSubCode = " ";
					}	
				}
				
				UserProfileDetailDAO userDetail = new UserProfileDetailDAO();
				UserProfileDetailDAOPK userDetailPk = new UserProfileDetailDAOPK();
				userDetailPk.setFunctionCode(funcCode);
				userDetailPk.setFunctionSubCode(funcSubCode);
				userDetailPk.setUserProfileHeader(userProfile);
				
				userDetail.setUserProfileDetailPK(userDetailPk);
				
				userDetail.setDataFilter1String(" ");
				userDetail.setDataFilter2String(" ");
				userDetail.setDataFilter3String(" ");
				
				userDetail.setModCtrlTxt(modCtrlTxt);
				userDetail.setCreateUser(remoteUser);
				userDetail.setCreateDate(changeDate);
				userDetail.setChangeUser(remoteUser);
				userDetail.setChangeDate(changeDate);
				userDetail.setOpPageName(opPageName);
				
				userProfile.getUserProfileDetails().add(userDetail);
			}
		}
		
		//insert first page
		userProfileHeaderRepository.save(userProfile);
		userProfileHeaderRepository.flush();
		
		userProfileDetailRepository.insertFirstPages(AppConstants.SYS_CATG_CDE, inputJson.getFilterValue("userName"), remoteUser, changeDate, modCtrlTxt, opPageName);
		
		result.setStatus(AppConstants.RESPONSE_JSON_SUCCESS_CODE);
		
		return result;
	}

	@Override
	@Transactional(value = "transactionManagerJselMain", rollbackFor = Exception.class)
	public synchronized ResponseJson deleteUserProfile(CommonJson inputJson) throws Exception {
		ResponseJson result = new ResponseJson();
		
		if (SecurityUtils.getCurrentLogin().equalsIgnoreCase(inputJson.getFilterValue("userName"))){
			result.setStatus(AppConstants.RESPONSE_JSON_FAIL_CODE);
			result.setMessage("For security purpose, you cannot setup your own user profile.");
			return result;
		}
		
		UserProfileHeaderDAO userProfileToRemove = userProfileHeaderRepository.findOne(inputJson.getFilterValue("userName"));
		commonRoutineService.setSecurityInfoPackage();
		
		if (userProfileToRemove == null) {
			throw new RecordRemovedException();
		}
		
		if (!userProfileToRemove.getModCtrlTxt().equalsIgnoreCase(inputJson.getFilterValue("modCtrlTxt"))) {
			throw new RecordModifiedException();
		}
		
		userProfileToRemove.getUserProfileDetails().clear();
		userProfileHeaderRepository.delete(userProfileToRemove);
		
		result.setStatus(AppConstants.RESPONSE_JSON_SUCCESS_CODE);
		
		return result;
	}
	
	private ResponseJson validateUserName(String userName) {
		ResponseJson output = new ResponseJson();
		
		try {
			CommonJson commonJson = jssvIsoOAuth2RestTemplate.getForObject(env.getProperty(AppConstants.JSSV_LINK) 
					+ "/oths/check_user_email?username=" + userName + "&target=USTLD2", CommonJson.class);
			
			if (commonJson.getFilterValue("status").contentEquals(AppConstants.RESPONSE_JSON_SUCCESS_CODE)) {
				output.setStatus(AppConstants.RESPONSE_JSON_SUCCESS_CODE);
			} else {
				output.setStatus(AppConstants.RESPONSE_JSON_FAIL_CODE);
				output.setMessage("User network account name ("+userName+") is not valid.");
				
				log.debug("api return error: {}", commonJson.getProps().toString());
			}
		} catch (Exception e) {
			output.setStatus(AppConstants.RESPONSE_JSON_FAIL_CODE);
			output.setMessage("Error occur when validating the user ("+userName+").");
			
			log.error(Thread.currentThread().getStackTrace()[1].getMethodName()+"@"+this.getClass().getSimpleName()+" Error Occur...", e);
		}
		
		return output;
	}
	
	@Override
	public List<CommonJson> getUserProfileList(String userName) {
		List<CommonJson> outputJsonList = new ArrayList<CommonJson>();
		List<UserProfileHeaderDAO> userProfileHeaderList = userProfileHeaderRepository.findByUserNameContainingAllIgnoringCase(userName);
		Collections.sort(userProfileHeaderList);
		
		for (UserProfileHeaderDAO userProfileHeaderDAO: userProfileHeaderList) {
			CommonJson outputJson = new CommonJson();
			
			outputJson.set("userName", userProfileHeaderDAO.getUserName());
			outputJsonList.add(outputJson);
		}
		
		return outputJsonList;
	}
	
	@SuppressWarnings("unchecked")
    @Override
	public List<CommonJson> getUserProfileView(String userName) {
		List<CommonJson> result = new ArrayList<CommonJson>();
		UserProfileHeaderDAO userProfile = userProfileHeaderRepository.findOne(userName);
		
		if(userProfile != null) {
			CommonJson sysCatg = new CommonJson();
			sysCatg.set("funcCatgs", new ArrayList<CommonJson>());
			sysCatg.set("seq", 1);
			
			List<FunctionCatalogDAO> functionCatalogDAOs = functionCatalogRepository.findBySystemCatgCode(AppConstants.SYS_CATG_CDE);
			Collections.sort(functionCatalogDAOs);
			
			for(FunctionCatalogDAO functionCatalogDAO : functionCatalogDAOs) {
				CommonJson funcCatg = new CommonJson();
				funcCatg.set("functions", new ArrayList<CommonJson>());
				funcCatg.set("seq", functionCatalogDAO.getFunctionCatalogSeq());
				funcCatg.set("functionCatgDesc",functionCatalogDAO.getFunctionCatalogDesc());
				List<FunctionDAO> functionDAOs  = functionRepository.findBySystemCatgCodeAndFuncCatgCodeAndUserAndMeunGenOrUserProfGen(
						functionCatalogDAO.getFunctionCatalogPK().getSystemCatalogCode(), functionCatalogDAO.getFunctionCatalogPK().getFunctionCatalogCode(), userName);
				
				if (!functionDAOs.isEmpty()) {
					Collections.sort(functionDAOs);
					String prevfuncCode = "";
					CommonJson func = new CommonJson();
					func.set("subFunctions", new ArrayList<CommonJson>());
					
					for (FunctionDAO functionDAO : functionDAOs){
						if (functionDAO.getFunctionPK().getFunctionCode().equalsIgnoreCase(prevfuncCode)){ //has func sub code
							CommonJson subFunc = new CommonJson();
							subFunc.set("subFuncSeq", functionDAO.getFunctionSubSeq());
							subFunc.set("funcSubCode", functionDAO.getFunctionPK().getFunctionSubCode());
							subFunc.set("subFuncDesc", functionDAO.getFunctionDesc());
							subFunc.set("dataLevelFilter", "");
							//subFunctions.add(subFunc);
							func.get("subFunctions", ArrayList.class).add(subFunc);
						} else {
							if(!prevfuncCode.isEmpty()){
								funcCatg.get("functions", ArrayList.class).add(func);
							}	
							prevfuncCode = functionDAO.getFunctionPK().getFunctionCode();
							func = new CommonJson();
							func.set("subFunctions", new ArrayList<CommonJson>());
							func.set("funcCode", functionDAO.getFunctionPK().getFunctionCode());
							func.set("funcDesc", functionDAO.getFunctionDesc());
							func.set("funcSeq", functionDAO.getFunctionSeq());
							func.set("dataLevelFilter", "");
						}					
					}
					
					funcCatg.get("functions", ArrayList.class).add(func);
					sysCatg.get("funcCatgs", ArrayList.class).add(funcCatg);
				}				
			}
			
			if (!sysCatg.get("funcCatgs", ArrayList.class).isEmpty()) {
				result.add(sysCatg);
			}
		}
		
		return result;
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public List<CommonJson> getUserProfileAddView() {
		List<CommonJson> result = new ArrayList<CommonJson>();
		
		CommonJson sysCatg = new CommonJson();
		sysCatg.set("funcCatgs", new ArrayList<CommonJson>());
		sysCatg.set("seq", 1);
		
		List<FunctionCatalogDAO> functionCatalogDAOs = functionCatalogRepository.findBySystemCatgCode(AppConstants.SYS_CATG_CDE);
		Collections.sort(functionCatalogDAOs);
		
		for (FunctionCatalogDAO functionCatalogDAO : functionCatalogDAOs){
			CommonJson funcCatg = new CommonJson();
			funcCatg.set("functions", new ArrayList<CommonJson>());
			funcCatg.set("dataLevelFilter","");
			funcCatg.set("seq", functionCatalogDAO.getFunctionCatalogSeq());
			funcCatg.set("functionCatgDesc", functionCatalogDAO.getFunctionCatalogDesc());
			List<FunctionDAO> functionDAOs  = functionRepository.findBySystemCatgCodeAndFuncCatgCodeAndMeunGenOrUserProfGen(
					functionCatalogDAO.getFunctionCatalogPK().getSystemCatalogCode(), functionCatalogDAO.getFunctionCatalogPK().getFunctionCatalogCode());
			
			if(!functionDAOs.isEmpty()){
				Collections.sort(functionDAOs);
				String prevfuncCode = "";
				CommonJson func = new CommonJson();
				func.set("subFunctions", new ArrayList<CommonJson>());
				
				for (FunctionDAO functionDAO : functionDAOs) {
					if (functionDAO.getFunctionPK().getFunctionCode().equalsIgnoreCase(prevfuncCode)) { //has func sub code
						CommonJson subFunc = new CommonJson();
						subFunc.set("subFuncSeq", functionDAO.getFunctionSubSeq());
						subFunc.set("funcSubCode", functionDAO.getFunctionPK().getFunctionSubCode());
						subFunc.set("subFuncDesc", functionDAO.getFunctionDesc());
						subFunc.set("dataLevelFilter", "");
						func.get("subFunctions", ArrayList.class).add(subFunc);
					} else {
						if (!prevfuncCode.isEmpty()) {
							funcCatg.get("functions", ArrayList.class).add(func);
						}	
						prevfuncCode = functionDAO.getFunctionPK().getFunctionCode();
						func = new CommonJson();
						func.set("subFunctions", new ArrayList<CommonJson>());
						func.set("funcCode", functionDAO.getFunctionPK().getFunctionCode());
						func.set("funcDesc", functionDAO.getFunctionDesc());
						func.set("funcSeq", functionDAO.getFunctionSeq());
						func.set("dataLevelFilter", "");
					}					
				}
				
				funcCatg.get("functions", ArrayList.class).add(func);
				sysCatg.get("funcCatgs", ArrayList.class).add(funcCatg);
			}				
		}
		
		if (!sysCatg.get("funcCatgs", ArrayList.class).isEmpty()) {
			result.add(sysCatg);
		}
		
		return result;
	}

	@Override
	public CommonJson getUserProfile(String userName) {
		CommonJson outputJson = new CommonJson();
		UserProfileHeaderDAO userProfileHeaderDAO = userProfileHeaderRepository.findOne(userName);
		
		outputJson.set("userName", userProfileHeaderDAO.getUserName());
		outputJson.set("defaultPrintQueue", userProfileHeaderDAO.getDefaultPrintQueue());
		outputJson.set("createUser", userProfileHeaderDAO.getCreateUser());
		outputJson.set("createDate", GeneralUtil.convertTimestampToString(userProfileHeaderDAO.getCreateDate(), "D"));
		outputJson.set("changeUser", userProfileHeaderDAO.getChangeUser());
		outputJson.set("changeDate", GeneralUtil.convertTimestampToString(userProfileHeaderDAO.getChangeDate(), "D"));
		outputJson.set("modCtrlTxt", userProfileHeaderDAO.getModCtrlTxt());
		
		return outputJson;
	}
	
	@Override
	public CommonJson getUserProfileWithDetails(String userName) {
		CommonJson outputJson = new CommonJson();
		UserProfileHeaderDAO userProfileHeaderDAO = userProfileHeaderRepository.findOne(userName);
		
		outputJson.set("userName", userProfileHeaderDAO.getUserName());
		outputJson.set("defaultPrintQueue", userProfileHeaderDAO.getDefaultPrintQueue());
		outputJson.set("createUser", userProfileHeaderDAO.getCreateUser());
		outputJson.set("createDate", GeneralUtil.convertTimestampToString(userProfileHeaderDAO.getCreateDate(), "D"));
		outputJson.set("changeUser", userProfileHeaderDAO.getChangeUser());
		outputJson.set("changeDate", GeneralUtil.convertTimestampToString(userProfileHeaderDAO.getChangeDate(), "D"));
		outputJson.set("modCtrlTxt", userProfileHeaderDAO.getModCtrlTxt());
		
		outputJson.set("userProfileDetails", userProfileHeaderDAO.getUserProfileDetails());
		
		return outputJson;
	}
}
