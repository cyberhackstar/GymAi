package com.gymai.api_gateway.config;

import org.springframework.cloud.gateway.filter.ratelimit.KeyResolver;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.util.StringUtils;
import reactor.core.publisher.Mono;

import java.util.Objects;

@Configuration
public class GatewayConfig {

    /**
     * Key resolver for rate limiting based on user ID from JWT token
     * Falls back to IP address if no user ID is available
     */
    @Bean
    public KeyResolver userKeyResolver() {
        return exchange -> {
            ServerHttpRequest request = exchange.getRequest();
            
            // Try to get user ID from JWT token header (set by AuthenticationFilter)
            String userId = request.getHeaders().getFirst("X-User-Id");
            if (StringUtils.hasText(userId)) {
                return Mono.just(userId);
            }
            
            // Fallback to IP address for unauthenticated requests
            String clientIp = getClientIp(request);
            return Mono.just(clientIp);
        };
    }

    /**
     * Key resolver for rate limiting based on IP address only
     */
    @Bean
    public KeyResolver ipKeyResolver() {
        return exchange -> {
            String clientIp = getClientIp(exchange.getRequest());
            return Mono.just(clientIp);
        };
    }

    /**
     * Key resolver for rate limiting based on API endpoint
     */
    @Bean
    public KeyResolver pathKeyResolver() {
        return exchange -> {
            String path = exchange.getRequest().getPath().value();
            return Mono.just(path);
        };
    }

    /**
     * Extract client IP address from request, considering proxy headers
     */
    private String getClientIp(ServerHttpRequest request) {
        String xForwardedFor = request.getHeaders().getFirst("X-Forwarded-For");
        if (StringUtils.hasText(xForwardedFor)) {
            return xForwardedFor.split(",")[0].trim();
        }
        
        String xRealIp = request.getHeaders().getFirst("X-Real-IP");
        if (StringUtils.hasText(xRealIp)) {
            return xRealIp;
        }
        
        String remoteAddr = request.getHeaders().getFirst("X-Forwarded-Host");
        if (StringUtils.hasText(remoteAddr)) {
            return remoteAddr;
        }
        
        // Fallback to remote address from connection
        return Objects.requireNonNull(request.getRemoteAddress()).getAddress().getHostAddress();
    }

    /**
     * Custom load balancer configuration (if needed for multiple instances)
     */
    // @Bean
    // @LoadBalanced
    // public WebClient.Builder webClientBuilder() {
    //     return WebClient.builder();
    // }
}