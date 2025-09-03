package com.gymai.auth_service.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import lombok.extern.slf4j.Slf4j;

import jakarta.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Arrays;

@Service
@Slf4j
public class FrontendOriginService {

  @Value("${app.frontend.default-url:https://gymai.neelahouse.cloud}")
  private String defaultFrontendUrl;

  @Value("#{'${app.frontend.allowed-urls:https://gymai.neelahouse.cloud}'.split(',')}")
  private List<String> allowedFrontendUrls;

  /**
   * Extract frontend origin using multiple strategies with cross-domain support
   */
  public String extractFrontendOrigin(HttpServletRequest request) {
    log.info("=== Extracting Frontend Origin ===");

    // Strategy 1: Session attribute (most reliable)
    Object sessionOrigin = request.getSession(false) != null
        ? request.getSession(false).getAttribute("frontend_origin")
        : null;
    if (sessionOrigin != null && isValidOrigin(sessionOrigin.toString())) {
      log.info("✅ Found valid frontend origin in session: {}", sessionOrigin);
      return sessionOrigin.toString();
    }

    // Strategy 2: Cookie
    if (request.getCookies() != null) {
      for (jakarta.servlet.http.Cookie cookie : request.getCookies()) {
        if ("frontend_origin".equals(cookie.getName()) && isValidOrigin(cookie.getValue())) {
          log.info("✅ Found valid frontend origin in cookie: {}", cookie.getValue());
          return cookie.getValue();
        }
      }
    }

    // Strategy 3: Query parameter
    String paramOrigin = request.getParameter("frontend_origin");
    if (paramOrigin != null && isValidOrigin(paramOrigin)) {
      try {
        String decodedOrigin = java.net.URLDecoder.decode(paramOrigin, java.nio.charset.StandardCharsets.UTF_8);
        if (isValidOrigin(decodedOrigin)) {
          log.info("✅ Found valid frontend origin in parameter: {}", decodedOrigin);
          return decodedOrigin;
        }
      } catch (Exception e) {
        log.warn("Failed to decode parameter origin: {}", paramOrigin, e);
      }
    }

    // Strategy 4: Extract from Referer (non-OAuth providers only)
    String referer = request.getHeader("Referer");
    if (referer != null && !isOAuthProvider(referer)) {
      try {
        java.net.URL url = new java.net.URL(referer);
        String refererOrigin = url.getProtocol() + "://" + url.getHost() +
            (url.getPort() != -1 && url.getPort() != 80 && url.getPort() != 443 ? ":" + url.getPort() : "");

        if (isValidOrigin(refererOrigin)) {
          log.info("✅ Found valid frontend origin from referer: {}", refererOrigin);
          return refererOrigin;
        }
      } catch (Exception e) {
        log.warn("Failed to parse referer: {}", referer, e);
      }
    }

    // Strategy 5: Smart subdomain conversion
    String detectedOrigin = detectFromHeaders(request);
    if (detectedOrigin != null && isValidOrigin(detectedOrigin)) {
      log.info("✅ Detected valid frontend origin from headers: {}", detectedOrigin);
      return detectedOrigin;
    }

    // Strategy 6: Development fallback
    if (isDevelopmentEnvironment(request)) {
      String devOrigin = "http://localhost:4200";
      if (isValidOrigin(devOrigin)) {
        log.info("✅ Using development origin: {}", devOrigin);
        return devOrigin;
      }
    }

    log.warn("❌ No valid frontend origin found, using default: {}", defaultFrontendUrl);
    return defaultFrontendUrl;
  }

  /**
   * Validate if origin is in allowed list
   */
  public boolean isValidOrigin(String origin) {
    if (origin == null || origin.trim().isEmpty()) {
      return false;
    }

    String trimmedOrigin = origin.trim();

    // Direct match
    for (String allowedUrl : allowedFrontendUrls) {
      if (allowedUrl.trim().equals(trimmedOrigin)) {
        return true;
      }
    }

    // Flexible matching for different subdomains/protocols
    try {
      java.net.URL candidateUrl = new java.net.URL(trimmedOrigin);
      String candidateHost = candidateUrl.getHost().toLowerCase();

      for (String allowedUrl : allowedFrontendUrls) {
        try {
          java.net.URL allowedUrlObj = new java.net.URL(allowedUrl.trim());
          String allowedHost = allowedUrlObj.getHost().toLowerCase();

          // Same host match
          if (candidateHost.equals(allowedHost)) {
            return true;
          }

          // Subdomain flexibility (e.g., app.gymai.com matches gymai.com)
          if (candidateHost.endsWith("." + allowedHost) || allowedHost.endsWith("." + candidateHost)) {
            return true;
          }

        } catch (Exception e) {
          // If allowed URL is malformed, skip it
          continue;
        }
      }

    } catch (Exception e) {
      // If candidate URL is malformed, it's not valid
      return false;
    }

    // Special case for localhost variations
    if (trimmedOrigin.contains("localhost") || trimmedOrigin.contains("127.0.0.1")) {
      return allowedFrontendUrls.stream()
          .anyMatch(url -> url.contains("localhost") || url.contains("127.0.0.1"));
    }

    return false;
  }

  /**
   * Detect frontend origin from request headers
   */
  private String detectFromHeaders(HttpServletRequest request) {
    String xForwardedHost = request.getHeader("X-Forwarded-Host");
    String cfVisitor = request.getHeader("cf-visitor");
    String xForwardedProto = request.getHeader("X-Forwarded-Proto");

    if (xForwardedHost != null) {
      String protocol = "https"; // Default to HTTPS

      // Check Cloudflare visitor for actual protocol
      if (cfVisitor != null && cfVisitor.contains("\"scheme\":\"http\"")) {
        protocol = "http";
      } else if (xForwardedProto != null) {
        protocol = xForwardedProto;
      }

      // Try different subdomain conversions
      String[] conversionPatterns = {
          "auth-service-", "api-", "auth.", "api."
      };

      for (String pattern : conversionPatterns) {
        if (xForwardedHost.contains(pattern)) {
          String frontendHost = xForwardedHost.replace(pattern, "");
          String detectedOrigin = protocol + "://" + frontendHost;

          if (isValidOrigin(detectedOrigin)) {
            return detectedOrigin;
          }
        }
      }

      // Also try the host as-is (might be the actual frontend)
      String directOrigin = protocol + "://" + xForwardedHost;
      if (isValidOrigin(directOrigin)) {
        return directOrigin;
      }
    }

    return null;
  }

  /**
   * Check if referer is from OAuth provider
   */
  private boolean isOAuthProvider(String referer) {
    String[] oauthProviders = {
        "accounts.google.com",
        "github.com",
        "facebook.com",
        "twitter.com",
        "linkedin.com"
    };

    return Arrays.stream(oauthProviders)
        .anyMatch(provider -> referer.toLowerCase().contains(provider));
  }

  /**
   * Check if this is a development environment
   */
  private boolean isDevelopmentEnvironment(HttpServletRequest request) {
    String host = request.getHeader("Host");
    return host != null && (host.contains("localhost") || host.contains("127.0.0.1"));
  }

  /**
   * Get all allowed origins for CORS configuration
   */
  public List<String> getAllowedOrigins() {
    return allowedFrontendUrls;
  }
}