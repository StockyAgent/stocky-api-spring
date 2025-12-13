package dev.stocky.api.domain.batch;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/batch")
@RequiredArgsConstructor
public class BatchController {

  private final BatchService batchService;

  private static final String BATCH_KEY = "TMP_BATCH_KEY_1234"; // 임시 배치 키 (보안을 위해 실제 환경에서는 더 안전한 방법 사용 권장)

  @PostMapping("/dispatch/regular-report")
  public ResponseEntity<String> triggerRegularReportEmail(
      @RequestHeader("X-Batch-Key") String batchKey
  ) {
    if (!BATCH_KEY.equals(batchKey)) {
      return ResponseEntity.status(403).body("Forbidden: Invalid batch key");
    }

    int count = batchService.dispatchRegularReportEmail();
    return ResponseEntity.ok(
        "Regular report email dispatch triggered. " + count + " Requests sent.");
  }

}
