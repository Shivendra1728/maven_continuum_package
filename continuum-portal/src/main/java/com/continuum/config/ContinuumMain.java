package com.continuum.config;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;

@SpringBootApplication(exclude = {DataSourceAutoConfiguration.class })
//@EntityScan(basePackages = "com.continuum.repos.entity")
public class ContinuumMain {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		SpringApplication.run(ContinuumMain.class, args);
	
	
}
}