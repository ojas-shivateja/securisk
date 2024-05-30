package com.insure.rfq.login.securityconfiguration;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import com.insure.rfq.login.entity.UserRegisteration;

public class UserDetailsInfo implements UserDetails {
  
  /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
private String email;
  private String password;
  
  public UserDetailsInfo(UserRegisteration usersNew)
  {
	  email=usersNew.getEmail();
	  password=usersNew.getPassword();
	  
  }
	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
		Set<GrantedAuthority>authorities= new HashSet<>();
		authorities.add(new SimpleGrantedAuthority("USER"));
		return  authorities;
	}

	@Override
	public String getPassword() {
		return password;
	}

	@Override
	public String getUsername() {
		return email;
	}

	@Override
	public boolean isAccountNonExpired() {
		return true;
	}

	@Override
	public boolean isAccountNonLocked() {
		return true;
	}

	@Override
	public boolean isCredentialsNonExpired() {
		return true;
	}

	@Override
	public boolean isEnabled() {
		return true;
	}

}
