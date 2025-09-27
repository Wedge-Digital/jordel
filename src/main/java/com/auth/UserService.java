package com.td.aion.io.web;

import com.td.aion.io.repositories.user.UserEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
public class UserService extends AbstractUserService{

    private UserEntity anonymousUser() {
        return new UserEntity();
    }

    public UserEntity getCurrentUser() {
        Object Principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (Principal instanceof UserEntity) {
            return (UserEntity) Principal;
        }
        return anonymousUser();
    }
}
