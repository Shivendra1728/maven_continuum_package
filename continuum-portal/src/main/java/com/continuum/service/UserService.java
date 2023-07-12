package com.continuum.service;


public interface UserService {

    String getUserByUsernameOrEmail(String usernameOrEmail, String password);
    

}