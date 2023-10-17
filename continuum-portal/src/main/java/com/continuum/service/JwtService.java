package com.continuum.service;

import javax.servlet.http.HttpServletRequest;
import javax.validation.constraints.NotNull;

import org.springframework.http.ResponseEntity;

public interface JwtService {
	ResponseEntity<?> decodeJwt(@NotNull String bToken, HttpServletRequest request);

}