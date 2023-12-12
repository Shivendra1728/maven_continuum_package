package com.di.integration.p21.controller;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.di.integration.p21.service.P21TokenSerivce;

@RestController
@RequestMapping("/P21/token")
public class P21TokenController {

	@Autowired
	P21TokenSerivce service;

	@GetMapping("/getToken")
	public String getToken(HttpServletResponse response) throws Exception {

		return service.getToken(null);
	}

	@GetMapping("/storeToken")
	public String storeToken(HttpServletResponse response) {
		String token = "your_token_value";

		// Create a new cookie with the token
		Cookie cookie = new Cookie("token", token);

		// Set additional properties for the cookie (optional)
		cookie.setMaxAge(3600); // Set the expiration time in seconds
		cookie.setPath("/"); // Set the path for which the cookie is valid

		// Add the cookie to the response
		response.addCookie(cookie);

		return "Token stored in the cookie.";
	}

	@GetMapping("/fetchToken")
	public String fetchToken(@CookieValue(value = "token", defaultValue = "") String token) {
		// Use the fetched token as needed
		return "Token: " + token;
	}

}
