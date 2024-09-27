package jsfas.db.main.persistence.service;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Method;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import javax.inject.Inject;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import javax.servlet.http.HttpServletRequest;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.networknt.schema.JsonSchema;
import com.networknt.schema.JsonSchemaFactory;
import com.networknt.schema.SpecVersion;
import com.networknt.schema.ValidationMessage;

import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.mail.javamail.MimeMessagePreparator;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.ui.freemarker.FreeMarkerTemplateUtils;
import org.springframework.util.ResourceUtils;
import org.springframework.web.servlet.ModelAndView;

import freemarker.template.Configuration;
import jsfas.common.CacheVersion;
import jsfas.common.annotation.Function;
import jsfas.common.annotation.Functions;
import jsfas.common.constants.AppConstants;
import jsfas.common.constants.RestURIConstants;
import jsfas.common.exception.JsonSchemaLoadingFailedException;
import jsfas.common.json.CommonJson;
import jsfas.common.json.ResponseJson;
import jsfas.common.object.MenuItem;
import jsfas.common.utils.GeneralUtil;
import jsfas.db.main.persistence.domain.FunctionCatalogDAO;
import jsfas.db.main.persistence.domain.FunctionDAO;
import jsfas.db.main.persistence.domain.FunctionDAOPK;
import jsfas.db.main.persistence.domain.FunctionPageDAO;
import jsfas.db.main.persistence.domain.SystemCatalogDAO;
import jsfas.db.main.persistence.domain.UserProfileDetailDAO;
import jsfas.db.main.persistence.domain.UserProfileHeaderDAO;
import jsfas.db.main.persistence.repository.FunctionCatalogRepository;
import jsfas.db.main.persistence.repository.FunctionPageRepository;
import jsfas.db.main.persistence.repository.FunctionRepository;
import jsfas.db.main.persistence.repository.MessageTableRepository;
import jsfas.db.main.persistence.repository.SystemCatalogRepository;
import jsfas.db.main.persistence.repository.UserProfileDetailRepository;
import jsfas.db.main.persistence.repository.UserProfileHeaderRepository;
import jsfas.security.SecurityUtils;

public class CommonRoutineEventHandler implements CommonRoutineService {

	@Autowired
	private Environment env;
	
	private final Logger log = LoggerFactory.getLogger(CommonRoutineEventHandler.class);
	
	@Autowired
	private SystemCatalogRepository systemCatalogRepository;
	
	@Autowired
	private FunctionPageRepository functionPageRepository;
	
	@Autowired
	private FunctionRepository functionRepository;
	
	@Autowired
	private FunctionCatalogRepository functionCatalogRepository;

	@Autowired
	private UserProfileHeaderRepository userProfileHeaderRepository;
	
	@Autowired
	private UserProfileDetailRepository userProfileDetailRepository;

	@Autowired
	private MessageTableRepository messageTableRepository;
	
	@Autowired
	private Configuration freemarkerConfiguration;
	
	@Autowired
	private JavaMailSender javaMailSender;
	
	@Autowired
	private CacheVersion cacheVersion;
	
	private final Map<String, JsonSchema> schemaCache = new HashMap<>();
	
	private final ObjectMapper objectMapper = new ObjectMapper();
	
	@Override
	public boolean isUserApprovedForFuncPage(String pageName) {

		Authentication auth = SecurityContextHolder.getContext().getAuthentication();		
		String userName = auth.getName();
		
		UserProfileHeaderDAO user = userProfileHeaderRepository.findOne(userName);
		List<FunctionPageDAO> functionPages = functionPageRepository.findByPageName(pageName);
		
		if(user != null) {
			for(UserProfileDetailDAO detail : user.getUserProfileDetails()) {
				
				//Logic for func page with funcCode only
				if(functionPages.get(0).getFunctionPagePK().getFunction().getFunctionPK().getFunctionSubCode().trim().isEmpty()) {
					if(detail.getUserProfileDetailPK().getFunctionCode().equalsIgnoreCase(functionPages.get(0).getFunctionPagePK().getFunction().getFunctionPK().getFunctionCode())) {
						return true;
					}
				}
				
				//Logic for func page with funcCode + funcSubCode 
				if(detail.getUserProfileDetailPK().getFunctionCode().equalsIgnoreCase(functionPages.get(0).getFunctionPagePK().getFunction().getFunctionPK().getFunctionCode())
						&& detail.getUserProfileDetailPK().getFunctionSubCode().equalsIgnoreCase(functionPages.get(0).getFunctionPagePK().getFunction().getFunctionPK().getFunctionSubCode())) {
					return true;
				}
								
			}
		}
		
		return false;
	}
	
