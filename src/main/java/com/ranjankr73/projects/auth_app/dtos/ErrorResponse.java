package com.ranjankr73.projects.auth_app.dtos;

import org.springframework.http.HttpStatus;

public record ErrorResponse(
        String message,
        HttpStatus error,
        int statusCode
) {
}
