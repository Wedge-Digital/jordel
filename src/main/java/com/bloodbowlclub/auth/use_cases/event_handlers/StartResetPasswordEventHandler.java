package com.bloodbowlclub.auth.use_cases.event_handlers;

import com.bloodbowlclub.auth.domain.user_account.events.PasswordResetStartedEvent;
import com.bloodbowlclub.lib.domain.events.AbstractEventDispatcher;
import com.bloodbowlclub.lib.domain.events.DomainEvent;
import com.bloodbowlclub.lib.domain.events.EventHandler;
import com.bloodbowlclub.lib.services.email_service.AbstractEmailService;
import jakarta.annotation.PostConstruct;
import jakarta.mail.MessagingException;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;


@Component
public class StartResetPasswordEventHandler extends EventHandler {
    private final AbstractEmailService emailService;
    private static final org.slf4j.Logger logger = LoggerFactory.getLogger(StartResetPasswordEventHandler.class);

    private String buildResetPasswordLink(String token, String username) {
        return "https://bloodbowlclub.com/reset_password?token="+token+"&username="+username;
    }

    public StartResetPasswordEventHandler(
            @Qualifier("eventDispatcher") AbstractEventDispatcher dispatcher,
            @Qualifier("emailService") AbstractEmailService emailService) {
        super(dispatcher);
        this.emailService = emailService;
    }

    @Override
    public void receive(DomainEvent evt) {
        PasswordResetStartedEvent event = (PasswordResetStartedEvent) evt;
        String recoverUrl = buildResetPasswordLink(
                event.getPasswordToken().toString(),
                event.getUserAccount().getUsername().toString()
        );
        try {
            emailService.sendResetPasswordEmail(
                    event.getUserAccount().getEmail().toString(),
                    event.getUserAccount().getId(),
                    recoverUrl
            );
        } catch (MessagingException exc) {
            logger.error("exception in email sending : {}", exc.toString());
        }
    }

    @Override
    @PostConstruct
    public void initSubscription() {
        logger.info("Subscribing to reset password link ==============================================================");
        this.dispatcher.subscribe(PasswordResetStartedEvent.class, this);
    }
}
