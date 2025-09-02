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
            rabbitMqSender.sendMessageToRoute(
                    new UserEvent(user.getEmail(), user.getName(), "OAUTH_LOGIN"),
                    directExchange.getName(),
                    loginRouting);

            // Extract frontend URL with comprehensive strategy
            String frontendBaseUrl = extractFrontendUrlFromRequest(request);
            log.info("Final frontend URL: {}", frontendBaseUrl);

            // Resolve frontend redirect URL
            String redirectUrl = resolveRedirectUrl(frontendBaseUrl);

            // Create the final redirect URL with tokens
            String finalRedirect = String.format(
                    "%s/oauth-callback?access_token=%s&refresh_token=%s&profile_completed=%s",
                    redirectUrl,
                    URLEncoder.encode(accessToken, StandardCharsets.UTF_8),
                    URLEncoder.encode(refreshToken, StandardCharsets.UTF_8),
                    user.isProfileCompleted());

            // Clear the temporary OAuth2 authorization request cookie
            clearOAuth2AuthRequestCookie(response);

            // Redirect user to frontend with tokens
            log.info("=== Redirecting OAuth user to frontend: {} ===", finalRedirect);
            log.info("=== Redirecting OAuth user to frontend: {} ===", finalRedirect);
            response.sendRedirect(finalRedirect);

        } catch (Exception e) {
            log.error("OAuth authentication failed", e);
            // Use the same frontend detection for error redirect
            String frontendUrl = extractFrontendUrlFromRequest(request);
            String errorRedirect = (frontendUrl != null ? frontendUrl : defaultFrontendUrl)
                    + "/login?error=oauth_failed";
            log.error("Redirecting to error page: {}", errorRedirect);
            response.sendRedirect(errorRedirect);
        }
    }

    private String extractFrontendUrlFromRequest(HttpServletRequest request) {
        log.info("=== Extracting Frontend URL ===");

        // Log all request information for debugging
        logRequestDetails(request);

        // Strategy 1: Query parameter (most reliable for initial OAuth request)
        String frontendOrigin = request.getParameter("frontend_origin");
        if (frontendOrigin != null && !frontendOrigin.trim().isEmpty()) {
            try {
                String decodedOrigin = java.net.URLDecoder.decode(frontendOrigin, StandardCharsets.UTF_8);
                log.info("✅ Found frontend origin in query parameter: {}", decodedOrigin);
                return decodedOrigin;
            } catch (Exception e) {
                log.warn("Failed to decode frontend origin from query parameter: {}", frontendOrigin, e);
            }
        }

        // Strategy 2: Session attribute (set during OAuth initiation)
        Object sessionFrontendUrl = request.getSession(false) != null
                ? request.getSession(false).getAttribute("frontend_origin")
                : null;
        if (sessionFrontendUrl != null) {
            String sessionUrl = sessionFrontendUrl.toString();
            log.info("✅ Found frontend origin in session: {}", sessionUrl);
            return sessionUrl;
        }

        // Strategy 3: Cookie
        if (request.getCookies() != null) {
            for (jakarta.servlet.http.Cookie cookie : request.getCookies()) {
                if ("frontend_origin".equals(cookie.getName())) {
                    String cookieValue = cookie.getValue();
                    log.info("✅ Found frontend origin in cookie: {}", cookieValue);
                    return cookieValue;
                }
            }
        }

        // Strategy 4: Referer header analysis
        String referer = request.getHeader("Referer");
        if (referer != null) {
            try {
                java.net.URL url = new java.net.URL(referer);
                String baseUrl = url.getProtocol() + "://" + url.getHost();
                if (url.getPort() != -1 && url.getPort() != 80 && url.getPort() != 443) {
                    baseUrl += ":" + url.getPort();
                }
                log.info("✅ Extracted base URL from referer: {}", baseUrl);
                return baseUrl;
            } catch (Exception e) {
                log.warn("Failed to parse referer URL: {}", referer, e);
            }
        }

        // Strategy 5: Smart environment detection
        String host = request.getHeader("Host");
        String xForwardedHost = request.getHeader("X-Forwarded-Host");
        String xForwardedProto = request.getHeader("X-Forwarded-Proto");

        // Use forwarded headers if available (production behind proxy)
        if (xForwardedHost != null) {
            String protocol = xForwardedProto != null ? xForwardedProto : "https";
            String detectedFrontend = protocol + "://" + xForwardedHost.replace("auth.", "");
            log.info("✅ Detected frontend from forwarded headers: {}", detectedFrontend);
            return detectedFrontend;
        }

        // Development environment detection
        if (host != null && host.contains("localhost")) {
            log.info("✅ Development environment detected, using localhost:4200");
            return "http://localhost:4200";
        }

        log.warn("❌ No valid frontend URL found using any strategy");
        return null;
    }

    private void logRequestDetails(HttpServletRequest request) {
        log.info("Request URL: {}", request.getRequestURL());
        log.info("Query String: {}", request.getQueryString());
        log.info("Method: {}", request.getMethod());

        // Log headers
        log.info("=== Request Headers ===");
        request.getHeaderNames().asIterator().forEachRemaining(name -> {
            log.info("Header {}: {}", name, request.getHeader(name));
        });

        // Log parameters
        log.info("=== Request Parameters ===");
        request.getParameterMap().forEach((key, values) -> {
            log.info("Param {}: {}", key, String.join(",", values));
        });

        // Log cookies
        if (request.getCookies() != null) {
            log.info("=== Cookies ===");
            for (jakarta.servlet.http.Cookie cookie : request.getCookies()) {
                log.info("Cookie {}: {}", cookie.getName(), cookie.getValue());
            }
        }
    }

    private String resolveRedirectUrl(String redirectUrlParam) {
        if (redirectUrlParam != null && !redirectUrlParam.trim().isEmpty()) {
            try {
                String decodedUrl = java.net.URLDecoder.decode(redirectUrlParam, StandardCharsets.UTF_8);
                log.info("Checking decoded redirect URL: {}", decodedUrl);

                // Direct match check
                if (allowedFrontendUrls.contains(decodedUrl)) {
                    log.info("✅ Direct match found in allowed URLs");
                    return decodedUrl;
                }

                // Base URL matching for flexible subdomain/port handling
                for (String allowedUrl : allowedFrontendUrls) {
                    String allowedBase = extractBaseUrl(allowedUrl);
                    String candidateBase = extractBaseUrl(decodedUrl);

                    if (candidateBase.equals(allowedBase)) {
                        log.info("✅ Base URL match: {} matches allowed base {}", candidateBase, allowedBase);
                        return decodedUrl;
                    }
                }

                // Localhost special handling
                if (decodedUrl.contains("localhost") &&
                        allowedFrontendUrls.stream().anyMatch(url -> url.contains("localhost"))) {
                    log.info("✅ Localhost URL allowed");
                    return decodedUrl;
                }

            } catch (Exception e) {
                log.warn("Failed to decode/validate redirect URL: {}", redirectUrlParam, e);
            }
        }

        log.warn("Using default URL. Redirect URL '{}' not in allowed list: {}",
                redirectUrlParam, allowedFrontendUrls);
        return defaultFrontendUrl;
    }

    private String extractBaseUrl(String url) {
        try {
            java.net.URL parsedUrl = new java.net.URL(url);
            String baseUrl = parsedUrl.getProtocol() + "://" + parsedUrl.getHost();
            if (parsedUrl.getPort() != -1 && parsedUrl.getPort() != 80 && parsedUrl.getPort() != 443) {
                baseUrl += ":" + parsedUrl.getPort();
            }
            return baseUrl;
        } catch (Exception e) {
            return url;
        }
    }

    private User findOrCreateOAuth2User(String email, String name, String provider) {
        return userRepository.findByEmail(email)
                .map(existing -> {
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
                    rabbitMqSender.sendMessageToRoute(
                            new UserEvent(saved.getEmail(), saved.getName(), "OAUTH_REGISTRATION"),
                            directExchange.getName(),
                            registrationRouting);
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

    private void clearOAuth2AuthRequestCookie(HttpServletResponse response) {
        jakarta.servlet.http.Cookie cookie = new jakarta.servlet.http.Cookie(
                HttpCookieOAuth2AuthorizationRequestRepository.OAUTH2_AUTH_REQUEST_COOKIE_NAME,
                null);
        cookie.setPath("/");
        cookie.setHttpOnly(true);
        cookie.setMaxAge(0); // immediately expire
        response.addCookie(cookie);
    }

}