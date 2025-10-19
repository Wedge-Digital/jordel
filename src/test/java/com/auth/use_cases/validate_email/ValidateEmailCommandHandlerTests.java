package com.auth.use_cases.validate_email;

import com.auth.domain.user_account.AbstractUserAccount;
import com.auth.domain.user_account.DraftUserAccount;
import com.auth.domain.user_account.commands.ValidateEmailCommand;
import com.auth.domain.user_account.events.AccountRegisteredEvent;
import com.auth.domain.user_account.events.EmailValidatedEvent;
import com.auth.use_cases.ValidateEmailCommandHandler;
import com.lib.domain.events.DomainEvent;
import com.lib.services.MessageSourceConfig;
import com.lib.services.Result;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.ArrayList;
import java.util.Date;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = MessageSourceConfig.class)
public class ValidateEmailCommandHandlerTests {
    private ValidateEmailCommand command;

    @Autowired
    private MessageSource messageSource;

    private ValidateEmailCommandHandler handler;

    private ArrayList<DomainEvent> events = new ArrayList<>();
    private AccountRegisteredEvent registration;
    private EmailValidatedEvent emailValidated;
    private String userId = "01K6TR3FQJRPBZMNN1TJP5D3YY";

    private DraftUserAccount baseAccount = new DraftUserAccount();

    private void init_event() {
        registration = new AccountRegisteredEvent(
                userId,
                "Bagouze",
                "bagouze@me.com",
                "my_password",
                new Date()
        );
        emailValidated = new EmailValidatedEvent(userId);
    }

    @Test
    public void test_validate_email_command_handler_succeeds() {
        init_event();
        Result<AbstractUserAccount> accountHydratation = baseAccount.apply(registration);
        command = new ValidateEmailCommand("01K6TR3FQJRPBZMNN1TJP5D3YY");
        AbstractUserAccount userAccount = accountHydratation.getValue();
        userAccount.confirmEmail(command);
        Assertions.assertFalse(userAccount.domainEvents().isEmpty());
    }

    @Test
    public void test_validate_email_on_already_validated_account_has_no_effect() {
        init_event();
        ArrayList<DomainEvent> events = new ArrayList<>();
        events.add(registration);
        events.add(emailValidated);
        Result<AbstractUserAccount> accountHydratation = baseAccount.applyAll(events);
        command = new ValidateEmailCommand("01K6TR3FQJRPBZMNN1TJP5D3YY");
        AbstractUserAccount userAccount = accountHydratation.getValue();
        userAccount.confirmEmail(command);
        Assertions.assertTrue(userAccount.domainEvents().isEmpty());
    }
}
