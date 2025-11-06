package com.bloodbowlclub.auth.domain.user_account;

import com.bloodbowlclub.auth.domain.user_account.commands.RegisterAccountCommand;
import com.bloodbowlclub.auth.domain.user_account.commands.ValidateEmailCommand;
import com.bloodbowlclub.auth.domain.user_account.events.AccountRegisteredEvent;
import com.bloodbowlclub.auth.domain.user_account.events.EmailValidatedEvent;
import com.bloodbowlclub.auth.domain.user_account.values.*;
import com.bloodbowlclub.lib.domain.AggregateRoot;
import com.bloodbowlclub.lib.services.Result;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.bloodbowlclub.lib.services.ResultMap;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.experimental.SuperBuilder;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@JsonTypeInfo(
        use = JsonTypeInfo.Id.CLASS,
        include = JsonTypeInfo.As.PROPERTY,
        property = "@class"
)
@Data
@SuperBuilder
public class DraftUserAccount extends AggregateRoot {

    @Valid
    protected Username username;

    @Valid
    protected Email email;

    @NotNull
    protected Password password;

    @NotNull
    @Size(min = 1, max = 5)
    protected List<UserRole> roles;

    public DraftUserAccount(String username) {
        this.username = new Username(username);
    }

    public ResultMap<Void> registerSimpleUser(RegisterAccountCommand command) {
        this.email = new Email(command.getEmail());
        this.password = new Password(command.getPassword());
        this.roles = List.of(UserRole.SIMPLE_USER);

        if (isValid()) {
            AccountRegisteredEvent registeredEvent = new AccountRegisteredEvent(
                    command.getUsername(),
                    email,
                    password);
            this.addEvent(registeredEvent);
            return ResultMap.success(null);
        }
        return validationErrors();
    }

    public void validate(ValidateEmailCommand command){
        EmailValidatedEvent accountValidatedEvent = new EmailValidatedEvent(this.getId(), this.getUsername());
        this.addEvent(accountValidatedEvent);
    }

    @Override
    public String getId() {
        return this.username.toString();
    }

    public Result<AggregateRoot> apply(AccountRegisteredEvent event) {
        this.username = new Username(event.getAggregateId());
        this.email = event.getEmail();
        this.password = event.getPassword();
        return Result.success(this);
    }

    public Result<AggregateRoot> apply(EmailValidatedEvent event) {
        ActiveUserAccount active = new ActiveUserAccount(this);
        active.setValidatedAt(Date.from(event.getTimeStampedAt()));
        return Result.success(active);
    }

}
