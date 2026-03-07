package com.ranjankr73.projects.auth_app.services;

import com.ranjankr73.projects.auth_app.entities.User;
import com.ranjankr73.projects.auth_app.entities.UserPrincipal;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
@Getter
@Setter
public class JwtService {
    private final SecretKey key;
    private final long accessTtlSeconds;
    private final long refreshTtlSeconds;
    private final String issuer;

    public JwtService(
            @Value("${security.jwt.secret}") String secret,
            @Value("${security.jwt.access-ttl-seconds}") long accessTtlSeconds,
            @Value("${security.jwt.refresh-ttl-seconds}") long refreshTtlSeconds,
            @Value("${security.jwt.issuer}") String issuer
    ){
        if(secret == null || secret.length() < 64){
            throw new IllegalArgumentException("Invalid secret");
        }

        this.key = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));

        this.accessTtlSeconds = accessTtlSeconds;
        this.refreshTtlSeconds = refreshTtlSeconds;
        this.issuer = issuer;
    }

    public String generateAccessToken(UserPrincipal user){
        Instant now = Instant.now();
        List<String> roles = user
                .getAuthorities()
                .stream()
                .map(GrantedAuthority::getAuthority)
                .toList();

        return Jwts.builder()
                .id(UUID.randomUUID().toString())
                .subject(user.getId().toString())
                .issuer(issuer)
                .issuedAt(Date.from(now))
                .expiration(Date.from(now.plusSeconds(accessTtlSeconds)))
                .claims(Map.of(
                        "email", user.getUsername(),
                        "roles", roles,
                        "type", "access"
                ))
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }

    public String generateRefreshToken(UserPrincipal user, String jti){
        Instant now = Instant.now();

        return Jwts.builder()
                .id(jti)
                .subject(user.getId().toString())
                .issuer(issuer)
                .issuedAt(Date.from(now))
                .expiration(Date.from(now.plusSeconds(refreshTtlSeconds)))
                .claim("type", "refresh")
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }

    public Jws<Claims> parse(String token){
        return Jwts.parser().verifyWith(key).build().parseSignedClaims(token);
    }

    public UUID extractUserId(String token){
        Claims claims = parse(token).getPayload();
        return UUID.fromString(claims.getSubject());
    }

    public String extractUsername(String token) {
        Claims claims = parse(token).getPayload();
        return claims.get("email", String.class);
    }

    public List<String> extractRoles(String token) {
        Claims claims = parse(token).getPayload();
        return claims.get("roles", List.class);
    }

    public boolean isAccessToken(String token) {
        Claims claims = parse(token).getPayload();
        return "access".equals(claims.get("type"));
    }

    public boolean isRefreshToken(String token) {
        Claims claims = parse(token).getPayload();
        return "refresh".equals(claims.get("type"));
    }

    public String extractJti(String token) {
        Claims claims = parse(token).getPayload();
        return claims.getId();
    }

    public boolean isTokenExpired(String token){
        Claims claims = parse(token).getPayload();
        return claims.getExpiration().before(new Date());
    }

    public boolean validateToken(String token){
        try {
            parse(token);
            return true;
        }catch (Exception e){
            return false;
        }
    }
}
