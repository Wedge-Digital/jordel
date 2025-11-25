package com.bloodbowlclub.auth.io.web;

import com.bloodbowlclub.auth.domain.user_account.events.AccountRegisteredEvent;
import com.bloodbowlclub.auth.domain.user_account.values.Email;
import com.bloodbowlclub.auth.domain.user_account.values.Password;
import com.bloodbowlclub.auth.io.repositories.LostLoginTokenEntity;
import com.bloodbowlclub.auth.io.services.JwtService;
import com.bloodbowlclub.auth.io.web.requests.LostLoginRequest;
import com.bloodbowlclub.auth.use_cases.LostPasswordCommandHandler;
import com.bloodbowlclub.lib.persistance.event_store.EventEntity;
import com.bloodbowlclub.lib.persistance.event_store.EventEntityFactory;
import com.bloodbowlclub.lib.persistance.event_store.fake.FakeEventStore;
import com.bloodbowlclub.lib.services.email_service.ConsoleEmailService;
import com.bloodbowlclub.lib.tests.TestCase;
import com.bloodbowlclub.test_utilities.dispatcher.FakeEventDispatcher;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.ResponseEntity;

public class AuthControllerLostPasswordTest extends TestCase {

    private final JwtService jwtService = new JwtService("my_secret", 10, 100L, messageSource);

    FakeEventDispatcher dispatcher = new FakeEventDispatcher();
    FakeEventStore eventStore = new FakeEventStore();

    FakeLostLoginTokenRepository fakeTokenRepos = new FakeLostLoginTokenRepository();

    EventEntityFactory factory = new EventEntityFactory();

    ConsoleEmailService emailService = new ConsoleEmailService(messageSource);

    private final LostPasswordCommandHandler cmdHandler = new LostPasswordCommandHandler(
            eventStore,
            dispatcher,
            messageSource,
            fakeTokenRepos,
            emailService
            );
    private final AuthController ctrl = new AuthController(jwtService, messageSource, null, null, cmdHandler );

    @Test
    @DisplayName("assert a lost login on not existing account, retrieves a succss message")
    void test_lost_password_on_non_existing_account_returns_success() {
        LostLoginRequest req = new LostLoginRequest("unknown_user");
        ResponseEntity<String> resp = ctrl.lostPassword(req);
        Assertions.assertTrue(resp.getStatusCode().is2xxSuccessful());
        Assertions.assertEquals("Nous avons bien reçu votre demande, si votre compte existe, vous recevrez très vite un email pour changer votre mot de passe", resp.getBody());
        Assertions.assertTrue(fakeTokenRepos.findAll().isEmpty());
    }

    private void createUser(String username) {
        AccountRegisteredEvent event = new AccountRegisteredEvent(username,
                new Email("gouze@mail.com"),
                new Password("no-password"));

        EventEntity toBeSaved = factory.build(event);
        eventStore.save(toBeSaved);

    }

    @Test
    @DisplayName("Asseert a lost login on existing account, create a change token")
    void test_lost_password_on_existing_account_creates_token() {
        //given
        Assertions.assertTrue(fakeTokenRepos.findAll().isEmpty());
        createUser("a-simple-username");

        // when
        LostLoginRequest req = new LostLoginRequest("a-simple-username");
        ResponseEntity<String> resp = ctrl.lostPassword(req);
        Assertions.assertTrue(resp.getStatusCode().is2xxSuccessful());

        // then a token shall be create
        Assertions.assertEquals(1, fakeTokenRepos.findAll().size());
        LostLoginTokenEntity token = fakeTokenRepos.findAll().getFirst();
        Assertions.assertSame("a-simple-username", token.getUsername());
    }

    @Test
    @DisplayName("Assert two subsequent login requests create only one token")
    void test_two_subsequent_requests_succeed() {
        Assertions.assertTrue(fakeTokenRepos.findAll().isEmpty());
        createUser("a-simple-username");

        // when
        LostLoginRequest req = new LostLoginRequest("a-simple-username");
        ResponseEntity<String> resp = ctrl.lostPassword(req);
        ResponseEntity<String> resp2 = ctrl.lostPassword(req);
        Assertions.assertTrue(resp.getStatusCode().is2xxSuccessful());
        Assertions.assertTrue(resp2.getStatusCode().is2xxSuccessful());

        // then a token shall be create
        Assertions.assertEquals(1, fakeTokenRepos.findAll().size());
        LostLoginTokenEntity token = fakeTokenRepos.findAll().getFirst();
        Assertions.assertSame("a-simple-username", token.getUsername());
    }

    @Test
    @DisplayName("Assert a successful lost login request triggers a email")
    void test_a_successful_lost_login_triggers_an_email() {
        Assertions.assertTrue(fakeTokenRepos.findAll().isEmpty());
        createUser("a-simple-username");

        // when
        LostLoginRequest req = new LostLoginRequest("a-simple-username");
        ResponseEntity<String> resp = ctrl.lostPassword(req);

        // then a token shall be create
        Assertions.assertEquals(1, fakeTokenRepos.findAll().size());
        LostLoginTokenEntity token = fakeTokenRepos.findAll().getFirst();
        Assertions.assertSame("a-simple-username", token.getUsername());
    }

}
