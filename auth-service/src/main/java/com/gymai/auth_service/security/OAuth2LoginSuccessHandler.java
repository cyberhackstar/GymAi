package com.gymai.auth_service.security;

import org.springframework.amqp.core.DirectExchange;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import com.gymai.auth_service.entity.AuthProvider;
import com.gymai.auth_service.entity.User;
import com.gymai.auth_service.messaging.RabbitMqSender;
import com.gymai.auth_service.messaging.UserEvent;
import com.gymai.auth_service.repository.UserRepository;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.user.OAuth2User;
import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;

@Component
@Slf4j
public class OAuth2LoginSuccessHandler implements AuthenticationSuccessHandler {

    private final UserRepository userRepository;
    private final JwtService jwtService;
    private final RabbitMqSender rabbitMqSender;
    private final DirectExchange directExchange;

    @Value("${app.frontend.default-url:https://gymai.neelahouse.cloud}")
    private String defaultFrontendUrl;

    @Value("#{'${app.frontend.allowed-urls:https://gymai.neelahouse.cloud}'.split(',')}")
    private List<String> allowedFrontendUrls;

    @Value("${event.routing.registration}")
    private String registrationRouting;

    @Value("${event.routing.login}")
    private String loginRouting;

    public OAuth2LoginSuccessHandler(UserRepository userRepository,
            JwtService jwtService,
            RabbitMqSender rabbitMqSender,
            DirectExchange directExchange) {
        this.userRepository = userRepository;
        this.jwtService = jwtService;
        this.rabbitMqSender = rabbitMqSender;
        this.directExchange = directExchange;
    }

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request,
            HttpServletResponse response,
            Authentication authentication) throws IOException {
        try {
            log.info("=== OAuth2 Authentication Success Handler Started ===");

            OAuth2User oauth2User = (OAuth2User) authentication.getPrincipal();
            String provider = getProvider(authentication);
            String email = extractEmail(oauth2User, provider);
            String name = extractName(oauth2User, provider);

            log.info("OAuth login successful for {} via {}", email, provider);

            // Find or create user
            User user = findOrCreateOAuth2User(email, name, provider);

            // Generate tokens
            String accessToken = jwtService.generateAccessToken(user);
            String refreshToken = jwtService.generateRefreshToken(user);
            user.setRefreshToken(refreshToken);
            userRepository.save(user);

            // Publish event
            try {
                rabbitMqSender.sendMessageToRoute(
                        new UserEvent(user.getEmail(), user.getName(), "OAUTH_LOGIN"),
                        directExchange.getName(),
                        loginRouting);
                log.info("Successfully sent OAuth login event");
            } catch (Exception e) {
                log.warn("Failed to send OAuth login event, continuing with login", e);
            }

            // Extract frontend URL
            String frontendBaseUrl = extractFrontendUrlFromRequest(request);
            String redirectUrl = resolveRedirectUrl(frontendBaseUrl);

            log.info("Final redirect URL: {}", redirectUrl);

            // Create the final redirect URL with tokens
            String finalRedirect = String.format(
                    "%s/oauth-callback?access_token=%s&refresh_token=%s&profile_completed=%s",
                    redirectUrl,
                    URLEncoder.encode(accessToken, StandardCharsets.UTF_8),
                    URLEncoder.encode(refreshToken, StandardCharsets.UTF_8),
                    user.isProfileCompleted());

            // Clear OAuth cookies
            clearOAuth2Cookies(response);

            log.info("Redirecting OAuth user to: {}", finalRedirect);
            response.sendRedirect(finalRedirect);

        } catch (Exception e) {
            log.error("OAuth authentication processing failed", e);
            handleOAuthError(request, response, e);
        }
    }

    private String extractFrontendUrlFromRequest(HttpServletRequest request) {
        log.info("=== Extracting Frontend URL ===");

        // Strategy 1: Session attribute (most reliable)
        Object sessionFrontendUrl = request.getSession(false) != null
                ? request.getSession(false).getAttribute("frontend_origin")
                : null;
        if (sessionFrontendUrl != null) {
            String sessionUrl = sessionFrontendUrl.toString();
            log.info("Found frontend origin in session: {}", sessionUrl);
            return sessionUrl;
        }

        // Strategy 2: Cookie
        if (request.getCookies() != null) {
            for (jakarta.servlet.http.Cookie cookie : request.getCookies()) {
                if ("frontend_origin".equals(cookie.getName())) {
                    String cookieValue = cookie.getValue();
                    log.info("Found frontend origin in cookie: {}", cookieValue);
                    return cookieValue;
                }
            }
        }

        // Strategy 3: Query parameter (less reliable for callback)
        String frontendOrigin = request.getParameter("frontend_origin");
        if (frontendOrigin != null && !frontendOrigin.trim().isEmpty()) {
            try {
                String decodedOrigin = java.net.URLDecoder.decode(frontendOrigin, StandardCharsets.UTF_8);
                log.info("Found frontend origin in query parameter: {}", decodedOrigin);
                return decodedOrigin;
            } catch (Exception e) {
                log.warn("Failed to decode frontend origin from query parameter: {}", frontendOrigin, e);
            }
        }

        // Strategy 4: Referer header analysis
        String referer = request.getHeader("Referer");
        if (referer != null && referer.contains("accounts.google.com")) {
            // This is coming from Google, try to extract from request context
            String xForwardedHost = request.getHeader("X-Forwarded-Host");
            String xForwardedProto = request.getHeader("X-Forwarded-Proto");

            if (xForwardedHost != null) {
                String protocol = xForwardedProto != null ? xForwardedProto : "https";
                // Convert auth subdomain to main domain
                String frontendHost = xForwardedHost.replace("auth-service-", "");
                String detectedFrontend = protocol + "://" + frontendHost;
                log.info("Detected frontend from forwarded headers: {}", detectedFrontend);
                return detectedFrontend;
            }
        }

        // Strategy 5: Development environment detection
        String host = request.getHeader("Host");
        if (host != null && (host.contains("localhost") || host.contains("127.0.0.1"))) {
            log.info("Development environment detected, using localhost:4200");
            return "http://localhost:4200";
        }

        log.warn("No valid frontend URL found, using default");
        return null;
    }

    private String resolveRedirectUrl(String candidateUrl) {
        if (candidateUrl != null && !candidateUrl.trim().isEmpty()) {
            try {
                String decodedUrl = java.net.URLDecoder.decode(candidateUrl, StandardCharsets.UTF_8);

                // Direct match check
                if (allowedFrontendUrls.contains(decodedUrl)) {
                    log.info("Direct match found in allowed URLs");
                    return decodedUrl;
                }

                // Base URL matching
                for (String allowedUrl : allowedFrontendUrls) {
                    if (urlsMatch(decodedUrl, allowedUrl)) {
                        log.info("URL match found: {} matches {}", decodedUrl, allowedUrl);
                        return decodedUrl;
                    }
                }

                // Localhost special handling
                if (decodedUrl.contains("localhost") || decodedUrl.contains("127.0.0.1")) {
                    log.info("Localhost URL allowed");
                    return decodedUrl;
                }

            } catch (Exception e) {
                log.warn("Failed to validate redirect URL: {}", candidateUrl, e);
            }
        }

        log.info("Using default frontend URL: {}", defaultFrontendUrl);
        return defaultFrontendUrl;
    }

    private boolean urlsMatch(String url1, String url2) {
        try {
            java.net.URL parsed1 = new java.net.URL(url1);
            java.net.URL parsed2 = new java.net.URL(url2);

            return parsed1.getProtocol().equals(parsed2.getProtocol()) &&
                    parsed1.getHost().equals(parsed2.getHost()) &&
                    parsed1.getPort() == parsed2.getPort();
        } catch (Exception e) {
            return url1.equals(url2);
        }
    }

    private void handleOAuthError(HttpServletRequest request, HttpServletResponse response, Exception e)
            throws IOException {
        String frontendUrl = extractFrontendUrlFromRequest(request);
        String errorRedirect = (frontendUrl != null ? frontendUrl : defaultFrontendUrl)
                + "/login?error=oauth_failed&message=" + URLEncoder.encode(e.getMessage(), StandardCharsets.UTF_8);
        log.error("Redirecting to error page: {}", errorRedirect);
        response.sendRedirect(errorRedirect);
    }

    private void clearOAuth2Cookies(HttpServletResponse response) {
        // Clear OAuth2 authorization request cookie
        jakarta.servlet.http.Cookie authRequestCookie = new jakarta.servlet.http.Cookie(
                HttpCookieOAuth2AuthorizationRequestRepository.OAUTH2_AUTH_REQUEST_COOKIE_NAME, null);
        authRequestCookie.setPath("/");
        authRequestCookie.setHttpOnly(true);
        authRequestCookie.setMaxAge(0);
        response.addCookie(authRequestCookie);

        // Clear frontend origin cookie after use
        jakarta.servlet.http.Cookie frontendCookie = new jakarta.servlet.http.Cookie("frontend_origin", null);
        frontendCookie.setPath("/");
        frontendCookie.setMaxAge(0);
        response.addCookie(frontendCookie);
    }

    private User findOrCreateOAuth2User(String email, String name, String provider) {
        return userRepository.findByEmail(email)
                .map(existing -> {
                    // Update provider if user was previously local
                    if (existing.getProvider() == AuthProvider.LOCAL) {
                        existing.setProvider(AuthProvider.valueOf(provider.toUpperCase()));
                        return userRepository.save(existing);
                    }
                    return existing;
                })
                .orElseGet(() -> {
                    User newUser = User.builder()
                            .email(email)
                            .name(name != null ? name : "OAuth User")
                            .userRole("USER")
                            .provider(AuthProvider.valueOf(provider.toUpperCase()))
                            .profileCompleted(false)
                            .build();
                    User saved = userRepository.save(newUser);

                    // Send registration event (with error handling)
                    try {
                        rabbitMqSender.sendMessageToRoute(
                                new UserEvent(saved.getEmail(), saved.getName(), "OAUTH_REGISTRATION"),
                                directExchange.getName(),
                                registrationRouting);
                    } catch (Exception e) {
                        log.warn("Failed to send registration event", e);
                    }

                    return saved;
                });
    }

    private String getProvider(Authentication authentication) {
        if (authentication instanceof OAuth2AuthenticationToken) {
            return ((OAuth2AuthenticationToken) authentication).getAuthorizedClientRegistrationId();
        }
        return "unknown";
    }

    private String extractEmail(OAuth2User oauth2User, String provider) {
        switch (provider.toLowerCase()) {
            case "google":
                return oauth2User.getAttribute("email");
            case "github":
                String email = oauth2User.getAttribute("email");
                if (email == null) {
                    String login = oauth2User.getAttribute("login");
                    email = login + "@github.local";
                }
                return email;
            default:
                throw new RuntimeException("Unsupported OAuth provider: " + provider);
        }
    }

    private String extractName(OAuth2User oauth2User, String provider) {
        switch (provider.toLowerCase()) {
            case "google":
                return oauth2User.getAttribute("name");
            case "github":
                String name = oauth2User.getAttribute("name");
                return name != null ? name : oauth2User.getAttribute("login");
            default:
                return "OAuth User";
        }
    }
}