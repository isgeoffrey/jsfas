package jsfas.db.main.persistence.repository;

import java.util.List;

import org.springframework.data.jpa.repository.Query;

import jsfas.db.CommonRepository;
import jsfas.db.main.persistence.domain.ElApplStatTabDAO;

public interface ElApplStatTabRepository extends CommonRepository<ElApplStatTabDAO, String> {

	@Query(value = "SELECT * FROM EL_APPL_STAT_TAB ORDER BY UPPER(APPL_STAT_DESCR) ", nativeQuery = true)
	List<ElApplStatTabDAO> findAllSortByDescr();
}
