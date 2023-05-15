package com.continuum.config;

import org.modelmapper.ModelMapper;
import org.modelmapper.config.Configuration.AccessLevel;
import org.modelmapper.convention.MatchingStrategies;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

@Configuration
@EntityScan(basePackages = {"com.continuum.repos.entity"}) 
@ComponentScan(basePackages = {"com.continuum","com.di.commons"})
@EnableJpaRepositories(basePackages = {"com.continuum.repos.repositories"})
public class ContinuumConfig {

	@Bean
	public ModelMapper modelMapper() {
	   ModelMapper modelMapper= new ModelMapper();
	   modelMapper.getConfiguration().setAmbiguityIgnored(true);
	   modelMapper.getConfiguration()
       .setMatchingStrategy(MatchingStrategies.STRICT);
	   modelMapper.getConfiguration()
	   .setFieldMatchingEnabled(true)
	   .setFieldAccessLevel(AccessLevel.PRIVATE);
	   return modelMapper;
	}
	
	
	@Bean
    public CorsFilter corsFilter() {
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        CorsConfiguration config = new CorsConfiguration();
        config.addAllowedOrigin("*");
        config.addAllowedMethod("*");
        config.addAllowedHeader("*");
        source.registerCorsConfiguration("/**", config);
        return new CorsFilter(source);
    }
}
