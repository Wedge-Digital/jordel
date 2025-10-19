package com.auth.use_cases;

import com.auth.domain.user_account.AbstractUserAccount;
import com.auth.domain.user_account.DraftUserAccount;
import com.auth.domain.user_account.UserAccountHydrator;
import com.auth.domain.user_account.commands.ValidateEmailCommand;
import com.lib.persistance.event_log.EventLogEntity;
import com.lib.persistance.event_log.EventLogRepository;
import com.auth.use_cases.policies.UserAccountShallExistPolicy;
import com.lib.domain.events.AbstractEventDispatcher;
import com.lib.services.Result;
import com.lib.services.ResultMap;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;

@Component
public class ValidateEmailCommandHandler {

    private final UserAccountShallExistPolicy accountShallExistPolicy;
    private final AbstractEventDispatcher businessDispatcher;

    private final UserAccountHydrator userAccountHydrator;

    public ValidateEmailCommandHandler(UserAccountShallExistPolicy accountShallExistPolicy, AbstractEventDispatcher businessDispatcher, UserAccountHydrator userAccountHydrator) {
        this.accountShallExistPolicy = accountShallExistPolicy;
        this.businessDispatcher = businessDispatcher;
        this.userAccountHydrator = userAccountHydrator;
    }

    public ResultMap<String> handle(ValidateEmailCommand command) {
        ResultMap<String> userAccountCheck = this.accountShallExistPolicy.check(command.getAccountId());

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

        return ResultMap.success(command.getAccountId());
    }
}
