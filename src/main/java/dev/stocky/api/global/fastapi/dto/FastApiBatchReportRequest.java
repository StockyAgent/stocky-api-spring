package dev.stocky.api.global.fastapi.dto;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FastApiBatchReportRequest {

  private List<String> symbols;
  private String investmentStyle;
  private String startDate;
  private String endDate;
  private String category;
}
