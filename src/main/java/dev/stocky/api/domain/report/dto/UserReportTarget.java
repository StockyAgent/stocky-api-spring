package dev.stocky.api.domain.report.dto;

import dev.stocky.api.domain.user.InvestmentStyle;
import dev.stocky.api.domain.user.User;
import java.util.List;
import lombok.Getter;

@Getter
public class UserReportTarget {

  private static final String DEFAULT_INVEST_TYPE =
      InvestmentStyle.INVESTOR.name().toLowerCase();

  private final User user;
  private final String investType;
  private final List<String> symbols;

  public UserReportTarget(User user, List<String> symbols) {
    this.user = user;
    this.investType = user.getInvestmentStyle() != null
        ? user.getInvestmentStyle().name().toLowerCase()
        : DEFAULT_INVEST_TYPE;
    this.symbols = symbols;
  }

  public Long getUserId() {
    return user.getId();
  }

  public String getEmail() {
    return user.getEmail();
  }

  public String getName() {
    return user.getName();
  }
}
