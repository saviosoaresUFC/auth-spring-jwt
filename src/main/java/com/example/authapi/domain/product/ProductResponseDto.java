package com.example.authapi.domain.product;

import java.util.UUID;

public record ProductResponseDto(
        UUID id,
        String observation,
        String productName,
        boolean status,
        String urlImage
){
    public ProductResponseDto(Product product) {
        this(product.getId(), product.getObservation(), product.getProductName(), product.isStatus(), product.getUrlImage());
    }
}
