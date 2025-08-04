package com.gymai.auth_service.service;

import com.gymai.auth_service.dto.AuthRequest;
import com.gymai.auth_service.dto.AuthResponse;
import com.gymai.auth_service.dto.UserRegisterDto;
import com.gymai.auth_service.entity.User;
import com.gymai.auth_service.exception.UserAlreadyExistsException;
import com.gymai.auth_service.repository.UserRepository;
import com.gymai.auth_service.security.JwtService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;

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
                .build();

        userRepository.save(user);
        log.info("New user registered: {}", user.getEmail());
        // Generate JWT token after registration
        log.info("Generating token for user: {} (ID: {})", user.getEmail(), user.getId());
        String token = jwtService.generateToken(user);
        return new AuthResponse(token);
    }

    public AuthResponse authenticate(AuthRequest request) {
    log.info("Authenticating user: {}", request.getEmail());

    Authentication authentication = authenticationManager.authenticate(
            new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword()));
    SecurityContextHolder.getContext().setAuthentication(authentication);

    // ✅ Load full User entity
    User user = userRepository.findByEmail(request.getEmail())
            .orElseThrow(() -> new RuntimeException("User not found: " + request.getEmail()));

    // ✅ Use the single generateToken(User) method
    log.info("Generated token for user: {} (ID: {})", user.getEmail(), user.getId());

    String token = jwtService.generateToken(user);
    return new AuthResponse(token);
}

}
