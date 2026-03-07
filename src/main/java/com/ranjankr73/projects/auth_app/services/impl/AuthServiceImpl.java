package com.ranjankr73.projects.auth_app.services.impl;

import com.ranjankr73.projects.auth_app.dtos.LoginRequest;
import com.ranjankr73.projects.auth_app.dtos.TokenResponse;
import com.ranjankr73.projects.auth_app.dtos.Tokens;
import com.ranjankr73.projects.auth_app.dtos.UserDto;
import com.ranjankr73.projects.auth_app.entities.RefreshToken;
import com.ranjankr73.projects.auth_app.entities.UserPrincipal;
import com.ranjankr73.projects.auth_app.exceptions.ResourceNotFoundException;
import com.ranjankr73.projects.auth_app.helpers.UserMapper;
import com.ranjankr73.projects.auth_app.repositories.RefreshTokenRepository;
import com.ranjankr73.projects.auth_app.services.AuthService;
import com.ranjankr73.projects.auth_app.services.JwtService;
import com.ranjankr73.projects.auth_app.services.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final UserService userService;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final RefreshTokenRepository refreshTokenRepository;

    @Override
    public UserDto registerUser(UserDto userDto) {
        return userService.createUser(userDto);
    }

    @Override
    public Tokens loginUser(LoginRequest loginRequest) {
        Authentication authentication = authenticationManager
                .authenticate(
                        new UsernamePasswordAuthenticationToken(loginRequest.email(), loginRequest.password())
                );

        UserPrincipal principal = (UserPrincipal) authentication.getPrincipal();

        String jti = UUID.randomUUID().toString();
        RefreshToken token = RefreshToken.builder()
                .jti(jti)
                .user(principal.getUser())
                .expiresAt(Instant.now().plusSeconds(jwtService.getRefreshTtlSeconds()))
                .build();

        refreshTokenRepository.save(token);

        String accessToken = jwtService.generateAccessToken(principal);
        String refreshToken = jwtService.generateRefreshToken(principal, jti);

        return new Tokens(
                new TokenResponse(
                    accessToken,
                    3600,
                    "Bearer",
                        UserMapper.toDto(principal.getUser())
                ),
                refreshToken
        );
    }

    @Override
    public Tokens refreshToken(String token){

        if(!jwtService.isRefreshToken(token)){
            throw new BadCredentialsException("Invalid token type");
        }

        String jti = jwtService.extractJti(token);

        RefreshToken storedToken = refreshTokenRepository
                .findByJti(jti)
                .orElseThrow(() -> new ResourceNotFoundException("Refresh token not found"));

        if(storedToken.isRevoked() || storedToken.isUsed()){
            throw new IllegalArgumentException("Refresh token already user or revoked");
        }

        if(storedToken.getExpiresAt().isBefore(Instant.now())){
            throw new IllegalArgumentException("Refresh token expired");
        }

        storedToken.setUsed(true);
        refreshTokenRepository.save(storedToken);

        UserPrincipal principal = new UserPrincipal(storedToken.getUser());

        String newJti = UUID.randomUUID().toString();

        RefreshToken newRefreshToken = RefreshToken.builder()
                .jti(newJti)
                .user(principal.getUser())
                .expiresAt(Instant.now().plusSeconds(jwtService.getRefreshTtlSeconds()))
                .build();

        refreshTokenRepository.save(newRefreshToken);

        String accessToken = jwtService.generateAccessToken(principal);
        String refreshToken = jwtService.generateRefreshToken(principal, newJti);

        return new Tokens(
                new TokenResponse(
                        accessToken,
                        jwtService.getAccessTtlSeconds(),
                        "Bearer",
                        UserMapper.toDto(principal.getUser())
                ),
                refreshToken
        );
    }

    @Override
    public void logout(String refreshToken) {
        String jti = jwtService.extractJti(refreshToken);

        refreshTokenRepository.findByJti(jti)
                .ifPresent(token -> {
                    token.setRevoked(true);
                    refreshTokenRepository.save(token);
                });
    }
}
