package com.bloodbowlclub.auth.use_cases;

import com.bloodbowlclub.auth.domain.user_account.BaseUserAccount;
import com.bloodbowlclub.auth.domain.user_account.commands.RegisterAccountCommand;
import com.bloodbowlclub.lib.Command;
import com.bloodbowlclub.lib.domain.events.AbstractEventDispatcher;
import com.bloodbowlclub.lib.persistance.event_store.EventStore;
import com.bloodbowlclub.lib.services.result.ErrorCode;
import com.bloodbowlclub.lib.services.result.ResultMap;
import com.bloodbowlclub.lib.services.TranslatableMessage;
import com.bloodbowlclub.lib.use_cases.CommandHandler;
import com.bloodbowlclub.lib.use_cases.Policy;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

@Component("registerCommandHandler")
public class RegisterCommandHandler extends CommandHandler {

    @Qualifier("UserNameShallNotExistPolicy")
    private final Policy userNameShallNotExistPolicy;

    public RegisterCommandHandler(@Qualifier("eventStore") EventStore eventStore,
                                  @Qualifier("aggregateShallNotExistPolicy") Policy userNameShallNotExistPolicy,
                                  AbstractEventDispatcher businessDispatcher
                                  ) {
        super(eventStore,  businessDispatcher);
        this.userNameShallNotExistPolicy = userNameShallNotExistPolicy;
    }

    @Override
    public ResultMap<Void> handle(Command registerUserCommand) {
        RegisterAccountCommand command = (RegisterAccountCommand) registerUserCommand;
        ResultMap<Void> usernameCheck = this.userNameShallNotExistPolicy.check(command.getUsername());

        if (usernameCheck.isFailure()) {
            TranslatableMessage errorMessage = usernameCheck.errorMap().get("aggregateId");
            return ResultMap.failure("username", errorMessage, ErrorCode.ALREADY_EXISTS);
        }

        BaseUserAccount newAccount = new BaseUserAccount(command.getUsername());
        ResultMap<Void> registerResult = newAccount.registerSimpleUser(command);

        if (registerResult.isFailure()) {
            return registerResult;
        }

        saveAndDispatch(newAccount.domainEvents());

        return registerResult;
    }
}
