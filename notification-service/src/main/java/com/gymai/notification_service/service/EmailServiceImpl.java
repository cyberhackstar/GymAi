package com.gymai.notification_service.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import com.gymai.notification_service.dto.EmailTemplate;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import jakarta.mail.internet.InternetAddress;

import java.io.UnsupportedEncodingException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Slf4j
@Service
@RequiredArgsConstructor
public class EmailServiceImpl implements EmailService {

    private final JavaMailSender mailSender;

    // Use a static variable for the custom sender email
    private static final String SENDER_EMAIL = "gymai@neelahouse.cloud";

    @Value("${app.name:GymAI}")
    private String appName;

    @Override
    public void sendEmail(EmailTemplate emailTemplate) {
        log.info("Attempting to send an email to: {} with subject: {}", emailTemplate.getTo(),
                emailTemplate.getSubject());
        try {
            if (emailTemplate.isHtml()) {
                log.debug("Detected HTML content. Sending as a multipart message.");
                sendHtmlEmail(emailTemplate);
            } else {
                log.debug("Detected plain text content. Sending as a simple message.");
                sendTextEmail(emailTemplate);
            }
            log.info("Email sent successfully to: {}", emailTemplate.getTo());
        } catch (Exception e) {
            log.error("Failed to send email to: {}. Error: {}", emailTemplate.getTo(), e.getMessage(), e);
            throw new RuntimeException("Failed to send email", e);
        }
    }

    private void sendTextEmail(EmailTemplate emailTemplate) {
        log.debug("Preparing to send a text email to: {}", emailTemplate.getTo());
        SimpleMailMessage message = new SimpleMailMessage();
        try {
            message.setFrom(SENDER_EMAIL);
            message.setTo(emailTemplate.getTo());
            message.setSubject(emailTemplate.getSubject());
            message.setText(emailTemplate.getBody());
            mailSender.send(message);
        } catch (Exception e) {
            log.error("Error preparing or sending text email to {}: {}", emailTemplate.getTo(), e.getMessage(), e);
            throw e;
        }
    }

