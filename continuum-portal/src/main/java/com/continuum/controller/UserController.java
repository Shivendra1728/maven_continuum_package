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

	

	@GetMapping("/login")
	public ResponseEntity<LoginResponse> login(@RequestParam String usernameOrEmail, @RequestParam String password)throws Exception {
		String user = userService.getUserByUsernameOrEmail(usernameOrEmail, password);
		String token = null;
		Date expirationDate = null;
		if (user != null) {
			authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(usernameOrEmail, password));
			token = jwtTokenUtil.generateToken(usernameOrEmail);
			String extractedUsername = jwtTokenUtil.getUsernameFromToken(token);
			expirationDate = jwtTokenUtil.getExpirationDateFromToken(token);
			Object customClaim = jwtTokenUtil.getClaimFromToken(token, "role");
			Claims allClaims = jwtTokenUtil.getAllClaimsFromToken(token);
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
	}

}
