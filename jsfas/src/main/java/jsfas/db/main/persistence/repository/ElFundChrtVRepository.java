package jsfas.db.main.persistence.repository;

import java.util.List;
import java.util.Map;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import jsfas.db.CommonRepository;
import jsfas.db.main.persistence.domain.ElFundChrtVDAO;

public interface ElFundChrtVRepository extends CommonRepository<ElFundChrtVDAO, String> {

	@Query(value = ""
			+ "SELECT fund_cde, fund_long_desc FROM el_fund_chrt_v "
			+ "	where "
			+ "(trim(:dept_id) is not null and dept_id = :dept_id and current_yr_budget = 'Y') OR (trim(:project_id) is not null and project_id = :project_id)", nativeQuery = true)
	List<Map<String, Object>> findByDeptIdOrProjId(@Param("dept_id") String deptId, @Param("project_id") String projId);
	
	@Query(value = ""
			+ "SELECT class_cde FROM el_fund_chrt_v "
			+ "    where "
			+ "    project_id = :project_id AND fund_cde = :fund_cde"
			+ "    and trim(class_cde) is not null", nativeQuery = true)
	List<Map<String, Object>> findClassCdeByProjIdAndFundCde(@Param("project_id") String projId, @Param("fund_cde") String fundCde);
}
