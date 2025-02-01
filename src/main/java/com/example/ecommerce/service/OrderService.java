// src/main/java/com/example/ecommerce/service/OrderService.java
package com.example.ecommerce.service;

import com.example.ecommerce.domain.Order;
import com.example.ecommerce.domain.Product;
import com.example.ecommerce.domain.User;
import com.example.ecommerce.dto.CreateOrderRequest;
import com.example.ecommerce.messaging.OrderCreatedProducer;
import com.example.ecommerce.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class OrderService {
    
    private final OrderRepository orderRepository;
    private final UserService userService;
    private final ProductService productService;
    private final OrderCreatedProducer orderCreatedProducer;
    
    @Transactional
    public Order createOrder(CreateOrderRequest req) {
        // 유저 & 상품 조회
        User user = userService.getUser(req.getUserId());
        Product product = productService.decreaseStock(req.getProductId(), req.getQuantity());
        
        // 주문 생성
        Order order = new Order();
        order.setUser(user);
        order.setProduct(product);
        order.setQuantity(req.getQuantity());
        order.setTotalPrice(product.getPrice() * req.getQuantity());

        Order savedOrder = orderRepository.save(order);

        // 주문 생성 이벤트를 RabbitMQ로 발행
        orderCreatedProducer.sendOrderCreated(savedOrder);

        return savedOrder;
    }

    public Order getOrder(Long orderId) {
        return orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found"));
    }
}
