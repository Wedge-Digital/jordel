package com.bloodbowlclub.shared.coach;

import com.bloodbowlclub.auth.domain.user_account.values.Email;
import com.bloodbowlclub.lib.domain.AggregateRoot;
import com.bloodbowlclub.lib.domain.events.DomainEvent;
import com.bloodbowlclub.lib.services.result.Result;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;

import java.time.LocalDateTime;

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
    private final LocalDateTime createdAt;


    public Coach(
            CoachID entityId,
            CoachName coachName,
            Email email,
            LocalDateTime createdAt
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
