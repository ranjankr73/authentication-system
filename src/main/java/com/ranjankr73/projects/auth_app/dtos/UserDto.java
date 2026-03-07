package com.ranjankr73.projects.auth_app.dtos;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.ranjankr73.projects.auth_app.entities.Provider;
import lombok.*;

import java.time.Instant;
import java.util.Set;
import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserDto {
    private UUID id;
    private String name;
    private String email;

    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private String password;

    private String image;
    private boolean enabled;
    private Set<String> roles;
    private Provider provider;

    private Instant createdAt;
    private Instant updatedAt;
}
