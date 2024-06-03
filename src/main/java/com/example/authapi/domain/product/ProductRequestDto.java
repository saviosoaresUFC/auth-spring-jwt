package com.example.authapi.domain.product;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record ProductRequestDto(
    @NotBlank(message = "Name is required") String name,
    @NotNull(message = "Price is required") Integer price
) {
}
