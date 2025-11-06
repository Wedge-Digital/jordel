package com.bloodbowlclub.auth.domain.user_account;

import com.bloodbowlclub.auth.domain.user_account.commands.RegisterCommand;
import com.bloodbowlclub.auth.domain.user_account.commands.ValidateEmailCommand;
import com.bloodbowlclub.auth.domain.user_account.events.AccountRegisteredEvent;
import com.bloodbowlclub.auth.domain.user_account.events.EmailValidatedEvent;
import com.bloodbowlclub.auth.domain.user_account.values.*;
import com.bloodbowlclub.auth.domain.user_account.values.*;
import com.bloodbowlclub.lib.domain.AggregateRoot;
import com.bloodbowlclub.lib.domain.events.DomainEvent;
import com.bloodbowlclub.lib.services.Result;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.bloodbowlclub.lib.services.ResultMap;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.NoArgsConstructor;
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
@NoArgsConstructor
public class DraftUserAccount extends AggregateRoot {

    @Valid
    @NotNull
    protected UserAccountID userId;

    @Valid
    protected Username username;

    @Valid
    protected Email email;

    @NotNull
    protected Password password;

    @NotNull
    @Valid
    @Past
    protected Date createdAt;

    @NotNull
    @Size(min = 1, max = 5)
    protected List<UserRole> roles;

    public DraftUserAccount(String userId) {
        this.userId = new UserAccountID(userId);
    }

    public ResultMap<Void> register(RegisterCommand command) {
        this.userId =new UserAccountID(command.getUserId());
        this.username = new Username(command.getUsername());
        this.email = new Email(command.getEmail());
        this.password = new Password(command.getPassword());
        this.createdAt = new Date();

        if (isValid()) {
            AccountRegisteredEvent registeredEvent = AccountRegisteredEvent.builder()
                    .username(command.getUsername())
                    .email(command.getEmail())
                    .password(command.getPassword())
                    .createdAt(this.createdAt)
                    .build();
            this.addEvent(registeredEvent);
            return ResultMap.success(null);
        }
        return validationErrors();
    }

    public void confirmEmail(ValidateEmailCommand command){
        // une confirmation arrivée là ne peux plus échouer
        Date now = new Date();
        EmailValidatedEvent accountValidatedEvent = new EmailValidatedEvent(now);
        this.addEvent(accountValidatedEvent);
    }

    @Override
    public String getId() {
        return this.userId.toString();
    }

    public void setId(String id) {
        this.userId = new UserAccountID(id);
    }

    public Result<AggregateRoot> apply(AccountRegisteredEvent event) {
        this.username = new Username(event.getUsername());
        this.email = new Email(event.getEmail());
        this.password = new Password(event.getPassword());
        this.createdAt = event.getCreatedAt();
        return Result.success(this);
    }

    public Result<AggregateRoot> apply(EmailValidatedEvent event) {
        ActiveUserAccount casted = (ActiveUserAccount) this;
        casted.setValidatedAt(event.getValidatedAt());
        return Result.success(casted);
    }

}
