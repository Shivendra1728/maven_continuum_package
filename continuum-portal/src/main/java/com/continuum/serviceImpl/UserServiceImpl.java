package com.continuum.serviceImpl;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.continuum.controller.LoginController;
import com.continuum.repos.entity.User;
import com.continuum.repos.entity.User_Address;
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
		Optional<User> optionalUser = userRepository.findById(id);
		if (optionalUser.isPresent()) {
			User eUser = optionalUser.get();
			if (user.getUsername() != null) {
				eUser.setUsername(user.getUsername());
			}
			if (user.getFirstName() != null) {
				eUser.setFirstName(user.getFirstName());
			}
			if (user.getLastName() != null) {
				eUser.setLastName(user.getLastName());
			}
			if (user.getUser_address() != null) {
				eUser.setUser_address(user.getUser_address());
			}
			if (user.getUser_contact() != null) {
				eUser.setUser_contact(user.getUser_contact());
			}
			if (user.getGender() != null) {
				eUser.setGender(user.getGender());
			}
			if (user.getNote() != null) {
				eUser.setNote(user.getNote());
			}
			if (user.getRoles() != null) {
				eUser.setRoles(user.getRoles());
			}
			userRepository.save(eUser);
			return "User updated";
		} else {
			return "User not found";
		}
	}
}
