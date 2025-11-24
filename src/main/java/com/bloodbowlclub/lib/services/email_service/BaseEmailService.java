package com.bloodbowlclub.lib.services.email_service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.springframework.mail.javamail.JavaMailSender;

import java.util.HashMap;

@Service
public class BaseEmailService {

    @Autowired
    private JavaMailSender emailSender;

    private final TemplateEngine templateEngine;

    public BaseEmailService(TemplateEngine templateEngine) {
        this.templateEngine = templateEngine;
    }

    public void sendRawMessage(String to, String subject, String text) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom("");  // votre adresse d'expéditeur
        message.setTo(to);
        message.setSubject(subject);
        message.setText(text);
        emailSender.send(message);
    }

    public void sendRawHtmlMessage(String to, String subject, String htmlBody) throws MessagingException {
        MimeMessage message = emailSender.createMimeMessage();

        // True indique que le message contient du contenu HTML
        MimeMessageHelper helper = new MimeMessageHelper(message, true);
        helper.setFrom("bloodbowlclub@gmail.com");
        helper.setTo(to);
        helper.setSubject(subject);
        helper.setText(htmlBody, true); // true pour indiquer que c'est du HTML

        emailSender.send(message);
    }

    public void sendtemplatizedMessage(String to, HashMap<String, Object> vars) throws MessagingException {
        MimeMessage message = emailSender.createMimeMessage();
        String htmlContent = templateEngine.processTemplateFromFile(
                "lost_login.html",
                vars
        );

        MimeMessageHelper helper = new MimeMessageHelper(message, true);

        helper.setFrom("bloodbowlclub@gmail.com");
        helper.setTo(to);
        helper.setSubject("template");
        helper.setText(htmlContent, true); // true pour indiquer que c'est du HTML

        emailSender.send(message);
    }
}
