package com.bloodbowlclub.lib.use_cases;

import com.bloodbowlclub.auth.domain.user_account.values.Username;
import com.bloodbowlclub.lib.Command;
import lombok.Getter;

@Getter
public abstract class UserCommand implements Command {
    private final Username creator;

    protected UserCommand(Username creator) {
        this.creator = creator;
    }
}
