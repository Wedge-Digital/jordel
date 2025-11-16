package com.bloodbowlclub.auth.domain.user_account.commands;

import com.bloodbowlclub.auth.domain.user_account.values.Username;
import com.bloodbowlclub.lib.Command;
import com.bloodbowlclub.lib.use_cases.UserCommand;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class LoginCommand implements Command {
    private final String username;
    private final String password;
}
