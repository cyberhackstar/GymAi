package com.gymai.auth_service.controller;

import com.gymai.auth_service.dto.AuthRequest;
import com.gymai.auth_service.dto.AuthResponse;
import com.gymai.auth_service.dto.RefreshTokenRequest;
import com.gymai.auth_service.dto.UserRegisterDto;
import com.gymai.auth_service.entity.User;
import com.gymai.auth_service.security.JwtService;
import com.gymai.auth_service.service.AuthService;
import com.gymai.auth_service.repository.UserRepository;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Slf4j
public class AuthController {

    private final AuthService authService;
    private final JwtService jwtService;
    private final UserRepository userRepository;

    /**
     * Endpoint for user registration.
     */
    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(@Valid @RequestBody UserRegisterDto request) {
        log.info("Register request received for email: {}", request.getEmail());
        request.setRole("USER"); // Default role for new users
        AuthResponse response = authService.register(request);
        log.info("User registered successfully: {}", request.getEmail());
        return ResponseEntity.ok(response);
    }

    /**
     * Endpoint for user login.
     */
    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody AuthRequest request) {
        log.info("Login attempt for email: {}", request.getEmail());
        AuthResponse response = authService.authenticate(request);
        log.info("Login successful for email: {}", request.getEmail());
        return ResponseEntity.ok(response);
    }

    /**
     * Endpoint for refreshing an access token using a valid refresh token.
     */
    @PostMapping("/refresh")
    public ResponseEntity<?> refreshToken(@Valid @RequestBody RefreshTokenRequest refreshRequest) {
        String refreshToken = refreshRequest.getRefreshToken();
        log.info("Refresh token request received");

        try {
            if (!jwtService.validateRefreshToken(refreshToken)) {
                log.warn("Invalid or expired refresh token");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid or expired refresh token");
            }

            String username = jwtService.extractUsername(refreshToken);
            Optional<User> userOpt = userRepository.findByEmail(username);

            if (userOpt.isEmpty()) {
                log.warn("User not found for refresh token");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("User not found");
            }

            User user = userOpt.get();
            String newAccessToken = jwtService.generateAccessToken(user);

            return ResponseEntity.ok(AuthResponse.builder()
                    .accessToken(newAccessToken)
                    .refreshToken(refreshToken) // Keep the same refresh token until it expires
                    .build());

        } catch (Exception e) {
            log.error("Error during token refresh: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Could not refresh token");
        }
    }
}
