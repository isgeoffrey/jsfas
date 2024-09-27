package jsfas.common.object;

public class ScheduleJobInfo {

	private String jobName;
	private String jobGroup;
	private String triggerName;
	private String triggerGroup;
	private String nextFireTime;
	private String prevFireTime;
	private String triggerState;

	public ScheduleJobInfo() {}
	
	public ScheduleJobInfo(ScheduleJobInfo scheduleJobInfo) {
	    this.jobName = scheduleJobInfo.jobName;
	    this.jobGroup = scheduleJobInfo.jobGroup;
	    this.nextFireTime = scheduleJobInfo.nextFireTime;
	    this.prevFireTime = scheduleJobInfo.prevFireTime;
	    this.triggerState = scheduleJobInfo.triggerState;
	}
	
	public String getJobName() {
		return jobName;
	}
	
	public ScheduleJobInfo setJobName(String jobName) {
		this.jobName = jobName;
		return this;
	}
	
	public String getJobGroup() {
        return jobGroup;
    }
	
    public ScheduleJobInfo setJobGroup(String jobGroup) {
        this.jobGroup = jobGroup;
        return this;
    }
	public String getTriggerName() {
		return triggerName;
	}
	
	public ScheduleJobInfo setTriggerName(String triggerName) {
		this.triggerName = triggerName;
		return this;
	}
	
	public String getTriggerGroup() {
        return triggerGroup;
    }
    public ScheduleJobInfo setTriggerGroup(String triggerGroup) {
        this.triggerGroup = triggerGroup;
        return this;
    }
    
	public ScheduleJobInfo setNextFireTime(String nextFireTime) {
	    this.nextFireTime = nextFireTime;
	    return this;
	}
	
	public String getNextFireTime() {
	    return this.nextFireTime;
	}
	
	public ScheduleJobInfo setPrevFireTime(String prevFireTime) {
        this.prevFireTime = prevFireTime;
        return this;
    }
    public String getPrevFireTime() {
        return this.prevFireTime;
    }
    
	public String getTriggerState() {
		return triggerState;
	}
	
	public ScheduleJobInfo setTriggerState(String triggerState) {
		this.triggerState = triggerState;
		return this;
	}
	
	@Override
	public String toString() {
	    return String.format("%s,%s,%s,%s,%s,%s,%s", jobName, jobGroup, triggerName, triggerGroup, nextFireTime, prevFireTime, triggerState);
	}
	
}
