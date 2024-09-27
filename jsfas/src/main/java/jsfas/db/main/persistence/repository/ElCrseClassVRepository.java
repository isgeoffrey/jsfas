package jsfas.db.main.persistence.repository;

import java.util.List;
import java.util.Map;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import jsfas.db.CommonRepository;
import jsfas.db.main.persistence.domain.ElCrseClassVDAO;
import jsfas.db.main.persistence.domain.ElCrseClassVDAOPK;

public interface ElCrseClassVRepository extends CommonRepository<ElCrseClassVDAO, ElCrseClassVDAOPK>{

	@Query(value = ""
			+ "SELECT  "
			+ "    t4.strm, t4.crse_id, t4.subject || t4.catalog_nbr as crse_cde, t4.crse_title, t4.class_section, t4.units_acad_prog "
			+ "	   , NVL(t3.colist_crse_ids, ' ')  colist_crse_ids "
			+ "	   , term_v.yr_term_desc "
			+ "FROM el_crse_class_v t4 "
			+ "LEFT JOIN ( "
			+ "    SELECT  "
			+ "        t1.strm, t1.crse_id, LISTAGG(distinct t2.crse_id, '; ') WITHIN GROUP (order by t2.crse_id) colist_crse_ids "
			+ "    FROM el_crse_colist_v t1 "
			+ "    LEFT JOIN ( "
			+ "        SELECT  "
			// 20231229 #32 : fix to get colist course id; only return course with class in the semester
			+ "            colist.STRM, colist.CRSE_GRP, colist.crse_id "
			+ "        FROM el_crse_colist_v colist "
			+ "        INNER JOIN el_crse_class_v class on colist.crse_id = class.crse_id and colist.strm = class.strm "
			+ "    ) t2 ON t1.STRM = t2.STRM AND t1.CRSE_GRP = t2.CRSE_GRP AND t1.crse_id <> t2.crse_id "
			+ "    GROUP BY t1.strm, t1.crse_id "
			+ ") t3 ON t4.strm = t3.strm AND t4.crse_id = t3.crse_id "
			+ "LEFT JOIN EL_ACAD_TERM_V term_v ON t4.strm = term_v.strm "
			+ "WHERE t4.strm BETWEEN :start_strm AND :end_strm ", nativeQuery = true)
	List<Map<String, Object>> findCourseDetailsByStartStrmAndEndStrm(@Param("start_strm") String startStrm, @Param("end_strm") String endStrm);
	
	@Query(value = ""
			+ "select distinct crse_id, subject || catalog_nbr as crse_cde, crse_title, crse_offer_nbr, class_session_code "
			+ "from el_crse_class_v "
			+ "where strm = :strm "
			+ "and crse_id = :crse_id  ", nativeQuery = true)
	List<Map<String, Object>> findCourseInfoByStrmAndCrseId(@Param("strm") String strm, @Param("crse_id") String crseId);
	
	@Query(value = ""
			+ "SELECT  "
			+ "    t4.strm, t4.crse_id, t4.subject || t4.catalog_nbr as crse_cde, t4.class_section, t4.units_acad_prog "
			+ "	   , term_v.yr_term_desc "
			+ "FROM el_crse_class_v t4 "
			+ "LEFT JOIN EL_ACAD_TERM_V term_v ON t4.strm = term_v.strm "
			+ "WHERE t4.strm = :start_strm "
			+ "order by t4.subject || t4.catalog_nbr ", nativeQuery = true)
	List<Map<String, Object>> findCourseDetailsByStrm(@Param("start_strm") String startStrm);
	
	@Query(value = ""
			+ "SELECT DISTINCT "
			+ "    :start_strm, t4.crse_id, t4.subject || t4.catalog_nbr as crse_cde "
			+ "FROM el_crse_class_v t4 "
			+ "LEFT JOIN EL_ACAD_TERM_V term_v ON t4.strm = term_v.strm "
			+ "WHERE t4.strm >= (:start_strm - 200) "
			+ "order by t4.subject || t4.catalog_nbr ", nativeQuery = true)
	List<Map<String, Object>> findPreviousCourseDetailsByStrm(@Param("start_strm") String startStrm);
	
	@Query(value = ""
			+ "select distinct crse_id, subject || catalog_nbr as crse_cde, crse_title "
			+ "from el_crse_class_v "
			+ "where strm >= (:strm - 200) "
			+ "and crse_id = :crse_id "
			+ "and subject || catalog_nbr = :crse_cde ", nativeQuery = true)
	List<Map<String, Object>> findPreviousCourseInfoByStrmAndCrseIdAndCrseCode(@Param("strm") String strm, @Param("crse_id") String crseId, @Param("crse_cde") String crse_cde);
	
	@Query(value = ""
			+ "    SELECT  "
			+ "        t1.strm, t1.crse_id, NVL(LISTAGG(distinct t2.crse_id, '; ') WITHIN GROUP (order by t2.crse_id), ' ') colist_crse_ids "
			+ "    FROM el_crse_colist_v t1 "
			+ "    LEFT JOIN ( "
			+ "        SELECT  "
			// 20231229 #32 : fix to get colist course id; only return course with class in the semester
			+ "            colist.STRM, colist.CRSE_GRP, colist.crse_id "
			+ "        FROM el_crse_colist_v colist "
			+ "        INNER JOIN el_crse_class_v class on colist.crse_id = class.crse_id and colist.strm = class.strm "
			+ "    ) t2 ON t1.STRM = t2.STRM AND t1.CRSE_GRP = t2.CRSE_GRP AND t1.crse_id <> t2.crse_id "
			+ "	   WHERE t1.strm = :strm and t1.crse_id = :crse_id "
			+ "    GROUP BY t1.strm, t1.crse_id "
			+ "", nativeQuery = true)
	Map<String, Object> findCoCouseByStrmAndCrseId(@Param("strm") String strm, @Param("crse_id") String crseId);
}
