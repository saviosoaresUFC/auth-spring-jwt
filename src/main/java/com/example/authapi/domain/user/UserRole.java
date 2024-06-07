package com.example.authapi.domain.user;

import lombok.Getter;

@Getter
public enum UserRole {
    USER("user"),
    DIRECTORY("directory"),
    ADMIN("admin");

    private final String role;

    UserRole(String role) {
        this.role = role;
    }
}
