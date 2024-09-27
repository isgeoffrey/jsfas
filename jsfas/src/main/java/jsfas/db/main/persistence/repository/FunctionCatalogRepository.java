package jsfas.db.main.persistence.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import jsfas.db.main.persistence.domain.FunctionCatalogDAO;
import jsfas.db.main.persistence.domain.FunctionCatalogDAOPK;

public interface FunctionCatalogRepository extends JpaRepository<FunctionCatalogDAO, FunctionCatalogDAOPK> {
	@Query(value = "SELECT * FROM FAS_FUNC_CATG_TAB WHERE SYS_CATG_CDE = :sysCatgCode "
			+ " AND FUNC_CATG_CDE = :funcCatgCode ", nativeQuery = true)
	List<FunctionCatalogDAO> findBySystemCatgCodeAndFuncCatgCode(@Param("sysCatgCode") String sysCatgCode,@Param("funcCatgCode") String funcCatgCode);
	
	@Query(value = "SELECT * FROM FAS_FUNC_CATG_TAB WHERE SYS_CATG_CDE = :sysCatgCode "
			+ " ORDER BY FUNC_CATG_SEQ_NBR, FUNC_CATG_CDE ", nativeQuery = true)
	List<FunctionCatalogDAO> findBySystemCatgCode(@Param("sysCatgCode") String sysCatgCode);
}
