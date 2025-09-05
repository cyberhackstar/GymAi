package com.gymai.auth_service.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/cors")
@Slf4j
public class CorsTestController {

  /**
   * Simple CORS test endpoint
   */
  @GetMapping("/test")
  @CrossOrigin(origins = "*", maxAge = 3600)
  public ResponseEntity<?> corsTest(HttpServletRequest request) {
    log.info("=== CORS Test Endpoint Called ===");

    Map<String, String> headers = new HashMap<>();
    request.getHeaderNames().asIterator().forEachRemaining(headerName -> {
      headers.put(headerName, request.getHeader(headerName));
    });

    Map<String, Object> response = Map.of(
        "message", "CORS test successful",
        "method", request.getMethod(),
        "origin", request.getHeader("Origin"),
        "host", request.getHeader("Host"),
        "userAgent", request.getHeader("User-Agent"),
        "referer", request.getHeader("Referer"),
        "allHeaders", headers,
        "timestamp", System.currentTimeMillis());

    log.info("CORS Test Response: {}", response);
    return ResponseEntity.ok(response);
  }

  /**
   * OPTIONS preflight test
   */
  @RequestMapping(value = "/test", method = RequestMethod.OPTIONS)
  @CrossOrigin(origins = "*", maxAge = 3600)
  public ResponseEntity<?> corsPreflightTest(HttpServletRequest request) {
    log.info("=== CORS Preflight (OPTIONS) Test ===");
    log.info("Origin: {}", request.getHeader("Origin"));
    log.info("Access-Control-Request-Method: {}", request.getHeader("Access-Control-Request-Method"));
    log.info("Access-Control-Request-Headers: {}", request.getHeader("Access-Control-Request-Headers"));

    return ResponseEntity.ok()
        .header("Access-Control-Allow-Origin", request.getHeader("Origin"))
        .header("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS")
        .header("Access-Control-Allow-Headers", "Authorization, Content-Type, X-Requested-With")
        .header("Access-Control-Allow-Credentials", "true")
        .header("Access-Control-Max-Age", "3600")
        .build();
  }

  /**
   * Test authenticated endpoint
   */
  @GetMapping("/auth-test")
  public ResponseEntity<?> authTest(HttpServletRequest request) {
    log.info("=== Authenticated CORS Test ===");

    Map<String, Object> response = Map.of(
        "message", "Authenticated CORS test successful",
        "method", request.getMethod(),
        "origin", request.getHeader("Origin"),
        "authorization", request.getHeader("Authorization") != null ? "Present" : "Missing",
        "timestamp", System.currentTimeMillis());

    return ResponseEntity.ok(response);
  }
}