	@Override
	public boolean isAuthorized(String functionCode, String functionSubCode, String functionPage) {
		String loginUser = SecurityUtils.getCurrentLogin();
		
		if (loginUser != null) {
		    if(env.getProperty(AppConstants.RBAC_PERM_ENABLED, Boolean.class, false)) {
		        List<FunctionPageDAO> functionPageList = Collections.emptyList();
		        
		        if(functionPage == null || functionPage.trim().isEmpty()) {
		            functionPageList = functionPageRepository.findByFuncCdeFuncSubCde(functionCode, functionSubCode);
		        } else {
		            functionPageList = functionPageRepository.findByFuncCdeFuncSubCdePageName(functionCode, functionSubCode, functionPage);
		        }
		        
		        if(functionPageList.isEmpty()) {
		            return false;
		        }
	            FunctionPageDAO functionPageDao = functionPageList.get(0);
	            String rbacPerm = GeneralUtil.initNullString(functionPageDao.getRbacPerm()).trim();
	            return !rbacPerm.isEmpty()? SecurityUtils.getSubject().isPermitted(rbacPerm): false;
		    } else {
                if (functionSubCode == null || functionSubCode.trim().isEmpty()) {
                    functionSubCode = "%%";
                }

                List<UserProfileDetailDAO> userProfileDetail = userProfileDetailRepository.findAuthorized(loginUser, functionCode, functionSubCode);
                if (userProfileDetail.size() > 0) {
                    return true;
                }
		    }
		}
		return false;
	}
	
	@Override
	public boolean isAuthorized(Function[] functions) {
		if (functions != null) {
			for (Function function: functions) {
			    String functionCode = function.functionCode();
                String functionSubCode = function.functionSubCode();
                String functionPage = function.functionPage();
                
			    if (isAuthorized(functionCode, functionSubCode, functionPage)) {
			        return true;
			    }
			}
			return false;
		}
		return false;
	};

	@Override
	public String getFuncPageTitleByFuncPage(String pageName) {
		List<FunctionPageDAO> pages = functionPageRepository.findByPageName(pageName);
		return pages.get(0).getPageTitleTxt();
	}

	@Override
	public String getFuncDescByFuncPage(String pageName) {
		List<FunctionPageDAO> pages = functionPageRepository.findByPageName(pageName);
		return pages.get(0).getFunctionPagePK().getFunction().getFunctionDesc();
	}
	
	@Override
	public FunctionPageDAO getFuncPage(String funcCde, String funcSubCde, String pageName) {
		List<FunctionPageDAO> functionPageList = functionPageRepository.findByFuncCdeFuncSubCdePageName(funcCde, funcSubCde, pageName);
		if (!functionPageList.isEmpty()) {
			return functionPageList.get(0);
		}
		return null;
	}

	@Override
	public List<String> getFuncCodesByFuncPage(String pageName) {
		List<String> result = new ArrayList<String>();
		List<FunctionPageDAO> pages = functionPageRepository.findByPageName(pageName);
		result.add(pages.get(0).getFunctionPagePK().getFunction().getFunctionPK().getFunctionCode());
		result.add(pages.get(0).getFunctionPagePK().getFunction().getFunctionPK().getFunctionSubCode());
		return result;
	}

	@Override
	public String getSystemCatalogDescByFuncPage(String pageName) {
		List<FunctionPageDAO> pages = functionPageRepository.findByPageName(pageName);
		return pages.get(0).getFunctionPagePK().getFunction().getSystemCatalog().getSystemCatalogLongDesc();
	}

