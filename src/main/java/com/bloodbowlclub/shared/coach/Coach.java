package com.bloodbowlclub.shared.coach;

import com.bloodbowlclub.auth.domain.user_account.values.Email;
import com.bloodbowlclub.lib.domain.AggregateRoot;
import com.bloodbowlclub.lib.domain.events.DomainEvent;
import com.bloodbowlclub.lib.services.Result;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;

import java.util.Date;

public class Coach extends AggregateRoot {

    @NotNull
    @Valid
    private final CoachID coachId;
    @NotNull
    @Valid
    private final CoachName coachName;
    @NotNull
    @Valid
    private final Email email;
    @NotNull
    @Past
    @Valid
    private final Date createdAt;


    public Coach(
            CoachID entityId,
            CoachName coachName,
            Email email,
            Date createdAt
    ) {
        this.coachId = entityId;
        this.coachName = coachName;
        this.email = email;
        this.createdAt = createdAt;
    }

    @Override
    public Result<AggregateRoot> apply(DomainEvent event) {
        return null;
    }

    @Override
    public String getId() {
        return coachId.toString();
    }
}
