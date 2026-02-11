package dev.stocky.api.global.sqs;

import dev.stocky.api.domain.report.dto.DeepAnalysisRequestDto;
import dev.stocky.api.domain.report.dto.SymbolListRequestDto;
import io.awspring.cloud.sqs.operations.SqsTemplate;
import java.util.List;
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

  @Value("${app.sqs.queue.deep-request}")
  private String deepRequestQueue;

  @Value("${app.sqs.queue.news-request}")
  private String newsRequestQueue;

  @Value("${app.sqs.queue.regular-generation}")
  private String regularGenerationQueue;

  // 정기 리포트 요청 전송 (기존 유저별 방식 — Phase 0-E에서 정리 예정)
  public void sendRegularRequest(SymbolListRequestDto requestDto) {
    log.info("🚀 SQS 전송 [Regular Request - Legacy]: symbols={}",
        requestDto.getSymbols());

    // 객체(DTO)를 넣으면 자동으로 JSON으로 변환되어 전송됩니다.
    sqsTemplate.send(to -> to
        .queue(regularRequestQueue)
        .payload(requestDto));
  }

  // 뉴스 수집 요청 전송 (심볼 기준)
  public void sendNewsCollectionRequest(List<String> symbols) {
    SymbolListRequestDto requestDto = SymbolListRequestDto.builder()
        .symbols(symbols)
        .build();

    log.info("🚀 SQS 전송 [News Collection]: symbols={}", symbols);

    sqsTemplate.send(to -> to
        .queue(newsRequestQueue)
        .payload(requestDto));
  }

  // 리포트 생성 요청 전송 (심볼 기준)
  public void sendRegularGenerationRequest(List<String> symbols) {
    SymbolListRequestDto requestDto = SymbolListRequestDto.builder()
        .symbols(symbols)
        .build();

    log.info("🚀 SQS 전송 [Regular Generation]: symbols={}", symbols);

    sqsTemplate.send(to -> to
        .queue(regularGenerationQueue)
        .payload(requestDto));
  }

  // 심층 리포트 요청 전송
  public void sendDeepRequest(DeepAnalysisRequestDto requestDto) {
    log.info("🚀 SQS 전송 [Deep Request]: symbol={}", requestDto.getSymbol());

    sqsTemplate.send(to -> to
        .queue(deepRequestQueue)
        .payload(requestDto));
  }
}
