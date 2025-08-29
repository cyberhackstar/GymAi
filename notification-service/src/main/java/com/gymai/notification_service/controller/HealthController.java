package com.gymai.notification_service.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api/health")
@RequiredArgsConstructor
public class HealthController {

    private final RabbitAdmin rabbitAdmin;

    @GetMapping
    public ResponseEntity<Map<String, String>> health() {
        log.info("Health check endpoint called for email-notification-service");

        Map<String, String> status = new HashMap<>();
        status.put("status", "UP");
        status.put("service", "email-notification-service");

        try {
            // Check RabbitMQ connection
            rabbitAdmin.getQueueProperties("gymai.email.queue");
            status.put("rabbitmq", "CONNECTED");
            log.info("RabbitMQ connection check successful: CONNECTED");
        } catch (Exception e) {
            status.put("rabbitmq", "DISCONNECTED");
            log.error("RabbitMQ connection check failed: DISCONNECTED", e);
        }

        log.debug("Health check response: {}", status);
        return ResponseEntity.ok(status);
    }
}
