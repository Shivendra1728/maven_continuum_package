/*
 * package com.continuum.config;
 * 
 * import org.springframework.context.annotation.Configuration; import
 * org.springframework.web.servlet.config.annotation.CorsRegistry; import
 * org.springframework.web.servlet.config.annotation.EnableWebMvc; import
 * org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
 * 
 * @Configuration
 * 
 * @EnableWebMvc public class WebConfig implements WebMvcConfigurer {
 * 
 * @Override public void addCorsMappings(CorsRegistry registry) {
 * registry.addMapping("/continuum/**") .allowedOrigins("http://localhost:3000")
 * .allowedMethods("PUT", "DELETE","GET") .allowedHeaders("*")
 * .exposedHeaders("header1", "header2") .allowCredentials(false).maxAge(3600);
 * } }
 */