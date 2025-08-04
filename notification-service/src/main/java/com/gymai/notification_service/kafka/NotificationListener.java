package com.gymai.notification_service.kafka;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import com.gymai.notification_service.dto.NotificationDTO;
import com.gymai.notification_service.service.EmailService;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class NotificationListener {

    private final EmailService emailService;

    @KafkaListener(topics = "notification_topic", groupId = "notification-group",
                   containerFactory = "notificationKafkaListenerContainerFactory")
    public void handleNotification(NotificationDTO dto) {
        emailService.sendEmail(dto.getEmail(), dto.getSubject(), dto.getMessage());
    }
}
