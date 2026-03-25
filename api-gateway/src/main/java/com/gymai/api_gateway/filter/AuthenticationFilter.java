package com.gymai.api_gateway.filter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import com.gymai.api_gateway.util.JwtUtil;

import reactor.core.publisher.Mono;

@Component
public class AuthenticationFilter extends AbstractGatewayFilterFactory<AuthenticationFilter.Config> {

    private static final Logger logger = LoggerFactory.getLogger(AuthenticationFilter.class);

    @Autowired
    private JwtUtil jwtUtil;

    public AuthenticationFilter() {
        super(Config.class);
    }

    @Override
    public GatewayFilter apply(Config config) {
        return (exchange, chain) -> {
            ServerHttpRequest request = exchange.getRequest();
            ServerHttpResponse response = exchange.getResponse();

            // Skip authentication for public endpoints
            String path = request.getPath().value();
            if (isPublicEndpoint(path)) {
                logger.debug("Skipping authentication for public endpoint: {}", path);
                return chain.filter(exchange);
            }

            // Extract Authorization header
            String authHeader = request.getHeaders().getFirst(HttpHeaders.AUTHORIZATION);
            
            if (!StringUtils.hasText(authHeader) || !authHeader.startsWith("Bearer ")) {
                logger.warn("Missing or invalid Authorization header for path: {}", path);
                return handleUnauthorized(response, "Missing or invalid Authorization header");
            }

            String token = authHeader.substring(7);

            try {
                // Validate JWT token
                if (!jwtUtil.isTokenValid(token)) {
                    logger.warn("Invalid JWT token for path: {}", path);
                    return handleUnauthorized(response, "Invalid or expired token");
                }

                // Extract user information from token - handle potential null values
                String username = jwtUtil.extractUsername(token);
                String userId = jwtUtil.extractUserId(token);
                String role = jwtUtil.extractRole(token);
                String email = jwtUtil.extractEmail(token);
                // String provider = jwtUtil.extractProvider(token);
                // Boolean profileCompleted = jwtUtil.extractProfileCompleted(token);
                // Fix: extractTokenType method does not exist in JwtUtil, use extractClaim instead
                String tokenType = jwtUtil.extractClaim(token, claims -> claims.get("type", String.class));

                // Validate that it's an ACCESS token
                if (!"ACCESS".equalsIgnoreCase(tokenType)) {
                    logger.warn("Invalid token type '{}' used for authentication at path: {}", tokenType, path);
                    return handleUnauthorized(response, "Invalid token type");
                }

                // Set up logging context
                MDC.put("userId", userId != null ? userId : "unknown");
                MDC.put("username", username != null ? username : "unknown");

                // Add user information to request headers for downstream services
                ServerHttpRequest modifiedRequest = request.mutate()
                        .header("X-User-Id", userId != null ? userId : "")
                        .header("X-Username", username != null ? username : "")
                        .header("X-User-Email", email != null ? email : "")
                        // Fix: 'name', 'provider', 'profileCompleted' variables are undefined, remove or define them
                        //.header("X-User-Name", name != null ? name : "")
                        .header("X-User-Role", role != null ? role : "USER")
                        //.header("X-User-Provider", provider != null ? provider : "LOCAL")
                        //.header("X-Profile-Completed", profileCompleted != null ? profileCompleted.toString() : "true")
                        .header("X-Token-Type", tokenType != null ? tokenType : "ACCESS")
                        .build();

                logger.debug("Authentication successful for user: {} (ID: {}) with role: {} accessing path: {}", 
                           username, userId, role, path);

                return chain.filter(exchange.mutate().request(modifiedRequest).build())
                        .doFinally(signalType -> {
                            // Clean up MDC
                            MDC.clear();
                        });

            } catch (Exception e) {
                logger.error("JWT validation failed for path: {}, error: {}", path, e.getMessage());
                MDC.clear();
                return handleUnauthorized(response, "Token validation failed");
            }
        };
    }

    private Mono<Void> handleUnauthorized(ServerHttpResponse response, String message) {
        response.setStatusCode(HttpStatus.UNAUTHORIZED);
        response.getHeaders().add("Content-Type", "application/json");
        response.getHeaders().add("WWW-Authenticate", "Bearer");
        
        String errorBody = String.format(
                "{\"error\":\"Unauthorized\",\"message\":\"%s\",\"timestamp\":\"%s\"}",
                message, java.time.Instant.now().toString());
        
        org.springframework.core.io.buffer.DataBuffer buffer = response.bufferFactory().wrap(errorBody.getBytes());
        return response.writeWith(Mono.just(buffer));
    }

    private boolean isPublicEndpoint(String path) {
        return path.contains("/login") || 
               path.contains("/register") || 
               path.contains("/refresh") ||
               path.contains("/health") ||
               path.contains("/actuator") ||
               path.contains("/swagger") ||
               path.contains("/api-docs") ||
               path.contains("/favicon.ico") ||
               path.startsWith("/oauth2/") ||
               path.startsWith("/login/oauth2/") ||
               path.contains("/set-frontend-origin") ||
               path.contains("/error");
    }

    public static class Config {
        private boolean validateTokenType = true;
        private boolean requireProfileCompleted = false;
        private String requiredRole;

        public boolean isValidateTokenType() {
            return validateTokenType;
        }

        public void setValidateTokenType(boolean validateTokenType) {
            this.validateTokenType = validateTokenType;
        }

        public boolean isRequireProfileCompleted() {
            return requireProfileCompleted;
        }

        public void setRequireProfileCompleted(boolean requireProfileCompleted) {
            this.requireProfileCompleted = requireProfileCompleted;
        }

        public String getRequiredRole() {
            return requiredRole;
        }

        public void setRequiredRole(String requiredRole) {
            this.requiredRole = requiredRole;
        }
    }
}