package dev.stocky.api.domain.watchlist.dto;

import dev.stocky.api.domain.watchlist.WatchList;
import lombok.Getter;

@Getter
public class WatchListResponseDto {

  private final Long id;
  private final String code;
  private final String name;

  public WatchListResponseDto(WatchList watchList) {
    this.id = watchList.getId();
    this.code = watchList.getStock().getFigi();
    this.name = watchList.getStock().getName();
  }


}
