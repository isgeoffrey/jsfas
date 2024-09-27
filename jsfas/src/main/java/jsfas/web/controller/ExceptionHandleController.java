package jsfas.web.controller;

import java.io.IOException;
import java.util.Optional;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.ModelAndView;

import jsfas.common.exception.InvalidParameterException;
import jsfas.common.exception.NotAuthorizedForFunctionException;
import jsfas.common.utils.GeneralUtil;
import jsfas.db.main.persistence.domain.FunctionDAO;
import jsfas.db.main.persistence.service.CommonRoutineService;
import jsfas.security.SecurityUtils;

import org.apache.catalina.connector.ClientAbortException;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.apache.shiro.authz.AuthorizationException;

@Order(0)
@ControllerAdvice
public class ExceptionHandleController {

	@Autowired
	private CommonRoutineService commonRoutineService;

	private final Logger log = LoggerFactory.getLogger(this.getClass());
	
	@ExceptionHandler({NotAuthorizedForFunctionException.class, AuthorizationException.class})
	public ModelAndView notAuthorizedError(HttpServletRequest request, Exception ex) {
	    ModelAndView output = new ModelAndView("web/error");
	    
	    if(Optional.ofNullable(request).isPresent()) {
	        log.error("{} encountered unauthorized error for url = {}", SecurityUtils.getCurrentLogin(), request.getRequestURL());
        } else {
            log.error("{} encountered unauthorized error", SecurityUtils.getCurrentLogin());
        }
	    
	    if(ex instanceof NotAuthorizedForFunctionException) {
	        NotAuthorizedForFunctionException notAuthzEx = NotAuthorizedForFunctionException.class.cast(ex);

	        log.error("Function Code = {}", notAuthzEx.getFunctionCode());
	        log.error("Function Sub Code = {}", notAuthzEx.getFunctionSubCode());
	        
	        FunctionDAO functionDAO = commonRoutineService.getFunction(
	                GeneralUtil.initBlankString(notAuthzEx.getFunctionCode()), GeneralUtil.initBlankString(notAuthzEx.getFunctionSubCode()));
	        
	        String pageTitle = functionDAO.getSystemCatalog().getSystemCatalogShortDesc();
	        String errorTitle = "Access Denied";
	        
	        String errorMsg = commonRoutineService.getMessageForHtml("TTE0012", functionDAO.getFunctionDesc());
	        
	        output.addObject("pageTitle", pageTitle);
	        output.addObject("errorTitle", errorTitle);
	        output.addObject("errorMsg", errorMsg);
	    } else if(ex instanceof AuthorizationException) {
	        AuthorizationException authzEx = AuthorizationException.class.cast(ex);
	        String authzExMsg = authzEx.getMessage();
	        int index = authzExMsg.indexOf("[");
	        if(index != -1) {
	            authzExMsg = authzExMsg.substring(index);
	        }
	        log.error(authzExMsg);
	        String errorMsg = commonRoutineService.getMessageForHtml("TTE0013", authzExMsg);
	        output.addObject("pageTitle", "Access Denied");
	        output.addObject("errorMsg", errorMsg);
	    }
		
		return output;
	}
	
	@ExceptionHandler(HttpRequestMethodNotSupportedException.class)
	public ModelAndView httpRequestMethodNotSupported(HttpServletRequest request, Exception e) {
		log.debug("handleError URL = " + request.getRequestURL());
		log.debug("handleError MSG = " + Thread.currentThread().getStackTrace()[1].getMethodName()+"@"+this.getClass().getSimpleName(), e);
		
		ModelAndView output = new ModelAndView("web/error");
		
		String errorTitle = "Invalid Access!";
		String errorMsg = "Please access the page by valid entry point.";
		
		output.addObject("errorTitle", errorTitle);
		output.addObject("errorMsg", errorMsg);
		return output;
	}
	
	//handle broken pipe or connection abort exception
	@ExceptionHandler(IOException.class)
	public Object brokenPipeOrConnectionAbortError(HttpServletRequest request, IOException e) throws IOException {
		log.debug("handleError URL = " + request.getRequestURL());
		log.debug("handleError MSG = " + Thread.currentThread().getStackTrace()[1].getMethodName()+"@"+this.getClass().getSimpleName(), e);
		
		if (StringUtils.containsIgnoreCase(ExceptionUtils.getMessage(e), "Broken pipe") || 
				e.getClass().getName().equals(ClientAbortException.class.getName())) {
	        return null;   
	    }
		
		throw e;
	}
	
	@ExceptionHandler(MissingServletRequestParameterException.class)
	public ModelAndView missingParameterError(HttpServletRequest request, MissingServletRequestParameterException e) 
			throws MissingServletRequestParameterException {
		if (request.getMethod().equals("GET")) {
			log.debug("handleError URL = " + request.getRequestURL());
			log.debug("handleError MSG = " + Thread.currentThread().getStackTrace()[1].getMethodName()+"@"+this.getClass().getSimpleName(), e);
			
			String errorMsg = "Invalid URL or parameters. Please try to access the page by valid entry point.";
			ModelAndView output = new ModelAndView("web/error");
			output.addObject("errorMsg", errorMsg);
			return output;
		}
		
		throw e;
	}
	
	@ExceptionHandler(InvalidParameterException.class)
	public ModelAndView invalidParameterError(HttpServletRequest request, InvalidParameterException e) {
		log.debug("handleError URL = " + request.getRequestURL());
		log.debug("handleError MSG = " + Thread.currentThread().getStackTrace()[1].getMethodName()+"@"+this.getClass().getSimpleName(), e);
		
		String errorMsg = "Invalid URL or parameters.";
		ModelAndView output = new ModelAndView("web/error");
		output.addObject("errorMsg", errorMsg);
		return output;
	}
	
	@ExceptionHandler(Exception.class)
	public Object handleError(HttpServletRequest request, Exception e) throws Exception {
	    if(Optional.ofNullable(request).isPresent()) {
	        log.error("{} encountered error for url = {}", SecurityUtils.getCurrentLogin(), request.getRequestURL());
	    } else {
	        log.error("{} encountered error", SecurityUtils.getCurrentLogin()); 
	    }
	    
		log.error("handleError URL = " + request.getRequestURL());
		log.error("handleError MSG = " + Thread.currentThread().getStackTrace()[1].getMethodName()+"@"+this.getClass().getSimpleName(), e);

		String errorMsg = commonRoutineService.getMessagePlaceholderForHtml("Unexpected error. Please inform ISO.");
		ModelAndView output = new ModelAndView("web/error");
		output.addObject("errorMsg", errorMsg);
		return output;
	}

}
