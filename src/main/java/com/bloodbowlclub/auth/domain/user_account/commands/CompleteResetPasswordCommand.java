package com.bloodbowlclub.auth.domain.user_account.commands;

import com.bloodbowlclub.lib.Command;

public record CompleteResetPasswordCommand(String username, String token, String newPassword) implements Command {
}
