package jsfas.db.main.persistence.repository;

import java.util.List;
import java.util.Map;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import jsfas.db.CommonRepository;
import jsfas.db.main.persistence.domain.ElInpostStaffImpVDAO;

public interface ElInpostStaffImpVRepository extends CommonRepository<ElInpostStaffImpVDAO, String> {

//	@Query(value = ""
//			+ "SELECT  "
//			+ "    DISTINCT emplid, user_nam, display_nam, TOS, job_catg_cde, nvl(trim(dept_short_nam), nvl(trim(dept_id), ' ')) as dept "
//			+ "FROM EL_INPOST_STAFF_IMP_V  "
//			+ "WHERE "
//			+ "    (UPPER(user_nam) LIKE '%' || UPPER(:search_key) || '%'  "
//			+ "    OR UPPER(display_nam) LIKE '%' || UPPER(:search_key) || '%')"
//			+ "AND primary_job_ind = 'Y' "
//			+ "AND acad_nonacad_ind LIKE :acad_nonacad_ind  ", nativeQuery = true)
//	List<Map<String, Object>> searchAll(@Param("search_key") String searchKey, @Param("acad_nonacad_ind") String acadNonacadInd);
	// 20231228 #1: allow filter search list by acad_nonacad_ind
	
	@Query(value = ""
			+ "WITH added_row_number AS ( "
			+ "SELECT "
			+ "    b.emplid, "
			+ "    b.user_nam, "
			+ "    b.display_nam, "
			+ "    (select TOS from EL_INPOST_STAFF_IMP_V a where a.emplid = b.emplid and a.primary_job_ind = 'Y') TOS, "
			+ "    b.job_catg_cde, "
			+ "    nvl(trim(b.dept_short_nam), nvl(trim(b.dept_id), ' ')) as dept, "
			+ "    nvl(trim(b.dept_id), ' ') as dept_id, "
			+ "    ROW_NUMBER() OVER(PARTITION BY emplid ORDER BY primary_job_ind DESC) row_number "
			+ "FROM EL_INPOST_STAFF_IMP_V b "
			+ "WHERE "
			+ "    (UPPER(b.user_nam) LIKE '%' || UPPER(:search_key) || '%'  "
			+ "    OR UPPER(b.display_nam) LIKE '%' || UPPER(:search_key) || '%') "
			+ "AND b.acad_nonacad_ind = 'A' "
			+ "order by primary_job_ind desc, job_nbr "
			+ ") "
			+ "SELECT * FROM added_row_number WHERE row_number = 1 ", nativeQuery = true)
	List<Map<String, Object>> searchAllApplicant(@Param("search_key") String searchKey);
	// separate the sql for applicant and staff
	
	@Query(value = ""
			+ "WITH added_row_number AS ( "
			+ "SELECT "
			+ "    b.emplid, "
			+ "    b.user_nam, "
			+ "    b.display_nam, "
			+ "    (select TOS from EL_INPOST_STAFF_IMP_V a where a.emplid = b.emplid and a.primary_job_ind = 'Y') TOS, "
			+ "    b.job_catg_cde, "
			+ "    nvl(trim(b.dept_short_nam), nvl(trim(b.dept_id), ' ')) as dept, "
			+ "    ROW_NUMBER() OVER(PARTITION BY emplid ORDER BY primary_job_ind DESC) row_number "
			+ "FROM EL_INPOST_STAFF_IMP_V b "
			+ "WHERE "
			+ "emplid = :emplid "
			+ "AND b.acad_nonacad_ind = 'A' "
			+ "order by primary_job_ind desc, job_nbr "
			+ ") "
			+ "SELECT * FROM added_row_number WHERE row_number = 1 ", nativeQuery = true)
	Map<String, Object> searchApplicantByEmplid(@Param("emplid") String emplid);
	
