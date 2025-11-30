package com.bloodbowlclub.lib.services.email_service;

import jakarta.mail.MessagingException;
import org.springframework.context.MessageSource;

import java.util.HashMap;
import java.util.Locale;

public abstract class AbstractEmailService {
    protected final TemplateEngine templateEngine = new TemplateEngine();

    private HashMap<MailTemplate, String> templateRepository = new HashMap<>();
    private final MessageSource messageSource;

    public AbstractEmailService(MessageSource messageSource) {
        this.messageSource = messageSource;
        templateRepository.put(MailTemplate.WELCOME, null);
        templateRepository.put(MailTemplate.LOST_PASSWORD, "lost_login.html");
        templateRepository.put(MailTemplate.INVITE, null);
    }

    protected String getTemplate(MailTemplate templateName) {
        return templateRepository.get(templateName);
    }

    void sendWelcomeEmail(String to, String username) {

    }

    public void sendResetPasswordEmail(String to, String coachname, String recoverLoginUrl) throws MessagingException {
        String mailSubject = messageSource.getMessage("mail.lost_login.subject", null, Locale.getDefault());
        String template = Locale.getDefault().toString() + "/lost_login.html";
        HashMap<String, Object> vars = new HashMap<>();
        vars.put("username", coachname);
        vars.put("reset_pwd_url", recoverLoginUrl);
        this.sendTemplatizedMessage(to, mailSubject, vars, template);
    }

    void sendInviteEmail(String to, String inviterUsername, String teamName) {

    }

    protected abstract void sendTemplatizedMessage(String to, String subject, HashMap<String, Object> vars, String templatePath) throws MessagingException;

    protected abstract void sendRawMessage(String to, String subject, String text);

    protected abstract void sendRawHtmlMessage(String to, String subject, String htmlBody) throws MessagingException;

}
