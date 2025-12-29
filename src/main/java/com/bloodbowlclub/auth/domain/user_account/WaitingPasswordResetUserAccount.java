package com.bloodbowlclub.auth.domain.user_account;

import com.bloodbowlclub.auth.domain.user_account.events.PasswordResetCompletedEvent;
import com.bloodbowlclub.auth.domain.user_account.values.Password;
import com.bloodbowlclub.auth.domain.user_account.values.PasswordResetToken;
import com.bloodbowlclub.lib.domain.AggregateRoot;
import com.bloodbowlclub.lib.services.result.ErrorCode;
import com.bloodbowlclub.lib.services.result.Result;
import com.bloodbowlclub.lib.services.result.ResultMap;
import jakarta.validation.Valid;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@EqualsAndHashCode(callSuper = true)
@Data
@NoArgsConstructor
public class WaitingPasswordResetUserAccount extends ActiveUserAccount {

    @Valid
    private PasswordResetToken passwordToken;

    public WaitingPasswordResetUserAccount(BaseUserAccount activeUserAccount, PasswordResetToken passwordToken) {
        super(activeUserAccount);
        this.passwordToken = passwordToken;
    }

    @Override
    public ResultMap<Void> startResetPassword() {
        return ResultMap.success(null);
    }

    @Override
    public ResultMap<Void> completeResetPassword(PasswordResetToken candidateToken, Password newPassword) {
        if (candidateToken.equals(this.passwordToken)) {
            PasswordResetCompletedEvent passwordResetCompletedEvent = new PasswordResetCompletedEvent(this, newPassword);
            this.addEvent(passwordResetCompletedEvent);
            return ResultMap.success(null);
        }
        return ResultMap.failure("UserAccount", "", ErrorCode.BAD_REQUEST);
    }

    public boolean canResetPassword() {
        return true;
    }

    public Result<AggregateRoot> apply(PasswordResetCompletedEvent event) {
        ActiveUserAccount active = ActiveUserAccount.builder()
                .username(this.username)
                .email(this.email)
                .password(event.getNewPassword())
                .roles(this.roles)
                .build();
        return Result.success(active);
    }

}
