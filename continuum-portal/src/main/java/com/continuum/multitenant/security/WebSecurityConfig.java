package com.continuum.multitenant.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

/**
 * @author RK
 */
@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

	@Autowired
	private JwtUserDetailsService jwtUserDetailsService;

	@Autowired
	private JwtAuthenticationEntryPoint unauthorizedHandler;

	@Override
	@Bean
	public AuthenticationManager authenticationManagerBean() throws Exception {
		return super.authenticationManagerBean();
	}

	@Override
	public void configure(AuthenticationManagerBuilder auth) throws Exception {
		auth.userDetailsService(jwtUserDetailsService).passwordEncoder(passwordEncoder());
	}

	@Bean
	public JwtAuthenticationFilter authenticationTokenFilterBean() throws Exception {
		return new JwtAuthenticationFilter();
	}

	@Override
	protected void configure(HttpSecurity http) throws Exception {
		http.cors().and().csrf().disable().authorizeRequests().antMatchers("/login/**").permitAll()
				.antMatchers("/signupCust/**").permitAll().antMatchers("/forgetPassword/**").permitAll()
				.antMatchers("/updatePassword/**").permitAll().antMatchers("/activateAccount/**").permitAll()
				.antMatchers("/swagger/**").permitAll().antMatchers("/api/upload-csv/**").permitAll().antMatchers("/**")
				.authenticated().and().exceptionHandling().authenticationEntryPoint(unauthorizedHandler).and()
				.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS);
		http.addFilterBefore(authenticationTokenFilterBean(), UsernamePasswordAuthenticationFilter.class);
	}

//    @Bean
//    public PasswordEncoder passwordEncoder() {
//        PasswordEncoder encoder = PasswordEncoderFactories.createDelegatingPasswordEncoder();
//        return encoder;
//    }

	@Bean
	public PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}

	/*
	 * @Bean public FilterRegistrationBean platformCorsFilter() {
	 * UrlBasedCorsConfigurationSource source = new
	 * UrlBasedCorsConfigurationSource();
	 * 
	 * CorsConfiguration configAutenticacao = new CorsConfiguration();
	 * configAutenticacao.setAllowCredentials(true);
	 * configAutenticacao.addAllowedOrigin("*");
	 * configAutenticacao.addAllowedHeader("Authorization");
	 * configAutenticacao.addAllowedHeader("Content-Type");
	 * configAutenticacao.addAllowedHeader("Accept");
	 * configAutenticacao.addAllowedMethod("POST");
	 * configAutenticacao.addAllowedMethod("GET");
	 * configAutenticacao.addAllowedMethod("DELETE");
	 * configAutenticacao.addAllowedMethod("PUT");
	 * configAutenticacao.addAllowedMethod("OPTIONS");
	 * configAutenticacao.setMaxAge(3600L); source.registerCorsConfiguration("/**",
	 * configAutenticacao);
	 * 
	 * FilterRegistrationBean bean = new FilterRegistrationBean(new
	 * CorsFilter(source)); bean.setOrder(-110); return bean; }
	 */
}
