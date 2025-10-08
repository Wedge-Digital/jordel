package com.auth.io.services;

import com.auth.domain.user_account.ActiveUserAccount;
import com.shared.services.Result;

public abstract class AbstractAuthService {

    public abstract Result<ActiveUserAccount> isUserIsKnownAndActive(String userId);
}
