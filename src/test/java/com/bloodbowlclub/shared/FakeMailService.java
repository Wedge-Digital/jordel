package com.bloodbowlclub.shared;

import com.bloodbowlclub.lib.services.email_service.AbstractEmailService;
import jakarta.mail.MessagingException;
import lombok.Getter;
import org.springframework.context.MessageSource;

import java.util.HashMap;

@Getter
public class FakeMailService extends AbstractEmailService {
    private boolean resetPasswordSent =  false;

    public FakeMailService(MessageSource messageSource) {
        super(messageSource);
    }

    @Override
    public void sendResetPasswordEmail(String to, String coachname, String recoverLoginUrl) throws MessagingException {
        this.resetPasswordSent = true;
    }

    @Override
    protected void sendTemplatizedMessage(String to, String subject, HashMap<String, Object> vars, String templatePath) throws MessagingException {
        return;

    }

    @Override
    protected void sendRawMessage(String to, String subject, String text) {
        return;

    }

    @Override
    protected void sendRawHtmlMessage(String to, String subject, String htmlBody) throws MessagingException {
        return;
    }
}
