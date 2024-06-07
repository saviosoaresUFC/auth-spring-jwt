package com.example.authapi.domain.user;

import jakarta.validation.constraints.NotNull;

public record AuthenticationDTO(
        @NotNull(message = "Email cannot be null") String email,
        @NotNull(message = "Password cannot be null") String password
) {
}
