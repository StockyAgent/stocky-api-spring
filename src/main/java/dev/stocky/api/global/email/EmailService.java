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
   * 이메일 발송 로직
   */
  private String sendEmail(String toEmail, String subject, String templateName, Context context) {
    try {
      MimeMessage message = javaMailSender.createMimeMessage();
      MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

      // 기본 정보 설정
      helper.setFrom(senderEmail);
      helper.setTo(toEmail);
      helper.setSubject(subject);

      // HTML 렌더링
      String htmlContent = templateEngine.process(templateName, context);
      helper.setText(htmlContent, true); // true = HTML 모드

      javaMailSender.send(message);
      log.info("✅ 이메일 발송 성공: to={}", toEmail);

      return htmlContent; // 발송된 이메일 본문 반환

    } catch (MessagingException e) {
      log.error("❌ 이메일 발송 실패: to={}, error={}", toEmail, e.getMessage());
      throw new RuntimeException(e);
    }
  }

  /**
   * 정기 리포트 이메일 발송
   */
  public String sendRegularReportEmail(String toEmail, Long userId, List<ReportDto> reports) {
    Context context = new Context();
    context.setVariable("reports", reports);

    // 템플릿: regular-report.html
    return sendEmail(toEmail, "[Stocky] 오늘의 주식 리포트", "regular-report", context);
  }

  /**
   * 긴급 리포트 이메일 발송
   */
  public String sendUrgentReportEmail(String toEmail, ReportDto report) {
    Context context = new Context();
    context.setVariable("report", report);

    // 템플릿: urgent-report.html
    // TODO: urgent-report 템플릿 작성 필요
    return sendEmail(toEmail, "[Stocky] 긴급 뉴스 알림: " + report.getSymbol(), "urgent-report", context);
  }
}
