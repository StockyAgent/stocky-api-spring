package dev.stocky.api.domain.watchlist;

import dev.stocky.api.domain.stock.Stock;
import dev.stocky.api.domain.user.User;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface WatchListRepository extends JpaRepository<WatchList, Long> {

  List<WatchList> findAllByUser(User user); // 내 관심 목록 조회

  boolean existsByUserAndStock(User user, Stock stock); // 중복 구독 확인

  void deleteByUserAndStock(User user, Stock stock); // 구독 취소

}
