package com.auth.use_cases.event_receivers;

import com.auth.domain.user_account.events.AccountRegisteredEvent;
import com.lib.persistance.event_store.EventEntity;
import com.lib.persistance.event_store.EventStore;
import com.lib.domain.events.AbstractEventDispatcher;
import com.lib.domain.events.DomainEvent;
import com.lib.domain.events.EventHandler;
import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Component;

@Component
public class UserAccountEventDumper implements EventHandler {

    private final EventStore eventRepo;

    private final AbstractEventDispatcher eventBus;

    public UserAccountEventDumper(EventStore eventRepo, AbstractEventDispatcher eventBus) {
        this.eventRepo = eventRepo;
        this.eventBus = eventBus;
    }

    private void init() {
        this.eventBus.subscribe(AccountRegisteredEvent.class, this);
    }

    @Override
    public void receive(DomainEvent event) {
        EventEntity eventEntity = new EventEntity(event, event.getCreatedBy());
        eventRepo.save(eventEntity);
    }

}
