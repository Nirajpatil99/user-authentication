package com.example.user_authentication.dto;

public record UserResponse(
        Long id,
        String username,
        String email,
        String role) {
}
