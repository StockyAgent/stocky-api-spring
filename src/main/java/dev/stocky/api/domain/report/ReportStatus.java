package dev.stocky.api.domain.report;

public enum ReportStatus {
  PENDING, // 생성 중
  SENT,    // 발송 완료
  FAILED   // 발송 실패
}
