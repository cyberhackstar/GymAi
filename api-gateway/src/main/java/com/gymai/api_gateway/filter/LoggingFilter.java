package com.gymai.api_gateway.filter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Component
public class LoggingFilter implements GlobalFilter, Ordered {

    private static final Logger logger = LoggerFactory.getLogger(LoggingFilter.class);
    private static final String START_TIME = "startTime";

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();
        ServerHttpResponse response = exchange.getResponse();

        // Record start time
        exchange.getAttributes().put(START_TIME, System.currentTimeMillis());

        // Log incoming request
        logRequest(request);

        return chain.filter(exchange).then(
            Mono.fromRunnable(() -> {
                // Log response after processing
                logResponse(request, response, exchange);
            })
        );
    }

    private void logRequest(ServerHttpRequest request) {
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME);
        String clientIp = getClientIp(request);
        String userAgent = request.getHeaders().getFirst("User-Agent");
        String authorization = request.getHeaders().getFirst("Authorization");
        
        logger.info("INCOMING REQUEST - [{}] {} {} from {} - User-Agent: {} - Auth: {}", 
                   timestamp,
                   request.getMethod(), 
                   request.getURI(), 
                   clientIp,
                   userAgent != null ? userAgent.substring(0, Math.min(userAgent.length(), 50)) : "N/A",
                   authorization != null ? "Bearer ***" : "None");

        // Log request headers (excluding sensitive ones)
        if (logger.isDebugEnabled()) {
            request.getHeaders().forEach((name, values) -> {
                if (!isSensitiveHeader(name)) {
                    logger.debug("Request Header: {} = {}", name, values);
                }
            });
        }
    }

    private void logResponse(ServerHttpRequest request, ServerHttpResponse response, ServerWebExchange exchange) {
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME);
        Long startTime = exchange.getAttribute(START_TIME);
        long duration = startTime != null ? System.currentTimeMillis() - startTime : 0;
        
        logger.info("OUTGOING RESPONSE - [{}] {} {} - Status: {} - Duration: {}ms", 
                   timestamp,
                   request.getMethod(), 
                   request.getURI(),
                   response.getStatusCode(),
                   duration);

        // Log response headers in debug mode
        if (logger.isDebugEnabled()) {
            response.getHeaders().forEach((name, values) -> 
                logger.debug("Response Header: {} = {}", name, values));
        }

        // Log slow requests as warnings
        if (duration > 5000) { // 5 seconds
            logger.warn("SLOW REQUEST DETECTED - {} {} took {}ms", 
                       request.getMethod(), request.getURI(), duration);
        }
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
               request.getRemoteAddress().getAddress().getHostAddress() : "Unknown";
    }

    private boolean isSensitiveHeader(String headerName) {
        return headerName.toLowerCase().contains("authorization") ||
               headerName.toLowerCase().contains("cookie") ||
               headerName.toLowerCase().contains("password") ||
               headerName.toLowerCase().contains("token");
    }

    @Override
    public int getOrder() {
        return Ordered.LOWEST_PRECEDENCE;
    }
}
