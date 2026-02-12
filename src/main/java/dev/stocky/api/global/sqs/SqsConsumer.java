package dev.stocky.api.global.sqs;

import dev.stocky.api.domain.report.ReportService;
import dev.stocky.api.domain.report.dto.DeepAnalysisResultDto;
import dev.stocky.api.domain.report.dto.RegularAnalysisResultDto;
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

  // 1. 정기 리포트 결과 수신
  // 할당 스레드 수로 우선순위가 있는 것처럼 보이게 설정
  @SqsListener(
      value = "${app.sqs.queue.regular-response}",
      maxConcurrentMessages = "1",
      maxMessagesPerPoll = "1"
  )
  public void receiveRegularResult(RegularAnalysisResultDto resultDto) {
    if (resultDto.getReports() == null || resultDto.getReports().isEmpty()) {
      log.warn("⚠️ SQS 수신 [Regular Result]: userId={} - 보고서 없음", resultDto.getUserId());
      return;
    }

    log.info("📩 SQS 수신 [Regular Result]: userId={}, reportCount={}",
        resultDto.getUserId(), resultDto.getReports().size());

    // 이메일 생성 및 발송 로직 호출
    reportService.processRegularReport(resultDto);
  }

  // 2. 긴급 뉴스 알림 수신
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

  // 3. 심층 리포트 결과 수신
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
