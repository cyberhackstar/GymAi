package com.gymai.api_gateway.filter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.time.Instant;

@Component
public class RateLimitFilter extends AbstractGatewayFilterFactory<RateLimitFilter.Config> {

    private static final Logger logger = LoggerFactory.getLogger(RateLimitFilter.class);

    @Autowired
    private ReactiveRedisTemplate<String, String> redisTemplate;

    public RateLimitFilter() {
        super(Config.class);
    }

    @Override
    public GatewayFilter apply(Config config) {
        return (exchange, chain) -> {
            ServerHttpRequest request = exchange.getRequest();
            ServerHttpResponse response = exchange.getResponse();

            // Generate rate limit key based on user ID or IP
            String rateLimitKey = generateRateLimitKey(request, config);
            
            return checkRateLimit(rateLimitKey, config)
                    .flatMap(allowed -> {
                        if (!allowed) {
                            logger.warn("Rate limit exceeded for key: {}", rateLimitKey);
                            response.setStatusCode(HttpStatus.TOO_MANY_REQUESTS);
                            response.getHeaders().add("X-RateLimit-Limit", String.valueOf(config.getLimit()));
                            response.getHeaders().add("X-RateLimit-Remaining", "0");
                            response.getHeaders().add("Retry-After", String.valueOf(config.getWindowSize()));
                            return response.setComplete();
                        }
                        
                        // Add rate limit headers
                        return getRemainingRequests(rateLimitKey, config)
                                .doOnNext(remaining -> {
                                    response.getHeaders().add("X-RateLimit-Limit", String.valueOf(config.getLimit()));
                                    response.getHeaders().add("X-RateLimit-Remaining", String.valueOf(remaining));
                                    response.getHeaders().add("X-RateLimit-Reset", String.valueOf(Instant.now().plusSeconds(config.getWindowSize()).getEpochSecond()));
                                })
                                .then(chain.filter(exchange));
                    });
        };
    }

    private String generateRateLimitKey(ServerHttpRequest request, Config config) {
        String userId = request.getHeaders().getFirst("X-User-Id");
        String path = request.getPath().value();
        String method = request.getMethod().name();
        
        if (userId != null && !userId.isEmpty()) {
            return String.format("rate_limit:user:%s:%s:%s", userId, method, path);
        }
        
        // Fallback to IP address
        String clientIp = getClientIp(request);
        return String.format("rate_limit:ip:%s:%s:%s", clientIp, method, path);
    }

    private Mono<Boolean> checkRateLimit(String key, Config config) {
        long currentTime = Instant.now().getEpochSecond();
        String windowKey = key + ":" + (currentTime / config.getWindowSize());
        
        return redisTemplate.opsForValue()
                .get(windowKey)
                .cast(String.class)
                .map(Integer::parseInt)
                .defaultIfEmpty(0)
                .flatMap(currentCount -> {
                    if (currentCount >= config.getLimit()) {
                        return Mono.just(false);
                    }
                    
                    // Increment counter
                    return redisTemplate.opsForValue()
                            .increment(windowKey)
                            .flatMap(newCount -> {
                                if (newCount == 1) {
                                    // Set expiration for new window
                                    return redisTemplate.expire(windowKey, Duration.ofSeconds(config.getWindowSize()))
                                            .then(Mono.just(true));
                                }
                                return Mono.just(true);
                            });
                })
                .onErrorReturn(true); // Allow on Redis errors
    }

    private Mono<Integer> getRemainingRequests(String key, Config config) {
        long currentTime = Instant.now().getEpochSecond();
        String windowKey = key + ":" + (currentTime / config.getWindowSize());
        
        return redisTemplate.opsForValue()
                .get(windowKey)
                .cast(String.class)
                .map(Integer::parseInt)
                .defaultIfEmpty(0)
                .map(currentCount -> Math.max(0, config.getLimit() - currentCount))
                .onErrorReturn(config.getLimit());
    }

    private String getClientIp(ServerHttpRequest request) {
        String xForwardedFor = request.getHeaders().getFirst("X-Forwarded-For");
        if (xForwardedFor != null && !xForwardedFor.isEmpty()) {
            return xForwardedFor.split(",")[0].trim();
        }
        
        String xRealIp = request.getHeaders().getFirst("X-Real-IP");
        if (xRealIp != null && !xRealIp.isEmpty()) {
            return xRealIp;
        }
        
        return request.getRemoteAddress() != null ? 
               request.getRemoteAddress().getAddress().getHostAddress() : "unknown";
    }

    public static class Config {
        private int limit = 100; // requests per window
        private int windowSize = 60; // seconds
        private boolean skipSuccessfulRequests = false;

        public int getLimit() {
            return limit;
        }

        public void setLimit(int limit) {
            this.limit = limit;
        }

        public int getWindowSize() {
            return windowSize;
        }

        public void setWindowSize(int windowSize) {
            this.windowSize = windowSize;
        }

        public boolean isSkipSuccessfulRequests() {
            return skipSuccessfulRequests;
        }

        public void setSkipSuccessfulRequests(boolean skipSuccessfulRequests) {
            this.skipSuccessfulRequests = skipSuccessfulRequests;
        }
    }
}
