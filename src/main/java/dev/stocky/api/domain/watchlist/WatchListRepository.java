package dev.stocky.api.domain.watchlist;

import dev.stocky.api.domain.user.User;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface WatchListRepository extends JpaRepository<WatchList, Long> {

  List<WatchList> findAllByUser(User user); // 내 관심 목록 조회

  void deleteAllByUser(User user); // 유저의 모든 관심 목록 삭제

  @Query("select distinct u from WatchList wl join wl.user u where wl.stock.symbol = :symbol")
  List<User> findAllUsersBySymbol(@Param("symbol") String symbol); // 특정 종목을 관심 목록에 추가한 모든 유저 조회

  @Query("select s.symbol from WatchList wl join wl.stock s where wl.user = :user")
  List<String> findSymbolsByUser(@Param("user") User user);
}
