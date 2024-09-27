package jsfas.security;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.jasig.cas.client.validation.Assertion;
import org.springframework.security.cas.userdetails.AbstractCasAssertionUserDetailsService;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.util.Assert;

/**
 * Authenticate a user from the database.
 */
public class CustomUserDetailsService extends AbstractCasAssertionUserDetailsService {


    private static final String NON_EXISTENT_PASSWORD_VALUE = "NO_PASSWORD";

    private final String[] attributes;

    private boolean convertToUpperCase = true;

    public CustomUserDetailsService(final String[] attributes) {
        Assert.notNull(attributes, "attributes cannot be null.");
        Assert.isTrue(attributes.length > 0, "At least one attribute is required to retrieve roles from.");
        this.attributes = attributes;
    }

    @SuppressWarnings("unchecked")
    @Override
    protected UserDetails loadUserDetails(final Assertion assertion) {

        final List<GrantedAuthority> grantedAuthorities = new ArrayList<GrantedAuthority>();
        
        grantedAuthorities.add(new SimpleGrantedAuthority(AuthoritiesConstants.USER));
        
        ArrayList<String> roles = new ArrayList<String>();
        
        for (String attrName : this.attributes) {
        	List<String> attributesList = null;
        	Object attrObj = assertion.getPrincipal().getAttributes().get(attrName);
        	
        	if (List.class.isInstance(attrObj)) {
        		attributesList = (List<String>) attrObj;
        	}
        	
        	if (String.class.isInstance(attrObj)) {
        		attributesList = Arrays.asList(new String[] {(String) attrObj});
        	}
        	
        	if (attributesList != null) {
	        	roles.addAll(attributesList);
	        	roles.replaceAll(String::toUpperCase);
        	}
        }
        
        roles.replaceAll(String::toUpperCase);
        
        if(roles.contains("STAFF") 
        		|| roles.contains("DEPARTMENTAL")
        		|| roles.contains("DEPARTMENT")
        		|| roles.contains("PROJECT")
        		|| roles.contains("EMERITUS")
        		|| roles.contains("AFFILIATE")
        		|| roles.contains("IS SUPPORT")){
            	grantedAuthorities.add(new SimpleGrantedAuthority(AuthoritiesConstants.STAFF));
        }
            
        if(roles.contains("IDM PERSON") && roles.contains("STAFF")){
        	grantedAuthorities.add(new SimpleGrantedAuthority(AuthoritiesConstants.ADMIN));
        }
        
        return new AppUserDetails(assertion.getPrincipal().getName(), NON_EXISTENT_PASSWORD_VALUE, true, true, true, true, grantedAuthorities, assertion.getPrincipal().getAttributes());
    }

    /**
     * Converts the returned attribute values to uppercase values.
     *
     * @param convertToUpperCase true if it should convert, false otherwise.
     */
    public void setConvertToUpperCase(final boolean convertToUpperCase) {
        this.convertToUpperCase = convertToUpperCase;
    }
}
