/**
 * 
 */
package com.maqbool.server.services;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import com.maqbool.server.web.security.UserDetails;

/**
 * @author maqboolahmed
 */
@Component
public class BaseServiceImpl {

	protected Logger logger = LoggerFactory.getLogger(getClass());

	@Autowired
	protected Environment env;
	
	protected UserDetails getUserDetails() {
		UserDetails userDetails = null;
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		userDetails = (UserDetails) authentication.getPrincipal();
		return userDetails;
	}
	
}
