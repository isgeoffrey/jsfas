package jsfas.db.main.persistence.repository;

import java.util.List;
import java.util.Map;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import jsfas.common.constants.AprvTypeConstants;
import jsfas.common.constants.PymtStatusConstants;
import jsfas.db.CommonRepository;
import jsfas.db.main.persistence.domain.ElApplPymtScheduleDAO;

public interface ElApplPymtScheduleRepository extends CommonRepository<ElApplPymtScheduleDAO, String> {

	@Query(value = "SELECT * FROM EL_APPL_PYMT_SCHEDULE WHERE appl_hdr_id = :applHdrId AND id NOT IN :idList ", nativeQuery = true)
	List<ElApplPymtScheduleDAO> findByApplHdrIdForRemove(@Param("applHdrId") String applHdrId, @Param("idList")  List<String> upsertDAOId);

	List<ElApplPymtScheduleDAO> findByApplHdrId(String applHdrId);

	@Query(value = "SELECT distinct bco_aprv_id, bco_aprv_name FROM el_appl_pymt_schedule "
			+ "WHERE appl_hdr_id = :appl_hdr_id "
			+ "AND bco_aprv_id NOT IN (SELECT aprv_user_id FROM el_appl_aprv_status where appl_hdr_id = :appl_hdr_id and aprv_type_cde = '" + AprvTypeConstants.BCO_APPL + "' )", nativeQuery = true)
	List<Map<String, String>> findBcoAprvNotInElApplAprvStatus(@Param("appl_hdr_id") String applHdrId);
	
	@Query(value = "SELECT distinct proj_id, proj_nbr, dept_id, fund_cde, class_cde, acct_cde, analysis_cde, bco_aprv_id, bco_aprv_name FROM el_appl_pymt_schedule "
			+ "WHERE appl_hdr_id = :appl_hdr_id "
			+ "AND bco_aprv_id NOT IN (SELECT aprv_user_id FROM el_appl_aprv_status where appl_hdr_id = :appl_hdr_id and aprv_type_cde = '" + AprvTypeConstants.BCO_APPL + "' )", nativeQuery = true)
	List<Map<String, Object>> findBcoAprvNotInElApplAprvStatusForPymt(@Param("appl_hdr_id") String applHdrId);

	@Query(value = "SELECT * FROM EL_APPL_PYMT_SCHEDULE "
			+ "WHERE appl_hdr_id = :applHdrId "
			+ "AND appl_pymt_method_id in ("
			+ "                            SELECT id "
			+ "                            FROM el_appl_pymt_method "
			+ "                            WHERE appl_hdr_id = :applHdrId "
			+ "                                 AND pymt_category = 'SALARY')", nativeQuery = true)
	List<ElApplPymtScheduleDAO> findSalaryByApplHdrId(@Param("applHdrId") String applHdrId);
	
	@Query(value = "SELECT * FROM EL_APPL_PYMT_SCHEDULE "
			+ "WHERE appl_hdr_id = :applHdrId "
			+ "AND appl_pymt_method_id in ("
			+ "                            SELECT id "
			+ "                            FROM el_appl_pymt_method "
			+ "                            WHERE appl_hdr_id = :applHdrId "
			+ "                                 AND pymt_category = 'MPF')", nativeQuery = true)
	List<ElApplPymtScheduleDAO> findMPFByApplHdrId(@Param("applHdrId") String applHdrId);
	
	@Query(value = "SELECT * FROM EL_APPL_PYMT_SCHEDULE "
			+ "WHERE appl_hdr_id = :applHdrId "
			+ "AND pymt_status_cde = '" + PymtStatusConstants.POST +"' "
			+ "AND appl_pymt_method_id in ("
			+ "                            SELECT id "
			+ "                            FROM el_appl_pymt_method "
			+ "                            WHERE appl_hdr_id = :applHdrId "
			+ "                                 AND pymt_category = 'SALARY')", nativeQuery = true)
	List<ElApplPymtScheduleDAO> findPostSalaryByApplHdrId(@Param("applHdrId") String applHdrId);

	@Query(value = "SELECT * FROM EL_APPL_PYMT_SCHEDULE "
			+ "WHERE appl_hdr_id = :applHdrId "
			+ "AND pymt_sched_no = :pymtSchedNo "
			+ "AND appl_pymt_method_id in ("
			+ "                            SELECT id "
			+ "                            FROM el_appl_pymt_method "
			+ "                            WHERE appl_hdr_id = :applHdrId "
			+ "                                 AND pymt_category = 'SALARY')", nativeQuery = true)
	List<ElApplPymtScheduleDAO> findSelectedByApplHdrIdAndSchedNo(@Param("applHdrId") String applHdrId, @Param("pymtSchedNo") int pymtSchedNo);
	
	@Query(value = "SELECT count(*) FROM (SELECT count(*) FROM el_appl_pymt_schedule "
			+ "                        WHERE pymt_status_cde = 'POST' "
			+ "                        AND appl_hdr_id = :applHdrId "
			+ "                        GROUP by pymt_sched_no)", nativeQuery = true)
	int countPaidScheduleNo(@Param("applHdrId") String applHdrId);

