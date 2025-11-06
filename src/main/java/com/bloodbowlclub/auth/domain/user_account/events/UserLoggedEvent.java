package com.bloodbowlclub.auth.domain.user_account.events;

import com.bloodbowlclub.auth.domain.user_account.values.Username;
import com.bloodbowlclub.lib.domain.events.UserDomainEvent;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import lombok.Data;


@JsonTypeInfo(
        use = JsonTypeInfo.Id.CLASS,
        include = JsonTypeInfo.As.PROPERTY,
        property = "@class"
)
@Data
public class UserLoggedEvent extends UserDomainEvent {

    public UserLoggedEvent(String agregateId, Username createdBy) {
        super(agregateId, createdBy);
    }
}
