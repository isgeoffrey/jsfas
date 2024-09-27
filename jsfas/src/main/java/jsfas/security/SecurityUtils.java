package jsfas.security;

import org.apache.shiro.mgt.SecurityManager;
import org.apache.shiro.subject.Subject;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;

import jsfas.common.constants.EduPersonAffiliationConstants;
import jsfas.common.constants.VoPersonAffiliationConstants;
import jsfas.common.json.CommonJson;
import jsfas.redis.domain.Token;

import java.util.Collection;
import java.util.List;

/**
 * Utility class for Spring Security.
 */
public final class SecurityUtils {

    private SecurityUtils() {
    }

    /**
     * Get the login of the current user.
     */
    public static String getCurrentLogin() {
        SecurityContext securityContext = SecurityContextHolder.getContext();
        Authentication authentication = securityContext.getAuthentication();
        
        return getCurrentLogin(authentication);
    }
    
    /**
     * Get the attributes of the current user.
     * @return login attribute (CommonJson)
     */
    public static CommonJson getCurrentLoginAttributes() {
        SecurityContext securityContext = SecurityContextHolder.getContext();
        Authentication authentication = securityContext.getAuthentication();
        
        return getCurrentLoginAttributes(authentication);
    }
    
    /**
     * Get the token of the current user.
     */
    public static String getCurrentToken() {
        SecurityContext securityContext = SecurityContextHolder.getContext();
        Authentication authentication = securityContext.getAuthentication();
        
        return getCurrentToken(authentication);
    }

    /**
     * Check if a user is authenticated.
     *
     * @return true if the user is authenticated, false otherwise
     */
    public static boolean isAuthenticated() {
        SecurityContext securityContext = SecurityContextHolder.getContext();
        Authentication authentication = securityContext.getAuthentication();
        
        if(authentication == null) {
            return false;
        }
        
        final Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();

        if (authorities != null) {
            for (GrantedAuthority authority : authorities) {
                if (authority.getAuthority().equals(AuthoritiesConstants.ANONYMOUS)) {
                    return false;
                }
            }
        }

        return true;
    }
    
    /**
     * Check if the login user is student.
     * @return true or false
     */
    @SuppressWarnings("unchecked")
	public static boolean isStudent() {
    	CommonJson userAttributes = getCurrentLoginAttributes();
    	List<String> eduPersonAffiliationList = userAttributes.get("eduPersonAffiliation", List.class);
    	
    	if (eduPersonAffiliationList != null && !eduPersonAffiliationList.isEmpty()) {
    		if (eduPersonAffiliationList.contains(EduPersonAffiliationConstants.STUDENT)) return true;
    	}
    	
    	List<String> voPersonAffiliationList = userAttributes.get("voPersonAffiliation", List.class);
    	if (voPersonAffiliationList != null && !voPersonAffiliationList.isEmpty()) {
    		if (voPersonAffiliationList.contains(VoPersonAffiliationConstants.STUDENT)) return true;
    	}
    	
    	return false;
    }
    
    /**
     * Check if the login user is undergraduate.
     * @return true or false
     */
    @SuppressWarnings("unchecked")
    public static boolean isUndergraduate() {
    	CommonJson userAttributes = getCurrentLoginAttributes();
    	List<String> voPersonAffiliationList = userAttributes.get("voPersonAffiliation", List.class);
    	if (voPersonAffiliationList != null && !voPersonAffiliationList.isEmpty()) {
    		if (voPersonAffiliationList.contains(VoPersonAffiliationConstants.UNDERGRADUATE)) return true;
    	}
    	return false;
    }
    
    /**
     * Check if the login user is postgraduate.
     * @return true or false
     */
    @SuppressWarnings("unchecked")
    public static boolean isPostgraudate() {
    	CommonJson userAttributes = getCurrentLoginAttributes();
    	List<String> voPersonAffiliationList = userAttributes.get("voPersonAffiliation", List.class);
    	if (voPersonAffiliationList != null && !voPersonAffiliationList.isEmpty()) {
    		if (voPersonAffiliationList.contains(VoPersonAffiliationConstants.UNDERGRADUATE)) return true;
    	}
    	return false;
    }
    
    /**
     * Check if the login user is staff.
     * @return true or false
     */
    @SuppressWarnings("unchecked")
    public static boolean isStaff() {
    	CommonJson userAttributes = getCurrentLoginAttributes();
    	List<String> eduPersonAffiliationList = userAttributes.get("eduPersonAffiliation", List.class);
    	
    	if (eduPersonAffiliationList != null && !eduPersonAffiliationList.isEmpty()) {
    		if (eduPersonAffiliationList.contains(EduPersonAffiliationConstants.STAFF)) return true;
    	}
    	
    	List<String> voPersonAffiliationList = userAttributes.get("voPersonAffiliation", List.class);
    	if (voPersonAffiliationList != null && !voPersonAffiliationList.isEmpty()) {
    		if (voPersonAffiliationList.contains(VoPersonAffiliationConstants.STAFF)) return true;
    	}
    	
    	return false;
    }
    
    /**
     * Get user department name from CAS (if any)
     * @return user department from CAS or "" (empty string) if cannot found
     */
    public static String getUserDept() {
    	CommonJson userAttributes = getCurrentLoginAttributes();
    	return userAttributes.getOrDefault("departmentNumber", "");
    }
    
    public static String getCurrentLogin(Authentication authentication) {
    	AppUserDetails springSecurityUser = null;
        String userName = null;
        if(authentication != null) {
        	if (authentication.getPrincipal() instanceof Token) {
        		Token token = (Token) authentication.getPrincipal();
        		userName = token.getUsername();
        	} else if (authentication.getPrincipal() instanceof UserDetails) {
                springSecurityUser = (AppUserDetails) authentication.getPrincipal();
                userName = springSecurityUser.getUsername();
            } else if (authentication.getPrincipal() instanceof String) {
                userName = (String) authentication.getPrincipal();
            }
        }

        return userName;
    }
    
    public static CommonJson getCurrentLoginAttributes(Authentication authentication) {
    	AppUserDetails springSecurityUser = null;
        CommonJson loginAttributes = new CommonJson();
        if(authentication != null) {
            if (authentication.getPrincipal() instanceof UserDetails) {
                springSecurityUser = (AppUserDetails) authentication.getPrincipal();
                loginAttributes.props().putAll(springSecurityUser.getAttributes());
            } else if (authentication.getPrincipal() instanceof String) {
            	loginAttributes.set("username", authentication.getPrincipal());
            }
        }

        return loginAttributes;
    }
    
    public static Subject getSubject() {
        return org.apache.shiro.SecurityUtils.getSubject();
    }
    
    public static SecurityManager getSecurityManager() {
        return org.apache.shiro.SecurityUtils.getSecurityManager();
    }
    
    public static void setSecurityManager(SecurityManager securityManager) {
        org.apache.shiro.SecurityUtils.setSecurityManager(securityManager);
    }
    
    public static String getCurrentToken(Authentication authentication) {
        String tokenStr = "";
        
        if (authentication != null) {
        	if (authentication.getPrincipal() instanceof Token) {
        		Token token = (Token) authentication.getPrincipal();
        		tokenStr = token.getId();
        	}
        }

        return tokenStr;
    }
    
}
