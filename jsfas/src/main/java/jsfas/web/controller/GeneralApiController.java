package jsfas.web.controller;


import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import jsfas.common.constants.RestURIConstants;
import jsfas.common.json.CommonJson;
import jsfas.common.json.Response;
import jsfas.common.utils.GeneralUtil;


@RestController
@ControllerAdvice
public class GeneralApiController extends CommonApiController {
	
	@RequestMapping(value = RestURIConstants.STK_STATUS, method = RequestMethod.GET)
	public Response getCategoryList(HttpServletRequest request, @RequestParam Map<String,String> paramMap) throws Exception {
		Response response = new Response();
		
		JSONObject requestParam = new JSONObject(paramMap);
		
		JSONArray outputJson = new JSONArray();
		for (int i=0;i<3;i++) {
			JSONObject jsonObject = new JSONObject();
			jsonObject.put("testing", i);
			outputJson.put(jsonObject);
		}
	
		CommonJson data = new CommonJson().set("stk_status", 
				GeneralUtil.jsonArrayToCommonJson(outputJson));
		
		response.setData(data);
		return setSuccess(response);
	}

}
