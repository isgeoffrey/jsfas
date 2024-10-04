package jsfas.web.controller;

import java.io.IOException;
import java.lang.reflect.Method;
import java.util.Enumeration;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletRequest;

import org.apache.catalina.connector.ClientAbortException;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.apache.shiro.authz.AuthorizationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.PessimisticLockingFailureException;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.Errors;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.validation.SmartValidator;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.multipart.MaxUploadSizeExceededException;

import com.networknt.schema.ValidationMessage;

import jsfas.common.constants.AppConstants;
import jsfas.common.constants.ResponseCodeConstants;
import jsfas.common.constants.ResponseMessageConstants;
import jsfas.common.exception.InvalidParameterException;
import jsfas.common.exception.JsonValidationFailedException;
import jsfas.common.exception.NotAuthorizedForFunctionException;
import jsfas.common.exception.RecordExistException;
import jsfas.common.exception.RecordModifiedException;
import jsfas.common.exception.RecordNotExistException;
import jsfas.common.exception.RecordRemovedException;
import jsfas.common.json.CommonJson;
import jsfas.common.json.CommonResponseJson;
import jsfas.common.json.Response;
import jsfas.common.json.ResponseJson;
import jsfas.common.utils.GeneralUtil;
import jsfas.db.main.persistence.domain.FunctionDAO;
import jsfas.db.main.persistence.service.CommonRoutineService;
import jsfas.security.SecurityUtils;

public class CommonApiController {

	@Autowired
	private CommonRoutineService commonRoutineService;
	
	@Autowired
	private SmartValidator smartValidator;

	private final Logger log = LoggerFactory.getLogger(this.getClass());
	protected String recordDesc = "Record";
	
	//Common Functions
	//----------------------------
	protected void validate(Object validatorObj, Errors result) throws InvalidParameterException {
		smartValidator.validate(validatorObj, result);
		if (result.hasErrors()) {
			List<ObjectError> objectErrorList = result.getAllErrors();
			
			for (ObjectError objectError: objectErrorList) {
				FieldError fieldError = (FieldError) objectError;
				log.debug("Parameter Error: {} {}", fieldError.getField(), fieldError.getDefaultMessage());
			}
			
			if (objectErrorList.size() > 0) {
				FieldError fieldError = (FieldError) objectErrorList.get(0);
				throw new InvalidParameterException("Invalid parameter: " + fieldError.getField() + " " + fieldError.getDefaultMessage());
			}
			
			throw new InvalidParameterException();
		}
	}
	
	/*
	 * This function validate HTTP request parameter, query and body through json schema
	 */
	protected void validateRequestWithJsonSchema(HttpServletRequest request) throws JsonValidationFailedException {
		validateRequestWithJsonSchema(request, null);
	}

	/*
	 * This function validate HTTP request parameter, query and body through json schema
	 */
	protected void validateRequestWithJsonSchema(HttpServletRequest request, CommonJson requestBody) throws JsonValidationFailedException {
		log.debug("validateRequestWithJsonSchema - " + request.getServletPath());
		String jsonSchemaKey = getJsonSchemaKey(request);
		CommonJson requestQuery = getRequestQuery(request);
		CommonJson formattedRequestJson = getFormmatedCommonJsonForValidation(requestQuery, requestBody);
		Set<ValidationMessage> validationResult = commonRoutineService.validateRequestWithJsonSchema(jsonSchemaKey, formattedRequestJson);
		if(!validationResult.isEmpty())
			log.info(validationResult.toString());
		if (!validationResult.isEmpty()) {
			throw new JsonValidationFailedException(validationResult);
		}
	}

	/*
	 * @param: (HttpServletRequest) HttpServletRequest object
	 * @return: (String) return a string with Method + Path. (e.g. POST /test/json)
	 */
	protected String getJsonSchemaKey(HttpServletRequest request) {
		return request.getMethod() + " " + request.getServletPath();
	}

