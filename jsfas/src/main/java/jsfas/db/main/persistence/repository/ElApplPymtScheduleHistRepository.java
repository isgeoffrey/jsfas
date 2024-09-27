package jsfas.db.main.persistence.repository;

import java.util.List;
import java.util.Map;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import jsfas.common.constants.AprvTypeConstants;
import jsfas.db.CommonRepository;
import jsfas.db.main.persistence.domain.ElApplPymtScheduleHistDAO;

public interface ElApplPymtScheduleHistRepository extends CommonRepository<ElApplPymtScheduleHistDAO, String> {

	@Query(value = ""
			+ "SELECT  "
			+ "    proj_id, dept_id, fund_cde, class_cde, acct_cde, analysis_cde, sum(pymt_line_amt) pymt_sum "
			+ "FROM EL_APPL_PYMT_SCHEDULE_HIST  "
			+ "WHERE  "
			+ "    appl_hdr_id = :appl_hdr_id "
			+ "    AND version_no = (SELECT MAX(version_no) FROM EL_APPL_PYMT_SCHEDULE_HIST where appl_hdr_id = :appl_hdr_id) "
			+ "GROUP BY proj_id, dept_id, fund_cde, class_cde, acct_cde, analysis_cde", nativeQuery = true)
	List<Map<String, Object>> findLatestApprovedPymtGroupByCoa(@Param("appl_hdr_id") String applHdrId);
}
