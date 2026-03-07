package com.ranjankr73.projects.auth_app.services;

import com.ranjankr73.projects.auth_app.dtos.LoginRequest;
import com.ranjankr73.projects.auth_app.dtos.Tokens;
import com.ranjankr73.projects.auth_app.dtos.UserDto;

public interface AuthService {
    UserDto registerUser(UserDto userDto);
    Tokens loginUser(LoginRequest loginRequest);
    Tokens refreshToken(String refreshToken);
    void logout(String refreshToken);
}
