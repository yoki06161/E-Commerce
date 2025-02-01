package com.example.ecommerce.dto;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class CreateProductRequest {
    private String name;
    private int price;
    private int stockQuantity;
}
