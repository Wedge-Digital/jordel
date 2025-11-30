package com.bloodbowlclub.auth.use_cases;

import com.bloodbowlclub.auth.domain.user_account.BaseUserAccount;
import com.bloodbowlclub.auth.domain.user_account.commands.ValidateEmailCommand;
import com.bloodbowlclub.auth.use_cases.policies.AgregateShallExistPolicy;
import com.bloodbowlclub.lib.Command;
import com.bloodbowlclub.lib.domain.AggregateRoot;
import com.bloodbowlclub.lib.domain.events.AbstractEventDispatcher;
import com.bloodbowlclub.lib.domain.events.DomainEvent;
import com.bloodbowlclub.lib.persistance.event_store.EventEntity;
import com.bloodbowlclub.lib.persistance.event_store.EventStore;
import com.bloodbowlclub.lib.services.result.ErrorCode;
import com.bloodbowlclub.lib.services.result.Result;
import com.bloodbowlclub.lib.services.result.ResultMap;
import com.bloodbowlclub.lib.use_cases.CommandHandler;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;

@Component
public class ValidateEmailCommandHandler extends CommandHandler {

    private final AgregateShallExistPolicy accountShallExistPolicy;
    private final AbstractEventDispatcher businessDispatcher;

    public ValidateEmailCommandHandler(AgregateShallExistPolicy accountShallExistPolicy,
                                       @Qualifier("EventStore") EventStore eventStore,
                                       AbstractEventDispatcher businessDispatcher, MessageSource messageSource) {
        super(eventStore, businessDispatcher, messageSource);
        this.accountShallExistPolicy = accountShallExistPolicy;
        this.businessDispatcher = businessDispatcher;
    }

    public ResultMap<Void> handle(Command inputUserCommand) {
        ValidateEmailCommand command  = (ValidateEmailCommand) inputUserCommand;
        ResultMap<Void> userAccountCheck = this.accountShallExistPolicy.check(command.getCreator().toString());

        if (userAccountCheck.isFailure()) {
            return userAccountCheck;
        }

        String username = command.getCreator().toString();
        BaseUserAccount userAccount = new BaseUserAccount(username);
        List<EventEntity> eventList = eventStore.findBySubject(username);
        if (eventList.isEmpty()) {
            return ResultMap.failure("Account", "no history for account", ErrorCode.NOT_FOUND);
        }
        List<DomainEvent> domainEvents = eventList.stream().map(EventEntity::getData).toList();
        Result<AggregateRoot> agregateHydratation = userAccount.hydrate(domainEvents);

        if (agregateHydratation.isFailure()) {
            HashMap<String, String> errorMap = new HashMap<>();
            errorMap.put("user_account", agregateHydratation.getError());
            return ResultMap.failure(errorMap, ErrorCode.UNPROCESSABLE_ENTITY);
        }

        AggregateRoot hydrated =  agregateHydratation.getValue();
        if (!(hydrated instanceof BaseUserAccount newAccount)) {
            return ResultMap.failure("account", "hydratation error", ErrorCode.UNPROCESSABLE_ENTITY);
        }

        newAccount.validate(command);

        businessDispatcher.asyncDispatchList(newAccount.domainEvents());

        return ResultMap.success(null);
    }
}
