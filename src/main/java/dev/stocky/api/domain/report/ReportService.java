package dev.stocky.api.domain.report;

import dev.stocky.api.domain.report.dto.ReportDto;
import dev.stocky.api.domain.report.dto.UserReportTarget;
import dev.stocky.api.domain.report.history.ReportHistoryService;
import dev.stocky.api.domain.report.history.ReportType;
import dev.stocky.api.domain.user.User;
import dev.stocky.api.domain.watchlist.WatchListRepository;
import dev.stocky.api.domain.watchlist.dto.UserSymbolDto;
import dev.stocky.api.global.email.NotificationSender;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class ReportService {

  private final NotificationSender notificationSender;
  private final WatchListRepository watchListRepository;
  private final ReportHistoryService reportHistoryService;
  private final ReportQueryService reportQueryService;

  // 정기 리포트 이메일 발송 (FastAPI REST API 기반 — 투자 스타일별 그룹 조회)
  public void deliverDailyReportEmails() {
    String todayKst = LocalDate.now(ZoneId.of("Asia/Seoul")).toString();
    log.info("정기 리포트 이메일 발송 시작: date={}", todayKst);

    List<UserReportTarget> targets = buildUserReportTargets();
    if (targets.isEmpty()) {
      log.info("이메일 발송 스킵: 관심 종목 등록 유저 없음");
      return;
    }

    // 1단계: 투자 스타일별로 전체 심볼 수집
    Map<String, Set<String>> symbolsByInvestType = new HashMap<>();
    for (UserReportTarget target : targets) {
      symbolsByInvestType
          .computeIfAbsent(target.getInvestType(), k -> new HashSet<>())
          .addAll(target.getSymbols());
    }

    // 2단계: 투자 스타일별 batch_lookup 호출 (최대 2번)
    Map<String, Map<String, String>> reportCache = new HashMap<>();
    for (var entry : symbolsByInvestType.entrySet()) {
      String investType = entry.getKey();
      Set<String> allSymbols = entry.getValue();

      Map<String, String> reports = reportQueryService.fetchTodayReports(
          allSymbols, todayKst, investType
      );
      reportCache.put(investType, reports);

      log.info("리포트 조회 완료: investType={}, 심볼 {}개 중 {}개 리포트 존재",
          investType, allSymbols.size(), reports.size());
    }

    // 3단계: 유저별 이메일 분배 (HTTP 요청 없이 로컬 캐시에서)
    int successCount = 0;
    int skipCount = 0;

    for (UserReportTarget target : targets) {
      Map<String, String> reportMap = reportCache.getOrDefault(
          target.getInvestType(), Map.of()
      );

      if (reportMap.isEmpty()) {
        skipCount++;
        continue;
      }

      boolean sent = sendReportEmail(target, reportMap);
      if (sent) {
        successCount++;
      } else {
        skipCount++;
      }
    }

    log.info("정기 리포트 이메일 발송 완료: 성공 {}명, 스킵 {}명, 전체 {}명, API 호출 {}번",
        successCount, skipCount, targets.size(), reportCache.size());
  }

  // 긴급 리포트 처리
  public void processUrgentReport(ReportDto resultDto) {
    String symbol = resultDto.getSymbol();

    List<User> users = watchListRepository.findAllUsersBySymbol(symbol);
    log.info("Found {} subscribers for symbol: {}", users.size(), symbol);

    users.stream().distinct().forEach(user -> {
      try {
        String mailContent = notificationSender.sendUrgentReport(user.getEmail(), resultDto);
        log.info("Sent urgent alert email to: {}", user.getEmail());

        reportHistoryService.saveHistory(user, ReportType.URGENT, mailContent);

      } catch (Exception e) {
        log.error("Failed to send urgent email to: {}", user.getEmail(), e);
      }
    });
  }

  // 사용자별로 관심 symbol 모으는 함수 (2-phase: 심볼 수집 → 객체 생성)
  private List<UserReportTarget> buildUserReportTargets() {
    List<UserSymbolDto> rows = watchListRepository.findAllUsersWithSymbols();
    if (rows.isEmpty()) {
      return List.of();
    }

    Map<Long, List<String>> symbolMap = new HashMap<>();
    Map<Long, User> userMap = new HashMap<>();

    for (UserSymbolDto row : rows) {
      User user = row.getUser();
      userMap.putIfAbsent(user.getId(), user);
      symbolMap.computeIfAbsent(user.getId(), id -> new ArrayList<>())
          .add(row.getSymbol());
    }

    return symbolMap.entrySet().stream()
        .map(e -> new UserReportTarget(userMap.get(e.getKey()), e.getValue()))
        .toList();
  }

  private boolean sendReportEmail(UserReportTarget target, Map<String, String> reportMap) {
    List<ReportDto> reports = target.getSymbols().stream()
        .filter(reportMap::containsKey)
        .map(symbol -> ReportDto.builder()
            .symbol(symbol)
            .content(reportMap.get(symbol))
            .build())
        .toList();

    if (reports.isEmpty()) {
      return false;
    }

    try {
      String mailContent = notificationSender.sendRegularReport(
          target.getEmail(), target.getName(), reports);
      reportHistoryService.saveHistory(target.getUser(), ReportType.REGULAR, mailContent);
      return true;
    } catch (Exception e) {
      log.error("이메일 발송 실패: user={}, error={}", target.getEmail(), e.getMessage());
      return false;
    }
  }
}
