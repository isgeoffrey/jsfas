package jsfas.db.main.persistence.repository;

import java.util.List;
import java.util.Map;
import java.sql.Timestamp;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import jsfas.db.CommonRepository;
import jsfas.db.main.persistence.domain.FasStkPlanHdrDAO;

public interface FasStkPlanHdrRepostiory extends CommonRepository<FasStkPlanHdrDAO, String> {

	// @Query(value="SELECT stk_hdr.* "+
	// 	"FROM fas_stk_plan_hdr stk_hdr "+
	// 	"LEFT JOIN fas_user_func_v user_role "+
	// 	"ON stk_hdr.CUST_DEPTID = user_role.CUST_DEPTID "+
	// 	"WHERE user_role.OPRID = :OPRID "+
	// 	"AND (user_role.ZR_FO_SUPERUSER = 'Y' OR user_role.CUST_DEPTID IS NOT NULL) "+
	// 	"AND stk_hdr.stk_plan_id LIKE :stkPlanID "+
	// 	"AND stk_hdr.cust_deptid LIKE :deptID "+
	// 	"AND stk_hdr.stk_plan_status LIKE :status "+
	// 	"AND (stk_hdr.creat_dat >= :creatFrom OR :creatFrom IS NULL) "+
	// 	"AND (stk_hdr.creat_dat <= :creatTo OR :creatTo IS NULL) ",
	// 	nativeQuery = true)
	@Query(value="SELECT * "+
		"FROM fas_stk_plan_hdr  ",
		nativeQuery = true)
	List<FasStkPlanHdrDAO> findStocktakeHdrByUserId(@Param("OPRID") String OPRID, @Param("stkPlanID") String stkPlanID, @Param("deptID") String deptID, @Param("status") String status, @Param("creatFrom") Timestamp creatFrom, @Param("creatTo") Timestamp creatTo);
    
}
