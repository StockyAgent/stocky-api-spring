package dev.stocky.api.domain.report.dto;

import java.util.List;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Getter
@NoArgsConstructor
@ToString
public class RegularAnalysisResultDto {

  private Long userId;           // 유저 아이디 (누구 건지 식별용)
  private List<ReportBody> reports; // 리포트 객체 리스트
}
