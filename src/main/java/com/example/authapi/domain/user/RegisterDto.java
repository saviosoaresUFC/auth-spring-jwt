package com.example.authapi.domain.user;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record RegisterDto (
    @NotBlank(message = "Name is required") String name,
    @NotBlank(message = "Email is required") String email,
    @NotBlank(message = "Password is required") String password,
    @NotNull(message = "Role is required") UserRole role
){
}
