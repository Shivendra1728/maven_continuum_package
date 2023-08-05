package com.di.commons.mapper;

import java.util.List;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.continuum.repos.entity.User;
import com.di.commons.dto.UserDTO;

@Component
public class UserMapper {

	@Autowired
	ModelMapper modelMapper;

	public UserDTO UserToUserDTO(User users) {

		UserDTO userDTO = modelMapper.map(users, UserDTO.class);
		return userDTO;
	}

	public User UserDTOToUser(UserDTO userDTO) {
		User users = modelMapper.map(userDTO, User.class);
		return users;
	}

	<S, T> List<T> mapList(List<S> source, Class<T> targetClass) {
		return source.stream().map(element -> modelMapper.map(element, targetClass)).collect(Collectors.toList());

	}
}
