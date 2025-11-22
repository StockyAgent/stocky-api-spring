package dev.stocky.api.domain.watchlist.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class WatchListRequestDto {

  private String figi; // 주식의 FIGI
  private String name; // 주식 이름

}
