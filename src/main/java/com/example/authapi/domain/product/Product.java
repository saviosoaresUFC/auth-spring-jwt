package com.example.authapi.domain.product;

import jakarta.persistence.*;
import lombok.*;

@Entity(name = "Product")
@Table(name = "products")
@Getter
@AllArgsConstructor
@NoArgsConstructor
//@EqualsAndHashCode(of = "id")
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;
    private String name;
    private Integer price;

    public Product (ProductRequestDto productDto) {
        this.name = productDto.name();
        this.price = productDto.price();
    }

}
