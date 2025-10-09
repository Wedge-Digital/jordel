package com.auth.domain.user_account;

import com.auth.domain.user_account.commands.RegisterCommand;
import com.auth.domain.user_account.commands.ValidateEmailCommand;
import com.auth.domain.user_account.events.AccountRegisteredEvent;
import com.auth.domain.user_account.events.EmailValidatedEvent;
import com.auth.domain.user_account.values.*;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.shared.services.ResultMap;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Size;

import java.util.*;

@JsonTypeInfo(
        use = JsonTypeInfo.Id.CLASS,
        include = JsonTypeInfo.As.PROPERTY,
        property = "@class"
)
public class DraftUserAccount extends AbstractUserAccount {

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
    protected List<UserRole> roles = new ArrayList<>(){{add(UserRole.SIMPLE_USER);}};

    public DraftUserAccount() {
        super();
    }

    public DraftUserAccount(String id, String username, String email, String password, Date createdAt)
    {
        super();
        this.userId =new UserAccountID(id);
        this.username = new Username(username);
        this.email = new Email(email);
        this.password = new Password(password);
        this.createdAt = createdAt;
    }

    public ResultMap<String> register(RegisterCommand command) {
        this.userId =new UserAccountID(command.getUserId());
        this.username = new Username(command.getUsername());
        this.email = new Email(command.getEmail());
        this.password = new Password(command.getPassword());
        this.createdAt = new Date();

        if (isValid()) {
            AccountRegisteredEvent registeredEvent = new AccountRegisteredEvent(
                    this.userId.toString(),
                    this.username.toString(),
                    this.email.toString(),
                    this.password.toString(),
                    this.createdAt);
            this.addEvent(registeredEvent);
            return ResultMap.success(this.getId());
        }
        return validationErrors();
    }

    @Override
    public void confirmEmail(ValidateEmailCommand command){
        // une confirmation arrivée là ne peux plus échouer
        EmailValidatedEvent accountValidatedEvent = new EmailValidatedEvent(
                this.userId.toString()
        );
        this.addEvent(accountValidatedEvent);
    }

    @Override
    public boolean isActivated() {
        return false;
    }

    public Username getUsername() {
        return username;
    }

    public Email getEmail() {
        return email;
    }

    public Password getPassword() {
        return password;
    }

    public List<UserRole> getRoles() {
        return roles;
    }

    @Override
    public String getId() {
        return this.userId.toString();
    }

    public void setId(String id) {
        this.userId = new UserAccountID(id);
    }

    public Date getCreatedAt() {
        return createdAt;
    }


}
