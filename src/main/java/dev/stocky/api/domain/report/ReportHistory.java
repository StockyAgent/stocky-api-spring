package dev.stocky.api.domain.report;

import dev.stocky.api.domain.user.User;
import dev.stocky.api.global.entity.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table(name = "report_histories")
public class ReportHistory extends BaseTimeEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "user_id", nullable = false)
  private User user;

  // 리포트 종류 (정기, 긴급)
  @Enumerated(EnumType.STRING)
  @Column(nullable = false)
  private ReportType type;

  // 발송 상태 (대기중, 완료, 실패)
  @Enumerated(EnumType.STRING)
  @Column(nullable = false)
  private ReportStatus status;

  // 이메일 본문 스냅샷 (추후 S3로 마이그레이션 예정)
  // 내용이 길므로 TEXT 타입 지정
  @Column(columnDefinition = "TEXT")
  private String emailContentSnapshot;

  @Builder
  public ReportHistory(
      User user,
      ReportType type,
      ReportStatus status,
      String emailContentSnapshot
  ) {
    this.user = user;
    this.type = type;
    this.status = status;
    this.emailContentSnapshot = emailContentSnapshot;
  }

  public void updateStatus(ReportStatus status) {
    this.status = status;
  }
}