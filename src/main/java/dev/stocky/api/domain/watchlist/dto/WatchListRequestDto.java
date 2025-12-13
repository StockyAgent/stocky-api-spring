package dev.stocky.api.domain.watchlist.dto;

import java.util.List;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class WatchListRequestDto {

  // 관심 종목 리스트
  private List<StockItem> stocks;

  @Getter
  @NoArgsConstructor
  public static class StockItem {

    private String figi;
    private String symbol;
    private String name;
  }

}
