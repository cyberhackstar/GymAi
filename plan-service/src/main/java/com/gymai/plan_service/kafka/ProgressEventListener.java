// package com.gymai.plan_service.kafka;

// import com.gymai.plan_service.service.PlanService;
// import lombok.RequiredArgsConstructor;
// import lombok.extern.slf4j.Slf4j;
// import org.springframework.kafka.annotation.KafkaListener;
// import org.springframework.stereotype.Component;

// @Component
// @RequiredArgsConstructor
// @Slf4j
// public class ProgressEventListener {

// private final PlanService planService;

// @KafkaListener(topics = "user-progress", groupId = "plan-service-group")
// public void listen(String message) {
// log.info("Received Kafka message on 'user-progress': {}", message);
// // Expected message format: userId:feedback_text
// try {
// String[] parts = message.split(":", 2);
// Long userId = Long.parseLong(parts[0]);
// String feedback = parts[1];

// log.info("Parsed Kafka message - userId: {}, feedback: {}", userId,
// feedback);

// planService.updateFeedback(userId, feedback);

// log.info("Successfully updated feedback for userId: {}", userId);
// } catch (Exception e) {
// log.error("Error while processing Kafka message: {}", message, e);
// }
// }
// }
