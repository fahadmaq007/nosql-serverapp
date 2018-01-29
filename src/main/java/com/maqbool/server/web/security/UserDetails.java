package com.maqbool.server.web.security;

import java.util.Collection;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

public class UserDetails implements org.springframework.security.core.userdetails.UserDetails {

	/**
	 * 
	 */
	private static final long serialVersionUID = -3503023310043256890L;
	private String orgId;
	private String username;
	
	public UserDetails(String orgId, String username) {
		this.orgId = orgId;
		this.username = username;
	}

	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
		return null;
	}

	@Override
	public String getPassword() {
		return null;
	}

	@Override
	public String getUsername() {
		return this.username;
	}

	@Override
	public boolean isAccountNonExpired() {
		return false;
	}

	@Override
	public boolean isAccountNonLocked() {
		return false;
	}

	@Override
	public boolean isCredentialsNonExpired() {
		return false;
	}

	@Override
	public boolean isEnabled() {
		return false;
	}

	public String getOrgId() {
		return orgId;
	}
	
	public static void setAuthentication(UserDetails userDetails) {
		SecurityContext context = SecurityContextHolder.getContext();
	    Authentication authentication =  new UsernamePasswordAuthenticationToken(userDetails, null);
	    context.setAuthentication(authentication);
	}
	
	public static UserDetails getInstance() {
		SecurityContext context = SecurityContextHolder.getContext();
		Authentication auth = context.getAuthentication();
		UserDetails details = (UserDetails) auth.getPrincipal();
		return details;
	}
}
