package com.bloodbowlclub.auth.use_cases;

import com.bloodbowlclub.auth.domain.user_account.AbstractUserAccount;
import com.bloodbowlclub.auth.domain.user_account.UserAccountHydrator;
import com.bloodbowlclub.auth.domain.user_account.commands.ValidateEmailCommand;
import com.bloodbowlclub.auth.use_cases.policies.UserAccountShallExistPolicy;
import com.bloodbowlclub.lib.domain.events.AbstractEventDispatcher;
import com.bloodbowlclub.lib.services.Result;
import com.bloodbowlclub.lib.services.ResultMap;
import com.bloodbowlclub.lib.use_cases.Command;
import com.bloodbowlclub.lib.use_cases.CommandHandler;
import org.springframework.stereotype.Component;

import java.util.HashMap;

@Component
public class ValidateEmailCommandHandler extends CommandHandler {

    private final UserAccountShallExistPolicy accountShallExistPolicy;
    private final AbstractEventDispatcher businessDispatcher;

    private final UserAccountHydrator userAccountHydrator;

    public ValidateEmailCommandHandler(UserAccountShallExistPolicy accountShallExistPolicy, AbstractEventDispatcher businessDispatcher, UserAccountHydrator userAccountHydrator) {
        this.accountShallExistPolicy = accountShallExistPolicy;
        this.businessDispatcher = businessDispatcher;
        this.userAccountHydrator = userAccountHydrator;
    }

    public ResultMap<Void> handle(Command inputCommand) {
        ValidateEmailCommand command  = (ValidateEmailCommand) inputCommand;
        ResultMap<Void> userAccountCheck = this.accountShallExistPolicy.check(command.getAccountId());

        if (userAccountCheck.isFailure()) {
            return userAccountCheck;
        }

        Result<AbstractUserAccount> agregateHydratation = userAccountHydrator.hydrate(command.getAccountId());

        if (agregateHydratation.isFailure()) {
            HashMap<String, String> errorMap = new HashMap<>();
            errorMap.put("user_account", agregateHydratation.getError());
            return ResultMap.failure(errorMap);
        }

        AbstractUserAccount newAccount = agregateHydratation.getValue();
        newAccount.confirmEmail(command);

        businessDispatcher.asyncDispatchList(newAccount.domainEvents());

        return ResultMap.success(null);
    }
}
