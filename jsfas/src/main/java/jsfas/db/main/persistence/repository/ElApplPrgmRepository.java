package jsfas.db.main.persistence.repository;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import jsfas.db.CommonRepository;
import jsfas.db.main.persistence.domain.ElApplPrgmDAO;

public interface ElApplPrgmRepository extends CommonRepository<ElApplPrgmDAO, String> {

	ElApplPrgmDAO findByApplHdrId(String applHdrId);
	
	@Query(value = "SELECT * FROM EL_APPL_PRGM WHERE appl_hdr_id = :appl_hdr_id AND id != :id ", nativeQuery = true)
	List<ElApplPrgmDAO> findByApplHdrIdAndId(@Param("appl_hdr_id") String applHdrId, @Param("id") String id);
}
