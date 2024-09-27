package jsfas.web.controller;


import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletRequest;

import org.quartz.SchedulerException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

import com.fasterxml.jackson.databind.ObjectMapper;

import jsfas.common.annotation.Function;
import jsfas.common.constants.AppConstants;
import jsfas.common.constants.RestURIConstants;
import jsfas.common.object.ScheduleJobInfo;
import jsfas.db.main.persistence.service.CommonRoutineService;
import jsfas.db.main.persistence.service.SchedulerService;

/**
 * @author iswill, isalister
 * @since 26/02/2018
 * @see View Controller for Scheduler
 */
//enable to annotation to active scheduler feature
//enable only if scheduler required
@RestController
@ControllerAdvice
public class SchedulerViewController {
	
    @Autowired
    private CommonRoutineService commonRoutineService;
    
    @Autowired
    private SchedulerService schedulerService;
    
    @Autowired
    private ObjectMapper objectMapper;
	
	@Function(functionCode = AppConstants.SCHEDULER_FUNC_CDE, functionPage = RestURIConstants.SCHEDULER_PAGE)
    @RequestMapping(value = RestURIConstants.SCHEDULER_PATH, method = RequestMethod.GET)
	public ModelAndView scheduler(HttpServletRequest request) throws Exception {
		ModelAndView output = commonRoutineService.getCommonModelAndView(request, AppConstants.SYS_CATG_CDE);
		Map<String, Set<String>> jobInfoMap = schedulerService.getScheduleJobInfoList().stream()
		                                                                               .collect(Collectors.groupingBy(ScheduleJobInfo::getJobGroup
		                                                                                      , Collectors.mapping(ScheduleJobInfo::getJobName, Collectors.toSet())));
		output.addObject("jobInfoMap", objectMapper.writeValueAsString(jobInfoMap));
		
		return output;
	}
}
