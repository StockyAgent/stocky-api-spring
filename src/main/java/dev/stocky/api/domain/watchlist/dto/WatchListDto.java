package dev.stocky.api.domain.watchlist.dto;

import dev.stocky.api.domain.watchlist.WatchList;
import lombok.Getter;

@Getter
public class WatchListDto {

  private final Long id;
  private final String figi;
  private final String name;

  public WatchListDto(WatchList watchList) {
    this.id = watchList.getId();
    this.figi = watchList.getStock().getFigi();
    this.name = watchList.getStock().getName();
  }


}
