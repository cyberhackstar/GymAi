package com.gymai.notification_service.config;

import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.config.SimpleRabbitListenerContainerFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {

    @Value("${event.exchange:gymai.events}")
    private String exchangeName;

    @Value("${event.queue.email:gymai.email.queue}")
    private String emailQueueName;

    @Value("${event.routing.registration:user.registration}")
    private String registrationRouting;

    @Value("${event.routing.login:user.login}")
    private String loginRouting;

    // Additional routing keys for other events you might have
    @Value("${event.routing.order:order.created}")
    private String orderRouting;

    @Value("${event.routing.password-reset:user.password.reset}")
    private String passwordResetRouting;

    @Bean
    public DirectExchange directExchange() {
        return new DirectExchange(exchangeName, true, false);
    }

    @Bean
    public Queue emailQueue() {
        return QueueBuilder.durable(emailQueueName).build();
    }

    // Bind queue to multiple routing keys to consume all events
    @Bean
    public Binding registrationBinding() {
        return BindingBuilder.bind(emailQueue())
                .to(directExchange())
                .with(registrationRouting);
    }

    @Bean
    public Binding loginBinding() {
        return BindingBuilder.bind(emailQueue())
                .to(directExchange())
                .with(loginRouting);
    }

    @Bean
    public Binding orderBinding() {
        return BindingBuilder.bind(emailQueue())
                .to(directExchange())
                .with(orderRouting);
    }

    @Bean
    public Binding passwordResetBinding() {
        return BindingBuilder.bind(emailQueue())
                .to(directExchange())
                .with(passwordResetRouting);
    }

    @Bean
    public Jackson2JsonMessageConverter messageConverter() {
        return new Jackson2JsonMessageConverter();
    }

    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory) {
        RabbitTemplate template = new RabbitTemplate(connectionFactory);
        template.setMessageConverter(messageConverter());
        return template;
    }

    @Bean
    public SimpleRabbitListenerContainerFactory rabbitListenerContainerFactory(ConnectionFactory connectionFactory) {
        SimpleRabbitListenerContainerFactory factory = new SimpleRabbitListenerContainerFactory();
        factory.setConnectionFactory(connectionFactory);
        factory.setMessageConverter(messageConverter());
        factory.setConcurrentConsumers(3);
        factory.setMaxConcurrentConsumers(10);
        return factory;
    }

    @Bean
    public RabbitAdmin rabbitAdmin(ConnectionFactory connectionFactory) {
        return new RabbitAdmin(connectionFactory);
    }
}
