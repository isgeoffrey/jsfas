package jsfas.db.main.persistence.repository;

import org.springframework.data.jpa.repository.Query;

import jsfas.db.CommonRepository;
import jsfas.db.main.persistence.domain.ElEmNotificationDAO;

public interface ElEmNotificationRepository extends CommonRepository<ElEmNotificationDAO, String> {

	@Query(value = "SELECT * FROM EL_EM_NOTIFICATION WHERE STATUS='pending' AND ROWNUM <= 1 ORDER BY CREAT_DAT", nativeQuery = true)
	ElEmNotificationDAO findQueuedNotification();
}