	/*
	 * @param: (HttpServletRequest) HttpServletRequest object
	 * @return: (CommonJson) Query string to CommonJson Object
	 */
	protected CommonJson getRequestQuery(HttpServletRequest request) {
		Enumeration<String> pNames = request.getParameterNames();
		if (pNames == null) {
			return null;
		}
		CommonJson json = new CommonJson();
		while(pNames.hasMoreElements()){
			String name=(String)pNames.nextElement();
			String value=request.getParameter(name);
			json.set(name, value);
		}
		return json;
	}

	/*
	 * @param: (CommonJson) RquestQuery
	 * @param: (CommonJson) Request Body
	 * @return: (CommonJson) a formatted json schema object for validation
	 */
	protected CommonJson getFormmatedCommonJsonForValidation(CommonJson requestQuery, CommonJson requestBody) {
		CommonJson json = new CommonJson();
		if (requestQuery != null) {
			json.set("query", requestQuery);
		}
		if (requestBody != null) {
			json.set("body", requestBody);
		}
		return json;
	}

	protected void validateJson(CommonJson validatorObj, HttpServletRequest request) throws JsonValidationFailedException {
		log.debug("validateJson (default schema)");
		validateJson(validatorObj, getOpPageName(request));
	}

	protected void validateJson(CommonJson validatorObj, String schema) throws JsonValidationFailedException {
		log.debug("validateJson with {}", schema);

		Set<ValidationMessage> validationResult =  commonRoutineService.validateJson(validatorObj, schema);

		if (!validationResult.isEmpty()) {
			throw new JsonValidationFailedException(validationResult);
		}
	}
	
	protected String getOpPageName(HttpServletRequest request) {
		Method method = (Method) request.getAttribute("method");
		RequestMapping requestMapping = method.getAnnotation(RequestMapping.class);
		return requestMapping.value()[0].split("/")[1];
	}
	

	protected Response setSuccess(Response response) {
		response.setMeta(ResponseCodeConstants.SUCCESS, ResponseMessageConstants.SUCCESS);
		return response;
	}
	
	
	//Exception Handling Functions
	//----------------------------
	@ExceptionHandler(JsonValidationFailedException.class)
	public Response onJsonValidationFailedException(JsonValidationFailedException ex) {
		Response response = new Response();
		response.setMeta(ResponseCodeConstants.ERROR, "Json validation failed");

		List<String> messages = ex.getValidationMessages().stream()
				.map(ValidationMessage::getMessage)
				.collect(Collectors.toList());

		response.setData(new CommonJson().set("messages", messages));

		return response;
	}
	
	@ExceptionHandler(RecordNotExistException.class)
	public Response recordNotExistException(HttpServletRequest request, RecordNotExistException e) {
		Response response = new Response();
		//response.setMeta(ResponseCodeConstants.ERROR, commonRoutineService.getMessage("TTE0006", recordDesc));
		response.setMeta(ResponseCodeConstants.ERROR, e.getMessage());

		if(Optional.ofNullable(request).isPresent()) {
			log.debug("{} encountered record not exist error for url = {}", SecurityUtils.getCurrentLogin(), request.getRequestURL());
		} else {
			log.debug("{} encountered record not exist error", SecurityUtils.getCurrentLogin());
		}

		log.debug("handleError URL = " + request.getRequestURL());
		log.debug("handleError MSG = " + Thread.currentThread().getStackTrace()[1].getMethodName()+"@"+this.getClass().getSimpleName(), e);
		return response;
	}
	
	@ExceptionHandler(RecordModifiedException.class)
	public Response recordModifiedError(HttpServletRequest request, RecordModifiedException e) {
		Response response = new Response();
		//response.setMeta(ResponseCodeConstants.ERROR, commonRoutineService.getMessage("TTE0005", recordDesc));
		response.setMeta(ResponseCodeConstants.CONFLICT, e.getMessage());

		if(Optional.ofNullable(request).isPresent()) {
			log.debug("{} encountered record modified error for url = {}", SecurityUtils.getCurrentLogin(), request.getRequestURL());
		} else {
			log.debug("{} encountered record modified error", SecurityUtils.getCurrentLogin());
		}

		log.debug("handleError URL = " + request.getRequestURL());
		log.debug("handleError MSG = " + Thread.currentThread().getStackTrace()[1].getMethodName()+"@"+this.getClass().getSimpleName(), e);
		return response;
	}
	
