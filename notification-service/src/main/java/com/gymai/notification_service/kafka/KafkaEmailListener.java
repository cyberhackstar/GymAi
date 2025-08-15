package com.gymai.notification_service.kafka;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import com.gymai.notification_service.dto.UserRegistrationEvent;
import com.gymai.notification_service.service.EmailService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class KafkaEmailListener {

    private final EmailService emailService;

    @KafkaListener(topics = "user-registration", groupId = "notification-service-group", containerFactory = "kafkaListenerContainerFactory")
    public void listenUserRegistration(UserRegistrationEvent event) {
        emailService.sendRegistrationSuccessEmail(event.getEmail(), event.getUsername());
    }
}
