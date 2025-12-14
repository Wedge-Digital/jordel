package com.bloodbowlclub.auth.io.web;

import com.bloodbowlclub.auth.domain.user_account.BaseUserAccount;
import com.bloodbowlclub.auth.domain.user_account.events.AccountRegisteredEvent;
import com.bloodbowlclub.auth.domain.user_account.events.PasswordResetStartedEvent;
import com.bloodbowlclub.auth.domain.user_account.values.Email;
import com.bloodbowlclub.auth.domain.user_account.values.Password;
import com.bloodbowlclub.auth.domain.user_account.values.Username;
import com.bloodbowlclub.lib.persistance.event_store.EventEntity;
import com.bloodbowlclub.lib.persistance.event_store.EventEntityFactory;
import com.bloodbowlclub.lib.persistance.event_store.EventStore;

public class UserTestUtils {

    private static EventEntityFactory factory = new EventEntityFactory();

    public static BaseUserAccount createUser(String username, EventStore eventStore) {
        BaseUserAccount userAccount = BaseUserAccount.builder()
                .username(new Username(username))
                .email(new Email("gouze@mail.com"))
                .password(new Password("no-password"))
                .build();
        AccountRegisteredEvent event = new AccountRegisteredEvent(userAccount);
        EventEntity toBeSaved = factory.build(event);
        eventStore.save(toBeSaved);
        return userAccount;
    }


    public static String startResetPassword(BaseUserAccount userAccount, EventStore eventStore) {
        PasswordResetStartedEvent event = new PasswordResetStartedEvent(userAccount);
        EventEntity toBeSaved = factory.build(event);
        eventStore.save(toBeSaved);
        return event.getPasswordToken().toString();
    }


    public static String createPasswordToken(String username) {
        return "";
    }
}
