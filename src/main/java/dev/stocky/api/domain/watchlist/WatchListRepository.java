package dev.stocky.api.domain.watchlist;

import dev.stocky.api.domain.user.User;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface WatchListRepository extends JpaRepository<WatchList, Long> {

  List<WatchList> findAllByUser(User user); // 내 관심 목록 조회

  void deleteAllByUser(User user); // 유저의 모든 관심 목록 삭제

}
