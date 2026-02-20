package dev.stocky.api.domain.batch;

import dev.stocky.api.domain.report.ReportService;
import dev.stocky.api.domain.watchlist.WatchListRepository;
import dev.stocky.api.global.sqs.SqsSender;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class BatchService {

  private final WatchListRepository watchListRepository;
  private final SqsSender sqsSender;
  private final ReportService reportService;

  // 전체 유저 watchlist에서 중복 제거된 심볼 리스트 수집
  private List<String> collectAllSymbols() {
    return watchListRepository.findAllDistinctSymbols();
  }

  // 04:30 뉴스 수집 트리거 (심볼 기준)
  @Scheduled(cron = "0 30 4 * * *")
  public void triggerNewsCollection() {
    List<String> symbols = collectAllSymbols();
    if (symbols.isEmpty()) {
      log.info("뉴스 수집 스킵: 관심 종목 없음");
      return;
    }
    log.info("뉴스 수집 트리거 실행. 심볼 수: {}", symbols.size());
    sqsSender.sendNewsCollectionRequest(symbols);
  }

  // 06:00 리포트 생성 요청 (심볼 기준)
  @Scheduled(cron = "0 0 6 * * *")
  public void triggerReportGeneration() {
    List<String> symbols = collectAllSymbols();
    if (symbols.isEmpty()) {
      log.info("리포트 생성 스킵: 관심 종목 없음");
      return;
    }
    log.info("리포트 생성 트리거 실행. 심볼 수: {}", symbols.size());
    sqsSender.sendRegularGenerationRequest(symbols);
  }

  // 08:30 이메일 발송 (유저 기준)
  @Scheduled(cron = "0 30 8 * * *")
  public void triggerEmailDelivery() {
    log.info("이메일 발송 스케줄러 실행");
    reportService.deliverDailyReportEmails();
  }
}
