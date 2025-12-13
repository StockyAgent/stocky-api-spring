package dev.stocky.api.domain.report;

import dev.stocky.api.domain.report.dto.RegularAnalysisResultDto;
import dev.stocky.api.domain.report.dto.ReportDto;
import dev.stocky.api.domain.user.User;
import dev.stocky.api.domain.user.UserRepository;
import dev.stocky.api.domain.watchlist.WatchListRepository;
import dev.stocky.api.global.email.EmailService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class ReportService {

  private final EmailService emailService;
  private final UserRepository userRepository;
  private final WatchListRepository watchListRepository;
  private final ReportHistoryService reportHistoryService;

  // 1. 정기 리포트 처리
  public void processRegularReport(RegularAnalysisResultDto resultDto) {

    Long userId = resultDto.getUserId();

    // 1. 유저 조회 (이메일 알기 위해)
    User user = userRepository.findById(userId)
        .orElseThrow(() -> new IllegalArgumentException("User not found: " + userId));

    log.info("Processing report for user: {}", user.getEmail());

    // 2. 이메일 발송
    String mailContent = emailService.sendRegularReportEmail(user.getEmail(), userId,
        resultDto.getReports());

    reportHistoryService.saveHistory(user, ReportType.REGULAR, mailContent);
  }


  // 2. 긴급 리포트 처리
  public void processUrgentReport(ReportDto resultDto) {
    String symbol = resultDto.getSymbol();

    // 1. 해당 주식(symbol)을 구독한 User 목록 조회 (WatchListRepository)
    List<User> users = watchListRepository.findAllUsersBySymbol(symbol);
    log.info("Found {} subscribers for symbol: {}", users.size(), symbol);

    users.stream().distinct().forEach(user -> {
      try {
        // 이메일 발송
        String mailContent = emailService.sendUrgentReportEmail(user.getEmail(), resultDto);
        log.info("Sent urgent alert email to: {}", user.getEmail());

        reportHistoryService.saveHistory(user, ReportType.URGENT, mailContent);

      } catch (Exception e) {
        // 🚨 중요: 한 명이 실패해도 로그만 남기고 다음 사람에게 계속 보내야 함
        log.error("Failed to send urgent email to: {}", user.getEmail(), e);
      }
    });
  }


}
