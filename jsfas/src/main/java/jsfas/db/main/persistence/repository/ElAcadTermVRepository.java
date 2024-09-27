package jsfas.db.main.persistence.repository;

import java.util.List;
import java.util.Map;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import jsfas.db.CommonRepository;
import jsfas.db.main.persistence.domain.ElAcadTermVDAO;

public interface ElAcadTermVRepository extends CommonRepository<ElAcadTermVDAO, String> {
	
	@Query(value="select DISTINCT acad_yr, acad_yr_desc from el_acad_term_v order by acad_yr" , nativeQuery = true)
	List<Map<String, Object>> getAllAcadYr();
	
	@Query(value=""
			+ "select * from el_acad_term_v "
			+ "where TRUNC(sysdate) >= term_begin_dat AND TRUNC(sysdate) <= ssr_trmac_last_dat " , nativeQuery = true)
	ElAcadTermVDAO getCurrentTerm();
	
	@Query(value = "SELECT * FROM EL_ACAD_TERM_V where strm between :start_strm and :end_strm ", nativeQuery = true)
	List<ElAcadTermVDAO> findWithinStartAndEndTerm(@Param("start_strm") String startTerm, @Param("end_strm") String endTerm);
		
	// ETAP-118 set start end date for future term
	@Query(value = ""
			+ "SELECT MIN(NVL(term_begin_dat, TO_DATE('01-SEP-' || (acad_yr)))) min_start, MAX(NVL(term_end_dat, TO_DATE('31-AUG-' || (acad_yr + 1)))) max_end "
			+ "FROM EL_ACAD_TERM_V "
			+ "WHERE "
			+ "strm between :start_strm AND :end_strm "
			+ "", nativeQuery = true)
	Map<String, Object> getTermStartAndEndDat(@Param("start_strm") String startTerm, @Param("end_strm") String endTerm);
}
