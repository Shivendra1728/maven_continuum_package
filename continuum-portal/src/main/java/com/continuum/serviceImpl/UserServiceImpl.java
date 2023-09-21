package com.continuum.serviceImpl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.stereotype.Service;

import com.continuum.service.UserService;
import com.continuum.tenant.repos.entity.ReturnOrder;
import com.continuum.tenant.repos.entity.User;
import com.continuum.tenant.repos.repositories.ReturnOrderRepository;
import com.continuum.tenant.repos.repositories.UserRepository;
import com.di.commons.dto.UserDTO;
import com.di.commons.mapper.UserMapper;

@Service
public class UserServiceImpl implements UserService {

	@Autowired
	UserRepository userRepository;

	@Autowired
	ReturnOrderRepository returnOrderRepository;

	@Autowired
	UserMapper usermaper;

	@Override
	public Long createUser(User user) throws Exception {

		if (userRepository.existsByEmail(user.getEmail())) {
			throw new Exception("Email already exists. Cannot create a user with the same email.");
		} else {
			String hashedPassword = BCrypt.hashpw(user.getPassword(), BCrypt.gensalt());
			user.setPassword(hashedPassword);
			userRepository.save(user);
		}
		User u = userRepository.findByEmail(user.getEmail());
		return u.getId();

	}

	@Override
	public List<User> getUserById(Long id) {
		if (id != 0) {
			Optional<User> userDTO = userRepository.findById(id);
			List<User> userdto = userDTO.map(Collections::singletonList).orElse(Collections.emptyList());
			return userdto;
		} else {
			
			return userRepository.findByStatus(true);
		}
	}

	@Override
	public String deleteUserById(Long id, String userName) {
		Optional<User> optionalUser = userRepository.findById(id);
		User assignUser = userRepository.findByUserName(userName);

		if (optionalUser.isPresent() && assignUser != null) {
			User user = optionalUser.get();
			user.setStatus(false);
			user.setFirstName(assignUser.getFirstName());
			user.setLastName(assignUser.getLastName());
			user.setEmail(assignUser.getEmail());
			user.setRoles(assignUser.getRoles());
			userRepository.save(user);

			List<ReturnOrder> returnOrders = returnOrderRepository.findByUserId(id);
			for (ReturnOrder returnOrder : returnOrders) {
				returnOrder.setUser(assignUser);
			}
			returnOrderRepository.saveAll(returnOrders);

			return "User updated";
		} else {
			return "User Not Found";
		}
	}

	@Override
	public String updateUser(Long id, User user) {
		Optional<User> optionalUser = userRepository.findById(id);
		if (optionalUser.isPresent()) {
			User eUser = optionalUser.get();
			if (user.getUserName() != null) {
				eUser.setUserName(user.getUserName());
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
			if (user.getDob() != null) {
				eUser.setDob(user.getDob());
			}
			if (user.getAge() != null) {
				eUser.setAge(user.getAge());
			}
			if (user.getBloodGroup() != null) {
				eUser.setBloodGroup(user.getBloodGroup());
			}
			if (user.getMaritalStatus() != null) {
				eUser.setMaritalStatus(user.getMaritalStatus());
			}
			if (user.getNationality() != null) {
				eUser.setNationality(user.getNationality());
			}
			if (user.getProfile() != null) {
				eUser.setProfile(user.getProfile());
			}
			if (user.getEmail() != null) {
				eUser.setEmail(user.getEmail());
			}
			userRepository.save(eUser);
			return "User updated";
		} else {
			return "User not found";
		}
	}
}
