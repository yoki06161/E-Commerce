// src/main/java/com/example/ecommerce/messaging/OrderCreatedConsumer.java
package com.example.ecommerce.messaging;

import com.example.ecommerce.config.RabbitMQConfig;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class OrderCreatedConsumer {

    @RabbitListener(queues = RabbitMQConfig.ORDER_CREATED_QUEUE)
    public void consumeOrderCreatedMessage(String message) {
        // 여기서는 단순히 로그 출력
        // 실제로는 이메일 전송, 알림, 배송 예약 로직 등을 수행할 수 있음
        log.info("[RabbitMQ] Received: {}", message);
    }
}
