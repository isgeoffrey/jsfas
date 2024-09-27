package jsfas.db.main.persistence.repository;

import java.sql.Timestamp;
import java.util.List;
import java.util.Map;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import jsfas.db.CommonRepository;
import jsfas.db.main.persistence.domain.ElJobDataVDAO;
import jsfas.db.main.persistence.domain.ElJobDataVDAOPK;

public interface ElJobDataVRepository extends CommonRepository<ElJobDataVDAO, ElJobDataVDAOPK> {

	@Query(value = ""
			+ "SELECT * FROM EL_JOB_DATA_V "
			+ " WHERE EMPLID = :emplid "
			+ " AND EFFDT = (SELECT max(EFFDT) FROM EL_JOB_DATA_V "
			+ "              WHERE EMPLID = :emplid "
			+ "              AND EFFDT <= :effdt) " , nativeQuery = true)
	ElJobDataVDAO findByEmplidWithClosestEffdt(@Param("emplid") String emplid, @Param("effdt") Timestamp effdt);
}
