package com.auth.domain.user_account;

import com.auth.domain.user_account.commands.RegisterCommand;
import com.auth.domain.user_account.events.AccountRegisteredEvent;
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
    private UserAccountID userId;

    @Valid
    protected Username username;

    @Valid
    protected Email email;

    @NotNull
    protected Password password;
    @NotNull
    @Valid
    @Past
    private Date createdAt;

    @NotNull
    @Size(min = 1, max = 5)
    private List<UserRole> roles = new ArrayList<>(){{add(UserRole.SIMPLE_USER);}};

    public DraftUserAccount()
    {
        super();
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
}
