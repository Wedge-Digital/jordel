package com.bloodbowlclub.auth.use_cases.event_receivers;

import com.bloodbowlclub.auth.domain.user_account.events.AccountRegisteredEvent;
import com.bloodbowlclub.lib.domain.events.AbstractEventDispatcher;
import com.bloodbowlclub.lib.domain.events.DomainEvent;
import com.bloodbowlclub.lib.domain.events.EventHandler;
import com.bloodbowlclub.lib.persistance.event_store.EventEntity;
import com.bloodbowlclub.lib.persistance.event_store.EventStore;
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
