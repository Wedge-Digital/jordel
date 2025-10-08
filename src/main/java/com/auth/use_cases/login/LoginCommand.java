package com.auth.use_cases.login;

public class LoginCommand {
    String  username;
    String password;

    public LoginCommand(String username, String password) {
        this.username = username;
        this.password = password;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }
}
