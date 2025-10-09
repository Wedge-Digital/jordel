package com.auth.domain.user_account;

import com.auth.domain.user_account.commands.ValidateEmailCommand;
import com.auth.domain.user_account.events.EmailValidatedEvent;
import com.auth.domain.user_account.values.*;
import com.shared.domain.AggregateRoot;
import com.shared.services.MessageSourceConfig;
import com.shared.services.ResultMap;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Size;
import org.apache.commons.lang3.LocaleUtils;

import java.util.Date;
import java.util.List;
import java.util.Locale;

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
