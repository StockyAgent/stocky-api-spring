package dev.stocky.api.domain.report;

import dev.stocky.api.domain.batch.BatchService;
import dev.stocky.api.global.sqs.dto.DeepAnalysisRequestDto;
import dev.stocky.api.global.sqs.SqsSender;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

@Slf4j
@RestController
@RequestMapping("/api/reports")
@RequiredArgsConstructor
public class ReportController {

  private final SqsSender sqsSender;
  private final BatchService batchService;

  @Value("${app.batch.key}")
  private String batchKey;

  @PostMapping("/deep")
  public ResponseEntity<String> requestDeepReport(@Valid @RequestBody DeepAnalysisRequestDto request) {
    log.info("📢 [API Request] Deep Report 요청 수신: symbol={}", request.getSymbol());
    sqsSender.sendDeepRequest(request);
    return ResponseEntity.ok("Deep report request sent");
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
