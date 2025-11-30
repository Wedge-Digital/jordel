package com.bloodbowlclub.auth.domain.user_account.commands;

import com.bloodbowlclub.lib.Command;
import lombok.AllArgsConstructor;

public record CompleteResetPasswordCommand(String username, String token, String newPassword) implements Command {
}
