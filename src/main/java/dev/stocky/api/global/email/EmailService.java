package dev.stocky.api.global.email;

import dev.stocky.api.domain.report.dto.ReportDto;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

@Slf4j
@Service
@RequiredArgsConstructor
public class EmailService {

  private final JavaMailSender javaMailSender;
  private final TemplateEngine templateEngine;

  // application.yml 등에 발신자 이메일 설정해두면 좋음 (여기선 하드코딩 or Value)
  // 반드시 AWS SES에서 인증된 이메일이어야 함
  @Value("${app.email.sender}")
  private String senderEmail;

  /**
   * 정기 리포트 이메일 발송
   */
  public void sendRegularReportEmail(String toEmail, Long userId, List<ReportDto> reports) {
    log.info("📧 이메일 발송 시작: to={}, count={}", toEmail, reports.size());

    try {
      MimeMessage message = javaMailSender.createMimeMessage();
      MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

      // 1. 기본 정보 설정
      helper.setFrom(senderEmail);
      helper.setTo(toEmail);
      helper.setSubject("[Stocky] 오늘의 주식 분석 리포트가 도착했습니다! 📈");

      // 2. Thymeleaf 템플릿에 데이터 주입
      Context context = new Context();
      context.setVariable("userId", userId);
      context.setVariable("reports", reports);

      // 3. HTML 렌더링
      String htmlContent = templateEngine.process("regular-report", context);
      helper.setText(htmlContent, true); // true = HTML 모드

      // 4. 발송
      javaMailSender.send(message);
      log.info("✅ 이메일 발송 성공!");

    } catch (MessagingException e) {
      log.error("❌ 이메일 발송 실패: {}", e.getMessage());
      throw new RuntimeException(e);
    }
  }
}
