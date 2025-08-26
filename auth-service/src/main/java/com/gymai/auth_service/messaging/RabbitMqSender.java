package com.gymai.auth_service.messaging;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class RabbitMqSender {

    private final RabbitTemplate rabbitTemplate;

    public void sendMessageToRoute(Object message, String exchange, String routingKey) {
        try {
            rabbitTemplate.convertAndSend(exchange, routingKey, message);
            log.info("✅ Sent message to exchange [{}] with routing key [{}]: {}",
                    exchange, routingKey, message);
        } catch (Exception e) {
            log.error("❌ Failed to send message to RabbitMQ: {}", e.getMessage(), e);
        }
    }
}
