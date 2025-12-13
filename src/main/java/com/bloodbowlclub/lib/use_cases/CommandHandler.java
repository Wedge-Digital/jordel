package com.bloodbowlclub.lib.use_cases;

import com.bloodbowlclub.lib.Command;
import com.bloodbowlclub.lib.domain.events.AbstractEventDispatcher;
import com.bloodbowlclub.lib.domain.events.DomainEvent;
import com.bloodbowlclub.lib.persistance.event_store.EventEntity;
import com.bloodbowlclub.lib.persistance.event_store.EventEntityFactory;
import com.bloodbowlclub.lib.persistance.event_store.EventStore;
import com.bloodbowlclub.lib.services.result.ResultMap;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.MessageSource;

import java.util.List;
import java.util.stream.Collectors;

public abstract class CommandHandler {
    @Qualifier("eventStore")
    protected final EventStore eventStore;

    @Qualifier("EventDispatcher")
    private final AbstractEventDispatcher businessDispatcher;

    protected final MessageSource messageSource;

    private EventEntityFactory factory = new EventEntityFactory();

    protected CommandHandler(EventStore eventStore,
                             AbstractEventDispatcher businessDispatcher,
                             MessageSource messageSource) {
        this.eventStore = eventStore;
        this.businessDispatcher = businessDispatcher;
        this.messageSource = messageSource;
    }

    public abstract ResultMap<Void> handle(Command userCommand);

    protected void saveAndDispatch(List<DomainEvent> eventList) {
        List<EventEntity> entities = eventList.stream()
                .map(domainEvent -> factory.build(domainEvent))
                .collect(Collectors.toList());
        eventStore.saveAll(entities);

        businessDispatcher.asyncDispatchList(eventList);

    }

}
