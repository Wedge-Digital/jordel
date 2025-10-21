package com.bloodbowlclub.auth.use_cases.projectors;

import com.bloodbowlclub.auth.domain.user_account.AbstractUserAccount;
import com.bloodbowlclub.auth.domain.user_account.UserAccountHydrator;
import com.bloodbowlclub.auth.domain.user_account.events.AccountRegisteredEvent;
import com.bloodbowlclub.auth.domain.user_account.events.EmailValidatedEvent;
import com.bloodbowlclub.lib.domain.events.AbstractEventDispatcher;
import com.bloodbowlclub.lib.domain.events.DomainEvent;
import com.bloodbowlclub.lib.domain.events.Projector;
import com.bloodbowlclub.lib.persistance.event_store.EventStore;
import com.bloodbowlclub.lib.persistance.read_cache.ReadEntity;
import com.bloodbowlclub.lib.persistance.read_cache.ReadEntityType;
import com.bloodbowlclub.lib.persistance.read_cache.ReadRepository;
import com.bloodbowlclub.lib.services.Result;
import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Component;

@Component
public class UserAccountProjector implements Projector {


    private final AbstractEventDispatcher eventBus;

    private final UserAccountHydrator userAccountHydrator;

    private final ReadRepository readRepo;

    public UserAccountProjector(EventStore eventRepo, AbstractEventDispatcher eventBus, UserAccountHydrator userAccountHydrator, ReadRepository readRepo) {
        this.eventBus = eventBus;
        this.userAccountHydrator = userAccountHydrator;
        this.readRepo = readRepo;
    }

    @PostConstruct
    private void init() {
        this.eventBus.subscribe(AccountRegisteredEvent.class, this);
        this.eventBus.subscribe(EmailValidatedEvent.class, this);
    }

    @Override
    public void receive(DomainEvent event) {
        Result<AbstractUserAccount> aggregateHydration = userAccountHydrator.hydrate(event.getAggregateId());
        if (aggregateHydration.isFailure()) {
            return ;
        }
        ReadEntity readEntity = new ReadEntity(ReadEntityType.USER_ACCOUNT, aggregateHydration.getValue());
        readRepo.save(readEntity);
    }

}
