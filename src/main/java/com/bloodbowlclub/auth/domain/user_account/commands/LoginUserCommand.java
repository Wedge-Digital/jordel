package com.bloodbowlclub.auth.domain.user_account.commands;

import com.bloodbowlclub.auth.domain.user_account.values.Username;
import com.bloodbowlclub.lib.use_cases.UserCommand;

public class LoginUserCommand extends UserCommand {
    private final String username;
    private final String password;

    public LoginUserCommand(Username creator, String username, String password) {
        super(creator);
        this.username = username;
        this.password = password;
    }
}
