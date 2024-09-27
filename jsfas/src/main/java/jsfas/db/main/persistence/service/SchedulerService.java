package jsfas.db.main.persistence.service;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Pageable;

import jsfas.common.json.CommonJson;
import jsfas.common.object.ScheduleJobInfo;

public interface SchedulerService {
    
    public void resumeSchedulerTrigger(CommonJson inputJson) throws Exception;
    
    public void resumeSchedulerAllTrigger() throws Exception;
    
    public void pauseSchedulerTrigger(CommonJson inputJson) throws Exception;
    
    public void pauseSchedulerAllTrigger() throws Exception;
    
    public void addOrChangeSchedulerTrigger(CommonJson inputJson) throws Exception;
    
    public void removeSchedulerTrigger(CommonJson inputJson) throws Exception;
    
    public List<ScheduleJobInfo> getScheduleJobInfoList() throws Exception;
    
    public List<ScheduleJobInfo> getScheduleJobInfoList(Optional<CommonJson> optionalSearchJson, Optional<Pageable> optionalPageable) throws Exception;
}
