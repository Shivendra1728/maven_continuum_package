package com.continuum.service;

import org.springframework.http.ResponseEntity;

import com.di.commons.dto.UserDTO;

public interface JwtService {
	 ResponseEntity<?> decodeJwt(String base64Token);
	 
	 
}
