package jsfas.db.main.persistence.service;

import java.sql.Timestamp;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;

import jsfas.common.constants.AppConstants;
import jsfas.common.json.ResponseJson;
import jsfas.common.utils.GeneralUtil;
// import jsfas.db.main.persistence.domain.ElEmNotificationDAO;
// import jsfas.db.main.persistence.repository.ElEmNotificationRepository;

public class EmailNotificationEventHandler implements EmailNotificationService{
	
	private final Logger log = LoggerFactory.getLogger(CommonRoutineEventHandler.class);
	
	@Autowired
	private Environment env;
	
	@Autowired
	CommonRoutineService commonRoutineService;
	
	// @Autowired
	// private ElEmNotificationRepository elEmNotificationRepository;
	
	@Override
	public ResponseJson sendPendingEmailNotification(String updateUser, String opPageName) throws Exception {
		ResponseJson responseJson = new ResponseJson();
		
		// ElEmNotificationDAO elEmNotificationDAO = elEmNotificationRepository.findQueuedNotification();
		
		// if(elEmNotificationDAO == null) {
		// 	// No pending email to send
		// 	return responseJson;
		// }
		
		// String emailFrom = GeneralUtil.initBlankString(elEmNotificationDAO.getEmailFrom());
		// String emailFromName = "";
		// String emailTo = GeneralUtil.initBlankString(elEmNotificationDAO.getEmailTo());
		// String emailCc = GeneralUtil.initBlankString(elEmNotificationDAO.getEmailCc());
		// String emailSubject = GeneralUtil.initBlankString(elEmNotificationDAO.getSubject());
		// String emailBody = GeneralUtil.initBlankString(elEmNotificationDAO.getContent());
		
		// if(" ".equals(emailTo)) {
		// 	// empty recipient, stop to send email and update the status
		// 	Timestamp currentTms = GeneralUtil.getCurrentTimestamp();
			
		// 	elEmNotificationDAO.setStatus("TERMINATED");
			
		// 	elEmNotificationDAO.setModCtrlTxt(GeneralUtil.genModCtrlTxt());
		// 	elEmNotificationDAO.setChngUser(updateUser);
		// 	elEmNotificationDAO.setChngDat(currentTms);
		// 	elEmNotificationDAO.setOpPageNam(opPageName);
			
		// 	elEmNotificationRepository.save(elEmNotificationDAO);
			
		// 	return responseJson;
		// }
		
		// if(" ".equals(emailFrom)) {
		// 	// set System default sender 
		// 	emailFromName = "HKUST Extra Load Activities System";
		// 	emailFrom = "noreply@ust.hk";
		// }
		
		// int max_retry = 3;
		
		// // try to send email
		// for (int i = 0; i <= max_retry; i++) {
		// 	try {
		// 		responseJson = commonRoutineService.sendEmail(emailFrom, emailFromName, emailTo, emailCc, emailSubject, emailBody,true);
		// 		log.debug("responseJson: " + responseJson.toString());
		// 		if (responseJson != null && responseJson.getStatus().equalsIgnoreCase(AppConstants.RESPONSE_JSON_SUCCESS_CODE)) {
					
		// 			Timestamp currentTms = GeneralUtil.getCurrentTimestamp();
					
		// 			elEmNotificationDAO.setStatus("SENT");
		// 			elEmNotificationDAO.setRetryCnt(i);
					
		// 			elEmNotificationDAO.setModCtrlTxt(GeneralUtil.genModCtrlTxt());
		// 			elEmNotificationDAO.setChngUser(updateUser);
		// 			elEmNotificationDAO.setChngDat(currentTms);
		// 			elEmNotificationDAO.setOpPageNam(opPageName);
					
		// 			elEmNotificationRepository.save(elEmNotificationDAO);
		// 			break;
		// 		} else if(i == (max_retry)) {
		// 			// already try to send email several time
		// 			Timestamp currentTms = GeneralUtil.getCurrentTimestamp();
					
		// 			elEmNotificationDAO.setStatus("ERROR");
		// 			elEmNotificationDAO.setRetryCnt(i);
					
		// 			elEmNotificationDAO.setModCtrlTxt(GeneralUtil.genModCtrlTxt());
		// 			elEmNotificationDAO.setChngUser(updateUser);
		// 			elEmNotificationDAO.setChngDat(currentTms);
		// 			elEmNotificationDAO.setOpPageNam(opPageName);
					
		// 			elEmNotificationRepository.save(elEmNotificationDAO);
					
		// 		}
		// 	} catch (Exception e) {
		// 		if (i == (max_retry)) {
		// 			log.error("Error here"); /// TBC
					
		// 			Timestamp currentTms = GeneralUtil.getCurrentTimestamp();
					
		// 			elEmNotificationDAO.setStatus("ERROR");
		// 			elEmNotificationDAO.setRetryCnt(i);
					
		// 			elEmNotificationDAO.setModCtrlTxt(GeneralUtil.genModCtrlTxt());
		// 			elEmNotificationDAO.setChngUser(updateUser);
		// 			elEmNotificationDAO.setChngDat(currentTms);
		// 			elEmNotificationDAO.setOpPageNam(opPageName);
					
		// 			elEmNotificationRepository.save(elEmNotificationDAO);
		// 			throw e;
		// 		}
		// 	}
		// }
		
		return responseJson;
	}
	
}
