package jsfas.db.main.persistence.repository;

import java.util.List;
import java.util.Map;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import jsfas.common.constants.AprvTypeConstants;
import jsfas.db.CommonRepository;
import jsfas.db.main.persistence.domain.ElApplBudgetDAO;

public interface ElApplBudgetRepository extends CommonRepository<ElApplBudgetDAO, String> {

	@Query(value = "SELECT * FROM EL_APPL_BUDGET WHERE appl_hdr_id = :applHdrId AND id NOT IN :idList ", nativeQuery = true)
	List<ElApplBudgetDAO> findByApplHdrIdForRemove(@Param("applHdrId") String applHdrId, @Param("idList") List<String> upsertDAOId);

	@Query( value = ""
			+ "SELECT "
			+ "    NVL(e.el_type_id, ' ') el_type_id , "
			+ "    e.el_type_descr, "
			+ "    e.pymt_amt, "
			+ "    e.pmt_currency,"
			+ "    b.id el_appl_budget_id, "
			+ "    b.acct_cde, "
			+ "    b.analysis_cde, "
			+ "    b.fund_cde, "
			+ "    b.proj_id, "
			+ "    b.proj_nbr, "
			+ "    b.class, "
			+ "    b.bco_aprv_id, "
			+ "    b.bco_aprv_name, "
			+ "    b.budg_acct_share, "
			+ "    b.budg_acct_amt, "
			+ "    b.mod_ctrl_txt "
			+ "FROM "
			+ "    el_appl_budget b "
			+ "    LEFT JOIN el_appl_el_type e ON b.appl_el_type_id = e.id "
			+ "WHERE "
			+ "    b.appl_hdr_id = :appl_hdr_id", nativeQuery = true)
	List<Map<String, Object>> findDetailsByApplHdrId(@Param("appl_hdr_id") String applHdrId);
	
	List<ElApplBudgetDAO> findByApplHdrId(String applHdrId);
	
	@Query(value = "SELECT distinct bco_aprv_id, bco_aprv_name FROM el_appl_pymt_schedule "
			+ "WHERE appl_hdr_id = :appl_hdr_id "
			+ "AND bco_aprv_id NOT IN (SELECT aprv_user_id FROM el_appl_aprv_status where appl_hdr_id = :appl_hdr_id and aprv_type_cde = '" + AprvTypeConstants.BCO_APPL + "' )", nativeQuery = true)
	List<Map<String, String>> findBcoAprvNotInElApplAprvStatus(@Param("appl_hdr_id") String applHdrId);
}
