package com.bloodbowlclub.auth.domain.user_account.events;

import com.bloodbowlclub.auth.domain.user_account.BaseUserAccount;
import com.bloodbowlclub.auth.domain.user_account.values.Email;
import com.bloodbowlclub.auth.domain.user_account.values.Password;
import com.bloodbowlclub.lib.domain.events.DomainEvent;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import lombok.Getter;
import lombok.NoArgsConstructor;

@JsonTypeInfo(
        use = JsonTypeInfo.Id.CLASS,
        include = JsonTypeInfo.As.PROPERTY,
        property = "@class"
)
@Getter
@NoArgsConstructor
public abstract class UserAccountEvent extends DomainEvent {
    private BaseUserAccount userAccount;

    public UserAccountEvent(BaseUserAccount userAccount) {
        super(userAccount.getId());
        this.userAccount = userAccount;
    }

}
