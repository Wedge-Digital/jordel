package com.bloodbowlclub.auth.domain.user_account.commands;

import com.bloodbowlclub.lib.Command;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class LoginCommand implements Command {
    private final String username;
    private final String password;
}
