package jsfas.config;

import java.util.Properties;

import javax.inject.Inject;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.core.env.Environment;
import org.springframework.core.io.ClassPathResource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.ui.freemarker.FreeMarkerConfigurationFactoryBean;
import org.springframework.web.servlet.config.annotation.DefaultServletHandlerConfigurer;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.view.InternalResourceViewResolver;

import jsfas.common.CacheVersion;
import jsfas.common.PasswordProtector;
import jsfas.common.constants.AppConstants;
import jsfas.common.jasper.JasperReportsConfig;
import jsfas.common.jasper.JasperReportsConfigurer;
import jsfas.common.spring.PropertiesMap;
import jsfas.common.utils.Message;
import jsfas.web.interceptor.CommonInterceptor;
import jsfas.web.interceptor.CustomHttpResponseInterceptor;

/**
 * @author iseric
 * @since 12/5/2016
 * @see Class for properties setup, basically no need to modify
 */
@Configuration
@EnableWebMvc
public class ApplicationConfigurerAdapter implements WebMvcConfigurer {

	@Autowired
	Environment env;
	
	@Configuration
	@Profile({"predev","default"})
	public static class DefaultConfiguration {

	    @Bean(name="properties")
	    public static PropertiesMap mapper() {
	        PropertiesMap propsMap = new PropertiesMap();
	        propsMap.setLocation(new ClassPathResource("application.properties"));
	        return propsMap;
	    }
	}

	@Configuration
	@Profile("test")
	public static class TestConfiguration {

	    @Bean(name="properties")
	    public static PropertiesMap mapper() {
	        PropertiesMap propsMap = new PropertiesMap();
	        propsMap.setLocation(new ClassPathResource("application-test.properties"));
	        return propsMap;
	    }
	}

	@Configuration
	@Profile("dev")
	public static class DevConfiguration {

	    @Bean(name="properties")
	    public static PropertiesMap mapper() {
	        PropertiesMap propsMap = new PropertiesMap();
	        propsMap.setLocation(new ClassPathResource("application-dev.properties"));
	        return propsMap;
	    }
	}
	
	@Configuration
	@Profile("uat")
	public static class UatConfiguration {

	    @Bean(name="properties")
	    public static PropertiesMap mapper() {
	        PropertiesMap propsMap = new PropertiesMap();
	        propsMap.setLocation(new ClassPathResource("application-uat.properties"));
	        return propsMap;
	    }
	}
	
	@Configuration
	@Profile("production")
	public static class ProductionConfiguration {

	    @Bean(name="properties")
	    public static PropertiesMap mapper() {
	        PropertiesMap propsMap = new PropertiesMap();
	        propsMap.setLocation(new ClassPathResource("application-production.properties"));
	        return propsMap;
	    }
	}
	
    @Override
    public void configureDefaultServletHandling(DefaultServletHandlerConfigurer configurer) {
        //configurer.enable();
    }

    @Bean
    public InternalResourceViewResolver viewResolver() {
        InternalResourceViewResolver resolver = new InternalResourceViewResolver();
        resolver.setPrefix("/WEB-INF/views/");
        resolver.setSuffix(".jsp");
        resolver.setExposedContextBeanNames("properties", "cacheVersion", "message");
        return resolver;
    }
    
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
            registry.addResourceHandler("/resources/**")
                    .addResourceLocations("/WEB-INF/views/resources/");
    }

    @Override
    public void addViewControllers(ViewControllerRegistry registry) {
        registry.addViewController("logout").setViewName("web/logout");
    };
    
    //register common intercepter for controller
    @Bean
    public CommonInterceptor commonInterceptor() {
    	return new CommonInterceptor();
    }
    
    @Bean
    public CustomHttpResponseInterceptor customHttpResponseInterceptor() {
    	return new CustomHttpResponseInterceptor();
    }
    
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
    	registry.addInterceptor(commonInterceptor()).excludePathPatterns("/fonts/**").excludePathPatterns("/images/**")
			.excludePathPatterns("/scripts/**").excludePathPatterns("/styles/**").excludePathPatterns("/views/**")
			.excludePathPatterns("/i18n/**").excludePathPatterns("/resources/**");
    	// .excludePathPatterns("/static-resource-root/**")
    	registry.addInterceptor(customHttpResponseInterceptor()); // to handle browser "back" button issue, form will  re-post and may have potential risk
    }
    
    //register mailSender and freemarker template engine for email sending
	@Bean
	public JavaMailSender mailSender() {
		JavaMailSenderImpl mailSender = new JavaMailSenderImpl();
		Properties mailProperties = new Properties();
		
		if (env.getProperty(AppConstants.SMTP_LOGIN_REQUIRED).equalsIgnoreCase("y")) {
			mailProperties.put("mail.smtp.auth", "true");
			mailSender.setUsername(env.getProperty(AppConstants.SMTP_USERNAME));
			mailSender.setPassword(env.getProperty(AppConstants.SMTP_PASSWORD));
		} else {
			mailProperties.put("mail.smtp.auth", "false");
		}
		
		mailSender.setJavaMailProperties(mailProperties);
		mailSender.setHost(env.getProperty(AppConstants.SMTP_HOST));
		
		return mailSender;
	}
	
	@Bean
	public FreeMarkerConfigurationFactoryBean freemarkerConfiguration() {
		FreeMarkerConfigurationFactoryBean configuration = new FreeMarkerConfigurationFactoryBean();
		configuration.setTemplateLoaderPath("/WEB-INF/email_templates");
		
		return configuration;
	}
	
	@Bean
    public JasperReportsConfig jasperReportsConfig() {
        JasperReportsConfigurer jasperReportsConfigurer = new JasperReportsConfigurer();
        jasperReportsConfigurer.setTemplatePath("/WEB-INF/report_templates");
        return jasperReportsConfigurer;
    }
    
	//register cache version bean (for cater client side's browser cache)
    @Bean
	public CacheVersion cacheVersion() {
		return new CacheVersion();
	}
    
	//register CtMessageUtil, ct customize method
    @Bean
    public Message message() {
    	return new Message();
    }
    
    //System (default) PasswordProtector
    @Bean
    public PasswordProtector passwordProtector() {
    	return new PasswordProtector(env);
    }

}