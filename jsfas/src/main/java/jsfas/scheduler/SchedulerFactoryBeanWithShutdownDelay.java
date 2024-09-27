package jsfas.scheduler;

import org.quartz.SchedulerException;
import org.springframework.scheduling.quartz.SchedulerFactoryBean;

//TODO: Quartz bug cause memory leak in tomcat, refer to https://stackoverflow.com/questions/2730354/spring-scheduler-shutdown-error/6772253
public class SchedulerFactoryBeanWithShutdownDelay extends SchedulerFactoryBean {

  @Override
  public void destroy() throws SchedulerException {
      super.destroy();
      // TODO: Ugly workaround for https://jira.terracotta.org/jira/browse/QTZ-192
      try {
          Thread.sleep(1000);
      } catch (InterruptedException ex) {
          throw new RuntimeException(ex);
      }
  }
}
