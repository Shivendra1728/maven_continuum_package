package com.continuum.controller;

 

import java.util.Date;

 

import org.apache.http.HttpStatus;

import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.http.ResponseEntity;

import org.springframework.security.authentication.AuthenticationManager;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;

import org.springframework.web.bind.annotation.GetMapping;

import org.springframework.web.bind.annotation.RequestParam;

import org.springframework.web.bind.annotation.RestController;

 

import com.continuum.response.LoginResponse;

import com.continuum.service.UserService;

import com.di.commons.helper.JwtTokenUtil;

 

import io.jsonwebtoken.Claims;

 

@RestController

public class UserController {

 

    @Autowired

    private UserService userService;

 

    @Autowired

    private JwtTokenUtil jwtTokenUtil;

 

    @Autowired

    private AuthenticationManager authenticationManager;

 

    @Autowired

    public UserController(UserService userService, JwtTokenUtil jwtTokenUtil) {

        this.userService = userService;

        this.jwtTokenUtil = jwtTokenUtil;

    }

 

    /*

     * @GetMapping("/login") public String login(@RequestParam String

     * usernameOrEmail, @RequestParam String password) {

     *

     * return userService.getUserByUsernameOrEmail(usernameOrEmail, password);

     *

     * }

     */

 

    @GetMapping("/login")

    public ResponseEntity<LoginResponse> login(@RequestParam String usernameOrEmail, @RequestParam String password)

            throws Exception {

 

        String user= userService.getUserByUsernameOrEmail(usernameOrEmail, password);

        // = userService.findByUsernameOrEmail(usernameOrEmail,password);

        String token = null;

         Date expirationDate = null;

        if(user!= null) {

            System.out.println("username>>>>>>>>"+usernameOrEmail+"---------"+"password>>>>>>>>"+password);

 

        // Authenticate the user

        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(usernameOrEmail, password));

        // Generate token

                 token = jwtTokenUtil.generateToken(usernameOrEmail);

                System.out.println("token>>>>>> " + token);

                

                // Extract information from the token

                String extractedUsername = jwtTokenUtil.getUsernameFromToken(token);

                       expirationDate = jwtTokenUtil.getExpirationDateFromToken(token);

                Object customClaim = jwtTokenUtil.getClaimFromToken(token, "role");

                Claims allClaims = jwtTokenUtil.getAllClaimsFromToken(token);

                

                

//                ZoneId zoneId = ZoneId.of("UTC");

//                LocalDateTime expirationDateTime = LocalDateTime.ofInstant(expirationDate.toInstant(), zoneId);

//                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");

//                String formattedExpirationDate = expirationDateTime.format(formatter);

                

                System.out.println("Extracted Username: " + extractedUsername);

                System.out.println("Expiration Date: " + expirationDate);

                System.out.println("Custom Claim: " + customClaim);

                System.out.println("All Claims: " + allClaims);

                

                

                

        }

        

         LoginResponse response = new LoginResponse();

            if (token != null) {

                response.setMessage("Login success!");

                response.setToken(token);

               

                response.setExpirationDate(expirationDate);

                return ResponseEntity.ok(response);

            } else {

                response.setMessage("Login Failed!");

                return ResponseEntity.status(HttpStatus.SC_UNAUTHORIZED).body(response);

            }

        

 

        //return ResponseEntity.ok(token);

    }

 

}

 