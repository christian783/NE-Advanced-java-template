package io.app.my_app.service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.nio.charset.StandardCharsets;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class MailService {

    private final JavaMailSender javaMailSender;
    private final TemplateEngine templateEngine;

    @Value("${application.mail.from:no-reply@myapp.com}")
    private String from;

    public void sendPlainText(String to, String subject, String body) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(from);
        message.setTo(to);
        message.setSubject(subject);
        message.setText(body);
        javaMailSender.send(message);
    }

    /**
     * Send an HTML email using a Thymeleaf template
     *
     * @param to       Recipient email address
     * @param subject  Email subject
     * @param template Template name (e.g., "email/otp-verification")
     * @param model    Data model for template variables
     */
    public void sendHtmlEmail(String to, String subject, String template, Map<String, Object> model) {
        try {
            MimeMessage mimeMessage = javaMailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, StandardCharsets.UTF_8.name());

            //email properties
            helper.setFrom(from);
            helper.setTo(to);
            helper.setSubject(subject);

            Context context = new Context();
            context.setVariables(model);
            String htmlContent = templateEngine.process(template, context);

            helper.setText(htmlContent, true);

            javaMailSender.send(mimeMessage);
            log.info("HTML email sent to {} using template: {}", to, template);
        } catch (MessagingException e) {
            log.error("Failed to send HTML email to {}", to, e);
            throw new RuntimeException("Failed to send email: " + e.getMessage(), e);
        }
    }

    /**
     * Send an HTML email using a Thymeleaf template with CC/BCC
     *
     * @param to       Recipient email address
     * @param subject  Email subject
     * @param template Template name
     * @param model    Data model for template variables
     * @param cc       CC recipients (optional)
     * @param bcc      BCC recipients (optional)
     */
    public void sendHtmlEmail(String to, String subject, String template, Map<String, Object> model, String[] cc, String[] bcc) {
        try {
            MimeMessage mimeMessage = javaMailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, StandardCharsets.UTF_8.name());

            helper.setFrom(from);
            helper.setTo(to);
            if (cc != null && cc.length > 0) {
                helper.setCc(cc);
            }
            if (bcc != null && bcc.length > 0) {
                helper.setBcc(bcc);
            }
            helper.setSubject(subject);

            Context context = new Context();
            context.setVariables(model);
            String htmlContent = templateEngine.process(template, context);

            helper.setText(htmlContent, true);
            javaMailSender.send(mimeMessage);
            log.info("HTML email sent to {} using template: {}", to, template);
        } catch (MessagingException e) {
            log.error("Failed to send HTML email to {}", to, e);
            throw new RuntimeException("Failed to send email: " + e.getMessage(), e);
        }
    }
}
