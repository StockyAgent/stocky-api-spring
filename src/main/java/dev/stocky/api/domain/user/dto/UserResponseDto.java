package dev.stocky.api.domain.user.dto;

import dev.stocky.api.domain.user.User;
import lombok.Getter;

@Getter
public class UserResponseDto {

  private final Long id;
  private final String email;
  private final String name;
  private final String provider; // google

  // Entity -> DTO 변환 생성자
  public UserResponseDto(User user) {
    this.id = user.getId();
    this.email = user.getEmail();
    this.name = user.getName();
    this.provider = user.getProvider();
  }
}