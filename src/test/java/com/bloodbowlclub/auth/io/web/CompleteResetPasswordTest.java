package com.bloodbowlclub.auth.io.web;

import com.bloodbowlclub.auth.domain.user_account.BaseUserAccount;
import com.bloodbowlclub.auth.io.services.JwtService;
import com.bloodbowlclub.auth.io.web.login.LoginRequest;
import com.bloodbowlclub.auth.io.web.requests.CompleteResetPasswordRequest;
import com.bloodbowlclub.auth.use_cases.CompleteResetPasswordCommandHandler;
import com.bloodbowlclub.auth.use_cases.LoginCommandHandler;
import com.bloodbowlclub.lib.persistance.event_store.EventEntity;
import com.bloodbowlclub.lib.persistance.event_store.EventEntityFactory;
import com.bloodbowlclub.lib.persistance.event_store.fake.FakeEventStore;
import com.bloodbowlclub.lib.services.result.exceptions.BadRequest;
import com.bloodbowlclub.lib.services.result.exceptions.NotFound;
import com.bloodbowlclub.lib.tests.TestCase;
import com.bloodbowlclub.shared.FakeMailService;
import com.bloodbowlclub.test_utilities.dispatcher.FakeEventDispatcher;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.HashMap;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertThrows;

public class CompleteResetPasswordTest extends TestCase {

    private final JwtService jwtService = new JwtService(
            "Vk2B1V8nR3y6oTzP4wq9ZfL8dC1mQ7sN0hJ3Xk5aR9uE2pC6vF0gT8bY2nL4rW6",
            10,
            100L,
            messageSource);

    FakeEventDispatcher dispatcher = new FakeEventDispatcher();
    FakeEventStore eventStore = new FakeEventStore();

    EventEntityFactory factory = new EventEntityFactory();

    FakeMailService fakeEmailService = new FakeMailService(messageSource);

    private final CompleteResetPasswordCommandHandler cmdHandler = new CompleteResetPasswordCommandHandler(
            eventStore,
            dispatcher,
            messageSource
            );

    private final LoginCommandHandler loginHandler = new LoginCommandHandler(
            eventStore,
            dispatcher,
            messageSource
    );

    private final AuthController ctrl = new AuthController(
            jwtService,
            loginHandler,
            null,
            null,
            cmdHandler);


    @Test
    @DisplayName("Assert a complete reset password, on an unknow userAccount returns an error")
    void test_complete_on_unknown_user_account_fails() {
        CompleteResetPasswordRequest req = CompleteResetPasswordRequest.builder()
                .username("no-username")
                .new_password("new-password")
                .token("an-unknown-token")
                .build();

        // when
        NotFound ex = assertThrows(NotFound.class, () ->  ctrl.completeResetPassword(req));

        // then
        HashMap<String, String> errors = new HashMap<>();
        errors.put("username", "Le compte utilisateur no-username n'existe pas");
        Assertions.assertEquals(errors, ex.getErrors());
    }

    @Test
    @DisplayName("Assert a complete reset password, without having started it, should fail")
    void test_assert_complete_without_start_should_fail() {
        // given
        UserTestUtils.createUser("another-user", eventStore);
        // when
        CompleteResetPasswordRequest req = CompleteResetPasswordRequest.builder()
                .username("another-user")
                .new_password("new-password")
                .token("an-unknown-token")
                .build();

        BadRequest ex = assertThrows(BadRequest.class, () ->  ctrl.completeResetPassword(req));

        // then
        HashMap<String, String> errors = new HashMap<>();
        errors.put("UserAccount", "Impossible de changer le mot de passe de l'utilisateur another-user");
        Assertions.assertEquals(errors, ex.getErrors());
    }

    @Test
    @DisplayName("Assert a complete reset password, with a bad token, should fail")
    void test_complete_on_unknown_token_fails() {
        // given
        BaseUserAccount userAcount = UserTestUtils.createUser("another-user", eventStore);
        UserTestUtils.startResetPassword(userAcount, eventStore);

        // when
        CompleteResetPasswordRequest req = CompleteResetPasswordRequest.builder()
                .username("another-user")
                .new_password("new-password")
                .token("an-unknown-token")
                .build();

        BadRequest ex = assertThrows(BadRequest.class, () ->  ctrl.completeResetPassword(req));
            // then
        HashMap<String, String> errors = new HashMap<>();
        errors.put("UserAccount", "Impossible de changer le mot de passe de l'utilisateur another-user");
        Assertions.assertEquals(errors, ex.getErrors());
    }

    @Test
    @DisplayName("Assert a complete reset password, is successful when the account has started a reset pwd and the token is good")
    void test_complete_reset_password_succeed() {
        BaseUserAccount userAcount = UserTestUtils.createUser("another-user", eventStore);
        String resetToken = UserTestUtils.startResetPassword(userAcount, eventStore);

        // when
        CompleteResetPasswordRequest req = CompleteResetPasswordRequest.builder()
                .username("another-user")
                .new_password("new-password")
                .token(resetToken)
                .build();
        ResponseEntity<Void> res = ctrl.completeResetPassword(req);
        Assertions.assertEquals(HttpStatus.OK, res.getStatusCode());

        List<EventEntity> allEvent = eventStore.findAll();
        Assertions.assertEquals(3, allEvent.size());
    }


    @Test
    @DisplayName("check a user can login with his new password, after having resetted his password")
    void test_check_a_user_can_login_with_new_password() {
        // given
        jwtService.init();
        BaseUserAccount userAcount = UserTestUtils.createUser("another-user", eventStore);
        String resetToken = UserTestUtils.startResetPassword(userAcount, eventStore);
        CompleteResetPasswordRequest req = CompleteResetPasswordRequest.builder()
                .username("another-user")
                .new_password("resetted-password")
                .token(resetToken)
                .build();
        ResponseEntity<Void> res = ctrl.completeResetPassword(req);
        Assertions.assertEquals(HttpStatus.OK, res.getStatusCode());

        //When
        LoginRequest loginRequest = new LoginRequest(
               "another-user",
                "resetted-password"
        );

        //then
        ResponseEntity<?> loginResult = ctrl.login(loginRequest);
        Assertions.assertEquals(HttpStatus.OK, loginResult.getStatusCode());
        JwtTokensResponse castedResponse = (JwtTokensResponse) loginResult.getBody();
        Assertions.assertFalse(castedResponse.getAccessToken().isEmpty());
    }

}
