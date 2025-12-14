package dev.stocky.api.global.oauth2;

import dev.stocky.api.domain.user.Role;
import dev.stocky.api.global.jwt.JwtTokenProvider;
import dev.stocky.api.global.util.CookieUtil;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

@Slf4j
@Component
@RequiredArgsConstructor
public class OAuth2LoginSuccessHandler implements AuthenticationSuccessHandler {

  private final JwtTokenProvider jwtTokenProvider;
  private final CookieUtil cookieUtil;

  @Value("${app.oauth2.redirect-uri}")
  private String REDIRECT_URI;

  @Override
  public void onAuthenticationSuccess(
      HttpServletRequest request,
      HttpServletResponse response,
      Authentication authentication
  ) throws IOException, ServletException {
    log.info("OAuth2 Login 성공!");

    OAuth2User oAuth2User = (OAuth2User) authentication.getPrincipal();
    String email = oAuth2User.getAttribute("email");
    // Role은 DB에서 가져오거나, 여기서는 간단히 USER로 고정 (CustomOAuth2User를 만들어서 가져오는게 정석)
    String role = Role.USER.getKey();

    // 1. 토큰 생성
    String accessToken = jwtTokenProvider.createAccessToken(email, role);
    String refreshToken = jwtTokenProvider.createRefreshToken(email);

    // 2. 리프레시 토큰을 쿠키에 저장
    cookieUtil.addRefreshTokenCookie(response, refreshToken);

    // 3. 액세스 토큰만 쿼리 파라미터로 전달하여 리다이렉트
    String targetUrl = UriComponentsBuilder.fromUriString(REDIRECT_URI)
        .queryParam("accessToken", accessToken)
        .build()
        .encode(StandardCharsets.UTF_8)
        .toUriString();

    response.sendRedirect(targetUrl);
  }

}
