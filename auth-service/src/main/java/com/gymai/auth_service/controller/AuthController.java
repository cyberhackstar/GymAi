package com.gymai.auth_service.controller;

import com.gymai.auth_service.dto.AuthRequest;
import com.gymai.auth_service.dto.AuthResponse;
import com.gymai.auth_service.dto.UserRegisterDto;
import com.gymai.auth_service.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * Controller for authentication endpoints such as register and login.
 */
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Slf4j
public class AuthController {

    private final AuthService authService;

    /**
     * Endpoint for user registration.
     *
     * @param request User registration details
     * @return JWT token in AuthResponse
     */
    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(@Valid @RequestBody UserRegisterDto request) {
        log.info("Register request received for email: {}", request.getEmail());
        AuthResponse response = authService.register(request);
        log.info("User registered successfully: {}", request.getEmail());
        return ResponseEntity.ok(response);
    }

    /**
     * Endpoint for user login.
     *
     * @param request User login credentials
     * @return JWT token in AuthResponse
     */
    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody AuthRequest request) {
        log.info("Login attempt for email: {}", request.getEmail());
        AuthResponse response = authService.authenticate(request);
        log.info("Login successful for email: {}", request.getEmail());
        return ResponseEntity.ok(response);
    }
}
