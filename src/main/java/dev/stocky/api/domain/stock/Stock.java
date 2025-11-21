package dev.stocky.api.domain.stock;

import dev.stocky.api.global.entity.BaseTimeEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table(name = "stocks")
public class Stock extends BaseTimeEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  // 종목 코드
  @Column(nullable = false, unique = true)
  private String figi;

  // 종목 이름
  @Column(nullable = false)
  private String name;

  @Builder
  public Stock(String figi, String name) {
    this.figi = figi;
    this.name = name;
  }

}
