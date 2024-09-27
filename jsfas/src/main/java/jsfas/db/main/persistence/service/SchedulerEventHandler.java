package jsfas.db.main.persistence.service;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.quartz.JobDetail;
import org.quartz.JobKey;
import org.quartz.Scheduler;
import org.quartz.SimpleScheduleBuilder;
import org.quartz.Trigger;
import org.quartz.Trigger.TriggerState;
import org.quartz.TriggerBuilder;
import org.quartz.TriggerKey;
import org.quartz.impl.matchers.GroupMatcher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.scheduling.quartz.SchedulerFactoryBean;

import jsfas.common.constants.AppConstants;
import jsfas.common.exception.RecordExistException;
import jsfas.common.exception.RecordRemovedException;
import jsfas.common.json.CommonJson;
import jsfas.common.object.ScheduleJobInfo;
import jsfas.common.utils.DynamicComparator;
import jsfas.common.utils.GeneralUtil;

public class SchedulerEventHandler implements SchedulerService {

    @Autowired
    SchedulerFactoryBean schedulerFactoryBean;
    
    private final Logger log = LoggerFactory.getLogger(this.getClass());
    
    @Override
    public void resumeSchedulerTrigger(CommonJson inputJson) throws Exception {
        Scheduler scheduler = schedulerFactoryBean.getScheduler();
        TriggerKey triggerKey = TriggerKey.triggerKey(inputJson.get("triggerName"), inputJson.get("triggerGroup"));
        TriggerState triggerState = scheduler.getTriggerState(triggerKey);
        
        if(!scheduler.checkExists(triggerKey)) {
            throw new RecordRemovedException();
        }
        
        try {
        	if(triggerState.equals(TriggerState.PAUSED) || triggerState.equals(TriggerState.ERROR)) {
                scheduler.resumeTrigger(triggerKey);
            }
        } catch(Exception e) {
        	
        }
    }
    
    @Override
    public void resumeSchedulerAllTrigger() throws Exception {
        Scheduler scheduler = schedulerFactoryBean.getScheduler();
        Set<TriggerKey> triggerKeySet = scheduler.getTriggerKeys(GroupMatcher.anyTriggerGroup());
        Iterator<TriggerKey> iter = triggerKeySet.iterator();
        Optional<TriggerKey> optTriggerKey = Optional.empty();
        try {
            while(iter.hasNext()) {
                optTriggerKey = Optional.ofNullable(iter.next());
                resumeSchedulerTrigger(new CommonJson().set("triggerName", optTriggerKey.get().getName())
                                                       .set("triggerGroup", optTriggerKey.get().getGroup()));
            }  
        } catch (RecordRemovedException e) {
            optTriggerKey.ifPresent(triggerKey -> {
                log.warn("Ignore resume trigger name={}, group={}", triggerKey.getName(), triggerKey.getGroup()); 
            });
        }
    }
    
    @Override
    public void pauseSchedulerTrigger(CommonJson inputJson) throws Exception {
        Scheduler scheduler = schedulerFactoryBean.getScheduler();
        TriggerKey triggerKey = TriggerKey.triggerKey(inputJson.get("triggerName"), inputJson.get("triggerGroup"));
        TriggerState triggerState = scheduler.getTriggerState(triggerKey);
        
        if(!scheduler.checkExists(triggerKey)) {
            throw new RecordRemovedException();
        }
        
        if(triggerState.equals(TriggerState.BLOCKED) || triggerState.equals(TriggerState.NORMAL) || triggerState.equals(TriggerState.COMPLETE)) {
            scheduler.pauseTrigger(triggerKey);
        }
    }
    
    @Override
    public void pauseSchedulerAllTrigger() throws Exception {
        Scheduler scheduler = schedulerFactoryBean.getScheduler();
        Set<TriggerKey> triggerKeySet = scheduler.getTriggerKeys(GroupMatcher.anyTriggerGroup());
        Iterator<TriggerKey> iter = triggerKeySet.iterator();
        Optional<TriggerKey> optTriggerKey = Optional.empty();
        
        try {
            while(iter.hasNext()) {
                optTriggerKey = Optional.ofNullable(iter.next());
                pauseSchedulerTrigger(new CommonJson().set("triggerName", optTriggerKey.get().getName())
                                                      .set("triggerGroup", optTriggerKey.get().getGroup()));
            } 
        } catch (RecordRemovedException e) {
            optTriggerKey.ifPresent(triggerKey -> {
                log.warn("Ignore pause trigger name={}, group={}", triggerKey.getName(), triggerKey.getGroup()); 
            });
        }
        
    }
    
