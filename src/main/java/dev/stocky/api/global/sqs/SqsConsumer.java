package dev.stocky.api.global.sqs;

import dev.stocky.api.domain.report.ReportService;
import dev.stocky.api.domain.report.dto.RegularAnalysisResultDto;
import dev.stocky.api.domain.report.dto.ReportDto;
import io.awspring.cloud.sqs.annotation.SqsListener;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class SqsConsumer {

  private final ReportService reportService;

  // 1. 정기 리포트 결과 수신
  @SqsListener("${app.sqs.queue.regular-response}")
  public void receiveRegularResult(RegularAnalysisResultDto resultDto) {
    log.info("📩 SQS 수신 [Regular Result]: userId={}, reportCount={}",
        resultDto.getUserId(), resultDto.getReports().size());

    // 이메일 생성 및 발송 로직 호출
    reportService.processRegularReport(resultDto);
  }

  // 2. 긴급 뉴스 알림 수신
  @SqsListener("${app.sqs.queue.urgent-alert}")
  public void receiveUrgentAlert(ReportDto resultDto) {
    log.info("🚨 SQS 수신 [Urgent Alert]: symbol={}", resultDto.getSymbol());

    // 구독자 조회 및 단체 발송 로직 호출
    reportService.processUrgentReport(resultDto);
  }

}
