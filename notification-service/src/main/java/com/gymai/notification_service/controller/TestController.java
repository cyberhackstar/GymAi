package com.gymai.notification_service.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.gymai.notification_service.dto.NotificationDTO;
import com.gymai.notification_service.service.EmailService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/notify")
@RequiredArgsConstructor
public class TestController {

    private final EmailService emailService;

    @PostMapping("/send")
    public ResponseEntity<?> testEmail(@RequestBody NotificationDTO dto) {
        emailService.sendEmail(dto.getEmail(), dto.getSubject(), dto.getMessage());
        return ResponseEntity.ok("Email sent");
    }
}
