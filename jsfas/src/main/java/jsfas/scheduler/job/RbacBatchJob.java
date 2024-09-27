package jsfas.scheduler.job;

import javax.inject.Inject;

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
import jsfas.db.rbac.persistence.service.RbacCommandService;

//annotation to persist job data and disallow concurrent execution
@PersistJobDataAfterExecution
@DisallowConcurrentExecution
public class RbacBatchJob extends QuartzJobBean {

    private final Logger log = LoggerFactory.getLogger(this.getClass());
    
    @Autowired
    private RbacCommandService rbacCommandService;
    
    @Inject
    private Environment env;
    
    @Override
    protected void executeInternal(JobExecutionContext context) throws JobExecutionException {
        // TODO Auto-generated method stub
        log.info("RbacBatchJob start");
        try {
            rbacCommandService.processBatch(); 
        } catch(Exception ex) {
            boolean refireImmediately = false;
            
            log.error(String.format("Error occurred msg=%s", ex.getMessage()), ex);
            
            if(env.getProperty(AppConstants.SCHEDULER_RETRY_COUNT, Integer.class, AppConstants.DEFAULT_SCHEDULER_RETRY_COUNT) 
                    >= context.getRefireCount()) {
                log.error(String.format("Refire job, current refire count=%d", context.getRefireCount()));
                refireImmediately = true;
            }
            throw new JobExecutionException(ex, refireImmediately);
        }
        
        log.info("RbacBatchJob end");
    }

}
