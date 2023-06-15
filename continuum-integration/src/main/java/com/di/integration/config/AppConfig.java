package com.di.integration.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.web.client.RestTemplate;

@Configuration
@PropertySource("classpath:p21/application-integration-p21.properties")
//@EnableCaching
public class AppConfig {
	
	@Bean
	RestTemplate getRestTemplate() {
		return new RestTemplate();
	}
	
	
}
