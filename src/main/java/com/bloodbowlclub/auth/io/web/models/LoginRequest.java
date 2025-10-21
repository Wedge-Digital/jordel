package com.bloodbowlclub.auth.io.web.models;

import com.bloodbowlclub.auth.domain.user_account.commands.LoginCommand;

public class LoginRequest extends LoginCommand {

    public LoginRequest(String username, String password) {
        super(username, password);
    }
}