	@Override
	public List<SystemCatalogDAO> getMenuGenSysCatalog() {
		List<SystemCatalogDAO> result = new ArrayList<SystemCatalogDAO>();
		result = systemCatalogRepository.findByMenuGen();
		Collections.sort(result);
		return result;
	}
	
	@Override
	public List<FunctionDAO> getMenuGenFuncMenuByUser(String sysCatgCode) {
		List<FunctionDAO> result = functionRepository.findBySystemCatgCodeAndMenuGenAndUser(sysCatgCode, SecurityUtils.getCurrentLogin()); 
		Collections.sort(result);
		return result;
	}

	@Override
	public SystemCatalogDAO getSystemCatalog(String sysCatCde) {
		return systemCatalogRepository.findById(sysCatCde).get();
	}
	
	@Override
	public List<FunctionCatalogDAO> getFuncCatalog(String sysCatgCode) {
		List<FunctionCatalogDAO> result = new ArrayList<FunctionCatalogDAO>();
		result = functionCatalogRepository.findBySystemCatgCode(sysCatgCode);
		//Collections.sort(result);
		return result;
	}

	@Override
	public List<FunctionDAO> getFuncMenu(String sysCatgCode, String funcCatgCode) {
		String userName = SecurityUtils.getCurrentLogin();
		List<FunctionDAO> result = functionRepository.findBySystemCatgCodeAndFuncCatgCodeAndUser(sysCatgCode, funcCatgCode, userName); 
		Collections.sort(result);
		return result;
	}

	@Override
	public List<FunctionDAO> getAllFuncMenu(String sysCatgCode, String funcCatgCode) {
		List<FunctionDAO> result = functionRepository.findBySystemCatgCodeAndFuncCatgCode(sysCatgCode, funcCatgCode); 
		Collections.sort(result);
		return result;
	}

	@Override
	public FunctionCatalogDAO getFuncCatalog(String sysCatgCode, String funcCatgCode) {
		List<FunctionCatalogDAO> catalogs = functionCatalogRepository.findBySystemCatgCodeAndFuncCatgCode(sysCatgCode, funcCatgCode);
		if(catalogs.size() > 0) {
			return catalogs.get(0);
		}
		else {
			return null;
		}
	}
	
	@Override
	public List<FunctionDAO> getFuncForMenu(String sysCatgCode) {
		List<FunctionDAO> result = functionRepository.findBySystemCatgCodeForMenu(sysCatgCode); 
		return result;
	}
	
	@Override
	public Map<String, FunctionCatalogDAO> getFuncCatalogMap(String sysCatgCode) {
		Map<String, FunctionCatalogDAO> functionCatalogMap = new LinkedHashMap<String, FunctionCatalogDAO>();
		List<FunctionCatalogDAO> FunctionCatalogList = functionCatalogRepository.findBySystemCatgCode(sysCatgCode);
		
		for (FunctionCatalogDAO functionCatalogDAO : FunctionCatalogList) {
			functionCatalogMap.put(
					GeneralUtil.initBlankString(functionCatalogDAO.getFunctionCatalogPK().getSystemCatalogCode()) + 
					GeneralUtil.initBlankString(functionCatalogDAO.getFunctionCatalogPK().getFunctionCatalogCode()), 
					functionCatalogDAO);
		}
		
		return functionCatalogMap;
	}
	
	@Override
	public FunctionDAO getFunction(String funcCode, String funcSubCode) {
		FunctionDAOPK functionDAO = new FunctionDAOPK();
		functionDAO.setFunctionCode(funcCode);
		functionDAO.setFunctionSubCode(funcSubCode);
		
		return functionRepository.findById(functionDAO).get();
	}
	
	@Override
	public Map<String, UserProfileDetailDAO> getUserDetailForMenuMap(String userName) {
		Map<String, UserProfileDetailDAO> userProfileDetailListMap = new HashMap<String, UserProfileDetailDAO>();
		List<UserProfileDetailDAO> gpUserProfileDetailList = userProfileDetailRepository.findByUserName(userName);
		
		for (UserProfileDetailDAO userProfileDetailDAO: gpUserProfileDetailList) {
			userProfileDetailListMap.put(
					GeneralUtil.initBlankString(userProfileDetailDAO.getUserProfileDetailPK().getFunctionCode()) + 
					GeneralUtil.initBlankString(userProfileDetailDAO.getUserProfileDetailPK().getFunctionSubCode()), 
					userProfileDetailDAO);
		}
		
		return userProfileDetailListMap;
	}

