package jsfas.db.main.persistence.repository;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import jsfas.db.CommonRepository;
import jsfas.db.main.persistence.domain.ElApplCourseDAO;

public interface ElApplCourseRepository extends CommonRepository<ElApplCourseDAO, String> {

	@Query(value = "SELECT * FROM EL_APPL_COURSE WHERE appl_hdr_id = :applHdrId AND id NOT IN :idList ", nativeQuery = true)
	List<ElApplCourseDAO> findByApplHdrIdForRemove(@Param("applHdrId") String applHdrId, @Param("idList") List<String> idList);
	
	List<ElApplCourseDAO> findByApplHdrId(String applHdrId);
	
	@Query(value = "SELECT * FROM EL_APPL_COURSE WHERE appl_hdr_id = :applHdrId AND credit = -1 ", nativeQuery = true)
	List<ElApplCourseDAO> findByApplHdrIdForCourseNotInSIS(@Param("applHdrId") String applHdrId);
}
