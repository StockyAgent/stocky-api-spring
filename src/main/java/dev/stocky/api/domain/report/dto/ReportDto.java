package dev.stocky.api.domain.report.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class ReportDto {

  private String symbol;    // 주식 심볼
  private String content; // 리포트 내용 (요약, 분석 등)
}
