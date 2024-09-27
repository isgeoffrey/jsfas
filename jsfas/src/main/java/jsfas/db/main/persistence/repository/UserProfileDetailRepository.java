package jsfas.db.main.persistence.repository;

import java.sql.Timestamp;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import jsfas.db.main.persistence.domain.UserProfileDetailDAO;
import jsfas.db.main.persistence.domain.UserProfileDetailDAOPK;

public interface UserProfileDetailRepository extends JpaRepository<UserProfileDetailDAO, UserProfileDetailDAOPK> {
	
	@Query(value = "SELECT * FROM EL_USER_PROFILE_DTL WHERE USER_NAM = :userName "
			+ "AND ACCESS_FUNC_CDE = :functionCode AND ACCESS_FUNC_SUB_CDE LIKE :functionSubCode", nativeQuery = true)
	List<UserProfileDetailDAO> findAuthorized(
			@Param("userName") String userName, 
			@Param("functionCode") String functionCode,
			@Param("functionSubCode") String functionSubCode);
	
	@Query(value = "SELECT * FROM EL_USER_PROFILE_DTL WHERE "
			+ "ACCESS_FUNC_CDE = :functionCode AND ACCESS_FUNC_SUB_CDE LIKE :functionSubCode", nativeQuery = true)
	List<UserProfileDetailDAO> findByFuncCodeAndFuncSubCode(
			@Param("functionCode") String functionCode,
			@Param("functionSubCode") String functionSubCode);
	
	@Query(value = "SELECT * FROM EL_USER_PROFILE_DTL WHERE USER_NAM = :userName ", nativeQuery = true)
	List<UserProfileDetailDAO> findByUserName(
			@Param("userName") String userName); 
	
	@Modifying
	@Query(value = "INSERT INTO EL_USER_PROFILE_DTL "
			+ "SELECT "
			+ "    :userName AS USER_NAM, F.FUNC_CDE AS ACCESS_FUNC_CDE, F.FUNC_SUB_CDE AS ACCESS_FUNC_SUB_CDE, "
			+ "    ' ' AS ACCESS_DATA_FILTER_1_STR, ' ' AS ACCESS_DATA_FILTER_2_STR, ' ' AS ACCESS_DATA_FILTER_3_STR, "
			+ "    :modCtrlTxt AS MOD_CTRL_TXT, :changeUser AS CREAT_USER, :changeDate AS CREAT_DAT, :changeUser AS CHNG_USER, :changeDate AS CHNG_DAT, :opPageName AS OP_PAGE_NAM "
			+ "FROM EL_FUNC_TAB F "
			+ "LEFT OUTER JOIN EL_USER_PROFILE_DTL D "
			+ "ON F.FUNC_CDE = D.ACCESS_FUNC_CDE AND F.FUNC_SUB_CDE = D.ACCESS_FUNC_SUB_CDE AND D.USER_NAM = :userName "
			+ "WHERE F.MENU_GEN_IND = 'Y' AND F.SYS_CATG_CDE = :sysCatCode AND D.USER_NAM is NULL "
			+ "AND F.FUNC_CDE IN (SELECT DISTINCT ACCESS_FUNC_CDE FROM EL_USER_PROFILE_DTL WHERE USER_NAM = :userName) ", nativeQuery = true)
	void insertFirstPages(
			@Param("sysCatCode") String sysCatCode,
			@Param("userName") String userName,
			@Param("changeUser") String changeUser,
			@Param("changeDate") Timestamp changeDate,
			@Param("modCtrlTxt") String modCtrlTxt,
			@Param("opPageName") String opPageName);
}
