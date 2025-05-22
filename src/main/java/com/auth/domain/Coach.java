package com.auth.domain;

import java.util.Date;

public class Coach {
    private final EntityID entityId;
    private final CoachName coachName;
    private final EmailAddress email;
    private final EncryptedPassword encryptedPassword;
    private final Boolean isActive;
    private final Date createdAt;
    private final Date lastLogin;


    public Coach(
            EntityID entityId,
            CoachName coachName,
            EmailAddress email,
            EncryptedPassword password,
            Boolean isActive,
            Date createdAt,
            Date lastLogin
    ) {
        this.entityId = entityId;
        this.coachName = coachName;
        this.email = email;
        this.encryptedPassword = password;
        this.isActive = isActive;
        this.createdAt = createdAt;
        this.lastLogin = lastLogin;
    }
}
