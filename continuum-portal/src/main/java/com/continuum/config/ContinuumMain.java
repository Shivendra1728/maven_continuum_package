package com.continuum.config;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

@SpringBootApplication
@EnableCaching
public class ContinuumMain {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		SpringApplication.run(ContinuumMain.class, args);
	}
}
	