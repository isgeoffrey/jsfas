package jsfas;

import javax.servlet.ServletContext;

import org.apache.commons.lang.StringUtils;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.freemarker.FreeMarkerAutoConfiguration;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.core.env.Environment;
import org.springframework.web.context.WebApplicationContext;

import ch.qos.logback.ext.spring.web.LogbackConfigListener;
import ch.qos.logback.ext.spring.web.WebLogbackConfigurer;

@SpringBootApplication
@EnableAutoConfiguration(exclude = {
		  FreeMarkerAutoConfiguration.class
		})
public class JsfasApplication extends SpringBootServletInitializer {

	//for spring boot only
	public static void main(String[] args) {
		SpringApplication.run(JsfasApplication.class, args);
	}
	//for spring boot only
	
	//for run on tomcat only
	@Override
	protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
		return application.sources(JsfasApplication.class);
	}
	
	@Override
	protected WebApplicationContext createRootApplicationContext(ServletContext servletContext) {
		WebApplicationContext rootAppContext = super.createRootApplicationContext(servletContext);
		
		Environment env = rootAppContext.getEnvironment();
        String envStr = StringUtils.EMPTY;
        
        if(env.getActiveProfiles().length != 0 && !env.getActiveProfiles()[0].equalsIgnoreCase("predev") && !env.getActiveProfiles()[0].equalsIgnoreCase("default"))  {
            envStr = "." + env.getActiveProfiles()[0];
        }
        
        servletContext.setInitParameter(WebLogbackConfigurer.CONFIG_LOCATION_PARAM, String.format("classpath:logback%s.xml", envStr));
		servletContext.setInitParameter(WebLogbackConfigurer.EXPOSE_WEB_APP_ROOT_PARAM, String.valueOf(false));
		servletContext.addListener(new LogbackConfigListener()); //listener to split log config
		//servletContext.addListener(new SessionListener(env.getProperty("server.session.timeout"))); //listener to enable session timeout
		servletContext.setInitParameter("dispatchOptionsRequest", "true");
		return rootAppContext;
	}
	
	/* enable only for session timeout
	public class SessionListener implements HttpSessionListener {
		private int interval = 1800;
		
		public SessionListener(String intervalStr) {
			try {
				int interval = Integer.parseInt(intervalStr);
				this.interval = interval;
			} catch (Exception e) {
				System.out.println("parse default session timeout");
			}
		}
		
		@Override
		public void sessionCreated(HttpSessionEvent event) {
			event.getSession().setMaxInactiveInterval(interval); //session timeout in seconds
		}

		@Override
		public void sessionDestroyed(HttpSessionEvent event) {
		}
	}
	*/
	//for run on tomcat only
}
