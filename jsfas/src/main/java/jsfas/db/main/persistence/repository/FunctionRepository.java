package jsfas.db.main.persistence.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import jsfas.db.main.persistence.domain.FunctionDAO;
import jsfas.db.main.persistence.domain.FunctionDAOPK;

public interface FunctionRepository extends JpaRepository<FunctionDAO, FunctionDAOPK> {

	@Query(value = "SELECT * FROM EL_FUNC_TAB "
			+ " WHERE SYS_CATG_CDE = :sysCatgCode "
			+ " AND FUNC_CATG_CDE = :funcCatgCode "
			+ " AND FUNC_CDE IN (SELECT ACCESS_FUNC_CDE FROM EL_USER_PROFILE_DTL WHERE USER_NAM = :userName)", nativeQuery = true)
	List<FunctionDAO> findBySystemCatgCodeAndFuncCatgCodeAndUser(
			@Param("sysCatgCode") String sysCatgCode,
			@Param("funcCatgCode") String funcCatgCode,
			@Param("userName") String userName);
	
	@Query(value = "SELECT * FROM EL_FUNC_TAB "
			+ " WHERE SYS_CATG_CDE = :sysCatgCode "
			+ " AND FUNC_CATG_CDE = :funcCatgCode ", nativeQuery = true)
	List<FunctionDAO> findBySystemCatgCodeAndFuncCatgCode(
			@Param("sysCatgCode") String sysCatgCode,
			@Param("funcCatgCode") String funcCatgCode);
	
	@Query(value = "SELECT * FROM EL_FUNC_TAB "
			+ " WHERE SYS_CATG_CDE = :sysCatgCode "
			+ " AND FUNC_CATG_CDE = :funcCatgCode "
			+ " AND (MENU_GEN_IND = 'Y' or USER_PROF_GEN_IND = 'Y') "
			+ " AND ((FUNC_CDE,FUNC_SUB_CDE) IN (SELECT ACCESS_FUNC_CDE,ACCESS_FUNC_SUB_CDE FROM EL_USER_PROFILE_DTL WHERE USER_NAM = :userName) "
			+ " or (FUNC_CDE, FUNC_SUB_CDE) IN (SELECT ACCESS_FUNC_CDE,' ' as ACCESS_FUNC_SUB_CDE FROM EL_USER_PROFILE_DTL WHERE USER_NAM = :userName))", nativeQuery = true)
	List<FunctionDAO> findBySystemCatgCodeAndFuncCatgCodeAndUserAndMeunGenOrUserProfGen(
			@Param("sysCatgCode") String sysCatgCode,
			@Param("funcCatgCode") String funcCatgCode,
			@Param("userName") String userName);

	@Query(value = "SELECT * FROM EL_FUNC_TAB "
			+ " WHERE SYS_CATG_CDE = :sysCatCode AND MENU_GEN_IND = 'Y' "
			+ " ORDER BY FUNC_CATG_CDE, FUNC_SEQ_NBR, FUNC_SUB_SEQ_NBR, FUNC_CDE, FUNC_SUB_CDE ", nativeQuery = true)
	List<FunctionDAO> findBySystemCatgCodeForMenu(
			@Param("sysCatCode") String sysCatCode);

	@Query(value = "SELECT * FROM EL_FUNC_TAB WHERE SYS_CATG_CDE = :sysCatgCode "
			+ "AND MENU_GEN_IND = 'Y' "
			+ "AND FUNC_CDE IN (SELECT ACCESS_FUNC_CDE FROEL_USER_PROFILE_DTL WHERE USER_NAM = :userName)", nativeQuery = true)
	List<FunctionDAO> findBySystemCatgCodeAndMenuGenAndUser(
			@Param("sysCatgCode") String sysCatgCode,
			@Param("userName") String userName);
	
	@Query(value = "SELECT * FROM EL_FUNC_TAB WHERE SYS_CATG_CDE = :sysCatgCode "
			+ "AND FUNC_CATG_CDE = :funcCatgCode "
			+ "AND (MENU_GEN_IND = 'Y' or USER_PROF_GEN_IND = 'Y')", nativeQuery = true)
	List<FunctionDAO> findBySystemCatgCodeAndFuncCatgCodeAndMeunGenOrUserProfGen(@Param("sysCatgCode") String sysCatgCode,@Param("funcCatgCode") String funcCatgCode);

}
