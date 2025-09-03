package com.gymai.auth_service.security;

import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.oauth2.client.web.DefaultOAuth2AuthorizationRequestResolver;
import org.springframework.security.oauth2.client.web.OAuth2AuthorizationRequestResolver;
import org.springframework.security.oauth2.core.endpoint.OAuth2AuthorizationRequest;
import org.springframework.stereotype.Component;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;

import java.util.HashMap;
import java.util.Map;

@Component
@Slf4j
public class CustomOAuth2AuthorizationRequestResolver implements OAuth2AuthorizationRequestResolver {

  private final DefaultOAuth2AuthorizationRequestResolver defaultResolver;

  public CustomOAuth2AuthorizationRequestResolver(ClientRegistrationRepository clientRegistrationRepository) {
    this.defaultResolver = new DefaultOAuth2AuthorizationRequestResolver(
        clientRegistrationRepository, "/oauth2/authorization");
  }

  @Override
  public OAuth2AuthorizationRequest resolve(HttpServletRequest request) {
    OAuth2AuthorizationRequest authorizationRequest = defaultResolver.resolve(request);
    return customizeAuthorizationRequest(authorizationRequest, request);
  }

  @Override
  public OAuth2AuthorizationRequest resolve(HttpServletRequest request, String clientRegistrationId) {
    OAuth2AuthorizationRequest authorizationRequest = defaultResolver.resolve(request, clientRegistrationId);
    return customizeAuthorizationRequest(authorizationRequest, request);
  }

  private OAuth2AuthorizationRequest customizeAuthorizationRequest(
      OAuth2AuthorizationRequest authorizationRequest,
      HttpServletRequest request) {

    if (authorizationRequest == null) {
      return null;
    }

    // Store frontend origin in session for later retrieval
    String frontendOrigin = request.getParameter("frontend_origin");
    if (frontendOrigin != null && !frontendOrigin.trim().isEmpty()) {
      try {
        String decodedOrigin = java.net.URLDecoder.decode(frontendOrigin, java.nio.charset.StandardCharsets.UTF_8);
        request.getSession().setAttribute("frontend_origin", decodedOrigin);
        log.info("Stored frontend origin in session during OAuth authorization: {}", decodedOrigin);

        // Also store in request attributes for immediate access
        request.setAttribute("frontend_origin", decodedOrigin);

      } catch (Exception e) {
        log.warn("Failed to decode frontend origin parameter: {}", frontendOrigin, e);
      }
    } else {
      // Try to extract from current request context
      String currentOrigin = extractCurrentOrigin(request);
      if (currentOrigin != null) {
        request.getSession().setAttribute("frontend_origin", currentOrigin);
        log.info("Extracted and stored frontend origin from request context: {}", currentOrigin);
      }
    }

    // Create custom attributes for the authorization request
    Map<String, Object> additionalParameters = new HashMap<>(authorizationRequest.getAdditionalParameters());

    // Store frontend origin in the state for cross-request tracking
    String frontendFromSession = (String) request.getSession().getAttribute("frontend_origin");
    if (frontendFromSession != null) {
      additionalParameters.put("frontend_origin", frontendFromSession);
    }

    return OAuth2AuthorizationRequest.from(authorizationRequest)
        .additionalParameters(additionalParameters)
        .build();
  }

  private String extractCurrentOrigin(HttpServletRequest request) {
    // Try to determine current origin from headers
    String xForwardedHost = request.getHeader("X-Forwarded-Host");
    String xForwardedProto = request.getHeader("X-Forwarded-Proto");
    String cfVisitor = request.getHeader("cf-visitor");

    if (xForwardedHost != null) {
      String protocol = "https"; // Default to HTTPS

      // Check Cloudflare visitor header for actual protocol
      if (cfVisitor != null && cfVisitor.contains("\"scheme\":\"http\"")) {
        protocol = "http";
      } else if (xForwardedProto != null) {
        protocol = xForwardedProto;
      }

      // Convert auth subdomain to main frontend domain
      String frontendHost = xForwardedHost.replace("auth-service-", "");
      return protocol + "://" + frontendHost;
    }

    // Fallback for development
    String host = request.getHeader("Host");
    if (host != null && (host.contains("localhost") || host.contains("127.0.0.1"))) {
      return "http://localhost:4200";
    }

    return null;
  }
}