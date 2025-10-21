package com.bloodbowlclub.auth.domain.user_account.commands;

import com.bloodbowlclub.lib.use_cases.Command;

public record ValidateEmailCommand(String userId) implements Command {

    public String getAccountId() {
        return userId;
    }
}
