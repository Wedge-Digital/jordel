package com.bloodbowlclub.auth.domain.user_account.events;

import com.bloodbowlclub.lib.domain.events.DomainEvent;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import lombok.Builder;
import lombok.Data;
import lombok.experimental.SuperBuilder;

import java.util.Date;

@JsonTypeInfo(
        use = JsonTypeInfo.Id.CLASS,
        include = JsonTypeInfo.As.PROPERTY,
        property = "@class"
)
@Data
@SuperBuilder
public class UserLoggedEvent extends DomainEvent {
    private Date loggedAt;

    public UserLoggedEvent(String agregateId, String createdBy,  Date loggedAt) {
        super(agregateId, createdBy);
        this.loggedAt = loggedAt;
    }
}
