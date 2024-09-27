package jsfas.config;

import java.util.TimeZone;

import javax.sql.DataSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.core.io.ClassPathResource;
import org.springframework.scheduling.quartz.CronTriggerFactoryBean;
import org.springframework.scheduling.quartz.JobDetailFactoryBean;
import org.springframework.scheduling.quartz.SchedulerFactoryBean;
import org.springframework.transaction.PlatformTransactionManager;

import jsfas.common.constants.AppConstants;
import jsfas.scheduler.job.BatchApprovalDailyEmailProc;
import jsfas.scheduler.job.PayrollToHrmsProc;
import jsfas.scheduler.job.PostBrPostProc;
import jsfas.scheduler.job.RbacBatchJob;
import jsfas.scheduler.job.SendEmNotifProc;

//enable to annotation to active scheduler feature
//enable only if scheduler required
@Configuration 
@ComponentScan("jsfas.scheduler.job") 
public class QuartzConfiguration {

    private final Logger log = LoggerFactory.getLogger(QuartzConfiguration.class);
    
    @Autowired
	private Environment env;
	
	//job detail and trigger setting
    
	//rbac job setting
	//setting job detail bean
    @Bean
    public JobDetailFactoryBean rbacBatchJobDetailFactoryBean() {
        JobDetailFactoryBean jobDetail = new JobDetailFactoryBean();
        
        //assign the job class
        jobDetail.setJobClass(RbacBatchJob.class);
        
        jobDetail.setGroup("elRbacBatchGroup");        // the job group name
        jobDetail.setName("elRbacBatch");              // the job name
        jobDetail.setDurability(true);
        return jobDetail;
    }
    
    //setting the job trigger
    @Bean
    public CronTriggerFactoryBean rbacBatchJobTriggerFactoryBean() {
        CronTriggerFactoryBean cronTigger = new CronTriggerFactoryBean();
        cronTigger.setJobDetail(rbacBatchJobDetailFactoryBean().getObject());
        cronTigger.setGroup("elRbacBatchTriggerGroup");           //the trigger group name
        cronTigger.setName("elRbacBatchTrigger");                 //the trigger name
        cronTigger.setCronExpression("0 0 0 1/1 * ? *");            //the cron expression, which will be override by the setting in database if any
        return cronTigger;
    }
    //rbac job setting
	
	//job detail and trigger setting end
	
	//Refer to http://www.quartz-scheduler.org/documentation/quartz-2.x/cookbook/MultipleSchedulers.html
	//For example:
	/*
	 * In "App A" create "Scheduler A" (with config that points it at database tables prefixed with "A"), and invoke start() on 
	 * "Scheduler A". Now "Scheduler A" in "App A" will execute jobs scheduled by “Scheduler A” in "App A"
	 * 
	 * In "App A" create "Scheduler B" (with config that points it at database tables prefixed with "B"), and DO NOT invoke start() on 
	 * "Scheduler B". Now "Scheduler B" in "App A" can schedule jobs to be ran where "Scheduler B" is started.
	 * 
	 * In "App B" create "Scheduler B" (with config that points it at database tables prefixed with "B"), and invoke start() on 
	 * "Scheduler B". Now "Scheduler B" in "App B" will execute jobs scheduled by "Scheduler B" in "App A".
	 */
    
    // Send Email Job
    @Bean
    public JobDetailFactoryBean sendEmailJobDetailFactoryBean() {
    	JobDetailFactoryBean jobDetail = new JobDetailFactoryBean();
    	
    	//assign the job class
        jobDetail.setJobClass(SendEmNotifProc.class);
        jobDetail.setGroup("SendEmailProcessGroup"); // the job group name
        jobDetail.setName("SendEmailProcess"); // the job name
        jobDetail.setDurability(true);
        return jobDetail;
    }
    
    @Bean
    public CronTriggerFactoryBean sendEmailJobTriggerFactoryBean() {
    	CronTriggerFactoryBean cronTrigger = new CronTriggerFactoryBean();
    	
    	cronTrigger.setJobDetail(sendEmailJobDetailFactoryBean().getObject());
    	cronTrigger.setGroup("SendEmailBatchTriggerGroup");
		cronTrigger.setName("SendEmailBatchTrigger");
		cronTrigger.setCronExpression("0/10 * * * * ?");            //the cron expression, which will be override by the setting in database if any
		cronTrigger.setTimeZone(TimeZone.getTimeZone("Asia/Hong_Kong"));
		
		return cronTrigger;
    }
    
    // Payment interface to HRMS job
    @Bean
    public JobDetailFactoryBean payrollToHrmsProcDetailFactoryBean() {
        JobDetailFactoryBean jobDetail = new JobDetailFactoryBean();
        
        //assign the job class
        jobDetail.setJobClass(PayrollToHrmsProc.class);
        
        jobDetail.setGroup("IntergraionProcessGroup");        // the job group name
        jobDetail.setName("PayrollToHrmsProc");              // the job name
        jobDetail.setDurability(true);
        return jobDetail;
    }
    