	@ExceptionHandler(RecordRemovedException.class)
	public Response recordRemovedError(HttpServletRequest request, RecordRemovedException e) {
		Response response = new Response();
		//response.setMeta(ResponseCodeConstants.ERROR, commonRoutineService.getMessage("TTE0006", recordDesc));
		response.setMeta(ResponseCodeConstants.ERROR, e.getMessage());

		if(Optional.ofNullable(request).isPresent()) {
			log.debug("{} encountered record removed error for url = {}", SecurityUtils.getCurrentLogin(), request.getRequestURL());
		} else {
			log.debug("{} encountered record removed error", SecurityUtils.getCurrentLogin());
		}

		log.debug("handleError URL = " + request.getRequestURL());
		log.debug("handleError MSG = " + Thread.currentThread().getStackTrace()[1].getMethodName()+"@"+this.getClass().getSimpleName(), e);
		return response;
	}
	
	@ExceptionHandler({RecordExistException.class, DataIntegrityViolationException.class})
	public Response recordExistError(HttpServletRequest request, Exception e) {
		Response response = new Response();
		//response.setMeta(ResponseCodeConstants.ERROR, commonRoutineService.getMessage("TTE0003", recordDesc));
		response.setMeta(ResponseCodeConstants.ERROR, e.getMessage());

		if(Optional.ofNullable(request).isPresent()) {
			log.debug("{} encountered record exist error for url = {}", SecurityUtils.getCurrentLogin(), request.getRequestURL());
		} else {
			log.debug("{} encountered record exist error", SecurityUtils.getCurrentLogin());
		}

		log.debug("handleError URL = " + request.getRequestURL());
		log.debug("handleError MSG = " + Thread.currentThread().getStackTrace()[1].getMethodName()+"@"+this.getClass().getSimpleName(), e);
		return response;
	}
	
	@ExceptionHandler(PessimisticLockingFailureException.class)
	public Response recordLockedError(HttpServletRequest request, PessimisticLockingFailureException e) {
		Response response = new Response();
		response.setMeta(ResponseCodeConstants.ERROR, e.getMessage());
		
        if(Optional.ofNullable(request).isPresent()) {
            log.debug("{} encountered record locked error for url = {}", SecurityUtils.getCurrentLogin(), request.getRequestURL());
        } else {
            log.debug("{} encountered record locked error", SecurityUtils.getCurrentLogin());
        }
        
        log.debug("handleError URL = " + request.getRequestURL());
        log.debug("handleError MSG = " + Thread.currentThread().getStackTrace()[1].getMethodName()+"@"+this.getClass().getSimpleName(), e);
        
        return response;
    }
	
	@ExceptionHandler(InvalidParameterException.class)
	public Response invalidParameterError(HttpServletRequest request, InvalidParameterException e) {
		Response response = new Response();
		response.setMeta(ResponseCodeConstants.ERROR, ResponseMessageConstants.INVALID_PARAMETER);

		if (!ResponseMessageConstants.INVALID_PARAMETER.equals(e.getMessage())) {
			response.setMessage(e.getMessage());
		}

		if (Optional.ofNullable(request).isPresent()) {
			log.debug("{} encountered invalid parameter error for url = {}", SecurityUtils.getCurrentLogin(), request.getRequestURL());
		} else {
			log.debug("{} encountered invalid parameter error", SecurityUtils.getCurrentLogin());
		}

		log.debug("handleError URL = " + request.getRequestURL());
		log.debug("handleError MSG = " + Thread.currentThread().getStackTrace()[1].getMethodName()+"@"+this.getClass().getSimpleName(), e);
		return response;
	}
	
