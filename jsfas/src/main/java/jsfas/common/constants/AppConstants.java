package jsfas.common.constants;

import org.quartz.Trigger.TriggerState;

import jsfas.common.excel.XCDbl;
import jsfas.common.excel.XCStr;

/**
 * @author iseric123
 * @since 12/5/2016
 * @see Class for Application Constants
 */
public class AppConstants {
	
	//common constants
	public static final String MAIN_DB_URL = "db.main.url";
	public static final String MAIN_DB_USERNAME = "db.main.username";
	public static final String MAIN_DB_PASSWORD = "db.main.password";
	
	public static final String PROD_SUPPORT_USERNAME = "prod.support.username";
	
	public static final String DB_DRIVER = "db.driver-class-name";
	public static final String DB_MAX_ACTIVE = "db.max-active";
	public static final String DB_MAX_IDLE = "db.max-idle";
	public static final String DB_MAX_WAIT = "db.max-wait";
	
	public static final int LOCK_TIMEOUT = 10000;
	
	public static final String REDIS_PREFIX = "JSFAS:";
	public static final int SESSION_TIMEOUT = 3600;
	
	public static final String PASSWORD_PROTECTOR_PASSWORD = "MWRyb3dzc2FwanN3aGRldmNyeXB0b3Bhc3N3b3JkYWI=";
	public static final String PASSWORD_PROTECTOR_SALT = "YXoxMzV0bGFzanN3aGRldmNyeXB0b3NhbHRlZjI0Njg=";
	
	public static final String SMTP_HOST = "smtp.host";
	public static final String SMTP_USERNAME = "smtp.username";
	public static final String SMTP_PASSWORD = "smtp.password";
	public static final String SMTP_LOGIN_REQUIRED = "smtp.login.required";
	
	public static final String BUDGET_APPROVAL_API_URL = "budget.approval.url";

	public static final String APRV_SR = "aprv.sr";
	public static final String APRV_SFM = "aprv.sfm";
	public static final String APRV_PNB ="aprv.pnb";
	public static final String APRV_PROVOST_ID = "aprv.provost.id";
	public static final String APRV_PROVOST_NAME = "aprv.provost.name";
	
	//Role-based access control environment constants
	public static final String RBAC_ROLE_NODE_TYPE = "rbac.role.node.type";
	public static final String RBAC_USER_NODE_TYPE = "rbac.user.node.type";
	public static final String RBAC_ACTION_NODE_TYPE = "rbac.action.node.type";
	public static final String RBAC_FIELD_NODE_TYPE = "rbac.field.node.type";
	public static final String RBAC_GROUP_NODE_TYPE = "rbac.group.node.type";
	public static final String RBAC_ENTITY_NODE_TYPE = "rbac.entity.node.type";
	public static final String RBAC_ROLE_DEFAULT = "rbac.role.default";
	public static final String RBAC_ROLE_SYSADMIN = "rbac.role.sysadmin";
	public static final String RBAC_PERM_STATUS_DEFAULT = "rbac.perm.status.default";
	public static final String RBAC_PERM_STATUS_ALLOWED = "rbac.perm.status.allowed";
	public static final String RBAC_PERM_STATUS_EXPIRED = "rbac.perm.status.expired";
	public static final String RBAC_PERM_STATUS_INACTIVE = "rbac.perm.status.inactive";
	public static final String RBAC_PERM_STATUS_REJECTED = "rbac.perm.status.rejected";
	public static final String RBAC_PERM_FMT_STR = "rbac.perm.fmt.str";
	public static final String RBAC_ROLE_USERNAME_CHECK = "rbac.role.username.check";
    public static final String RBAC_ROLE_USERNAME_TAUTOCOMPLETE = "rbac.role.username.tautocomplete";
	public static final String RBAC_PERM_INHERIT_NOCHECK = "rbac.perm.inherit.nocheck";
    public static final String RBAC_PERM_PREDICATE_ENABLED = "rbac.perm.predicate.enabled";
    public static final String RBAC_PERM_EFFPERIOD_ENABLED = "rbac.perm.effperiod.enabled";
	public static final String RBAC_PERM_ENABLED = "rbac.perm.enabled";
	
    //Role-based access control action type constants
    public static final String ADD_ACTION_TYPE = "ADD001";
    public static final String CHG_ACTION_TYPE = "CHG001";
    public static final String REM_ACTION_TYPE = "REM001";
    public static final String VW_ACTION_TYPE = "VW0001";
    
	public static final String RESPONSE_JSON_SUCCESS_CODE = "200";
	public static final String RESPONSE_JSON_FAIL_CODE = "400";
	public static final String RESPONSE_JSON_FAIL_N_REFRESH_CODE = "401";
	public static final String RESPONSE_JSON_RECORD_EXIST = "402";
	public static final String RESPONSE_JSON_RECORD_MODIFIED = "403";
	public static final String RESPONSE_JSON_RECORD_REMOVED = "404";
	
	public static final String PRED_QUERY_PARAM_JSON = "JSON";
	public static final String PRED_QUERY_SQL = "SQL";
	
