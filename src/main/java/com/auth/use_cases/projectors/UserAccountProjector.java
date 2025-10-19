package com.auth.use_cases.projectors;

import com.auth.domain.user_account.AbstractUserAccount;
import com.auth.domain.user_account.UserAccountHydrator;
import com.auth.domain.user_account.events.AccountRegisteredEvent;
import com.auth.domain.user_account.events.EmailValidatedEvent;
import com.auth.io.persistance.read.UserAccountReadRepository;
import com.lib.domain.events.AbstractEventDispatcher;
import com.lib.domain.events.DomainEvent;
import com.lib.domain.events.EventHandler;
import com.lib.domain.events.Projector;
import com.lib.persistance.event_log.EventLogEntity;
import com.lib.persistance.event_log.EventLogRepository;
import com.lib.persistance.read_cache.ReadEntity;
import com.lib.persistance.read_cache.ReadEntityType;
import com.lib.persistance.read_cache.ReadRepository;
import com.lib.services.Result;
import com.lib.services.ResultMap;
import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Component;

import java.util.HashMap;

@Component
public class UserAccountProjector implements Projector {


    private final AbstractEventDispatcher eventBus;

    private final UserAccountHydrator userAccountHydrator;

    private final ReadRepository readRepo;

    public UserAccountProjector(EventLogRepository eventRepo, AbstractEventDispatcher eventBus, UserAccountHydrator userAccountHydrator, ReadRepository readRepo) {
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
