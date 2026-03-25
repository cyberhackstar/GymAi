package com.gymai.api_gateway.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/fallback")
public class FallbackController {

    @GetMapping("/auth")
    public ResponseEntity<Map<String, Object>> authServiceFallback() {
        Map<String, Object> response = new HashMap<>();
        response.put("error", "Authentication Service Unavailable");
        response.put("message", "The authentication service is currently unavailable. Please try again later.");
        response.put("timestamp", LocalDateTime.now());
        response.put("service", "auth-service");
        response.put("status", "SERVICE_UNAVAILABLE");
        
        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body(response);
    }

    @GetMapping("/users")
    public ResponseEntity<Map<String, Object>> userServiceFallback() {
        Map<String, Object> response = new HashMap<>();
        response.put("error", "User Service Unavailable");
        response.put("message", "The user service is currently unavailable. Please try again later.");
        response.put("timestamp", LocalDateTime.now());
        response.put("service", "user-service");
        response.put("status", "SERVICE_UNAVAILABLE");
        
        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body(response);
    }

    @GetMapping("/plans")
    public ResponseEntity<Map<String, Object>> planServiceFallback() {
        Map<String, Object> response = new HashMap<>();
        response.put("error", "Plan Service Unavailable");
        response.put("message", "The plan service is currently unavailable. Please try again later.");
        response.put("timestamp", LocalDateTime.now());
        response.put("service", "plan-service");
        response.put("status", "SERVICE_UNAVAILABLE");
        
        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body(response);
    }

    @GetMapping("/notifications")
    public ResponseEntity<Map<String, Object>> notificationServiceFallback() {
        Map<String, Object> response = new HashMap<>();
        response.put("error", "Notification Service Unavailable");
        response.put("message", "The notification service is currently unavailable. Please try again later.");
        response.put("timestamp", LocalDateTime.now());
        response.put("service", "notification-service");
        response.put("status", "SERVICE_UNAVAILABLE");
        
        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body(response);
    }

    @GetMapping("/default")
    public ResponseEntity<Map<String, Object>> defaultFallback() {
        Map<String, Object> response = new HashMap<>();
        response.put("error", "Service Unavailable");
        response.put("message", "One or more services are currently unavailable. Please try again later.");
        response.put("timestamp", LocalDateTime.now());
        response.put("service", "unknown");
        response.put("status", "SERVICE_UNAVAILABLE");
        
        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body(response);
    }
}