	@Override
	public String getMessage(String code, String... vars) {
		return getMessagePlaceholder(messageTableRepository.findByMessageCodeAllIgnoringCase(code).get(0).getMessageText(), vars);
	}
	
	@Override
	public String getMessageForHtml(String code, String... vars) {
		return getMessage(code, vars).concat(getMessagePlaceholder("<span class='pull-right'>(&1) &2</span>", code, GeneralUtil.genModCtrlTxt()));
	}
	
	@Override
	public String getMessagePlaceholder(String message, String... vars) {
	    for (int i = 0; i < vars.length; i++) {
            message = message.replace("&" + (i+1), vars[i]);
        }
        return message;
	}
	
	@Override
	public String getMessagePlaceholderForHtml(String message, String... vars) {
	    return getMessagePlaceholder(message, vars).concat(getMessagePlaceholder("<span class='pull-right'>&1</span>", GeneralUtil.genModCtrlTxt()));
	}

	@Override
	public ModelAndView getNotAuthView(String pageUri) {
		String systemCatalogDesc = getSystemCatalogDescByFuncPage(pageUri.split("/")[1]);
		String funcDesc = getFuncDescByFuncPage(pageUri.split("/")[1]);
		String message = getMessagePlaceholderForHtml("Sorry, you have not been authorized to access the <b><i>'&1 - &2'</i></b>"
		        , systemCatalogDesc, funcDesc);
		ModelAndView output = new ModelAndView("web/error");
		output.addObject("errorMsg", message);
		return output;
	}

	@Override
	public ResponseJson getNotAuthResponse(String pageUri) {
		String systemCatalogDesc = getSystemCatalogDescByFuncPage(pageUri.split("/")[1]);
		String funcDesc = getFuncDescByFuncPage(pageUri.split("/")[1]);
		String message = "Sorry, you have not been authorized to access the <b><i>'"+
							systemCatalogDesc+" - "+funcDesc+"'</b></i>";
		ResponseJson result = new ResponseJson();
		result.setStatus(AppConstants.RESPONSE_JSON_FAIL_CODE);
		result.setMessage(message);			
		return result;
	}

