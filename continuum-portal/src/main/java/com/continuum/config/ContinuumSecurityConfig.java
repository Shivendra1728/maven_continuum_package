package com.continuum.config;

 

import java.util.ArrayList;

import java.util.List;

 

import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.context.annotation.Bean;

import org.springframework.context.annotation.Configuration;

import org.springframework.security.authentication.AuthenticationManager;

import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;

import org.springframework.security.config.annotation.web.builders.HttpSecurity;

import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;

import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;

import org.springframework.security.config.http.SessionCreationPolicy;

import org.springframework.security.core.GrantedAuthority;

import org.springframework.security.core.authority.SimpleGrantedAuthority;

import org.springframework.security.core.userdetails.UserDetailsService;

import org.springframework.security.crypto.password.NoOpPasswordEncoder;

import org.springframework.security.crypto.password.PasswordEncoder;

 

import com.continuum.repos.entity.User;

import com.continuum.service.UserService;

import com.di.commons.helper.JwtTokenUtil;

 

@SuppressWarnings("deprecation")

@EnableWebSecurity

@Configuration

public class ContinuumSecurityConfig extends WebSecurityConfigurerAdapter {

 

     @Autowired

     UserService userService;

     

     @Autowired

     JwtTokenUtil jwtTokenUtil;

    

    User user;

 

    @Autowired

    public ContinuumSecurityConfig(UserService userService, JwtTokenUtil jwtTokenUtil) {

        this.userService = userService;

        this.jwtTokenUtil = jwtTokenUtil;

   

        

    }

    

    @Bean

    public PasswordEncoder passwordEncoder() {

        

        return NoOpPasswordEncoder.getInstance();

       // return new BCryptPasswordEncoder();

    }

 

    @Override

    protected void configure(HttpSecurity http) throws Exception {

        http.csrf().disable()

        .authorizeRequests()

        .antMatchers("/*").permitAll()

        .antMatchers("/admin").hasRole("Admin")

        .antMatchers("/login").hasRole("USER") // Restrict access to /login endpoint to users with the "USER" role

        //.anyRequest().authenticated()

        .and()

        .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS);

    }

 

    @Override

    protected void configure(AuthenticationManagerBuilder auth) throws Exception {

        auth.userDetailsService(userDetailsService());

    }

 

    @Override

    protected AuthenticationManager authenticationManager() throws Exception {

        return super.authenticationManager();

    }

 

    @Bean

    @Override

    public AuthenticationManager authenticationManagerBean() throws Exception {

        return super.authenticationManagerBean();

    }

    @Override

    protected UserDetailsService userDetailsService() {

        return username -> {

            User user = userService.getUserByUsernameOrEmail(username); // Retrieve user by username or email

 

            String password=user.getPassword();

            System.out.println("username>>>>>>>>"+username+"---------"+"password>>>>>>>>"+password);

 

            // Create a collection of GrantedAuthority objects for the user

            List<GrantedAuthority> authorities = new ArrayList<>();

            authorities.add(new SimpleGrantedAuthority("USER")); // Replace with actual roles for the user

 

            return org.springframework.security.core.userdetails.User

                    .withUsername(username)

                    .password(password)

                    .authorities(authorities)

                    .accountExpired(false)

                    .accountLocked(false)

                    .credentialsExpired(false)

                    .disabled(false)

                    .build();

        };

    }

}