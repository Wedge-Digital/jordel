package com.bloodbowlclub.auth.domain.user_account.events;

import com.bloodbowlclub.auth.domain.user_account.BaseUserAccount;
import com.bloodbowlclub.auth.domain.user_account.values.Email;
import com.bloodbowlclub.auth.domain.user_account.values.Password;
import com.bloodbowlclub.auth.domain.user_account.values.Username;
import com.bloodbowlclub.lib.domain.events.UserDomainEvent;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.bloodbowlclub.lib.domain.events.DomainEvent;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.experimental.SuperBuilder;

import java.util.Date;

@JsonTypeInfo(
        use = JsonTypeInfo.Id.CLASS,
        include = JsonTypeInfo.As.PROPERTY,
        property = "@class"
)
public class EmailValidatedEvent extends UserAccountEvent {
    public EmailValidatedEvent(BaseUserAccount userAccount) {
        super(userAccount);
    }
}
