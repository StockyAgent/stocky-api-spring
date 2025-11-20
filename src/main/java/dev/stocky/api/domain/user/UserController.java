package dev.stocky.api.domain.user;

import dev.stocky.api.domain.user.dto.UserResponseDto;
import dev.stocky.api.domain.user.dto.UserUpdateDto;
import dev.stocky.api.global.util.CookieUtil;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

  private final UserService userService; // (Service는 아래에서 설명)
  private final CookieUtil cookieUtil;

  // 1. 내 정보 조회
  @GetMapping("/me")
  public ResponseEntity<?> getMyInfo(@AuthenticationPrincipal UserDetails userDetails) {
    // UserDetails에는 이메일(username)이 들어있음
    UserResponseDto userInfo = userService.getMyInfo(userDetails.getUsername());
    return ResponseEntity.ok(userInfo);
  }

  // 2. 내 정보 수정 (예: 닉네임 변경)
  @PatchMapping("/me")
  public ResponseEntity<?> updateMyInfo(
      @AuthenticationPrincipal UserDetails userDetails,
      @RequestBody UserUpdateDto updateDto
  ) {
    userService.updateMyInfo(userDetails.getUsername(), updateDto);
    return ResponseEntity.ok("정보가 수정되었습니다.");
  }

  // 3. 회원 탈퇴
  @DeleteMapping("/me")
  public ResponseEntity<?> withdraw(
      @AuthenticationPrincipal UserDetails userDetails,
      HttpServletResponse response
  ) {
    // DB에서 삭제
    userService.withdraw(userDetails.getUsername());

    // 리프레시 토큰 쿠키도 같이 삭제해줘야 완벽한 탈퇴!
    cookieUtil.deleteRefreshTokenCookie(response);

    return ResponseEntity.ok("회원 탈퇴가 완료되었습니다.");
  }
}