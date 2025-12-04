package com.bloodbowlclub.auth.domain.user_account.events;

import com.bloodbowlclub.auth.domain.user_account.BaseUserAccount;
import com.bloodbowlclub.auth.domain.user_account.values.Email;
import com.bloodbowlclub.auth.domain.user_account.values.Password;
import com.bloodbowlclub.auth.domain.user_account.values.Username;
import com.bloodbowlclub.lib.domain.AggregateRoot;
import com.bloodbowlclub.lib.services.result.Result;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.bloodbowlclub.lib.domain.events.DomainEvent;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.util.Date;

@JsonTypeInfo(
        use = JsonTypeInfo.Id.CLASS,
        include = JsonTypeInfo.As.PROPERTY,
        property = "@class"
)
@Getter
@NoArgsConstructor
public class AccountRegisteredEvent extends UserAccountEvent {
    public AccountRegisteredEvent(BaseUserAccount userAccount) {
        super(userAccount);
    }

    @Override
    public Result<AggregateRoot> applyTo(AggregateRoot aggregate) {
        return aggregate.apply(this);
    }
}
