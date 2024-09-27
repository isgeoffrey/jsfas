package jsfas.db.main.persistence.repository;

import java.sql.Timestamp;
import java.util.List;
import java.util.Map;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import jsfas.db.CommonRepository;
import jsfas.db.main.persistence.domain.FoApplicationEnquiryRaw;

public interface FoApplicationEnquiryRepository extends CommonRepository<FoApplicationEnquiryRaw, String> {
	@Query(value=
			"SELECT "
			+ "ROWNUM AS id, "
			+ "hdr.id as app_id, "
			+ "hdr.appl_nbr as app_nbr, "
			+ "hdr.br_no, "
			+ "hdr.version_no, "
			+ "dept_tb.DEPT_SHORT_DESC as appl_requester_dept, "
			+ "dept_app_tb.DEPT_SHORT_DESC as appl_user_dept, "
			+ "hdr.appl_requester_id, "
			+ "hdr.appl_requester_name, "
			+ "hdr.appl_user_id, "
			+ "hdr.appl_user_name, "
			+ "hdr.appl_dttm, "
			+ "catg.category_descr, "
			+ "hdr.appl_stat_cde, "
			+ "CASE "
			+ "		WHEN :pendingFoSr = 'Y' THEN 'Pending FO SR review' "
			+ "		WHEN :pendingBco = 'Y' THEN 'Pending payment approval - BCO' "
			+ "		ELSE stat.appl_stat_descr "
			+ "END as appl_stat_descr, "
			+ "COALESCE((SELECT aprv_user_id  "
			+ "FROM el_appl_aprv_status  "
			+ "WHERE approved <= 0  "
			+ "AND hdr.id = el_appl_aprv_status.appl_hdr_id  "
			+ "ORDER BY arpv_seq  "
			+ "FETCH FIRST 1 ROW ONLY),'') as pending_aprver_id,  "

			+ "COALESCE((SELECT aprv_user_name  "
			+ "FROM el_appl_aprv_status  "
			+ "WHERE approved <= 0  "
			+ "AND hdr.id = el_appl_aprv_status.appl_hdr_id  "
			+ "ORDER BY arpv_seq  "
			+ "FETCH FIRST 1 ROW ONLY),'') as pending_aprver_name,  "
			
			+ "COALESCE((SELECT aprv_type_cde  "
			+ "FROM el_appl_aprv_status  "
			+ "WHERE approved <= 0  "
			+ "AND hdr.id = el_appl_aprv_status.appl_hdr_id  "
			+ "ORDER BY arpv_seq  "
			+ "FETCH FIRST 1 ROW ONLY),'') as pending_aprver_type,  "

			+ "type.el_type_nam, "
//			 + "COUNT(distinct hdr.id) OVER (PARTITION BY hdr.appl_user_id) AS appl_user_id_count, "
			+ "(SELECT COUNT(APPL_USER_ID) FROM el_appl_hdr WHERE APPL_USER_ID = hdr.appl_user_id AND APPL_STAT_CDE NOT IN ('DRAFT', 'REMOVED', 'RETURNED')) as appl_user_id_count, "
			+ "(SELECT yr_term_desc FROM el_acad_term_v term WHERE term.strm = hdr.appl_start_term) appl_start_term_desc, "
			+ "hdr.appl_start_term, "
			+ "(SELECT yr_term_desc FROM el_acad_term_v term WHERE term.strm = hdr.appl_end_term) appl_end_term_desc, "
			+ "hdr.appl_end_term, "
			+ "hdr.appl_start_dt,  "
			+ "hdr.appl_end_dt,  "
			+ "prgm.prgm_cde,  "
			+ "prgm.sch_cde,  "
			+ "prgm.dept,  "
			+ "appl.pymt_amt as app_amt,  "

			+ "(SELECT sum(pymt_amt)  "
			+ "FROM el_appl_pymt_method  "
			+ "WHERE pymt_category = 'MPF' AND hdr.id = el_appl_pymt_method.appl_hdr_id) as mpf_amt,  "
			+ " "
			+ "pymt_m.pymt_type_cde,  "
			+ "pymt_m.pymt_amt, "

			+ "(SELECT count(DISTINCT pymt_sched_no)  "
			+ "FROM el_appl_pymt_schedule  "
			+ "WHERE pymt_m.id = el_appl_pymt_schedule.appl_pymt_method_id) as instalm_no,  "

			+ "COALESCE((SELECT max(pymt_sched_no)  "
			+ "FROM el_appl_pymt_schedule  "
			+ "WHERE pymt_m.id = el_appl_pymt_schedule.appl_pymt_method_id AND pymt_status_cde IN ('SUBMIT', 'POST')),0) as instalm_seq,  "

			+ "pymt_m.pymt_start_dt,  "
			+ "pymt_m.pymt_end_dt,  "
			+ "hdr.creat_dat,  "
			+ "hdr.chng_dat,  "
			+ "aprv.aprv_type_cde,  "
			+ "aprv.aprv_user_id,  "
			+ "aprv.aprv_user_name,  "
			+ "COALESCE(aprv.approved, 0) as approved,  "
			+ "aprv.aprv_dttm,  "
			+ "aprv.aprv_inpost_name, "
			+ "schl_appl.schl_short_desc as appl_schl_short, "
			+ "schl_appl.schl_long_desc as appl_schl_long, "
			+ "schl_req.schl_short_desc as req_schl_short, "
			+ "schl_req.schl_long_desc as req_schl_long "
			+ "FROM  "
			+ "el_appl_hdr hdr  "
			+ "LEFT JOIN el_appl_stat_tab stat ON hdr.appl_stat_cde = stat.appl_stat_cde  "
			+ "LEFT JOIN el_appl_el_type appl ON hdr.id = appl.appl_hdr_id  "
			+ "LEFT JOIN el_type_tab type ON appl.el_type_id = type.id  "
			+ "LEFT JOIN el_category_tab catg ON hdr.category_cde = catg.category_cde  "
			+ "LEFT JOIN el_appl_prgm prgm ON hdr.id = prgm.appl_hdr_id  "
			+ "LEFT JOIN (select * from el_appl_pymt_method where pymt_category = 'SALARY') pymt_m on hdr.id = pymt_m.appl_hdr_id  "
			+ "LEFT JOIN (select aprv_status.*, inpost.display_nam as aprv_inpost_name from el_appl_aprv_status aprv_status left JOIN (select distinct user_nam, display_nam from el_inpost_staff_imp_v) inpost on inpost.user_nam = aprv_status.aprv_user_id) aprv ON hdr.id = aprv.appl_hdr_id "
			+ "LEFT JOIN EL_DEPT_CHRT_V dept_tb on dept_tb.DEPT_ID = hdr.appl_requester_deptid "
			+ "LEFT JOIN EL_DEPT_CHRT_V dept_app_tb on dept_app_tb.DEPT_ID = hdr.appl_user_deptid "
			+ "LEFT JOIN EL_SCHL_CHRT_V schl_appl on schl_appl.dept_id = hdr.appl_user_deptid "
			+ "LEFT JOIN EL_SCHL_CHRT_V schl_req on schl_req.dept_id = hdr.appl_user_deptid "
			+ "WHERE "
			+ "hdr.appl_stat_cde NOT IN ('DRAFT', 'REMOVED') "
			+ "AND (hdr.appl_user_name LIKE '%' || :applName || '%' OR hdr.appl_user_id LIKE :applName ) "
			+ "AND (hdr.appl_requester_name LIKE '%' || :requesterName || '%' OR hdr.appl_requester_id LIKE :requesterName) "
			+ "AND hdr.appl_nbr LIKE :applNbr "
			+ "AND COALESCE(schl_appl.schl_id,' ') LIKE :applSchlId "
			+ "AND dept_tb.DEPT_SHORT_DESC LIKE :requesterDept " //requesterDept
			+ "AND hdr.appl_stat_cde LIKE :applStatCde " //applStatCde
			+ "AND hdr.br_no LIKE :brNo " //brNo
			+ "AND hdr.appl_dttm >= :applFrom " //applFrom
			+ "AND hdr.appl_dttm < :applToPlusOneMonth " // applToPlusOneMonth
			+ "AND appl.el_type_id LIKE :elTypes " // elTypes
			// pending for FO_SR or FO_SFM
			+"AND (:pendingFoSr = 'N' OR  hdr.id IN (SELECT a.appl_hdr_id  FROM EL_APPL_APRV_STATUS a "
									+ "JOIN ( "
									+ "    SELECT appl_hdr_id , MIN(arpv_seq) min_arpv_seq FROM EL_APPL_APRV_STATUS a "
									+ "    JOIN EL_APPL_HDR h ON a.appl_hdr_id = h.id "
									+ "    WHERE  "
									+ "        (h.APPL_STAT_CDE = 'PENDING' OR  h.APPL_STAT_CDE = 'PENDING_PYMT_APPR') "
									+ "        AND approved = -1"
									+ "    GROUP BY a.appl_hdr_id "
									+ ") b "
									+ "ON a.appl_hdr_id = b.appl_hdr_id AND a.arpv_seq = b.min_arpv_seq "
									+ "WHERE a.aprv_type_cde = 'FO_SR' OR a.aprv_type_cde = 'FO_SFM')"
									+ ") "
			// pending for BCO
			+"     AND (:pendingBco = 'N' OR  hdr.id IN (SELECT a.appl_hdr_id  FROM EL_APPL_APRV_STATUS a "
									+ "JOIN ( "
									+ "    SELECT appl_hdr_id , MIN(arpv_seq) min_arpv_seq FROM EL_APPL_APRV_STATUS a "
									+ "    JOIN EL_APPL_HDR h ON a.appl_hdr_id = h.id "
									+ "    WHERE  "
									+ "        h.APPL_STAT_CDE = 'PENDING_PYMT_APPR' "
									+ "        AND approved = -1"
									+ "    GROUP BY a.appl_hdr_id "
									+ ") b "
									+ "ON a.appl_hdr_id = b.appl_hdr_id AND a.arpv_seq = b.min_arpv_seq "
									+ "WHERE a.aprv_type_cde = 'BCO_APPL' OR a.aprv_type_cde = 'BCO_PYMT')"
									+ ") "
			+" AND (TO_NUMBER(:startSem) <= TO_NUMBER(NVL(TRIM(hdr.appl_start_term), '0'))  "
			+ "     OR TO_NUMBER(:startSem) <= TO_NUMBER(NVL(TRIM(hdr.appl_end_term), '0'))) "
			+ "AND (TO_NUMBER(:endSem) >= TO_NUMBER(NVL(TRIM(hdr.appl_start_term), '9999'))  "
			+ "     OR TO_NUMBER(:endSem) >= TO_NUMBER(NVL(TRIM(hdr.appl_end_term), '9999'))) "
//			+ "AND hdr.appl_nbr = '0000000320'"
			, nativeQuery = true)
//	List<Map<String,Object>> getTempEnquiryData();
	List<FoApplicationEnquiryRaw> getTempEnquiryData(String applName, String applSchlId, String requesterName, String applNbr, String requesterDept, String applStatCde, String brNo, Timestamp applFrom, Timestamp applToPlusOneMonth, String elTypes
			, String pendingFoSr, String pendingBco,Integer startSem, Integer endSem);
//	List<FoApplicationEnquiryRaw> getTempEnquiryData(String applName, String requesterName, String applNbr);
}
