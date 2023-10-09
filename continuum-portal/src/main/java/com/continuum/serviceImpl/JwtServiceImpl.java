package com.continuum.serviceImpl;

import java.util.Base64;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.stereotype.Service;

import com.continuum.service.JwtService;
import com.continuum.tenant.repos.entity.Customer;
import com.continuum.tenant.repos.entity.User;
import com.continuum.tenant.repos.repositories.CustomerRepository;
import com.continuum.tenant.repos.repositories.UserRepository;
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
	
	private static final Logger logger = LoggerFactory.getLogger(ReturnOrderServiceImpl.class);

	

	public UserDTO decodeJwt(String base64Token) {
		
		try{
			String[] parts = base64Token.split("\\.");
			String payload = new String(Base64.getDecoder().decode(parts[1]));
			logger.info(payload);
			ObjectMapper objectMapper = new ObjectMapper();
			Map<String, Object> payloadMap = objectMapper.readValue(payload, new TypeReference<Map<String, Object>>() {});

        // Extract user information from the payload
        UserDTO userDTO = new UserDTO();
        userDTO.setFirstName((String) payloadMap.get("firstName"));
        userDTO.setLastName((String) payloadMap.get("lastName"));
        userDTO.setEmail((String) payloadMap.get("email"));
        
       
		Customer customer = new Customer();
		customer.setCustomerId((String) payloadMap.get("customerId"));
		customerRepository.save(customer);
		
		
		
		userDTO.setPassword(null);
		userDTO.setFullName((String) payloadMap.get("firstName")+(String) payloadMap.get("lastName"));
		userDTO.setUserName((String) payloadMap.get("firstName"));
		
		
		User user= userMapper.UserDTOToUser(userDTO);
		userDTO.setCustomer(customer);
	    userRepository.save(user);
        return userDTO;
		}
		catch (Exception e) {
            // Handle decoding errors
            e.printStackTrace(); 
            logger.info("COULD NOT EXTRACT DATA.");
            return null;
        }
	
		
}
}