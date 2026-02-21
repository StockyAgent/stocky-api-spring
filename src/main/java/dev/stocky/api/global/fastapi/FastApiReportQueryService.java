package dev.stocky.api.global.fastapi;

import dev.stocky.api.domain.report.ReportQueryService;
import dev.stocky.api.global.fastapi.dto.FastApiBatchReportResponse;
import dev.stocky.api.global.fastapi.dto.FastApiReportItem;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class FastApiReportQueryService implements ReportQueryService {

  private final FastApiReportClient fastApiReportClient;

  /**
   * FastAPI REST API를 통해 오늘 날짜의 리포트를 조회한다.
   * status == "COMPLETED" 인 항목만 반환한다.
   *
   * @param symbols    관심 심볼 목록
   * @param dateKst    조회 날짜 (KST, yyyy-MM-dd)
   * @param investType 투자 성향 (예: "investor", "trader")
   * @return symbol → HTML content 맵 (COMPLETED 상태만)
   */
  @Override
  public Map<String, String> fetchTodayReports(
      Set<String> symbols,
      String dateKst,
      String investType
  ) {
    if (symbols == null || symbols.isEmpty()) {
      return Collections.emptyMap();
    }

    FastApiBatchReportResponse response = fastApiReportClient.fetchBatchReports(
        new ArrayList<>(symbols), investType, dateKst
    );

    if (response == null || response.getReports() == null) {
      log.warn("FastAPI 응답 없음: investType={}", investType);
      return Collections.emptyMap();
    }

    int totalReturned = response.getReports().size();

    Map<String, String> result = response.getReports().stream()
        .filter(item -> "COMPLETED".equals(item.getStatus()))
        .filter(item -> item.getContent() != null && !item.getContent().isBlank())
        .collect(Collectors.toMap(
            FastApiReportItem::getSymbol,
            FastApiReportItem::getContent,
            (existing, duplicate) -> existing  // 중복 심볼은 첫 번째 값 유지
        ));

    int filtered = totalReturned - result.size();
    if (filtered > 0) {
      log.debug("FastAPI 필터링: investType={}, 전체 {}개 중 {}개 제외 (PENDING/FAILED/empty)",
          investType, totalReturned, filtered);
    }

    log.info("FastAPI 리포트 조회 완료: investType={}, 요청 {}개, COMPLETED {}개",
        investType, symbols.size(), result.size());
    return result;
  }
}
