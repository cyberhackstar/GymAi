package com.gymai.auth_service.security;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import com.gymai.auth_service.entity.User;

import java.security.Key;
import java.util.Date;
import java.util.stream.Collectors;

@Component
public class JwtService {

    private static final Logger logger = LoggerFactory.getLogger(JwtService.class);

    @Value("${jwt.secret}")
    private String jwtSecret;

    @Value("${jwt.expiration}")
    private long jwtExpirationMs;

    private Key key;

    @PostConstruct
    public void init() {
        key = Keys.hmacShaKeyFor(jwtSecret.getBytes());
        String base64Key = java.util.Base64.getEncoder().encodeToString(key.getEncoded());
        logger.info("JWT secret key initialized");
        logger.info("Base64 Encoded JWT Key: {}", base64Key); // ✅ Print the key
    }

    // For AuthService.register()
   public String generateToken(User user) {
    return Jwts.builder()
            .setSubject(user.getEmail())
            .claim("userId", user.getId())
            .claim("name", user.getName())
            .claim("role", user.getUserRole())
            .setIssuedAt(new Date())
            .setExpiration(new Date(System.currentTimeMillis() + jwtExpirationMs))
            .signWith(key, SignatureAlgorithm.HS256)
            .compact();
}


    // For AuthService.authenticate()
    // public String generateToken(UserDetails userDetails) {
    //     return Jwts.builder()
    //             .setSubject(userDetails.getUsername())
    //             .claim("roles", userDetails.getAuthorities().stream()
    //                     .map(GrantedAuthority::getAuthority).collect(Collectors.toList()))
    //             .setIssuedAt(new Date())
    //             .setExpiration(new Date(System.currentTimeMillis() + jwtExpirationMs))
    //             .signWith(key, SignatureAlgorithm.HS256)
    //             .compact();
    // }

    public String extractUsername(String token) {
        try {
            String username = Jwts.parserBuilder().setSigningKey(key).build()
                    .parseClaimsJws(token)
                    .getBody()
                    .getSubject();
            logger.debug("Extracted username from token: {}", username);
            return username;
        } catch (JwtException e) {
            logger.error("Failed to extract username from token: {}", e.getMessage());
            throw e;
        }
    }

    public boolean validateToken(String token, UserDetails userDetails) {
        try {
            final String username = extractUsername(token);
            boolean isValid = username.equals(userDetails.getUsername()) && !isTokenExpired(token);
            logger.debug("Token validation result for user {}: {}", username, isValid);
            return isValid;
        } catch (JwtException | IllegalArgumentException e) {
            logger.warn("Token validation failed: {}", e.getMessage());
            return false;
        }
    }

    private boolean isTokenExpired(String token) {
        try {
            final Date expiration = Jwts.parserBuilder().setSigningKey(key).build()
                    .parseClaimsJws(token)
                    .getBody()
                    .getExpiration();
            boolean expired = expiration.before(new Date());
            if (expired) {
                logger.warn("Token is expired.");
            }
            return expired;
        } catch (JwtException e) {
            logger.error("Failed to parse expiration from token: {}", e.getMessage());
            throw e;
        }
    }
}
