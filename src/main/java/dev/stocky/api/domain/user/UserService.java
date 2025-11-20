package dev.stocky.api.domain.user;

import dev.stocky.api.domain.user.dto.UserResponseDto;
import dev.stocky.api.domain.user.dto.UserUpdateDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true) // 기본적으로 읽기 전용 (성능 최적화)
public class UserService {

  private final UserRepository userRepository;

  // 1. 내 정보 조회
  public UserResponseDto getMyInfo(String email) {
    User user = findUserByEmail(email);
    return new UserResponseDto(user);
  }

  // 2. 내 정보 수정
  @Transactional // 쓰기 작업이므로 Transactional 필수
  public void updateMyInfo(String email, UserUpdateDto updateDto) {
    User user = findUserByEmail(email);
    user.update(updateDto.getName()); // User 엔티티의 update 메서드 호출
    // JPA의 Dirty Checking으로 인해 save를 안 호출해도 자동 업데이트됨
  }

  // 3. 회원 탈퇴
  @Transactional
  public void withdraw(String email) {
    User user = findUserByEmail(email);
    userRepository.delete(user); // DB에서 삭제
  }

  // 공통 메서드: 이메일로 유저 찾기
  private User findUserByEmail(String email) {
    return userRepository.findByEmail(email)
        .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));
  }
}