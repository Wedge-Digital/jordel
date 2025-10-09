package com.auth.domain.user_account.commands;

public class ValidateEmailCommand {
    private final String userId;

    public ValidateEmailCommand(String userId) {
        this.userId = userId;
    }

    public String getAccountId() {
        return userId;
    }
}
