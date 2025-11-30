package com.bloodbowlclub.auth.domain.user_account;

import com.bloodbowlclub.auth.domain.user_account.commands.RegisterAccountCommand;
import com.bloodbowlclub.auth.domain.user_account.commands.ValidateEmailCommand;
import com.bloodbowlclub.auth.domain.user_account.events.AccountRegisteredEvent;
import com.bloodbowlclub.auth.domain.user_account.events.EmailValidatedEvent;
import com.bloodbowlclub.auth.domain.user_account.events.PasswordResetStartedEvent;
import com.bloodbowlclub.auth.domain.user_account.events.UserLoggedEvent;
import com.bloodbowlclub.auth.domain.user_account.values.*;
import com.bloodbowlclub.lib.domain.AggregateRoot;
import com.bloodbowlclub.lib.services.result.ErrorCode;
import com.bloodbowlclub.lib.services.result.Result;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.bloodbowlclub.lib.services.result.ResultMap;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.util.Date;
import java.util.List;

@EqualsAndHashCode(callSuper = true)
@JsonTypeInfo(
        use = JsonTypeInfo.Id.CLASS,
        include = JsonTypeInfo.As.PROPERTY,
        property = "@class"
)
@Data
@SuperBuilder
@NoArgsConstructor
public class BaseUserAccount extends AggregateRoot {

    @Valid
    @NotNull
    protected Username username;

    @Valid
    @NotNull
    protected Email email;

    @NotNull
    protected Password password;

    @NotNull
    @Size(min = 1, max = 5)
    protected List<UserRole> roles;

    public BaseUserAccount(String username) {
        this.username = new Username(username);
    }

    //===============================================================================================================
    //
    // Méthodes métier
    //
    //===============================================================================================================

    public ResultMap<Void> registerSimpleUser(RegisterAccountCommand command) {
        this.email = new Email(command.getEmail());
        this.password = new Password(command.getPassword());
        this.roles = List.of(UserRole.SIMPLE_USER);

        if (isValid()) {
            AccountRegisteredEvent registeredEvent = new AccountRegisteredEvent(this);
            this.addEvent(registeredEvent);
            return ResultMap.success(null);
        }
        return validationErrors();
    }

    public void validate(ValidateEmailCommand command){
        EmailValidatedEvent accountValidatedEvent = new EmailValidatedEvent(this);
        this.addEvent(accountValidatedEvent);
    }

    public Result<Void> login(String password) {
        if (this.password == null){
            return Result.failure(null, ErrorCode.BAD_REQUEST);
        }
        Result<Void> loginSuccess = this.password.matches(password);
        if (loginSuccess.isFailure()) {
            return loginSuccess;
        }
        UserLoggedEvent  userLoggedEvent = new UserLoggedEvent(this);
        this.addEvent(userLoggedEvent);
        return loginSuccess;
    }

    public boolean canResetPassword() {
        return false;
    }

    public ResultMap<Void> startResetPassword() {
        PasswordResetStartedEvent event = new PasswordResetStartedEvent(this);
        this.addEvent(event);
        return ResultMap.success(null);
    }

    public ResultMap<Void> completeResetPassword(PasswordResetToken token, Password newPassword) {
        return ResultMap.failure("UserAccount", "", ErrorCode.BAD_REQUEST);
    }

    @Override
    @JsonIgnore
    public String getId() {
        return this.username.toString();
    }


    //===============================================================================================================
    //
    // Application des évènements
    //
    //===============================================================================================================

    public Result<AggregateRoot> apply(PasswordResetStartedEvent event) {
        WaitingPasswordResetUserAccount userAccount = new WaitingPasswordResetUserAccount(this, event.getPasswordToken());
        return Result.success(userAccount);
    }

    public Result<AggregateRoot> apply(AccountRegisteredEvent event) {
        this.username = new Username(event.getAggregateId());
        this.email = event.getUserAccount().getEmail();
        this.password = event.getUserAccount().getPassword();
        this.roles = List.of(UserRole.SIMPLE_USER);
        return Result.success(this);
    }

    public Result<AggregateRoot> apply(EmailValidatedEvent event) {
        ActiveUserAccount active = new ActiveUserAccount(this);
        active.setValidatedAt(Date.from(event.getTimeStampedAt()));
        return Result.success(active);
    }

    public Result<AggregateRoot> apply(UserLoggedEvent event) {
        ActiveUserAccount active = new ActiveUserAccount(this);
        active.setLastLogin(Date.from(event.getTimeStampedAt()));
        return Result.success(active);
    }

}
