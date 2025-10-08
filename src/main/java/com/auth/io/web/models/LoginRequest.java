package com.auth.io.web.models;

import com.auth.use_cases.login.LoginCommand;

public class LoginRequest extends LoginCommand {

    public LoginRequest(String username, String password) {
        super(username, password);
    }
}