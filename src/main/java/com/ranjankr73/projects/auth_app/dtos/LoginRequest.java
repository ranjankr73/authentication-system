package com.ranjankr73.projects.auth_app.dtos;

public record LoginRequest(
        String email,
        String password
) {}