	@Override
	public ResponseJson sendEmail(String emailFrom, String emailFromName,
			String emailTo, String emailCc, String emailSubject,
			String emailBody, boolean isGenFooter) {
		ResponseJson result = new ResponseJson();
		
		emailCc = GeneralUtil.initBlankString(emailCc);
		emailTo = GeneralUtil.initBlankString(emailTo);
		
		if(isGenFooter) {
			emailBody = emailBody +  "<br>+--------------------------------------------------------------------------+<br>";
			emailBody = emailBody +  "|&nbsp;&nbsp;&nbsp;This is a system-generated email, please DO NOT reply&nbsp;&nbsp;&nbsp;|<br>";
			emailBody = emailBody +  "+--------------------------------------------------------------------------+<br></p>";
		}
		
		String smtpHost = env.getProperty(AppConstants.SMTP_HOST);
		String smtpUsername = env.getProperty(AppConstants.SMTP_USERNAME);
		String smtpPassword = env.getProperty(AppConstants.SMTP_PASSWORD);
		
		Properties props = new Properties();
		props.put("mail.smtp.host", smtpHost);		
		Session session;
		
		//----------------------------------------------------------------------------------
		//for local test, please change the smtp.login.required=y in application.properties
		//----------------------------------------------------------------------------------
		if(env.getProperty(AppConstants.SMTP_LOGIN_REQUIRED).equalsIgnoreCase("y")){
			props.put("mail.smtp.auth", "true");
			session = Session.getDefaultInstance(props, new javax.mail.Authenticator() {
				protected PasswordAuthentication getPasswordAuthentication() {
					return new PasswordAuthentication(smtpUsername, smtpPassword);
				}
			});
		}
		else {
			props.put("mail.smtp.auth", "false");
			session = Session.getDefaultInstance(props, new javax.mail.Authenticator() {});
		}
		
		try {	           
			Message msg = new MimeMessage(session);
            msg.setFrom(new InternetAddress(emailFrom, emailFromName));
            
            String[] emailToArray = emailTo.split(",");
            String[] emailCcArray = emailCc.split(",");
            
           	for(String email : emailToArray) {
           		if(!email.trim().isEmpty()) {
           			msg.addRecipient(Message.RecipientType.TO, new InternetAddress(email));
           		}                	
            }
            
        	for(String email : emailCcArray) {
        		if(!email.trim().isEmpty()) {
        			msg.addRecipient(Message.RecipientType.CC, new InternetAddress(email));
        		}            		
            }

            msg.setSubject(emailSubject);
            msg.setContent(emailBody, "text/html; charset=utf-8");
            Transport.send(msg);
            result.setStatus(AppConstants.RESPONSE_JSON_SUCCESS_CODE);
        }
		catch(AddressException e) {
            result.setStatus(AppConstants.RESPONSE_JSON_FAIL_CODE);
            result.setMessage("Email Address Error.");
            log.error("AddressException = " + e);
        }
		catch(MessagingException e) {
        	result.setStatus(AppConstants.RESPONSE_JSON_FAIL_CODE);
        	log.error("MessagingException = " + e);
        }
		catch(UnsupportedEncodingException e) {
        	result.setStatus(AppConstants.RESPONSE_JSON_FAIL_CODE);
        	log.error("UnsupportedEncodingException = " + e);
		}
		
		return result;
	}
	
	@SuppressWarnings("rawtypes")
    @Override
	public void sendEmail(final String from, final String fromName, final String to, final String[] cc, final String[] bcc,
			final String subject, final String templateFile, final Map params) throws Exception {
		
		MimeMessagePreparator preparator = new MimeMessagePreparator() {
			@Override
			public void prepare(MimeMessage mimeMessage) throws Exception {
				MimeMessageHelper message = new MimeMessageHelper(mimeMessage, true, "UTF-8");

				message.setFrom(GeneralUtil.initBlankString(from), GeneralUtil.initBlankString(fromName));
				message.setTo(GeneralUtil.initBlankString(to));
				
				if (cc != null && cc.length > 0 ) {
					message.setCc(cc);
				}
				
				if (bcc != null && bcc.length > 0) {
					message.setBcc(bcc);
				}
				
				message.setSubject(GeneralUtil.initBlankString(subject));
				
				String text = FreeMarkerTemplateUtils.processTemplateIntoString(freemarkerConfiguration.getTemplate(templateFile, "UTF-8"), params);
				message.setText(text, true);
			}
		};

		javaMailSender.send(preparator);
	}
	
	@Override
	public void setSecurityInfoPackage() {
		try {
			if (SecurityUtils.getCurrentLogin().trim().equalsIgnoreCase("anonymousUser")) {
				userProfileHeaderRepository.setUserName("anonymousUser");
			} else {
				userProfileHeaderRepository.setUserName(SecurityUtils.getCurrentLogin());
			}						
		} catch (Exception e) {
			
		} 
	}
	
	private List<MenuItem> getMenuItemList(String sysCatCde) {
		List<MenuItem> menuItemList = new ArrayList<MenuItem>();
		Map<String, FunctionCatalogDAO> funcCatalogMap = getFuncCatalogMap(sysCatCde);
		
		for (Map.Entry<String, FunctionCatalogDAO> entry : funcCatalogMap.entrySet()) {
		    String key = entry.getKey();
		    FunctionCatalogDAO functionCatalog = entry.getValue();
		    List<MenuItem> subItemList = new ArrayList<MenuItem>();
		    
		    List<FunctionDAO> functionList = functionCatalog.getFunctions();
		    Collections.sort(functionList);
		    
		    for (FunctionDAO function : functionList) {
		    	if (function.getMenuGenInd().equalsIgnoreCase("Y")) {
		    		subItemList.add(new MenuItem(
		    				function.getFunctionPK().getFunctionCode().trim() + function.getFunctionPK().getFunctionSubCode().trim()
		    				, function.getFunctionDesc(), "/" + function.getFirstPageName() + "?dummy=" + cacheVersion.getStaticVer()));
		    	}
		    }
		    
		    if(!subItemList.isEmpty()) {
		        menuItemList.add(new MenuItem(key, functionCatalog.getFunctionCatalogDesc(), subItemList));
		    }
		}
		
		menuItemList.add(new MenuItem("logout", "Logout", RestURIConstants.LOGOUT));
		
		return menuItemList;
	}
	
