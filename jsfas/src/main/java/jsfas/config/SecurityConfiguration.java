package jsfas.config;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.inject.Inject;
import javax.servlet.Filter;

import org.jasig.cas.client.session.SingleSignOutFilter;
import org.jasig.cas.client.validation.Cas20ServiceTicketValidator;
import org.jasig.cas.client.validation.Saml11TicketValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Conditional;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpMethod;
import org.springframework.security.cas.ServiceProperties;
import org.springframework.security.cas.authentication.CasAssertionAuthenticationToken;
import org.springframework.security.cas.authentication.CasAuthenticationProvider;
import org.springframework.security.cas.web.CasAuthenticationEntryPoint;
import org.springframework.security.cas.web.CasAuthenticationFilter;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.AuthenticationUserDetailsService;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.logout.LogoutFilter;
import org.springframework.security.web.authentication.logout.LogoutHandler;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.security.web.authentication.session.SessionAuthenticationStrategy;
import org.springframework.security.web.authentication.session.SessionFixationProtectionStrategy;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

import jsfas.common.CustomAccessDeniedHandler;
import jsfas.common.condition.RBACCondition;
import jsfas.common.constants.AppConstants;
import jsfas.common.constants.RestURIConstants;
import jsfas.db.main.persistence.service.RedisService;
import jsfas.security.CustomUserDetailsService;
import jsfas.web.filter.ShiroSubjectBindingFilter;
import jsfas.web.filter.TokenAuthenticationFilter;
import jsfas.web.logout.ShiroLogoutHandler;

/**
 * @author iseric
 * @since 12/5/2016
 * @see spring security setting for CAS
 */
@Configuration
@EnableWebSecurity
public class SecurityConfiguration extends WebSecurityConfigurerAdapter {
	
	@Autowired
    private RedisService redisService;

	private static final String CAS_URL_LOGIN = "cas.service.login";
	private static final String CAS_URL_LOGOUT = "cas.service.logout";
	private static final String CAS_URL_PREFIX = "cas.url.prefix";
	private static final String CAS_SERVICE_URL = "app.service.security";
	private static final String APP_ADMIN_USER_NAME = "app.admin.userName";

	@Autowired
	private Environment env;

	@Bean
	public Set<String> adminList() {
		Set<String> admins = new HashSet<String>();
		String adminUserName = env.getProperty(APP_ADMIN_USER_NAME);

		admins.add("admin");
		if((adminUserName != null) && (!adminUserName.isEmpty())) {
			admins.add(adminUserName);
		}
		return admins;
	}

	@Bean
	public ServiceProperties serviceProperties() {
		ServiceProperties sp = new ServiceProperties();
		sp.setService(env.getRequiredProperty(CAS_SERVICE_URL));
		sp.setSendRenew(false);
		return sp;
	}

	
	@Bean
	public CasAuthenticationProvider casAuthenticationProvider() {
		CasAuthenticationProvider casAuthenticationProvider = new CasAuthenticationProvider();
		casAuthenticationProvider.setAuthenticationUserDetailsService(customUserDetailsService());
		casAuthenticationProvider.setServiceProperties(serviceProperties());
		casAuthenticationProvider.setTicketValidator(casSamlServiceTicketValidator());
		casAuthenticationProvider.setKey("casAuthenticationProviderKey");
		return casAuthenticationProvider;
	}

	@Bean
	public AuthenticationUserDetailsService<CasAssertionAuthenticationToken> customUserDetailsService() {
	    String[] attributes = new String[] {"eduPersonAffiliation", "voPersonAffiliation"};

	    CustomUserDetailsService userDetailsService = new CustomUserDetailsService(attributes);
	    userDetailsService.setConvertToUpperCase(false);

        return userDetailsService;
	}

	@Bean
	public SessionAuthenticationStrategy sessionStrategy() {
		SessionAuthenticationStrategy sessionStrategy = new SessionFixationProtectionStrategy();
		return sessionStrategy;
	}

	@Bean
	public Saml11TicketValidator casSamlServiceTicketValidator() {
		Saml11TicketValidator validator = new Saml11TicketValidator(env.getRequiredProperty(CAS_URL_PREFIX));
		validator.setTolerance(10000L);
		return validator;
	}

	@Bean
	public Cas20ServiceTicketValidator cas20ServiceTicketValidator() {
		Cas20ServiceTicketValidator validator = new Cas20ServiceTicketValidator(env.getRequiredProperty(CAS_URL_PREFIX));
		return validator;
	}

