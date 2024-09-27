package jsfas.web.controller;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.hibernate.validator.constraints.NotEmpty;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Order;
import org.springframework.format.annotation.NumberFormat;
import org.springframework.scheduling.quartz.SchedulerFactoryBean;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import jsfas.common.constants.AppConstants;
import jsfas.common.constants.RestURIConstants;
import jsfas.common.json.CommonJson;
import jsfas.common.json.Response;
import jsfas.common.json.ResponseJson;
import jsfas.common.object.ScheduleJobInfo;
import jsfas.db.main.persistence.service.SchedulerService;

/**
 * @author iswill, isalister
 * @since 26/02/2018
 * @see View Controller for Scheduler
 */
@RestController
@ControllerAdvice
public class SchedulerApiController extends CommonApiController {

    @Autowired
    private SchedulerService schedulerService;
    
    @Autowired
    SchedulerFactoryBean schedulerFactoryBean;
    
    @SuppressWarnings("unchecked")
    @RequestMapping(value = RestURIConstants.GET_SCHEDULER_JOB_LIST, method = RequestMethod.POST)
    public Response getSchedulerJobList(@RequestBody CommonJson inputJson, BindingResult result) throws Exception {
    	Response response = new Response();
    	CommonJson responseJson = new CommonJson();
        
        validate(new Object() {
            
            @NumberFormat
            private int draw = inputJson.get("draw", Integer.class);
            
            @NumberFormat
            private int start = inputJson.get("start", Integer.class);
            
            @NumberFormat
            private int length = inputJson.get("length", Integer.class);
            
            @NotEmpty
            private List<String> sort = inputJson.get("sort", List.class);

        }, result);
        
        int draw = inputJson.get("draw", Integer.class);
        int start = inputJson.get("start", Integer.class);
        int length = inputJson.get("length", Integer.class);
        List<String> sort = inputJson.get("sort", List.class);
        
        List<Order> orderList = new ArrayList<>();
        for(String sortString: sort) {
            List<String> sortParts = new ArrayList<>(Arrays.asList(sortString.split(",")));
            String propertyName = sortParts.remove(0);
            Sort.Direction direction = Sort.Direction.ASC;
            if(!sortParts.isEmpty()) {
                direction = Sort.Direction.fromString(sortParts.remove(0));
            }
            orderList.add(new Order(direction, propertyName));
        }
        List<ScheduleJobInfo> jobInfoList = schedulerService.getScheduleJobInfoList(Optional.ofNullable(inputJson)
                , Optional.ofNullable(new PageRequest(start
                                                    , length
                                                    , new Sort(orderList))));
        responseJson.set("draw", draw)
                    .set("start", start)
                    .set("length", length)
                    .set("recordsTotal", jobInfoList.size())
                    .set("recordsFiltered", jobInfoList.size())
                    .set("status", AppConstants.RESPONSE_JSON_SUCCESS_CODE)
                    .set("data", jobInfoList);
        
        response.setData(responseJson);
        
        return setSuccess(response);
    }
    
    @RequestMapping(value = RestURIConstants.RESUME_SCHEDULER_TRIGGER, method = RequestMethod.POST)
    public Response resumeSchedulerTrigger(@RequestBody CommonJson inputJson, BindingResult result) throws Exception {

    	
        validate(new Object() {
            
            @NotEmpty
            private String triggerName = inputJson.get("triggerName");
            
            @NotEmpty
            private String triggerGroup = inputJson.get("triggerGroup");
                    
        }, result);
        
        Response response = new Response();

        try {
        	schedulerService.resumeSchedulerTrigger(inputJson);
        } catch(Exception e) {
        	response.setMessage("Unexpert Error");
        }
       // responseJson.setStatus(AppConstants.RESPONSE_JSON_SUCCESS_CODE);
        
        return setSuccess(response);
    }
    
    @RequestMapping(value = RestURIConstants.PAUSE_SCHEDULER_TRIGGER, method = RequestMethod.POST)
    public Response pauseSchedulerTrigger(@RequestBody CommonJson inputJson, BindingResult result) throws Exception {
        
        validate(new Object() {
            
            @NotEmpty
            private String triggerName = inputJson.get("triggerName");
            
            @NotEmpty
            private String triggerGroup = inputJson.get("triggerGroup");
                    
        }, result);

        Response response = new Response();
        try {
            schedulerService.pauseSchedulerTrigger(inputJson);
	        setSuccess(response);
        } catch(Exception e) {
    		response.setMeta(400, "Unexpert Error");
        }
        //responseJson.setStatus(AppConstants.RESPONSE_JSON_SUCCESS_CODE);

        return response;
    }
    