	@Override
	public ModelAndView getCommonModelAndView(HttpServletRequest request, String sysCatCde) {
		Method method = (Method) request.getAttribute("method");
		Functions functions = method.getAnnotation(Functions.class);
		Function function = null;
		
		if (functions != null && functions.value().length != 0) {
			function = functions.value()[0];
		} else {
			function = method.getAnnotation(Function.class);
		}
		
		ModelAndView output = new ModelAndView();
		SystemCatalogDAO systemCatalogDAO = getSystemCatalog(sysCatCde);
		
		//page title
		output.addObject("pageTitle", systemCatalogDAO.getSystemCatalogShortDesc());
		
		//system name
		output.addObject("sysFullName", systemCatalogDAO.getSystemCatalogLongDesc());
		output.addObject("sysShortName", systemCatalogDAO.getSystemCatalogShortDesc());
		
		//menu object
		output.addObject("menuItemList", getMenuItemList(sysCatCde));
		
		//set active menu object
		if (function != null) {
			output.addObject("activeMenuItem", function.functionCode());
		}
		
		//date string
		String dateString = GeneralUtil.getCurrentDateStr("N");
		output.addObject("dateString", dateString);
		
		//get username
		output.addObject("userName", SecurityUtils.getCurrentLogin());
		output.addObject("userAttributes", SecurityUtils.getCurrentLoginAttributes());
		
		if (function != null) {
			//get page info
			FunctionPageDAO thisPage = getFuncPage(function.functionCode(), function.functionSubCode(), 
					function.functionPage());
			output.addObject("thisPage", thisPage);
			
			//get func info
			FunctionDAO func = getFunction(function.functionCode(), " ");
		
			//set breadcrumb
			List<MenuItem> breadcrumbList = new ArrayList<MenuItem>();
			breadcrumbList.add(new MenuItem("", systemCatalogDAO.getSystemCatalogLongDesc(), RestURIConstants.ADMIN_MENU));
			if (func != null && !function.functionSubCode().trim().isEmpty()) {
				breadcrumbList.add(new MenuItem("", func.getFunctionDesc(), "/" + func.getFirstPageName() + "?dummy=" + cacheVersion.getStaticVer()));
			}
			breadcrumbList.add(new MenuItem("", thisPage.getPageTitleTxt(), "/" + thisPage.getFunctionPagePK().getPageName() + "?dummy=" + cacheVersion.getStaticVer()));
			output.addObject("breadcrumbList", breadcrumbList);
			
			//body css class
			output.addObject("bodyCssClass", thisPage.getCssName());
			
			//set view
			output.setViewName("web/" + function.functionPage());
		}
		
		return output;
	}
	
	public Set<ValidationMessage> validateRequestWithJsonSchema(String jsonSchemaKey, CommonJson formattedRequest) {
		log.debug("resolveArgument");

		// get JsonSchema from schemaPath
		JsonSchema schema = getJsonSchema(jsonSchemaKey);
		//File jsonSchemaFile = ResourceUtils.getFile("classpath:json_schemas/test_schema.json");

		// parse json payload
		JsonNode json = objectMapper.valueToTree(formattedRequest.props());

		// Do actual validation
		Set<ValidationMessage> validationResult = schema.validate(json);

		return validationResult;
	}

