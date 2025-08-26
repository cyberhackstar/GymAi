package com.gymai.auth_service.messaging;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {

  @Value("${event.exchange}")
  private String exchange;

  @Value("${event.routing.registration}")
  private String registrationRouting;

  @Value("${event.routing.login}")
  private String loginRouting;

  // single queue for user-events (simple setup)
  public static final String USER_EVENTS_QUEUE = "gymai.user.events";

  @Bean
  public DirectExchange eventExchange() {
    return new DirectExchange(exchange, true, false);
  }

  @Bean
  public Queue userEventsQueue() {
    return new Queue(USER_EVENTS_QUEUE, true);
  }

  @Bean
  public Binding registrationBinding(Queue userEventsQueue, DirectExchange eventExchange) {
    return BindingBuilder.bind(userEventsQueue).to(eventExchange).with(registrationRouting);
  }

  @Bean
  public Binding loginBinding(Queue userEventsQueue, DirectExchange eventExchange) {
    return BindingBuilder.bind(userEventsQueue).to(eventExchange).with(loginRouting);
  }
}