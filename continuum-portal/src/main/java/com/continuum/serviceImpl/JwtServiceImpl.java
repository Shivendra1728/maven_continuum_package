package com.continuum.serviceImpl;

import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.validation.constraints.NotNull;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.stereotype.Service;

import com.continuum.multitenant.constant.UserStatus;
import com.continuum.multitenant.mastertenant.entity.MasterTenant;
import com.continuum.multitenant.mastertenant.service.MasterTenantService;
import com.continuum.multitenant.util.JwtTokenUtil;
import com.continuum.service.JwtService;
import com.continuum.tenant.repos.entity.Customer;
import com.continuum.tenant.repos.entity.Role;
import com.continuum.tenant.repos.entity.User;
import com.continuum.tenant.repos.repositories.CustomerRepository;
import com.continuum.tenant.repos.repositories.RolesRepository;
import com.continuum.tenant.repos.repositories.UserRepository;
import com.di.commons.dto.AuthResponse;
import com.di.commons.dto.UserDTO;
import com.di.commons.helper.DBContextHolder;
import com.di.commons.mapper.UserMapper;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

@Service
public class JwtServiceImpl implements JwtService {
	private Map<String, String> mapValue = new HashMap<>();

	@Autowired
	UserRepository userRepository;

	@Autowired
	CustomerRepository customerRepository;

	@Autowired
	private AuthenticationManager authenticationManager;

	@Autowired
	UserMapper userMapper;

	@Autowired
	RolesRepository rolesRepository;

	@Autowired
	MasterTenantService masterTenantService;

	@Autowired
	private JwtTokenUtil jwtTokenUtil;

	private Map<String, String> roleMappings = new HashMap<>();
	private static final Logger logger = LoggerFactory.getLogger(ReturnOrderServiceImpl.class);

	public ResponseEntity<?> decodeJwt(@NotNull String base64Token, HttpServletRequest request) {

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
			Role roles = new Role();
			roles = rolesRepository.findByRole(role);

			if (role != null) {
				userDTO.setRole(roles);
			}

			Customer customer = new Customer();
			customer.setCustomerId((String) payloadMap.get("customerID"));
			

			String hashedPassword = BCrypt.hashpw("12345", BCrypt.gensalt());
			userDTO.setPassword(hashedPassword);

			userDTO.setFullName((String) payloadMap.get("firstName") + (String) payloadMap.get("lastName"));
			userDTO.setUserName((String) payloadMap.get("UserName"));
			userDTO.setCustomer(customer);
			
			User existingUser = userRepository.findByEmail(userDTO.getEmail());
			if (existingUser != null) {
				// preparetoken and also give tenant
				if (null == existingUser.getUserName() || existingUser.getUserName().isEmpty()) {
					return new ResponseEntity<>("User name is required", HttpStatus.BAD_REQUEST);
				}
				// set database parameter

				String tenentId = request.getHeader("host").split("\\.")[0];
				MasterTenant masterTenant = masterTenantService.findByDbName(tenentId);
				if (null == masterTenant || masterTenant.getStatus().toUpperCase().equals(UserStatus.INACTIVE)) {
					throw new RuntimeException("Please contact service provider.");
				}
				// Entry Client Wise value dbName store into bean.
				loadCurrentDatabaseInstance(masterTenant.getDbName(), existingUser.getUserName());

				final Authentication authentication = authenticationManager
						.authenticate(new UsernamePasswordAuthenticationToken(existingUser.getUserName(), 12345));
				SecurityContextHolder.getContext().setAuthentication(authentication);
				UserDetails userDetails = (UserDetails) authentication.getPrincipal();

				final String token = jwtTokenUtil.generateToken(existingUser.getUserName(), tenentId);
				String name = existingUser.getFirstName() + " " + existingUser.getLastName();
				long userId = existingUser.getId();

				return ResponseEntity
						.ok(new AuthResponse(existingUser.getUserName(), name, token, existingUser.getRole(), userId,
								existingUser.getCustomer().getCustomerId(), jwtTokenUtil.getExpirationDateFromToken(token)));
			} else {
				User user = userMapper.UserDTOToUser(userDTO);
				customerRepository.save(customer);
				userRepository.save(user);
				// preparetoken and also give tenant
				if (null == user.getUserName() || user.getUserName().isEmpty()) {
					return new ResponseEntity<>("User name is required", HttpStatus.BAD_REQUEST);
				}
				// set database parameter

				String tenentId = request.getHeader("host").split("\\.")[0];
				MasterTenant masterTenant = masterTenantService.findByDbName(tenentId);
				if (null == masterTenant || masterTenant.getStatus().toUpperCase().equals(UserStatus.INACTIVE)) {
					throw new RuntimeException("Please contact service provider.");
				}
				// Entry Client Wise value dbName store into bean.
				loadCurrentDatabaseInstance(masterTenant.getDbName(), user.getUserName());

				final Authentication authentication = authenticationManager
						.authenticate(new UsernamePasswordAuthenticationToken(user.getUserName(), 12345));
				SecurityContextHolder.getContext().setAuthentication(authentication);
				UserDetails userDetails = (UserDetails) authentication.getPrincipal();

				final String token = jwtTokenUtil.generateToken(user.getUserName(), tenentId);
				String name = user.getFirstName() + " " + user.getLastName();
				long userId = user.getId();

				return ResponseEntity.ok(new AuthResponse(user.getUserName(), name, token, user.getRole(), userId,
						user.getCustomer().getCustomerId(), jwtTokenUtil.getExpirationDateFromToken(token)));
			}

		} catch (Exception e) {
			// Handle decoding errors
			e.printStackTrace();
			logger.info("COULD NOT EXTRACT DATA.");
			return null;
		}

	}

	private void loadCurrentDatabaseInstance(String databaseName, String userName) {
		DBContextHolder.setCurrentDb(databaseName);
		mapValue.put(userName, databaseName);
	}

}