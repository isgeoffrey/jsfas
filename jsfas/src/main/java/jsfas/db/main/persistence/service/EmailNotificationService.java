package jsfas.db.main.persistence.service;

import jsfas.common.json.ResponseJson;

public interface EmailNotificationService {
	
	ResponseJson sendPendingEmailNotification(String updateUser, String opPageName) throws Exception;

}
