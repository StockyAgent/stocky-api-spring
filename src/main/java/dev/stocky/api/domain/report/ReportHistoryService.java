package dev.stocky.api.domain.report;

import dev.stocky.api.domain.user.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ReportHistoryService {

  private final ReportHistoryRepository reportHistoryRepository;

  @Transactional
  public void saveHistory(User user, ReportType reportType, String htmlContent) {
    ReportHistory history = ReportHistory.builder()
        .user(user)
        .type(reportType)
        .status(ReportStatus.SENT)
        .emailContentSnapshot(htmlContent)
        .build();

    reportHistoryRepository.save(history);
  }

}
