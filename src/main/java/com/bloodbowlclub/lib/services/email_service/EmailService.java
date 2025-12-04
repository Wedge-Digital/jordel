package com.bloodbowlclub.lib.services.email_service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.context.MessageSource;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.springframework.mail.javamail.JavaMailSender;

import java.util.HashMap;

@Service("emailService")
public class EmailService extends AbstractEmailService {

    private final JavaMailSender emailBackend;

    public EmailService(JavaMailSender javaMailSender, MessageSource messageSource) {
        super(messageSource);
        this.emailBackend = javaMailSender;
    }

    @Override
    public void sendRawMessage(String to, String subject, String text) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom("");  // votre adresse d'expéditeur
        message.setTo(to);
        message.setSubject(subject);
        message.setText(text);
        emailBackend.send(message);
    }

    @Override
    public void sendRawHtmlMessage(String to, String subject, String htmlBody) throws MessagingException {
        MimeMessage message = emailBackend.createMimeMessage();

        // True indique que le message contient du contenu HTML
        MimeMessageHelper helper = new MimeMessageHelper(message, true);
        helper.setFrom("bloodbowlclub@gmail.com");
        helper.setTo(to);
        helper.setSubject(subject);
        helper.setText(htmlBody, true); // true pour indiquer que c'est du HTML

        emailBackend.send(message);
    }

    @Override
    protected void sendTemplatizedMessage(String to, String subject, HashMap<String, Object> vars, String templatePath) throws MessagingException {
        MimeMessage message = emailBackend.createMimeMessage();
        String htmlContent = templateEngine.processTemplateFromFile(
                templatePath,
                vars
        );

        MimeMessageHelper helper = new MimeMessageHelper(message, true);

        helper.setFrom("bloodbowlclub@gmail.com");
        helper.setTo(to);
        helper.setSubject(subject);
        helper.setText(htmlContent, true); // true pour indiquer que c'est du HTML

        emailBackend.send(message);
    }
}
