package com.auth.domain.user_account;

import com.auth.domain.user_account.commands.ValidateEmailCommand;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import jakarta.validation.constraints.Past;

import java.util.Date;

@JsonTypeInfo(
        use = JsonTypeInfo.Id.CLASS,
        include = JsonTypeInfo.As.PROPERTY,
        property = "@class"
)
public class ActiveUserAccount extends DraftUserAccount {

    @Past
    private Date lastLogin;

    @Past
    private Date validatedAt;

    public ActiveUserAccount(DraftUserAccount draft, Date validatedAt) {
        super(draft.getId(),
                draft.getUsername().toString(),
                draft.getEmail().toString(),
                draft.getPassword().toString(),
                draft.getCreatedAt()
                );
        this.validatedAt = validatedAt;
        this.version = 1;

    }

    public boolean login(String password){
        this.lastLogin = new Date();
        return this.password.matches(password);
    }

    @Override
    public boolean isActivated() {
        return true;
    }

    @Override
    public void confirmEmail(ValidateEmailCommand command){
    }
}
