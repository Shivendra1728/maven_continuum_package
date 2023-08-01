package com.continuum.serviceImpl;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.continuum.repos.entity.User;
import com.continuum.repos.repositories.UserRepository;
import com.continuum.service.UserService;
import com.di.commons.dto.ContactDTO;
import com.di.commons.dto.UserDTO;
import com.di.commons.mapper.UserMapper;

@Service
public class UserServiceImpl implements UserService {

	@Autowired
	UserRepository userRepository;

	@Autowired
	UserMapper usermaper;

	@Autowired
	UserDTO userDTO;

	@Override

	public String createUser(User user) {
		if (userRepository.existsByEmail(user.getEmail())) {
			return "Email already exists. Cannot create user with the same email.";
		}
		userRepository.save(user);
		return "User Created Sucessfully";
	}

	@Override
	public List<User> getUserById(Long id) {
		if (id != 0) {
			Optional<User> userDTO = userRepository.findById(id);
			List<User> userdto = userDTO.map(Collections::singletonList).orElse(Collections.emptyList());
			return userdto;
		} else {
			return userRepository.findAll();
		}
	}

	@Override
	public String deleteUserById(Long id) {
		Optional<User> optionalUser = userRepository.findById(id);

		if (optionalUser.isPresent()) {
			User user = optionalUser.get();
			user.setStatus(false);
			userRepository.save(user);
			return "Status updated";
		} else {
			return "User Not Found";
		}
	}

	@Override
	public String updateUser(Long id, User user) {
//		Optional<User> optionalUser = userRepository.findById(id);
//		if (optionalUser.isPresent()) {
//			User eUser = optionalUser.get();
//			eUser.setCreatedDate(user.getCreatedDate());
//			eUser.setEmail(user.getEmail());
//			eUser.setFirstName(user.getFirstName());
//			eUser.setGender(user.getGender());
//			eUser.setId(user.getId());
//			eUser.setLastName(user.getLastName());
//			userRepository.save(eUser);
//			return "User updated";
//		} else {
			return "User not found";
//		}
	}
}
