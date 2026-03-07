package com.ranjankr73.projects.auth_app.config;

import com.ranjankr73.projects.auth_app.entities.UserPrincipal;
import com.ranjankr73.projects.auth_app.repositories.UserRepository;
import com.ranjankr73.projects.auth_app.services.JwtService;
import io.jsonwebtoken.*;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class JwtFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final UserRepository userRepository;

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {

//        String path = request.getServletPath();
//
//        if(path.startsWith("/api/v1/auth/")){
//            filterChain.doFilter(request, response);
//            return;
//        }

        String authHeader = request.getHeader("Authorization");

        if(authHeader != null && authHeader.startsWith("Bearer ") &&
            SecurityContextHolder.getContext().getAuthentication() == null){

            String token = authHeader.substring(7);

            try {

                if(!jwtService.isAccessToken(token)){
                    filterChain.doFilter(request, response);
                    return;
                }

                UUID userId = jwtService.extractUserId(token);

                userRepository.findById(userId)
                        .ifPresent(user -> {
                            UserPrincipal principal = new UserPrincipal(user);

                            UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                                    principal,
                                    null,
                                    principal.getAuthorities()
                            );

                            authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                            SecurityContextHolder.getContext().setAuthentication(authentication);
                        });
            }catch (ExpiredJwtException e){
                e.printStackTrace();
            }catch (MalformedJwtException e){
                e.printStackTrace();
            }catch (JwtException e){
                e.printStackTrace();
            }catch (Exception e){
                e.printStackTrace();
            }
        }

        filterChain.doFilter(request, response);
    }
}
