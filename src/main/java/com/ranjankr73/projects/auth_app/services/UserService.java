package com.ranjankr73.projects.auth_app.services;

import com.ranjankr73.projects.auth_app.dtos.UserDto;

import java.util.List;

public interface UserService {
    UserDto createUser(UserDto userDto);

    UserDto getUserByEmail(String email);

    UserDto updateUser(UserDto userDto, String userId);

    void deleteUser(String userId);

    UserDto getUserById(String userId);

    List<UserDto> getAllUsers();
}
