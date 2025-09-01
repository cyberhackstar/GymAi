// Corrected OAuth2LoginSuccessHandler.java
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
            OAuth2User oauth2User = (OAuth2User) authentication.getPrincipal();
            String provider = getProvider(authentication);
            String email = extractEmail(oauth2User, provider);
            String name = extractName(oauth2User, provider);

            log.info("OAuth login successful for {} via {}", email, provider);

            // ✅ Find or create user
            User user = findOrCreateOAuth2User(email, name, provider);

            // ✅ Generate tokens
            String accessToken = jwtService.generateAccessToken(user);
            String refreshToken = jwtService.generateRefreshToken(user);
            user.setRefreshToken(refreshToken);
            userRepository.save(user);

            // ✅ Publish event
            rabbitMqSender.sendMessageToRoute(
                    new UserEvent(user.getEmail(), user.getName(), "OAUTH_LOGIN"),
                    directExchange.getName(),
                    loginRouting);

            // ✅ Get frontend URL from OAuth2 state parameter
            String frontendBaseUrl = extractFrontendUrlFromState(request);
            log.info("Extracted frontend URL from OAuth state: {}", frontendBaseUrl);

            // ✅ Resolve frontend redirect URL
            String redirectUrl = resolveRedirectUrl(frontendBaseUrl);

            // ✅ Create the final redirect URL with tokens
            String finalRedirect = String.format(
                    "%s/oauth-callback?access_token=%s&refresh_token=%s&profile_completed=%s",
                    redirectUrl,
                    URLEncoder.encode(accessToken, StandardCharsets.UTF_8),
                    URLEncoder.encode(refreshToken, StandardCharsets.UTF_8),
                    user.isProfileCompleted());

            log.info("Redirecting OAuth user to frontend: {}", finalRedirect);
            response.sendRedirect(finalRedirect);

        } catch (Exception e) {
            log.error("OAuth authentication failed", e);
            response.sendRedirect(defaultFrontendUrl + "/login?error=oauth_failed");
        }
    }

    private String resolveRedirectUrl(String redirectUrlParam) {
        // ✅ First check if the parameter is provided and valid
        if (redirectUrlParam != null && !redirectUrlParam.trim().isEmpty()) {
            // ✅ Decode the URL if it's encoded
            try {
                String decodedUrl = java.net.URLDecoder.decode(redirectUrlParam, StandardCharsets.UTF_8);
                log.info("Decoded redirect URL: {}", decodedUrl);

                // ✅ Check if the decoded URL is in allowed list
                if (allowedFrontendUrls.contains(decodedUrl)) {
                    return decodedUrl;
                }

                // ✅ Check if any allowed URL starts with this base URL (for different
                // ports/protocols)
                for (String allowedUrl : allowedFrontendUrls) {
                    if (decodedUrl.startsWith(extractBaseUrl(allowedUrl))) {
                        log.info("Using allowed base URL match: {}", decodedUrl);
                        return decodedUrl;
                    }
                }

            } catch (Exception e) {
                log.warn("Failed to decode redirect URL: {}", redirectUrlParam, e);
            }
        }

        log.warn("Redirect URL {} not in allowed list or invalid, using default {}",
                redirectUrlParam, defaultFrontendUrl);
        return defaultFrontendUrl;
    }

    // ✅ Extract frontend URL from cookie (simplest approach)
    private String extractFrontendUrlFromState(HttpServletRequest request) {
        // ✅ First try to get from cookie
        if (request.getCookies() != null) {
            for (jakarta.servlet.http.Cookie cookie : request.getCookies()) {
                if ("frontend_origin".equals(cookie.getName())) {
                    String frontendOrigin = cookie.getValue();
                    log.info("Found frontend origin in cookie: {}", frontendOrigin);
                    return frontendOrigin;
                }
            }
        }

        // ✅ Fallback: extract from referer header
        String referer = request.getHeader("Referer");
        if (referer != null) {
            try {
                java.net.URL url = new java.net.URL(referer);
                String baseUrl = url.getProtocol() + "://" + url.getHost();
                if (url.getPort() != -1) {
                    baseUrl += ":" + url.getPort();
                }
                log.info("Extracted base URL from referer: {}", baseUrl);
                return baseUrl;
            } catch (Exception e) {
                log.warn("Failed to parse referer URL: {}", referer, e);
            }
        }

        log.warn("No valid frontend URL found, using default");
        return null;
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

    // ✅ Helper method to extract base URL (protocol + domain)
    private String extractBaseUrl(String url) {
        try {
            java.net.URL parsedUrl = new java.net.URL(url);
            return parsedUrl.getProtocol() + "://" + parsedUrl.getHost() +
                    (parsedUrl.getPort() != -1 ? ":" + parsedUrl.getPort() : "");
        } catch (Exception e) {
            return url;
        }
    }
}
