package dev.stocky.api.global.fastapi;

import dev.stocky.api.global.fastapi.dto.FastApiBatchReportRequest;
import dev.stocky.api.global.fastapi.dto.FastApiBatchReportResponse;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

@Slf4j
@Component
public class FastApiReportClient {

  private static final String BATCH_LOOKUP_PATH = "/ai/api/report/reports/batch_lookup";
  private static final String CATEGORY_DAILY = "DAILY";
  private static final int CONNECT_TIMEOUT_MS = 5_000;
  private static final int READ_TIMEOUT_MS = 30_000;

  private final RestClient restClient;

  public FastApiReportClient(
      @Value("${app.ai-server-url}") String aiServerUrl,
      @Value("${app.ai-internal-api-key}") String internalApiKey
  ) {
    SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();
    factory.setConnectTimeout(CONNECT_TIMEOUT_MS);
    factory.setReadTimeout(READ_TIMEOUT_MS);

    this.restClient = RestClient.builder()
        .baseUrl(aiServerUrl)
        .requestFactory(factory)
        .defaultHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE)
        .defaultHeader("Accept", MediaType.APPLICATION_JSON_VALUE)
        .defaultHeader("X-Internal-Api-Key", internalApiKey)
        .build();
  }

  /**
   * FastAPI batch_lookup 엔드포인트를 호출하여 심볼별 리포트를 조회한다.
   *
   * @param symbols         심볼 목록 (예: ["AAPL", "TSLA"])
   * @param investmentStyle 투자 성향 (예: "investor", "trader")
   * @param dateKst         조회 날짜 (KST 기준, yyyy-MM-dd 형식)
   * @return FastApiBatchReportResponse (오류 발생 시 빈 reports 목록 반환)
   */
  public FastApiBatchReportResponse fetchBatchReports(
      List<String> symbols,
      String investmentStyle,
      String dateKst
  ) {
    FastApiBatchReportRequest request = FastApiBatchReportRequest.builder()
        .symbols(symbols)
        .investmentStyle(investmentStyle)
        .startDate(dateKst)
        .endDate(dateKst)
        .category(CATEGORY_DAILY)
        .build();

    log.debug("FastAPI batch_lookup 요청: symbols={}, investmentStyle={}, date={}",
        symbols, investmentStyle, dateKst);

    try {
      FastApiBatchReportResponse response = restClient.post()
          .uri(BATCH_LOOKUP_PATH)
          .contentType(MediaType.APPLICATION_JSON)
          .body(request)
          .retrieve()
          .body(FastApiBatchReportResponse.class);

      if (response == null) {
        log.warn("FastAPI 응답이 null");
        return emptyResponse();
      }

      log.debug("FastAPI batch_lookup 응답 수신: reports={}개",
          response.getReports() == null ? 0 : response.getReports().size());
      return response;

    } catch (Exception e) {
      log.error("FastAPI 리포트 조회 실패: symbols={}, error={}", symbols, e.getMessage(), e);
      return emptyResponse();
    }
  }

  private FastApiBatchReportResponse emptyResponse() {
    return new FastApiBatchReportResponse(List.of());
  }
}
