package com.auth.use_cases.register;

import com.auth.domain.user_account.DraftUserAccount;
import com.auth.domain.user_account.commands.RegisterCommand;
import com.auth.use_cases.policies.EmailShallNotExistPolicy;
import com.auth.use_cases.policies.UserShallNotExistPolicy;
import com.shared.domain.events.AbstractEventDispatcher;
import com.shared.domain.events.EventDispatcher;
import com.shared.services.ResultMap;
import org.springframework.stereotype.Component;

@Component
public class RegisterCommandHandler {

    private final UserShallNotExistPolicy userShallNotExistPolicy;
    private final EmailShallNotExistPolicy emailShallNotExistPolicy;

    private final AbstractEventDispatcher businessDispatcher;

    public RegisterCommandHandler(UserShallNotExistPolicy userShallNotExistPolicy, EmailShallNotExistPolicy emailShallNotExistPolicy, AbstractEventDispatcher businessDispatcher) {
        this.userShallNotExistPolicy = userShallNotExistPolicy;
        this.emailShallNotExistPolicy = emailShallNotExistPolicy;
        this.businessDispatcher = businessDispatcher;
    }

    public ResultMap<String> handle(RegisterCommand command) {
        ResultMap<String> usernameCheck = this.userShallNotExistPolicy.check(command.getUsername());
        ResultMap<String> emailCheck = this.emailShallNotExistPolicy.check(command.getEmail());
        ResultMap<String> allChecks = ResultMap.combine(usernameCheck, emailCheck);

        if (allChecks.isFailure()) {
            return allChecks;
        }

        DraftUserAccount newAccount = new DraftUserAccount();
        ResultMap<String> registerResult = newAccount.register(command);

        if (registerResult.isFailure()) {
            return registerResult;
        }

        businessDispatcher.dispatchAll(newAccount.domainEvents());

        return registerResult;
    }
}
