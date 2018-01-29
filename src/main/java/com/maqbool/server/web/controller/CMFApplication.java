/**
 * 
 */
package com.maqbool.server.web.controller;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.support.SpringBootServletInitializer;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication(scanBasePackages = { "com.causeway.mobile.mwf",
		"com.causeway.mobile.commons" })
@EnableScheduling
public class CMFApplication extends SpringBootServletInitializer {

    @Override
    protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
        return application.sources(CMFApplication.class);
    }

	public static void main(String[] args) {
		SpringApplication.run(CMFApplication.class, args);
	}
}