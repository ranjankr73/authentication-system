package com.ranjankr73.projects.auth_app.repositories;

import com.ranjankr73.projects.auth_app.entities.RefreshToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface RefreshTokenRepository extends JpaRepository<RefreshToken, UUID> {
    Optional<RefreshToken> findByJti(String jti);
    void deleteByUserId(UUID userId);
}
