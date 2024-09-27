package jsfas.db.main.persistence.repository;

import java.util.List;
import java.util.Map;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import jsfas.db.CommonRepository;
import jsfas.db.main.persistence.domain.ElAcadPlanVDAO;

public interface ElAcadPlanVRepository extends CommonRepository<ElAcadPlanVDAO, String> {
	@Query(value = ""
			+ "SELECT ACAD_PLAN , ACAD_PLAN_DESCR , ACAD_ORG, ACAD_ORG_DESCR_SHORT, ACAD_GROUP FROM EL_ACAD_PLAN_V "
			+ "    WHERE UPPER(ACAD_PLAN_DESCR) LIKE '%' || UPPER(:seach_key) || '%'", nativeQuery = true)
	List<Map<String, Object>> searchPrgm(@Param("seach_key") String searchKey);
	
	ElAcadPlanVDAO findByAcadPlanAndAcadOrgAndAcadGroup(String acadPlan, String acadOrg, String acadGroup);
}
