package com.auth.use_cases;

import com.auth.domain.user_account.DraftUserAccount;
import com.auth.domain.user_account.commands.RegisterCommand;
import com.lib.persistance.event_store.EventEntity;
import com.lib.persistance.event_store.EventStore;
import com.lib.use_cases.Command;
import com.lib.use_cases.CommandHandler;
import com.lib.use_cases.Policy;
import com.lib.domain.events.AbstractEventDispatcher;
import com.lib.services.ResultMap;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class RegisterCommandHandler extends CommandHandler {

    @Qualifier("EventStore")
    private final JpaRepository eventStore;

    @Qualifier("UserNameShallNotExistPolicy")
    private final Policy userNameShallNotExistPolicy;

    @Qualifier("IdShallNotExistPolicy")
    private final Policy idShallNotExistPolicy;

    @Qualifier("EmailShallNotExistPolicy")
    private final Policy emailShallNotExistPolicy;

    @Qualifier("EventDispatcher")
    private final AbstractEventDispatcher businessDispatcher;

    public RegisterCommandHandler(@Qualifier("EventStore") JpaRepository eventStore,
                                  @Qualifier("UsernameShallNotExistPolicy") Policy userNameShallNotExistPolicy,
                                  @Qualifier("IdShallNotExistPolicy" ) Policy idShallNotExistPolicy,
                                  @Qualifier("EmailShallNotExistPolicy") Policy emailShallNotExistPolicy,
                                  AbstractEventDispatcher businessDispatcher) {
        this.eventStore = eventStore;
        this.userNameShallNotExistPolicy = userNameShallNotExistPolicy;
        this.idShallNotExistPolicy = idShallNotExistPolicy;
        this.emailShallNotExistPolicy = emailShallNotExistPolicy;
        this.businessDispatcher = businessDispatcher;
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
        List<EventEntity> entities = newAccount.domainEvents().stream()
                .map(domainEvent -> new EventEntity(domainEvent, null))
                .collect(Collectors.toList());
        eventStore.saveAll(entities);

        businessDispatcher.asyncDispatchList(newAccount.domainEvents());

        return registerResult;
    }
}
