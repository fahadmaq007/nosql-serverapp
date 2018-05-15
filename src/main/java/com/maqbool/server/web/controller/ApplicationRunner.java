/**
 * 
 */
package com.maqbool.server.web.controller;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.support.SpringBootServletInitializer;

@SpringBootApplication(scanBasePackages = { "com.maqbool.server" })
public class ApplicationRunner extends SpringBootServletInitializer {

	
    @Override
    protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
        return application.sources(ApplicationRunner.class);
    }

	public static void main(String[] args) {
		SpringApplication.run(ApplicationRunner.class, args);
	}
}