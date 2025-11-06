package com.bloodbowlclub.auth.domain.user_account.commands;

import com.bloodbowlclub.lib.Command;
import lombok.Data;

@Data
public class RegisterAccountCommand implements Command {
    private String username;
    private String email;
    private String password;

    public RegisterAccountCommand(String username, String email, String password) {
        this.username = username;
        this.email = email;
        this.password = password;
    }
}
