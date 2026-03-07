package com.ranjankr73.projects.auth_app.dtos;

public record Tokens(
        TokenResponse tokenResponse,
        String refreshToken
) {}
