package com.auth.use_cases;

import com.auth.domain.user_account.AbstractUserAccount;
import com.auth.domain.user_account.DraftUserAccount;
import com.auth.domain.user_account.commands.RegisterCommand;
import com.auth.domain.user_account.commands.ValidateEmailCommand;
import com.auth.io.persistance.write.BusinessEventEntity;
import com.auth.io.persistance.write.BusinessEventRepository;
import com.auth.use_cases.policies.EmailShallNotExistPolicy;
import com.auth.use_cases.policies.UserAccountShallExistPolicy;
import com.auth.use_cases.policies.UserShallNotExistPolicy;
import com.shared.domain.events.AbstractEventDispatcher;
import com.shared.services.Result;
import com.shared.services.ResultMap;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;

@Component
public class ValidateEmailCommandHandler {

    private final UserAccountShallExistPolicy accountShallExistPolicy;
    private final AbstractEventDispatcher businessDispatcher;

    private final BusinessEventRepository eventRepo;

    public ValidateEmailCommandHandler(UserAccountShallExistPolicy accountShallExistPolicy, AbstractEventDispatcher businessDispatcher, BusinessEventRepository eventRepo) {
        this.accountShallExistPolicy = accountShallExistPolicy;
        this.businessDispatcher = businessDispatcher;
        this.eventRepo = eventRepo;
    }

    private Result<AbstractUserAccount> hydrateAcount(String accountId) {
        List<BusinessEventEntity> eventEntityList = eventRepo.findBySource(accountId);
        DraftUserAccount account = new DraftUserAccount();
        Result<AbstractUserAccount> agregateHydratation = account.applyAll(eventEntityList.stream().map(BusinessEventEntity::getData).toList());

        if (agregateHydratation.isFailure()) {
            return Result.failure(agregateHydratation.getError());
        }
        return agregateHydratation;
    }

    public ResultMap<String> handle(ValidateEmailCommand command) {
        ResultMap<String> userAccountCheck = this.accountShallExistPolicy.check(command.getAccountId());

        if (userAccountCheck.isFailure()) {
            return userAccountCheck;
        }

        Result<AbstractUserAccount> agregateHydratation = hydrateAcount(command.getAccountId());

        if (agregateHydratation.isFailure()) {
            HashMap<String, String> errorMap = new HashMap<>();
            errorMap.put("user_account", agregateHydratation.getError());
            return ResultMap.failure(errorMap);
        }

        AbstractUserAccount newAccount = agregateHydratation.getValue();
        newAccount.confirmEmail(command);

        businessDispatcher.dispatchAll(newAccount.domainEvents());

        return ResultMap.success(command.getAccountId());
    }
}
