package jsfas.db.main.persistence.repository;

import java.util.List;
import java.util.Map;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import jsfas.db.CommonRepository;
import jsfas.db.main.persistence.domain.ElApplPymtMethodHistDAO;

public interface ElApplPymtMethodHistRepository extends CommonRepository<ElApplPymtMethodHistDAO, String> {

	@Query(value = "select max(version_no) FROM el_appl_pymt_method_hist where appl_hdr_id = :applHdrId ", nativeQuery = true)
	int findMaxVersionNoByApplId(@Param("applHdrId") String applHdrId);
	

	@Query(value = ""
			+ "SELECT "
			+ "    m.id el_appl_pymt_method_id, "
			+ "    m.pymt_type_cde, "
			+ "    m.pymt_amt, "
			+ "    m.mod_ctrl_txt, "
			+ "    NVL(s.id, ' ') el_appl_pymt_schedule_id, "
			+ "    s.appl_pymt_method_id, "
			+ "    s.pymt_sched_no, "
			+ "    s.pymt_start_dt, "
			+ "    s.pymt_end_dt, "
			+ "    s.pymt_sched_line, "
			+ "    NVL(s.proj_id, ' ') AS proj_id, "
			+ "    NVL(s.proj_nbr, ' ') AS proj_nbr, "
			+ "    NVL(s.dept_id, ' ') AS dept_id, "
			+ "    NVL(s.fund_cde, ' ') AS fund_cde, "
			+ "    NVL(s.class_cde, ' ') AS class_cde, "
			+ "    NVL(s.acct_cde, ' ') AS acct_cde, "
			+ "    NVL(s.analysis_cde, ' ') AS analysis_cde, "
			+ "    NVL(s.bco_aprv_id, ' ') AS bco_aprv_id, "
			+ "    NVL(s.bco_aprv_name, ' ') AS bco_aprv_name, "
			+ "    s.pymt_line_amt     AS schedule_pymt_amt, "
			+ "    s.pymt_submit_dttm, "
			+ "    s.pymt_status_cde, "
			+ "    s.mod_ctrl_txt AS schedule_mod_ctrl_txt, "
			+ "    e.el_type_id "
			+ "FROM "
			+ "    el_appl_pymt_method_hist m "
			+ "    LEFT JOIN el_appl_pymt_schedule_hist s ON m.id = s.appl_pymt_method_id "
			+ "    LEFT JOIN el_appl_el_type_hist       e ON m.appl_el_type_id = e.id "
			+ "WHERE "
			+ "    m.appl_hdr_id = :appl_hdr_id"
			+ "    AND m.pymt_category = 'SALARY' "
			+ "    AND m.version_no = (SELECT MAX(version_no) FROM el_appl_pymt_method_hist WHERE appl_hdr_id = :appl_hdr_id AND pymt_category = 'SALARY') "
			+ "ORDER BY s.pymt_sched_no, s.pymt_sched_line ", nativeQuery = true)
	List<Map<String, Object>> findDetailsByApplHdrId(@Param("appl_hdr_id") String applHdrId);
}
