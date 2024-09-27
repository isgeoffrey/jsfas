package jsfas.db.main.persistence.repository;

import java.util.List;

import org.springframework.data.jpa.repository.Query;

import jsfas.db.CommonRepository;
import jsfas.db.main.persistence.domain.ElParamTabDAO;
import jsfas.db.main.persistence.domain.ElParamTabDAOPK;

public interface ElParamTabRepository extends CommonRepository<ElParamTabDAO, ElParamTabDAOPK> {

	@Query(value = "SELECT value FROM EL_PARAM_TAB WHERE type = 'SYS' AND program = 'PROVOST_APRV' AND name = 'APRV_ID' ", nativeQuery = true)
	String findProvostAprvId();
	
	@Query(value = "SELECT value FROM EL_PARAM_TAB WHERE type = 'SYS' AND program = 'PROVOST_APRV' AND name = 'APRV_NAME' ", nativeQuery = true)
	String findProvostAprvName();

	@Query(value = "SELECT value FROM EL_PARAM_TAB WHERE type = 'SYS' AND program = 'DAILY_EMAIL' AND name = 'USER_LIST' ", nativeQuery = true)
	String findBatchApprovalUserList();

	@Query(value = "SELECT value FROM EL_PARAM_TAB WHERE type = 'SYS' AND program = 'JIRA' AND name = 'ETAP_88' ", nativeQuery = true)
	String findETAP88Flag();

	@Query(value = "SELECT * FROM EL_PARAM_TAB WHERE type = 'SCH' AND program = 'DAILY_EMAIL' AND name = 'START_TIME' ", nativeQuery = true)
	ElParamTabDAO findBatchApprovalStartTime();

	@Query(value = "SELECT * FROM EL_PARAM_TAB WHERE type = 'SCH' AND program = 'DAILY_EMAIL' AND name = 'END_TIME' ", nativeQuery = true)
	ElParamTabDAO findBatchApprovalEndTime();
	
}
