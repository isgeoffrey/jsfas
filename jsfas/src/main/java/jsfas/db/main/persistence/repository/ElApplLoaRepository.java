package jsfas.db.main.persistence.repository;

import java.util.List;
import java.util.Map;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import jsfas.db.CommonRepository;
import jsfas.db.main.persistence.domain.ElApplLoaDAO;

public interface ElApplLoaRepository extends CommonRepository<ElApplLoaDAO, String> {


	@Query(value = "SELECT * FROM EL_APPL_ACT WHERE appl_hdr_id = :appl_hdr_id ORDER BY action_dttm ", nativeQuery = true)
	List<ElApplLoaDAO> findApprovedById(@Param("appl_hdr_id") String applHdrId);

	@Query(value="SELECT file_id, file_name, file_upld_dttm, al.mod_ctrl_txt as mod_ctrl_txt, file_category "
			+ "FROM el_appl_loa al "
			+ "LEFT JOIN "
			+ "( "
			+ "    SELECT * FROM el_upld_file "
			+ ") uf "
			+ "ON al.file_id = uf.id "
			+ "WHERE appl_hdr_id = :appl_hdr_id "
			+ "ORDER BY file_upld_dttm ", nativeQuery = true)
	List<Map<String, Object>> findFilesByApplHdrId(@Param("appl_hdr_id") String applHdrId);
	
	@Query(value="SELECT * FROM el_appl_loa "
			+ "WHERE appl_hdr_id = :appl_hdr_id", nativeQuery = true)
	List<ElApplLoaDAO> findByApplHdrId(@Param("appl_hdr_id") String applHdrId);
	
	@Query(value="SELECT * FROM el_appl_loa "
			+ "WHERE file_id = :file_id FETCH first 1 row only", nativeQuery = true)
	ElApplLoaDAO findOneByFileId(@Param("file_id") String fileId);
	
	@Query(value="Select DISTINCT "
			+ "    appl.APPL_USER_NAME, "
			+ "    appl.APPL_START_DT, "
			+ "    appl.APPL_END_DT, "
			+ "    el.el_type_nam, "
			+ "    el_appl.el_type_descr, "
			+ "    prgm_descr.ACAD_PLAN_DESCR, "
			+ "    pymt.PYMT_TYPE_CDE, "
			+ "    pymt.PYMT_AMT, "
			+ "    nvl((select yr_term_desc from EL_ACAD_TERM_V term_desc WHERE appl.appl_start_term = term_desc.strm ),' ') start_term, "
			+ "    nvl((select yr_term_desc from EL_ACAD_TERM_V term_desc WHERE appl.appl_end_term = term_desc.strm ),' ') end_term, "
			+ "    nvl((select listagg((crse_cde ||(CASE WHEN section IS NOT NULL AND section <> ' ' AND section <> '-' THEN '-' || section ELSE '' END)), ', ') WITHIN GROUP (ORDER BY appl_hdr_id) FROM el_appl_course where el_appl_course.appl_hdr_id = appl.id),' ') course_list "
			+ "from EL_APPL_HDR appl "
			+ "left join EL_APPL_EL_TYPE el_appl on appl.id =  el_appl.appl_hdr_id "
			+ "left join EL_TYPE_TAB el on el.id = el_appl.EL_TYPE_ID "
			+ "left join EL_APPL_PRGM prgm on prgm.APPL_HDR_ID = el_appl.appl_hdr_id "
			+ "left join EL_ACAD_PLAN_V prgm_descr on prgm_descr.ACAD_PLAN = prgm.PRGM_CDE "
			+ "left join (select * from EL_APPL_PYMT_METHOD where PYMT_CATEGORY = 'SALARY' )pymt on pymt.APPL_HDR_ID = el_appl.appl_hdr_id "
			+ "Where appl.id = :appl_hdr_id",nativeQuery = true)
	List<Map<String, Object>> getFileDetails(@Param("appl_hdr_id") String appl_hdr_id);
	
	@Query(value="Select DISTINCT appl.APPL_USER_NAME,appl.APPL_START_DT,appl.APPL_END_DT, el.el_type_nam, el.el_type_descr, prgm_descr.ACAD_PLAN_DESCR, pymt.PYMT_TYPE_CDE, pymt.PYMT_AMT "
			+ "from EL_APPL_HDR appl "
			+ "left join EL_APPL_EL_TYPE el_appl on appl.id =  el_appl.appl_hdr_id "
			+ "left join EL_TYPE_TAB el on el.id = el_appl.EL_TYPE_ID "
			+ "left join EL_APPL_PRGM prgm on prgm.APPL_HDR_ID = el_appl.appl_hdr_id "
			+ "left join EL_ACAD_PLAN_V prgm_descr on prgm_descr.ACAD_PLAN = prgm.PRGM_CDE "
			+ "left join (select * from EL_APPL_PYMT_METHOD where PYMT_CATEGORY = 'SALARY' )pymt on pymt.APPL_HDR_ID = el_appl.appl_hdr_id "
			+ "Where appl.id = :appl_hdr_id",nativeQuery = true)
	List<Map<String, Object>> getFileDetails_old(@Param("appl_hdr_id") String appl_hdr_id);
}
