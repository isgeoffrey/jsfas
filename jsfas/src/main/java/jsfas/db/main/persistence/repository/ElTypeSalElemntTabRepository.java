package jsfas.db.main.persistence.repository;

import java.util.List;
import java.util.Map;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import jsfas.db.CommonRepository;
import jsfas.db.main.persistence.domain.ElTypeSalElemntTabDAO;

public interface ElTypeSalElemntTabRepository extends CommonRepository<ElTypeSalElemntTabDAO, String> {
	
	@Query(value = ""
			+ "SELECT el.category_cde , el.EL_TYPE_NAM, el.EL_TYPE_DESCR, sal.* FROM EL_TYPE_SAL_ELEMNT_TAB sal "
			+ "INNER JOIN EL_TYPE_TAB el ON sal.EL_TYPE_ID = el.ID "
			+ "WHERE"
			+ "		el.ID LIKE :elTypeID "
			+ "		AND el.category_cde LIKE :categoryCde "
			+ "		AND el.obsolete = 0 "
			+ "		AND sal.obsolete LIKE :obsolete ", nativeQuery = true)
	List<Map<String, String>> searchAll(@Param("elTypeID") String elTypeID, @Param("categoryCde") String categoryCde, @Param("obsolete") String obsolete);
	
	List<ElTypeSalElemntTabDAO> findByElTypeIdAndObsolete(String elTypeId, Integer obsolete);
	
	@Query(value = "SELECT * FROM EL_TYPE_SAL_ELEMNT_TAB WHERE el_type_id = :elTypeId AND id NOT IN :salElemntIdList AND obsolete = 0 ", nativeQuery = true)
	List<ElTypeSalElemntTabDAO> findForObsolete(@Param("elTypeId") String elTypeID, @Param("salElemntIdList") List<String> salElemntIdList);

	ElTypeSalElemntTabDAO findByElTypeIdAndPymtTypeCdeAndObsolete(String elTypeId, String pymtTypeCde, Integer Obsolete);
}
