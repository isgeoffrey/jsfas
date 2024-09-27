package jsfas.web.controller;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.servlet.error.ErrorAttributes;
import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.http.HttpStatus;
import org.springframework.web.context.request.WebRequest;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.servlet.ModelAndView;

import jsfas.common.json.CommonJson;
import jsfas.common.json.Meta;
import jsfas.common.json.Response;
import jsfas.db.main.persistence.service.CommonRoutineService;

@RestController
public class WhiteLabelErrorController implements ErrorController {

	@Autowired
	CommonRoutineService commonRoutineService;
	
	private static final Logger log = LoggerFactory.getLogger(WhiteLabelErrorController.class);
	
	private static final String PATH = "/error";

	private final ErrorAttributes errorAttributes;

	@Autowired
	public WhiteLabelErrorController(ErrorAttributes errorAttributes) {
		Assert.notNull(errorAttributes, "ErrorAttributes must not be null");
		this.errorAttributes = errorAttributes;
	}
	
//	@RequestMapping(value = PATH)
//	public ModelAndView error(HttpServletRequest req, WebRequest webRequest, Exception ex) {
//		log.error("whitelabel error = " + getErrorAttributes(req, webRequest, getTraceParameter(req)));
//		
//		String errorMsg = commonRoutineService.getMessagePlaceholderForHtml("Unexpected error. Please inform ISO.");
//		ModelAndView output = new ModelAndView("web/error");
//		output.addObject("errorMsg", errorMsg);
//		return output;
//	}
	
	@RequestMapping(value = PATH)
	public Response error(HttpServletRequest req, HttpServletResponse res, WebRequest webRequest, Exception ex) {
		Map<String, Object> errorAttributes = getErrorAttributes(req, webRequest, getTraceParameter(req));
		int status = (Integer) errorAttributes.get("status");
		Response response = new Response();
		
		// return 200 regardless
		res.setStatus(HttpServletResponse.SC_OK);
		
		// If HTTP status code is 401, it means token is not valid
		if (status == 401) {
			response.setMeta(new Meta(4032, "Unauthorized. Invalid token."));
			return response;
		}
		
	    response.setCode(status);
	    response.setMessage((String)errorAttributes.get("error"));
	    CommonJson data = new CommonJson();
	    data.props().putAll(errorAttributes);
	    response.setData(data);
	    
	    if (status != HttpStatus.FORBIDDEN.value() || status != HttpStatus.NOT_FOUND.value()) {
	    	log.error("whitelabel error = " + errorAttributes);
	    }
	    
	    //force HTTP 401 return to 200
	    if (res.getStatus() == HttpServletResponse.SC_UNAUTHORIZED) {
		    res.setStatus(HttpServletResponse.SC_OK);
	    }
		
		return response;
	}

    
    @Override
    public String getErrorPath() {
        return PATH;
    }
	
	private boolean getTraceParameter(HttpServletRequest request) {
		String parameter = request.getParameter("trace");
		if (parameter == null) {
			return false;
		}
		return !"false".equals(parameter.toLowerCase());
	}

	private Map<String, Object> getErrorAttributes(HttpServletRequest aRequest, WebRequest webRequest,
			boolean includeStackTrace) {
		RequestAttributes requestAttributes = new ServletRequestAttributes(aRequest);
		return errorAttributes.getErrorAttributes(webRequest, includeStackTrace);
	}
}
