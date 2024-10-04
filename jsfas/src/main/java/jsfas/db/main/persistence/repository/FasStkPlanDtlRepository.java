package jsfas.db.main.persistence.repository;

import java.sql.Timestamp;
import java.util.List;
import java.util.Map;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import jsfas.db.CommonRepository;
import jsfas.db.main.persistence.domain.FasStkPlanDtlDAO;
import jsfas.db.main.persistence.domain.FasStkPlanDtlDAOPK;


public interface FasStkPlanDtlRepository extends CommonRepository<FasStkPlanDtlDAO, FasStkPlanDtlDAOPK> {

	@Query(value="SELECT "+
		"COUNT(*) AS total, "+
		"SUM(CASE "+
		"WHEN NVL(stk_status,'') = '' or NVL(stk_status,'') = ' ' THEN 1 ELSE 0 "+
		" END) AS pending, "+
		"SUM(CASE "+
		"WHEN stk_status <> ' ' THEN 1 ELSE 0 "+
		"END) AS updated "+
		"FROM fas_stk_plan_dtl "+
		"WHERE stk_plan_id = :planID",
		nativeQuery = true)
	// List<Map<String,Object>> findSumamryByPlanId(String planID);
	Map<String,Object> findSumamryByPlanId(String planID);

	@Query(value="SELECT * FROM fas_stk_plan_dtl where stk_plan_id = :planID", nativeQuery = true)
	List<FasStkPlanDtlDAO> findAllDtlFromPlanId(String planID);
}
