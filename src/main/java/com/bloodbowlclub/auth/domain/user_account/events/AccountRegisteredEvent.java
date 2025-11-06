package com.bloodbowlclub.auth.domain.user_account.events;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.bloodbowlclub.lib.domain.events.DomainEvent;
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
public class AccountRegisteredEvent extends DomainEvent {
    private String  username;
    private String  email;
    private String  password;
    private Date createdAt;
}
