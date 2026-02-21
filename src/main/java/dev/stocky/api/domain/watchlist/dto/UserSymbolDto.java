package dev.stocky.api.domain.watchlist.dto;

import dev.stocky.api.domain.user.User;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class UserSymbolDto {

  private final User user;
  private final String symbol;
}
