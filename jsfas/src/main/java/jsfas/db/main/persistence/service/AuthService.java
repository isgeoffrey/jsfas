package jsfas.db.main.persistence.service;

import jsfas.common.json.CommonJson;

/**
 * Authorization Service
 * @author iswill
 * @since 13/1/2021
 */
public interface AuthService {
	public String getCasUrl(String appPath, String returnPath);
	public CommonJson casAuth(String appPath, String ticket, String path);
	public CommonJson localLogin(String username);
	public String logout();
}
