package com.auth.io.web.models;

import com.auth.domain.user_account.commands.LoginCommand;

public class LoginRequest extends LoginCommand {

    public LoginRequest(String username, String password) {
        super(username, password);
    }
}