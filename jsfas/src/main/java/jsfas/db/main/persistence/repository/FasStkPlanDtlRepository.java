package jsfas.db.main.persistence.repository;

import java.sql.Timestamp;
import java.util.List;
import java.util.Map;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import jsfas.db.CommonRepository;
import jsfas.db.main.persistence.domain.FasStkPlanDtlDAO;
import jsfas.db.main.persistence.domain.FasStkPlanDtlDAOPK;
import jsfas.db.main.persistence.domain.FasStkPlanDtlStgDAO;


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

	@Query(value="SELECT dtl.*,stg.stk_status as stg_stk_status "+
		"FROM fas_stk_plan_dtl dtl "+
		"join fas_stk_plan_dtl_stg stg "+
		"on stg.stk_plan_id  = dtl.stk_plan_id "+
		"and stg.business_unit = dtl.business_unit "+
		"and stg.asset_id = dtl.asset_id "+
		"where dtl.stk_plan_id = :planID "+
		// "and stg.stk_status != dtl.stk_status "+
		"and NVL(TRIM(stg.stk_status),' ') != ' '", nativeQuery = true)
	List<Map<String,Object>> findDtlAndStagingOverlapFromPlanId(String planID);

	@Query(value="Select dtl.stk_plan_id, " +
		"dtl.profile_id, " +
		"dtl.profile_descr, " +
		"dtl.business_unit, " +
		"dtl.asset_id, " +
		"dtl.asset_descr_long, " +
		"dtl.total_cost, " +
		"dtl.nbv, " +
		"dtl.voucher_id, " +
		"dtl.invoice_id, " +
		"dtl.invoice_dt, " +
		"dtl.po_id, " +
		"dtl.region_name, " +
		"dtl.not_ust_proprty, " +
		"dtl.donation_flag, " +
		"dtl.location, " +
		"stg.stk_status as stk_status, " +
		"dtl.mod_ctrl_txt, " +
		"dtl.creat_user, " +
		"dtl.creat_dat, " +
		"dtl.chng_user, " +
		"dtl.chng_dat, " +
		"dtl.op_page_nam " +
		"FROM fas_stk_plan_dtl dtl " +
		"join fas_stk_plan_dtl_stg stg " +
		"on stg.stk_plan_id  = dtl.stk_plan_id " +
		"and stg.business_unit = dtl.business_unit " +
		"and stg.asset_id = dtl.asset_id " +
		"where dtl.stk_plan_id = :planID " +
		"and NVL(TRIM(stg.stk_status),' ') != ' '",nativeQuery = true)
	List<FasStkPlanDtlDAO> coerceStgToDtlData(String planID);

}
