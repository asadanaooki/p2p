package com.example.p2p.component;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.AllArgsConstructor;
import lombok.Data;

@Component
@AllArgsConstructor
public class EmailSender {

    private static final Logger logger = LoggerFactory.getLogger(EmailSender.class);

    private JavaMailSender mailSender;

    public void send(EmailMessage message) {
        logger.info("メール送信開始");

        MimeMessage mime = mailSender.createMimeMessage();
        try {
            MimeMessageHelper helper = new MimeMessageHelper(mime, false, "UTF-8");
            helper.setFrom(message.getFrom());
            helper.setTo(message.getTo());
            helper.setText(message.getBody(), message.isHtml());
            helper.setSubject(message.getSubject());

            mailSender.send(mime);

            logger.info("メール送信成功");
        }
        catch (MessagingException e) {
            logger.error("メール送信失敗", e);
            throw new RuntimeException(e);
        }
    }

    @Data
    public static class EmailMessage {

        // TODO: 仮値
        private String from = "temp@example.com";

        private String to;

        private String subject;

        private String body;

        private boolean html;

    }

}
