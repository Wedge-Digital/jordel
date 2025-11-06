package com.bloodbowlclub.auth.use_cases;

import com.bloodbowlclub.auth.domain.user_account.DraftUserAccount;
import com.bloodbowlclub.auth.domain.user_account.commands.RegisterCommand;
import com.bloodbowlclub.lib.domain.events.AbstractEventDispatcher;
import com.bloodbowlclub.lib.persistance.event_store.EventEntity;
import com.bloodbowlclub.lib.persistance.event_store.EventStore;
import com.bloodbowlclub.lib.services.ResultMap;
import com.bloodbowlclub.lib.use_cases.Command;
import com.bloodbowlclub.lib.use_cases.CommandHandler;
import com.bloodbowlclub.lib.use_cases.Policy;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.MessageSource;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class RegisterCommandHandler extends CommandHandler {

    @Qualifier("UserNameShallNotExistPolicy")
    private final Policy userNameShallNotExistPolicy;

    @Qualifier("IdShallNotExistPolicy")
    private final Policy idShallNotExistPolicy;

    @Qualifier("EmailShallNotExistPolicy")
    private final Policy emailShallNotExistPolicy;

    public RegisterCommandHandler(@Qualifier("EventStore") EventStore eventStore,
                                  @Qualifier("UsernameShallNotExistPolicy") Policy userNameShallNotExistPolicy,
                                  @Qualifier("IdShallNotExistPolicy" ) Policy idShallNotExistPolicy,
                                  @Qualifier("EmailShallNotExistPolicy") Policy emailShallNotExistPolicy,
                                  AbstractEventDispatcher businessDispatcher,
                                  MessageSource messageSource
                                  ) {
        super(eventStore,  businessDispatcher, messageSource);
        this.userNameShallNotExistPolicy = userNameShallNotExistPolicy;
        this.idShallNotExistPolicy = idShallNotExistPolicy;
        this.emailShallNotExistPolicy = emailShallNotExistPolicy;
    }

    @Override
    public ResultMap<Void> handle(Command registerCommand) {
        RegisterCommand command = (RegisterCommand) registerCommand;
        ResultMap<Void> userIdCheck = this.idShallNotExistPolicy.check(command.getUserId());
        ResultMap<Void> usernameCheck = this.userNameShallNotExistPolicy.check(command.getUsername());
        ResultMap<Void> emailCheck = this.emailShallNotExistPolicy.check(command.getEmail());
        ResultMap<Void> allChecks = ResultMap.combine(userIdCheck, usernameCheck, emailCheck);

        if (allChecks.isFailure()) {
            return allChecks;
        }

        DraftUserAccount newAccount = new DraftUserAccount();
        ResultMap<Void> registerResult = newAccount.register(command);

        if (registerResult.isFailure()) {
            return registerResult;
        }

        saveAndDispatch(newAccount.domainEvents());


        return registerResult;
    }
}
