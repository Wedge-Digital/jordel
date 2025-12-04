package com.bloodbowlclub.auth.domain.user_account.events;

import com.bloodbowlclub.auth.domain.user_account.BaseUserAccount;
import com.bloodbowlclub.auth.domain.user_account.values.Password;
import com.bloodbowlclub.auth.domain.user_account.values.PasswordResetToken;
import com.bloodbowlclub.lib.domain.AggregateRoot;
import com.bloodbowlclub.lib.services.result.Result;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import jakarta.validation.Valid;
import lombok.Getter;
import lombok.NoArgsConstructor;


@JsonTypeInfo(
        use = JsonTypeInfo.Id.CLASS,
        include = JsonTypeInfo.As.PROPERTY,
        property = "@class"
)
@Getter
@NoArgsConstructor
public class PasswordResetCompletedEvent extends UserAccountEvent {
    Password newPassword;

    public PasswordResetCompletedEvent(BaseUserAccount userAccount, Password newPassword) {
        super(userAccount);
        this.newPassword = newPassword;
    }

    @Override
    public Result<AggregateRoot> applyTo(AggregateRoot aggregate) {
        return aggregate.apply(this);
    }
}
