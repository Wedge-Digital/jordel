package com.bloodbowlclub.lib.services.email_service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.context.MessageSource;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashMap;

@Service("consoleService")
public class ConsoleEmailService extends AbstractEmailService {

    public ConsoleEmailService(MessageSource messageSource) {
        super(messageSource);
    }

    protected void sendRawMessage(String to, String subject, String text) {
        System.out.println("=== EMAIL SENT TO CONSOLE ===");
        System.out.println("Recipient: " + to);
        System.out.println("Subject: " + subject);
        System.out.println("Sent at: " + LocalDateTime.now());
        System.out.println("===============================");
        System.out.println(text);
        System.out.println("===============================");
    }

    protected void sendRawHtmlMessage(String to, String subject, String htmlBody) throws MessagingException {
        System.out.println("=== EMAIL SENT TO CONSOLE ===");
        System.out.println("Recipient: " + to);
        System.out.println("Subject: " + subject);
        System.out.println("Sent at: " + LocalDateTime.now());
        System.out.println("===============================");
        System.out.println(htmlBody);
        System.out.println("===============================");
    }

    protected void sendTemplatizedMessage(String to, String subject, HashMap<String, Object> vars, String templatePath) throws MessagingException {
        String htmlContent = templateEngine.processTemplateFromFile(
                templatePath,
                vars
        );
        System.out.println("=== EMAIL SENT TO CONSOLE ===");
        System.out.println("Recipient: " + to);
        System.out.println("Subject: " + subject);
        System.out.println("Sent at: " + LocalDateTime.now());
        System.out.println("===============================");
        System.out.println(htmlContent);
        System.out.println("===============================");
    }
}
