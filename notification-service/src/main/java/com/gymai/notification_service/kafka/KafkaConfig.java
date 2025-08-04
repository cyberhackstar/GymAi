package com.gymai.notification_service.kafka;

import java.util.Map;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;

import com.gymai.notification_service.dto.NotificationDTO;

@Configuration
public class KafkaConfig {

    @Bean
    public ConsumerFactory<String, NotificationDTO> consumerFactory() {
        return new DefaultKafkaConsumerFactory<>(Map.of(
            ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092",
            ConsumerConfig.GROUP_ID_CONFIG, "notification-group",
            ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class,
            ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, JsonDeserializer.class
        ), new StringDeserializer(), new JsonDeserializer<>(NotificationDTO.class));
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, NotificationDTO> notificationKafkaListenerContainerFactory() {
        var factory = new ConcurrentKafkaListenerContainerFactory<String, NotificationDTO>();
        factory.setConsumerFactory(consumerFactory());
        return factory;
    }
}
