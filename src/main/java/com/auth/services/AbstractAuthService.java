package com.auth.services;

import com.td.aion.io.repositories.user.UserEntity;
import com.td.aion.utils.Result;

public abstract class AbstractAuthService {

    public abstract Result<UserEntity> isUserIsKnownAndActive(String userId);
}
