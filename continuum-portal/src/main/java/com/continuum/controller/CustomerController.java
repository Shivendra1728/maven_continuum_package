package com.continuum.controller;

import java.util.Date;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.continuum.multitenant.util.JwtTokenUtil;
import com.continuum.service.CustomerService;
import com.continuum.tenant.repos.entity.User;
import com.continuum.tenant.repos.repositories.CustomerRepository;
import com.continuum.tenant.repos.repositories.UserRepository;
import com.di.commons.dto.CustomerDTO;

import io.swagger.v3.oas.annotations.parameters.RequestBody;

@RestController

public class CustomerController {

	public static String extractedUsername = "";

	@Autowired
	CustomerService customerService;

	@Autowired
	CustomerRepository repo;

	@Autowired
	UserRepository userRepository;

	@Autowired
	private JwtTokenUtil jwtTokenUtil;

	@Autowired
	public CustomerController(CustomerService customerService, JwtTokenUtil jwtTokenUtil) {

		this.customerService = customerService;

		this.jwtTokenUtil = jwtTokenUtil;

	}

	public CustomerDTO createCustomer(@RequestBody CustomerDTO customerDTO) throws Exception {

		return customerService.createCustomer(customerDTO);

	}

	@PostMapping("/signupCust")
	public Map<String, Object> createCustomerInDB(@RequestBody CustomerDTO customerDTO, HttpServletRequest request) {

		return customerService.createCustomerInDB(customerDTO, request);

	}

	@GetMapping("/activateAccount")
	public String activateAccount(@RequestParam String uuid) {
		User user = userRepository.findByActivationUuid(uuid);
		if (user != null) {
			// Check if the token has expired
			Date expirationTime = user.getActivationResetTokenExpiration();
			Date currentTime = new Date();

			if (expirationTime != null && expirationTime.after(currentTime)) {
				// Token is not expired, allow password update
				user.setStatus(true);
				user.setActivationUuid(null);
				userRepository.save(user);
				return "Account Activated";
			} else {
				// Token has expired, show an error message
				return "Token Expired";
			}
		} else {
			return "Invalid User";
		}

	}

}