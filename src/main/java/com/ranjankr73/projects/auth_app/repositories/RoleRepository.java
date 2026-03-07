package com.ranjankr73.projects.auth_app.repositories;

import com.ranjankr73.projects.auth_app.entities.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface RoleRepository extends JpaRepository<Role, UUID> {
    Optional<Role> findByNameIgnoreCase(String name);
    List<Role> findAllByNameIn(Collection<String> names);
    boolean existsByNameIgnoreCase(String name);
}
