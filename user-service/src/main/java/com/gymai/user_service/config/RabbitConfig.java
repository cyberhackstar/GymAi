package com.gymai.user_service.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitConfig {

    public static final String USER_QUEUE = "user.events.queue";
    public static final String EXCHANGE = "gym.exchange";
    public static final String ROUTING_KEY = "user.event";

    @Bean
    public Queue userQueue() {
        return new Queue(USER_QUEUE, true);
    }

    @Bean
    public DirectExchange gymExchange() {
        return new DirectExchange(EXCHANGE);
    }

    @Bean
    public Binding bindingUserQueue(Queue userQueue, DirectExchange gymExchange) {
        return BindingBuilder.bind(userQueue).to(gymExchange).with(ROUTING_KEY);
    }
}