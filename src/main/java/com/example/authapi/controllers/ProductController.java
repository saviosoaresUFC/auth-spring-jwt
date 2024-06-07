package com.example.authapi.controllers;

import com.example.authapi.domain.product.Product;
import com.example.authapi.domain.product.ProductRequestDto;
import com.example.authapi.domain.product.ProductResponseDto;
import com.example.authapi.repositories.ProductRepository;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/product")
@RequiredArgsConstructor
public class ProductController {
    private final ProductRepository productRepository;

    @PostMapping("/add")
    public ResponseEntity<String> addProduct(@RequestBody @Valid ProductRequestDto productRequestDto) {
        Product newProduct = new Product(productRequestDto);
        productRepository.save(newProduct);
        return ResponseEntity.ok("Product added successfully");
    }

    @GetMapping("/getAll")
    public ResponseEntity<Iterable<ProductResponseDto>> getAllProducts() {
        List<ProductResponseDto> productList = productRepository.findAll().stream().map(ProductResponseDto::new).toList();
        return ResponseEntity.ok(productList);
    }
}
