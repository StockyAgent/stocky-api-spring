package dev.stocky.api.domain.watchlist;

import dev.stocky.api.domain.watchlist.dto.WatchListRequestDto;
import dev.stocky.api.domain.watchlist.dto.WatchListResponseDto;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/watchlist")
@RequiredArgsConstructor
public class WatchListController {

  private final WatchListService watchListService;

  // 1. 관심 종목 등록
  @PostMapping
  public ResponseEntity<String> addWatchlist(
      @AuthenticationPrincipal UserDetails userDetails,
      @RequestBody WatchListRequestDto watchListRequestDto
  ) {
    watchListService.addWatchList(userDetails.getUsername(), watchListRequestDto);
    return ResponseEntity.ok("관심 종목에 추가되었습니다.");
  }

  // 2. 내 관심 종목 조회
  @GetMapping
  public ResponseEntity<List<WatchListResponseDto>> getMyWatchlist(
      @AuthenticationPrincipal UserDetails userDetails
  ) {
    List<WatchListResponseDto> watchlist = watchListService.getWatchLists(
        userDetails.getUsername());
    return ResponseEntity.ok(watchlist);
  }

  // 3. 관심 종목 삭제 (종목 코드로 삭제)
  @DeleteMapping("/{stockCode}")
  public ResponseEntity<String> deleteWatchlist(
      @AuthenticationPrincipal UserDetails userDetails,
      @PathVariable String stockCode
  ) {
    watchListService.deleteWatchList(userDetails.getUsername(), stockCode);
    return ResponseEntity.ok("관심 종목에서 삭제되었습니다.");
  }
}
