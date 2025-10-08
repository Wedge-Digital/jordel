package com.auth.domain.user_account;

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

    public ActiveUserAccount() {
    }

    public boolean login(String password){
        this.lastLogin = new Date();
        return this.password.matches(password);
    }

    public boolean isActive() {
        return true;
    }
}
