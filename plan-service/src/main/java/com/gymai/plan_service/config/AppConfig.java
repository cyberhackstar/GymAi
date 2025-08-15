package com.gymai.plan_service.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.web.client.RestTemplate;

import java.util.concurrent.Executor;

@Configuration
public class AppConfig {

  @Bean("planExecutor")
  public Executor planExecutor() {
    ThreadPoolTaskExecutor exec = new ThreadPoolTaskExecutor();
    exec.setCorePoolSize(4);
    exec.setMaxPoolSize(10);
    exec.setQueueCapacity(100);
    exec.setThreadNamePrefix("plan-exec-");
    exec.initialize();
    return exec;
  }

  @Bean
  public RestTemplate restTemplate() {
    return new RestTemplate();
  }

}
