package dev.stocky.api.domain.user.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class UserUpdateDto {

  private String name; // 변경할 이름 (닉네임)
}