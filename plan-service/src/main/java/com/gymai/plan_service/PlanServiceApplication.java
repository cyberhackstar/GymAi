package com.gymai.plan_service;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
// @EnableFeignClients(basePackages = "com.gymai.plan_service.feign")
// @ComponentScan(basePackages = { "com.gymai.plan_service",
// "com.gymai.security" })
// @EnableCaching
@EnableAsync
public class PlanServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(PlanServiceApplication.class, args);
	}

}
