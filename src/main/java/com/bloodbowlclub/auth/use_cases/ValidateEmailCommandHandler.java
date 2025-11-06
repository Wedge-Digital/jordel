package com.bloodbowlclub.auth.use_cases;

import com.bloodbowlclub.auth.domain.user_account.DraftUserAccount;
import com.bloodbowlclub.auth.domain.user_account.commands.ValidateEmailCommand;
import com.bloodbowlclub.auth.use_cases.policies.UserAccountShallExistPolicy;
import com.bloodbowlclub.lib.domain.AggregateRoot;
import com.bloodbowlclub.lib.domain.events.AbstractEventDispatcher;
import com.bloodbowlclub.lib.persistance.event_store.EventStore;
import com.bloodbowlclub.lib.services.Result;
import com.bloodbowlclub.lib.services.ResultMap;
import com.bloodbowlclub.lib.use_cases.Command;
import com.bloodbowlclub.lib.use_cases.CommandHandler;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.MessageSource;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Component;

import java.util.HashMap;

@Component
public class ValidateEmailCommandHandler extends CommandHandler {

    private final UserAccountShallExistPolicy accountShallExistPolicy;
    private final AbstractEventDispatcher businessDispatcher;

    public ValidateEmailCommandHandler(UserAccountShallExistPolicy accountShallExistPolicy,
                                       @Qualifier("EventStore") EventStore eventStore,
                                       AbstractEventDispatcher businessDispatcher, MessageSource messageSource) {
        super(eventStore, businessDispatcher, messageSource);
        this.accountShallExistPolicy = accountShallExistPolicy;
        this.businessDispatcher = businessDispatcher;
    }

    public ResultMap<Void> handle(Command inputCommand) {
        ValidateEmailCommand command  = (ValidateEmailCommand) inputCommand;
        ResultMap<Void> userAccountCheck = this.accountShallExistPolicy.check(command.getAccountId());

        if (userAccountCheck.isFailure()) {
            return userAccountCheck;
        }

        Result<AggregateRoot> agregateHydratation = eventStore.hydrate(command.getAccountId());

        if (agregateHydratation.isFailure()) {
            HashMap<String, String> errorMap = new HashMap<>();
            errorMap.put("user_account", agregateHydratation.getError());
            return ResultMap.failure(errorMap);
        }

        AggregateRoot hydrated =  agregateHydratation.getValue();
        if (!(hydrated instanceof DraftUserAccount newAccount)) {
            return ResultMap.failure("account", "hydratation error");
        }

        newAccount.confirmEmail(command);

        businessDispatcher.asyncDispatchList(newAccount.domainEvents());

        return ResultMap.success(null);
    }
}
