package com.gymai.auth_service.security;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.client.web.AuthorizationRequestRepository;
import org.springframework.security.oauth2.core.endpoint.OAuth2AuthorizationRequest;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.header.writers.ReferrerPolicyHeaderWriter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private static final Logger logger = LoggerFactory.getLogger(SecurityConfig.class);

    private final JwtAuthenticationFilter jwtAuthFilter;
    private final JwtAuthenticationEntryPoint authEntryPoint;
    private final UserDetailsService userDetailsService;
    private final OAuth2LoginSuccessHandler oauth2LoginSuccessHandler;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        logger.info("Setting up Security Filter Chain");

        return http
                .csrf(csrf -> {
                    logger.debug("Disabling CSRF");
                    csrf.disable();
                })
                .cors(cors -> {
                    logger.debug("Enabling CORS configuration");
                    cors.configurationSource(corsConfigurationSource());
                })
                .authorizeHttpRequests(auth -> {
                    logger.debug("Configuring authorized endpoints");
                    auth.requestMatchers("/api/auth/**", "/error", "/actuator/**", "/oauth2/**",
                            "/login/**", "/favicon.ico").permitAll();
                    auth.requestMatchers(HttpMethod.OPTIONS, "/**").permitAll();
                    auth.anyRequest().authenticated();
                })
                .exceptionHandling(eh -> {
                    logger.debug("Setting authentication entry point");
                    eh.authenticationEntryPoint(authEntryPoint);
                })
                .oauth2Login(oauth2 -> oauth2
                        .authorizationEndpoint(auth -> auth
                                .authorizationRequestRepository(cookieAuthorizationRequestRepository())
                                .baseUri("/oauth2/authorization"))
                        .redirectionEndpoint(redirect -> redirect
                                .baseUri("/login/oauth2/code/*"))
                        .loginPage("/login")
                        .successHandler(oauth2LoginSuccessHandler)
                        .failureHandler((request, response, exception) -> {
                            logger.error("OAuth2 login failed: {}", exception.getMessage());

                            // Extract frontend URL using same logic as success handler
                            String frontendOrigin = extractFrontendOrigin(request);
                            String errorUrl = frontendOrigin + "/login?error=oauth_failed&reason=" +
                                    java.net.URLEncoder.encode(exception.getMessage(),
                                            java.nio.charset.StandardCharsets.UTF_8);

                            logger.info("Redirecting to error URL: {}", errorUrl);
                            response.sendRedirect(errorUrl);
                        }))
                .sessionManagement(session -> {
                    logger.debug("Configuring session for OAuth state management");
                    // Allow sessions for OAuth but keep stateless for JWT
                    session.sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED);
                    session.maximumSessions(1).maxSessionsPreventsLogin(false);
                    session.sessionFixation().migrateSession();
                })
                .authenticationProvider(authenticationProvider())
                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class)
                .formLogin(form -> form.disable())
                .httpBasic(basic -> basic.disable())
                .build();
    }

    private String extractFrontendOrigin(jakarta.servlet.http.HttpServletRequest request) {
        // Try session first
        Object sessionOrigin = request.getSession(false) != null
                ? request.getSession(false).getAttribute("frontend_origin")
                : null;
        if (sessionOrigin != null) {
            return sessionOrigin.toString();
        }

        // Try cookies
        if (request.getCookies() != null) {
            for (jakarta.servlet.http.Cookie cookie : request.getCookies()) {
                if ("frontend_origin".equals(cookie.getName())) {
                    return cookie.getValue();
                }
            }
        }

        // Try X-Forwarded headers to detect frontend
        String xForwardedHost = request.getHeader("X-Forwarded-Host");
        if (xForwardedHost != null) {
            String protocol = "https"; // Default to HTTPS for production
            String frontendHost = xForwardedHost.replace("auth-service-", "");
            return protocol + "://" + frontendHost;
        }

        // Default fallback
        return "https://gymai.neelahouse.cloud";
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        logger.info("Initializing BCryptPasswordEncoder");
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthorizationRequestRepository<OAuth2AuthorizationRequest> cookieAuthorizationRequestRepository() {
        return new HttpCookieOAuth2AuthorizationRequestRepository();
    }

    @Bean
    public AuthenticationProvider authenticationProvider() {
        logger.info("Setting up AuthenticationProvider");
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(userDetailsService);
        authProvider.setPasswordEncoder(passwordEncoder());
        return authProvider;
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        logger.info("Creating AuthenticationManager");
        return config.getAuthenticationManager();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        logger.info("Configuring CORS");
        CorsConfiguration config = new CorsConfiguration();

        // Specific allowed origins
        config.setAllowedOrigins(List.of(
                "http://localhost:4200",
                "http://localhost:3000",
                "https://gymaibybhawesh.netlify.app",
                "https://gymai.neelahouse.cloud",
                "https://gym-ai.vercel.app",
                // Allow your auth service for OAuth callbacks
                "http://auth-service-gymai.neelahouse.cloud",
                "https://auth-service-gymai.neelahouse.cloud"));

        config.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        config.setAllowedHeaders(List.of("Authorization", "Content-Type", "X-Requested-With", "Accept", "Origin"));
        config.setExposedHeaders(List.of("Authorization"));
        config.setAllowCredentials(true);
        config.setMaxAge(3600L); // Cache preflight for 1 hour

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        return source;
    }
}