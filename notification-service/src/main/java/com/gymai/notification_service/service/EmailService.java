package com.gymai.notification_service.service;

import com.gymai.notification_service.dto.EmailTemplate;

public interface EmailService {
    void sendEmail(EmailTemplate emailTemplate);

    void sendWelcomeEmail(String email, String name);

    void sendLoginNotificationEmail(String email, String name);

    void sendOrderConfirmationEmail(String email, String name);

    void sendPasswordResetEmail(String email, String name);
}