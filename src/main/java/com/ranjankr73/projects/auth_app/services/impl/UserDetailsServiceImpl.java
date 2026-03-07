package com.ranjankr73.projects.auth_app.services.impl;

import com.ranjankr73.projects.auth_app.entities.User;
import com.ranjankr73.projects.auth_app.entities.UserPrincipal;
import com.ranjankr73.projects.auth_app.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserDetailsServiceImpl implements UserDetailsService {

    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        User user = userRepository
                .findByEmailIgnoreCase(username)
                .orElseThrow(() -> new UsernameNotFoundException("User with given email not found"));

        if(!user.isEnabled()){
            throw new UsernameNotFoundException("User account is disabled");
        }

        return new UserPrincipal(user);
    }
}