	private JsonSchema getJsonSchema(String jsonSchemaKey) {
		return schemaCache.computeIfAbsent(jsonSchemaKey, key -> {
			log.debug("key = " + key);
			log.debug("jsonSchemaKey = " + jsonSchemaKey);

			File jsonSchemaMappingFile;
			File jsonSchemaFile;

			try {
				// read mapping file
				jsonSchemaMappingFile = ResourceUtils.getFile("classpath:json_schemas/index.json");
				String jsonSchemaMappingFileString = new String(Files.readAllBytes(jsonSchemaMappingFile.toPath()));
				CommonJson jsonSchemaMapping = GeneralUtil.stringToCommonJsonObject(jsonSchemaMappingFileString);
				String jsonSchemaRelativePath = jsonSchemaMapping.get(jsonSchemaKey);

				// read json schema file
				jsonSchemaFile = ResourceUtils.getFile("classpath:json_schemas/" + jsonSchemaRelativePath);
				if (!jsonSchemaFile.exists()) {
					throw new Error();
				}
			} catch (IOException e1) {
				throw new JsonSchemaLoadingFailedException("Schema file does not exist, key: " + key);
			}


			JsonSchemaFactory schemaFactory = JsonSchemaFactory.getInstance(SpecVersion.VersionFlag.V4);

			try {
				JsonNode schemaNode = objectMapper.readTree(jsonSchemaFile);
				return schemaFactory.getSchema(schemaNode);
			} catch (Exception e) {
				throw new JsonSchemaLoadingFailedException("An error occurred while loading JSON Schema, key: " + key, e);
			}
		});
	}

	//Json schema
    @Override
    public Set<ValidationMessage> validateJson(CommonJson inputJson, String jsonSchemaName) {
    	log.debug("resolveArgument");

        // get JsonSchema from schemaPath
        JsonSchema schema = getJsonSchema(jsonSchemaName);
        //File jsonSchemaFile = ResourceUtils.getFile("classpath:json_schemas/test_schema.json");

        // parse json payload
        JsonNode json = objectMapper.valueToTree(inputJson.props());

        // Do actual validation
        Set<ValidationMessage> validationResult = schema.validate(json);

        return validationResult;
    }
    
    // calling Admin Intranet API - show item in worklist
   	public void callAIWorkListNotiAPI(JSONObject params) throws Exception {
   		try {
   			String request_id = params.getString("request_id");
   			String initiator = params.getString("initiator");
   			String action = params.getString("action");
   			JSONArray receiverArr = params.optJSONArray("receiver");
   			String message = params.optString("message");
   			String url = params.optString("url");
   			
   			log.debug("request_id {} initiator {} action {} receiverArr {} message {} url {}", request_id, initiator, action, receiverArr, message, url);
   			
   			String result = "";
   			String apiUrl = env.getProperty("ai.worklist.link.notify");
   			String returnUrl = env.getProperty("app.service.frontend.home");
   			
   			List<NameValuePair> urlParameters = new ArrayList<>();
   			urlParameters.add(new BasicNameValuePair("system", "Extra Load Activities System"));
   			urlParameters.add(new BasicNameValuePair("request_id",request_id));
   			urlParameters.add(new BasicNameValuePair("action", action));
   			urlParameters.add(new BasicNameValuePair("initiator", initiator));
   			urlParameters.add(new BasicNameValuePair("message", message));
   			urlParameters.add(new BasicNameValuePair("url",url));

   			if (receiverArr != null && receiverArr.length() > 0) {
   	   			for (int i=0; i< receiverArr.length(); i++) {
   	   				urlParameters.add(new BasicNameValuePair("assignee["+i+"]", receiverArr.getString(i)));
   	   			}   				
   			}

   			log.debug("urlParameters {}", urlParameters);
   			HttpPost post = new HttpPost(apiUrl);
   			post.setEntity(new UrlEncodedFormEntity(urlParameters));

   			CloseableHttpClient httpClient = HttpClients.createDefault();

   	        try {
   	        	// try 1
   		        CloseableHttpResponse response = httpClient.execute(post);
   		        result = EntityUtils.toString(response.getEntity());
   	        } catch (Exception e1) {
   		        try {
   		        	// try 2
   			        CloseableHttpResponse response = httpClient.execute(post);
   			        result = EntityUtils.toString(response.getEntity());
   		        } catch (Exception e2) {
   			        try {
   			        	// try 3
   				        CloseableHttpResponse response = httpClient.execute(post);
   				        result = EntityUtils.toString(response.getEntity());
   			        } catch (Exception e3) {
   				        log.error(e3.toString());	// no affect user in FE, just log the error
   			        }
   		        }
   	        }

   	        if (result.indexOf("error") > -1)	// server returns error
   		        log.error("[Notice API] AI server returns error: " + result);
   	        else
   	        	log.info(result);
   		}catch (Exception e) {
   	        log.error("[Notice API] Call AI API error: " + e.toString());	// no affect user in FE, just log the error
           }

   	}

