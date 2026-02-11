package dev.stocky.api.domain.batch;

import dev.stocky.api.domain.report.dto.SymbolListRequestDto;
import dev.stocky.api.domain.user.User;
import dev.stocky.api.domain.user.UserRepository;
import dev.stocky.api.domain.watchlist.WatchListRepository;
import dev.stocky.api.global.sqs.SqsSender;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class BatchService {

  private final UserRepository userRepository;
  private final WatchListRepository watchListRepository;
  private final SqsSender sqsSender;

  // 전체 유저 watchlist에서 중복 제거된 심볼 리스트 수집
  private List<String> collectAllSymbols() {
    return watchListRepository.findAllDistinctSymbols();
  }

  // 03:30 뉴스 수집 트리거 (심볼 기준)
  @Scheduled(cron = "0 30 3 * * *")
  public void triggerNewsCollection() {
    List<String> symbols = collectAllSymbols();
    if (symbols.isEmpty()) {
      log.info("뉴스 수집 스킵: 관심 종목 없음");
      return;
    }
    log.info("뉴스 수집 트리거 실행. 심볼 수: {}", symbols.size());
    sqsSender.sendNewsCollectionRequest(symbols);
  }

  // 05:00 리포트 생성 요청 (심볼 기준)
  @Scheduled(cron = "0 0 5 * * *")
  public void triggerReportGeneration() {
    List<String> symbols = collectAllSymbols();
    if (symbols.isEmpty()) {
      log.info("리포트 생성 스킵: 관심 종목 없음");
      return;
    }
    log.info("리포트 생성 트리거 실행. 심볼 수: {}", symbols.size());
    sqsSender.sendRegularGenerationRequest(symbols);
  }

  // 07:30 이메일 발송 (유저 기준 — Phase 0-D에서 구현 예정)
  @Scheduled(cron = "0 30 7 * * *")
  public void triggerEmailDelivery() {
    log.info("이메일 발송 스케줄러 실행 (Phase 0-D에서 구현 예정)");
    // TODO: Phase 0-D — DynamoDB 조회 → 유저별 심볼 매핑 → 이메일 발송
  }

  // 기존 수동 배치 트리거 (BatchController에서 호출, Phase 0-E에서 정리 예정)
  @Async("taskExecutor")
  public void dispatchRegularReportEmail() {
    List<User> users = userRepository.findAll();

    log.info("정기 리포트 배치 시작 (Legacy). 대상 유저 수: {}", users.size());

    int successCount = 0;
    for (User user : users) {
      List<String> symbols = watchListRepository.findSymbolsByUser(user);

      if (symbols.isEmpty()) {
        continue;
      }

      SymbolListRequestDto requestDto = SymbolListRequestDto.builder()
          .symbols(symbols)
          .build();

      sqsSender.sendRegularRequest(requestDto);
      successCount++;
    }

    log.info("정기 리포트 배치 완료 (Legacy). 전송된 요청 수: {}", successCount);
  }
}
