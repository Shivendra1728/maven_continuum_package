package com.continuum.config;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@EntityScan(basePackages = {"com.continuum.repos.entity"}) 
@ComponentScan(basePackages = {"com.continuum"})
@EnableJpaRepositories(basePackages = {"com.continuum.repos.repositories"})
public class ContinuumMain {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
SpringApplication.run(ContinuumMain.class, args);
	}

}
