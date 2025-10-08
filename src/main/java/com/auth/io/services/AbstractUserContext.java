package com.auth.io.services;

import com.auth.domain.user_account.ActiveUserAccount;

public abstract class AbstractUserContext {

    public abstract ActiveUserAccount getCurrentUser();
}
