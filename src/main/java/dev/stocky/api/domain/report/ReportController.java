package dev.stocky.api.domain.report;

import dev.stocky.api.domain.report.dto.DeepAnalysisRequestDto;
import dev.stocky.api.global.sqs.SqsSender;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/api/reports")
@RequiredArgsConstructor
public class ReportController {

  private final SqsSender sqsSender;

  @PostMapping("/deep")
  public ResponseEntity<String> requestDeepReport(@RequestBody DeepAnalysisRequestDto request) {
    log.info("📢 [API Request] Deep Report 요청 수신: userId={}, symbol={}", request.getUserId(), request.getSymbol());
    sqsSender.sendDeepRequest(request);
    return ResponseEntity.ok("Deep report request sent");
  }
}
