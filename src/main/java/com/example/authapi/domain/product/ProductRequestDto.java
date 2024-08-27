package com.example.authapi.domain.product;

import jakarta.validation.constraints.NotNull;

public record ProductRequestDto(
    String observation,
    @NotNull(message = "Name is required") String productName,
    @NotNull(message = "Link is required") String urlImage
) {
}
