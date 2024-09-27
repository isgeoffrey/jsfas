package jsfas.web.controller;

import javax.servlet.http.HttpServletRequest;

import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import jsfas.common.json.CommonJson;
import jsfas.common.json.Response;
import jsfas.security.SecurityUtils;

@RestController
@ControllerAdvice
public class TestFunctionController extends CommonApiController {

	/*
	 * Example GET endpoint testing json scheme
	 */
	@RequestMapping(value = "/test/json", method = RequestMethod.GET)
	public Response getTestJson(HttpServletRequest request, 
			@RequestParam(defaultValue="") String key1, @RequestParam(defaultValue="") String key2) 
					throws Exception {
		
		validateRequestWithJsonSchema(request);
		
		Response response = new Response();
		CommonJson data = new CommonJson();
		data.set("user", SecurityUtils.getCurrentLogin());
		data.set("input_key1", key1);
		data.set("input_key2", key2);
		response.setData(data);

		return setSuccess(response);
	}

	/*
	 * Example POST endpoint testing json scheme
	 */
	@RequestMapping(value = "/test/json", method = RequestMethod.POST)
	public Response postTestJson(HttpServletRequest request, 
			@RequestParam(defaultValue="") String key1, @RequestBody CommonJson requestBody, BindingResult result) 
					throws Exception {
		
		validateRequestWithJsonSchema(request, requestBody);

		Response response = new Response();
		CommonJson data = new CommonJson();		
		data.set("user", SecurityUtils.getCurrentLogin());
		data.set("input_key1", key1);
		data.set("input_request_body", requestBody);
		response.setData(data);

		return setSuccess(response);
	}
}
