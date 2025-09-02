package com.gymai.auth_service.security;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.oauth2.client.web.AuthorizationRequestRepository;
import org.springframework.security.oauth2.core.endpoint.OAuth2AuthorizationRequest;
import org.springframework.stereotype.Component;

@Component
public class CustomAuthorizationRequestRepository
    implements AuthorizationRequestRepository<OAuth2AuthorizationRequest> {

  private static final String REDIRECT_URI_PARAM_COOKIE_NAME = "redirect_uri";
  private final HttpCookieOAuth2AuthorizationRequestRepository defaultRepo = new HttpCookieOAuth2AuthorizationRequestRepository();

  @Override
  public OAuth2AuthorizationRequest loadAuthorizationRequest(HttpServletRequest request) {
    return defaultRepo.loadAuthorizationRequest(request);
  }

  @Override
  public void saveAuthorizationRequest(OAuth2AuthorizationRequest authorizationRequest,
      HttpServletRequest request,
      HttpServletResponse response) {
    defaultRepo.saveAuthorizationRequest(authorizationRequest, request, response);

    // ✅ Save redirect_uri param in a cookie
    String redirectUri = request.getParameter("redirect_uri");
    if (redirectUri != null) {
      Cookie cookie = new Cookie(REDIRECT_URI_PARAM_COOKIE_NAME, redirectUri);
      cookie.setPath("/");
      cookie.setHttpOnly(false); // must be readable in handler
      cookie.setSecure(true);
      cookie.setMaxAge(180); // 3 minutes
      response.addCookie(cookie);
    }
  }

  @Override
  public OAuth2AuthorizationRequest removeAuthorizationRequest(HttpServletRequest request,
      HttpServletResponse response) {
    return defaultRepo.removeAuthorizationRequest(request, response);
  }
}
