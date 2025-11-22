package dev.stocky.api.domain.watchlist;

import dev.stocky.api.domain.stock.Stock;
import dev.stocky.api.domain.stock.StockRepository;
import dev.stocky.api.domain.user.User;
import dev.stocky.api.domain.user.UserRepository;
import dev.stocky.api.domain.watchlist.dto.WatchListRequestDto;
import dev.stocky.api.domain.watchlist.dto.WatchListResponseDto;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class WatchListService {

  private final WatchListRepository watchListRepository;
  private final StockRepository stockRepository;
  private final UserRepository userRepository;

  // 관심 주식 추가
  @Transactional
  public Long addWatchList(String email, WatchListRequestDto watchListRequestDto) {
    User user = findUserByEmail(email);

    // FIGI로 주식 조회, 없으면 새로 생성
    Stock stock = stockRepository.findByFigi(watchListRequestDto.getFigi())
        .orElseGet(() -> stockRepository.save(
            Stock.builder()
                .figi(watchListRequestDto.getFigi())
                .name(watchListRequestDto.getName())
                .build()
        ));

    if (watchListRepository.existsByUserAndStock(user, stock)) {
      throw new IllegalArgumentException("이미 관심 주식으로 등록된 종목입니다.");
    }

    WatchList watchList = WatchList.builder()
        .user(user)
        .stock(stock)
        .build();

    return watchListRepository.save(watchList).getId();
  }

  // 관심 주식 조회
  public List<WatchListResponseDto> getWatchLists(String email) {
    User user = findUserByEmail(email);

    return watchListRepository.findAllByUser(user).stream()
        .map(WatchListResponseDto::new)
        .collect(Collectors.toList());
  }

  // 관심 주식 삭제
  @Transactional
  public void deleteWatchList(String email, String figi) {
    User user = findUserByEmail(email);
    Stock stock = stockRepository.findByFigi(figi)
        .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 종목입니다."));

    watchListRepository.deleteByUserAndStock(user, stock);
  }

  private User findUserByEmail(String email) {
    return userRepository.findByEmail(email)
        .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));
  }

}
