package com.bloodbowlclub.auth.use_cases.register;

import com.bloodbowlclub.auth.domain.user_account.commands.RegisterAccountCommand;
import com.bloodbowlclub.auth.domain.user_account.events.AccountRegisteredEvent;
import com.bloodbowlclub.auth.use_cases.RegisterCommandHandler;
import com.bloodbowlclub.auth.use_cases.register.fake_policies.FailurePolicy;
import com.bloodbowlclub.auth.use_cases.register.fake_policies.SuccessPolicy;
import com.bloodbowlclub.lib.services.result.ErrorCode;
import com.bloodbowlclub.lib.tests.TestCase;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.bloodbowlclub.lib.domain.events.EventDispatcher;
import com.bloodbowlclub.lib.persistance.event_store.EventEntity;
import com.bloodbowlclub.lib.persistance.event_store.EventStore;
import com.bloodbowlclub.lib.persistance.event_store.fake.FakeEventStore;
import com.bloodbowlclub.lib.services.result.ResultMap;
import com.bloodbowlclub.test_utilities.dispatcher.FakeEventDispatcher;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.context.support.ResourceBundleMessageSource;
import org.springframework.lang.Nullable;

import java.util.HashMap;
import java.util.Locale;


public class RegisterAccountCommandHandlerTests extends TestCase {
    private RegisterAccountCommand command;
    private final EventStore eventStore = new FakeEventStore();
    SuccessPolicy successPolicy = new SuccessPolicy(messageSource);
    FailurePolicy failurePolicy = new FailurePolicy(messageSource, ErrorCode.BAD_REQUEST);

    private void init_command() {
        command = new RegisterAccountCommand(
                "bagouze",
                "bertrand.begouin@gmail.com",
                "mon_password");
    }

    private ResourceBundleMessageSource messageSource() {
        ResourceBundleMessageSource ms = new ResourceBundleMessageSource();
        ms.setBasenames("messages", "i18n/messages"); // adaptez aux emplacements réels
        ms.setDefaultEncoding("UTF-8");
        ms.setFallbackToSystemLocale(false);
        return ms;
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
                new FakeEventStore(),
                successPolicy,
                new EventDispatcher(),
                messageSource

        );
        ResultMap<Void> commandHandlingResult = commandHandler.handle(command);
        Assertions.assertTrue(commandHandlingResult.isSuccess());
    }

    @Test
    public void test_register_command_handler_succeed_dispatch_business_events() {
        init_command();
        FakeEventDispatcher eventDispatcher = new FakeEventDispatcher();
        Assertions.assertTrue(eventDispatcher.getDispatchedEvents().isEmpty());

        RegisterCommandHandler commandHandler = new RegisterCommandHandler(
                new FakeEventStore(),
                successPolicy,
                eventDispatcher,
                messageSource
        );
        ResultMap<Void> commandHandlingResult = commandHandler.handle(command);
        Assertions.assertTrue(commandHandlingResult.isSuccess());
        Assertions.assertEquals(1, eventDispatcher.getDispatchedEvents().size());
        Assertions.assertEquals(AccountRegisteredEvent.class, eventDispatcher.getDispatchedEvents().get(0).getClass());
    }

    @Test
    public void test_register_a_user_with_preexisting_username_shall_fail() {
        init_command();
        RegisterCommandHandler commandHandler = new RegisterCommandHandler(
                new FakeEventStore(),
                failurePolicy,
                new EventDispatcher(),
                messageSource);
        ResultMap<Void> commandHandlingResult = commandHandler.handle(command);
        Assertions.assertTrue(commandHandlingResult.isFailure());
//        ExpectErrorMessage(commandHandlingResult, "username", "user_registration.username.already_exists", new Object[]{command.username()});
    }

    @Test
    public void test_register_a_user_with_an_already_existing_email_shall_fail() {
        init_command();
        RegisterCommandHandler commandHandler = new RegisterCommandHandler(
                new FakeEventStore(),
                failurePolicy,
                new EventDispatcher(),
                messageSource);
        ResultMap<Void> commandHandlingResult = commandHandler.handle(command);
        Assertions.assertTrue(commandHandlingResult.isFailure());
    }

    @Test
    public void test_register_a_user_with_invalid_user_datas_shall_fail() throws JsonProcessingException {
        command = new RegisterAccountCommand(
                "bagouze",
                "bertrand.begouin",
                "mon_password");
        RegisterCommandHandler commandHandler = new RegisterCommandHandler(
                new FakeEventStore(),
                successPolicy,
                new EventDispatcher(),
                messageSource);
        ResultMap<Void> commandHandlingResult = commandHandler.handle(command);
        Assertions.assertTrue(commandHandlingResult.isFailure());
        HashMap<String, String> expectedResult = new HashMap<>();
        expectedResult.put("email", messageSource.getMessage("email.invalid",null, Locale.getDefault()));
        Assertions.assertEquals(expectedResult, commandHandlingResult.errorMap());
    }

    @Test
    public void test_register_command_handler_succeed_save_an_event_in_eventStore() {
        init_command();
        Assertions.assertTrue(eventStore.findAll().isEmpty());
        RegisterCommandHandler commandHandler = new RegisterCommandHandler(
                eventStore,
                successPolicy,
                new EventDispatcher(),
                messageSource);

        ResultMap<Void> commandHandlingResult = commandHandler.handle(command);
        Assertions.assertTrue(commandHandlingResult.isSuccess());
        Assertions.assertEquals(1, eventStore.findAll().size());
        EventEntity eventEntity = eventStore.findAll().getFirst();
        Assertions.assertEquals(AccountRegisteredEvent.class, eventEntity.getData().getClass());
        Assertions.assertEquals("bagouze", eventEntity.getData().getAggregateId());
        AccountRegisteredEvent castedEvent = (AccountRegisteredEvent) eventEntity.getData();
        Assertions.assertEquals("bertrand.begouin@gmail.com", castedEvent.getEmail().toString());
    }
}
