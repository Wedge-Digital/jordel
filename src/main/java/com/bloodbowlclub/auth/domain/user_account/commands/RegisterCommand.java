package com.bloodbowlclub.auth.domain.user_account.commands;

import com.bloodbowlclub.lib.use_cases.Command;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Setter;

@AllArgsConstructor
@Data
@Setter
public class RegisterCommand implements Command {
    private String userId;
    private String username;
    private String email;
    private String password;
}