	@Query(value = "SELECT "
			+ "eps.CHNG_DAT, "
			+ "eah.APPL_NBR, "
			+ "eah.APPL_USER_EMPLID, "
			+ "eps.SAL_ELEMNT, "
			+ "eah.APPL_START_DT, "
			+ "eah.APPL_END_DT, "
			+ "eps.PYMT_LINE_AMT, "
			+ "eps.PYMT_START_DT, "
			+ "eps.PYMT_END_DT, "
			+ "eps.PYMT_REV_START_DT, "
			+ "eps.PYMT_REV_END_DT, "
			+ "eps.EMPL_NBR, "
			+ "eps.ACCT_CDE, "
			+ "eps.ANALYSIS_CDE, "
			+ "eps.FUND_CDE, "
			+ "eps.DEPT_ID, "
			+ "eps.PROJ_ID, "
			+ "eps.CLASS_CDE, "
			+ "eps.BR_NO, "
			+ "eps.BR_LINE_NO, "
			+ "eps.BR_DIST_LINE_NO, "
			+ "eps.APPL_HDR_ID, "
			+ "epm.PYMT_TYPE_CDE, "
			+ "aet.EL_TYPE_DESCR "
			+ "FROM "
			+ "EL_APPL_PYMT_SCHEDULE eps "
			+ "LEFT JOIN el_appl_hdr eah ON eps.appl_hdr_id = eah.id "
			+ "LEFT JOIN el_appl_pymt_method epm ON eps.appl_hdr_id = epm.appl_hdr_id "
			+ "LEFT JOIN el_appl_el_type aet ON aet.id = epm.appl_el_type_id "
			+ "WHERE epm.pymt_type_cde <> 'RECURR' "
			+ "AND epm.pymt_category = 'SALARY' "
			+ "AND eps.pymt_submit_dttm <> to_date('17/11/1858', 'DD/MM/YYYY') "
			+ "AND eps.pymt_status_cde = '" + PymtStatusConstants.POST +"' ", nativeQuery = true)
	List<Map<String, Object>> findPostedInstalmPaymentSchedulesForReport ();
	
	@Query(value = "SELECT "
			+ "eps.CHNG_DAT, "
			+ "eah.APPL_NBR, "
			+ "eah.APPL_USER_EMPLID, "
			+ "eps.SAL_ELEMNT, "
			+ "eah.APPL_START_DT, "
			+ "eah.APPL_END_DT, "
			+ "eps.PYMT_LINE_AMT, "
			+ "eps.PYMT_START_DT, "
			+ "eps.PYMT_END_DT, "
			+ "eps.PYMT_REV_START_DT, "
			+ "eps.PYMT_REV_END_DT, "
			+ "eps.EMPL_NBR, "
			+ "eps.ACCT_CDE, "
			+ "eps.ANALYSIS_CDE, "
			+ "eps.FUND_CDE, "
			+ "eps.DEPT_ID, "
			+ "eps.PROJ_ID, "
			+ "eps.CLASS_CDE, "
			+ "eps.BR_NO, "
			+ "eps.BR_LINE_NO, "
			+ "eps.BR_DIST_LINE_NO, "
			+ "eps.APPL_HDR_ID, "
			+ "epm.PYMT_TYPE_CDE, "
			+ "aet.EL_TYPE_DESCR "
			+ "FROM "
			+ "EL_APPL_PYMT_SCHEDULE eps "
			+ "LEFT JOIN el_appl_hdr eah ON eps.appl_hdr_id = eah.id "
			+ "LEFT JOIN el_appl_pymt_method epm ON eps.appl_hdr_id = epm.appl_hdr_id "
			+ "LEFT JOIN el_appl_el_type aet ON aet.id = epm.appl_el_type_id "
			+ "WHERE epm.pymt_type_cde = 'RECURR' "
			+ "AND epm.pymt_category = 'SALARY' "
			+ "AND eps.pymt_submit_dttm <> to_date('17/11/1858', 'DD/MM/YYYY') "
			+ "AND eps.pymt_status_cde = '" + PymtStatusConstants.POST +"' ", nativeQuery = true)
	List<Map<String, Object>> findPostedRecurrPaymentSchedulesForReport ();
	
	
	@Query(value = "SELECT DISTINCT pymt_sched_no,  "
			+ "(SELECT SUM(pymt_line_amt) AS total_pymt_amount "
			+ "FROM EL_APPL_PYMT_SCHEDULE "
			+ "WHERE appl_hdr_id = :applHdrId "
			+ "AND appl_pymt_method_id IN ( "
			+ "  SELECT id "
			+ "  FROM el_appl_pymt_method "
			+ "  WHERE appl_hdr_id = :applHdrId "
			+ "  AND pymt_category = 'SALARY' "
			+ " AND pymt_sched_no = e.pymt_sched_no) "
			+ ") as TOTAL_SUM, PYMT_STATUS_CDE "
			+ "FROM EL_APPL_PYMT_SCHEDULE e  "
			+ "WHERE appl_hdr_id = :applHdrId  "
			+ "AND appl_pymt_method_id in ( "
			+ "SELECT id  "
			+ "FROM el_appl_pymt_method  "
			+ "WHERE appl_hdr_id = :applHdrId  "
			+ "AND pymt_category = 'SALARY') ", nativeQuery = true)
	List<Map<String, Object>> findSelectedByApplHdrIdWithSchedNoPymt(@Param("applHdrId") String applHdrId);
	
}
