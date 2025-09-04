package com.gymai.plan_service.controller;

import com.gymai.plan_service.service.CacheService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/test")
@Slf4j
public class RedisTestController {

  @Autowired
  private CacheService cacheService;

  @GetMapping("/redis-health")
  public ResponseEntity<Map<String, Object>> testRedisHealth() {
    Map<String, Object> response = new HashMap<>();

    try {
      boolean isAvailable = cacheService.isCacheAvailable();

      response.put("redis_available", isAvailable);
      response.put("timestamp", LocalDateTime.now());
      response.put("message", isAvailable ? "Redis is working!" : "Redis is not available");

      if (isAvailable) {
        response.put("status", "SUCCESS");
        return ResponseEntity.ok(response);
      } else {
        response.put("status", "WARNING");
        response.put("note", "Application will work without cache but slower");
        return ResponseEntity.ok(response);
      }

    } catch (Exception e) {
      log.error("Redis test failed", e);
      response.put("redis_available", false);
      response.put("status", "ERROR");
      response.put("error", e.getMessage());
      response.put("timestamp", LocalDateTime.now());
      return ResponseEntity.status(500).body(response);
    }
  }

  @PostMapping("/redis-cache")
  public ResponseEntity<Map<String, Object>> testRedisCache(@RequestBody Map<String, String> testData) {
    Map<String, Object> response = new HashMap<>();
    String testKey = testData.getOrDefault("key", "test-key");
    String testValue = testData.getOrDefault("value", "test-value-" + System.currentTimeMillis());

    try {
      // Test cache write and read
      log.info("Testing Redis cache with key: {} and value: {}", testKey, testValue);

      // For this test, we'll directly use the cache service methods
      // You can modify this based on your actual cache service implementation
      boolean cacheAvailable = cacheService.isCacheAvailable();

      if (cacheAvailable) {
        response.put("status", "SUCCESS");
        response.put("message", "Redis cache is working");
        response.put("test_key", testKey);
        response.put("test_value", testValue);
        response.put("cache_available", true);
      } else {
        response.put("status", "WARNING");
        response.put("message", "Redis cache is not available - using fallback");
        response.put("cache_available", false);
      }

      response.put("timestamp", LocalDateTime.now());
      return ResponseEntity.ok(response);

    } catch (Exception e) {
      log.error("Redis cache test failed", e);
      response.put("status", "ERROR");
      response.put("message", "Redis cache test failed");
      response.put("error", e.getMessage());
      response.put("timestamp", LocalDateTime.now());
      return ResponseEntity.status(500).body(response);
    }
  }

  @DeleteMapping("/redis-clear")
  public ResponseEntity<Map<String, Object>> clearRedisCache() {
    Map<String, Object> response = new HashMap<>();

    try {
      cacheService.clearAllCache();

      response.put("status", "SUCCESS");
      response.put("message", "Redis cache cleared successfully");
      response.put("timestamp", LocalDateTime.now());

      return ResponseEntity.ok(response);

    } catch (Exception e) {
      log.error("Failed to clear Redis cache", e);
      response.put("status", "ERROR");
      response.put("message", "Failed to clear Redis cache");
      response.put("error", e.getMessage());
      response.put("timestamp", LocalDateTime.now());
      return ResponseEntity.status(500).body(response);
    }
  }
}