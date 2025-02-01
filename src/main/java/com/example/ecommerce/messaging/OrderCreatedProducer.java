package com.example.ecommerce.messaging;

import com.example.ecommerce.config.RabbitMQConfig;
import com.example.ecommerce.domain.Order;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class OrderCreatedProducer {

    private final RabbitTemplate rabbitTemplate;

    public void sendOrderCreated(Order order) {
        // 주문 ID, 유저명, 총가격 등 간단한 정보만 전송
        String message = String.format(
            "OrderCreated - OrderID:%d, User:%s, TotalPrice:%d",
            order.getId(), order.getUser().getUsername(), order.getTotalPrice()
        );

        rabbitTemplate.convertAndSend(
                RabbitMQConfig.ORDER_EXCHANGE, 
                RabbitMQConfig.ORDER_CREATED_ROUTING_KEY, 
                message
        );
    }
}
