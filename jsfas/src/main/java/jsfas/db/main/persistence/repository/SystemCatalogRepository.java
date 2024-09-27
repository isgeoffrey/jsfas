package jsfas.db.main.persistence.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import jsfas.db.main.persistence.domain.SystemCatalogDAO;

public interface SystemCatalogRepository extends JpaRepository<SystemCatalogDAO, String> {

	@Query(value = "SELECT * FROM FAS_SYS_CATG_TAB ", nativeQuery = true)
	List<SystemCatalogDAO> findByMenuGen();
	
}
