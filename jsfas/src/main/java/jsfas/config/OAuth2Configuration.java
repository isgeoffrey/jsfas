package jsfas.config;

import java.util.Arrays;

import javax.inject.Inject;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.core.env.Environment;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.security.oauth2.client.DefaultOAuth2ClientContext;
import org.springframework.security.oauth2.client.OAuth2ClientContext;
import org.springframework.security.oauth2.client.OAuth2RestTemplate;
import org.springframework.security.oauth2.client.resource.OAuth2ProtectedResourceDetails;
import org.springframework.security.oauth2.client.token.grant.password.ResourceOwnerPasswordResourceDetails;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableOAuth2Client;

import jsfas.common.PasswordProtector;
import jsfas.common.constants.AppConstants;

@Configuration
@EnableOAuth2Client
public class OAuth2Configuration {

	public static final String JSSV_ISO_USERNAME = "jssv.oauth2.sviso.username";
	public static final String JSSV_ISO_PASSWORD = "jssv.oauth2.sviso.password";
	
	public static final String JSSV_SIS_USERNAME = "jssv.oauth2.svisstdt.username";
	public static final String JSSV_SIS_PASSWORD = "jssv.oauth2.svisstdt.password";
	
	private static final String JSSV_HRIS_USERNAME = "jssv_hris.oauth2.username";
	private static final String JSSV_HRIS_PASSWORD = "jssv_hris.oauth2.password";
	
	public static final String JSSV_ACCESSTOKENURI = "jssv.oauth2.accesstokenuri";
	public static final String JSSV_CLIENTID = "jssv.oauth2.clientid";
	public static final String JSSV_CLIENTSECRET = "jssv.oauth2.clientsecret";
    
	@Autowired
    private Environment env;
    
    @Autowired
	PasswordProtector pp;
    
	@Bean
    public OAuth2ClientContext clientContextSis() {
    	return new DefaultOAuth2ClientContext();
    }
	
	//special case allow call even user not login
	public class ExtendedResourceOwnerPasswordResourceDetails extends ResourceOwnerPasswordResourceDetails {
		@Override
		public boolean isClientOnly() {
		    return true;
		}
	}
	
	@Bean
	public OAuth2ProtectedResourceDetails jssvSisOAuth2ProtectedResourceDetails() {
		ExtendedResourceOwnerPasswordResourceDetails resourceDetails = new ExtendedResourceOwnerPasswordResourceDetails();
		resourceDetails.setAccessTokenUri(env.getRequiredProperty(AppConstants.JSSV_LINK) + env.getRequiredProperty(JSSV_ACCESSTOKENURI));
		resourceDetails.setUsername(env.getRequiredProperty(JSSV_SIS_USERNAME));
		resourceDetails.setPassword(pp.decrypt(env.getRequiredProperty(JSSV_SIS_PASSWORD)));
		//resourceDetails.setPassword(env.getRequiredProperty(JSSV_SIS_PASSWORD));
		resourceDetails.setClientId(env.getRequiredProperty(JSSV_CLIENTID));
		resourceDetails.setClientSecret(pp.decrypt(env.getRequiredProperty(JSSV_CLIENTSECRET)));
		//resourceDetails.setClientSecret(env.getRequiredProperty(JSSV_CLIENTSECRET));
		resourceDetails.setGrantType("password");
		resourceDetails.setScope(Arrays.asList("read"));
		
		return resourceDetails;
	}
	
	@Bean
    public OAuth2RestTemplate jssvSisRestTemplate() {
        OAuth2RestTemplate restTemplate = new OAuth2RestTemplate(jssvSisOAuth2ProtectedResourceDetails(), clientContextSis());
        restTemplate.setMessageConverters(Arrays.asList(new MappingJackson2HttpMessageConverter()));
        return restTemplate;
    }

