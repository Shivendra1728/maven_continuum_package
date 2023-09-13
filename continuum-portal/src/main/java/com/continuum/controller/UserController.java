package com.continuum.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.continuum.service.UserService;
import com.continuum.tenant.repos.entity.User;

@RestController
@RequestMapping(value = "/users")
public class UserController {

	@Autowired
	private UserService userService;

	@PostMapping
	public Long createUser(@RequestBody User user) throws Exception{
		return userService.createUser(user);
	}

	@GetMapping("/getbyId")
	public List<User> getUserById(@RequestParam("id") Long id) {
		return userService.getUserById(id);
	}

	@DeleteMapping("/deleteById")
	public String deleteById(@RequestParam("id") Long id,@RequestParam String userName) {
		return userService.deleteUserById(id,userName);

	}

	@PutMapping("/UpdateById")
	public String updateUser(@RequestParam("id") long id, @RequestBody User user) {
		return userService.updateUser(id, user);
	}
}
