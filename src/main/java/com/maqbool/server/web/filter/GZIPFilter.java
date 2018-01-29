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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Zip filter class.
 * Compresses the response data if the HttpRequest has 'gzip' accept-encoding.
 * @author maqboolahmed
 *
 */
public class GZIPFilter implements Filter {

	private Logger logger = LoggerFactory.getLogger(getClass());
	
	public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain) 
		throws IOException, ServletException {
		if (req instanceof HttpServletRequest) {
			HttpServletRequest request = (HttpServletRequest) req;
			HttpServletResponse response = (HttpServletResponse) res;
			String ae = request.getHeader("accept-encoding");
			if (ae != null && ae.indexOf("gzip") != -1) {
				//logger.debug("gzip request... " + request.getRequestURI());
				GZIPResponseWrapper wrappedResponse = new GZIPResponseWrapper(response);
				chain.doFilter(req, wrappedResponse);
				wrappedResponse.finishResponse();
				return;
			}
		}
		chain.doFilter(req, res);
	}

	public void init(FilterConfig filterConfig) {
		
	}

	public void destroy() {
		
	}
}
