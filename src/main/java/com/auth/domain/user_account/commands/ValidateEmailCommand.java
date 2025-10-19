package com.auth.domain.user_account.commands;

public record ValidateEmailCommand(String userId) {

    public String getAccountId() {
        return userId;
    }
}
