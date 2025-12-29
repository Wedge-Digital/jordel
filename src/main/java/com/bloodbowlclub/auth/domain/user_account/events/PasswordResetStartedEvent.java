package com.bloodbowlclub.auth.domain.user_account.events;

import com.bloodbowlclub.auth.domain.user_account.BaseUserAccount;
import com.bloodbowlclub.auth.domain.user_account.values.PasswordResetToken;
import com.bloodbowlclub.lib.domain.AggregateRoot;
import com.bloodbowlclub.lib.services.result.Result;
import jakarta.validation.Valid;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class PasswordResetStartedEvent extends UserAccountEvent {
    @Valid
    private PasswordResetToken passwordToken;


    public PasswordResetStartedEvent(BaseUserAccount userAccount) {
        super(userAccount);
        this.passwordToken = PasswordResetToken.New();
    }

    @Override
    public Result<AggregateRoot> applyTo(AggregateRoot aggregate) {
        return aggregate.apply(this);
    }
}
