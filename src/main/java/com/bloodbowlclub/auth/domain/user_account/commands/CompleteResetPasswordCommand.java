package com.bloodbowlclub.auth.domain.user_account.commands;

import com.bloodbowlclub.lib.Command;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class CompleteResetPasswordCommand implements Command {
    private final String token;
    private final String newPassword;
}
