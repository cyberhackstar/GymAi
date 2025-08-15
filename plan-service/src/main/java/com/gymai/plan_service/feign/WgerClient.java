package com.gymai.plan_service.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Map;

@FeignClient(name = "wger", url = "${wger.service.url}")
public interface WgerClient {

    // public exercise list endpoint e.g. /exercise/?language=2&limit=100
    @GetMapping("/exercise/")
    Map<String, Object> getExercises(
            @RequestParam(value = "language", required = false, defaultValue = "2") int language,
            @RequestParam(value = "limit", required = false, defaultValue = "50") int limit);
}
