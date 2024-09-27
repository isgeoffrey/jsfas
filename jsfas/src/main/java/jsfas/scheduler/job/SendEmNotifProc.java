package jsfas.scheduler.job;

import org.quartz.DisallowConcurrentExecution;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.PersistJobDataAfterExecution;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.scheduling.quartz.QuartzJobBean;

import jsfas.common.constants.AppConstants;
import jsfas.db.main.persistence.service.EmailNotificationService;

@PersistJobDataAfterExecution
@DisallowConcurrentExecution
public class SendEmNotifProc extends QuartzJobBean {
	private final Logger log = LoggerFactory.getLogger(this.getClass());

	@Autowired
	private Environment env;
	
	@Autowired
	EmailNotificationService emailNotificationService;
	
	final String job_name = "send email notification process";
	
	@Override
	protected void executeInternal(JobExecutionContext context) throws JobExecutionException {
		
		String jobUser = env.getProperty(AppConstants.SCHEDULEJOB_USERNAME);
		
		try {

			emailNotificationService.sendPendingEmailNotification(jobUser, "fas_notif_proc_job");
			
		} catch (Exception ex) {
			boolean refireImmediately = false;
			log.error(String.format("Error occurred msg=%s", ex.getMessage()), ex);
			if (env.getProperty(AppConstants.SCHEDULER_RETRY_COUNT, Integer.class,
					AppConstants.DEFAULT_SCHEDULER_RETRY_COUNT) >= context.getRefireCount()) {
				log.error(String.format("Refire job, current refire count=%d", context.getRefireCount()));
				refireImmediately = true;
			}
			throw new JobExecutionException(ex, refireImmediately);
		}
		
	}
}
