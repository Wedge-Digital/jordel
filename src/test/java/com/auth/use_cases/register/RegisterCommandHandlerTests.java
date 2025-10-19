package com.auth.use_cases.register;

import com.auth.domain.user_account.commands.RegisterCommand;
import com.auth.domain.user_account.events.AccountRegisteredEvent;
import com.auth.use_cases.RegisterCommandHandler;
import com.auth.use_cases.register.fake_policies.FailurePolicy;
import com.auth.use_cases.register.fake_policies.SuccessPolicy;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.lib.domain.events.EventDispatcher;
import com.lib.services.MessageSourceConfig;
import com.lib.services.ResultMap;
import com.test_utilities.dispatcher.FakeEventDispatcher;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.lang.Nullable;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.HashMap;
import java.util.Locale;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = MessageSourceConfig.class)
public class RegisterCommandHandlerTests {
    private RegisterCommand command;

    @Autowired
    private MessageSource messageSource;

    SuccessPolicy successPolicy = new SuccessPolicy(messageSource);
    FailurePolicy failurePolicy = new FailurePolicy(messageSource);

    private void init_command() {
        command = new RegisterCommand("01K6TR3FQJRPBZMNN1TJP5D3YY",
                "bagouze",
                "bertrand.begouin@gmail.com",
                "mon_password");
    }

    private void ExpectErrorMessage(ResultMap<String> result,String messageCode, String expectedMessageCode, @Nullable Object[] args) {
        Assertions.assertTrue(result.isFailure());
        String expectedErrorMessage = messageSource.getMessage(expectedMessageCode, args, Locale.getDefault());
        Assertions.assertEquals(messageCode+":"+expectedErrorMessage, result.getErrorMessage());
    }

    @Test
    public void test_register_command_handler_succeeds() {
        init_command();
        RegisterCommandHandler commandHandler = new RegisterCommandHandler(
                successPolicy,
                successPolicy,
                successPolicy,
                new EventDispatcher()
        );
        ResultMap<String> commandHandlingResult = commandHandler.handle(command);
        Assertions.assertTrue(commandHandlingResult.isSuccess());
    }

    @Test
    public void test_register_command_handler_succeed_dispatch_business_events() {
        init_command();
        FakeEventDispatcher eventDispatcher = new FakeEventDispatcher();
        Assertions.assertTrue(eventDispatcher.getDispatchedEvents().isEmpty());

        RegisterCommandHandler commandHandler = new RegisterCommandHandler(
                successPolicy,
                successPolicy,
                successPolicy,
                eventDispatcher
        );
        ResultMap<String> commandHandlingResult = commandHandler.handle(command);
        Assertions.assertTrue(commandHandlingResult.isSuccess());
        Assertions.assertEquals(1, eventDispatcher.getDispatchedEvents().size());
        Assertions.assertEquals(AccountRegisteredEvent.class, eventDispatcher.getDispatchedEvents().get(0).getClass());
    }

    @Test
    public void test_register_a_user_with_preexisting_username_shall_fail() {
        init_command();
        RegisterCommandHandler commandHandler = new RegisterCommandHandler(
                failurePolicy,
                successPolicy,
                successPolicy,
                new EventDispatcher());
        ResultMap<String> commandHandlingResult = commandHandler.handle(command);
        Assertions.assertTrue(commandHandlingResult.isFailure());
//        ExpectErrorMessage(commandHandlingResult, "username", "user_registration.username.already_exists", new Object[]{command.username()});
    }

    @Test
    public void test_register_a_user_with_an_already_existing_email_shall_fail() {
        init_command();
        RegisterCommandHandler commandHandler = new RegisterCommandHandler(
                successPolicy,
                successPolicy,
                failurePolicy,
                new EventDispatcher());
        ResultMap<String> commandHandlingResult = commandHandler.handle(command);
        Assertions.assertTrue(commandHandlingResult.isFailure());
//        ExpectErrorMessage(commandHandlingResult, "email", "user_registration.email.already_exists", new Object[]{command.email()});
    }

    @Test
    public void test_register_a_user_with_invalid_user_datas_shall_fail() throws JsonProcessingException {
        command = new RegisterCommand("01K6TR3FQJRPBZMNN1TJP5D3",
                "bagouze",
                "bertrand.begouin",
                "mon_password");
        RegisterCommandHandler commandHandler = new RegisterCommandHandler(
                successPolicy,
                successPolicy,
                successPolicy,
                new EventDispatcher());
        ResultMap<String> commandHandlingResult = commandHandler.handle(command);
        Assertions.assertTrue(commandHandlingResult.isFailure());
        HashMap<String, String> expectedResult = new HashMap<>();
        String email_error = messageSource.getMessage("email.invalid",null, Locale.getDefault());
        expectedResult.put("email", email_error);
        String id_error = messageSource.getMessage("ULID.invalid", null, Locale.getDefault());
        expectedResult.put("userId", id_error);
        Assertions.assertEquals(expectedResult, commandHandlingResult.listErrors());
    }
}
