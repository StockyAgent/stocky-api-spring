package dev.stocky.api.global.sqs;

import dev.stocky.api.global.sqs.dto.SymbolListRequestDto;
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

  @Value("${app.sqs.queue.news-request}")
  private String newsRequestQueue;

  @Value("${app.sqs.queue.regular-generation}")
  private String regularGenerationQueue;

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

}
