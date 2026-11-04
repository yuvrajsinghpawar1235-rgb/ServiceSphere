package com.servicesphere.dto;

public class AuthDtos {
    public record LoginRequest(String email, String password) {}
    public record AuthResponse(String token, String email, String role) {}
}