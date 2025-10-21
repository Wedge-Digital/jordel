package com.bloodbowlclub.auth.io.services;

import com.bloodbowlclub.auth.domain.user_account.ActiveUserAccount;

public abstract class AbstractUserContext {

    public abstract ActiveUserAccount getCurrentUser();
}