	@Bean
	public CasAuthenticationFilter casAuthenticationFilter() throws Exception {
		CasAuthenticationFilter casAuthenticationFilter = new CasAuthenticationFilter();
		casAuthenticationFilter.setAuthenticationManager(authenticationManager());
		casAuthenticationFilter.setSessionAuthenticationStrategy(sessionStrategy());
		return casAuthenticationFilter;
	}

    @Bean
	public CasAuthenticationEntryPoint casAuthenticationEntryPoint() {
		CasAuthenticationEntryPoint casAuthenticationEntryPoint = new CasAuthenticationEntryPoint();
		casAuthenticationEntryPoint.setLoginUrl(env.getRequiredProperty(CAS_URL_LOGIN));
		casAuthenticationEntryPoint.setServiceProperties(serviceProperties());
		return casAuthenticationEntryPoint;
	}

	@Bean
	public SingleSignOutFilter singleSignOutFilter() {
		SingleSignOutFilter singleSignOutFilter = new SingleSignOutFilter();
		singleSignOutFilter.setIgnoreInitConfiguration(true);
		singleSignOutFilter.setCasServerUrlPrefix(env.getRequiredProperty(CAS_URL_PREFIX));
		return singleSignOutFilter;
	}

	@Bean
	public LogoutFilter requestCasGlobalLogoutFilter() {
	    List<LogoutHandler> logoutHandlers = new ArrayList<>();
	    if(env.getProperty(AppConstants.RBAC_PERM_ENABLED, Boolean.class, false)) {
	        logoutHandlers.add(new ShiroLogoutHandler());
	    }
	    logoutHandlers.add(new SecurityContextLogoutHandler());
		LogoutFilter logoutFilter = new LogoutFilter(env.getRequiredProperty(CAS_URL_LOGOUT), logoutHandlers.toArray(new LogoutHandler[0]));
		logoutFilter.setLogoutRequestMatcher(new AntPathRequestMatcher("/web/logout", "GET"));
		return logoutFilter;
	}

	@Inject
	public void configureGlobal(AuthenticationManagerBuilder auth) throws Exception {
		auth.authenticationProvider(casAuthenticationProvider());
	}

	@Bean
	public AccessDeniedHandler accessDeniedHandler() {
		CustomAccessDeniedHandler accessDeniedHandler = new CustomAccessDeniedHandler();
		accessDeniedHandler.setEnv(env);
		return accessDeniedHandler;
	};
	
	@Bean
	@Conditional(value = RBACCondition.class)
	public Filter shiroSubjectBindingFilter() {
	    return new ShiroSubjectBindingFilter();
	}
	
	@Bean
	public TokenAuthenticationFilter tokenAuthenticationFilter() throws Exception {
		TokenAuthenticationFilter tokenAuthenticationFilter = new TokenAuthenticationFilter();
		tokenAuthenticationFilter.setRedisService(redisService);
		return tokenAuthenticationFilter;
	};
	
   @Override
    public void configure(WebSecurity web) throws Exception {
        web.ignoring()
//        	.antMatchers(RestURIConstants.CAS_URL)
//        	.antMatchers(RestURIConstants.CAS_AUTH)
//        	.antMatchers(RestURIConstants.LOGIN)
//        	.antMatchers(RestURIConstants.CAS_LOGOUT)
            .antMatchers("/*") //temporary to bypass all auth and validation
            .antMatchers("/**") //temporary to bypass all auth and validation
        	.antMatchers("/fonts/**").antMatchers("/images/**").antMatchers("/scripts/**")
			.antMatchers("/styles/**").antMatchers("/views/**").antMatchers("/i18n/**").antMatchers("/resources/**")
			;
  }
	
	@Override
	protected void configure(HttpSecurity http) throws Exception {
    	//http.cors().configurationSource(request -> corsConfiguration);	
    	http.cors().and().csrf().disable();
    	http.logout()
    	.invalidateHttpSession(true)
    	.deleteCookies("JSESSIONID").disable();
    	http.addFilterAfter(tokenAuthenticationFilter(), UsernamePasswordAuthenticationFilter.class);  	
	    http.addFilterAfter(shiroSubjectBindingFilter(), TokenAuthenticationFilter.class);
	    
    	http.authorizeRequests().anyRequest().authenticated().and().httpBasic();
    }
}
