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
public class EmailNotificationSender implements NotificationSender {

  private final JavaMailSender javaMailSender;
  private final TemplateEngine templateEngine;

  @Value("${app.email.sender}")
  private String senderEmail;

  private String sendEmail(String toEmail, String subject, String templateName, Context context) {
    try {
      MimeMessage message = javaMailSender.createMimeMessage();
      MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

      helper.setFrom(senderEmail);
      helper.setTo(toEmail);
      helper.setSubject(subject);

      String htmlContent = templateEngine.process(templateName, context);
      helper.setText(htmlContent, true);

      javaMailSender.send(message);
      log.info("이메일 발송 성공: to={}", toEmail);

      return htmlContent;

    } catch (MessagingException e) {
      log.error("이메일 발송 실패: to={}, error={}", toEmail, e.getMessage());
      throw new RuntimeException(e);
    }
  }

  @Override
  public String sendRegularReport(String toEmail, String userName, List<ReportDto> reports) {
    Context context = new Context();
    context.setVariable("userName", userName);
    context.setVariable("reports", reports);

    return sendEmail(toEmail, "[Stocky] 오늘의 주식 리포트", "regular-report", context);
  }

  @Override
  public String sendUrgentReport(String toEmail, ReportDto report) {
    Context context = new Context();
    context.setVariable("report", report);

    return sendEmail(toEmail, "[Stocky] 긴급 뉴스 알림: " + report.getSymbol(), "urgent-report", context);
  }
}
