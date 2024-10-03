package jsfas.common.constants;

/**
 * @author iseric
 * @since 12/5/2016
 * @see Class for Restful URI link mapping
 */
public class RestURIConstants {
		
	//--------------------------------------------------------------------------
	//---------------------------------WEB--------------------------------------
	//--------------------------------------------------------------------------
	public static final String INDEX = "/"; //index mapping
	public static final String LOGOUT = "/web/logout";
	public static final String ADMIN_MENU = "/fas_admin"; //admin menu
	
	// API

	// Authorization
	public static final String CAS_URL = "/auth/cas/url";
	public static final String CAS_AUTH = "/auth/cas/auth";
	public static final String LOGIN = "/auth/login";
	public static final String CAS_LOGOUT = "/auth/logout";
	public static final String GET_FUNC_MENU = "/auth/auth_path_name";
	
	// Files
	public static final String FILES = "/files";
	public static final String FILES_BY_ID = "/files/{id}";
	
	// General
	public static final String STK_STATUS = "/stk_status";
}

