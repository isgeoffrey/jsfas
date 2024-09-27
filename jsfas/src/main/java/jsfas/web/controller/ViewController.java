package jsfas.web.controller;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

import jsfas.common.constants.AppConstants;
import jsfas.common.constants.RestURIConstants;
import jsfas.common.json.CommonJson;
import jsfas.common.utils.GeneralUtil;
import jsfas.db.main.persistence.domain.FunctionCatalogDAO;
import jsfas.db.main.persistence.service.CommonRoutineService;
import jsfas.security.SecurityUtils;

/**
 * @author iseric
 * @since 12/5/2016
 * @see Controller for Web View
 */
@RestController
@ControllerAdvice
public class ViewController {
	@Autowired
	private CommonRoutineService commonRoutineService;

	private final Logger log = LoggerFactory.getLogger(this.getClass());
	
	@RequestMapping(value = RestURIConstants.INDEX)
	public ModelAndView handleIndexError(HttpServletRequest req, Exception ex) {
	    String errorMsg = commonRoutineService.getMessagePlaceholderForHtml("Please enter a valid link to access the application.");
		ModelAndView output = new ModelAndView("web/error");
		output.addObject("errorMsg", errorMsg);
		return output;
	}
	
	@RequestMapping(value = RestURIConstants.ADMIN_MENU, method = RequestMethod.GET)
	public ModelAndView adMenu(HttpServletRequest request) {
		ModelAndView output = commonRoutineService.getCommonModelAndView(request, AppConstants.SYS_CATG_CDE);
		
		List<FunctionCatalogDAO> functionCatalogList = commonRoutineService.getFuncCatalog(AppConstants.SYS_CATG_CDE);
		output.addObject("functionCatalogList", functionCatalogList);
		
		output.setViewName("web/tr_admin");
		
		CommonJson loginAttributes = SecurityUtils.getCurrentLoginAttributes();
		log.info("remoteUser: {}", SecurityUtils.getCurrentLogin());
		
		log.info("loginAttributes: {}", loginAttributes.props());
		
		log.info("isStudent: {}", SecurityUtils.isStudent());
		log.info("isStaff: {}", SecurityUtils.isStaff());
		log.info("isUndergraduate: {}", SecurityUtils.isUndergraduate());
		log.info("isPostgraudate: {}", SecurityUtils.isPostgraudate());
		
		log.info("userDept: {}", SecurityUtils.getUserDept());

		//log.info("userLoginInfo {}", output.getModel().get("userLoginInfo"));
		return output;
	}
}
