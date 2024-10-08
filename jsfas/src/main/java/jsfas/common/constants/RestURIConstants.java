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
	public static final String STK_PLAN_STATUS = "/stk_plan_status";
	public static final String DEPT_LIST = "/dept";
	
	// Stocktake plan
	public static final String STK_PLAN_LIST = "/stk_plan_list";
	public static final String STK_PLAN = "/stk_plan";
	public static final String STK_PLAN_HDR = "/stk_plan_hdr";
	public static final String STK_PLAN_DTL_SUMMARY = "/stk_plan_dtl_summary";

	public static final String STK_ITEM = "/stk_item";
	public static final String STK_ITEM_CLEAR = "/stk_item_clear";

	public static final String STK_PLAN_LOCK = "/stk_plan_lock";

	public static final String STK_PENDING = "/stk_pending";
	public static final String STG_ITEM = "/stg_item";
	public static final String STG_ITEM_CLEAR = "/stg_item_clear";	

	public static final String SUBMIT_STK_PLAN = "/submit_stk_plan";
	public static final String UPDATE_STK_ITEM = "/update_stk_item";
}

