package dev.stocky.api.domain.watchlist;

import dev.stocky.api.domain.stock.Stock;
import dev.stocky.api.domain.user.User;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table(name = "watch_lists",
    // 유저 ID + 주식 ID 조합은 유니크해야 함 (한 사람이 같은 주식 중복 구독 불가)
    uniqueConstraints = {
        @UniqueConstraint(
            name = "uk_user_stock",
            columnNames = {"user_id", "stock_id"}
        )
    }
)
public class WatchList {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "user_id", nullable = false)
  private User user;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "stock_id", nullable = false)
  private Stock stock;

  @Builder
  public WatchList(User user, Stock stock) {
    this.user = user;
    this.stock = stock;
  }

}
