package com.continuum.serviceImpl;

import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.continuum.multitenant.util.JwtTokenUtil;
import com.continuum.service.JwtService;
import com.continuum.tenant.repos.entity.Customer;
import com.continuum.tenant.repos.entity.Role;
import com.continuum.tenant.repos.entity.Roles;
import com.continuum.tenant.repos.entity.User;
import com.continuum.tenant.repos.repositories.CustomerRepository;
import com.continuum.tenant.repos.repositories.RolesRepository;
import com.continuum.tenant.repos.repositories.UserRepository;
import com.di.commons.dto.AuthResponse;
import com.di.commons.dto.UserDTO;
import com.di.commons.mapper.UserMapper;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

@Service
public class JwtServiceImpl implements JwtService {

	@Autowired
	UserRepository userRepository;

	@Autowired
	CustomerRepository customerRepository;

	@Autowired
	UserMapper userMapper;
	
	@Autowired
	RolesRepository rolesRepository;

	@Autowired
	private JwtTokenUtil jwtTokenUtil;

	private Map<String, String> roleMappings = new HashMap<>();
	private static final Logger logger = LoggerFactory.getLogger(ReturnOrderServiceImpl.class);

	

	public ResponseEntity<?> decodeJwt(String base64Token) {

		try {

			String[] parts = base64Token.split("\\.");
			String payload = new String(Base64.getDecoder().decode(parts[1]));
			logger.info(payload);
			ObjectMapper objectMapper = new ObjectMapper();
			Map<String, Object> payloadMap = objectMapper.readValue(payload, new TypeReference<Map<String, Object>>() {
			});

			// Extract user information from the payload
			UserDTO userDTO = new UserDTO();
			userDTO.setFirstName((String) payloadMap.get("firstName"));
			userDTO.setLastName((String) payloadMap.get("lastName"));
			userDTO.setEmail((String) payloadMap.get("email"));

			// Extract roles as a list of role names
			String role = (String) payloadMap.get("role");
			Role roles =new Role();
			roles= rolesRepository.findByRole(role);
			
			if (role != null) {
				userDTO.setRole(roles);
			}
			
			Customer customer = new Customer();
			customer.setCustomerId((String) payloadMap.get("customerID"));
			customerRepository.save(customer);

			userDTO.setPassword(null);
			userDTO.setFullName((String) payloadMap.get("firstName") + (String) payloadMap.get("lastName"));
			userDTO.setUserName((String) payloadMap.get("UserName"));
			userDTO.setCustomer(customer);
			User user = userMapper.UserDTOToUser(userDTO);

			userRepository.save(user);

			// preparetoken
			final String token = jwtTokenUtil.generateToken(user.getUserName(), user.getCustomer().getCustomerId());
			String name = user.getFirstName() + " " + user.getLastName();
			long userId = user.getId();

			return ResponseEntity.ok(new AuthResponse(user.getUserName(), name, token, user.getRole(), userId,
					user.getCustomer().getCustomerId(), jwtTokenUtil.getExpirationDateFromToken(token)));
		} catch (Exception e) {
			// Handle decoding errors
			e.printStackTrace();
			logger.info("COULD NOT EXTRACT DATA.");
			return null;
		}

	}
}