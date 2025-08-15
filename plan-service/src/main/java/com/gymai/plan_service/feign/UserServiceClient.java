package com.gymai.plan_service.feign;

import com.gymai.plan_service.dto.UserDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(
    name = "user-service",
    url = "${user.service.url}", // example: http://localhost:8082
    configuration = FeignClientConfig.class
)
public interface UserServiceClient {
    @GetMapping("/api/user/profile/{userId}")
    UserDto getUserProfile(@PathVariable("userId") Long userId);
}