	@Query(value = ""
			+ "SELECT  "
			+ "    DISTINCT emplid, user_nam, display_nam "
			+ "FROM EL_INPOST_STAFF_IMP_V  "
			+ "WHERE "
			+ "    (UPPER(user_nam) LIKE '%' || UPPER(:search_key) || '%'  "
			+ "    OR UPPER(display_nam) LIKE '%' || UPPER(:search_key) || '%')"
			+ "AND primary_job_ind = 'Y' ", nativeQuery = true)
	List<Map<String, Object>> searchAllStaff(@Param("search_key") String searchKey);
	// separate the sql for applicant and staff
	
	
	@Query(value = ""
			+ "SELECT EMPLID, USER_NAM, DISPLAY_NAM, DEPT_ID, JOB_CATG_CDE FROM EL_INPOST_STAFF_IMP_V "
			+ "WHERE user_nam = :userId AND primary_job_ind LIKE :primary_job_ind AND acad_nonacad_ind LIKE :acad_nonacad_ind "
			+ "order by primary_job_ind desc, job_nbr ", nativeQuery = true)
	List<Map<String, Object>> findByUserIdAndPrimaryIndAndAcadNonAcadInd(@Param("userId") String userId, @Param("primary_job_ind") String primaryJobInd, @Param("acad_nonacad_ind") String acadNonacadInd);
	
	// TBC
	@Query(value = ""
			+ "SELECT EMPLID, USER_NAM, DISPLAY_NAM, DEPT_ID, JOB_CATG_CDE FROM EL_INPOST_STAFF_IMP_V "
			+ "WHERE JOB_CATG_CDE in ("
			+ "'A010', 'A020', 'A030', 'R020', 'I020'"
			+ ") "
			+ "AND primary_job_ind = 'Y' "
			+ "user_nam = :userId ", nativeQuery = true)
	List<Map<String, Object>> findApplicantByUserId(@Param("userId") String userId);
	
	List<ElInpostStaffImpVDAO> findByUserNam(String UserNam);
	
	List<ElInpostStaffImpVDAO> findByEmplid(String Emplid);
	
	@Query(value = "SELECT * FROM (SELECT * FROM el_inpost_staff_imp_v WHERE emplid = :emplid ORDER BY primary_job_ind DESC) WHERE ROWNUM = 1 ", nativeQuery = true)
	ElInpostStaffImpVDAO findPrimaryByEmplid(@Param("emplid") String emplid);
	
	@Query(value = ""
			+ "SELECT * FROM el_inpost_staff_imp_v "
			+ "WHERE emplid = :emplid "
			+ "AND ( "
			+ "job_code = 'D050' OR job_code = 'D055' OR "
			+ "job_code = '090055' OR job_code = '090049' OR job_code = 'A015' OR job_code = '090050' "
			+ ")", nativeQuery = true)
	List<ElInpostStaffImpVDAO> findDeanAndAvpRoleByEmplid(@Param("emplid") String emplid);

	@Query(value = ""
			+ "SELECT * from EL_INPOST_STAFF_IMP_V "
			+ "WHERE (job_code in ('A040','A041','A045','A046','A050')) AND SCHL_CDE in ('SAIS', 'SBM', 'SENG', 'SHSS', 'SSCI')", nativeQuery = true)
	List<ElInpostStaffImpVDAO> findDeptHeadForSchool();
	
	@Query(value = ""
			+ "SELECT * from EL_INPOST_STAFF_IMP_V "
			+ "WHERE (job_code = 'D050' OR job_code = 'D055') AND SCHL_CDE in ('SAIS', 'SBM', 'SENG', 'SHSS', 'SSCI')", nativeQuery = true)
	List<ElInpostStaffImpVDAO> findDeansForSchool();
	
	@Query(value = ""
			+ "SELECT * FROM el_inpost_staff_imp_v "
			+ "WHERE emplid = :emplid "
			+ "AND SCHL_CDE = 'SBM'", nativeQuery = true)
	List<ElInpostStaffImpVDAO> findSBMByEmplid(@Param("emplid") String emplid);
}
