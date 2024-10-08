package jsfas.db.main.persistence.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.sql.Timestamp;
import java.util.List;
import java.util.Map;

import jsfas.db.CommonRepository;
import jsfas.db.main.persistence.domain.FasStkPlanDtlStgDAO;
import jsfas.db.main.persistence.domain.FasStkPlanDtlStgDAOPK;

public interface FasStkPlanDtlStgRepository extends CommonRepository<FasStkPlanDtlStgDAO, FasStkPlanDtlStgDAOPK> {

	@Query(value="SELECT * FROM fas_stk_plan_dtl_stg where stk_plan_id = :planID", nativeQuery = true)
	List<FasStkPlanDtlStgDAO> findAllDtlFromPlanId(String planID);

	
	@Query(value="SELECT COUNT(*) AS pending " +
				"FROM fas_stk_plan_dtl dtl " +
				"JOIN fas_stk_plan_dtl_stg stg " + 
				"ON stg.stk_plan_id = dtl.stk_plan_id " + 
				"AND stg.business_unit = dtl.business_unit " + 
				"AND stg.asset_id = dtl.asset_id " + 
				"WHERE dtl.stk_plan_id = :planID " + 
				"AND stg.stk_status != dtl.stk_status " + 
				"AND NVL(TRIM(stg.stk_status), ' ') != ' '", nativeQuery = true)
	Map<String,Object> findPendingStagingById(String planID);

	
}
