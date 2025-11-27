package dev.stocky.api.domain.report.dto;

import dev.stocky.api.domain.user.InvestmentStyle;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RegularAnalysisRequestDto {

  private Long userId;                 // 유저 아이디
  private InvestmentStyle investmentStyle; // 투자 성향 (Enum or String)
  private List<String> figies;         // 관심 등록한 주식의 figi 리스트
}
