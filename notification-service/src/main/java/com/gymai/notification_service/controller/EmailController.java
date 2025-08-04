package com.gymai.notification_service.controller;


import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.gymai.notification_service.dto.EmailRequest;
import com.gymai.notification_service.service.EmailService;

@RestController
@RequestMapping("/api/notify")
@RequiredArgsConstructor
public class EmailController {

    private final EmailService emailService;

    @PostMapping("/send-verification")
    public ResponseEntity<String> sendEmail(@RequestBody EmailRequest request) {
        emailService.sendVerificationEmail(request.getTo(), request.getToken());
        return ResponseEntity.ok("Verification email sent!");
    }
}
