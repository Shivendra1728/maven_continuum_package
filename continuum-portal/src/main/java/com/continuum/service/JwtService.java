package com.continuum.service;

import com.di.commons.dto.UserDTO;

public interface JwtService {
	 UserDTO decodeJwt(String base64Token);
	 
	 
}
