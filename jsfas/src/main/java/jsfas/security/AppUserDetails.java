package jsfas.security;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;

public class AppUserDetails extends User implements UserDetails {

	private static final long serialVersionUID = -4777124807325532850L;

	private List<String> roles;

	private Map<String, Object> attributes;	
	
	public AppUserDetails(String username, String password, Collection<? extends GrantedAuthority> authorities, Map<String, Object> attributes) {
		this(username, password, true, true, true, true, authorities, attributes);
	}
	
	public AppUserDetails(String username, String password, boolean enabled,
			boolean accountNonExpired, boolean credentialsNonExpired,
			boolean accountNonLocked, Collection<? extends GrantedAuthority> authorities, Map<String, Object> attributes) {
		super(username, password, accountNonLocked, accountNonLocked, accountNonLocked, accountNonLocked, authorities);
		
		this.roles = new ArrayList<>();
		for (GrantedAuthority authority : authorities) {
			this.roles.add(authority.getAuthority());
		}
		this.attributes = Collections.unmodifiableMap(attributes);
	}

	public Map<String, Object> getAttributes() {
		return Collections.unmodifiableMap(attributes);
	}

	public void setAttributes(Map<String, Object> attributes) {
		this.attributes = Collections.unmodifiableMap(attributes);
	}

	@Override
	public String toString() {
		return "AppUserDetails(UserDetails) [username=" + getUsername() + ", authorities=" + getAuthorities().toString() + ", isAccountNonExpired()="
				+ isAccountNonExpired() + ", isAccountNonLocked()=" + isAccountNonLocked()
				+ ", isCredentialsNonExpired()=" + isCredentialsNonExpired() + ", isEnabled()=" + isEnabled() + "]";
	}

}
