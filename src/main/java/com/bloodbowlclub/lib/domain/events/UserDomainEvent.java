package com.bloodbowlclub.lib.domain.events;

import com.bloodbowlclub.auth.domain.user_account.values.Username;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import lombok.Data;
import lombok.Getter;

@JsonTypeInfo(
        use = JsonTypeInfo.Id.CLASS,
        include = JsonTypeInfo.As.PROPERTY,
        property = "@class"
)
@Data
@Getter
public abstract class UserDomainEvent extends DomainEvent {

    private Username createdBy;

    public UserDomainEvent(String agregateId, Username createdBy) {
        super(agregateId);
        this.createdBy = createdBy;
    }

}