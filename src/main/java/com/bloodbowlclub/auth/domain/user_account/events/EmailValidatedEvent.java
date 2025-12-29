package com.bloodbowlclub.auth.domain.user_account.events;

import com.bloodbowlclub.auth.domain.user_account.BaseUserAccount;
import com.bloodbowlclub.lib.domain.AggregateRoot;
import com.bloodbowlclub.lib.services.result.Result;
import lombok.NoArgsConstructor;

@NoArgsConstructor
public class EmailValidatedEvent extends UserAccountEvent {
    public EmailValidatedEvent(BaseUserAccount userAccount) {
        super(userAccount);
    }

    @Override
    public Result<AggregateRoot> applyTo(AggregateRoot aggregate) {
        return aggregate.apply(this);
    }
}
