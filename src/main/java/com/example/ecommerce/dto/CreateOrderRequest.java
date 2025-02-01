package com.example.ecommerce.dto;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class CreateOrderRequest {
    private Long userId;
    private Long productId;
    private int quantity;
}
