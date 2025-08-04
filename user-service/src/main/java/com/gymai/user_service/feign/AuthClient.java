package com.gymai.user_service.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;

@FeignClient(name = "auth-service", url = "${feign.client.config.auth-service.url}")
public interface AuthClient {

    @GetMapping("/api/auth/validate")
    boolean validateToken(@RequestHeader("Authorization") String token);
}
