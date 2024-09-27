package jsfas.security;

import org.apache.shiro.subject.SimplePrincipalCollection;
import org.apache.shiro.subject.support.SubjectThreadState;
import org.apache.shiro.web.session.HttpServletSession;
import org.apache.shiro.web.subject.support.WebDelegatingSubject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.shiro.mgt.SecurityManager;
import org.apache.shiro.realm.Realm;

public final class ShiroUtils {

    private static final Logger log = LoggerFactory.getLogger(ShiroUtils.class);
    
    protected ShiroUtils() {
        
    }
    
    public static void bindSubject(String username, Realm realm, SecurityManager securityManager, HttpServletRequest request, HttpServletResponse response) {
        String host = request.getRemoteHost();
        
//        log.info("Binding subject for principal {} realm {} from host {}", username,  realm.getName(), host);
        new SubjectThreadState(new WebDelegatingSubject(
                new SimplePrincipalCollection(username, realm.getName()), true, host, 
                new HttpServletSession(request.getSession(), host), true, 
                request, response, securityManager)).bind();
    }
}
