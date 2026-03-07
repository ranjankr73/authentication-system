package com.ranjankr73.projects.auth_app.repositories;

import com.ranjankr73.projects.auth_app.entities.Provider;
import com.ranjankr73.projects.auth_app.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserRepository extends JpaRepository<User, UUID> {
    Optional<User> findByEmailIgnoreCase(String email);
    Optional<User> findByEmailAndProvider(String email, Provider provider);
    boolean existsByEmailIgnoreCase(String email);
}
