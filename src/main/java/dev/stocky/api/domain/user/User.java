package dev.stocky.api.domain.user;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@Entity
@Table(name = "users") // MySQL 예약어 회피를 위해 users로 명명
public class User {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(nullable = false, unique = true)
  private String email;

  @Column(nullable = false)
  private String name;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false)
  private Role role;

  @Column
  private String provider; // google, naver 등 (확장성 고려)

  @Enumerated(EnumType.STRING)
  @Column
  private InvestmentStyle investmentStyle;

  @Builder
  public User(String email, String name, Role role, String provider,
      InvestmentStyle investmentStyle) {
    this.email = email;
    this.name = name;
    this.role = role;
    this.provider = provider;
    this.investmentStyle = investmentStyle;
  }

  // 소셜 로그인 시 정보 업데이트를 위한 메서드
  public User update(String name) {
    this.name = name;
    return this;
  }

  // 투자 성향 수정 메서드 (회원 정보 수정 시 사용)
  public void updateInvestmentStyle(InvestmentStyle style) {
    this.investmentStyle = style;
  }

  public String getRoleKey() {
    return this.role.getKey();
  }

}
