package com.example.ecommerce.domain;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "orders")
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
public class Order {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // User와 N:1 관계
    @ManyToOne
    private User user;

    // Product와 N:1 관계
    @ManyToOne
    private Product product;

    private int quantity;

    private int totalPrice; // (price * quantity)
    
}
