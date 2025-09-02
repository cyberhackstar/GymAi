package com.gymai.auth_service.security;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.oauth2.core.endpoint.OAuth2AuthorizationRequest;
import org.springframework.security.oauth2.client.web.AuthorizationRequestRepository;
import org.springframework.util.SerializationUtils;

import java.util.Base64;

public class HttpCookieOAuth2AuthorizationRequestRepository
    implements AuthorizationRequestRepository<OAuth2AuthorizationRequest> {

  public static final String OAUTH2_AUTH_REQUEST_COOKIE_NAME = "oauth2_auth_request";
  private static final int COOKIE_EXPIRE_SECONDS = 180;

  @Override
  public OAuth2AuthorizationRequest loadAuthorizationRequest(HttpServletRequest request) {
    return fetchCookie(request, OAUTH2_AUTH_REQUEST_COOKIE_NAME);
  }

  @Override
  public void saveAuthorizationRequest(OAuth2AuthorizationRequest authorizationRequest,
      HttpServletRequest request,
      HttpServletResponse response) {
    if (authorizationRequest == null) {
      removeAuthorizationRequestCookies(request, response);
      return;
    }

    String cookieValue = Base64.getUrlEncoder()
        .encodeToString(SerializationUtils.serialize(authorizationRequest));
    Cookie cookie = new Cookie(OAUTH2_AUTH_REQUEST_COOKIE_NAME, cookieValue);
    cookie.setPath("/");
    cookie.setHttpOnly(true);
    cookie.setMaxAge(COOKIE_EXPIRE_SECONDS);
    response.addCookie(cookie);
  }

  @Override
  public OAuth2AuthorizationRequest removeAuthorizationRequest(HttpServletRequest request,
      HttpServletResponse response) {
    OAuth2AuthorizationRequest authRequest = loadAuthorizationRequest(request);
    removeAuthorizationRequestCookies(request, response);
    return authRequest;
  }

  private void removeAuthorizationRequestCookies(HttpServletRequest request, HttpServletResponse response) {
    Cookie cookie = new Cookie(OAUTH2_AUTH_REQUEST_COOKIE_NAME, "");
    cookie.setPath("/");
    cookie.setMaxAge(0);
    response.addCookie(cookie);
  }

  private OAuth2AuthorizationRequest fetchCookie(HttpServletRequest request, String name) {
    if (request.getCookies() == null)
      return null;

    for (Cookie cookie : request.getCookies()) {
      if (name.equals(cookie.getName())) {
        try {
          byte[] decoded = Base64.getUrlDecoder().decode(cookie.getValue());
          return (OAuth2AuthorizationRequest) SerializationUtils.deserialize(decoded);
        } catch (Exception ignored) {
        }
      }
    }
    return null;
  }
}
