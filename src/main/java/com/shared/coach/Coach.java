package com.shared.coach;

import com.auth.domain.Email;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;

import java.util.Date;

public class Coach {

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
}
