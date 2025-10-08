package com.auth.use_cases.event_receivers.user_account;

import com.auth.io.persistance.write.BusinessEventEntity;
import com.auth.io.persistance.write.BusinessEventRepository;
import com.auth.domain.user_account.events.AccountRegisteredEvent;
import com.shared.domain.events.AbstractEventDispatcher;
import com.shared.domain.events.DomainEvent;
import com.shared.domain.events.EventHandler;
import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Component;

@Component
public class UserAccountEventDumper implements EventHandler {

    private final BusinessEventRepository eventRepo;

    private final AbstractEventDispatcher eventBus;

    public UserAccountEventDumper(BusinessEventRepository eventRepo, AbstractEventDispatcher eventBus) {
        this.eventRepo = eventRepo;
        this.eventBus = eventBus;
    }

    @PostConstruct
    private void init() {
        this.eventBus.subscribe(AccountRegisteredEvent.class, this);
    }

    @Override
    public void receive(DomainEvent event) {
        BusinessEventEntity eventEntity = new BusinessEventEntity(event);
        eventRepo.save(eventEntity);
    }

}