	@ExceptionHandler(HttpMessageNotReadableException.class)
	public Response missingRequestBody(HttpServletRequest request, HttpMessageNotReadableException e) {
		Response response = new Response();
		response.setMeta(ResponseCodeConstants.ERROR, ResponseMessageConstants.INVALID_PARAMETER);

		if (e.getMessage().contains("Required request body is missing")) {
			log.debug(e.getMessage());
			response.setMessage("Missing request body");
		} else {
			throw e;
		}

		if (Optional.ofNullable(request).isPresent()) {
			log.debug("{} encountered invalid parameter error for url = {}", SecurityUtils.getCurrentLogin(), request.getRequestURL());
		} else {
			log.debug("{} encountered invalid parameter error", SecurityUtils.getCurrentLogin());
		}

		log.debug("handleError URL = " + request.getRequestURL());
		log.debug("handleError MSG = " + Thread.currentThread().getStackTrace()[1].getMethodName()+"@"+this.getClass().getSimpleName(), e);
		return response;
	}
	
	@ExceptionHandler({NotAuthorizedForFunctionException.class, AuthorizationException.class})
	public Response notAuthorizedError(HttpServletRequest request, Exception e) {
		Response response = new Response();
		response.setCode(ResponseCodeConstants.UNAUTHORIZED);

		if(Optional.ofNullable(request).isPresent()) {
			log.debug("{} encountered unauthorized error for url = {}", SecurityUtils.getCurrentLogin(), request.getRequestURL());
		} else {
			log.debug("{} encountered unauthorized error", SecurityUtils.getCurrentLogin());
		}

		response.setMessage("Unauthorized for function");
		
		if (e instanceof AuthorizationException) {
			response.setMessage(e.getMessage());
		}

		log.debug("handleError MSG = " + Thread.currentThread().getStackTrace()[1].getMethodName()+"@"+this.getClass().getSimpleName(), e);

		return response;
	}
	
	@ExceptionHandler(MaxUploadSizeExceededException.class)
	public Response maxUploadSizeExceeded(HttpServletRequest request, MaxUploadSizeExceededException e) {
		Response response = new Response();
		response.setMeta(ResponseCodeConstants.ERROR, ResponseMessageConstants.MAX_UPLOAD_SIZE_EXCEEDED);

		if (Optional.ofNullable(request).isPresent()) {
			log.debug("{} encountered invalid parameter error for url = {}", SecurityUtils.getCurrentLogin(), request.getRequestURL());
		} else {
			log.debug("{} encountered invalid parameter error", SecurityUtils.getCurrentLogin());
		}

		log.debug("handleError URL = " + request.getRequestURL());
		log.debug("handleError MSG = " + Thread.currentThread().getStackTrace()[1].getMethodName()+"@"+this.getClass().getSimpleName(), e);
		return response;
	}
	
	//handle broken pipe or connection abort exception
	@ExceptionHandler(IOException.class)
	public Response brokenPipeOrClientAbortError(HttpServletRequest request, IOException e) throws Exception {
		log.debug("handleError URL = " + request.getRequestURL());
		log.debug("handleError MSG = " + Thread.currentThread().getStackTrace()[1].getMethodName()+"@"+this.getClass().getSimpleName(), e);

		if (StringUtils.containsIgnoreCase(ExceptionUtils.getMessage(e), "Broken pipe") ||
				e.getClass().getName().equals(ClientAbortException.class.getName())) {
			return null;
		}

		return unexpectedError(request, e);
	}
	
	@ExceptionHandler(Exception.class)
	public Response unexpectedError(HttpServletRequest request, Exception e) throws Exception {
		Response response = new Response();
		response.setMeta(ResponseCodeConstants.ERROR, "Unexpected Error!");

		if(Optional.ofNullable(request).isPresent()) {
			log.error("{} encountered error for url = {}", SecurityUtils.getCurrentLogin(), request.getRequestURL());
		} else {
			log.error("{} encountered error", SecurityUtils.getCurrentLogin());
		}

		log.error("handleError URL = " + request.getRequestURL());
		log.error("handleError MSG = " + Thread.currentThread().getStackTrace()[1].getMethodName()+"@"+this.getClass().getSimpleName(), e);

		//e.printStackTrace();
		return response;
	}
}