    @Bean
    public OAuth2ProtectedResourceDetails jssvIsoOAuth2ProtectedResourceDetails() {
        ResourceOwnerPasswordResourceDetails resourceDetails = new ResourceOwnerPasswordResourceDetails();
        resourceDetails.setAccessTokenUri(env.getRequiredProperty(AppConstants.JSSV_LINK) + env.getRequiredProperty(JSSV_ACCESSTOKENURI));
        resourceDetails.setUsername(env.getRequiredProperty(JSSV_ISO_USERNAME));
        resourceDetails.setPassword(pp.decrypt(env.getRequiredProperty(JSSV_ISO_PASSWORD)));
        //resourceDetails.setPassword(env.getRequiredProperty(JSSV_ISO_PASSWORD));
        resourceDetails.setClientId(env.getRequiredProperty(JSSV_CLIENTID));
        resourceDetails.setClientSecret(pp.decrypt(env.getRequiredProperty(JSSV_CLIENTSECRET)));
		//resourceDetails.setClientSecret(env.getRequiredProperty(JSSV_CLIENTSECRET));
        resourceDetails.setGrantType("password");
        resourceDetails.setScope(Arrays.asList("read"));
        return resourceDetails;
    }
    
    @Bean
    public OAuth2ClientContext clientContextIso() {
    	return new DefaultOAuth2ClientContext();
    }
    
    @Bean
    public OAuth2RestTemplate jssvIsoOAuth2RestTemplate() {
        OAuth2RestTemplate restTemplate = new OAuth2RestTemplate(jssvIsoOAuth2ProtectedResourceDetails(), clientContextIso());
        restTemplate.setMessageConverters(Arrays.asList(new MappingJackson2HttpMessageConverter()));
        return restTemplate;
    }
    
    @Bean
    public OAuth2ClientContext clientContextIsoJob() {
    	return new DefaultOAuth2ClientContext();
    }
    
    @Bean
    @Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE, proxyMode = ScopedProxyMode.TARGET_CLASS)
    public OAuth2RestTemplate jssvIsoOAuth2RestTemplateForJob() {
        OAuth2RestTemplate restTemplate = new OAuth2RestTemplate(jssvIsoOAuth2ProtectedResourceDetails(), clientContextIsoJob());
        restTemplate.setMessageConverters(Arrays.asList(new MappingJackson2HttpMessageConverter()));
        return restTemplate;
    }
    
    @Bean
    public OAuth2ClientContext clientContextHris() {
    	return new DefaultOAuth2ClientContext();
    }
	
	@Bean
	public OAuth2ProtectedResourceDetails jssvHrisOAuth2ProtectedResourceDetails() {
		ResourceOwnerPasswordResourceDetails resourceDetails = new ResourceOwnerPasswordResourceDetails();
		resourceDetails.setAccessTokenUri(env.getRequiredProperty(AppConstants.JSSV_LINK) + env.getRequiredProperty(JSSV_ACCESSTOKENURI));
		resourceDetails.setUsername(env.getRequiredProperty(JSSV_HRIS_USERNAME));
		resourceDetails.setPassword(pp.decrypt(env.getRequiredProperty(JSSV_HRIS_PASSWORD)));
		resourceDetails.setClientId(env.getRequiredProperty(JSSV_CLIENTID));
		resourceDetails.setClientSecret(pp.decrypt(env.getRequiredProperty(JSSV_CLIENTSECRET)));
		resourceDetails.setGrantType("password");
		resourceDetails.setScope(Arrays.asList("read"));
		
		return resourceDetails;
	}
	
    @Bean
    //@Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE, proxyMode = ScopedProxyMode.TARGET_CLASS)
    public OAuth2RestTemplate jssvHrisRestTemplateForJob() {
        OAuth2RestTemplate restTemplate = new OAuth2RestTemplate(jssvHrisOAuth2ProtectedResourceDetails(), clientContextHris());
        restTemplate.setMessageConverters(Arrays.asList(new MappingJackson2HttpMessageConverter()));
        return restTemplate;
    }
}
