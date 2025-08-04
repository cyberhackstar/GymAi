package com.gymai.security.service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Date;
import java.util.function.Function;

@Slf4j
@Service
public class JwtService {

    private final SecretKey key;

    public JwtService(@Value("${jwt.secret}") String secret) {
        this.key = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
        String encodedKey = Base64.getEncoder().encodeToString(key.getEncoded());
        log.info("JWT Secret Key initialized: {}", encodedKey);
    }

    public String extractUsername(String token) {
        String username = extractClaim(token, Claims::getSubject);
        log.debug("Extracted username from token: {}", username);
        return username;
    }

    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        Claims claims = extractAllClaims(token);
        T claim = claimsResolver.apply(claims);
        log.debug("Extracted claim: {}", claim);
        return claim;
    }

    private Claims extractAllClaims(String token) {
        try {
            Claims claims = Jwts.parser()
                    .setSigningKey(key)
                    .parseClaimsJws(token)
                    .getBody();
            log.debug("Extracted all claims: {}", claims);
            return claims;
        } catch (Exception e) {
            log.error("Failed to parse JWT token: {}", e.getMessage());
            throw e;
        }
    }

    public String extractEmail(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    public Long extractUserId(String token) {
        Long userId = extractAllClaims(token).get("userId", Long.class);
        log.debug("Extracted userId: {}", userId);
        return userId;
    }

    public String extractName(String token) {
        String name = extractAllClaims(token).get("name", String.class);
        log.debug("Extracted name: {}", name);
        return name;
    }

    public boolean isTokenValid(String token, UserDetails userDetails) {
        final String username = extractUsername(token);
        boolean valid = (username.equals(userDetails.getUsername()) && !isTokenExpired(token));
        log.info("Token valid status for {}: {}", username, valid);
        return valid;
    }

    private boolean isTokenExpired(String token) {
        boolean expired = extractExpiration(token).before(new Date());
        log.debug("Token expired: {}", expired);
        return expired;
    }

    private Date extractExpiration(String token) {
        Date expiration = extractClaim(token, Claims::getExpiration);
        log.debug("Token expiration: {}", expiration);
        return expiration;
    }
}
