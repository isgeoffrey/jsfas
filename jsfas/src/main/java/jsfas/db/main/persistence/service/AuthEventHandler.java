package jsfas.db.main.persistence.service;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;

import com.fasterxml.jackson.databind.ObjectMapper;

import jsfas.common.constants.AppConstants;
import jsfas.common.exception.InvalidParameterException;
import jsfas.common.json.CommonJson;
import jsfas.common.utils.GeneralUtil;
import jsfas.db.jssv.persistence.service.OAuth2RestService;
import jsfas.db.main.persistence.domain.ElInpostStaffImpVDAO;
import jsfas.db.main.persistence.repository.ElInpostStaffImpVRepository;
import jsfas.db.rbac.persistence.repository.UserRoleGroupRepository;
import jsfas.redis.domain.Token;
import jsfas.security.SecurityUtils;

public class AuthEventHandler implements AuthService {
	
	@Autowired
	Environment env;
	
	@Autowired
	CommonRoutineService commonRoutineService;
	
	@Autowired
	RedisService redisService;
	
	@Autowired
	ElInpostStaffImpVRepository elInpostStaffImpVRepository;
	
	@Autowired
	UserRoleGroupRepository userRoleGroupRepository;
	
	@Autowired
	OAuth2RestService oAuth2RestService;
	
	private static final Logger log = LoggerFactory.getLogger(AuthEventHandler.class);
	
	@Override
	public String getCasUrl(String appPath, String returnPath) {
		String encodedPath = "";
		try {
			encodedPath = URLEncoder.encode(env.getProperty("cas.nj.callback.path.prefix") + appPath + "/callback/cas" + "?path=" + returnPath, "UTF-8");
		} catch (Exception e) {
			//ignore
		}
		
		return env.getProperty("cas.service.login") + "?service=" + encodedPath;
	}
	
	@Override
	public CommonJson casAuth(String appPath, String ticket, String path) {
		CommonJson outputJson = new CommonJson();
		
		String service = env.getProperty("cas.nj.callback.path.prefix") + appPath + "/callback/cas" + "?path=" + path;
		
		CommonJson response = new CommonJson();
		//Exchange ticket
		//https://castest.ust.hk/cas/login?service=http%3A%2F%2Fisz999.ust.hk%3A8080%2Fjstp%2FFcas%2test
		
		HttpURLConnection conn = null;
		
		try {
			log.info("Validate CAS Ticket");
			String casServiceValidateUrl = env.getProperty("cas.service.validate");
			String urlStr = casServiceValidateUrl
					+ "?service=" + URLEncoder.encode(service, "UTF-8")
					+ "&ticket=" + URLEncoder.encode(ticket, "UTF-8")
					+ "&format=JSON";
			
			URL url = new URL(urlStr);
			conn = (HttpURLConnection) url.openConnection();

			conn.setRequestMethod("GET");
			InputStream is = conn.getInputStream();
			
			response = new ObjectMapper().readValue(is, CommonJson.class);
			
			log.debug("response: {}", response);
			
			is.close();
		} catch (Exception e) {
			log.error("{}", e);
		} finally {
			if (conn != null) conn.disconnect();
		}
		
		Map serviceResponse = response.get("serviceResponse", Map.class);
		
		if (serviceResponse == null) {
			log.info("CAS service no response");
			return outputJson.set("description", "cas serviceResponse null");
		}
		
		if (serviceResponse.containsKey("authenticationSuccess")) {
			//success
			String username = (String) ((Map) serviceResponse.get("authenticationSuccess")).get("user");
			
			log.info("Validate user {}", username);
			// TODO:  user verification
			// check only staff can access the system			
			boolean staffFound = false;
			CommonJson staffJson;
			try {
				if (username.equalsIgnoreCase(env.getProperty(AppConstants.PROD_SUPPORT_USERNAME))) {
					staffFound = true;
				} else {
					staffJson = oAuth2RestService.getStaffInfo(username);
					staffFound = !staffJson.getFilterValue("staff_id").isBlank();
				}
			} catch (Exception e) {
				
			}
			log.info("staff found from api:{}", staffFound);
			// 2nd call if API hv error or incorrect result
			if (!staffFound) {
				List<ElInpostStaffImpVDAO> elInpostStaffImpVDAOList = elInpostStaffImpVRepository.findByUserNam(username);
				log.debug("elInpostStaffImpVDAOList.size() {}", elInpostStaffImpVDAOList.size());
				if(elInpostStaffImpVDAOList.size() == 0) {
					return null;
				}
			}
			
			log.info("Create token for user {}", username);
			UUID uuid = UUID.randomUUID();
			Token token = new Token(uuid.toString(), username);
			redisService.save("TOKEN:" + token.getId(), token);
			
			outputJson
				.set("token", token.getId())
				.set("username", token.getUsername());
			
			if (userRoleGroupRepository.findByRoleGroupDesc("FO P&B").contains(username)) {
				outputJson.set("isPnB", true);
			}
			//TODO: add user type
		}
		
		if (serviceResponse.containsKey("authenticationFailure")) {
			//fail
			log.info("CAS failed");
			outputJson.props().putAll((Map) serviceResponse.get("authenticationFailure"));
			return null;
		}
		
		return outputJson;
	}
	
	@Override
	public CommonJson localLogin(String username) {
		CommonJson outputJson = new CommonJson();
		
		UUID uuid = UUID.randomUUID();
		Token token = new Token(uuid.toString(), username);
		
		redisService.save("TOKEN:" + token.getId(), token);
		
		outputJson
			.set("token", token.getId());
		
		return outputJson;
	}

	@Override
	public String logout() {
		String tokenStr = SecurityUtils.getCurrentToken();
		log.debug("token: {}", tokenStr);
		
		try {
			Token token = (Token) redisService.findById("TOKEN:" + tokenStr);
			redisService.delete("TOKEN:" + token.getId());
			return "success";
		} catch (Exception e) {
			log.debug("token not exist");
			return "token not exist";
		}
		
	}
	
}