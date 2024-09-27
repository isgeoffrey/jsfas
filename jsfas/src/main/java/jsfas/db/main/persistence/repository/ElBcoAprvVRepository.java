package jsfas.db.main.persistence.repository;

import java.util.List;
import java.util.Map;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import jsfas.db.CommonRepository;
import jsfas.db.main.persistence.domain.ElBcoAprvVDAO;
import jsfas.db.main.persistence.domain.ElBcoAprvVDAOPK;

public interface ElBcoAprvVRepository extends CommonRepository<ElBcoAprvVDAO, ElBcoAprvVDAOPK> {

	@Query(value = ""
			+ "SELECT  "
			+ "     user_id, user_name "
			+ "FROM EL_BCO_APRV_V "
			+ "WHERE "
			+ "    basic_id = :proj_id "
			+ "    AND dept_proj_ind = 'P' "
			+ "    AND business_unit = 'HKUST' " , nativeQuery = true)
	List<Map<String, Object>> findByProjId(@Param("proj_id") String projId);

	@Query(value = ""
			+ "SELECT  "
			+ "     user_id, user_name "
			+ "FROM EL_BCO_APRV_V "
			+ "WHERE "
			+ "    basic_id = :id "
			+ "    AND dept_proj_ind = :id_type "
			+ "    AND business_unit = 'HKUST' " , nativeQuery = true)
	List<Map<String, Object>> findByIdandType(@Param("id") String id, @Param("id_type") String idType);
	
	@Query(value = ""
			+ "SELECT * FROM EL_BCO_APRV_V  "
			+ "WHERE DEPT_PROJ_IND = 'D' "
			+ "AND FUND_CODE = '#' AND ACCOUNT = '#' "
			+ "AND BUSINESS_UNIT = 'HKUST' "
			+ "AND BASIC_ID = :dept_id "
			+ "AND USER_ID = :user_id " , nativeQuery = true)
	List<ElBcoAprvVDAO> findDeptHeadApprvRightByDeptIdAndUserId(@Param("dept_id") String deptId, @Param("user_id") String userId);
	
	@Query(value = ""
			+ "SELECT * FROM EL_BCO_APRV_V  "
			+ "WHERE DEPT_PROJ_IND = 'D' "
			+ "AND FUND_CODE = '#' AND ACCOUNT = '#' "
			+ "AND BUSINESS_UNIT = 'HKUST' "
			+ "AND BCO_IND = 'Y' "
			+ "AND USER_ID = :user_id " , nativeQuery = true)
	List<ElBcoAprvVDAO> findDeptHeadApprvRightByUserId(@Param("user_id") String userId);
	
	@Query(value = ""
			+ "SELECT * FROM ( "
			+ "    SELECT * FROM EL_BCO_APRV_V  "
			+ "			WHERE DEPT_PROJ_IND = 'D' "
			+ "			AND FUND_CODE = '#' AND ACCOUNT = '#' "
			+ "			AND BUSINESS_UNIT = 'HKUST' "
			+ "            AND BASIC_ID = :deptid "
			+ "            ORDER BY BCO_IND DESC, MAX_BR_AMT DESC "
			+ "            ) "
			+ "WHERE ROWNUM = 1 " , nativeQuery = true)
	ElBcoAprvVDAO findDefaultDeptHeadByDeptId(@Param("deptid") String deptid);
}
