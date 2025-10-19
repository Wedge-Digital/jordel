package com.auth.use_cases.event_receivers;

import com.auth.domain.user_account.events.AccountRegisteredEvent;
import com.lib.persistance.event_log.EventLogEntity;
import com.lib.persistance.event_log.EventLogRepository;
import com.lib.domain.events.AbstractEventDispatcher;
import com.lib.domain.events.DomainEvent;
import com.lib.domain.events.EventHandler;
import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Component;

@Component
public class UserAccountEventDumper implements EventHandler {

    private final EventLogRepository eventRepo;

    private final AbstractEventDispatcher eventBus;

    public UserAccountEventDumper(EventLogRepository eventRepo, AbstractEventDispatcher eventBus) {
        this.eventRepo = eventRepo;
        this.eventBus = eventBus;
    }

    @PostConstruct
    private void init() {
        this.eventBus.subscribe(AccountRegisteredEvent.class, this);
    }

    @Override
    public void receive(DomainEvent event) {
        EventLogEntity eventEntity = new EventLogEntity(event, event.getCreatedBy());
        eventRepo.save(eventEntity);
    }

}
