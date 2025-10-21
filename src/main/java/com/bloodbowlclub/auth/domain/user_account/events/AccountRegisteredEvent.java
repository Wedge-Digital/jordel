package com.bloodbowlclub.auth.domain.user_account.events;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.bloodbowlclub.lib.domain.events.DomainEvent;

import java.util.Date;

@JsonTypeInfo(
        use = JsonTypeInfo.Id.CLASS,
        include = JsonTypeInfo.As.PROPERTY,
        property = "@class"
)
public class AccountRegisteredEvent extends DomainEvent {
    private String  username;
    private String  email;
    private String  password;
    private Date createdAt;

    public AccountRegisteredEvent(String id, String username, String email, String password, Date createdAt) {
        super(id, id);
        this.username = username;
        this.email = email;
        this.password = password;
        this.createdAt = createdAt;
    }

    public String getUsername() {
        return username;
    }

    public String getEmail() {
        return email;
    }

    public String getPassword() {
        return password;
    }

    public Date getCreatedAt() {
        return createdAt;
    }
}
