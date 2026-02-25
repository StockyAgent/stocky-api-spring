package dev.stocky.api.domain.report;

import dev.stocky.api.domain.batch.BatchService;
import dev.stocky.api.global.fastapi.FastApiReportClient;
import dev.stocky.api.global.fastapi.dto.FastApiDeepReportResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

@Slf4j
@RestController
@RequestMapping("/api/reports")
@RequiredArgsConstructor
public class ReportController {

  private final FastApiReportClient fastApiReportClient;
  private final BatchService batchService;

  @Value("${app.batch.key}")
  private String batchKey;

  @PostMapping("/deep/{symbol}")
  public ResponseEntity<FastApiDeepReportResponse> requestDeepReport(
      @PathVariable String symbol
  ) {
    if (!symbol.matches("^[A-Za-z0-9._-]+$")) {
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid symbol format");
    }

    log.info("📢 [API Request] Deep Report 요청 수신: symbol={}", symbol);

    FastApiDeepReportResponse response = fastApiReportClient.requestDeepReport(symbol);
    String status = response.getStatus();

    if ("DONE".equals(status)) {
      return ResponseEntity.ok(response);
    }
    if ("PROCESSING".equals(status) || "ACCEPTED".equals(status)) {
      return ResponseEntity.status(HttpStatus.ACCEPTED).body(response);
    }
    if ("ERROR".equals(status)) {
      return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body(response);
    }

    log.error("FastAPI 예상치 못한 status 수신: symbol={}, status={}", symbol, status);
    return ResponseEntity.status(HttpStatus.BAD_GATEWAY).body(response);
  }

  // 배치 수동 트리거 — 개발자 전용 (BATCH_KEY 인증)

  @PostMapping("/trigger/news")
  public ResponseEntity<String> triggerNews(@RequestHeader("X-Batch-Key") String key) {
    validateBatchKey(key);
    batchService.triggerNewsCollection();
    return ResponseEntity.ok("뉴스 수집 트리거 완료");
  }

  @PostMapping("/trigger/report-generation")
  public ResponseEntity<String> triggerReportGeneration(@RequestHeader("X-Batch-Key") String key) {
    validateBatchKey(key);
    batchService.triggerReportGeneration();
    return ResponseEntity.ok("리포트 생성 트리거 완료");
  }

  @PostMapping("/trigger/email-delivery")
  public ResponseEntity<String> triggerEmailDelivery(@RequestHeader("X-Batch-Key") String key) {
    validateBatchKey(key);
    batchService.triggerEmailDelivery();
    return ResponseEntity.ok("이메일 발송 트리거 완료");
  }

  private void validateBatchKey(String key) {
    if (!batchKey.equals(key)) {
      throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Invalid batch key");
    }
  }
}
