package jsfas.web.interceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import jsfas.common.annotation.Function;
import jsfas.common.annotation.Functions;
import jsfas.common.annotation.Page;
import jsfas.common.exception.NotAuthorizedForFunctionException;
import jsfas.db.main.persistence.service.CommonRoutineService;

public class CommonInterceptor implements HandlerInterceptor {

	@Autowired
	private CommonRoutineService commonRoutineService;
	
	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
			throws Exception {
		HandlerMethod handlerMethod = (HandlerMethod) handler;
		request.setAttribute("method", handlerMethod.getMethod());
		
		Function function = handlerMethod.getMethodAnnotation(Function.class);
		Functions functions = handlerMethod.getMethodAnnotation(Functions.class);
		
		//processing function annotation
		if (function != null) {
			String functionCode = function.functionCode();
			String functionSubCode = function.functionSubCode();
			String functionPage = function.functionPage();
			
			if (function.accessChecking() && !commonRoutineService.isAuthorized(functionCode, functionSubCode, functionPage)) {
				throw new NotAuthorizedForFunctionException(functionCode, functionSubCode);
			}
		}
		
		//processing functions annotation
		if (functions != null) {
			if (functions.accessChecking() && !commonRoutineService.isAuthorized(functions.value())) {
				throw new NotAuthorizedForFunctionException(functions.value());
			}
		}
		
		return true;
	}

	@Override
	public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler,
			ModelAndView modelAndView) throws Exception {
		HandlerMethod handlerMethod = (HandlerMethod) handler;
		Page page = handlerMethod.getMethodAnnotation(Page.class);
		
		//processing page annotation
		if (page != null) {
			if (page.noMenu() && modelAndView != null) {
				modelAndView.getModelMap().remove("menuItemList");
			}
			if (!page.activePage().isEmpty() && modelAndView != null) {
				modelAndView.addObject("activeMenuItem", page.activePage());
			}
			if (!page.view().isEmpty() && modelAndView != null) {
				modelAndView.setViewName(page.view());
			}
		}
	}

	@Override
	public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex)
			throws Exception {
	}
}