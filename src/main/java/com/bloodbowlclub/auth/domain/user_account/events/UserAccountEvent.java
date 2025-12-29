package com.bloodbowlclub.auth.domain.user_account.events;

import com.bloodbowlclub.auth.domain.user_account.BaseUserAccount;
import com.bloodbowlclub.lib.domain.events.DomainEvent;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public abstract class UserAccountEvent extends DomainEvent {
    private BaseUserAccount userAccount;

    public UserAccountEvent(BaseUserAccount userAccount) {
        super(userAccount.getId());
        this.userAccount = userAccount;
    }

}
