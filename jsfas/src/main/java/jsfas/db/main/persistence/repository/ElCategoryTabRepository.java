package jsfas.db.main.persistence.repository;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import jsfas.db.CommonRepository;
import jsfas.db.main.persistence.domain.ElCategoryTabDAO;

public interface ElCategoryTabRepository extends CommonRepository<ElCategoryTabDAO, String> {

	@Query(value = "SELECT * FROM EL_CATEGORY_TAB WHERE obsolete LIKE :obsolete ", nativeQuery = true)
	List<ElCategoryTabDAO> findByObsolete(@Param("obsolete") String obsolete);
}
