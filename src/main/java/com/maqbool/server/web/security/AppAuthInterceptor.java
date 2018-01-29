package com.maqbool.server.web.security;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

@Component
public class AppAuthInterceptor extends HandlerInterceptorAdapter {

	private Logger logger = LoggerFactory.getLogger(getClass());

	private static final String OPTIONS = "OPTIONS";
	
	@Override
	public boolean preHandle(HttpServletRequest request,
			HttpServletResponse response, Object handler) throws Exception {

		String method = request.getMethod();
		if (OPTIONS.equalsIgnoreCase(method)) {
    		return true;
    	}
		
		final String orgId = request.getHeader("x-auth-organizationid");
		final String userEmail = request.getHeader("x-auth-email");
		final String orgName = request.getHeader("x-organization_name");
		
		logger.debug("bucket Name === " + orgId + " === " + orgName);
		UserDetails userDetails = new UserDetails(orgId, userEmail);
	    UserDetails.setAuthentication(userDetails);
		return true;
	}
}
