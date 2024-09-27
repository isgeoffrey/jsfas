package jsfas.db.main.persistence.repository;

import java.util.List;
import java.util.Map;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import jsfas.db.CommonRepository;
import jsfas.db.main.persistence.domain.ElTypeTabDAO;

public interface ElTypeTabRepository extends CommonRepository<ElTypeTabDAO, String> {
	
	@Query(value = ""
			+ "SELECT el.* "
			+ ", NVL( (SELECT 1 FROM EL_TYPE_SAL_ELEMNT_TAB a WHERE el.id = a.el_type_id AND a.obsolete = 0 and rownum = 1), 0) has_active_sal_elemnt "
			+ "FROM EL_TYPE_TAB el "
			+ "WHERE obsolete LIKE :obsolete "
			, nativeQuery = true)
	List<Map<String, Object>> searchAll(@Param("obsolete") String obsolete);

}
