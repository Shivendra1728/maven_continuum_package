package com.continuum.config;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
public class ContinuumMain {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		SpringApplication.run(ContinuumMain.class, args);
	}
}
