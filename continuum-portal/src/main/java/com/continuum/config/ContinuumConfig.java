package com.continuum.config;

import org.modelmapper.ModelMapper;
import org.modelmapper.config.Configuration.AccessLevel;
import org.modelmapper.convention.MatchingStrategies;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

import com.continuum.serviceImpl.IntegrationConstantsProvider;
import com.di.integration.p21.service.TenantInfoProviderService;

@Configuration
//@EntityScan(basePackages = {"com.continuum.repos.entity"}) 
@ComponentScan(basePackages = {"com.continuum","com.di"})
@EnableJpaRepositories(basePackages = {"com.continuum.repos.repositories","com.continuum.serviceImpl","com.continuum.service"})
//@EnableAsync
//@Import({MasterDatabaseConfig.class,TenantDatabaseConfig.class,DataSourceBasedMultiTenantConnectionProviderImpl.class})
public class ContinuumConfig {
	
	@Bean
	public TenantInfoProviderService tenantInfoProvider() {
		return new IntegrationConstantsProvider();
	}

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
	
	@Bean(name = "taskExecutor")
    public ThreadPoolTaskExecutor taskExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(5);
        executor.setMaxPoolSize(10);
        executor.setQueueCapacity(25);
        return executor;
    }
}