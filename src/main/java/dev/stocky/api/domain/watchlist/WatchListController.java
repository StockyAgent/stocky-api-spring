package dev.stocky.api.domain.watchlist;

import dev.stocky.api.domain.watchlist.dto.WatchListRequestDto;
import dev.stocky.api.domain.watchlist.dto.WatchListResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/watchlist")
@RequiredArgsConstructor
public class WatchListController {

  private final WatchListService watchListService;

  // 관심 종목 목록 동기화 (등록/수정/삭제 통합)
  @PutMapping
  public ResponseEntity<String> syncWatchList(
      @AuthenticationPrincipal String email,
      @RequestBody WatchListRequestDto requestDto
  ) {
    watchListService.syncWatchList(email, requestDto);
    return ResponseEntity.ok("관심 종목이 업데이트되었습니다.");
  }

  // 내 관심 종목 조회
  @GetMapping
  public ResponseEntity<WatchListResponseDto> getWatchList(
      @AuthenticationPrincipal String email
  ) {
    WatchListResponseDto response = watchListService.getWatchlist(email);
    return ResponseEntity.ok(response);
  }
}
