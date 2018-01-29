package com.maqbool.server.web.config;

import javax.servlet.Filter;
import javax.servlet.MultipartConfigElement;

import org.springframework.boot.web.servlet.MultipartConfigFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

import com.maqbool.server.web.filter.AppCORSFilter;
import com.maqbool.server.web.filter.GZIPFilter;
import com.maqbool.server.web.security.AppAuthInterceptor;

@Configuration
public class WebConfig extends WebMvcConfigurerAdapter {

	private static final String MAX_UPLOAD_SIZE = "20mb";
	
	@Override
	public void addInterceptors(InterceptorRegistry registry) {
//		registry.addInterceptor(appAuthInterceptor());
	}

	@Bean
	public AppAuthInterceptor appAuthInterceptor() {
		return new AppAuthInterceptor();
	}

	@Bean
	public Filter gzipFilter() {
		return new GZIPFilter();
	}
	
	@Bean
	public Filter appCORSFilter() {
		return new AppCORSFilter();
	}
	
	@Bean
    public MultipartConfigElement multipartConfigElement() {
        MultipartConfigFactory factory = new MultipartConfigFactory();
        factory.setMaxFileSize(MAX_UPLOAD_SIZE);
        factory.setMaxRequestSize(MAX_UPLOAD_SIZE);
        return factory.createMultipartConfig();
    }
}