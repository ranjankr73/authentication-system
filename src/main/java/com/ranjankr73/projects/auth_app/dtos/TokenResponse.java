package com.ranjankr73.projects.auth_app.dtos;

public record TokenResponse<user>(
        String accessToken,
        long expiresIn,
        String tokenType,
        UserDto user
) {}
