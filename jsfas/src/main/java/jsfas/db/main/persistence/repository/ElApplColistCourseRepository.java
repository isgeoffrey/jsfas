package jsfas.db.main.persistence.repository;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import jsfas.db.CommonRepository;
import jsfas.db.main.persistence.domain.ElApplColistCourseDAO;

public interface ElApplColistCourseRepository extends CommonRepository<ElApplColistCourseDAO, String> {

	@Query(value = "SELECT * FROM EL_APPL_COLIST_COURSE WHERE appl_hdr_id = :applHdrId AND id NOT IN :idList ", nativeQuery = true)
	List<ElApplColistCourseDAO> findByApplHdrIdForRemove(@Param("applHdrId") String applHdrId,@Param("idList") List<String> idList);
	
	List<ElApplColistCourseDAO> findByApplCrseId(String applCrseId);
	
	List<ElApplColistCourseDAO> findByApplHdrId(String applCrseId);
}
