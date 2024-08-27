package com.example.authapi.domain.user;

import jakarta.validation.constraints.NotBlank;

public record RefreshTokenRequestDTO(
        @NotBlank(message = "Enter your refresh-token") String refreshToken
) {
}
