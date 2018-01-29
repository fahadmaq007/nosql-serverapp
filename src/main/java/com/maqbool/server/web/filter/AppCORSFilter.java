/**
 * 
 */
package com.maqbool.server.web.filter;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Component;

/**
 * @author niraj.gupta
 *
 */
@Component
public class AppCORSFilter implements Filter {

	/**
	 * {@inheritDoc}
	 */
	public void destroy() {
		
	}

	/**
	 * {@inheritDoc}
	 */
	public void doFilter(ServletRequest req, ServletResponse res,
			FilterChain chain) throws IOException, ServletException {
		HttpServletResponse response = (HttpServletResponse)res;
		HttpServletRequest request = (HttpServletRequest)req;
		response.setHeader("Access-Control-Allow-Origin", request.getHeader("origin"));
		response.setHeader("Access-Control-Allow-Methods", "POST, GET, PUT, PATCH, OPTIONS, DELETE");
		response.setHeader("Access-Control-Max-Age", "3600");
		response.setHeader("Access-Control-Allow-Headers",
		        "Origin,X-Requested-With, Content-Type, Accept, Authorization, X-Bucket, X-UserName, X-Auth-OrganizationId");
		chain.doFilter(req, res);
	}

	/**
	 * {@inheritDoc}
	 */
	public void init(FilterConfig arg0) throws ServletException {
		
	}

}
