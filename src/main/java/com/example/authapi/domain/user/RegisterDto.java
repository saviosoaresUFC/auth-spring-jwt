package com.example.authapi.domain.user;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record RegisterDto (
    @NotBlank(message = "Login is required") String login,
    @NotBlank(message = "Password is required") String password,
    @NotNull(message = "Role is required") UserRole role
){
}
