package dev.stocky.api.global.sqs;

import dev.stocky.api.domain.report.ReportService;
import dev.stocky.api.global.sqs.dto.DeepAnalysisResultDto;
import dev.stocky.api.domain.report.dto.ReportDto;
import io.awspring.cloud.sqs.annotation.SqsListener;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class SqsConsumer { // TODO: 실패 처리 로직 추가 및 테스트 필요

  private final ReportService reportService;

  // 1. 긴급 뉴스 알림 수신
  @SqsListener(
      value = "${app.sqs.queue.urgent-alert}",
      maxConcurrentMessages = "5",
      maxMessagesPerPoll = "5"
  )
  public void receiveUrgentAlert(ReportDto resultDto) {
    log.info("🚨 SQS 수신 [Urgent Alert]: symbol={}", resultDto.getSymbol());

    // 구독자 조회 및 단체 발송 로직 호출
    reportService.processUrgentReport(resultDto);
  }

  // 2. 심층 리포트 결과 수신
  @SqsListener(
      value = "${app.sqs.queue.deep-response}",
      maxConcurrentMessages = "3",
      maxMessagesPerPoll = "3"
  )
  public void receiveDeepResult(DeepAnalysisResultDto resultDto) {
    log.info("📩 SQS 수신 [Deep Result]: symbol={}", resultDto.getSymbol());

    // TODO: 실제 처리 로직 구현 (ReportService 호출 등)
  }

}
