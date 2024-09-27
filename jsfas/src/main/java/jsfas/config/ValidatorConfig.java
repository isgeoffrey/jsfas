package jsfas.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.validation.SmartValidator;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;

/**
 * @author iseric
 * @since 12/5/2016
 * @see This is a Class for Dependency Injection Bean setting
 */
@Configuration
public class ValidatorConfig {

	@Bean
	public SmartValidator smartValidator() {
    	return new LocalValidatorFactoryBean();
    }
	
}
