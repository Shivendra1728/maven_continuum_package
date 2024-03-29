package com.di.integration.config;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
@SpringBootApplication
@ComponentScan(basePackages="com.continuum.tenant.repos.repositories")
public class IntegrationMain {

	public static void main(String[] args) {
		SpringApplication.run(IntegrationMain.class,args);
	}
	
}
