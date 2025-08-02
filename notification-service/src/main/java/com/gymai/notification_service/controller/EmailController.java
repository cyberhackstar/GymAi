
package com.gym.notification.controller;

import com.gym.notification.dto.EmailRequest;
import com.gym.notification.service.EmailService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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
