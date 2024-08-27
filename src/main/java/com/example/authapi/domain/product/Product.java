package com.example.authapi.domain.product;

import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

@Entity
@Table(name = "tb_products")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
//@EqualsAndHashCode(of = "id")
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    private String observation;
    private String productName;
    private boolean status;
    private String urlImage;

    public Product (ProductRequestDto productDto) {
        this.observation = productDto.observation();
        this.productName = productDto.productName();
        this.urlImage = productDto.urlImage();
    }

}
