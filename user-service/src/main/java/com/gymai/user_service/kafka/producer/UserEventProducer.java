package com.gymai.user_service.kafka.producer;

import com.gymai.user_service.dto.UserDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class UserEventProducer {

    private final KafkaTemplate<String, Object> kafkaTemplate;

    @Value("${kafka.user-topic}")
    private String topic;

    public void sendUserEvent(UserDto user) {
        log.info("Sending user event to topic {}: {}", topic, user);
        kafkaTemplate.send(topic, user.getUserId().toString(), user);
    }
}