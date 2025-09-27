package com.auth.jwt;

import com.td.aion.utils.Result;

public abstract class AbstractUserChecker {

    public abstract Result<KeycloakUser> getUserInfosFromToken(String token);
}
