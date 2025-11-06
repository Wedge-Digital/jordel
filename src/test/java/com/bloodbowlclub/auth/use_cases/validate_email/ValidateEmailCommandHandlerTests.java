package com.bloodbowlclub.auth.use_cases.validate_email;

import com.bloodbowlclub.auth.domain.user_account.DraftUserAccount;
import com.bloodbowlclub.auth.domain.user_account.commands.ValidateEmailCommand;
import com.bloodbowlclub.auth.domain.user_account.events.AccountRegisteredEvent;
import com.bloodbowlclub.auth.domain.user_account.events.EmailValidatedEvent;
import com.bloodbowlclub.auth.use_cases.ValidateEmailCommandHandler;
import com.bloodbowlclub.lib.config.MessageSourceConfig;
import com.bloodbowlclub.lib.domain.AggregateRoot;
import com.bloodbowlclub.lib.domain.events.DomainEvent;
import com.bloodbowlclub.lib.services.Result;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.time.Instant;
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
        registration = AccountRegisteredEvent.builder()
                .aggregateId(userId)
                .username("Bagouze")
                .email("bagouze@me.com")
                .password("my_password")
                .createdAt(new Date())
                .build();

        emailValidated = EmailValidatedEvent.builder()
                .aggregateId(userId)
                .timeStampedAt(Instant.now())
                .build();
    }

    @Test
    public void test_validate_email_command_handler_succeeds() {
        init_event();
        Result<AggregateRoot> accountHydratation = baseAccount.apply(registration);
        command = new ValidateEmailCommand("01K6TR3FQJRPBZMNN1TJP5D3YY");
        DraftUserAccount casted = (DraftUserAccount) accountHydratation.getValue();
        casted.confirmEmail(command);
        Assertions.assertFalse(casted.domainEvents().isEmpty());
    }

    @Test
    public void test_validate_email_on_already_validated_account_has_no_effect() {
        init_event();
        ArrayList<DomainEvent> events = new ArrayList<>();
        events.add(registration);
        events.add(emailValidated);
        Result<AggregateRoot> accountHydratation = baseAccount.reconstruct(events);
        command = new ValidateEmailCommand("01K6TR3FQJRPBZMNN1TJP5D3YY");
        DraftUserAccount casted = (DraftUserAccount) accountHydratation.getValue();
        casted.confirmEmail(command);
        Assertions.assertTrue(casted.domainEvents().isEmpty());
    }
}
