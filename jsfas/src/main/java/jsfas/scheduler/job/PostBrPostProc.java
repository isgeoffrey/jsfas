package jsfas.scheduler.job;

import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.scheduling.quartz.QuartzJobBean;

import jsfas.common.constants.AppConstants;
import jsfas.db.main.persistence.service.LoaEventHandler;

public class PostBrPostProc extends QuartzJobBean {
	private final Logger log = LoggerFactory.getLogger(this.getClass());

	@Autowired
	private Environment env;
	
	@Autowired
    private LoaEventHandler loaService;
	
	@Override
	protected void executeInternal(JobExecutionContext context) throws JobExecutionException {

		String jobUser = env.getProperty(AppConstants.SCHEDULEJOB_USERNAME);
		String op_page_nam = "post_br_post_proc";
		
		try {
			//TODO: 
			// The schedule job is used to process the application which is already completed the BR Post Process
			// Case 1: application is approved and completed BR Post (For the first time, LOA is not yet generated)
			// APPL_STAT_CDE = APPROVED and BR_POST_IND = Y
			// Generate LOA and Update the application status
			// email send to applicant and requester
			// To Do Later: add my-ai work list to applicant for accept offer
			
			// call the function to generate payroll file.
			loaService.loaScheduledJob(jobUser, op_page_nam);
			//
			
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
