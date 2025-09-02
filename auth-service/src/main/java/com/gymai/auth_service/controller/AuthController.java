// Complete Updated AuthController.java
package com.gymai.auth_service.controller;

import com.gymai.auth_service.dto.AuthRequest;
import com.gymai.auth_service.dto.AuthResponse;
import com.gymai.auth_service.dto.RefreshTokenRequest;
import com.gymai.auth_service.dto.UserRegisterDto;
import com.gymai.auth_service.entity.User;
import com.gymai.auth_service.security.JwtService;
import com.gymai.auth_service.service.AuthService;
import com.gymai.auth_service.repository.UserRepository;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
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
     * Enhanced endpoint for refreshing an access token using a valid refresh token.
     */
    @PostMapping("/refresh")
    public ResponseEntity<?> refreshToken(@Valid @RequestBody RefreshTokenRequest refreshRequest) {
        String refreshToken = refreshRequest.getRefreshToken();
        log.info("Refresh token request received");

        try {
            // Validate refresh token format and expiration
            if (!jwtService.validateRefreshToken(refreshToken)) {
                log.warn("Invalid or expired refresh token format");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(Map.of("error", "Invalid or expired refresh token"));
            }

            // Extract username from refresh token
            String username = jwtService.extractUsername(refreshToken);
            Optional<User> userOpt = userRepository.findByEmail(username);

            if (userOpt.isEmpty()) {
                log.warn("User not found for refresh token");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(Map.of("error", "User not found"));
            }

            User user = userOpt.get();

            // Validate that the refresh token matches the one stored in database
            if (!authService.validateRefreshToken(refreshToken, username)) {
                log.warn("Refresh token does not match stored token for user: {}", username);
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(Map.of("error", "Invalid refresh token"));
            }

            // Generate new access token
            String newAccessToken = jwtService.generateAccessToken(user);

            // Optionally generate new refresh token if the current one is about to expire
            String newRefreshToken = refreshToken;
            if (jwtService.isTokenAboutToExpire(refreshToken)) {
                newRefreshToken = jwtService.generateRefreshToken(user);
                authService.saveRefreshToken(user.getId(), newRefreshToken);
                log.info("Generated new refresh token for user: {}", username);
            }

            log.info("Token refreshed successfully for user: {}", username);
            return ResponseEntity.ok(AuthResponse.builder()
                    .accessToken(newAccessToken)
                    .refreshToken(newRefreshToken)
                    .build());

        } catch (Exception e) {
            log.error("Error during token refresh: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("error", "Could not refresh token"));
        }
    }

    /**
     * Endpoint for user logout - revokes refresh token
     */
    @PostMapping("/logout")
    public ResponseEntity<?> logout(Authentication authentication) {
        try {
            String email = authentication.getName();
            authService.revokeRefreshToken(email);
            log.info("User logged out successfully: {}", email);
            return ResponseEntity.ok(Map.of("message", "Logged out successfully"));
        } catch (Exception e) {
            log.error("Error during logout: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Could not logout"));
        }
    }

    /**
     * Endpoint to check if user profile is completed
     */
    @GetMapping("/profile-status")
    public ResponseEntity<?> getProfileStatus(Authentication authentication) {
        try {
            String email;
            if (authentication.getPrincipal() instanceof OAuth2User) {
                OAuth2User oauth2User = (OAuth2User) authentication.getPrincipal();
                email = oauth2User.getAttribute("email");
            } else {
                email = authentication.getName();
            }

            User user = userRepository.findByEmail(email)
                    .orElseThrow(() -> new RuntimeException("User not found"));

            return ResponseEntity.ok(Map.of(
                    "profileCompleted", user.isProfileCompleted(),
                    "email", user.getEmail(),
                    "name", user.getName(),
                    "provider", user.getProvider()));

        } catch (Exception e) {
            log.error("Error getting profile status: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("error", "Could not get profile status"));
        }
    }

    /**
     * Endpoint to get current user info
     */
    @GetMapping("/me")
    public ResponseEntity<?> getCurrentUser(Authentication authentication) {
        try {
            String email = authentication.getName();
            User user = userRepository.findByEmail(email)
                    .orElseThrow(() -> new RuntimeException("User not found"));

            return ResponseEntity.ok(Map.of(
                    "id", user.getId(),
                    "email", user.getEmail(),
                    "name", user.getName(),
                    "role", user.getUserRole(),
                    "provider", user.getProvider(),
                    "profileCompleted", user.isProfileCompleted()));

        } catch (Exception e) {
            log.error("Error getting current user: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("error", "Could not get user info"));
        }
    }

    /**
     * Endpoint to validate token
     */
    @GetMapping("/validate")
    public ResponseEntity<?> validateToken(Authentication authentication) {
        try {
            String email = authentication.getName();
            return ResponseEntity.ok(Map.of(
                    "valid", true,
                    "email", email,
                    "authenticated", authentication.isAuthenticated()));
        } catch (Exception e) {
            log.error("Error validating token: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("valid", false, "error", "Invalid token"));
        }
    }

    @PostMapping("/set-frontend-origin")
    public ResponseEntity<String> setFrontendOrigin(
            @RequestBody Map<String, String> request,
            HttpServletRequest servletRequest) {

        String frontendOrigin = request.get("frontendOrigin");
        if (frontendOrigin != null) {
            servletRequest.getSession().setAttribute("frontend_origin", frontendOrigin);
            log.info("Set frontend origin in session: {}", frontendOrigin);
            return ResponseEntity.ok("Frontend origin set");
        }
        return ResponseEntity.badRequest().body("Invalid request");
    }
}