package com.continuum.config;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.web.client.RestTemplate;

@SpringBootApplication(exclude = {DataSourceAutoConfiguration.class })
//@EntityScan(basePackages = "com.continuum.repos.entity")
public class ContinuumMain {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		SpringApplication.run(ContinuumMain.class, args);
		
		// Access the Actuator "dependencies" endpoint programmatically
        RestTemplate restTemplate = new RestTemplate();
        String endpointUrl = "http://localhost:8080/actuator/dependencies";
        String response = restTemplate.getForObject(endpointUrl, String.class);

        // Process the response here
        System.out.println(response);
	}
	
	
}
