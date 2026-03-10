package com.ranjankr73.projects.auth_app.controllers;

import com.ranjankr73.projects.auth_app.dtos.LoginRequest;
import com.ranjankr73.projects.auth_app.dtos.TokenResponse;
import com.ranjankr73.projects.auth_app.dtos.Tokens;
import com.ranjankr73.projects.auth_app.dtos.UserDto;
import com.ranjankr73.projects.auth_app.helpers.CookieUtil;
import com.ranjankr73.projects.auth_app.services.AuthService;
import com.ranjankr73.projects.auth_app.services.JwtService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;
    private final JwtService jwtService;

    @PostMapping("/register")
    public ResponseEntity<UserDto> registerUser(@RequestBody UserDto userDto){
        return ResponseEntity.status(HttpStatus.CREATED).body(authService.registerUser(userDto));
    }

    @PostMapping("/login")
    public ResponseEntity<TokenResponse> loginUser(@RequestBody LoginRequest loginRequest, HttpServletResponse response){
        Tokens tokens = authService.loginUser(loginRequest);

        CookieUtil.attachRefreshToken(response, tokens.refreshToken(), (int)jwtService.getRefreshTtlSeconds());

        return ResponseEntity.ok(tokens.tokenResponse());
    }

    @PostMapping("/refresh")
    public ResponseEntity<TokenResponse> refresh(
            @CookieValue("refreshToken") String refreshToken,
            HttpServletResponse response
    ){
        Tokens tokens = authService.refreshToken(refreshToken);

        // Attaching the refresh token to cookie
        CookieUtil.attachRefreshToken(
                response,
                tokens.refreshToken(),
                (int) jwtService.getRefreshTtlSeconds()
        );

        return ResponseEntity.ok(tokens.tokenResponse());
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout(
            @CookieValue("refreshToken") String refreshToken,
            HttpServletResponse response
    ){
        authService.logout(refreshToken);

        Cookie cookie = new Cookie("refreshToken", null);

        cookie.setHttpOnly(true);
        cookie.setSecure(true);
        cookie.setPath("/api/v1/auth");
        cookie.setMaxAge(0);
        cookie.setAttribute("SameSite", "Strict");

        response.addCookie(cookie);

        return ResponseEntity.noContent().build();
    }
}
