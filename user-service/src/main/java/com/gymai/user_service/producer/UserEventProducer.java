package com.gymai.user_service.producer;

import com.gymai.user_service.dto.UserDto;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class UserEventProducer {

  private final RabbitTemplate rabbitTemplate;
  private final ObjectMapper objectMapper;

  // These names must match application.yml
  private static final String EXCHANGE = "gym.exchange";
  private static final String ROUTING_KEY = "user.event";

  public void publishUserEvent(UserDto dto) {
    try {
      String payload = objectMapper.writeValueAsString(dto);
      rabbitTemplate.convertAndSend(EXCHANGE, ROUTING_KEY, payload);
      log.debug("Published user event to exchange='{}' routingKey='{}' payload={}", EXCHANGE, ROUTING_KEY, payload);
    } catch (JsonProcessingException e) {
      log.error("Failed to serialize user event: {}", e.getMessage(), e);
    }
  }
}