package com.ranjankr73.projects.auth_app.helpers;

import com.ranjankr73.projects.auth_app.dtos.UserDto;
import com.ranjankr73.projects.auth_app.entities.Role;
import com.ranjankr73.projects.auth_app.entities.User;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

public class UserMapper {

    private UserMapper(){}

    public static UserDto toDto(User user){
        if(user == null) return null;

        return UserDto.builder()
                .id(user.getId())
                .name(user.getName())
                .email(user.getEmail())
                .image(user.getImage())
                .enabled(user.isEnabled())
                .roles(
                        user.getRoles()
                                .stream()
                                .map(Role::getName)
                                .collect(Collectors.toSet())
                )
                .provider(user.getProvider())
                .createdAt(user.getCreatedAt())
                .updatedAt(user.getUpdatedAt())
                .build();
    }

    public static User toEntity(UserDto dto){
        if(dto == null) return null;

        return User.builder()
                .id(dto.getId())
                .name(dto.getName())
                .email(dto.getEmail())
                .password(dto.getPassword())
                .image(dto.getImage())
                .enabled(dto.isEnabled())
                .roles(new HashSet<>())
                .provider(dto.getProvider())
                .build();
    }

    public static void updateEntity(User user, UserDto dto) {

        if (dto.getName() != null)
            user.setName(dto.getName());

        if (dto.getImage() != null)
            user.setImage(dto.getImage());

        if (dto.getProvider() != null)
            user.setProvider(dto.getProvider());

        user.setEnabled(dto.isEnabled());
    }

    public static Set<String> mapRoles(Set<Role> roles) {
        return roles.stream()
                .map(Role::getName)
                .collect(Collectors.toSet());
    }
}