   	public void callAIWorkListApprvAPI(String request_id, String approve, String assignee, String action) {
   		try {
   			String result = "";
   			String apiUrl = env.getProperty("ai.worklist.link.apprv");
   			List<NameValuePair> urlParameters = new ArrayList<>();
   			urlParameters.add(new BasicNameValuePair("system", "Extra Load Activities System"));
   			urlParameters.add(new BasicNameValuePair("request_id",request_id));
   			urlParameters.add(new BasicNameValuePair("action", action));
   			urlParameters.add(new BasicNameValuePair("approve", approve));
   			urlParameters.add(new BasicNameValuePair("assignee", assignee));

   			HttpPost post = new HttpPost(apiUrl);
   			post.setEntity(new UrlEncodedFormEntity(urlParameters));

   			CloseableHttpClient httpClient = HttpClients.createDefault();

   			try {
   				// try 1
   				CloseableHttpResponse response = httpClient.execute(post);
   				result = EntityUtils.toString(response.getEntity());
   			} catch (Exception e1) {
   				try {
   					// try 2
   					CloseableHttpResponse response = httpClient.execute(post);
   					result = EntityUtils.toString(response.getEntity());
   				} catch (Exception e2) {
   					try {
   						// try 3
   						CloseableHttpResponse response = httpClient.execute(post);
   						result = EntityUtils.toString(response.getEntity());
   					} catch (Exception e3) {
   						log.error(e3.toString());	// no affect user in FE, just log the error
   					}
   				}
   			}

   			if (result.indexOf("error") > -1)	// server returns error
   				log.error("[Apprv API] AI server returns error: " + result);
   			else
   				log.info(result);
   		}catch (Exception e) {
   			log.error("[Apprv API] Call AI API error: " + e.toString());	// no affect user in FE, just log the error
   		}

   	}

	public void callAIWorkListCompleteAPI(String request_id, String approve, String status) {
		try {
			String result = "";
			String apiUrl = env.getProperty("ai.worklist.link.complete");
			List<NameValuePair> urlParameters = new ArrayList<>();
			urlParameters.add(new BasicNameValuePair("system", "Extra Load Activities System"));
			urlParameters.add(new BasicNameValuePair("request_id",request_id));
			urlParameters.add(new BasicNameValuePair("approve", approve));
			urlParameters.add(new BasicNameValuePair("status", status));

			HttpPost post = new HttpPost(apiUrl);
			post.setEntity(new UrlEncodedFormEntity(urlParameters));

			CloseableHttpClient httpClient = HttpClients.createDefault();

			try {
				// try 1
				CloseableHttpResponse response = httpClient.execute(post);
				result = EntityUtils.toString(response.getEntity());
			} catch (Exception e1) {
				try {
					// try 2
					CloseableHttpResponse response = httpClient.execute(post);
					result = EntityUtils.toString(response.getEntity());
				} catch (Exception e2) {
					try {
						// try 3
						CloseableHttpResponse response = httpClient.execute(post);
						result = EntityUtils.toString(response.getEntity());
					} catch (Exception e3) {
						log.error(e3.toString());	// no affect user in FE, just log the error
					}
				}
			}

			if (result.indexOf("error") > -1)	// server returns error
				log.error("[Complete API] AI server returns error: " + result);
			else
				log.info(result);
		}catch (Exception e) {
			log.error("[Complete API] Call AI API error: " + e.toString());	// no affect user in FE, just log the error
		}

	}

}
