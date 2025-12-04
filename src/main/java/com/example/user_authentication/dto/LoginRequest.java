package com.example.user_authentication.dto;

public record LoginRequest(
        String email,
        String password) {

}
