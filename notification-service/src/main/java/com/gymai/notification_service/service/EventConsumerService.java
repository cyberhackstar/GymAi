package com.gymai.notification_service.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;

import com.gymai.notification_service.dto.UserEvent;

@Slf4j
@Service
@RequiredArgsConstructor
public class EventConsumerService {

    private final EmailService emailService;

    @RabbitListener(queues = "${event.queue.email:gymai.email.queue}")
    public void handleUserEvent(UserEvent event) {
        log.info("Received event: {} for user: {}", event.getEventType(), event.getEmail());

        try {
            switch (event.getEventType().toUpperCase()) {
                case "REGISTRATION":
                    emailService.sendWelcomeEmail(event.getEmail(), event.getName());
                    break;
                case "LOGIN":
                    emailService.sendLoginNotificationEmail(event.getEmail(), event.getName());
                    break;
                case "ORDER_CREATED":
                case "ORDER":
                    emailService.sendOrderConfirmationEmail(event.getEmail(), event.getName());
                    break;
                case "PASSWORD_RESET":
                    emailService.sendPasswordResetEmail(event.getEmail(), event.getName());
                    break;
                default:
                    log.warn("Unknown event type: {}", event.getEventType());
            }
        } catch (Exception e) {
            log.error("Failed to process event: {} for user: {}", event.getEventType(), event.getEmail(), e);
            // Here you might want to implement retry logic or dead letter queue
        }
    }
}