package com.example.user_authentication.service;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.example.user_authentication.dto.AuthResponse;
import com.example.user_authentication.dto.LoginRequest;
import com.example.user_authentication.dto.RegisterRequest;
import com.example.user_authentication.dto.UserResponse;
import com.example.user_authentication.entity.User;
import com.example.user_authentication.repository.UserRepository;
import com.example.user_authentication.util.JwtUtil;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    public AuthService(UserRepository userRepository, PasswordEncoder passwordEncoder, JwtUtil jwtUtil) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtil = jwtUtil;
    }

    public com.example.user_authentication.dto.MessageResponse register(RegisterRequest request) {
        if (userRepository.findByEmail(request.email()).isPresent()) {
            throw new RuntimeException("Email already exists");
        }

        User user = new User();
        user.setUsername(request.username());
        user.setEmail(request.email());
        user.setPasswordHash(passwordEncoder.encode(request.password()));

        userRepository.save(user);
        return new com.example.user_authentication.dto.MessageResponse("User registered successfully");
    }

    public AuthResponse login(LoginRequest request) {
        User user = userRepository.findByEmail(request.email())
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (!passwordEncoder.matches(request.password(), user.getPasswordHash())) {
            throw new RuntimeException("Invalid password");
        }

        String token = jwtUtil.generateToken(user);
        return new AuthResponse(token);
    }

    public List<UserResponse> getAllUsers() {
        return userRepository.findAll().stream()
                .map(user -> new UserResponse(
                        user.getId(),
                        user.getUsername(),
                        user.getEmail(),
                        user.getRole()))
                .collect(Collectors.toList());
    }

}
