package jsfas.web.listener;

import org.apache.shiro.realm.Realm;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.mgt.SecurityManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.Conditional;
import org.springframework.security.authentication.event.AbstractAuthenticationEvent;
import org.springframework.security.authentication.event.AuthenticationSuccessEvent;
import org.springframework.security.authentication.event.InteractiveAuthenticationSuccessEvent;
import org.springframework.stereotype.Component;

import jsfas.common.condition.RBACCondition;
import jsfas.security.SecurityUtils;
import jsfas.security.ShiroUtils;

@Component
@Conditional(value = RBACCondition.class)
public class ShiroSpringSecurityEventListener implements ApplicationListener<AbstractAuthenticationEvent> {

    @Autowired
    private Realm realm;
    
    @Autowired
    private SecurityManager securityManager;
    
    @Autowired
    private HttpServletRequest request;
    
    @Autowired
    private HttpServletResponse response;
    
    @Override
    public void onApplicationEvent(AbstractAuthenticationEvent event) {
        // TODO Auto-generated method stub
        if(event instanceof AuthenticationSuccessEvent || event instanceof InteractiveAuthenticationSuccessEvent) {
            String username = SecurityUtils.getCurrentLogin(event.getAuthentication());
            ShiroUtils.bindSubject(username, realm, securityManager, request, response);
            SecurityUtils.getSubject().login(new UsernamePasswordToken(username, username));
        }
    }

}