    @RequestMapping(value = RestURIConstants.RESUME_SCHEDULER_ALL_TRIGGER, method = RequestMethod.POST)
    public Response resumeSchedulerAllTrigger() throws Exception { 
    	Response response = new Response();

        try {
	        schedulerService.resumeSchedulerAllTrigger();
	        setSuccess(response);
	    } catch(Exception e) {
    		response.setMeta(400, "Unexpert Error");
	    }
        //responseJson.setStatus(AppConstants.RESPONSE_JSON_SUCCESS_CODE);

        return response;
    }
    
    @RequestMapping(value = RestURIConstants.PAUSE_SCHEDULER_ALL_TRIGGER, method = RequestMethod.POST)
    public Response pauseSchedulerAllTrigger() throws Exception {
    	Response response = new Response();

        try {
	        schedulerService.pauseSchedulerAllTrigger();
	        setSuccess(response);
	    } catch(Exception e) {
    		response.setMeta(400, "Unexpert Error");
	    }
        //responseJson.setStatus(AppConstants.RESPONSE_JSON_SUCCESS_CODE);
        
        return response;
    }
    
    @RequestMapping(value = RestURIConstants.ADD_SCHEDULER_TRIGGER, method = RequestMethod.POST)
    public Response addSchedulerTrigger(@RequestBody CommonJson inputJson, BindingResult result) throws Exception {
        
        validate(new Object() {

            @NotEmpty
            private String jobGroup = inputJson.get("jobGroup");
            
            @NotEmpty
            private String jobName = inputJson.get("jobName");
            
            @NotEmpty
            private String triggerName = inputJson.get("triggerName");

            @NotEmpty
            private String nextFireTime = inputJson.get("nextFireTime");

        }, result);

    	Response response = new Response();
    	try {
    		schedulerService.addOrChangeSchedulerTrigger(inputJson.set("isNew", true));
    		setSuccess(response);
    	} catch (Exception e) {
    		response.setMeta(400, e.getMessage());
    	}
       // responseJson.setStatus(AppConstants.RESPONSE_JSON_SUCCESS_CODE);
        return response;
    }
    
    @RequestMapping(value = RestURIConstants.CHG_SCHEDULER_TRIGGER, method = RequestMethod.POST)
    public Response changeSchedulerTrigger(@RequestBody CommonJson inputJson, BindingResult result) throws Exception {
        
        validate(new Object() {

            @NotEmpty
            private String jobGroup = inputJson.get("jobGroup");
            
            @NotEmpty
            private String jobName = inputJson.get("jobName");
            
            @NotEmpty
            private String triggerName = inputJson.get("triggerName");
            
            @NotEmpty
            private String oldTriggerName = inputJson.get("oldTriggerName");

            @NotEmpty
            private String nextFireTime = inputJson.get("nextFireTime");

        }, result);

    	Response response = new Response();
    	try {
    		schedulerService.addOrChangeSchedulerTrigger(inputJson.set("isNew", false));
    		setSuccess(response);
    	} catch (Exception e) {
    		response.setMeta(400, e.getMessage());
    	}	
        //responseJson.setStatus(AppConstants.RESPONSE_JSON_SUCCESS_CODE);
        return response;
    }
    
    @RequestMapping(value = RestURIConstants.REM_SCHEDULER_TRIGGER, method = RequestMethod.POST)
    public Response removeSchedulerTrigger(@RequestBody CommonJson inputJson, BindingResult result) throws Exception {
        
        validate(new Object() {
            
            @NotEmpty
            private String triggerName = inputJson.get("triggerName");
        }, result);
        
//        ResponseJson responseJson = new ResponseJson();
    	Response response = new Response();
    	try {
    		schedulerService.removeSchedulerTrigger(inputJson);
    		setSuccess(response);
    	} catch (Exception e) {
    		response.setMeta(400, e.getMessage());
    	}	
        //responseJson.setStatus(AppConstants.RESPONSE_JSON_SUCCESS_CODE);
        return response;
    }
    
    @RequestMapping(value = RestURIConstants.GET_SCHEDULE_JOB_INFO_LIST, method = RequestMethod.GET)
    public Response getScheduleJobInfoList() throws Exception { 
    	Response response = new Response();
    	CommonJson commonJson = new CommonJson();

        try {
    		Map<String, Set<String>> jobInfoMap = schedulerService.getScheduleJobInfoList().stream()
                    .collect(Collectors.groupingBy(ScheduleJobInfo::getJobGroup
                           , Collectors.mapping(ScheduleJobInfo::getJobName, Collectors.toSet())));
    		
    		commonJson.set("jobInfoMap", jobInfoMap);
    		response.setData(commonJson);
	    } catch(Exception e) {
	    	response.setMessage("Unexpert Error");
	    }
        
        return setSuccess(response);
    }
}