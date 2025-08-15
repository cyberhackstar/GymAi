package com.gymai.user_service.controller;

import com.gymai.user_service.entity.UserProfile;
import com.gymai.user_service.repository.UserProfileRepository;
import com.gymai.security.service.JwtService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
@Slf4j
public class UserProfileController {

    private final UserProfileRepository profileRepository;
    private final JwtService jwtService;

    @PostMapping("/profile")
    public ResponseEntity<UserProfile> saveProfile(@RequestBody UserProfile profile, HttpServletRequest request) {
        String token = extractTokenFromHeader(request);
        log.info("🔐 Received token in POST /profile");

        Long userId = jwtService.extractUserId(token);
        String name = jwtService.extractName(token);
        String email = jwtService.extractEmail(token);

        log.info("🧠 Extracted from token - userId: {}, name: {}, email: {}", userId, name, email);

        // Override name & email from token
        profile.setUserId(userId);
        profile.setName(name);
        profile.setEmail(email);

        UserProfile saved = profileRepository.save(profile);
        log.info("✅ Saved user profile for userId {}: {}", userId, saved);

        return ResponseEntity.ok(saved);
    }

    @GetMapping("/profile")
    public ResponseEntity<UserProfile> getProfile(HttpServletRequest request) {
        String token = extractTokenFromHeader(request);
        log.info("🔐 Received token in GET /profile");

        Long userId = jwtService.extractUserId(token);
        log.info("🧠 Extracted userId from token: {}", userId);

        return profileRepository.findById(userId)
                .map(profile -> {
                    log.info("✅ Found user profile for userId {}: {}", userId, profile);
                    return ResponseEntity.ok(profile);
                })
                .orElseGet(() -> {
                    log.warn("❌ No profile found for userId: {}", userId);
                    return ResponseEntity.notFound().build();
                });
    }

    @GetMapping("/profile/check")
    public ResponseEntity<Boolean> isProfileCompleted(HttpServletRequest request) {
        String token = extractTokenFromHeader(request);
        Long userId = jwtService.extractUserId(token);

        boolean exists = profileRepository.existsById(userId);
        log.info("📌 Profile completion check for userId {} → {}", userId, exists);

        return ResponseEntity.ok(exists);
    }

    private String extractTokenFromHeader(HttpServletRequest request) {
        String header = request.getHeader("Authorization");
        if (header == null || !header.startsWith("Bearer ")) {
            log.error("❌ Missing or invalid Authorization header");
            throw new RuntimeException("Missing or invalid Authorization header");
        }
        return header.substring(7); // Strip "Bearer "
    }
}
