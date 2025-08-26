package com.gymai.user_service.controller;

import com.gymai.user_service.dto.UserDto;
import com.gymai.user_service.service.UserProfileService;
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

    private final UserProfileService userProfileService;
    private final JwtService jwtService;

    @PostMapping("/profile")
    public ResponseEntity<UserDto> saveProfile(@RequestBody UserDto dto, HttpServletRequest request) {
        String token = extractTokenFromHeader(request);
        log.debug("Received token in POST /profile");

        Long userId = jwtService.extractUserId(token);
        String name = jwtService.extractName(token);
        String email = jwtService.extractEmail(token);

        log.debug("Extracted from token - userId: {}, name: {}, email: {}", userId, name, email);

        // Ensure DTO reflects authoritative token data
        dto.setUserId(userId);
        dto.setName(name);
        dto.setEmail(email);

        UserDto saved = userProfileService.saveOrUpdate(userId, dto);
        log.info("Saved user profile for userId {}", userId);

        return ResponseEntity.ok(saved);
    }

    @GetMapping("/profile")
    public ResponseEntity<UserDto> getProfile(HttpServletRequest request) {
        String token = extractTokenFromHeader(request);
        log.debug("Received token in GET /profile");

        Long userId = jwtService.extractUserId(token);
        log.debug("Extracted userId from token: {}", userId);

        UserDto dto = userProfileService.getById(userId);
        return ResponseEntity.ok(dto);
    }

    @GetMapping("/profile/check")
    public ResponseEntity<Boolean> isProfileCompleted(HttpServletRequest request) {
        String token = extractTokenFromHeader(request);
        Long userId = jwtService.extractUserId(token);

        boolean exists = userProfileService.existsById(userId);
        log.debug("Profile completion check for userId {} → {}", userId, exists);

        return ResponseEntity.ok(exists);
    }

    private String extractTokenFromHeader(HttpServletRequest request) {
        String header = request.getHeader("Authorization");
        if (header == null || !header.startsWith("Bearer ")) {
            log.error("Missing or invalid Authorization header");
            throw new RuntimeException("Missing or invalid Authorization header");
        }
        return header.substring(7);
    }
}