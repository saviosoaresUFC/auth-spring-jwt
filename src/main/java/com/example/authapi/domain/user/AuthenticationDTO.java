package com.example.authapi.domain.user;

import jakarta.validation.constraints.NotNull;

public record AuthenticationDTO(
        @NotNull(message = "Login cannot be null") String login,
        @NotNull(message = "Password cannot be null") String password
) {
}
