package com.ranjankr73.projects.auth_app.services.impl;

import com.ranjankr73.projects.auth_app.dtos.UserDto;
import com.ranjankr73.projects.auth_app.entities.Provider;
import com.ranjankr73.projects.auth_app.entities.Role;
import com.ranjankr73.projects.auth_app.entities.User;
import com.ranjankr73.projects.auth_app.exceptions.ResourceNotFoundException;
import com.ranjankr73.projects.auth_app.helpers.UserHelper;
import com.ranjankr73.projects.auth_app.helpers.UserMapper;
import com.ranjankr73.projects.auth_app.repositories.RoleRepository;
import com.ranjankr73.projects.auth_app.repositories.UserRepository;
import com.ranjankr73.projects.auth_app.services.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    @Transactional
    public UserDto createUser(UserDto userDto) {

        if(userDto.getEmail() == null || userDto.getEmail().isBlank()){
            throw new IllegalArgumentException("Email is required");
        }

        if(userRepository.existsByEmailIgnoreCase(userDto.getEmail())){
            throw new IllegalArgumentException("User with given email already exists");
        }

        User user = UserMapper.toEntity(userDto);

        if(userDto.getPassword() != null && !userDto.getPassword().isBlank()){
            user.setPassword(passwordEncoder.encode(userDto.getPassword()));
        }

        user.setProvider(userDto.getProvider() != null ? userDto.getProvider() : Provider.LOCAL);

        Role role = roleRepository
                .findByNameIgnoreCase("ROLE_USER")
                .orElseThrow(() -> new ResourceNotFoundException("Default role not found"));

        System.out.println(role);

        user.getRoles().add(role);

        User savedUser = userRepository.save(user);

        return UserMapper.toDto(savedUser);
    }

    @Override
    @Transactional(readOnly = true)
    public UserDto getUserByEmail(String email) {
        User user = userRepository
                .findByEmailIgnoreCase(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with given email id"));

        return UserMapper.toDto(user);
    }

    @Override
    @Transactional
    public UserDto updateUser(UserDto userDto, String userId) {
        UUID uId = UserHelper.parseUUID(userId);
        User existingUser = userRepository
                .findById(uId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with given id"));

        if(userDto.getName() != null) existingUser.setName(userDto.getName());
        if(userDto.getPassword() != null) existingUser.setPassword(passwordEncoder.encode(userDto.getPassword()));
        if(userDto.getImage() != null) existingUser.setImage(userDto.getImage());
        if(userDto.getProvider() != null) existingUser.setProvider(userDto.getProvider());
        existingUser.setEnabled(userDto.isEnabled());

        User updatedUser = userRepository.save(existingUser);

        return UserMapper.toDto(updatedUser);
    }

    @Override
    @Transactional
    public void deleteUser(String userId) {
        UUID uId = UserHelper.parseUUID(userId);
        User user = userRepository.findById(uId).orElseThrow(() -> new ResourceNotFoundException("User not found with given id"));
        userRepository.delete(user);
    }

    @Override
    @Transactional(readOnly = true)
    public UserDto getUserById(String userId) {
        UUID uId = UserHelper.parseUUID(userId);
        User user = userRepository.findById(uId).orElseThrow(() -> new ResourceNotFoundException("User not found with given id"));

        return UserMapper.toDto(user);
    }

    @Override
    @Transactional(readOnly = true)
    public List<UserDto> getAllUsers() {
        return userRepository
                .findAll()
                .stream()
                .map(UserMapper::toDto)
                .toList();
    }
}
