package io.app.my_app.service;

import java.util.Map;

public interface MailService {
    void sendPlainText(String to, String subject, String body);

    void sendHtmlEmail(String to, String subject, String template, Map<String, Object> model);

    void sendHtmlEmail(String to, String subject, String template, Map<String, Object> model, String[] cc, String[] bcc);
}
