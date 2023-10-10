package com.continuum.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.continuum.service.JwtService;
import com.di.commons.dto.UserDTO;

@RestController
public class JwtDecodeController {

	@Autowired
	private JwtService jwtService;

	@GetMapping("/decode")
	public ResponseEntity<?> decodeToken(@RequestParam String base64Token) {
		return jwtService.decodeJwt(base64Token);
	}
}