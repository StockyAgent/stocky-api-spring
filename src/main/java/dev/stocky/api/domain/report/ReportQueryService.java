package dev.stocky.api.domain.report;

import java.util.Map;
import java.util.Set;

public interface ReportQueryService {

  /**
   * 투자 스타일 + 심볼 기준으로 오늘 날짜 리포트를 조회한다.
   *
   * @param symbols    관심 심볼 목록
   * @param dateKst    조회 날짜 (KST, yyyy-MM-dd)
   * @param investType 투자 성향 (예: "investor", "trader")
   * @return symbol → HTML content 맵 (완료된 리포트만)
   */
  Map<String, String> fetchTodayReports(Set<String> symbols, String dateKst, String investType);
}
