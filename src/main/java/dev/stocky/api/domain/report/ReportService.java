package dev.stocky.api.domain.report;

import dev.stocky.api.domain.report.dto.RegularAnalysisResultDto;
import dev.stocky.api.domain.report.dto.ReportDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class ReportService {

  // private final SesSender sesSender; // (나중에 구현)
  // private final ReportHistoryRepository reportHistoryRepository;
  // private final WatchListRepository watchListRepository;

  // 1. 정기 리포트 처리
  @Transactional
  public void processRegularReport(RegularAnalysisResultDto resultDto) {
    log.info("TODO: 정기 리포트 이메일 발송 로직 수행. User ID: {}", resultDto.getUserId());
    // 1. User 조회
    // 2. 이메일 HTML 생성 (Thymeleaf)
    // 3. ReportHistory 저장 (SENT)
    // 4. SES 발송
  }

  // 2. 긴급 리포트 처리
  @Transactional
  public void processUrgentReport(ReportDto resultDto) {
    String figi = resultDto.getFigi();
    log.info("TODO: 긴급 리포트 구독자 조회 및 발송. FIGI: {}", figi);

    // 1. 해당 주식(figi)을 구독한 User 목록 조회 (WatchListRepository)
    // 2. Loop 돌면서 이메일 발송 및 ReportHistory 저장
  }

}
