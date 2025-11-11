package com.bloodbowlclub.auth.domain.user_account;

import com.bloodbowlclub.auth.domain.user_account.commands.ValidateEmailCommand;
import com.bloodbowlclub.auth.domain.user_account.events.EmailValidatedEvent;
import com.bloodbowlclub.auth.domain.user_account.events.UserLoggedEvent;
import com.bloodbowlclub.auth.domain.user_account.values.Username;
import com.bloodbowlclub.lib.domain.AggregateRoot;
import com.bloodbowlclub.lib.services.Result;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Past;
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
public class ActiveUserAccount extends DraftUserAccount {

    @Past
    @Valid
    private Date lastLogin;

    @Past
    private Date validatedAt;

    public ActiveUserAccount(DraftUserAccount draftAccount) {
        super(draftAccount.getUsername().toString());
        this.email =  draftAccount.getEmail();
        this.password = draftAccount.getPassword();
        this.roles = draftAccount.getRoles();
    }

    public Result<AggregateRoot> apply(UserLoggedEvent event) {
        this.lastLogin = Date.from(event.getTimeStampedAt());
        return Result.success(this);
    }
}
