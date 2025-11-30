package com.bloodbowlclub.auth.use_cases.login;


import com.bloodbowlclub.JsonAssertions;
import com.bloodbowlclub.auth.domain.user_account.commands.LoginCommand;
import com.bloodbowlclub.auth.domain.user_account.events.AccountRegisteredEvent;
import com.bloodbowlclub.auth.domain.user_account.events.UserLoggedEvent;
import com.bloodbowlclub.auth.domain.user_account.values.Email;
import com.bloodbowlclub.auth.domain.user_account.values.Password;
import com.bloodbowlclub.auth.io.web.UserTestUtils;
import com.bloodbowlclub.auth.use_cases.LoginCommandHandler;
import com.bloodbowlclub.lib.domain.events.DomainEvent;
import com.bloodbowlclub.lib.persistance.event_store.EventEntity;
import com.bloodbowlclub.lib.persistance.event_store.EventEntityFactory;
import com.bloodbowlclub.lib.persistance.event_store.fake.FakeEventStore;
import com.bloodbowlclub.lib.services.result.ResultMap;
import com.bloodbowlclub.lib.tests.TestCase;
import com.bloodbowlclub.test_utilities.dispatcher.FakeEventDispatcher;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.context.i18n.LocaleContextHolder;


import java.io.IOException;
import java.util.HashMap;
import java.util.List;

public class LoginCommandTest extends TestCase {

    FakeEventStore fakeEventStore = new FakeEventStore();
    FakeEventDispatcher fakeEventDispatcher = new FakeEventDispatcher();
    LoginCommandHandler handler = new LoginCommandHandler(fakeEventStore, fakeEventDispatcher, messageSource);
    EventEntityFactory factory = new EventEntityFactory();



    @Test
    @DisplayName("Test a login attempts with ok password succeed")
    void Test_successful_login() {
        String username = "Bagouze";
        UserTestUtils.createUser(username, fakeEventStore);
        ResultMap<Void> handling = handler.handle(new LoginCommand(username, "no-password"));
        Assertions.assertTrue(handling.isSuccess());
    }

    @Test
    @DisplayName("Test a successful login retrieves a couple of jwt token")
    void Test_successful_login_dispatch_a_userlogged_event() throws IOException {
        String username = "Bagouze";
        UserTestUtils.createUser(username, fakeEventStore);
        ResultMap<Void> handling = handler.handle(new LoginCommand(username, "no-password"));
        Assertions.assertTrue(handling.isSuccess());

        List<DomainEvent> dispatchedEvents = fakeEventDispatcher.getDispatchedEvents();
        Assertions.assertEquals(1, dispatchedEvents.size());
        DomainEvent event = dispatchedEvents.get(0);
        Assertions.assertTrue(event instanceof UserLoggedEvent);

        List<EventEntity> allEvent = fakeEventStore.findAll();
        Assertions.assertEquals(2, allEvent.size());
        JsonAssertions.assertEqualsFixture(
                allEvent,
               List.of("id","time","timeStampedAt","password")
        );
    }

    @Test
    @DisplayName("check a login with a wrong password fails, and return a failure")
    public void testLoginFails() {
        String username = "Bagouze";
        UserTestUtils.createUser(username, fakeEventStore);
        ResultMap<Void> handling = handler.handle(new LoginCommand(username, "coincoin33"));
        Assertions.assertTrue(handling.isFailure());

        HashMap<String, String> errors = new HashMap<>();
        String expectedError = messageSource.getMessage("user_account.bad_credentials", new Object[]{username}, LocaleContextHolder.getLocale());
        errors.put("password", expectedError);
        Assertions.assertEquals(errors, handling.errorMap());
    }

    @Test
    @DisplayName("check a login on a not existing account, shall fails, retrieving an error")
    public void test_login_not_existing() {
        String username = "unknown_user";
        ResultMap<Void> handling = handler.handle(new LoginCommand(username, "password"));
        Assertions.assertTrue(handling.isFailure());
        HashMap<String, String> errors = new HashMap<>();
        String expectedError = messageSource.getMessage("user_account.not_existing", new Object[]{username}, LocaleContextHolder.getLocale());
        errors.put("username", expectedError);
        Assertions.assertEquals(errors, handling.errorMap());
    }

}
