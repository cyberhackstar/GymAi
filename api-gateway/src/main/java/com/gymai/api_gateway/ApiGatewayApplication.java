package com.gymai.api_gateway;


import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.reactive.CorsWebFilter;
import org.springframework.web.cors.reactive.UrlBasedCorsConfigurationSource;

import java.time.Duration;
import java.util.Arrays;
import java.util.Collections;

@SpringBootApplication
public class ApiGatewayApplication {

    public static void main(String[] args) {
        SpringApplication.run(ApiGatewayApplication.class, args);
    }

    @Bean
    public RouteLocator customRouteLocator(RouteLocatorBuilder builder) {
        return builder.routes()
                // Auth Service Routes - Public endpoints (no authentication)
                .route("auth-public", r -> r
                        .path("/api/auth/login", "/api/auth/register", "/api/auth/refresh", "/api/auth/set-frontend-origin")
                        .and().method("POST", "OPTIONS")
                        .filters(f -> f
                                .stripPrefix(2)
                                .circuitBreaker(config -> config
                                        .setName("auth-service-cb")
                                        .setFallbackUri("/fallback/auth"))
                                .retry(config -> config
                                        .setRetries(2)
                                        .setBackoff(Duration.ofMillis(100), Duration.ofSeconds(1), 2, false)))
                        .uri("http://auth-service:8080"))
                
                // OAuth2 Routes - No authentication needed
                .route("auth-oauth", r -> r
                        .path("/api/auth/oauth2/**")
                        .filters(f -> f
                                .stripPrefix(2)
                                .circuitBreaker(config -> config
                                        .setName("auth-oauth-cb")
                                        .setFallbackUri("/fallback/auth")))
                        .uri("http://auth-service:8080"))
                
                // Auth Service Routes - Protected endpoints
                .route("auth-protected", r -> r
                        .path("/api/auth/**")
                        .filters(f -> f
                                .stripPrefix(2)
                                .circuitBreaker(config -> config
                                        .setName("auth-service-cb")
                                        .setFallbackUri("/fallback/auth"))
                                .retry(config -> config
                                        .setRetries(2)
                                        .setBackoff(Duration.ofMillis(100), Duration.ofSeconds(1), 2, false)))
                        .uri("http://auth-service:8080"))
                
                // User Service Routes
                .route("user-service", r -> r
                        .path("/api/users/**")
                        .filters(f -> f
                                .stripPrefix(2) // Remove /api/users prefix
                                .circuitBreaker(config -> config
                                        .setName("user-service-cb")
                                        .setFallbackUri("/fallback/users"))
                                .retry(config -> config
                                        .setRetries(3)
                                        .setBackoff(Duration.ofMillis(100), Duration.ofSeconds(2), 2, false)))
                        .uri("http://user-service:8080"))
                
                // Plan Service Routes
                .route("plan-service", r -> r
                        .path("/api/plans/**")
                        .filters(f -> f
                                .stripPrefix(2) // Remove /api/plans prefix
                                .circuitBreaker(config -> config
                                        .setName("plan-service-cb")
                                        .setFallbackUri("/fallback/plans"))
                                .retry(config -> config
                                        .setRetries(3)
                                        .setBackoff(Duration.ofMillis(100), Duration.ofSeconds(2), 2, false)))
                        .uri("http://plan-service:8080"))
                
                // Notification Service Routes
                .route("notification-service", r -> r
                        .path("/api/notifications/**")
                        .filters(f -> f
                                .stripPrefix(2) // Remove /api/notifications prefix
                                .circuitBreaker(config -> config
                                        .setName("notification-service-cb")
                                        .setFallbackUri("/fallback/notifications"))
                                .retry(config -> config
                                        .setRetries(3)
                                        .setBackoff(Duration.ofMillis(100), Duration.ofSeconds(2), 2, false)))
                        .uri("http://notification-service:8080"))
                
                // Health check routes for all services
                .route("auth-health", r -> r
                        .path("/api/health/auth")
                        .filters(f -> f.stripPrefix(3))
                        .uri("http://auth-service:8080/actuator/health"))
                
                .route("user-health", r -> r
                        .path("/api/health/users")
                        .filters(f -> f.stripPrefix(3))
                        .uri("http://user-service:8080/actuator/health"))
                
                .route("plan-health", r -> r
                        .path("/api/health/plans")
                        .filters(f -> f.stripPrefix(3))
                        .uri("http://plan-service:8080/actuator/health"))
                
                .route("notification-health", r -> r
                        .path("/api/health/notifications")
                        .filters(f -> f.stripPrefix(3))
                        .uri("http://notification-service:8080/actuator/health"))
                
                .build();
    }

    @Bean
    public CorsWebFilter corsWebFilter() {
        CorsConfiguration corsConfig = new CorsConfiguration();
        corsConfig.setAllowedOriginPatterns(Collections.singletonList("*"));
        corsConfig.setMaxAge(3600L);
        corsConfig.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS", "PATCH"));
        corsConfig.setAllowedHeaders(Arrays.asList("*"));
        corsConfig.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", corsConfig);

        return new CorsWebFilter(source);
    }
}