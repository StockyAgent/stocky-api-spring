package dev.stocky.api.global.email;

import dev.stocky.api.domain.report.dto.ReportDto;
import java.util.List;

/**
 * 알림 발송 추상화 인터페이스. 현재 구현체: {@link EmailNotificationSender} (Gmail SMTP).
 *
 * 발송 수단 교체 시: 이 인터페이스를 구현한 새 클래스에 {@code @Service}를 붙이고,
 * 기존 구현체의 {@code @Service}를 제거한다. ReportService·템플릿은 변경 불필요.
 *
 * 전환 예시 (SES): build.gradle에 {@code software.amazon.awssdk:ses} 추가,
 * application.yml의 {@code spring.mail} 섹션 제거, AWS 자격증명에 SES 권한 추가.
 */
public interface NotificationSender {
  String sendRegularReport(String toEmail, String userName, List<ReportDto> reports);
  String sendUrgentReport(String toEmail, ReportDto report);
}