    private void sendHtmlEmail(EmailTemplate emailTemplate) throws MessagingException, UnsupportedEncodingException {
        log.debug("Preparing to send an HTML email to: {}", emailTemplate.getTo());
        try {
            MimeMessage mimeMessage = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true, "UTF-8");

            helper.setFrom(new InternetAddress(SENDER_EMAIL, appName));
            helper.setTo(emailTemplate.getTo());
            helper.setSubject(emailTemplate.getSubject());
            helper.setText(emailTemplate.getBody(), true);

            mailSender.send(mimeMessage);
        } catch (MessagingException | UnsupportedEncodingException e) {
            log.error("Error preparing or sending HTML email to {}: {}", emailTemplate.getTo(), e.getMessage(), e);
            throw e;
        }
    }

    @Override
    public void sendWelcomeEmail(String email, String name) {
        log.info("Initiating welcome email for new user: {}", email);
        String subject = "Welcome to " + appName + "!";
        String body = buildWelcomeEmailBody(name);
        EmailTemplate template = new EmailTemplate(email, subject, body, true);
        sendEmail(template);
    }

    @Override
    public void sendLoginNotificationEmail(String email, String name) {
        log.info("Initiating login notification email for user: {}", email);
        String subject = "Login Notification - " + appName;
        String body = buildLoginNotificationBody(name);
        EmailTemplate template = new EmailTemplate(email, subject, body, true);
        sendEmail(template);
    }

    @Override
    public void sendOrderConfirmationEmail(String email, String name) {
        log.info("Initiating order confirmation email for user: {}", email);
        String subject = "Order Confirmation - " + appName;
        String body = buildOrderConfirmationBody(name);
        EmailTemplate template = new EmailTemplate(email, subject, body, true);
        sendEmail(template);
    }

    @Override
    public void sendPasswordResetEmail(String email, String name) {
        log.info("Initiating password reset email for user: {}", email);
        String subject = "Password Reset - " + appName;
        String body = buildPasswordResetBody(name);
        EmailTemplate template = new EmailTemplate(email, subject, body, true);
        sendEmail(template);
    }

    private String buildWelcomeEmailBody(String name) {
        // ... (body remains the same)
        return """
                <!DOCTYPE html>
                <html>
                <head>
                    <meta charset="UTF-8">
                    <title>Welcome Email</title>
                </head>
                <body style="font-family: Arial, sans-serif; line-height: 1.6; color: #333;">
                    <div style="max-width: 600px; margin: 0 auto; padding: 20px;">
                        <h2 style="color: #2c3e50;">Welcome to %s! 🏋️‍♂️</h2>
                        <p>Dear %s,</p>
                        <p>Thank you for registering with us! We're excited to have you on board.</p>
                        <p>Your account has been successfully created. You can now enjoy all the features our platform has to offer.</p>
                        <div style="background-color: #f8f9fa; padding: 15px; border-radius: 5px; margin: 20px 0;">
                            <h3 style="margin-top: 0;">What's next?</h3>
                            <ul>
                                <li>Complete your profile setup</li>
                                <li>Explore our features</li>
                                <li>Join our community</li>
                            </ul>
                        </div>
                        <p>If you have any questions, feel free to reach out to our support team.</p>
                        <p>Best regards,<br>The %s Team</p>
                    </div>
                </body>
                </html>
                """
                .formatted(appName, name, appName);
    }

    private String buildLoginNotificationBody(String name) {
        // ... (body remains the same)
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        String formattedDateTime = LocalDateTime.now().format(formatter);
        return """
                <!DOCTYPE html>
                <html>
                <head>
                    <meta charset="UTF-8">
                    <title>Login Notification</title>
                </head>
                <body style="font-family: Arial, sans-serif; line-height: 1.6; color: #333;">
                    <div style="max-width: 600px; margin: 0 auto; padding: 20px;">
                        <h2 style="color: #2c3e50;">Login Detected 🔐</h2>
                        <p>Hello %s,</p>
                        <p>We noticed a recent login to your account at %s.</p>
                        <div style="background-color: #e8f5e8; padding: 15px; border-radius: 5px; margin: 20px 0; border-left: 4px solid #28a745;">
                            <p><strong>Login Time:</strong> %s</p>
                            <p><strong>Status:</strong> Successful</p>
                        </div>
                        <p>If this wasn't you, please contact our support team immediately.</p>
                        <p>Best regards,<br>The %s Team</p>
                    </div>
                </body>
                </html>
                """
                .formatted(name, appName, formattedDateTime, appName);
    }

    private String buildOrderConfirmationBody(String name) {
        // ... (body remains the same)
        return """
                <!DOCTYPE html>
                <html>
                <head>
                    <meta charset="UTF-8">
                    <title>Order Confirmation</title>
                </head>
                <body style="font-family: Arial, sans-serif; line-height: 1.6; color: #333;">
                    <div style="max-width: 600px; margin: 0 auto; padding: 20px;">
                        <h2 style="color: #2c3e50;">Order Confirmation ✅</h2>
                        <p>Dear %s,</p>
                        <p>Thank you for your order! We've received it and are processing it now.</p>
                        <div style="background-color: #fff3cd; padding: 15px; border-radius: 5px; margin: 20px 0; border-left: 4px solid #ffc107;">
                            <p><strong>Order Status:</strong> Processing</p>
                            <p><strong>Order Date:</strong> %s</p>
                        </div>
                        <p>You'll receive another email once your order ships.</p>
                        <p>Thank you for choosing %s!</p>
                        <p>Best regards,<br>The %s Team</p>
                    </div>
                </body>
                </html>
                """
                .formatted(name, LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE), appName, appName);
    }

    private String buildPasswordResetBody(String name) {
        // ... (body remains the same)
        return """
                <!DOCTYPE html>
                <html>
                <head>
                    <meta charset="UTF-8">
                    <title>Password Reset</title>
                </head>
                <body style="font-family: Arial, sans-serif; line-height: 1.6; color: #333;">
                    <div style="max-width: 600px; margin: 0 auto; padding: 20px;">
                        <h2 style="color: #2c3e50;">Password Reset Request 🔑</h2>
                        <p>Hello %s,</p>
                        <p>We received a request to reset your password for your %s account.</p>
                        <div style="background-color: #f8d7da; padding: 15px; border-radius: 5px; margin: 20px 0; border-left: 4px solid #dc3545;">
                            <p><strong>Security Notice:</strong> If you didn't request this reset, please ignore this email.</p>
                        </div>
                        <p>Your password has been successfully reset. You can now login with your new credentials.</p>
                        <p>Best regards,<br>The %s Team</p>
                    </div>
                </body>
                </html>
                """
                .formatted(name, appName, appName);
    }
}
