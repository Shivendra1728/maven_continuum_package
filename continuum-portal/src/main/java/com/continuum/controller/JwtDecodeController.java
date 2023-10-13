package com.continuum.controller;

import javax.servlet.http.HttpServletRequest;
import javax.validation.constraints.NotNull;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.continuum.service.JwtService;

@RestController
public class JwtDecodeController {

	@Autowired
	private JwtService jwtService;

	@GetMapping("/decode")
	public ResponseEntity<?> decodeToken(@RequestParam @NotNull String base64Token, HttpServletRequest request) {
		return jwtService.decodeJwt(base64Token, request);
	}
}
