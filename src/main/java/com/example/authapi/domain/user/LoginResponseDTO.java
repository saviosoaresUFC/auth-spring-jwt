package com.example.authapi.domain.user;

public record LoginResponseDTO( String name, String token, UserRole role, String refreshToken) {
}
