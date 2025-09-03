// Complete Updated AuthService.java
package com.gymai.auth_service.service;

import com.gymai.auth_service.dto.AuthRequest;
import com.gymai.auth_service.dto.AuthResponse;
import com.gymai.auth_service.dto.UserRegisterDto;
import com.gymai.auth_service.entity.User;
import com.gymai.auth_service.entity.AuthProvider;
import com.gymai.auth_service.exception.UserAlreadyExistsException;
import com.gymai.auth_service.messaging.RabbitMqSender;
import com.gymai.auth_service.messaging.UserEvent;
import com.gymai.auth_service.repository.UserRepository;
import com.gymai.auth_service.security.JwtService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.amqp.core.DirectExchange;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final RabbitMqSender rabbitMqSender;
    private final DirectExchange directExchange;

    @Value("${event.routing.registration}")
    private String registrationRouting;

    @Value("${event.routing.login}")
    private String loginRouting;

    public AuthResponse register(UserRegisterDto registerDto) {
        if (userRepository.findByEmail(registerDto.getEmail()).isPresent()) {
            log.warn("Attempt to register with existing email: {}", registerDto.getEmail());
            throw new UserAlreadyExistsException("Email already exists");
        }

        User user = User.builder()
                .name(registerDto.getName())
                .email(registerDto.getEmail())
                .userRole(registerDto.getRole().toUpperCase())
                .password(passwordEncoder.encode(registerDto.getPassword()))
                .provider(AuthProvider.LOCAL)
                .profileCompleted(false)
                .build();

        user = userRepository.save(user);
        log.info("New user registered: {}", user.getEmail());

        String accessToken = jwtService.generateAccessToken(user);
        String refreshToken = jwtService.generateRefreshToken(user);

        // Save refresh token to database
        user.setRefreshToken(refreshToken);
        userRepository.save(user);

        UserEvent event = new UserEvent(user.getEmail(), user.getName(), "REGISTRATION");
        rabbitMqSender.sendMessageToRoute(event, directExchange.getName(), registrationRouting);

        return new AuthResponse(accessToken, refreshToken);
    }

    public AuthResponse authenticate(AuthRequest request) {
        log.info("Authenticating user: {}", request.getEmail());

        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword()));
        SecurityContextHolder.getContext().setAuthentication(authentication);

        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new RuntimeException("User not found: " + request.getEmail()));

        String accessToken = jwtService.generateAccessToken(user);
        String refreshToken = jwtService.generateRefreshToken(user);

        // Save refresh token to database
        user.setRefreshToken(refreshToken);
        userRepository.save(user);

        UserEvent event = new UserEvent(user.getEmail(), user.getName(), "LOGIN");
        rabbitMqSender.sendMessageToRoute(event, directExchange.getName(), loginRouting);

        return new AuthResponse(accessToken, refreshToken);
    }

    // OAuth2 user creation/update
    public User findOrCreateOAuth2User(String email, String name, String provider) {
        Optional<User> existingUser = userRepository.findByEmail(email);

        if (existingUser.isPresent()) {
            User user = existingUser.get();
            // Update provider info if user was previously LOCAL
            if (user.getProvider() == AuthProvider.LOCAL) {
                user.setProvider(AuthProvider.valueOf(provider.toUpperCase()));
                user = userRepository.save(user);
                log.info("Updated existing user provider: {} to {}", email, provider);
            }
            return user;
        } else {
            // Create new OAuth user
            User newUser = User.builder()
                    .email(email)
                    .name(name != null ? name : "OAuth User")
                    .userRole("USER")
                    .provider(AuthProvider.valueOf(provider.toUpperCase()))
                    .profileCompleted(false)
                    .password(null) // OAuth users don't have passwords
                    .build();

            User savedUser = userRepository.save(newUser);
            log.info("New OAuth user created: {} via {}", email, provider);

            // Publish registration event for OAuth users
            UserEvent event = new UserEvent(savedUser.getEmail(), savedUser.getName(), "REGISTRATION");
            rabbitMqSender.sendMessageToRoute(event, directExchange.getName(), registrationRouting);

            return savedUser;
        }
    }

    public AuthResponse createOAuthResponse(User user) {
        String accessToken = jwtService.generateAccessToken(user);
        String refreshToken = jwtService.generateRefreshToken(user);

        // Save refresh token to database
        user.setRefreshToken(refreshToken);
        userRepository.save(user);

        // Publish login event for OAuth users
        UserEvent event = new UserEvent(user.getEmail(), user.getName(), "LOGIN");
        rabbitMqSender.sendMessageToRoute(event, directExchange.getName(), loginRouting);

        return new AuthResponse(accessToken, refreshToken);
    }

    // Method to save refresh token (for compatibility with existing code)
    public void saveRefreshToken(Long userId, String refreshToken) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        user.setRefreshToken(refreshToken);
        userRepository.save(user);
        log.info("Refresh token saved for user: {}", user.getEmail());
    }

    // Method to validate refresh token
    public boolean validateRefreshToken(String refreshToken, String email) {
        Optional<User> userOpt = userRepository.findByEmail(email);
        if (userOpt.isEmpty()) {
            return false;
        }

        User user = userOpt.get();
        return refreshToken.equals(user.getRefreshToken()) &&
                jwtService.validateRefreshToken(refreshToken);
    }

    // Method to revoke refresh token (for logout)
    public void revokeRefreshToken(String email) {
        Optional<User> userOpt = userRepository.findByEmail(email);
        if (userOpt.isPresent()) {
            User user = userOpt.get();
            user.setRefreshToken(null);
            userRepository.save(user);
            log.info("Refresh token revoked for user: {}", email);
        }
    }
}