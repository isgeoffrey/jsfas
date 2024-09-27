package jsfas.db.main.persistence.repository;

import org.springframework.data.jpa.repository.Query;

import jsfas.db.CommonRepository;
import jsfas.db.main.persistence.domain.ElSchlChrtVDAO;

import java.util.List;
import java.util.Map;

public interface ElSchlChrtVRepository extends CommonRepository<ElSchlChrtVDAO, String> {
	
	@Query(value = "SELECT DISTINCT SCHL_ID, SCHL_LONG_DESC, SCHL_SHORT_DESC FROM el_schl_chrt_v order by SCHL_LONG_DESC ASC", nativeQuery = true)
	List<Map<String,Object>>findAllDistinctSorted();

}
