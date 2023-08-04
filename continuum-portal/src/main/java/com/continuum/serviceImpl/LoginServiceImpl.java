/*
 * package com.continuum.serviceImpl;
 * 
 * import org.springframework.beans.factory.annotation.Autowired; import
 * org.springframework.stereotype.Service;
 * 
 * import com.continuum.repos.repositories.LoginRepository; import
 * com.continuum.service.LoginService; import
 * com.continuum.tenant.repos.entity.User; import
 * com.di.commons.mapper.LoginMapper; import com.di.commons.mapper.UserMapper;
 * 
 * @Service public class LoginServiceImpl implements LoginService { private
 * final LoginRepository loginRepository; private final LoginMapper loginMapper;
 * 
 * @Autowired public LoginServiceImpl(LoginRepository loginRepository,
 * LoginMapper loginMapper) { this.loginRepository = loginRepository;
 * this.loginMapper = loginMapper; }
 * 
 * @Override public String getUserByUsernameOrEmail(String usernameOrEmail,
 * String password) { User userEntity =
 * loginRepository.findByUsername(usernameOrEmail); if (userEntity == null) {
 * userEntity = loginRepository.findByEmail(usernameOrEmail); } if (userEntity
 * != null && userEntity.getPassword().equals(password)) { return
 * loginMapper.mapToDTO(userEntity); } return "Login Failed!"; }
 * 
 * @Override public User getUserByUsernameOrEmail(String username) { User
 * userEntity = loginRepository.findByUsername(username); if (userEntity ==
 * null) { userEntity = loginRepository.findByEmail(username); } return
 * userEntity; } }
 */