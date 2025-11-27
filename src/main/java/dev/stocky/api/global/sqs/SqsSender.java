package dev.stocky.api.global.sqs;

import dev.stocky.api.domain.report.dto.RegularAnalysisRequestDto;
import io.awspring.cloud.sqs.operations.SqsTemplate;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class SqsSender {

  private final SqsTemplate sqsTemplate;

  @Value("${app.sqs.queue.regular-request}")
  private String regularRequestQueue;

  // 정기 리포트 요청 전송. 매일 아침 작동
  public void sendRegularRequest(RegularAnalysisRequestDto requestDto) {
    log.info("🚀 SQS 전송 [Regular Request]: userId={}, figies={}",
        requestDto.getUserId(), requestDto.getFigies());

    // 객체(DTO)를 넣으면 자동으로 JSON으로 변환되어 전송됩니다.
    sqsTemplate.send(to -> to
        .queue(regularRequestQueue)
        .payload(requestDto));
  }
}
