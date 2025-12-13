package dev.stocky.api.domain.auth;

import dev.stocky.api.domain.user.User;
import dev.stocky.api.domain.user.UserRepository;
import dev.stocky.api.global.jwt.JwtTokenProvider;
import dev.stocky.api.global.util.CookieUtil;
import io.jsonwebtoken.Claims;
import jakarta.servlet.http.HttpServletResponse;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

  private final JwtTokenProvider jwtTokenProvider;
  private final UserRepository userRepository;
  private final CookieUtil cookieUtil;

  @PostMapping("/reissue")
  public ResponseEntity<?> reissue(
      @CookieValue(name = "refresh_token", required = false) String refreshToken,
      HttpServletResponse response
  ) {
    // 1. 쿠키에 리프레시 토큰이 없는 경우
    if (refreshToken == null) {
      return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Refresh Token이 없습니다.");
    }

    // 2. 리프레시 토큰 유효성 검사
    if (!jwtTokenProvider.validateToken(refreshToken)) {
      return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("유효하지 않은 Refresh Token입니다.");
    }

    // 3. 토큰에서 유저 이메일 추출
    Claims claims = jwtTokenProvider.parseClaims(refreshToken);
    String email = claims.getSubject();

    // 4. DB에서 사용자 조회
    // todo: 유저가 없으면(탈퇴 등) 401 에러 리턴
    User user = userRepository.findByEmail(email)
        .orElseThrow(() -> new RuntimeException("존재하지 않는 사용자입니다.")); // todo: Custom Exception으로 변경

    // 5. DB에 저장된 가장 최신의 Role 가져오기
    String role = user.getRoleKey(); // 예: "ROLE_USER"

    // 6. 새로운 Access Token 발급
    String newAccessToken = jwtTokenProvider.createAccessToken(email, role);

    // 7. 새로운 Refresh Token 발급 & 쿠키 갱신 (Lightweight RTR 적용)
    // 사용자가 서비스를 이용할 때마다 리프레시 토큰 수명이 다시 2주로 연장됨 (무한 로그인)
    String newRefreshToken = jwtTokenProvider.createRefreshToken(email);
    cookieUtil.addRefreshTokenCookie(response, newRefreshToken);

    log.info("토큰 재발급 성공: {}", email);

    return ResponseEntity.ok(Map.of("accessToken", newAccessToken));
  }

  @PostMapping("/logout")
  public ResponseEntity<?> logout(HttpServletResponse response) {
    // 쿠키 삭제 (MaxAge를 0으로 덮어쓰기)
    cookieUtil.deleteRefreshTokenCookie(response);

    return ResponseEntity.ok("로그아웃 되었습니다.");
  }


}
