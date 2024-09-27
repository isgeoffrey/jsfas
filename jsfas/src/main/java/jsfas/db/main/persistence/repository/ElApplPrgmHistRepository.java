package jsfas.db.main.persistence.repository;

import java.sql.Timestamp;
import java.util.List;
import java.util.Map;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import jsfas.common.constants.ApplStatusConstants;
import jsfas.common.constants.PymtStatusConstants;
import jsfas.db.CommonRepository;
import jsfas.db.main.persistence.domain.ElApplPrgmHistDAO;

public interface ElApplPrgmHistRepository extends CommonRepository<ElApplPrgmHistDAO, String> {
	
	
}
