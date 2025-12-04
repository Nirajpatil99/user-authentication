package com.example.user_authentication.dto;

public record RegisterRequest(
        String username,
        String email,
        String password) {
}
