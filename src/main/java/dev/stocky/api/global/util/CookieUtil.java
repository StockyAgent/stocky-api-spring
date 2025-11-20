package dev.stocky.api.global.util;

import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Component;

@Component
public class CookieUtil {

  // 리프레시 토큰 쿠키 이름 상수화
  public static final String REFRESH_TOKEN_COOKIE_NAME = "refresh_token";

  @Value("${app.cookie.refresh-token-expiration}")
  private int refreshTokenExpiration;

  @Value("${app.cookie.secure}")
  private boolean secure;

  @Value("${app.cookie.same-site}")
  private String sameSite;

  public void addRefreshTokenCookie(HttpServletResponse response, String refreshToken) {
    ResponseCookie cookie = ResponseCookie.from(REFRESH_TOKEN_COOKIE_NAME, refreshToken)
        .path("/") // 모든 경로에서 접근 가능
        .secure(secure) // YML 설정에 따라 true/false 자동 적용
        .httpOnly(true) // 자바스크립트에서 접근 불가
        .maxAge(refreshTokenExpiration / 1000) // 밀리초 -> 초 변환
        .sameSite(sameSite) // YML 설정에 따라 None/Lax 자동 적용
        .build();

    response.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());
  }

  // 로그아웃 시 쿠키 삭제를 위한 메서드 (미리 만들어두는 기능)
  public void deleteRefreshTokenCookie(HttpServletResponse response) {
    ResponseCookie cookie = ResponseCookie.from(REFRESH_TOKEN_COOKIE_NAME, "")
        .path("/")
        .maxAge(0)
        .build();

    response.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());
  }

}
