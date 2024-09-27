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
	public static final String ADMIN_MENU = "/el_admin"; //admin menu
	
	// API
    public static final String EXE_RBAC_CMD_LIST = "/el_exe_rbac_cmd_list";
    public static final String GET_RBAC_USER_INFO = "/el_get_rbac_user_info";
	public static final String GET_RBAC_USER_ROLE_GRP = "/el_get_rbac_user_role_grp";
	public static final String GET_RBAC_ENTITY_REL = "/el_get_rbac_entity_rel";
    public static final String GET_RBAC_ROLE_GRP_PERM = "/el_get_rbac_role_grp_perm";
    public static final String GET_RBAC_USERS_BY_PARAM_FUZZY = "/el_get_rbac_users_by_param_fuzzy";
    public static final String CHK_RBAC_USER_NAME = "/el_chk_rbac_user_name";
    
	//--------------------------------------------------------------------------
	//------------------------------SCHEDULER-----------------------------------
	//--------------------------------------------------------------------------
    public static final String SCHEDULER_PAGE = "el_scheduler";
	public static final String SCHEDULER_PATH = "/" + SCHEDULER_PAGE;
	public static final String GET_SCHEDULER_JOB_LIST = "/el_get_scheduler_job_list";
	public static final String RESUME_SCHEDULER_TRIGGER = "/el_resume_scheduler_trigger";
	public static final String RESUME_SCHEDULER_ALL_TRIGGER = "/el_resume_scheduler_all_trigger";
	public static final String PAUSE_SCHEDULER_TRIGGER = "/el_pause_scheduler_trigger";
	public static final String PAUSE_SCHEDULER_ALL_TRIGGER = "/el_pause_scheduler_all_trigger";
	public static final String ADD_SCHEDULER_TRIGGER = "/el_add_scheduler_trigger";
	public static final String CHG_SCHEDULER_TRIGGER = "/el_chg_scheduler_trigger";
	public static final String REM_SCHEDULER_TRIGGER = "/el_rem_scheduler_trigger";
	public static final String GET_SCHEDULE_JOB_INFO_LIST = "/el_get_scheduler_job_info_list";
	
	
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
	public static final String CATEGORY_LIST = "/el-categories";
	public static final String PAYMENT_TYPE_LIST = "/pymt-types";
	public static final String ACCOUT_CODE_LIST = "/acct-cdes"; 
	public static final String ANALYSIS_CODE_LIST = "/anal-cdes";
	public static final String FUND_CODE_LIST = "/fund-cdes";
	public static final String PROJ_ID_LIST = "/proj-ids";
	public static final String CLASS_CODE_LIST = "/class-cdes";
	public static final String DEPT_ID_LIST = "/dept-ids";
	public static final String SAL_ELEMENT_LIST = "/sal-elements";
	public static final String APPL_STAT_LIST = "/appl-stats";
	
	public static final String ACAD_TERMS = "/acad-terms";
	public static final String COURSE_LIST = "/courses";
	public static final String PROGRAM_LIST = "/prgms";
	public static final String CO_COURSE_LIST = "co-courses";
	public static final String SCHL_LIST = "/schls";
	
	public static final String STAFF_LIST = "/staffs";
	public static final String BCO_APPROVERS = "/bco-apprvs";
	
	public static final String DEFAULT_DEPT_HEAD = "/default-dept-head/{deptid}";
	// Extra Load Type
	public static final String EXTRA_LOAD_TYPES = "/el-types";
	public static final String EXTRA_LOAD_TYPES_BY_ID = "/el-types/{type-id}";
	public static final String EXTRA_LOAD_TYPE_MAPPING = "/el-types-mapping";
	public static final String EXTRA_LOAD_TYPE_MAPPING_BY_ID = "/el-types-mapping/{id}";
	
	// Extra Load Application
	public static final String EXTRA_LOAD_APPLICATIONS = "/appls";
	public static final String EXTRA_LOAD_APPLICATIONS_BY_ID = "/appls/{id}";
	public static final String EXTRA_LOAD_APPLICATIONS_APPROVERS = "/appls/apprvs";
	public static final String EXTRA_LOAD_APPLICATIONS_FOR_APPROVERS = "/appr-appls";
	public static final String EXTRA_LOAD_APPLICATIONS_STATUS = "/appls/status/{id}";
	public static final String ENQUIRE_EXTRA_LOAD_APPLICATIONS = "/appls-enq";
	
	// Letter of Appointment 
	public static final String LOA = "/appointments";
	public static final String LOA_BY_ID = "/appointments/{id}";
	public static final String LOA_STATUS_BY_ID = "/appointments/status/{id}";
	public static final String LOA_FILE_BY_ID = "/appointments/files/{id}";
	public static final String SEND_REMIND_EMAIL = "/appointments/send_remind_email/{id}";
	
	// Payment Submission
	public static final String PAYMENTS = "/pymts";
	public static final String PAYMENTS_BY_ID = "/pymts/{id}";
	public static final String PAYMENTS_STATUS = "/pymts/status/{id}";
	public static final String PAYMENTS_FOR_APPROVERS = "/pymts-appls";
	
	public static final String PAYMENTS_PREVIEW_BY_ID = "/pymts_preview/{id}";
	public static final String PENDING_PNB_ENQ = "/pnb_enq";
	public static final String PENDING_PNB_REPORT = "/pnb_report";
	public static final String PNB_UPDATE_SCHEDULE = "/pnb_update_schedule/{id}";
	public static final String PNB_BATCH_UPDATE_SCHEDULE = "/pnb_batch_update_schedule";
	
	// Payment Details
	public static final String TOTAL_AMOUNT = "/pymt-amt";
	public static final String MPF_ALLOCATION = "/mpf-alloc";
	
	// Payment Records
	public static final String PYMT_RECORDS = "/pymt-records/{id}";
	
	// Enquiry for FO
	public static final String ENQUIRY_LOA = "/enquiry/appointments";
	public static final String ENQUIRY_APPLICATION = "/enquiry/application";
	public static final String ENQUIRY_REPORT_APPLICATION_DATA = "/enquiry/report_application_data";
	
	// For PROVOST exceptional approval
	public static final String APPROVED_APPLS = "/approved_appls/{id}";
	
	// For Reporting for FO
	public static final String REPORT_PAYMENT_STATUS = "/report/payment_status";
	public static final String REPORT_PAYMENT_PROCESSED = "/report/payment_processed";
	
	// For Batch Approval
	public static final String BATCH_APPROVAL = "/batch_approval";
}

