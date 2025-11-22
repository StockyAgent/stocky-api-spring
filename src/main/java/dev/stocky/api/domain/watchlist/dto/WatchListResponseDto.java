package dev.stocky.api.domain.watchlist.dto;

import java.util.List;
import lombok.Getter;

@Getter
public class WatchListResponseDto {

  private final int count;
  private final List<WatchListDto> items;

  public WatchListResponseDto(List<WatchListDto> items) {
    this.count = items.size();
    this.items = items;
  }

}
