package dev.stocky.api.domain.watchlist;

import dev.stocky.api.domain.stock.Stock;
import dev.stocky.api.domain.stock.StockRepository;
import dev.stocky.api.domain.user.User;
import dev.stocky.api.domain.user.UserRepository;
import dev.stocky.api.domain.watchlist.dto.WatchListDto;
import dev.stocky.api.domain.watchlist.dto.WatchListRequestDto;
import dev.stocky.api.domain.watchlist.dto.WatchListRequestDto.StockItem;
import dev.stocky.api.domain.watchlist.dto.WatchListResponseDto;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
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

  /**
   * 관심 종목 목록 동기화 (등록 + 수정 + 삭제 통합) - 프론트엔드의 리스트와 DB 상태를 일치시킵니다. - Stock 정보(이름)가 변경되었다면 함께 업데이트합니다.
   */
  @Transactional
  public void syncWatchList(String email, WatchListRequestDto watchListRequestDto) {
    User user = findUserByEmail(email);
    List<StockItem> requestStocks = watchListRequestDto.getStocks();

    // 1. 요청 리스트가 비어있으면 -> 전체 삭제 후 종료 (초기화)
    if (requestStocks == null || requestStocks.isEmpty()) {
      watchListRepository.deleteAllByUser(user);
      return;
    }

    // 2. 요청된 주식 목록 확보 (Stock Master Data 관리)
    List<Stock> targetStocks = new ArrayList<>();

    for (StockItem item : requestStocks) {
      // 없으면 생성, 있으면 조회
      Stock stock = stockRepository.findByFigi(item.getFigi())
          .orElseGet(() -> stockRepository.save(Stock.builder()
              .figi(item.getFigi())
              .name(item.getName())
              .build()));

      // [중요] Dirty Checking: 프론트에서 온 이름이 최신이면 DB 업데이트
      stock.updateName(item.getName());

      targetStocks.add(stock);
    }

    // 3. 현재 DB에 저장된 유저의 구독 목록 조회
    List<WatchList> currentWatchList = watchListRepository.findAllByUser(user);

    // 4. 비교 로직 (Diff)
    // 4-1. 삭제 대상: DB엔 있는데 요청엔 없는 것
    Set<String> targetCodes = targetStocks.stream().map(Stock::getFigi).collect(Collectors.toSet());
    List<WatchList> toDelete = currentWatchList.stream()
        .filter(w -> !targetCodes.contains(w.getStock().getFigi()))
        .toList();
    watchListRepository.deleteAll(toDelete);

    // 4-2. 추가 대상: 요청엔 있는데 DB엔 없는 것
    Set<String> currentCodes = currentWatchList.stream()
        .map(w -> w.getStock().getFigi())
        .collect(Collectors.toSet());

    List<WatchList> toAdd = targetStocks.stream()
        .filter(s -> !currentCodes.contains(s.getFigi()))
        .map(s -> WatchList.builder().user(user).stock(s).build())
        .toList();
    watchListRepository.saveAll(toAdd);
  }

  // 내 관심 종목 조회
  public WatchListResponseDto getWatchlist(String email) {
    User user = findUserByEmail(email);
    
    List<WatchListDto> items = watchListRepository.findAllByUser(user).stream()
        .map(WatchListDto::new)
        .collect(Collectors.toList());

    return new WatchListResponseDto(items);
  }

  private User findUserByEmail(String email) {
    return userRepository.findByEmail(email)
        .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));
  }

}
