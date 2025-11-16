package com.bloodbowlclub.auth.domain.user_account;

import com.bloodbowlclub.auth.domain.user_account.events.AccountRegisteredEvent;
import com.bloodbowlclub.auth.domain.user_account.events.EmailValidatedEvent;
import com.bloodbowlclub.auth.domain.user_account.events.UserLoggedEvent;
import com.bloodbowlclub.auth.domain.user_account.values.Email;
import com.bloodbowlclub.auth.domain.user_account.values.Password;
import com.bloodbowlclub.auth.domain.user_account.values.Username;
import com.bloodbowlclub.lib.domain.AggregateRoot;
import com.bloodbowlclub.lib.domain.events.DomainEvent;
import com.bloodbowlclub.lib.services.Result;
import io.jsonwebtoken.lang.Assert;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Date;

public class UserAccountTest {

    private ArrayList<DomainEvent> events = new ArrayList<>();
    private AccountRegisteredEvent registration;
    private EmailValidatedEvent emailValidated;
    private final String email = "bagouze@me.com";

    private DraftUserAccount baseAccount;

    private void init_events(String username) {
        registration = new AccountRegisteredEvent(
                username,
                new Email(email),
                new Password("my_password")
        );
        events.add(registration);
        emailValidated = new EmailValidatedEvent(username, new Username("creator"));
        events.add(emailValidated);
    }


    @Test
    public void test_hydrate_user_account_from_registration_succeed() {
        //Given
        init_events("Bagouze");
        baseAccount = new DraftUserAccount("Bagouze");

        // When
        Result<AggregateRoot> hydratationResult = baseAccount.hydrate(events);

        // Then
        Assertions.assertTrue(hydratationResult.isSuccess());
        AggregateRoot hydrated = hydratationResult.getValue();
        Assertions.assertInstanceOf(ActiveUserAccount.class, hydrated);
        DraftUserAccount casted = (ActiveUserAccount) hydrated;

        Assertions.assertEquals("Bagouze", casted.getId());
        Assertions.assertEquals(email, casted.getEmail().toString());
    }

    @Test
    @DisplayName("Try to apply registration, then login, and then login, should succeed with latest login")
    void test_hydratation_successives() {
        registration = new AccountRegisteredEvent(
                "Bagouze",
                new Email(email),
                new Password("my_password")
        );
        events.add(registration);
        UserLoggedEvent userLoggedEvent = new UserLoggedEvent("Bagouze");
        events.add(userLoggedEvent);
        UserLoggedEvent userLoggedEvent2 = new UserLoggedEvent("Bagouze");
        events.add(userLoggedEvent2);

        baseAccount = new DraftUserAccount("Bagouze");

        // When
        Result<AggregateRoot> hydratationResult = baseAccount.hydrate(events);
        Assertions.assertTrue(hydratationResult.isSuccess());
        AggregateRoot hydrated = hydratationResult.getValue();
        Assertions.assertInstanceOf(ActiveUserAccount.class, hydrated);
        ActiveUserAccount casted = (ActiveUserAccount) hydrated;
        Assertions.assertNotNull(casted.getLastLogin());

    }

}
