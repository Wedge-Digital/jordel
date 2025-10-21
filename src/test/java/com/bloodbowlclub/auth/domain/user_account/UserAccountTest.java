package com.bloodbowlclub.auth.domain.user_account;

import com.bloodbowlclub.auth.domain.user_account.events.AccountRegisteredEvent;
import com.bloodbowlclub.auth.domain.user_account.events.EmailValidatedEvent;
import com.bloodbowlclub.lib.domain.events.DomainEvent;
import com.bloodbowlclub.lib.services.Result;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Date;

public class UserAccountTest {

    private ArrayList<DomainEvent> events = new ArrayList<>();
    private AccountRegisteredEvent registration;
    private EmailValidatedEvent emailValidated;
    private final String userId = "01K6TR3FQJRPBZMNN1TJP5D3YY";
    private final String email = "bagouze@me.com";

    private DraftUserAccount baseAccount = new DraftUserAccount();

    private void init_events() {
        registration = new AccountRegisteredEvent(
                userId,
                "Bagouze",
                email,
                "my_password",
                new Date()
        );
        events.add(registration);
        emailValidated = new EmailValidatedEvent(userId);
        events.add(emailValidated);
    }

    @Test
    public void test_hydrate_user_account_from_registration_succeed() {
        init_events();
        Result<AbstractUserAccount> hydratationResult = baseAccount.apply(registration);
        Assertions.assertTrue(hydratationResult.isSuccess());
        AbstractUserAccount hydrated = hydratationResult.getValue();
        Assertions.assertInstanceOf(DraftUserAccount.class, hydrated);
        DraftUserAccount casted = (DraftUserAccount) hydrated;
        Assertions.assertEquals(userId, casted.getId());
        Assertions.assertEquals(email, casted.getEmail().toString());
        Assertions.assertFalse(hydrated.isActivated());
    }

    @Test
    public void test_hydrate_user_account_from_email_validated_succeed() {
        init_events();
        Result<AbstractUserAccount> hydratationResult = baseAccount.applyAll(events);
        Assertions.assertTrue(hydratationResult.isSuccess());
        AbstractUserAccount hydrated = hydratationResult.getValue();
        Assertions.assertInstanceOf(ActiveUserAccount.class, hydrated);
        Assertions.assertTrue(hydrated.isActivated());
        ActiveUserAccount casted = (ActiveUserAccount) hydrated;
        Assertions.assertNotNull(casted.getCreatedAt());
    }

    @Test
    public void test_hydrate_in_reverse_order_from_email_validated_succeed() {
        init_events();
        Result<AbstractUserAccount> hydratationResult = baseAccount.applyAll(events.reversed());
        Assertions.assertTrue(hydratationResult.isSuccess());
        AbstractUserAccount hydrated = hydratationResult.getValue();
        Assertions.assertInstanceOf(ActiveUserAccount.class, hydrated);
        Assertions.assertTrue(hydrated.isActivated());
        ActiveUserAccount casted = (ActiveUserAccount) hydrated;
        Assertions.assertNotNull(casted.getCreatedAt());
    }
}
