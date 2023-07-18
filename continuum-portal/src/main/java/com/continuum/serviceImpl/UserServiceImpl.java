package com.continuum.serviceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.continuum.repos.entity.User;
import com.continuum.repos.repositories.UserRepository;
import com.continuum.service.UserService;
import com.di.commons.mapper.UserMapper;
@Service
public class UserServiceImpl implements UserService {
	private final UserRepository userRepository;
	private final UserMapper userMapper;
	@Autowired
	public UserServiceImpl(UserRepository userRepository, UserMapper userMapper) {
		this.userRepository = userRepository;
		this.userMapper = userMapper;
	}
	@Override
	public String getUserByUsernameOrEmail(String usernameOrEmail, String password) {
		User userEntity = userRepository.findByUsername(usernameOrEmail);
		if (userEntity == null) {
			userEntity = userRepository.findByEmail(usernameOrEmail);
		}
		if (userEntity != null && userEntity.getPassword().equals(password)) {
			return userMapper.mapToDTO(userEntity);
		}
		return "Login Failed!";
	}

	@Override
	public User getUserByUsernameOrEmail(String username) {
		User userEntity = userRepository.findByUsername(username);
		if (userEntity == null) {
			userEntity = userRepository.findByEmail(username);
		}
		return userEntity;
	}
}