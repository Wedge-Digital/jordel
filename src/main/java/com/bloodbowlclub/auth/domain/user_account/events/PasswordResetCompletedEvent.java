package com.bloodbowlclub.auth.domain.user_account.events;

import com.bloodbowlclub.auth.domain.user_account.BaseUserAccount;
import com.bloodbowlclub.auth.domain.user_account.values.Password;
import com.bloodbowlclub.auth.domain.user_account.values.PasswordResetToken;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import jakarta.validation.Valid;
import lombok.Getter;


@JsonTypeInfo(
        use = JsonTypeInfo.Id.CLASS,
        include = JsonTypeInfo.As.PROPERTY,
        property = "@class"
)
@Getter
public class PasswordResetCompletedEvent extends UserAccountEvent {
    Password newPassword;

    public PasswordResetCompletedEvent(BaseUserAccount userAccount, Password newPassword) {
        super(userAccount);
        this.newPassword = newPassword;
    }
}
