package com.auth.domain.user_account.commands;

import com.lib.use_cases.Command;

public record ValidateEmailCommand(String userId) implements Command {

    public String getAccountId() {
        return userId;
    }
}
