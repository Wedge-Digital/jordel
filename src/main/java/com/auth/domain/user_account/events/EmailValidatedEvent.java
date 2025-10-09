package com.auth.domain.user_account.events;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.shared.domain.events.DomainEvent;

import java.util.Date;

@JsonTypeInfo(
        use = JsonTypeInfo.Id.CLASS,
        include = JsonTypeInfo.As.PROPERTY,
        property = "@class"
)
public class EmailValidatedEvent extends DomainEvent {

    private Date validatedAt;

    public EmailValidatedEvent(String userId) {
        super(userId);
        this.validatedAt = new Date();
    }

    public Date getValidatedAt() {
        return validatedAt;
    }
}
