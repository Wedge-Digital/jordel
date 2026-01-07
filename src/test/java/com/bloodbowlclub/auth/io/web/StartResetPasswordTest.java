package com.bloodbowlclub.auth.io.web;

import com.bloodbowlclub.auth.domain.user_account.BaseUserAccount;
import com.bloodbowlclub.auth.io.services.JwtService;
import com.bloodbowlclub.auth.io.web.requests.StartResetPasswordRequest;
import com.bloodbowlclub.auth.use_cases.StartResetPasswordCommandHandler;
import com.bloodbowlclub.auth.use_cases.event_handlers.StartResetPasswordEventHandler;
import com.bloodbowlclub.lib.domain.AggregateRoot;
import com.bloodbowlclub.lib.domain.events.EventDispatcher;
import com.bloodbowlclub.lib.persistance.event_store.fake.FakeEventStore;
import com.bloodbowlclub.lib.services.result.Result;
import com.bloodbowlclub.lib.tests.TestCase;
import com.bloodbowlclub.shared.FakeMailService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.ResponseEntity;

public class StartResetPasswordTest extends TestCase {

    private final JwtService jwtService = new JwtService("my_secret", 10, 100L);

    EventDispatcher dispatcher = new EventDispatcher();
    FakeEventStore eventStore = new FakeEventStore();

    FakeMailService fakeEmailService = new FakeMailService(messageSource);

    private final StartResetPasswordCommandHandler cmdHandler = new StartResetPasswordCommandHandler(
            eventStore,
            dispatcher
            );
    private final AuthController ctrl = new AuthController(
            jwtService,
            null,
            null,
            cmdHandler,
            null,
            messageSource
            );

    private BaseUserAccount getUserAccount(String username) {
        Result<AggregateRoot> userAccount = eventStore.findUser(username);
        Assertions.assertTrue(userAccount.isSuccess());
        return (BaseUserAccount) userAccount.getValue();
    }

    private void checkUserCanChangeItsPassword(String username) {
        BaseUserAccount userAccount = getUserAccount(username);
        Assertions.assertTrue(userAccount.canResetPassword());
    }

    private void checkAnEmailHasBeenTriggered() {
        Assertions.assertTrue(this.fakeEmailService.isResetPasswordSent());
    }

    private void checkUserCannotChangePassword(String username) {
        BaseUserAccount userAccount = getUserAccount(username);
        Assertions.assertFalse(userAccount.canResetPassword());
    }

    @Test
    @DisplayName("assert a lost login on not existing account, retrieves a success message")
    void test_lost_password_on_non_existing_account_returns_success() {
        StartResetPasswordRequest req = new StartResetPasswordRequest("unknown_user");
        ResponseEntity<Void> resp = ctrl.startResetPassword(req);
        Assertions.assertTrue(resp.getStatusCode().is2xxSuccessful());
    }

    @Test
    @DisplayName("Assert a lost login on existing account, allow user account to change password")
    void test_lost_password_on_existing_account_creates_token() {
        //given
        UserTestUtils.createUser("a-simple-username", eventStore);
        checkUserCannotChangePassword("a-simple-username");

        // when
        StartResetPasswordRequest req = new StartResetPasswordRequest("a-simple-username");

        // then the user can change it's password
        ResponseEntity<Void> resp = ctrl.startResetPassword(req);
        Assertions.assertTrue(resp.getStatusCode().is2xxSuccessful());
        checkUserCanChangeItsPassword("a-simple-username");
    }

    @Test
    @DisplayName("Assert two subsequent reset password request, still makes a user able to change it's password")
    void test_two_subsequent_requests_succeed() {
        UserTestUtils.createUser("a-simple-username", eventStore);
        checkUserCannotChangePassword("a-simple-username");

        // when
        StartResetPasswordRequest req = new StartResetPasswordRequest("a-simple-username");
        ResponseEntity<Void> resp = ctrl.startResetPassword(req);
        Assertions.assertEquals(2, eventStore.findAll().size());
        ResponseEntity<Void> resp2 = ctrl.startResetPassword(req);
        Assertions.assertTrue(resp.getStatusCode().is2xxSuccessful());
        Assertions.assertTrue(resp2.getStatusCode().is2xxSuccessful());

        // then a token shall be create
        checkUserCanChangeItsPassword("a-simple-username");
        // event store shall contains only 2 events
        Assertions.assertEquals(2, eventStore.findAll().size());
    }

    @Test
    @DisplayName("Assert a successful lost login request triggers a email")
    void test_a_successful_lost_login_triggers_an_email() throws InterruptedException {
        // Given
        UserTestUtils.createUser("a-simple-username", eventStore);
        StartResetPasswordEventHandler evtHandler = new StartResetPasswordEventHandler(dispatcher, fakeEmailService);
        evtHandler.initSubscription();

        // when
        StartResetPasswordRequest req = new StartResetPasswordRequest("a-simple-username");
        ResponseEntity<Void> resp = ctrl.startResetPassword(req);

        // then a token shall be create
        checkUserCanChangeItsPassword("a-simple-username");

        Thread.sleep(250);

        //TBD Check that the console email is displayed
        checkAnEmailHasBeenTriggered();
    }

}
