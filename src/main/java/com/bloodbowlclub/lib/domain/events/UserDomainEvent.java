package com.bloodbowlclub.lib.domain.events;

import com.bloodbowlclub.auth.domain.user_account.values.Username;
import lombok.Data;
import lombok.Getter;

@Data
@Getter
public abstract class UserDomainEvent extends DomainEvent {

    private Username createdBy;

    public UserDomainEvent(String agregateId, Username createdBy) {
        super(agregateId);
        this.createdBy = createdBy;
    }

}