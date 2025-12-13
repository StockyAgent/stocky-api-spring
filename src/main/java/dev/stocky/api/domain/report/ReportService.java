package dev.stocky.api.domain.report;

import dev.stocky.api.domain.report.dto.RegularAnalysisResultDto;
import dev.stocky.api.domain.report.dto.ReportDto;
import dev.stocky.api.domain.user.User;
import dev.stocky.api.domain.user.UserRepository;
import dev.stocky.api.global.email.EmailService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class ReportService {

  private final EmailService emailService;
  private final UserRepository userRepository;

  // 1. 정기 리포트 처리
  @Transactional
  public void processRegularReport(RegularAnalysisResultDto resultDto) {

    Long userId = resultDto.getUserId();

    // 1. 유저 조회 (이메일 알기 위해)
    User user = userRepository.findById(userId)
        .orElseThrow(() -> new IllegalArgumentException("User not found: " + userId));

    log.info("Processing report for user: {}", user.getEmail());

    // 2. 이메일 발송
    emailService.sendRegularReportEmail(user.getEmail(), userId, resultDto.getReports());
    // 3. ReportHistory 저장 (SENT)
    // 4. SES 발송
  }

  // 2. 긴급 리포트 처리
  @Transactional
  public void processUrgentReport(ReportDto resultDto) {
    String symbol = resultDto.getSymbol();
    log.info("TODO: 긴급 리포트 구독자 조회 및 발송. SYMBOL: {}", symbol);

    // 1. 해당 주식(symbol)을 구독한 User 목록 조회 (WatchListRepository)
    // 2. Loop 돌면서 이메일 발송 및 ReportHistory 저장
  }

}
