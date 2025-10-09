package com.auth.use_cases.validate_email;

import com.auth.domain.user_account.AbstractUserAccount;
import com.auth.domain.user_account.DraftUserAccount;
import com.auth.domain.user_account.commands.ValidateEmailCommand;
import com.auth.domain.user_account.events.AccountRegisteredEvent;
import com.auth.domain.user_account.events.EmailValidatedEvent;
import com.auth.use_cases.ValidateEmailCommandHandler;
import com.shared.domain.events.DomainEvent;
import com.shared.services.MessageSourceConfig;
import com.shared.services.Result;
import com.shared.services.ResultMap;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

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
        ResultMap<String> emailConfirmation = accountHydratation.getValue().confirmEmail(command);
        Assertions.assertTrue(emailConfirmation.isSuccess());
    }

    @Test
    public void test_validate_email_on_already_validated_account_has_no_effect() {
        init_event();
        Result<AbstractUserAccount> accountHydratation = baseAccount.applyAll(List.of(registration, emailValidated));
        command = new ValidateEmailCommand("01K6TR3FQJRPBZMNN1TJP5D3YY");
        ResultMap<String> emailConfirmation = accountHydratation.getValue().confirmEmail(command);
        Assertions.assertTrue(emailConfirmation.isSuccess());
    }
}
