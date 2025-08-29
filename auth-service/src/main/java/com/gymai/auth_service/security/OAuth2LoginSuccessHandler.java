// Corrected OAuth2LoginSuccessHandler.java
package com.gymai.auth_service.security;

import org.springframework.amqp.core.DirectExchange;
import org.springframework.beans.factory.annotation.Autowired;
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

    @Value("${app.frontend.default-url:http://localhost:4200}")
    private String defaultFrontendUrl;

    @Value("#{'${app.frontend.allowed-urls:http://localhost:4200}'.split(',')}")
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

            // ✅ Resolve frontend redirect
            String redirectUrlParam = request.getParameter("redirectUrl");
            String redirectUrl = resolveRedirectUrl(redirectUrlParam);

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
        if (redirectUrlParam != null && allowedFrontendUrls.contains(redirectUrlParam)) {
            return redirectUrlParam;
        }
        log.warn("Redirect URL {} not in allowed list, using default {}", redirectUrlParam, defaultFrontendUrl);
        return defaultFrontendUrl;
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
}
