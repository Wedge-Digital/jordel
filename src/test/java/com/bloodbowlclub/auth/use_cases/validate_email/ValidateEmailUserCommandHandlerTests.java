package com.bloodbowlclub.auth.use_cases.validate_email;

import com.bloodbowlclub.auth.domain.user_account.DraftUserAccount;
import com.bloodbowlclub.auth.domain.user_account.commands.ValidateEmailCommand;
import com.bloodbowlclub.auth.domain.user_account.events.AccountRegisteredEvent;
import com.bloodbowlclub.auth.domain.user_account.events.EmailValidatedEvent;
import com.bloodbowlclub.auth.use_cases.ValidateEmailCommandHandler;
import com.bloodbowlclub.lib.domain.events.DomainEvent;
import com.bloodbowlclub.lib.tests.TestCase;
import org.junit.jupiter.api.Test;
import java.util.ArrayList;

public class ValidateEmailUserCommandHandlerTests extends TestCase {
    private ValidateEmailCommand command;

    private ValidateEmailCommandHandler handler;

    private ArrayList<DomainEvent> events = new ArrayList<>();
    private AccountRegisteredEvent registration;
    private EmailValidatedEvent emailValidated;

    private DraftUserAccount baseAccount = new DraftUserAccount("Bagouze");

    @Test
    public void test_validate_email_command_handler_succeeds() {
    }

    @Test
    public void test_validate_email_on_already_validated_account_has_no_effect() {
    }
}