    //setting the job trigger
    @Bean
    public CronTriggerFactoryBean payrollToHrmsProcTriggerFactoryBean() {
        CronTriggerFactoryBean cronTigger = new CronTriggerFactoryBean();
        cronTigger.setJobDetail(payrollToHrmsProcDetailFactoryBean().getObject());
        cronTigger.setGroup("IntergraionTriggerGroup");           //the trigger group name
        cronTigger.setName("IntergraionTrigger");                 //the trigger name
        cronTigger.setCronExpression("0 0 1 ? * * *");            //the cron expression, which will be override by the setting in database if any
        return cronTigger;
    }
    
    // Post Br Post Proc job
    @Bean
    public JobDetailFactoryBean postBrPostProcDetailFactoryBean() {
        JobDetailFactoryBean jobDetail = new JobDetailFactoryBean();
        
        //assign the job class
        jobDetail.setJobClass(PostBrPostProc.class);
        
        jobDetail.setGroup("PostBrPostProcProcessGroup");        // the job group name
        jobDetail.setName("PostBrPostProc");              // the job name
        jobDetail.setDurability(true);
        return jobDetail;
    }
    
    //setting the job trigger
    @Bean
    public CronTriggerFactoryBean postBrPostProcTriggerFactoryBean() {
        CronTriggerFactoryBean cronTigger = new CronTriggerFactoryBean();
        cronTigger.setJobDetail(postBrPostProcDetailFactoryBean().getObject());
        cronTigger.setGroup("PostBrPostProcTriggerGroup");           //the trigger group name
        cronTigger.setName("PostBrPostProcTrigger");                 //the trigger name
        cronTigger.setCronExpression("0 0 2 ? * * *");            //the cron expression, which will be override by the setting in database if any
        return cronTigger;
    }
    
    // Batch Approval Daily Email job
    @Bean
    public JobDetailFactoryBean batchApprovalDailyEmailProcDetailFactoryBean() {
        JobDetailFactoryBean jobDetail = new JobDetailFactoryBean();
        
        //assign the job class
        jobDetail.setJobClass(BatchApprovalDailyEmailProc.class);
        
        jobDetail.setGroup("BatchApprovalDailyEmailProcessGroup");        // the job group name
        jobDetail.setName("BatchApprovalDailyEmailProc");              // the job name
        jobDetail.setDurability(true);
        return jobDetail;
    }
    
    //setting the job trigger
    @Bean
    public CronTriggerFactoryBean batchApprovalDailyEmailProcTriggerFactoryBean() {
        CronTriggerFactoryBean cronTigger = new CronTriggerFactoryBean();
        cronTigger.setJobDetail(batchApprovalDailyEmailProcDetailFactoryBean().getObject());
        cronTigger.setGroup("BatchApprovalDailyEmailTriggerGroup");           //the trigger group name
        cronTigger.setName("BatchApprovalDailyEmailTrigger");                 //the trigger name
        cronTigger.setCronExpression("0 0 3 ? * * *");            //the cron expression, which will be override by the setting in database if any
        return cronTigger;
    }
    
	@Bean
	@Autowired
	public SchedulerFactoryBean schedulerFactoryBean(
			DataSource dataSourceJsfasMain, PlatformTransactionManager transactionManagerJsfasMain, ApplicationContext applicationContext) {
		
		SchedulerFactoryBean scheduler = new SchedulerFactoryBean();
		
		scheduler.setConfigLocation(new ClassPathResource("quartz.properties"));
        scheduler.setDataSource(dataSourceJsfasMain);
        scheduler.setTransactionManager(transactionManagerJsfasMain);
        
        AutowiringSpringBeanJobFactory jobFactory = new AutowiringSpringBeanJobFactory();
        jobFactory.setApplicationContext(applicationContext);
        scheduler.setJobFactory(jobFactory);

        //override the job definitions in the DB on the app start up.
        scheduler.setOverwriteExistingJobs(false);

        //scheduler.setAutoStartup(true);
        scheduler.setSchedulerName("jsfasScheduler");
        scheduler.setApplicationContextSchedulerContextKey("applicationContext");

        //wait for jobs to complete on app shutdown
        scheduler.setWaitForJobsToCompleteOnShutdown(true);
        
        //set the job details
        scheduler.setJobDetails(
        		rbacBatchJobDetailFactoryBean().getObject(),
        		sendEmailJobDetailFactoryBean().getObject(),
        		payrollToHrmsProcDetailFactoryBean().getObject(),
        		postBrPostProcDetailFactoryBean().getObject(),
        		batchApprovalDailyEmailProcDetailFactoryBean().getObject()
        );
            
        //set the job triggers
        scheduler.setTriggers(
        		rbacBatchJobTriggerFactoryBean().getObject(),
        		sendEmailJobTriggerFactoryBean().getObject(),
        		payrollToHrmsProcTriggerFactoryBean().getObject(),
        		postBrPostProcTriggerFactoryBean().getObject(),
        		batchApprovalDailyEmailProcTriggerFactoryBean().getObject()
        		
        );
        
        scheduler.setAutoStartup(false);
        
		if (env.getProperty(AppConstants.SCHEDULER_ENABLED, AppConstants.NO).equalsIgnoreCase(AppConstants.YES)) {
		    log.info("Scheduler feature enabled");
			scheduler.setAutoStartup(true);
		}
		
		return scheduler;
	}
	
}