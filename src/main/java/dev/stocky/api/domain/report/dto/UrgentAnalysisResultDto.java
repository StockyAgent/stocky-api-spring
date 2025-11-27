package dev.stocky.api.domain.report.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Getter
@NoArgsConstructor
@ToString
public class UrgentAnalysisResultDto {

  private String figi;
  private String content;

  // 만약 ReportBody 객체 자체를 메시지 루트로 쓴다면 아래 생성자 활용
  public ReportBody toReportBody() {
    return new ReportBody(figi, content);
  }

}
