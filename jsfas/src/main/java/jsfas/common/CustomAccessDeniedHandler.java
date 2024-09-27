package jsfas.common;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.core.env.Environment;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandlerImpl;
import org.springframework.security.web.csrf.InvalidCsrfTokenException;
import org.springframework.security.web.csrf.MissingCsrfTokenException;

public class CustomAccessDeniedHandler extends AccessDeniedHandlerImpl {
	private Environment env;
	
	public void setEnv(Environment env) {
		this.env = env;
	}
	
	public Environment getEnv() {
		return this.env;
	}
	
	@Override
	public void handle(HttpServletRequest request, HttpServletResponse response,
			AccessDeniedException accessDeniedException) throws IOException, ServletException {
		if (accessDeniedException instanceof MissingCsrfTokenException
				|| accessDeniedException instanceof InvalidCsrfTokenException) {
			response.sendRedirect(env.getProperty("cas.service.expire"));
		}
		super.handle(request, response, accessDeniedException);
	}
}
