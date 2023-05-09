package com.continuum.config;

import org.modelmapper.ModelMapper;
import org.modelmapper.config.Configuration.AccessLevel;
import org.modelmapper.convention.MatchingStrategies;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
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
}
