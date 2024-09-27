package jsfas.web.filter;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.shiro.realm.Realm;
import org.apache.shiro.subject.Subject;
import org.apache.shiro.util.ThreadContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.apache.commons.lang3.StringUtils;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.mgt.SecurityManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.filter.OncePerRequestFilter;

import jsfas.db.main.persistence.service.RedisService;
import jsfas.redis.domain.Token;
import jsfas.security.SecurityUtils;
import jsfas.security.ShiroUtils;
import jsfas.web.filter.ShiroSubjectBindingFilter;

public class ShiroSubjectBindingFilter extends OncePerRequestFilter {
	private final String HEADER = "Authorization";
	private final String PREFIX = "Bearer ";

    @Autowired
    private Realm realm;
    
    @Autowired
    private SecurityManager securityManager;

    @Autowired
	private RedisService redisService;
    
	private static final Logger log = LoggerFactory.getLogger(ShiroSubjectBindingFilter.class);
    
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        // TODO Auto-generated method stub
        try {
            bind(request, response);

//			log.info("asdf: {}");
	    	if(SecurityUtils.isAuthenticated()) {
	        	Subject subject = SecurityUtils.getSubject();
	        	
	        	SecurityManager securityManager = SecurityUtils.getSecurityManager();
	        	
		    	String tokenStr = StringUtils.defaultString(request.getHeader(HEADER)).replace(PREFIX, "");
//				log.info("token: {}", tokenStr);
				Token token = (Token) redisService.findById("TOKEN:" + tokenStr);
		    	
		    	UsernamePasswordToken authenticationToken = new UsernamePasswordToken(token.getUsername(), token.getUsername());
	    		securityManager.login(subject, authenticationToken);
	    	}

            filterChain.doFilter(request, response);
        } finally {
            unbind();
        }

    }
    
    protected void bind(HttpServletRequest request, HttpServletResponse response) {
        if(SecurityUtils.isAuthenticated()) {
            ShiroUtils.bindSubject(SecurityUtils.getCurrentLogin(), realm, securityManager, request, response);
        } else {
            ThreadContext.bind(securityManager);
        }
    }
    
    protected void unbind() {
        ThreadContext.unbindSubject();
        ThreadContext.unbindSecurityManager();
    }

}
