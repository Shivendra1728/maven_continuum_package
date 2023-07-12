package com.di.commons.mapper;
import org.springframework.stereotype.Component;

import com.continuum.repos.entity.User;
import com.di.commons.dto.UserDTO;
@Component
public class UserMapper {
	
			public String mapToDTO(User userEntity) {
	        UserDTO userDTO = new UserDTO();
	        userDTO.setId(userEntity.getId());
	        userDTO.setUsername(userEntity.getUsername());
	        userDTO.setEmail(userEntity.getEmail());
	        return "Login Success!";
	    }
			
}
