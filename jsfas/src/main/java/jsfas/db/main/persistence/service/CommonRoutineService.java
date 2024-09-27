package jsfas.db.main.persistence.service;

import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;

import org.json.JSONObject;
import org.springframework.web.servlet.ModelAndView;

import com.networknt.schema.ValidationMessage;

import jsfas.common.annotation.Function;
import jsfas.common.json.CommonJson;
import jsfas.common.json.ResponseJson;
import jsfas.db.main.persistence.domain.FunctionCatalogDAO;
import jsfas.db.main.persistence.domain.FunctionDAO;
import jsfas.db.main.persistence.domain.FunctionPageDAO;
import jsfas.db.main.persistence.domain.SystemCatalogDAO;
import jsfas.db.main.persistence.domain.UserProfileDetailDAO;

/**
 * @author iseric
 * @since 6/5/2016
 */
public interface CommonRoutineService {

	
	/**
	 * check if the login user is approved to access page
	 * @param page
	 * @return true / false
	 */
	public boolean isUserApprovedForFuncPage(String pageName);
	
	/**
	 * check if the login user is authorized to access the page with functionCode and functionSubCode
	 * @param functionCode
	 * @param functionSubCode
	 * @param functionPage
	 * @return true / false
	 */
	public boolean isAuthorized(String functionCode, String functionSubCode, String functionPage);
	
	/**
	 * check if the login user is authorized to access the page with functionCode and functionSubCode
	 * @param functions
	 * @return true / false
	 */
	public boolean isAuthorized(Function[] functions);
	
	/**
	 * @param pageName
	 * @return funcPageTitle
	 */
	public String getFuncPageTitleByFuncPage(String pageName);
	
	/**
	 * @param pageName
	 * @return funcDesc
	 */
	public String getFuncDescByFuncPage(String pageName);
	
	/**
	 * @param funcCde
	 * @param funcSubCde
	 * @param pageName
	 * @return function page
	 */
	public FunctionPageDAO getFuncPage(String funcCde, String funcSubCde, String pageName);
		
	/**
	 * @param pageName
	 * @return funcCode + functionSubCode(result.get(0) = funcCode, result.get(1) = funcSubCode)
	 */
	public List<String> getFuncCodesByFuncPage(String pageName);
	
	
	/**
	 * @param pageName
	 * @return systemCatalogDesc
	 */
	public String getSystemCatalogDescByFuncPage(String pageName);
	
	
	/**
	 * @return all system catalogs with SYS_CATG_MENU_GEN_IND = Y
	 */
	public List<SystemCatalogDAO> getMenuGenSysCatalog();
	
	
	public List<FunctionDAO> getMenuGenFuncMenuByUser(String sysCatgCode);

	
	/**
	 * @param sysCatgCode
	 * @return all function catalogs by sysCatgCode
	 */
	public List<FunctionCatalogDAO> getFuncCatalog(String sysCatgCode);
	
	/**
	 * @param sysCatCde
	 * @return system catalog detail
	 */
	public SystemCatalogDAO getSystemCatalog(String sysCatCde);

	/**
	 * @param sysCatgCode
	 * @param funcCatgCode
	 * @return all functions by SysCatfCode and funcCatgCode and login user
	 */
	public List<FunctionDAO> getFuncMenu(String sysCatgCode, String funcCatgCode);
	
	
	/**
	 * @param sysCatgCode
	 * @param funcCatgCode
	 * @return all functions by SysCatfCode and funcCatgCode
	 */
	public List<FunctionDAO> getAllFuncMenu(String sysCatgCode, String funcCatgCode);
	
	/**
	 * @param sysCatgCode
	 * @param funcCatgCode
	 * @return function catalog by SysCatfCode and funcCatgCode
	 */
	public FunctionCatalogDAO getFuncCatalog(String sysCatgCode, String funcCatgCode);
	
	/**
	 * @param sysCatgCode
	 * @return functions with MENU_GEN_IND = Y for generating menu
	 */
	public List<FunctionDAO> getFuncForMenu(String sysCatgCode);
	
	/**
	 * @param sysCatgCode
	 * @return function catalog map (sysCatCde + funcCatCde : GpFunctionCatalogDAO)
	 */
	public Map<String, FunctionCatalogDAO> getFuncCatalogMap(String sysCatgCode);
	
	/**
	 * @param funcCode
	 * @param FuncSubCode
	 * @return Function
	 */
	public FunctionDAO getFunction(String funcCode, String funcSubCode);
	
	/**
	 * @param userName
	 * @return user detail map (funcCde + funcSubCde : GpUserProfileDetailDAO)
	 */
	public Map<String, UserProfileDetailDAO> getUserDetailForMenuMap(String userName);

	/**
	 * @param code
	 * @param var1, var2, var3...
	 * @return Message Text
	 */
	public String getMessage(String code, String... vars);
	
	/**
	 * @param code
	 * @param vars (var1, var2, var3, ...)
	 * @return Message Text with code, for HTML display
	 */
	public String getMessageForHtml(String code, String... vars);
	
	/**
     * @param message
     * @param vars (var1, var2, var3, ...)
     * @return Message Placeholder Text
     */
    public String getMessagePlaceholder(String message, String... vars);
	
    /**
     * @param message
     * @param vars (var1, var2, var3, ...)
     * @return Message Placeholder Text with code, for HTML display
     */
    public String getMessagePlaceholderForHtml(String message, String... vars);
    
	/**
	 * @param pageUri
	 * @return Not Auth View
	 */
	public ModelAndView getNotAuthView(String pageUri);
	
	/**
	 * @param pageUri
	 * @return Not Auth ResponseJson
	 */
	public ResponseJson getNotAuthResponse(String pageUri);
	
	/**
	 * @param emailFrom
	 * @param emailFromName
	 * @param emailTo
	 * @param emailCc
	 * @param emailSubject
	 * @param emailBody
	 * @param isGenFooter
	 * @return sendEmail successfully with status 200
	 */
	public ResponseJson sendEmail(String emailFrom, String emailFromName,
			String emailTo, String emailCc, String emailSubject,
			String emailBody, boolean isGenFooter);
	
	/**
	 * Send email function using template engine
	 * 
	 * @param from
	 * @param fromName
	 * @param to
	 * @param cc (optional)
	 * @param bcc (optional)
	 * @param subject
	 * @param templateFile
	 * @param params (map)
	 * @return void
	 * @throws Exception if mail fail to send
	 */
	@SuppressWarnings("rawtypes")
    public void sendEmail(final String from, final String fromName, final String to, final String[] cc, final String[] bcc,
			final String subject, final String templateFile, final Map params) throws Exception;
	
	public void setSecurityInfoPackage();
	
	public ModelAndView getCommonModelAndView(HttpServletRequest request, String sysCatCde);
	
	public Set<ValidationMessage> validateJson(CommonJson inputJson, String jsonSchemaName);

	public Set<ValidationMessage> validateRequestWithJsonSchema(String jsonSchemaKey, CommonJson formattedRequest);
	
	public void callAIWorkListNotiAPI(JSONObject params) throws Exception ;
	public void callAIWorkListApprvAPI(String request_id, String approve, String receiverArr, String action);
	public void callAIWorkListCompleteAPI(String request_id, String approve, String status);
}
