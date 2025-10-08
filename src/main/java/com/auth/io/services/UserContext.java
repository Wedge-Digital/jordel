package com.auth.io.services;

import com.auth.domain.user_account.ActiveUserAccount;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
public class UserContext extends AbstractUserContext{

    private ActiveUserAccount anonymousUser() {
        return null;
    }

    public ActiveUserAccount getCurrentUser() {
        Object Principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (Principal instanceof ActiveUserAccount) {
            return (ActiveUserAccount) Principal;
        }
        return anonymousUser();
    }
}
