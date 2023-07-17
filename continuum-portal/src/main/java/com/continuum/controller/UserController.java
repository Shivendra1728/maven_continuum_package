package com.continuum.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import com.continuum.service.UserService;


@RestController
public class UserController {

	private final UserService userService;
	
	@Autowired
	public UserController(UserService userService) {
		this.userService = userService;
	}

	@GetMapping("/login")
	public String login(@RequestParam String usernameOrEmail, @RequestParam String password) {
		return userService.getUserByUsernameOrEmail(usernameOrEmail, password);
	
	}
}