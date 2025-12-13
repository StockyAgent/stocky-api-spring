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

  // symbol 컬럼 추가 (예: AAPL, TSLA 등)
  @Column(nullable = false, unique = true)
  private String symbol;

  // 종목 이름
  @Column(nullable = false)
  private String name;

  @Builder
  public Stock(String figi, String symbol, String name) {
    this.figi = figi;
    this.symbol = symbol;
    this.name = name;
  }

  // 이름, 심볼 변경
  public void update(String name, String symbol) {
    this.name = name;
    this.symbol = symbol;
  }

}
