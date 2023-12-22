package com.continuum.controller;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.continuum.service.ForgetPasswordService;
import com.continuum.tenant.repos.entity.Role;

@RestController
public class ForgetPasswordController {

	@Autowired
	ForgetPasswordService forgetPasswordService;

	@PostMapping("/forgetPassword")
	public String forgetPassword(@RequestParam("email") String email, HttpServletRequest request) {
		return forgetPasswordService.forgetPassword(email, request);
	}

	@PutMapping("/updatePassword")
	public Role updatePassword(@RequestParam("UUID") String uuid, @RequestParam String password) {
		return forgetPasswordService.updatePassword(uuid, password);

	}
}
