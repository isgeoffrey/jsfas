package jsfas.db.main.persistence.repository;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import jsfas.db.CommonRepository;
import jsfas.db.main.persistence.domain.ElApplElTypeDAO;

public interface ElApplElTypeRepository extends CommonRepository<ElApplElTypeDAO, String> {

	ElApplElTypeDAO findByApplHdrIdAndElTypeId(String appplHdrId, String elTypeId);
	
	List<ElApplElTypeDAO> findByApplHdrId(String appplHdrId);

	@Query(value = "SELECT * FROM EL_APPL_EL_TYPE WHERE appl_hdr_id = :applHdrId AND id NOT IN :idList ", nativeQuery = true)
	List<ElApplElTypeDAO> findByApplHdrIdForRemove(@Param("applHdrId") String applHdrId, @Param("idList") List<String> upsertDAOId);
}
