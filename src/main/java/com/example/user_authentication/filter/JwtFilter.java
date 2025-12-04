package com.example.user_authentication.filter;

import java.io.IOException;
import java.util.List;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.example.user_authentication.repository.UserRepository;
import com.example.user_authentication.util.JwtUtil;

import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class JwtFilter extends OncePerRequestFilter {
    private final JwtUtil jwtUtil;
    private final UserRepository userRepository;

    public JwtFilter(JwtUtil jwtUtil, UserRepository userRepository) {
        this.jwtUtil = jwtUtil;
        this.userRepository = userRepository;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        String authHeader = request.getHeader("Authorization");

        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String token = authHeader.substring(7);

            try {
                Claims claims = jwtUtil.parse(token);
                Long userId = Long.valueOf(claims.getSubject());
                userRepository.findById(userId).ifPresent(user -> {
                    UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(user,
                            null, List.of(new SimpleGrantedAuthority(user.getRole())));

                    SecurityContextHolder.getContext().setAuthentication(authentication);
                });

            } catch (Exception e) {
                SecurityContextHolder.clearContext();
                throw new ServletException("Invalid token");
            }
        }

        filterChain.doFilter(request, response);
    }

}
