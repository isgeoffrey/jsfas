package jsfas.db.main.persistence.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import jsfas.db.main.persistence.domain.FunctionPageDAO;
import jsfas.db.main.persistence.domain.FunctionPageDAOPK;

public interface FunctionPageRepository extends JpaRepository<FunctionPageDAO, FunctionPageDAOPK> {

	@Query(value = "SELECT * FROM EL_FUNC_PAGES_TAB "
			+ " WHERE PAGE_NAM = :pageName ", nativeQuery = true)
	List<FunctionPageDAO> findByPageName(@Param("pageName") String pageName);
	
	@Query(value = "SELECT * FROM EL_FUNC_PAGES_TAB "
			+ " WHERE FUNC_CDE = :funcCode "
			+ " AND NVL(FUNC_SUB_CDE, ' ') = :funcSubCode "
			+ " AND PAGE_NAM = :pageName ", nativeQuery = true)
	List<FunctionPageDAO> findByFuncCdeFuncSubCdePageName(@Param("funcCode") String funcCode, @Param("funcSubCode") String funcSubCode, @Param("pageName") String pageName);
	
	@Query(value = "SELECT * FROM EL_FUNC_PAGES_TAB "
            + " WHERE FUNC_CDE = :funcCode "
            + " AND NVL(FUNC_SUB_CDE, ' ') = :funcSubCode ", nativeQuery = true)
    List<FunctionPageDAO> findByFuncCdeFuncSubCde(@Param("funcCode") String funcCode, @Param("funcSubCode") String funcSubCode);
	
	@Query(value = "SELECT * FROM EL_FUNC_PAGES_TAB "
			+ " WHERE FUNC_CDE = :funcCode "
			+ " AND PAGE_NAM = :pageName ", nativeQuery = true)
	List<FunctionPageDAO> findByFuncCodePageName(@Param("funcCode") String funcCode, @Param("pageName") String pageName);
	
}
