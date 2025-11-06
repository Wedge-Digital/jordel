package com.bloodbowlclub.auth.domain.user_account.commands;

import com.bloodbowlclub.auth.domain.user_account.values.Username;
import com.bloodbowlclub.lib.use_cases.UserCommand;
import lombok.Getter;

@Getter
public class ValidateEmailCommand extends UserCommand {
    private String emailToValidate;

    protected ValidateEmailCommand(Username creator,  String emailToValidate) {
        super(creator);
        this.emailToValidate = emailToValidate;
    }
}
