package com.example.authapi.controllers;

import com.example.authapi.domain.product.Product;
import com.example.authapi.domain.product.ProductRequestDto;
import com.example.authapi.domain.product.ProductResponseDto;
import com.example.authapi.repositories.ProductRepository;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/product")
@RequiredArgsConstructor
public class ProductController {
    private final ProductRepository productRepository;

//    Save a product
    @PostMapping("/save_product")
    public ResponseEntity<Product> addProduct(@RequestBody @Valid ProductRequestDto productRequestDto) {
        Product newProduct = new Product();
        newProduct.setStatus(true);
        BeanUtils.copyProperties(productRequestDto, newProduct);
        return ResponseEntity.status(HttpStatus.CREATED).body(productRepository.save(newProduct));
    }

    @GetMapping("/get_all_products")
    public ResponseEntity<Iterable<ProductResponseDto>> getAllProducts() {
        List<ProductResponseDto> productList = productRepository.findAll().stream().map(ProductResponseDto::new).toList();
        return ResponseEntity.status(HttpStatus.OK).body(productList);
    }
}
