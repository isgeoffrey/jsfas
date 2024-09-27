package jsfas.db.jssv.persistence.service;

import java.util.LinkedHashMap;
import java.util.List;

import javax.inject.Inject;
import javax.servlet.http.HttpSession;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.env.Environment;
import org.springframework.security.oauth2.client.OAuth2RestTemplate;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import jsfas.common.constants.AppConstants;
import jsfas.common.exception.ApiCallFailException;
import jsfas.common.json.CommonJson;

public class OAuth2RestEventHandler implements OAuth2RestService {
	@Inject
	private Environment env;
	
	@Autowired
	@Qualifier("jssvHrisRestTemplateForJob")
	private OAuth2RestTemplate jssvHrisRestTemplate;
	
	private final Logger log = LoggerFactory.getLogger(this.getClass());
	
	@Override
	public CommonJson getStaffInfo(String itscAc) {
		CommonJson outputJson = new CommonJson();
		/*
		 * try { ServletRequestAttributes attr = (ServletRequestAttributes)
		 * RequestContextHolder.currentRequestAttributes(); HttpSession session =
		 * attr.getRequest().getSession(); if(session != null) { outputJson =
		 * (CommonJson) session.getAttribute("cachedStaffInfo_"+itscAc); if(outputJson
		 * != null) { log.debug("Function getStaffInfo() session arribute found");
		 * return outputJson; }else { outputJson = new CommonJson(); } }
		 * }catch(Exception e) { //ignore error for schedule job }
		 */
		try {
			CommonJson commonJson = null;
			// try twice
			try {
				commonJson = jssvHrisRestTemplate.getForObject(env.getProperty(AppConstants.JSSV_LINK) + "/hris/inpost_staff?itsc_ac=" + itscAc , CommonJson.class);				
			} catch (Exception e) {
				Thread.sleep(100);
				commonJson = jssvHrisRestTemplate.getForObject(env.getProperty(AppConstants.JSSV_LINK) + "/hris/inpost_staff?itsc_ac=" + itscAc , CommonJson.class);
			}
			
			if (commonJson.getFilterValue("status").contentEquals(AppConstants.RESPONSE_JSON_SUCCESS_CODE)) {
				@SuppressWarnings("unchecked")
				LinkedHashMap<String, String> profileMap = ((List<LinkedHashMap<String, String>>) commonJson.get("profile", List.class)).get(0);
				
				outputJson.set("staff_id", profileMap.get("staff_id"));
				outputJson.set("pri_surname", profileMap.get("pri_surname"));
				outputJson.set("pri_givename", profileMap.get("pri_givename"));
				outputJson.set("pri_fullname", profileMap.get("pri_fullname"));
				outputJson.set("prf_fullname", profileMap.get("prf_fullname"));
				outputJson.set("deptid", profileMap.get("deptid"));
				outputJson.set("deptcode", profileMap.get("deptcode"));
				outputJson.set("deptdesc", profileMap.get("deptdesc"));
				outputJson.set("work_email", profileMap.get("work_email"));
			} else {
				log.debug("itsc username: {}", itscAc);
				log.debug("api return error: {}", commonJson.getProps().toString());
				
				outputJson.set("staff_id", "");
				outputJson.set("pri_surname", "");
				outputJson.set("pri_givename", "");
				outputJson.set("pri_fullname", "");
				outputJson.set("prf_fullname", "");
				outputJson.set("deptid", "");
				outputJson.set("deptcode", "");
				outputJson.set("deptdesc", "");
				outputJson.set("work_email", "");
			}
		} catch (Exception e) {
			log.info(Thread.currentThread().getStackTrace()[1].getMethodName()+"@"+this.getClass().getSimpleName()+" itseAc = {}, Error Occur...", itscAc, e);
			throw new ApiCallFailException();
		}

		/*
		 * try { ServletRequestAttributes attr = (ServletRequestAttributes)
		 * RequestContextHolder.currentRequestAttributes(); HttpSession session =
		 * attr.getRequest().getSession();
		 * session.setAttribute("cachedStaffInfo_"+itscAc, outputJson);
		 * log.debug("Function getStaffInfo()save in session arribute");
		 * }catch(Exception e) { //ignore error for schedule job }
		 */
		
		log.debug("Function getStaffInfo()@SecurityUtils return result @API outputJson: {}", outputJson);
		
		return outputJson;
	}

}
