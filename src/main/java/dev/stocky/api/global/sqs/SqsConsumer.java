package dev.stocky.api.global.sqs;

import dev.stocky.api.domain.report.ReportService;
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

}