	public static final String SCHEDULER_ENABLED="scheduler.enabled";
	public static final String SCHEDULER_RETRY_COUNT = "scheduler.retry.count";
    public static final Integer DEFAULT_SCHEDULER_RETRY_COUNT = -1;
    
	public static final String SCHEDULER_TRIGGER_STATE_PAUSED = TriggerState.PAUSED.toString();
	public static final String SCHEDULER_TRIGGER_STATE_NORMAL = TriggerState.NORMAL.toString();
	public static final String SCHEDULER_TRIGGER_STATE_ERROR = TriggerState.ERROR.toString();
	public static final String SCHEDULER_TRIGGER_STATE_BLOCKED = TriggerState.BLOCKED.toString();
	public static final String SCHEDULER_TRIGGER_STATE_COMPLETE = TriggerState.COMPLETE.toString();
	
	public static final String SCHEDULEJOB_USERNAME = "schedulejob.username";
    
    public static final Integer SYS_CATG_CDE_LEN = 1;
    public static final Integer FUNC_CATG_CDE_LEN = 1;
    public static final Integer FUNC_CDE_LEN = SYS_CATG_CDE_LEN + FUNC_CATG_CDE_LEN + 2;
    public static final Integer FUNC_SUB_CDE_LEN = 1;
    
    public static final Integer MAX_SEARCH_SIZE = 15;
	
    public static final String YES ="y";
    public static final String NO = "n";
	
	//application specific constants
	public static final String SYS_SHORT_NAME = "JSFAS";
	public static final String SYS_FULL_NAME = "Java Spring Template Project";
	public static final String SYS_CATG_CDE = "A";
	public static final String SCHEDULER_ADHOC_SIMPLE_TRIGGER_GROUP = SYS_SHORT_NAME.toLowerCase() + "AdhocSimpleTriggerGroup";
	
	//application function code
	public static final String RBAC_MAINT_FUNC_CDE = "AD04";
	public static final String SCHEDULER_FUNC_CDE = "PDS03";
	public static final String FAS_TYPE_MAINT_FUNC_CDE = "AD03";
	public static final String ENQUIRE_APPL_FUNC_CDE = "AE01";
	public static final String ENQUIRE_LOA_FUNC_CDE = "AE02";
	public static final String REPORT_PAYMENT_STATUS_FUNC_CDE = "AE03";
	public static final String REPORT_PAYMENT_PROCESSED_FUNC_CDE = "AE04";
	public static final String REPORT_APPLICATION_DATA_FUNC_CDE = "AE05";
	public static final String ENQUIRE_FNB_FUNC_CDE = "AE06";
	public static final String UPLOAD_PI_DIR = "upload.pi.dir";
	public static final String UPLOAD_EA_DIR = "upload.ea.dir";
	
	//application function sub code
	public static final String VW_FUNC_SUB_CDE = "E";
    public static final String ADD_FUNC_SUB_CDE = "A";
    public static final String CHG_FUNC_SUB_CDE = "C";
    public static final String REM_FUNC_SUB_CDE = "R";
    public static final String EXE_FUNC_SUB_CDE = "X"; 
    
    // web application path prefix
    public static final String PATH_APP_PREFIX = "path.app.prefix";
    
    // JSSV
    public static final String JSSV_LINK = "jssv.link";
    
    //upload excel header
    public static final String XLS_HDR_EXIST = "Exist";
    public static final String XLS_HDR_NOT_EXIST = "Not Exist";
    public static final String XLS_HDR_YET_TO_BE_LOCATED = "Yet-to-be Located";
    public static final String XLS_HDR_CUSTODAIN_DEPARTMENT_CODE = "Custodian Department Code";
    public static final String XLS_HDR_CUSTODAIN_DEPARTMENT_DESCR = "Custodian Department Description";
    public static final String XLS_HDR_BUSINESS_UNIT = "Business Unit";
    public static final String XLS_HDR_ASSEST_PROFILE_ID = "Asset Profile ID";
    public static final String XLS_HDR_ASSEST_PROFILE_DESCR = "Asset Profile Description";
    public static final String XLS_HDR_ASSET_ID = "Asset ID";
    public static final String XLS_HDR_DETAILED_ITEM_DESCR = "Detailed Item Description";
    public static final String XLS_HDR_TOTAL_COST = "Total Cost";
    public static final String XLS_HDR_NET_BOOK_VALUE = "Net Book Value";
    public static final String XLS_HDR_INVOICE_DATE = "Invoice Date";
    public static final String XLS_HDR_PO_ID = "PO / BR No.";
    public static final String XLS_HDR_REGION = "Region";
    public static final String XLS_HDR_NOT_UST_PROPERTY = "Not UST Property";
    public static final String XLS_HDR_DONATED_ITEM = "Donated Item";
    public static final String XLS_HDR_LOCATION = "Location";
    public static final String XLS_HDR_VOUCHER_ID = "Voucher ID";
    public static final String XLS_HDR_INVOICE_ID = "Invoice ID";
    
}
