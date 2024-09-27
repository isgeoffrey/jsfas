package jsfas.web.controller;

import java.io.Console;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.validation.constraints.NotEmpty;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import jsfas.common.constants.AppConstants;
import jsfas.common.constants.ResponseCodeConstants;
import jsfas.common.constants.ResponseMessageConstants;
import jsfas.common.constants.RestURIConstants;
import jsfas.common.exception.NotAuthorizedForFunctionException;
import jsfas.common.json.CommonJson;
import jsfas.common.json.Response;
import jsfas.common.utils.GeneralUtil;
import jsfas.db.main.persistence.domain.FunctionCatalogDAO;
import jsfas.db.main.persistence.domain.FunctionDAO;
import jsfas.db.main.persistence.service.AuthEventHandler;
import jsfas.db.main.persistence.service.AuthService;
import jsfas.db.main.persistence.service.CommonRoutineService;

@RestController
@ControllerAdvice
public class AuthController extends CommonApiController {
	
	@Autowired
	private Environment env;
	
	@Autowired
	private CommonRoutineService commonRoutineService;
	
	@Autowired
	private AuthService authService;

    private final Logger log = LoggerFactory.getLogger(this.getClass());
    
	@RequestMapping(value = RestURIConstants.CAS_URL, method = {RequestMethod.GET})
	public Response casUrl(HttpServletRequest request, @RequestParam(defaultValue="") String appPath, @RequestParam(defaultValue="/") String path) {
		
		//validation
		validateRequestWithJsonSchema(request);
		
		Response response = new Response();
		
		CommonJson data = new CommonJson().set("url", authService.getCasUrl(appPath, path));
		response.setData(data);
		
		return setSuccess(response);
	}
	
	@RequestMapping(value = RestURIConstants.CAS_AUTH, method = {RequestMethod.POST})
	public Response casAuth(HttpServletRequest request, @RequestBody CommonJson inputJson, 
			BindingResult result) throws Exception {
		
		//validation
		validateRequestWithJsonSchema(request, inputJson);
		
		Response response = new Response();
		
		CommonJson data = authService.casAuth(GeneralUtil.initNullString(inputJson.get("appPath")), inputJson.get("ticket"), inputJson.get("path"));
		
		if (data != null && data.props().containsKey("token")) {
			response.setData(data);
			return setSuccess(response);
		} else {
			response.setMeta(ResponseCodeConstants.ERROR, "CAS error!");
			return response;
		}
		
	}
	
	@CrossOrigin
	@RequestMapping(value = RestURIConstants.LOGIN, method = {RequestMethod.POST})
	public Response login(HttpServletRequest request, @RequestBody CommonJson inputJson, 
			BindingResult result) throws Exception {
		if (env.getActiveProfiles().length > 0 && env.getActiveProfiles()[0].equalsIgnoreCase("production")) {
			throw new NotAuthorizedForFunctionException();
        }
		
		//validation
		validateRequestWithJsonSchema(request, inputJson);
		
		Response response = new Response();
		
		CommonJson data = authService.localLogin(inputJson.getFilterValue("email").trim());
		
		response.setData(data);
		
		return setSuccess(response);
	}
	
	@RequestMapping(value = RestURIConstants.CAS_LOGOUT, method = {RequestMethod.GET, RequestMethod.POST})
	public Response logout(HttpServletRequest request) throws Exception {
		
		//validation
		validateRequestWithJsonSchema(request);
		
		Response response = new Response();
		
		String returnMsg = authService.logout();
		
		CommonJson data = new CommonJson().set("logout_url", env.getProperty("cas.service.logout"));
		response.setData(data);
		
		response.setMeta(ResponseCodeConstants.SUCCESS, returnMsg);
		return response;
	}

	@RequestMapping(value = RestURIConstants.GET_FUNC_MENU, method = {RequestMethod.GET})
	public Response AuthPathName(HttpServletRequest request) {

		Response response = new Response();
		
		String sysCatgCde = AppConstants.SYS_CATG_CDE;
		
		List<FunctionCatalogDAO> funcCatalogList = commonRoutineService.getFuncCatalog(sysCatgCde);
		List<String> funcCatalogName = new ArrayList();
		HashMap<String, String> functionCodeMap = new HashMap<String,String>();
		for(FunctionCatalogDAO funcCatalog : funcCatalogList) {
			String funcCatgCde = funcCatalog.getFunctionCatalogPK().getFunctionCatalogCode();
			List<FunctionDAO> funcMenuList = commonRoutineService.getAllFuncMenu(sysCatgCde, funcCatgCde);
			for(FunctionDAO funcMenu : funcMenuList) {
				funcCatalogName.add(funcMenu.getFunctionPK().getFunctionCode());
				functionCodeMap.put(funcMenu.getFunctionPK().getFunctionCode(), funcMenu.getFirstPageName());
			}
		}	
		List<String> authPageName = new ArrayList();
		for(String functionCode : functionCodeMap.keySet()) {
			String functionPage = functionCodeMap.get(functionCode);

			if(commonRoutineService.isAuthorized(functionCode, " ", functionPage)) {
				authPageName.add(functionPage);
			}
			
		}
		
		CommonJson data = new CommonJson()
				.set("authPageName", authPageName);
		response.setData(data);
		
		return setSuccess(response);
	}
}
