package com.bloodbowlclub.auth.io.services;

import com.bloodbowlclub.auth.domain.user_account.ActiveUserAccount;
import com.bloodbowlclub.lib.services.result.Result;

public abstract class AbstractAuthService {

    public abstract Result<ActiveUserAccount> isUserIsKnownAndActive(String userId);
}