    @Override
    public void addOrChangeSchedulerTrigger(CommonJson inputJson) throws Exception {
        boolean isNew = inputJson.get("isNew", Boolean.class);
        Scheduler scheduler = schedulerFactoryBean.getScheduler();
        JobDetail jobDetail = scheduler.getJobDetail(JobKey.jobKey(inputJson.get("jobName"), inputJson.get("jobGroup")));
        TriggerKey triggerKey = TriggerKey.triggerKey(inputJson.get("triggerName"), AppConstants.SCHEDULER_ADHOC_SIMPLE_TRIGGER_GROUP);
        TriggerKey oldTriggerKey = TriggerKey.triggerKey(inputJson.get("oldTriggerName"), AppConstants.SCHEDULER_ADHOC_SIMPLE_TRIGGER_GROUP);
        
        if(isNew && scheduler.checkExists(triggerKey)) {
            throw new RecordExistException();
        }
        if(!isNew && !scheduler.checkExists(oldTriggerKey)) {
            throw new RecordRemovedException();
        }
        
        if(isNew) {
            Trigger trigger = TriggerBuilder.newTrigger()
                                            .withIdentity(triggerKey)
                                            .withSchedule(SimpleScheduleBuilder.simpleSchedule())
                                            .forJob(jobDetail)
                                            .startAt(GeneralUtil.getDFormatter9().parse(inputJson.get("nextFireTime")))
                                            .build();  
            scheduler.scheduleJob(trigger);
        } else {
            Trigger trigger = scheduler.getTrigger(oldTriggerKey)
                                       .getTriggerBuilder()
                                       .withIdentity(triggerKey)
                                       .startAt(GeneralUtil.getDFormatter9().parse(inputJson.get("nextFireTime")))
                                       .build();
            if(scheduler.unscheduleJob(oldTriggerKey)) {
                scheduler.scheduleJob(trigger);
            }
        }
    }
    
    @Override
    public void removeSchedulerTrigger(CommonJson inputJson) throws Exception {
        Scheduler scheduler = schedulerFactoryBean.getScheduler();
        TriggerKey triggerKey = TriggerKey.triggerKey(inputJson.get("triggerName"), AppConstants.SCHEDULER_ADHOC_SIMPLE_TRIGGER_GROUP);
        if(!scheduler.checkExists(triggerKey)) {
            throw new RecordRemovedException();
        }
        scheduler.unscheduleJob(triggerKey);
    }
    
    @Override
    public List<ScheduleJobInfo> getScheduleJobInfoList() throws Exception {
        return getScheduleJobInfoList(Optional.empty(), Optional.empty());
    }
    
    @SuppressWarnings("unchecked")
    @Override
    public List<ScheduleJobInfo> getScheduleJobInfoList(Optional<CommonJson> optionalSearchJson, Optional<Pageable> optionalPageable) throws Exception {
        CommonJson searchJson = optionalSearchJson.orElse(new CommonJson());
        String search = searchJson.getFilterValue("search");
        
        Scheduler scheduler = schedulerFactoryBean.getScheduler();
        List<ScheduleJobInfo> jobInfoList = new ArrayList<>();
        for (String groupName: scheduler.getJobGroupNames()) {
            for (JobKey jobKey: scheduler.getJobKeys(GroupMatcher.jobGroupEquals(groupName))) {
                List<Trigger> triggers = (List<Trigger>) scheduler.getTriggersOfJob(jobKey);
                for(Trigger trigger: triggers) {
                    ScheduleJobInfo jobInfo = new ScheduleJobInfo();
                    TriggerKey triggerKey = trigger.getKey();
                    jobInfo.setJobName(jobKey.getName())
                           .setJobGroup(jobKey.getGroup())
                           .setTriggerName(triggerKey.getName())
                           .setTriggerGroup(triggerKey.getGroup())
                           .setTriggerState(scheduler.getTriggerState(triggerKey).toString());
                    if(Optional.ofNullable(trigger.getNextFireTime()).isPresent()) {
                        jobInfo.setNextFireTime(GeneralUtil.getStringByDate9(new Timestamp(trigger.getNextFireTime().getTime())));
                    }
                    if(Optional.ofNullable(trigger.getPreviousFireTime()).isPresent()) {
                        jobInfo.setPrevFireTime(GeneralUtil.getStringByDate9(new Timestamp(trigger.getPreviousFireTime().getTime())));
                    }
                    jobInfoList.add(jobInfo);
                }
            }
        }
        
        if(!jobInfoList.isEmpty()) {
            if(!search.trim().isEmpty()) {
                jobInfoList = jobInfoList.stream().filter(o -> o.toString().contains(search.trim())).collect(Collectors.toList());
            }
            if(optionalPageable.isPresent()) {
                Pageable pageable = optionalPageable.get();
                
                int offset = Math.toIntExact(pageable.getOffset());		// test Java 11 Upgrade
                int pageSize = pageable.getPageSize();
                
                if(Optional.ofNullable(pageable.getSort()).isPresent()) {
                    jobInfoList = jobInfoList.stream()
                                             .sorted(DynamicComparator.of(ScheduleJobInfo.class, pageable.getSort()))
                                             .collect(Collectors.toList());
                }
                if(!jobInfoList.isEmpty()) {
                    jobInfoList = jobInfoList.subList(offset, offset + pageSize > jobInfoList.size()? jobInfoList.size(): offset + pageSize);
                }
            };
        }
        return jobInfoList;
    }
}
