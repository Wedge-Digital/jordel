package com.auth.domain;

import com.shared.coach.CoachID;
import com.shared.coach.CoachName;
import com.shared.coach.Coach;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.util.Date;
import java.util.Locale;

public class User extends Coach {

    @NotEmpty
    private final EncryptedPassword encryptedPassword;
    @NotNull
    private final Boolean isActive;

    private final Date lastLogin;

    @NotNull
    @Valid
    private final Locale lang;

    public User(
            CoachID entityId,
            CoachName coachName,
            Email email,
            EncryptedPassword password,
            Boolean isActive,
            Locale lang,
            Date createdAt,
            Date lastLogin
    ) {
        super(entityId, coachName, email, createdAt);
        this.encryptedPassword = password;
        this.isActive = isActive;
        this.lastLogin = lastLogin;
        this.lang = lang;
    }
}
