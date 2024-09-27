package jsfas.db.main.persistence.repository;

import java.sql.Timestamp;
import java.util.List;
import java.util.Map;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import jsfas.common.constants.ApplStatusConstants;
import jsfas.common.constants.PymtStatusConstants;
import jsfas.db.CommonRepository;
import jsfas.db.main.persistence.domain.ElApplHdrHistDAO;
import jsfas.db.main.persistence.domain.ElApplHdrHistDAOPK;

public interface ElApplHdrHistRepository extends CommonRepository<ElApplHdrHistDAO, ElApplHdrHistDAOPK> {
	
	@Query(value = "select nvl(max(version_no), 0) FROM el_appl_hdr_hist where id = :id ", nativeQuery = true)
	int findMaxVersionNoById(@Param("id") String id);
	
}
